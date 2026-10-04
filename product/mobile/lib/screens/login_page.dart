import 'package:crosswords/providers/auth_provider.dart';
import 'package:dio/dio.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../services/api_service.dart';

class LoginPage extends StatefulWidget {
  final Future<void> Function() onLoginSuccess;
  final VoidCallback toggleTheme;
  final bool isDarkMode;

  const LoginPage({
    super.key,
    required this.onLoginSuccess,
    required this.toggleTheme,
    required this.isDarkMode,
  });

  @override
  _LoginPageState createState() => _LoginPageState();
}

class _LoginPageState extends State<LoginPage> {
  final TextEditingController _usernameController = TextEditingController();
  final TextEditingController _passwordController = TextEditingController();

  bool _loading = false;
  String? _errorMessage;

  Future<void> _performRegistration() async {
    setState(() {
      _loading = true;
      _errorMessage = null;
    });

    try {
      await ApiService.login(
        _usernameController.text,
        _passwordController.text,
      );

      await widget.onLoginSuccess();

      final ctx = context;
      Provider.of<AuthProvider>(ctx, listen: false).setAuthenticated(true);

      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(
            content:
                Text("Выполнен вход как ${_usernameController.text.trim()}")),
      );

      if (mounted) {
        Navigator.of(ctx).pop();
      }
    } on DioException catch (dioError) {
      setState(() {
        if (dioError.response?.statusCode == 400) {
          _errorMessage = "Неверный логин или пароль.";
        } else {
          _errorMessage = "Ошибка сервера: ${dioError.response?.statusCode}";
        }
      });
    } catch (e) {
      setState(() {
        _errorMessage = "Произошла ошибка: ${e.toString()}";
      });
    } finally {
      setState(() {
        _loading = false;
      });
    }
  }

  Widget _buildInputField(TextEditingController controller, String labelText,
      {bool isPassword = false}) {
    return Container(
      decoration: BoxDecoration(
        color: Theme.of(context).bottomNavigationBarTheme.backgroundColor,
        borderRadius: BorderRadius.circular(8),
      ),
      child: TextField(
        controller: controller,
        obscureText: isPassword,
        style: const TextStyle(fontSize: 18),
        decoration: InputDecoration(
          labelText: labelText,
          hintStyle: TextStyle(color: Colors.grey[600], fontSize: 16),
          border: InputBorder.none,
          contentPadding:
              const EdgeInsets.symmetric(horizontal: 8, vertical: 8),
        ),
      ),
    );
  }

  Widget _buildErrorMessage() {
    if (_errorMessage == null) return const SizedBox.shrink();
    return Text(_errorMessage!, style: const TextStyle(color: Colors.red));
  }

  Widget _buildRegisterButton() {
    return ElevatedButton(
      onPressed: _loading ? null : _performRegistration,
      style: ElevatedButton.styleFrom(
        backgroundColor: Theme.of(context).primaryColor,
        padding: const EdgeInsets.symmetric(horizontal: 50, vertical: 20),
        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
      ),
      child: _loading
          ? const CircularProgressIndicator()
          : const Text('Войти',
              style: TextStyle(fontSize: 18, color: Colors.black)),
    );
  }

  Widget _buildRegisterIcon() {
    return const Icon(Icons.person_add, size: 100, color: Colors.grey);
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Theme.of(context).scaffoldBackgroundColor,
      appBar: AppBar(
        backgroundColor: Colors.transparent,
        elevation: 0,
        actions: [
          IconButton(
            icon: Icon(widget.isDarkMode ? Icons.wb_sunny : Icons.nights_stay,
                size: 28),
            onPressed: widget.toggleTheme,
          ),
        ],
      ),
      body: SafeArea(
        child: SingleChildScrollView(
          child: Padding(
            padding: const EdgeInsets.all(32.0),
            child: Column(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                const SizedBox(height: 40),
                _buildRegisterIcon(),
                const SizedBox(height: 24),
                const Text(
                  'Вход в аккаунт',
                  style: TextStyle(fontSize: 28, fontWeight: FontWeight.bold),
                ),
                const SizedBox(height: 20),
                _buildInputField(_usernameController, 'Логин'),
                const SizedBox(height: 10),
                _buildInputField(_passwordController, 'Пароль',
                    isPassword: true),
                const SizedBox(height: 20),
                _buildErrorMessage(),
                const SizedBox(height: 10),
                _buildRegisterButton(),
                const SizedBox(height: 40),
              ],
            ),
          ),
        ),
      ),
    );
  }
}
