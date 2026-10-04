#!/usr/bin/env python3
"""Collect baseline evidence without importing product code or contacting services."""
import argparse
import hashlib
import json
import subprocess
from pathlib import Path

DEFAULT_ROOT = Path(__file__).resolve().parents[2]

SELECTIONS = {
    "E01": ("product/backend-web/backend/src/main/java/com/backend/crosswords/config/SecurityConfig.java", [(35, 98)]),
    "E02": ("product/backend-web/backend/src/main/java/com/backend/crosswords/config/JWTFilter.java", [(40, 139)]),
    "E03": ("product/backend-web/backend/src/main/java/com/backend/crosswords/corpus/services/AnnotationService.java", [(26, 61)]),
    "E04": ("product/backend-web/backend/src/main/java/com/backend/crosswords/corpus/services/DocService.java", [(59, 136)]),
    "E05": ("product/classifier/news_service.py", [(48, 65), (100, 172), (174, 213)]),
    "E06": ("product/digest-creator/digest_service.py", [(15, 65)]),
    "E07": ("product/mailman/app.py", [(16, 76), (103, 118)]),
    "E08": ("product/mobile/lib/services/api_service.dart", [(16, 58), (84, 106)]),
    "E09": ("product/backend-web/.github/workflows/docker-publish.yml", [(1, 40), (55, 90)]),
    "E10": ("product/backend-web/backend/src/test/java/com/backend/crosswords/CrosswordsApplicationTests.java", [(1, 30)]),
    "E11": ("product/mobile/test/widget_test.dart", [(1, 40)]),
    "E12": ("product/backend-web/frontend/package.json", [(1, 50)]),
    "E13": ("product/webscraper/dags/web_scraper_hub_dag.py", [(77, 150)]),
    "E14": ("product/backend-web/backend/src/main/resources/application.properties", [(1, 54)]),
    "E15": ("product/backend-web/frontend/src/pages/DocumentPage.vue", [(14, 80)]),
    "E16": ("product/backend-web/backend/src/main/java/com/backend/crosswords/corpus/services/MailManService.java", [(20, 55)]),
}


def git(root, *args):
    return subprocess.check_output(["git", "-C", str(root), *args])


def collect(root, output):
    baseline = git(root, "rev-parse", "HEAD").decode().strip()
    paths = [p for p in git(root, "ls-files", "-z", "product").decode().split("\0") if p]
    manifest = []
    for name in sorted(paths):
        working_data = (root / name).read_bytes()
        blob = git(root, "rev-parse", f"{baseline}:{name}").decode().strip()
        actual_blob = git(root, "hash-object", f"--path={name}", name).decode().strip()
        if actual_blob != blob:
            raise SystemExit(f"Product differs from baseline: {name}")
        data = git(root, "cat-file", "blob", blob)
        mode = git(root, "ls-tree", baseline, "--", name).decode().split()[0]
        manifest.append({"path": name, "sha256": hashlib.sha256(data).hexdigest(), "working_sha256": hashlib.sha256(working_data).hexdigest(), "git_blob": blob, "mode": mode})
    output.mkdir(parents=True, exist_ok=True)
    (output / "product-manifest.json").write_text(json.dumps({"baseline": baseline, "count": len(manifest), "files": manifest}, ensure_ascii=False, indent=2) + "\n")
    excerpts = ["# Свидетельства из исходного состояния", "", f"Baseline учебного репозитория: `{baseline}`.", "", "Фрагменты извлечены автоматически; номера относятся к исходным файлам. Это наблюдения кода, а не доказательство поведения развернутой системы.", ""]
    for key, (name, ranges) in SELECTIONS.items():
        lines = (root / name).read_text().splitlines()
        digest = hashlib.sha256((root / name).read_bytes()).hexdigest()
        excerpts += [f"## {key}", "", f"Источник: [{name}](../../{name}). SHA-256: `{digest}`.", ""]
        for start, end in ranges:
            excerpts += [f"Строки {start}–{min(end, len(lines))}:", "", "```text"]
            excerpts += [f"{i}: {lines[i-1]}" for i in range(start, min(end, len(lines)) + 1)]
            excerpts += ["```", ""]
    (output / "source-excerpts.md").write_text("\n".join(excerpts))
    history = git(root, "log", "--format=%H %s", baseline).decode()
    (output / "local-history.txt").write_text(history)
    print(json.dumps({"baseline": baseline, "product_files": len(manifest), "excerpts": len(SELECTIONS)}, ensure_ascii=False))


if __name__ == "__main__":
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--root", type=Path, default=DEFAULT_ROOT)
    parser.add_argument("--output", type=Path, required=True)
    args = parser.parse_args()
    collect(args.root.resolve(), args.output.resolve())
