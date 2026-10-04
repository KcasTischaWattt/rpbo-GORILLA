import 'package:flutter/material.dart';

class ExitConfirmationHandler extends StatelessWidget {
  final Widget child;
  final Future<bool> Function(BuildContext) onExitRequested;
  final VoidCallback? onExitConfirmed;

  const ExitConfirmationHandler({
    super.key,
    required this.child,
    required this.onExitRequested,
    this.onExitConfirmed,
  });

  Future<bool> _showExitConfirmationDialog(BuildContext context) async {
    return await showDialog(
          context: context,
          builder: (context) => AlertDialog(
            title: const Text("Подтверждение выхода"),
            content: const Text(
                "Вы уверены, что хотите выйти? Все несохраненные данные будут потеряны."),
            actions: [
              TextButton(
                onPressed: () => Navigator.of(context).pop(false),
                child: const Text("Остаться"),
              ),
              TextButton(
                onPressed: () {
                  if (onExitConfirmed != null) {
                    onExitConfirmed!();
                  }
                  Navigator.of(context).pop(true);
                },
                child: const Text("Выйти", style: TextStyle(color: Colors.red)),
              ),
            ],
          ),
        ) ??
        false;
  }

  @override
  Widget build(BuildContext context) {
    return PopScope(
      canPop: false,
      onPopInvokedWithResult: (didPop, result) async {
        if (didPop) return;
        bool shouldExit = await _showExitConfirmationDialog(context);
        if (!context.mounted) return;
        if (shouldExit) {
          if (onExitConfirmed != null) {
            onExitConfirmed!();
          }
          Navigator.of(context).pop();
        }
      },
      child: child,
    );
  }
}
