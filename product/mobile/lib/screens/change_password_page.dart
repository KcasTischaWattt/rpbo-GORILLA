import 'package:crosswords/screens/widgets/loading_button.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

import '../providers/user_settings_provider.dart';

class ChangePasswordPage extends StatefulWidget {
  const ChangePasswordPage({super.key});

  @override
  _ChangePasswordPageState createState() => _ChangePasswordPageState();
}

class _ChangePasswordPageState extends State<ChangePasswordPage> {
  final TextEditingController _oldPasswordController = TextEditingController();
  final TextEditingController _newPasswordController = TextEditingController();

  Future<void> _showAlertDialog(String title, String message) {
    return showDialog<void>(
      context: context,
      builder: (BuildContext context) {
        return AlertDialog(
          title: Text(title),
          content: Text(message),
          actions: [
            TextButton(
              child: const Text('ОК'),
              onPressed: () {
                Navigator.of(context).pop();
              },
            ),
          ],
        );
      },
    );
  }

  void _onSubmit() async {
    final oldPassword = _oldPasswordController.text.trim();
    final newPassword = _newPasswordController.text.trim();

    final userSettingsProvider =
        Provider.of<UserSettingsProvider>(context, listen: false);
    final errorMessage =
        await userSettingsProvider.changePassword(oldPassword, newPassword);

    if (errorMessage == null) {
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text("Пароль успешно изменён")),
        );
        Navigator.pop(context);
      }
    } else {
      _showAlertDialog('Ошибка', errorMessage);
    }
  }

  Future<void> _confirmChange() async {
    final confirmed = await showDialog<bool>(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text("Подтвердить смену пароля"),
        content: const Text("Вы уверены, что хотите изменить пароль?"),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context, false),
            child: const Text("Отмена"),
          ),
          TextButton(
            onPressed: () => Navigator.pop(context, true),
            child:
                const Text("Подтвердить", style: TextStyle(color: Colors.red)),
          ),
        ],
      ),
    );

    if (confirmed == true) {
      _onSubmit();
    }
  }

  Widget _buildPasswordField(String label, TextEditingController controller) {
    return Container(
      margin: const EdgeInsets.symmetric(vertical: 8),
      decoration: BoxDecoration(
        color: Theme.of(context).bottomNavigationBarTheme.backgroundColor,
        borderRadius: BorderRadius.circular(12),
      ),
      child: TextField(
        controller: controller,
        obscureText: true,
        decoration: InputDecoration(
          labelText: label,
          contentPadding:
              const EdgeInsets.symmetric(horizontal: 16, vertical: 14),
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
        title: const Text("Смена пароля",
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
            _buildPasswordField("Старый пароль", _oldPasswordController),
            _buildPasswordField("Новый пароль", _newPasswordController),
            const SizedBox(height: 20),
            Align(
              alignment: Alignment.centerLeft,
              child: LoadingButton(
                isLoading: userSettingsProvider.isPasswordChanging,
                onPressed: _confirmChange,
                text: 'Подтвердить',
              ),
            )
          ],
        ),
      ),
    );
  }
}
