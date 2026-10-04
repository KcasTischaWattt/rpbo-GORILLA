import 'package:crosswords/screens/widgets/digest_components/section_title.dart';
import 'package:crosswords/screens/widgets/item_chips_list_widget.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../../../providers/subscription_provider.dart';

class FollowersSection extends StatelessWidget {
  final TextEditingController recipientController;

  const FollowersSection({super.key, required this.recipientController});

  void _showManageFollowersDialog(BuildContext context) {
    showDialog(
      context: context,
      builder: (context) {
        return Consumer<SubscriptionProvider>(
          builder: (context, provider, child) {
            return AlertDialog(
              title: const Text("Управление подписчиками"),
              content: SizedBox(
                width: double.maxFinite,
                child: ListView(
                  shrinkWrap: true,
                  children: provider.followers.map((user) {
                    final isCurrentUser = user == provider.currentUserEmail;
                    return ListTile(
                      title: Text(user),
                      trailing: isCurrentUser
                          ? null
                          : IconButton(
                        icon: const Icon(Icons.delete),
                        onPressed: () {
                          _showDeleteConfirmationDialog(context, user);
                        },
                      ),
                    );
                  }).toList(),
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
      },
    );
  }

  void _showDeleteConfirmationDialog(BuildContext context, String user) {
    final provider = Provider.of<SubscriptionProvider>(context, listen: false);

    showDialog(
      context: context,
      builder: (context) {
        return AlertDialog(
          title: const Text("Подтверждение удаления"),
          content:
              Text("Вы уверены, что хотите удалить '$user' из подписчиков?"),
          actions: [
            TextButton(
              onPressed: () => Navigator.pop(context),
              child: const Text("Отмена"),
            ),
            TextButton(
              onPressed: () {
                provider.removeFollower(user);
                Navigator.pop(context);
                Navigator.pop(context);
                _showManageFollowersDialog(context);
              },
              child: const Text("Удалить", style: TextStyle(color: Colors.red)),
            ),
          ],
        );
      },
    );
  }

  @override
  Widget build(BuildContext context) {
    return Consumer<SubscriptionProvider>(
      builder: (context, provider, child) {
        return Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            SectionTitle(title: "Добавить получателя"),
            Row(
              children: [
                Expanded(
                  child: Container(
                    decoration: BoxDecoration(
                      color: Theme.of(context)
                          .bottomNavigationBarTheme
                          .backgroundColor,
                      borderRadius: BorderRadius.circular(8),
                    ),
                    child: TextField(
                      controller: recipientController,
                      style: const TextStyle(fontSize: 18),
                      decoration: InputDecoration(
                        hintText: 'Добавить получателя',
                        hintStyle:
                            TextStyle(color: Colors.grey[600], fontSize: 16),
                        border: InputBorder.none,
                        contentPadding:
                            EdgeInsets.symmetric(horizontal: 8, vertical: 8),
                      ),
                    ),
                  ),
                ),
                IconButton(
                  icon: const Icon(Icons.add_circle),
                  onPressed: () async {
                    final provider = Provider.of<SubscriptionProvider>(context,
                        listen: false);
                    final email = recipientController.text.trim();
                    if (email.isEmpty) return;
                    try {
                      await provider.addFollowerWithValidation(email);
                      recipientController.clear();
                    } catch (e) {
                      ScaffoldMessenger.of(context).showSnackBar(
                        SnackBar(content: Text(e.toString())),
                      );
                    }
                  },
                ),
              ],
            ),
            const SizedBox(height: 8),
            ItemListWidget(
              items: provider.followers,
              dialogTitle: "Все подписчики",
              chipColor: Theme.of(context).primaryColor,
              textColor: Colors.black,
              fontWeight: FontWeight.bold,
            ),
            const SizedBox(height: 8),
            Row(
              mainAxisAlignment: MainAxisAlignment.end,
              children: [
                TextButton(
                  onPressed: () => _showManageFollowersDialog(context),
                  child: Row(
                    children: [
                      Text("Настроить подписчиков",
                          style: TextStyle(fontSize: 16)),
                      const SizedBox(width: 8),
                      const Icon(Icons.settings, size: 24),
                    ],
                  ),
                ),
              ],
            ),
          ],
        );
      },
    );
  }
}
