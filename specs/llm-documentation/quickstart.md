# Quickstart: Creating LLM Documentation

**Date**: 2026-01-09

## Workflow Overview

```
1. Select target (plugin or package)
2. Draft skeleton structure
3. Inspect code to fill details
4. Mark confidence levels
5. Batch uncertain items for interview
6. Review and validate
```

---

## Step 1: Select Target

Check progress in `docs/LLM_DOCUMENTATION_PLAN.md`. Pick next unchecked item.

**Priority order**:
1. Root documents (CLAUDE.md, DOMAIN_GLOSSARY.md, ARCHITECTURE.md, KEY_CLASSES.md)
2. Core plugins (debrief.core, cmap.legacy, debrief.legacy, plotViewer, track_shift)
3. Key packages (Wrappers, GenericData, Shapes, Canvas, Algorithms)
4. Secondary modules

---

## Step 2: Draft Skeleton

### For Plugin README

```markdown
# [plugin-name]

## Purpose
[Leave blank - fill from code inspection]

## Entry Points
[Leave blank]

## Key Packages
| Package | Purpose |
|---------|---------|

## Spatial Rendering
[Include only if plugin has rendering code]

## Algorithms
[Include only if plugin has algorithm code]

## Dependencies
[Leave blank]
```

### For Package README

```markdown
# [package-name]

## Purpose
[Leave blank]

## Key Classes
| Class | Purpose | Confidence |
|-------|---------|------------|

## Usage Patterns
[Leave blank]

## Edge Cases
[Leave blank]
```

---

## Step 3: Inspect Code

### Finding Entry Points

```bash
# List public classes in a package
ls org.mwc.cmap.legacy/src/MWC/Algorithms/*.java

# Find main classes (look for "main" or entry methods)
grep -r "public static void main" org.mwc.debrief.legacy/src/

# Find interfaces (often entry points)
grep -l "^public interface" org.mwc.cmap.legacy/src/MWC/GUI/*.java
```

### Understanding Class Purpose

1. Read class Javadoc (if exists)
2. Check class name against DOMAIN_GLOSSARY.md
3. Look at public methods
4. Check what instantiates this class (find usages)

### Documenting Algorithms

For each algorithm, capture:

```markdown
### [Algorithm Name]
- **Purpose**: [What problem it solves]
- **Inputs**: [Data types and sources]
- **Outputs**: [Results and side effects]
- **Key Classes**: [Entry points and core logic]
- **Edge Cases**: [Known special handling]
- **Performance Notes**: [Constraints, optimisation history]
```

### Documenting Spatial Rendering

For rendering code, capture:

```markdown
## Spatial Rendering

### Coordinate System
[World coordinates vs screen coordinates]

### Projection
[Which projection class, how selected]

### Draw Order / Z-Index
[How overlapping elements are ordered]

### Shape Rendering
[How shapes translate to screen]
```

---

## Step 4: Mark Confidence

After each factual statement, add confidence marker:

| If you... | Use |
|-----------|-----|
| Read it directly from code | **[VERIFIED]** |
| Deduced from naming/patterns | **[INFERRED]** |
| Guessed or unsure | **[UNCERTAIN]** |

Example:
```markdown
TrackWrapper stores a collection of FixWrapper objects. **[VERIFIED]**
Positions are sorted by time. **[INFERRED]**
Duplicate timestamps are rejected. **[UNCERTAIN]**
```

---

## Step 5: Batch Interview Questions

Collect all **[UNCERTAIN]** items into Interview Log in `docs/LLM_DOCUMENTATION_PLAN.md`:

| Date | Topic | Question | Answer |
|------|-------|----------|--------|
| 2026-01-09 | TrackWrapper | Are duplicate timestamps rejected? | [pending] |

Then conduct focused interview session to resolve batch.

---

## Step 6: Review and Validate

### Self-Check

- [ ] Every section has content (no blank placeholders)
- [ ] Confidence markers on all factual claims
- [ ] No **[UNCERTAIN]** markers without interview log entry
- [ ] Links to related documents work
- [ ] Algorithm/rendering sections follow required structure

### LLM Validation (Optional)

Test with fresh Claude session:

1. Load only CLAUDE.md into context
2. Ask: "Find the code that handles [feature from this doc]"
3. Verify LLM navigates to correct location within 2-3 queries
4. If not, improve navigation hints

---

## Common Patterns

### Cross-Referencing

```markdown
See [MWC.GenericData](../MWC/GenericData/README.md) for WorldLocation details.
```

### Noting Historical Context

```markdown
> **Historical note**: This algorithm predates GPS availability and assumes
> manual position entry. [Source: Doc interview 2026-01-09]
```

### Marking Out-of-Scope

```markdown
> **Out of scope**: The Eclipse property sheet integration is not documented
> per [constitution](/.specify/memory/constitution.md).
```

---

## File Checklist

Update `docs/LLM_DOCUMENTATION_PLAN.md` when complete:

```markdown
### Phase 1: Root Documentation
- [x] Expand CLAUDE.md with navigation guidance  ← mark complete
- [ ] Draft DOMAIN_GLOSSARY.md
```
