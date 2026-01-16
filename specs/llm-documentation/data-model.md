# Data Model: LLM Documentation Entities

**Date**: 2026-01-09

## Documentation Entities

### Root Documents

| Entity | File | Purpose | Dependencies |
|--------|------|---------|--------------|
| Entry Point | `CLAUDE.md` | Direct LLM to companion docs, key directories | None |
| Glossary | `DOMAIN_GLOSSARY.md` | Maritime analysis terminology | None |
| Architecture | `ARCHITECTURE.md` | Module dependency map, data flow | Glossary |
| Key Classes | `KEY_CLASSES.md` | Critical 20-30 classes with roles | Architecture, Glossary |

### Plugin READMEs

| Entity | File | Purpose | Dependencies |
|--------|------|---------|--------------|
| Core Plugin | `org.mwc.debrief.core/README.md` | Eclipse integration, context ops | Architecture |
| CMap Legacy | `org.mwc.cmap.legacy/README.md` | Data types, algorithms, canvas | Glossary |
| Debrief Legacy | `org.mwc.debrief.legacy/README.md` | Wrappers, file I/O | CMap Legacy |
| Plot Viewer | `org.mwc.cmap.plotViewer/README.md` | Plot rendering | CMap Legacy |
| Track Shift | `org.mwc.debrief.track_shift/README.md` | TMA, bearing analysis | Debrief Legacy |

### Package READMEs

| Entity | File | Purpose | Parent |
|--------|------|---------|--------|
| Wrappers | `Debrief/Wrappers/README.md` | Domain model wrappers | Debrief Legacy |
| GenericData | `MWC/GenericData/README.md` | Core data types | CMap Legacy |
| Shapes | `MWC/GUI/Shapes/README.md` | Shape rendering | CMap Legacy |
| Canvas | `MWC/GUI/Canvas/README.md` | Canvas abstraction | CMap Legacy |
| Algorithms | `MWC/Algorithms/README.md` | Geodetic calculations | CMap Legacy |
| ReaderWriter | `Debrief/ReaderWriter/README.md` | File format handlers | Debrief Legacy |

---

## Document Structure Templates

### Root Document Template

```markdown
# [DOCUMENT_TITLE]

## Purpose
[One sentence: what problem this solves for an LLM]

## Quick Reference
[Table or list of most common lookups]

## [Main Content Sections]
[Organized by use case, not by code structure]

## See Also
[Links to related documents]
```

### Plugin README Template

```markdown
# [PLUGIN_NAME]

## Purpose
[What this plugin does, in one paragraph]

## Entry Points
[Key classes an LLM should start with]

## Key Packages
| Package | Purpose |
|---------|---------|

## Spatial Rendering (if applicable)
[Required section per constitution]

## Algorithms (if applicable)
[Required section per constitution]

## Dependencies
[What this plugin requires]
```

### Package README Template

```markdown
# [PACKAGE_NAME]

## Purpose
[What this package provides]

## Key Classes
| Class | Purpose | Confidence |
|-------|---------|------------|

## Usage Patterns
[How classes work together]

## Edge Cases
[Known special handling]
```

---

## Confidence Markers

All documentation MUST use:

| Marker | Usage |
|--------|-------|
| **VERIFIED** | Confirmed by code inspection |
| **INFERRED** | Deduced from naming/context |
| **UNCERTAIN** | Needs interview clarification |

Example:
```markdown
The `TrackWrapper` class manages track segments. **[VERIFIED]**
Track positions are interpolated using linear interpolation. **[INFERRED]**
The algorithm handles anti-meridian crossing. **[UNCERTAIN]**
```

---

## Content Boundaries (per Constitution)

### IN SCOPE

- Logic and algorithms (computational behaviour)
- Spatial rendering (map plot, canvas operations)
- Data structures and domain models
- File format parsing and generation
- Design rationale and historical context

### OUT OF SCOPE

- UI widgets, dialogs, Eclipse views
- Eclipse RCP patterns (OSGi, extension points)
- Framework boilerplate

---

## Documentation Dependencies Graph

```
CLAUDE.md (entry point)
    │
    ├── DOMAIN_GLOSSARY.md
    │
    ├── ARCHITECTURE.md
    │       │
    │       └── KEY_CLASSES.md
    │
    └── Plugin READMEs
            │
            ├── org.mwc.cmap.legacy/README.md
            │       ├── MWC/GenericData/README.md
            │       ├── MWC/Algorithms/README.md
            │       ├── MWC/GUI/Canvas/README.md
            │       └── MWC/GUI/Shapes/README.md
            │
            └── org.mwc.debrief.legacy/README.md
                    ├── Debrief/Wrappers/README.md
                    └── Debrief/ReaderWriter/README.md
```

---

## Validation Criteria

Documentation is complete when:

1. LLM can locate relevant code within 2-3 queries
2. All algorithm sections include: Purpose, Inputs, Outputs, Key Classes, Edge Cases
3. All spatial rendering sections include: Coordinate System, Projection, Draw Order
4. Confidence markers present on all factual claims
5. No UNCERTAIN markers remain without interview follow-up logged
