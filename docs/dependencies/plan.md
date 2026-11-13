# Debrief / Debrief-Lite dependencies: baseline, then security patches

## Context
Debrief and Debrief-Lite are in the same repo (`debrief/debrief`; Lite is `org.mwc.debrief.lite`, built with Ant). The platform itself is up to date (Java 21, Tycho 5.0.4, Eclipse 2026-09). The libraries are not: 1,184 jars are committed to the repo, Maven doesn't manage them, and several have known CVE families. Nothing scans them. CodeQL is on `@v1`, there is no SBOM, and no test compares output against golden files.

The owner chose **baseline + security first**. The later work is gated: Gate A → Phase B (move jars to Maven) → Phase C (automated update PRs) → Gate D → Phase D1 (GeoTools) and D2 (Lite UI libraries). D3 (Jackson 1 and the old Postgres drivers) can start after Gate A, and E (test tooling) after Phase B. Each phase gets its own spec and PRs.

The work goes in `debrief/debrief` (branch from `develop`, PRs to `develop`).

## Findings that shape the plan
- Lite takes **every** jar in `cmap.legacy`, `cmap.legacy/libs`, `debrief.legacy`, `debrief.legacy/libs`, `cmap.jfreechart` and `cmap.geotools` (`org.mwc.debrief.legacy/build.xml:65-70`, `org.mwc.debrief.lite/build.xml:69-77,120-129,~212`). So it ships two versions of POI (3.14 and 4.1.2), xmlbeans (2.6 and 3.1) and jackson-core (2.9.9 and 2.11.2). The RCP build uses only the MANIFEST copies.
- **Lite's runtime classpath was a hand-written list** (`org.mwc.debrief.lite/conf/manifest.txt`), not the jars it ships. Found in PR 2: Lite actually ran POI 3.14 and xmlbeans 2.6, couldn't load Jackson 2 databind or commons-compress, and listed 20 jars that didn't exist. PR 2 generates the `Class-Path` from the shipped jars.
- **Not referenced by the RCP build:** `debrief.legacy/libs/poi-*-3.14*`, `xmlbeans-2.6.0` (`.classpath` only), and `asset.comms/libs/RestLet/lib/*xstream*` (that module isn't an OSGi bundle).
- **No source imports:** `commons-text-1.6`, `hsqldb-2.2.8` and `jackson-core-2.9.9`, all in `cmap.geotools`. GeoTools 21.2 probably needs them at runtime; `hsqldb` almost certainly, for `gt-epsg-hsql`.
- **Where each library is used, and whether a test covers it:**

  | Library | Used in | Tested? |
  |---|---|---|
  | XStream | `cmap.xyplot/.../XYPlotView.java:1112,1256` | No |
  | POI | `cmap.legacy/.../Word/NarrativeWriter.java` (untested); `debrief.legacy/.../Word/Import*` | Word import tested |
  | Jackson 2 | `debrief.legacy/.../GeoPDF/*` | Yes |
  | jsoup | `debrief.legacy/.../powerPoint/*` | Yes |
  | commons-compress | `debrief.legacy/.../csv_gz/Import_CSV_GZ.java` | Yes |
  | sqlite-jdbc | `pepys/.../SqliteDatabaseConnection.java` | Pepys tests, skipped on macOS |
  | postgresql 42.2.11 | `pepys/.../PostgresDatabaseConnection.java` | No |
  | postgresql 8.3 | `asset.legacy`, `gnd.demonstrator` | No |
  | Jackson 1.9.4 (codehaus) | 10 imports in `cmap.legacy`, GNDManager, `asset.legacy` | — |

- **Changing a jar in a bundle means editing 4–5 places** in step: the jar itself, `MANIFEST.MF` `Bundle-ClassPath`, `Export-Package` where packages are listed (`cmap.legacy` exports 163 POI packages; `cmap.core` exports 16 XStream ones), `build.properties`, and `.classpath`. Lite picks jars up by folder glob, and since PR 2 its `Class-Path` is generated from the same set, so it follows automatically.

## PR 1 — Baseline (one library change: XStream)

The new XY-plot test showed that XStream 1.4.12 can't create an `XStream` on Java 21, so saving and restoring XY plots was already broken. The owner chose to move the XStream bump (PR 3 item 1) into PR 1. See [the baseline](baseline-2026-11.md) for this and the scan's other findings.

1. **Scan workflow** `.github/workflows/dependency-scan.yml`, on PRs to `develop`, weekly, and on manual dispatch.
   - Syft builds a CycloneDX SBOM from the committed jars (it reads `META-INF/maven` inside each jar), excluding `org.mwc.debrief.ui_test/rcptt-runner/**` and `contribs/**`. Those are reported separately.
   - Grype scans the SBOM. The SBOM and report are uploaded as artefacts.
   - **Report-only at first.** Severity gating is turned on in PR 3+.
2. **Pin the scanner** with a scanner config file in `.github/` (Grype's ignore list, with reasons). An exception has to be justified in writing.
3. **Golden-file tests** for the paths the upgrades will touch, in the existing tests where possible.
   - **Lite headless:** extend `org.mwc.debrief.lite/src/test/java/org/mwc/debrief/lite/headless/FileRoundTripTest.java` to compare the saved DPF from `sample.dpf` and `boat1.rep` with committed golden files. Volatile fields such as timestamps are masked; `HeadlessFixture.SAMPLE_DATA` is reused.
   - **RCP side**, in `org.mwc.debrief.test2` / nested `testMe` classes:
     - GeoJSON output from `GenerateGeoJSON` (sample.dpf) against a golden file, as canonical JSON.
     - `NarrativeWriter.TestMe` already writes a .docx and reads it back with POI, but it wasn't in `AllTests`, so it never ran. Register it.
     - A **new** XYPlotView state round-trip that serialises through XStream and back. If the view can't be built headless, extract the XStream call into a testable helper.
     - Word import, already covered by `TestImportWord`.
     - Pepys sqlite queries, already covered.
   - **Golden files** go in `org.mwc.debrief.legacy/test_data/golden/` and are regenerated with a documented system property.
4. **Baseline record:** `docs/dependencies/baseline-2026-11.md` lists every jar with its version, path and the bundles that use it, the CVE counts by severity, the duplicates and the apparently unused jars.
5. **Dependabot** `.github/dependabot.yml`, for `github-actions` only. It also fixes `codeql-action@v1`, `checkout@v2` and `GabrielBB/xvfb-action@v1`.
6. **Spec:** `specs/002-dependency-security/spec.md` (Spec Kit, per repo convention), covering PRs 1–5 and their acceptance criteria.

## PR 2 — Remove duplicates and unused jars
- Delete `debrief.legacy/libs/poi-*-3.14*` and `xmlbeans-2.6.0`, and update `.classpath`. Lite then uses POI 4.1.2 and xmlbeans 3.1.0, the same versions as the RCP build.
- Delete `asset.comms/libs/RestLet/lib/{com.thoughtworks.xstream,org.restlet.ext.xstream}.jar` and update `.classpath`.
- ~~Remove `commons-text-1.6` and `jackson-core-2.9.9` from `cmap.geotools`~~ **Kept.** They are real runtime dependencies of GeoTools 21.2: `gt-main`'s `StaticGeometry` filter functions use commons-text, and `JsonPointerFunction` uses jackson-core. GeoTools loads both as plugins, and every main `gt-*` jar names them in its own `Class-Path`. They get bumped in PR 3+ instead.
- **Generate Lite's `Class-Path`** from the jars it ships (`lite.runtime.libraries` in `org.mwc.debrief.lite/build.xml`), replacing the hand-written list. Lite then also loads the jars it shipped but never loaded (Jackson 2, commons-compress, the rest of GeoTools and others). Lite left out `jackson-core-2.9.9` until the Jackson alignment: the GeoTools jars' own `Class-Path` put it ahead of 2.11.2, which broke `jackson-databind` 2.11.2 (`NoSuchMethodError`).
- Fix the GNDManager `.classpath`/MANIFEST mismatch.

## PR 3+ — Security bumps, one library per PR, ordered by exposure
Versions are set at implementation time from Maven Central (latest stable); the ones below are expected targets.

| Order | Library | Expected target | Notes |
|---|---|---|---|
| 1 | XStream (`cmap.core`) | 1.4.21 | **Done in PR 1.** The package list is unchanged; the allow-list is in `XYPlotStateXml`. |
| 2 | postgresql (`pepys`) | — | **Removed instead** (owner's decision): Debrief no longer connects to Postgres. Pepys reads SQLite only; the ASSET record-to-database observer and the GND demonstrator are gone, with all four Postgres/PostGIS drivers. |
| 3 | sqlite-jdbc (`pepys`) | — | **Removed with the Pepys plugin** (owner's decision: the Pepys database is retired). That takes sqlite-jdbc, c3p0, mchange-commons and the Windows SpatiaLite DLLs (SQLite 3.31.1, zlib 1.2.8) with it. |
| 4 | Jackson 2 (`debrief.legacy`, `geotools`) | 2.22.4 (annotations 2.22) | **Done.** One version in both bundles. 2.23.0 was 4 days old, so the latest 2.22 patch was used. `cmap.geotools` exports `core.exc` as well. Lite's jackson-core exclusion is gone. |
| 5 | jsoup (`debrief.legacy`) | 1.23.2 | **Done.** 1.24.1 was 10 days old. The PowerPoint golden test caught a change: jsoup now lower-cases attribute names set on detached elements (`nodeType` became `nodetype`), so `PlotTracks` sets attributes with `attributes().put`, which keeps case. |
| 6 | commons-compress | 1.27+ | Needs newer commons-io and lang3; check the other copies of both. |
| 7 | POI + xmlbeans | 5.x / 5.x | Brings in new runtime jars: log4j-api, SparseBitSet, commons-math3, collections4. Larger PR. |
| 8 | commons-text, hsqldb (if kept) | Latest that works with GeoTools 21.2 | If GeoTools won't accept it, record the exception and defer to the GeoTools spec. |
| 9 | h2 (`geotools`) | — | **Removed instead.** Nothing used it: `h2-1.4.200`, `gt-jdbc-h2` and `gt-jdbc` (also Critical) went from `cmap.geotools`, and the unused `gt-jdbc-spatialite-20.5` from Pepys. |
| 10 | logback (`cmap.media`) | — | **Replaced.** It was only Xuggler's SLF4J binding: now slf4j-api + slf4j-simple 1.7.36 (slf4j-api 1.5.6 would fail with no binding). |
| 11 | Pepys native SQLite and zlib DLLs; c3p0, mchange-commons | — | **Removed with the Pepys plugin** (see item 3). |
| 12 | zip4j, commons-io, guava | Current | Found by the baseline scan. |
| — | Jackson 1.9.4 (codehaus) | — | **Not bumped.** No fixed versions exist. Tracked as a follow-up spec (migrate to Jackson 2). |

**Each PR:** update the jar, MANIFEST, `Export-Package`, `build.properties` and `.classpath`; then rescan, and the CVE count for that library must fall to zero (or each remaining CVE is excepted with a reason). The scan gate (fail on new High/Critical) is switched on once the first bump lands.

**Native libraries.** ~~`debrief.legacy/src/native/windows` is a bundled GDAL and MapServer toolchain~~ **Done:** GeoPDF export now writes the PDF in Java (PDFBox, `GeoPdfWriter`) and reprojects background charts with GeoTools, so the 95 MB GDAL/MapServer bundle (with OpenSSL 1.1, libcurl, libpq, libxml2 and zlib) is removed, from Lite too. Charts on NAD27-style datums may sit up to the EPSG transformation's stated accuracy (about 10 m) from where GDAL's grid-based shift put them.

## Verification for every PR
1. `mvn clean verify -U -B -fae` under xvfb with JDK 21. If the container can't reach `download.eclipse.org`, CI is the verifier, and that is stated in the PR.
2. The `lite-ui-tests` job passes (`-DliteUiTests=true`).
3. The golden-file tests pass unchanged. Any regeneration has to be explained in the PR.
4. The scan artefact shows the target CVEs gone and no new High/Critical findings.
5. Smoke test from the CI artefacts: start the product on Linux under xvfb with a screenshot. Open `sample.dpf` and export a narrative to Word. On Windows and macOS, a manual check by the owner, listed in the PR.
6. Lite: run `dist/debrief-lite-<version>.zip`, open a REP file, step time.

## Gate A: before any restructuring
All of the following hold:
- PRs 1–3+ are merged.
- No High or Critical CVE is open without a written exception.
- Golden-file and Lite UI tests pass on `develop` for 2 consecutive weeks of scheduled runs.
- The owner has done the manual smoke test on Windows and macOS on a `develop` build.

## Phase B: move jars to Maven (after Gate A)
Spec `specs/003-maven-dependencies/`. Move the third-party jars to Maven Central coordinates.
- **How.** A `<location type="Maven">` in `org.mwc.debrief.targetplatforms/eclipse-latest.target` (and the `.tpd`). Bundles use `Import-Package` (or `Require-Bundle` on the wrapped bundle) instead of `Bundle-ClassPath`.
- **Order.** One bundle per PR: `debrief.legacy`, `cmap.legacy`, `cmap.core`, `cmap.jfreechart`. `cmap.geotools` waits for Phase C.
- **Lite's Ant build.** It gets its jars from a `maven-dependency-plugin:copy-dependencies` step in a new `org.mwc.debrief.lite/pom.xml` stage, not folder globs. That removes the second classpath.
- **Kept in the repo.** Jars with no Maven coordinates stay committed and listed in `docs/dependencies/vendored.md` with a reason: jmf, vrml97, flanagan, January, openmap, javalite.
- **Checks.** The full verification list, plus the same set of classes in the Lite dist zip and the RCP product: before and after, the sorted list of classes in the shipped jars differs only by version.

## Phase C: automated updates (after Phase B)
- **Weekly update PR.** A scheduled workflow runs `mvn org.eclipse.tycho.extras:tycho-version-bump-plugin:update-target`, which bumps the Maven locations and the p2 release URL, and opens the PR.
- **Dependabot or Renovate.** Renovate gets a regex manager for the `.target` file. Dependabot keeps `github-actions`.
- **Every bot PR** runs the full CI: build, Lite UI tests, golden files and scan.
- **No auto-merge.** We revisit this at Gate D.
- **New CVEs.** A weekly scan raises an issue for each one.

## Gate D: before the major migrations
- Phase B is complete.
- Golden-file coverage now includes **screenshot diffs of map rendering**. It renders a fixed set of plots and a shapefile at fixed sizes, headless, and compares against references with a per-pixel tolerance.
- At least 4 bot update PRs have merged cleanly.

## Phase D1: GeoTools 21.2 → current (after Gate D)
Spec `specs/004-geotools/`.
- **API change.** `org.opengis` → `org.geotools.api`, using GeoTools' migration script.
- **Replace `gt-swt-23-SNAPSHOT`.** GeoTools no longer publishes gt-swt, so we port the classes we use into `cmap.geotools`.
- **Align** `gt-jdbc-spatialite`.
- **Drop the GeoTools pins.** commons-text and hsqldb are no longer pinned by GeoTools.
- **Acceptance:** the screenshot diffs are within tolerance; coordinates are within 1 m when projected and transformed back on the sample plots; and the full verification list passes.

## Phase D2: Lite UI libraries (after Gate D; can run in parallel with D1)
- **Radiance** 3.0.0 → current: flamingo ribbon, substance, trident; packages and API change.
- **swingx** 1.6.6-SNAPSHOT → a released fork, or remove it.
- **Acceptance:** Lite UI tests pass and screenshot diffs of the Lite main window are within tolerance. The owner does a manual walkthrough on all three operating systems.

## Phase D3: Jackson 1 and the old Postgres drivers (after Gate A; can run in parallel with B)
- **Jackson 1 → Jackson 2.** Migrate the 10 codehaus imports (`cmap.legacy`, GNDManager, `asset.legacy`), then delete the 1.9.4 jars. Golden JSON tests come first.
- **Postgres 8.3.** Done early, with the Pepys driver: all PostgreSQL support was removed (see PR 3+ item 2).

## Phase E: test tooling (after Phase B)
- **JUnit 4.12 → JUnit 5** with the vintage engine. Tycho Surefire supports it, and the existing tests run unchanged.
- **RCPTT runner 2.5.0** (about 600 committed jars): replace it with the current RCPTT Maven plugin, or retire it in favour of the screenshot and headless tests. **The owner decides at Gate D.**
