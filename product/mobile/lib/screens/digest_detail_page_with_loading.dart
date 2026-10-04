import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../providers/digest_provider.dart';
import '../data/models/digest.dart';
import 'digest_detail_page.dart';

class DigestDetailPageWithLoading extends StatefulWidget {
  final String digestId;

  const DigestDetailPageWithLoading({super.key, required this.digestId});

  @override
  State<DigestDetailPageWithLoading> createState() =>
      _DigestDetailPageWithLoadingState();
}

class _DigestDetailPageWithLoadingState
    extends State<DigestDetailPageWithLoading> {
  Digest? _digest;
  bool _isLoading = true;
  String? _error;

  @override
  void initState() {
    super.initState();
    _loadDigest();
  }

  Future<void> _loadDigest() async {
    try {
      final provider = Provider.of<DigestProvider>(context, listen: false);
      final loaded = await provider.loadDigestById(widget.digestId);
      if (!mounted) return;
      setState(() {
        _digest = loaded;
        _isLoading = false;
      });
    } catch (e) {
      setState(() {
        _error = e.toString();
        _isLoading = false;
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    if (_isLoading) {
      return const Scaffold(
        body: Center(child: CircularProgressIndicator()),
      );
    }

    if (_error != null) {
      return Scaffold(
        appBar: AppBar(title: const Text("Ошибка")),
        body: Center(child: Text("Ошибка загрузки: $_error")),
      );
    }

    return DigestDetailPage(digest: _digest!);
  }
}
