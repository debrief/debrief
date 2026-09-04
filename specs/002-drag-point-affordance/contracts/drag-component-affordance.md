# UI Contract: Drag Component Planning-Point Affordance

**Date**: 2026-09-04
**Spec**: [../spec.md](../spec.md)
**Data Model**: [../data-model.md](../data-model.md)

## Contract Scope

Defines user-visible behavior for identifying draggable planning-track points while using Drag Component mode.

## Preconditions

1. A plot editor is open.
2. A planning track is loaded and visible.
3. Drag Component mode is active.

## Inputs and Events

| Event | Source | Contracted Behavior |
|-------|--------|---------------------|
| Mode activated (`DragComponent`) | User command/toolbar/shortcut | System evaluates current planning-track draggable endpoints and renders affordances. |
| Mode deactivated (any other mode) | User command/toolbar/shortcut | System removes all drag-point affordances immediately on next repaint. |
| Cursor moved | Mouse movement | Hovered endpoint state updates; cursor and marker emphasis stay synchronized. |
| Drag started on endpoint | Mouse down + move | Existing drag preview/behavior starts; endpoint remains visually identified during drag. |
| Drag committed | Mouse up | Drag action commits through existing undoable action flow; affordances refresh against new geometry. |
| Track visibility/editability changed | Data/model update | Affordance set refreshes to include only currently draggable points. |
| Zoom/pan changed | Viewport update | Affordances remain visible and correctly positioned for in-view draggable points. |

## Output Guarantees

1. Only points that are currently draggable on planning tracks are marked.
2. Non-draggable points and non-planning-track points are not marked by this feature.
3. Markers are visible enough to distinguish drag targets without requiring trial drag.
4. Existing Drag Component behaviors (cursor changes, drag mechanics, undo/redo, recalculation) are preserved.
5. Affordances never persist outside Drag Component mode.

## Error and Edge Behavior

- If no planning tracks are loaded, no affordances are shown and no error is raised.
- If a track/segment becomes non-editable, associated affordances are removed in the next repaint cycle.
- If endpoint conversion to screen coordinates fails (off-screen/invalid), marker is skipped for that frame.

## Non-Functional Contract

- Affordance changes are reflected by the next visible repaint after mode, pointer, or model state changes.
- Affordance rendering must not introduce noticeable lag during normal planning-leg drag operations.

## Acceptance Mapping

- FR-001/FR-002/FR-007/FR-009: Marker presence is limited to valid draggable planning points.
- FR-003/FR-006: Marker clarity and visibility support rapid recognition at normal zoom/pan levels.
- FR-004/FR-005: Mode and editability transitions update affordance visibility.
- FR-008: Drag execution semantics remain unchanged.
