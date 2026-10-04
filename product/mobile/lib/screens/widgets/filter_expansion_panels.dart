import 'package:flutter/material.dart';
import '../../providers/abstract/filter_provider.dart';

class FilterExpansionPanels extends StatefulWidget {
  final FilterProvider provider;

  const FilterExpansionPanels({super.key, required this.provider});

  @override
  State<FilterExpansionPanels> createState() => _FilterExpansionPanelsState();
}

class _FilterExpansionPanelsState extends State<FilterExpansionPanels> {
  ExpansionPanelRadio _buildExpansionPanel({
    required String value,
    required String title,
    required List<String> items,
    required List<String> selectedItems,
    required void Function(String) onToggle,
  }) {
    return ExpansionPanelRadio(
      value: value,
      canTapOnHeader: true,
      backgroundColor: Theme.of(context).scaffoldBackgroundColor,
      headerBuilder: (BuildContext context, bool isExpanded) {
        return ListTile(
          title: Text(
            title,
            style: const TextStyle(
              fontSize: 16,
              fontWeight: FontWeight.bold,
            ),
          ),
        );
      },
      body: _buildCheckboxList(items, selectedItems, onToggle),
    );
  }

  Widget _buildCheckboxList(List<String> items, List<String> selectedItems,
      void Function(String) onToggle) {
    return Container(
      color: Theme.of(context).scaffoldBackgroundColor,
      constraints: const BoxConstraints(maxHeight: 140),
      padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 4),
      child: SingleChildScrollView(
        child: Column(
          children: items.map((item) {
            return _buildCheckboxItem(item, selectedItems, onToggle);
          }).toList(),
        ),
      ),
    );
  }

  Widget _buildCheckboxItem(
      String item, List<String> selectedItems, void Function(String) onToggle) {
    return CheckboxListTile(
      contentPadding: const EdgeInsets.symmetric(horizontal: 6),
      title: Text(item, style: const TextStyle(fontSize: 14)),
      value: selectedItems.contains(item),
      onChanged: (bool? value) {
        setState(() {
          onToggle(item);
        });
      },
    );
  }

  @override
  Widget build(BuildContext context) {
    return Container(
      color: Theme.of(context).scaffoldBackgroundColor,
      child: ExpansionPanelList.radio(
        elevation: 0,
        expandedHeaderPadding: EdgeInsets.zero,
        dividerColor: Colors.transparent,
        materialGapSize: 1,
        children: [
          _buildExpansionPanel(
            value: 'sources',
            title: 'Источники',
            items: widget.provider.sources,
            selectedItems: widget.provider.selectedSources,
            onToggle: widget.provider.toggleSource,
          ),
          _buildExpansionPanel(
            value: 'tags',
            title: 'Тэги',
            items: widget.provider.tags,
            selectedItems: widget.provider.selectedTags,
            onToggle: widget.provider.toggleTag,
          ),
        ],
      ),
    );

  }
}
