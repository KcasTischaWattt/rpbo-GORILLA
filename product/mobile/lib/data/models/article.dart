class Article {
  final int id;
  final String title;
  final String source;
  final String summary;
  final String text;
  final List<String> tags;
  final String date;
  final bool favorite;
  final String language;
  final String url;

  Article({
    required this.id,
    required this.title,
    required this.source,
    required this.summary,
    required this.text,
    required this.tags,
    required this.date,
    required this.favorite,
    required this.language,
    required this.url,
  });

  factory Article.fromJson(Map<String, dynamic> json) {
    return Article(
      id: json['id'],
      title: json['title'],
      source: json['source'],
      summary: json['summary'],
      text: json['text'],
      tags: List<String>.from(json['tags'] ?? []),
      date: json['date'],
      favorite: json['favourite'] ?? false,
      language: json['language'],
      url: json['URL'],
    );
  }

  Article copyWith({
    int? id,
    String? title,
    String? source,
    String? summary,
    String? text,
    List<String>? tags,
    String? date,
    bool? favorite,
    String? language,
    String? url,
  }) {
    return Article(
      id: id ?? this.id,
      title: title ?? this.title,
      source: source ?? this.source,
      summary: summary ?? this.summary,
      text: text ?? this.text,
      tags: tags ?? this.tags,
      date: date ?? this.date,
      favorite: favorite ?? this.favorite,
      language: language ?? this.language,
      url: url ?? this.url,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'title': title,
      'source': source,
      'summary': summary,
      'text': text,
      'tags': tags,
      'date': date,
      'favorite': favorite,
      'language': language,
      'URL': url,
    };
  }
}
