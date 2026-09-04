# Quickstart: Validate Draggable Planning-Point Affordance

**Date**: 2026-09-04
**Spec**: [spec.md](./spec.md)
**Contract**: [contracts/drag-component-affordance.md](./contracts/drag-component-affordance.md)
**Data Model**: [data-model.md](./data-model.md)

## Prerequisites

- Java 11 and Maven available.
- Debrief workspace builds successfully.
- Sample planning datasets available under `org.mwc.cmap.combined.feature/root_installs/sample_data/`.

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

Use either `planning_tracks.rep` or `Demo/TrialsPlanning/TrialsPlanning3.dpf` from the sample data folder.

### Scenario 1: Affordance appears only in Drag Component mode

1. Open plot editor with planning track data loaded.
2. Switch to a non-drag-component mode.
3. Verify no draggable-point affordances are shown.
4. Activate Drag Component (toolbar item or shortcut).
5. Verify draggable planning points become visibly marked.

Expected result:
- Matches contract preconditions and output guarantees 1, 2, and 5.

### Scenario 2: Users can identify the correct drag target before dragging

1. With Drag Component active, inspect a planning track with multiple legs.
2. Identify a marked draggable endpoint.
3. Start drag on that endpoint and adjust leg geometry.

Expected result:
- Marked endpoint is the point that drags.
- Drag behavior remains normal and leg recalculation occurs as before.

### Scenario 3: Non-draggable elements are not mis-signaled

1. Keep Drag Component active.
2. Attempt to locate affordances on non-planning-track points or non-draggable points.

Expected result:
- No false affordances appear on unsupported points.

### Scenario 4: Affordance stability during zoom and pan

1. With Drag Component active, zoom in/out and pan across the planning track.
2. Verify in-view draggable endpoints remain marked and visually distinct.

Expected result:
- Marker positions stay aligned with draggable points and remain visible enough for recognition.

### Scenario 5: Undo/redo and mode transitions

1. Drag a planning-leg endpoint and commit.
2. Run undo, then redo.
3. Switch away from Drag Component and back again.

Expected result:
- Undo/redo behavior is unchanged.
- Affordances disappear outside mode and reappear when mode is reactivated.

## Completion Criteria

- All scenarios pass without regressions to existing drag behavior.
- Observed behavior satisfies `contracts/drag-component-affordance.md` and FR-001 through FR-009 in `spec.md`.
