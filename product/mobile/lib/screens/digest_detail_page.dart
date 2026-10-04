import 'package:flutter/material.dart';
import '../providers/digest_provider.dart';
import '../data/models/digest.dart';
import 'package:provider/provider.dart';
import '../providers/subscription_provider.dart';
import 'digest_edit_page.dart';
import 'widgets/item_chips_list_widget.dart';
import 'widgets/custom_expansion_tile_widget.dart';

class DigestDetailPage extends StatefulWidget {
  final Digest digest;

  const DigestDetailPage({super.key, required this.digest});

  @override
  _DigestDetailPageState createState() => _DigestDetailPageState();
}

class _DigestDetailPageState extends State<DigestDetailPage> {
  late Digest _digest;

  @override
  void initState() {
    super.initState();
    _digest = widget.digest;
  }

  void _showSettingsMenu(BuildContext context) {
    final bool isOwner = _digest.isOwner;
    final bool isSubscribed = _digest.subscribeOptions.subscribed;

    final menuItems = <Map<String, dynamic>>[];

    if (isOwner) {
      menuItems.add({
        'icons': Icons.edit,
        'text': "Редактировать",
        'action': () async {
          Navigator.pop(context);

          final subscriptionProvider =
              Provider.of<SubscriptionProvider>(context, listen: false);

          showDialog(
            context: context,
            barrierDismissible: false,
            builder: (context) {
              return const Center(
                child: CircularProgressIndicator(),
              );
            },
          );

          try {
            final subscription = await subscriptionProvider
                .fetchSubscriptionByDigestId(_digest.id);
            if (!mounted) return;

            Navigator.pop(context);

            Navigator.push(
              context,
              MaterialPageRoute(
                builder: (context) =>
                    DigestEditPage(subscription: subscription),
              ),
            );
          } catch (e) {
            if (!mounted) return;

            Navigator.pop(context);

            ScaffoldMessenger.of(context).showSnackBar(
              SnackBar(content: Text('Ошибка загрузки подписки: $e')),
            );
          }
        }
      });
    }

    if (isSubscribed) {
      menuItems.add({
        'icons': Icons.notifications,
        'text': "Настройка уведомлений",
        'action': () {
          Navigator.pop(context);
          _showNotificationSettingsDialog(context, _digest);
        }
      });
    }

    menuItems.add({
      'icons': isSubscribed ? Icons.unsubscribe : Icons.subscriptions,
      'text': isSubscribed ? "Отписаться" : "Подписаться",
      'action': () async {
        Navigator.pop(context);
        final subscriptionProvider =
            Provider.of<SubscriptionProvider>(context, listen: false);
        final success = await subscriptionProvider.handleUnsubscribe(
          context: context,
          digestId: _digest.id,
        );
        if (!isSubscribed && success) {
          setState(() {
            _digest = _digest.copyWith(
              subscribeOptions: _digest.subscribeOptions.copyWith(
                subscribed: true,
              ),
            );
          });
        } else if (isSubscribed && success) {
          Navigator.pop(context);
        } else {
          ScaffoldMessenger.of(context).showSnackBar(
            const SnackBar(content: Text('Ошибка при подписке')),
          );
        }
      },
    });

    showModalBottomSheet(
      context: context,
      backgroundColor: Colors.transparent,
      isScrollControlled: true,
      builder: (BuildContext bottomSheetContext) {
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
            children: menuItems.map((item) {
              return ListTile(
                leading: Icon(item['icons'] as IconData, size: 28),
                title: Text(item['text'] as String,
                    style: TextStyle(fontSize: 20)),
                onTap: item['action'] as VoidCallback,
              );
            }).toList(),
          ),
        );
      },
    );
  }

  void _showNotificationSettingsDialog(BuildContext context, Digest digest) {
    bool mobileNotifications = digest.subscribeOptions.mobileNotifications;
    bool emailNotifications = digest.subscribeOptions.sendToMail;

    showDialog(
      context: context,
      builder: (BuildContext dialogContext) {
        return StatefulBuilder(
          builder: (context, setState) {
            return AlertDialog(
              title: const Text("Настройки уведомлений"),
              content: Column(
                mainAxisSize: MainAxisSize.min,
                children: [
                  CheckboxListTile(
                    title: const Text("Мобильные уведомления"),
                    value: mobileNotifications,
                    onChanged: (bool? value) {
                      setState(() {
                        mobileNotifications = value ?? false;
                      });
                    },
                  ),
                  CheckboxListTile(
                    title: const Text("Уведомления на почту"),
                    value: emailNotifications,
                    onChanged: (bool? value) {
                      setState(() {
                        emailNotifications = value ?? false;
                      });
                    },
                  ),
                ],
              ),
              actions: [
                TextButton(
                  onPressed: () => Navigator.pop(dialogContext),
                  child: const Text("Отмена"),
                ),
                TextButton(
                  onPressed: () async {
                    Navigator.pop(dialogContext);

                    final subscriptionProvider =
                        Provider.of<SubscriptionProvider>(context,
                            listen: false);
                    final digestProvider =
                        Provider.of<DigestProvider>(context, listen: false);

                    try {
                      final subscription = await subscriptionProvider
                          .fetchSubscriptionByDigestId(digest.id);

                      final updatedSubscription = subscription.copyWith(
                        subscribeOptions:
                            subscription.subscribeOptions.copyWith(
                          mobileNotifications: mobileNotifications,
                          sendToMail: emailNotifications,
                        ),
                      );

                      await subscriptionProvider
                          .updateSubscriptionSettings(updatedSubscription);
                      setState(() {
                        _digest = _digest.copyWith(
                          subscribeOptions: _digest.subscribeOptions.copyWith(
                            mobileNotifications: mobileNotifications,
                            sendToMail: emailNotifications,
                          ),
                        );
                      });

                      final updatedDigest = _digest.copyWith(
                        subscribeOptions: _digest.subscribeOptions.copyWith(
                          mobileNotifications: mobileNotifications,
                          sendToMail: emailNotifications,
                        ),
                      );
                      digestProvider.updateDigest(updatedDigest);
                      if (mounted) {
                        ScaffoldMessenger.of(context).showSnackBar(
                          const SnackBar(
                              content: Text('Настройки уведомлений сохранены')),
                        );
                      }
                    } catch (e) {
                      if (mounted) {
                        ScaffoldMessenger.of(context).showSnackBar(
                          SnackBar(
                              content: Text('Ошибка сохранения настроек: $e')),
                        );
                      }
                    }
                  },
                  child: const Text("Применить"),
                ),
              ],
            );
          },
        );
      },
    );
  }

  AppBar _buildAppBar() {
    return AppBar(
      toolbarHeight: 60,
      centerTitle: true,
      title: Text(
        _digest.title,
        style: TextStyle(fontSize: 24, fontWeight: FontWeight.bold),
      ),
      actions: [
        IconButton(
          icon: const Icon(Icons.settings, size: 24),
          onPressed: () => _showSettingsMenu(context),
        ),
      ],
      backgroundColor: Theme.of(context).scaffoldBackgroundColor,
      surfaceTintColor: Colors.transparent,
      elevation: 0,
      shadowColor: Colors.transparent,
    );
  }

  Widget _buildRatingExpansionTile(BuildContext context) {
    final digestProvider = Provider.of<DigestProvider>(context, listen: false);

    return CustomExpansionTile(
      title: "Оцените качество дайджеста",
      customContent: Row(
        mainAxisAlignment: MainAxisAlignment.center,
        children: List.generate(5, (index) {
          final isRated =
              _digest.userRating != null && index < _digest.userRating!;

          return IconButton(
            icon: Icon(
              isRated ? Icons.star : Icons.star_border,
              color: Theme.of(context).primaryColor,
            ),
            onPressed: () async {
              final rating = index + 1;
              try {
                await digestProvider.rateDigest(_digest, rating);
                setState(() {
                  _digest = _digest.copyWith(userRating: rating);
                });

                if (mounted) {
                  ScaffoldMessenger.of(context).showSnackBar(
                    SnackBar(content: Text('Оценка $rating сохранена!')),
                  );
                }
              } catch (e) {
                if (mounted) {
                  ScaffoldMessenger.of(context).showSnackBar(
                    SnackBar(content: Text('Ошибка при оценке: $e')),
                  );
                }
              }
            },
          );
        }),
      ),
      children: [],
    );
  }

  Widget _buildDateAndOwner() {
    return LayoutBuilder(
      builder: (context, constraints) {
        final textStyle = DefaultTextStyle.of(context).style;
        final dateText = _digest.date;
        final ownerText = _digest.owner;
        final separator = " | ";

        final fullText = "$dateText$separator$ownerText";

        final textPainter = TextPainter(
          text: TextSpan(text: fullText, style: textStyle),
          maxLines: 1,
          textDirection: TextDirection.ltr,
        )..layout(maxWidth: constraints.maxWidth);

        final fitsInOneLine = textPainter.didExceedMaxLines == false;

        return Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            if (fitsInOneLine)
              Row(
                mainAxisSize: MainAxisSize.min,
                children: [
                  Text(fullText),
                  if (_digest.isOwner) Icon(Icons.workspace_premium, size: 16),
                ],
              )
            else ...[
              Text(dateText),
              Row(
                mainAxisSize: MainAxisSize.min,
                children: [
                  if (_digest.isOwner) Icon(Icons.workspace_premium, size: 16),
                  Expanded(
                    child: Text(
                      ownerText,
                      overflow: TextOverflow.ellipsis,
                      maxLines: 1,
                    ),
                  ),
                ],
              ),
            ],
          ],
        );
      },
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: _buildAppBar(),
      body: Padding(
        padding: const EdgeInsets.all(12.0),
        child: SingleChildScrollView(
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              // Дата и владелец
              _buildDateAndOwner(),
              const SizedBox(height: 8),

              // Название дайджеста
              Text(
                _digest.title,
                style: TextStyle(fontSize: 24, fontWeight: FontWeight.bold),
              ),
              const SizedBox(height: 8),

              // Описание дайджеста
              Text(_digest.description,
                  style: Theme.of(context).textTheme.bodyMedium),
              const SizedBox(height: 16),

              // Теги
              ItemListWidget(
                items: _digest.tags,
                dialogTitle: "Все теги",
                chipColor: Theme.of(context).primaryColor,
                textColor: Colors.black,
                fontWeight: FontWeight.bold,
              ),
              const SizedBox(height: 16),

              // Контент дайджеста
              Text(_digest.text),
              const SizedBox(height: 16),

              // Источники
              ItemListWidget(
                items: _digest.sources,
                dialogTitle: "Все источники",
                chipColor: Theme.of(context).secondaryHeaderColor,
                textColor: Colors.white,
                fontWeight: FontWeight.normal,
              ),
              const SizedBox(height: 16),

              // Аккордеон "Оцените качество дайджеста"
              _buildRatingExpansionTile(context),
              const SizedBox(height: 16),
            ],
          ),
        ),
      ),
    );
  }
}
