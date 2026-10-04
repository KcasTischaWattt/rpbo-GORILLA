import 'package:dio/dio.dart';

import '../services/api_service.dart';
import 'package:flutter/material.dart';

class UserSettingsProvider extends ChangeNotifier {
  bool _subscribable = false;
  bool _sendToMail = false;
  bool _mobileNotifications = false;
  bool _personalSendToMail = false;
  bool _personalMobileNotifications = false;
  bool _needsConfirmation = false;

  bool _isLoading = false;
  bool _isPasswordChanging = false;
  bool _isEmailChanging = false;

  bool get subscribable => _subscribable;

  bool get sendToMail => _sendToMail;

  bool get mobileNotifications => _mobileNotifications;

  bool get personalSendToMail => _personalSendToMail;

  bool get personalMobileNotifications => _personalMobileNotifications;

  bool get isLoading => _isLoading;

  bool get isPasswordChanging => _isPasswordChanging;

  bool get isEmailChanging => _isEmailChanging;

  bool get needsConfirmation => _needsConfirmation;

  Future<void> loadSettings() async {
    try {
      _isLoading = true;
      notifyListeners();
      final response = await ApiService.get('/users/personal_info');
      _subscribable = response.data['subscribable'];
      _sendToMail = response.data['send_to_mail'];
      _mobileNotifications = response.data['mobile_notifications'];
      _personalSendToMail = response.data['personal_send_to_mail'];
      _personalMobileNotifications =
          response.data['personal_mobile_notifications'];
    } finally {
      _isLoading = false;
      notifyListeners();
    }
  }

  Future<void> _updateSettings() async {
    await ApiService.put('/users/subscription_settings/set', data: {
      'subscribable': subscribable,
      'send_to_mail': sendToMail,
      'mobile_notifications': mobileNotifications,
      'personal_send_to_mail': personalSendToMail,
      'personal_mobile_notifications': personalMobileNotifications,
    });
  }

  Future<void> setSubscribable(bool value) async {
    _subscribable = value;
    notifyListeners();
    await _updateSettings();
  }

  Future<void> setSendToMail(bool value) async {
    _sendToMail = value;
    notifyListeners();
    await _updateSettings();
  }

  Future<void> setMobileNotifications(bool value) async {
    _mobileNotifications = value;
    notifyListeners();
    await _updateSettings();
  }

  Future<void> setPersonalSendToMail(bool value) async {
    _personalSendToMail = value;
    notifyListeners();
    await _updateSettings();
  }

  Future<void> setPersonalMobileNotifications(bool value) async {
    _personalMobileNotifications = value;
    notifyListeners();
    await _updateSettings();
  }

  /// Смена пароля пользователя
  Future<String?> changePassword(String oldPassword, String newPassword) async {
    if (oldPassword.isEmpty || newPassword.isEmpty) {
      return 'Пожалуйста, заполните все поля.';
    }
    try {
      _isPasswordChanging = true;
      notifyListeners();
      await ApiService.changePassword(oldPassword, newPassword);
      return null;
    } on DioException catch (e) {
      if (e.response?.statusCode == 400) {
        return 'Новый пароль не должен совпадать со старым.';
      } else if (e.response?.statusCode == 403) {
        return 'Старый пароль неверный.';
      } else {
        return 'Не удалось изменить пароль. Попробуйте позже.';
      }
    } catch (e) {
      return 'Произошла непредвиденная ошибка.';
    } finally {
      _isPasswordChanging = false;
      notifyListeners();
    }
  }

  /// Смена почты пользователя
  Future<String?> changeEmail(String newEmail) async {
    if (newEmail.isEmpty) {
      return 'Пожалуйста, заполните поле почты.';
    }
    try {
      _isEmailChanging = true;
      notifyListeners();
      await ApiService.changeEmail(newEmail);
      return null;
    } on DioException catch (e) {
      if (e.response?.statusCode == 400) {
        return 'Новый email не должен совпадать со старым';
      } else {
        return 'Не удалось изменить почту. Попробуйте позже.';
      }
    } catch (e) {
      return 'Произошла непредвиденная ошибка.';
    } finally {
      _isEmailChanging = false;
      notifyListeners();
    }
  }

  /// Проверка необходимости подтверждения почты
  Future<void> loadVerificationStatus() async {
    try {
      final result = await ApiService.needsEmailConfirmation();
      _needsConfirmation = result;
      notifyListeners();
    } catch (_) {}
  }
}
