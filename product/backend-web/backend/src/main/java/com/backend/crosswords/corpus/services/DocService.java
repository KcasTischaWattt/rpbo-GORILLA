package com.backend.crosswords.corpus.services;

import com.backend.crosswords.admin.enums.AuthorityEnum;
import com.backend.crosswords.admin.models.CrosswordUserDetails;
import com.backend.crosswords.admin.models.User;
import com.backend.crosswords.admin.services.UserService;
import com.backend.crosswords.corpus.dto.*;
import com.backend.crosswords.corpus.enums.Language;
import com.backend.crosswords.corpus.enums.Source;
import com.backend.crosswords.corpus.models.*;
import com.backend.crosswords.corpus.repositories.jpa.DocMetaRepository;
import com.backend.crosswords.corpus.repositories.elasticsearch.DocSearchRepository;
import org.modelmapper.ModelMapper;
import org.opensearch.common.unit.Fuzziness;
import org.opensearch.data.client.orhlc.NativeSearchQueryBuilder;
import org.opensearch.index.query.QueryBuilder;
import org.opensearch.index.query.QueryBuilders;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.firewall.RequestRejectedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DocService {
    private final ModelMapper modelMapper;
    private final DocSearchRepository docSearchRepository;
    private final DocMetaRepository docMetaRepository;
    private final ElasticsearchOperations elasticsearchOperations;
    private final TagService tagService;
    private final DocRatingService ratingService;
    private final UserService userService;
    private final PackageService packageService;
    private final AnnotationService annotationService;
    private final CommentService commentService;

    public DocService(ModelMapper modelMapper, DocSearchRepository docSearchRepository, DocMetaRepository docMetaRepository, ElasticsearchOperations elasticsearchOperations, TagService tagService, DocRatingService ratingService, UserService userService, PackageService packageService, AnnotationService annotationService, CommentService commentService) {
        this.modelMapper = modelMapper;
        this.docSearchRepository = docSearchRepository;
        this.docMetaRepository = docMetaRepository;
        this.elasticsearchOperations = elasticsearchOperations;
        this.tagService = tagService;
        this.ratingService = ratingService;
        this.userService = userService;
        this.packageService = packageService;
        this.annotationService = annotationService;
        this.commentService = commentService;
    }

    private DocDTO transformDocIntoDocDTO(DocMeta docMeta, Boolean includeAnnotations) {
        var docDTO = modelMapper.map(docMeta, DocDTO.class);
        var docES = docSearchRepository.findById(docDTO.getId()).orElseThrow();
        User user;
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            CrosswordUserDetails crosswordUserDetails = (CrosswordUserDetails) authentication.getPrincipal();
            user = crosswordUserDetails.getUser();
            docDTO.setFavourite(packageService.checkDocInFavourites(user, docMeta));
            var rating = ratingService.getRatingsForDocumentByUser(docMeta, user);
            docDTO.setRatingSummary(rating.get(0));
            docDTO.setRatingClassification(rating.get(1));
            docDTO.setAuthed(true);
            boolean moderator = false;
            if (user.getRole() != null && user.getRole().getAuthorities() != null) {
                for (var authority : user.getRole().getAuthorities()) {
                    if (authority.equals(AuthorityEnum.EDIT_DELETE_DOCS)) {
                        moderator = true;
                        break;
                    }
                }
            }

            docDTO.setIsModerator(moderator);
        } catch (ClassCastException e) {
            docDTO.setFavourite(null);
            docDTO.setRatingClassification(null);
            docDTO.setRatingSummary(null);
            docDTO.setAuthed(false);
            docDTO.setIsModerator(false);
            user = null;
        }
        docDTO.setTagNames(new ArrayList<>());
        for (var tag : docMeta.getTags()) {
            docDTO.getTagNames().add(tag.getName());
        }
        docDTO.setText(docES.getText());
        docDTO.setTitle(docES.getTitle());
        docDTO.setRusSource(docMeta.getSource().getRussianName());
        if (includeAnnotations != null && includeAnnotations && user != null) {
            docDTO.setDocsAnnotations(new ArrayList<>());
            for (var annotation : docMeta.getAnnotations()) {
                if (user.getId().equals(annotation.getOwner().getId())) {
                    docDTO.getDocsAnnotations().add(modelMapper.map(annotation, AnnotationDTO.class));
                }
            }
        }
        return docDTO;
    }

    @Transactional
    public void createDoc(CreateDocDTO createDocDTO) throws IllegalArgumentException {
        var docMeta = modelMapper.map(createDocDTO, DocMeta.class);
        if (docMeta.getLanguage() == null) {
            throw new IllegalArgumentException("Language is unappropriated or null!");
        }
        docMeta.setSource(Source.fromRussianName(createDocDTO.getRusSource()));
        Timestamp timeNow = new Timestamp(System.currentTimeMillis());
        if (createDocDTO.getDate() == null) {
            docMeta.setDate(timeNow);
        }
        docMeta.setLastEdit(timeNow);

        docMetaRepository.save(docMeta);
        tagService.getTagsInNamesAndSaveForDoc(createDocDTO.getTagDTOs(), docMeta);

        docMeta = docMetaRepository.save(docMeta);
        var docES = modelMapper.map(createDocDTO, DocES.class);
        docES.setId(docMeta.getId());
        docSearchRepository.save(docES);
    }

    public List<DocDTO> getAllDocs() {
        List<DocDTO> result = new ArrayList<>();
        for (var docMeta : docMetaRepository.findAll()) {
            result.add(this.transformDocIntoDocDTO(docMeta, false));
        }
        return result;
    }

    public DocDTO getDocByIdAndTransformIntoDTO(Long id, Boolean includeAnnotations) {
        var docMeta = docMetaRepository.findById(id).orElseThrow(() -> new NoSuchElementException("No documents with such id!"));
        return this.transformDocIntoDocDTO(docMeta, includeAnnotations);
    }

    private SearchResultDTO formSearchResultDTO(int pageNumber, List<DocDTO> resultHits) {
        boolean isAuthed = true;
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            CrosswordUserDetails crosswordUserDetails = (CrosswordUserDetails) authentication.getPrincipal();
        } catch (ClassCastException e) {
            isAuthed = false;
        }
        return new SearchResultDTO(pageNumber, isAuthed, resultHits);
    }

    public SearchResultDTO searchDocsAndTransformIntoDTO(SearchDocDTO searchDocDTO, User user) {
        List<DocDTO> resultHits = new ArrayList<>();
        var searchQueryBuilder = new NativeSearchQueryBuilder();
        QueryBuilder queryBuilder = null;
        // TODO либо продумать новую функцию скоринга, либо отказаться от него
        float minScore = 1F;
        if (searchDocDTO.getDateFrom() != null && searchDocDTO.getDateTo() != null && searchDocDTO.getDateFrom().after(searchDocDTO.getDateTo())) {
            var nothing = searchDocDTO.getDateFrom();
            searchDocDTO.setDateFrom(searchDocDTO.getDateTo());
            searchDocDTO.setDateTo(nothing);
        }
        List<Language> languages = searchDocDTO.getLanguage() != null && !searchDocDTO.getLanguage().isEmpty()
                ? searchDocDTO.getLanguage().stream()
                .map(Language::valueOf).toList()
                : null;

        List<Source> sources = searchDocDTO.getSources() != null && !searchDocDTO.getSources().isEmpty()
                ? searchDocDTO.getSources().stream()
                .map(Source::fromRussianName) // Ваш метод конвертации
                .toList()
                : null;
        var tags = searchDocDTO.getTags();
        var folders = searchDocDTO.getFolders();
        List<Long> filtersIds = docMetaRepository.findFilteredDocIds(
                searchDocDTO.getDateFrom(),
                searchDocDTO.getDateTo(),
                languages,
                sources,
                tags == null || tags.isEmpty() ? null : tags,
                folders == null || folders.isEmpty() ? null : folders,
                user == null ? null : user.getId()
        );
        if (searchDocDTO.getPageNumber() == null || searchDocDTO.getPageNumber() < 0) {
            searchDocDTO.setPageNumber(0);
        }
        if (searchDocDTO.getMatchesPerPage() == null || searchDocDTO.getMatchesPerPage() <= 0) {
            searchDocDTO.setMatchesPerPage(10);
        }
        QueryBuilder filterBuilder = QueryBuilders.termsQuery("id", filtersIds);
        searchQueryBuilder.withFilter(filterBuilder);
        String field = searchDocDTO.getSearchInText() != null && searchDocDTO.getSearchInText() ? "text" : "title";
        if (searchDocDTO.getSearchTerm() != null && !searchDocDTO.getSearchTerm().equals("") && searchDocDTO.getSearchMode() != null)
        {
            switch (searchDocDTO.getSearchMode()) {
                case "id" -> {
                    try {
                        resultHits.add(this.getDocByIdAndTransformIntoDTO(Long.parseLong(searchDocDTO.getSearchTerm()), false));
                    } catch (IllegalArgumentException | NoSuchElementException e) {
                        return this.formSearchResultDTO(-1, resultHits);
                    }
                    return this.formSearchResultDTO(-1, resultHits);
                }
                case "semantic", "syntax" -> {
                    if (searchDocDTO.getMatchesPerPage() <= 0) {
                        return this.formSearchResultDTO(-1, resultHits);
                    }
                    queryBuilder = QueryBuilders.boolQuery()
                            .should(QueryBuilders.matchQuery(field, searchDocDTO.getSearchTerm()).fuzziness(Fuzziness.AUTO))
                            .should(QueryBuilders.matchPhraseQuery(field, searchDocDTO.getSearchTerm())).boost(2.0F);
                    /*float percentage = searchDocDTO.getApprovalPercentage() == null ? 0.5F : searchDocDTO.getApprovalPercentage();
                    minScore = searchDocDTO.getSearchTerm().split(" ").length * percentage;*/
                }
                case "certain", "exact" -> {
                    queryBuilder = QueryBuilders.matchPhraseQuery(field, searchDocDTO.getSearchTerm());
                }
                default -> throw new IllegalArgumentException("There is no such search modes!");
            }
            searchQueryBuilder.withQuery(queryBuilder);
            //searchQueryBuilder.withMinScore(minScore);
        }
        int pageNumber = searchDocDTO.getPageNumber() == null ? 0 : searchDocDTO.getPageNumber();
        int matchesPerPage = searchDocDTO.getMatchesPerPage() == null ? 10 : searchDocDTO.getMatchesPerPage();
        var searchQuery = searchQueryBuilder
                .withPageable(PageRequest.of(pageNumber, matchesPerPage))
                .withSort(Sort.by(Sort.Order.desc("_score"), Sort.Order.desc("date")))
                .build();
        try {
            var searchHits = elasticsearchOperations.search(searchQuery, DocES.class, IndexCoordinates.of("document"));
            searchHits.forEach(hit ->
            {
                var docES = hit.getContent();
                var docMeta = docMetaRepository.findById(docES.getId()).orElseThrow();
                resultHits.add(this.transformDocIntoDocDTO(docMeta, false));
            });
            int nextPage = pageNumber + 1;
            if (searchHits.getTotalHits() <= (long) nextPage * matchesPerPage) {
                nextPage = -1;
            }
            return this.formSearchResultDTO(nextPage, resultHits);
        } catch (NoSuchElementException e) {
            return this.formSearchResultDTO(-1, resultHits);
        }
    }

    private boolean equalsMetaData(DocMeta doc, SearchDocDTO searchDocDTO, User user) {
        if (searchDocDTO == null) {
            return true;
        }
        if( (searchDocDTO.getDateFrom() == null || searchDocDTO.getDateFrom().before(doc.getDate())) &&
                (searchDocDTO.getDateTo() == null || searchDocDTO.getDateTo().after(doc.getDate())) &&
                (searchDocDTO.getLanguage() == null || searchDocDTO.getLanguage().isEmpty() || searchDocDTO.getLanguage().contains(doc.getLanguage().toString())) &&
                (searchDocDTO.getSources() == null || searchDocDTO.getSources().isEmpty() || searchDocDTO.getSources().contains(doc.getSource().getRussianName())) &&
                (searchDocDTO.getTags() == null || searchDocDTO.getTags().isEmpty() || tagService.getSetOfTagsNames(doc.getTags()).containsAll(searchDocDTO.getTags()))) {
            if (searchDocDTO.getFolders() != null && !searchDocDTO.getFolders().isEmpty() && user != null) {
                for (var packageName : searchDocDTO.getFolders()) {
                    if (packageService.getPackageByName(packageName, user).getDocs().contains(doc)) {
                        return true;
                    }
                }
                return false;
            }
            return true;
        }
        return false;
    }

    public void deleteDocById(Long id) {
        var checkDocMeta = docMetaRepository.findById(id);
        if (checkDocMeta.isEmpty()) {
            throw new NoSuchElementException("There is no documents in Postgres with such id!");
        }
        var docMeta = docMetaRepository.findById(id).orElseThrow();
        tagService.removeTagsFromDoc(docMeta);
        packageService.removeDocFromPackages(docMeta);

        docMetaRepository.delete(docMeta);
        // учесть, что ES не понимает @Transactional и в случае ошибки не откатит изменения обратно, поэтому может произойти повреждение данных в ES
        var checkDocEs = docSearchRepository.findById(id);
        if (checkDocEs.isEmpty()) {
            throw new NoSuchElementException("There is no documents in ES with such id!");
        }
        var docES = docSearchRepository.findById(id).orElseThrow();
        docSearchRepository.delete(docES);
    }

    public void rateDocById(Long id, RateDocDTO rateDocDTO) {
        if (rateDocDTO == null) {
            return;
        }
        if (rateDocDTO.getClassificationRating() != null) {
            if (rateDocDTO.getClassificationRating() < 1 || rateDocDTO.getClassificationRating() > 5) {
                throw new IllegalArgumentException("Classification rating's arguments must be in range of 1 to 5!");
            }
        }
        if (rateDocDTO.getSummaryRating() != null) {
            if (rateDocDTO.getSummaryRating() < 1 || rateDocDTO.getSummaryRating() > 5) {
                throw new IllegalArgumentException("Summary rating's arguments must be in range of 1 to 5!");
            }
        }
        DocMeta doc;

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CrosswordUserDetails crosswordUserDetails = (CrosswordUserDetails) authentication.getPrincipal();
        User user = crosswordUserDetails.getUser();

        doc = docMetaRepository.findById(id).orElseThrow();
        docSearchRepository.findById(id).orElseThrow(); // проверка, что базы данных синхронизированы
        user = userService.loadUserById(user.getId());

        ratingService.createRating(doc, user, rateDocDTO.getSummaryRating(), rateDocDTO.getClassificationRating());
    }

    @Transactional
    public ResponseEntity<HttpStatus> editDocById(Long id, EditDocDTO editDocDTO) {
        var checkDocMeta = docMetaRepository.findById(id);
        if (checkDocMeta.isEmpty()) {
            throw new NoSuchElementException("There is no documents in Postgres with such id!");
        }
        DocMeta docMeta = checkDocMeta.get();

        var checkDocES = docSearchRepository.findById(id);
        if (checkDocES.isEmpty()) {
            throw new NoSuchElementException("There is no documents in ES with such id!");
        }
        DocES docES = checkDocES.get();

        docMeta.setSource(Source.fromRussianName(editDocDTO.getRusSource()));
        docMeta.setLanguage(Language.getValueOf(editDocDTO.getLanguage()));
        docMeta.setDate(editDocDTO.getDate());
        docMeta.setSummary(editDocDTO.getSummary());
        docMeta.setUrl(editDocDTO.getUrl());

        docES.setText(editDocDTO.getText());
        docES.setTitle(editDocDTO.getTitle());

        if ((editDocDTO.getDropRatingSummary() || editDocDTO.getDropRatingClassification()) && docMeta.getRatings() != null) {
            ratingService.dropRatings(docMeta.getRatings(), editDocDTO.getDropRatingSummary(), editDocDTO.getDropRatingClassification());
        }

        tagService.removeTagsFromDoc(docMeta);
        docMetaRepository.save(docMeta);
        tagService.getTagsInNamesAndSaveForDoc(editDocDTO.getTagDTOs(), docMeta);

        docMeta.setLastEdit(new Timestamp(System.currentTimeMillis()));

        docMetaRepository.save(docMeta);
        docSearchRepository.save(docES);

        return ResponseEntity.ok(HttpStatus.OK);
    }

    @Deprecated
    public void deleteDocumentsItself() {
        List<IndexOperations> indexOpsList = List.of(
                elasticsearchOperations.indexOps(IndexCoordinates.of("document")),
                elasticsearchOperations.indexOps(IndexCoordinates.of("digest_subscription")),
                elasticsearchOperations.indexOps(IndexCoordinates.of("digest"))
        );
        for (var indexOps : indexOpsList) {
            if (indexOps.exists()) {
                boolean isDeleted = indexOps.delete();
                if (isDeleted) {
                } else {
                    throw new RequestRejectedException("Failed to delete index '" + "document" + "'.");
                }
            } else {
                throw new NoSuchElementException("Index '" + "document" + "' does not exist.");
            }
        }
    }

    public void addDocByIdIntoPackageByName(User user, Long docId, String packageName) {
        var check = docMetaRepository.findById(docId);
        if (check.isEmpty()) {
            throw new NoSuchElementException("There is no documents with such id!");
        }
        var doc = check.get();
        packageService.addDocIntoPackageByName(user, doc, packageName);
    }

    public void removeDocByIdFromPackageByName(User user, Long docId, String packageName) {
        var check = docMetaRepository.findById(docId);
        if (check.isEmpty()) {
            throw new NoSuchElementException("There is no documents with such id!");
        }
        var doc = check.get();
        packageService.removeDocFromPackageByName(user, doc, packageName);
    }

    public void addDocToFavouritesById(Long id) {
        var docMeta = docMetaRepository.findById(id).orElseThrow();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CrosswordUserDetails crosswordUserDetails = (CrosswordUserDetails) authentication.getPrincipal();
        packageService.addDocToFavourites(crosswordUserDetails.getUser(), docMeta);
    }

    public void removeDocFromFavouritesById(Long id) {
        var docMeta = docMetaRepository.findById(id).orElseThrow();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CrosswordUserDetails crosswordUserDetails = (CrosswordUserDetails) authentication.getPrincipal();
        packageService.removeDocFromFavourites(crosswordUserDetails.getUser(), docMeta);
    }

    @Transactional
    public Long annotateDocById(User user, Long id, CreateUpdateAnnotationDTO createUpdateAnnotationDTO) {
        var docMeta = docMetaRepository.findById(id).orElseThrow(() -> new NoSuchElementException("There is no documents with such id!"));
        Annotation annotation = annotationService.createAnnotation(user, docMeta, createUpdateAnnotationDTO);
        docMeta.getAnnotations().add(annotation);
        docMetaRepository.save(docMeta);
        return annotation.getId();
    }

    @Transactional
    public void deleteAnnotationByIdFromDocById(User user, Long docId, Long annotationId) {
        var docMeta = docMetaRepository.findById(docId).orElseThrow(() -> new NoSuchElementException("There is no documents with such id!"));
        annotationService.deleteAnnotationByIdFromDoc(user, docMeta, annotationId);
        docMetaRepository.save(docMeta);
    }

    @Transactional
    public Comment commentDocById(User user, Long id, CreateUpdateCommentDTO createUpdateCommentDTO) {
        var docMeta = docMetaRepository.findById(id).orElseThrow(() -> new NoSuchElementException("There is no documents with such id!"));
        Comment comment = commentService.commentDoc(user, docMeta, createUpdateCommentDTO);
        docMeta.getComments().add(comment);
        docMetaRepository.save(docMeta);
        return comment;
    }

    @Transactional
    public void deleteCommentByIdFromDocById(User user, Long docId, Long commentId) {
        var docMeta = docMetaRepository.findById(docId).orElseThrow(() -> new NoSuchElementException("There is no documents with such id!"));
        commentService.deleteCommentByIdFromDoc(user, docMeta, commentId);
        docMetaRepository.save(docMeta);
    }

    public List<PackagesForDocDTO> getPackagesForDocAndTransformIntoDTO(Long id, User user) {
        var docMeta = docMetaRepository.findById(id).orElseThrow(() -> new NoSuchElementException("There is no documents with such id!"));
        List<PackagesForDocDTO> docsPackages = new ArrayList<>();
        for (var usersPackage : packageService.getPackagesForUser(user)) {
            PackagesForDocDTO getPackagesForDocDTO = new PackagesForDocDTO();
            getPackagesForDocDTO.setName(usersPackage.getId().getName());
            getPackagesForDocDTO.setIsIncluded(usersPackage.getDocs().contains(docMeta));
            docsPackages.add(getPackagesForDocDTO);
        }
        return docsPackages;
    }

    public List<Comment> getAllUsersCommentsFromDocById(User user, Long docId) {
        return commentService.getAllUsersCommentsFromDoc(user, docMetaRepository.findById(docId).orElseThrow(
                () -> new NoSuchElementException("There is no documents with such id!")));
    }

    @Transactional
    public Comment updateCommentByIdForDocById(User user, Long docId, Long commentId, CreateUpdateCommentDTO createUpdateCommentDTO) {
        var docMeta = docMetaRepository.findById(docId).orElseThrow(() -> new NoSuchElementException("There is no documents with such id!"));
        Comment comment = commentService.updateCommentByIdForDoc(user, docMeta, commentId, createUpdateCommentDTO);
        docMetaRepository.save(docMeta);
        return comment;
    }

    @Transactional
    public void updateAnnotationByIdForDocById(User user, Long docId, Long annotationId, CreateUpdateAnnotationDTO createUpdateAnnotationDTO) {
        var docMeta = docMetaRepository.findById(docId).orElseThrow(() -> new NoSuchElementException("There is no documents with such id!"));
        annotationService.updateAnnotationByIdForDoc(user, docMeta, annotationId, createUpdateAnnotationDTO);
        docMetaRepository.save(docMeta);
    }

    public List<DocMeta> getAllDocsByTemplateForToday(DigestTemplate template) {
        List<DocMeta> docs = new ArrayList<>();
        for (var doc : docMetaRepository.findAllWithTagsForToday(DigestService.startOfDay, DigestService.endOfDay)) {
            Boolean first = (template.getTags().isEmpty() || tagService.getSetOfTagsNames(doc.getTags()).stream().anyMatch(tagService.getSetOfTagsNames(template.getTags())::contains));
            Boolean second = (template.getSources().isEmpty() || template.getSources().contains(doc.getSource()));
            if (first && second) {
                docs.add(doc);
            }
        }
        return docs;
    }

    public String getDocTextByDocId(Long id) {
        return docSearchRepository.findById(id).orElseThrow(() -> new NoSuchElementException("There is no documents with such id!")).getText();
    }

    public String getDocTitleByDocId(Long id) {
        return docSearchRepository.findById(id).orElseThrow(() -> new NoSuchElementException("There is no documents with such id!")).getTitle();
    }

    public List<DocDTO> transformDocsIntoDTO(Set<DocMeta> docs, Boolean includeAnnotations) {
        List<DocDTO> docDTOs = new ArrayList<>();
        for (var doc : docs) {
            docDTOs.add(this.transformDocIntoDocDTO(doc, includeAnnotations));
        }
        return docDTOs;
    }

    @Transactional
    public void setCoreForDocs(DigestCore core, List<DocMeta> docMetas) {
        docMetas = docMetaRepository.findDocMetasWithCores(docMetas);
        for (var docMeta : docMetas) {
            docMeta.getDigestCores().add(core);
            docMetaRepository.save(docMeta);
        }
    }
}
