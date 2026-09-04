# Feature Specification: Draggable Track Point Affordance

**Feature Branch**: `002-drag-point-affordance`  
**Created**: 2026-09-04  
**Status**: Draft  
**Input**: User description: "we allow users to create planning tracks. Once loaded, they can use the \"Drag Component\" tool to drag the legs. We should offer an extra UI affordance on the draggable track points - to make it easier for the user to recognise which points can be dragged."

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Recognize Draggable Points Quickly (Priority: P1)

As an analyst editing a planning track, I can immediately recognize which track points are draggable when using the Drag Component tool, so I can adjust legs without trial and error.

**Why this priority**: This is the core usability problem and directly affects whether users can perform leg-editing efficiently.

**Independent Test**: Can be fully tested by loading a planning track, activating Drag Component, and confirming users can identify draggable points and move a leg without guesswork.

**Acceptance Scenarios**:

1. **Given** a planning track is loaded and editable, **When** the user activates Drag Component, **Then** every draggable track point is shown with a distinct visual cue.
2. **Given** Drag Component is active, **When** the user attempts to edit a leg, **Then** they can identify the correct draggable point before starting the drag action.

---

### User Story 2 - Avoid Confusion With Non-Draggable Elements (Priority: P2)

As an analyst, I can distinguish draggable planning-track points from non-draggable points or other visual elements, so I do not waste time trying to drag unsupported targets.

**Why this priority**: Preventing misclicks and false drag attempts reduces user frustration and avoids accidental workflow interruptions.

**Independent Test**: Can be tested by presenting a mix of draggable and non-draggable points and verifying users only attempt drag actions on valid points.

**Acceptance Scenarios**:

1. **Given** mixed editable and non-editable elements are visible, **When** Drag Component is active, **Then** only draggable planning-track points display the draggable affordance.
2. **Given** a point is not draggable, **When** the user views it in edit mode, **Then** it does not appear as a draggable target.

---

### User Story 3 - Preserve Visibility Across Normal Working Conditions (Priority: P3)

As an analyst, I can still recognize draggable points while zooming and panning around planning tracks, so the affordance remains useful throughout normal editing workflows.

**Why this priority**: The feature must remain practical in real-world map usage, not only at one zoom level or static view.

**Independent Test**: Can be tested by changing zoom levels and map view while Drag Component is active and confirming draggable points remain identifiable.

**Acceptance Scenarios**:

1. **Given** Drag Component is active, **When** the user zooms in or out within supported editing views, **Then** draggable points remain visibly distinguishable from surrounding content.

---

### Edge Cases

- Planning tracks with very closely spaced points must still allow users to identify which points can be dragged.
- If a planning track or segment is locked/non-editable, drag affordances must not imply that dragging is available.
- If the user switches tools rapidly (for example from Drag Component to another tool), draggable affordances must update to match the currently active tool state.
- When no planning track is loaded, no draggable-point affordance should be shown.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: The system MUST display a clear visual affordance on planning-track points that are draggable when Drag Component is active.
- **FR-002**: The system MUST apply the affordance only to points that can currently be dragged.
- **FR-003**: Users MUST be able to distinguish draggable planning-track points from non-draggable points without performing a trial drag.
- **FR-004**: The system MUST remove or suppress draggable-point affordances when Drag Component is not active.
- **FR-005**: The system MUST update draggable-point affordances immediately when editability changes (for example due to lock/unlock state).
- **FR-006**: The system MUST keep draggable-point affordances visible enough to recognize during typical zoom and pan interactions used for planning-track editing.
- **FR-007**: The system MUST avoid signaling drag availability for tracks or points that cannot be modified.
- **FR-008**: The system MUST preserve existing drag behavior for planning-track leg editing while adding the new affordance.
- **FR-009**: The system MUST provide consistent draggable affordance behavior each time a planning track is loaded in an editable state.

### Key Entities *(include if feature involves data)*

- **Planning Track**: A user-created track used for planning, composed of legs and points that may be editable.
- **Track Point**: A position marker within a planning track; may be draggable or non-draggable depending on current tool and editability state.
- **Drag Component Tool State**: The active editing mode that determines whether dragging interactions and their visual affordances are shown.
- **Editability State**: The condition that determines whether a planning track or point can be modified at a given moment.

### Assumptions

- The feature applies to planning tracks only, not all track types.
- The Drag Component tool already exists and remains the primary interaction for moving planning-track legs.
- Existing visual styling standards for the application remain in place, and this feature adds to them rather than redefining them.
- Affordance visibility expectations are based on standard operator display settings used for routine Debrief analysis.

### Dependencies

- Accurate identification of planning-track points that are currently draggable.
- Reliable detection of active tool mode and editability changes.
- Existing planning-track loading and leg-drag workflows remaining available.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: In usability validation, at least 90% of users correctly identify a draggable planning-track point within 5 seconds of activating Drag Component.
- **SC-002**: In representative editing tasks, median time to begin a correct leg drag is reduced by at least 30% compared with the current behavior.
- **SC-003**: Misattempts to drag non-draggable points decrease by at least 50% during observed planning-track editing sessions.
- **SC-004**: At least 85% of users rate draggable-point discoverability as clear or very clear after completing a standard planning-track edit task.
