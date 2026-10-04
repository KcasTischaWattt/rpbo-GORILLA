# Выполнимость сборки и пределы проверок

Дата: 4 октября 2026 года, часовой пояс Europe/Moscow. Исходное состояние продукта: `640de504f232d6cd99fc1bd35ead266d4bcbb824`; source hash проверяется по [манифесту](product-manifest.json). Генерируемые `target/`, `node_modules/` и `dist/` не входят в учебный пакет и не изменяют продуктовые исходники.

## Backend

Среда: macOS arm64, Microsoft OpenJDK **21.0.11**, Maven wrapper **3.9.9**, целевая версия Java **17** из pom.xml. Из каталога `product/backend-web/backend`:

```sh
MAVEN_USER_HOME=/tmp/crosswords-maven-home sh ./mvnw -B \
  -Dmaven.repo.local=/tmp/crosswords-maven-repo -DskipTests package
```

Результат: `BUILD SUCCESS`, JAR собран. [Полный журнал](backend-build.log.txt). Передача `-DskipTests` сознательна для этой проверки: требуется сборка без неизвестного production-окружения, а единственный `@SpringBootTest` зависит от внешних настроек. Это **не evidence прошедших тестов**. Нельзя переносить такой пропуск в готовность К2/К3 без выбранных поведенческих проверок.

Maven wrapper-файл в checkout не исполняемый; команда `sh ./mvnw` сохраняет исходник и режим. `sh mvnw` без `./` не подходит конкретному wrapper и не используется в воспроизводимой инструкции. JDK 21 компилирует с target 17; запуск на JDK 17 отдельно не проверен.

## Web

Среда: Node **26.5.0**, npm **11.17.0**, Quasar/Vite из package-lock.json. Из каталога `product/backend-web/frontend`:

```sh
npm ci --ignore-scripts --cache /tmp/crosswords-npm-cache --no-audit --no-fund
npm exec -- quasar prepare
npm run build
```

Результат: production SPA собрана в `dist/spa`. [Журнал сборки](web-build.log.txt). Есть предупреждения о chunk size и стороннем Recogito; сборка завершена успешно. Это не проверка поведения браузера, API, CSRF, заметок или web-тестов. `--no-audit` не означает, что зависимости признаны безопасными: анализ зависимости входит в будущий выбор методов по S7.

## Mobile и совместный runtime

Flutter SDK не найден через командный PATH; мобильная сборка не выполнялась. Сохранены код, pubspec и lock; Firebase-файл исключён при переносе. Полный backend runtime требует PostgreSQL/OpenSearch и настроек интеграций. Docker CLI доступен, но runtime в этой проверке не запускался. Совместный запуск всех компонентов, реальные push/SMTP/Telegram и актуальные сайты не проверялись.

Это ограничения полученного evidence, а не доказательство неисправности компонентов. Для M1/M2 предусмотрены изолированные product-code тесты и локальные заменители зависимостей. Если потребуется полный мобильный прогон, отдельно получить разрешённую тестовую Firebase-конфигурацию и Flutter SDK, записать версии и scope; результат не выводить из наличия платформенных директорий.

## Ограниченная поведенческая probe классификатора

```sh
python3 ek1/tools/probe_pipeline.py
```

Запускаются неизменённые AST-тела `send_to_backend` и `main` из исходного файла. KafkaConsumer, HTTP и LLM заменены in-memory объектами; один синтетический backend-отказ приводит к одному `commit()`. [Машиночитаемый результат](pipeline-probe.json). Не проверены импорт/startup всего classifier, реальные Kafka offsets, сеть, перезапуск и устойчивое хранение. Python-версия фактического запуска записана в [среде](environment.json).
