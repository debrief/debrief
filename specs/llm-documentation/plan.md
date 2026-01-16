# Implementation Plan: LLM Documentation for Legacy Debrief

**Branch**: `future-analysis` | **Date**: 2026-01-09 | **Spec**: [docs/LLM_DOCUMENTATION_PLAN.md](/docs/LLM_DOCUMENTATION_PLAN.md)
**Input**: Documentation plan from `/docs/LLM_DOCUMENTATION_PLAN.md`

## Summary

Create documentation enabling LLMs to navigate the legacy Debrief codebase for:
1. **Emergency hotfixes** during transition to DebriefNG
2. **Knowledge extraction** for algorithm porting and domain capture

Technical approach: Hierarchical documentation with root-level guides pointing to distributed README files in key plugins/packages, using confidence markers and structured algorithm/rendering sections.

## Technical Context

**Language/Version**: Markdown documentation (targeting Java 11 codebase)
**Primary Dependencies**: N/A (documentation only)
**Storage**: Git repository (markdown files)
**Testing**: Manual validation via fresh LLM sessions on synthetic hotfix tasks
**Target Platform**: Claude Code / LLM code assistants
**Project Type**: Documentation
**Performance Goals**: LLM can locate relevant code within 2-3 queries
**Constraints**: Non-destructive (no source code changes)
**Scale/Scope**: ~25 plugins, ~30 key packages, 4 root documents

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

| Principle | Status | Notes |
|-----------|--------|-------|
| I. Navigability Over Comprehensiveness | ✅ PASS | Focus on helping LLM find code, not cataloguing everything |
| II. Signal Over Ceremony | ✅ PASS | Concise statements, entry points before internals |
| III. Logic and Algorithms Are Primary | ✅ PASS | Spatial rendering + algorithms prioritised; UI excluded |
| IV. Explore Before Asking | ✅ PASS | Code inspection first, interview fills gaps |
| V. Document What Is, Not What Should Be | ✅ PASS | Describe existing behaviour, no opinions |

**Content Boundaries Compliance**:
- IN SCOPE: Logic, algorithms, spatial rendering, data structures, file formats ✅
- OUT OF SCOPE: UI widgets, Eclipse RCP patterns ✅

**Methodology Compliance**:
- Confidence markers (VERIFIED/INFERRED/UNCERTAIN) required ✅
- Interview protocol defined ✅

## Project Structure

### Documentation (this feature)

```text
specs/llm-documentation/
├── plan.md              # This file
├── research.md          # Phase 0: Codebase structure research
├── data-model.md        # Phase 1: Documentation entity model
└── quickstart.md        # Phase 1: How to start documenting
```

### Output Structure (repository root)

```text
# Root-level documentation
CLAUDE.md                    # Entry point (expand existing)
DOMAIN_GLOSSARY.md           # Maritime analysis concepts
ARCHITECTURE.md              # Module dependency map
KEY_CLASSES.md               # Critical 20-30 classes

# Distributed README files
org.mwc.debrief.core/README.md
org.mwc.cmap.legacy/README.md
org.mwc.debrief.legacy/README.md
org.mwc.cmap.plotViewer/README.md
org.mwc.debrief.track_shift/README.md

# Sub-package documentation (within parent plugins)
org.mwc.debrief.legacy/src/Debrief/Wrappers/README.md
org.mwc.cmap.legacy/src/MWC/GenericData/README.md
org.mwc.cmap.legacy/src/MWC/GUI/Shapes/README.md
org.mwc.cmap.legacy/src/MWC/GUI/Canvas/README.md
org.mwc.cmap.legacy/src/MWC/Algorithms/README.md
```

**Structure Decision**: Hierarchical documentation with root guides + distributed READMEs at plugin/package level. Follows codebase layout for discoverability.

## Complexity Tracking

No constitution violations requiring justification.

## Phased Delivery

### Phase 1: Root Documentation
- CLAUDE.md expansion
- DOMAIN_GLOSSARY.md
- ARCHITECTURE.md
- KEY_CLASSES.md

### Phase 2: Core Plugin READMEs
- org.mwc.debrief.core
- org.mwc.cmap.legacy
- org.mwc.debrief.legacy
- org.mwc.cmap.plotViewer
- org.mwc.debrief.track_shift

### Phase 3: Key Sub-Package READMEs
- Debrief.Wrappers (domain model)
- MWC.GenericData (data types)
- MWC.GUI.Shapes (spatial rendering)
- MWC.GUI.Canvas (spatial rendering)
- MWC.Algorithms (geodetic calculations)

### Phase 4: Secondary Modules
- org.mwc.asset.core/legacy
- org.mwc.debrief.satc.core (genetic algorithm)
- org.mwc.cmap.naturalearth
- org.mwc.cmap.gt2Plot
