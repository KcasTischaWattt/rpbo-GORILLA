package com.backend.crosswords.corpus.services;

import com.backend.crosswords.admin.models.FcmToken;
import com.backend.crosswords.admin.models.User;
import com.backend.crosswords.admin.services.FcmTokenService;
import com.backend.crosswords.admin.services.UserService;
import com.backend.crosswords.config.Pair;
import com.backend.crosswords.corpus.dto.*;
import com.backend.crosswords.corpus.models.*;
import com.backend.crosswords.corpus.repositories.elasticsearch.DigestSearchRepository;
import com.backend.crosswords.corpus.repositories.jpa.DigestCoreRepository;
import com.backend.crosswords.corpus.repositories.jpa.DigestRepository;
import org.apache.http.ConnectionClosedException;
import org.opensearch.data.client.orhlc.NativeSearchQueryBuilder;
import org.opensearch.index.query.BoolQueryBuilder;
import org.opensearch.index.query.IdsQueryBuilder;
import org.opensearch.index.query.QueryBuilder;
import org.opensearch.index.query.QueryBuilders;
import org.springframework.data.domain.*;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.*;

@Service
public class DigestService {
    private final DigestCoreRepository coreRepository;
    private final DigestSubscriptionService subscriptionService;
    private final DigestSubscriptionSettingsService subscriptionSettingsService;
    private final DigestTemplateService templateService;
    private final TelegramNotificationService telegramNotificationService;
    private final DigestRepository digestRepository;
    private final DigestSearchRepository digestSearchRepository;
    private final DocService docService;
    private final DigestRatingService ratingService;
    private final UserService userService;
    private final TagService tagService;
    private final ElasticsearchOperations elasticsearchOperations;
    private final MailManService mailManService;
    private final DigestGeneratorService generatorService;
    private final FirebaseMessagingService firebaseMessagingService;
    private final FcmTokenService fcmTokenService;
    private final Queue<DigestTemplate> templates = new LinkedList<>();
    public static Timestamp startOfDay;
    public static Timestamp endOfDay;
    public DigestService(DigestCoreRepository digestCoreRepository, DigestSubscriptionService subscriptionService, DigestSubscriptionSettingsService subscriptionSettingsService, DigestTemplateService templateService, TelegramNotificationService telegramNotificationService, DigestRepository digestRepository, DigestSearchRepository digestSearchRepository, DocService docService, DigestRatingService ratingService, UserService userService, TagService tagService, ElasticsearchOperations elasticsearchOperations, MailManService mailManService, DigestGeneratorService generatorService, FirebaseMessagingService firebaseMessagingService, FcmTokenService fcmTokenService) {
        this.coreRepository = digestCoreRepository;
        this.subscriptionService = subscriptionService;
        this.subscriptionSettingsService = subscriptionSettingsService;
        this.templateService = templateService;
        this.telegramNotificationService = telegramNotificationService;
        this.digestRepository = digestRepository;
        this.digestSearchRepository = digestSearchRepository;
        this.docService = docService;
        this.ratingService = ratingService;
        this.userService = userService;
        this.tagService = tagService;
        this.elasticsearchOperations = elasticsearchOperations;
        this.mailManService = mailManService;
        this.generatorService = generatorService;
        this.firebaseMessagingService = firebaseMessagingService;
        this.fcmTokenService = fcmTokenService;
    }
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    protected DigestCore createNewDigestCore(DigestTemplate template) throws ConnectionClosedException {
        template = templateService.getTemplateFromId(template.getUuid()); // необходимо, чтобы сделать полную загрузку данных, избегаю ленивую
        var docMetas = docService.getAllDocsByTemplateForToday(template);
        System.out.println("Число подходящих документов: " + docMetas.size());
        if (docMetas.size() == 0) {
            return null;
        }
        DigestCore core = new DigestCore();
        core.setDate(new Timestamp(System.currentTimeMillis()));
        core.setDocs(docMetas);
        core.setTemplate(template);

        final DigestCore finalCore = coreRepository.save(core);
        docService.setCoreForDocs(finalCore, docMetas);
        // StringBuilder docMetasText = new StringBuilder();
        GenerateDigestDTO generateDigestDTO = new GenerateDigestDTO();
        for (var docMeta : docMetas) {
            var text = docService.getDocTextByDocId(docMeta.getId());
            var summary = docMeta.getSummary();
            // docMetasText.append(text);
            GenerateDigestsDocumentsDTO generateDigestsDocumentsDTO =  new GenerateDigestsDocumentsDTO(text, summary);
            generateDigestDTO.getDocuments().add(generateDigestsDocumentsDTO);
        }
        try {
            generatorService.generateDigest(generateDigestDTO)
                    .doOnSuccess(digestText -> this.saveDigestCoreWithText(finalCore, digestText))
                    .block();
        } catch (Exception e) {
            System.out.println("Что-то пошло не так при создании дайджеста: " + e.getMessage());
            return null;
        }
        return finalCore;
    }
    protected void saveDigestCoreWithText(DigestCore core, String digestText) {
        core.setText(digestText);
        coreRepository.save(core);
    }
    private void createNewDigests() throws ConnectionClosedException {
        List<Digest> digests = new ArrayList<>();
        List<DigestES> digestsES = new ArrayList<>();
        while (!templates.isEmpty()) {
            var template = templates.poll();
            var core = this.createNewDigestCore(template);
            if (core != null) {
                System.out.println("Ядро успешно создалось: " + core.getText());
                for (var subscription : subscriptionService.getAllDigestSubscriptionsByTemplateWithSettings(template)) {
                    var coreId = core.getId();
                    var subscriptionId = subscription.getId();
                    Digest digest = new Digest(new DigestId(coreId, subscriptionId), core, subscription);
                    digests.add(digest);
                    String digestESId = coreId + "-" + subscriptionId;
                    var digestES = new DigestES(digestESId, subscriptionService.getDigestSubscriptionsTitle(subscriptionId), core.getDate());
                    digestsES.add(digestES);

                    SendDigestByEmailsDTO sendDigestByEmailsDTO = new SendDigestByEmailsDTO();
                    sendDigestByEmailsDTO.setTitle(digestES.getTitle());
                    sendDigestByEmailsDTO.setText(core.getText());
                    sendDigestByEmailsDTO.setWebLink("http://crosswords-corpus.press/digests/" + digestESId);

                    List<User> subscribersWithMobileNotifications = new ArrayList<>();
                    for (var subscriptionSettings : subscription.getSubscriptionSettings()) {
                        var user = subscriptionSettings.getSubscriber();
                        if (user.getVerified() && subscriptionSettings.getSendToMail()) {
                            sendDigestByEmailsDTO.getRecipients().add(user.getEmail());
                        }
                        if (subscriptionSettings.getMobileNotifications()) {
                            subscribersWithMobileNotifications.add(user);
                        }
                        if (user.getTelegramId() != null && user.getTelegramNotifications() && subscriptionSettings.getMobileNotifications()) {
                            telegramNotificationService.sendDigestNotification(
                                    user.getTelegramId(),
                                    digestESId,
                                    digestES.getTitle(),
                                    core.getText()
                            ).subscribe();
                        }
                    }
                    if (!sendDigestByEmailsDTO.getRecipients().isEmpty()) {
                        mailManService.sendEmail(sendDigestByEmailsDTO).subscribe();
                    }
                    List<FcmToken> expiredFcmTokens = new ArrayList<>();
                    var fcmTokens = fcmTokenService.getTokensByUsers(subscribersWithMobileNotifications);
                    for (var fcmToken : fcmTokens) {
                        var subscriptionsTitle = digestES.getTitle();
                        Map<String, String> data = new HashMap<>();
                        data.put("title", subscriptionsTitle);
                        data.put("id", digestESId);
                        firebaseMessagingService.sendNotification(fcmToken.getToken(), subscriptionsTitle, data)
                                .doOnSuccess(digestText -> {})
                                .doOnError(e -> fcmTokenService.deleteFcmTokenFormUser(fcmToken.getUser(), fcmToken.getToken()))
                                .subscribe();
                    }
                    fcmTokenService.deleteAllExpiredTokens(expiredFcmTokens);
                }
            } else {
                System.out.println("Ядро не создалось!");
            }
        }
        digestRepository.saveAll(digests);
        digestSearchRepository.saveAll(digestsES);
    }

    @Scheduled(cron = "${scheduler.cron}")
    public void scheduledDigestCreation() throws ConnectionClosedException {
        System.out.println("Scheduled digests creation is starting!");
        startOfDay = Timestamp.valueOf(LocalDate.now().minusDays(1).atStartOfDay());
        endOfDay = Timestamp.valueOf(LocalDate.now().atStartOfDay());
        templates.addAll(templateService.getAllTemplates());
        this.createNewDigests();
        System.out.println("Scheduled digests creation is ended!");
    }

    public Digest getDigestById(String digestId) {
        var ids = digestId.split("-");
        DigestId id;
        try {
            id = new DigestId(Long.valueOf(ids[0]), Long.valueOf(ids[1]));
        } catch (Exception e) {
            throw new NoSuchElementException("There is no digests with such id!");
        }
        return digestRepository.findById(id).orElseThrow(() -> new NoSuchElementException("There is no digests with such id!"));
    }

    public CertainDigestDTO getDigestByIdAndTransformIntoDTO(String digestId, User user) throws IllegalAccessException {
        CertainDigestDTO certainDigestDTO = new CertainDigestDTO();
        var digest = this.getDigestById(digestId);
        var core = digest.getCore();
        var subscription = digest.getSubscription();
        var subscriptionES = subscriptionService.getDigestSubscriptionESById(subscription.getId());
        var template = templateService.getTemplateFromId(core.getTemplate().getUuid());
        DigestSubscriptionSettings subscriptionSettings;

        if (user != null) {
            try {
                subscriptionSettings = subscriptionSettingsService.getSubscriptionSettingsBySubscriptionAndUser(subscription, user);
            } catch (NoSuchElementException e) {
                if (!subscription.getIsPublic()) {
                    throw new IllegalAccessException("You can't get PDF of this private subscription, you are not its subscriber!");
                }
                subscriptionSettings = null;
            }
            certainDigestDTO.setIsAuthed(true);
        } else {
            if (!subscription.getIsPublic()) {
                throw new IllegalAccessException("You can't get PDF of this private subscription, you are not its subscriber!");
            }
            subscriptionSettings = null;
            certainDigestDTO.setIsAuthed(null);
        }


        certainDigestDTO.setId(digestId);

        certainDigestDTO.setTitle(subscriptionES.getTitle());

        var avrRating = ratingService.getCoresAverageRating(core);
        certainDigestDTO.setAverageRating(avrRating == null ? -1 : avrRating);
        certainDigestDTO.setUserRating(ratingService.getCoresUsersRating(core, user));

        for (var source : template.getSources()) {
            certainDigestDTO.getSources().add(source.getRussianName());
        }
        for (var tag : template.getTags()) {
            certainDigestDTO.getTags().add(tag.getName());
        }

        certainDigestDTO.setDescription(subscription.getDescription());
        certainDigestDTO.setText(core.getText());

        certainDigestDTO.setDate(core.getDate());

        certainDigestDTO.setIsPublic(subscription.getIsPublic());

        String ownersUsername = subscription.getOwner().getUsername();
        certainDigestDTO.setIsOwner(user == null || Objects.equals(user.getUsername(), ownersUsername));
        certainDigestDTO.setOwner(ownersUsername);

        for (var doc : core.getDocs()) {
            BasedOnDTO basedOnDTO = new BasedOnDTO(doc.getId(), docService.getDocTitleByDocId(doc.getId()), doc.getUrl());
            certainDigestDTO.getBasedOn().add(basedOnDTO);
        }
        if (subscriptionSettings == null) {
            certainDigestDTO.setSubscribeOptions(new GetSubscribeOptionsDTO(false, false, false));
        } else {
            certainDigestDTO.setSubscribeOptions(new GetSubscribeOptionsDTO(subscriptionSettings.getSendToMail(), subscriptionSettings.getMobileNotifications(), true));
        }

        return certainDigestDTO;
    }

    public DigestsDTO getAllAvailableDigests(User user, Integer pageNumber, Integer matchesPerPage) {
        if (user == null) {
           return this.getAllPublicDigests(pageNumber, matchesPerPage);
        }
        pageNumber = pageNumber == null || pageNumber < 0 ? 0 : pageNumber;
        matchesPerPage = matchesPerPage == null || matchesPerPage <= 0 ? 20 : matchesPerPage;
        Pageable pageable = PageRequest.of(pageNumber, matchesPerPage);
        Slice<Digest> digestPage = digestRepository.findAllUsersDigests(user.getId(), pageable);
        return this.transformDigestsIntoDigestsDTO(digestPage.getContent(), user, digestPage.hasNext() ? pageNumber + 1 : -1);
    }

    public void rateDigestCoreByDigestId(String id, Integer digestCoreRating, User user) {
        if (digestCoreRating != null && (digestCoreRating < 1 || digestCoreRating > 5)) {
            throw new IllegalArgumentException("Rating's arguments must be in range of 1 to 5!");
        }
        var digest = this.getDigestById(id);
        var core = digest.getCore();
        user = userService.loadUserById(user.getId());
        ratingService.createRating(core, user, digestCoreRating);
    }

    private List<String> filterDigestsAndGetESIds(Collection<Digest> searchResult, Timestamp dateFrom, Timestamp dateTo, List<String> tags, List<String> sources, Boolean subscribeOnly, User user) {
        List<String> digests = new ArrayList<>();
        for (var digest : searchResult) {
            if (this.equalsMetaData(dateFrom, dateTo, tags, sources, subscribeOnly, digest, user)) {
                String digestESId = digest.getCore().getId() + "-" + digest.getSubscription().getId();
                digests.add(digestESId);
            }
        }
        return digests;
    }

    public DigestsDTO getDigestsBySearchAndTransformIntoDTO(String searchBody, Timestamp dateFrom, Timestamp dateTo, List<String> tags, List<String> sources, Boolean subscribeOnly, User user, Integer pageNumber, Integer matchesPerPage) {
        pageNumber = pageNumber == null || pageNumber < 0 ? 0 : pageNumber;
        matchesPerPage = matchesPerPage == null || matchesPerPage <= 0 ? 10 : matchesPerPage;
        if (dateFrom != null && dateTo != null && dateFrom.after(dateTo)) {
            var nothing = dateFrom;
            dateFrom = dateTo;
            dateTo = nothing;
        }
        NativeSearchQueryBuilder searchQueryBuilder = new NativeSearchQueryBuilder();
        var filtersIds = this.filterDigestsAndGetESIds(digestRepository.findAll(), dateFrom, dateTo, tags, sources, subscribeOnly, user);
        IdsQueryBuilder filterBuilder = QueryBuilders.idsQuery().addIds(filtersIds.toArray(new String[0]));
        List<Digest> digests;
        if (searchBody != null) {
            QueryBuilder queryBuilder = QueryBuilders.matchPhraseQuery("title", searchBody);
            BoolQueryBuilder boolQuery = QueryBuilders.boolQuery()
                    .must(filterBuilder)
                    .must(queryBuilder);
            searchQueryBuilder.withFilter(boolQuery);
        } else {
            searchQueryBuilder.withFilter(filterBuilder);
        }
        var searchQuery = searchQueryBuilder
                .withPageable(PageRequest.of(pageNumber, matchesPerPage))
                .withSort(Sort.by(Sort.Order.desc("_score"), Sort.Order.desc("date")))
                .build();
        var searchHits = elasticsearchOperations.search(searchQuery, DigestES.class, IndexCoordinates.of("digest"));
        digests = new ArrayList<>();
        searchHits.forEach(hit ->
        {
            var digestES = hit.getContent();
            var ids = digestES.getId().split("-");
            var id = new DigestId(Long.valueOf(ids[0]), Long.valueOf(ids[1]));
            var digest = digestRepository.findById(id).orElseThrow(() -> new NoSuchElementException("There is no digests with such id!"));
            digests.add(digest);
        });
        int nextPage = pageNumber + 1;
        if (searchHits.getTotalHits() <= (long) nextPage * matchesPerPage) {
            nextPage = -1;
        }
        return this.transformDigestsIntoDigestsDTO(digests, user, nextPage);
    }
    private DigestsDTO transformDigestsIntoDigestsDTO(List<Digest> digests, User user, Integer nextPage) {
        DigestsDTO digestsDTO = new DigestsDTO();
        digestsDTO.setIsAuthed(user != null);
        digestsDTO.setNextPage(nextPage);
        for (var digest : digests) {
            var core = digest.getCore();
            var subscription = digest.getSubscription();
            var subscriptionES = subscriptionService.getDigestSubscriptionESById(subscription.getId());
            var template = templateService.getTemplateFromId(core.getTemplate().getUuid());

            DigestSubscriptionSettings subscriptionSettings;
            if (user != null) {
                try {
                    subscriptionSettings = subscriptionSettingsService.getSubscriptionSettingsBySubscriptionAndUser(subscription, user);
                } catch (NoSuchElementException e) {
                    subscriptionSettings = null;
                }
            } else {
                subscriptionSettings = null;
            }

            DigestDTO digestDTO = new DigestDTO();

            digestDTO.setId(digest.getCore().getId().toString() + "-" + digest.getSubscription().getId().toString());

            digestDTO.setTitle(subscriptionES.getTitle());

            digestDTO.setAverageRating(ratingService.getCoresAverageRating(core));
            digestDTO.setUserRating(ratingService.getCoresUsersRating(core, user));

            for (var source : template.getSources()) {
                digestDTO.getSources().add(source.getRussianName());
            }
            for (var tag : template.getTags()) {
                digestDTO.getTags().add(tag.getName());
            }

            digestDTO.setDescription(subscription.getDescription());
            digestDTO.setText(core.getText());

            digestDTO.setDate(core.getDate());

            digestDTO.setIsPublic(subscription.getIsPublic());

            String ownersUsername = subscription.getOwner().getUsername();
            digestDTO.setIsOwner(user == null || Objects.equals(user.getUsername(), ownersUsername));
            digestDTO.setOwner(ownersUsername);

            for (var doc : core.getDocs()) {
                digestDTO.getUrls().add(doc.getUrl());
            }
            if (subscriptionSettings == null) {
                digestDTO.setSubscribeOptions(new GetSubscribeOptionsDTO(false, false, false));
            } else {
                digestDTO.setSubscribeOptions(new GetSubscribeOptionsDTO(subscriptionSettings.getSendToMail(), subscriptionSettings.getMobileNotifications(), true));
            }
            digestsDTO.getDigests().add(digestDTO);
        }
        return digestsDTO;
    }
    private boolean equalsMetaData(Timestamp dateFrom, Timestamp dateTo, List<String> tags, List<String> sources, Boolean subscribe_only, Digest digest, User user) {
        var core = digest.getCore();
        var subscription = digest.getSubscription();
        var template = templateService.getTemplateFromId(core.getTemplate().getUuid());
        var digestDate = core.getDate();
        Set<String> digestSources = new HashSet<>();
        for (var source : template.getSources()) {
            digestSources.add(source.getRussianName());
        }
        boolean first = ((dateFrom == null || dateFrom.before(digestDate)) &&
                (dateTo == null || dateTo.after(digestDate)) &&
                (sources == null || sources.isEmpty() || digestSources.containsAll(sources)) &&
                (tags == null || tags.isEmpty() || tagService.getSetOfTagsNames(template.getTags()).containsAll(tags)));

        DigestSubscriptionSettings subscriptionSettings;
        if (user != null) {
            try {
                subscriptionSettings = subscriptionSettingsService.getSubscriptionSettingsBySubscriptionAndUser(subscription, user);
            } catch (NoSuchElementException e) {
                subscriptionSettings = null;
            }
        } else {
            subscriptionSettings = null;
        }

        boolean second = false;
        if (subscription.getIsPublic()) {
            if (subscribe_only) {
                second = subscriptionSettings != null;
            } else {
                second = true;
            }
        } else {
            second = subscriptionSettings != null;
        }
        return first && second;
    }

    public Pair<DigestSubscriptionSettingsDTO, Boolean> updateDigestSubscriptionSettingsForUserByDigestId(String id, DigestSubscriptionSettingsDTO subscriptionSettingsDTO, User user) {
        var digest = this.getDigestById(id);
        var subscription = digest.getSubscription();
        var resultPair = subscriptionSettingsService.updateDigestSubscriptionSettingsForUser(subscription, user, subscriptionSettingsDTO);
        resultPair.setSecond(subscriptionService.checkDigestSubscriptionDeletion(resultPair.getSecond(), subscription, user));
        return resultPair;
    }

    public List<String> getAllDigestSubscriptionsUsersExceptOwner(String id) {
        var digest = this.getDigestById(id);
        var subscription = digest.getSubscription();
        return subscriptionSettingsService.getAllDigestSubscriptionsUsersUsernamesExceptOwner(subscription);
    }

    public void changeDigestSubscriptionsOwnerByDigestId(User user, String id, String owner) throws IllegalAccessException {
        var digest = this.getDigestById(id);
        var subscriptionId = digest.getSubscription().getId();
        subscriptionService.changeDigestSubscriptionsOwner(user, subscriptionId, owner);
    }

    public CheckAccessDTO checkUsersAccessToDigestByIdAndConvertIntoDTO(String id, User user) {
        var digest = this.getDigestById(id);
        return subscriptionService.checkUsersAccessToSubscriptionByIdAndConvertIntoDTO(digest.getSubscription().getId(), user);
    }

    public DigestsDTO getAllPublicDigests(Integer pageNumber, Integer matchesPerPage) {
        pageNumber = pageNumber == null || pageNumber < 0 ? 0 : pageNumber;
        matchesPerPage = matchesPerPage == null || matchesPerPage <= 0 ? 20 : matchesPerPage;
        Pageable pageable = PageRequest.of(pageNumber, matchesPerPage);
        Slice<Digest> digestPage = digestRepository.findAllPublicDigests(pageable);
        return this.transformDigestsIntoDigestsDTO(digestPage.getContent(), null, digestPage.hasNext() ? pageNumber + 1 : -1);
    }

    public DigestsDTO getAllUsersPrivateDigests(User user, Integer pageNumber, Integer matchesPerPage) {
        pageNumber = pageNumber == null || pageNumber < 0 ? 0 : pageNumber;
        matchesPerPage = matchesPerPage == null || matchesPerPage <= 0 ? 20 : matchesPerPage;
        Pageable pageable = PageRequest.of(pageNumber, matchesPerPage);
        Slice<Digest> digestPage = digestRepository.findAllPrivateUsersDigests(user.getId(), pageable);
        return this.transformDigestsIntoDigestsDTO(digestPage.getContent(), user, digestPage.hasNext() ? pageNumber + 1 : -1);
    }

    public void deleteDigestById(String digestId) throws NoSuchElementException {
        var digest = this.getDigestById(digestId);
        digestRepository.delete(digest);
    }

    @Transactional
    public void updateDigestById(String id, UpdateDigestDTO updateDigestDTO) throws NoSuchElementException {
        var digest = this.getDigestById(id);
        var core = digest.getCore();
        core.setText(updateDigestDTO.getText());
        coreRepository.save(core);
        digestRepository.save(digest);
    }

    /*public void deleteDigestById(String digestId) {
        var digest = this.getDigestById(digestId);
    }*/
}
