#!/usr/bin/env python3
"""Point the target platform at the latest Eclipse simultaneous release.

Used by .github/workflows/eclipse-target-bump.yml. Standard library only.

    eclipse_target.py [--apply] [--github-output FILE]

The target platform (org.mwc.debrief.targetplatforms/eclipse-latest.target and
its DSL source eclipse-latest.tpd) pins one build of one release, e.g.
https://download.eclipse.org/releases/2026-09/202609091000/ . This script:

1. reads EPP's release.xml, whose <present> entry is the newest release that
   has gone GA (milestones and release candidates are <future>);
2. reads that release's composite repository and takes its newest build;
3. compares the result with the URL in the target files, and with --apply
   rewrites them (and the Eclipse version named in CLAUDE.md).

It prints what it found and, with --github-output, writes `current`,
`latest`, `release`, `platform` and `changed` (true/false) for the workflow.
"""

import argparse
import io
import os
import re
import sys
import urllib.request
import xml.etree.ElementTree as ET
import zipfile

BASE = "https://download.eclipse.org"
RELEASES_XML = BASE + "/technology/epp/downloads/release/release.xml"
ROOT = os.path.abspath(os.path.join(os.path.dirname(__file__), "..", ".."))
TARGET_FILES = [
    "org.mwc.debrief.targetplatforms/eclipse-latest.target",
    "org.mwc.debrief.targetplatforms/eclipse-latest.tpd",
]
URL_RE = re.compile(re.escape(BASE) + r"/releases/(\d{4}-\d{2})/(\d{12})/")


def fetch(url):
    req = urllib.request.Request(url, headers={"User-Agent": "debrief-ci"})
    with urllib.request.urlopen(req, timeout=60) as r:
        return r.read()


def latest_release():
    root = ET.fromstring(fetch(RELEASES_XML))
    present = root.findtext("present")
    if not present or not re.fullmatch(r"\d{4}-\d{2}/R", present.strip()):
        raise SystemExit(f"unexpected <present> in release.xml: {present!r}")
    return present.strip().split("/")[0]


def latest_build(release):
    data = fetch(f"{BASE}/releases/{release}/compositeContent.jar")
    with zipfile.ZipFile(io.BytesIO(data)) as z:
        root = ET.fromstring(z.read("compositeContent.xml"))
    builds = [c.get("location", "").strip("/") for c in root.iter("child")]
    builds = sorted(b for b in builds if re.fullmatch(r"\d{12}", b))
    if not builds:
        raise SystemExit(f"no builds listed for release {release}")
    return builds[-1]


def platform_of(release):
    """Eclipse 2026-09 is platform 4.41; each quarterly release adds one."""
    year, month = map(int, release.split("-"))
    return f"4.{41 + (year - 2026) * 4 + (month - 9) // 3}"


def current_urls():
    found = set()
    for f in TARGET_FILES:
        with open(os.path.join(ROOT, f)) as fh:
            found.update(m.group(0) for m in URL_RE.finditer(fh.read()))
    if len(found) != 1:
        raise SystemExit(f"expected one release URL in the target files, found {sorted(found)}")
    return found.pop()


def apply(current, latest, release):
    for f in TARGET_FILES:
        path = os.path.join(ROOT, f)
        with open(path) as fh:
            text = fh.read()
        with open(path, "w") as fh:
            fh.write(text.replace(current, latest))
    path = os.path.join(ROOT, "CLAUDE.md")
    with open(path) as fh:
        text = fh.read()
    text = re.sub(r"Based on Eclipse \d{4}-\d{2} \(platform 4\.\d+\)",
                  f"Based on Eclipse {release} (platform {platform_of(release)})", text)
    with open(path, "w") as fh:
        fh.write(text)


def main():
    p = argparse.ArgumentParser(description=__doc__.split("\n")[0])
    p.add_argument("--apply", action="store_true", help="rewrite the files")
    p.add_argument("--github-output", help="append key=value results to this file")
    args = p.parse_args()

    current = current_urls()
    release = latest_release()
    latest = f"{BASE}/releases/{release}/{latest_build(release)}/"
    changed = latest != current
    print(f"current: {current}\nlatest:  {latest}\nchanged: {changed}")

    if changed and args.apply:
        apply(current, latest, release)
    if args.github_output:
        with open(args.github_output, "a") as fh:
            fh.write(f"current={current}\nlatest={latest}\nrelease={release}\n"
                     f"platform={platform_of(release)}\nchanged={str(changed).lower()}\n")
    return 0


if __name__ == "__main__":
    sys.exit(main())
