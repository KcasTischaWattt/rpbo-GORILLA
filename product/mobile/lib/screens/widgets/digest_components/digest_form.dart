import 'package:crosswords/providers/subscription_provider.dart';
import 'package:crosswords/screens/widgets/action_buttons.dart';
import 'package:crosswords/screens/widgets/digest_components/digest_name_input.dart';
import 'package:crosswords/screens/widgets/digest_components/followers_section.dart';
import 'package:crosswords/screens/widgets/digest_components/notification_settings.dart';
import 'package:crosswords/screens/widgets/digest_components/section_title.dart';
import 'package:crosswords/screens/widgets/expanding_text_field.dart';
import 'package:crosswords/screens/widgets/filter_expansion_panels.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

class DigestForm extends StatelessWidget {
  final TextEditingController titleController;
  final TextEditingController descriptionController;
  final TextEditingController recipientController;
  final VoidCallback onPrimaryPressed;
  final VoidCallback onSecondaryPressed;
  final String formTitle;
  final String primaryButtonText;
  final IconData primaryButtonIcon;

  const DigestForm({
    super.key,
    required this.titleController,
    required this.descriptionController,
    required this.recipientController,
    required this.onPrimaryPressed,
    required this.onSecondaryPressed,
    required this.formTitle,
    required this.primaryButtonText,
    required this.primaryButtonIcon,
  });

  @override
  Widget build(BuildContext context) {
    final provider = Provider.of<SubscriptionProvider>(context, listen: false);

    return SingleChildScrollView(
      padding: const EdgeInsets.all(12.0),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          SectionTitle(title: "Название дайджеста"),
          const SizedBox(height: 12),
          DigestNameInput(controller: titleController),
          const SizedBox(height: 12),
          FilterExpansionPanels(provider: provider),
          const SizedBox(height: 12),
          ExpandingTextField(
            controller: descriptionController,
            hintText: "Опиcание...",
            maxLinesBeforeScroll: 7,
          ),
          const SizedBox(height: 12),
          NotificationSettings(),
          const SizedBox(height: 12),
          FollowersSection(recipientController: recipientController),
          const SizedBox(height: 12),
          ActionButtons(
            onPrimaryPressed: onPrimaryPressed,
            onSecondaryPressed: onSecondaryPressed,
            primaryText: primaryButtonText,
            secondaryText: 'Сбросить поля',
            primaryIcon: primaryButtonIcon,
            isLoading: Provider.of<SubscriptionProvider>(context).isCreating,
          ),
          const SizedBox(height: 16),
        ],
      ),
    );
  }
}
