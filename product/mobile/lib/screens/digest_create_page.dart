import 'package:crosswords/providers/subscription_provider.dart';
import 'package:crosswords/screens/base_digest_change_page.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

class DigestCreatePage extends StatefulWidget {
  const DigestCreatePage({super.key});

  @override
  _DigestCreatePageState createState() => _DigestCreatePageState();
}

class _DigestCreatePageState extends BaseDigestPage<DigestCreatePage> {
  @override
  void onInit(SubscriptionProvider provider) {
    WidgetsBinding.instance.addPostFrameCallback((_) {
      provider.fetchCurrentUserEmail();
    });
  }

  @override
  void onPrimaryPressed() async {
    final provider = Provider.of<SubscriptionProvider>(context, listen: false);
    try {
      await provider.createSubscription();
      if (!mounted) return;
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('Подписка успешно создана')),
      );
      Navigator.pop(context);
    } catch (e) {
      if (!mounted) return;
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(
          content: Text(
            e.toString().replaceFirst('Exception: ', ''),
          ),
        ),
      );
    }
  }

  @override
  String getAppBarTitle() => "Создание дайджеста";

  @override
  String getFormTitle() => "Создание дайджеста";

  @override
  String getPrimaryButtonText() => "Создать";

  @override
  IconData getPrimaryButtonIcon() => Icons.add;
}
