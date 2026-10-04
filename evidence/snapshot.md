# Snapshot Crosswords

Дата подготовки: 2026-10-04.

Baseline — HEAD ветки по умолчанию каждого источника на момент клонирования, до курсовых изменений в этом учебном snapshot. Это не попытка восстановить состояние на неизвестную историческую дату начала курса. История источников не импортируется и не меняется.

| Компонент | Canonical repository | Ветка | Baseline SHA | Файлов перенесено |
|---|---|---|---|---:|
| `backend-web` | [CehhGhost/Crosswords](https://github.com/CehhGhost/Crosswords) | `main` | `ad9546b4421cfccd8474a3d39b6a7a91a6174a7f` | 304 |
| `mobile` | [KcasTischaWattt/crosswords-mobile-app](https://github.com/KcasTischaWattt/crosswords-mobile-app) | `master` | `fab8dccfbf0a123662ff1847aa83a8e769356bfd` | 206 |
| `classifier` | [mperestoronin/media-corpus-classifier](https://github.com/mperestoronin/media-corpus-classifier) | `main` | `607dbeae250685bbc594eea2353065bdd343c88e` | 8 |
| `mailman` | [mperestoronin/media-corpus-mailman](https://github.com/mperestoronin/media-corpus-mailman) | `main` | `84a0dc408e85a6da2e5ddf21770e86a457d34bb1` | 5 |
| `webscraper` | [mperestoronin/media-corpus-webscraper](https://github.com/mperestoronin/media-corpus-webscraper) | `main` | `66e7f951ec29d5a16b48525ad475a78060835425` | 17 |
| `digest-creator` | [mperestoronin/media-corpus-digest-creator](https://github.com/mperestoronin/media-corpus-digest-creator) | `main` | `255f408abbbf98dc753d909fc0802981ce5ba255` | 4 |

## Метод переноса

Каждый источник клонирован отдельно во временную директорию. В `product/<компонент>/` перенесены отслеживаемые файлы из Git-объектов указанного HEAD, за вычетом исключений ниже. Содержимое включённых файлов сверяется по Git blob SHA; режимы файлов, включая исполняемые скрипты, сохраняются в индексе учебного репозитория. Исходный код не редактируется.

## Исключения

- Служебные `.git` всех шести временных клонов; неотслеживаемые локальные файлы не импортируются.
- `backend-web/frontend/.vscode/extensions.json` и `settings.json` — настройки IDE (2 файла).
- `backend-web/out/artifacts/crosswords_jar/crosswords.jar` — готовый артефакт сборки (1 файл).
- `mobile/android/app/.cxx/` — кэш и результаты CMake/Android-сборки (121 файл).
- `mobile/android/app/google-services.json` — конфигурация Firebase с API-ключом (1 файл). Ключ не воспроизводится в отчёте. Для Android-сборки требуется собственный локальный файл.

Всего исключено 125 отслеживаемых файлов, перенесено 544. Shared-файлы Xcode (`.xcodeproj`, `.xcworkspace`, `xcshareddata`), Flutter scaffolding, lock-файлы, ресурсы и конфигурация сборки сохранены. Это части проекта, а не персональные настройки IDE.

## Проверка потенциальных секретов

- В исходниках Gitleaks обнаружил `gcp-api-key` в `mobile/android/app/google-services.json:18`. Файл целиком исключён; действительность ключа и его ограничения не проверялись.
- `backend-web/frontend/.npmrc` проверен: содержит только настройки пакетного менеджера, без токенов.
- Параметры доступа backend, classifier, mailman и webscraper используют переменные окружения; GitHub workflows ссылаются на `secrets.*`, без значений. Workflows сохранены внутри компонентов и не установлены как корневые workflows учебного репозитория.
- `webscraper/docker-compose.yaml` содержит стандартные локальные значения Airflow/PostgreSQL (`airflow`) и пустой пароль Redis. Это явные типовые значения конфигурации, сохранённые без изменений; не обнаруженные персональные credentials.
- Финальная проверка всего рабочего дерева выполнена Gitleaks 8.30.1 с правилами по умолчанию, полным скрытием значений и игнорированием inline-разрешений. Потенциальные секреты не обнаружены (0 findings).
- Рекурсивно проверены имена файлов и директорий: внутри `product/` отсутствуют `.git`, `.env`, файлы credentials, приватные ключи, исключённая Firebase-конфигурация, локальные настройки IDE и каталоги сборок/кэшей.
- Проверены полнота переноса, точное совпадение Git blob SHA и режимов всех 544 файлов с baseline.

Проверка относится к подготовленному snapshot, не ко всей истории canonical repositories. Сборка и запуск компонентов в рамках переноса не выполнялись.
