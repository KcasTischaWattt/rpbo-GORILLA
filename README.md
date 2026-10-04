# Crosswords — rpbo-GORILLA

Командный учебный репозиторий команды GORILLA для курса «Разработка безопасного ПО».

## Структура

```text
.
├── README.md
├── project.md
├── evidence/
├── presentation/
└── product/
    ├── backend-web/
    ├── mobile/
    ├── classifier/
    ├── mailman/
    ├── webscraper/
    └── digest-creator/
```

`product/` содержит snapshot шести компонентов. Продуктовая разработка и история остаются в отдельных canonical repositories; этот репозиторий не заменяет их.

- [project.md](project.md) — документ проекта по шаблону Secure SDLC.
- [evidence/snapshot.md](evidence/snapshot.md) — источники, baseline SHA, исключения и результаты проверки snapshot.
- `presentation/` — материалы презентации.

Инструкции компонентов находятся внутри `product/`. Для запуска требуются собственные локальные настройки и секреты. Файл `product/mobile/android/app/google-services.json` намеренно исключён: для Android-сборки с Firebase нужно предоставить собственную локальную конфигурацию по этому пути, не добавляя её в Git.
