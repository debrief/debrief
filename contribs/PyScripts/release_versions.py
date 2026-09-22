#!/usr/bin/env python3
"""Version bumping for a Debrief release.

Replaces the original BuildSupport.py.  Works out which OSGi bundles have
changed since the last published release, increments their versions, keeps the
marketing version / about box / product / release tag in lockstep, and then
syncs the Maven poms via the Tycho versions plugin.

Usage:

    release_versions.py plan  [--since TAG] [--json]
    release_versions.py apply [--since TAG] [--date YYYYMMDD] [--skip-pom]
    release_versions.py tag-name [--date YYYYMMDD]

``plan`` is read only.  ``apply`` performs the edits.  Nothing happens at
import time, so the module can be exercised from tests.
"""

import argparse
import json
import os
import re
import ssl
import subprocess
import sys
import urllib.request
import xml.etree.ElementTree as ET
from datetime import date as date_cls

# ---------------------------------------------------------------------------
# constants
# ---------------------------------------------------------------------------

LATEST_RELEASE_URL = "https://api.github.com/repos/debrief/debrief/releases/latest"

# Release tags look like 20250307_3_1_44
TAG_PATTERN = re.compile(r"^\d{8}_\d+(?:_\d+)+$")

MANIFEST_PATH = os.path.join("META-INF", "MANIFEST.MF")
FEATURE_FILE = "feature.xml"

PRODUCT_FILE = os.path.join("org.mwc.debrief.product", "debriefng.product")
PRODUCT_POM = os.path.join("org.mwc.debrief.product", "pom.xml")
MAPPINGS_FILE = os.path.join("org.mwc.debrief.core", "about.mappings")

# Not OSGi bundles, but versioned explicitly by this script - so changes
# here are not "unmatched paths".
HANDLED_NON_BUNDLE_DIRS = {"org.mwc.debrief.product"}

# org.mwc.debrief.core is always bumped: it carries about.mappings, and the
# bundle has to change for Eclipse to pick the new about box up on update.
ALWAYS_BUMP_PLUGIN = "org.mwc.debrief.core"

# Which feature covers which family of bundles.  Explicit and reviewable,
# rather than buried in an if/elif chain.
FEATURE_FOR_PREFIX = {
    "org.mwc.asset": "org.mwc.asset.core.feature",
    "org.mwc.cmap": "org.mwc.cmap.combined.feature",
    "org.mwc.debrief": "org.mwc.debrief.combined.feature",
}

TYCHO_UPDATE_POM = [
    "mvn", "-B", "org.eclipse.tycho:tycho-versions-plugin:update-pom",
]


class ReleaseError(Exception):
    """Anything that should abort the release with a readable message."""


# ---------------------------------------------------------------------------
# version arithmetic
# ---------------------------------------------------------------------------

def bump(version):
    """Increment the final dot-separated segment of ``version``.

    The original implementation did a global substring replace, which turned
    ``0.14.1`` into ``0.24.2``.  This replaces only the last segment.
    """
    segments = version.split(".")
    last = segments[-1]
    if not last.isdigit():
        raise ReleaseError(
            "cannot increment version %r: last segment %r is not numeric"
            % (version, last))
    segments[-1] = str(int(last) + 1)
    return ".".join(segments)


# ---------------------------------------------------------------------------
# reading versions out of files
# ---------------------------------------------------------------------------

_MANIFEST_VERSION = re.compile(r"^(Bundle-Version:\s*)(\S+)\s*$")

# The <feature> / <product> root element version attribute.  Anchored on the
# element name so it can never match <?xml version="1.0"?>, the pinned
# version="5.0.1.v201404251740" of an included plugin, or the 80-odd
# version="0.0.0" plugin references inside a feature.
_ROOT_VERSION = re.compile(
    r"(<(?:feature|product)\b[^>]*?\bversion\s*=\s*\")([^\"]+)(\")",
    re.DOTALL)

_MAPPING_LINE = re.compile(r"^(\d+)=(.*)$")


def read_manifest_version(path):
    with open(path, encoding="utf-8", errors="surrogateescape") as handle:
        for line in handle:
            match = _MANIFEST_VERSION.match(line)
            if match:
                return match.group(2)
    return None


def read_root_version(path):
    with open(path, encoding="utf-8") as handle:
        match = _ROOT_VERSION.search(handle.read())
    return match.group(2) if match else None


def read_marketing_version(path=MAPPINGS_FILE):
    """The 3.1.NN version shown in the About box."""
    with open(path, encoding="utf-8") as handle:
        for line in handle:
            match = _MAPPING_LINE.match(line.strip())
            if match and match.group(1) == "0":
                return match.group(2).strip()
    raise ReleaseError("no '0=' entry found in %s" % path)


def read_pom_version(path):
    """The project's own <version>, i.e. the direct child of <project>.

    A plain regex is not good enough here: org.mwc.debrief.product/pom.xml also
    carries ${tycho.version} and the antrun plugin's own 1.7.
    """
    tree = ET.parse(path)
    root_el = tree.getroot()
    namespace = ""
    if root_el.tag.startswith("{"):
        namespace = root_el.tag[:root_el.tag.index("}") + 1]
    element = root_el.find("%sversion" % namespace)
    return element.text.strip() if element is not None else None


# ---------------------------------------------------------------------------
# writing versions back
# ---------------------------------------------------------------------------

def set_manifest_version(path, new_version):
    """Rewrite only the Bundle-Version line.

    Every other line - including the Bundle-ClassPath continuation lines that
    follow it - is passed through untouched.
    """
    with open(path, encoding="utf-8", errors="surrogateescape", newline="") as handle:
        lines = handle.readlines()

    for index, line in enumerate(lines):
        stripped = line.rstrip("\r\n")
        match = _MANIFEST_VERSION.match(stripped)
        if match:
            ending = line[len(stripped):]
            lines[index] = match.group(1) + new_version + ending
            break
    else:
        raise ReleaseError("no Bundle-Version line in %s" % path)

    with open(path, "w", encoding="utf-8", errors="surrogateescape", newline="") as handle:
        handle.writelines(lines)


def set_root_version(path, new_version):
    """Rewrite the version attribute of the <feature>/<product> root element."""
    with open(path, encoding="utf-8", newline="") as handle:
        contents = handle.read()

    updated, count = _ROOT_VERSION.subn(
        lambda m: m.group(1) + new_version + m.group(3), contents, count=1)
    if count != 1:
        raise ReleaseError("no <feature>/<product> version attribute in %s" % path)

    with open(path, "w", encoding="utf-8", newline="") as handle:
        handle.write(updated)


def set_mappings(path, marketing_version, release_date):
    """Rewrite about.mappings: 0=version, 1=YYYYMMDD, 2=YYYY-MM-DD.

    Keyed on the literal key rather than the original ``int(line[0])``, so the
    entries can appear in any order and the keys may be more than one digit.
    The file has no trailing newline in the repo; that is preserved.
    """
    values = {
        "0": marketing_version,
        "1": release_date.strftime("%Y%m%d"),
        "2": release_date.strftime("%Y-%m-%d"),
    }

    with open(path, encoding="utf-8", newline="") as handle:
        contents = handle.read()

    trailing_newline = contents.endswith("\n")
    out = []
    for line in contents.splitlines():
        match = _MAPPING_LINE.match(line.strip())
        if match and match.group(1) in values:
            out.append("%s=%s" % (match.group(1), values.pop(match.group(1))))
        else:
            out.append(line)

    # any key that was missing gets appended, so the file is always complete
    for key in sorted(values):
        out.append("%s=%s" % (key, values[key]))

    with open(path, "w", encoding="utf-8", newline="") as handle:
        handle.write("\n".join(out) + ("\n" if trailing_newline else ""))


# ---------------------------------------------------------------------------
# git / github
# ---------------------------------------------------------------------------

def _run(command, root=None):
    result = subprocess.run(
        command, cwd=root, stdout=subprocess.PIPE, stderr=subprocess.PIPE)
    if result.returncode != 0:
        raise ReleaseError(
            "command failed (%s): %s\n%s"
            % (result.returncode, " ".join(command),
               result.stderr.decode("utf-8", "replace").strip()))
    return result.stdout.decode("utf-8", "replace")


def local_release_tags(root=None):
    tags = _run(["git", "tag"], root).split()
    return sorted(tag for tag in tags if TAG_PATTERN.match(tag))


def latest_release_tag(root=None):
    """The tag of the most recent published release.

    Asks GitHub first; falls back to the newest local release-shaped tag so the
    script still works offline.
    """
    request = urllib.request.Request(
        LATEST_RELEASE_URL, headers={"Accept": "application/vnd.github+json"})
    token = os.environ.get("GITHUB_TOKEN")
    if token:
        request.add_header("Authorization", "Bearer %s" % token)
    try:
        context = ssl.create_default_context()
        with urllib.request.urlopen(request, context=context, timeout=30) as response:
            return json.loads(response.read().decode("utf-8"))["tag_name"]
    except Exception as error:  # network, proxy, rate limit, ...
        tags = local_release_tags(root)
        if not tags:
            raise ReleaseError(
                "could not reach the GitHub releases API (%s) and no local "
                "release tag was found - pass --since <tag> explicitly" % error)
        print("warning: GitHub API unavailable (%s); using local tag %s"
              % (error, tags[-1]), file=sys.stderr)
        return tags[-1]


def changed_paths(tag, root=None):
    """Paths changed between ``tag`` and HEAD.

    Fetches the tag first if the clone does not have it - a shallow CI clone
    otherwise fails here with an opaque message.
    """
    try:
        _run(["git", "rev-parse", "--verify", "%s^{commit}" % tag], root)
    except ReleaseError:
        try:
            _run(["git", "fetch", "--tags", "--quiet"], root)
            _run(["git", "rev-parse", "--verify", "%s^{commit}" % tag], root)
        except ReleaseError:
            raise ReleaseError(
                "tag %r is not in this clone and could not be fetched. "
                "If this is a shallow clone, run "
                "'git fetch --unshallow --tags' first." % tag)

    output = _run(["git", "diff", "--name-only", "%s..HEAD" % tag], root)
    return [line for line in output.splitlines() if line.strip()]


# ---------------------------------------------------------------------------
# bundle discovery
# ---------------------------------------------------------------------------

def discover_bundles(root="."):
    """Map every bundle directory to its kind.

    Built by looking at the filesystem rather than from a hard-coded list, so a
    new plugin is picked up automatically.  Directories such as
    org.mwc.debrief.satc.core (a README stub) are deliberately absent.
    """
    bundles = {}
    for name in sorted(os.listdir(root)):
        directory = os.path.join(root, name)
        if not os.path.isdir(directory) or name.startswith("."):
            continue
        if os.path.isfile(os.path.join(directory, MANIFEST_PATH)):
            bundles[name] = "plugin"
        elif os.path.isfile(os.path.join(directory, FEATURE_FILE)):
            bundles[name] = "feature"
    return bundles


def affected(paths, bundles):
    """Split changed paths into bundles touched and paths that match none.

    The original code took ``path.split("/")[0]`` and relied on a later
    os.path.exists to quietly drop the rubbish.  Here the unmatched paths are
    returned so they can be shown to whoever is cutting the release.
    """
    touched = set()
    skipped = []
    for path in paths:
        head = path.split("/")[0]
        if head in bundles:
            touched.add(head)
        elif head in HANDLED_NON_BUNDLE_DIRS:
            pass  # the product dir is versioned explicitly, further down
        else:
            skipped.append(path)
    return touched, skipped


def features_for(plugins):
    features = set()
    for plugin in plugins:
        for prefix, feature in FEATURE_FOR_PREFIX.items():
            if plugin.startswith(prefix):
                features.add(feature)
    return features


def release_tag(marketing_version, release_date):
    return "%s_%s" % (release_date.strftime("%Y%m%d"),
                      marketing_version.replace(".", "_"))


# ---------------------------------------------------------------------------
# planning
# ---------------------------------------------------------------------------

def build_plan(root=".", since=None, release_date=None):
    release_date = release_date or date_cls.today()
    since = since or latest_release_tag(root)

    bundles = discover_bundles(root)
    touched, skipped = affected(changed_paths(since, root), bundles)

    plugins = {name for name in touched if bundles[name] == "plugin"}
    # the about.mappings carrier always moves, so the new about box ships
    plugins.add(ALWAYS_BUMP_PLUGIN)

    # Features are derived from the FINAL plugin set, not from the raw diff.
    # If a plugin moves, the feature that ships it has to move too or the P2
    # update site will never offer the new bundle - and that includes the
    # always-bumped org.mwc.debrief.core.
    features = features_for(plugins)
    # a directly-edited feature is bumped whether or not the mapping caught it
    features.update(name for name in touched if bundles[name] == "feature")

    changes = []
    for plugin in sorted(plugins):
        path = os.path.join(root, plugin, MANIFEST_PATH)
        current = read_manifest_version(path)
        if current is None:
            skipped.append("%s (no Bundle-Version)" % plugin)
            continue
        changes.append({"kind": "plugin", "id": plugin,
                        "file": os.path.join(plugin, MANIFEST_PATH),
                        "old": current, "new": bump(current)})

    for feature in sorted(features):
        path = os.path.join(root, feature, FEATURE_FILE)
        if not os.path.isfile(path):
            skipped.append("%s (no feature.xml)" % feature)
            continue
        current = read_root_version(path)
        changes.append({"kind": "feature", "id": feature,
                        "file": os.path.join(feature, FEATURE_FILE),
                        "old": current, "new": bump(current)})

    product_version = read_root_version(os.path.join(root, PRODUCT_FILE))
    marketing = read_marketing_version(os.path.join(root, MAPPINGS_FILE))

    if product_version != marketing:
        raise ReleaseError(
            "product version (%s) and about.mappings version (%s) disagree - "
            "fix that by hand before releasing" % (product_version, marketing))

    new_marketing = bump(marketing)
    changes.append({"kind": "product", "id": "DebriefNG", "file": PRODUCT_FILE,
                    "old": product_version, "new": new_marketing})
    changes.append({"kind": "mappings", "id": "about.mappings",
                    "file": MAPPINGS_FILE,
                    "old": marketing, "new": new_marketing})

    return {
        "since": since,
        "date": release_date.strftime("%Y-%m-%d"),
        "marketing_version": {"old": marketing, "new": new_marketing},
        "tag": release_tag(new_marketing, release_date),
        "changes": changes,
        "skipped": sorted(set(skipped)),
    }


# ---------------------------------------------------------------------------
# applying
# ---------------------------------------------------------------------------

def apply_plan(plan, root=".", release_date=None):
    release_date = release_date or date_cls.today()
    new_marketing = plan["marketing_version"]["new"]

    for change in plan["changes"]:
        path = os.path.join(root, change["file"])
        if change["kind"] == "plugin":
            set_manifest_version(path, change["new"])
        elif change["kind"] in ("feature", "product"):
            set_root_version(path, change["new"])
        elif change["kind"] == "mappings":
            set_mappings(path, new_marketing, release_date)
        print("  %-8s %-45s %s -> %s"
              % (change["kind"], change["id"], change["old"], change["new"]))


def verify(root=".", check_poms=True):
    """The three places the marketing version lives must agree.

    org.mwc.debrief.product/pom.xml matters more than it looks: its antrun step
    copies product-${project.version}.zip to P2_Repository.zip, so a pom left
    behind by a skipped update-pom silently breaks a release asset.
    """
    product = read_root_version(os.path.join(root, PRODUCT_FILE))
    mappings = read_marketing_version(os.path.join(root, MAPPINGS_FILE))

    problems = []
    if product != mappings:
        problems.append("debriefng.product %s != about.mappings %s"
                        % (product, mappings))
    if check_poms:
        product_pom = read_pom_version(os.path.join(root, PRODUCT_POM))
        if product_pom != product:
            problems.append("%s %s != debriefng.product %s"
                            % (PRODUCT_POM, product_pom, product))
    if problems:
        raise ReleaseError("version consistency check failed:\n  "
                           + "\n  ".join(problems))
    return product


def update_poms(root="."):
    print("Syncing Maven poms (tycho-versions-plugin:update-pom)...")
    try:
        _run(TYCHO_UPDATE_POM, root)
    except ReleaseError as error:
        raise ReleaseError(
            "%s\n\nTycho needs the same JDK the build uses (Java 11); it "
            "aborts with 'Unknown OSGi execution environment' on a newer one. "
            "Check 'java -version', then re-run, or run ./update_pom.sh by "
            "hand once the toolchain is right." % error)


# ---------------------------------------------------------------------------
# cli
# ---------------------------------------------------------------------------

def _parse_date(text):
    return date_cls(int(text[0:4]), int(text[4:6]), int(text[6:8]))


def print_plan(plan):
    print("Baseline release : %s" % plan["since"])
    print("Release date     : %s" % plan["date"])
    print("Debrief version  : %s -> %s"
          % (plan["marketing_version"]["old"], plan["marketing_version"]["new"]))
    print("Release tag      : %s" % plan["tag"])
    print()
    print("%d file(s) to update:" % len(plan["changes"]))
    for change in plan["changes"]:
        print("  %-8s %-45s %s -> %s"
              % (change["kind"], change["id"], change["old"], change["new"]))
    if plan["skipped"]:
        print()
        print("%d changed path(s) matched no bundle (no version bumped):"
              % len(plan["skipped"]))
        # Grouped by top-level directory, because the raw list is mostly docs
        # and would bury the one entry that matters.  Anything that looks like
        # a bundle is called out: that would be a plugin shipping unversioned.
        groups = {}
        for path in plan["skipped"]:
            groups.setdefault(path.split("/")[0], []).append(path)
        for head in sorted(groups):
            suspect = " <- looks like a bundle, check this" \
                if head.startswith(("org.mwc.", "org.eclipse.")) else ""
            if len(groups[head]) == 1:
                print("  %s%s" % (groups[head][0], suspect))
            else:
                print("  %s/ (%d files)%s" % (head, len(groups[head]), suspect))


def main(argv=None):
    parser = argparse.ArgumentParser(description=__doc__.splitlines()[0])
    parser.add_argument("--root", default=".",
                        help="repository root (default: current directory)")
    sub = parser.add_subparsers(dest="command", required=True)

    plan_cmd = sub.add_parser("plan", help="show what would change (read only)")
    plan_cmd.add_argument("--since", help="baseline release tag")
    plan_cmd.add_argument("--date", type=_parse_date,
                          help="release date as YYYYMMDD (default: today)")
    plan_cmd.add_argument("--json", action="store_true",
                          help="emit the plan as JSON")

    apply_cmd = sub.add_parser("apply", help="perform the version bumps")
    apply_cmd.add_argument("--since", help="baseline release tag")
    apply_cmd.add_argument("--date", type=_parse_date,
                           help="release date as YYYYMMDD (default: today)")
    apply_cmd.add_argument("--skip-pom", action="store_true",
                           help="do not run the Tycho pom sync (testing only)")

    tag_cmd = sub.add_parser("tag-name",
                             help="print the tag for the NEXT release")
    tag_cmd.add_argument("--date", type=_parse_date,
                         help="release date as YYYYMMDD (default: today)")

    args = parser.parse_args(argv)

    try:
        if args.command == "tag-name":
            release_date = args.date or date_cls.today()
            current = read_marketing_version(
                os.path.join(args.root, MAPPINGS_FILE))
            print(release_tag(bump(current), release_date))
            return 0

        plan = build_plan(args.root, args.since, args.date)

        if args.command == "plan":
            if args.json:
                print(json.dumps(plan, indent=2))
            else:
                print_plan(plan)
            return 0

        print_plan(plan)
        print()
        print("Applying...")
        apply_plan(plan, args.root, args.date)
        if not args.skip_pom:
            update_poms(args.root)
        version = verify(args.root, check_poms=not args.skip_pom)
        print()
        print("Done. Debrief %s, tag for this release: %s"
              % (version, plan["tag"]))
        return 0

    except ReleaseError as error:
        print("error: %s" % error, file=sys.stderr)
        if args.command == "apply":
            print("The working tree may be partly updated. Run "
                  "'git checkout -- .' to discard the bumps and try again.",
                  file=sys.stderr)
        return 1


if __name__ == "__main__":
    sys.exit(main())
