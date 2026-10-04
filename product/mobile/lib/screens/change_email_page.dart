import 'package:crosswords/screens/widgets/loading_button.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

import '../providers/user_settings_provider.dart';

class ChangeEmailPage extends StatefulWidget {
  const ChangeEmailPage({super.key});

  @override
  _ChangeEmailPageState createState() => _ChangeEmailPageState();
}

class _ChangeEmailPageState extends State<ChangeEmailPage> {
  final TextEditingController _emailController = TextEditingController();

  Future<void> _onSubmit() async {
    final newEmail = _emailController.text.trim();

    final userSettingsProvider =
        Provider.of<UserSettingsProvider>(context, listen: false);
    final errorMessage = await userSettingsProvider.changeEmail(newEmail);

    if (errorMessage == null) {
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text("Email успешно изменён")),
        );
        Navigator.pop(context);
      }
    } else {
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text(errorMessage)),
        );
      }
    }
  }

  Widget _buildEmailField() {
    return Container(
      margin: const EdgeInsets.symmetric(vertical: 8),
      decoration: BoxDecoration(
        color: Theme.of(context).bottomNavigationBarTheme.backgroundColor,
        borderRadius: BorderRadius.circular(12),
      ),
      child: TextField(
        controller: _emailController,
        keyboardType: TextInputType.emailAddress,
        decoration: const InputDecoration(
          labelText: "Новая почта",
          contentPadding: EdgeInsets.symmetric(horizontal: 16, vertical: 14),
          border: InputBorder.none,
        ),
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    final userSettingsProvider = Provider.of<UserSettingsProvider>(context);

    return Scaffold(
      appBar: AppBar(
        title: const Text("Смена почты",
            style: TextStyle(fontSize: 24, fontWeight: FontWeight.bold)),
        backgroundColor: Theme.of(context).scaffoldBackgroundColor,
        surfaceTintColor: Colors.transparent,
        elevation: 0,
        shadowColor: Colors.transparent,
      ),
      body: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          children: [
            _buildEmailField(),
            const SizedBox(height: 20),
            Align(
              alignment: Alignment.centerLeft,
              child: LoadingButton(
                isLoading: userSettingsProvider.isEmailChanging,
                onPressed: _onSubmit,
                text: "Подтвердить",
              ),
            )
          ],
        ),
      ),
    );
  }
}
