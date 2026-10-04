/// Класс, представляющий параметры поиска статей
class ArticleSearchParams {
  final String _searchQuery;
  final String _dateFrom;
  final String _dateTo;
  final List<String> _selectedSources;
  final List<String> _selectedTags;
  final bool _searchInText;
  final String _searchOption;

  /// Поисковая строка (запрос пользователя).
  String get searchQuery => _searchQuery;

  /// Дата начала диапазона фильтра по дате
  String get dateFrom => _dateFrom;

  /// Дата окончания диапазона фильтра по дате
  String get dateTo => _dateTo;

  /// Список выбранных источников
  List<String> get selectedSources => _selectedSources;

  /// Список выбранных тегов
  List<String> get selectedTags => _selectedTags;

  /// Флаг, указывающий, нужно ли искать в тексте статьи
  bool get searchInText => _searchInText;

  /// Выбранный режим поиска.
  ///
  /// Возможные значения: "Поиск по смыслу", "Точный поиск", "Поиск по ID".
  String get searchOption => _searchOption;

  /// Создает экземпляр параметров поиска
  ArticleSearchParams({
    required String searchQuery,
    required String dateFrom,
    required String dateTo,
    required List<String> selectedSources,
    required List<String> selectedTags,
    required bool searchInText,
    required String searchOption,
  })  : _searchQuery = searchQuery,
        _dateFrom = dateFrom,
        _dateTo = dateTo,
        _selectedSources = selectedSources,
        _selectedTags = selectedTags,
        _searchInText = searchInText,
        _searchOption = searchOption;

  /// Создает экземпляр из JSON
  factory ArticleSearchParams.fromJson(Map<String, dynamic> json) {
    return ArticleSearchParams(
      searchQuery: json['search_query'],
      dateFrom: json['date_from'],
      dateTo: json['date_to'],
      selectedSources: List<String>.from(json['selected_sources']),
      selectedTags: List<String>.from(json['selected_tags']),
      searchInText: json['search_in_text'],
      searchOption: json['search_option'],
    );
  }

  /// Возвращает копию параметров поиска с возможностью переопределения значений
  ArticleSearchParams copyWith({
    String? searchQuery,
    String? dateFrom,
    String? dateTo,
    List<String>? selectedSources,
    List<String>? selectedTags,
    bool? searchInText,
    String? searchOption,
  }) {
    return ArticleSearchParams(
      searchQuery: searchQuery ?? this.searchQuery,
      dateFrom: dateFrom ?? this.dateFrom,
      dateTo: dateTo ?? this.dateTo,
      selectedSources: selectedSources ?? List.from(this.selectedSources),
      selectedTags: selectedTags ?? List.from(this.selectedTags),
      searchInText: searchInText ?? this.searchInText,
      searchOption: searchOption ?? this.searchOption,
    );
  }

  /// Сбрасывает все фильтры, кроме режима поиска
  ///
  /// Очищает запрос, даты, источники, теги и флаг `searchInText`
  ArticleSearchParams resetFilters() {
    return ArticleSearchParams(
      searchQuery: '',
      dateFrom: '',
      dateTo: '',
      selectedSources: [],
      selectedTags: [],
      searchInText: false,
      searchOption: searchOption,
    );
  }

  /// Преобразует параметры в формат, ожидаемый API
  ///
  /// Учитывает режим поиска, фильтры, пагинацию и флаг избранного
  Map<String, dynamic> toApiJson(int page, int pageSize, bool isFavorite) {
    final isCertain = searchOption == "Точный поиск";
    return {
      "language": null,
      "sources": selectedSources.isEmpty ? null : selectedSources,
      "tags": selectedTags.isEmpty ? null : selectedTags,
      "folders": isFavorite ? ["Избранное"] : null,
      "search_body": searchQuery.isEmpty ? null : searchQuery,
      "search_mode": _getSearchMode(),
      "search_in_text": isCertain ? searchInText : null,
      "date_from": dateFrom.isEmpty ? null : dateFrom,
      "date_to": dateTo.isEmpty ? null : dateTo,
      "next_page": page,
      "matches_per_page": pageSize,
    };
  }

  /// Определяет строку значения `search_mode` для API в зависимости от режима
  String _getSearchMode() {
    switch (searchOption) {
      case "Точный поиск":
        return "certain";
      case "Поиск по смыслу":
        return "semantic";
      case "Поиск по ID":
        return "id";
      default:
        return "certain";
    }
  }

  /// Возвращает строковое представление объекта
  @override
  String toString() {
    return 'SearchParams(searchQuery: $searchQuery, dateFrom: $dateFrom, dateTo: $dateTo, '
        'selectedSources: $selectedSources, selectedTags: $selectedTags, '
        'searchInText: $searchInText, searchOption: $searchOption)';
  }
}
