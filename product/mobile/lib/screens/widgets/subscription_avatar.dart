import 'package:crosswords/data/constants/tag_icons.dart';
import 'package:crosswords/data/models/subscription.dart';
import 'package:flutter/material.dart';

class SubscriptionAvatar extends StatelessWidget {
  final Subscription subscription;
  final double radius;

  const SubscriptionAvatar({
    super.key,
    required this.subscription,
    this.radius = 25,
  });

  @override
  Widget build(BuildContext context) {
    final firstTag = subscription.tags.isNotEmpty ? subscription.tags.first : null;

    if (firstTag == null || !tagIcons.containsKey(firstTag)) {
      return CircleAvatar(
        radius: radius,
        backgroundColor: Colors.grey[300],
        child: Icon(Icons.person, size: radius, color: Colors.grey[600]),
      );
    }

    return CircleAvatar(
      radius: radius,
      backgroundColor: Colors.white,
      backgroundImage: AssetImage(tagIcons[firstTag]!),
    );
  }
}