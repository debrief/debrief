# Research: Draggable Track Point Affordance

**Date**: 2026-09-04
**Spec**: [spec.md](./spec.md)

## Research Outcomes

### Decision 1: Scope affordances to planning-track draggable endpoints only

- Decision: Show affordances only for draggable points exposed by planning tracks when Drag Component mode is active.
- Rationale: User need is to identify draggable planning-leg points; broader scope risks signaling drag support where none exists.
- Alternatives considered:
  - Show affordances for all track points in all drag modes (rejected: high false-positive risk).
  - Show affordances only after hover (rejected: does not solve pre-hover discoverability).

### Decision 2: Reuse existing hotspot source of truth

- Decision: Use existing draggable hotspot generation from `Debrief.Wrappers.CompositeTrackWrapper.findNearestHotSpotIn(...)` as the source of draggable endpoints.
- Rationale: This method already defines which planning-leg endpoints are draggable and how drag updates propagate to segment course/recalculation.
- Alternatives considered:
  - Derive draggable points from all visible fixes (rejected: not equivalent to true drag targets).
  - Maintain a parallel draggable index (rejected: duplicate logic and drift risk).

### Decision 3: Render affordances as mode-scoped overlay, not persisted model data

- Decision: Render draggable-point affordances in Drag Component mode via chart repaint/overlay behavior rather than adding persistent entities to planning-track data.
- Rationale: Affordance is interaction state, not domain state; this avoids file format and model persistence impact.
- Alternatives considered:
  - Add new flags/attributes to planning segments (rejected: unnecessary persistence coupling).
  - Always paint affordances from track rendering code (rejected: cannot respect active tool state cleanly).

### Decision 4: Keep hit testing and drag mechanics unchanged

- Decision: Preserve existing jitter threshold checks, cursor transitions, drag action creation, undo/redo, and planning-leg recalc behavior.
- Rationale: Requirement FR-008 requires no regression in existing drag behavior.
- Alternatives considered:
  - Increase hit radius to match affordance size (rejected: behavior change beyond scope).
  - Introduce alternative drag target semantics (rejected: would alter established workflows).

### Decision 5: Define explicit visual state contract

- Decision: Use distinct visual states for available, hovered, and active-drag targets while keeping non-draggable points unmarked.
- Rationale: Clarifies what can be dragged and reinforces interaction feedback without extra clicks.
- Alternatives considered:
  - Cursor-only feedback (rejected: discoverability remains weak).
  - Text labels/tooltips on every point (rejected: chart clutter and poorer scanability).

### Decision 6: Validate with mixed automated and manual checks

- Decision: Combine existing Tycho/RCPTT suites with manual planning-track scenarios using provided sample data.
- Rationale: Existing automation covers command availability and data import; manual runs confirm in-chart affordance visibility and behavior under zoom/pan.
- Alternatives considered:
  - Manual-only validation (rejected: insufficient regression confidence).
  - New full end-to-end UI automation before implementation (rejected: useful but not required to begin feature work).

## Key Evidence from Code Inspection

- Drag Component mode and hover target assignment are implemented in `org.mwc.debrief.core/src/org/mwc/debrief/core/actions/DragComponent.java`.
- Planning-leg draggable endpoints are exposed in `org.mwc.debrief.legacy/src/Debrief/Wrappers/CompositeTrackWrapper.java`.
- Draggable component contracts are defined in `org.mwc.cmap.legacy/src/MWC/GUI/Shapes/HasDraggableComponents.java`.
- Command/toolbar and keyboard wiring for Drag Component are in `org.mwc.debrief.core/plugin.xml` and `org.mwc.debrief.core/src/org/mwc/debrief/core/actions/RadioHandler.java`.
- Sample planning datasets for validation are under `org.mwc.cmap.combined.feature/root_installs/sample_data/`.

## Clarification Status

All Technical Context clarifications are resolved; no `NEEDS CLARIFICATION` items remain.
