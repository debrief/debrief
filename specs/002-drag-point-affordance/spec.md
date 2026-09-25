# Feature Specification: Draggable Track Point Affordance

**Feature Branch**: `002-drag-point-affordance`  
**Created**: 2026-09-04  
**Status**: Draft  
**Input**: User description: "we allow users to create planning tracks. Once loaded, they can use the \"Drag Component\" tool to drag the legs. We should offer an extra UI affordance on the draggable track points - to make it easier for the user to recognise which points can be dragged."

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Recognize Draggable Leg Ends Quickly (Priority: P1)

As an analyst editing a planning track, I can immediately recognize which leg ends are draggable when using the Drag Component tool, so I can adjust leg courses without trial and error.

**Why this priority**: This is the core usability problem and directly affects whether users can perform leg-editing efficiently.

**Independent Test**: Can be fully tested by loading a planning track, activating Drag Component, and confirming users can identify an eligible leg-end handle and move a leg without guesswork.

**Acceptance Scenarios**:

1. **Given** a visible planning track is loaded, **When** the user activates Drag Component, **Then** every currently draggable visible leg end is shown with a distinct endpoint handle.
2. **Given** Drag Component is active, **When** the user attempts to edit a leg, **Then** the user can identify the leg end handle before starting the drag action and the existing drag interaction remains unchanged.

---

### User Story 2 - Avoid Confusion With Non-Draggable Elements (Priority: P2)

As an analyst, I can distinguish draggable planning-track leg ends from ordinary track points and other visual elements, so I do not waste time trying to drag unsupported targets.

**Why this priority**: Preventing misclicks and false drag attempts reduces user frustration and avoids accidental workflow interruptions.

**Independent Test**: Can be tested by presenting planning and ordinary tracks together and verifying handles appear only on eligible planning-segment ends.

**Acceptance Scenarios**:

1. **Given** planning and ordinary tracks are visible together, **When** Drag Component is active, **Then** only eligible planning-track leg ends display the endpoint handle.
2. **Given** a planning segment or endpoint is hidden or has no valid endpoint, **When** the user views it in Drag Component mode, **Then** it does not display an endpoint handle.

---

### User Story 3 - Preserve Visibility Across Normal Working Conditions (Priority: P3)

As an analyst, I can still recognize draggable leg ends while zooming and panning around planning tracks, so the affordance remains useful throughout normal editing workflows.

**Why this priority**: The feature must remain practical in real-world map usage, not only at one zoom level or static view.

**Independent Test**: Can be tested by changing zoom levels and map view while Drag Component is active and confirming endpoint handles remain aligned and identifiable.

**Acceptance Scenarios**:

1. **Given** Drag Component is active, **When** the user zooms in or out within supported editing views, **Then** eligible leg-end handles remain aligned and visibly distinguishable from surrounding content.

---

### Edge Cases

- Planning tracks with very closely spaced points must still allow users to identify which points can be dragged.
- The current model has no separate lock state. Eligibility must follow the same visibility and endpoint rules used by the existing Drag Component hit test; introducing a new lock model is out of scope.
- Closing segments are planning segments and must follow the same endpoint rule as other planning segments.
- If two eligible endpoints project to the same or nearly the same screen location, the affordance must not change which candidate the existing hit test selects.
- If the user switches tools rapidly (for example from Drag Component to another tool), draggable affordances must update to match the currently active tool state.
- When no planning track is loaded, no endpoint handle should be shown.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: The system MUST display an endpoint handle centered on each eligible planning-segment end when Drag Component is active.
- **FR-002**: The endpoint handle MUST be a high-contrast outlined marker with a contrasting center, have a minimum visible size of 8 screen pixels in both dimensions, and MUST NOT rely on color alone to communicate drag availability.
- **FR-003**: The system MUST use a visually stronger variant of the endpoint handle while the pointer is over the corresponding draggable endpoint and while that endpoint is being dragged.
- **FR-004**: The system MUST remove or suppress draggable-point affordances when Drag Component is not active.
- **FR-005**: The system MUST derive endpoint-handle eligibility from visible planning segments with a valid last fix, using the same eligibility rules as the existing planning-track Drag Component hit test.
- **FR-006**: The system MUST refresh endpoint handles on the next visible repaint after planning-track visibility, endpoint geometry, mode, zoom, or pan changes.
- **FR-007**: The system MUST avoid signaling drag availability for ordinary tracks, non-planning plot elements, hidden planning segments, or hidden/invalid endpoints.
- **FR-008**: The system MUST preserve existing Drag Component behavior, including hit tolerance, course snapping, planning-leg recalculation, undo, and redo.
- **FR-009**: The system MUST keep affordance rendering state local to each chart editor; one editor's markers, hover state, or disposal MUST NOT affect another editor.

### Key Entities *(include if feature involves data)*

- **Planning Track**: A user-created track used for planning, composed of legs and points that may be editable.
- **Track Point**: A position marker within a planning track. For this feature, only the last valid fix of a visible planning segment is an affordance candidate; screen visibility is determined by the current chart projection.
- **Drag Component Tool State**: The active editing mode that determines whether dragging interactions and their visual affordances are shown.
- **Endpoint Eligibility**: The existing runtime condition that a planning segment is visible and has a valid last fix that the Drag Component hit-test path can modify. No separate persisted lock state or new endpoint-visibility rule is introduced by this feature.

### Assumptions

- The feature applies to planning tracks only, not all track types.
- The Drag Component tool already exists and remains the primary interaction for moving planning-track legs.
- Existing visual styling standards for the application remain in place, and this feature adds to them rather than redefining them.
- Affordance visibility expectations are based on standard operator display settings used for routine Debrief analysis.

### Dependencies

- Accurate identification of planning-segment endpoints that are currently draggable.
- Reliable detection of active tool mode, visibility, geometry, zoom, and pan changes.
- Existing planning-track loading and leg-drag workflows remaining available.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: In the representative planning-track sample, 100% of eligible visible leg ends have exactly one visible endpoint handle when Drag Component is active, and no ineligible plot elements have one.
- **SC-002**: In a usability evaluation of at least 5 analysts completing 10 representative leg-edit tasks each, at least 90% identify a correct endpoint handle within 5 seconds of activating Drag Component.
- **SC-003**: Against the current build baseline using the same task script, the median time from Drag Component activation to the first correct leg drag decreases by at least 30%.
- **SC-004**: In the same evaluation, at least 85% of participants rate endpoint discoverability as clear or very clear, and observed attempts to drag unsupported targets decrease by at least 50% against baseline.
