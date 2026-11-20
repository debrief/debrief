# Quickstart: Validate Draggable Planning-Point Affordance

**Date**: 2026-09-04
**Spec**: [spec.md](./spec.md)
**Contract**: [contracts/drag-component-affordance.md](./contracts/drag-component-affordance.md)
**Data Model**: [data-model.md](./data-model.md)

## Prerequisites

- Java 11 and Maven available.
- Debrief workspace builds successfully.
- Sample planning datasets available under `org.mwc.cmap.combined.feature/root_installs/sample_data/`.
- A product build or Eclipse launch configuration capable of opening a plot editor.

## Setup Commands

```bash
mvn test -pl org.mwc.debrief.test2
mvn clean install -pl org.mwc.debrief.product -am -DskipTests
```

Optional UI automation baseline:

```bash
mvn -f org.mwc.debrief.ui_test/pom.xml test
```

Optional sample data sanity checks:

```bash
rg -n "PLANNING_ORIGIN|PLANNING_RANGE_SPEED|PLANNING_SPEED_TIME" org.mwc.cmap.combined.feature/root_installs/sample_data/planning_tracks.rep
rg -n "<composite_track|<planning_segment" org.mwc.cmap.combined.feature/root_installs/sample_data/compositeTrack.dpf
```

## Manual Validation Scenarios

Use `planning_tracks.rep` or `Demo/TrialsPlanning/TrialsPlanning3.dpf` from the sample data folder. Record the build identifier and the dataset used for each run.

### Scenario 1: Handles appear for eligible planning endpoints

1. Open the selected planning dataset in a plot editor.
2. Identify the visible planning segments and their visible last fixes.
3. Switch to a non-Drag-Component mode and verify no endpoint handles are shown.
4. Activate Drag Component from the toolbar or shortcut.
5. Verify that each visible planning-segment end has one high-contrast outlined handle with a contrasting center, at least 8 screen pixels wide and high.
6. Verify ordinary track points and non-planning plot elements have no handle.

Expected result:
- Matches the eligibility and visual contracts.

### Scenario 2: Handle maps to the existing drag behavior

1. With Drag Component active, choose a marked planning-segment end.
2. Hover it and verify the existing point-hit cursor appears and the handle becomes emphasized.
3. Drag it to a new bearing and release.
4. Verify the planning leg recalculates as it did before the feature.
5. Verify undo and redo still reverse and reapply the same drag.

Expected result:
- The handle improves recognition without changing target selection, hit tolerance, course snapping, recalculation, undo, or redo.

### Scenario 3: Visibility and endpoint edge cases

1. Hide a planning segment and verify its handle disappears.
2. Hide an endpoint fix, if the UI/data path supports this state, and verify its handle is absent.
3. Open a dataset containing a closing segment and verify its endpoint follows the same rule as other planning segments.
4. Load a plot with no planning track and verify no handles are shown.

Expected result:
- No handle implies drag availability for hidden, invalid, or absent candidates.

### Scenario 4: Zoom, pan, overlap, and mode transitions

1. With Drag Component active, zoom in and out across the planning track.
2. Pan across the track and verify handles remain centered on current endpoint locations.
3. Inspect closely spaced or overlapping endpoints. Verify handles do not alter which endpoint the existing nearest-hit logic selects.
4. Switch rapidly between Drag Component and another tool several times.
5. Verify handles disappear outside Drag Component and do not remain after a chart is closed.

Expected result:
- Handles track current projection and lifecycle state without stale overlays or cross-chart leakage.

### Scenario 5: Multiple chart editors

1. Open the same or different planning datasets in two chart editors.
2. Activate Drag Component and confirm both editors show their own eligible handles.
3. Hover or drag an endpoint in one editor.
4. Verify the other editor's marker positions, hover state, and data remain independent.
5. Close one editor and confirm the remaining editor continues to render and interact normally.

Expected result:
- Marker state and cleanup are local to each chart editor.

## Usability Measurement Protocol

Use the current build as the baseline and the feature build as the comparison. Use the same dataset, task order, and instructions for both builds where practical.

- Recruit at least 5 analysts or representative users.
- Give each participant 10 representative planning-leg edit tasks.
- For each task, record time from Drag Component activation to the first correct drag.
- Count attempts to drag an unsupported point before the first correct drag.
- Ask each participant to rate endpoint discoverability from 1 (very unclear) to 5 (very clear).
- Report the median time, unsupported-target attempt rate, percentage identifying a handle within 5 seconds, and percentage rating discoverability 4 or 5.

## Completion Criteria

- All manual scenarios pass without regressions to existing drag behavior.
- The sample-data run confirms 100% of eligible visible endpoints are marked and no ineligible elements are marked.
- The usability results can be compared with the current-build baseline and evaluated against SC-001 through SC-004 in `spec.md`.
- Observed behavior satisfies `contracts/drag-component-affordance.md`.
