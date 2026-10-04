import 'package:flutter/material.dart';

class FadeBackground extends StatefulWidget {
  final Widget child;
  const FadeBackground({required this.child, Key? key}) : super(key: key);

  @override
  _FadeBackgroundState createState() => _FadeBackgroundState();
}

class _FadeBackgroundState extends State<FadeBackground> with SingleTickerProviderStateMixin {
  late AnimationController _controller;
  late Animation<double> _opacityAnimation;

  @override
  void initState() {
    super.initState();
    _controller = AnimationController(
      vsync: this,
      duration: const Duration(milliseconds: 250),
    );
    _opacityAnimation = Tween<double>(begin: 0, end: 1).animate(_controller);
    _controller.forward();
  }

  @override
  void dispose() {
    _controller.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return AnimatedBuilder(
      animation: _opacityAnimation,
      builder: (context, child) {
        return GestureDetector(
          onTap: () => Navigator.pop(context),
          child: Container(
            color: Colors.black.withOpacity(_opacityAnimation.value * 0.5),
            child: widget.child,
          ),
        );
      },
    );
  }
}
