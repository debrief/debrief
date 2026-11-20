# Data Model: Draggable Track Point Affordance

**Date**: 2026-09-04
**Spec**: [spec.md](./spec.md)

## Existing Domain Entities

### 1) PlanningTrack

- Purpose: User-created planning track made of ordered planning legs.
- Backing type: `Debrief.Wrappers.CompositeTrackWrapper`.
- Key fields and behavior:
  - `visible`
  - Ordered `PlanningSegment` children
  - `origin` and `startDate` used during recalculation
- Eligibility rules:
  - The track must be visible.
  - The track must be traversed by the existing Drag Component hit-test path.
  - No separate persisted lock state is introduced by this feature.

### 2) PlanningSegment

- Purpose: One leg inside a planning track with computed fixes.
- Backing type: `Debrief.Wrappers.Track.PlanningSegment`.
- Key fields and behavior:
  - `visible`
  - Ordered fixes, including `firstFix` and `lastFix`
  - Course, speed, distance, duration, and depth used to recalculate the leg
- Eligibility rules:
  - The segment must be visible.
  - The segment must have a valid last fix.
  - The endpoint must be projectable in the current chart to be rendered in that frame.
  - Closing segments follow the same rule as other planning segments.

## Runtime Derived Entities

### 3) DraggableEndpointCandidate

- Purpose: Runtime representation of one planning-segment end that the Drag Component can target.
- Backing source: A new non-cursor-dependent enumeration helper in `CompositeTrackWrapper`, sharing eligibility with `findNearestHotSpotIn(...)`.
- Key fields:
  - `parentTrack`: `CompositeTrackWrapper`
  - `parentSegment`: `PlanningSegment`
  - `worldLocation`: endpoint location used by the existing `shift(WorldLocation, WorldVector)` path
  - `eligible`: derived from current visibility and valid-endpoint rules
- Invariants:
  - Candidate identity must remain aligned with the object used by drag apply and undo.
  - Candidates are not persisted.
  - Ordinary tracks and unrelated plottables are not candidates for this feature.

### 4) DragAffordanceMarker

- Purpose: Runtime visual handle for one eligible endpoint.
- Lifecycle: Created and rendered per chart editor; never persisted.
- Key fields:
  - `candidate`
  - `screenPoint`, derived from the chart projection during repaint
  - `visualState`: `available`, `hovered`, or `dragging`
  - `visible`
- Visual invariants:
  - Available markers use a high-contrast outline with a contrasting center.
  - The marker is at least 8 screen pixels wide and high.
  - Hovered and dragging markers use a stronger visual treatment.
  - Marker meaning does not rely on color alone.

### 5) DragComponentChartState

- Purpose: Per-chart runtime state for Drag Component interaction and affordance rendering.
- Backing behavior: Owned by the chart-specific mode/overlay lifecycle associated with `DragComponent.DragComponentMode`.
- Key fields:
  - `chart`
  - `canvas`
  - `modeActive`
  - `eligibleCandidates`
  - `hoverTarget`
  - `hoverComponent`
  - `isDragging`
  - `paintListener` or equivalent repaint registration
- Invariants:
  - State is not shared between chart editors.
  - State is cleared when the mode is replaced or the chart/canvas is disposed.
  - A chart with no planning track has no markers.

## Relationships

- One `PlanningTrack` has many `PlanningSegment` entries.
- Each eligible `PlanningSegment` contributes one `DraggableEndpointCandidate` for its last fix.
- Each candidate maps to one marker per active chart editor, unless its screen coordinate is unavailable for the current frame.
- `DragComponentChartState` controls marker rendering and hover/drag visual states for one chart.

## State Transitions

| State | Trigger | Next State | Expected Result |
|-------|---------|------------|-----------------|
| Inactive | User activates Drag Component | Armed | Each chart builds current eligible candidates and renders endpoint handles. |
| Armed | Pointer enters an eligible endpoint hit zone | Hovered | Existing hit cursor is shown and the matching handle is emphasized. |
| Hovered | Mouse down and drag begins | Dragging | Existing drag preview and endpoint movement begin. |
| Dragging | Mouse up (commit) | Armed | Existing undoable action commits and markers refresh from current geometry. |
| Armed/Hovered/Dragging | User leaves Drag Component mode | Inactive | Handles and per-chart listeners/state are removed. |
| Any active state | Chart/canvas disposed | Disposed | Listeners and chart-local marker state are released without affecting other charts. |

## Derived Data and Invariants

- Marker visibility is derived from `(Drag Component active AND planning track visible AND segment visible AND valid last fix AND projectable in current chart frame)`.
- Screen coordinates are derived at render time from the current chart projection.
- Marker rendering does not modify planning-track persistence data.
- Affordance presence must not alter hit tolerance, candidate selection, course snapping, recalculation, undo, or redo.
