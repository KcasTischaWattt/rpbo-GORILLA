#!/usr/bin/env python3
"""Archive and independently verify the immutable EK1 tag, including all Git files."""
import argparse
import hashlib
import json
import subprocess
import tempfile
import time
import zipfile
from pathlib import Path

from validate_bundle import validate


def git(root, *args):
    return subprocess.check_output(["git", "-C", str(root), *args])


def package(root, output):
    # Require an actual tag, rather than a same-named branch or working tree.
    commit = git(root, "rev-parse", "refs/tags/ek1^{commit}").decode().strip()
    output.mkdir(parents=True, exist_ok=True)
    archive = output / "crosswords-ek1.zip"
    expected = {}
    for entry in git(root, "ls-tree", "-rz", commit).split(b"\0"):
        if not entry: continue
        metadata, name = entry.split(b"\t", 1)
        mode, kind, blob = metadata.decode().split()
        if kind != "blob" or mode not in {"100644", "100755"}:
            raise SystemExit(f"Unexpected tree entry: {name!r}")
        expected[name.decode()] = (blob, mode)
    # git archive applies .gitattributes (e.g. eol=crlf to mvnw.cmd).
    # Read raw blobs instead so the ZIP is byte-identical to the tag's tree.
    timestamp = int(git(root, "show", "-s", "--format=%ct", commit).decode().strip())
    date_time = time.gmtime(max(timestamp, 315532800))[:6]  # ZIP starts in 1980
    with zipfile.ZipFile(archive, "w", compression=zipfile.ZIP_DEFLATED, compresslevel=6) as z:
        z.comment = commit.encode()
        with subprocess.Popen(["git", "-C", str(root), "cat-file", "--batch"],
                              stdin=subprocess.PIPE, stdout=subprocess.PIPE) as batch:
            try:
                for relative, (blob, mode) in expected.items():
                    batch.stdin.write(blob.encode() + b"\n")
                    batch.stdin.flush()
                    header = batch.stdout.readline().decode().split()
                    if len(header) != 3 or header[:2] != [blob, "blob"]:
                        raise SystemExit(f"Unexpected Git object response: {relative}")
                    size = int(header[2])
                    data = batch.stdout.read(size)
                    if len(data) != size or batch.stdout.read(1) != b"\n":
                        raise SystemExit(f"Incomplete Git blob: {relative}")
                    member = zipfile.ZipInfo("crosswords-ek1/" + relative, date_time)
                    member.create_system = 3
                    member.external_attr = int(mode, 8) << 16
                    member.compress_type = zipfile.ZIP_DEFLATED
                    z.writestr(member, data)
            finally:
                batch.stdin.close()
            if batch.wait() != 0:
                raise SystemExit("Git blob reader failed")
    with tempfile.TemporaryDirectory(prefix="crosswords-ek1-archive-") as temp:
        with zipfile.ZipFile(archive) as z:
            if z.testzip(): raise SystemExit("Archive CRC check failed")
            file_members = [m for m in z.infolist() if not m.is_dir()]
            names = [m.filename.removeprefix("crosswords-ek1/") for m in file_members]
            if len(names) != len(set(names)) or set(names) != set(expected):
                raise SystemExit("Archive file set differs from Git tag")
            for member in file_members:
                relative = member.filename.removeprefix("crosswords-ek1/")
                if not member.filename.startswith("crosswords-ek1/") or ".." in Path(relative).parts:
                    raise SystemExit("Unsafe archive member")
                data = z.read(member)
                blob = hashlib.sha1(b"blob " + str(len(data)).encode() + b"\0" + data).hexdigest()
                if blob != expected[relative][0]: raise SystemExit(f"Archive differs from Git blob: {relative}")
                executable = bool((member.external_attr >> 16) & 0o111)
                if executable != (expected[relative][1] == "100755"):
                    raise SystemExit(f"Archive executable mode differs: {relative}")
            z.extractall(temp)
        extracted = Path(temp) / "crosswords-ek1"
        errors, checks = validate(extracted.resolve())
        if errors: raise SystemExit("Extracted archive validation failed: " + "; ".join(errors))
        # Adversarial checks prove important validation branches actually reject damage.
        victim = extracted / "product/classifier/news_service.py"
        saved = victim.read_bytes()
        victim.write_bytes(saved + b"\n# synthetic tamper\n")
        failures, _ = validate(extracted.resolve())
        if not any("Baseline content changed" in e for e in failures):
            raise SystemExit("Validator failed to detect modified baseline")
        victim.write_bytes(saved)
        victim.unlink()
        failures, _ = validate(extracted.resolve())
        if not any("Missing baseline file" in e for e in failures):
            raise SystemExit("Validator failed to detect a missing baseline file")
        victim.write_bytes(saved)
        original_project = (extracted / "project.md").read_text()
        (extracted / "project.md").write_text(original_project + "\n[broken](definitely-missing.md)\n")
        failures, _ = validate(extracted.resolve())
        if not any("Broken local link" in e for e in failures):
            raise SystemExit("Validator failed to detect a broken local reference")
    checksum = hashlib.sha256(archive.read_bytes()).hexdigest()
    (output / "crosswords-ek1.zip.sha256").write_text(f"{checksum}  crosswords-ek1.zip\n")
    result = {"status": "PASS", "tag": "ek1", "commit": commit, "archive": archive.name,
              "sha256": checksum, "size_bytes": archive.stat().st_size, "files": len(expected),
              "archive_checks": ["CRC", "complete Git tree file set", "each Git blob", "executable modes", "extracted package validation"],
              "negative_checks": ["modified baseline rejected", "missing baseline rejected", "broken link rejected"],
              "bundle_checks": checks}
    (output / "archive-audit.json").write_text(json.dumps(result, ensure_ascii=False, indent=2) + "\n")
    print(json.dumps(result, ensure_ascii=False, indent=2))


if __name__ == "__main__":
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--root", type=Path, default=Path(__file__).resolve().parents[2])
    parser.add_argument("--output", type=Path)
    args = parser.parse_args()
    root = args.root.resolve()
    package(root, (args.output or root / "ek1/dist").resolve())
