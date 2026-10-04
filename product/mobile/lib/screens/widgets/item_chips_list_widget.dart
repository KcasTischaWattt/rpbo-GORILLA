import 'package:flutter/material.dart';

class ItemListWidget extends StatelessWidget {
  final List<String> items;
  final String dialogTitle;
  final Color chipColor;
  final Color textColor;
  final FontWeight fontWeight;

  const ItemListWidget({
    super.key,
    required this.items,
    required this.dialogTitle,
    required this.chipColor,
    required this.textColor,
    required this.fontWeight,
  });

  /// Универсальный метод для диалогового окна
  void _showAllItemsDialog(BuildContext context) {
    showDialog(
      context: context,
      builder: (context) {
        return AlertDialog(
          title: Text(dialogTitle),
          content: SizedBox(
            width: double.maxFinite,
            child: ListView(
              shrinkWrap: true,
              children: items
                  .map((item) => Text(
                item,
                style: const TextStyle(fontWeight: FontWeight.normal),
              ))
                  .toList(),
            ),
          ),
          actions: [
            TextButton(
              onPressed: () => Navigator.pop(context),
              child: const Text("Закрыть"),
            ),
          ],
        );
      },
    );
  }

  /// Универсальный метод построения списка источников и тэгов
  @override
  Widget build(BuildContext context) {
    if (items.isEmpty) return const SizedBox.shrink();

    double screenWidth = MediaQuery.of(context).size.width;
    int itemLimit = screenWidth <= 350 ? 3 : 4;
    int renderedItems = items.length > itemLimit ? itemLimit - 1 : items.length;

    return Wrap(
      spacing: 8,
      runSpacing: 4,
      children: [
        ...items.take(renderedItems).map((item) => Chip(
          label: Text(item,
              style: TextStyle(
                  color: textColor, fontSize: 14, fontWeight: fontWeight)),
          backgroundColor: chipColor,
        )),
        if (items.length > itemLimit)
          GestureDetector(
            onTap: () => _showAllItemsDialog(context),
            child: Chip(
              label: Text(
                "Ещё ${items.length - renderedItems}",
                style: TextStyle(
                    color: textColor, fontSize: 14, fontWeight: fontWeight),
              ),
              backgroundColor: chipColor,
            ),
          ),
      ],
    );
  }
}