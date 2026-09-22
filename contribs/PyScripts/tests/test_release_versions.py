"""Tests for release_versions.py.

Run with:  python3 -m pytest contribs/PyScripts/tests

Most of these exist because the script they replace (BuildSupport.py) got the
case wrong.  Each one is named after the failure it guards against.
"""

import os
import shutil
import sys
from datetime import date

import pytest

sys.path.insert(0, os.path.join(os.path.dirname(__file__), os.pardir))

import release_versions as rv  # noqa: E402

FIXTURES = os.path.join(os.path.dirname(__file__), "fixtures")


@pytest.fixture
def fixture(tmp_path):
    """Copy a fixture into a temp dir and hand back its path."""
    def _copy(name):
        target = tmp_path / name
        shutil.copy(os.path.join(FIXTURES, name), target)
        return str(target)
    return _copy


# ---------------------------------------------------------------------------
# bump
# ---------------------------------------------------------------------------

@pytest.mark.parametrize("current,expected", [
    # the case that made BuildSupport turn 0.14.1 into 0.24.2
    ("0.14.1", "0.14.2"),
    ("1.1.27", "1.1.28"),
    ("3.1.44", "3.1.45"),
    ("1.0.9", "1.0.10"),
    ("1.0.303", "1.0.304"),
    # every digit the same - a global replace would produce 2.2.2
    ("1.1.1", "1.1.2"),
    ("3.2.2", "3.2.3"),
    ("1.0", "1.1"),
])
def test_bump_increments_only_the_last_segment(current, expected):
    assert rv.bump(current) == expected


def test_bump_rejects_a_qualifier():
    with pytest.raises(rv.ReleaseError):
        rv.bump("5.0.1.v201404251740")


# ---------------------------------------------------------------------------
# MANIFEST.MF
# ---------------------------------------------------------------------------

def test_manifest_bump_leaves_classpath_continuations_alone(fixture):
    path = fixture("MANIFEST.MF")
    rv.set_manifest_version(path, rv.bump(rv.read_manifest_version(path)))

    contents = open(path).read()
    assert "Bundle-Version: 0.14.2\n" in contents
    assert "0.24.2" not in contents
    assert " libs/openmap.jar,\n libs/jmf.jar\n" in contents
    assert contents.startswith("Manifest-Version: 1.0\n")


def test_manifest_without_a_version_is_an_error(tmp_path):
    path = tmp_path / "MANIFEST.MF"
    path.write_text("Manifest-Version: 1.0\nBundle-Name: nope\n")
    with pytest.raises(rv.ReleaseError):
        rv.set_manifest_version(str(path), "1.0.1")


# ---------------------------------------------------------------------------
# feature.xml / .product
# ---------------------------------------------------------------------------

def test_feature_bump_does_not_touch_the_xml_declaration(fixture):
    """BuildSupport's whole-file replace rewrote <?xml version="1.0"?>."""
    path = fixture("feature.xml")
    assert rv.read_root_version(path) == "1.0"
    rv.set_root_version(path, rv.bump("1.0"))

    contents = open(path).read()
    assert '<?xml version="1.0" encoding="UTF-8"?>' in contents
    assert 'version="1.1"' in contents
    assert "a comment mentioning 1.0" in contents


def test_feature_bump_leaves_pinned_plugin_versions_alone(fixture):
    path = fixture("feature.xml")
    rv.set_root_version(path, "2.0")

    contents = open(path).read()
    assert 'version="0.0.0"' in contents
    assert 'version="5.0.1.v201404251740"' in contents


def test_product_bump_touches_only_the_product_element(fixture):
    path = fixture("debriefng.product")
    assert rv.read_root_version(path) == "3.1.44"
    rv.set_root_version(path, rv.bump("3.1.44"))

    contents = open(path).read()
    assert 'version="3.1.45"' in contents
    assert '<?xml version="1.0" encoding="UTF-8"?>' in contents
    assert '<?pde version="3.5"?>' in contents


# ---------------------------------------------------------------------------
# about.mappings
# ---------------------------------------------------------------------------

def test_mappings_updates_version_and_both_dates(fixture):
    path = fixture("about.mappings")
    assert rv.read_marketing_version(path) == "3.1.44"

    rv.set_mappings(path, "3.1.45", date(2026, 9, 22))

    contents = open(path).read()
    assert "0=3.1.45" in contents
    assert "1=20260922" in contents
    assert "2=2026-09-22" in contents
    assert contents.startswith("# about.mappings")
    # the real file has no trailing newline; keep it that way
    assert not contents.endswith("\n")


def test_mappings_tolerates_reordered_entries(tmp_path):
    path = tmp_path / "about.mappings"
    path.write_text("# header\n2=2025-03-07\n0=3.1.44\n1=20250307\n")

    rv.set_mappings(str(path), "3.1.45", date(2026, 9, 22))

    lines = path.read_text().splitlines()
    assert lines == ["# header", "2=2026-09-22", "0=3.1.45", "1=20260922"]


def test_mappings_appends_a_missing_entry(tmp_path):
    path = tmp_path / "about.mappings"
    path.write_text("0=3.1.44\n")

    rv.set_mappings(str(path), "3.1.45", date(2026, 9, 22))

    contents = path.read_text()
    assert "1=20260922" in contents
    assert "2=2026-09-22" in contents


# ---------------------------------------------------------------------------
# bundle discovery
# ---------------------------------------------------------------------------

def _tree(tmp_path):
    for name in ("org.mwc.cmap.legacy", "org.mwc.debrief.core"):
        (tmp_path / name / "META-INF").mkdir(parents=True)
        (tmp_path / name / "META-INF" / "MANIFEST.MF").write_text(
            "Bundle-Version: 1.0.1\n")
    (tmp_path / "org.mwc.cmap.combined.feature").mkdir()
    (tmp_path / "org.mwc.cmap.combined.feature" / "feature.xml").write_text(
        '<feature version="1.1.1"/>')
    # a README-only stub, like org.mwc.debrief.satc.core in the real repo
    (tmp_path / "org.mwc.debrief.satc.core").mkdir()
    (tmp_path / "org.mwc.debrief.satc.core" / "README.md").write_text("hi")
    (tmp_path / "docs").mkdir()
    return tmp_path


def test_discover_bundles_classifies_plugins_features_and_stubs(tmp_path):
    bundles = rv.discover_bundles(str(_tree(tmp_path)))
    assert bundles == {
        "org.mwc.cmap.legacy": "plugin",
        "org.mwc.debrief.core": "plugin",
        "org.mwc.cmap.combined.feature": "feature",
    }
    assert "org.mwc.debrief.satc.core" not in bundles
    assert "docs" not in bundles


def test_affected_reports_paths_that_match_no_bundle(tmp_path):
    """BuildSupport swallowed these; they should be visible."""
    bundles = rv.discover_bundles(str(_tree(tmp_path)))
    touched, skipped = rv.affected([
        "org.mwc.cmap.legacy/src/MWC/GUI/Layers.java",
        "README.md",
        "docs/ARCHITECTURE.md",
        "org.mwc.debrief.satc.core/README.md",
        "org.mwc.debrief.product/debriefng.product",
    ], bundles)

    assert touched == {"org.mwc.cmap.legacy"}
    assert skipped == ["README.md", "docs/ARCHITECTURE.md",
                       "org.mwc.debrief.satc.core/README.md"]
    # the product dir is versioned explicitly, so it is not "unmatched"
    assert not any("org.mwc.debrief.product" in p for p in skipped)


# ---------------------------------------------------------------------------
# features and tags
# ---------------------------------------------------------------------------

def test_features_follow_the_plugins_that_ship_in_them():
    """A moved plugin needs its feature moved, or P2 never offers it."""
    assert rv.features_for({"org.mwc.debrief.core"}) == {
        "org.mwc.debrief.combined.feature"}
    assert rv.features_for({"org.mwc.cmap.legacy"}) == {
        "org.mwc.cmap.combined.feature"}
    assert rv.features_for({"org.mwc.asset.comms"}) == {
        "org.mwc.asset.core.feature"}
    assert rv.features_for({"org.eclipse.nebula.jface.cdatetime"}) == set()


def test_release_tag_matches_the_published_naming():
    assert rv.release_tag("3.1.44", date(2025, 3, 7)) == "20250307_3_1_44"
    assert rv.release_tag("3.1.45", date(2026, 9, 22)) == "20260922_3_1_45"
    assert rv.TAG_PATTERN.match(rv.release_tag("3.1.45", date(2026, 9, 22)))
