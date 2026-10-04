import 'dart:async';

import 'package:flutter/material.dart';
import 'package:pin_code_fields/pin_code_fields.dart';
import '../services/api_service.dart';

class EmailVerificationPage extends StatefulWidget {
  final String email;
  final VoidCallback onVerificationSuccess;
  final VoidCallback onSkip;
  final VoidCallback toggleTheme;
  final bool isDarkMode;

  const EmailVerificationPage({
    super.key,
    required this.email,
    required this.onVerificationSuccess,
    required this.onSkip,
    required this.toggleTheme,
    required this.isDarkMode,
  });

  @override
  State<EmailVerificationPage> createState() => _EmailVerificationPageState();
}

class _EmailVerificationPageState extends State<EmailVerificationPage> {
  String _code = '';
  bool _loading = false;
  String? _errorMessage;
  Timer? _resendTimer;
  int _secondsLeft = 60;
  bool _canResend = false;

  @override
  void initState() {
    super.initState();
    _sendVerificationCode();
  }

  @override
  void dispose() {
    _resendTimer?.cancel();
    super.dispose();
  }

  Future<void> _sendVerificationCode() async {
    setState(() {
      _canResend = false;
      _secondsLeft = 60;
    });

    try {
      await ApiService.post("/users/verification_code/send", {
        "email": widget.email,
      });
      _resendTimer?.cancel();
      _resendTimer = Timer.periodic(const Duration(seconds: 1), (timer) {
        if (_secondsLeft == 0) {
          timer.cancel();
          setState(() => _canResend = true);
        } else {
          setState(() => _secondsLeft--);
        }
      });
    } catch (e) {
      setState(() => _errorMessage = "Не удалось отправить код на почту.");
      setState(() {
        _canResend = true;
        _secondsLeft = 0;
      });
    }
  }

  Future<void> _checkCode() async {
    setState(() {
      _loading = true;
      _errorMessage = null;
    });

    try {
      await ApiService.post("/users/verification_code/check", {
        "code": _code.trim(),
      });

      widget.onVerificationSuccess();
    } catch (e) {
      setState(() => _errorMessage = "Неверный код.");
    } finally {
      setState(() => _loading = false);
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

  Widget _buildHeader() {
    return Column(
      children: [
        const SizedBox(height: 40),
        const Icon(Icons.mark_email_read_outlined,
            size: 100, color: Colors.grey),
        const SizedBox(height: 24),
        const Text(
          "Введите код из письма",
          style: TextStyle(fontSize: 24, fontWeight: FontWeight.bold),
        ),
        const SizedBox(height: 16),
        Text(
          "Мы отправили 6-значный код на ${widget.email}",
          textAlign: TextAlign.center,
          style: TextStyle(fontSize: 16, color: Colors.grey[600]),
        ),
        const SizedBox(height: 32),
      ],
    );
  }

  Widget _buildCodeField() {
    final theme = Theme.of(context);
    final fillColor = theme.scaffoldBackgroundColor;
    return PinCodeTextField(
      appContext: context,
      length: 6,
      keyboardType: TextInputType.number,
      animationType: AnimationType.fade,
      pinTheme: PinTheme(
        shape: PinCodeFieldShape.box,
        borderRadius: BorderRadius.circular(10),
        fieldHeight: 50,
        fieldWidth: 45,
        activeFillColor: fillColor,
        selectedFillColor: fillColor,
        inactiveFillColor: fillColor,
        activeColor: theme.primaryColor,
        selectedColor: theme.primaryColor,
        inactiveColor: Colors.grey,
      ),
      animationDuration: const Duration(milliseconds: 300),
      enableActiveFill: true,
      onChanged: (value) => setState(() => _code = value),
    );
  }

  Widget _buildConfirmButton() {
    return ElevatedButton(
      onPressed: _loading || _code.length != 6 ? null : _checkCode,
      style: ElevatedButton.styleFrom(
        backgroundColor: Theme.of(context).primaryColor,
        padding: const EdgeInsets.symmetric(horizontal: 50, vertical: 20),
        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
      ),
      child: _loading
          ? const CircularProgressIndicator()
          : const Text("Подтвердить",
              style: TextStyle(fontSize: 18, color: Colors.black)),
    );
  }

  Widget _buildResendButton() {
    return TextButton(
      onPressed: _canResend ? _sendVerificationCode : null,
      child: Text(
        _canResend
            ? "Отправить ещё раз"
            : "Можно повторить через $_secondsLeft сек",
        style: TextStyle(
          fontSize: 16,
          color: _canResend ? Theme.of(context).primaryColor : Colors.grey,
          decoration:
              _canResend ? TextDecoration.underline : TextDecoration.none,
        ),
      ),
    );
  }

  Widget _buildSkipButton() {
    return TextButton(
      onPressed: widget.onSkip,
      child: const Text(
        "Продолжить без подтверждения",
        style: TextStyle(fontSize: 16, decoration: TextDecoration.underline),
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Theme.of(context).scaffoldBackgroundColor,
      appBar: _buildAppBar(),
      body: SafeArea(
        child: SingleChildScrollView(
          padding: const EdgeInsets.all(32),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.center,
            children: [
              _buildHeader(),
              _buildCodeField(),
              if (_errorMessage != null)
                Padding(
                  padding: const EdgeInsets.only(top: 8),
                  child: Text(_errorMessage!,
                      style: const TextStyle(color: Colors.red)),
                ),
              const SizedBox(height: 32),
              _buildConfirmButton(),
              const SizedBox(height: 16),
              _buildResendButton(),
              const SizedBox(height: 16),
              _buildSkipButton(),
            ],
          ),
        ),
      ),
    );
  }
}
