# Feature Specification: Dependency Baseline and Security Patches

**Feature Branch**: `002-dependency-security`
**Created**: 2026-11-12
**Status**: Draft
**Input**: The baseline and security phase of [docs/dependencies/plan.md](../../docs/dependencies/plan.md): PR 1 (baseline), PR 2 (duplicates and unused jars) and PR 3 onwards (one security bump per library), up to Gate A.

## Background

Debrief and Debrief Lite ship third-party jars committed to the repo, not managed by Maven. Before this work nothing scanned them, and no test compared Debrief's output against a known-good copy, so a library upgrade could change behaviour unnoticed. [The baseline](../../docs/dependencies/baseline-2026-11.md) found 15 Critical and 120 High vulnerabilities in the shipped libraries.

## User Scenarios & Testing *(mandatory)*

### User Story 1 - See the vulnerabilities in what we ship (Priority: P1)

A maintainer opens a pull request, or looks at the weekly scheduled run, and sees which committed libraries have known vulnerabilities, how severe they are and which versions fix them, without running anything locally.

**Why this priority**: Nothing can be prioritised or verified until the exposure is visible, and kept visible as the database of known vulnerabilities grows.

**Independent Test**: Run the `Dependency scan` workflow by hand. Its summary lists the vulnerable packages, and its artefact holds the SBOMs and reports.

**Acceptance Scenarios**:

1. **Given** a pull request to `develop`, **When** CI runs, **Then** the `Dependency scan` job produces a CycloneDX and a Syft SBOM of the committed libraries and a Grype report, uploaded as the `dependency-scan` artefact.
2. **Given** the scan, **When** it reports, **Then** the RCPTT runner and `contribs/` are reported separately from the shipped products.
3. **Given** a jar that Syft can't identify, **When** the report is written, **Then** it is listed as not scanned rather than counted as clean.
4. **Given** report-only mode, **When** findings exist, **Then** the job still passes.

---

### User Story 2 - Know when an upgrade changes Debrief's output (Priority: P1)

A maintainer upgrades a library and the tests tell them whether Debrief writes different files, or can no longer read files it wrote before.

**Why this priority**: The security bumps are only safe if a behaviour change shows up as a failing test.

**Independent Test**: Change a golden file by one character. The matching test fails, names the file and the first differing line, and writes the actual output somewhere the developer can diff it.

**Acceptance Scenarios**:

1. **Given** Lite saves a plot loaded from `sample.dpf` or `boat1.rep`, **When** the headless tests run, **Then** the saved DPF matches the golden file, with run-dependent fields masked.
2. **Given** the tracks in `sample.dpf`, **When** GeoJSON is generated, **Then** it matches the golden file as canonical JSON, whatever the machine's time zone.
3. **Given** a narrative, **When** it is exported to Word, **Then** reading the `.docx` back with POI gives the same rows.
4. **Given** an XY plot dataset, **When** it goes through the XStream memento and back, **Then** the dataset is unchanged. A memento committed as a golden file still loads.
5. **Given** an intended output change, **When** the tests run with `DEBRIEF_GOLDEN_REGENERATE=true`, **Then** the golden files are rewritten, and the pull request explains the diff.

---

### User Story 3 - Remove the copies we don't need (Priority: P2)

Lite ships two versions of POI, xmlbeans and jackson-core, and the repo holds jars that no build uses. Removing them reduces exposure without any upgrade risk.

**Independent Test**: The jar lists are compared before and after. The full verification list passes.

**Acceptance Scenarios**:

1. **Given** PR 2, **When** it merges, **Then** POI 3.14 and xmlbeans 2.6 are gone from `debrief.legacy/libs`, and Lite ships the same POI and xmlbeans as the RCP build.
2. **Given** a jar proposed for removal from `cmap.geotools`, **When** the RCP product and Lite start and render a shapefile and NaturalEarth, **Then** it is removed. Otherwise it is kept and bumped instead.

---

### User Story 4 - Patch the vulnerable libraries (Priority: P2)

Each vulnerable library is upgraded in its own pull request, ordered by exposure, and the scan proves its vulnerabilities are gone.

**Independent Test**: Each bump PR's scan artefact shows zero findings for that library, or an exception for each one left.

**Acceptance Scenarios**:

1. **Given** a bump PR, **When** it merges, **Then** the jar, `MANIFEST.MF` (`Bundle-ClassPath` and `Export-Package`), `build.properties` and `.classpath` all name the new version.
2. **Given** a bump PR, **When** the scan runs, **Then** that library has no remaining findings that aren't excepted in `.github/grype.yaml` with a reason.
3. **Given** the first bump has merged, **When** a later PR adds a High or Critical finding, **Then** the scan job fails.

### Edge Cases

- A library has no fixed version (Jackson 1.9.4, postgresql 8.3). It gets a written exception, and the follow-up spec removes it.
- GeoTools 21.2 won't accept a newer commons-text or hsqldb. The exception is recorded and the work deferred to the GeoTools spec.
- A native library (DLL) isn't identified by Syft. It is listed by hand in the baseline and in the native-libraries follow-up.
- The scan's vulnerability database changes between runs. New findings on an unchanged tree are reported by the weekly run, not blamed on a PR.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: A workflow MUST scan the committed libraries on pull requests to `develop`, weekly, and on manual dispatch.
- **FR-002**: The scanner versions MUST be pinned in the workflow. The scan policy and exceptions MUST live in `.github/grype.yaml`, and each exception MUST carry a reason.
- **FR-003**: The scan MUST upload the SBOMs (CycloneDX and Syft JSON) and the Grype reports as artefacts, and write a summary to the job.
- **FR-004**: The scan MUST be report-only until the first security bump lands. After that it MUST fail on new High or Critical findings.
- **FR-005**: Golden-file tests MUST cover Lite's DPF save, GeoJSON output, the narrative Word export, and the XY plot XStream memento.
- **FR-006**: Golden files MUST be regenerable with one documented switch, and a mismatch MUST report the file, the first difference and where the actual output was written.
- **FR-007**: Dependabot MUST keep the workflows' GitHub Actions up to date.
- **FR-008**: The baseline MUST record every jar with its version, path and users, the vulnerability counts by severity, the duplicates and the apparently unused jars.
- **FR-009**: Each library bump MUST update the jar and every file that names it in step.

### Key Entities

- **SBOM**: a list of the libraries found in the repo, with versions and locations.
- **Golden file**: committed output that a test compares against.
- **Exception**: an ignore rule in `.github/grype.yaml`, with its reason and tracking issue.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: Every PR to `develop` shows the vulnerability summary for the shipped libraries.
- **SC-002**: The golden-file tests pass on `develop` and fail when the output changes.
- **SC-003**: After PR 3+, no High or Critical finding is open without a written exception (Gate A).
- **SC-004**: Golden-file and Lite UI tests pass on `develop` for 2 consecutive weeks of scheduled runs (Gate A).

## Assumptions

- Syft identifies a jar from its Maven metadata, manifest or file name. A jar it can't identify is reported as not scanned.
- CI is the verifier where the container can't reach `download.eclipse.org`.
- Manual smoke tests on Windows and macOS are done by the owner and listed in the PR.

## Delivered by PR 1

- `.github/workflows/dependency-scan.yml`, `.github/grype.yaml`, `.github/scripts/dependency_report.py`, `.github/dependabot.yml`.
- Golden-file tests and `org.mwc.debrief.legacy/test_data/golden/`.
- `docs/dependencies/baseline-2026-11.md`.
- **Moved forward from PR 3:** XStream 1.4.12 → 1.4.21, with an allow-list. The new test showed 1.4.12 can't create an `XStream` on Java 21, so saving and restoring XY plots was broken. The memento also leaves out JFreeChart's series listener fields, which Java 21 won't let XStream reach, and re-links each series on reading.
