import 'package:crosswords/screens/widgets/digest_components/section_title.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../../../providers/subscription_provider.dart';

class NotificationSettings extends StatelessWidget {
  const NotificationSettings({super.key});

  Widget _buildCheckbox(
      String title,
      bool Function(SubscriptionProvider) getValue,
      void Function(SubscriptionProvider, bool) setValue) {
    return Consumer<SubscriptionProvider>(
      builder: (context, provider, child) {
        return CheckboxListTile(
          title: Text(title),
          value: getValue(provider),
          onChanged: (value) {
            if (value != null) {
              setValue(provider, value);
            }
          },
        );
      },
    );
  }

  Widget _buildCheckboxRow() {
    return LayoutBuilder(
      builder: (context, constraints) {
        bool isNarrow = constraints.maxWidth <= 395;

        return Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            isNarrow ? _buildCheckboxColumnLayout() : _buildCheckboxRowLayout(),
            const SizedBox(height: 12),
            const Divider(thickness: 1, height: 20),
          ],
        );
      },
    );
  }

  Widget _buildCheckboxColumnLayout() {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        _buildCheckbox(
          "Почта",
              (provider) => provider.sendToMail,
              (provider, value) => provider.setSendToMail(value),
        ),
        _buildCheckbox(
          "Приложение",
              (provider) => provider.mobileNotifications,
              (provider, value) => provider.setMobileNotifications(value),
        ),
      ],
    );
  }

  Widget _buildCheckboxRowLayout() {
    return Row(
      children: [
        Expanded(
          child: _buildCheckbox(
            "Почта",
                (provider) => provider.sendToMail,
                (provider, value) => provider.setSendToMail(value),
          ),
        ),
        Expanded(
          child: _buildCheckbox(
            "Приложение",
                (provider) => provider.mobileNotifications,
                (provider, value) => provider.setMobileNotifications(value),
          ),
        ),
      ],
    );
  }


  @override
  Widget build(BuildContext context) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        SectionTitle(title: 'Настройки уведомлений и приватности'),
        _buildCheckboxRow(),
        Consumer<SubscriptionProvider>(
          builder: (context, provider, child) {
            return CheckboxListTile(
              title: const Text("Сделать публичным"),
              value: provider.isPublic,
              onChanged: (value) {
                provider.setIsPublic(value!);
              },
            );
          },
        ),
        const SizedBox(height: 16),
      ],
    );
  }
}
