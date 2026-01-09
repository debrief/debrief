# Tasks: LLM Documentation for Legacy Debrief

**Input**: Design documents from `/specs/llm-documentation/`
**Prerequisites**: plan.md (required), research.md, data-model.md, quickstart.md

**Tests**: No automated tests. Validation via fresh LLM sessions on synthetic hotfix tasks.

**Organization**: Tasks organized by documentation phase (Root → Core Plugins → Packages → Secondary).

## Format: `[ID] [P?] [Phase] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Phase]**: Which documentation phase (ROOT, CORE, PKG, SEC)
- Include exact file paths in descriptions

---

## Phase 1: Setup

**Purpose**: Establish documentation infrastructure and templates

- [ ] T001 Create documentation templates directory at docs/templates/
- [ ] T002 [P] Create plugin README template at docs/templates/plugin-readme-template.md
- [ ] T003 [P] Create package README template at docs/templates/package-readme-template.md
- [ ] T004 [P] Create algorithm section template at docs/templates/algorithm-section-template.md
- [ ] T005 [P] Create spatial rendering section template at docs/templates/spatial-rendering-template.md

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core reference documents that all other docs depend on

**CRITICAL**: ROOT docs must be complete before plugin/package READMEs can reference them

- [ ] T006 [ROOT] Expand CLAUDE.md with navigation guidance to companion docs at CLAUDE.md
- [ ] T007 [ROOT] Draft DOMAIN_GLOSSARY.md with maritime analysis terminology at DOMAIN_GLOSSARY.md
- [ ] T008 [ROOT] Draft ARCHITECTURE.md with module dependency map at ARCHITECTURE.md
- [ ] T009 [ROOT] Draft KEY_CLASSES.md with 20-30 critical classes at KEY_CLASSES.md

**Checkpoint**: Root documentation complete—plugin READMEs can now reference these

---

## Phase 3: Core Plugin READMEs (Priority: P1)

**Goal**: Document the 5 core plugins enabling LLM navigation to primary codebase areas

**Independent Test**: LLM can locate track handling, rendering, or algorithm code within 2 queries

### org.mwc.cmap.legacy (HIGH - algorithms + rendering)

- [ ] T010 [P] [CORE] Inspect org.mwc.cmap.legacy/src/MWC/ package structure
- [ ] T011 [CORE] Draft org.mwc.cmap.legacy/README.md with Purpose, Entry Points, Key Packages
- [ ] T012 [CORE] Add Algorithms section to org.mwc.cmap.legacy/README.md (geodetic calculations)
- [ ] T013 [CORE] Add Spatial Rendering section to org.mwc.cmap.legacy/README.md (canvas, shapes)
- [ ] T014 [CORE] Mark confidence levels and log UNCERTAIN items for interview

### org.mwc.debrief.legacy (HIGH - domain model)

- [ ] T015 [P] [CORE] Inspect org.mwc.debrief.legacy/src/Debrief/ package structure
- [ ] T016 [CORE] Draft org.mwc.debrief.legacy/README.md with Purpose, Entry Points, Key Packages
- [ ] T017 [CORE] Document wrapper pattern (TrackWrapper, FixWrapper, SensorWrapper)
- [ ] T018 [CORE] Document file format handlers (REP, DPF/XML)
- [ ] T019 [CORE] Mark confidence levels and log UNCERTAIN items for interview

### org.mwc.debrief.core (MEDIUM - entry points)

- [ ] T020 [P] [CORE] Inspect org.mwc.debrief.core/src/ context operations
- [ ] T021 [CORE] Draft org.mwc.debrief.core/README.md with Purpose, Entry Points
- [ ] T022 [CORE] Document ContextOperations as analysis entry points
- [ ] T023 [CORE] Mark out-of-scope Eclipse RCP patterns per constitution

### org.mwc.cmap.plotViewer (HIGH - spatial rendering)

- [ ] T024 [P] [CORE] Inspect org.mwc.cmap.plotViewer/src/ rendering code
- [ ] T025 [CORE] Draft org.mwc.cmap.plotViewer/README.md with Purpose, Entry Points
- [ ] T026 [CORE] Add detailed Spatial Rendering section (projection, draw order, shape rendering)
- [ ] T027 [CORE] Mark confidence levels and log UNCERTAIN items for interview

### org.mwc.debrief.track_shift (HIGH - algorithms)

- [ ] T028 [P] [CORE] Inspect org.mwc.debrief.track_shift/src/ TMA/bearing code
- [ ] T029 [CORE] Draft org.mwc.debrief.track_shift/README.md with Purpose, Entry Points
- [ ] T030 [CORE] Add Algorithms section (TMA generation, bearing analysis)
- [ ] T031 [CORE] Mark confidence levels and log UNCERTAIN items for interview

**Checkpoint**: Core plugin READMEs complete—LLM can navigate to major codebase areas

---

## Phase 4: Key Sub-Package READMEs (Priority: P2)

**Goal**: Document critical packages for domain model, data types, and rendering internals

**Independent Test**: LLM can understand TrackWrapper structure or projection algorithm

### MWC.GenericData (data types)

- [ ] T032 [P] [PKG] Inspect org.mwc.cmap.legacy/src/MWC/GenericData/*.java classes
- [ ] T033 [PKG] Draft org.mwc.cmap.legacy/src/MWC/GenericData/README.md
- [ ] T034 [PKG] Document WorldLocation, WorldSpeed, WorldDistance, WorldVector, HiResDate
- [ ] T035 [PKG] Document usage patterns and edge cases

### MWC.Algorithms (geodetic calculations)

- [ ] T036 [P] [PKG] Inspect org.mwc.cmap.legacy/src/MWC/Algorithms/*.java classes
- [ ] T037 [PKG] Draft org.mwc.cmap.legacy/src/MWC/Algorithms/README.md
- [ ] T038 [PKG] Document Conversions, EarthModel, PlainProjection
- [ ] T039 [PKG] Document Projections/ subpackage (FlatProjection, Mercator2/3)
- [ ] T040 [PKG] Add Algorithm sections for each geodetic calculation

### MWC.GUI.Canvas (canvas abstraction)

- [ ] T041 [P] [PKG] Inspect org.mwc.cmap.legacy/src/MWC/GUI/Canvas/*.java classes
- [ ] T042 [PKG] Draft org.mwc.cmap.legacy/src/MWC/GUI/Canvas/README.md
- [ ] T043 [PKG] Document CanvasAdaptor, SwingCanvas, MetafileCanvas
- [ ] T044 [PKG] Add Spatial Rendering section (coordinate transform, projection integration)

### MWC.GUI.Shapes (shape rendering)

- [ ] T045 [P] [PKG] Inspect org.mwc.cmap.legacy/src/MWC/GUI/Shapes/*.java classes
- [ ] T046 [PKG] Draft org.mwc.cmap.legacy/src/MWC/GUI/Shapes/README.md
- [ ] T047 [PKG] Document PlainShape, CircleShape, VectorShape, TextLabel
- [ ] T048 [PKG] Add Spatial Rendering section (shape-to-screen translation)

### Debrief.Wrappers (domain model)

- [ ] T049 [P] [PKG] Inspect org.mwc.debrief.legacy/src/Debrief/Wrappers/*.java classes
- [ ] T050 [PKG] Draft org.mwc.debrief.legacy/src/Debrief/Wrappers/README.md
- [ ] T051 [PKG] Document TrackWrapper (120KB core class) in detail
- [ ] T052 [PKG] Document FixWrapper, SensorWrapper, SensorContactWrapper
- [ ] T053 [PKG] Document TMAWrapper, CompositeTrackWrapper
- [ ] T054 [PKG] Document wrapper relationships and usage patterns

### Debrief.ReaderWriter (file formats)

- [ ] T055 [P] [PKG] Inspect org.mwc.debrief.legacy/src/Debrief/ReaderWriter/ subpackages
- [ ] T056 [PKG] Draft org.mwc.debrief.legacy/src/Debrief/ReaderWriter/README.md
- [ ] T057 [PKG] Document Replay/ (REP format: ImportReplay, FormatTracks)
- [ ] T058 [PKG] Document XML/ (DPF format: DebriefXMLReaderWriter)
- [ ] T059 [PKG] Document other formats (NMEA, AIS) briefly

**Checkpoint**: Key package READMEs complete—LLM understands domain model and algorithms

---

## Phase 5: Secondary Module READMEs (Priority: P3)

**Goal**: Document secondary modules for completeness

**Independent Test**: LLM can navigate to ASSET or SATC genetic algorithm code

### ASSET modules

- [ ] T060 [P] [SEC] Inspect org.mwc.asset.core/src/ structure
- [ ] T061 [P] [SEC] Inspect org.mwc.asset.legacy/src/ structure
- [ ] T062 [SEC] Draft org.mwc.asset.core/README.md
- [ ] T063 [SEC] Draft org.mwc.asset.legacy/README.md

### SATC (genetic algorithm)

- [ ] T064 [P] [SEC] Inspect org.mwc.debrief.satc.core/src/ genetic algorithm
- [ ] T065 [SEC] Draft org.mwc.debrief.satc.core/README.md with Algorithm section
- [ ] T066 [SEC] Document genetic algorithm in detail (inputs, outputs, parameters)

### GeoTools integration

- [ ] T067 [P] [SEC] Inspect org.mwc.cmap.gt2Plot/src/ GeoTools code
- [ ] T068 [SEC] Draft org.mwc.cmap.gt2Plot/README.md

### Natural Earth

- [ ] T069 [P] [SEC] Inspect org.mwc.cmap.naturalearth/src/ map data
- [ ] T070 [SEC] Draft org.mwc.cmap.naturalearth/README.md

**Checkpoint**: Secondary modules documented—full codebase navigation possible

---

## Phase 6: Polish & Cross-Cutting Concerns

**Purpose**: Review, validation, and interview resolution

- [ ] T071 [P] Conduct interview session for all UNCERTAIN items logged
- [ ] T072 [P] Update all docs with interview answers
- [ ] T073 [P] Cross-reference check: verify all doc links work
- [ ] T074 Validate with fresh LLM session: synthetic hotfix task for track handling
- [ ] T075 Validate with fresh LLM session: synthetic hotfix task for rendering
- [ ] T076 Validate with fresh LLM session: synthetic hotfix task for algorithm
- [ ] T077 Update docs/LLM_DOCUMENTATION_PLAN.md progress checkboxes
- [ ] T078 Final review: ensure all confidence markers present

---

## Dependencies & Execution Order

### Phase Dependencies

```
Phase 1: Setup
    ↓
Phase 2: Foundational (ROOT docs) ← BLOCKS all subsequent phases
    ↓
Phase 3: Core Plugin READMEs ← Can proceed after ROOT complete
    ↓
Phase 4: Key Package READMEs ← Can proceed after parent plugin README complete
    ↓
Phase 5: Secondary Modules ← Can proceed after Core complete
    ↓
Phase 6: Polish ← Depends on all content phases
```

### Within Each Phase

- Inspection tasks [P] can run in parallel
- Draft tasks depend on inspection
- Section additions depend on draft
- Confidence marking is final step per doc

### Parallel Opportunities

**Phase 1**: All template tasks (T002-T005) can run in parallel
**Phase 3**: Plugin inspections (T010, T015, T020, T024, T028) can run in parallel
**Phase 4**: Package inspections (T032, T036, T041, T045, T049, T055) can run in parallel
**Phase 5**: Secondary inspections (T060, T061, T064, T067, T069) can run in parallel
**Phase 6**: Interview updates and validations can run in parallel

---

## Implementation Strategy

### MVP First (ROOT docs only)

1. Complete Phase 1: Setup
2. Complete Phase 2: ROOT docs (CLAUDE.md, DOMAIN_GLOSSARY.md, ARCHITECTURE.md, KEY_CLASSES.md)
3. **STOP and VALIDATE**: Test LLM navigation with ROOT docs only
4. Delivers: Basic codebase orientation for emergency hotfixes

### Incremental Delivery

1. ROOT docs → Validate → Deploy
2. Add Core Plugin READMEs → Validate → Deploy
3. Add Package READMEs → Validate → Deploy
4. Add Secondary modules → Validate → Deploy
5. Each phase improves LLM navigation depth

### Single Documenter Strategy

Execute phases sequentially:
1. Complete Setup
2. Complete ROOT docs
3. Work through plugins in priority order (cmap.legacy → debrief.legacy → debrief.core → plotViewer → track_shift)
4. Work through packages in dependency order
5. Secondary modules last

---

## Notes

- [P] tasks can run in parallel when targeting different files
- [Phase] label maps task to documentation phase for traceability
- Follow quickstart.md workflow for each documentation target
- Use confidence markers: VERIFIED, INFERRED, UNCERTAIN
- Log all UNCERTAIN items in docs/LLM_DOCUMENTATION_PLAN.md Interview Log
- Constitution compliance: exclude UI widgets, Eclipse RCP patterns
- Validate frequently with fresh LLM sessions
