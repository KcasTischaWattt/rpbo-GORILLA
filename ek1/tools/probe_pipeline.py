#!/usr/bin/env python3
"""Execute two unchanged classifier functions with a failing backend and fake Kafka.

No external imports, network, real messages, credentials or production services.
This is a bounded behavioral observation, not an end-to-end Kafka test.
"""
import argparse
import ast
import hashlib
import json
import logging
from pathlib import Path
from types import SimpleNamespace


class FakeConsumer:
    def __init__(self):
        self.commits = 0

    def __iter__(self):
        yield SimpleNamespace(value={"title": "Синтетическая новость", "text": "Учебный текст", "summary": ""})

    def commit(self):
        self.commits += 1


def probe(root):
    path = root / "product/classifier/news_service.py"
    source = path.read_text()
    tree = ast.parse(source, filename=str(path))
    selected = [n for n in tree.body if isinstance(n, ast.FunctionDef) and n.name in {"send_to_backend", "main"}]
    if len(selected) != 2:
        raise ValueError("Expected two baseline functions")
    consumer = FakeConsumer()
    calls = []

    def failing_post(*args, **kwargs):
        calls.append("backend_request_failed")
        raise ConnectionError("Synthetic backend failure; no network request made")

    logger = logging.getLogger("crosswords-offline-probe")
    logger.handlers = [logging.NullHandler()]
    logger.propagate = False
    namespace = {"consumer": consumer, "running": True, "logger": logger,
                 "requests": SimpleNamespace(post=failing_post), "BACKEND_SECRET_KEY": None,
                 "ADD_DOCUMENT_BACKEND_API_URL": "http://127.0.0.1:1/unused",
                 "classify_news": lambda news: ["Экономика"],
                 "generate_summary": lambda news: "Синтетическая выжимка"}
    # The function AST nodes retain their original bodies and source positions.
    exec(compile(ast.Module(body=selected, type_ignores=[]), str(path), "exec"), namespace)
    namespace["main"]()
    result = {"source": "product/classifier/news_service.py", "source_sha256": hashlib.sha256(path.read_bytes()).hexdigest(),
              "scope": "unchanged send_to_backend and main; fake requests and KafkaConsumer; classifier/LLM mocked",
              "backend_attempts_failed": len(calls), "consumer_commits": consumer.commits,
              "observation": "main commits a message after send_to_backend catches a backend error",
              "limits": "No real Kafka, broker configuration, consumer restart, persistent storage or end-to-end delivery tested"}
    if len(calls) != 1 or consumer.commits != 1:
        raise SystemExit("Baseline observation changed; inspect before using this evidence")
    return result


if __name__ == "__main__":
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--root", type=Path, default=Path(__file__).resolve().parents[2])
    parser.add_argument("--output", type=Path)
    args = parser.parse_args()
    text = json.dumps(probe(args.root.resolve()), ensure_ascii=False, indent=2) + "\n"
    if args.output:
        args.output.parent.mkdir(parents=True, exist_ok=True)
        args.output.write_text(text)
    print(text)
