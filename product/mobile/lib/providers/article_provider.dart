import 'package:crosswords/data/constants/filter_constants.dart';
import 'package:dio/dio.dart';
import 'package:flutter/material.dart';
import 'package:intl/intl.dart';
import '../data/models/article.dart';
import '../data/models/note.dart';
import '../services/api_service.dart';
import 'abstract/filter_provider.dart';
import '../data/models/search_params/article_search_params.dart';

class ArticleProvider extends ChangeNotifier implements FilterProvider {
  final List<Article> _articles = [];

  Article? _currentArticle;
  final List<Note> _notes = [];
  bool _isLoading = false;
  bool _isLoadingNotes = false;
  bool _showOnlyFavorites = false;
  bool _isLoadingMore = false;

  int _currentPage = 0;
  final int _pageSize = 10;

  ArticleSearchParams _currentSearchParams = ArticleSearchParams(
    searchQuery: '',
    dateFrom: '',
    dateTo: '',
    selectedSources: [],
    selectedTags: [],
    searchInText: false,
    searchOption: 'Поиск по смыслу',
  );

  ArticleSearchParams _tempSearchParams = ArticleSearchParams(
    searchQuery: '',
    dateFrom: '',
    dateTo: '',
    selectedSources: [],
    selectedTags: [],
    searchInText: false,
    searchOption: 'Поиск по смыслу',
  );

  @override
  List<String> get sources => defaultSources;

  @override
  List<String> get tags => defaultTags;

  List<Article> get articles => _articles;

  Article? get currentArticle => _currentArticle;

  bool get isLoading => _isLoading;

  bool get isLoadingNotes => _isLoadingNotes;

  bool get showOnlyFavorites => _showOnlyFavorites;

  bool get isLoadingMore => _isLoadingMore;

  List<Note> get notes => _notes;

  String get selectedSearchOption => _tempSearchParams.searchOption;

  bool get searchInText => _tempSearchParams.searchInText;

  String get searchQuery => _tempSearchParams.searchQuery;

  String get dateFrom => _tempSearchParams.dateFrom;

  String get dateTo => _tempSearchParams.dateTo;

  @override
  List<String> get selectedSources => _tempSearchParams.selectedSources;

  @override
  List<String> get selectedTags => _tempSearchParams.selectedTags;

  // TODO переделать систему получения заметок
  List<Note> getNotesForArticle(int articleId) {
    return _notes.where((note) => note.articleId == articleId).toList();
  }

  /// Метод для загрузки статей
  Future<void> loadArticles() async {
    _currentPage = 0;
    _isLoading = true;
    notifyListeners();

    try {
      final searchParams = _currentSearchParams.toApiJson(
          _currentPage, _pageSize, _showOnlyFavorites);
      List<Article> newArticles =
          await ApiService.fetchDocuments(searchParams: searchParams);
      _articles
        ..clear()
        ..addAll(newArticles);
      _currentPage++;
    } catch (e) {
      debugPrint("Ошибка при загрузке статей: $e");
    }

    _isLoading = false;
    notifyListeners();
  }

  /// Метод для подгрузки статей (пагинация)
  Future<void> loadMoreArticles() async {
    if (_isLoadingMore || _isLoading) return;

    _isLoadingMore = true;
    notifyListeners();

    try {
      final searchParams = _currentSearchParams.toApiJson(
          _currentPage, _pageSize, _showOnlyFavorites);
      List<Article> newArticles =
          await ApiService.fetchDocuments(searchParams: searchParams);
      _articles.addAll(newArticles);
      _currentPage++;
    } catch (e) {
      debugPrint("Ошибка при загрузке статей: $e");
    }

    _isLoadingMore = false;
    notifyListeners();
  }

  /// Метод для получения статьи по ID
  Future<void> loadArticleById(int id) async {
    _isLoading = true;
    notifyListeners();

    try {
      final article = await ApiService.getDocumentById(id);
      _currentArticle = article;
    } catch (e) {
      debugPrint("Ошибка при загрузке статьи: $e");
      _currentArticle = null;
    }

    _isLoading = false;
    notifyListeners();
  }

  /// Возвращает true, если статья найдена и загружена
  Future<bool> searchArticleById(String query) async {
    if (query.trim().isEmpty) {
      throw Exception("empty");
    }

    final id = int.tryParse(query.trim());
    if (id == null) {
      throw Exception("not_numeric");
    }

    _isLoading = true;
    notifyListeners();

    try {
      final article = await ApiService.getDocumentById(id);
      _currentArticle = article;
      _isLoading = false;
      notifyListeners();
      return true;
    } on DioException catch (e) {
      _isLoading = false;
      notifyListeners();
      if (e.response?.statusCode == 404) {
        throw Exception("not_found");
      } else {
        throw Exception("server_error");
      }
    } catch (_) {
      _isLoading = false;
      notifyListeners();
      throw Exception("unknown");
    }
  }

  /// Метод для получения избранных статей
  Future<void> fetchFavorites() async {
    // TODO запрос к API
    await Future.delayed(const Duration(seconds: 1));
    notifyListeners();
  }

  /// Метод для переключения избранного
  Future<void> toggleFavorite(int articleId) async {
    _isLoading = true;
    notifyListeners();

    try {
      final index = _articles.indexWhere((article) => article.id == articleId);
      if (index == -1) return;

      final article = _articles[index];
      if (article.favorite) {
        await ApiService.removeFromFavorites(articleId);
      } else {
        await ApiService.addToFavorites(articleId);
      }

      _articles[index] =
          _articles[index].copyWith(favorite: !_articles[index].favorite);
    } catch (e) {
      debugPrint("Ошибка при переключении избранного: $e");
    }

    _isLoading = false;
    notifyListeners();
  }

  /// Метод для переключения избранного у текущей статьи
  Future<void> toggleCurrentArticleFavorite() async {
    if (_currentArticle == null) return;

    try {
      final isFav = _currentArticle!.favorite;

      if (isFav) {
        await ApiService.removeFromFavorites(_currentArticle!.id);
      } else {
        await ApiService.addToFavorites(_currentArticle!.id);
      }

      _currentArticle =
          _currentArticle!.copyWith(favorite: !_currentArticle!.favorite);

      notifyListeners();
    } catch (e) {
      debugPrint("Ошибка при переключении избранного: $e");
    }
  }

  void toggleShowFavorites() {
    _showOnlyFavorites = !_showOnlyFavorites;
    loadArticles();
    notifyListeners();
  }

  void setSearchOption(String option) {
    _tempSearchParams = _tempSearchParams.copyWith(searchOption: option);
    notifyListeners();
  }

  void setSearchQuery(String query) {
    _tempSearchParams = _tempSearchParams.copyWith(searchQuery: query);
    notifyListeners();
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

  void setSearchInText(bool value) {
    _tempSearchParams = _tempSearchParams.copyWith(searchInText: value);
    notifyListeners();
  }

  void resetFilters() {
    _tempSearchParams = _tempSearchParams.resetFilters();
    _currentSearchParams = _currentSearchParams.resetFilters();
    notifyListeners();
  }

  void applySearchParams() {
    _currentSearchParams = _tempSearchParams.copyWith(
      searchQuery: _tempSearchParams.searchQuery,
      dateFrom: _tempSearchParams.dateFrom,
      dateTo: _tempSearchParams.dateTo,
      selectedSources: List<String>.from(_tempSearchParams.selectedSources),
      selectedTags: List<String>.from(_tempSearchParams.selectedTags),
      searchInText: _tempSearchParams.searchInText,
      searchOption: _tempSearchParams.searchOption,
    );
    notifyListeners();
  }

  /// Метод для загрузки заметок
  Future<void> loadNotes(int articleId) async {
    _isLoadingNotes = true;
    notifyListeners();

    try {
      final notesFromServer = await ApiService.fetchComments(articleId);
      _notes
        ..removeWhere((note) => note.articleId == articleId)
        ..addAll(notesFromServer);
    } catch (e) {
      debugPrint("Ошибка при загрузке комментариев: $e");
    }

    _isLoadingNotes = false;
    notifyListeners();
  }

  /// Метод для добавления заметки
  Future<void> addNote(int articleId, String text) async {
    if (text.trim().isEmpty) return;

    try {
      final newNote = await ApiService.addComment(articleId, text);
      _notes.add(newNote);
      notifyListeners();
    } catch (e) {
      debugPrint("Ошибка при добавлении заметки: $e");
    }
  }

  /// Метод для редактирования заметки
  Future<void> updateNote(int noteId, String newText) async {
    final index = _notes.indexWhere((note) => note.id == noteId);
    if (index == -1) return;

    final note = _notes[index];

    try {
      await ApiService.updateComment(note.articleId, noteId, newText);

      _notes[index] = note.copyWith(
        text: newText,
        updatedAt: DateTime.now().toIso8601String(),
      );
      notifyListeners();
    } catch (e) {
      debugPrint("Ошибка при редактировании заметки: $e");
    }
  }

  /// Метод для удаления заметки
  Future<void> deleteNote(int noteId) async {
    final note = _notes.firstWhere((n) => n.id == noteId);
    _notes.removeWhere((n) => n.id == noteId);
    notifyListeners();

    try {
      await ApiService.deleteComment(note.articleId, noteId);
    } catch (e) {
      debugPrint("Ошибка при удалении заметки: $e");
    }
  }

  /// Метод для подготовки параметров поиска
  void prepareSearchForAllDocuments() {
    if (_tempSearchParams.searchOption == "Поиск по ID") {
      _currentSearchParams = ArticleSearchParams(
        searchQuery: _tempSearchParams.searchQuery,
        dateFrom: '',
        dateTo: '',
        selectedSources: [],
        selectedTags: [],
        searchInText: false,
        searchOption: _tempSearchParams.searchOption,
      );
    } else {
      applySearchParams();
    }
    notifyListeners();
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

  /// Метод для очистки состояния провайдера
  void clear() {
    _articles.clear();
    _notes.clear();
    _isLoading = false;
    _showOnlyFavorites = false;
    _isLoadingMore = false;
    _currentPage = 1;
    _currentSearchParams = ArticleSearchParams(
      searchQuery: '',
      dateFrom: '',
      dateTo: '',
      selectedSources: [],
      selectedTags: [],
      searchInText: false,
      searchOption: 'Поиск по смыслу',
    );
    _tempSearchParams = ArticleSearchParams(
      searchQuery: '',
      dateFrom: '',
      dateTo: '',
      selectedSources: [],
      selectedTags: [],
      searchInText: false,
      searchOption: 'Поиск по смыслу',
    );
    notifyListeners();
  }
}
