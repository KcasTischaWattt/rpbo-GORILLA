import 'package:crosswords/data/constants/filter_constants.dart';
import 'package:flutter/material.dart';
import 'package:intl/intl.dart';
import '../data/models/digest.dart';
import '../data/models/subscribe_options.dart';
import '../services/api_service.dart';
import 'abstract/filter_provider.dart';
import '../data/models/search_params/digest_search_params.dart';

enum DigestsLoadMode {
  all,
  subscription,
  search,
}

class DigestProvider extends ChangeNotifier implements FilterProvider {
  final List<Digest> _digests = [];

  bool _isLoading = false;
  bool _isLoadingMore = false;
  int _allDigestsPage = 0;
  int _subscriptionDigestsPage = 0;
  int _searchedDigestsPage = 0;
  final int _pageSize = 10;

  String _selectedCategory = 'Все дайджесты';

  DigestsLoadMode _currentMode = DigestsLoadMode.all;

  DigestSearchParams _currentSearchParams = DigestSearchParams(
    searchQuery: '',
    dateFrom: '',
    dateTo: '',
    selectedSources: [],
    selectedTags: [],
  );

  DigestSearchParams _tempSearchParams = DigestSearchParams(
    searchQuery: '',
    dateFrom: '',
    dateTo: '',
    selectedSources: [],
    selectedTags: [],
  );

  List<Digest> get digests => _digests;

  @override
  List<String> get sources => defaultSources;

  @override
  List<String> get tags => defaultTags;

  bool get isLoading => _isLoading;

  bool get isLoadingMore => _isLoadingMore;

  String get selectedCategory => _selectedCategory;

  String get searchQuery => _tempSearchParams.searchQuery;

  String get dateFrom => _tempSearchParams.dateFrom;

  String get dateTo => _tempSearchParams.dateTo;

  DigestSearchParams get currentSearchParams => _currentSearchParams;

  DigestSearchParams get tempSearchParams => _tempSearchParams;

  DigestsLoadMode get currentMode => _currentMode;

  @override
  List<String> get selectedSources => _tempSearchParams.selectedSources;

  @override
  List<String> get selectedTags => _tempSearchParams.selectedTags;

  List<Digest> get filteredDigests {
    if (_selectedCategory == "Все дайджесты") {
      return _digests;
    } else if (_selectedCategory == "Подписки") {
      return _digests
          .where((digest) => digest.subscribeOptions.subscribed)
          .toList();
    } else if (_selectedCategory == "Приватные") {
      return _digests.where((digest) => !digest.public).toList();
    }
    return _digests;
  }

  void resetFilters() {
    _tempSearchParams = _tempSearchParams.resetFilters();
    _currentSearchParams = _currentSearchParams.resetFilters();
    notifyListeners();
  }

  void setCategory(String category) {
    _selectedCategory = category;
    notifyListeners();
  }

  void setSearchQuery(String query) {
    _tempSearchParams = _tempSearchParams.copyWith(searchQuery: query);
    notifyListeners();
  }

  void applySearchParams() {
    _currentSearchParams = _tempSearchParams.copyWith(
      searchQuery: _tempSearchParams.searchQuery,
      dateFrom: _tempSearchParams.dateFrom,
      dateTo: _tempSearchParams.dateTo,
      selectedSources: List<String>.from(_tempSearchParams.selectedSources),
      selectedTags: List<String>.from(_tempSearchParams.selectedTags),
    );
    notifyListeners();
  }

  void updateDigest(Digest updatedDigest) {
    final index =
        _digests.indexWhere((digest) => digest.id == updatedDigest.id);
    if (index != -1) {
      _digests[index] = updatedDigest;
      notifyListeners();
    }
  }

  Digest setRating(int rating, Digest digest) {
    return digest.copyWith(userRating: rating);
  }

  void setMode(DigestsLoadMode mode) {
    if (_currentMode != mode) {
      _currentMode = mode;
      _resetPagination();
    }
  }

  @override
  void toggleSource(String source) {
    final selectedSources =
        List<String>.from(_tempSearchParams.selectedSources);
    if (selectedSources.contains(source)) {
      selectedSources.remove(source);
    } else {
      selectedSources.add(source);
    }
    _tempSearchParams =
        _tempSearchParams.copyWith(selectedSources: selectedSources);
    notifyListeners();
  }

  @override
  void toggleTag(String tag) {
    final selectedTags = List<String>.from(_tempSearchParams.selectedTags);
    if (selectedTags.contains(tag)) {
      selectedTags.remove(tag);
    } else {
      selectedTags.add(tag);
    }
    _tempSearchParams = _tempSearchParams.copyWith(selectedTags: selectedTags);
    notifyListeners();
  }

  Future<void> _fetchDigests({bool isLoadMore = false}) async {
    if (_isLoading || (isLoadMore && _isLoadingMore)) return;

    if (isLoadMore) {
      _isLoadingMore = true;
    } else {
      if (_isLoading) return;
      _isLoading = true;
      _allDigestsPage = 0;
      _digests.clear();
    }

    notifyListeners();

    try {
      final newDigests = await ApiService.fetchDigests(
        pageNumber: _allDigestsPage,
        matchesPerPage: _pageSize,
      );

      if (newDigests.isNotEmpty) {
        _digests.addAll(newDigests);
        _allDigestsPage++;
      }
    } catch (e) {
      debugPrint("Ошибка при загрузке дайджестов: $e");
    }

    _isLoading = false;
    _isLoadingMore = false;
    notifyListeners();
  }

  Future<void> _searchDigests({bool isLoadMore = false}) async {
    if (_isLoading || (isLoadMore && _isLoadingMore)) return;

    if (isLoadMore) {
      _isLoadingMore = true;
    } else {
      _isLoading = true;
      if (!isLoadMore) {
        _searchedDigestsPage = 0;
      }
    }

    notifyListeners();

    try {
      final newDigests = await ApiService.searchDigests(
        searchParams: _currentSearchParams,
        pageNumber: _searchedDigestsPage,
        matchesPerPage: _pageSize,
      );

      if (!isLoadMore) {
        _digests.clear();
      }

      if (newDigests.isNotEmpty) {
        _digests.addAll(newDigests);
        _searchedDigestsPage++;
      }
    } catch (e) {
      debugPrint("Ошибка при поиске дайджестов: $e");
    }

    _isLoading = false;
    _isLoadingMore = false;
    notifyListeners();
  }

  Future<void> _loadDigestsBySubscription({
    required int subscriptionId,
    bool isLoadMore = false,
  }) async {
    if (_isLoading || (isLoadMore && _isLoadingMore)) return;

    if (isLoadMore) {
      _isLoadingMore = true;
    } else {
      _isLoading = true;
      _subscriptionDigestsPage = 0;
      _digests.clear();
    }

    _isLoading = true;
    notifyListeners();

    try {
      final response = await ApiService.fetchDigestsBySubscription(
        subscriptionId: subscriptionId,
        nextPage: _subscriptionDigestsPage,
        matchesPerPage: _pageSize,
      );

      final subscriptionData = response.data['subscription'];

      final subscriptionSources =
          List<String>.from(subscriptionData['sources'] ?? []);
      final subscriptionTags =
          List<String>.from(subscriptionData['tags'] ?? []);
      final subscriptionPublic = subscriptionData['public'] ?? true;
      final subscriptionOwner = subscriptionData['is_owner'] == true
          ? (await ApiService.getCurrentUserEmail())
          : "unknown@unknown.com";
      final subscribeOptions =
          SubscribeOptions.fromJson(subscriptionData['subscribe_options']);
      final subscriptionTitle = subscriptionData['title'] ?? "";
      final subscriptionDescription = subscriptionData['description'] ?? "";

      final newDigests =
          (subscriptionData['digests'] as List<dynamic>).map((digestJson) {
        return Digest(
          id: digestJson['id'],
          title: subscriptionTitle,
          averageRating: (digestJson['average_rating'] as num?)?.toDouble(),
          userRating: null,
          sources: subscriptionSources,
          description: subscriptionDescription,
          text: digestJson['text'] ?? '',
          tags: subscriptionTags,
          date: digestJson['date'],
          public: subscriptionPublic,
          isOwner: subscriptionData['is_owner'] ?? false,
          owner: subscriptionOwner,
          urls: [],
          subscribeOptions: subscribeOptions,
        );
      }).toList();

      _digests.addAll(newDigests);

      if (subscriptionData['next_page'] != null) {
        _subscriptionDigestsPage = subscriptionData['next_page'];
      } else {
        _subscriptionDigestsPage = -1;
      }
    } catch (e) {
      debugPrint('Ошибка загрузки дайджестов по подписке: $e');
    }

    _isLoading = false;
    _isLoadingMore = false;
    notifyListeners();
  }

  Future<void> loadDigests() async {
    setMode(DigestsLoadMode.all);
    await _fetchDigests();
  }

  Future<void> loadMoreDigests() async {
    setMode(DigestsLoadMode.all);
    await _fetchDigests(isLoadMore: true);
  }

  Future<void> loadSearchedDigests() async {
    setMode(DigestsLoadMode.search);
    await _searchDigests();
  }

  Future<void> loadMoreSearchedDigests() async {
    setMode(DigestsLoadMode.search);
    await _searchDigests(isLoadMore: true);
  }

  Future<void> loadDigestsBySubscription(int subscriptionId) async {
    setMode(DigestsLoadMode.subscription);
    await _loadDigestsBySubscription(subscriptionId: subscriptionId);
  }

  Future<void> loadMoreDigestsBySubscription(int subscriptionId) async {
    setMode(DigestsLoadMode.subscription);
    await _loadDigestsBySubscription(
      subscriptionId: subscriptionId,
      isLoadMore: true,
    );
  }

  Future<Digest> loadDigestById(String digestId) async {
    return await ApiService.fetchDigestById(digestId);
  }

  Future<void> rateDigest(Digest digest, int rating) async {
    final oldDigest = digest.copyWith();
    try {
      final updatedDigest = digest.copyWith(userRating: rating);
      updateDigest(updatedDigest);
      await ApiService.rateDigest(digest.id, rating);
    } catch (e) {
      updateDigest(oldDigest);
      debugPrint('Ошибка при оценке дайджеста: $e');
      rethrow;
    }
  }

  void setDateFromDateTime(DateTime date) {
    final formatted = DateFormat('dd/MM/yyyy').format(date);
    _tempSearchParams = _tempSearchParams.copyWith(dateFrom: formatted);
    notifyListeners();
  }

  void setDateToDateTime(DateTime date) {
    final formatted = DateFormat('dd/MM/yyyy').format(date);
    _tempSearchParams = _tempSearchParams.copyWith(dateTo: formatted);
    notifyListeners();
  }

  void clear() {
    _digests.clear();
    _isLoading = false;
    _isLoadingMore = false;
    _selectedCategory = 'Все дайджесты';
    _currentSearchParams = DigestSearchParams(
      searchQuery: '',
      dateFrom: '',
      dateTo: '',
      selectedSources: [],
      selectedTags: [],
    );
    _tempSearchParams = DigestSearchParams(
      searchQuery: '',
      dateFrom: '',
      dateTo: '',
      selectedSources: [],
      selectedTags: [],
    );
    _resetPagination();
    notifyListeners();
  }

  void _resetPagination() {
    _allDigestsPage = 0;
    _subscriptionDigestsPage = 0;
    _searchedDigestsPage = 0;
  }
}
