import 'package:flutter/material.dart';
import 'package:intl/intl.dart';
import 'package:provider/provider.dart';
import '../providers/article_provider.dart';
import 'article_detail_page.dart';
import 'widgets/filter_expansion_panels.dart';
import 'widgets/action_buttons.dart';

class ArticleSearchPage extends StatefulWidget {
  const ArticleSearchPage({super.key});

  @override
  _ArticleSearchPageState createState() => _ArticleSearchPageState();
}

class _ArticleSearchPageState extends State<ArticleSearchPage> {
  late TextEditingController _searchController;
  late TextEditingController _dateFromController;
  late TextEditingController _dateToController;
  bool _isSearchExpanded = false;

  @override
  void initState() {
    super.initState();

    final provider = Provider.of<ArticleProvider>(context, listen: false);
    _searchController = TextEditingController(text: provider.searchQuery);
    _dateFromController = TextEditingController(text: provider.dateFrom);
    _dateToController = TextEditingController(text: provider.dateTo);

    _searchController.addListener(_onSearchQueryChanged);
  }

  @override
  void dispose() {
    _searchController.removeListener(_onSearchQueryChanged);

    _searchController.dispose();
    _dateFromController.dispose();
    _dateToController.dispose();

    super.dispose();
  }

  void _resetFilters() {
    final provider = Provider.of<ArticleProvider>(context, listen: false);
    provider.resetFilters();

    _searchController.text = provider.searchQuery;
    _dateFromController.text = provider.dateFrom;
    _dateToController.text = provider.dateTo;
  }

  void _setSearchOption(String option) {
    Provider.of<ArticleProvider>(context, listen: false)
        .setSearchOption(option);
    _toggleSearchExpansion();
  }

  void _onSearchQueryChanged() {
    Provider.of<ArticleProvider>(context, listen: false)
        .setSearchQuery(_searchController.text);
  }

  void _toggleSearchExpansion() {
    setState(() {
      _isSearchExpanded = !_isSearchExpanded;
    });
  }

  Future<void> _selectDate(BuildContext context,
      TextEditingController controller, Function(DateTime) setDate) async {
    final DateTime? picked = await showDatePicker(
      context: context,
      initialDate: DateTime.now(),
      firstDate: DateTime(2000),
      lastDate: DateTime(2101),
    );
    if (picked != null) {
      final formattedDate = DateFormat('dd/MM/yyyy').format(picked);
      controller.text = formattedDate;
      setDate(picked);
    }
  }

  /// Метод для отображения Snackbar с сообщением
  void _showSnackBar(String message) {
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(content: Text(message)),
    );
  }

  /// Метод для выполнения поиска по статьям
  void _performSearch() {
    final provider = Provider.of<ArticleProvider>(context, listen: false);
    if (provider.selectedSearchOption == "Поиск по ID") {
      _handleSearchById(context);
      return;
    }

    provider.applySearchParams();
    provider.loadArticles();
    Navigator.pop(context);
  }

  /// Метод для обработки поиска по ID
  Future<void> _handleSearchById(BuildContext context) async {
    final provider = Provider.of<ArticleProvider>(context, listen: false);
    final query = _searchController.text.trim();
    try {
      final found = await provider.searchArticleById(query);
      if (!context.mounted) return;
      if (found) {
        Navigator.push(
          context,
          MaterialPageRoute(
            builder: (context) => ArticleDetailPage(
              articleId: provider.currentArticle!.id,
            ),
          ),
        );
      }
    } catch (e) {
      _handleSearchError(e is Exception ? e : Exception("unknown"));
    }
  }

  /// Метод для обработки ошибок поиска
  void _handleSearchError(Exception e) {
    switch (e.toString()) {
      case 'Exception: empty':
        _showSnackBar("Введите ID документа и повторите попытку");
        break;
      case 'Exception: not_numeric':
        _showSnackBar("ID должен быть числом");
        break;
      case 'Exception: not_found':
        _showSnackBar("Документ не найден");
        break;
      case 'Exception: server_error':
        _showSnackBar("Ошибка сервера при поиске");
        break;
      default:
        _showSnackBar("Неизвестная ошибка");
    }
  }

  /// Виджет AppBar
  AppBar _buildAppBar() {
    return AppBar(
      toolbarHeight: 60,
      backgroundColor: Theme.of(context).scaffoldBackgroundColor,
      surfaceTintColor: Colors.transparent,
      elevation: 0,
      shadowColor: Colors.transparent,
      title: const Text(
        'Поиск статей',
        style: TextStyle(fontSize: 24, fontWeight: FontWeight.bold),
      ),
    );
  }

  /// Декорация контейнера аккордеона выбора типа поиска
  BoxDecoration _containerDecoration() {
    return BoxDecoration(
      color: Theme.of(context).bottomNavigationBarTheme.backgroundColor,
      borderRadius: BorderRadius.circular(8),
      border: Border.all(
        color: Theme.of(context).scaffoldBackgroundColor,
        width: 1,
      ),
    );
  }

  /// Заголовок аккордеона выбора типа поиска
  Widget _buildAccordionHeader(ArticleProvider provider) {
    return Row(
      mainAxisAlignment: MainAxisAlignment.spaceBetween,
      children: [
        Text(
          provider.selectedSearchOption,
          style: const TextStyle(fontSize: 17, fontWeight: FontWeight.bold),
        ),
        Icon(
          _isSearchExpanded
              ? Icons.keyboard_arrow_up
              : Icons.keyboard_arrow_down,
          size: 24,
        ),
      ],
    );
  }

  /// Содержимое аккордеона выбора типа поиска
  Widget _buildAccordionContent() {
    return AnimatedSize(
      duration: const Duration(milliseconds: 300),
      curve: Curves.easeInOut,
      child: _isSearchExpanded
          ? Column(
              children: [
                ListTile(
                  contentPadding: const EdgeInsets.symmetric(vertical: 6),
                  title: const Text('Поиск по смыслу'),
                  onTap: () => _setSearchOption('Поиск по смыслу'),
                ),
                ListTile(
                  contentPadding: const EdgeInsets.symmetric(vertical: 6),
                  title: const Text('Точный поиск'),
                  onTap: () => _setSearchOption('Точный поиск'),
                ),
                ListTile(
                  contentPadding: const EdgeInsets.only(top: 6, bottom: 0),
                  title: const Text('Поиск по ID'),
                  onTap: () => _setSearchOption('Поиск по ID'),
                ),
              ],
            )
          : SizedBox.shrink(),
    );
  }

  /// Аккордеон выбора типа поиска
  Widget _buildSearchAccordion(ArticleProvider provider) {
    return Padding(
      padding: const EdgeInsets.symmetric(horizontal: 6, vertical: 8),
      child: GestureDetector(
        onTap: _toggleSearchExpansion,
        child: Container(
          padding: const EdgeInsets.all(12),
          decoration: _containerDecoration(),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              _buildAccordionHeader(provider),
              _buildAccordionContent(),
            ],
          ),
        ),
      ),
    );
  }

  /// Виджет для поля ввода строки поиска
  Widget _buildSearchInput() {
    return Container(
      decoration: _containerDecoration(),
      child: Row(
        children: [
          Expanded(
            child: TextField(
              controller: _searchController,
              style: const TextStyle(fontSize: 16),
              decoration: InputDecoration(
                hintText: 'Строка поиска',
                hintStyle: TextStyle(color: Colors.grey[600], fontSize: 16),
                border: InputBorder.none,
                contentPadding:
                    const EdgeInsets.symmetric(horizontal: 8, vertical: 8),
              ),
            ),
          ),
          Padding(
            padding: const EdgeInsets.only(right: 16),
            child: Icon(
              Icons.search,
              color: Colors.grey[600],
              size: 24,
            ),
          ),
        ],
      ),
    );
  }

  /// Чекбокс "Искать в тексте"
  Widget _buildCheckbox(ArticleProvider provider) {
    return CheckboxListTile(
      contentPadding: EdgeInsets.zero,
      title: const Text(
        'Искать в тексте',
        style: TextStyle(fontSize: 14),
      ),
      value: provider.searchInText,
      onChanged: (bool? value) => provider.setSearchInText(value ?? false),
      controlAffinity: ListTileControlAffinity.leading,
    );
  }

  /// Виджет фильтров
  Widget _buildFilters(ArticleProvider provider) {
    return Column(
      children: [
        Consumer<ArticleProvider>(
          builder: (context, provider, child) {
            return FilterExpansionPanels(provider: provider);
          },
        ),
        const SizedBox(height: 16),
        _buildDatePickers(),
      ],
    );
  }

  /// Поля выбора даты
  Widget _buildDatePickers() {
    final provider = Provider.of<ArticleProvider>(context);
    return Row(
      children: [
        Expanded(
            child: _buildDatePickerField(
                'Дата С', _dateFromController, provider.setDateFromDateTime)),
        const SizedBox(width: 12),
        Expanded(
            child: _buildDatePickerField(
                'Дата По', _dateToController, provider.setDateToDateTime)),
      ],
    );
  }

  BoxDecoration _dateContainerDecoration() {
    return BoxDecoration(
      color: Theme.of(context).bottomNavigationBarTheme.backgroundColor,
      borderRadius: BorderRadius.circular(8),
    );
  }

  /// Поле выбора даты
  Widget _buildDatePickerField(String label, TextEditingController controller,
      Function(DateTime) setDate) {
    return Container(
      decoration: _dateContainerDecoration(),
      child: TextField(
        controller: controller,
        readOnly: true,
        style: const TextStyle(fontSize: 16),
        onTap: () => _selectDate(context, controller, setDate),
        decoration: InputDecoration(
          labelText: label,
          labelStyle: const TextStyle(fontSize: 14),
          border: InputBorder.none,
          contentPadding:
              const EdgeInsets.symmetric(horizontal: 12, vertical: 12),
        ),
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    final provider = Provider.of<ArticleProvider>(context);
    return Scaffold(
      appBar: _buildAppBar(),
      body: SingleChildScrollView(
        child: Padding(
          padding: const EdgeInsets.symmetric(horizontal: 6),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              _buildSearchAccordion(provider),
              const SizedBox(height: 10),
              _buildSearchInput(),
              if (provider.selectedSearchOption == 'Точный поиск')
                _buildCheckbox(provider),
              if (provider.selectedSearchOption != 'Поиск по ID')
                _buildFilters(provider),
              const SizedBox(height: 16),
              ActionButtons(
                onPrimaryPressed: _performSearch,
                onSecondaryPressed: _resetFilters,
                primaryText: 'Найти',
                secondaryText: 'Сбросить фильтры',
                primaryIcon: Icons.search,
              ),
              const SizedBox(height: 24),
            ],
          ),
        ),
      ),
    );
  }
}
