import 'package:crosswords/main.dart';
import 'package:crosswords/providers/user_settings_provider.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:crosswords/providers/auth_provider.dart';
import '../services/api_service.dart';
import 'change_password_page.dart';
import 'change_email_page.dart';
import 'email_verification_page.dart';
import 'notifications_settings_page.dart';

class SettingsPage extends StatefulWidget {
  final void Function(ThemeMode) setTheme;
  final Future<void> Function() onLogout;

  const SettingsPage({
    super.key,
    required this.setTheme,
    required this.onLogout,
  });

  @override
  _SettingsPageState createState() => _SettingsPageState();
}

class _SettingsPageState extends State<SettingsPage> {
  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addPostFrameCallback((_) {
      final provider =
          Provider.of<UserSettingsProvider>(context, listen: false);
      provider.loadSettings();
      provider.loadVerificationStatus();
    });
  }

  String get currentThemeName {
    return Theme.of(context).brightness == Brightness.dark
        ? "Тёмная"
        : "Светлая";
  }

  AppBar _buildAppBar() {
    return AppBar(
      toolbarHeight: 60,
      backgroundColor: Theme.of(context).scaffoldBackgroundColor,
      surfaceTintColor: Colors.transparent,
      elevation: 0,
      shadowColor: Colors.transparent,
      title: const Text(
        'Настройки',
        style: TextStyle(fontSize: 24, fontWeight: FontWeight.bold),
      ),
    );
  }

  Widget _buildSettingsBlock(
      String title, List<Widget> children, Color cardColor) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Padding(
          padding: EdgeInsets.symmetric(vertical: 8),
          child: Text(
            title,
            style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold),
          ),
        ),
        Container(
          decoration: BoxDecoration(
            color: cardColor,
            borderRadius: BorderRadius.circular(12),
          ),
          padding: EdgeInsets.symmetric(vertical: 8),
          child: Column(children: children),
        ),
      ],
    );
  }

  Widget _buildSwitchTile(
      String title, bool value, Function(bool) onChanged, ThemeData theme) {
    return SwitchListTile(
      title: Text(title, style: TextStyle(fontSize: 14)),
      value: value,
      activeColor: theme.primaryColor,
      inactiveTrackColor: theme.bottomNavigationBarTheme.backgroundColor,
      onChanged: onChanged,
    );
  }

  Widget _buildStaticTile(String title, String subtitle, ThemeData theme,
      {VoidCallback? onTap, bool isSelected = false}) {
    return Material(
      color:
          isSelected ? theme.primaryColor.withOpacity(0.1) : Colors.transparent,
      child: InkWell(
        onTap: onTap,
        child: ListTile(
          title: Text(title, style: TextStyle(fontSize: 14)),
          subtitle: subtitle.isNotEmpty
              ? Text(subtitle, style: TextStyle(color: Colors.grey))
              : null,
          trailing: Icon(Icons.arrow_forward_ios, size: 16, color: Colors.grey),
        ),
      ),
    );
  }

  void _showThemeBottomSheet() {
    showModalBottomSheet(
      context: context,
      backgroundColor: Colors.transparent,
      isScrollControlled: true,
      builder: (context) {
        return Container(
          width: double.infinity,
          margin: const EdgeInsets.symmetric(horizontal: 8, vertical: 8),
          padding: const EdgeInsets.symmetric(vertical: 8),
          decoration: BoxDecoration(
            color: Theme.of(context).bottomNavigationBarTheme.backgroundColor,
            borderRadius: BorderRadius.circular(12),
          ),
          child: Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              _buildBottomSheetTile("Светлая", () {
                _setTheme("Светлая", ThemeMode.light);
              }),
              _buildBottomSheetTile("Тёмная", () {
                _setTheme("Тёмная", ThemeMode.dark);
              }),
            ],
          ),
        );
      },
    );
  }

  void _setTheme(String mode, ThemeMode newThemeMode) {
    ;
    widget.setTheme(newThemeMode);
    Navigator.pop(context);
  }

  Widget _buildBottomSheetTile(String title, VoidCallback onTap) {
    return ListTile(
      title: Text(title, style: TextStyle(fontSize: 16)),
      onTap: onTap,
    );
  }

  Future<bool?> _showLogoutConfirmationDialog(BuildContext context) {
    return showDialog<bool>(
      context: context,
      builder: (context) {
        return AlertDialog(
          title: const Text('Выход'),
          content: const Text('Вы уверены, что хотите выйти из аккаунта?'),
          actions: [
            TextButton(
              onPressed: () => Navigator.pop(context, false),
              child: const Text('Отмена'),
            ),
            TextButton(
              onPressed: () => Navigator.pop(context, true),
              child: const Text(
                'Выйти',
                style: TextStyle(color: Colors.red),
              ),
            ),
          ],
        );
      },
    );
  }

  Widget _buildAuthButton(BuildContext context) {
    final authProvider = Provider.of<AuthProvider>(context);
    final isAuth = authProvider.isAuthenticated;
    final buttonText = isAuth ? 'Выйти' : 'Войти';

    return Align(
      alignment: Alignment.centerLeft,
      child: ElevatedButton(
        onPressed: () async {
          if (isAuth) {
            final shouldLogout = await _showLogoutConfirmationDialog(context);
            if (shouldLogout == true) {
              await widget.onLogout();
            }
          } else {
            Provider.of<AuthProvider>(context, listen: false)
                .setUnauthenticated();
          }
        },
        style: ElevatedButton.styleFrom(
          backgroundColor: isAuth
              ? Theme.of(context).dangerColor
              : Theme.of(context).primaryColor,
          padding: const EdgeInsets.symmetric(horizontal: 24, vertical: 12),
          shape: RoundedRectangleBorder(
            borderRadius: BorderRadius.circular(12),
          ),
          elevation: 0,
        ),
        child: Text(
          buttonText,
          style: const TextStyle(fontSize: 16, color: Colors.black),
        ),
      ),
    );
  }

  void _navigateToChangeEmail() {
    Navigator.push(
      context,
      MaterialPageRoute(builder: (context) => const ChangeEmailPage()),
    );
  }

  void _navigateToChangePassword() {
    Navigator.push(
      context,
      MaterialPageRoute(builder: (context) => const ChangePasswordPage()),
    );
  }

  void _navigateToNotificationSettings() {
    Navigator.push(
      context,
      MaterialPageRoute(builder: (context) => const MySubscriptionsPage()),
    );
  }

  Widget _buildEmailConfirmationWarning(
      ThemeData theme, UserSettingsProvider userSettingsProvider) {
    if (!userSettingsProvider.needsConfirmation) return const SizedBox.shrink();

    return Container(
      margin: const EdgeInsets.only(bottom: 16),
      padding: const EdgeInsets.all(12),
      decoration: BoxDecoration(
        color: theme.primaryColor.withOpacity(0.1),
        border: Border.all(color: theme.primaryColor, width: 1.5),
        borderRadius: BorderRadius.circular(12),
      ),
      child: Row(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          const Icon(Icons.warning_amber_rounded,
              color: Colors.orange, size: 28),
          const SizedBox(width: 12),
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                const Text("Требуется подтверждение",
                    style:
                        TextStyle(fontSize: 16, fontWeight: FontWeight.bold)),
                const SizedBox(height: 4),
                const Text(
                  "Чтобы получать дайджесты на почту, необходимо подтвердить адрес электронной почты.",
                  style: TextStyle(fontSize: 14),
                ),
                const SizedBox(height: 8),
                Align(
                  alignment: Alignment.centerLeft,
                  child: ElevatedButton.icon(
                    onPressed: () async {
                      final email = await ApiService.getCurrentUserEmail();
                      Navigator.push(
                        context,
                        MaterialPageRoute(
                          builder: (_) => EmailVerificationPage(
                            email: email,
                            onVerificationSuccess: () {
                              userSettingsProvider.loadVerificationStatus();
                              Navigator.of(context).pop();
                            },
                            onSkip: () => Navigator.of(context).pop(),
                            toggleTheme: () => widget.setTheme(
                              Theme.of(context).brightness == Brightness.dark
                                  ? ThemeMode.light
                                  : ThemeMode.dark,
                            ),
                            isDarkMode:
                                Theme.of(context).brightness == Brightness.dark,
                          ),
                        ),
                      );
                    },
                    style: ElevatedButton.styleFrom(
                      backgroundColor: theme.primaryColor,
                      padding: const EdgeInsets.symmetric(
                          horizontal: 20, vertical: 12),
                      shape: RoundedRectangleBorder(
                          borderRadius: BorderRadius.circular(10)),
                    ),
                    icon: const Icon(Icons.email_outlined, color: Colors.black),
                    label: const Text("Подтвердить почту",
                        style: TextStyle(color: Colors.black)),
                  ),
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildGeneralSettingsBlock(
      ThemeData theme, Color cardColor, bool isAuthenticated) {
    return _buildSettingsBlock(
      'Общие',
      [
        if (isAuthenticated)
          _buildStaticTile('Сменить почту', '', theme,
              onTap: _navigateToChangeEmail),
        if (isAuthenticated)
          _buildStaticTile('Сменить пароль', '', theme,
              onTap: _navigateToChangePassword),
        _buildStaticTile('Язык', 'Русский', theme),
        _buildStaticTile('Тема', currentThemeName, theme,
            onTap: _showThemeBottomSheet),
      ],
      cardColor,
    );
  }

  Widget _buildNotificationsBlock(
      ThemeData theme, UserSettingsProvider provider, Color cardColor) {
    return _buildSettingsBlock(
      'Уведомления и рассылки',
      [
        _buildSwitchTile('Разрешить добавлять меня в рассылку',
            provider.subscribable, provider.setSubscribable, theme),
        AnimatedSwitcher(
          duration: const Duration(milliseconds: 300),
          child: provider.subscribable
              ? Column(
                  key: ValueKey<bool>(provider.subscribable),
                  children: [
                    _buildSwitchTile('Разрешить сторонние уведомления на почту',
                        provider.sendToMail, provider.setSendToMail, theme),
                    _buildSwitchTile(
                        'Разрешить сторонние мобильные уведомления',
                        provider.mobileNotifications,
                        provider.setMobileNotifications,
                        theme),
                  ],
                )
              : const SizedBox.shrink(),
        ),
        _buildSwitchTile('Разрешить уведомления на почту',
            provider.personalSendToMail, provider.setPersonalSendToMail, theme),
        _buildSwitchTile(
            'Разрешить мобильные уведомления',
            provider.personalMobileNotifications,
            provider.setPersonalMobileNotifications,
            theme),
        _buildStaticTile('Настройки уведомлений', '', theme,
            onTap: _navigateToNotificationSettings),
      ],
      cardColor,
    );
  }

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final cardColor =
        theme.bottomNavigationBarTheme.backgroundColor ?? Colors.grey[900];
    final isAuthenticated = Provider.of<AuthProvider>(context).isAuthenticated;
    final provider = Provider.of<UserSettingsProvider>(context, listen: true);

    return Scaffold(
      appBar: _buildAppBar(),
      backgroundColor: theme.scaffoldBackgroundColor,
      body: provider.isLoading
          ? const Center(child: CircularProgressIndicator())
          : ListView(
              padding: const EdgeInsets.all(16),
              children: [
                if (isAuthenticated)
                  _buildEmailConfirmationWarning(theme, provider),
                _buildGeneralSettingsBlock(theme, cardColor!, isAuthenticated),
                const SizedBox(height: 20),
                if (isAuthenticated)
                  _buildNotificationsBlock(theme, provider, cardColor),
                const SizedBox(height: 10),
                _buildAuthButton(context),
                const SizedBox(height: 16),
              ],
            ),
    );
  }
}
