#!/usr/bin/env python3
"""Open a GitHub issue for each new vulnerability in a Grype report.

Used by the weekly run of .github/workflows/dependency-scan.yml. Standard
library plus the gh CLI (GH_TOKEN must be set).

    scan_issues.py --grype scan/grype-debrief.json --repo owner/name \
        [--min-severity Low] [--max-new 20] [--dry-run]

One issue per vulnerability and package. Its title is

    Vulnerable dependency: <vulnerability id> in <package>

and it carries the label `dependency-vulnerability`. A finding that already
has an open issue with that title is skipped, so each finding is raised once.
Close the issue when the library is upgraded or removed. If the finding is
accepted rather than fixed, add an ignore rule to .github/grype.yaml (with
its reason and the issue number) before closing: a closed issue does not stop
the next run raising it again.
"""

import argparse
import collections
import json
import subprocess
import sys

LABEL = "dependency-vulnerability"
LABEL_COLOUR = "b60205"
LABEL_TEXT = "Raised by the weekly dependency scan"
SEVERITIES = ["Critical", "High", "Medium", "Low", "Negligible", "Unknown"]


def title_of(vuln_id, package):
    return f"Vulnerable dependency: {vuln_id} in {package}"


def findings(grype_path, min_severity):
    """Group matches by (vulnerability, package); keep the worst severity."""
    with open(grype_path) as f:
        report = json.load(f)
    cut = SEVERITIES.index(min_severity)
    grouped = collections.OrderedDict()
    for m in report.get("matches", []):
        v, a = m["vulnerability"], m["artifact"]
        sev = v.get("severity") or "Unknown"
        if sev not in SEVERITIES or SEVERITIES.index(sev) > cut:
            continue
        key = (v["id"], a["name"])
        f = grouped.setdefault(key, {
            "id": v["id"], "package": a["name"], "severity": sev,
            "versions": set(), "paths": set(), "fixes": set(),
            "url": v.get("dataSource") or "", "description": "",
            "aliases": set(),
        })
        if SEVERITIES.index(sev) < SEVERITIES.index(f["severity"]):
            f["severity"] = sev
        f["versions"].add(a.get("version") or "?")
        for loc in a.get("locations", []):
            f["paths"].add(loc.get("path", "").lstrip("/"))
        f["fixes"].update(v.get("fix", {}).get("versions", []))
        for rel in m.get("relatedVulnerabilities", []):
            if rel.get("id") and rel["id"] != v["id"]:
                f["aliases"].add(rel["id"])
            if not f["description"] and rel.get("description"):
                f["description"] = rel["description"]
        if not f["description"] and v.get("description"):
            f["description"] = v["description"]
    return sorted(grouped.values(),
                  key=lambda f: (SEVERITIES.index(f["severity"]), f["id"], f["package"]))


def body_of(f):
    lines = [
        f"The weekly dependency scan found **{f['id']}** "
        f"({f['severity']}) in **{f['package']}**.",
        "",
        f"- Version found: {', '.join(sorted(f['versions']))}",
        f"- Fixed in: {', '.join(sorted(f['fixes'])) or 'no fixed version known'}",
    ]
    if f["aliases"]:
        lines.append(f"- Also known as: {', '.join(sorted(f['aliases']))}")
    if f["url"]:
        lines.append(f"- Details: {f['url']}")
    lines += ["", "Found in:", ""]
    lines += [f"- `{p}`" for p in sorted(f["paths"]) if p]
    if f["description"]:
        desc = " ".join(f["description"].split())
        if len(desc) > 600:
            desc = desc[:600].rsplit(" ", 1)[0] + " ..."
        lines += ["", "> " + desc]
    lines += [
        "",
        "**What to do.** Upgrade or remove the library, then close this issue. "
        "A jar fetched from Maven Central is bumped in "
        "`org.mwc.debrief.thirdparty/pom.xml` (Dependabot may already have a PR). "
        "If the finding is accepted instead, add a narrow ignore rule to "
        "`.github/grype.yaml` that names this issue, then close it: otherwise "
        "the next scan raises it again.",
        "",
        "See `docs/dependencies/plan.md`.",
    ]
    return "\n".join(lines)


def gh(*args, capture=True):
    return subprocess.run(["gh", *args], check=True, text=True,
                          capture_output=capture).stdout


def open_titles(repo):
    out = gh("issue", "list", "--repo", repo, "--label", LABEL, "--state", "open",
             "--limit", "1000", "--json", "title")
    return {i["title"] for i in json.loads(out)}


def main():
    p = argparse.ArgumentParser(description=__doc__.split("\n")[0])
    p.add_argument("--grype", required=True)
    p.add_argument("--repo", required=True)
    p.add_argument("--min-severity", default="Low", choices=SEVERITIES)
    p.add_argument("--max-new", type=int, default=20,
                   help="open at most this many issues in one run")
    p.add_argument("--dry-run", action="store_true",
                   help="print what would be opened; do not call GitHub")
    args = p.parse_args()

    found = findings(args.grype, args.min_severity)
    existing = set() if args.dry_run else open_titles(args.repo)
    new = [f for f in found if title_of(f["id"], f["package"]) not in existing]
    print(f"{len(found)} finding(s) at {args.min_severity} or above; "
          f"{len(found) - len(new)} already have an open issue; {len(new)} new.")

    if new and not args.dry_run:
        gh("label", "create", LABEL, "--repo", args.repo, "--force",
           "--color", LABEL_COLOUR, "--description", LABEL_TEXT)

    for f in new[:args.max_new]:
        title = title_of(f["id"], f["package"])
        if args.dry_run:
            print(f"\n=== {title}\n{body_of(f)}")
            continue
        url = gh("issue", "create", "--repo", args.repo, "--title", title,
                 "--label", LABEL, "--body", body_of(f)).strip()
        print(f"opened {url}")

    if len(new) > args.max_new:
        # a flood usually means a scanner or database change, not real news:
        # say so loudly instead of opening dozens of issues
        print(f"::warning::{len(new) - args.max_new} more new finding(s) not "
              f"raised (limit {args.max_new}). See the scan report.")
    return 0


if __name__ == "__main__":
    sys.exit(main())
