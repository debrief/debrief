# Data Model: Draggable Track Point Affordance

**Date**: 2026-09-04
**Spec**: [spec.md](./spec.md)

## Entities

### 1) PlanningTrack

- Purpose: User-created planning track made of ordered planning legs.
- Backing type: `Debrief.Wrappers.CompositeTrackWrapper`.
- Key fields:
  - `name`
  - `visible`
  - `segments` (ordered list of `PlanningSegment`)
  - `origin` and `startDate` (used during recalc)
- Validation rules:
  - Affordances are eligible only when track is visible and currently editable.
  - No affordance must be shown when no planning track is available.

### 2) PlanningSegment

- Purpose: One leg inside a planning track with computed fixes.
- Backing type: `Debrief.Wrappers.Track.PlanningSegment`.
- Key fields:
  - `course`
  - `speed`
  - `distance`
  - `duration`
  - `depth`
  - `visible`
  - `firstFix` and `lastFix`
- Validation rules:
  - Draggable endpoint is the segment `lastFix` location used by existing hotspot logic.
  - Hidden/non-editable segments must not produce draggable affordances.

### 3) DraggableEndpointCandidate

- Purpose: Runtime representation of one planning-segment point that can be dragged.
- Backing source: Derived from existing hotspot logic in `CompositeTrackWrapper.findNearestHotSpotIn(...)`.
- Key fields:
  - `parentTrack` (PlanningTrack reference)
  - `parentSegment` (PlanningSegment reference)
  - `worldLocation` (drag-target location)
  - `isDraggable` (true only when current mode and editability permit)
- Validation rules:
  - Candidate set includes only points that can be dragged now.
  - Candidate identity must stay aligned with the object used by drag apply/undo.

### 4) DragAffordanceMarker

- Purpose: Visual indicator rendered for a draggable endpoint.
- Lifecycle: Runtime-only; not persisted.
- Key fields:
  - `endpoint` (DraggableEndpointCandidate reference)
  - `screenPoint`
  - `visualState` (`available`, `hovered`, `dragging`)
  - `visible`
- Validation rules:
  - Markers appear only while Drag Component mode is active.
  - Non-draggable points never receive markers.
  - Marker updates must track zoom/pan and mode changes.

### 5) DragComponentInteractionState

- Purpose: Runtime state of Drag Component interaction and rendering.
- Backing type: `DragComponent.DragComponentMode`.
- Key fields:
  - `modeActive`
  - `hoverTarget`
  - `hoverComponent`
  - `isDragging`
  - `lastPointerPosition`
- Validation rules:
  - Entering/exiting mode toggles affordance visibility consistently.
  - Drag commit must preserve existing undo/redo behavior.

## Relationships

- One `PlanningTrack` has many `PlanningSegment` entries.
- Each `PlanningSegment` contributes zero or one current `DraggableEndpointCandidate`.
- Each `DraggableEndpointCandidate` maps to zero or one visible `DragAffordanceMarker` at any repaint.
- `DragComponentInteractionState` controls whether markers are rendered and which marker is in `hovered` or `dragging` state.

## State Transitions

| State | Trigger | Next State | Expected Result |
|-------|---------|------------|-----------------|
| Inactive | User activates Drag Component | Armed | Draggable markers become visible for eligible planning points. |
| Armed | Pointer enters draggable endpoint hit zone | Hovered | Hover feedback updates cursor and marker emphasis. |
| Hovered | Mouse down and drag begins | Dragging | Existing drag preview/movement begins for selected endpoint. |
| Dragging | Mouse up (commit) | Armed | Drag action committed; markers refresh for recalculated geometry. |
| Armed/Hovered/Dragging | User leaves Drag Component mode | Inactive | All draggable-point affordances are removed. |

## Derived Data and Invariants

- Draggable affordance visibility is derived from `(modeActive AND endpoint.isDraggable AND endpoint is visible in viewport)`.
- Marker rendering does not modify planning-track persistence data.
- Affordance presence must not alter drag targeting, course snapping, or recalc outputs.
