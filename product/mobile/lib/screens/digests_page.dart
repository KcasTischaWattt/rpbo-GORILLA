import 'package:crosswords/screens/widgets/loading_button.dart';
import 'package:crosswords/screens/widgets/loading_refresh_button.dart';
import 'package:crosswords/screens/widgets/subscription_avatar.dart';
import '../data/models/subscribe_options.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:velocity_x/velocity_x.dart';
import '../providers/auth_provider.dart';
import '../providers/digest_provider.dart';
import '../data/models/digest.dart';
import 'digest_edit_page.dart';
import 'subscriptions_page.dart';
import 'package:flutter/gestures.dart';
import 'digest_search_page.dart';
import '../providers/subscription_provider.dart';
import '../data/models/subscription.dart';
import 'digest_detail_page.dart';
import 'digest_create_page.dart';

class DigestsPage extends StatefulWidget {
  const DigestsPage({super.key});

  @override
  _DigestsPageState createState() => _DigestsPageState();
}

class _DigestsPageState extends State<DigestsPage>
    with SingleTickerProviderStateMixin {
  late ScrollController _scrollController;
  String? _loadingDigestId;
  String? _loadingEditDigestId;

  @override
  void initState() {
    super.initState();
    _scrollController = ScrollController();
    _scrollController.addListener(_onScroll);

    Future.microtask(() {
      if (!mounted) return;
      final provider = Provider.of<DigestProvider>(context, listen: false);
      final subscriptionProvider =
          Provider.of<SubscriptionProvider>(context, listen: false);

      if (provider.digests.isEmpty) {
        provider.loadDigests();
      }
      if (subscriptionProvider.subscriptions.isEmpty) {
        subscriptionProvider.loadSubscriptions();
      }
    });
  }

  @override
  void dispose() {
    _scrollController.removeListener(_onScroll);
    _scrollController.dispose();
    super.dispose();
  }

  bool _shouldLoadMore(DigestProvider provider) {
    if (!_scrollController.hasClients) return false;
    return _scrollController.position.pixels >=
            _scrollController.position.maxScrollExtent - 200 &&
        !provider.isLoadingMore &&
        !provider.isLoading;
  }

  void _onScroll() {
    final provider = Provider.of<DigestProvider>(context, listen: false);
    if (_shouldLoadMore(provider)) {
      provider.loadMoreDigests();
    }
  }

  void _onCategoryChanged(String category, DigestProvider digestProvider,
      SubscriptionProvider subscriptionProvider) {
    digestProvider.setCategory(category);
    subscriptionProvider.setCategory(category);
  }

  Widget _buildCategoryButtons(DigestProvider digestProvider) {
    final isAuthenticated = Provider.of<AuthProvider>(context).isAuthenticated;
    if (!isAuthenticated) return const SizedBox.shrink();

    final subscriptionProvider =
        Provider.of<SubscriptionProvider>(context, listen: false);
    return AnimatedSize(
      duration: const Duration(milliseconds: 300),
      curve: Curves.easeInOut,
      child: AnimatedOpacity(
        duration: const Duration(milliseconds: 300),
        curve: Curves.easeInOut,
        opacity:
            subscriptionProvider.selectedSubscriptionId == null ? 1.0 : 0.0,
        child: subscriptionProvider.selectedSubscriptionId == null
            ? SizedBox(
                height: 50,
                child: ListView.builder(
                    scrollDirection: Axis.horizontal,
                    itemCount: 3,
                    itemBuilder: (context, index) {
                      final categories = [
                        "Все дайджесты",
                        "Подписки",
                        "Приватные"
                      ];
                      final category = categories[index];
                      final isSelected =
                          digestProvider.selectedCategory == category;
                      return Padding(
                        padding: const EdgeInsets.symmetric(horizontal: 4),
                        child: ChoiceChip(
                          label: Text(category,
                              style: const TextStyle(fontSize: 14)),
                          selected: isSelected,
                          onSelected: (_) => _onCategoryChanged(
                              category, digestProvider, subscriptionProvider),
                          visualDensity: VisualDensity.compact,
                        ),
                      );
                    }),
              )
            : const SizedBox.shrink(),
      ),
    );
  }

  Widget _buildSubscriptionDescription() {
    final selectedSubscription = _getSelectedSubscription();
    final subscriptionProvider = Provider.of<SubscriptionProvider>(context);

    return AnimatedSize(
      duration: const Duration(milliseconds: 300),
      curve: Curves.easeInOut,
      child: AnimatedOpacity(
        duration: const Duration(milliseconds: 300),
        curve: Curves.easeInOut,
        opacity:
            subscriptionProvider.selectedSubscriptionId == null ? 0.0 : 1.0,
        child: subscriptionProvider.selectedSubscriptionId == null
            ? const SizedBox.shrink()
            : Padding(
                padding: const EdgeInsets.all(6),
                child: Card(
                  color: Theme.of(context).scaffoldBackgroundColor,
                  shape: RoundedRectangleBorder(
                      borderRadius: BorderRadius.circular(8)),
                  child: Padding(
                    padding: const EdgeInsets.all(6),
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Text(
                          selectedSubscription!.title,
                          style: const TextStyle(
                              fontSize: 16, fontWeight: FontWeight.bold),
                        ),
                        const SizedBox(height: 8),
                        Text(
                          selectedSubscription.description,
                          style: const TextStyle(fontSize: 14),
                        ),
                      ],
                    ),
                  ),
                ),
              ),
      ),
    );
  }

  Widget _buildSubscriptionsList(SubscriptionProvider provider) {
    final subscriptions = provider.filteredSubscriptions;

    if (provider.isLoading) {
      return const Center(child: CircularProgressIndicator());
    }

    if (subscriptions.isEmpty) {
      return const Center(child: Text("Нет подписок"));
    }

    return ListView.builder(
      scrollDirection: Axis.horizontal,
      itemCount: provider.filteredSubscriptions.length,
      itemBuilder: (context, index) {
        return _buildSubscriptionItem(subscriptions[index]);
      },
    );
  }

  Widget _buildSubscriptionItem(Subscription subscription) {
    final subscriptionProvider = Provider.of<SubscriptionProvider>(context);
    bool isSelected =
        subscriptionProvider.selectedSubscriptionId == subscription.id;
    return GestureDetector(
      onTap: () {
        final digestProvider =
            Provider.of<DigestProvider>(context, listen: false);
        if (subscriptionProvider.selectedSubscriptionId == subscription.id) {
          setState(() {
            subscriptionProvider.resetSelectedSubscription();
          });
          digestProvider.loadDigests();
          return;
        }

        setState(() {
          subscriptionProvider.setSelectedSubscription(subscription.id);
        });

        digestProvider.loadDigestsBySubscription(subscription.id);
      },
      child: AnimatedContainer(
        duration: const Duration(milliseconds: 200),
        curve: Curves.easeInOut,
        padding: const EdgeInsets.symmetric(horizontal: 8),
        decoration: BoxDecoration(
          color: isSelected
              ? Theme.of(context).bottomNavigationBarTheme.backgroundColor
              : Colors.transparent,
          borderRadius: BorderRadius.circular(12),
        ),
        child: SizedBox(
          width: 80,
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              SubscriptionAvatar(subscription: subscription),
              const SizedBox(height: 4),
              Text(
                subscription.title,
                overflow: TextOverflow.ellipsis,
                maxLines: 1,
                style: TextStyle(
                  fontSize: 12,
                  fontWeight: isSelected ? FontWeight.bold : FontWeight.normal,
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }

  Widget _buildAllSubscriptionsButton() {
    return Container(
      width: 60,
      height: 80,
      alignment: Alignment.center,
      child: TextButton(
        onPressed: () {
          Navigator.push(
            context,
            MaterialPageRoute(builder: (context) => const SubscriptionsPage()),
          );
        },
        child: const Text(
          "Все",
          style: TextStyle(
              fontSize: 14, fontWeight: FontWeight.bold, color: Colors.blue),
        ),
      ),
    );
  }

  Widget _buildSubscriptionsRow() {
    return Consumer<SubscriptionProvider>(
      builder: (context, provider, child) {
        return SizedBox(
          height: 80,
          child: Row(
            children: [
              Expanded(child: _buildSubscriptionsList(provider)),
              _buildAllSubscriptionsButton(),
            ],
          ),
        );
      },
    );
  }

  // диалоговое окно с источниками
  void _showAllSourcesDialog(BuildContext context, List<String> sources) {
    showDialog(
      context: context,
      builder: (context) {
        return AlertDialog(
          title: const Text("Все источники"),
          content: SizedBox(
            width: double.maxFinite,
            child: ListView(
              shrinkWrap: true,
              children: sources
                  .map((source) => Text(
                        source,
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

  // диалоговое окно с тэгами
  void _showAllTagsDialog(BuildContext context, List<String> tags) {
    showDialog(
      context: context,
      builder: (context) {
        return AlertDialog(
          title: const Text("Все теги"),
          content: SizedBox(
            width: double.maxFinite,
            child: ListView(
              shrinkWrap: true,
              children: tags
                  .map((tag) => Text(
                        tag,
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

  Widget _buildSourcesText(List<String> sources) {
    if (sources.isEmpty) return const SizedBox.shrink();

    final textColor = Theme.of(context).textTheme.bodyLarge!.color!;

    return RichText(
      text: TextSpan(
        children: sources.length <= 3
            ? _buildShortSourcesText(sources)
            : _buildCondensedSourcesText(sources, textColor),
        style: TextStyle(fontSize: 14, color: textColor),
      ),
    );
  }

  List<InlineSpan> _buildShortSourcesText(List<String> sources) {
    List<InlineSpan> spans = [
      const TextSpan(
        text: "Источники: ",
        style: TextStyle(fontWeight: FontWeight.normal, fontSize: 14),
      ),
    ];

    for (int i = 0; i < sources.length; i++) {
      spans.add(_buildSourceSpan(sources[i]));
      if (i < sources.length - 1) {
        spans.add(const TextSpan(
            text: ", ", style: TextStyle(fontWeight: FontWeight.normal)));
      }
    }
    return spans;
  }

  List<InlineSpan> _buildCondensedSourcesText(
      List<String> sources, Color textColor) {
    return [
      const TextSpan(
        text: "Источники: ",
        style: TextStyle(fontWeight: FontWeight.normal, fontSize: 14),
      ),
      _buildSourceSpan(sources[0]),
      const TextSpan(
          text: ", ", style: TextStyle(fontWeight: FontWeight.normal)),
      _buildSourceSpan(sources[1]),
      const TextSpan(
          text: " и ", style: TextStyle(fontWeight: FontWeight.normal)),
      TextSpan(
        text: "ещё ${sources.length - 2}",
        style: TextStyle(
          fontWeight: FontWeight.bold,
          decoration: TextDecoration.underline,
          color: textColor,
        ),
        recognizer: TapGestureRecognizer()
          ..onTap = () {
            _showAllSourcesDialog(context, sources);
          },
      ),
    ];
  }

  TextSpan _buildSourceSpan(String source) {
    return TextSpan(
      text: source,
      style: const TextStyle(fontWeight: FontWeight.bold),
    );
  }

  // Тэги
  Widget _buildTags(List<String> tags) {
    if (tags.isEmpty) return const SizedBox.shrink();

    double screenWidth = MediaQuery.of(context).size.width;
    int tagLimit = screenWidth <= 350 ? 2 : 3;
    int renderedTags = tags.length > tagLimit ? tagLimit - 1 : tags.length;

    return Wrap(
      spacing: 8,
      runSpacing: 4,
      children: [
        ...tags.take(renderedTags).map((tag) => Chip(
              label: Text(tag,
                  style: const TextStyle(
                      color: Colors.black,
                      fontSize: 12,
                      fontWeight: FontWeight.bold)),
              backgroundColor: Theme.of(context).primaryColor,
            )),
        if (tags.length > tagLimit)
          GestureDetector(
            onTap: () => _showAllTagsDialog(context, tags),
            child: Chip(
              label: Text(
                "Ещё ${tags.length - renderedTags}",
                style: const TextStyle(
                    color: Colors.black,
                    fontSize: 12,
                    fontWeight: FontWeight.bold),
              ),
              backgroundColor: Theme.of(context).primaryColor,
            ),
          ),
      ],
    );
  }

  bool _shouldShowLoading(DigestProvider provider, List<Digest> digests) {
    return provider.isLoading;
  }

  Widget _buildLoadingIndicator() {
    return const Padding(
      padding: EdgeInsets.all(12),
      child: Center(child: CircularProgressIndicator()),
    );
  }

  Widget _buildDigestCard(Digest digest) {
    return _buildCardContainer(
      child: LayoutBuilder(
        builder: (context, constraints) {
          bool isNarrowScreen = constraints.maxWidth <= 300;
          return _buildDigestContent(digest, isNarrowScreen);
        },
      ),
    );
  }

  Widget _buildCardContainer({required Widget child}) {
    return Card(
      color: Theme.of(context).bottomNavigationBarTheme.backgroundColor,
      margin: const EdgeInsets.symmetric(vertical: 6),
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(8)),
      child: Padding(padding: const EdgeInsets.all(8), child: child),
    );
  }

  Widget _buildDigestContent(Digest digest, bool isNarrowScreen) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        _buildDigestLayout(digest, isNarrowScreen),
        const SizedBox(height: 8),
        _buildSourcesText(digest.sources),
        const SizedBox(height: 8),
        _buildDigestText(digest.text),
        const SizedBox(height: 8),
        _buildTags(digest.tags),
        const SizedBox(height: 4),
        _buildFooter(digest),
      ],
    );
  }

  Widget _buildDigestLayout(Digest digest, bool isNarrowScreen) {
    if (isNarrowScreen) {
      return Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          _buildDigestTitle(digest),
          const SizedBox(height: 8),
          _buildDigestActions(digest),
        ],
      );
    } else {
      return Row(
        mainAxisAlignment: MainAxisAlignment.spaceBetween,
        children: [
          Expanded(child: _buildDigestTitle(digest)),
          _buildDigestActions(digest),
        ],
      );
    }
  }

  Widget _buildDigestActions(Digest digest) {
    final isAuthenticated = Provider.of<AuthProvider>(context).isAuthenticated;
    if (!isAuthenticated) return const SizedBox.shrink();

    return Row(
      mainAxisSize: MainAxisSize.min,
      children: [
        _buildSubscribeButton(digest),
        _buildNotificationButton(digest),
        _buildEditButton(digest),
      ],
    );
  }

  Widget _buildSubscribeButton(Digest digest) {
    final subscriptionProvider =
        Provider.of<SubscriptionProvider>(context, listen: false);

    return IconButton(
      icon: Icon(digest.subscribeOptions.subscribed
          ? Icons.check_circle
          : Icons.add_circle_outline),
      onPressed: () async {
        await subscriptionProvider.handleUnsubscribe(
          context: context,
          digestId: digest.id,
        );
      },
    );
  }

  Widget _buildNotificationButton(Digest digest) {
    final subscriptionProvider =
        Provider.of<SubscriptionProvider>(context, listen: false);
    final digestProvider = Provider.of<DigestProvider>(context, listen: false);

    if (!digest.subscribeOptions.subscribed) return const SizedBox.shrink();

    final isEnabled = digest.subscribeOptions.mobileNotifications;

    return IconButton(
      icon: Icon(
        isEnabled ? Icons.notifications_active : Icons.notifications_none,
      ),
      onPressed: () async {
        digestProvider.updateDigest(
          digest.copyWith(
            subscribeOptions: digest.subscribeOptions.copyWith(
              mobileNotifications: !isEnabled,
            ),
          ),
        );

        try {
          final subscription =
              await subscriptionProvider.fetchSubscriptionByDigestId(digest.id);

          final updatedSubscription = subscription.copyWith(
            subscribeOptions: subscription.subscribeOptions.copyWith(
              mobileNotifications: !isEnabled,
            ),
          );
          await subscriptionProvider
              .updateSubscriptionSettings(updatedSubscription);
        } catch (e) {
          if (!mounted) return;

          digestProvider.updateDigest(digest);

          ScaffoldMessenger.of(context).showSnackBar(
            SnackBar(content: Text('Ошибка изменения уведомлений: $e')),
          );
        }
      },
    );
  }

  Widget _buildEditButton(Digest digest) {
    if (!digest.subscribeOptions.subscribed) return const SizedBox.shrink();
    if (!digest.isOwner) return const SizedBox.shrink();

    final isLoading = _loadingEditDigestId == digest.id;

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
                _loadingEditDigestId = digest.id;
              });

              try {
                final subscription = await subscriptionProvider
                    .fetchSubscriptionByDigestId(digest.id);
                if (!mounted) return;
                Navigator.push(
                  context,
                  MaterialPageRoute(
                    builder: (context) =>
                        DigestEditPage(subscription: subscription),
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
                    _loadingEditDigestId = null;
                  });
                }
              }
            },
    );
  }

  Widget _buildDigestTitle(Digest digest) {
    Icon icon = digest.public
        ? const Icon(Icons.public_rounded, size: 21)
        : const Icon(Icons.lock, size: 20);

    return Row(
      children: [
        icon,
        const SizedBox(width: 8),
        Expanded(
          child: digest.title.text.bold.xl3
              .color(Theme.of(context).textTheme.bodyLarge!.color!)
              .maxLines(1)
              .overflow(TextOverflow.ellipsis)
              .make(),
        ),
      ],
    );
  }

  Widget _buildDigestText(String text) {
    return Text(
      text,
      style: Theme.of(context).textTheme.bodyLarge,
      maxLines: 3,
      overflow: TextOverflow.ellipsis,
    );
  }

  Widget _buildFooter(Digest digest) {
    return Row(
      mainAxisAlignment: MainAxisAlignment.spaceBetween,
      children: [
        _buildDateText(digest.date),
        _buildReadMoreButton(digest),
      ],
    );
  }

  Widget _buildDateText(String date) {
    return date.text
        .size(14)
        .color(Theme.of(context).textTheme.bodyLarge?.color ?? Colors.black)
        .make();
  }

  Widget _buildReadMoreButton(Digest digest) {
    return LoadingButton(
      isLoading: _loadingDigestId == digest.id,
      onPressed: () async {
        setState(() {
          _loadingDigestId = digest.id;
        });

        final digestProvider =
            Provider.of<DigestProvider>(context, listen: false);

        try {
          final fullDigest = await digestProvider.loadDigestById(digest.id);
          if (!mounted) return;
          Navigator.push(
            context,
            MaterialPageRoute(
              builder: (context) => DigestDetailPage(digest: fullDigest),
            ),
          );
        } catch (e) {
          if (!mounted) return;
          ScaffoldMessenger.of(context).showSnackBar(
            SnackBar(content: Text('Ошибка загрузки дайджеста: $e')),
          );
        } finally {
          if (mounted) {
            setState(() {
              _loadingDigestId = null;
            });
          }
        }
      },
      text: 'Подробнее',
    );
  }

  Widget _buildDigestList(DigestProvider provider, List<Digest> digests) {
    if (_shouldShowLoading(provider, digests)) {
      return const Center(child: CircularProgressIndicator());
    }

    if (digests.isEmpty) {
      return const Center(child: Text("Нет дайджестов"));
    }

    return ListView.builder(
      controller: _scrollController,
      padding: const EdgeInsets.all(8),
      itemCount: digests.length + (provider.isLoadingMore ? 1 : 0),
      itemBuilder: (context, index) {
        if (index == digests.length) {
          return _buildLoadingIndicator();
        }
        return _buildDigestCard(digests[index]);
      },
    );
  }

  Subscription? _getSelectedSubscription() {
    final subscriptionProvider = Provider.of<SubscriptionProvider>(context);
    if (subscriptionProvider.selectedSubscriptionId == null) return null;

    return subscriptionProvider.filteredSubscriptions.firstWhere(
      (sub) => sub.id == subscriptionProvider.selectedSubscriptionId,
      orElse: () => Subscription(
        id: -1,
        title: 'Неизвестная подписка',
        description: '',
        sources: [],
        tags: [],
        subscribeOptions: SubscribeOptions(
          subscribed: false,
          sendToMail: false,
          mobileNotifications: false,
        ),
        creationDate: '',
        public: false,
        owner: '',
        isOwner: false,
        followers: [],
      ),
    );
  }

  AppBar _buildAppBar() {
    final subscriptionProvider = Provider.of<SubscriptionProvider>(context);
    final digestProvider = Provider.of<DigestProvider>(context);
    final isAuthenticated = Provider.of<AuthProvider>(context).isAuthenticated;
    final selectedSubscription = _getSelectedSubscription();

    return AppBar(
      title: AnimatedSwitcher(
        duration: const Duration(milliseconds: 300),
        child: Text(
          subscriptionProvider.selectedSubscriptionId == null
              ? 'Дайджесты'
              : selectedSubscription!.title,
          key: ValueKey(subscriptionProvider.selectedSubscriptionId),
          overflow: TextOverflow.ellipsis,
          style: const TextStyle(fontSize: 24, fontWeight: FontWeight.bold),
        ),
      ),
      leading: subscriptionProvider.selectedSubscriptionId != null
          ? IconButton(
              icon: const Icon(Icons.arrow_back),
              onPressed: () {
                setState(() {
                  subscriptionProvider.resetSelectedSubscription();
                });
              },
            )
          : null,
      actions: [
        if (isAuthenticated)
          IconButton(
            icon: const Icon(Icons.add),
            onPressed: () async {
              await Navigator.push(
                context,
                MaterialPageRoute(
                    builder: (context) => const DigestCreatePage()),
              );
            },
          ),
        IconButton(
          icon: const Icon(Icons.search),
          onPressed: () async {
            await Navigator.push(
              context,
              MaterialPageRoute(builder: (context) => const DigestSearchPage()),
            );
          },
        ),
        LoadingRefreshButton(
          onRefresh: () async {
            final subscriptionProvider =
                Provider.of<SubscriptionProvider>(context, listen: false);
            final digestProvider =
                Provider.of<DigestProvider>(context, listen: false);

            if (subscriptionProvider.selectedSubscriptionId != null) {
              await digestProvider.loadDigestsBySubscription(
                  subscriptionProvider.selectedSubscriptionId!);
            } else {
              await digestProvider.loadDigests();
            }

            await subscriptionProvider.loadSubscriptions();
          },
          isDisabled:
              digestProvider.isLoading || subscriptionProvider.isLoading,
        ),
      ],
      backgroundColor: Theme.of(context).scaffoldBackgroundColor,
      surfaceTintColor: Colors.transparent,
      elevation: 0,
      shadowColor: Colors.transparent,
    );
  }

  @override
  Widget build(BuildContext context) {
    final provider = Provider.of<DigestProvider>(context);
    final digests = provider.filteredDigests;

    return Scaffold(
      appBar: _buildAppBar(),
      body: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          _buildCategoryButtons(provider),
          _buildSubscriptionsRow(),
          _buildSubscriptionDescription(),
          Expanded(child: _buildDigestList(provider, digests)),
        ],
      ),
    );
  }
}
