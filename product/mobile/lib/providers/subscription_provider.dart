import 'package:crosswords/data/constants/filter_constants.dart';
import 'package:crosswords/providers/abstract/filter_provider.dart';
import 'package:dio/dio.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../data/models/digest.dart';
import '../data/models/subscription.dart';
import '../data/models/subscribe_options.dart';
import '../services/api_service.dart';
import 'digest_provider.dart';

class SubscriptionProvider extends ChangeNotifier implements FilterProvider {
  final List<Subscription> _subscriptions = [];
  String _currentUserEmail = '';

  int? _selectedSubscriptionId;
  bool _isLoading = false;
  String _selectedCategory = "Все дайджесты";

  List<String> _selectedSources = [];
  List<String> _selectedTags = [];
  String _title = '';
  String _description = '';
  List<String> _followers = [];
  String _owner = '';
  bool _sendToMail = false;
  bool _mobileNotifications = false;
  bool _isPublic = false;
  String _currentFollowerInput = '';
  bool _isCreating = false;

  bool get isLoading => _isLoading;

  int? get selectedSubscriptionId => _selectedSubscriptionId;

  String get currentUserEmail => _currentUserEmail;

  List<Subscription> get subscriptions => _subscriptions;

  @override
  List<String> get selectedSources => _selectedSources;

  @override
  List<String> get selectedTags => _selectedTags;

  @override
  List<String> get sources => defaultSources;

  @override
  List<String> get tags => defaultTags;

  bool get sendToMail => _sendToMail;

  bool get mobileNotifications => _mobileNotifications;

  bool get isPublic => _isPublic;

  String get title => _title;

  String get description => _description;

  String get owner => _owner;

  List<String> get followers => _followers;

  String get currentFollowerInput => _currentFollowerInput;

  String get selectedCategory => _selectedCategory;

  bool get isCreating => _isCreating;

  List<Subscription> get filteredSubscriptions {
    if (_selectedCategory == "Все дайджесты") {
      return _subscriptions;
    } else if (_selectedCategory == "Подписки") {
      return _subscriptions
          .where((sub) => sub.subscribeOptions.subscribed)
          .toList();
    } else if (_selectedCategory == "Приватные") {
      return _subscriptions.where((sub) => !sub.public).toList();
    }
    return _subscriptions;
  }

  void setSelectedSubscription(int? subscriptionId) {
    _selectedSubscriptionId = subscriptionId;
    notifyListeners();
  }

  void resetSelectedSubscription() {
    _selectedSubscriptionId = null;
    notifyListeners();
  }

  void setSendToMail(bool value) {
    _sendToMail = value;
    notifyListeners();
  }

  void setMobileNotifications(bool value) {
    _mobileNotifications = value;
    notifyListeners();
  }

  void setIsPublic(bool value) {
    _isPublic = value;
    notifyListeners();
  }

  void setTitle(String value) {
    _title = value;
    notifyListeners();
  }

  void setDescription(String value) {
    _description = value;
    notifyListeners();
  }

  void setOwner(String value) {
    _owner = value;
    notifyListeners();
  }

  void setFollowers(List<String> value) {
    _followers = value;
    notifyListeners();
  }

  void setCurrentFollowerInput(String value) {
    _currentFollowerInput = value;
    notifyListeners();
  }

  void setCategory(String category) {
    _selectedCategory = category;
    notifyListeners();
  }

  void setTags(List<String> tags) {
    _selectedTags = tags;
    notifyListeners();
  }

  void setSources(List<String> sources) {
    _selectedSources = sources;
    notifyListeners();
  }

  bool addFollower(String value) {
    if (_followers.contains(value)) {
      return false;
    }
    _followers.add(value);
    notifyListeners();
    return true;
  }

  bool removeFollower(String value) {
    if (value == _currentUserEmail) {
      return false;
    }
    if (!_followers.contains(value)) {
      return false;
    }
    _followers.remove(value);
    notifyListeners();
    return true;
  }

  void reset() {
    _selectedSources.clear();
    _selectedTags.clear();
    _title = '';
    _description = '';
    _followers.clear();
    _owner = '';
    _sendToMail = false;
    _mobileNotifications = false;
    _isPublic = false;
    _currentFollowerInput = '';
    notifyListeners();
  }

  bool areFieldsEmpty() {
    return _title.isEmpty ||
        _selectedSources.isEmpty ||
        _description.isEmpty ||
        _followers.isEmpty ||
        _owner.isEmpty ||
        _selectedTags.isEmpty ||
        _currentFollowerInput.isEmpty ||
        !_sendToMail ||
        !_mobileNotifications ||
        !_isPublic;
  }

  void addDefault() {
    // TODO добавление пользоваателя
    addFollower("default");
    notifyListeners();
  }

  void resetAndAddDefault() {
    reset();
    addDefault();
    notifyListeners();
  }

  void addSubscription() {
    // TODO добавление подписки
    Subscription subscription = Subscription(
      id: _subscriptions.length + 1,
      title: _title,
      description: _description,
      sources: _selectedSources,
      tags: _selectedTags,
      subscribeOptions: SubscribeOptions(
        subscribed: true,
        sendToMail: _sendToMail,
        mobileNotifications: _mobileNotifications,
      ),
      creationDate: DateTime.now().toString(),
      public: _isPublic,
      owner: "default",
      isOwner: true,
      followers: _followers,
    );
    _subscriptions.add(subscription);
    notifyListeners();
  }

  // TODO удалить в будущем
  void updateSubscription(Subscription updatedSubscription) {
    final index =
        _subscriptions.indexWhere((sub) => sub.id == updatedSubscription.id);
    if (index != -1) {
      _subscriptions[index] = updatedSubscription;
      notifyListeners();
    }
  }

  void transferOwnership(Subscription subscription, String newOwner) {
    int index = _subscriptions.indexWhere((sub) => sub.id == subscription.id);

    // TODO перенос владельца
    if (index != -1) {
      _subscriptions[index] =
          subscription.copyWith(owner: newOwner, isOwner: false);
      notifyListeners();
    }
  }

  Future<void> loadSubscriptions() async {
    _isLoading = true;
    _subscriptions.clear();
    notifyListeners();

    try {
      final newSubscriptions = await ApiService.fetchAvailableSubscriptions();
      _subscriptions.addAll(newSubscriptions);
    } catch (e) {
      debugPrint("Ошибка при загрузке подписок: $e");
    }

    _isLoading = false;
    notifyListeners();
  }

  @override
  void toggleSource(String source) {
    if (_selectedSources.contains(source)) {
      _selectedSources.remove(source);
    } else {
      _selectedSources.add(source);
    }
  }

  @override
  void toggleTag(String tag) {
    if (_selectedTags.contains(tag)) {
      _selectedTags.remove(tag);
    } else {
      _selectedTags.add(tag);
    }
  }

  Future<void> createSubscription() async {
    if (_title.trim().isEmpty ||
        _description.trim().isEmpty ||
        _selectedSources.isEmpty ||
        _selectedTags.isEmpty ||
        _followers.isEmpty) {
      throw ('Заполните все обязательные поля');
    }

    _isCreating = true;
    notifyListeners();

    try {
      await ApiService.createSubscription(
        title: _title,
        description: _description,
        sources: _selectedSources,
        tags: _selectedTags,
        followers: _followers,
        subscribeOptions: SubscribeOptions(
          sendToMail: _sendToMail,
          mobileNotifications: _mobileNotifications,
          subscribed: true,
        ),
        isPublic: _isPublic,
      );
    } on DioException catch (e) {
      if (e.response?.statusCode == 400) {
        throw ('Некорректные источники');
      } else if (e.response?.statusCode == 401) {
        throw ('Вы не авторизованы');
      } else if (e.response?.statusCode == 404) {
        throw ('Не найден один или несколько подписчиков');
      } else {
        throw ('Неизвестная ошибка: ${e.message}');
      }
    } catch (e) {
      rethrow;
    } finally {
      _isCreating = false;
      notifyListeners();
    }
  }

  Future<void> fetchCurrentUserEmail() async {
    try {
      _currentUserEmail = await ApiService.getCurrentUserEmail();
      if (!_followers.contains(_currentUserEmail)) {
        _followers.insert(0, _currentUserEmail);
      }
      notifyListeners();
    } catch (e) {
      debugPrint('Ошибка получения email пользователя: $e');
    }
  }

  Future<void> addFollowerWithValidation(String email) async {
    try {
      final data = await ApiService.getUserSubscriptionSettings(email);

      if (data['subscribable'] != true) {
        throw ('Пользователя нельзя добавить в рассылку');
      }

      if (_followers.contains(email)) {
        throw ('Этот пользователь уже добавлен');
      }

      _followers.add(email);
      notifyListeners();
    } on DioException catch (e) {
      if (e.response?.statusCode == 404) {
        throw ('Пользователь не найден');
      } else if (e.response?.statusCode == 403) {
        throw ('Пользователя нельзя добавить в рассылку');
      } else {
        throw ('Ошибка проверки пользователя: ${e.message}');
      }
    } catch (e) {
      rethrow;
    }
  }

  Future<void> updateSubscriptionSettings(
      Subscription updatedSubscription) async {
    final index =
        _subscriptions.indexWhere((sub) => sub.id == updatedSubscription.id);
    if (index == -1) return;

    try {
      await ApiService.put(
          '/subscriptions/${updatedSubscription.id}/settings/update',
          data: {
            'subscribed': updatedSubscription.subscribeOptions.subscribed,
            'send_to_mail': updatedSubscription.subscribeOptions.sendToMail,
            'mobile_notifications':
                updatedSubscription.subscribeOptions.mobileNotifications,
          });

      _subscriptions[index] = updatedSubscription;
      notifyListeners();
    } catch (e) {
      debugPrint('Ошибка обновления настроек подписки: $e');
    }
  }

  Future<void> selectSubscription(
      int? subscriptionId, BuildContext context) async {
    _selectedSubscriptionId = subscriptionId;
    notifyListeners();

    final digestProvider = Provider.of<DigestProvider>(context, listen: false);

    if (subscriptionId != null) {
      await digestProvider.loadDigestsBySubscription(subscriptionId);
    } else {
      await digestProvider.loadDigests();
    }
  }

  Future<Subscription> fetchSubscriptionByDigestId(String digestId) async {
    return await ApiService.fetchSubscriptionByDigestId(digestId);
  }

  Future<void> toggleSubscriptionByDigest(Digest digest) async {
    final newSubscribedState = !digest.subscribeOptions.subscribed;

    try {
      final subscription = await fetchSubscriptionByDigestId(digest.id);

      final response = await ApiService.put(
        '/subscriptions/${subscription.id}/settings/update',
        data: {
          'subscribed': newSubscribedState,
          'send_to_mail': digest.subscribeOptions.sendToMail,
          'mobile_notifications': digest.subscribeOptions.mobileNotifications,
        },
      );

      if (response.data['deleted'] == true) {
        _subscriptions.removeWhere((sub) => sub.id == subscription.id);
        notifyListeners();
        return;
      }

      final index =
          _subscriptions.indexWhere((sub) => sub.id == subscription.id);
      if (index != -1) {
        _subscriptions[index] = subscription.copyWith(
          subscribeOptions: digest.subscribeOptions.copyWith(
            subscribed: newSubscribedState,
          ),
        );
        notifyListeners();
      }
    } catch (e) {
      debugPrint('Ошибка переключения подписки через дайджест: $e');
      rethrow;
    }
  }

  Future<bool> isCurrentUserOwner(int subscriptionId) async {
    try {
      return await ApiService.checkOwnership(subscriptionId);
    } catch (e) {
      debugPrint('Ошибка проверки владельца: $e');
      return false;
    }
  }

  Future<List<String>> getSubscriptionFollowers(int subscriptionId) async {
    try {
      final followers =
          await ApiService.fetchSubscriptionFollowers(subscriptionId);
      followers.removeWhere((email) => email == _currentUserEmail);
      return followers;
    } catch (e) {
      debugPrint('Ошибка получения подписчиков: $e');
      return [];
    }
  }

  Future<void> transferSubscriptionOwnership(
      int subscriptionId, String newOwnerEmail) async {
    try {
      await ApiService.changeSubscriptionOwner(subscriptionId, newOwnerEmail);
    } catch (e) {
      debugPrint('Ошибка передачи владения: $e');
      rethrow;
    }
  }

  Future<bool> handleUnsubscribe({
    required BuildContext context,
    int? subscriptionId,
    String? digestId,
  }) async {
    final scaffoldMessenger = ScaffoldMessenger.of(context);

    try {
      Subscription subscription;
      if (digestId != null) {
        subscription = await fetchSubscriptionByDigestId(digestId);
      } else if (subscriptionId != null) {
        subscription = _subscriptions.firstWhere(
          (sub) => sub.id == subscriptionId,
          orElse: () => throw Exception('Подписка не найдена'),
        );
      } else {
        throw Exception('Не указан ни subscriptionId, ни digestId');
      }

      if (!subscription.subscribeOptions.subscribed) {
        await _toggleSubscription(
          subscription,
          context: context,
          digestId: digestId,
        );
        return true;
      }

      final isOwner = await isCurrentUserOwner(subscription.id);
      final followers = await getSubscriptionFollowers(subscription.id);

      if (isOwner && followers.isEmpty) {
        final confirmed = await _showLastOwnerWarningDialog(context);
        if (confirmed) {
          await _toggleSubscription(
            subscription,
            context: context,
            digestId: digestId,
          );
          return true;
        } else {
          return false;
        }
      } else if (isOwner && followers.isNotEmpty) {
        final newOwner = await _showTransferOwnershipDialog(context, followers);
        if (newOwner != null) {
          await transferSubscriptionOwnership(subscription.id, newOwner);
          await _toggleSubscription(
            subscription,
            context: context,
            digestId: digestId,
          );
          return true;
        } else {
          return false;
        }
      } else {
        final confirmed = await _showUnsubscribeConfirmDialog(
          context,
          subscription.public,
          subscription.title,
        );
        if (confirmed) {
          await _toggleSubscription(
            subscription,
            context: context,
            digestId: digestId,
          );
          return true;
        } else {
          return false;
        }
      }
    } catch (e) {
      debugPrint('Ошибка обработки подписки/отписки: $e');
      scaffoldMessenger.showSnackBar(
        SnackBar(content: Text('Ошибка: $e')),
      );
      return false;
    }
  }

  Future<void> _toggleSubscription(
    Subscription subscription, {
    required BuildContext context,
    String? digestId,
  }) async {
    final newSubscribedState = !subscription.subscribeOptions.subscribed;

    try {
      final response = await ApiService.put(
        '/subscriptions/${subscription.id}/settings/update',
        data: {
          'subscribed': newSubscribedState,
          'send_to_mail': subscription.subscribeOptions.sendToMail,
          'mobile_notifications':
              subscription.subscribeOptions.mobileNotifications,
        },
      );

      if (response.data['deleted'] == true) {
        _subscriptions.removeWhere((sub) => sub.id == subscription.id);
      } else {
        final index =
            _subscriptions.indexWhere((sub) => sub.id == subscription.id);
        if (index != -1) {
          _subscriptions[index] = subscription.copyWith(
            subscribeOptions: subscription.subscribeOptions.copyWith(
              subscribed: newSubscribedState,
            ),
          );
        }
      }

      notifyListeners();

      await loadSubscriptions();

      final digestProvider =
          Provider.of<DigestProvider>(context, listen: false);
      await digestProvider.loadDigests();
    } catch (e) {
      debugPrint('Ошибка переключения подписки: $e');
      rethrow;
    }
  }

  Future<bool> _showLastOwnerWarningDialog(BuildContext context) async {
    return await showDialog<bool>(
          context: context,
          builder: (context) => AlertDialog(
            title: const Text('Внимание!'),
            content: const Text(
                'Вы последний подписчик. После отписки подписка и все дайджесты будут удалены. Продолжить?'),
            actions: [
              TextButton(
                onPressed: () => Navigator.pop(context, false),
                child: const Text('Отмена'),
              ),
              TextButton(
                onPressed: () => Navigator.pop(context, true),
                child: const Text('Удалить и отписаться',
                    style: TextStyle(color: Colors.red)),
              ),
            ],
          ),
        ) ??
        false;
  }

  Future<String?> _showTransferOwnershipDialog(
      BuildContext context, List<String> followers) async {
    String? selectedOwner;

    return await showDialog<String>(
      context: context,
      builder: (context) {
        return StatefulBuilder(
          builder: (context, setState) => AlertDialog(
            title: const Text('Выберите нового владельца'),
            content: SingleChildScrollView(
              child: Column(
                children: followers
                    .map((follower) => RadioListTile<String>(
                          title: Text(follower),
                          value: follower,
                          groupValue: selectedOwner,
                          onChanged: (value) {
                            setState(() => selectedOwner = value);
                          },
                        ))
                    .toList(),
              ),
            ),
            actions: [
              TextButton(
                onPressed: () => Navigator.pop(context),
                child: const Text('Отмена'),
              ),
              TextButton(
                onPressed: selectedOwner == null
                    ? null
                    : () => Navigator.pop(context, selectedOwner),
                child: const Text('Передать и отписаться',
                    style: TextStyle(color: Colors.red)),
              ),
            ],
          ),
        );
      },
    );
  }

  Future<bool> _showUnsubscribeConfirmDialog(
      BuildContext context, bool isPublic, String title) async {
    String message = isPublic
        ? 'Вы уверены, что хотите отписаться от "$title"?'
        : 'Этот дайджест приватный. Чтобы снова подписаться, вам придётся запросить разрешение у владельца. Отписаться?';

    return await showDialog<bool>(
          context: context,
          builder: (context) => AlertDialog(
            title: const Text('Отмена подписки'),
            content: Text(message),
            actions: [
              TextButton(
                onPressed: () => Navigator.pop(context, false),
                child: const Text('Отмена'),
              ),
              TextButton(
                onPressed: () => Navigator.pop(context, true),
                child: const Text('Отписаться',
                    style: TextStyle(color: Colors.red)),
              ),
            ],
          ),
        ) ??
        false;
  }

  Future<Subscription> fetchSubscriptionById(int subscriptionId) async {
    return await ApiService.fetchSubscriptionById(subscriptionId);
  }

  Future<void> saveEditedSubscription(Subscription oldSubscription) async {
    _isCreating = true;
    notifyListeners();

    final updated = oldSubscription.copyWith(
      title: _title,
      description: _description,
      sources: _selectedSources,
      tags: _selectedTags,
      followers: _followers,
      public: _isPublic,
      subscribeOptions: SubscribeOptions(
        sendToMail: _sendToMail,
        mobileNotifications: _mobileNotifications,
        subscribed: oldSubscription.subscribeOptions.subscribed,
      ),
      // TODO при расширении функционала добавить owner
      owner: oldSubscription.owner,
    );

    try {
      await ApiService.updateSubscription(updated);

      final index =
      _subscriptions.indexWhere((sub) => sub.id == updated.id);
      if (index != -1) {
        _subscriptions[index] = updated;
      }

      notifyListeners();
    } catch (e) {
      debugPrint('Ошибка при обновлении подписки: $e');
      rethrow;
    } finally {
      _isCreating = false;
      notifyListeners();
    }
  }

  Future<Subscription> resetToOriginalState(int subscriptionId) async {
    final fresh = await fetchSubscriptionById(subscriptionId);

    setTitle(fresh.title);
    setDescription(fresh.description);
    setFollowers(fresh.followers);
    setSendToMail(fresh.subscribeOptions.sendToMail);
    setMobileNotifications(fresh.subscribeOptions.mobileNotifications);
    setSources(fresh.sources);
    setTags(fresh.tags);
    setIsPublic(fresh.public);
    setOwner(fresh.owner);

    return fresh;
  }

  void clear() {
    _subscriptions.clear();
    _selectedSources.clear();
    _selectedTags.clear();
    _title = '';
    _description = '';
    _followers.clear();
    _owner = '';
    _sendToMail = false;
    _mobileNotifications = false;
    _isPublic = false;
    _currentFollowerInput = '';
    _isLoading = false;
    _selectedCategory = "Все дайджесты";
    _selectedSubscriptionId = null;
    notifyListeners();
  }
}
