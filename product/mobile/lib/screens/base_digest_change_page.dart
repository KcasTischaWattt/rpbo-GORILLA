import 'package:crosswords/providers/subscription_provider.dart';
import 'package:crosswords/screens/widgets/digest_components/digest_app_bar.dart';
import 'package:crosswords/screens/widgets/digest_components/digest_form.dart';
import 'package:crosswords/screens/widgets/digest_components/exit_confirmation_handler.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

abstract class BaseDigestPage<T extends StatefulWidget> extends State<T> {
  final TextEditingController _titleController = TextEditingController();
  final TextEditingController _descriptionController = TextEditingController();
  final TextEditingController _recipientController = TextEditingController();

  @override
  void initState() {
    super.initState();
    final provider = Provider.of<SubscriptionProvider>(context, listen: false);
    _titleController.text = provider.title;
    _descriptionController.text = provider.description;
    _recipientController.text = provider.currentFollowerInput;

    _titleController.addListener(() => provider.setTitle(_titleController.text));
    _descriptionController.addListener(() => provider.setDescription(_descriptionController.text));
    _recipientController.addListener(() => provider.setCurrentFollowerInput(_recipientController.text));

    onInit(provider);
  }

  @override
  void dispose() {
    _titleController.dispose();
    _descriptionController.dispose();
    _recipientController.dispose();
    super.dispose();
  }

  /// Метод для инициализации данных в `initState()`, чтобы можно было кастомизировать поведение в `DigestEditPage`
  void onInit(SubscriptionProvider provider);

  /// Метод для сохранения изменений (разный в `DigestEditPage` и `DigestCreatePage`)
  void onPrimaryPressed();

  /// Метод сброса данных (общий)
  void _resetFilters() {
    final provider = Provider.of<SubscriptionProvider>(context, listen: false);
    provider.resetAndAddDefault();
    _titleController.text = provider.title;
    _descriptionController.text = provider.description;
    _recipientController.text = provider.currentFollowerInput;
  }

  /// Подтверждение сброса данных
  void _confirmResetFilters() {
    final provider = Provider.of<SubscriptionProvider>(context, listen: false);
    if (provider.areFieldsEmpty()) {
      showDialog(
        context: context,
        builder: (context) => AlertDialog(
          title: const Text("Подтверждение сброса"),
          content: const Text("Вы уверены, что хотите сбросить все поля? Это действие нельзя отменить."),
          actions: [
            TextButton(onPressed: () => Navigator.pop(context), child: const Text("Отмена")),
            TextButton(
              onPressed: () {
                Navigator.pop(context);
                _resetFilters();
              },
              child: const Text("Сбросить", style: TextStyle(color: Colors.red)),
            ),
          ],
        ),
      );
    } else {
      _resetFilters();
    }
  }

  @override
  Widget build(BuildContext context) {
    return ExitConfirmationHandler(
      onExitRequested: (context) async => true,
      onExitConfirmed: () => Provider.of<SubscriptionProvider>(context, listen: false).reset(),
      child: Scaffold(
        appBar: DigestAppBar(title: getAppBarTitle()),
        body: Padding(
          padding: const EdgeInsets.all(0),
          child: DigestForm(
            titleController: _titleController,
            descriptionController: _descriptionController,
            recipientController: _recipientController,
            onPrimaryPressed: onPrimaryPressed,
            onSecondaryPressed: _confirmResetFilters,
            formTitle: getFormTitle(),
            primaryButtonText: getPrimaryButtonText(),
            primaryButtonIcon: getPrimaryButtonIcon(),
          ),
        ),
      ),
    );
  }

  /// Заголовки и кнопки, чтобы страницы можно было кастомизировать
  String getAppBarTitle();
  String getFormTitle();
  String getPrimaryButtonText();
  IconData getPrimaryButtonIcon();
}
