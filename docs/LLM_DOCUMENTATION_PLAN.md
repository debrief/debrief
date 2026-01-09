# LLM Documentation Plan for Legacy Debrief

## Purpose

Make the legacy Debrief codebase navigable by LLMs (e.g., Claude Code) for two purposes:

1. **Emergency hotfixes** — Enable an LLM to understand, locate, and safely modify code during the transition period before DebriefNG delivery
2. **Knowledge extraction** — Support algorithm porting, golden test data generation, and domain knowledge capture for the modernisation effort

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
| `CLAUDE.md` | Entry point — directs LLM to companion docs and key directories | Exists (expand) |
| `DOMAIN_GLOSSARY.md` | Maritime analysis concepts: TMA, fixes, bearings, tracks, sensor contacts, contributions, legs | Not started |
| `ARCHITECTURE.md` | Module dependency map, data flow, plugin responsibilities | Not started |
| `KEY_CLASSES.md` | The 20-30 critical classes with roles and relationships | Not started |

## Distributed README.md Coverage

### Core Plugins

| Plugin | Purpose | Status |
|--------|---------|--------|
| `org.mwc.debrief.core` | Main Debrief plugin, Eclipse integration, context operations | Not started |
| `org.mwc.cmap.legacy` | Core data types (WorldLocation, WorldSpeed, etc.), shapes, layers, canvas | Not started |
| `org.mwc.debrief.legacy` | Track wrappers, file readers/writers, domain logic | Not started |
| `org.mwc.cmap.plotViewer` | Plot/chart rendering — **spatial rendering focus** | Not started |
| `org.mwc.debrief.track_shift` | TMA and bearing analysis tools — **algorithm focus** | Not started |

### Key Sub-Packages

| Package | Parent Plugin | Purpose | Status |
|---------|---------------|---------|--------|
| `Debrief.Wrappers` | debrief.legacy | Track, Fix, Sensor wrappers — core domain model | Not started |
| `MWC.GenericData` | cmap.legacy | WorldLocation, WorldSpeed, WorldDistance, WorldVector | Not started |
| `Debrief.ReaderWriter` | debrief.legacy | File format importers/exporters (REP, DPF, etc.) | Not started |
| `MWC.GUI.Shapes` | cmap.legacy | Drawable shapes for plot — **spatial rendering** | Not started |
| `MWC.GUI.Canvas` | cmap.legacy | Canvas abstraction, projection — **spatial rendering** | Not started |
| `MWC.Algorithms` | cmap.legacy | Geodetic calculations, Rhumb-line, etc. — **algorithm focus** | Not started |

### Secondary Modules

| Plugin | Purpose | Status |
|--------|---------|--------|
| `org.mwc.asset.core` | ASSET simulation framework core | Not started |
| `org.mwc.asset.legacy` | ASSET domain model and algorithms | Not started |
| `org.mwc.debrief.satc.core` | Semi-Automated Track Construction — **algorithm focus** | Not started |
| `org.mwc.cmap.naturalearth` | Natural Earth map data | Not started |
| `org.mwc.cmap.gt2Plot` | GeoTools integration | Not started |

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
```

## Progress Tracking

### Phase 1: Root Documentation
- [ ] Expand CLAUDE.md with navigation guidance
- [ ] Draft DOMAIN_GLOSSARY.md
- [ ] Draft ARCHITECTURE.md
- [ ] Draft KEY_CLASSES.md

### Phase 2: Core Plugin READMEs
- [ ] org.mwc.debrief.core/README.md
- [ ] org.mwc.cmap.legacy/README.md
- [ ] org.mwc.debrief.legacy/README.md
- [ ] org.mwc.cmap.plotViewer/README.md
- [ ] org.mwc.debrief.track_shift/README.md

### Phase 3: Key Sub-Package READMEs
- [ ] Debrief.Wrappers
- [ ] MWC.GenericData
- [ ] Debrief.ReaderWriter
- [ ] MWC.GUI.Shapes
- [ ] MWC.GUI.Canvas
- [ ] MWC.Algorithms

### Phase 4: Secondary Module READMEs
- [ ] org.mwc.asset.core
- [ ] org.mwc.asset.legacy
- [ ] org.mwc.debrief.satc.core
- [ ] org.mwc.cmap.naturalearth
- [ ] org.mwc.cmap.gt2Plot

## Interview Log

Track questions asked and answers received during documentation:

| Date | Topic | Question | Answer |
|------|-------|----------|--------|
| | | | |

## Notes

- Legacy codebase uses Eclipse RCP but we're not documenting RCP patterns (not needed for hotfixes or algorithm extraction)
- Future Debrief replaces UI entirely, so UI documentation is out of scope
- SATC (Semi-Automated Track Construction) contains the genetic algorithm implementation — high priority for algorithm documentation
