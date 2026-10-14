# UI Contract: Drag Component Planning-Point Affordance

**Date**: 2026-09-04
**Spec**: [../spec.md](../spec.md)
**Data Model**: [../data-model.md](../data-model.md)

## Contract Scope

Defines user-visible behavior for identifying eligible planning-segment ends while using Drag Component mode.

## Eligibility Contract

An endpoint handle is rendered only when all conditions are true:

1. Drag Component mode is active.
2. The candidate belongs to a visible `CompositeTrackWrapper` planning track.
3. The planning segment is visible.
4. The segment has a valid last fix.
5. The candidate follows the same endpoint eligibility predicate as the existing planning-track Drag Component hit test.

The current model has no separate lock state. This feature does not add one. If a future editability rule prevents a candidate from being returned by the Drag Component hit-test path, the same rule must prevent its handle from being rendered.

## Visual Contract

- Each eligible endpoint has one centered endpoint handle.
- The available handle is an outlined marker with a contrasting center, at least 8 screen pixels wide and high.
- The marker must remain distinguishable against the planning track, map, and nearby labels without relying on color alone.
- The hovered endpoint uses a stronger outline/fill treatment than available endpoints.
- The actively dragged endpoint remains visually identified during drag preview and commit.
- Handles must not add text labels or alter existing track labels.
- If eligible endpoints are visually close or overlap, rendering must not change which candidate the existing nearest-hit logic selects.

## Preconditions

1. A plot editor is open.
2. A visible planning track is loaded.
3. Drag Component mode is active.

## Inputs and Events

| Event | Source | Contracted Behavior |
|-------|--------|---------------------|
| Mode activated (`DragComponent`) | User command, toolbar, or shortcut | Each chart evaluates eligible planning endpoints and renders handles. |
| Mode deactivated (any other mode) | User command, toolbar, or shortcut | Each chart removes its handles and chart-local listeners/state. |
| Cursor moved | Mouse movement | Existing nearest-hit logic determines the hovered candidate; its matching handle is emphasized. |
| Drag started on endpoint | Mouse down and move | Existing drag preview and endpoint movement starts; no new hit tolerance is introduced. |
| Drag committed | Mouse up | Existing undoable drag action commits; handles refresh from recalculated geometry. |
| Track or segment visibility changes | Data/model update | Handles refresh to include only currently eligible endpoints. |
| Endpoint geometry changes | Drag/recalculation/model update | Handles refresh from the latest world locations. |
| Zoom or pan changes | Viewport update | Handles are projected again and remain aligned with in-view endpoints. |
| Chart/canvas disposal | Workbench lifecycle | The chart releases its handle state/listeners without affecting other charts. |

## Output Guarantees

1. Every eligible visible planning-segment end has a corresponding handle in each active chart editor.
2. Ordinary track points and non-planning plot elements are not marked by this feature.
3. Hidden segments, invalid endpoints, and absent planning tracks produce no handles. The feature does not introduce a separate endpoint-visibility rule beyond the existing hit-test eligibility.
4. Existing Drag Component behavior, including cursor transitions, hit tolerance, drag mechanics, course snapping, recalculation, undo, and redo, is preserved.
5. Handles do not persist outside Drag Component mode or into REP/DPF data.
6. Marker state in one chart cannot alter marker state, hover state, or disposal behavior in another chart.

## Error and Edge Behavior

- If no planning track is loaded, no handles are shown and no error is raised.
- If a screen coordinate cannot be produced for an endpoint in the current frame, that handle is skipped for the frame.
- If two candidates project to the same location, both remain eligible in the model and existing nearest-hit ordering remains authoritative; the renderer must not invent a new target.
- If Drag Component is replaced while a drag is active, the old chart-local listener/state is released and the existing drag lifecycle is not left attached to the canvas.

## Non-Functional Contract

- Changes caused by mode, visibility, geometry, zoom, pan, or disposal are reflected by the next visible repaint or lifecycle update.
- Handle rendering must not introduce noticeable lag during normal planning-leg drag operations.
- The marker must remain discoverable at normal working zoom levels and in the supplied planning-track sample data.

## Acceptance Mapping

- FR-001/FR-002/FR-005/FR-007/FR-009: Eligibility and marker presence are limited to valid planning endpoints.
- FR-003: Available, hovered, and dragging visual states are distinct.
- FR-004/FR-006: Mode and viewport/model transitions update marker visibility and positions.
- FR-008: Existing drag execution semantics remain unchanged.
