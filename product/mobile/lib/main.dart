import 'package:crosswords/providers/user_settings_provider.dart';
import 'package:crosswords/providers/auth_provider.dart';
import 'package:crosswords/screens/digest_detail_page_with_loading.dart';
import 'package:crosswords/services/push_notification_service.dart';
import 'package:firebase_core/firebase_core.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:google_fonts/google_fonts.dart';
import 'services/api_service.dart';
import 'providers/article_provider.dart';
import 'providers/digest_provider.dart';
import 'providers/subscription_provider.dart';
import 'screens/articles_page.dart';
import 'screens/digests_page.dart';
import 'screens/notifications_page.dart';
import 'screens/settings_page.dart';
import 'screens/auth_page.dart';
import 'package:shared_preferences/shared_preferences.dart';

final GlobalKey<NavigatorState> navigatorKey = GlobalKey<NavigatorState>();

void main() async {
  ApiService.initializeInterceptors();
  WidgetsFlutterBinding.ensureInitialized();
  await Firebase.initializeApp();
  await PushNotificationService().initialize();
  runApp(
    MultiProvider(
      providers: [
        ChangeNotifierProvider(create: (_) => ArticleProvider()),
        ChangeNotifierProvider(create: (_) => DigestProvider()),
        ChangeNotifierProvider(create: (_) => SubscriptionProvider()),
        ChangeNotifierProvider(create: (_) => AuthProvider()),
        ChangeNotifierProvider(create: (_) => UserSettingsProvider()),
      ],
      child: const MyApp(),
    ),
  );
}

class MyApp extends StatefulWidget {
  const MyApp({super.key});

  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  ThemeMode _themeMode = ThemeMode.dark;
  bool _isThemeLoaded = false;

  @override
  void initState() {
    super.initState();
    _loadThemeMode();
    Future.microtask(
        () => Provider.of<AuthProvider>(context, listen: false).checkAuth());
  }

  Future<void> _onLogout() async {
    await Provider.of<AuthProvider>(context, listen: false).logout(context);
  }

  Future<void> _loadThemeMode() async {
    final prefs = await SharedPreferences.getInstance();
    final savedTheme = prefs.getString('theme_mode');
    if (mounted) {
      setState(() {
        _themeMode = savedTheme != null
            ? ThemeMode.values.firstWhere(
                (e) => e.name == savedTheme,
                orElse: () => ThemeMode.system,
              )
            : ThemeMode.system;
        _isThemeLoaded = true;
      });
    }
  }

  void _setTheme(ThemeMode newThemeMode) {
    setState(() {
      _themeMode = newThemeMode;
    });
    _saveThemeMode(newThemeMode);
  }

  void _toggleTheme() {
    setState(() {
      _themeMode =
          _themeMode == ThemeMode.light ? ThemeMode.dark : ThemeMode.light;
    });
    _saveThemeMode(_themeMode);
  }

  Future<void> _saveThemeMode(ThemeMode mode) async {
    final prefs = await SharedPreferences.getInstance();
    await prefs.setString('theme_mode', mode.name);
  }

  Route<dynamic>? _onGenerateRoute(RouteSettings settings) {
    final uri = Uri.parse(settings.name ?? '');
    if (uri.pathSegments.length == 2 &&
        uri.pathSegments.first == 'digest' &&
        uri.pathSegments[1].isNotEmpty) {
      return MaterialPageRoute(
        builder: (context) =>
            DigestDetailPageWithLoading(digestId: uri.pathSegments[1]),
      );
    }
    return MaterialPageRoute(
      builder: (_) => const Scaffold(
        body: Center(child: Text('Страница не найдена')),
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    return Consumer<AuthProvider>(
      builder: (context, authProvider, _) {
        final themeModeToUse = _isThemeLoaded ? _themeMode : ThemeMode.system;

        if (authProvider.isLoading) {
          return MaterialApp(
            theme: _buildLightTheme(),
            darkTheme: _buildDarkTheme(),
            themeMode: themeModeToUse,
            home: const SplashScreen(),
          );
        }

        return MaterialApp(
          navigatorKey: navigatorKey,
          theme: _buildLightTheme(),
          darkTheme: _buildDarkTheme(),
          themeMode: _themeMode,
          onGenerateRoute: _onGenerateRoute,
          home: authProvider.showMainApp
              ? MainApp(
                  setTheme: _setTheme,
                  onLogout: _onLogout,
                  isAuthenticated: authProvider.isAuthenticated,
                )
              : AuthPage(
                  setLogin: () => authProvider.checkAuth(),
                  toggleTheme: _toggleTheme,
                  isDarkMode: _themeMode == ThemeMode.dark,
                ),
        );
      },
    );
  }

  ThemeData _buildLightTheme() {
    return ThemeData(
      brightness: Brightness.light,
      scaffoldBackgroundColor: const Color(0xFFF5F5F5),
      primaryColor: const Color(0xFFFFD700),
      secondaryHeaderColor: const Color(0xFF517ECF),
      textTheme: GoogleFonts.latoTextTheme().apply(
        bodyColor: Colors.black,
        displayColor: Colors.black,
      ),
      bottomNavigationBarTheme: const BottomNavigationBarThemeData(
        backgroundColor: Color(0xFFFFFFFF),
        selectedItemColor: Color(0xFF474389),
        unselectedItemColor: Colors.grey,
      ),
    );
  }

  ThemeData _buildDarkTheme() {
    return ThemeData(
      brightness: Brightness.dark,
      scaffoldBackgroundColor: const Color(0xFF121212),
      primaryColor: const Color(0xFFFFD700),
      secondaryHeaderColor: const Color(0xFF3E63A0),
      textTheme: GoogleFonts.latoTextTheme().apply(
        bodyColor: Colors.white,
        displayColor: Colors.white,
      ),
      bottomNavigationBarTheme: const BottomNavigationBarThemeData(
        backgroundColor: Color(0xFF1F1F1F),
        selectedItemColor: Colors.white,
        unselectedItemColor: Colors.grey,
      ),
    );
  }
}

class MainApp extends StatefulWidget {
  final void Function(ThemeMode) setTheme;
  final bool isAuthenticated;
  final Future<void> Function() onLogout;

  const MainApp({
    super.key,
    required this.setTheme,
    required this.isAuthenticated,
    required this.onLogout,
  });

  @override
  _MainAppState createState() => _MainAppState();
}

class _MainAppState extends State<MainApp> {
  int _selectedIndex = 0;

  late final List<Widget> _pages;

  @override
  void initState() {
    super.initState();
    _pages = [
      ArticlesPage(),
      DigestsPage(),
      NotificationsPage(),
      SettingsPage(
        setTheme: widget.setTheme,
        onLogout: widget.onLogout,
      ),
    ];
  }

  void _onItemTapped(int index) {
    setState(() {
      _selectedIndex = index;
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Padding(
        padding: const EdgeInsets.all(16),
        child: _pages[_selectedIndex],
      ),
      bottomNavigationBar: BottomNavigationBar(
        type: BottomNavigationBarType.fixed,
        currentIndex: _selectedIndex,
        onTap: _onItemTapped,
        selectedFontSize: 14,
        unselectedFontSize: 12,
        iconSize: 24,
        items: const [
          BottomNavigationBarItem(
            icon: Icon(Icons.article),
            label: 'Статьи',
          ),
          BottomNavigationBarItem(
            icon: Icon(Icons.dashboard),
            label: 'Дайджесты',
          ),
          BottomNavigationBarItem(
            icon: Icon(Icons.notifications),
            label: 'Уведомления',
          ),
          BottomNavigationBarItem(
            icon: Icon(Icons.settings),
            label: 'Настройки',
          ),
        ],
      ),
    );
  }
}

extension CustomColors on ThemeData {
  Color get dangerColor {
    return brightness == Brightness.dark
        ? const Color(0xFFEA4C46)
        : const Color(0xFFFF5252);
  }
}

class SplashScreen extends StatelessWidget {
  const SplashScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Center(
        child: CircularProgressIndicator(
          valueColor: AlwaysStoppedAnimation(Theme.of(context).primaryColor),
        ),
      ),
    );
  }
}
