import 'package:crosswords/data/models/subscription.dart';
import 'package:crosswords/providers/subscription_provider.dart';
import 'package:crosswords/screens/widgets/loading_refresh_button.dart';
import 'package:crosswords/screens/widgets/subscription_avatar.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../providers/auth_provider.dart';
import 'digest_edit_page.dart';

class SubscriptionsPage extends StatefulWidget {
  const SubscriptionsPage({super.key});

  @override
  _SubscriptionsPageState createState() => _SubscriptionsPageState();
}

class _SubscriptionsPageState extends State<SubscriptionsPage>
    with SingleTickerProviderStateMixin {
  bool _showOnlySubscriptions = false;
  int? _loadingEditSubscriptionId;

  @override
  void initState() {
    super.initState();
    _loadSubscriptions();
  }

  void _loadSubscriptions() {
    Future.microtask(() {
      if (!mounted) return;
      final provider =
          Provider.of<SubscriptionProvider>(context, listen: false);
      if (provider.subscriptions.isEmpty) {
        provider.loadSubscriptions();
      }
    });
  }

  Widget _buildOnlySubscriptionsToggle() {
    final isAuthenticated = Provider.of<AuthProvider>(context).isAuthenticated;
    if (!isAuthenticated) return const SizedBox.shrink();

    return Padding(
      padding: const EdgeInsets.all(8.0),
      child: Align(
        alignment: Alignment.centerLeft,
        child: ChoiceChip(
          label: const Text("Только подписки"),
          selected: _showOnlySubscriptions,
          onSelected: (bool selected) {
            // TODO обновление списка подписок
            setState(() {
              _showOnlySubscriptions = selected;
            });
          },
        ),
      ),
    );
  }

  Widget _buildSubscriptionList(SubscriptionProvider provider) {
    final subscriptions = _showOnlySubscriptions
        ? provider.subscriptions
            .where((sub) => sub.subscribeOptions.subscribed)
            .toList()
        : provider.subscriptions;

    if (subscriptions.isEmpty) {
      return const Center(child: Text("Нет подписок"));
    }

    return ListView.builder(
      itemCount: subscriptions.length,
      itemBuilder: (context, index) {
        return _buildSubscriptionItem(subscriptions[index], provider);
      },
    );
  }

  void _navigateToDigestsPage(int subscriptionId) async {
    final subscriptionProvider =
        Provider.of<SubscriptionProvider>(context, listen: false);
    await subscriptionProvider.selectSubscription(subscriptionId, context);
    Navigator.pop(context);
  }

  Widget _buildSubscriptionItem(
      Subscription subscription, SubscriptionProvider provider) {
    return InkWell(
      onTap: () {
        _navigateToDigestsPage(subscription.id);
      },
      borderRadius: BorderRadius.circular(8),
      child: SizedBox(
        height: 60,
        child: ListTile(
          leading: SubscriptionAvatar(subscription: subscription),
          title: _buildTitle(subscription),
          trailing: _buildTrailingButtons(subscription, provider),
          contentPadding: const EdgeInsets.symmetric(horizontal: 16),
        ),
      ),
    );
  }

  Widget _buildTitle(Subscription subscription) {
    return Text(
      subscription.title,
      maxLines: 2,
      overflow: TextOverflow.ellipsis,
      style: const TextStyle(fontSize: 16),
    );
  }

  Widget _buildTrailingButtons(
      Subscription subscription, SubscriptionProvider provider) {
    final isAuthenticated = Provider.of<AuthProvider>(context).isAuthenticated;
    if (!isAuthenticated) return const SizedBox.shrink();

    return Row(
      mainAxisSize: MainAxisSize.min,
      children: [
        if (subscription.isOwner && subscription.subscribeOptions.subscribed)
          _buildEditButton(subscription),
        if (subscription.subscribeOptions.subscribed)
          _buildNotificationButton(subscription, provider),
        _buildSubscriptionToggleButton(subscription, provider),
      ],
    );
  }

  Widget _buildEditButton(Subscription subscription) {
    final isLoading = _loadingEditSubscriptionId == subscription.id;

    return IconButton(
      icon: isLoading
          ? const SizedBox(
              width: 24,
              height: 24,
              child: CircularProgressIndicator(strokeWidth: 2),
            )
          : const Icon(Icons.edit),
      onPressed: isLoading
          ? null
          : () async {
              final subscriptionProvider =
                  Provider.of<SubscriptionProvider>(context, listen: false);
              setState(() {
                _loadingEditSubscriptionId = subscription.id;
              });

              try {
                final fetchedSubscription = await subscriptionProvider
                    .fetchSubscriptionById(subscription.id);
                if (!mounted) return;
                Navigator.push(
                  context,
                  MaterialPageRoute(
                    builder: (context) =>
                        DigestEditPage(subscription: fetchedSubscription),
                  ),
                );
              } catch (e) {
                if (!mounted) return;
                ScaffoldMessenger.of(context).showSnackBar(
                  SnackBar(content: Text('Ошибка загрузки подписки: $e')),
                );
              } finally {
                if (mounted) {
                  setState(() {
                    _loadingEditSubscriptionId = null;
                  });
                }
              }
            },
    );
  }

  Widget _buildNotificationButton(
      Subscription subscription, SubscriptionProvider provider) {
    return IconButton(
      icon: Icon(
        subscription.subscribeOptions.mobileNotifications
            ? Icons.notifications_active
            : Icons.notifications_none,
      ),
      onPressed: () async {
        final newSubscription = subscription.copyWith(
          subscribeOptions: subscription.subscribeOptions.copyWith(
            mobileNotifications: !subscription.subscribeOptions.mobileNotifications,
          ),
        );

        provider.updateSubscription(newSubscription);

        try {
          await provider.updateSubscriptionSettings(newSubscription);
        } catch (e) {
          provider.updateSubscription(subscription);

          ScaffoldMessenger.of(context).showSnackBar(
            SnackBar(content: Text('Ошибка обновления уведомлений: $e')),
          );
        }
      },
    );
  }


  Widget _buildSubscriptionToggleButton(
      Subscription subscription, SubscriptionProvider provider) {
    return IconButton(
        icon: Icon(
          subscription.subscribeOptions.subscribed
              ? Icons.check_circle
              : Icons.add_circle_outline,
        ),
        onPressed: () async {
          final subscriptionProvider =
              Provider.of<SubscriptionProvider>(context, listen: false);
          await subscriptionProvider.handleUnsubscribe(
            context: context,
            subscriptionId: subscription.id,
          );
        });
  }

  AppBar _buildAppBar() {
    final subscriptionProvider =
        Provider.of<SubscriptionProvider>(context, listen: false);

    return AppBar(
      toolbarHeight: 60,
      backgroundColor: Theme.of(context).scaffoldBackgroundColor,
      surfaceTintColor: Colors.transparent,
      elevation: 0,
      shadowColor: Colors.transparent,
      title: const Text(
        'Темы дайджестов',
        style: TextStyle(fontSize: 24, fontWeight: FontWeight.bold),
      ),
      actions: [
        LoadingRefreshButton(
          onRefresh: () async {
            await subscriptionProvider.loadSubscriptions();
          },
          isDisabled: subscriptionProvider.isLoading,
        ),
      ],
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: _buildAppBar(),
      body: Column(
        children: [
          _buildOnlySubscriptionsToggle(),
          Expanded(
            child: Consumer<SubscriptionProvider>(
              builder: (context, provider, child) {
                if (provider.isLoading) {
                  return const Center(
                    child: CircularProgressIndicator(),
                  );
                }
                return _buildSubscriptionList(provider);
              },
            ),
          ),
        ],
      ),
    );
  }
}
