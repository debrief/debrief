#!/usr/bin/env python3
"""Summarise a Syft SBOM and a Grype report of Debrief's committed libraries.

Used by .github/workflows/dependency-scan.yml for the job summary, and to
write docs/dependencies/baseline-*.md. Standard library only.

    dependency_report.py --repo . --sbom sbom.syft.json --grype grype.json \
        [--inventory] [--title TEXT] > report.md

The SBOM must be Syft's own JSON format (-o syft-json), which records where
each package was found. --inventory adds a table of every committed jar.
"""

import argparse
import collections
import json
import os
import re
import subprocess
import sys

SEVERITIES = ["Critical", "High", "Medium", "Low", "Negligible", "Unknown"]

# Folders whose top-level jars Debrief Lite ships in lib/ and names on its
# generated Class-Path (org.mwc.debrief.lite/build.xml, lite.runtime.libraries).
LITE_FOLDERS = [
    "org.mwc.cmap.legacy",
    "org.mwc.cmap.legacy/libs",
    "org.mwc.debrief.legacy",
    "org.mwc.cmap.jfreechart",
    "org.mwc.cmap.geotools",
    "org.mwc.debrief.lite",
]

# reported separately: test tooling and contributed extras, not shipped
SEPARATE = ("org.mwc.debrief.ui_test/rcptt-runner/", "contribs/")


def git_files(repo, pattern):
    out = subprocess.check_output(["git", "-C", repo, "ls-files", pattern], text=True)
    return [line for line in out.splitlines() if line]


def manifest_classpath(repo, bundle):
    """the entries of a bundle's Bundle-ClassPath, as repo-relative paths"""
    path = os.path.join(repo, bundle, "META-INF", "MANIFEST.MF")
    if not os.path.isfile(path):
        return None
    with open(path, encoding="utf-8", errors="replace") as f:
        text = f.read().replace("\r\n", "\n")
    # join continuation lines (a line starting with one space continues the last)
    text = re.sub(r"\n ", "", text)
    for line in text.split("\n"):
        if line.startswith("Bundle-ClassPath:"):
            entries = [e.strip().split(";")[0] for e in line.split(":", 1)[1].split(",")]
            return [bundle + "/" + e for e in entries if e and e != "."]
    return []


def relpath(location):
    p = (location.get("accessPath") or location.get("path") or "").lstrip("/")
    return p.split(":")[0]  # a jar inside a jar is reported as outer.jar:inner.jar


def version_key(version):
    """sorts 1.2.10 after 1.2.9; numeric parts before text ones"""
    return [(0, int(x), "") if x.isdigit() else (1, 0, x) for x in re.split(r"[.\-]", version)]


def short_paths(paths, limit=3):
    paths = sorted(paths)
    text = ", ".join("`%s`" % p for p in paths[:limit])
    if len(paths) > limit:
        text += " and %d more" % (len(paths) - limit)
    return text


def severity_cells(counter):
    return " | ".join(str(counter.get(s, 0) or "") for s in SEVERITIES[:4])


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("--repo", default=".")
    ap.add_argument("--sbom", required=True)
    ap.add_argument("--grype", required=True)
    ap.add_argument("--inventory", action="store_true")
    ap.add_argument("--brief", action="store_true",
                    help="only the severity table and vulnerable packages (for the separately scanned folders)")
    ap.add_argument("--title", default="Dependency scan")
    args = ap.parse_args()

    with open(args.sbom, encoding="utf-8") as f:
        sbom = json.load(f)
    with open(args.grype, encoding="utf-8") as f:
        grype = json.load(f)

    repo = args.repo
    jars = [j for j in git_files(repo, "*.jar") if not j.startswith(SEPARATE)]
    natives = [
        j
        for pat in ("*.dll", "*.so", "*.dylib", "*.jnilib")
        for j in git_files(repo, pat)
        if not j.startswith(SEPARATE)
    ]

    # what each committed file was identified as
    found = collections.defaultdict(list)
    for a in sbom.get("artifacts", []):
        for loc in a.get("locations", []):
            found[relpath(loc)].append(a)

    def identity(path):
        """name, version and how it was identified, for the outermost package"""
        cands = [a for a in found.get(path, []) if a.get("version")]
        if not cands:
            return None
        # prefer the jar's own entry over shaded/nested ones
        cands.sort(key=lambda a: (":" in (a["locations"][0].get("accessPath") or ""), a["name"]))
        a = cands[0]
        meta = a.get("metadata") or {}
        how = "pom" if meta.get("pomProperties") else ("manifest" if meta.get("manifest") else "file name")
        if a.get("type") == "binary":
            how = "binary"
        return a["name"], a["version"], how

    # where the RCP build and Lite pick each jar up
    bundles = sorted({j.split("/")[0] for j in jars})
    on_classpath = {}
    for b in bundles:
        cp = manifest_classpath(repo, b)
        for entry in cp or []:
            on_classpath[entry] = b
    lite = {j for j in jars if os.path.dirname(j) in LITE_FOLDERS}

    # vulnerabilities, grouped by package and version
    matches = grype.get("matches", [])
    by_pkg = collections.defaultdict(lambda: {"sev": collections.Counter(), "ids": {}, "paths": set(), "fix": set()})
    unique = collections.defaultdict(set)
    for m in matches:
        v = m["vulnerability"]
        a = m["artifact"]
        sev = v.get("severity", "Unknown")
        key = (a["name"], a["version"])
        rec = by_pkg[key]
        if v["id"] not in rec["ids"]:
            rec["sev"][sev] += 1
            rec["ids"][v["id"]] = sev
        for loc in a.get("locations", []):
            rec["paths"].add(relpath(loc))
        for fv in (v.get("fix") or {}).get("versions") or []:
            rec["fix"].add(fv)
        unique[sev].add(v["id"])

    out = []
    w = out.append
    db = (grype.get("descriptor") or {}).get("db") or {}
    built = (db.get("status") or {}).get("built") or db.get("built") or "unknown"
    w("## %s\n" % args.title)
    if args.brief:
        w("Grype database built %s.\n" % built)
    else:
        w("Scanned %d committed jars and %d native libraries (excluding `%s`). Grype database built %s.\n"
          % (len(jars), len(natives), "`, `".join(s.rstrip("/") for s in SEPARATE), built))
    w("| Severity | Distinct vulnerabilities | Affected packages |")
    w("|---|---|---|")
    for s in SEVERITIES[:4]:
        pk = sum(1 for r in by_pkg.values() if r["sev"].get(s))
        w("| %s | %d | %d |" % (s, len(unique.get(s, ())), pk))
    w("")

    if by_pkg:
        w("### Vulnerable packages\n")
        w("Counts are distinct vulnerability IDs. *Fixed in* lists the versions Grype knows of that fix at least one of them.\n")
        w("| Package | Version | Critical | High | Medium | Low | Fixed in | Found in |")
        w("|---|---|---|---|---|---|---|---|")
        rows = sorted(by_pkg.items(), key=lambda kv: tuple(-kv[1]["sev"].get(s, 0) for s in SEVERITIES) + kv[0])
        for (name, version), r in rows:
            fixes = sorted(r["fix"], key=version_key)
            fix = ", ".join(fixes[-2:]) if fixes else "none known"
            w("| %s | %s | %s | %s | %s |" % (name, version, severity_cells(r["sev"]), fix, short_paths(r["paths"])))
        w("")

    if args.brief:
        sys.stdout.write("\n".join(out))
        return

    unidentified = [j for j in jars if identity(j) is None]
    weak = [j for j in jars if identity(j) and identity(j)[2] == "file name"]
    w("### Coverage\n")
    w("- %d of %d jars identified with a version; %d of those only from the file name (so matched by name alone, "
      "with no Maven group: treat their results as approximate)." % (len(jars) - len(unidentified), len(jars), len(weak)))
    w("- Not identified, so **not scanned**: %s." % (short_paths(unidentified, 20) if unidentified else "none"))
    w("")

    # same library at more than one version
    versions = collections.defaultdict(set)
    for j in jars:
        ident = identity(j)
        if ident:
            versions[ident[0]].add((ident[1], j))
    dups = {n: v for n, v in versions.items() if len({ver for ver, _ in v}) > 1}
    if dups:
        w("### Same library, more than one version\n")
        w("| Library | Versions and paths |")
        w("|---|---|")
        for n in sorted(dups):
            w("| %s | %s |" % (n, "; ".join("%s `%s`" % (ver, p) for ver, p in sorted(dups[n]))))
        w("")

    unused = sorted(j for j in jars if j not in on_classpath and j not in lite)
    w("### Jars on no `Bundle-ClassPath` and not shipped by Lite\n")
    w("These are candidates for removal, but check Ant builds, `.classpath` files and help/build tooling first.\n")
    for j in unused:
        w("- `%s`" % j)
    w("")

    if args.inventory:
        w("### Inventory\n")
        w("| Path | Library | Version | Identified from | RCP (`Bundle-ClassPath`) | Lite |")
        w("|---|---|---|---|---|---|")
        for j in sorted(jars) + sorted(natives):
            ident = identity(j) or ("?", "?", "not identified")
            rcp = on_classpath.get(j, "")
            w("| `%s` | %s | %s | %s | %s | %s |" % (j, ident[0], ident[1], ident[2], rcp and "yes", "yes" if j in lite else ""))
        w("")

    sys.stdout.write("\n".join(out))


if __name__ == "__main__":
    main()
