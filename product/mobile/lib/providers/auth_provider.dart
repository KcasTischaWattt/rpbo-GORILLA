import 'package:crosswords/providers/article_provider.dart';
import 'package:crosswords/providers/digest_provider.dart';
import 'package:crosswords/providers/subscription_provider.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../services/api_service.dart';

class AuthProvider with ChangeNotifier {
  bool _isAuthenticated = false;
  bool _showMainApp = false;
  bool _isLoading = true;

  bool get isAuthenticated => _isAuthenticated;
  bool get showMainApp => _showMainApp;
  bool get isLoading => _isLoading;

  Future<void> checkAuth() async {
    _isLoading = true;
    notifyListeners();

    _isAuthenticated = await ApiService.checkAuth();
    _showMainApp = _isAuthenticated;
    notifyListeners();

    _isLoading = false;
    notifyListeners();
  }

  void setUnauthenticated() {
    _isAuthenticated = false;
    _showMainApp = false;
    notifyListeners();
  }

  void setAuthenticated(bool value) {
    _isAuthenticated = value;
    _showMainApp = true;
    notifyListeners();
  }

  void continueWithoutLogin() {
    _isAuthenticated = false;
    _showMainApp = true;
    notifyListeners();
  }

  Future<void> logout(BuildContext context) async {
    Provider.of<ArticleProvider>(context, listen: false).clear();
    Provider.of<DigestProvider>(context, listen: false).clear();
    Provider.of<SubscriptionProvider>(context, listen: false).clear();
    await ApiService.logout();
    _isAuthenticated = false;
    _showMainApp = false;
    notifyListeners();
  }
}
