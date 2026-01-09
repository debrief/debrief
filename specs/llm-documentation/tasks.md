# Tasks: LLM Documentation for Legacy Debrief

**Input**: Design documents from `/specs/llm-documentation/`
**Prerequisites**: plan.md, research.md, data-model.md, quickstart.md, clarifications from docs/LLM_DOCUMENTATION_PLAN.md

**Tests**: No automated tests. Validation via fresh LLM sessions on synthetic hotfix tasks.

**Organization**: Tasks organized by documentation phase (Root → Core Plugins → Packages → Secondary).

**Quality Standard**: Complete, thorough content—all template sections filled with substantive content. Goal is knowledge preservation for Future Debrief.

**Diagram Requirements**:
- Mermaid for complex visualizations (flowcharts, class diagrams, sequence diagrams)
- ASCII for simple directory trees
- Diagrams at all levels (root, plugin, package)
- Pseudocode for complex algorithms

## Format: `[ID] [P?] [Phase] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Phase]**: Which documentation phase (ROOT, CORE, PKG, SEC)
- Include exact file paths in descriptions

---

## Phase 1: Setup

**Purpose**: Establish documentation infrastructure and templates with Mermaid support

- [X] T001 Create documentation templates directory at docs/templates/
- [X] T002 [P] Create plugin README template with Mermaid diagram placeholders at docs/templates/plugin-readme-template.md
- [X] T003 [P] Create package README template with Mermaid diagram placeholders at docs/templates/package-readme-template.md
- [X] T004 [P] Create algorithm section template with pseudocode block at docs/templates/algorithm-section-template.md
- [X] T005 [P] Create spatial rendering section template at docs/templates/spatial-rendering-template.md
- [X] T006 [P] Create Mermaid diagram examples file at docs/templates/mermaid-examples.md

---

## Phase 2: Foundational - ROOT Documentation (Blocking Prerequisites)

**Purpose**: Core reference documents that all other docs depend on. Must include Mermaid diagrams.

**CRITICAL**: ROOT docs must be complete before plugin/package READMEs can reference them

### CLAUDE.md Entry Point

- [X] T007 [ROOT] Inspect existing CLAUDE.md structure and content
- [X] T008 [ROOT] Expand CLAUDE.md with navigation guidance to companion docs at CLAUDE.md
- [X] T009 [ROOT] Add Mermaid flowchart showing documentation hierarchy to CLAUDE.md
- [X] T010 [ROOT] Add quick reference table for common LLM queries to CLAUDE.md

### DOMAIN_GLOSSARY.md

- [X] T011 [P] [ROOT] Research maritime analysis terminology from codebase (TMA, fixes, bearings, tracks, sensors, contributions, legs)
- [X] T012 [ROOT] Draft DOMAIN_GLOSSARY.md with all maritime terms at DOMAIN_GLOSSARY.md
- [X] T013 [ROOT] Add Mermaid class diagram showing domain concept relationships to DOMAIN_GLOSSARY.md
- [X] T014 [ROOT] Add historical context and lessons learned for key concepts to DOMAIN_GLOSSARY.md
- [X] T015 [ROOT] Mark confidence levels and log UNCERTAIN items for interview

### ARCHITECTURE.md

- [X] T016 [P] [ROOT] Inspect plugin dependencies and data flow across codebase
- [X] T017 [ROOT] Draft ARCHITECTURE.md skeleton with module sections at ARCHITECTURE.md
- [X] T018 [ROOT] Add Mermaid flowchart showing data flow between major modules to ARCHITECTURE.md
- [X] T019 [ROOT] Add Mermaid flowchart showing plugin dependency graph to ARCHITECTURE.md
- [X] T020 [ROOT] Add Mermaid sequence diagram showing typical track loading flow to ARCHITECTURE.md
- [X] T021 [ROOT] Document two-layer architecture (Eclipse RCP vs Legacy) with rationale to ARCHITECTURE.md
- [X] T022 [ROOT] Add lessons learned and design rationale sections to ARCHITECTURE.md
- [X] T023 [ROOT] Mark confidence levels and log UNCERTAIN items for interview

### KEY_CLASSES.md

- [X] T024 [P] [ROOT] Identify 20-30 critical classes from research.md and code inspection
- [X] T025 [ROOT] Draft KEY_CLASSES.md with class inventory table at KEY_CLASSES.md
- [X] T026 [ROOT] Add Mermaid class diagram showing wrapper hierarchy (TrackWrapper→FixWrapper→SensorWrapper) to KEY_CLASSES.md
- [X] T027 [ROOT] Add Mermaid class diagram showing data type relationships (WorldLocation, WorldSpeed, etc.) to KEY_CLASSES.md
- [X] T028 [ROOT] Add Mermaid class diagram showing canvas/rendering class relationships to KEY_CLASSES.md
- [X] T029 [ROOT] Document each class with purpose, key methods, and usage patterns to KEY_CLASSES.md
- [X] T030 [ROOT] Add design rationale and historical context for key architectural decisions to KEY_CLASSES.md
- [X] T031 [ROOT] Mark confidence levels and log UNCERTAIN items for interview

**Checkpoint**: Root documentation complete—plugin READMEs can now reference these diagrams and terminology

---

## Phase 3: Core Plugin READMEs (Priority: P1)

**Goal**: Document the 5 core plugins with thorough content, Mermaid diagrams, and knowledge preservation focus

**Independent Test**: LLM can locate track handling, rendering, or algorithm code within 2 queries

### org.mwc.cmap.legacy (HIGH - algorithms + rendering)

- [X] T032 [P] [CORE] Inspect org.mwc.cmap.legacy/src/MWC/ package structure thoroughly
- [X] T033 [CORE] Draft org.mwc.cmap.legacy/README.md with Purpose, Entry Points, Key Packages
- [X] T034 [CORE] Add Mermaid flowchart showing MWC package relationships to org.mwc.cmap.legacy/README.md
- [X] T035 [CORE] Add Algorithms section with prose + pseudocode for geodetic calculations to org.mwc.cmap.legacy/README.md
- [X] T036 [CORE] Add Mermaid sequence diagram for coordinate transformation flow to org.mwc.cmap.legacy/README.md
- [X] T037 [CORE] Add Spatial Rendering section (canvas, shapes, projection) to org.mwc.cmap.legacy/README.md
- [X] T038 [CORE] Add Mermaid class diagram for canvas hierarchy to org.mwc.cmap.legacy/README.md
- [X] T039 [CORE] Add design rationale and lessons learned section to org.mwc.cmap.legacy/README.md
- [X] T040 [CORE] Mark confidence levels and log UNCERTAIN items for interview

### org.mwc.debrief.legacy (HIGH - domain model)

- [X] T041 [P] [CORE] Inspect org.mwc.debrief.legacy/src/Debrief/ package structure thoroughly
- [X] T042 [CORE] Draft org.mwc.debrief.legacy/README.md with Purpose, Entry Points, Key Packages
- [X] T043 [CORE] Add Mermaid class diagram showing wrapper relationships to org.mwc.debrief.legacy/README.md
- [X] T044 [CORE] Document wrapper pattern (TrackWrapper, FixWrapper, SensorWrapper) with rationale to org.mwc.debrief.legacy/README.md
- [X] T045 [CORE] Add Mermaid sequence diagram for REP file parsing flow to org.mwc.debrief.legacy/README.md
- [X] T046 [CORE] Document file format handlers (REP, DPF/XML) with format details to org.mwc.debrief.legacy/README.md
- [X] T047 [CORE] Add design rationale and lessons learned section to org.mwc.debrief.legacy/README.md
- [X] T048 [CORE] Mark confidence levels and log UNCERTAIN items for interview

### org.mwc.debrief.core (MEDIUM - entry points)

- [X] T049 [P] [CORE] Inspect org.mwc.debrief.core/src/ context operations thoroughly
- [X] T050 [CORE] Draft org.mwc.debrief.core/README.md with Purpose, Entry Points
- [X] T051 [CORE] Add Mermaid flowchart showing context operation categories to org.mwc.debrief.core/README.md
- [X] T052 [CORE] Document ContextOperations as analysis entry points with usage examples to org.mwc.debrief.core/README.md
- [X] T053 [CORE] Mark out-of-scope Eclipse RCP patterns per constitution to org.mwc.debrief.core/README.md
- [X] T054 [CORE] Add design rationale for context operation architecture to org.mwc.debrief.core/README.md
- [X] T055 [CORE] Mark confidence levels and log UNCERTAIN items for interview

### org.mwc.cmap.plotViewer (HIGH - spatial rendering)

- [X] T056 [P] [CORE] Inspect org.mwc.cmap.plotViewer/src/ rendering code thoroughly
- [X] T057 [CORE] Draft org.mwc.cmap.plotViewer/README.md with Purpose, Entry Points
- [X] T058 [CORE] Add detailed Spatial Rendering section (projection, draw order, shape rendering) to org.mwc.cmap.plotViewer/README.md
- [X] T059 [CORE] Add Mermaid sequence diagram for plot rendering pipeline to org.mwc.cmap.plotViewer/README.md
- [X] T060 [CORE] Add Mermaid flowchart showing coordinate system transformations to org.mwc.cmap.plotViewer/README.md
- [X] T061 [CORE] Document rendering fidelity requirements for Future Debrief to org.mwc.cmap.plotViewer/README.md
- [X] T062 [CORE] Add design rationale and lessons learned for rendering decisions to org.mwc.cmap.plotViewer/README.md
- [X] T063 [CORE] Mark confidence levels and log UNCERTAIN items for interview

### org.mwc.debrief.track_shift (HIGH - algorithms)

- [X] T064 [P] [CORE] Inspect org.mwc.debrief.track_shift/src/ TMA/bearing code thoroughly
- [X] T065 [CORE] Draft org.mwc.debrief.track_shift/README.md with Purpose, Entry Points
- [X] T066 [CORE] Add Algorithms section with TMA generation prose + pseudocode to org.mwc.debrief.track_shift/README.md
- [X] T067 [CORE] Add Mermaid sequence diagram for TMA solution generation flow to org.mwc.debrief.track_shift/README.md
- [X] T068 [CORE] Add bearing analysis algorithms with pseudocode to org.mwc.debrief.track_shift/README.md
- [X] T069 [CORE] Add Mermaid flowchart showing track shift workflow to org.mwc.debrief.track_shift/README.md
- [X] T070 [CORE] Add design rationale and lessons learned for TMA algorithms to org.mwc.debrief.track_shift/README.md
- [X] T071 [CORE] Mark confidence levels and log UNCERTAIN items for interview

**Checkpoint**: Core plugin READMEs complete—LLM can navigate to major codebase areas with full context

---

## Phase 4: Key Sub-Package READMEs (Priority: P2)

**Goal**: Document critical packages with thorough content, diagrams, and pseudocode for algorithms

**Independent Test**: LLM can understand TrackWrapper structure or projection algorithm in detail

### MWC.GenericData (data types)

- [X] T072 [P] [PKG] Inspect org.mwc.cmap.legacy/src/MWC/GenericData/*.java classes thoroughly
- [X] T073 [PKG] Draft org.mwc.cmap.legacy/src/MWC/GenericData/README.md with all sections
- [X] T074 [PKG] Add Mermaid class diagram showing data type hierarchy to MWC/GenericData/README.md
- [X] T075 [PKG] Document WorldLocation, WorldSpeed, WorldDistance, WorldVector, HiResDate in detail to MWC/GenericData/README.md
- [X] T076 [PKG] Document usage patterns, edge cases, and conversion gotchas to MWC/GenericData/README.md
- [X] T077 [PKG] Add design rationale for data type decisions to MWC/GenericData/README.md
- [X] T078 [PKG] Mark confidence levels and log UNCERTAIN items for interview

### MWC.Algorithms (geodetic calculations)

- [X] T079 [P] [PKG] Inspect org.mwc.cmap.legacy/src/MWC/Algorithms/*.java classes thoroughly
- [X] T080 [PKG] Draft org.mwc.cmap.legacy/src/MWC/Algorithms/README.md with all sections
- [X] T081 [PKG] Add Mermaid class diagram showing algorithm class relationships to MWC/Algorithms/README.md
- [X] T082 [PKG] Document Conversions with pseudocode for key conversions to MWC/Algorithms/README.md
- [X] T083 [PKG] Document EarthModel, PlainProjection with pseudocode to MWC/Algorithms/README.md
- [X] T084 [PKG] Document Projections/ subpackage (FlatProjection, Mercator2/3) with pseudocode to MWC/Algorithms/README.md
- [X] T085 [PKG] Add Mermaid sequence diagram for projection calculation flow to MWC/Algorithms/README.md
- [X] T086 [PKG] Add design rationale and lessons learned for geodetic decisions to MWC/Algorithms/README.md
- [X] T087 [PKG] Mark confidence levels and log UNCERTAIN items for interview

### MWC.GUI.Canvas (canvas abstraction)

- [X] T088 [P] [PKG] Inspect org.mwc.cmap.legacy/src/MWC/GUI/Canvas/*.java classes thoroughly
- [X] T089 [PKG] Draft org.mwc.cmap.legacy/src/MWC/GUI/Canvas/README.md with all sections
- [X] T090 [PKG] Add Mermaid class diagram showing canvas hierarchy to MWC/GUI/Canvas/README.md
- [X] T091 [PKG] Document CanvasAdaptor, SwingCanvas, MetafileCanvas in detail to MWC/GUI/Canvas/README.md
- [X] T092 [PKG] Add Spatial Rendering section (coordinate transform, projection integration) to MWC/GUI/Canvas/README.md
- [X] T093 [PKG] Add Mermaid sequence diagram for canvas drawing operations to MWC/GUI/Canvas/README.md
- [X] T094 [PKG] Add design rationale and rendering lessons learned to MWC/GUI/Canvas/README.md
- [X] T095 [PKG] Mark confidence levels and log UNCERTAIN items for interview

### MWC.GUI.Shapes (shape rendering)

- [X] T096 [P] [PKG] Inspect org.mwc.cmap.legacy/src/MWC/GUI/Shapes/*.java classes thoroughly
- [X] T097 [PKG] Draft org.mwc.cmap.legacy/src/MWC/GUI/Shapes/README.md with all sections
- [X] T098 [PKG] Add Mermaid class diagram showing shape hierarchy to MWC/GUI/Shapes/README.md
- [X] T099 [PKG] Document PlainShape, CircleShape, VectorShape, TextLabel in detail to MWC/GUI/Shapes/README.md
- [X] T100 [PKG] Add Spatial Rendering section (shape-to-screen translation) to MWC/GUI/Shapes/README.md
- [X] T101 [PKG] Add Mermaid sequence diagram for shape rendering pipeline to MWC/GUI/Shapes/README.md
- [X] T102 [PKG] Add design rationale and shape rendering lessons to MWC/GUI/Shapes/README.md
- [X] T103 [PKG] Mark confidence levels and log UNCERTAIN items for interview

### Debrief.Wrappers (domain model)

- [X] T104 [P] [PKG] Inspect org.mwc.debrief.legacy/src/Debrief/Wrappers/*.java classes thoroughly
- [X] T105 [PKG] Draft org.mwc.debrief.legacy/src/Debrief/Wrappers/README.md with all sections
- [X] T106 [PKG] Add Mermaid class diagram showing wrapper inheritance hierarchy to Debrief/Wrappers/README.md
- [X] T107 [PKG] Document TrackWrapper (120KB core class) comprehensively to Debrief/Wrappers/README.md
- [X] T108 [PKG] Document FixWrapper, SensorWrapper, SensorContactWrapper in detail to Debrief/Wrappers/README.md
- [X] T109 [PKG] Document TMAWrapper, CompositeTrackWrapper in detail to Debrief/Wrappers/README.md
- [X] T110 [PKG] Add Mermaid sequence diagram for track data manipulation to Debrief/Wrappers/README.md
- [X] T111 [PKG] Document wrapper relationships, usage patterns, and edge cases to Debrief/Wrappers/README.md
- [X] T112 [PKG] Add design rationale for wrapper pattern and lessons learned to Debrief/Wrappers/README.md
- [X] T113 [PKG] Mark confidence levels and log UNCERTAIN items for interview

### Debrief.ReaderWriter (file formats)

- [X] T114 [P] [PKG] Inspect org.mwc.debrief.legacy/src/Debrief/ReaderWriter/ subpackages thoroughly
- [X] T115 [PKG] Draft org.mwc.debrief.legacy/src/Debrief/ReaderWriter/README.md with all sections
- [X] T116 [PKG] Add Mermaid flowchart showing file format handler architecture to Debrief/ReaderWriter/README.md
- [X] T117 [PKG] Document Replay/ (REP format: ImportReplay, FormatTracks) with format spec to Debrief/ReaderWriter/README.md
- [X] T118 [PKG] Add Mermaid sequence diagram for REP parsing flow to Debrief/ReaderWriter/README.md
- [X] T119 [PKG] Document XML/ (DPF format: DebriefXMLReaderWriter) with format spec to Debrief/ReaderWriter/README.md
- [X] T120 [PKG] Document other formats (NMEA, AIS) with format details to Debrief/ReaderWriter/README.md
- [X] T121 [PKG] Add design rationale and file format lessons learned to Debrief/ReaderWriter/README.md
- [X] T122 [PKG] Mark confidence levels and log UNCERTAIN items for interview

**Checkpoint**: Key package READMEs complete—LLM understands domain model, algorithms, and rendering in depth

---

## Phase 5: Secondary Module READMEs (Priority: P3)

**Goal**: Document secondary modules with focus on SATC genetic algorithm

**Independent Test**: LLM can navigate to ASSET or SATC genetic algorithm code and understand it

### ASSET modules

- [X] T123 [P] [SEC] Inspect org.mwc.asset.core/src/ structure thoroughly
- [X] T124 [P] [SEC] Inspect org.mwc.asset.legacy/src/ structure thoroughly
- [X] T125 [SEC] Draft org.mwc.asset.core/README.md with all sections
- [X] T126 [SEC] Draft org.mwc.asset.legacy/README.md with all sections
- [X] T127 [SEC] Add Mermaid class diagram for ASSET domain model to org.mwc.asset.legacy/README.md
- [X] T128 [SEC] Add design rationale and lessons learned to ASSET READMEs

### SATC (genetic algorithm - HIGH PRIORITY)

- [X] T129 [P] [SEC] Inspect org.mwc.debrief.satc.core/src/ genetic algorithm thoroughly
- [X] T130 [SEC] Draft org.mwc.debrief.satc.core/README.md with comprehensive Algorithm section
- [X] T131 [SEC] Add Mermaid flowchart showing genetic algorithm workflow to org.mwc.debrief.satc.core/README.md
- [X] T132 [SEC] Document genetic algorithm with detailed pseudocode (fitness, selection, crossover, mutation) to org.mwc.debrief.satc.core/README.md
- [X] T133 [SEC] Add Mermaid sequence diagram for SATC solution generation to org.mwc.debrief.satc.core/README.md
- [X] T134 [SEC] Document algorithm parameters, tuning history, and lessons learned to org.mwc.debrief.satc.core/README.md
- [X] T135 [SEC] Mark confidence levels and log UNCERTAIN items for interview

### GeoTools integration

- [X] T136 [P] [SEC] Inspect org.mwc.cmap.gt2Plot/src/ GeoTools code thoroughly
- [X] T137 [SEC] Draft org.mwc.cmap.gt2Plot/README.md with all sections
- [X] T138 [SEC] Add Mermaid diagram showing GeoTools integration points to org.mwc.cmap.gt2Plot/README.md
- [X] T139 [SEC] Document integration lessons learned to org.mwc.cmap.gt2Plot/README.md

### Natural Earth

- [X] T140 [P] [SEC] Inspect org.mwc.cmap.NaturalEarth/src/ map data thoroughly
- [X] T141 [SEC] Draft org.mwc.cmap.NaturalEarth/README.md with all sections
- [X] T142 [SEC] Document map data integration and usage patterns to org.mwc.cmap.NaturalEarth/README.md

**Checkpoint**: Secondary modules documented—full codebase navigation and knowledge preservation complete

---

## Phase 6: Polish & Cross-Cutting Concerns

**Purpose**: Review, validation, interview resolution, and cross-referencing

### Interview Resolution

- [X] T143 [P] Compile all UNCERTAIN items from all READMEs into interview agenda
- [N/A] T144 Conduct interview session with Doc for all UNCERTAIN items (no UNCERTAIN in READMEs)
- [N/A] T145 [P] Update all docs with interview answers and historical context (no UNCERTAIN in READMEs)
- [N/A] T146 [P] Convert remaining UNCERTAIN markers to VERIFIED or INFERRED with notes (all VERIFIED)

### Cross-Reference Verification

- [X] T147 [P] Verify all internal doc links work correctly
- [X] T148 [P] Verify all Mermaid diagrams render correctly in GitHub (68 diagrams across 25 files)
- [X] T149 [P] Verify terminology consistency across all documents against DOMAIN_GLOSSARY.md
- [X] T150 [P] Verify all algorithm sections have pseudocode where appropriate

### LLM Validation

- [ ] T151 Validate with fresh LLM session: synthetic hotfix task for track handling
- [ ] T152 Validate with fresh LLM session: synthetic hotfix task for rendering
- [ ] T153 Validate with fresh LLM session: synthetic hotfix task for algorithm (e.g., TMA)
- [ ] T154 Validate with fresh LLM session: knowledge extraction task (e.g., port projection algorithm)
- [ ] T155 Document validation results and any navigation improvements needed

### Final Cleanup

- [X] T156 Update docs/LLM_DOCUMENTATION_PLAN.md progress checkboxes to complete
- [X] T157 Final review: ensure all confidence markers present (220 markers across 29 files)
- [X] T158 Final review: ensure all lessons learned sections are substantive
- [X] T159 Create documentation changelog summarizing what was captured

---

## Dependencies & Execution Order

### Phase Dependencies

```
Phase 1: Setup (templates)
    ↓
Phase 2: ROOT docs ← BLOCKS all subsequent phases
    ↓
Phase 3: Core Plugins ← Can proceed after ROOT complete
    ↓
Phase 4: Packages ← Can proceed after parent plugin README complete
    ↓
Phase 5: Secondary ← Can proceed after Core complete
    ↓
Phase 6: Polish ← Depends on all content phases
```

### Within Each Phase

- Inspection tasks [P] can run in parallel
- Draft tasks depend on inspection
- Mermaid diagram tasks depend on draft
- Design rationale tasks depend on content being written
- Confidence marking is final step per doc

### Parallel Opportunities

**Phase 1**: All template tasks (T002-T006) can run in parallel
**Phase 2**: Research tasks (T011, T016, T024) can run in parallel
**Phase 3**: Plugin inspections (T032, T041, T049, T056, T064) can run in parallel
**Phase 4**: Package inspections (T072, T079, T088, T096, T104, T114) can run in parallel
**Phase 5**: Secondary inspections (T123, T124, T129, T136, T140) can run in parallel
**Phase 6**: Cross-reference and validation tasks can run in parallel

---

## Implementation Strategy

### Knowledge Preservation First

Execute with focus on capturing institutional knowledge:
1. Complete ROOT docs with full architectural context and rationale
2. Interview early and often to capture lessons learned
3. Prioritize "why" alongside "what" in all documentation
4. Don't rush—thoroughness over speed

### Incremental Delivery

1. ROOT docs → Validate → Deploy
2. Add Core Plugin READMEs with diagrams → Validate → Deploy
3. Add Package READMEs with pseudocode → Validate → Deploy
4. Add Secondary modules (especially SATC) → Validate → Deploy
5. Each phase preserves more institutional knowledge

### Single Documenter Strategy

Execute phases sequentially with interview batching:
1. Complete Setup and ROOT docs
2. Batch interview for ROOT uncertainties
3. Work through plugins in priority order with diagrams
4. Batch interview for plugin uncertainties
5. Work through packages with pseudocode
6. Batch interview for package uncertainties
7. Secondary modules last (SATC is priority)
8. Final validation pass

---

## Notes

- [P] tasks can run in parallel when targeting different files
- [Phase] label maps task to documentation phase for traceability
- **Quality over speed**: Every README must be complete and thorough
- **Knowledge preservation**: Capture lessons learned and design rationale throughout
- **Mermaid diagrams**: Required at all levels for GitHub rendering
- **Pseudocode**: Required for all complex algorithms
- Constitution compliance: exclude UI widgets, Eclipse RCP patterns
- Validate frequently with fresh LLM sessions
- Log all UNCERTAIN items for batched interviews
