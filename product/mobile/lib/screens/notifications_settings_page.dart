import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:crosswords/data/models/subscription.dart';
import 'package:crosswords/providers/subscription_provider.dart';
import 'package:crosswords/screens/widgets/subscription_avatar.dart';

class MySubscriptionsPage extends StatefulWidget {
  const MySubscriptionsPage({super.key});

  @override
  State<MySubscriptionsPage> createState() => _MySubscriptionsPageState();
}

class _MySubscriptionsPageState extends State<MySubscriptionsPage> {
  String _searchQuery = '';

  @override
  void initState() {
    super.initState();
    _loadSubscriptions();
  }

  void _loadSubscriptions() {
    Future.microtask(() {
      final provider =
          Provider.of<SubscriptionProvider>(context, listen: false);
      provider.loadSubscriptions();
    });
  }

  List<Subscription> _filterSubscriptions(List<Subscription> subscriptions) {
    return subscriptions
        .where((sub) =>
            sub.subscribeOptions.subscribed &&
            sub.title.toLowerCase().contains(_searchQuery.toLowerCase()))
        .toList();
  }

  AppBar _buildAppBar() {
    return AppBar(
      toolbarHeight: 60,
      backgroundColor: Theme.of(context).scaffoldBackgroundColor,
      surfaceTintColor: Colors.transparent,
      elevation: 0,
      shadowColor: Colors.transparent,
      title: const Text('Мои подписки',
          style: TextStyle(fontSize: 24, fontWeight: FontWeight.bold)),
    );
  }

  Widget _buildSearchField() {
    return Padding(
      padding: const EdgeInsets.all(12),
      child: TextField(
        onChanged: (value) => setState(() => _searchQuery = value),
        style: const TextStyle(fontSize: 16),
        decoration: InputDecoration(
          hintText: "Поиск по подпискам...",
          filled: true,
          fillColor: Theme.of(context).bottomNavigationBarTheme.backgroundColor,
          border: OutlineInputBorder(
            borderRadius: BorderRadius.circular(12),
            borderSide: BorderSide.none,
          ),
          prefixIcon: const Icon(Icons.search),
        ),
      ),
    );
  }

  Widget _buildSubscriptionItem(Subscription subscription) {
    final provider = Provider.of<SubscriptionProvider>(context, listen: false);

    return Card(
      color: Theme.of(context).bottomNavigationBarTheme.backgroundColor,
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
      margin: const EdgeInsets.symmetric(horizontal: 12, vertical: 6),
      child: ListTile(
        leading: SubscriptionAvatar(subscription: subscription),
        title: Text(subscription.title,
            maxLines: 1, overflow: TextOverflow.ellipsis),
        subtitle: Row(
          children: [
            _buildNotificationToggle(
              icon: Icons.email,
              active: subscription.subscribeOptions.sendToMail,
              onTap: () async {
                final newSubscription = subscription.copyWith(
                  subscribeOptions: subscription.subscribeOptions.copyWith(
                    sendToMail: !subscription.subscribeOptions.sendToMail,
                  ),
                );
                await provider.updateSubscriptionSettings(newSubscription);
              },
            ),
            const SizedBox(width: 8),
            _buildNotificationToggle(
              icon: Icons.notifications,
              active: subscription.subscribeOptions.mobileNotifications,
              onTap: () async {
                final newSubscription = subscription.copyWith(
                  subscribeOptions: subscription.subscribeOptions.copyWith(
                    mobileNotifications:
                        !subscription.subscribeOptions.mobileNotifications,
                  ),
                );
                await provider.updateSubscriptionSettings(newSubscription);
              },
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildNotificationToggle({
    required IconData icon,
    required bool active,
    required VoidCallback onTap,
  }) {
    return GestureDetector(
      onTap: onTap,
      child: Icon(
        icon,
        color: active ? Theme.of(context).primaryColor : Colors.grey,
        size: 24,
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    return Consumer<SubscriptionProvider>(
      builder: (context, provider, _) {
        final filtered = _filterSubscriptions(provider.subscriptions);

        return Scaffold(
          appBar: _buildAppBar(),
          body: Column(
            children: [
              _buildSearchField(),
              if (provider.isLoading)
                const Expanded(
                    child: Center(child: CircularProgressIndicator()))
              else if (filtered.isEmpty)
                const Expanded(child: Center(child: Text("Нет подписок")))
              else
                Expanded(
                  child: ListView.builder(
                    itemCount: filtered.length,
                    itemBuilder: (context, index) =>
                        _buildSubscriptionItem(filtered[index]),
                  ),
                ),
            ],
          ),
        );
      },
    );
  }
}
