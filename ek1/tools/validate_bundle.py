#!/usr/bin/env python3
"""Validate evidence and local references in a checkout or an extracted EK1 archive.

This validates the package, not the truth of every methodological judgment or
security of Crosswords. Human content review is recorded separately in audit.md.
"""
import argparse
import hashlib
import json
import re
import sys
from pathlib import Path
from urllib.parse import unquote, urlsplit

from probe_pipeline import probe

REQUIRED = ["project.md", "README.md", "evidence/snapshot.md", "ek1/README.md", "ek1/audit.md", "ek1/architecture.md",
            "ek1/requirements.md", "ek1/process-review.md", "ek1/defense.md", "ek1/report.md",
            "ek1/evidence/product-manifest.json", "ek1/evidence/source-excerpts.md", "ek1/evidence/local-history.txt",
            "ek1/evidence/sources.json", "ek1/evidence/environment.json", "ek1/evidence/builds.md",
            "ek1/evidence/pipeline-probe.json", "ek1/evidence/backend-build.log.txt", "ek1/evidence/web-build.log.txt",
            "ek1/tools/collect_evidence.py", "ek1/tools/probe_pipeline.py", "ek1/tools/validate_bundle.py", "ek1/tools/package_bundle.py"]


def unfenced(text):
    return re.sub(r"^```[^\n]*\n.*?^```\s*$", "", text, flags=re.M | re.S)


def slug(text):
    text = text.strip().lower().replace("`", "")
    return re.sub(r"[^\w\- ]", "", text).replace(" ", "-")


def validate(root):
    errors = []
    for name in REQUIRED:
        if not (root / name).is_file(): errors.append(f"Missing required artifact: {name}")
    if errors:
        return errors, {}
    project = (root / "project.md").read_text()
    for n in range(1, 6):
        if not re.search(rf"^## {n}\. .+\n[\s\S]+?(?=^## |\Z)", project, flags=re.M):
            errors.append(f"Project section {n} absent or empty")
    for marker in ("<название", "<ФИО", "<ссылка>", "TODO", "TBD"):
        if marker in project: errors.append(f"Unfilled project marker: {marker}")
    manifest = json.loads((root / "ek1/evidence/product-manifest.json").read_text())
    entries = manifest["files"]
    if manifest["count"] != len(entries) or len(entries) != 544:
        errors.append("Baseline manifest must contain exactly 544 product files")
    seen = set()
    for record in entries:
        name = record["path"]
        path = root / name
        if name in seen: errors.append(f"Duplicate manifest file: {name}")
        seen.add(name)
        if not name.startswith("product/") or not path.resolve().is_relative_to(root):
            errors.append(f"Unsafe manifest path: {name}")
            continue
        if not path.is_file():
            errors.append(f"Missing baseline file: {name}")
            continue
        actual = hashlib.sha256(path.read_bytes()).hexdigest()
        # Git checkout may convert CRLF in .cmd; the archive contains Git bytes.
        if actual not in {record["sha256"], record["working_sha256"]}:
            errors.append(f"Baseline content changed: {name}")
    docs = [root / "README.md", root / "project.md", root / "evidence/snapshot.md"] + sorted((root / "ek1").rglob("*.md"))
    docs = [p for p in docs if "dist" not in p.relative_to(root).parts]
    link_count = 0
    for doc in docs:
        text = unfenced(doc.read_text())
        for target in re.findall(r"\[[^\]\n]+\]\(([^)\n]+)\)", text):
            target = target.strip().strip("<>")
            parsed = urlsplit(target)
            if parsed.scheme or parsed.netloc: continue
            link_count += 1
            dest = (doc.parent / unquote(parsed.path)).resolve() if parsed.path else doc
            if not dest.is_relative_to(root) or not dest.exists():
                errors.append(f"Broken local link in {doc.relative_to(root)}: {target}")
                continue
            if parsed.fragment and dest.suffix == ".md":
                headings = re.findall(r"^#+ (.+)$", unfenced(dest.read_text()), flags=re.M)
                if unquote(parsed.fragment) not in {slug(h) for h in headings}:
                    errors.append(f"Broken heading link in {doc.relative_to(root)}: {target}")
    # Exact excerpt validation against product files, including line numbers.
    excerpts = (root / "ek1/evidence/source-excerpts.md").read_text()
    blocks = re.split(r"^## E\d+\s*$", excerpts, flags=re.M)[1:]
    if len(blocks) != 16: errors.append("Expected 16 source excerpt blocks")
    for block in blocks:
        match = re.search(r"Источник: \[(.+?)\]", block)
        if not match: errors.append("Excerpt missing source"); continue
        source_path = root / match.group(1)
        if not source_path.exists(): continue  # reported by link/manifest validation
        data = source_path.read_bytes()
        if hashlib.sha256(data).hexdigest() not in block:
            errors.append(f"Excerpt SHA mismatch: {match.group(1)}")
        lines = data.decode().splitlines()
        for code in re.findall(r"```text\n(.*?)\n```", block, flags=re.S):
            for entry in code.splitlines():
                line = re.fullmatch(r"(\d+): (.*)", entry)
                if not line: errors.append("Malformed excerpt line"); continue
                pos = int(line.group(1))
                if pos < 1 or pos > len(lines) or lines[pos-1] != line.group(2):
                    errors.append(f"Excerpt content mismatch: {match.group(1)}:{pos}")
    recorded = json.loads((root / "ek1/evidence/pipeline-probe.json").read_text())
    try:
        if probe(root) != recorded: errors.append("Recorded probe differs from fresh execution")
    except Exception as exc:
        errors.append(f"Probe could not repeat: {exc}")
    if "BUILD SUCCESS" not in (root / "ek1/evidence/backend-build.log.txt").read_text():
        errors.append("Backend build log lacks successful result")
    if "SPA UI compiled with success" not in (root / "ek1/evidence/web-build.log.txt").read_text():
        errors.append("Web build log lacks successful result")
    source = json.loads((root / "ek1/evidence/sources.json").read_text())
    if source["course_commit"] != "5ce5fdf4ba4bb32ac62d1b9c6728f3a6d8fa6884":
        errors.append("Course source version changed without explicit review")
    return errors, {"product_files": len(entries), "source_blocks": len(blocks), "markdown_files": len(docs), "local_links": link_count,
                    "probe": "repeated unchanged baseline functions", "limits": "Package integrity only; semantic audit, user review, runtime and defense are separate"}


if __name__ == "__main__":
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--root", type=Path, default=Path(__file__).resolve().parents[2])
    parser.add_argument("--output", type=Path)
    args = parser.parse_args()
    errors, checks = validate(args.root.resolve())
    result = {"status": "FAIL" if errors else "PASS", "checks": checks, "errors": errors}
    text = json.dumps(result, ensure_ascii=False, indent=2) + "\n"
    if args.output:
        args.output.parent.mkdir(parents=True, exist_ok=True)
        args.output.write_text(text)
    print(text)
    sys.exit(bool(errors))
