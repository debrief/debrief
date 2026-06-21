# Debrief Capability Audit

> Broad but shallow audit of Debrief capabilities to support porting to a new architecture.
> Sources: PDF tutorials, DebriefNG user documentation, cheat sheets, repo documentation, code inspection.
>
> **Deep links:** code references in the tables below link to the implementing source file, formats
> link to representative [sample data](org.mwc.cmap.combined.feature/root_installs/sample_data), and
> the domain types link to their definitions. Links are repo-relative and resolve on the current branch.

**Reference documentation:** [`CLAUDE.md`](CLAUDE.md) ·
[`ARCHITECTURE.md`](ARCHITECTURE.md) ·
[`KEY_CLASSES.md`](KEY_CLASSES.md) ·
[`DOMAIN_GLOSSARY.md`](DOMAIN_GLOSSARY.md) ·
plugin READMEs:
[legacy](org.mwc.debrief.legacy/README.md) ·
[cmap.legacy](org.mwc.cmap.legacy/README.md) ·
[core](org.mwc.debrief.core/README.md) ·
[track_shift](org.mwc.debrief.track_shift/README.md) ·
[plotViewer](org.mwc.cmap.plotViewer/README.md) ·
[satc.core](org.mwc.debrief.satc.core/README.md)

**Sample data:** the [`sample_data/`](org.mwc.cmap.combined.feature/root_installs/sample_data)
directory holds 287 example files; a full inventory is in the `SAMPLE_DATA.md` catalog (added in
[#5221](https://github.com/debrief/debrief/pull/5221)).

---

## 1. DATA INGEST — How Data Gets Into Debrief

### 1.1 File Format Import

| Format | Extension | Import Class | Description | Sample |
|--------|-----------|-------------|-------------|--------|
| Replay | `.rep` | [`ImportReplay`](org.mwc.debrief.legacy/src/Debrief/ReaderWriter/Replay/ImportReplay.java) | Primary flat-file format: tracks, sensors, TMA, shapes, annotations, narratives | [`boat1.rep`](org.mwc.cmap.combined.feature/root_installs/sample_data/boat1.rep), [`sen_tracks.rep`](org.mwc.cmap.combined.feature/root_installs/sample_data/sen_tracks.rep) |
| Debrief Plot File | `.dpf`, `.xml` | [`DebriefXMLReaderWriter`](org.mwc.debrief.legacy/src/Debrief/ReaderWriter/XML/DebriefXMLReaderWriter.java) | Full XML-based plot file with formatting, preferences, viewport settings | [`sample.dpf`](org.mwc.cmap.combined.feature/root_installs/sample_data/sample.dpf) |
| KML (LineString) | `.kml` | [`ImportKML`](org.mwc.debrief.legacy/src/Debrief/ReaderWriter/XML/KML/ImportKML.java) | Handheld GPS / mobile phone tracks; timestamp from filename | — |
| KML (MultiGeometry) | `.kml` | [`ImportKML`](org.mwc.debrief.legacy/src/Debrief/ReaderWriter/XML/KML/ImportKML.java) | Multi-vehicle time-stamped tracks from more capable GPS | — |
| NMEA | `.txt`, `.log` | [`ImportNMEA`](org.mwc.debrief.legacy/src/Debrief/ReaderWriter/NMEA/ImportNMEA.java) | GPS/navigation sentences | [`nmea.log`](org.mwc.cmap.combined.feature/root_installs/sample_data/other_formats/nmea.log) |
| NMEA Radar | `.txt` | [`NMEA_Radar_FileImporter`](org.mwc.debrief.legacy/src/Debrief/ReaderWriter/FlatFile/NMEA_Radar_FileImporter.java) | Radar-specific NMEA extension | — |
| AIS | `.txt` | [`ImportAIS`](org.mwc.debrief.legacy/src/Debrief/ReaderWriter/ais/ImportAIS.java) ([`AISDecoder`](org.mwc.debrief.legacy/src/Debrief/ReaderWriter/ais/AISDecoder.java), [`AISParser`](org.mwc.debrief.legacy/src/Debrief/ReaderWriter/ais/AISParser.java)) | Automatic Identification System vessel data | — |
| GPX | `.gpx` | [`GPXLoader`](org.mwc.debrief.core/src/org/mwc/debrief/core/loaders/GPXLoader.java) | GPS Track Exchange Format | [`pinatest2.gpx`](org.mwc.cmap.combined.feature/root_installs/sample_data/other_formats/pinatest2.gpx) |
| Antares | `.txt` | [`ImportAntares`](org.mwc.debrief.legacy/src/Debrief/ReaderWriter/Antares/ImportAntares.java) | Military-standard format | — |
| Nisida | `.txt` | [`ImportNisida`](org.mwc.debrief.legacy/src/Debrief/ReaderWriter/Nisida/ImportNisida.java) | Naval navigation format | — |
| OTH-Gold | `.txt` | [`OTH_Importer`](org.mwc.debrief.legacy/src/Debrief/ReaderWriter/FlatFile/OTH_Importer.java), [`OTH_Helper`](org.mwc.debrief.legacy/src/Debrief/ReaderWriter/FlatFile/OTH_Helper.java) | Over-the-horizon radar data | [`OTH_Gold.txt`](org.mwc.cmap.combined.feature/root_installs/sample_data/other_formats/OTH_Gold.txt) |
| BRT | various | [`BRTImporter`](org.mwc.debrief.legacy/src/Debrief/ReaderWriter/BRT/BRTImporter.java), [`BRTHelper`](org.mwc.debrief.legacy/src/Debrief/ReaderWriter/BRT/BRTHelper.java) | Third-party BRT format | [`TOWED_1000m.brt`](org.mwc.cmap.combined.feature/root_installs/sample_data/other_formats/test_data/brt_import/TOWED_1000m.brt) |
| PC Argos | various | [`ImportPCArgos`](org.mwc.debrief.legacy/src/Debrief/ReaderWriter/PCArgos/ImportPCArgos.java), [`ImportArgosFix`](org.mwc.debrief.legacy/src/Debrief/ReaderWriter/PCArgos/ImportArgosFix.java) | Satellite receiver format | — |
| PMRF | various | [`ImportPMRF`](org.mwc.debrief.legacy/src/Debrief/ReaderWriter/PCArgos/ImportPMRF.java), [`ImportPMRFFix`](org.mwc.debrief.legacy/src/Debrief/ReaderWriter/PCArgos/ImportPMRFFix.java) | PMRF satellite data | — |
| CSV/GZ | `.csv.gz` | [`Import_CSV_GZ`](org.mwc.debrief.legacy/src/Debrief/ReaderWriter/XML/csv_gz/Import_CSV_GZ.java) | Compressed CSV format | [`csv_gz/`](org.mwc.cmap.combined.feature/root_installs/sample_data/other_formats/csv_gz) |
| UK CSV | `.csv` | CSV importer | UK-standard CSV track format | — |
| S2087 CSV | `.csv` | CSV importer | S2087 sensor CSV format | — |
| SATC | various | [`ImportSATC`](org.mwc.debrief.legacy/src/Debrief/ReaderWriter/FlatFile/ImportSATC.java) | Semi-Automated Track Construction scenario files | [`SATC/`](org.mwc.cmap.combined.feature/root_installs/sample_data/SATC) |
| Flat File (SAM) | `.txt` | [`FlatFileExporter`](org.mwc.debrief.legacy/src/Debrief/ReaderWriter/FlatFile/FlatFileExporter.java) (bidirectional) | Tab-separated columns for SAM analysis application | — |
| Word Documents | `.doc`, `.docx` | [`ImportWord_LEGACY`](org.mwc.debrief.legacy/src/Debrief/ReaderWriter/Word/ImportWord_LEGACY.java) | MS Word narrative extraction | [`other_formats/`](org.mwc.cmap.combined.feature/root_installs/sample_data/other_formats) |
| ASW Data Documents | various | [`ImportASWDataDocument`](org.mwc.debrief.legacy/src/Debrief/ReaderWriter/Word/ImportASWDataDocument.java) | ASW narrative extraction | — |
| Narrative Documents | various | [`ImportNarrativeDocument`](org.mwc.debrief.legacy/src/Debrief/ReaderWriter/Word/ImportNarrativeDocument.java), [`ImportRiderNarrativeDocument`](org.mwc.debrief.legacy/src/Debrief/ReaderWriter/Word/ImportRiderNarrativeDocument.java) | Narrative text extraction | — |
| C-Log | various | [`CLogFileImporter`](org.mwc.debrief.legacy/src/Debrief/ReaderWriter/FlatFile/CLogFileImporter.java) | C-Log format | — |

> Pepys SpatiaLite database example: [`pepys.sqlite`](org.mwc.cmap.combined.feature/root_installs/sample_data/other_formats/pepys.sqlite).

### 1.2 REP Format Sub-Types

The Replay format supports many record types via dedicated importers (all under
[`Debrief/ReaderWriter/Replay/`](org.mwc.debrief.legacy/src/Debrief/ReaderWriter/Replay)):

| Record Type | Import Handler | Description |
|-------------|---------------|-------------|
| Positions (Fixes) | [`ImportFix`](org.mwc.debrief.legacy/src/Debrief/ReaderWriter/Replay/ImportFix.java) | Vessel position with time, lat/lon, course, speed |
| Bearings | [`ImportBearing`](org.mwc.debrief.legacy/src/Debrief/ReaderWriter/Replay/ImportBearing.java) | Bearing observations |
| Sensor data | [`ImportSensor`](org.mwc.debrief.legacy/src/Debrief/ReaderWriter/Replay/ImportSensor.java), [`ImportSensor2`](org.mwc.debrief.legacy/src/Debrief/ReaderWriter/Replay/ImportSensor2.java), [`ImportSensor3`](org.mwc.debrief.legacy/src/Debrief/ReaderWriter/Replay/ImportSensor3.java) | Sensor contacts with bearing/frequency |
| Sensor arcs | [`ImportSensorArc`](org.mwc.debrief.legacy/src/Debrief/ReaderWriter/Replay/ImportSensorArc.java) | Sensor arc visualisations |
| Labels | [`ImportLabel`](org.mwc.debrief.legacy/src/Debrief/ReaderWriter/Replay/ImportLabel.java) | Text annotations |
| Narratives | [`ImportNarrative`](org.mwc.debrief.legacy/src/Debrief/ReaderWriter/Replay/ImportNarrative.java), [`ImportNarrative2`](org.mwc.debrief.legacy/src/Debrief/ReaderWriter/Replay/ImportNarrative2.java) | Time-stamped event text |
| Time text | [`ImportTimeText`](org.mwc.debrief.legacy/src/Debrief/ReaderWriter/Replay/ImportTimeText.java) | Time-displayed text |
| Period text | [`ImportPeriodText`](org.mwc.debrief.legacy/src/Debrief/ReaderWriter/Replay/ImportPeriodText.java) | Time-period annotations |
| TMA positions | [`ImportTMA_Pos`](org.mwc.debrief.legacy/src/Debrief/ReaderWriter/Replay/ImportTMA_Pos.java) | Absolute TMA solutions |
| TMA range/bearing | [`ImportTMA_RngBrg`](org.mwc.debrief.legacy/src/Debrief/ReaderWriter/Replay/ImportTMA_RngBrg.java) | Relative TMA solutions |
| Shapes | [`ImportCircle`](org.mwc.debrief.legacy/src/Debrief/ReaderWriter/Replay/ImportCircle.java), [`ImportEllipse`](org.mwc.debrief.legacy/src/Debrief/ReaderWriter/Replay/ImportEllipse.java), [`ImportEllipse2`](org.mwc.debrief.legacy/src/Debrief/ReaderWriter/Replay/ImportEllipse2.java), [`ImportPolygon`](org.mwc.debrief.legacy/src/Debrief/ReaderWriter/Replay/ImportPolygon.java), [`ImportPolyline`](org.mwc.debrief.legacy/src/Debrief/ReaderWriter/Replay/ImportPolyline.java), [`ImportRectangle`](org.mwc.debrief.legacy/src/Debrief/ReaderWriter/Replay/ImportRectangle.java), [`ImportLine`](org.mwc.debrief.legacy/src/Debrief/ReaderWriter/Replay/ImportLine.java), [`ImportVector`](org.mwc.debrief.legacy/src/Debrief/ReaderWriter/Replay/ImportVector.java), [`ImportWheel`](org.mwc.debrief.legacy/src/Debrief/ReaderWriter/Replay/ImportWheel.java) | Geometric annotations ([`shapes.rep`](org.mwc.cmap.combined.feature/root_installs/sample_data/shapes.rep)) |
| Dynamic shapes | [`ImportDynamicCircle`](org.mwc.debrief.legacy/src/Debrief/ReaderWriter/Replay/ImportDynamicCircle.java), [`ImportDynamicPolygon`](org.mwc.debrief.legacy/src/Debrief/ReaderWriter/Replay/ImportDynamicPolygon.java), [`ImportDynamicRectangle`](org.mwc.debrief.legacy/src/Debrief/ReaderWriter/Replay/ImportDynamicRectangle.java) | Time-varying shapes ([`DynamicShapeSetTest.dpf`](org.mwc.cmap.combined.feature/root_installs/sample_data/DynamicShapeSetTest.dpf)) |
| Planning legs | [`ImportPlanningLegOrigin`](org.mwc.debrief.legacy/src/Debrief/ReaderWriter/Replay/ImportPlanningLegOrigin.java), [`ImportPlanningLegRangeSpeed`](org.mwc.debrief.legacy/src/Debrief/ReaderWriter/Replay/ImportPlanningLegRangeSpeed.java), [`ImportPlanningLegRangeTime`](org.mwc.debrief.legacy/src/Debrief/ReaderWriter/Replay/ImportPlanningLegRangeTime.java), [`ImportPlanningLegSpeedTime`](org.mwc.debrief.legacy/src/Debrief/ReaderWriter/Replay/ImportPlanningLegSpeedTime.java) | Exercise planning segments ([`planning_tracks.rep`](org.mwc.cmap.combined.feature/root_installs/sample_data/planning_tracks.rep)) |
| Track segments | [`ImportCreateNewTrackSegment`](org.mwc.debrief.legacy/src/Debrief/ReaderWriter/Replay/ImportCreateNewTrackSegment.java) | Segment creation markers |

### 1.3 Geospatial/Background Data

| Data Source | Description |
|------------|-------------|
| Natural Earth dataset | Multi-resolution vector/raster map backgrounds (110M, 50M, 10M). Styled by cartographer. ~80MB detailed set |
| VPF (Vector Product Format) | VMap Level 0 unclassified vectored database: coastlines, depth/elevation contours |
| DNC (Digital Nautical Chart) | Vector-based maritime features from NIMA |
| ETOPO-2/5 | Gridded bathymetry in 2' or 5' steps |
| Shapefiles | `.shp` ESRI vector format (with .dbf, .shx, .prj) via GeoTools |
| GeoTIFF | `.tif` Georeferenced raster imagery ([`SP27GTIF.tif`](org.mwc.cmap.combined.feature/root_installs/sample_data/SP27GTIF.tif), [`PlotWithGeoTiff.dpf`](org.mwc.cmap.combined.feature/root_installs/sample_data/PlotWithGeoTiff.dpf)) |
| Chart Library | Third-party chart portfolios |
| Built-in coastline | Low-resolution global coastline bundled with Debrief |

### 1.4 Import Modes

- **Over-The-Ground (OTG)**: Position data represents actual geographic positions
- **Dead Reckoning (DR)**: Position data represents DR-calculated positions
- Mode selected at import time; cannot switch after import
- Default mode configurable in Maritime Analysis Preferences

### 1.5 Import Mechanisms

- Drag-and-drop from Navigator view or OS file explorer
- File > Open menu
- Project-based workspace with linked folders (local or network)
- Clipboard paste (REP format from text editor or Excel)
- Sample data auto-provisioning on first run

---

## 2. DATA OPERATIONS — Actions Applied to Data

Most operations below are Eclipse *context operations* under
[`org.mwc.debrief.core/.../ContextOperations/`](org.mwc.debrief.core/src/org/mwc/debrief/core/ContextOperations).

### 2.1 Track Manipulation

| Operation | Code Reference | Description |
|-----------|---------------|-------------|
| Split track | Context menu on fix | Divide track before/after a selected point |
| Merge tracks | [`MergeTracks`](org.mwc.debrief.core/src/org/mwc/debrief/core/ContextOperations/MergeTracks.java) | Combine track segments into single track |
| Group tracks | [`GroupTracks`](org.mwc.debrief.core/src/org/mwc/debrief/core/ContextOperations/GroupTracks.java), [`GroupLightweightTracks`](org.mwc.debrief.core/src/org/mwc/debrief/core/ContextOperations/GroupLightweightTracks.java) | Create named track groups |
| Interpolate track | [`InterpolateTrack`](org.mwc.debrief.core/src/org/mwc/debrief/core/ContextOperations/InterpolateTrack.java) | Insert synthetic fixes at regular intervals; linear position/course/speed |
| Trim track | [`TrimTrack`](org.mwc.debrief.core/src/org/mwc/debrief/core/ContextOperations/TrimTrack.java) | Crop to specified time window |
| Remove track jumps | [`RemoveTrackJumps`](org.mwc.debrief.core/src/org/mwc/debrief/core/ContextOperations/RemoveTrackJumps.java) | Detect and remove position discontinuities (INS update artefacts) |
| Smooth track jumps | [`SmoothTrackJumps`](org.mwc.debrief.core/src/org/mwc/debrief/core/ContextOperations/SmoothTrackJumps.java) | Interpolate around detected jumps |
| Split into legs | [`SplitTracksIntoLegs`](org.mwc.debrief.core/src/org/mwc/debrief/core/ContextOperations/SplitTracksIntoLegs.java) | Divide by ownship steady-course/speed periods |
| Generate infill segment | [`GenerateInfillSegment`](org.mwc.debrief.core/src/org/mwc/debrief/core/ContextOperations/GenerateInfillSegment.java), [`DynamicInfillSegment`](org.mwc.debrief.legacy/src/Debrief/Wrappers/Track/DynamicInfillSegment.java) | Create interpolated positions to fill gaps (dotted-line display) |
| Convert to lightweight | [`ConvertTrackToLightweightTrack`](org.mwc.debrief.core/src/org/mwc/debrief/core/ContextOperations/ConvertTrackToLightweightTrack.java) | Reduce data volume for performance |
| Convert to normal | [`ConvertLightweightTrackToTrack`](org.mwc.debrief.core/src/org/mwc/debrief/core/ContextOperations/ConvertLightweightTrackToTrack.java) | Restore full resolution from lightweight |
| Calculate track length | [`CalculateTrackLength`](org.mwc.debrief.core/src/org/mwc/debrief/core/ContextOperations/CalculateTrackLength.java) | Cumulative distance along track |

### 2.2 Track Dragging Modes

Four drag modes for TMA segment refinement:

| Mode | Effect |
|------|--------|
| **Translate** | Move whole track: changes range/bearing, maintains course/speed |
| **Rotate** | Pivot end of track: changes course, maintains speed |
| **Stretch** | Extend/contract track: maintains course, changes speed |
| **Shear** | Modify both course and speed simultaneously |

Additional drag modes for general features:
- **Drag Whole Feature** — move entire annotation/shape
- **Drag Component** — move individual point within a feature (e.g., rectangle corner)
- **Drag Track Segment** — move single track segment

### 2.3 Sensor Data Operations

| Operation | Description |
|-----------|-------------|
| Generate new sensor | [`GenerateNewSensor`](org.mwc.debrief.core/src/org/mwc/debrief/core/ContextOperations/GenerateNewSensor.java) — add sensor observation container |
| Generate sensor contact | [`GenerateNewSensorContact`](org.mwc.debrief.core/src/org/mwc/debrief/core/ContextOperations/GenerateNewSensorContact.java) — add individual bearing/range |
| Generate sensor arc | [`GenerateNewInsertSensorArcAction`](org.mwc.debrief.core/src/org/mwc/debrief/core/ContextOperations/GenerateNewInsertSensorArcAction.java) — create sensor arc visualisation |
| Merge contacts | [`MergeContacts`](org.mwc.debrief.core/src/org/mwc/debrief/core/ContextOperations/MergeContacts.java) — combine overlapping/nearby sensor observations |
| Copy bearings to clipboard | [`CopyBearingsToClipboard`](org.mwc.debrief.core/src/org/mwc/debrief/core/ContextOperations/CopyBearingsToClipboard.java) — extract bearing data |
| Rainbow shade sonar cuts | [`RainbowShadeSonarCuts`](org.mwc.debrief.core/src/org/mwc/debrief/core/ContextOperations/RainbowShadeSonarCuts.java) — colour-code sensor contacts by bearing/frequency |
| Resolve ambiguity | Keep port/starboard bearing (towed array ambiguity) |
| Drop ambiguous data | Remove bearings that cannot be disambiguated |
| Set array offset | Configure towed array physical offset (e.g. 451m) |
| Grid Editor editing | Cell-by-cell bearing/frequency editing |
| Interpolation | Linear, spline, or curved fitting of sensor data |
| Outlier correction | Drag errant data points to alignment |
| Data density reduction | Filter visible frequency to reduce clutter |

> Sensor example data: [`sen_frig_sensor.dsf`](org.mwc.cmap.combined.feature/root_installs/sample_data/sen_frig_sensor.dsf);
> towed-array variants in [`TA_DUMMY_DATA/`](org.mwc.cmap.combined.feature/root_installs/sample_data/other_formats/TA_DUMMY_DATA).
> `SensorContactWrapper` is documented in the [track_shift README](org.mwc.debrief.track_shift/README.md).

### 2.4 Drawing Features / Annotations

Geographically-fixed features placed on plot (see
[`shapes.rep`](org.mwc.cmap.combined.feature/root_installs/sample_data/shapes.rep) /
[`shapes.dpf`](org.mwc.cmap.combined.feature/root_installs/sample_data/shapes.dpf)):

| Feature | Description |
|---------|-------------|
| Label | Labelled symbol, supports multi-line text |
| Ellipse | Oriented ellipse with semi-major/minor axes |
| Rectangle | Geographic rectangle |
| Line | Line between two geographic points |
| Circle | Circle at location with configurable radius |
| Polygon | Multi-point closed shape |
| Polyline | Multi-point open line |
| Vector | Bearing + distance from origin |
| Wheel | Circular pattern |
| Dynamic shapes | Time-varying circle, polygon, rectangle |

All annotations support time start/end properties and can be placed on Tote for analysis.

### 2.5 Chart Features

| Feature | Description |
|---------|-------------|
| Grid (Lat/Lon or Square) | Configurable delta, auto-mode for optimal spacing |
| Local Grid | Grid with user-defined origin |
| Scale | Distance scale bar with configurable units |
| Time Display (Absolute) | Current DTG overlay, configurable position/format |
| Time Display (Relative) | Relative timer with configurable origin event |
| Range Rings | Concentric range circles from primary track |
| Coastline | Built-in low-resolution coastline |
| ETOPO Bathymetry | Gridded depth display |
| Natural Earth | Multi-resolution vector map backgrounds |

### 2.6 Planning / Exercise Design

- **Planning Segments**: Define intended routes by course/speed/distance
  - Three calculation modes: Range+Speed→Time, Range+Time→Speed, Speed+Time→Range
- Planning legs importable via REP format ([`ImportPlanningLegOrigin`](org.mwc.debrief.legacy/src/Debrief/ReaderWriter/Replay/ImportPlanningLegOrigin.java), etc.) — example: [`planning_tracks.rep`](org.mwc.cmap.combined.feature/root_installs/sample_data/planning_tracks.rep), [`compositeTrack.dpf`](org.mwc.cmap.combined.feature/root_installs/sample_data/compositeTrack.dpf)
- XY plots of planning tracks for route assessment

### 2.7 Visibility & Filtering

- Layer-based show/hide (entire tracks, features, layers)
- Individual fix visibility control
- Time-period filtering (start/end markers with snap-to-hour)
- Selective sensor data visibility (block-by-block)
- Narrative source/type filtering
- Symbol frequency control (All, Sparse, Custom intervals)
- Label frequency control

### 2.8 Scripting / Automation

- **Scripting Perspective**: JavaScript/Groovy scripting environment
- Record UI actions as scripts
- Access Debrief objects programmatically
- Create custom command buttons from scripts
- Assign keyboard shortcuts to scripts
- Debug perspective for script development

> Scripting examples: [`other_formats/Scripts/`](org.mwc.cmap.combined.feature/root_installs/sample_data/other_formats/Scripts).

---

## 3. DATA EXPLOITATION — How Data Gets Used Within Debrief

The exploitation/analysis views below live mainly in the
[track_shift plugin](org.mwc.debrief.track_shift/README.md) (TMA, residuals, Doppler) and the
[satc.core plugin](org.mwc.debrief.satc.core/README.md) (genetic-algorithm TMA).

### 3.1 Temporal Navigation & Playback

| Feature | Description |
|---------|-------------|
| Time Controller | Play/pause/step through time-stamped data |
| Normal mode | All data visible, highlight marker at current time |
| Snail mode | Current position + trailing history (configurable trail length) |
| Variable time increment | Adjustable step size (seconds to hours) |
| DTG format customisation | Multiple date/time display formats |
| Bookmarks | Named markers at significant times with annotations |
| Time-period filtering | Restrict view to operational windows |

### 3.2 Track Relationship Analysis

| Capability | Description |
|-----------|-------------|
| Primary/Secondary track assignment | Designate tracks for comparative analysis |
| Track Tote | Real-time inter-track metrics panel |
| Tote calculations | 20+ derived values computed on-the-fly (see below) |
| Unit Centric View | Plot non-primary tracks relative to primary track position |
| Snail Paint mode | Relative position trails in unit-centric view |

**Tote Calculations** (computed in real-time between primary and secondary tracks):

| Calculation | Description |
|-------------|-------------|
| Bearing (absolute) | Absolute bearing between tracks |
| Bearing (relative) | Relative bearing off own bow |
| Range | Distance between tracks |
| Range rate | Rate of range change |
| Course | Target heading |
| Speed | Target velocity |
| Depth | Depth |
| Doppler | Doppler frequency shift |
| Time (seconds) | Elapsed time |
| Speed rate / rate-rate | Speed derivatives |
| Course rate / rate-rate | Course derivatives |
| Course delta average | Averaged course change |
| Speed delta average | Averaged speed change |
| ATB | Across-the-bow parameter |
| Colour | Display colour coding |

### 3.3 XY Plotting / Graphing

- Time-variable plots of any calculated parameter
- Range vs. Time, Bearing vs. Time, Course vs. Time, etc.
- Multi-track comparative charts
- Cursor-based measurement on graphs (range & bearing)
- Zoom, pan, and navigation within plots
- JFreeChart-based plotting engine

### 3.4 Target Motion Analysis (TMA) — Manual (S2R)

Full Single-Sided Reconstruction workflow:
1. Load and groom ownship track (fix jumps, fill gaps, merge third-party data)
2. Groom sensor data (resolve ambiguity, edit bearings/frequencies, interpolate outliers)
3. Select sensor contacts for analysis period
4. Generate TMA segment ([`GenerateTMASegmentFromCuts`](org.mwc.debrief.core/src/org/mwc/debrief/core/ContextOperations/GenerateTMASegmentFromCuts.java))
5. Refine via drag operations (translate/rotate/stretch/shear) with real-time residual feedback
6. Generate additional legs for multi-leg engagement
7. Merge track segments into final target track

**TMA Segment Types** (under [`Debrief/Wrappers/Track/`](org.mwc.debrief.legacy/src/Debrief/Wrappers/Track)):
- [`CoreTMASegment`](org.mwc.debrief.legacy/src/Debrief/Wrappers/Track/CoreTMASegment.java) — basic TMA with course/speed
- [`AbsoluteTMASegment`](org.mwc.debrief.legacy/src/Debrief/Wrappers/Track/AbsoluteTMASegment.java) — world-coordinate TMA
- [`RelativeTMASegment`](org.mwc.debrief.legacy/src/Debrief/Wrappers/Track/RelativeTMASegment.java) — ownship-relative (range/bearing from host)
- [`GenerateTUASolution`](org.mwc.debrief.core/src/org/mwc/debrief/core/ContextOperations/GenerateTUASolution.java) — TMA with Target Uncertainty Area ellipse

**TMA Generation Sources**:
- [`GenerateTMASegmentFromCuts`](org.mwc.debrief.core/src/org/mwc/debrief/core/ContextOperations/GenerateTMASegmentFromCuts.java) — from selected sensor contacts
- [`GenerateTMASegmentFromInfillSegment`](org.mwc.debrief.core/src/org/mwc/debrief/core/ContextOperations/GenerateTMASegmentFromInfillSegment.java) — from interpolated data
- [`GenerateTMASegmentFromOwnshipPositions`](org.mwc.debrief.core/src/org/mwc/debrief/core/ContextOperations/GenerateTMASegmentFromOwnshipPositions.java) — from ownship positions
- [`ConvertAbsoluteTmaToRelative`](org.mwc.debrief.core/src/org/mwc/debrief/core/ContextOperations/ConvertAbsoluteTmaToRelative.java) — coordinate system conversion

### 3.5 Target Motion Analysis — Semi-Automated (SATC)

Genetic Algorithm-based automated TMA (implemented in the
[`org.mwc.debrief.satc.core`](org.mwc.debrief.satc.core/README.md) plugin; sample scenarios in
[`SATC/`](org.mwc.cmap.combined.feature/root_installs/sample_data/SATC) and
[`SATC_Test/`](org.mwc.cmap.combined.feature/root_installs/sample_data/SATC_Test)):

**Workflow**:
1. Create scenario from bearing data
2. Add contributions:
   - Bearing measurements (with configurable error bounds)
   - Speed constraints (hard min/max + estimate)
   - Target leg definitions (straight-line assumptions)
3. Automated ownship leg detection (3 methods: Moving Average, Peak Tracking, Artificial)
4. Automated zig detection (course change identification)
5. Calculate solution (GA optimisation with configurable precision: LOW/MEDIUM/HIGH)
6. Validate against truth tracks
7. Import solution as composite track or standalone track

**GA Architecture**:
- Islands Model (multiple parallel populations with synchronized migrations)
- Simple and Complex islands with different characteristics
- Decision variables: target lat/lon, course, speed per leg
- Fitness: minimize bearing residuals + course acceleration penalty
- Operators: mutation (gaussian), crossover (uniform blending), elite preservation (μ+λ)
- Population: 45-120 solutions, 5-30 seconds runtime by precision level
- Output: composite track from best solution

**SATC Contributions**:
- Bearing Measurement — observed bearing ± error degrees
- Speed Forecast — hard min/max + analyst estimate
- Leg Forecast — straight-leg / altering definitions
- Ownship Legs — auto-detected from ownship track

### 3.6 Bearing Ambiguity Resolution

- Passive sonar 180° ambiguity (port vs. starboard)
- Multi-leg permutation search (2^n, chunked at 20 legs)
- Scoring by minimum turn angle at zig points
- Quadratic polynomial bearing curve fitting at leg boundaries
- Handles 0°/360° domain wraparound

> Ambiguous-bearing sample data: [`S2R/Ambig_tracks.rep`](org.mwc.cmap.combined.feature/root_installs/sample_data/S2R/Ambig_tracks.rep).

### 3.7 Doppler / Frequency Analysis

- Doppler shift curve fitting (4-parameter logistic model)
- CPA (Closest Point of Approach) detection via inflection point
- Frequency residuals view (observed vs. calculated)
- Narrowband frequency stability assessment
- Waterfall frequency graphs

> Frequency sample data: [`S2R/freq/`](org.mwc.cmap.combined.feature/root_installs/sample_data/S2R/freq).

### 3.8 Multipath Analysis

- Target depth estimation from sound propagation multipath effects
- Sound Velocity Profile (SVP) input
- Time-delay measured vs. calculated comparison
- Nelder-Mead simplex optimisation for best target depth
- Slider-based manual depth exploration

> Multipath sample data: [`MultiPath/`](org.mwc.cmap.combined.feature/root_installs/sample_data/MultiPath).

### 3.9 Sensor Data Visualisation

- Bearing fan overlay on plot
- Sensor range plotting ([`GenerateSensorRangePlot`](org.mwc.debrief.core/src/org/mwc/debrief/core/ContextOperations/GenerateSensorRangePlot.java))
- Rainbow colour-coding by bearing/frequency
- Plot-Lock: lock plot to sensor bearing fan, drag to minimise bearing error
- Stacked Dots view: bearing error curves
- Waterfall displays for frequency data

### 3.10 Media Synchronisation

- **Video sync**: Time-synchronised video playback (AVI format)
  - Filename-based DTG extraction for start time
  - Bidirectional control: video→time controller, time controller→video
  - Track position interpolation for smooth sub-second display
- **Image viewer**: Time-stamped photograph display
  - Folder-based image loading (filename = DTG)
  - Thumbnail/preview panes, time-linked navigation

> Timed-image sample sequence: [`other_formats/TimedImages/`](org.mwc.cmap.combined.feature/root_installs/sample_data/other_formats/TimedImages).

### 3.11 Narrative Analysis

- Time-stamped textual event records
- Narrative Viewer with filtering by source and type
- Bidirectional time-linking (narrative→time, time→narrative)
- Command narratives, event logs, auxiliary comments
- Narrative entries from REP files with metadata (date, origin, type)

> Narrative sample data: [`narrative.rep`](org.mwc.cmap.combined.feature/root_installs/sample_data/narrative.rep), [`narrative2.rep`](org.mwc.cmap.combined.feature/root_installs/sample_data/narrative2.rep).

---

## 4. DATA EXPORT — How Products Get Out of Debrief

### 4.1 File Format Export

| Format | Code Reference | Description |
|--------|---------------|-------------|
| DPF/XML | [`DebriefXMLReaderWriter`](org.mwc.debrief.legacy/src/Debrief/ReaderWriter/XML/DebriefXMLReaderWriter.java) | Full plot file with all formatting, preferences, viewport |
| REP (Replay) | [`FormatTracks`](org.mwc.debrief.legacy/src/Debrief/ReaderWriter/Replay/FormatTracks.java) | Flat-file track data (no formatting preserved) |
| CSV | [`ExportTrackAsCSV`](org.mwc.debrief.core/src/org/mwc/debrief/core/ContextOperations/ExportTrackAsCSV.java) | Configurable column tabular export |
| GPX | [`ExportGPX`](org.mwc.debrief.core/src/org/mwc/debrief/core/operations/ExportGPX.java), [`PlotGpx`](org.mwc.debrief.legacy/src/Debrief/ReaderWriter/powerPoint/PlotGpx.java) | GPS Track Exchange Format |
| Flat File (SAM) | [`FlatFileExporter`](org.mwc.debrief.legacy/src/Debrief/ReaderWriter/FlatFile/FlatFileExporter.java) | Tab-separated for SAM analysis application with SIF metrics |
| GeoJSON | [`GenerateGeoJSON`](org.mwc.debrief.legacy/src/Debrief/ReaderWriter/GeoPDF/GenerateGeoJSON.java), [`GenerateSegmentedGeoJSON`](org.mwc.debrief.legacy/src/Debrief/ReaderWriter/GeoPDF/GenerateSegmentedGeoJSON.java) | Geographic JSON for GIS interoperability |
| GeoPDF | [`GeoPDF`](org.mwc.debrief.legacy/src/Debrief/ReaderWriter/GeoPDF/GeoPDF.java), [`GeoPDFLegacyBuilder`](org.mwc.debrief.legacy/src/Debrief/ReaderWriter/GeoPDF/GeoPDFLegacyBuilder.java), [`GeoPDFSegmentedBuilder`](org.mwc.debrief.legacy/src/Debrief/ReaderWriter/GeoPDF/GeoPDFSegmentedBuilder.java) | Geospatial PDF maps |
| KML | [`ImportKML`](org.mwc.debrief.legacy/src/Debrief/ReaderWriter/XML/KML/ImportKML.java) (KML read/write) | For Google Earth / GIS tools |

### 4.2 Image/Visual Export

| Method | Description |
|--------|-------------|
| WMF (Windows Metafile) | Vector graphics export via [`MetafileCanvas`](org.mwc.cmap.legacy/src/MWC/GUI/Canvas/MetafileCanvas.java) (`File > Export to WMF`) |
| BMP | Bitmap raster export |
| Copy to clipboard | Plot image copy for paste into any application |
| Print | Standard OS print dialog |

### 4.3 Presentation Export

| Method | Description |
|--------|-------------|
| Dynamic PPTX | Animated PowerPoint with Debrief-style icons on master template |
| Static PPTX | Still snapshots inserted into slides |
| PPTX master template | Configurable template with donor elements for symbol styling |
| [`PackPresentation`](org.mwc.debrief.legacy/src/Debrief/ReaderWriter/powerPoint/PackPresentation.java) | PowerPoint package builder |
| [`ParsePresentation`](org.mwc.debrief.legacy/src/Debrief/ReaderWriter/powerPoint/ParsePresentation.java) | PowerPoint parser for template configuration |

**Dynamic PPTX Workflow**:
1. Configure master template with symbol styling
2. Resize plot to match PowerPoint slide dimensions
3. Prepare scenario (zoom, time controller settings, track visibility)
4. Play scenario with "Export to PPTX" mode
5. Pause to trigger export dialog
6. Export produces animated slides with track movements

### 4.4 Clipboard Operations

- REP format paste to clipboard ([`GeneratePasteRepClipboard`](org.mwc.debrief.core/src/org/mwc/debrief/core/ContextOperations/GeneratePasteRepClipboard.java))
- Bearing data copy to clipboard ([`CopyBearingsToClipboard`](org.mwc.debrief.core/src/org/mwc/debrief/core/ContextOperations/CopyBearingsToClipboard.java))
- Plot image copy for document insertion
- Time-period-restricted data export to clipboard

### 4.5 Data Interoperability

- REP format for inter-tool data exchange
- Flat file (SAM) for downstream analysis applications
- GeoJSON for web mapping / GIS systems
- GPX for GPS tool ecosystem
- KML for Google Earth and OGC-compliant systems
- CSV for spreadsheet / statistical tools

---

## 5. DATA QUALITY — Monitoring and Assessment

### 5.1 Visual Quality Inspection

| Tool | Purpose |
|------|---------|
| Bearing fan display | Inspect sensor geometry; identify coverage gaps |
| Waterfall frequency graph | Assess frequency drift/stability over time |
| Symbol density visualisation | Identify data gaps via All/Sparse/Custom frequency |
| Label frequency display | Time label overlay to identify temporal gaps/jumps |
| Outline View tree | Data inventory: item counts per track/sensor |

### 5.2 Track Quality Assessment

| Capability | Description |
|-----------|-------------|
| Jump detection | [`RemoveTrackJumps`](org.mwc.debrief.core/src/org/mwc/debrief/core/ContextOperations/RemoveTrackJumps.java) — identifies position discontinuities exceeding expected speed×time distance |
| Jump smoothing | [`SmoothTrackJumps`](org.mwc.debrief.core/src/org/mwc/debrief/core/ContextOperations/SmoothTrackJumps.java) — interpolate corrections around detected jumps |
| Timestamp collision detection | [`FixWrapperCollisionCheck`](org.mwc.debrief.legacy/src/Debrief/Wrappers/Track/FixWrapperCollisionCheck.java) — detect duplicate timestamps, auto-correct by adding 1ms delays |
| Track splitting at gaps | Split track at missing data points to isolate and separately fix problem areas |
| Visual gap identification | Symbol/label frequency display reveals data breaks |

### 5.3 TMA Residual Analysis

| View | Purpose |
|------|---------|
| Bearing Residuals | Calculated vs. measured bearing differences over time (absolute or relative degrees) |
| Frequency Residuals | Observed vs. calculated Doppler frequency over time |
| Stacked Dots | Bearing error curves with real-time updates during track dragging |
| SATC Performance tab | Solution fitness evolution over GA generations/cycles |
| SATC Contribution effectiveness | Error bar charts showing which contributions constrain solutions |

### 5.4 Solution Validation

| Method | Description |
|--------|-------------|
| Truth track comparison | Range vs. time plotting: solution track vs. known truth |
| Range error trending | Error magnitude from engagement start to end |
| Multi-solution overlay | Compare different TMA solutions visually |
| Bearing residual magnitude | Quantitative bearing error assessment |
| Target depth optimisation | Multipath analysis with Nelder-Mead minimisation of time-delay error |

### 5.5 Import Validation

| Check | Description |
|-------|-------------|
| Format compliance | Line parser verification for REP/XML/CSV; per-line error recovery |
| Coordinate bounds | Latitude [-90,+90], longitude [-180,+180] validation |
| Unit conversion safety | Knots/m/s/km/h consistency checks |
| XML schema validation | DTD/schema compliance for DPF files |
| AIS protocol validation | [`AISParseException`](org.mwc.debrief.legacy/src/Debrief/ReaderWriter/ais/AISParseException.java) for malformed sentences |
| GA bounds enforcement | [`XMLRange`](org.mwc.asset.legacy/src/ASSET/Util/XMLFactory/XMLRange.java) min/max/step validation prevents invalid solutions |

### 5.6 Calculated Quality Metrics

| Metric | Description |
|--------|-------------|
| System Integrity Factor (SIF) | Bearing SIF, Range SIF, Combined SIF in flat-file export |
| Bearing accuracy | Per-observation accuracy field in sensor data |
| Range accuracy | Per-observation range accuracy |
| Course/speed accuracy | Predicted vs. actual accuracy fields |
| Frequency accuracy | Per-observation frequency accuracy |
| GA fitness score | Minimised bearing/range residual + acceleration penalty |

---

## Appendix A: Key Views / UI Components

| View | Purpose |
|------|---------|
| Plot Editor | Main geographic display with track/sensor/annotation rendering |
| Outline View | Hierarchical data tree with visibility control |
| Properties View | Attribute editor for selected items |
| Time Controller | Temporal navigation with play/pause/step |
| Track Tote | Real-time comparative metrics between primary/secondary |
| Bearing Residuals | TMA bearing error graph |
| Frequency Residuals | TMA frequency error graph |
| Grid Editor | Cell-by-cell data editing (bearings, frequencies) |
| Narrative Viewer | Time-stamped text event display |
| XY Plot | Configurable time-variable graphs |
| Unit Centric View | Relative position plot centred on primary track |
| Media Player | Synchronised video playback |
| Image Viewer | Time-synchronised photograph display |
| Multipath Analysis | Target depth estimation from sound propagation |
| SATC Maintain Contributions | SATC scenario management |
| SATC Performance | GA solution convergence display |
| Bookmarks | Named time-position markers |
| Navigator | Project and file browser |

## Appendix B: Projection Support

Projection classes under [`MWC/Algorithms/Projections/`](org.mwc.cmap.legacy/src/MWC/Algorithms/Projections):

| Projection | Description |
|-----------|-------------|
| [`FlatProjection`](org.mwc.cmap.legacy/src/MWC/Algorithms/Projections/FlatProjection.java) | Flat-earth approximation for local tactical operations (default) |
| [`Mercator2`](org.mwc.cmap.legacy/src/MWC/Algorithms/Projections/Mercator2.java) / [`Mercator3`](org.mwc.cmap.legacy/src/MWC/Algorithms/Projections/Mercator3.java) | Mercator projections for larger-area display |
| [`GtProjection`](org.mwc.cmap.geotools/src/org/mwc/cmap/geotools/gt2plot/GtProjection.java) | GeoTools bridge: EPSG:4326 ↔ EPSG:3395 for GIS integration |

## Appendix C: Domain Data Types

Core types under [`MWC/GenericData/`](org.mwc.cmap.legacy/src/MWC/GenericData) and
[`MWC/TacticalData/`](org.mwc.cmap.legacy/src/MWC/TacticalData); wrappers under
[`Debrief/Wrappers/`](org.mwc.debrief.legacy/src/Debrief/Wrappers). See also
[`KEY_CLASSES.md`](KEY_CLASSES.md) and [`DOMAIN_GLOSSARY.md`](DOMAIN_GLOSSARY.md).

| Type | Purpose |
|------|---------|
| [`WorldLocation`](org.mwc.cmap.legacy/src/MWC/GenericData/WorldLocation.java) | Lat/lon/depth geographic position (fundamental) |
| [`HiResDate`](org.mwc.cmap.legacy/src/MWC/GenericData/HiResDate.java) | Microsecond-precision timestamp |
| [`WorldSpeed`](org.mwc.cmap.legacy/src/MWC/GenericData/WorldSpeed.java) | Speed with unit conversions (kts, m/s, km/h, etc.) |
| [`WorldDistance`](org.mwc.cmap.legacy/src/MWC/GenericData/WorldDistance.java) | Distance with unit conversions (nm, yds, m, km, etc.) |
| [`WorldVector`](org.mwc.cmap.legacy/src/MWC/GenericData/WorldVector.java) | Bearing + distance vector |
| [`WorldArea`](org.mwc.cmap.legacy/src/MWC/GenericData/WorldArea.java) | Rectangular geographic bounding region |
| [`Fix`](org.mwc.cmap.legacy/src/MWC/TacticalData/Fix.java) | Position + time + course + speed |
| [`TrackWrapper`](org.mwc.debrief.legacy/src/Debrief/Wrappers/TrackWrapper.java) | Track container (fixes, sensors, TMA segments) |
| [`SensorWrapper`](org.mwc.debrief.legacy/src/Debrief/Wrappers/SensorWrapper.java) | Sensor observations container |
| [`SensorContactWrapper`](org.mwc.debrief.legacy/src/Debrief/Wrappers/SensorContactWrapper.java) | Individual bearing/range/frequency observation |
| [`TMAWrapper`](org.mwc.debrief.legacy/src/Debrief/Wrappers/TMAWrapper.java) | TMA solution container |

---

*Audit conducted 2026-03-01. Broad coverage of capabilities from documentation, cheat sheets, and code inspection. Depth to follow on individual capabilities as needed.*
