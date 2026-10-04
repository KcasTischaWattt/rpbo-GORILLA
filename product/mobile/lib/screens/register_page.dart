import 'package:crosswords/providers/auth_provider.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../services/api_service.dart';
import 'email_verification_page.dart';

class RegisterPage extends StatefulWidget {
  final Future<void> Function() onRegisterSuccess;
  final VoidCallback toggleTheme;
  final bool isDarkMode;

  const RegisterPage({
    super.key,
    required this.onRegisterSuccess,
    required this.toggleTheme,
    required this.isDarkMode,
  });

  @override
  _RegisterPageState createState() => _RegisterPageState();
}

class _RegisterPageState extends State<RegisterPage> {
  final TextEditingController _nameController = TextEditingController();
  final TextEditingController _surnameController = TextEditingController();
  final TextEditingController _emailController = TextEditingController();
  final TextEditingController _passwordController = TextEditingController();

  bool _loading = false;
  String? _errorMessage;

  Future<void> _performRegistration() async {
    setState(() {
      _loading = true;
      _errorMessage = null;
    });

    try {
      await ApiService.register(
        _nameController.text,
        _surnameController.text,
        _emailController.text,
        _passwordController.text,
      );

      await Navigator.of(context).push(MaterialPageRoute(
        builder: (_) => EmailVerificationPage(
          email: _emailController.text.trim(),
          onVerificationSuccess: () {
            Navigator.of(context).pop();
            widget.onRegisterSuccess();
          },
          onSkip: () {
            Navigator.of(context).pop();
            widget.onRegisterSuccess();
          },
          toggleTheme: widget.toggleTheme,
          isDarkMode: widget.isDarkMode,
        ),
      ));

      final ctx = context;
      Provider.of<AuthProvider>(ctx, listen: false).setAuthenticated(true);

      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(
            content:
                Text("Выполнен вход как ${_emailController.text.trim()}")),
      );

      if (mounted) {
        Navigator.of(ctx).pop();
      }
    } catch (e) {
      setState(() {
        _errorMessage = "Ошибка регистрации";
      });
    }

    setState(() {
      _loading = false;
    });
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
          : const Text('Зарегистрироваться',
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
              children: [
                const SizedBox(height: 40),
                _buildRegisterIcon(),
                const SizedBox(height: 24),
                const Text(
                  'Регистрация',
                  style: TextStyle(fontSize: 28, fontWeight: FontWeight.bold),
                ),
                const SizedBox(height: 20),
                _buildInputField(_nameController, 'Ваше Имя'),
                const SizedBox(height: 10),
                _buildInputField(_surnameController, 'Ваша Фамилия'),
                const SizedBox(height: 10),
                _buildInputField(_emailController, 'Email'),
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
