import 'package:flutter/material.dart';

class LoadingRefreshButton extends StatefulWidget {
  final Future<void> Function() onRefresh;
  final bool isDisabled;

  const LoadingRefreshButton({
    super.key,
    required this.onRefresh,
    this.isDisabled = false,
  });

  @override
  State<LoadingRefreshButton> createState() => _LoadingRefreshButtonState();
}

class _LoadingRefreshButtonState extends State<LoadingRefreshButton>
    with SingleTickerProviderStateMixin {
  late final AnimationController _rotationController;

  @override
  void initState() {
    super.initState();
    _rotationController = AnimationController(
      vsync: this,
      duration: const Duration(seconds: 1),
    );

    _rotationController.addStatusListener((status) {
      if (status == AnimationStatus.completed) {
        _rotationController.repeat();
      }
    });
  }

  @override
  void dispose() {
    _rotationController.dispose();
    super.dispose();
  }

  Future<void> _handleRefresh() async {
    if (widget.isDisabled) return;

    _rotationController.repeat();
    try {
      await widget.onRefresh();
    } finally {
      _rotationController.stop();
      await Future.delayed(const Duration(milliseconds: 200));
      _rotationController.reset();
    }
  }

  @override
  Widget build(BuildContext context) {
    return IconButton(
      icon: RotationTransition(
        turns: _rotationController,
        child: const Icon(Icons.refresh),
      ),
      onPressed: widget.isDisabled ? null : _handleRefresh,
    );
  }
}
