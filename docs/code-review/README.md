# Code review findings register

`findings.csv` is the output of an LLM-assisted, read-only review of the whole Debrief repository (branch `develop`, commit `4af8b988`, September 2026). Focus: algorithmic correctness, security, and general bug-finding. It is a triage register, not a list of confirmed bugs: every row is a *candidate* until someone reproduces it with a failing test or proof-of-concept input.

Open the CSV in a spreadsheet, sort and filter on the metadata columns, edit descriptions, and transfer accepted rows to GitHub issues (see the last section).

## Summary

| | Count |
|---|---|
| Rows | 264 |
| Critical | 2 |
| High | 47 |
| Medium | 130 |
| Low | 85 |

By category: security 33, algorithm-correctness 64, bug 124, supply-chain 38, ci-workflow 5.
By status: candidate 224, verified 37, unverified-needs-runtime 3.

Themes that recur across slices, each worth one fix rather than many:

- No XML parser in the ingestion path sets secure-processing or disables DOCTYPE, so `.dpf`, `.xml`, `.kml`, `.gpx` and ASSET scenario files can trigger external entity resolution. One hardened factory helper fixes most rows.
- Numeric attributes are parsed with a prefix-only `DecimalFormat` and failures are swallowed to a trace file, so malformed values silently become 0 or a truncated number in both the XML and Replay importers.
- Bearing and course arithmetic is folded inconsistently: 180 used where 360 is meant, radians compared with degree thresholds, 360-degree wrap applied to ranges and frequencies during resampling, no longitude wrap across 180° in the default earth model.
- Time deltas are computed in integer seconds or built with milliseconds passed as microseconds, giving infinite speeds or 1970 timestamps.
- `compareTo` implementations that return non-zero for distinct equal-time items make `TreeSet.remove` fail silently, so cuts and segments are dropped without error.
- Output filenames are built from track names that come from the data file, so exports can write outside the chosen directory.
- Bundled jars are old (zip4j 1.3.2 zip-slip, XStream 1.4.12, PDFBox 2.0.3, JDOM 1.0, a 2019 JRE shipped on Windows); several are dead weight that is not on any Bundle-ClassPath.
- The CodeQL workflow is disabled for inactivity and never ran; a fork PR can rewrite another PR's review-build comment.

One row filed by a reviewer was removed during merge because its cited manifest lines did not exist (it claimed POI 3.14 was on the classpath; the scripted supply-chain cross-check showed POI 4.1.2 is what ships). Treat that as the false-positive rate indicator: cross-check `instances` before acting on a row.

## How to read a row

| Column | Meaning |
|---|---|
| `id` | Stable key `CR-001`… Do not renumber; new rows get new ids. |
| `slice` | Which review slice produced it (table below). `A1+A6` means two slices found the same (file, issue type) and were merged. |
| `plugin` / `package` / `class` | Eclipse plugin directory, Java package, simple class name. `-` for non-Java files. |
| `file_path` | Repo-relative path. |
| `category` | `security`, `algorithm-correctness`, `bug`, `supply-chain`, `ci-workflow`. |
| `issue_type` | Closed taxonomy (below). One row per (class, issue type); several occurrences in the same class are listed together in `instances`. |
| `severity` | See scale below. Reviewer's estimate of impact **if** the finding is real. |
| `confidence` | High / Medium / Low: reviewer's belief that the finding is real. Low-confidence Critical rows deserve the first look, not dismissal. |
| `status` | `candidate` (read but not executed), `verified` (reviewer executed a check, e.g. recomputed a constant), `unverified-needs-runtime` (needs a running workbench or network to confirm). |
| `title` | ≤ 80 chars, issue-title ready. |
| `description` | What is wrong and which invariant or threat is violated. |
| `instances` | Line references at the reviewed commit, e.g. `L68; L947-L952`. |
| `trigger_input` | Concrete input or scenario that exercises the problem; the seed for a reproducer. |
| `suggested_fix` | 1–3 sentences. Not a design decision. |
| `effort` | S (< half a day), M (1–2 days), L (more, or cross-cutting). |
| `llm_value` | Why finding this needed reading and reasoning rather than a grep or CodeQL rule, or `grep-able`. Useful for deciding what a future PR review bot should check. |
| `related` | CVE ids, GitHub issue refs (e.g. `#5237`), other `CR-` ids, or `-`. |
| `github_issue` | Blank. Fill in when a row is transferred. |

### Severity scale

- **Critical**: a data file from an untrusted source can execute code or write arbitrary files; or an algorithm silently produces wrong analytical results in a mainstream path.
- **High**: crash or denial of service from a data file; data loss; wrong results in a less common path; exploitable with user interaction.
- **Medium**: wrong results in edge cases; resource leak; robustness bug with a visible failure.
- **Low**: code smell with plausible but unlikely impact; dead or unreferenced code.

### Issue-type taxonomy

Security: `xxe`, `xml-bomb`, `zip-slip`, `zip-bomb`, `unsafe-deserialization`, `command-injection`, `path-traversal`, `ssrf-or-unvalidated-url`, `insecure-temp-file`, `unbounded-allocation`, `outdated-dependency`, `vulnerable-dependency`, `workflow-permissions`, `secrets-handling`.

Algorithms: `angle-wrap`, `unit-mismatch`, `float-equality`, `precision-loss`, `off-by-one`, `division-by-zero`, `sign-error`, `degenerate-input`, `interpolation-error`, `time-handling`, `projection-error`, `statistical-error`, `nondeterminism`.

General bugs: `null-deref`, `swallowed-exception`, `resource-leak`, `integer-overflow`, `thread-safety`, `locale-parsing`, `silent-data-loss`, `string-parsing`, `dead-code`, `api-misuse`, `logic-error`, `performance`.

## Threat model used for security rows

An analyst opens a data file of unknown provenance (`.rep`, `.dpf`/`.xml`, `.kml`/`.kmz`, `.docx`, `.pptx`, `.xlsx`, `.gpx`, NMEA/AIS logs). Also in scope: third-party jars bundled in `libs/` and `contribs/` (supply chain), and anything that shells out, loads classes by name, reads the network, deserialises Java objects, or writes outside the workspace (local privilege).

## How the review was run

Eleven independent reviewer sessions, one per slice, each read the code listed below against a shared specification (evidence bar: cite exact lines, the invariant or threat violated, and a concrete triggering input; no speculation rows; cap of about 25 rows per slice, ranked). No production code was changed and no Maven build was run. Slice outputs were merged, validated against the taxonomy and enum columns, checked that every `file_path` exists, de-duplicated on (file, issue type), and sorted by severity.

| Slice | Target |
|---|---|
| A1 | XML ingestion: `DebriefXMLReaderWriter`, `DebriefEclipseXMLReaderWriter`, `ImportKML`, ASSET XML readers, NaturalEarth, KML transfer, `MWC.Utilities.ReaderWriter.XML` |
| A2 | Archives and Office formats: PowerPoint pack/unpack, KMZ, Word import, POI usage, export path handling |
| A3 | Deserialization, process execution, reflection, network, temp files, DIS |
| A4 | Supply chain: bundled jars, `contribs/` source copies, target platform, JRE download |
| A5 | `MWC.Algorithms` and `MWC.GenericData`: conversions, projections, earth models, world types, `HiResDate` |
| A6 | `Debrief.Wrappers`: tracks, fixes, sensors, TMA segments, interpolation |
| A7 | `org.mwc.debrief.track_shift`: zig detection, ambiguity resolution, residuals |
| A8 | ASSET simulation core and genetic algorithm (`org.mwc.debrief.satc.core` has no source in this repo; the GA lives in `org.mwc.asset.legacy`) |
| A9 | Text importers: Replay, NMEA, AIS, Nisida, FlatFile, BRT, PCArgos, Antares, GeoPDF |
| A10 | RCP layer: context operations, loaders, editors, plot viewer, Lite |
| A11 | CI workflows, build, release tooling |

### Coverage and gaps, per slice

**A1 — XML ingestion.** Fully read: `MWCXMLReaderWriter`, `MWCXMLReader`, `DebriefXMLReaderWriter`, `DebriefEclipseXMLReaderWriter`, `XMLLoader`, `ImportKML` (XML side), `NEFeatureRootHandler`, `ASSETReaderWriter`, `ScenarioGenerator`, `CoreControllerPresenter`, `MultiScenarioPresenter`, the core value handlers (Fix, Short/Long Location, WorldSpeed, WorldDistance, Duration, TimeRange, Colour, Font, Property, TimeSeriesDouble), `GpxUtil`, `JaxbGpxHelper`, `DebriefFormatDateTime`, `Duration.fromString`, unit-index lookups, `TrackWrapper.addFix`/`TrackSegment.addFix`, `KMLTX_Presenter`, core `StepperHandler`. Skimmed: `SingleScenarioView` (XML code commented out), Sensor/Layer handlers, remaining `MWC.Utilities.ReaderWriter.XML.Util` handlers, DIS JAXB sites. Not covered: `TrackHandler`, TMA handlers, `SensorHandler` bodies, the ~60 `ASSET.Util.XML` handler classes beyond those named, `Debrief.ReaderWriter.XML.Shapes`, `Import_CSV_GZ`, DIS JAXB unmarshalling for XXE. Writer side uses DOM/Transformer so escaping is correct; no rows filed there beyond charset and swallowed-save-error issues. All rows `candidate`. Rows: High 8, Medium 15, Low 5.

**A2 — archives and Office formats.** Fully read: the whole `Debrief.ReaderWriter.powerPoint` package and its model classes, `CoreCoordinateRecorder`, the zip side of `ImportKML` and `Import_CSV_GZ`, GeoPDF builder config/temp-file/exec sections, the Word narrative importers' entry points and POI handling, the core loaders for KML/Word/PDF, the PowerPoint export dialog and preferences page, flat-file and Doppler exporters' write paths. Skimmed: narrative text parsing bodies, GeoJSON generation, `KMLTX_Presenter` (in-memory zip only). Not covered: test fixtures, `contribs/`. No Excel (XSSF/HSSF) usage exists outside `contribs/`, so nothing to review there. KML XXE and string-index bugs were handed to A1. Verified by reading the bundled zip4j 1.3.2 class that `extractAll` has no canonical-path check. Rows: High 5, Medium 10, Low 8.

**A3 — deserialization, process execution, network, temp files, DIS.** Fully read: `OpenPlot`, `RightClickCutCopyAdaptor` (EditableTransfer, cut/clone paths), `RMIDiscovery`, `RMILookup`, `JarServer`, `NetworkDISProvider`, `SimulationRunner`, `PduFactory`, `VariableDatum.unmarshal`, `DISModule`, `HostServer`, `ASSETResource`, `CloneUtil`, `WriteClipboard`, `RightClickPasteAdaptor`, Lite `OutlinePanelView`, `XYPlotView` memento restore, `AbstractGeoPDFBuilder` (gdal exec and temp files), `EFileChooser`, `S57Handler`, `Application` help launch, `PlotEditor`, `LoaderCore`, temp-file sites in metafile export. Skimmed: open-dis PDU unmarshal loops, `NetworkPduSender`, `DisListenerView`, restlet verbs. Not covered: unused `edu.nps.moves.disutil` servers, `org.mwc.asset.NetCore` kryonet, xmpp test code, Tif/Shape/Replay loader path handling. Dropped as non-findings: constant-string `hh` help exec; c3p0 gadget in `org.mwc.debrief.pepys` (classloader sharing not verified). Nothing executed; all rows `candidate`. Rows: High 3, Medium 7, Low 11.

**A4 — supply chain.** Every plugin `MANIFEST.MF` Bundle-ClassPath and `build.properties` was cross-referenced against the jars on disk (scripted), so each row states whether the jar is actually shipped. Notable correction for readers: the POI 3.14 and xmlbeans 2.6.0 jars in `org.mwc.debrief.legacy/libs` are **not referenced** by the manifest; POI resolves to 4.1.2 from `org.mwc.cmap.legacy`. All 100 library/version pairs were queried live against the OSV database through the proxy (raw results kept with the review session), so CVE lists are OSV-confirmed except Xuggler's native FFmpeg (no Maven coordinates) and the bundled JRE row. Not covered: CVE applicability inside GeoTools internals, the 2026 c3p0 advisories' text, jars inside `contribs/` individually (aggregated to one row), RCPTT runner jars. Hard-coded database credentials found in `RecordStatusToDBObserverType` were folded into the pgjdbc row. Rows: High 6, Medium 8, Low 16.

**A5 — core algorithms and data types.** Fully read: `Conversions`, `EarthModel`, `FrequencyCalcs`, `PlainProjection`, `FlatProjection`, `Mercator2`, `Mercator3`, `JMapTransformMercator`, `FlatEarth`, `CompletelyFlatEarth`; `WorldLocation`, `WorldVector`, `WorldDistance`, `WorldSpeed`, `WorldArea`, `HiResDate`, `Duration`, `TimePeriod`, `WorldAcceleration`; all `MWC.Utilities.TextFormatting` date and location formatters. Skimmed: `LiveData` (event plumbing), `WorldPath`, caller contexts in the plot viewer zoom tools and XML handlers. Not covered: `Watchable` interfaces, `Algorithms/Editors`. This slice compiled the real legacy classes against stubs and JUnit and executed probes, so 15 of 25 rows are `verified` by running the code (for example the missing cos(latitude) scaling in `perpendicularDistanceBetween`, no longitude wrap across 180° in the default `FlatEarth`, and the two-digit-year pivot in `DebriefFormatDateTime`). The three Mercator projections are dead code and their rows are Low. An out-of-slice observation for the importers: `WorldDistanceHandler` parses its Value attribute with `Integer.parseInt`. Rows: High 3, Medium 10, Low 12.

**A6 — Debrief wrappers.** Fully read: `TrackWrapper`, `FixWrapper`, `TrackSegment`, `TrackWrapper_Support`, `CoreTMASegment`, `RelativeTMASegment`, `AbsoluteTMASegment`, `DynamicInfillSegment`, `FixWrapperCollisionCheck`, `WormInHoleOffset`, `ArrayOffsetHelper`, `TacticalDataWrapper`, `SensorContactWrapper`, `SensorWrapper` (non-test code), `TMAWrapper`, `CompositeTrackWrapper`, `PlanningSegment`, `SplittableLayer`, `Doublet`. Skimmed: `LightweightTrackWrapper` (data-access methods), `TMAContactWrapper`, `DynamicTrackShapes`, supporting `Plottables`/`TimePeriod`/`HiResDate`. Not covered: shape, label, buoy-pattern and polygon wrappers, `Extensions/*`, `Formatters/*`, the `TrackWrapper_Test` assertions, dynamic coverage arithmetic. Eight rows were verified by reproducing the arithmetic or collection behaviour in a standalone JDK program (for example the 360° wrap applied to range and frequency during resampling, radians compared against a 180-degree threshold in TMA interpolation, and `compareTo` returning 1 for distinct equal-time cuts so `TreeSet.remove` silently fails). Rows: High 6, Medium 13, Low 4.

**A7 — track_shift (zig detection, ambiguity resolution, residuals).** Fully read: `AmbiguityResolver`, `LegOfCuts`, `ZigDetector`, `SCStatistics`, `SCAlgorithms`, `AlternateLegWrapper`, `Steady_course`, all ownship leg detectors, the moving-average classes, `StackedDotHelper`, `BearingResidualsView`, `FrequencyResidualsView`, the Doppler curve fitters (`DopplerCurve`, `DopplerCurveFinMath`, logistic/sigmoid models), `Normaliser`, `InflectionPointDetector`, `ResolveAmbiguity`, `WrappingResidualRenderer`; legacy `Doublet` bearing-error wrap handling checked and found correct. Skimmed: `BaseStackedDotsView` calculation and slicing paths only, `ZoneChart` zone maths only (undo operations and toolbar not reviewed). Not covered: `magic/OptimiseTest` (experiment), axis/renderer helpers, preferences, activator. Six rows were verified by recomputing the arithmetic in Python (course-unwrap typo, 180-vs-360 fold, degrees-passed-as-radians, integer division in the sample-variance correction, list desync in the time-restricted moving average). Also noted but not filed: two `ZigDetector` tests have no assertions and pass seconds where milliseconds are expected, so they exercise nothing. Rows: High 2, Medium 11, Low 12.

**A8 — ASSET simulation core and genetic algorithm.** Fully read: `Scenario/Genetic` (GA, Gene, ScenarioRunner), `Util/RandomGenerator`, `Util/IdNumber`, the whole `Util/MonteCarlo` variance/permutation package, `CoreScenario`, `CoreParticipant`, `Status`, `DemandedStatus`, movement core and `TurnAlgorithm`, `CoreSensor`, `SensorList`, the initial-detection sensors (broadband, narrowband, active), environment and medium models, radiated characteristics, detection events and lists, the core decision models, and the file-recording and summary observers. Skimmed: `ScenarioGenerator` core methods, lookup sensors, replay/CSV/NMEA observers (writer lifecycle only). Not covered: tactical decision behaviours, waypoint movement, lookup sensor internals, dipping/bistatic/optic sensors, TMA handler, live and multi-force scenarios, server, plotting observers, GUI. Both Critical rows (own-ship self-noise always 0; narrowband sensor fabricating a detection for any narrowband target) were re-read and confirmed by the merging reviewer. Rows: Critical 2, High 4, Medium 20, Low 3.

**A9 — text importers.** Fully read: every `Debrief.ReaderWriter.Replay` line importer and formatter (fixes, sensors, TMA, narratives, shapes, planning legs, towed-array extensions) plus `FormatTracks`; `ImportNMEA`; the whole `ais` package (parser, 6-bit decoder, position and static messages); `ImportNisida`; `FlatFile` exporters and importers (`FlatFileExporter`, `OTH_Importer`, `CLogFileImporter`, `ImportSATC`, `NMEA_Radar_FileImporter`, `DopplerShiftExporter`); BRT, PCArgos and Antares importers; the generic `MWC.Utilities.ReaderWriter` base classes and date/location formatters; `MWCXMLReader.readThisDouble` (shared with A1) and the DMS constructor of `WorldLocation`. Skimmed: GeoJSON date formatting, importer tests, `Layers.findLayer`. Not covered: Swing range-data import UI, GeoPDF builders, JSON utilities, progress monitor. Nine rows were verified by executing the JDK behaviour (for example the prefix-only `DecimalFormat` parse, the 12-hour `hh` pattern in the radar importer, the default-locale month parse in Nisida). Rows: High 7, Medium 17, Low 1.

**A10 — RCP layer.** Fully read: all 33 classes in `org.mwc.debrief.core` `ContextOperations`; the core loaders (`CoreLoader`, `LoaderManager`, Replay, XML, KML, GPX and `GpxUtil`, Word, PDF, TIF, Shape, CSV.GZ); plot viewer drag/zoom/pan/range tools, `SWTChart`, `SWTCanvas`, `CorePlotEditor`, WMF/RTF export; `cmap.core` operation and paste/cut adaptors; `ExportDopplerShift`; Lite time controller. Skimmed: `PlotEditor` save/load flows, `PlotOutlinePage` handlers, `RightClickSupport` dispatch, Lite file open/save and slider conversions, property-support helpers. Not covered: the other core loaders (AIS, Antares, BRT, CLog, LogTrack, NMEA, Nisida, OTH, SATC, UK CSV), painters, property sheet page, GPX mappers, wizards and preferences, `cmap.core` data types and widgets, Lite outline/map/menu packages. Undo operations were reviewed for whether undo restores the model exactly; several do not. Rows: High 3, Medium 16, Low 6.

**A11 — CI workflows, build, release tooling.** Fully read: all three workflows, `auto_assign.yml`, `.travis.yml`, root and product `pom.xml`, `install-msitools.sh`, `contribs/msi/make_x64msi.sh`, `contribs/PyScripts/release_versions.py`, `.claude/settings.local.json`, the release skill, `RELEASING.md`, the legacy PDE build files. Skimmed: target platform URLs, product file, WiX sources, release-script tests, feature plugin lists. Not covered: checkstyle config and its old jars (checkstyle 5.6, jsch 0.1.44, used only by a dead Ant scp target), speckit commands, GitHub repository settings (tag protection, branch rules) which cannot be read from the checkout. One row verified against the Actions API: the CodeQL workflow is disabled for inactivity with zero runs. Rows: High 3, Medium 8, Low 8.

## Where the LLM review added value, and where it did not

The `llm_value` column records this per row. In aggregate:

- **Added value**: cross-file invariant checks (bearing range conventions, unit assumptions at call boundaries), reading uncommented legacy code and stating what it actually does against `DOMAIN_GLOSSARY.md`, degenerate-input reasoning (empty track, duplicate timestamps, dateline and pole crossing), and turning a taint path into a concrete proof-of-concept description.
- **Limited value, guarded by `confidence` and `status`**: numeric claims without execution (a few were checked with short Python computations and are marked `verified`), anything needing the live RCP workbench (marked `unverified-needs-runtime`), and CVE recall (see the A4 coverage note for whether the OSV database was reachable).
- **Grep-able rows** are kept because they are still real, but they are the ones a static rule or a future PR review bot could enforce without an LLM.

## Transferring a row to a GitHub issue

1. Filter to the rows you accept; set `status` to `rejected` on the rest (keep them, so the reasoning is not redone).
2. For each accepted row, create an issue titled from `title`, body from `description`, `instances`, `trigger_input`, `suggested_fix`; label by `category`; link `related`.
3. Write the issue number into `github_issue` and commit the CSV.
4. The first fix PR for a row should add the failing test or PoC input first, then the fix.

## Suggested next steps

- Reproduce the Critical and High security rows first; several share one fix (a hardened XML parser factory).
- Fold the `grep-able` rows and the recurring patterns into a `REVIEW.md` checklist for a per-PR review bot.
- Hand `supply-chain` rows tagged `#5237` to the Java 25 upgrade, which replaces most of the bundled jars.
