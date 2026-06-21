# Sample Data Catalog

This document catalogs the sample data files included in the Debrief repository at
`org.mwc.cmap.combined.feature/root_installs/sample_data/`. It is intended as a reference
for the debrief-future project to drive UI development using realistic maritime analysis data.

## Summary Statistics

| Metric | Value |
|--------|-------|
| Total files | 287 |
| Total size | 86 MB |
| Directories | 23 |
| File types | 31 distinct extensions |

### File Count by Type

| Extension | Count | Total Size | Description |
|-----------|------:|------------|-------------|
| `.rep` | 75 | 16.0 MB | Replay format (tracks, narratives, shapes) |
| `.dpf` | 46 | 14.5 MB | Debrief Plot File (XML-based plot state) |
| `.jpg` | 43 | 2.0 MB | Timed image snapshots |
| `.dsf` | 27 | 300 KB | Debrief Sensor Format (sensor contacts) |
| `.cs2` | 20 | 12.6 MB | CSAD sensor data (CSV-based) |
| `.txt` | 10 | 1.1 MB | Various text formats (NMEA, OTH-Gold, etc.) |
| `.csv` | 9 | 174 KB | Comma-separated data (exchange, SVP, etc.) |
| `.doc` / `.docx` | 10 | 531 KB | Narrative documentation (FCS, ASW formats) |
| `.xls` / `.xlsx` | 7 | 6.9 MB | Spreadsheet data (SATC experiments) |
| `.xml` | 5 | 247 KB | XML control/config files |
| `.gpx` | 4 | 126 KB | GPS Exchange Format tracks |
| `.js` | 4 | 10 KB | Debrief scripting examples |
| `.avi` | 2 | 12.6 MB | Video recordings |
| `.pdf` | 2 | 140 KB | Narrative documentation |
| `.pptx` | 2 | 154 KB | PowerPoint report templates |
| `.log` | 2 | 4.4 MB | NMEA log files |
| `.brt` | 2 | 1.6 KB | Bearing time data |
| `.gz` | 3 | 3.4 KB | Gzip-compressed CSV files |
| `.tif` | 1 | 636 KB | GeoTIFF raster chart |
| `.sqlite` | 1 | 12.4 MB | Pepys spatialite database |
| `.mp3` | 1 | 729 KB | Audio recording |
| `.zip` | 1 | 751 KB | Compressed DPF file |
| `.png` | 1 | 101 KB | Gantt chart screenshot |
| Other | 6 | ~12 KB | `.ini`, `.inp`, `.bat`, `.sh`, `.py`, `.index` |

---

## File Format Reference

### REP - Replay Format (`.rep`)

The Replay format is Debrief's primary text-based import format. It is line-oriented and
whitespace-delimited, supporting multiple record types identified by leading keywords.

**Position Fix Records** (most common, no prefix):
```
YYMMDD HHMMSS.SSS TRACKNAME SYMBOL LAT_DEG LAT_MIN LAT_SEC N/S LON_DEG LON_MIN LON_SEC E/W COURSE SPEED DEPTH
```
Example:
```
951212 050000.000 NELSON @C 22 11 10.63 N 21 41 52.37 W 269.7 2.0 0
```

**Key record types** (prefixed with `;`):

| Prefix | Purpose | Key Fields |
|--------|---------|------------|
| `;NARRATIVE:` | Event log entry | date, time, track, free text |
| `;NARRATIVE2:` | Typed narrative entry | date, time, track, type, free text |
| `;SENSOR:` | Sensor bearing/range contact | date, time, track, bearing, range, sensor name |
| `;SENSOR2:` | Ambiguous bearing contact | date, time, track, bearing1, bearing2, range, frequency |
| `;SENSOR3:` | Named sensor contact | date, time, track, bearing, range, quoted sensor name |
| `;SENSORARC:` | Sensor coverage arc | start/end time, track, arc angles, inner/outer range |
| `;RECT:` | Rectangle annotation | symbol, two corner positions, label |
| `;LINE:` | Line annotation | symbol, two endpoint positions, label |
| `;CIRCLE:` | Circle annotation | symbol, centre, radius, label |
| `;VECTOR:` | Vector annotation | symbol, origin, length, bearing, label |
| `;TEXT:` | Text label | symbol, position, label text |
| `;POLYLINE:` | Multi-point line | symbol, series of positions, label |
| `;PERIODTEXT:` | Timed text overlay | symbol, start/end time, position, label |
| `;PLANNING_ORIGIN:` | Planned track start | date, time, track, position |
| `;PLANNING_RANGE_SPEED:` | Planned leg (range/speed) | track, leg name, range, speed, course |
| `;PLANNING_SPEED_TIME:` | Planned leg (speed/time) | track, leg name, speed, duration, course |
| `;FORMAT_FIX:` | Display formatting | name, type (SYMBOL/LABEL/ARROW), visibility, interval |
| `;;` | Comment | ignored by parser |

**Coordinates**: Degrees, minutes, decimal seconds (DMS) with N/S E/W hemisphere indicators.

**Date formats**: `YYMMDD` (6-digit, e.g. `951212`) or `YYYYMMDD` (8-digit, e.g. `20090722`).

**Time format**: `HHMMSS.SSS` (milliseconds optional).

**Symbol codes**: `@A`-`@Z` for simple colors (A=Red, B=Green, C=Blue, etc.), or extended
format `@XYnn` encoding color, line style, width, and fill.

### DPF - Debrief Plot File (`.dpf`)

DPF is Debrief's native XML-based persistence format. It stores the complete plot state
including all spatial data, styling, projection settings, and UI state.

**Root structure**:
```xml
<plot xmlns="http://www.debrief.info/plot" Created="..." Name="Debrief Plot" PlotId="...">
  <session>
    <layers>...</layers>        <!-- All spatial data -->
    <projection>...</projection> <!-- Map viewport/projection -->
    <gui>...</gui>               <!-- UI state (stepper, tote) -->
  </session>
</plot>
```

**Key elements within `<layers>`**:

| Element | Purpose |
|---------|---------|
| `<track>` | Vessel track with `<TrackSegment>` containing `<fix>` elements |
| `<layer>` | Named container for shapes and annotations |
| `<narrative>` | Container for `<narrative_entry>` event log entries |
| `<composite_track>` | Planned route with `<planning_segment>` legs |
| `<dynamicLayer>` | Time-dependent display overlays |

**Position encoding**: All coordinates use `<shortLocation Lat="..." Long="..." Depth="..."/>` in decimal degrees.

**Time encoding**: DTG format `YYMMDD HHMMSS` or `YYMMDD HHMMSS.mmm`.

**Sensor data**: `<sensor>` elements within tracks containing `<sensor_contact>` children with bearing, range, and frequency attributes.

**TMA solutions**: `<tma>` elements within tracks containing `<tma_solution>` children with course, speed, depth, and error ellipse data.

**Shape types**: `<circle>`, `<rectangle>`, `<line>`, `<textlabel>`, `<polygon>`, `<ellipse>`, `<vector>`, `<wheel>`.

### DSF - Debrief Sensor Format (`.dsf`)

DSF files use the same line format as REP sensor records but are stored in standalone files.
They contain `;SENSOR:`, `;SENSOR2:`, or `;SENSOR3:` records and are typically loaded alongside
a REP file to overlay sensor contacts onto tracks.

```
;SENSOR: 700103 033125.000 Frigate @A NULL 80 12496 Frigate_Optic some message
```

**Towed Array (TA) variants** (in `TA_DUMMY_DATA/`):

| Prefix | Description |
|--------|-------------|
| `;TA_COG_ABS:` | Towed array, absolute bearing, COG reference |
| `;TA_COG_REL:` | Towed array, relative bearing, COG reference |
| `;TA_FORE_AFT:` | Towed array, fore/aft ambiguity |
| `;TA_NG_BRG_COG_ABS:` | Non-geographic bearing, absolute COG |

### CS2 - CSAD Sensor Data (`.cs2`)

CSV-based sensor observation format used for SATC (Semi-Automatic Track Construction) testing.
Contains high-rate sonar measurements.

**Header**:
```
#Time of Validity,Track Number,Contact Bearing,Contact Bearing Error,Frequency,Frequency Error,Contact Strength,Own heading,Own Speed,Sonar X,Sonar Y,S1 B,B Error
DD HH:MM:SS:.0,,deg,deg,Hz,Hz,dB,deg,m/s,m,m,deg,deg
```

**Fields**: Timestamp, track number, contact bearing/error, frequency/error, contact strength (dB), ownship heading/speed, sonar position (X/Y in metres), secondary bearing/error.

### GPX - GPS Exchange Format (`.gpx`)

Standard GPX 1.0 and 1.1 format for GPS track data. Contains `<trk>` elements with
`<trkseg>` segments of `<trkpt>` waypoints carrying lat/lon and time.

### NMEA Log (`.log`)

NMEA-style position/navigation sentences with `$POSL` prefix. Contains position (POS),
heading (HDG), velocity (VEL), and date/time (DZA) sentence types.

### BRT - Bearing Time (`.brt`)

Simple two-column CSV format: Unix epoch timestamp and bearing in degrees.

```
1263297600.000000, 69.00
1263297840.000000, 67.85
```

### Other Text Formats

| Format | Files | Description |
|--------|-------|-------------|
| OTH-Gold | `OTH_Gold.txt` | Over-The-Horizon Gold track exchange format |
| UK Track Exchange | `uk_track.rep` | UK military track exchange (CSV with ISO 8601 times) |
| CLog | `CLog_Trial.txt` | Command Log trial data |
| Antares | `antares.txt` | Antares format position data |
| Nisida | `nisida_sample.txt` | Nisida format sample |
| CSV Exchange | `CSV_EXCHANGE_SAMPLE.csv` | Debrief CSV exchange format |

### Pepys SQLite Database (`.sqlite`)

A SpatiaLite database (13 MB) containing the Pepys data model with domain tables:

| Table | Rows | Description |
|-------|-----:|-------------|
| `Platforms` | 33 | Vessels/units with type, nationality |
| `Sensors` | 43 | Sensor definitions with type and host platform |
| `States` | 12,239 | Time-stamped position/heading/speed observations |
| `Contacts` | 112 | Sensor contact detections (bearing, range, freq) |
| `Datafiles` | 25 | Source data file references |
| `Comments` | 21 | Timestamped text entries per platform |
| `Activations` | 13 | Sensor activation periods |
| `Geometries` | 8 | Spatial geometry features |
| `Nationalities` | 257 | Reference nationality table |
| `PlatformTypes` | 5 | Fisher, Ferry, Warship, etc. |
| `SensorTypes` | 6 | GPS, Position, Array Sonar, etc. |

---

## Directory Structure

```
sample_data/                          86 MB total
  *.rep, *.dpf, *.dsf, *.xml, *.tif  (top-level files, see inventory below)
  Demo/                               153 KB - Tutorial demonstration scenarios
    Analysis/                          4 files - Track analysis workflow
    Review/                            3 files - Post-exercise review workflow
    TrialsPlanning/                    6 files - Trials planning workflow
  MultiPath/                           185 KB - Multi-path propagation test data
  MultiStatics/                        1.1 MB - Multi-static sonar scenarios
  S2R/                                 8.2 MB - Ship-to-Report / bug reproduction data
    2553_missing_sensor_data/          5 files - Missing sensor data test case
    freq/                              6 files - Frequency analysis data
    issue_1621/                        2 files - Issue reproduction data
    smooth_back_jumps/                 1 file  - Track smoothing test
  SATC/                                2.8 MB - Semi-Automatic Track Construction scenarios
  SATC_Test/                           27 MB  - SATC algorithm test datasets
    OwnshipZig/                        4 files - Ownship zigzag experiment data
  other_formats/                       35 MB  - Non-native format examples
    Scripts/                           4 files - Debrief JavaScript scripting examples
    TA_DUMMY_DATA/                     13 files - Towed array test data
    TimedImages/                       43 files - Time-stamped JPEG image sequence
    csv_gz/                            6 files - Compressed CSV exchange data
    repository/                        5 files - Eclipse P2 repository update scripts
    test_data/brt_import/              4 files - Bearing-time import test data
```

---

## Complete File Inventory

### Top-Level Files

#### Tracks (REP)

| File | Size | Tracks | Time Period | Description |
|------|-----:|--------|-------------|-------------|
| `boat1.rep` | 32 KB | NELSON | 951212 0500-1141 | Single vessel track, ~6.5 hours |
| `boat2.rep` | 33 KB | COLLINGWOOD | 951212 0500-1141 | Companion vessel to boat1 |
| `boattest.rep` | 24 KB | NELSON | 951212 0500-1141 | Test variant of boat1 |
| `sen_tracks.rep` | 22 KB | Frigate, New_SSK | 700103 | Two-vessel scenario with frigate and submarine |
| `split_sen_tracks.rep` | 11 KB | Frigate, New_SSK | 700103 | Split version of sensor tracks |
| `BULK_BLUE_TRACKS.rep` | 21 KB | HVU, T23_A, T23_B | -- | Blue force: high-value unit + 2 frigates |
| `BULK_RED_TRACKS.rep` | 2.6 MB | SSK_001 - SSK_300 | -- | Red force: 300 submarine tracks (ASSET-generated) |
| `lightweight_test_tracks.rep` | 1.2 MB | 39 tracks | -- | FisherOne fleet of 39 vessels |
| `uk_track.rep` | 27 KB | NELSON | 951212 | UK Track Exchange Format (CSV-style) |
| `sparse_track.rep` | 2.1 KB | NELSON | 100112 | Sparse track with irregular time gaps |
| `sparse_track_self_split.rep` | 2.2 KB | NELSON | 100112 | Sparse track demonstrating auto-split |
| `reverse_chrono.rep` | 243 B | NELSON | 100112 | Fixes in reverse chronological order |
| `ColorTest.rep` | 2.5 KB | test_track | 100112 | Track with color/symbol variations |
| `planning_tracks.rep` | 878 B | SENSOR_PLANNED | 100112 | Planned track with range/speed legs |
| `offset_times.rep` | 55 KB | Various | 100112 | Multi-track time offset test data |

#### Narratives (REP)

| File | Size | Lines | Description |
|------|-----:|------:|-------------|
| `narrative.rep` | 1.2 KB | 19 | Simple narrative entries for NELSON/COLLINGWOOD exercise |
| `narrative2.rep` | 1.7 KB | 19 | Typed narrative entries (COMEX, CMD_COMMENT, etc.) |
| `narrative_large.rep` | 682 KB | ~5,200 | Large narrative dataset |
| `narrative_bulk.rep` | 1.7 MB | 12,150 | Bulk NARRATIVE2 entries with lorem-ipsum text |

#### Shapes and Annotations (REP)

| File | Size | Description |
|------|-----:|-------------|
| `shapes.rep` | 18 KB | Rectangles, lines, circles, vectors, text labels, SVG symbols |
| `boat_1_2_arcs.rep` | 3.9 KB | Sensor arc definitions for NELSON/COLLINGWOOD |
| `formatter.rep` | 240 B | Display formatting directives (symbol/label/arrow intervals) |

#### Sensor Data (DSF)

| File | Size | Description |
|------|-----:|-------------|
| `sen_frig_sensor.dsf` | 8.0 KB | Frigate optic sensor bearings and ranges |
| `sen_missing_host_sensor.dsf` | 8.0 KB | Sensor data with missing host track reference |
| `sen_ssk_sensor.dsf` | 1.0 KB | SSK (submarine) sensor contacts |
| `sen_ssk_freq.dsf` | 671 B | SSK frequency-domain sensor contacts |

#### Plot Files (DPF)

| File | Size | Description |
|------|-----:|-------------|
| `sample.dpf` | 232 KB | Standard sample plot (NELSON + COLLINGWOOD tracks, sensors, TMA) |
| `boat1t.dpf` | 134 KB | Single-track plot of NELSON |
| `shapes.dpf` | 47 KB | Shape/annotation demonstration plot |
| `compositeTrack.dpf` | 3.5 KB | Composite/planned track example |
| `sen_tracks_with_narrative.dpf` | 259 KB | Tracks with integrated narrative entries |
| `sample_lots_of_sensors.dpf` | 3.7 MB | Dense sensor contact overlay |
| `samplescale.dpf` | 304 KB | Plot with scale/grid features |
| `PlotWithGeoTiff.dpf` | 25 KB | Plot referencing GeoTIFF raster chart |
| `DynamicShapeSetTest.dpf` | 272 KB | Dynamic (time-varying) shape sets |

#### Other Top-Level Files

| File | Size | Description |
|------|-----:|-------------|
| `SP27GTIF.tif` | 636 KB | GeoTIFF raster chart (699x929px, 8-bit greyscale) |
| `ssn_control.xml` | 1.2 KB | ASSET simulation observer configuration |

---

### Demo/ - Tutorial Scenarios (153 KB)

Step-by-step tutorial data organized into three workflows.

#### Demo/Analysis/ - Track Analysis Workflow

| File | Size | Description |
|------|-----:|-------------|
| `Analysis1_Areas.rep` | 255 B | Rectangle shape defining the exercise area |
| `Analysis2_Track1.rep` | 32 KB | First vessel track (NELSON) |
| `Analysis3_Track2.rep` | 33 KB | Second vessel track (COLLINGWOOD) |
| `Analysis4_Narrative.rep` | 2.0 KB | Typed narrative (NARRATIVE2) for both vessels |

#### Demo/Review/ - Post-Exercise Review Workflow

| File | Size | Description |
|------|-----:|-------------|
| `Review1_Tracks.rep` | 22 KB | Combined track data for both vessels |
| `Review2_Sensor1.dsf` | 8.0 KB | Frigate sensor contact data |
| `Review3_Sensor2.dsf` | 1.0 KB | Submarine sensor contact data |

#### Demo/TrialsPlanning/ - Trials Planning Workflow

| File | Size | Description |
|------|-----:|-------------|
| `TrialsPlanning1.dpf` | 4.3 KB | Basic planned track with shape annotations |
| `TrialsPlanning2.dpf` | 6.4 KB | Extended planning scenario |
| `TrialsPlanning3.dpf` | 6.5 KB | Full planning scenario with sensor overlay |
| `TrialsPlanning3_hits.rep` | 254 B | Period-text hit markers |
| `TrialsPlanning3_sensor.rep` | 17 KB | Sensor platform track data |
| `TrialsPlanning3_subject.rep` | 17 KB | Subject/target track data |

---

### MultiPath/ - Multi-Path Propagation (185 KB)

Test data for multi-path acoustic propagation analysis.

| File | Size | Description |
|------|-----:|-------------|
| `GapsTestTrack.dpf` | 183 KB | Track plot for gap analysis testing |
| `GapsTestIntervals.csv` | 465 B | Time interval definitions (start, end, label) |
| `GapsTestSVP.csv` | 269 B | Sound Velocity Profile data (depth vs. speed) |

---

### MultiStatics/ - Multi-Static Sonar (1.1 MB)

Scenarios involving multiple static sonar buoys and vessel sensor offsets.

| File | Size | Description |
|------|-----:|-------------|
| `ThreeBuoys.dpf` | 129 KB | Three-buoy sonar field scenario |
| `Vessel_with_Sensor_Offset.dpf` | 407 KB | Vessel with offset sensor positions |
| `Vessel_with_Sensor_Offset.rep` | 124 KB | REP version of sensor offset vessel |
| `Sensor_Offset_short.dpf` | 73 KB | Short sensor offset test |
| `BlueParallelCourses.dpf` | 70 KB | Blue force parallel course scenario |
| `OffsetTracks.dpf` | 70 KB | Track offset demonstration |
| `multistatics_buoyfield.rep` | 68 KB | Buoy field track data |
| `MultiStatic_Vessel.rep` | 34 KB | Multi-static vessel track |
| `buoy_test_track.rep` | 33 KB | Buoy test track data |
| `Offset_null_positions.rep` | 31 KB | Offset positions with null values |

---

### S2R/ - Ship-to-Report & Bug Reproduction (8.2 MB)

Data files created for specific feature testing and bug reproduction (S2R = "Steps to Reproduce").

| File | Size | Description |
|------|-----:|-------------|
| `2648_tx_TMA_to_DR.dpf` | 3.2 MB | TMA-to-Dead-Reckoning conversion test |
| `2648_target_plot.rep` | 1.5 MB | Target plot for issue 2648 |
| `Ambig_tracks.rep` | 165 KB | Ambiguous bearing track data (SENSOR2 format) |
| `Ambig_tracks2.dpf` | 98 KB | Ambiguous tracks plot variant |
| `Ambig_tracks2.rep` | 27 KB | Ambiguous tracks REP variant |
| `Ambig_tracks3.dpf` | 107 KB | Third ambiguous tracks variant |
| `Ambig_tracks_hover_north.rep` | 16 KB | North-hovering ambiguous track |
| `Ambig_tracks_hover_north_HM.dsf` | 11 KB | Hull-mounted sensor for north hover |
| `Ambig_tracks_hover_north_TA.dsf` | 10 KB | Towed array sensor for north hover |
| `nonsuch.rep` | 66 KB | NONSUCH vessel track data |
| `nonsuch_otg.rep` | 56 KB | NONSUCH on-the-go variant |
| `twin_ownship_sensors.rep` | 61 KB | Ownship with two named sensors (SENSOR3 format) |
| `legs_20.rep` | 136 KB | 20-leg track segmentation test |
| `turn.rep` | 40 KB | Track turning manoeuvre data |
| `turn.dpf` | 198 KB | Plot of turning manoeuvre |
| `turn.dsf` | 2.4 KB | Sensor data for turn scenario |
| `turn_single_point.rep` | 80 B | Single-point track (edge case) |
| `mid_flow.dpf` | 301 KB | Mid-analysis workflow state |
| `midflow2.dpf` | 307 KB | Mid-flow analysis variant |
| `midflow2a.dpf` | 310 KB | Mid-flow analysis variant (a) |
| `midflow2b.dpf` | 328 KB | Mid-flow analysis variant (b) |
| `dodgy_track.rep` | 10 KB | Problematic track data for error testing |
| `third_party_track.rep` | 547 B | Third-party track reference |
| `Polyline_Measurement.rep` | 1.2 KB | Polyline measurement/annotation |
| `sensor.dsf` | 4.2 KB | Standalone sensor file |
| `lengths.csv` | 71 B | Track segment lengths |

#### S2R/2553_missing_sensor_data/ (258 KB)

Test case for issue 2553 (missing sensor data handling).

| File | Size | Description |
|------|-----:|-------------|
| `BlueTrack.rep` | 11 KB | Blue (ownship) track |
| `RedTrack.rep` | 11 KB | Red (target) track |
| `BlueSensorBrg.dsf` | 11 KB | Blue sensor bearing data |
| `BlueSensorFreqMangled.dsf` | 11 KB | Malformed frequency data |
| `WorkingFreqPlot.dpf` | 213 KB | Working frequency plot state |

#### S2R/freq/ (691 KB)

Frequency-domain analysis data.

| File | Size | Description |
|------|-----:|-------------|
| `MultiTonalFreqPlot.dpf` | 366 KB | Multi-tonal frequency waterfall plot |
| `Twin_cpa.dpf` | 220 KB | Twin CPA (Closest Point of Approach) analysis |
| `osshipaa_3.rep` | 66 KB | Ownship track for frequency analysis |
| `osshipaa.txt` | 31 KB | Ownship frequency data (text format) |
| `Contact_bearings.dsf` | 4.7 KB | Contact bearing measurements |
| `Contact_bearings.txt` | 2.7 KB | Contact bearings in text format |

#### S2R/issue_1621/ (285 KB)

| File | Size | Description |
|------|-----:|-------------|
| `longer_track.rep` | 56 KB | Extended track for issue 1621 |
| `shorter_os.dpf` | 229 KB | Shorter ownship plot |

#### S2R/smooth_back_jumps/ (56 KB)

| File | Size | Description |
|------|-----:|-------------|
| `jumps.rep` | 56 KB | Track with backward time jumps for smoothing tests |

---

### SATC/ - Semi-Automatic Track Construction (2.8 MB)

Data for SATC genetic algorithm development and testing. SATC uses bearing-only sensor
data to generate target track solutions.

| File | Size | Description |
|------|-----:|-------------|
| `BlueTrack.dpf` | 242 KB | Blue (ownship) track plot |
| `BlueTrack.rep` | 18 KB | Blue track position data |
| `RedTrack.rep` | 18 KB | Red (target) track position data |
| `BlueSensor.dsf` | 26 KB | Blue sensor bearing observations |
| `BlueSensor_sparse.rep` | 4.1 KB | Sparse sensor contacts in REP format |
| `SliceDemo.rep` | 17 KB | SATC time-slice demonstration track |
| `SliceDemo.dsf` | 24 KB | SATC time-slice sensor data |
| `CompositeStraightTest.dpf` | 365 KB | Composite straight-leg SATC test |
| `Deb_North_Low_Bearing_Rate_s_turns.dpf` | 210 KB | Low bearing-rate S-turn scenario |
| `FreqTracks.dpf` | 171 KB | Frequency-based tracks for SATC |
| `FreqTracksQuantized.dpf` | 326 KB | Quantized frequency tracks |
| `MDA_Test.dpf` | 200 KB | MDA (Maritime Domain Awareness) test |
| `L1_OwnshipTrack.rep` | 5.2 KB | Level 1 ownship track |
| `L1_SubjectTrack.rep` | 4.5 KB | Level 1 subject track |
| `L1_OwnshipSensor.dsf` | 1.2 KB | Level 1 sensor data |
| `L2_Scenario.dpf` | 141 KB | Level 2 complete scenario |
| `1936_verification.dpf` | 237 KB | Historical verification scenario |
| `old_turn_example.dpf` | 178 KB | Turning example |
| `thirty_min_earlier_first_turn.dpf` | 111 KB | 30-minute-early first turn variant |
| `thirty_min_extra_leg.dpf` | 107 KB | 30-minute extra leg variant |
| `thiry_min_one.dpf` | 99 KB | 30-minute single variant |
| `B_Rate_Ownship.rep` | 114 KB | Bearing-rate ownship track |
| `B_Rate_Sensor.dsf` | 17 KB | Bearing-rate sensor data |
| `B_Rate_Data.xls` | 45 KB | Bearing-rate experiment spreadsheet |
| `1936_rundata.xls` | 123 KB | 1936 experiment run data |
| `1959_experiment.xls` | 9.5 KB | 1959 experiment data |

---

### SATC_Test/ - SATC Algorithm Test Datasets (27 MB)

Large datasets for comprehensive SATC algorithm validation.

#### Track Datasets (REP)

| File | Size | Description |
|------|-----:|-------------|
| `Dataset1.rep` | 1.5 MB | Large track dataset 1 |
| `Dataset2b.rep` | 1.6 MB | Large track dataset 2b |
| `Dataset3.rep` | 1.3 MB | Large track dataset 3 |
| `OtherOwnship.rep` | 1.7 MB | Alternative ownship track |
| `OtherOwnship_Trimmed.rep` | 252 KB | Trimmed ownship variant |
| `ownship_1.rep` | 78 KB | Ownship track 1 |
| `ownship_2.rep` | 49 KB | Ownship track 2 |
| `source_1.rep` | 74 KB | Source track 1 |
| `source_2.rep` | 49 KB | Source track 2 |
| `tug_1.rep` | 71 KB | Tug vessel track |

#### CSAD Sensor Datasets (CS2)

20 files totaling 12.6 MB. Naming convention: `s{scenario}b{bearing_config}{type}.cs2`

| File | Size | Description |
|------|-----:|-------------|
| `s0b4nb.cs2` | 155 KB | Scenario 0, 4-bearing, narrowband |
| `s0b5bb.cs2` | 507 KB | Scenario 0, 5-bearing, broadband |
| `s0b5nb.cs2` | 262 KB | Scenario 0, 5-bearing, narrowband |
| `s0b6bb.cs2` | 312 KB | Scenario 0, 6-bearing, broadband |
| `s0b6nb.cs2` | 293 KB | Scenario 0, 6-bearing, narrowband |
| `s0b7bb.cs2` | 1.1 MB | Scenario 0, 7-bearing, broadband |
| `s0b8bb.cs2` | 1.0 MB | Scenario 0, 8-bearing, broadband |
| `s1b5nb.cs2` | 99 KB | Scenario 1, 5-bearing, narrowband |
| `s1b6bb.cs2` | 476 KB | Scenario 1, 6-bearing, broadband |
| `s1b6nb.cs2` | 223 KB | Scenario 1, 6-bearing, narrowband |
| `s1b7bb.cs2` | 3.9 MB | Scenario 1, 7-bearing, broadband (largest) |
| `s1b8bb.cs2` | 940 KB | Scenario 1, 8-bearing, broadband |
| `s1b10vdpr.cs2` | 888 KB | Scenario 1, 10-bearing, VDPR |
| `s1b20bb.cs2` | 594 KB | Scenario 1, 20-bearing, broadband |
| `s4b0bb.cs2` | 428 KB | Scenario 4, baseline broadband |
| `s4b18bb.cs2` | 472 KB | Scenario 4, 18-bearing, broadband |
| `s4b18nb.cs2` | 426 KB | Scenario 4, 18-bearing, narrowband |
| `s4b20nb.cs2` | 237 KB | Scenario 4, 20-bearing, narrowband |
| `s7b8bb.cs2` | 205 KB | Scenario 7, 8-bearing, broadband |
| `s7b9bb.cs2` | 230 KB | Scenario 7, 9-bearing, broadband |

#### Other SATC Test Files

| File | Size | Description |
|------|-----:|-------------|
| `ScenarioOne_TargetZig.dpf` | 153 KB | Target zigzag scenario plot |
| `ScenarioOne_Gantt.PNG` | 101 KB | Gantt chart visualization |
| `TwoLongTracks.dpf.zip` | 751 KB | Compressed long-duration track pair |
| `OwnshipZig/OwnshipZig.xls` | 2.8 MB | Ownship zigzag experiment spreadsheet |
| `OwnshipZig/OwnshipZig.xlsx` | 1.0 MB | Same in XLSX format |
| `OwnshipZig/OwnshipZig2.xlsx` | 2.6 MB | Extended zigzag experiment |
| `OwnshipZig/DynamicGraph.xlsx` | 41 KB | Dynamic graph data |

---

### other_formats/ - Non-Native Format Examples (35 MB)

#### NMEA and Navigation Logs

| File | Size | Lines | Description |
|------|-----:|------:|-------------|
| `NMEA_TRIAL.log` | 4.2 MB | 109,087 | NMEA `$POSL` sentence log (position, heading, velocity) |
| `nmea.log` | 147 B | 6 | Minimal NMEA sample |
| `150304_0854.txt` | 2.1 KB | -- | Timestamped position data (2015-03-04) |
| `150304_0854_bad.txt` | 4.3 KB | -- | Malformed version for error testing |
| `150304_0914.txt` | 51 KB | -- | Extended position data |
| `150304_0924.txt` | 18 KB | -- | Further position data |

#### Track Exchange Formats

| File | Size | Description |
|------|-----:|-------------|
| `OTH_Gold.txt` | 7.2 KB | Over-The-Horizon Gold track exchange format |
| `antares.txt` | 560 B | Antares format position sample |
| `nisida_sample.txt` | 868 B | Nisida format position sample |
| `CLog_Trial.txt` | 946 KB | Command Log trial data |

#### GPS Exchange (GPX)

| File | Size | Description |
|------|-----:|-------------|
| `gpx_1_0.gpx` | 1.4 KB | GPX version 1.0 sample track |
| `gpx_1_1.gpx` | 1.4 KB | GPX version 1.1 sample track |
| `pinatest2.gpx` | 119 KB | Extended GPX track (Pina test) |
| `test_land_track.gpx` | 966 B | Land-based GPX track for testing |

#### CSV Data

| File | Size | Description |
|------|-----:|-------------|
| `CSV_EXCHANGE_SAMPLE.csv` | 147 KB | Debrief CSV exchange format sample |
| `ExportWizard.csv` | 628 B | Export wizard output sample |
| `context.csv` | 4.3 KB | Context/metadata CSV |

#### Compressed CSV (csv_gz/)

| File | Size | Description |
|------|-----:|-------------|
| `BARTON_Tracks_xxxx_BSensorTrack_xxxx.csv` | 4.1 KB | Sensor track data |
| `BARTON_xxxx_OSD_xxxx.csv` | 4.4 KB | OSD (Operational Summary Data) |
| `BARTON_xxxx_SystemTrack_xxxx.csv` | 9.7 KB | System-generated track data |
| (+ `.gz` compressed versions of each) | -- | Gzip-compressed variants |

#### Narrative Documents

| File | Size | Description |
|------|-----:|-------------|
| `FCS_narrative.doc` | 57 KB | FCS narrative format (Word 97) |
| `FCS_narrative.docx` | 17 KB | FCS narrative format (OOXML) |
| `FCS_narrative.pdf` | 26 KB | FCS narrative format (PDF) |
| `FCS_extra_narrativetypes.doc` | 55 KB | FCS with additional narrative types |
| `FCS_narrative_no_metadata.doc` | 82 KB | FCS without metadata headers |
| `FCS_narrative_no_metadata.pdf` | 111 KB | Same in PDF |
| `narrative.doc` | 13 KB | Simple Word narrative |
| `test_narrative.doc` | 11 KB | Test narrative document |
| `RiderNarrative.docx` | 74 KB | Rider narrative document |
| `RiderNarrative2.docx` | 75 KB | Rider narrative variant |
| `ASW Data Format.docx` | 107 KB | ASW data format specification |
| `ASW Data Format2.doc` | 31 KB | ASW format specification (legacy) |

#### Towed Array Dummy Data (TA_DUMMY_DATA/)

Test data for towed array sensor processing with various bearing reference modes.

| File | Size | Description |
|------|-----:|-------------|
| `Freq_BlueTrack.dpf` | 313 KB | Blue track with frequency data (plot) |
| `Freq_BlueTrack.rep` | 11 KB | Blue track position data |
| `Freq_RedTrack.rep` | 11 KB | Red track position data |
| `Freq_BlueSensor.dsf` | 22 KB | Blue sensor frequency contacts |
| `Freq_BlueSensor_NoPos.dsf` | 20 KB | Frequency contacts without positions |
| `Lots_of_OS_Legs.dpf` | 87 KB | Ownship multi-leg scenario |
| `TA_COG_ABS.dsf` | 13 KB | Towed array, absolute COG bearing |
| `TA_COG_REL.dsf` | 11 KB | Towed array, relative COG bearing |
| `TA_CONTAINS_NULLS.dsf` | 2.8 KB | Data with null/missing values |
| `TA_FORE_AFT.dsf` | 10 KB | Fore/aft ambiguity data |
| `TA_LONG_MODULES.dsf` | 25 KB | Long towed array module data |
| `TA_NG_BRG_COG_ABS.dsf` | 12 KB | Non-geographic bearing, absolute COG |
| `TA_SHORT_MODULES.dsf` | 23 KB | Short towed array module data |

#### Media Files

| File | Size | Description |
|------|-----:|-------------|
| `19951212_102956_1sec.avi` | 5.5 MB | Video recording (1995-12-12 10:29:56, 1-sec intervals) |
| `19951212_103415.avi` | 7.1 MB | Video recording (1995-12-12 10:34:15) |
| `AirborneSound.mp3` | 729 KB | Airborne acoustic recording |

#### Timed Images (TimedImages/)

43 JPEG images (~47 KB each) from 1995-12-12. Named with timestamp pattern
`YYYYMMDD_HHMMSS.jpg`, spanning 09:36:00 to 10:46:00 at ~100-second intervals.
Designed for time-synchronized image overlay during track playback.

#### Report Templates

| File | Size | Description |
|------|-----:|-------------|
| `master_template.pptx` | 73 KB | PowerPoint report template |
| `master_template_01.pptx` | 78 KB | Updated report template |

#### Configuration and Control Files

| File | Size | Description |
|------|-----:|-------------|
| `control.inp` | 153 B | DIS (Distributed Interactive Simulation) control input |
| `control_other.inp` | 152 B | Alternative DIS control input |
| `sqlite_sample_data.ini` | 296 B | Pepys database connection configuration |
| `lookup_test_control.xml` | 1.2 KB | Lookup control/test configuration |
| `for_gpx.xml` | 238 KB | Debrief XML formatted for GPX export testing |

#### Scripting Examples (Scripts/)

JavaScript scripts for Debrief's built-in scripting engine.

| File | Size | Description |
|------|-----:|-------------|
| `filter_matching_symbol.js` | 2.4 KB | Filter tracks by symbol type |
| `offset_ellipses.js` | 2.2 KB | Apply offset to error ellipses |
| `read_CSV.js` | 2.9 KB | Read CSV data into Debrief |
| `write_CSV.js` | 2.8 KB | Export Debrief data to CSV |

#### Bearing-Time Import Test Data (test_data/brt_import/)

| File | Size | Description |
|------|-----:|-------------|
| `HULL_ZERO.brt` | 786 B | Hull-mounted sensor bearing-time data (0m offset) |
| `TOWED_1000m.brt` | 786 B | Towed array bearing-time data (1000m offset) |
| `All Tracks.rep` | 12 KB | Track data for BRT import testing |
| `All Tracks Use Current Heading.rep` | 13 KB | Tracks using current heading reference |

#### Error/Malformed Data

| File | Size | Description |
|------|-----:|-------------|
| `BAD_boat2.rep` | 1.3 KB | Malformed REP file for parser error testing |
| `BAD_sample.dpf` | 312 B | Malformed DPF file for parser error testing |
| `150304_0854_bad.txt` | 4.3 KB | Malformed position data |

#### Repository Update Scripts (repository/)

Eclipse P2 repository update tooling (not maritime data).

| File | Size | Description |
|------|-----:|-------------|
| `update.sh` | 5.1 KB | Shell-based repository update script |
| `update_Python.py` | 3.1 KB | Python-based repository update script |
| `compositeArtifacts.xml` | 930 B | P2 composite artifacts descriptor |
| `compositeContent.xml` | 930 B | P2 composite content descriptor |
| `p2.index` | 136 B | P2 repository index |

#### Network Sender Scripts

| File | Size | Description |
|------|-----:|-------------|
| `sender.sh` | 960 B | Linux script for DIS PDU generation |
| `sender.bat` | 1.0 KB | Windows script for DIS PDU generation |

#### Pepys Database

| File | Size | Description |
|------|-----:|-------------|
| `pepys.sqlite` | 12.4 MB | SpatiaLite database with Pepys data model (33 platforms, 43 sensors, 12,239 states, 112 contacts) |

---

## Scenario Groupings for UI Development

The sample data can be categorized into logical scenarios useful for driving debrief-future
user interface development.

### 1. Basic Two-Vessel Exercise

**Files**: `boat1.rep`, `boat2.rep`, `narrative.rep` (or `narrative2.rep`)
**Content**: NELSON and COLLINGWOOD vessels, ~6.5 hours, with narrative events.
**UI Use**: Basic track display, dual-track comparison, narrative timeline.

### 2. Track with Sensors and TMA

**Files**: `sample.dpf` (or `sen_tracks.rep` + `sen_frig_sensor.dsf` + `sen_ssk_sensor.dsf`)
**Content**: Frigate and SSK tracks with sonar bearing data and TMA solutions.
**UI Use**: Sensor bearing overlay, TMA solution display, error ellipses.

### 3. Bulk Fleet Operations

**Files**: `BULK_BLUE_TRACKS.rep`, `BULK_RED_TRACKS.rep`
**Content**: 3 blue force vessels + 300 red force submarines.
**UI Use**: Large-scale track rendering, force-on-force display, filtering/search.

### 4. Multi-Static Sonar

**Files**: `MultiStatics/ThreeBuoys.dpf`, `MultiStatics/multistatics_buoyfield.rep`
**Content**: Static sonar buoy network with vessel tracks.
**UI Use**: Buoy field visualization, multi-static bearing intersection.

### 5. Frequency Analysis

**Files**: `S2R/freq/MultiTonalFreqPlot.dpf`, `SATC/FreqTracks.dpf`
**Content**: Multi-tonal frequency waterfall data.
**UI Use**: Frequency spectrum display, narrowband/broadband visualization.

### 6. SATC Track Reconstruction

**Files**: `SATC/BlueTrack.rep` + `SATC/BlueSensor.dsf` + `SATC/RedTrack.rep`
**Content**: Ownship track with sensor data and known target track for validation.
**UI Use**: SATC algorithm input/output, constraint visualization, solution comparison.

### 7. Shapes and Annotations

**Files**: `shapes.rep`, `shapes.dpf`
**Content**: All supported shape types (rectangles, circles, lines, vectors, polygons, text).
**UI Use**: Annotation rendering, shape editing tools.

### 8. Planning and Composite Tracks

**Files**: `compositeTrack.dpf`, `planning_tracks.rep`, `Demo/TrialsPlanning/`
**Content**: Planned routes with course/speed/distance legs.
**UI Use**: Route planning interface, leg editing, waypoint management.

### 9. Narrative Timeline

**Files**: `narrative_bulk.rep` (12,150 entries), `narrative_large.rep`
**Content**: Dense typed narrative entries with multiple tracks.
**UI Use**: Timeline view, narrative filtering, search, event categorization.

### 10. Dynamic/Time-Varying Display

**Files**: `DynamicShapeSetTest.dpf`
**Content**: Time-varying shape overlays (sensor coverage arcs, CZ rings).
**UI Use**: Time-stepped playback, dynamic coverage visualization.

### 11. Ambiguous Bearing Resolution

**Files**: `S2R/Ambig_tracks.rep`, `S2R/Ambig_tracks2.dpf`
**Content**: Sensor contacts with ambiguous (dual) bearing solutions.
**UI Use**: Ambiguity resolution interface, bearing fan display.

### 12. GeoTIFF Chart Overlay

**Files**: `PlotWithGeoTiff.dpf`, `SP27GTIF.tif`
**Content**: Track plot with raster chart underlay.
**UI Use**: Chart/map background rendering, georeferenced image overlay.

---

## Key Vessel/Track Names in Sample Data

| Track Name | Type | Appears In |
|------------|------|------------|
| NELSON | Blue force vessel | boat1.rep, sample.dpf, narrative.rep, Demo/ |
| COLLINGWOOD | Blue force vessel | boat2.rep, narrative.rep, Demo/ |
| Frigate | Surface combatant | sen_tracks.rep, Demo/Review/ |
| New_SSK | Submarine (target) | sen_tracks.rep |
| NONSUCH | Blue force vessel | S2R/nonsuch.rep |
| HVU | High Value Unit | BULK_BLUE_TRACKS.rep |
| T23_A, T23_B | Type 23 frigates | BULK_BLUE_TRACKS.rep |
| SSK_001 - SSK_300 | Red submarines | BULK_RED_TRACKS.rep |
| FisherOne_002 - FisherOne_040 | Fisher vessels | lightweight_test_tracks.rep |
| SENSOR | Ownship (generic) | S2R/ scenarios |
| SUBJECT | Target (generic) | S2R/ scenarios |

---

## Geographic Regions in Sample Data

| Region | Approximate Location | Files |
|--------|---------------------|-------|
| Western Atlantic | ~22N, 21W | boat1.rep, boat2.rep, sample.dpf, Demo/ |
| Persian Gulf area | ~26N, 51E | sen_tracks.rep |
| Central Atlantic | ~5N, 30E | S2R/nonsuch.rep, planning_tracks.rep |
| North Sea area | ~60N, 0E | SATC/, TA_DUMMY_DATA/ |
| UK coastal | ~51N, 5W | uk_track.rep |
| West Africa | ~10N, 10-17E | pepys.sqlite |

---

## Notes for debrief-future

1. **Core formats to support first**: REP (position fixes + narratives) and DSF (sensor data) cover the majority of use cases and are simple text formats.

2. **DPF for state persistence**: The DPF format captures complete application state. For debrief-future, consider whether to adopt DPF as-is or define a new persistence format.

3. **Test coverage breadth**: The `S2R/` directory contains edge cases (missing data, malformed input, single-point tracks, reverse chronology) that should inform error handling in new implementations.

4. **Scale testing**: `BULK_RED_TRACKS.rep` (300 tracks), `lightweight_test_tracks.rep` (39 tracks), and `narrative_bulk.rep` (12,150 entries) provide performance benchmarks.

5. **Pepys integration**: The `pepys.sqlite` database represents a modern relational data model that may inform debrief-future's data architecture.

6. **Media synchronization**: The `TimedImages/` directory and AVI files demonstrate time-synchronized media playback requirements.

7. **Multi-format import**: The `other_formats/` directory demonstrates the breadth of import formats (NMEA, GPX, OTH-Gold, CSV, BRT, etc.) that users expect.
