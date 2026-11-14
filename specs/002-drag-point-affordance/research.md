# Research: Draggable Track Point Affordance

**Date**: 2026-09-04
**Spec**: [spec.md](./spec.md)

## Research Outcomes

### Decision 1: Scope affordances to visible planning-segment ends only

- Decision: Show one endpoint handle for each visible planning segment with a valid last fix when Drag Component mode is active; skip the handle for a frame if the current chart cannot project its location.
- Rationale: The current planning hotspot path uses each visible segment's last fix as the drag component. Broader point coverage would signal targets that the tool does not currently expose, while adding a new fix-visibility rule could change existing drag behavior.
- Alternatives considered:
  - Show affordances for all track points in all drag modes (rejected: high false-positive risk).
  - Show affordances only after hover (rejected: does not solve pre-hover discoverability).
  - Add a separate lock/editability model (rejected: no such model exists in the current planning-track data and it is outside this feature scope).

### Decision 2: Share eligibility, add enumeration

- Decision: Add a non-cursor-dependent endpoint enumeration helper in `CompositeTrackWrapper`, and make the existing nearest-hotspot path use the same endpoint/visibility predicate where practical.
- Rationale: `findNearestHotSpotIn(...)` is cursor-dependent and returns only the nearest candidate. It is authoritative for current drag semantics, but cannot by itself render all handles. Sharing the eligibility predicate prevents the marker list and hit test from drifting.
- Alternatives considered:
  - Derive draggable points from all fixes (rejected: not equivalent to true drag targets).
  - Maintain a separate parallel eligibility implementation (rejected: duplicate logic and drift risk).

### Decision 3: Render affordances as mode-scoped, non-persisted handles

- Decision: Render endpoint handles in Drag Component mode through chart repaint/overlay behavior rather than adding persistent entities to planning-track data.
- Rationale: Affordance is interaction state, not domain state; this avoids file format and model persistence impact.
- Alternatives considered:
  - Add new flags or attributes to planning segments (rejected: unnecessary persistence coupling).
  - Always paint affordances from track rendering code (rejected: cannot respect active tool state cleanly).

### Decision 4: Use a concrete endpoint handle visual

- Decision: Render a high-contrast outlined endpoint handle with a contrasting center, at least 8 screen pixels in both dimensions. Use a stronger outline/fill variant for the hovered or actively dragged endpoint, and do not rely on color alone.
- Rationale: A concrete shape, size, and contrast rule makes the discoverability objective testable while remaining independent of track colors and labels.
- Alternatives considered:
  - Color-only markers (rejected: weak against varied track colors and inaccessible for color-impaired users).
  - Text labels or tooltips on every point (rejected: chart clutter and poorer scanability).

### Decision 5: Keep hit testing and drag mechanics unchanged

- Decision: Preserve existing jitter threshold checks, cursor transitions, drag action creation, undo/redo, course snapping, and planning-leg recalculation.
- Rationale: The feature improves recognition without changing the established editing behavior.
- Alternatives considered:
  - Increase hit radius to match handle size (rejected: behavior change beyond scope).
  - Introduce alternative drag target semantics (rejected: would alter established workflows).

### Decision 6: Own rendering state per chart editor

- Decision: Keep marker collections, canvas references, hover state, and repaint listeners local to each chart editor. A shared drag-mode object must not contain mutable state that identifies one chart's markers as another chart's markers.
- Rationale: `CoreDragAction.SwitchModeAction` currently installs the same mode instance across open chart editors, while `DragComponentMode` stores canvas and hover state on the mode. Per-editor ownership avoids stale markers, cross-editor leakage, and disposal bugs.
- Alternatives considered:
  - Store one global marker collection in `PlotViewerPlugin` (rejected: chart projections and canvas lifecycles differ).
  - Attach one shared paint listener to all canvases (rejected: listener cleanup and state ownership become ambiguous).

### Decision 7: Render from current geometry on repaint

- Decision: Convert current eligible endpoint locations to screen coordinates during repaint or an equivalent invalidated render pass; skip endpoints that cannot produce a screen coordinate for that frame.
- Rationale: This keeps handles aligned after zoom, pan, recalculation, and drag operations without persisting screen coordinates.
- Alternatives considered:
  - Cache screen points until mouse movement (rejected: stale after viewport changes).
  - Store screen coordinates in the planning model (rejected: screen coordinates are view-specific).

### Decision 8: Validate with observable checks and a defined usability protocol

- Decision: Combine existing Tycho/RCPTT suites with manual planning-track scenarios and a small before/after usability protocol using the provided sample data.
- Rationale: Existing automation covers command availability and data import; visual marker presence, endpoint mapping, zoom/pan alignment, and discoverability require chart-level validation. The protocol makes the quantitative success criteria reproducible.
- Alternatives considered:
  - Manual-only validation (rejected: insufficient regression confidence).
  - Toolbar-only UI automation (rejected: it cannot validate chart affordances).

## Key Evidence from Code Inspection

- Drag Component mode and hover target assignment are implemented in `org.mwc.debrief.core/src/org/mwc/debrief/core/actions/DragComponent.java`.
- Planning-leg draggable endpoints are exposed by the cursor-dependent path in `org.mwc.debrief.legacy/src/Debrief/Wrappers/CompositeTrackWrapper.java`; a non-cursor-dependent enumeration path is required for all-marker rendering.
- The nearest search traverses any visible `HasDraggableComponents` in `org.mwc.cmap.legacy/src/MWC/GUI/Shapes/FindNearest.java`; the new marker path must explicitly restrict itself to planning tracks.
- Draggable component contracts are defined in `org.mwc.cmap.legacy/src/MWC/GUI/Shapes/HasDraggableComponents.java`.
- Shared drag-mode installation occurs in `org.mwc.cmap.plotViewer/src/org/mwc/cmap/plotViewer/actions/CoreDragAction.java`, and chart mode assignment occurs in `org.mwc.cmap.plotViewer/src/org/mwc/cmap/plotViewer/editors/chart/SWTChart.java`.
- Command/toolbar and keyboard wiring for Drag Component are in `org.mwc.debrief.core/plugin.xml` and `org.mwc.debrief.core/src/org/mwc/debrief/core/actions/RadioHandler.java`.
- Sample planning datasets for validation are under `org.mwc.cmap.combined.feature/root_installs/sample_data/`.

## Clarification Status

All Technical Context clarifications are resolved. The current model has no separate lock state; this feature uses existing visibility and endpoint eligibility rules instead of inventing one.
