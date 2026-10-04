import 'package:cookie_jar/cookie_jar.dart';
import 'package:crosswords/services/push_notification_service.dart';
import 'package:dio_cookie_manager/dio_cookie_manager.dart';
import 'package:dio/dio.dart';
import 'package:firebase_messaging/firebase_messaging.dart';
import 'package:flutter/material.dart';
import '../data/models/article.dart';
import 'dart:io' show Directory;
import 'package:flutter/foundation.dart' show kIsWeb;
import '../data/models/digest.dart';
import '../data/models/note.dart';
import '../data/models/search_params/digest_search_params.dart';
import '../data/models/subscribe_options.dart';
import '../data/models/subscription.dart';

class ApiService {
  // TODO поменять на false
  static final bool useMock = false;
  static bool isAuthenticatedMock = false;

  /// Экземпляр Dio с предопределенными параметрами и перехватчиками
  static final Dio _dio = Dio(
    BaseOptions(
      baseUrl: "http://crosswords-corpus.press:8081",
      connectTimeout: const Duration(seconds: 10),
      receiveTimeout: const Duration(seconds: 10),
      headers: {"Content-Type": "application/json"},
    ),
  );
  static final PersistCookieJar cookieJar = PersistCookieJar(
    storage: FileStorage('${Directory.systemTemp.path}/.cookies'),
  );
  static bool _interceptorsInitialized = false;

  static void initializeInterceptors() {
    if (_interceptorsInitialized) return;
    debugPrint(">>> Инициализация перехватчиков Dio");

    if (!kIsWeb) {
      _dio.interceptors.add(CookieManager(cookieJar));
    }

    _dio.interceptors.add(LogInterceptor(
      request: true,
      requestBody: true,
      responseBody: true,
      responseHeader: false,
      error: true,
    ));

    _dio.interceptors.add(InterceptorsWrapper(
      onError: (DioException e, ErrorInterceptorHandler handler) {
        if (e.response?.statusCode == 401) {
          /// TODO перенаправить на страницу логина
          debugPrint("401 Unauthorized — пользователь неавторизован");
        }
        return handler.next(e);
      },
    ));

    _interceptorsInitialized = true;
  }

  /// Проверка аутентификацию
  static Future<bool> checkAuth() async {
    if (useMock) {
      await Future.delayed(const Duration(seconds: 1));
      return isAuthenticatedMock;
    }

    try {
      await _dio.get("/users/check_auth");
      return true;
    } catch (e) {
      return false;
    }
  }

  /// Логин пользователя
  static Future<void> login(String username, String password) async {
    if (useMock) {
      await Future.delayed(const Duration(seconds: 1));
      isAuthenticatedMock = true;
      return;
    }

    final fcmToken = await FirebaseMessaging.instance.getToken();

    final response = await _dio.post("/users/login", data: {
      "username": username,
      "password": password,
      "fcm_token": fcmToken,
    });

    if (response.statusCode != 200) {
      throw Exception("Login failed with status code ${response.statusCode}");
    }
  }

  /// Регистрация пользователя
  static Future<void> register(
      String name, String surname, String email, String password) async {
    if (useMock) {
      await Future.delayed(const Duration(seconds: 1));
      return;
    }

    final fcmToken = await FirebaseMessaging.instance.getToken();

    await _dio.post(
      "/users/register",
      data: {
        "name": name,
        "surname": surname,
        "email": email,
        "password": password,
        "fcm_token": fcmToken,
      },
    );
  }

  static Future<void> logout() async {
    if (useMock) {
      await Future.delayed(const Duration(seconds: 1));
      isAuthenticatedMock = false;
      return;
    }

    final fcmToken = await FirebaseMessaging.instance.getToken();

    try {
      await PushNotificationService().deleteFcmToken(fcmToken);
      await _dio.post("/users/logout");
      debugPrint("Логаут завершён успешно");
    } catch (e) {
      debugPrint("Ошибка при выходе: $e");
    }
  }

  /// Получение списка статей
  static Future<List<Article>> fetchDocuments({
    required Map<String, dynamic> searchParams,
  }) async {
    final response = await _dio.post("/documents/search", data: searchParams);

    final List<dynamic> docsJson = response.data['documents'];
    return docsJson.map((json) => Article.fromJson(json)).toList();
  }

  /// Добавление статьи в избранное
  static Future<void> addToFavorites(int articleId) async {
    await _dio.post("/documents/$articleId/add_to_favourites");
  }

  /// Удаление статьи из избранного
  static Future<void> removeFromFavorites(int articleId) async {
    await _dio.post("/documents/$articleId/remove_from_favourites");
  }

  /// Получение избранных статей
  static Future<Article> getDocumentById(int id) async {
    final response = await _dio.get("/documents/$id");
    return Article.fromJson(response.data);
  }

  /// Получение списка комментариев к статье
  static Future<List<Note>> fetchComments(int docId) async {
    final response = await _dio.get("/documents/$docId/comment");
    final commentsJson = response.data['comments'] as List<dynamic>;

    return commentsJson.map((json) {
      return Note(
        id: json['id'],
        text: json['text'],
        articleId: docId,
        createdAt: json['created_at'],
        updatedAt: json['updated_at'],
      );
    }).toList();
  }

  /// Добавление комментария к статье
  static Future<Note> addComment(int docId, String text) async {
    final response = await _dio.post("/documents/$docId/comment", data: {
      "text": text,
    });

    final json = response.data;
    return Note(
      id: json['id'],
      text: json['text'],
      articleId: docId,
      createdAt: json['created_at'],
      updatedAt: json['updated_at'],
    );
  }

  /// Обновление комментария
  static Future<void> updateComment(
      int docId, int commentId, String text) async {
    await _dio.put("/documents/$docId/comment/$commentId", data: {
      "text": text,
    });
  }

  /// Удаление комментария
  static Future<void> deleteComment(int docId, int commentId) async {
    await _dio.delete("/documents/$docId/comment/$commentId");
  }

  /// Получение списка подписок
  static Future<List<Subscription>> fetchAvailableSubscriptions() async {
    final response = await _dio.get("/subscriptions/available");
    final List<dynamic> jsonList = response.data['digest_subscriptions'];

    return jsonList.map((json) => Subscription.fromJson(json)).toList();
  }

  /// Получение списка подписок пользователя
  static Future<List<Digest>> fetchDigests({
    required int pageNumber,
    required int matchesPerPage,
  }) async {
    final response = await _dio.get(
      "/digests",
      queryParameters: {
        "next_page": pageNumber,
        "matches_per_page": matchesPerPage,
      },
    );
    final List<dynamic> jsonList = response.data['digests'];
    return jsonList.map((json) => Digest.fromJson(json)).toList();
  }

  /// Создание подписки
  static Future<void> createSubscription({
    required String title,
    required String description,
    required List<String> sources,
    required List<String> tags,
    required List<String> followers,
    required SubscribeOptions subscribeOptions,
    required bool isPublic,
  }) async {
    final data = {
      "title": title,
      "description": description,
      "sources": sources,
      "tags": tags,
      "followers": followers,
      "subscribe_options": {
        "send_to_mail": subscribeOptions.sendToMail,
        "mobile_notifications": subscribeOptions.mobileNotifications,
      },
      "public": isPublic,
    };

    await _dio.post("/subscriptions/create", data: data);
  }

  /// Получение пользовательского email
  static Future<String> getCurrentUserEmail() async {
    final response = await _dio.get("/users/get_email");
    return response.data['email'];
  }

  /// Получение информации о настройках пользователя
  static Future<Map<String, dynamic>> getUserSubscriptionSettings(
      String email) async {
    final response = await _dio.post(
      "/users/subscription_settings/check",
      data: {"username": email},
    );
    return response.data;
  }

  /// Смена пароля
  static Future<void> changePassword(
      String oldPassword, String newPassword) async {
    await _dio.patch(
      "/users/change/password",
      data: {
        "old_password": oldPassword,
        "new_password": newPassword,
      },
    );
  }

  /// Смена email пользователя
  static Future<void> changeEmail(String newEmail) async {
    await _dio.patch(
      "/users/change/email",
      data: {
        "new_email": newEmail,
      },
    );
  }

  /// Поиск по дайджестам
  static Future<List<Digest>> searchDigests({
    required DigestSearchParams searchParams,
    required int pageNumber,
    required int matchesPerPage,
  }) async {
    final response = await _dio.get(
      "/digests/search",
      queryParameters:
          searchParams.toQueryParameters(pageNumber, matchesPerPage),
    );
    final List<dynamic> digestsJson = response.data['digests'] ?? [];

    return digestsJson.map((json) => Digest.fromJson(json)).toList();
  }

  /// Получение дайджеста по ID
  static Future<Digest> fetchDigestById(String digestId) async {
    final response = await _dio.get('/digests/$digestId');
    return Digest.fromJson(response.data);
  }

  /// Получение дайджестов по подписке
  static Future<Response> fetchDigestsBySubscription({
    required int subscriptionId,
    required int nextPage,
    required int matchesPerPage,
  }) async {
    return await _dio.get(
      "/subscriptions/$subscriptionId/digests",
      queryParameters: {
        "next_page": nextPage,
        "matches_per_page": matchesPerPage,
      },
    );
  }

  /// Получение подписки по ID дайджеста
  static Future<Subscription> fetchSubscriptionByDigestId(
      String digestId) async {
    final response = await _dio.get('/digests/$digestId/subscription');
    return Subscription.fromJson(response.data);
  }

  /// Проверка, является ли текущий пользователь владельцем подписки
  static Future<bool> checkOwnership(int subscriptionId) async {
    final response =
        await _dio.get('/subscriptions/$subscriptionId/check_ownership');
    return response.data['is_owner'] ?? false;
  }

  /// Получение списка подписчиков подписки
  static Future<List<String>> fetchSubscriptionFollowers(
      int subscriptionId) async {
    final response = await _dio.get('/subscriptions/$subscriptionId/followers');
    final List<dynamic> followers = response.data['followers'] ?? [];
    return followers.map((e) => e.toString()).toList();
  }

  /// Передача прав владения подпиской
  static Future<void> changeSubscriptionOwner(
      int subscriptionId, String newOwnerEmail) async {
    await _dio.patch('/subscriptions/$subscriptionId/change_owner', data: {
      "owner": newOwnerEmail,
    });
  }

  /// Получение информации о подписке по ID
  static Future<Subscription> fetchSubscriptionById(int subscriptionId) async {
    final response = await _dio.get('/subscriptions/$subscriptionId');
    return Subscription.fromJson(response.data);
  }

  /// Оценка дайджеста
  static Future<void> rateDigest(String digestId, int rating) async {
    await _dio.patch(
      '/digests/$digestId/rate',
      data: {'digest_core_rating': rating},
    );
  }

  /// Отправка FCM токена
  static Future<void> sendFcmToken(String token) async {
    try {
      await _dio.post("/users/fcm_token/create", queryParameters: {
        "fcmToken": token,
      });
    } catch (e) {
      debugPrint("Ошибка при отправке FCM токена: $e");
    }
  }

  /// Удаление FCM токена
  static Future<void> deleteFcmToken(String token) async {
    try {
      await _dio.delete("/users/fcm_token/delete", queryParameters: {
        "fcmToken": token,
      });
    } catch (e) {
      debugPrint("Ошибка при удалении FCM токена: $e");
    }
  }

  /// Обновление подписки
  static Future<void> updateSubscription(Subscription subscription) async {
    final data = {
      "title": subscription.title,
      "description": subscription.description,
      "sources": subscription.sources,
      "tags": subscription.tags,
      "followers": subscription.followers
          .map((email) => {"username": email})
          .toList(),
      "public": subscription.public,
      "subscribe_options": {
        "send_to_mail": subscription.subscribeOptions.sendToMail,
        "mobile_notifications": subscription.subscribeOptions.mobileNotifications,
      },
      "owner": subscription.owner,
    };

    await _dio.put('/subscriptions/${subscription.id}/update', data: data);
  }


  /// Проверка необходимости подтверждения email
  static Future<bool> needsEmailConfirmation() async {
    final response = await _dio.get("/users/check_verification");
    return response.data['needs_confirmation'] ?? false;
  }

  /// GET запрос к API
  static Future<Response> get(String endpoint) async {
    return await _dio.get(endpoint);
  }

  /// POST запрос к API
  static Future<Response> post(
      String endpoint, Map<String, dynamic> data) async {
    return await _dio.post(endpoint, data: data);
  }

  /// PUT запрос к API
  static Future<Response> put(String endpoint,
      {Map<String, dynamic>? data}) async {
    return await _dio.put(endpoint, data: data);
  }
}
