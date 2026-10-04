import 'package:crosswords/providers/auth_provider.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:intl/intl.dart';
import 'dart:ui' as ui;
import 'package:velocity_x/velocity_x.dart';
import '../providers/article_provider.dart';
import '../data/models/note.dart';
import 'package:flutter/services.dart';
import 'widgets/fade_background.dart';
import 'widgets/expanding_text_field.dart';
import 'package:url_launcher/url_launcher.dart';
import 'widgets/item_chips_list_widget.dart';
import 'widgets/custom_expansion_tile_widget.dart';

class ArticleDetailPage extends StatefulWidget {
  final int articleId;

  const ArticleDetailPage({super.key, required this.articleId});

  @override
  _ArticleDetailPageState createState() => _ArticleDetailPageState();
}

class _ArticleDetailPageState extends State<ArticleDetailPage> {
  final TextEditingController _commentController = TextEditingController();
  Note? _editingNote;
  bool _isNotesExpanded = true;
  bool _hasTextChanged = false;

  @override
  void initState() {
    super.initState();
    Future.microtask(() {
      final provider = Provider.of<ArticleProvider>(context, listen: false);
      provider.loadArticleById(widget.articleId);
      provider.loadNotes(widget.articleId);
    });

    _commentController.addListener(_onCommentChanged);
  }

  @override
  void dispose() {
    _commentController.removeListener(_onCommentChanged);
    _commentController.dispose();
    super.dispose();
  }

  /// Обработка изменения текста в поле ввода комментария
  void _onCommentChanged() {
    if (_editingNote == null) return;
    setState(() {
      _hasTextChanged =
          _commentController.text.trim() != _editingNote!.text.trim();
    });
  }

  /// Форматирование даты и времени
  String _formatDateTime(DateTime dateTime) {
    return DateFormat('dd/MM/yyyy HH:mm').format(dateTime);
  }

  /// Виджет пунктов меню
  Widget _buildMenuItem({
    required IconData icon,
    required String text,
    required VoidCallback onTap,
  }) {
    return ListTile(
      leading: Icon(icon, size: 28),
      title: Text(text, style: TextStyle(fontSize: 20)),
      onTap: onTap,
    );
  }

  /// Виджет меню
  Widget _buildMenu(
      BuildContext context, BuildContext bottomSheetContext, Note note) {
    final menuItems = [
      {
        'icons': Icons.content_copy,
        'text': "Копировать",
        'action': () => _copyNoteText(context, note)
      },
      {
        'icons': Icons.edit,
        'text': "Редактировать",
        'action': () => _editNote(context, note)
      },
      {
        'icons': Icons.delete,
        'text': "Удалить",
        'action': () => _confirmDeleteNote(context, bottomSheetContext, note)
      },
    ];

    return Container(
      width: double.infinity,
      margin: const EdgeInsets.symmetric(horizontal: 8, vertical: 8),
      padding: const EdgeInsets.symmetric(vertical: 8),
      decoration: BoxDecoration(
        color: Theme.of(context).bottomNavigationBarTheme.backgroundColor,
        borderRadius: BorderRadius.circular(12),
      ),
      child: Column(
        mainAxisSize: MainAxisSize.min,
        children: menuItems.map((item) {
          return _buildMenuItem(
            icon: item['icons'] as IconData,
            text: item['text'] as String,
            onTap: item['action'] as VoidCallback,
          );
        }).toList(),
      ),
    );
  }

  /// Копирование текста заметки
  void _copyNoteText(BuildContext context, Note note) {
    Clipboard.setData(ClipboardData(text: note.text));
    Navigator.pop(context);
    ScaffoldMessenger.of(context).showSnackBar(
      const SnackBar(content: Text("Заметка скопирована")),
    );
  }

  /// Подтверждение входа в режим редактирования
  void _confirmEnterEditMode(BuildContext context, Note note) {
    Navigator.pop(context);
    _showConfirmationDialog(
      context: context,
      title: "Начать редактирование?",
      content: "Введенный текст будет утерян.",
      cancelText: "Отмена",
      confirmText: "Редактировать",
      onConfirm: () {
        setState(() {
          _editingNote = note;
          _commentController.text = note.text;
        });
      },
    );
  }

  /// Редактирование заметки
  void _editNote(BuildContext context, Note note) {
    if (_commentController.text.trim().isNotEmpty) {
      _confirmEnterEditMode(context, note);
      return;
    }
    setState(() {
      _editingNote = note;
      _commentController.text = note.text;
    });
    Navigator.pop(context);
  }

  /// Подтверждение удаления заметки
  void _confirmDeleteNote(
      BuildContext context, BuildContext bottomSheetContext, Note note) {
    Navigator.pop(bottomSheetContext);
    WidgetsBinding.instance.addPostFrameCallback((_) {
      if (!mounted) return;
      _showConfirmationDialog(
        context: context,
        title: "Удалить заметку?",
        content: "Вы уверены, что хотите удалить эту заметку?",
        cancelText: "Отмена",
        confirmText: "Удалить",
        onConfirm: () {
          if (_editingNote != null && _editingNote!.id == note.id) {
            _resetEditing();
          }

          Provider.of<ArticleProvider>(context, listen: false)
              .deleteNote(note.id);
        },
      );
    });
  }

  /// Отображение опций заметки
  void _showNoteOptions(BuildContext context, Note note) {
    showModalBottomSheet(
      context: context,
      backgroundColor: Colors.transparent,
      isScrollControlled: true,
      builder: (BuildContext bottomSheetContext) {
        return FadeBackground(
          child: Column(
            mainAxisAlignment: MainAxisAlignment.end,
            children: [
              _buildMenu(context, bottomSheetContext, note),
              const SizedBox(height: 10),
            ],
          ),
        );
      },
    );
  }

  /// Показ диалога подтверждения действия
  void _showConfirmationDialog({
    required BuildContext context,
    required String title,
    required String content,
    required String cancelText,
    required String confirmText,
    required VoidCallback onConfirm,
  }) {
    showDialog(
      context: context,
      builder: (context) {
        return AlertDialog(
          title: Text(title),
          content: Text(content),
          actions: [
            TextButton(
              onPressed: () => Navigator.pop(context),
              child: Text(cancelText, style: const TextStyle(fontSize: 18)),
            ),
            TextButton(
              onPressed: () {
                onConfirm();
                Navigator.pop(context);
              },
              child: Text(confirmText,
                  style: const TextStyle(fontSize: 18, color: Colors.red)),
            ),
          ],
        );
      },
    );
  }

  /// Создает `TextPainter` для вычисления размеров текста
  TextPainter _createTextPainter({
    required String text,
    required TextStyle style,
    double? maxWidth,
  }) {
    return TextPainter(
      text: TextSpan(text: text, style: style),
      textDirection: ui.TextDirection.ltr,
      maxLines: null,
    )..layout(maxWidth: maxWidth ?? double.infinity);
  }

  /// Возвращает форматированную строку времени
  String _getTimeText(Note note) {
    final createdAt = DateTime.parse(note.createdAt);
    final updatedAt = DateTime.parse(note.updatedAt);
    return createdAt == updatedAt
        ? _formatDateTime(createdAt)
        : "изм. ${_formatDateTime(updatedAt)}";
  }

  /// Построение однострочного макета, если время и текст помещаются в строку
  Widget _buildSingleLineLayout(
      BuildContext context, String text, String time) {
    return Row(
      children: [
        Expanded(
          child: Text(
            text,
            style: TextStyle(
              fontSize: 18,
              color: Theme.of(context).textTheme.bodyLarge!.color,
            ),
          ),
        ),
        Text(
          time,
          style: TextStyle(fontSize: 14, color: Colors.grey[600]),
        ),
      ],
    );
  }

  /// Построение многострочного макета
  Widget _buildMultiLineLayout(BuildContext context, String text, String time) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Text(
          text,
          style: TextStyle(
            fontSize: 18,
            color: Theme.of(context).textTheme.bodyLarge!.color,
          ),
        ),
        const SizedBox(height: 4),
        Align(
          alignment: Alignment.bottomRight,
          child: Text(
            time,
            style: TextStyle(fontSize: 14, color: Colors.grey[600]),
          ),
        ),
      ],
    );
  }

  /// Построение содержимого заметки
  Widget buildNoteContent(Note note) {
    return LayoutBuilder(
      builder: (context, constraints) {
        final textPainter = _createTextPainter(
          text: note.text,
          style: TextStyle(
            fontSize: 18,
            color: Theme.of(context).textTheme.bodyLarge!.color,
          ),
          maxWidth: constraints.maxWidth,
        );

        final timeText = _getTimeText(note);
        final timePainter = _createTextPainter(
          text: timeText,
          style: TextStyle(fontSize: 14, color: Colors.grey[600]),
        );

        final lastLineWidth = textPainter.computeLineMetrics().isNotEmpty
            ? textPainter.computeLineMetrics().last.width
            : 0.0;

        if (textPainter.computeLineMetrics().length == 1 &&
            lastLineWidth + timePainter.width + 8 < constraints.maxWidth) {
          return _buildSingleLineLayout(
            context,
            note.text,
            timeText,
          );
        } else {
          return _buildMultiLineLayout(
            context,
            note.text,
            timeText,
          );
        }
      },
    );
  }

  /// Показ диалога подтверждения выхода
  void _showExitConfirmationDialog(BuildContext context) {
    _showConfirmationDialog(
      context: context,
      title: _editingNote != null
          ? "Отменить редактирование?"
          : "Отменить создание заметки?",
      content: "Изменения не будут сохранены.",
      cancelText: "Остаться",
      confirmText: "Выйти",
      onConfirm: () {
        _resetEditing();
        Navigator.pop(context);
      },
    );
  }

  /// Обработка нажатия кнопки "Назад"
  void _handlePop(BuildContext context, bool didPop, dynamic result) {
    if (didPop) return;

    if (_editingNote != null || _commentController.text.isNotEmpty) {
      _showExitConfirmationDialog(context);
    } else {
      Navigator.pop(context, result);
    }
  }

  /// Построение AppBar
  AppBar _buildAppBar(
      BuildContext context, bool isFavorite, ArticleProvider provider) {
    return AppBar(
      toolbarHeight: 60,
      centerTitle: true,
      title: Text(
        'Детали статьи',
        style: TextStyle(fontSize: 24, fontWeight: FontWeight.bold),
      ),
      actions: [
        if (Provider.of<AuthProvider>(context, listen: false).isAuthenticated)
          IconButton(
            icon: provider.isLoading
                ? const SizedBox(
                    width: 24,
                    height: 24,
                    child: CircularProgressIndicator(strokeWidth: 2))
                : Icon(
                    isFavorite ? Icons.favorite : Icons.favorite_border,
                    color: isFavorite ? Colors.red : Colors.grey,
                    size: 24,
                  ),
            onPressed: provider.isLoading
                ? null
                : () async {
                    await provider.toggleCurrentArticleFavorite();
                  },
          ),
      ],
      backgroundColor: Theme.of(context).scaffoldBackgroundColor,
      surfaceTintColor: Colors.transparent,
      elevation: 0,
      shadowColor: Colors.transparent,
    );
  }

  /// Построение информации о статье
  Widget _buildArticleContent(BuildContext context) {
    final provider = Provider.of<ArticleProvider>(context);
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        '${provider.currentArticle!.date} | ${provider.currentArticle!.source}'
            .text
            .xl
            .color(Theme.of(context).textTheme.bodySmall?.color ?? Colors.grey)
            .make(),
        const SizedBox(height: 8),
        provider.currentArticle!.title.text.bold.xl3.make(),
        const SizedBox(height: 16),
        ItemListWidget(
          items: provider.currentArticle!.tags,
          dialogTitle: "Все теги",
          chipColor: Theme.of(context).secondaryHeaderColor,
          textColor: Colors.white,
          fontWeight: FontWeight.normal,
        ),
        const SizedBox(height: 16),
        _buildSummaryExpansionTile(context),
        const SizedBox(height: 16),
        _buildArticleText(context),
        const SizedBox(height: 20),
        _buildReadOriginalButton(context, provider.currentArticle!.url),
        const SizedBox(height: 20),
      ],
    );
  }

  /// Построение краткого содержания статьи
  Widget _buildSummaryExpansionTile(BuildContext context) {
    return CustomExpansionTile(
      title: "Краткое содержание",
      icon: Icons.book,
      customContent: Text(
        Provider.of<ArticleProvider>(context).currentArticle!.summary,
        style: TextStyle(
          fontSize: 18,
          color: Theme.of(context).textTheme.bodyLarge!.color,
        ),
      ),
      children: [],
    );
  }

  /// Построение текста статьи
  Widget _buildArticleText(BuildContext context) {
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 12),
      decoration: BoxDecoration(
        color: Theme.of(context).scaffoldBackgroundColor,
        borderRadius: BorderRadius.circular(12),
      ),
      child: Text(
        Provider.of<ArticleProvider>(context).currentArticle!.text,
        style: TextStyle(
          fontSize: 18,
          color: Theme.of(context).textTheme.bodyLarge!.color,
        ),
      ),
    );
  }

  /// Открытие ссылки во внешнем браузере
  Future<void> _launchURL(String url) async {
    final Uri uri = Uri.parse(url);
    if (await canLaunchUrl(uri)) {
      await launchUrl(uri, mode: LaunchMode.externalApplication);
    } else {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text("Не удалось открыть ссылку")),
      );
    }
  }

  /// Построение кнопки "Читать оригинал"
  Widget _buildReadOriginalButton(BuildContext context, String url) {
    return ElevatedButton(
      onPressed: () => _launchURL(url),
      style: ElevatedButton.styleFrom(
        backgroundColor: Theme.of(context).primaryColor,
        padding: const EdgeInsets.symmetric(vertical: 12, horizontal: 12),
        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(8)),
      ),
      child: const Text('Читать оригинал',
          style: TextStyle(color: Colors.black, fontSize: 18)),
    );
  }

  /// Построение списка заметок
  Widget _buildNotesSection(BuildContext context, ArticleProvider provider) {
    if (!Provider.of<AuthProvider>(context, listen: false).isAuthenticated) {
      return const SizedBox.shrink();
    }

    if (provider.isLoadingNotes) {
      return const Padding(
        padding: EdgeInsets.symmetric(vertical: 20),
        child: Center(child: CircularProgressIndicator()),
      );
    }

    final notes = provider.getNotesForArticle(provider.currentArticle!.id);
    if (notes.isEmpty) return const Text("Заметок пока нет.");

    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        _buildNotesHeader(context, notes.length),
        ClipRect(
          child: AnimatedSize(
            duration: const Duration(milliseconds: 400),
            curve: Curves.easeInOut,
            child: _isNotesExpanded
                ? _buildNotesList(context, notes)
                : _buildCollapsedNotes(context, notes.length),
          ),
        ),
      ],
    );
  }

  /// Построение заголовка заметок
  Widget _buildNotesHeader(BuildContext context, int notesCount) {
    return Row(
      mainAxisAlignment: MainAxisAlignment.spaceBetween,
      children: [
        const Text('Заметки',
            style: TextStyle(fontSize: 22, fontWeight: FontWeight.bold)),
        IconButton(
          icon: Icon(_isNotesExpanded ? Icons.expand_less : Icons.expand_more),
          onPressed: () => setState(() => _isNotesExpanded = !_isNotesExpanded),
        ),
      ],
    );
  }

  /// Построение развёрнутого списка заметок
  Widget _buildNotesList(BuildContext context, List<Note> notes) {
    return AnimatedSize(
      duration: const Duration(milliseconds: 500),
      curve: Curves.easeInOut,
      child: ListView.builder(
        shrinkWrap: true,
        physics: const NeverScrollableScrollPhysics(),
        itemCount: notes.length,
        itemBuilder: (context, index) {
          final note = notes[index];
          return GestureDetector(
            onLongPress: () {
              _showNoteOptions(context, note);
            },
            child: Container(
              margin: const EdgeInsets.symmetric(vertical: 6),
              padding: const EdgeInsets.all(12),
              decoration: BoxDecoration(
                color:
                    Theme.of(context).bottomNavigationBarTheme.backgroundColor,
                borderRadius: BorderRadius.circular(12),
              ),
              child: buildNoteContent(note),
            ),
          );
        },
      ),
    );
  }

  /// Построение свёрнутого списка заметок
  Widget _buildCollapsedNotes(BuildContext context, int notesCount) {
    return GestureDetector(
      onTap: () => setState(() => _isNotesExpanded = true),
      child: Container(
        width: double.infinity,
        margin: const EdgeInsets.symmetric(vertical: 6),
        padding: const EdgeInsets.all(12),
        decoration: BoxDecoration(
          color: Theme.of(context).bottomNavigationBarTheme.backgroundColor,
          borderRadius: BorderRadius.circular(12),
        ),
        child: Text(
          'Всего заметок: $notesCount',
          style: const TextStyle(fontSize: 18, fontWeight: FontWeight.bold),
          textAlign: TextAlign.center,
        ),
      ),
    );
  }

  /// Построение поля ввода комментария
  Widget _buildCommentInput(BuildContext context, ArticleProvider provider) {
    if (!Provider.of<AuthProvider>(context, listen: false).isAuthenticated) {
      return const SizedBox.shrink();
    }

    return Row(
      children: [
        if (_editingNote != null)
          IconButton(
              icon: const Icon(Icons.close, size: 30),
              onPressed: _cancelEditing),
        Expanded(
            child: ExpandingTextField(
          controller: _commentController,
          hintText: "Оставить заметку...",
          maxLinesBeforeScroll: 5,
        )),
        const SizedBox(width: 10),
        IconButton(
            icon:
                Icon(_editingNote == null ? Icons.send : Icons.check, size: 30),
            onPressed: () => _saveOrUpdateNote(provider)),
      ],
    );
  }

  /// Отмена редактирования заметки
  void _cancelEditing() {
    if (_editingNote == null) return;

    if (!_hasTextChanged) {
      setState(() {
        _editingNote = null;
        _commentController.clear();
      });
      return;
    }

    _showConfirmationDialog(
      context: context,
      title: "Отменить редактирование?",
      content: "Изменения не будут сохранены.",
      cancelText: "Нет",
      confirmText: "Да",
      onConfirm: () {
        setState(() {
          _editingNote = null;
          _commentController.clear();
        });
      },
    );
  }

  /// Сброс режима редактирования
  void _resetEditing() {
    setState(() {
      _editingNote = null;
      _commentController.clear();
    });
  }

  /// Сохранение или обновление заметки
  void _saveOrUpdateNote(ArticleProvider provider) {
    final text = _commentController.text.trim();
    if (text.isEmpty) return;

    if (_editingNote != null) {
      // Обновление существующей заметки
      if (!_hasTextChanged) {
        _resetEditing();
        return;
      }

      provider.updateNote(_editingNote!.id, text);

      WidgetsBinding.instance.addPostFrameCallback((_) {
        if (!mounted) return;
        setState(() {
          _editingNote = null;
          _commentController.clear();
        });
      });
    } else {
      // Добавление новой заметки
      provider.addNote(provider.currentArticle!.id, text);
      _commentController.clear();
    }
  }

  /// Построение виджета
  @override
  Widget build(BuildContext context) {
    final provider = Provider.of<ArticleProvider>(context);
    final article = provider.currentArticle;

    if (provider.isLoading) {
      return const Scaffold(
        body: Center(child: CircularProgressIndicator()),
      );
    }

    if (article == null) {
      return const Scaffold(
        body: Center(child: Text("Ошибка загрузки статьи")),
      );
    }

    return PopScope(
      canPop: _editingNote == null && _commentController.text.isEmpty,
      onPopInvokedWithResult: (didPop, result) =>
          _handlePop(context, didPop, result),
      child: Scaffold(
        appBar: _buildAppBar(context, article.favorite, provider),
        body: SingleChildScrollView(
          padding: const EdgeInsets.all(16.0),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              _buildArticleContent(context),
              _buildNotesSection(context, provider),
              const SizedBox(height: 10),
              _buildCommentInput(context, provider),
            ],
          ),
        ),
      ),
    );
  }
}
