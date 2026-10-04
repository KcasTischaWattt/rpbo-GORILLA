import 'package:flutter/material.dart';

abstract class FilterProvider with ChangeNotifier {
  List<String> get sources;
  List<String> get selectedSources;
  void toggleSource(String source);

  List<String> get tags;
  List<String> get selectedTags;
  void toggleTag(String tag);
}