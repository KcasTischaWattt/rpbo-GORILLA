class SubscribeOptions {
  final bool subscribed;
  final bool sendToMail;
  final bool mobileNotifications;

  SubscribeOptions({
    required this.subscribed,
    required this.sendToMail,
    required this.mobileNotifications,
  });

  SubscribeOptions copyWith({
    bool? subscribed,
    bool? sendToMail,
    bool? mobileNotifications,
  }) {
    return SubscribeOptions(
      subscribed: subscribed ?? this.subscribed,
      sendToMail: sendToMail ?? this.sendToMail,
      mobileNotifications: mobileNotifications ?? this.mobileNotifications,
    );
  }

  factory SubscribeOptions.fromJson(Map<String, dynamic> json) {
    return SubscribeOptions(
      subscribed: json['subscribed'] ?? false,
      sendToMail: json['send_to_mail'] ?? false,
      mobileNotifications: json['mobile_notifications'] ?? false,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'subscribed': subscribed,
      'send_to_mail': sendToMail,
      'mobile_notifications': mobileNotifications,
    };
  }
}
