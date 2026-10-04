import 'package:flutter/material.dart';

class ActionButtons extends StatelessWidget {
  final VoidCallback onPrimaryPressed;
  final VoidCallback onSecondaryPressed;
  final String primaryText;
  final String secondaryText;
  final IconData? primaryIcon;
  final bool isLoading;

  const ActionButtons({
    super.key,
    required this.onPrimaryPressed,
    required this.onSecondaryPressed,
    required this.primaryText,
    required this.secondaryText,
    this.primaryIcon,
    this.isLoading = false,
  });

  @override
  Widget build(BuildContext context) {
    return Row(
      children: [
        Expanded(
          child: ElevatedButton(
            style: ElevatedButton.styleFrom(
              backgroundColor: Theme.of(context).primaryColor,
              shape: RoundedRectangleBorder(
                  borderRadius: BorderRadius.circular(8)),
              padding: const EdgeInsets.symmetric(vertical: 12),
            ),
            onPressed: isLoading ? null : onPrimaryPressed,
            child: Row(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                if (isLoading) ...[
                  const SizedBox(
                    width: 18,
                    height: 18,
                    child: CircularProgressIndicator(
                      strokeWidth: 2,
                      color: Colors.black,
                    ),
                  ),
                  const SizedBox(width: 8),
                ] else if (primaryIcon != null) ...[
                  Icon(primaryIcon, color: Colors.black),
                  const SizedBox(width: 8),
                ],
                Text(
                  primaryText,
                  style: const TextStyle(fontSize: 18, color: Colors.black),
                ),
              ],
            ),
          ),
        ),
        const SizedBox(width: 12),
        TextButton(
          onPressed: onSecondaryPressed,
          child: Text(secondaryText, style: const TextStyle(fontSize: 16)),
        ),
      ],
    );
  }
}
