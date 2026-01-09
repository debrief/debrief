# LLM Documentation Plan for Legacy Debrief

## Purpose

Make the legacy Debrief codebase navigable by LLMs (e.g., Claude Code) for three purposes:

1. **Emergency hotfixes** — Enable an LLM to understand, locate, and safely modify code during the transition period before DebriefNG delivery
2. **Knowledge extraction** — Support algorithm porting, golden test data generation, and domain knowledge capture for the modernisation effort
3. **Knowledge preservation** — Capture lessons learned, design rationale, and institutional knowledge from 25 years of development to inform Future Debrief architecture decisions

## Content Focus

| Focus Area | Priority | Notes |
|------------|----------|-------|
| Logic and algorithms | Primary | Core computational behaviour |
| Spatial rendering | Detailed | Map plot must be reproduced faithfully in future Debrief |
| Data structures | High | Domain model, file formats |
| UI elements | Skip | Future Debrief has entirely new UI |

## Root-Level Documentation

| Document | Purpose | Status |
|----------|---------|--------|
| `CLAUDE.md` | Entry point — directs LLM to companion docs and key directories | Complete |
| `DOMAIN_GLOSSARY.md` | Maritime analysis concepts: TMA, fixes, bearings, tracks, sensor contacts, contributions, legs | Complete |
| `ARCHITECTURE.md` | Module dependency map, data flow, plugin responsibilities | Complete |
| `KEY_CLASSES.md` | The 20-30 critical classes with roles and relationships | Complete |

## Distributed README.md Coverage

### Core Plugins

| Plugin | Purpose | Status |
|--------|---------|--------|
| `org.mwc.debrief.core` | Main Debrief plugin, Eclipse integration, context operations | Complete |
| `org.mwc.cmap.legacy` | Core data types (WorldLocation, WorldSpeed, etc.), shapes, layers, canvas | Complete |
| `org.mwc.debrief.legacy` | Track wrappers, file readers/writers, domain logic | Complete |
| `org.mwc.cmap.plotViewer` | Plot/chart rendering — **spatial rendering focus** | Complete |
| `org.mwc.debrief.track_shift` | TMA and bearing analysis tools — **algorithm focus** | Complete |

### Key Sub-Packages

| Package | Parent Plugin | Purpose | Status |
|---------|---------------|---------|--------|
| `Debrief.Wrappers` | debrief.legacy | Track, Fix, Sensor wrappers — core domain model | Complete |
| `MWC.GenericData` | cmap.legacy | WorldLocation, WorldSpeed, WorldDistance, WorldVector | Complete |
| `Debrief.ReaderWriter` | debrief.legacy | File format importers/exporters (REP, DPF, etc.) | Complete |
| `MWC.GUI.Shapes` | cmap.legacy | Drawable shapes for plot — **spatial rendering** | Complete |
| `MWC.GUI.Canvas` | cmap.legacy | Canvas abstraction, projection — **spatial rendering** | Complete |
| `MWC.Algorithms` | cmap.legacy | Geodetic calculations, Rhumb-line, etc. — **algorithm focus** | Complete |

### Secondary Modules

| Plugin | Purpose | Status |
|--------|---------|--------|
| `org.mwc.asset.core` | ASSET simulation framework core | Complete |
| `org.mwc.asset.legacy` | ASSET domain model and algorithms | Complete |
| `org.mwc.debrief.satc.core` | Semi-Automated Track Construction — **algorithm focus** | Complete |
| `org.mwc.cmap.NaturalEarth` | Natural Earth map data | Complete |
| `org.mwc.cmap.gt2Plot` | GeoTools integration | Complete |

## Methodology

For each documentation item:

1. **Skeleton** — Draft initial structure from existing CLAUDE.md and high-level code exploration
2. **Flesh out** — Inspect codebase to fill in details: key classes, methods, data flow
3. **Interview** — Where confidence is low, ask Doc targeted questions about intent, edge cases, history
4. **Review** — Doc reviews and corrects draft
5. **Validate** — (Optional) Test with fresh LLM session on synthetic hotfix task

## Spatial Rendering Section Structure

Each relevant README should include a clearly marked section:

```markdown
## Spatial Rendering

How items are drawn on the map plot. This section is detailed because 
future Debrief must reproduce this rendering faithfully.

### Coordinate System
### Projection
### Draw Order / Z-Index
### Shape Rendering
### Track Rendering
### Label Placement
```

## Algorithm Section Structure

Each relevant README should include:

```markdown
## Algorithms

### [Algorithm Name]
- **Purpose**: What problem it solves
- **Inputs**: Data types and sources
- **Outputs**: Results and side effects
- **Key Classes**: Entry points and core logic
- **Edge Cases**: Known special handling
- **Performance Notes**: Constraints, optimisation history

#### Pseudocode (for complex algorithms)
```text
// Simplified logic for porting reference
1. Initialize...
2. For each item...
3. Calculate...
```
```

## Progress Tracking

### Phase 1: Root Documentation
- [X] Expand CLAUDE.md with navigation guidance
- [X] Draft DOMAIN_GLOSSARY.md
- [X] Draft ARCHITECTURE.md
- [X] Draft KEY_CLASSES.md

### Phase 2: Core Plugin READMEs
- [X] org.mwc.debrief.core/README.md
- [X] org.mwc.cmap.legacy/README.md
- [X] org.mwc.debrief.legacy/README.md
- [X] org.mwc.cmap.plotViewer/README.md
- [X] org.mwc.debrief.track_shift/README.md

### Phase 3: Key Sub-Package READMEs
- [X] Debrief.Wrappers
- [X] MWC.GenericData
- [X] Debrief.ReaderWriter
- [X] MWC.GUI.Shapes
- [X] MWC.GUI.Canvas
- [X] MWC.Algorithms

### Phase 4: Secondary Module READMEs
- [X] org.mwc.asset.core
- [X] org.mwc.asset.legacy
- [X] org.mwc.debrief.satc.core
- [X] org.mwc.cmap.NaturalEarth
- [X] org.mwc.cmap.gt2Plot

## Interview Log

Track questions asked and answers received during documentation:

| Date | Topic | Question | Answer |
|------|-------|----------|--------|
| | | | |

## Clarifications

### Session 2026-01-09

- Q: What visualization format for architecture documentation? → A: Mermaid for complex diagrams, ASCII for simple trees
- Q: Which Mermaid diagram types for ARCHITECTURE.md? → A: Flowchart + Class + Sequence diagrams (full coverage)
- Q: How detailed should algorithm documentation be? → A: Prose + pseudocode for complex algorithms
- Q: Where should Mermaid diagrams appear? → A: All levels (root, plugin, and package READMEs)
- Q: README completion criteria? → A: Complete, thorough content—all template sections filled with quality content; goal is preserving legacy knowledge and lessons learned for Future Debrief, not minimum viable

## Notes

- Legacy codebase uses Eclipse RCP but we're not documenting RCP patterns (not needed for hotfixes or algorithm extraction)

## Documentation Changelog

**Completed: 2026-01-09**

### Summary

Created comprehensive LLM-navigable documentation for Legacy Debrief codebase. 154/159 tasks complete (97%). Remaining 5 tasks require fresh LLM session validation.

### Documents Created

**ROOT Level (4 docs)**:
- `CLAUDE.md` - Expanded entry point with quick navigation, common LLM queries
- `DOMAIN_GLOSSARY.md` - 60+ maritime/TMA terms with definitions
- `ARCHITECTURE.md` - Module dependencies, data flow, 8 Mermaid diagrams
- `KEY_CLASSES.md` - 25 critical classes with roles and relationships

**Core Plugin READMEs (5 docs)**:
- `org.mwc.cmap.legacy/README.md` - 1,200+ classes, algorithms, data types
- `org.mwc.debrief.legacy/README.md` - Domain model, file I/O
- `org.mwc.cmap.plotViewer/README.md` - Spatial rendering pipeline
- `org.mwc.debrief.track_shift/README.md` - TMA algorithms with pseudocode
- `org.mwc.debrief.core/README.md` - Eclipse integration, context operations

**Package READMEs (6 docs)**:
- `MWC.GenericData/README.md` - WorldLocation, WorldDistance, WorldSpeed, HiResDate
- `MWC.Algorithms/README.md` - Geodetic calculations, projections, Doppler
- `MWC.GUI.Canvas/README.md` - Canvas abstraction, double-buffering
- `MWC.GUI.Shapes/README.md` - Shape hierarchy, hit testing
- `Debrief.Wrappers/README.md` - TrackWrapper, FixWrapper, SensorWrapper
- `Debrief.ReaderWriter/README.md` - REP format, 40+ import handlers

**Secondary Module READMEs (5 docs)**:
- `org.mwc.asset.core/README.md` - ASSET Eclipse integration
- `org.mwc.asset.legacy/README.md` - Simulation engine, 416 classes
- `org.mwc.debrief.satc.core/README.md` - Genetic algorithm for TMA
- `org.mwc.cmap.gt2Plot/README.md` - GeoTools 21.2 bridge
- `org.mwc.cmap.NaturalEarth/README.md` - Cartographic data integration

### Statistics

| Metric | Count |
|--------|-------|
| README files created | 20 |
| Mermaid diagrams | 68 |
| VERIFIED confidence markers | 220 |
| Internal cross-links verified | 50+ |
| Algorithm pseudocode sections | 15+ |

### Knowledge Captured

- Two-layer architecture (Eclipse RCP vs Legacy)
- Wrapper pattern for domain model separation
- REP file format with 40+ line type handlers
- TMA bearing residual minimization algorithms
- Flat-earth vs Mercator projection implementations
- Genetic algorithm for SATC (crossover, mutation, elite selection)
- GeoTools coordinate transformation pipeline
- Canvas rendering with double-buffering

### Outstanding

T151-T155: Fresh LLM session validation tasks (require new conversation)
