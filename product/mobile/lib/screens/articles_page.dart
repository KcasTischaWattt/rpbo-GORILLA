import 'package:crosswords/screens/widgets/loading_refresh_button.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../providers/article_provider.dart';
import '../data/models/article.dart';
import 'article_detail_page.dart';
import 'article_search_page.dart';
import 'package:crosswords/providers/auth_provider.dart';

class ArticlesPage extends StatefulWidget {
  const ArticlesPage({super.key});

  @override
  _ArticlesPageState createState() => _ArticlesPageState();
}

class _ArticlesPageState extends State<ArticlesPage>
    with SingleTickerProviderStateMixin {
  late ScrollController _scrollController;

  @override
  void initState() {
    super.initState();
    _scrollController = ScrollController();
    _scrollController.addListener(_onScroll);

    Future.microtask(() {
      if (!mounted) return;
      final provider = Provider.of<ArticleProvider>(context, listen: false);
      if (provider.articles.isEmpty) {
        provider.prepareSearchForAllDocuments();
        provider.loadArticles();
      }
    });
  }

  @override
  void dispose() {
    _scrollController.dispose();
    super.dispose();
  }

  bool _shouldLoadMore(ArticleProvider provider) {
    return _scrollController.position.pixels >=
            _scrollController.position.maxScrollExtent - 200 &&
        !provider.isLoadingMore &&
        !provider.isLoading;
  }

  void _onScroll() {
    final provider = Provider.of<ArticleProvider>(context, listen: false);
    if (_shouldLoadMore(provider)) {
      provider.loadMoreArticles();
    }
  }

  Future<void> _toggleFavorite(int articleId) async {
    await Provider.of<ArticleProvider>(context, listen: false)
        .toggleFavorite(articleId);
  }

  Widget _buildTitle(ArticleProvider provider) {
    return Row(
      children: [
        const Text(
          'Статьи',
          style: TextStyle(fontSize: 24, fontWeight: FontWeight.bold),
        ),
        const SizedBox(width: 10),
        if (Provider.of<AuthProvider>(context, listen: false).isAuthenticated)
          IconButton(
            icon: provider.isLoading
                ? const SizedBox(
                    width: 24,
                    height: 24,
                    child: CircularProgressIndicator(strokeWidth: 2),
                  )
                : Icon(
                    provider.showOnlyFavorites
                        ? Icons.favorite
                        : Icons.favorite_border,
                    color:
                        provider.showOnlyFavorites ? Colors.red : Colors.grey,
                    size: 24,
                  ),
            onPressed: provider.isLoading ? null : provider.toggleShowFavorites,
          ),
      ],
    );
  }

  Widget _buildSearchButton() {
    return IconButton(
      icon: const Icon(Icons.search, size: 24),
      onPressed: () async {
        await Navigator.push(
          context,
          MaterialPageRoute(builder: (context) => const ArticleSearchPage()),
        );
      },
    );
  }

  AppBar _buildAppBar(ArticleProvider provider) {
    return AppBar(
      toolbarHeight: 60,
      title: _buildTitle(provider),
        actions: [
          _buildSearchButton(),
          LoadingRefreshButton(
            onRefresh: () async {
              provider.prepareSearchForAllDocuments();
              await provider.loadArticles();
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

  Widget _buildReadMoreButton(Article article) {
    return ElevatedButton(
      onPressed: () {
        Navigator.push(
          context,
          MaterialPageRoute(
              builder: (context) => ArticleDetailPage(articleId: article.id)),
        );
      },
      style: ElevatedButton.styleFrom(
        backgroundColor: Theme.of(context).primaryColor,
        padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 8),
        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(8)),
      ),
      child: const Text('Подробнее',
          style: TextStyle(color: Colors.black, fontSize: 18)),
    );
  }

  Widget _buildArticleMetadata(Article article) {
    return Row(
      children: [
        Expanded(
          child: Text(
            'Источник: ${article.source}',
            style:
                TextStyle(color: Theme.of(context).textTheme.bodySmall!.color),
          ),
        ),
        const SizedBox(width: 15),
        Text(
          article.date,
          style: TextStyle(color: Theme.of(context).textTheme.bodySmall!.color),
        ),
      ],
    );
  }

  Widget _buildArticleContent(Article article) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Text(
          article.title,
          style: TextStyle(
            fontSize: 20,
            fontWeight: FontWeight.bold,
            color: Theme.of(context).textTheme.bodyLarge!.color,
          ),
        ),
        const SizedBox(height: 8),
        _buildArticleMetadata(article),
        const SizedBox(height: 8),
        Text(
          article.summary,
          maxLines: 2,
          overflow: TextOverflow.ellipsis,
          style: Theme.of(context).textTheme.bodyLarge,
        ),
        const SizedBox(height: 16),
        _buildReadMoreButton(article),
      ],
    );
  }

  Widget _buildFavoriteButton(Article article, bool isFavorite) {
    return IconButton(
      icon: Icon(isFavorite ? Icons.favorite : Icons.favorite_border,
          color: isFavorite ? Colors.red : Colors.grey),
      onPressed: () async => await _toggleFavorite(article.id),
    );
  }

  Widget _buildArticleItem(Article article, ArticleProvider provider) {
    final isFavorite = article.favorite;
    return Card(
      color: Theme.of(context).bottomNavigationBarTheme.backgroundColor,
      margin: const EdgeInsets.symmetric(vertical: 6, horizontal: 1),
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(8)),
      child: Padding(
        padding: const EdgeInsets.all(8),
        child: Row(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Expanded(child: _buildArticleContent(article)),
            if (Provider.of<AuthProvider>(context, listen: false)
                .isAuthenticated)
              _buildFavoriteButton(article, isFavorite),
          ],
        ),
      ),
    );
  }

  Widget _buildBody(ArticleProvider provider) {
    if (provider.isLoading) {
      return const Center(child: CircularProgressIndicator());
    }

    final List<Article> displayedArticles = provider.articles;

    if (displayedArticles.isEmpty) {
      return const Center(child: Text("Документы не найдены"));
    }

    return ListView.builder(
      controller: _scrollController,
      itemCount: displayedArticles.length + (provider.isLoadingMore ? 1 : 0),
      itemBuilder: (context, index) {
        if (index == displayedArticles.length) {
          return _buildLoadingIndicator();
        }
        return _buildArticleItem(displayedArticles[index], provider);
      },
    );
  }

  @override
  Widget build(BuildContext context) {
    // сюда
    return Consumer<ArticleProvider>(
      builder: (context, provider, child) {
        return Scaffold(
          appBar: _buildAppBar(provider),
          body: _buildBody(provider),
        );
      },
    );
  }
}
