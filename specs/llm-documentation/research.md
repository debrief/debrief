# Research: LLM Documentation for Legacy Debrief

**Date**: 2026-01-09
**Confidence**: VERIFIED (code inspection)

## Codebase Structure Findings

### Plugin/Module Layout

| Plugin | Purpose | Documentation Priority |
|--------|---------|----------------------|
| `org.mwc.cmap.legacy` | Core data types, algorithms, canvas, shapes | HIGH - algorithms + rendering |
| `org.mwc.debrief.legacy` | Wrappers, file readers, domain logic | HIGH - domain model |
| `org.mwc.debrief.core` | Eclipse integration, context operations | MEDIUM - entry points |
| `org.mwc.cmap.plotViewer` | Plot/chart rendering | HIGH - spatial rendering |
| `org.mwc.debrief.track_shift` | TMA and bearing analysis | HIGH - algorithms |
| `org.mwc.asset.core` | ASSET simulation core | LOW |
| `org.mwc.debrief.satc.core` | Semi-Automated Track Construction | HIGH - genetic algorithm |

**Decision**: Focus on org.mwc.cmap.legacy, org.mwc.debrief.legacy, and algorithm-heavy modules first.

---

### Core Package Structure

#### MWC.* Packages (org.mwc.cmap.legacy/src/MWC/)

| Package | Purpose | Priority |
|---------|---------|----------|
| `MWC.GenericData` | WorldLocation, WorldSpeed, WorldDistance, WorldVector, HiResDate | HIGH |
| `MWC.Algorithms` | Geodetic calculations, EarthModel, Conversions | HIGH |
| `MWC.Algorithms.Projections` | FlatProjection, Mercator2, Mercator3 | HIGH |
| `MWC.GUI.Canvas` | CanvasAdaptor, SwingCanvas, MetafileCanvas | HIGH |
| `MWC.GUI.Shapes` | PlainShape, CircleShape, VectorShape, TextLabel | HIGH |
| `MWC.GUI.Layers` | Layer container, BaseLayer | MEDIUM |
| `MWC.Utilities.ReaderWriter` | File I/O utilities | MEDIUM |

#### Debrief.* Packages (org.mwc.debrief.legacy/src/Debrief/)

| Package | Purpose | Priority |
|---------|---------|----------|
| `Debrief.Wrappers` | TrackWrapper, FixWrapper, SensorWrapper | HIGH |
| `Debrief.Wrappers.Track` | TrackWrapper_Support, LightweightTrackWrapper | HIGH |
| `Debrief.ReaderWriter.Replay` | REP format import/export | HIGH |
| `Debrief.ReaderWriter.XML` | DPF/XML format handler | HIGH |
| `Debrief.Tools.Operations` | Track analysis operations | MEDIUM |

---

### Key Class Inventory

#### Wrapper Classes (Domain Model)

| Class | Location | Size | Purpose |
|-------|----------|------|---------|
| `TrackWrapper` | Debrief/Wrappers/TrackWrapper.java | 120KB | Main track container |
| `FixWrapper` | Debrief/Wrappers/FixWrapper.java | 56KB | Position fix |
| `SensorWrapper` | Debrief/Wrappers/SensorWrapper.java | 60KB | Sensor data container |
| `SensorContactWrapper` | Debrief/Wrappers/SensorContactWrapper.java | 43KB | Individual sensor contact |
| `TMAWrapper` | Debrief/Wrappers/TMAWrapper.java | 28KB | TMA solution |
| `CompositeTrackWrapper` | Debrief/Wrappers/CompositeTrackWrapper.java | - | Multi-leg tracks |

#### Algorithm Classes

| Class | Location | Purpose |
|-------|----------|---------|
| `Conversions` | MWC/Algorithms/Conversions.java | Unit conversions |
| `EarthModel` | MWC/Algorithms/EarthModel.java | Earth model interface |
| `PlainProjection` | MWC/Algorithms/PlainProjection.java | Base projection |
| `FlatProjection` | MWC/Algorithms/Projections/FlatProjection.java | Flat earth projection |
| `Mercator2/3` | MWC/Algorithms/Projections/ | Mercator implementations |
| `FrequencyCalcs` | MWC/Algorithms/FrequencyCalcs.java | Frequency calculations |

#### Canvas/Rendering Classes

| Class | Location | Purpose |
|-------|----------|---------|
| `CanvasAdaptor` | MWC/GUI/Canvas/CanvasAdaptor.java | Abstract canvas interface |
| `SwingCanvas` | MWC/GUI/Canvas/SwingCanvas.java | Swing rendering |
| `MetafileCanvas` | MWC/GUI/Canvas/MetafileCanvas.java | Metafile export |
| `PlainShape` | MWC/GUI/Shapes/PlainShape.java | Base shape interface |

---

### File Format Handlers

| Format | Location | Import Class | Export Class |
|--------|----------|--------------|--------------|
| REP (Replay) | Debrief/ReaderWriter/Replay/ | ImportReplay.java | FormatTracks.java |
| DPF/XML | Debrief/ReaderWriter/XML/ | DebriefXMLReaderWriter.java | (same) |
| NMEA | Debrief/ReaderWriter/NMEA/ | ImportNMEA.java | - |
| AIS | Debrief/ReaderWriter/AIS/ | ImportAIS.java | - |

---

### Context Operations (Analysis Entry Points)

Location: `org.mwc.debrief.core/src/org/mwc/debrief/core/ContextOperations/`

| Operation | File | Purpose |
|-----------|------|---------|
| TMA Generation | GenerateTMASegmentFromCuts.java | Generate TMA from bearing cuts |
| TUA Solver | GenerateTUASolution.java | Target motion analysis |
| Infill | GenerateInfillSegment.java | Dead reckoning infill |
| Interpolation | InterpolateTrack.java | Position interpolation |
| Track Split | SplitTracksIntoLegs.java | Leg division |
| Track Merge | MergeTracks.java | Data fusion |

---

## Architecture Insights

### Two-Layer Architecture

```
┌─────────────────────────────────────────────────────────┐
│  Eclipse RCP Layer (org.mwc.debrief.core, etc.)         │
│  - Views, editors, context operations                   │
│  - Extension points, OSGi bundles                       │
└───────────────────────┬─────────────────────────────────┘
                        │ calls
┌───────────────────────▼─────────────────────────────────┐
│  Legacy Layer (MWC.*, Debrief.*)                        │
│  - Pure Java, no Eclipse dependencies                   │
│  - Domain model, algorithms, file I/O                   │
└─────────────────────────────────────────────────────────┘
```

**Decision**: Focus documentation on Legacy Layer. Eclipse RCP layer is OUT OF SCOPE per constitution.

### Wrapper Pattern

All domain objects implement wrapper pattern for Eclipse property sheet integration:
- Base data in `MWC.GenericData` (WorldLocation, etc.)
- Wrapped for UI in `Debrief.Wrappers` (TrackWrapper, FixWrapper, etc.)

**Decision**: Document both layers—data types and their wrappers.

### Projection-Aware Design

All spatial calculations use pluggable `PlainProjection`:
- `FlatProjection` - local area approximation
- `Mercator2/3` - global Mercator
- `JMapTransformMercator` - GeoTools integration

**Decision**: Document projection model as critical for spatial rendering fidelity.

### Custom Geodetic Math

No external spatial libraries—all geodetic calculations are in-house:
- `MWC.Algorithms.Conversions` - unit conversions
- `MWC.Algorithms.EarthModels` - earth model implementations
- Custom great-circle and rhumb-line calculations

**Decision**: Document algorithms in detail for faithful porting.

---

## Documentation Strategy Decisions

| Decision | Rationale | Alternatives Considered |
|----------|-----------|------------------------|
| Start with root docs (CLAUDE.md, DOMAIN_GLOSSARY.md) | Entry points before internals | Start with detailed package docs |
| Focus on MWC.Algorithms first | Logic/algorithms primary per constitution | Start with Wrappers |
| Document projection model in detail | Critical for spatial fidelity | Assume standard Mercator |
| Use VERIFIED/INFERRED/UNCERTAIN markers | Constitution requirement | Plain prose |
| Skip Eclipse RCP patterns | Out of scope per constitution | Document extension points |

---

## Open Questions for Interview

| Topic | Question | Confidence Without Answer |
|-------|----------|--------------------------|
| TMA Algorithm | Is the TMA genetic algorithm in satc.core the primary solver, or are there others? | INFERRED |
| Projection Default | What is the default projection when loading a new plot? | UNCERTAIN |
| REP Format | Are there undocumented REP extensions used internally? | UNCERTAIN |
| Rendering Order | Is Z-order deterministic or layer-order dependent? | INFERRED |
