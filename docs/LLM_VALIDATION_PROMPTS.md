# LLM Validation Prompts

Test prompts for validating LLM documentation navigation. Use each prompt in a **fresh Claude Code session** with no prior context.

## Instructions

1. Start a new Claude Code session in the debrief repo
2. Use one prompt per session
3. Evaluate: Did the LLM find the right files? Did it understand the domain?
4. Record results in the table at the bottom

---

## T151: Track Handling Hotfix

```
A user reports that when they delete a fix from a track, the track's time period
doesn't update correctly - getStartDTG() still returns the old start time.

Find where track time bounds are calculated and identify what needs to change.
Don't make changes yet - just locate the code and explain the fix.
```

**Expected navigation**:
- CLAUDE.md → KEY_CLASSES.md → TrackWrapper
- Debrief.Wrappers/README.md
- `TrackWrapper.java` - getStartDTG(), getEndDTG(), or time recalculation

**Success criteria**: Locates TrackWrapper, understands FixWrapper relationship, identifies time bound calculation

---

## T152: Rendering Hotfix

```
Users report that when zoomed in very close on a track, the position symbols
overlap and become unreadable. We need to implement symbol decimation -
skip drawing some symbols when they'd overlap.

Where in the rendering pipeline should this logic be added?
What existing patterns can we follow?
```

**Expected navigation**:
- CLAUDE.md → org.mwc.cmap.plotViewer/README.md
- MWC.GUI.Canvas/README.md
- MWC.GUI.Shapes/README.md
- PlainChart rendering pipeline

**Success criteria**: Understands paint() chain, identifies where to add decimation logic, references existing shape rendering

---

## T153: TMA Algorithm Hotfix

```
The bearing residual calculation in TMA track shifting seems wrong when
ownship crosses the 0/360 degree boundary. The residual jumps by 360 degrees.

Find the bearing residual calculation and identify how to fix the
wrap-around issue.
```

**Expected navigation**:
- CLAUDE.md → org.mwc.debrief.track_shift/README.md
- DOMAIN_GLOSSARY.md (bearing residual definition)
- `StackedDotHelper.java` or residual calculation code

**Success criteria**: Locates bearing calculation, understands TMA concepts, identifies angle normalization issue

---

## T154: Knowledge Extraction - Projection Porting

```
We're porting Debrief to a new platform and need to understand the projection
system completely.

Document:
1. What projections are supported?
2. How does world-to-screen coordinate transformation work?
3. What's the default projection and why?
4. Provide pseudocode for the core transformation algorithm.
```

**Expected navigation**:
- CLAUDE.md → MWC.Algorithms/README.md
- org.mwc.cmap.legacy/README.md
- FlatProjection, PlainProjection classes
- Coordinate transformation pseudocode in docs

**Success criteria**: Finds projection classes, extracts algorithm pseudocode, understands flat-earth vs Mercator tradeoffs

---

## T155: Complex Navigation - Sensor Contact Import

```
A customer has a custom sensor data file format. We need to understand
how sensor contacts are imported and stored so we can write a new importer.

Trace the data flow from file import to SensorContactWrapper creation.
What interfaces/patterns should the new importer follow?
```

**Expected navigation**:
- CLAUDE.md → Debrief.ReaderWriter/README.md
- ARCHITECTURE.md (data flow)
- Debrief.Wrappers/README.md (SensorWrapper, SensorContactWrapper)
- ImportReplay or handler pattern

**Success criteria**: Understands importer pattern, traces data flow, identifies SensorContactWrapper fields

---

## Validation Results

| Test | Date | LLM Found Right Files | Understood Domain | Time to Solution | Notes |
|------|------|----------------------|-------------------|------------------|-------|
| T151 Track | | | | | |
| T152 Render | | | | | |
| T153 TMA | | | | | |
| T154 Projection | | | | | |
| T155 Sensor | | | | | |

### Scoring Guide

- **Found Right Files**: Yes/Partial/No - Did it navigate to the correct READMEs and source files?
- **Understood Domain**: Yes/Partial/No - Did it use correct terminology and understand relationships?
- **Time to Solution**: Fast (<2 min) / Medium (2-5 min) / Slow (>5 min)

### Improvement Actions

Record any documentation gaps discovered during validation:

| Test | Gap Identified | Suggested Fix |
|------|---------------|---------------|
| | | |
