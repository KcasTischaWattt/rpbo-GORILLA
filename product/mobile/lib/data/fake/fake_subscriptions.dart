import '../models/subscription.dart';
import '../models/subscribe_options.dart';

List<Subscription> fakeSubscriptions = [
  Subscription(
    id: 1,
    title: 'Главное о спорте',
    description: "самое важно о мире спорта",
    sources: ['Интерфакс', 'Коммерсантъ'],
    tags: ['Спорт', 'Футбол'],
    subscribeOptions: SubscribeOptions(
      subscribed: true,
      sendToMail: true,
      mobileNotifications: false,
    ),
    creationDate: '01/02/2025',
    public: true,
    owner: "abc@example.com",
    isOwner: false,
    followers: [
      "abc@gmail.com",
      "mylogin",
      "abcd@gmail.com",
      "abcde@gmail.com"
    ],
  ),
  Subscription(
    id: 2,
    title: 'Из мира еды',
    description: "самое вкусное",
    sources: ['Интерфакс', 'Коммерсантъ'],
    tags: ['Зарплаты', 'Экономика'],
    subscribeOptions: SubscribeOptions(
      subscribed: true,
      sendToMail: true,
      mobileNotifications: false,
    ),
    creationDate: '01/02/2025',
    public: false,
    owner: "someone@example.com",
    isOwner: true,
    followers: [
      "abc@gmail.com",
      "mylogin",
      "abcd@gmail.com",
      "abcde@gmail.com"
    ],
  ),
  Subscription(
    id: 3,
    title: 'Политика',
    description:
    "Another critical feature of the Smart Media Text Corpus is its annotation capability, allowing users to mark sections of articles, add notes, and categorize information.",
    sources: ['Интерфакс', 'Коммерсантъ'],
    tags: ['Политика', 'Экономика'],
    subscribeOptions: SubscribeOptions(
      subscribed: true,
      sendToMail: true,
      mobileNotifications: false,
    ),
    creationDate: '01/02/2025',
    public: false,
    owner: "someone@example.com",
    isOwner: true,
    followers: [
      "abc@gmail.com",
      "mylogin",
      "abcd@gmail.com",
      "abcde@gmail.com"
    ],
  ),
  Subscription(
    id: 4,
    title: 'Экономика России',
    description: "Анализ экономической ситуации в стране",
    sources: ['РБК', 'Коммерсантъ', 'ЦБ РФ'],
    tags: ['Экономика', 'СПБ', 'Кредит'],
    subscribeOptions: SubscribeOptions(
      subscribed: true,
      sendToMail: true,
      mobileNotifications: true,
    ),
    creationDate: '01/02/2025',
    public: true,
    owner: "economist@example.com",
    isOwner: false,
    followers: [
      "abc@gmail.com",
      "mylogin",
      "abcd@gmail.com",
      "abcde@gmail.com"
    ],
  ),
  Subscription(
    id: 5,
    title: 'Трансграничные переводы',
    description: "Всё о переводах в страны СНГ",
    sources: ['ЦБ Узбекистана', 'ЦБ Киргизии', 'ЦБ Азербайджана'],
    tags: ['Кредит', 'Экономика'],
    subscribeOptions: SubscribeOptions(
      subscribed: false,
      sendToMail: false,
      mobileNotifications: false,
    ),
    creationDate: '01/02/2025',
    public: false,
    owner: "perevod@example.com",
    isOwner: false,
    followers: [
      "abc@gmail.com",
      "mylogin",
      "abcd@gmail.com",
      "abcde@gmail.com"
    ],
  ),
  Subscription(
    id: 6,
    title: 'Глубокий анализ современных тенденций в науке, технологиях и инновациях',
    description: "Детальный разбор последних исследований, научных открытий и инновационных технологий.",
    sources: ['Наука и Жизнь', 'Популярная механика', 'MIT Technology Review'],
    tags: ['Технологии', 'Экономика', 'СБП', 'Кибербезопасность'],
    subscribeOptions: SubscribeOptions(
      subscribed: true,
      sendToMail: false,
      mobileNotifications: true,
    ),
    creationDate: '01/02/2025',
    public: true,
    owner: "scientist@example.com",
    isOwner: true,
    followers: [
      "abc@gmail.com",
      "mylogin",
      "abcd@gmail.com",
      "abcde@gmail.com"
    ],
  ),
];