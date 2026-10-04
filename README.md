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

## ЭК1

Концепция находится в [project.md](project.md), разделы 1–5; пояснения и исходные свидетельства — в [ek1/](ek1/README.md). [Аудит](ek1/audit.md) связывает документы с пятью критериями, [отчёт](ek1/report.md) описывает выполненную работу и ограничения, [материалы защиты](ek1/defense.md) помогают подготовить выступление.

Проверить комплект из корня репозитория:

```sh
python3 ek1/tools/validate_bundle.py
```

Тег `ek1` фиксирует версию для оценивания. Архив и SHA-256 создаются из тега командой `python3 ek1/tools/package_bundle.py` в игнорируемом каталоге `ek1/dist/`. [Инструкция упаковки и подачи](ek1/README.md). Загрузка в ЭИОС и защита выполняются участниками.
