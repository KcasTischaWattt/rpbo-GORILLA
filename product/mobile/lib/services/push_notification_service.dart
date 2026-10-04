import 'package:firebase_core/firebase_core.dart';
import 'package:firebase_messaging/firebase_messaging.dart';
import 'package:flutter/material.dart';
import 'package:flutter_local_notifications/flutter_local_notifications.dart';
import '../main.dart';
import '../screens/digest_detail_page_with_loading.dart';
import 'api_service.dart';

class PushNotificationService {
  final FirebaseMessaging _fcm = FirebaseMessaging.instance;
  final FlutterLocalNotificationsPlugin _localNotifications =
      FlutterLocalNotificationsPlugin();

  /// Инициализация сервиса push-уведомлений
  Future<void> initialize() async {
    // Запрос разрешения на получение уведомлений
    await _fcm.requestPermission();

    // Обработка обновления токена
    FirebaseMessaging.instance.onTokenRefresh.listen((newToken) async {
      await ApiService.sendFcmToken(newToken);
    });

    // Обработка нажатия на уведомление
    FirebaseMessaging.onMessageOpenedApp.listen((message) {
      final digestId = message.data['digestId'];
      if (digestId != null) {
        navigatorKey.currentState?.push(
          MaterialPageRoute(
            builder: (_) => DigestDetailPageWithLoading(digestId: digestId),
          ),
        );
      }
    });

    // Обработка фоновых уведомлений
    final initialMessage = await _fcm.getInitialMessage();
    if (initialMessage != null) {
      final digestId = initialMessage.data['digestId'];
      if (digestId != null) {
        navigatorKey.currentState?.push(
          MaterialPageRoute(
            builder: (_) => DigestDetailPageWithLoading(digestId: digestId),
          ),
        );
      }
    }

    // Обработка, если уведомление открыло закрытое приложение
    FirebaseMessaging.instance.getInitialMessage().then((message) {
      if (message != null) {
        _handleMessage(message);
      }
    });

// Обработка, если приложение в фоне
    FirebaseMessaging.onMessageOpenedApp.listen((message) {
      _handleMessage(message);
    });

    // Настройка локальных уведомлений
    const androidSettings =
        AndroidInitializationSettings('@mipmap/ic_launcher');
    final initSettings = InitializationSettings(android: androidSettings);
    await _localNotifications.initialize(initSettings);

    FirebaseMessaging.onMessage.listen((message) {
      final notification = message.notification;
      if (notification != null) {
        _localNotifications.show(
          0,
          notification.title,
          notification.body,
          const NotificationDetails(
            android: AndroidNotificationDetails(
              'channel_id',
              'Main Channel',
              importance: Importance.max,
              priority: Priority.high,
            ),
          ),
        );
      }
    });

    FirebaseMessaging.onBackgroundMessage(_firebaseMessagingBackgroundHandler);
  }

  /// Обработка фоновых сообщений
  static Future<void> _firebaseMessagingBackgroundHandler(
      RemoteMessage message) async {
    await Firebase.initializeApp();
    debugPrint("Handling background message: ${message.messageId}");
  }

  /// Удаление FCM токена
  Future<void> deleteFcmToken(String? token) async {
    if (token == null || token.isEmpty) {
      debugPrint("FCM токен отсутствует, удаление пропущено");
      return;
    }
    try {
      await ApiService.deleteFcmToken(token);
      await FirebaseMessaging.instance.deleteToken();
      debugPrint("FCM токен удалён");
    } catch (e) {
      debugPrint("Ошибка при удалении FCM токена: $e");
    }
  }

  /// Обработка сообщения
  void _handleMessage(RemoteMessage message) {
    final digestId = message.data['digestId'];
    if (digestId != null) {
      debugPrint("Навигация по уведомлению: digestId = $digestId");
      navigatorKey.currentState?.push(
        MaterialPageRoute(
          builder: (_) => DigestDetailPageWithLoading(digestId: digestId),
        ),
      );
    }
  }
}
