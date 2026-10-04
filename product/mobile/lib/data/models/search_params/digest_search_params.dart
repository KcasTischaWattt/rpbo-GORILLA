class DigestSearchParams {
  final String _searchQuery;
  final String _dateFrom;
  final String _dateTo;
  final List<String> _selectedSources;
  final List<String> _selectedTags;

  String get searchQuery => _searchQuery;
  String get dateFrom => _dateFrom;
  String get dateTo => _dateTo;
  List<String> get selectedSources => _selectedSources;
  List<String> get selectedTags => _selectedTags;
  DigestSearchParams({
    required String searchQuery,
    required String dateFrom,
    required String dateTo,
    required List<String> selectedSources,
    required List<String> selectedTags,
  })  : _searchQuery = searchQuery,
        _dateFrom = dateFrom,
        _dateTo = dateTo,
        _selectedSources = selectedSources,
        _selectedTags = selectedTags;

  factory DigestSearchParams.fromJson(Map<String, dynamic> json) {
    return DigestSearchParams(
      searchQuery: json['search_query'],
      dateFrom: json['date_from'],
      dateTo: json['date_to'],
      selectedSources: List<String>.from(json['selected_sources']),
      selectedTags: List<String>.from(json['selected_tags']),
    );
  }

  DigestSearchParams copyWith({
    String? searchQuery,
    String? dateFrom,
    String? dateTo,
    List<String>? selectedSources,
    List<String>? selectedTags,
  }) {
    return DigestSearchParams(
      searchQuery: searchQuery ?? this.searchQuery,
      dateFrom: dateFrom ?? this.dateFrom,
      dateTo: dateTo ?? this.dateTo,
      selectedSources: selectedSources ?? List.from(this.selectedSources),
      selectedTags: selectedTags ?? List.from(this.selectedTags),
    );
  }

  DigestSearchParams resetFilters() {
    return DigestSearchParams(
      searchQuery: '',
      dateFrom: '',
      dateTo: '',
      selectedSources: [],
      selectedTags: [],
    );
  }

  Map<String, dynamic> toQueryParameters(int pageNumber, int matchesPerPage) {
    final Map<String, dynamic> params = {
      'subscribe_only': false,
      'page_number': pageNumber,
      'matches_per_page': matchesPerPage,
    };

    if (_searchQuery.isNotEmpty) {
      params['search_body'] = _searchQuery;
    }
    if (_dateFrom.isNotEmpty) {
      params['date_from'] = _dateFrom;
    }
    if (_dateTo.isNotEmpty) {
      params['date_to'] = _dateTo;
    }
    if (_selectedTags.isNotEmpty) {
      params['tags'] = _selectedTags;
    }
    if (_selectedSources.isNotEmpty) {
      params['sources'] = _selectedSources;
    }

    return params;
  }


  @override
  String toString() {
    return 'SearchParams(searchQuery: $searchQuery, dateFrom: $dateFrom, dateTo: $dateTo, '
        'selectedSources: $selectedSources, selectedTags: $selectedTags)';
  }
}