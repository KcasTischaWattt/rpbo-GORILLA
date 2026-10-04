# Краткие результаты исходных проверок

Проверки выполнены 4 октября 2026 года на macOS arm64 для baseline 640de504f232d6cd99fc1bd35ead266d4bcbb824. Исходники продукта не менялись.

| Компонент | Среда | Результат и ограничение |
| --- | --- | --- |
| Backend | OpenJDK 21.0.11, Maven 3.9.9, target Java 17 | JAR собран. Тесты пропущены через -DskipTests, работа сервисов не проверялась. |
| Web | Node 26.5.0, npm 11.17.0 | SPA собрана. Были предупреждения о размере частей пакета и Recogito; браузер и API не проверялись. |
| Mobile | Flutter SDK отсутствовал | Сборка не выполнялась. |
| Все компоненты вместе | Нужны хранилища и настройки интеграций | Совместный запуск и реальные уведомления не проверялись. |

Для повторения backend-сборки из product/backend-web/backend:

```sh
MAVEN_USER_HOME=/tmp/crosswords-maven-home sh ./mvnw -B \
  -Dmaven.repo.local=/tmp/crosswords-maven-repo -DskipTests package
```

Для повторения web-сборки из product/backend-web/frontend:

```sh
npm ci --ignore-scripts --cache /tmp/crosswords-npm-cache --no-audit --no-fund
npm exec -- quasar prepare
npm run build
```

Локальная проверка классификатора запускается из корня командой python3 ek1/tools/probe_pipeline.py. В исходном запуске на Python 3.9.6 после одной ошибки отправки произошёл один вызов commit(). Сохранённый результат - pipeline-probe.json. Настоящие Kafka, HTTP и LLM заменены тестовыми объектами; потеря сообщения в брокере и поведение после перезапуска не проверялись.
