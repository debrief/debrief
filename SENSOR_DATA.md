# Debrief Sensor Data: Comprehensive Documentation

This document provides complete documentation of how Debrief manages, manipulates, edits, analyses, and presents sensor data. It is intended to support the implementation of sensor data support in Debrief-Future (the TS/browser-based application).

## Table of Contents

1. [Domain Overview](#1-domain-overview)
2. [Data Model](#2-data-model)
3. [Hierarchical Structure](#3-hierarchical-structure)
4. [Import / Export](#4-import--export)
5. [Rendering and Presentation](#5-rendering-and-presentation)
6. [Property Editing](#6-property-editing)
7. [Context Operations](#7-context-operations)
8. [Bearing Residual Analysis (Track Shift)](#8-bearing-residual-analysis-track-shift)
9. [Frequency Analysis](#9-frequency-analysis)
10. [TMA (Target Motion Analysis)](#10-tma-target-motion-analysis)
11. [Manual TMA and TMA Segments](#11-manual-tma-and-tma-segments)
12. [Array Offset and Towed Array Support](#12-array-offset-and-towed-array-support)
13. [Key Source Files Reference](#13-key-source-files-reference)

---

## 1. Domain Overview

In maritime analysis, **sensor data** represents observations made by a vessel's sensors (typically sonar). Each observation — called a **sensor contact** or **cut** — records the bearing (and optionally range and frequency) from the sensor to a detected target at a specific time.

Sensor data is fundamental to:
- **Target Motion Analysis (TMA)**: Estimating a target's course, speed, and position from a time-series of bearing observations
- **Track assessment**: Comparing measured sensor bearings against hypothesised target tracks to evaluate solution quality
- **Frequency analysis**: Using Doppler shift of received frequencies to constrain target motion estimates

### Key Domain Terms

| Term | Meaning |
|------|---------|
| **Cut** | A single sensor observation at one point in time |
| **Bearing** | The direction from the sensor to the detected target (degrees, 0-360) |
| **Ambiguous bearing** | A second possible bearing (common with towed arrays, which cannot distinguish port from starboard) |
| **Range** | Distance from sensor to target (optional; not always available from passive sonar) |
| **Frequency** | Received acoustic frequency (Hz); affected by Doppler shift from relative motion |
| **Sensor offset** | The physical displacement of the sensor array from the vessel's reference point |
| **Array centre** | The computed location of the sensor array at a given time, accounting for offset and tow geometry |
| **Doublet** | A pairing of one sensor observation with a corresponding target hypothesis, used for residual calculations |
| **Residual** | The error between a measured bearing/frequency and the value predicted by a target hypothesis |

---

## 2. Data Model

### 2.1 SensorContactWrapper (Individual Cut)

**Source:** [`org.mwc.debrief.legacy/src/Debrief/Wrappers/SensorContactWrapper.java`](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.legacy/src/Debrief/Wrappers/SensorContactWrapper.java) (1,544 lines)

Represents a single sensor observation. Implements `Watchable`, `TimeStampedDataItem`.

#### Core Fields

| Field | Type | Description | Source |
|-------|------|-------------|--------|
| `_DTG` | `HiResDate` | Timestamp of the observation (microsecond precision) | [L538](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.legacy/src/Debrief/Wrappers/SensorContactWrapper.java#L538) |
| `_bearing` | `double` | Primary bearing to target (degrees, 0-360) | [L550](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.legacy/src/Debrief/Wrappers/SensorContactWrapper.java#L550) |
| `_hasBearing` | `boolean` | Whether bearing data is present | [L545](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.legacy/src/Debrief/Wrappers/SensorContactWrapper.java#L545) |
| `_bearingAmbig` | `double` | Ambiguous (second) bearing (degrees); `Double.NaN` if absent | [L599](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.legacy/src/Debrief/Wrappers/SensorContactWrapper.java#L599) |
| `_hasAmbiguous` | `boolean` | Whether ambiguous bearing data is active | [L593](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.legacy/src/Debrief/Wrappers/SensorContactWrapper.java#L593) |
| `_range` | `WorldDistance` | Range to target; `null` if not available | [L543](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.legacy/src/Debrief/Wrappers/SensorContactWrapper.java#L543) |
| `_freq` | `double` | Received frequency (Hz) | [L607](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.legacy/src/Debrief/Wrappers/SensorContactWrapper.java#L607) |
| `_hasFreq` | `boolean` | Whether frequency data is present | [L605](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.legacy/src/Debrief/Wrappers/SensorContactWrapper.java#L605) |
| `_absoluteOrigin` | `WorldLocation` | Explicit sensor location; `null` to derive from host track | [L555](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.legacy/src/Debrief/Wrappers/SensorContactWrapper.java#L555) |
| `_calculatedOrigin` | `WorldLocation` | Cached computed origin (from host track + array offset) | [L560](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.legacy/src/Debrief/Wrappers/SensorContactWrapper.java#L560) |
| `_trackName` | `String` | Name of the host (ownship) track | [L533](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.legacy/src/Debrief/Wrappers/SensorContactWrapper.java#L533) |
| `_sensorName` | `String` | Name of the parent sensor | [L591](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.legacy/src/Debrief/Wrappers/SensorContactWrapper.java#L591) |
| `_theLabel` | `TextLabel` | Label text and display properties | [L585](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.legacy/src/Debrief/Wrappers/SensorContactWrapper.java#L585) |
| `_showLabel` | `boolean` | Whether to show the label | [L565](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.legacy/src/Debrief/Wrappers/SensorContactWrapper.java#L565) |
| `_myLineStyle` | `int` | Line style for the bearing line (SOLID, DASHED, etc.) | [L575](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.legacy/src/Debrief/Wrappers/SensorContactWrapper.java#L575) |
| `_theLineLocation` | `int` | Where on the line to place the label (START, MIDDLE, END) | [L590](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.legacy/src/Debrief/Wrappers/SensorContactWrapper.java#L590) |
| `_mySensor` | `SensorWrapper` | Reference to parent sensor | [L580](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.legacy/src/Debrief/Wrappers/SensorContactWrapper.java#L580) |

#### Key Methods

| Method | Purpose | Source |
|--------|---------|--------|
| `getCalculatedOrigin(WatchableList parent)` | Computes sensor position from host track + array offset. Returns cached value or calculates from `ArrayOffsetHelper.getArrayCentre()`. Falls back to `_absoluteOrigin` if set. | [L849](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.legacy/src/Debrief/Wrappers/SensorContactWrapper.java#L849) |
| `getFarEnd(WorldArea outerEnvelope)` | Returns the far end of the bearing line. If range is set, uses that distance. If no range, extends to the viewport boundary (capped at 5 degrees). | [L963](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.legacy/src/Debrief/Wrappers/SensorContactWrapper.java#L963) |
| `getAmbiguousFarEnd(WorldArea outerEnvelope)` | Same as `getFarEnd` but for the ambiguous bearing. | [L813](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.legacy/src/Debrief/Wrappers/SensorContactWrapper.java#L813) |
| `paint(WatchableList track, CanvasType dest, boolean keep_simple, int alpha)` | Renders the bearing line(s) and label onto a canvas. | [L1202](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.legacy/src/Debrief/Wrappers/SensorContactWrapper.java#L1202) |
| `rangeFrom(WorldLocation other)` | Calculates minimum distance from a point to the origin, far end, or nearest point on the bearing line. Used for hit-testing. | [L1337](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.legacy/src/Debrief/Wrappers/SensorContactWrapper.java#L1337) |
| `isBearingToPort()` | Determines whether the primary bearing is to port of the host vessel's course. Cached. | [L1151](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.legacy/src/Debrief/Wrappers/SensorContactWrapper.java#L1151) |
| `ditchBearing(boolean keepPort)` | Resolves ambiguity by keeping either the port or starboard bearing and discarding the other. | [L767](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.legacy/src/Debrief/Wrappers/SensorContactWrapper.java#L767) |
| `relBearing(double course, double bearing)` | Static utility: calculates relative bearing (±180°) given vessel course and absolute bearing. | [L516](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.legacy/src/Debrief/Wrappers/SensorContactWrapper.java#L516) |
| `resetColor()` | Resets colour to `null`, causing the contact to inherit colour from its parent `SensorWrapper`. | [L1402](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.legacy/src/Debrief/Wrappers/SensorContactWrapper.java#L1402) |
| `clearCalculatedOrigin()` | Invalidates the cached origin; forces recalculation on next paint. | [L720](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.legacy/src/Debrief/Wrappers/SensorContactWrapper.java#L720) |

#### Colour Inheritance

A `SensorContactWrapper` with a `null` colour inherits the colour of its parent `SensorWrapper`. Calling [`getColor()`](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.legacy/src/Debrief/Wrappers/SensorContactWrapper.java#L875) delegates to `_mySensor.getColor()` when the contact's own colour is null ([L881](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.legacy/src/Debrief/Wrappers/SensorContactWrapper.java#L881)). Individual contacts can override this with `setColor()`.

#### Comparison / Sorting

Contacts are sorted by DTG within their parent `SensorWrapper`. The [`compareTo()`](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.legacy/src/Debrief/Wrappers/SensorContactWrapper.java#L736) method compares by DTG. When two contacts share the same DTG, the comparator returns `1` (not `0`) to allow multiple contacts at the same time ([L748–L759](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.legacy/src/Debrief/Wrappers/SensorContactWrapper.java#L748)) — except when comparing the exact same object instance, which returns `0`.

### 2.2 SensorWrapper (Sensor Container)

**Source:** [`org.mwc.debrief.legacy/src/Debrief/Wrappers/SensorWrapper.java`](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.legacy/src/Debrief/Wrappers/SensorWrapper.java) (1,639 lines, [class declaration at L207](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.legacy/src/Debrief/Wrappers/SensorWrapper.java#L207))

Container for a time-ordered collection of `SensorContactWrapper` items. Extends `TacticalDataWrapper` (which extends `PlainWrapper` and implements `Layer`).

#### Core Fields

| Field | Type | Description | Source |
|-------|------|-------------|--------|
| `_myContacts` | `SortedSet<Editable>` | Time-ordered set of `SensorContactWrapper` objects (inherited from `TacticalDataWrapper`) | [TacticalDataWrapper](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.legacy/src/Debrief/Wrappers/TacticalDataWrapper.java) |
| `_sensorOffset` | `WorldDistance.ArrayLength` | Forward/backward offset of the sensor from the platform's reference point | [L899](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.legacy/src/Debrief/Wrappers/SensorWrapper.java#L899) |
| `_arrayCentreMode` | `ArrayCentreMode` | Method used to calculate the array centre (PLAIN, WORM, or measured dataset) | [L906](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.legacy/src/Debrief/Wrappers/SensorWrapper.java#L906) |
| `_baseFrequency` | `double` | The base (transmitted) frequency of the source; used for Doppler calculations | [L912](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.legacy/src/Debrief/Wrappers/SensorWrapper.java#L912) |
| `_lastDataFrequency` | `HiResDate` | Resample interval for display thinning | [L918](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.legacy/src/Debrief/Wrappers/SensorWrapper.java#L918) |
| `_lastVisibleFrequency` | `HiResDate` | Visible frequency (how often to show cuts in the display) | [TacticalDataWrapper L325](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.legacy/src/Debrief/Wrappers/TacticalDataWrapper.java#L325) |
| `_myHost` | `TrackWrapper` | Reference to the parent track (inherited from `TacticalDataWrapper`) | [TacticalDataWrapper](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.legacy/src/Debrief/Wrappers/TacticalDataWrapper.java) |
| `_additionalData` | `AdditionalData` | Supplemental data (e.g., measured array position datasets) | [L924](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.legacy/src/Debrief/Wrappers/SensorWrapper.java#L924) |

#### Key Methods

| Method | Purpose | Source |
|--------|---------|--------|
| `add(Editable)` | Adds a `SensorContactWrapper`, updates the time period, and sets the contact's sensor reference. | [L960](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.legacy/src/Debrief/Wrappers/SensorWrapper.java#L960) |
| `append(Layer, Color)` | Merges another sensor's contacts into this one. Preserves individual contact colours if different from the target sensor's default. | [L978](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.legacy/src/Debrief/Wrappers/SensorWrapper.java#L978) |
| `getNearestTo(HiResDate)` | Returns contact(s) at or nearest to the specified time. Handles multiple contacts at the same DTG. | [L1324](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.legacy/src/Debrief/Wrappers/SensorWrapper.java#L1324) |
| `getItemsBetween(HiResDate, HiResDate)` | Returns all contacts within a time range. | [TacticalDataWrapper L617](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.legacy/src/Debrief/Wrappers/TacticalDataWrapper.java#L617) |
| `filterListTo(HiResDate, HiResDate)` | Shows/hides contacts by time window. | [TacticalDataWrapper L557](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.legacy/src/Debrief/Wrappers/TacticalDataWrapper.java#L557) |
| `setResampleDataAt(HiResDate)` | Resamples (decimates) sensor data at a fixed time interval using linear interpolation of bearing, frequency, range, and ambiguous bearing. | [L1563](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.legacy/src/Debrief/Wrappers/SensorWrapper.java#L1563) |
| `setVisibleFrequency(HiResDate)` | Controls how frequently cuts are displayed (e.g., show every 30 seconds). | [TacticalDataWrapper L951](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.legacy/src/Debrief/Wrappers/TacticalDataWrapper.java#L951) |
| `getArrayCentre(HiResDate, WorldLocation, TrackWrapper)` | Calculates the sensor array position at a given time, delegating to `ArrayOffsetHelper`. | [L1115](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.legacy/src/Debrief/Wrappers/SensorWrapper.java#L1115) |
| `clearChildOffsets()` | Invalidates all child contacts' cached origins; called when the sensor offset or host track changes. | [L1022](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.legacy/src/Debrief/Wrappers/SensorWrapper.java#L1022) |
| `makeCopy(TimeStampedDataItem)` | Deep-copies a `SensorContactWrapper` (all fields). | [L1461](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.legacy/src/Debrief/Wrappers/SensorWrapper.java#L1461) |
| `mergeSensors(Editable, Layers, Layer, Editable[])` | Static method to merge multiple `SensorWrapper` objects into a single target. | [L873](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.legacy/src/Debrief/Wrappers/SensorWrapper.java#L873) |

### 2.3 TacticalDataWrapper (Base Class)

**Source:** [`org.mwc.debrief.legacy/src/Debrief/Wrappers/TacticalDataWrapper.java`](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.legacy/src/Debrief/Wrappers/TacticalDataWrapper.java) ([class at L52](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.legacy/src/Debrief/Wrappers/TacticalDataWrapper.java#L52))

Abstract base class shared by `SensorWrapper` and `TMAWrapper`. Provides:

- **Time-ordered storage**: `TreeSet<Editable>` sorted by DTG
- **Time period tracking**: Cached `_timePeriod` (start/end DTG)
- **Decimation/resampling**: [`decimate()`](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.legacy/src/Debrief/Wrappers/TacticalDataWrapper.java#L454) method with `LinearInterpolator` that handles bearing wraparound at 0°/360°
- **Visibility frequency**: [`setVisibleFrequency()`](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.legacy/src/Debrief/Wrappers/TacticalDataWrapper.java#L951) to thin displayed cuts
- **Layer interface**: [`paint()`](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.legacy/src/Debrief/Wrappers/TacticalDataWrapper.java#L744), `getBounds()`, `elements()`
- **Host track reference**: `_myHost`, `setHost()`, `getHost()`

### 2.4 Doublet (Observation-Hypothesis Pairing)

**Source:** [`org.mwc.debrief.legacy/src/Debrief/Wrappers/Track/Doublet.java`](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.legacy/src/Debrief/Wrappers/Track/Doublet.java) ([class at L32](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.legacy/src/Debrief/Wrappers/Track/Doublet.java#L32))

Pairs a sensor contact with a target fix and host fix for residual calculations.

#### Fields

| Field | Type | Description |
|-------|------|-------------|
| `_sensor` | `SensorContactWrapper` | The sensor observation |
| `_targetFix` | `FixWrapper` | The target's hypothesised position |
| `_targetTrack` | `TrackSegment` | Parent TMA segment containing the target |
| `_hostFix` | `FixWrapper` | The observing platform's position/velocity |

#### Key Methods

| Method | Purpose | Source |
|--------|---------|--------|
| `getMeasuredBearing()` | Returns the raw bearing from the sensor contact | [L323](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.legacy/src/Debrief/Wrappers/Track/Doublet.java#L323) |
| `getCalculatedBearing(sensorOffset, targetOffset)` | Computes the geometric bearing from sensor location to target location | [L246](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.legacy/src/Debrief/Wrappers/Track/Doublet.java#L246) |
| `calculateBearingError(measured, calculated)` | Returns error wrapped to ±180° | [L156](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.legacy/src/Debrief/Wrappers/Track/Doublet.java#L156) |
| `getMeasuredFrequency()` | Returns the raw frequency from the sensor contact | [L327](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.legacy/src/Debrief/Wrappers/Track/Doublet.java#L327) |
| `getCorrectedFrequency()` | Removes the observer's Doppler shift from the measured frequency | [L286](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.legacy/src/Debrief/Wrappers/Track/Doublet.java#L286) |
| `getPredictedFrequency(speedOfSoundKts)` | Predicts the expected frequency including the target's Doppler shift | [L337](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.legacy/src/Debrief/Wrappers/Track/Doublet.java#L337) |
| `calculateFreqError(measured, calculated)` | Returns frequency error | [L175](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.legacy/src/Debrief/Wrappers/Track/Doublet.java#L175) |
| `getAmbiguousMeasuredBearing()` | Returns the ambiguous bearing (if present) | [L233](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.legacy/src/Debrief/Wrappers/Track/Doublet.java#L233) |
| `getColor()` | Returns the colour of the sensor contact | — |

---

## 3. Hierarchical Structure

Sensor data lives within a three-level hierarchy inside a track:

```
TrackWrapper
├── [Positions] (SegmentList → TrackSegment → FixWrapper)
├── Sensors (SplittableLayer)
│   ├── SensorWrapper "Bow Array"
│   │   ├── SensorContactWrapper (09:00:00, brg=045°)
│   │   ├── SensorContactWrapper (09:00:30, brg=046°)
│   │   └── ... more cuts ...
│   └── SensorWrapper "Towed Array"
│       ├── SensorContactWrapper (09:00:00, brg=045°, ambig=315°)
│       └── ... more cuts ...
├── Solutions (TMAWrapper objects)
│   └── TMAWrapper "TMA_091500"
│       └── TMAContactWrapper (ellipse + bearing line)
└── Dynamic Shapes (DynamicTrackShapeWrapper objects)
    └── DynamicTrackCoverageWrapper "fwd sensor arc"
```

### Track-Sensor Relationship

**Adding a sensor to a track (`TrackWrapper.add()`):**
- If a sensor with the same name already exists, contacts are **merged** into the existing sensor via `append()`
- Otherwise, a new `SensorWrapper` is added to the `_mySensors` layer
- The sensor's host and track name are set

**Removing a sensor (`TrackWrapper.removeElement()`):**
- The sensor is removed from `_mySensors`
- The sensor's host reference is cleared to `null`
- Individual `SensorContactWrapper` items can also be removed — the track iterates all sensors to find and remove the contact

**Propagating changes:**
- When a fix moves (`fixMoved()`), all sensors are notified via `setHost(this)` to recalculate origins
- When the track's time period changes, `filterListTo()` and `trimTo()` are propagated to all sensors
- Decimation (`decimate()`) is propagated to all sensors

---

## 4. Import / Export

### 4.1 REP (Replay) Format — Import

Debrief supports four progressive versions of sensor lines in `.rep` files. All are registered in [`ImportReplay.checkImporters()`](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.legacy/src/Debrief/ReaderWriter/Replay/ImportReplay.java#L848) (sensor importers at [L871–L873](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.legacy/src/Debrief/ReaderWriter/Replay/ImportReplay.java#L871), sensor arc at [L891](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.legacy/src/Debrief/ReaderWriter/Replay/ImportReplay.java#L891)).

#### SENSOR (Version 1)

**Line prefix:** `;SENSOR:`

```
;SENSOR: YYMMDD HHMMSS.SSS TRACKNAME @@ DD MM SS.SS H DDD MM SS.SS H BBB.B RRRR SENSORNAME label text
```

| Field | Description |
|-------|-------------|
| Date/Time | `YYMMDD HHMMSS.SSS` |
| Track Name | Host track (may be quoted for multi-word names) |
| Symbology | 2-character code: 1st char = colour, 2nd char = line style |
| Sensor Location | Lat/lon in DMS format, or `NULL` to derive from host track |
| Bearing | Degrees (0-360), or `NULL` / `NAN` |
| Range | Yards, or `NULL` |
| Sensor Name | May be quoted |
| Label | Free text to end of line |

**Source:** [`ImportSensor.java`](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.legacy/src/Debrief/ReaderWriter/Replay/ImportSensor.java) ([class at L114](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.legacy/src/Debrief/ReaderWriter/Replay/ImportSensor.java#L114), [`readThisLine()` at L230](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.legacy/src/Debrief/ReaderWriter/Replay/ImportSensor.java#L230))

#### SENSOR2 (Version 2) — Adds Ambiguous Bearing and Frequency

**Line prefix:** `;SENSOR2:`

```
;SENSOR2: YYMMDD HHMMSS.SSS TRACKNAME @@ LAT LON BBB.B CCC.C FFF.F RRRR SENSORNAME label
```

Additional fields vs. V1:
- **Ambiguous Bearing** (`CCC.C`): Second bearing, or `NULL`
- **Frequency** (`FFF.F`): Hz, or `NULL`

**Source:** [`ImportSensor2.java`](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.legacy/src/Debrief/ReaderWriter/Replay/ImportSensor2.java) ([class at L111](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.legacy/src/Debrief/ReaderWriter/Replay/ImportSensor2.java#L111), [`readThisLine()` at L207](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.legacy/src/Debrief/ReaderWriter/Replay/ImportSensor2.java#L207))

#### SENSOR3 (Version 3) — Adds Accuracy Fields

**Line prefix:** `;SENSOR3:`

```
;SENSOR3: YYMMDD HHMMSS.SSS TRACKNAME @@ LAT LON BBB.B ACC.B FFF.F ACC.F RRRR SENSORNAME label
```

Additional fields vs. V2:
- **Bearing Accuracy** (`ACC.B`): Parsed but **not currently stored** in the wrapper (noted as TODO)
- **Frequency Accuracy** (`ACC.F`): Parsed but **not currently stored**

**Source:** [`ImportSensor3.java`](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.legacy/src/Debrief/ReaderWriter/Replay/ImportSensor3.java) ([class at L46](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.legacy/src/Debrief/ReaderWriter/Replay/ImportSensor3.java#L46), [`readThisLine()` at L138](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.legacy/src/Debrief/ReaderWriter/Replay/ImportSensor3.java#L138))

#### SENSORARC — Dynamic Sensor Coverage

**Line prefix:** `;SENSORARC`

```
;SENSORARC YYMMDD HHMMSS.SSS YYMMDD HHMMSS.SSS TRACKNAME @@ LEFT RIGHT INNER OUTER LABEL
```

Creates [`DynamicTrackCoverageWrapper`](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.legacy/src/Debrief/Wrappers/DynamicTrackShapes/DynamicTrackCoverageWrapper.java#L36) (visual sensor coverage fan/wedge). This is a shape, not observational data.

**Source:** [`ImportSensorArc.java`](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.legacy/src/Debrief/ReaderWriter/Replay/ImportSensorArc.java) ([class at L35](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.legacy/src/Debrief/ReaderWriter/Replay/ImportSensorArc.java#L35), [`readThisLine()` at L152](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.legacy/src/Debrief/ReaderWriter/Replay/ImportSensorArc.java#L152))

**Note:** REP export of sensor contacts is **not implemented** (returns a comment line).

### 4.2 XML/DPF Format — Full Round-Trip

#### SensorWrapper XML

**Element:** `<sensor>`

```xml
<sensor Name="Bow Array" Visible="TRUE" TrackName="OWNSHIP"
        LineThickness="1" ArrayMode="WORM" BaseFrequency="150.0">
  <colour Value="RED"/>
  <WorldDistance Units="Metres">500</WorldDistance>  <!-- sensor offset -->
  <sensor_contact ... />
  <sensor_contact ... />
</sensor>
```

**Exported attributes:** Name, Visible, TrackName, LineThickness, ArrayMode, BaseFrequency, Colour, SensorOffset.

**Source:** [`SensorHandler.java`](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.legacy/src/Debrief/ReaderWriter/XML/Tactical/SensorHandler.java) ([class at L33](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.legacy/src/Debrief/ReaderWriter/XML/Tactical/SensorHandler.java#L33), [`exportSensor()` at L40](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.legacy/src/Debrief/ReaderWriter/XML/Tactical/SensorHandler.java#L40))

#### SensorContactWrapper XML

**Element:** `<sensor_contact>`

```xml
<sensor_contact Dtg="951212 054902.486" Visible="true"
                Bearing="32.757" AmbiguousBearing="212.757"
                HasAmbiguousBearing="true"
                Frequency="150.5" HasFrequency="true"
                Label="contact 1" LabelShowing="false"
                LabelLocation="Left" LineStyle="Solid"
                PutLabelAt="Middle" Comment="some note">
  <colour Value="GREEN"/>
  <Range Units="Metres">5000</Range>
  <centre Lat="60.0" Long="0.0"/>
</sensor_contact>
```

**All fields are exported and re-imported**, including: DTG, bearing, ambiguous bearing, has-ambiguous flag, frequency, has-frequency flag, range (with units), label, label visibility, label location, line style, put-label-at, colour, origin location, and comment.

**Source:** [`SensorContactHandler.java`](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.legacy/src/Debrief/ReaderWriter/XML/Tactical/SensorContactHandler.java) ([class at L33](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.legacy/src/Debrief/ReaderWriter/XML/Tactical/SensorContactHandler.java#L33), [`exportFix()` at L85](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.legacy/src/Debrief/ReaderWriter/XML/Tactical/SensorContactHandler.java#L85))

### 4.3 Flat File / SAM Format — Export Only

**Source:** [`FlatFileExporter.java`](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.legacy/src/Debrief/ReaderWriter/FlatFile/FlatFileExporter.java) ([class at L55](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.legacy/src/Debrief/ReaderWriter/FlatFile/FlatFileExporter.java#L55), [`export()` at L472](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.legacy/src/Debrief/ReaderWriter/FlatFile/FlatFileExporter.java#L472))

Exports tab-separated data for Strand SAM analysis. Includes per-row: sensor status (bit flags for bearing/frequency presence), sensor XY position, bearing, frequency, bearing accuracy, frequency accuracy, and sensor type.

V1.01 format supports **dual sensor export** (two sensors per row).

### 4.4 Doppler Shift Export

**Source:** [`DopplerShiftExporter.java`](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.legacy/src/Debrief/ReaderWriter/FlatFile/DopplerShift/DopplerShiftExporter.java) ([class at L51](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.legacy/src/Debrief/ReaderWriter/FlatFile/DopplerShift/DopplerShiftExporter.java#L51), [`export()` at L91](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.legacy/src/Debrief/ReaderWriter/FlatFile/DopplerShift/DopplerShiftExporter.java#L91))

Exports measured vs predicted frequency in CSV:
```
base freq,150.0
time, measured frequency, predicted frequency
```

### 4.5 Symbology Colour Codes (REP Format)

The first character of the 2-character symbology code maps to colours:

| Code | Colour | Code | Colour |
|------|--------|------|--------|
| `@` | White | `A` | Blue |
| `B` | Green | `C` | Red |
| `D` | Yellow | `E` | Magenta |
| `F` | Orange | `G` | Purple |
| `H` | Cyan | `I` | Brown |
| `J` | Light Green | `K` | Pink |
| `L` | Gold | `M` | Light Gray |
| `N` | Gray | `O` | Dark Gray |
| `Q` | Black | `R` | Medium Blue |

---

## 5. Rendering and Presentation

### 5.1 Bearing Line Rendering ([`SensorContactWrapper.paint()`](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.legacy/src/Debrief/Wrappers/SensorContactWrapper.java#L1202))

The primary rendering method draws one or two bearing lines from the sensor origin to the far end.

**Rendering pipeline:**

```
1. Check visibility → return if not visible
2. Check parent track visibility at this DTG
3. Calculate origin (from host track + array offset, or absolute)
4. Convert origin to screen coordinates → return if off-screen
5. If hasBearing:
   a. Calculate far end of bearing line (using range or viewport extent)
   b. Determine colours for primary and ambiguous bearing
   c. Set line style (unless in snail/simple mode)
   d. Draw primary bearing line (with alpha transparency)
   e. If hasAmbiguousBearing: draw second line
   f. If label visible: draw label at START/MIDDLE/END of line
6. Restore solid line style
```

**Bearing line extent:** If the contact has a range, the line extends to that range. If no range is present, the line extends to twice the viewport dimension, capped at `MAXIMUM_SENSOR_BEARING_RANGE` (5 degrees).

### 5.2 Ambiguous Bearing Colour Convention

When ambiguous bearing is present and active:
- The **port** bearing is drawn in the base colour
- The **starboard** bearing is drawn in `baseColor.darker()`

This is determined by [`isBearingToPort()`](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.legacy/src/Debrief/Wrappers/SensorContactWrapper.java#L1151), which calculates the relative bearing from the host vessel's course at the contact's DTG.

### 5.3 Transparency

Sensor transparency is configurable via the preference `"SensorTransparency"` (0-255, where 255 = fully opaque). It is passed as the `alpha` parameter to `ExtendedCanvasType.drawLine()`.

### 5.4 Snail Mode (Time-Trail) Rendering

In snail mode, sensor contacts are rendered with a **time-fading** effect:

**Source:** [`SnailDrawTacticalContact.java`](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.legacy/src/Debrief/GUI/Tote/Painters/SnailDrawTacticalContact.java) ([class at L39](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.legacy/src/Debrief/GUI/Tote/Painters/SnailDrawTacticalContact.java#L39), [`drawMe()` at L111](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.legacy/src/Debrief/GUI/Tote/Painters/SnailDrawTacticalContact.java#L111))

```
For each contact in (currentDTG - trailLength) to currentDTG:
   proportion = (trailLength - age) / trailLength   // 1.0 = newest, 0.0 = oldest
   fadedColor = Color(R * proportion, G * proportion, B * proportion)
   paint contact with fadedColor
```

Older contacts fade towards black, creating a visual trail of bearing observations over time.

### 5.5 Label Positioning

Labels can be placed at three positions on the bearing line:
- **START**: At the sensor origin
- **MIDDLE**: At the midpoint of the bearing line
- **END**: At the far end of the bearing line

Label text is the contact's `_theLabel` (a `TextLabel`), with relative positioning (LEFT, CENTER, RIGHT) and colour matching the bearing line.

### 5.6 Outline View

In the Outline/Layer Manager view:
- `SensorWrapper` appears as a named child of the "Sensors" folder under its track
- Displays as `"Sensor:NAME (N cuts)"` where N is the contact count
- Each `SensorContactWrapper` is listed as a child, named by its formatted DTG

### 5.7 Multi-Line Tooltip

`SensorContactWrapper` implements `MultiLineTooltipProvider` (via [`getMultiLineName()`](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.legacy/src/Debrief/Wrappers/SensorContactWrapper.java#L1054)):
```
Sensor: [sensor_name]
DTG: [formatted_date_time]
Track: [label]
```

---

## 6. Property Editing

### 6.1 SensorWrapper Properties

Exposed via [`SensorWrapper.SensorInfo`](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.legacy/src/Debrief/Wrappers/SensorWrapper.java#L216) (implements `DynamicDescriptors` — properties change based on array mode):

| Property | Type | Category | Description |
|----------|------|----------|-------------|
| Name | String | — | Sensor name |
| Visible | Boolean | — | Overall visibility |
| Line Thickness | Integer | — | Width of all bearing lines (with `LineWidthPropertyEditor`) |
| Default Color | Color | — | Base colour for contacts |
| Coverage | String | Read-only | Start/finish DTG range |
| Visible Frequency | HiResDate | Display | How frequently to display cuts (with `TimeFrequencyPropertyEditor`) |
| Base Frequency | Double | Expert, Optional | Source frequency for Doppler calculations |
| Resample Data At | HiResDate | Expert, Temporal | Resample interval (with `TimeFrequencyPropertyEditor`) |
| Array Centre Mode | ArrayCentreMode | Expert, Spatial | PLAIN, WORM, or measured dataset (with `ArrayCentreModePropertyEditor`) |
| Sensor Offset | ArrayLength | — | Forward/backward offset (only shown in legacy array modes) |

### 6.2 SensorContactWrapper Properties

Exposed via [`SensorContactWrapper.SensorContactInfo`](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.legacy/src/Debrief/Wrappers/SensorContactWrapper.java#L254) (extends `Griddable`):

| Property | Type | Category | Description |
|----------|------|----------|-------------|
| Label | String | Format | Text label |
| Visible | Boolean | Format | Contact visibility |
| Label Visible | Boolean | Format | Whether to show label |
| Color | Color | Format | Contact colour (null = inherit) |
| Has Frequency | Boolean | Optional | Whether frequency data exists |
| Has Bearing | Boolean | Optional | Whether bearing data exists |
| Has Ambiguous Bearing | Boolean | Optional | Whether ambiguous bearing is active |
| Frequency | Double | Optional | Frequency value (Hz) |
| Ambiguous Bearing | Double | Optional | Second bearing value (degrees) |
| Label Location | Integer | — | Label relative position (with `LocationPropertyEditor`) |
| Put Label At | Integer | — | Where on line (START/MIDDLE/END) (with `LineLocationPropertyEditor`) |
| Line Style | Integer | — | SOLID, DASHED, etc. (with `LineStylePropertyEditor`) |
| Range | WorldDistance | Spatial | Range to target (with units) |
| Comment | String | Optional | Free-text comment |
| Bearing | Double | Spatial | Primary bearing (degrees) |
| Origin | WorldLocation | Spatial | Sensor location (only if explicitly set) |

### 6.3 Griddable Support

`SensorContactInfo` extends `Griddable`, enabling **bulk grid editing** of multiple contacts. The griddable view exposes: Label, Visible, Frequency, Bearing, Range, Ambiguous Bearing.

### 6.4 Right-Click Actions (Per Contact)

From `SensorContactInfo.getUndoableActions()`:
- **"Keep port bearing"**: Resolves ambiguity by keeping the port bearing (undoable)
- **"Keep starboard bearing"**: Resolves ambiguity by keeping the starboard bearing (undoable)

Additional methods:
- **"Reset Color"**: Resets colour to inherit from parent sensor
- **"Clear Origin"**: Removes absolute origin, reverts to calculated origin from host track

---

## 7. Context Operations

These are right-click menu actions available in the plot and outline views.

### 7.1 Sensor Creation

[**GenerateNewSensor**](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.core/src/org/mwc/debrief/core/ContextOperations/GenerateNewSensor.java) ([class at L46](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.core/src/org/mwc/debrief/core/ContextOperations/GenerateNewSensor.java#L46))
- **Trigger:** Right-click on a Track or "Sensors" layer
- **Menu:** "Add new sensor"
- **Wizard:** Collects sensor name and default colour
- **Result:** Creates new `SensorWrapper` and adds to track
- **Undo:** Supported (removes sensor)

[**GenerateNewSensorContact**](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.core/src/org/mwc/debrief/core/ContextOperations/GenerateNewSensorContact.java) ([class at L52](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.core/src/org/mwc/debrief/core/ContextOperations/GenerateNewSensorContact.java#L52))
- **Trigger:** Right-click on a `SensorWrapper`
- **Menu:** "Generate contact for this sensor"
- **Wizard:** Collects DTG (defaults to current time), range, bearing, and colour
- **Result:** Creates `SensorContactWrapper` and adds to sensor
- **Undo:** Supported (removes contact)

[**GenerateNewInsertSensorArcAction**](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.core/src/org/mwc/debrief/core/ContextOperations/GenerateNewInsertSensorArcAction.java) ([class at L60](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.core/src/org/mwc/debrief/core/ContextOperations/GenerateNewInsertSensorArcAction.java#L60))
- **Trigger:** Right-click on a `TrackWrapper`
- **Menu:** "Insert new Sensor Arc"
- **Wizard:** Collects start/end times, arc bounds (left/right angles, inner/outer range), and styling
- **Result:** Creates [`DynamicTrackCoverageWrapper`](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.legacy/src/Debrief/Wrappers/DynamicTrackShapes/DynamicTrackCoverageWrapper.java#L36) (visual sensor coverage fan)
- **Undo:** Supported

### 7.2 Sensor Data Manipulation

[**MergeContacts**](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.core/src/org/mwc/debrief/core/ContextOperations/MergeContacts.java) ([class at L39](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.core/src/org/mwc/debrief/core/ContextOperations/MergeContacts.java#L39))
- **Trigger:** Select 2+ `SensorWrapper` objects
- **Menu:** "Merge sensors into [first sensor name]"
- **Behaviour:** All contacts from source sensors are transferred to the target sensor. Individual contact colours are preserved if they differ from the target's default colour.
- **Undo:** NOT supported (irreversible)

[**RainbowShadeSonarCuts**](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.core/src/org/mwc/debrief/core/ContextOperations/RainbowShadeSonarCuts.java) ([class at L46](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.core/src/org/mwc/debrief/core/ContextOperations/RainbowShadeSonarCuts.java#L46))
- **Trigger:** Select sensor(s) or sensor contact(s)
- **Menu:** Submenu "Shading" with three options:
  - "Shade in rainbow colors" — Maps time to hue (HSB colour space)
  - "Shade in blue-red spectrum" — Maps time to blue→red gradient
  - "Reset shading" — Calls `resetColor()` on all contacts
- **Undo:** Supported

### 7.3 Sensor Analysis

[**GenerateSensorRangePlot**](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.core/src/org/mwc/debrief/core/ContextOperations/GenerateSensorRangePlot.java) ([class at L70](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.core/src/org/mwc/debrief/core/ContextOperations/GenerateSensorRangePlot.java#L70))
- **Trigger:** Select sensor(s) AND track(s)
- **Menu:** "View sensor range plot (one sensor versus one track)" (and variants for multiple)
- **Behaviour:** Calculates range from sensor array centre to track position at each fix time. Displays as JFreeChart time-series plot.

[**CopyBearingsToClipboard**](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.core/src/org/mwc/debrief/core/ContextOperations/CopyBearingsToClipboard.java) ([class at L92](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.core/src/org/mwc/debrief/core/ContextOperations/CopyBearingsToClipboard.java#L92))
- **Trigger:** Select tracks with `RelativeTMASegment` data
- **Menu:** "Copy [tracks] to clipboard as offsets from [reference track]" / "Create new [tracks] by adding clipboard bearings to [track]"
- **Behaviour:** Extracts bearing/position offsets between tracks via the system clipboard. Paste creates new tracks by applying offsets to a reference track.

### 7.4 TMA from Sensors

[**GenerateTMASegmentFromCuts**](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.core/src/org/mwc/debrief/core/ContextOperations/GenerateTMASegmentFromCuts.java) ([class at L82](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.core/src/org/mwc/debrief/core/ContextOperations/GenerateTMASegmentFromCuts.java#L82))
- **Trigger:** Select sensor(s) or sensor contact(s)
- **Menu:** "Generate TMA solution from all cuts" / "Generate TMA solution from selected cuts"
- **Wizard:** Collects range, bearing offset, target course, speed, and optional colour override
- **Result:** Creates new `TrackWrapper` containing a `RelativeTMASegment`. Optionally shades the used sensor cuts.
- **Undo:** Supported

(See [Section 11](#11-manual-tma-and-tma-segments) for full TMA details.)

---

## 8. Bearing Residual Analysis (Track Shift)

The **Bearing Residuals View** (`BearingResidualsView`) provides the primary tool for evaluating TMA solution quality by comparing measured sensor bearings against predicted bearings.

### 8.1 Architecture

**Source:** [`org.mwc.debrief.track_shift/src/org/mwc/debrief/track_shift/views/`](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.track_shift/src/org/mwc/debrief/track_shift/views/)

| Class | Purpose | Source |
|-------|---------|--------|
| `BaseStackedDotsView` | Abstract base for all residual views; manages chart layout, track selection, and zone display | [L203](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.track_shift/src/org/mwc/debrief/track_shift/views/BaseStackedDotsView.java#L203) |
| `BearingResidualsView` | Concrete view showing bearing residual dots | [L87](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.track_shift/src/org/mwc/debrief/track_shift/views/BearingResidualsView.java#L87) |
| `FrequencyResidualsView` | Concrete view showing frequency residual dots | [L74](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.track_shift/src/org/mwc/debrief/track_shift/views/FrequencyResidualsView.java#L74) |
| `StackedDotHelper` | Utility class that creates `Doublet` pairings and calculates residual data series | [L97](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.track_shift/src/org/mwc/debrief/track_shift/views/StackedDotHelper.java#L97) |

### 8.2 Doublet Generation ([`StackedDotHelper.getDoublets()`](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.track_shift/src/org/mwc/debrief/track_shift/views/StackedDotHelper.java#L1251))

The algorithm pairs sensor contacts with target track fixes:

```
For each primary track (ownship):
  For each sensor on the track:
    For each visible sensor contact:
      Find the target segment that covers this contact's DTG
      Interpolate the target fix at the contact's DTG
      Interpolate the host fix at the contact's DTG
      Create Doublet(contact, targetFix, hostFix, targetSegment)
```

Key behaviours:
- Only processes **visible** sensors and contacts (when `onlyVis` is true)
- Filters by whether bearing or frequency data is needed
- Target track segments are obtained from the `ISecondaryTrack`
- Interpolation is used to get exact target and host positions at the contact's DTG

### 8.3 Bearing Residual Calculation

For each `Doublet`:
1. **Measured bearing** = `sensor.getBearing()` (raw observation)
2. **Calculated bearing** = geometric bearing from sensor origin to target position (via `getCalculatedBearing()`)
3. **Bearing error** = `measured - calculated`, wrapped to ±180°

The view plots these errors as dots on a time axis, with ownship course shown as a line for context. Residuals clustered near zero indicate a good TMA solution.

### 8.4 Ambiguous Bearing Handling

If a contact has an ambiguous bearing, a second residual is calculated using `getAmbiguousMeasuredBearing()`. Both residuals are plotted, allowing the analyst to visually determine which bearing is correct.

### 8.5 Zone Detection (Ownship Legs)

The `StackedDotHelper` includes **ownship leg detection** algorithms:
- [`PeakTrackingOwnshipLegDetector`](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.track_shift/src/org/mwc/debrief/track_shift/zig_detector/ownship/PeakTrackingOwnshipLegDetector.java#L30) — Detects straight-line legs in the ownship track
- `ArtificalLegDetector` — An alternative algorithm
- `AlternateLegWrapper` — Wraps detected legs for display

Detected legs are shown as coloured zones on the bearing residual chart, helping analysts identify periods of steady ownship course.

### 8.6 Data Series Produced

The helper produces `TimeSeriesCollection` datasets containing:
- **Bearing error** (measured - calculated) for each doublet
- **Ownship course** (degrees) vs time
- **Target course** from each TMA segment
- **Ambiguous bearing error** (where applicable)

---

## 9. Frequency Analysis

### 9.1 FrequencyResidualsView

**Source:** [`FrequencyResidualsView.java`](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.track_shift/src/org/mwc/debrief/track_shift/views/FrequencyResidualsView.java) ([class at L74](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.track_shift/src/org/mwc/debrief/track_shift/views/FrequencyResidualsView.java#L74))

Extends `BaseStackedDotsView` to display frequency residuals.

### 9.2 Doppler Shift Calculation Pipeline

For each `Doublet`:

1. **Measured frequency** = `sensor.getFrequency()` (raw received frequency)
2. **Base frequency** = `sensor.getSensor().getBaseFrequency()` (transmitted frequency of source)
3. **Corrected frequency** = Removes the **observer's** Doppler shift (via [`getCorrectedFrequency()`](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.legacy/src/Debrief/Wrappers/Track/Doublet.java#L286)):
   ```
   dopplerComponent = calcDopplerComponent(bearingRads, hostCourseRads, hostSpeedKts, measuredFreq)
   correctedFreq = measuredFreq + dopplerComponent
   ```
4. **Predicted frequency** = Adds the **target's** Doppler shift based on the TMA solution's course/speed (via [`getPredictedFrequency()`](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.legacy/src/Debrief/Wrappers/Track/Doublet.java#L337)):
   ```
   predictedFreq = calcPredictedFreqSI(baseFreq, targetCourse, targetSpeed, bearing, speedOfSound)
   ```
5. **Frequency error** = `corrected - predicted`

### 9.3 `FrequencyCalcs` Utility

**Source:** [`MWC.Algorithms.FrequencyCalcs`](https://github.com/debrief/debrief/blob/master/org.mwc.cmap.legacy/src/MWC/Algorithms/FrequencyCalcs.java) ([class at L20](https://github.com/debrief/debrief/blob/master/org.mwc.cmap.legacy/src/MWC/Algorithms/FrequencyCalcs.java#L20))

Provides:
- [`calcDopplerComponent()`](https://github.com/debrief/debrief/blob/master/org.mwc.cmap.legacy/src/MWC/Algorithms/FrequencyCalcs.java#L296): Doppler shift from relative motion along the bearing line
- [`calcPredictedFreqSI()`](https://github.com/debrief/debrief/blob/master/org.mwc.cmap.legacy/src/MWC/Algorithms/FrequencyCalcs.java#L323): Expected received frequency given base freq, relative geometry, and speed of sound

### 9.4 Multistatic Support

[`Doublet.getPredictedMultistaticFrequency()`](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.legacy/src/Debrief/Wrappers/Track/Doublet.java#L357) supports two-way propagation (source and receiver both in motion), used for active sonar scenarios.

---

## 10. TMA (Target Motion Analysis)

### 10.1 TMAContactWrapper (Individual TMA Solution)

**Source:** [`org.mwc.debrief.legacy/src/Debrief/Wrappers/TMAContactWrapper.java`](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.legacy/src/Debrief/Wrappers/TMAContactWrapper.java) (1,309 lines, [class at L128](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.legacy/src/Debrief/Wrappers/TMAContactWrapper.java#L128))

Represents a single TMA solution at a specific time.

#### Fields

| Field | Type | Description | Source |
|-------|------|-------------|--------|
| `_DTG` | `HiResDate` | Timestamp | [L335](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.legacy/src/Debrief/Wrappers/TMAContactWrapper.java#L335) |
| `_targetBrgRads` | `double` | Bearing to target (radians) | [L341](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.legacy/src/Debrief/Wrappers/TMAContactWrapper.java#L341) |
| `_targetRange` | `WorldDistance` | Range to target | [L347](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.legacy/src/Debrief/Wrappers/TMAContactWrapper.java#L347) |
| `_targetCourseDegs` | `double` | Target course estimate (degrees) | [L397](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.legacy/src/Debrief/Wrappers/TMAContactWrapper.java#L397) |
| `_targetSpeedKts` | `double` | Target speed estimate (knots) | [L402](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.legacy/src/Debrief/Wrappers/TMAContactWrapper.java#L402) |
| `_targetDepth` | `double` | Target depth estimate | [L352](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.legacy/src/Debrief/Wrappers/TMAContactWrapper.java#L352) |
| `_theEllipse` | `EllipseShape` | Uncertainty ellipse | [L382](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.legacy/src/Debrief/Wrappers/TMAContactWrapper.java#L382) |
| `_showLine` | `Boolean` | Whether to show bearing line from ownship to solution | [L367](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.legacy/src/Debrief/Wrappers/TMAContactWrapper.java#L367) |
| `_showSymbol` | `boolean` | Whether to show symbol at solution location | [L362](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.legacy/src/Debrief/Wrappers/TMAContactWrapper.java#L362) |
| `_showVector` | `boolean` | Whether to show target velocity vector | [L416](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.legacy/src/Debrief/Wrappers/TMAContactWrapper.java#L416) |
| `_originalLocation` | `WorldLocation` | Absolute location (for absolute TMA mode) | [L422](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.legacy/src/Debrief/Wrappers/TMAContactWrapper.java#L422) |

#### Rendering

[`TMAContactWrapper.paint()`](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.legacy/src/Debrief/Wrappers/TMAContactWrapper.java#L1019) draws:
1. **Bearing line** from ownship position to solution centre
2. **Uncertainty ellipse** at the solution position
3. **Symbol** at the solution position (optional)
4. **Velocity vector** showing target course and speed (optional)

#### Position Calculation

[`getCentre(WatchableList track)`](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.legacy/src/Debrief/Wrappers/TMAContactWrapper.java#L659) computes the TMA solution position:
- For **relative** TMA: host track position + offset (range/bearing)
- For **absolute** TMA: uses `_originalLocation` directly

### 10.2 TMAWrapper (TMA Solution Container)

**Source:** [`org.mwc.debrief.legacy/src/Debrief/Wrappers/TMAWrapper.java`](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.legacy/src/Debrief/Wrappers/TMAWrapper.java) (777 lines, [class at L96](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.legacy/src/Debrief/Wrappers/TMAWrapper.java#L96))

Container for a time-ordered collection of `TMAContactWrapper` objects. Extends `TacticalDataWrapper` (same base class as `SensorWrapper`).

Key properties:
- **Show Bearing Lines** ([`_showBearingLines`](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.legacy/src/Debrief/Wrappers/TMAWrapper.java#L441)): Toggle for all solutions
- **Show Labels** ([`_showLabels`](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.legacy/src/Debrief/Wrappers/TMAWrapper.java#L454)): Toggle for all labels
- [`getNearestTo(HiResDate)`](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.legacy/src/Debrief/Wrappers/TMAWrapper.java#L623): Finds solution(s) at/near a time

---

## 11. Manual TMA and TMA Segments

### 11.1 TMA Segment Type Hierarchy

```
TrackSegment (base track segment)
└── CoreTMASegment (abstract, adds constant course/speed)
    ├── AbsoluteTMASegment (fixed world-coordinate origin)
    └── RelativeTMASegment (offset from a reference track/sensor)
```

### 11.2 CoreTMASegment (Abstract Base)

**Source:** [`org.mwc.debrief.legacy/src/Debrief/Wrappers/Track/CoreTMASegment.java`](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.legacy/src/Debrief/Wrappers/Track/CoreTMASegment.java) (336 lines, [class at L43](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.legacy/src/Debrief/Wrappers/Track/CoreTMASegment.java#L43))

Represents a TMA solution as a constant-course, constant-speed target hypothesis. Unlike a regular `TrackSegment` (which stores individual fixes), a TMA segment **generates** fix positions by dead-reckoning from an origin at a constant course and speed.

#### Fields

| Field | Type | Description | Source |
|-------|------|-------------|--------|
| `_courseDegs` | `double` | Constant target course (degrees) | [L62](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.legacy/src/Debrief/Wrappers/Track/CoreTMASegment.java#L62) |
| `_speed` | `WorldSpeed` | Constant target speed | [L67](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.legacy/src/Debrief/Wrappers/Track/CoreTMASegment.java#L67) |
| `_dragMsg` | `String` | Real-time feedback during interactive drag (e.g., "[120° 12 kts]") | — |

#### Interactive Drag Operations

TMA segments support four drag modes, each of which is fundamental to the manual TMA workflow:

| Operation | Method | Effect | Drag Message |
|-----------|--------|--------|-------------|
| **Translate** | [`shift(WorldVector)`](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.legacy/src/Debrief/Wrappers/Track/CoreTMASegment.java#L288) | Moves the entire segment without changing course/speed | — |
| **Rotate** | [`rotate(double, WorldLocation)`](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.legacy/src/Debrief/Wrappers/Track/CoreTMASegment.java#L223) | Changes course by rotating about an endpoint | `"[newCourse°]"` |
| **Stretch** | [`stretch(double, WorldLocation)`](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.legacy/src/Debrief/Wrappers/Track/CoreTMASegment.java#L296) | Changes speed by stretching/compressing along the bearing | `"[newSpeed kts]"` |
| **Shear** | [`shear(WorldLocation, WorldLocation)`](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.legacy/src/Debrief/Wrappers/Track/CoreTMASegment.java#L285) | Changes both course and speed simultaneously | `"[speed kts newCourse°]"` |

These operations provide real-time visual feedback via the `_dragMsg` field, which is displayed near the cursor during dragging.

### 11.3 AbsoluteTMASegment

**Source:** [`org.mwc.debrief.legacy/src/Debrief/Wrappers/Track/AbsoluteTMASegment.java`](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.legacy/src/Debrief/Wrappers/Track/AbsoluteTMASegment.java) (457 lines, [class at L38](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.legacy/src/Debrief/Wrappers/Track/AbsoluteTMASegment.java#L38))

A TMA segment anchored at an **absolute geographic position**.

#### Fields

| Field | Type | Description | Source |
|-------|------|-------------|--------|
| `_origin` | `WorldLocation` | Fixed starting point (lat/lon) | [L91](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.legacy/src/Debrief/Wrappers/Track/AbsoluteTMASegment.java#L91) |
| `_startTime` | `HiResDate` | Start time | [L87](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.legacy/src/Debrief/Wrappers/Track/AbsoluteTMASegment.java#L87) |
| `_endTime` | `HiResDate` | End time | [L89](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.legacy/src/Debrief/Wrappers/Track/AbsoluteTMASegment.java#L89) |

#### Fix Generation

[`createFixAt(long theTime, long startTime)`](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.legacy/src/Debrief/Wrappers/Track/AbsoluteTMASegment.java#L210):
- Calculates elapsed time from origin
- Dead-reckons position: origin + (speed × time) along course
- Returns a `Fix` at the computed location

### 11.4 RelativeTMASegment

**Source:** [`org.mwc.debrief.legacy/src/Debrief/Wrappers/Track/RelativeTMASegment.java`](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.legacy/src/Debrief/Wrappers/Track/RelativeTMASegment.java) (>1,500 lines, [class at L60](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.legacy/src/Debrief/Wrappers/Track/RelativeTMASegment.java#L60))

A TMA segment defined **relative to a reference track** (the ownship). This is the most commonly used TMA segment type, as it maintains the spatial relationship between ownship and the target hypothesis.

#### Fields

| Field | Type | Description | Source |
|-------|------|-------------|--------|
| `_referenceTrackName` | `String` | Name of the ownship track | [L148](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.legacy/src/Debrief/Wrappers/Track/RelativeTMASegment.java#L148) |
| `_referenceSensorName` | `String` | Optional: sensor used for offset | [L155](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.legacy/src/Debrief/Wrappers/Track/RelativeTMASegment.java#L155) |
| `_offset` | `WorldVector` | Initial offset from ownship at start time | [L161](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.legacy/src/Debrief/Wrappers/Track/RelativeTMASegment.java#L161) |
| `_referenceTrack` | `TrackWrapper` | Runtime reference to ownship track | [L167](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.legacy/src/Debrief/Wrappers/Track/RelativeTMASegment.java#L167) |
| `_referenceSensor` | `SensorWrapper` | Optional: sensor for array offset | [L173](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.legacy/src/Debrief/Wrappers/Track/RelativeTMASegment.java#L173) |

#### Construction from Sensor Contacts

The most important constructor takes an array of `SensorContactWrapper` objects ([L252](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.legacy/src/Debrief/Wrappers/Track/RelativeTMASegment.java#L252)):
```java
RelativeTMASegment(SensorContactWrapper[] observations, WorldVector offset,
                   WorldSpeed speed, double course, Layers layers)
```

This:
1. Creates a fix at each sensor observation time
2. Calculates the target position relative to the ownship at that time
3. Sets constant course and speed for the segment

#### Relationship to Ownship

At any given time T:
```
targetPosition(T) = ownshipPosition(T) + offset + (speed × course × elapsedTime)
```

The offset is recalculated when the ownship track moves, ensuring the TMA solution stays relative.

### 11.5 Creating TMA from Sensor Cuts (Workflow)

**Source:** [`GenerateTMASegmentFromCuts.java`](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.core/src/org/mwc/debrief/core/ContextOperations/GenerateTMASegmentFromCuts.java) (1,029 lines, [class at L82](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.core/src/org/mwc/debrief/core/ContextOperations/GenerateTMASegmentFromCuts.java#L82))

This is the primary "Manual TMA" workflow:

1. **Select sensor cuts** (either individual contacts or entire sensor)
2. **Right-click** → "Generate TMA solution from cuts"
3. **Wizard** collects:
   - Initial range and bearing from ownship to target
   - Estimated target course and speed
   - Optional colour override
4. **Operation creates:**
   - A `RelativeTMASegment` from the sensor contacts
   - A new `TrackWrapper` named "TMA_[timestamp]"
   - Optionally colours the used sensor cuts to show their association
5. **Analyst refines** by dragging the TMA segment (rotate/shear/stretch) and observing the bearing residuals view

### 11.6 Other TMA Generation Methods

[**GenerateTMASegmentFromOwnshipPositions**](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.core/src/org/mwc/debrief/core/ContextOperations/GenerateTMASegmentFromOwnshipPositions.java) ([class at L61](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.core/src/org/mwc/debrief/core/ContextOperations/GenerateTMASegmentFromOwnshipPositions.java#L61))
- Creates `AbsoluteTMASegment` from selected ownship fixes + user offset
- Used when the analyst wants to place a TMA solution at a known position relative to ownship fixes

[**GenerateTMASegmentFromInfillSegment**](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.core/src/org/mwc/debrief/core/ContextOperations/GenerateTMASegmentFromInfillSegment.java) ([class at L56](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.core/src/org/mwc/debrief/core/ContextOperations/GenerateTMASegmentFromInfillSegment.java#L56))
- Replaces a `DynamicInfillSegment` (interpolated gap fill) with an explicit `AbsoluteTMASegment`
- Used to formalise an interpolated section as a TMA solution

[**ConvertAbsoluteTmaToRelative**](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.core/src/org/mwc/debrief/core/ContextOperations/ConvertAbsoluteTmaToRelative.java) ([class at L63](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.core/src/org/mwc/debrief/core/ContextOperations/ConvertAbsoluteTmaToRelative.java#L63))
- Converts an `AbsoluteTMASegment` to a `RelativeTMASegment`
- Calculates the offset from the sensor array centre at the start time

[**SelectCutsForThisTMASegment**](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.core/src/org/mwc/debrief/core/ContextOperations/SelectCutsForThisTMASegment.java#L48) / [**ShowCutsForThisTMASegment**](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.core/src/org/mwc/debrief/core/ContextOperations/ShowCutsForThisTMASegment.java#L53)
- Highlights/colours sensor cuts that correspond to a TMA solution's time period

### 11.7 Interactive TMA Drag

**Source:** [`DragSegment.java`](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.core/src/org/mwc/debrief/core/actions/DragSegment.java) (258 lines, [class at L55](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.core/src/org/mwc/debrief/core/actions/DragSegment.java#L55))

**Drag modes** (available when a TMA segment is selected in the plot):

| Mode | Cursor | Action | Changes | Source |
|------|--------|--------|---------|--------|
| **Translate** | Move cursor | `segment.shift(vector)` | Position only | [`CoreDragOperation`](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.core/src/org/mwc/debrief/core/actions/drag/CoreDragOperation.java#L50) |
| **Rotate** | Rotate cursor | `segment.rotate(angle, origin)` | Course | [`RotateDragMode`](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.core/src/org/mwc/debrief/core/actions/drag/RotateDragMode.java#L40) |
| **Stretch** | Stretch cursor | `segment.stretch(range, origin)` | Speed | [`StretchDragMode`](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.core/src/org/mwc/debrief/core/actions/drag/StretchDragMode.java#L30) |
| **Shear** (default) | Shear cursor | `segment.shear(cursor, origin)` | Course + Speed | [`ShearDragMode`](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.core/src/org/mwc/debrief/core/actions/drag/ShearDragMode.java#L30) |

During dragging:
- The segment's `getDragTextMessage()` returns a live text summary (e.g., "[12.5 kts 045°]")
- The properties view updates in real time
- The bearing residuals view updates to show the effect on solution quality

---

## 12. Array Offset and Towed Array Support

### 12.1 The Problem

Sensors (especially towed sonar arrays) are physically offset from the vessel's reference point. The sensor array centre may be hundreds of metres behind the vessel. For accurate bearing calculations, the **array centre** position must be used, not the vessel position.

### 12.2 ArrayOffsetHelper

**Source:** [`org.mwc.debrief.legacy/src/Debrief/Wrappers/Track/ArrayOffsetHelper.java`](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.legacy/src/Debrief/Wrappers/Track/ArrayOffsetHelper.java) ([class at L34](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.legacy/src/Debrief/Wrappers/Track/ArrayOffsetHelper.java#L34))

#### Array Centre Modes

| Mode | Enum/Class | Behaviour | Source |
|------|-----------|-----------|--------|
| **PLAIN** | `LegacyArrayOffsetModes.PLAIN` | Array centre at a fixed distance along the vessel's current heading (simple backtrack) | [L64](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.legacy/src/Debrief/Wrappers/Track/ArrayOffsetHelper.java#L64) |
| **WORM** | `LegacyArrayOffsetModes.WORM` | "Worm in hole": array centre follows the vessel's historical track. The centre is placed at the vessel's position from N metres of track distance ago. Accurately models a towed array that follows the vessel's path through the water. | [L71](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.legacy/src/Debrief/Wrappers/Track/ArrayOffsetHelper.java#L71) |
| **Measured** | `MeasuredDatasetArrayMode` | Uses actual measured position data (from a time-series dataset) for the array centre location. Supports absolute positions (lat/lon) or relative offsets (metres). | [L80](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.legacy/src/Debrief/Wrappers/Track/ArrayOffsetHelper.java#L80) |
| **Deferred** | `DeferredDatasetArrayMode` | Placeholder for datasets that haven't been loaded yet; resolved later. | [L46](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.legacy/src/Debrief/Wrappers/Track/ArrayOffsetHelper.java#L46) |

#### Calculation Flow

```java
// See: ArrayOffsetHelper.getArrayCentre() at L141
ArrayOffsetHelper.getArrayCentre(sensor, time, hostLocation, track):
  mode = sensor.getArrayCentreMode()
  if mode == LEGACY (PLAIN or WORM):
    offset = sensor.getSensorOffset()  // e.g., 500 metres
    isWorm = mode == WORM
    return track.getBacktraceTo(time, offset, isWorm)
  if mode == MEASURED:
    return sensor.getMeasuredLocationAt(measuredMode, time, hostLocation)
```

#### Impact

Every `SensorContactWrapper.getCalculatedOrigin()` call uses this system:
- If the contact has an absolute origin (`_absoluteOrigin != null`), that is used directly
- Otherwise, `ArrayOffsetHelper.getArrayCentre()` is called with the parent sensor's settings

When the sensor offset or array mode changes, `clearChildOffsets()` invalidates all contacts' cached origins, forcing recalculation on the next paint or analysis operation.

---

## 13. Key Source Files Reference

### Domain Model

| File | Lines | Description |
|------|-------|-------------|
| [`SensorContactWrapper.java`](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.legacy/src/Debrief/Wrappers/SensorContactWrapper.java) | 1,544 | Individual sensor observation |
| [`SensorWrapper.java`](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.legacy/src/Debrief/Wrappers/SensorWrapper.java) | 1,639 | Sensor container |
| [`TacticalDataWrapper.java`](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.legacy/src/Debrief/Wrappers/TacticalDataWrapper.java) | ~800 | Base class for sensor/TMA containers |
| [`TMAContactWrapper.java`](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.legacy/src/Debrief/Wrappers/TMAContactWrapper.java) | 1,309 | Individual TMA solution |
| [`TMAWrapper.java`](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.legacy/src/Debrief/Wrappers/TMAWrapper.java) | 777 | TMA solution container |
| [`Doublet.java`](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.legacy/src/Debrief/Wrappers/Track/Doublet.java) | ~400 | Observation-hypothesis pairing |
| [`CoreTMASegment.java`](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.legacy/src/Debrief/Wrappers/Track/CoreTMASegment.java) | 336 | Abstract TMA segment |
| [`AbsoluteTMASegment.java`](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.legacy/src/Debrief/Wrappers/Track/AbsoluteTMASegment.java) | 457 | Absolute TMA segment |
| [`RelativeTMASegment.java`](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.legacy/src/Debrief/Wrappers/Track/RelativeTMASegment.java) | >1,500 | Relative TMA segment |
| [`ArrayOffsetHelper.java`](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.legacy/src/Debrief/Wrappers/Track/ArrayOffsetHelper.java) | ~300 | Array centre calculations |

### Import / Export

| File | Description |
|------|-------------|
| [`ImportSensor.java`](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.legacy/src/Debrief/ReaderWriter/Replay/ImportSensor.java) | REP v1 import |
| [`ImportSensor2.java`](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.legacy/src/Debrief/ReaderWriter/Replay/ImportSensor2.java) | REP v2 import (+ ambig bearing, freq) |
| [`ImportSensor3.java`](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.legacy/src/Debrief/ReaderWriter/Replay/ImportSensor3.java) | REP v3 import (+ accuracy fields) |
| [`ImportSensorArc.java`](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.legacy/src/Debrief/ReaderWriter/Replay/ImportSensorArc.java) | Sensor arc import |
| [`SensorHandler.java`](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.legacy/src/Debrief/ReaderWriter/XML/Tactical/SensorHandler.java) | XML sensor import/export |
| [`SensorContactHandler.java`](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.legacy/src/Debrief/ReaderWriter/XML/Tactical/SensorContactHandler.java) | XML contact import/export |
| [`FlatFileExporter.java`](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.legacy/src/Debrief/ReaderWriter/FlatFile/FlatFileExporter.java) | SAM flat file export |
| [`DopplerShiftExporter.java`](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.legacy/src/Debrief/ReaderWriter/FlatFile/DopplerShift/DopplerShiftExporter.java) | Doppler export |
| [`ImportReplay.java`](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.legacy/src/Debrief/ReaderWriter/Replay/ImportReplay.java) | Main replay importer (sensor registration at [L871](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.legacy/src/Debrief/ReaderWriter/Replay/ImportReplay.java#L871)) |

### Analysis

| File | Description |
|------|-------------|
| [`BaseStackedDotsView.java`](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.track_shift/src/org/mwc/debrief/track_shift/views/BaseStackedDotsView.java) | Abstract residual view |
| [`BearingResidualsView.java`](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.track_shift/src/org/mwc/debrief/track_shift/views/BearingResidualsView.java) | Bearing residual stacked dots |
| [`FrequencyResidualsView.java`](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.track_shift/src/org/mwc/debrief/track_shift/views/FrequencyResidualsView.java) | Frequency residual stacked dots |
| [`StackedDotHelper.java`](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.track_shift/src/org/mwc/debrief/track_shift/views/StackedDotHelper.java) | Doublet generation and data series |
| [`FrequencyCalcs.java`](https://github.com/debrief/debrief/blob/master/org.mwc.cmap.legacy/src/MWC/Algorithms/FrequencyCalcs.java) | Doppler shift calculations |

### Context Operations

| File | Description |
|------|-------------|
| [`GenerateNewSensor.java`](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.core/src/org/mwc/debrief/core/ContextOperations/GenerateNewSensor.java) | Create sensor |
| [`GenerateNewSensorContact.java`](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.core/src/org/mwc/debrief/core/ContextOperations/GenerateNewSensorContact.java) | Create contact |
| [`MergeContacts.java`](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.core/src/org/mwc/debrief/core/ContextOperations/MergeContacts.java) | Merge sensors |
| [`RainbowShadeSonarCuts.java`](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.core/src/org/mwc/debrief/core/ContextOperations/RainbowShadeSonarCuts.java) | Colour-shade contacts |
| [`GenerateSensorRangePlot.java`](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.core/src/org/mwc/debrief/core/ContextOperations/GenerateSensorRangePlot.java) | Range plot |
| [`GenerateTMASegmentFromCuts.java`](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.core/src/org/mwc/debrief/core/ContextOperations/GenerateTMASegmentFromCuts.java) | TMA from cuts |
| [`GenerateTMASegmentFromOwnshipPositions.java`](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.core/src/org/mwc/debrief/core/ContextOperations/GenerateTMASegmentFromOwnshipPositions.java) | TMA from fixes |
| [`ConvertAbsoluteTmaToRelative.java`](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.core/src/org/mwc/debrief/core/ContextOperations/ConvertAbsoluteTmaToRelative.java) | Convert TMA type |
| [`CopyBearingsToClipboard.java`](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.core/src/org/mwc/debrief/core/ContextOperations/CopyBearingsToClipboard.java) | Copy/paste bearings |
| [`SelectCutsForThisTMASegment.java`](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.core/src/org/mwc/debrief/core/ContextOperations/SelectCutsForThisTMASegment.java) | Select cuts for TMA |
| [`ShowCutsForThisTMASegment.java`](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.core/src/org/mwc/debrief/core/ContextOperations/ShowCutsForThisTMASegment.java) | Highlight cuts for TMA |
| [`GenerateNewInsertSensorArcAction.java`](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.core/src/org/mwc/debrief/core/ContextOperations/GenerateNewInsertSensorArcAction.java) | Insert sensor arc |

### Drag Operations

| File | Description |
|------|-------------|
| [`DragSegment.java`](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.core/src/org/mwc/debrief/core/actions/DragSegment.java) | TMA drag entry point |
| [`CoreDragOperation.java`](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.core/src/org/mwc/debrief/core/actions/drag/CoreDragOperation.java) | Drag base class |
| [`ShearDragMode.java`](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.core/src/org/mwc/debrief/core/actions/drag/ShearDragMode.java) | Shear drag (course + speed) |
| [`RotateDragMode.java`](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.core/src/org/mwc/debrief/core/actions/drag/RotateDragMode.java) | Rotate drag (course) |
| [`StretchDragMode.java`](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.core/src/org/mwc/debrief/core/actions/drag/StretchDragMode.java) | Stretch drag (speed) |

### Rendering

| File | Description |
|------|-------------|
| [`SnailDrawTacticalContact.java`](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.legacy/src/Debrief/GUI/Tote/Painters/SnailDrawTacticalContact.java) | Snail mode sensor rendering |
| [`SnailDrawTMAContact.java`](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.legacy/src/Debrief/GUI/Tote/Painters/SnailDrawTMAContact.java) | Snail mode TMA rendering |
| [`SnailDrawSWTSensorContact.java`](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.core/src/org/mwc/debrief/core/editors/painters/snail/SnailDrawSWTSensorContact.java) | SWT sensor rendering |
| [`DynamicTrackCoverageWrapper.java`](https://github.com/debrief/debrief/blob/master/org.mwc.debrief.legacy/src/Debrief/Wrappers/DynamicTrackShapes/DynamicTrackCoverageWrapper.java) | Dynamic sensor coverage shape |
