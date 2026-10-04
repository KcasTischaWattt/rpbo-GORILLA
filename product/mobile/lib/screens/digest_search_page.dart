import 'package:flutter/material.dart';
import 'package:intl/intl.dart';
import 'package:provider/provider.dart';
import '../providers/digest_provider.dart';
import 'digest_search_results_page.dart';
import 'widgets/filter_expansion_panels.dart';
import 'widgets/action_buttons.dart';

class DigestSearchPage extends StatefulWidget {
  const DigestSearchPage({super.key});

  @override
  _DigestSearchPageState createState() => _DigestSearchPageState();
}

class _DigestSearchPageState extends State<DigestSearchPage> {
  late TextEditingController _searchController;
  late TextEditingController _dateFromController;
  late TextEditingController _dateToController;

  @override
  void initState() {
    super.initState();

    final provider = Provider.of<DigestProvider>(context, listen: false);
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
    final provider = Provider.of<DigestProvider>(context, listen: false);
    provider.resetFilters();

    _searchController.text = provider.searchQuery;
    _dateFromController.text = provider.dateFrom;
    _dateToController.text = provider.dateTo;
  }

  void _onSearchQueryChanged() {
    Provider.of<DigestProvider>(context, listen: false)
        .setSearchQuery(_searchController.text);
  }

  Future<void> _selectDate(
      BuildContext context, TextEditingController controller, Function(DateTime) setDate) async {
    final DateTime? picked = await showDatePicker(
      context: context,
      initialDate: DateTime.now(),
      firstDate: DateTime(2000),
      lastDate: DateTime(2101),
    );
    if (picked != null) {
      setDate(picked);
      controller.text = DateFormat('dd/MM/yyyy').format(picked);
    }
  }

  void _performSearch() {
    final provider = Provider.of<DigestProvider>(context, listen: false);
    provider.applySearchParams();
    provider.loadSearchedDigests();
    Navigator.push(
      context,
      MaterialPageRoute(builder: (context) => const DigestSearchResultsPage()),
    );
  }

  AppBar _buildAppBar() {
    return AppBar(
      toolbarHeight: 60,
      backgroundColor: Theme.of(context).scaffoldBackgroundColor,
      surfaceTintColor: Colors.transparent,
      elevation: 0,
      shadowColor: Colors.transparent,
      title: const Text(
        'Поиск дайджестов',
        style: TextStyle(fontSize: 24, fontWeight: FontWeight.bold),
      ),
    );
  }

  Widget _buildSearchInput() {
    return Container(
      decoration: BoxDecoration(
        color: Theme.of(context).bottomNavigationBarTheme.backgroundColor,
        borderRadius: BorderRadius.circular(8),
      ),
      child: Row(
        children: [
          Expanded(
            child: TextField(
              controller: _searchController,
              style: const TextStyle(fontSize: 16),
              decoration: InputDecoration(
                hintText: 'Поиск по названию',
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

  Widget _buildFilters(DigestProvider provider) {
    return Consumer<DigestProvider>(
      builder: (context, provider, child) {
        return FilterExpansionPanels(provider: provider);
      },
    );
  }

  Widget _buildDatePickers() {
    final provider = Provider.of<DigestProvider>(context);
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

  BoxDecoration _containerDecoration() {
    return BoxDecoration(
      color: Theme.of(context).bottomNavigationBarTheme.backgroundColor,
      borderRadius: BorderRadius.circular(8),
    );
  }

  Widget _buildDatePickerField(String label, TextEditingController controller,
      Function(DateTime) setDate) {
    return Container(
      decoration: _containerDecoration(),
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
    final provider = Provider.of<DigestProvider>(context);
    return Scaffold(
      appBar: _buildAppBar(),
      body: SingleChildScrollView(
        child: Padding(
          padding: const EdgeInsets.symmetric(horizontal: 6),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              _buildSearchInput(),
              const SizedBox(height: 16),
              _buildFilters(provider),
              const SizedBox(height: 16),
              _buildDatePickers(),
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
