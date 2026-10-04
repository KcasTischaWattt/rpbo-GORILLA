import 'package:crosswords/screens/widgets/loading_button.dart';
import 'package:crosswords/screens/widgets/loading_refresh_button.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../providers/digest_provider.dart';
import '../providers/auth_provider.dart';
import '../data/models/digest.dart';
import '../screens/digest_detail_page.dart';
import 'package:velocity_x/velocity_x.dart';

class DigestSearchResultsPage extends StatefulWidget {
  const DigestSearchResultsPage({super.key});

  @override
  _DigestSearchResultsPageState createState() =>
      _DigestSearchResultsPageState();
}

class _DigestSearchResultsPageState extends State<DigestSearchResultsPage> {
  late ScrollController _scrollController;
  String? _loadingDigestId;

  @override
  void initState() {
    super.initState();
    _scrollController = ScrollController();
    _scrollController.addListener(_onScroll);
  }

  @override
  void dispose() {
    _scrollController.removeListener(_onScroll);
    _scrollController.dispose();
    super.dispose();
  }

  void _onScroll() {
    final provider = Provider.of<DigestProvider>(context, listen: false);
    if (_shouldLoadMore(provider)) {
      provider.loadMoreDigests();
    }
  }

  bool _shouldLoadMore(DigestProvider provider) {
    if (!_scrollController.hasClients) return false;
    return _scrollController.position.pixels >=
            _scrollController.position.maxScrollExtent - 200 &&
        !provider.isLoadingMore &&
        !provider.isLoading;
  }

  AppBar _buildAppBar() {
    final provider = Provider.of<DigestProvider>(context);

    return AppBar(
      title: const Text(
        'Результаты поиска',
        style: TextStyle(fontSize: 24, fontWeight: FontWeight.bold),
      ),
      actions: [
        LoadingRefreshButton(
          onRefresh: () async {
            await provider.loadSearchedDigests();
          },
          isDisabled: provider.isLoading,
        ),
      ],
      backgroundColor: Theme.of(context).scaffoldBackgroundColor,
      surfaceTintColor: Colors.transparent,
      elevation: 0,
      shadowColor: Colors.transparent,
    );
  }

  Widget _buildLoadingIndicator() {
    return const Padding(
      padding: EdgeInsets.all(12),
      child: Center(child: CircularProgressIndicator()),
    );
  }

  Widget _buildDigestList(DigestProvider provider) {
    final digests = provider.filteredDigests;

    if (provider.isLoading && digests.isEmpty) {
      return const Center(child: CircularProgressIndicator());
    }

    if (digests.isEmpty) {
      return const Center(child: Text("Ничего не найдено"));
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

  Widget _buildDigestCard(Digest digest) {
    return Card(
      color: Theme.of(context).bottomNavigationBarTheme.backgroundColor,
      margin: const EdgeInsets.symmetric(vertical: 6),
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(8)),
      child: Padding(
        padding: const EdgeInsets.all(8),
        child: LayoutBuilder(
          builder: (context, constraints) {
            bool isNarrowScreen = constraints.maxWidth <= 300;
            return _buildDigestContent(digest, isNarrowScreen);
          },
        ),
      ),
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

  Widget _buildDigestActions(Digest digest) {
    final isAuthenticated = Provider.of<AuthProvider>(context).isAuthenticated;
    if (!isAuthenticated) return const SizedBox.shrink();

    return Row(
      mainAxisSize: MainAxisSize.min,
      children: [
        IconButton(
          icon: Icon(digest.subscribeOptions.subscribed
              ? Icons.check_circle
              : Icons.add_circle_outline),
          onPressed: () {},
        ),
      ],
    );
  }

  Widget _buildSourcesText(List<String> sources) {
    if (sources.isEmpty) return const SizedBox.shrink();

    return Text(
      "Источники: ${sources.join(', ')}",
      style: const TextStyle(fontSize: 14, fontWeight: FontWeight.normal),
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

  Widget _buildTags(List<String> tags) {
    if (tags.isEmpty) return const SizedBox.shrink();

    return Wrap(
      spacing: 8,
      runSpacing: 4,
      children: tags
          .map((tag) => Chip(
                label: Text(
                  tag,
                  style: const TextStyle(
                      fontSize: 12,
                      fontWeight: FontWeight.bold,
                      color: Colors.black),
                ),
                backgroundColor: Theme.of(context).primaryColor,
              ))
          .toList(),
    );
  }

  Widget _buildFooter(Digest digest) {
    return Row(
      mainAxisAlignment: MainAxisAlignment.spaceBetween,
      children: [
        Text(
          digest.date,
          style: const TextStyle(fontSize: 14),
        ),
        LoadingButton(
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
        ),
      ],
    );
  }

  Widget _buildLoadingOverlay() {
    return Container(
      color: Colors.black.withValues(alpha: 0.3),
      child: const Center(
        child: CircularProgressIndicator(),
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    final provider = Provider.of<DigestProvider>(context);

    return Scaffold(
      appBar: _buildAppBar(),
      body: Stack(
        children: [
          _buildDigestList(provider),
          if (provider.isLoading && provider.filteredDigests.isNotEmpty)
            _buildLoadingOverlay(),
        ],
      ),
    );
  }
}
