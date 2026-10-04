import 'dart:collection';
import 'package:crosswords/data/constants/tag_icons.dart';

final List<String> _sources = [
  'Коммерсантъ',
  'Интерфакс',
  'Центробанк Узбекистан',
  'Центробанк Таджикистан',
  'Центробанк Кыргызстан',
  'Центробанк Азербайджан',
  // 'Центробанк Грузии',
  // 'Центробанк РФ'
];

final List<String> _tags = (tagIcons.keys.toList()..sort());

final UnmodifiableListView<String> defaultSources =
    UnmodifiableListView(_sources);
final UnmodifiableListView<String> defaultTags = UnmodifiableListView(_tags);
