package com.backend.crosswords.corpus.services;

import com.backend.crosswords.admin.models.User;
import com.backend.crosswords.admin.services.UserService;
import com.backend.crosswords.config.Pair;
import com.backend.crosswords.corpus.dto.DigestSubscriptionSettingsDTO;
import com.backend.crosswords.corpus.dto.FollowerDTO;
import com.backend.crosswords.corpus.dto.UpdateDigestSubscriptionDTO;
import com.backend.crosswords.corpus.models.DigestSubscription;
import com.backend.crosswords.corpus.models.DigestSubscriptionSettings;
import com.backend.crosswords.corpus.models.DigestSubscriptionSettingsId;
import com.backend.crosswords.corpus.repositories.jpa.DigestSubscriptionSettingsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class DigestSubscriptionSettingsService {
    private final DigestSubscriptionSettingsRepository subscriptionSettingsRepository;
    private final UserService userService;

    public DigestSubscriptionSettingsService(DigestSubscriptionSettingsRepository subscriptionSettingsRepository, UserService userService) {
        this.subscriptionSettingsRepository = subscriptionSettingsRepository;
        this.userService = userService;
    }

    @Transactional
    public void setSubscribersForSubscription(List<String> subscribers, DigestSubscription subscription) {
        User owner = subscription.getOwner();
        User user = owner;
        List<DigestSubscriptionSettings> userSettingsList = new ArrayList<>();
        for (int i = -1; i < subscribers.size(); ++i) {
            if (i != -1) {
                user = userService.getUserByUsername(subscribers.get(i));
                if (!owner.getId().equals(user.getId())) {
                    userSettingsList.add(this.setParametersForSubscriptionSettingsAndReturn(subscription, user));
                }
            } else {
                userSettingsList.add(this.setPersonalParametersForSubscriptionSettingsAndReturn(subscription, user));
            }

        }
        subscriptionSettingsRepository.saveAll(userSettingsList);
    }

    public DigestSubscriptionSettings getSubscriptionSettingsBySubscriptionAndUser(DigestSubscription subscription, User user) {
        return subscriptionSettingsRepository.findById(new DigestSubscriptionSettingsId(subscription.getId(), user.getId())).orElseThrow(() -> new NoSuchElementException("There is no settings between these user and subscription!"));
    }

    @Transactional
    public void setNewSubscribersForSubscription(UpdateDigestSubscriptionDTO updateDigestSubscriptionDTO, DigestSubscription subscription) {
        List<FollowerDTO> followerDTOs = updateDigestSubscriptionDTO.getFollowers();
        var oldFollowers = subscriptionSettingsRepository.findAllByDigestSubscription(subscription);
        oldFollowers.sort(Comparator.comparing(a -> a.getSubscriber().getUsername()));
        List<User> followers = new ArrayList<>();
        for (var followerDTO : followerDTOs) {
            var user = userService.getUserByUsername(followerDTO.getUsername());
            followers.add(user);
        }
        followers.sort(Comparator.comparing(User::getUsername));
        List<User> newFollowers = new ArrayList<>();
        List<DigestSubscriptionSettings> oldSubscriptionSettings = new ArrayList<>();
        List<DigestSubscriptionSettings> userSettingsList = new ArrayList<>();
        int i;
        int j;
        for (i = 0, j = 0; i < oldFollowers.size() && j < followers.size(); ) {
            var oldFollower = oldFollowers.get(i).getSubscriber();
            var follower = followers.get(j);
            var compare = oldFollower.getUsername().compareTo(follower.getUsername());
            if (compare == 0) {
                var oldSettings = subscriptionSettingsRepository.findById(new DigestSubscriptionSettingsId(subscription.getId(), oldFollower.getId())).orElseThrow(
                        () ->  new NoSuchElementException("There is no settings between these user and subscription!"));
                var newSettings = updateDigestSubscriptionDTO.getSubscribeOptions();
                boolean newMobileNotifications = oldFollower.getMobileNotifications() ? subscription.getMobileNotifications() : false;
                boolean newSendToMail = oldFollower.getSendToMail() ? subscription.getSendToMail() : false;
                if (newSettings.getMobileNotifications() != oldSettings.getMobileNotifications() || newSettings.getSendToMail() != oldSettings.getSendToMail()) {
                    boolean flag = false; // флаг, показывающий, что хотя бы что-то изменилось в настройках и это нужно сохранить
                    if (!oldSettings.getEdited() || newSettings.getMobileNotifications() != oldSettings.getMobileNotifications()) {
                        oldSettings.setMobileNotifications(newMobileNotifications);
                        flag = true;
                    }
                    if (!oldSettings.getEdited() || newSettings.getSendToMail() != oldSettings.getSendToMail()) {
                        oldSettings.setSendToMail(newSendToMail);
                        flag = true;
                    }
                    if (flag) {
                        userSettingsList.add(oldSettings);
                    }
                }
                ++i;
                ++j;
            } else if (compare > 0) {
                newFollowers.add(follower);
                ++j;
            } else {
                oldSubscriptionSettings.add(subscriptionSettingsRepository
                        .findById(new DigestSubscriptionSettingsId(subscription.getId(), oldFollower.getId())).orElseThrow(
                        () -> new NoSuchElementException("There is no settings between these user and subscription!"))
                );
                ++i;
            }
        }
        for (; i < oldFollowers.size(); ++i) {
            var oldFollower = oldFollowers.get(i).getSubscriber();
            oldSubscriptionSettings.add(subscriptionSettingsRepository
                    .findById(new DigestSubscriptionSettingsId(subscription.getId(), oldFollower.getId())).orElseThrow(
                            () -> new NoSuchElementException("There is no settings between these user and subscription!"))
            );
        }
        for (; j < followers.size(); ++j) {
            var follower = followers.get(j);
            newFollowers.add(follower);
            ++j;
        }
        subscriptionSettingsRepository.deleteAll(oldSubscriptionSettings);
        for (var user : newFollowers) {
            userSettingsList.add(this.setParametersForSubscriptionSettingsAndReturn(subscription, user));
        }
        subscriptionSettingsRepository.saveAll(userSettingsList);
    }

    private DigestSubscriptionSettings setPersonalParametersForSubscriptionSettingsAndReturn(DigestSubscription subscription, User user) {
        var userSettings = new DigestSubscriptionSettings();
        userSettings.setId(new DigestSubscriptionSettingsId(subscription.getId(), user.getId()));
        userSettings.setSubscriber(user);
        userSettings.setDigestSubscription(subscription);
        userSettings.setMobileNotifications(user.getPersonalMobileNotifications() ? subscription.getMobileNotifications() : false);
        userSettings.setSendToMail(user.getPersonalSendToMail() ? subscription.getSendToMail() : false);
        return userSettings;
    }
    private DigestSubscriptionSettings setParametersForSubscriptionSettingsAndReturn(DigestSubscription subscription, User user) {
        var userSettings = new DigestSubscriptionSettings();
        userSettings.setId(new DigestSubscriptionSettingsId(subscription.getId(), user.getId()));
        userSettings.setSubscriber(user);
        userSettings.setDigestSubscription(subscription);
        userSettings.setMobileNotifications(user.getMobileNotifications() ? subscription.getMobileNotifications() : false);
        userSettings.setSendToMail(user.getSendToMail() ? subscription.getSendToMail() : false);
        return userSettings;
    }

    public Pair<DigestSubscriptionSettingsDTO, Boolean> updateDigestSubscriptionSettingsForUser(DigestSubscription subscription, User user, DigestSubscriptionSettingsDTO subscriptionSettingsDTO) {
        DigestSubscriptionSettings subscriptionSettings;
        var checkSubscriptionSettings = subscriptionSettingsRepository.findById(new DigestSubscriptionSettingsId(subscription.getId(), user.getId()));
        if (checkSubscriptionSettings.isEmpty()) {
            if (subscription.getIsPublic()) {
                subscriptionSettings = subscriptionSettingsRepository.save(this.setPersonalParametersForSubscriptionSettingsAndReturn(subscription, user));
            } else {
                throw new NoSuchElementException("There is no settings between these user and subscription which is not public!");
            }
        } else {
            subscriptionSettings = checkSubscriptionSettings.get();
        }
        if (subscriptionSettingsDTO.getSubscribed()) {
            subscriptionSettings.setSendToMail(subscriptionSettingsDTO.getSendToMail() == null ? user.getPersonalSendToMail() : subscriptionSettingsDTO.getSendToMail());
            subscriptionSettings.setMobileNotifications(subscriptionSettingsDTO.getMobileNotifications() == null ? user.getPersonalMobileNotifications() : subscriptionSettingsDTO.getMobileNotifications());
            subscriptionSettings.setEdited(true);
            subscriptionSettingsRepository.save(subscriptionSettings);
            return new Pair<>(new DigestSubscriptionSettingsDTO(true, subscriptionSettings.getSendToMail(), subscriptionSettings.getMobileNotifications()), false);
        } else {
            subscriptionSettingsRepository.delete(subscriptionSettings);
            return new Pair<>(new DigestSubscriptionSettingsDTO(false, false, false), true);
        }
    }

    public List<String> getAllDigestSubscriptionsUsersUsernamesExceptOwner(DigestSubscription subscription) {
        var settings = subscriptionSettingsRepository.findAllByDigestSubscription(subscription);
        var ownersUsername = subscription.getOwner().getUsername();
        List<String> subscribersUsernames = new ArrayList<>();
        for (var setting : settings) {
            var username = setting.getSubscriber().getUsername();
            if (!username.equals(ownersUsername)) {
                subscribersUsernames.add(setting.getSubscriber().getUsername());
            }
        }
        return subscribersUsernames;
    }

    public List<DigestSubscriptionSettings> getAllUsersDigestSubscriptionsSettings(User user) {
        return subscriptionSettingsRepository.findAllBySubscriber(user);
    }

    public List<DigestSubscriptionSettings> getAllDigestSubscriptionSettingsByDigestSubscription(DigestSubscription subscription) {
        return subscriptionSettingsRepository.findAllByDigestSubscription(subscription);
    }

    public Set<DigestSubscription> getAllUsersDigestSubscriptions(User user) {
        return subscriptionSettingsRepository.findDigestSubscriptionsByUser(user);
    }
}
