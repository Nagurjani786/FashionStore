import html
import re
from pathlib import Path

import markdown

DOCS = Path(__file__).resolve().parents[1]
OUT = Path(__file__).resolve().parent / "html"
OUT.mkdir(parents=True, exist_ok=True)

TEMPLATE = """<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <title>{title}</title>
  <script src="https://cdn.jsdelivr.net/npm/mermaid@11/dist/mermaid.min.js"></script>
  <style>
    @page {{ size: A4; margin: 16mm 14mm; }}
    body {{
      font-family: "Segoe UI", Arial, sans-serif;
      font-size: 11pt;
      line-height: 1.45;
      color: #1f2328;
      max-width: 920px;
      margin: 0 auto;
      padding: 8px 4px 24px;
    }}
    h1, h2, h3 {{ color: #0d1117; page-break-after: avoid; }}
    h1 {{ font-size: 22pt; border-bottom: 2px solid #1f6feb; padding-bottom: 6px; }}
    h2 {{ font-size: 15pt; margin-top: 1.4em; border-bottom: 1px solid #d0d7de; padding-bottom: 4px; }}
    h3 {{ font-size: 12.5pt; }}
    table {{ border-collapse: collapse; width: 100%; margin: 12px 0; font-size: 10pt; page-break-inside: auto; }}
    th, td {{ border: 1px solid #d0d7de; padding: 6px 8px; text-align: left; vertical-align: top; }}
    th {{ background: #f6f8fa; }}
    tr {{ page-break-inside: avoid; }}
    code {{ font-family: Consolas, "Courier New", monospace; font-size: 9.5pt; background: #f6f8fa; padding: 1px 4px; }}
    pre {{ background: #f6f8fa; border: 1px solid #d0d7de; padding: 10px; overflow: hidden; white-space: pre-wrap; font-size: 9pt; }}
    .mermaid {{
      margin: 16px 0;
      page-break-inside: avoid;
      text-align: center;
    }}
    a {{ color: #0969da; }}
    hr {{ border: none; border-top: 1px solid #d0d7de; margin: 24px 0; }}
  </style>
</head>
<body>
{body}
<script>
  mermaid.initialize({{
    startOnLoad: false,
    theme: "neutral",
    securityLevel: "loose",
    flowchart: {{ useMaxWidth: true, htmlLabels: true }},
    sequence: {{ useMaxWidth: true }},
    er: {{ useMaxWidth: true }}
  }});
  mermaid.run().then(function () {{
    document.body.dataset.ready = "1";
  }}).catch(function () {{
    document.body.dataset.ready = "1";
  }});
</script>
</body>
</html>
"""

FENCE = re.compile(r"```mermaid\s*\n(.*?)```", re.DOTALL)


def md_to_html(text: str) -> str:
    placeholders = []

    def stash(match: re.Match) -> str:
        placeholders.append(match.group(1).strip())
        return f"\n\nMERMAID_PLACEHOLDER_{len(placeholders) - 1}\n\n"

    text = FENCE.sub(stash, text)
    converted = markdown.markdown(
        text,
        extensions=["tables", "fenced_code", "nl2br", "sane_lists"],
    )
    for i, source in enumerate(placeholders):
        block = f'<div class="mermaid">\n{html.escape(source)}\n</div>'
        converted = converted.replace(f"<p>MERMAID_PLACEHOLDER_{i}</p>", block)
        converted = converted.replace(f"MERMAID_PLACEHOLDER_{i}", block)
    return converted


def main() -> None:
    files = [
        ("README.md", "FashionStore documentation"),
        ("architecture.md", "FashionStore architecture"),
        ("database.md", "FashionStore database"),
        ("controllers.md", "FashionStore controllers"),
    ]
    for name, title in files:
        source = (DOCS / name).read_text(encoding="utf-8")
        body = md_to_html(source)
        html_name = name.replace(".md", ".html")
        (OUT / html_name).write_text(
            TEMPLATE.format(title=title, body=body),
            encoding="utf-8",
        )
        print(f"wrote {OUT / html_name}")


if __name__ == "__main__":
    main()
