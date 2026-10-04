import 'package:crosswords/screens/widgets/digest_components/digest_app_bar.dart';
import 'package:crosswords/screens/widgets/digest_components/digest_form.dart';
import 'package:crosswords/screens/widgets/digest_components/exit_confirmation_handler.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../providers/subscription_provider.dart';
import '../data/models/subscription.dart';

class DigestEditPage extends StatefulWidget {
  final Subscription subscription;

  const DigestEditPage({super.key, required this.subscription});

  @override
  _DigestEditPageState createState() => _DigestEditPageState();
}

class _DigestEditPageState extends State<DigestEditPage> {
  final TextEditingController _titleController = TextEditingController();
  final TextEditingController _descriptionController = TextEditingController();
  final TextEditingController _recipientController = TextEditingController();

  @override
  void initState() {
    super.initState();

    WidgetsBinding.instance.addPostFrameCallback((_) {
      final provider = Provider.of<SubscriptionProvider>(context, listen: false);

      provider.setTitle(widget.subscription.title);
      provider.setDescription(widget.subscription.description);
      provider.setFollowers(widget.subscription.followers);
      provider.setSendToMail(widget.subscription.subscribeOptions.sendToMail);
      provider.setSources(widget.subscription.sources);
      provider.setTags(widget.subscription.tags);
      provider.setMobileNotifications(
          widget.subscription.subscribeOptions.mobileNotifications);
      provider.setIsPublic(widget.subscription.public);
    });

    _titleController.text = widget.subscription.title;
    _descriptionController.text = widget.subscription.description;

    _titleController.addListener(_onTitleChanged);
    _descriptionController.addListener(_onDescriptionChanged);
    _recipientController.addListener(_onRecipientChanged);
  }

  @override
  void dispose() {
    _titleController.removeListener(_onTitleChanged);
    _descriptionController.removeListener(_onDescriptionChanged);
    _recipientController.removeListener(_onRecipientChanged);

    _recipientController.dispose();
    _titleController.dispose();
    _descriptionController.dispose();

    super.dispose();
  }

  void _onTitleChanged() {
    Provider.of<SubscriptionProvider>(context, listen: false)
        .setTitle(_titleController.text);
  }

  void _onDescriptionChanged() {
    Provider.of<SubscriptionProvider>(context, listen: false)
        .setDescription(_descriptionController.text);
  }

  void _onRecipientChanged() {
    Provider.of<SubscriptionProvider>(context, listen: false)
        .setCurrentFollowerInput(_recipientController.text);
  }

  void _saveChanges() async {
    final provider = Provider.of<SubscriptionProvider>(context, listen: false);

    try {
      showDialog(
        context: context,
        barrierDismissible: false,
        builder: (_) => const Center(child: CircularProgressIndicator()),
      );

      await provider.saveEditedSubscription(widget.subscription);

      if (!mounted) return;
      Navigator.pop(context);
      Navigator.pop(context);
    } catch (e) {
      if (!mounted) return;
      Navigator.pop(context);
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text("Не удалось сохранить изменения: $e")),
      );
    }
  }


  void _resetFilters() async {
    final provider = Provider.of<SubscriptionProvider>(context, listen: false);

    try {
      showDialog(
        context: context,
        barrierDismissible: false,
        builder: (_) => const Center(child: CircularProgressIndicator()),
      );

      final fresh = await provider.resetToOriginalState(widget.subscription.id);

      _titleController.text = fresh.title;
      _descriptionController.text = fresh.description;
      _recipientController.text = "";

      if (!mounted) return;
      Navigator.pop(context);
    } catch (e) {
      if (!mounted) return;
      Navigator.pop(context);
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text("Не удалось сбросить изменения: $e")),
      );
    }
  }

  void _confirmResetFilters() {
    if (Provider.of<SubscriptionProvider>(context, listen: false)
        .areFieldsEmpty()) {
      showDialog(
        context: context,
        builder: (context) {
          return AlertDialog(
            title: const Text("Подтверждение сброса"),
            content: const Text(
                "Вы уверены, что хотите сбросить все поля? Это действие нельзя отменить."),
            actions: [
              TextButton(
                onPressed: () => Navigator.pop(context),
                child: const Text("Отмена"),
              ),
              TextButton(
                onPressed: () {
                  Navigator.pop(context);
                  _resetFilters();
                },
                child:
                const Text("Сбросить", style: TextStyle(color: Colors.red)),
              ),
            ],
          );
        },
      );
    } else {
      _resetFilters();
    }
  }

  @override
  Widget build(BuildContext context) {
    return ExitConfirmationHandler(
      onExitRequested: (context) async => true,
      onExitConfirmed: () {
        Provider.of<SubscriptionProvider>(context, listen: false).reset();
      },
      child: Scaffold(
        appBar: const DigestAppBar(title: 'Редактирование дайджеста'),
        body: Padding(
          padding: const EdgeInsets.all(12.0),
          child: DigestForm(
            titleController: _titleController,
            descriptionController: _descriptionController,
            recipientController: _recipientController,
            onPrimaryPressed: _saveChanges,
            onSecondaryPressed: _confirmResetFilters,
            formTitle: 'Редактирование дайджеста',
            primaryButtonText: 'Сохранить',
            primaryButtonIcon: Icons.save,
          ),
        ),
      ),
    );
  }
}
