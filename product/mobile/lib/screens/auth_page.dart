import 'package:crosswords/providers/auth_provider.dart';
import 'package:crosswords/screens/register_page.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../services/api_service.dart';
import 'package:crosswords/screens/login_page.dart';

class AuthPage extends StatefulWidget {
  final Future<void> Function() setLogin;
  final VoidCallback toggleTheme;
  final bool isDarkMode;

  const AuthPage({
    super.key,
    required this.setLogin,
    required this.toggleTheme,
    required this.isDarkMode,
  });

  @override
  _AuthPageState createState() => _AuthPageState();
}

class _AuthPageState extends State<AuthPage> {
  bool _loading = false;

  Future<void> _performLogin(BuildContext context) async {
    if (!mounted) return;

    setState(() {
      _loading = true;
    });

    try {
      await ApiService.login("admin", "admin");
      await widget.setLogin();
    } catch (e) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text("Ошибка входа. Проверьте данные.")),
      );
    }

    if (mounted) {
      setState(() {
        _loading = false;
      });
    }
  }

  AppBar _buildAppBar() {
    return AppBar(
      backgroundColor: Colors.transparent,
      elevation: 0,
      actions: [
        IconButton(
          icon: Icon(widget.isDarkMode ? Icons.wb_sunny : Icons.nights_stay,
              size: 28),
          onPressed: widget.toggleTheme,
        ),
      ],
    );
  }

  ButtonStyle _buttonStyle(BuildContext context) {
    return ElevatedButton.styleFrom(
      backgroundColor: Theme.of(context).primaryColor,
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
      padding: const EdgeInsets.symmetric(horizontal: 50, vertical: 20),
    );
  }

  void _navigateToLogin(BuildContext context) {
    Navigator.push(
      context,
      MaterialPageRoute(
        builder: (context) => LoginPage(
          onLoginSuccess: widget.setLogin,
          toggleTheme: widget.toggleTheme,
          isDarkMode: widget.isDarkMode,
        ),
      ),
    );
  }

  void _navigateToRegister(BuildContext context) {
    Navigator.push(
      context,
      MaterialPageRoute(
        builder: (context) => RegisterPage(
          onRegisterSuccess: widget.setLogin,
          toggleTheme: widget.toggleTheme,
          isDarkMode: widget.isDarkMode,
        ),
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Theme.of(context).scaffoldBackgroundColor,
      appBar: _buildAppBar(),
      body: Center(
        child: Padding(
          padding: const EdgeInsets.all(32.0),
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              const Icon(Icons.account_circle, size: 120, color: Colors.grey),
              const SizedBox(height: 24),
              const Text(
                'Добро пожаловать в Умный Кропус СМИ',
                style: TextStyle(fontSize: 28, fontWeight: FontWeight.bold),
                textAlign: TextAlign.center,
              ),
              const SizedBox(height: 40),
              ElevatedButton(
                onPressed: _loading ? null : () => _navigateToLogin(context),
                style: _buttonStyle(context),
                child: _loading
                    ? const CircularProgressIndicator()
                    : const Text('Войти',
                        style: TextStyle(fontSize: 20, color: Colors.black)),
              ),
              const SizedBox(height: 20),
              ElevatedButton(
                onPressed: () => _navigateToRegister(context),
                style: _buttonStyle(context),
                child: const Text('Регистрация',
                    style: TextStyle(fontSize: 20, color: Colors.black)),
              ),
              const SizedBox(height: 20),
              TextButton(
                onPressed: () {
                  Provider.of<AuthProvider>(context, listen: false).continueWithoutLogin();
                },
                child: const Text(
                  'Продолжить без регистрации',
                  style: TextStyle(
                      fontSize: 18, decoration: TextDecoration.underline),
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
