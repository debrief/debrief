---

description: "Task list for implementing draggable planning-track point affordances"
---

# Tasks: Draggable Track Point Affordance

**Input**: Design documents from `/specs/002-drag-point-affordance/`
**Prerequisites**: plan.md (required), spec.md (required), research.md, data-model.md, contracts/, quickstart.md

**Tests**: No TDD-first requirement was explicitly requested in the specification, so this plan uses implementation tasks plus scenario validation tasks.

**Organization**: Tasks are grouped by user story so each story remains independently implementable and independently verifiable.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Task can run in parallel (different files, no dependency on unfinished tasks)
- **[Story]**: User story label (`[US1]`, `[US2]`, `[US3]`) used only in story phases
- Every task includes at least one concrete repository file path

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Prepare feature-local implementation and validation scaffolding.

- [ ] T001 Capture implementation checkpoints and acceptance traceability notes in `specs/002-drag-point-affordance/plan.md`
- [ ] T002 Confirm validation scenarios, endpoint rules, and usability measurements in `specs/002-drag-point-affordance/quickstart.md`
- [ ] T003 [P] Add an RCPTT chart-level scenario for Drag Component mode transitions in `org.mwc.debrief.ui_test/test-cases/toolbar/drag_component_affordance_visibility.test`

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Build shared drag-affordance infrastructure required by all stories.

**CRITICAL**: Complete this phase before starting any user story tasks.

- [ ] T004 Define per-chart affordance state and disposal ownership in `org.mwc.debrief.core/src/org/mwc/debrief/core/actions/DragComponent.java` and `org.mwc.cmap.plotViewer/src/org/mwc/cmap/plotViewer/actions/CoreDragAction.java`
- [ ] T005 [P] Add a non-cursor-dependent planning-endpoint enumeration helper in `org.mwc.debrief.legacy/src/Debrief/Wrappers/CompositeTrackWrapper.java` for visible segments with valid last fixes
- [ ] T006 [P] Refactor or share endpoint eligibility between enumeration and nearest-hit logic in `org.mwc.debrief.legacy/src/Debrief/Wrappers/CompositeTrackWrapper.java`
- [ ] T007 Wire per-chart repaint invalidation and cleanup for mode, visibility, geometry, zoom, pan, and canvas disposal in `org.mwc.debrief.core/src/org/mwc/debrief/core/actions/DragComponent.java`, `org.mwc.cmap.plotViewer/src/org/mwc/cmap/plotViewer/editors/chart/SWTChart.java`, and `org.mwc.cmap.plotViewer/src/org/mwc/cmap/plotViewer/actions/CoreDragAction.java`

**Checkpoint**: Foundation complete; user stories can now be implemented.

---

## Phase 3: User Story 1 - Recognize Draggable Leg Ends Quickly (Priority: P1) MVP

**Goal**: Users can immediately identify eligible planning-segment ends when Drag Component mode is active.

**Independent Test**: Load a planning track, activate Drag Component, and verify every eligible visible segment end has a high-contrast endpoint handle before any drag starts.

### Implementation for User Story 1

- [ ] T008 [US1] Render an outlined endpoint handle with contrasting center and minimum 8-pixel screen size for each eligible endpoint in `org.mwc.debrief.core/src/org/mwc/debrief/core/actions/DragComponent.java`
- [ ] T009 [US1] Add stronger hovered and actively dragged handle states synchronized with existing `_hoverComponent` and drag state in `org.mwc.debrief.core/src/org/mwc/debrief/core/actions/DragComponent.java`
- [ ] T010 [US1] Preserve endpoint handle visibility and existing drag semantics during preview, commit, undo, and redo in `org.mwc.debrief.core/src/org/mwc/debrief/core/actions/DragComponent.java`
- [ ] T011 [US1] Guard marker rendering for off-screen/invalid coordinate conversions and avoid color-only communication in `org.mwc.debrief.core/src/org/mwc/debrief/core/actions/DragComponent.java`
- [ ] T012 [P] [US1] Update user-facing Drag Component guidance to describe draggable-point affordances in `org.mwc.debrief.help/docbook/ng_help.xml`
- [ ] T013 [US1] Execute US1 sample-data validation, count eligible versus rendered endpoints, and record evidence in `specs/002-drag-point-affordance/quickstart.md`

**Checkpoint**: User Story 1 is complete and independently verifiable.

---

## Phase 4: User Story 2 - Avoid Confusion With Non-Draggable Elements (Priority: P2)

**Goal**: Users can distinguish eligible planning-segment ends from ordinary track points and unrelated chart elements.

**Independent Test**: With planning and ordinary tracks visible together, verify only eligible planning-segment ends show handles and hidden/invalid endpoints do not.

### Implementation for User Story 2

- [ ] T014 [US2] Restrict marker candidate enumeration to `CompositeTrackWrapper` planning endpoints in `org.mwc.debrief.core/src/org/mwc/debrief/core/actions/DragComponent.java`
- [ ] T015 [US2] Exclude hidden segments, empty segments, and invalid screen locations without adding a new endpoint-visibility rule in `org.mwc.debrief.legacy/src/Debrief/Wrappers/CompositeTrackWrapper.java` and `org.mwc.debrief.core/src/org/mwc/debrief/core/actions/DragComponent.java`
- [ ] T016 [US2] Ensure ordinary `TrackWrapper` content and other plottables do not receive endpoint handles in `org.mwc.debrief.core/src/org/mwc/debrief/core/actions/DragComponent.java`
- [ ] T017 [US2] Preserve existing nearest-hit and cursor behavior for overlapping candidates without introducing a new hit target in `org.mwc.debrief.core/src/org/mwc/debrief/core/actions/DragComponent.java`
- [ ] T018 [US2] Execute US2 visibility, closing-segment, overlap, and false-positive validation and record evidence in `specs/002-drag-point-affordance/quickstart.md`

**Checkpoint**: User Story 2 is complete and independently verifiable.

---

## Phase 5: User Story 3 - Preserve Visibility Across Normal Working Conditions (Priority: P3)

**Goal**: Affordances remain useful during zoom, pan, and rapid tool switching.

**Independent Test**: While Drag Component is active, zoom/pan and switch tools repeatedly; verify affordances stay aligned when active and disappear when inactive.

### Implementation for User Story 3

- [ ] T019 [US3] Recompute handle screen geometry from current projection on repaint so markers stay aligned through zoom/pan in `org.mwc.debrief.core/src/org/mwc/debrief/core/actions/DragComponent.java`
- [ ] T020 [US3] Clear per-chart handle state and listeners on mode switch by extending drag-mode lifecycle cleanup in `org.mwc.debrief.core/src/org/mwc/debrief/core/actions/DragComponent.java` and `org.mwc.cmap.plotViewer/src/org/mwc/cmap/plotViewer/actions/CoreDragAction.java`
- [ ] T021 [US3] Harden redraw and disposal paths for rapid tool switching, null editors, and disposed canvases in `org.mwc.debrief.core/src/org/mwc/debrief/core/actions/DragComponent.java` and `org.mwc.cmap.plotViewer/src/org/mwc/cmap/plotViewer/editors/chart/SWTChart.java`
- [ ] T022 [US3] Verify that shared mode switching creates or maintains independent chart-local handle state across open editors in `org.mwc.cmap.plotViewer/src/org/mwc/cmap/plotViewer/actions/CoreDragAction.java`
- [ ] T023 [US3] Execute US3 zoom, pan, rapid-switch, disposal, and multiple-editor validation and record evidence in `specs/002-drag-point-affordance/quickstart.md`

**Checkpoint**: User Story 3 is complete and independently verifiable.

---

## Phase 6: Polish & Cross-Cutting Concerns

**Purpose**: Final hardening, regression coverage, and release-readiness evidence.

- [ ] T024 [P] Add RCPTT assertions for Drag Component mode transitions and chart lifecycle in `org.mwc.debrief.ui_test/test-cases/toolbar/drag_component_affordance_visibility.test`
- [ ] T025 [P] Add or extend planning-endpoint eligibility regression coverage in `org.mwc.debrief.legacy/src/Debrief/Wrappers/Track/TrackWrapper_Test.java`
- [ ] T026 Run the full quickstart validation protocol, including usability measurements, and update evidence links in `specs/002-drag-point-affordance/quickstart.md` and `specs/002-drag-point-affordance/contracts/drag-component-affordance.md`

---

## Dependencies & Execution Order

### Phase Dependencies

- Phase 1 -> Phase 2 -> Phase 3/4/5 -> Phase 6
- User stories may begin only after Phase 2 completes

### User Story Dependency Graph

```text
Foundation (Phase 2)
    |
    +--> US1 (P1)
    |
    +--> US2 (P2)
    |
    +--> US3 (P3)

Recommended delivery order: US1 -> US2 -> US3
```

### Task-Level Dependencies

- T004-T007 must complete before T008-T023
- T008-T011 should complete before T013
- T014-T017 should complete before T018
- T019-T022 should complete before T023
- T024-T026 run after selected user stories are complete

---

## Parallel Opportunities

- **Setup**: T003 can run in parallel with T001-T002
- **Foundational**: T005 and T006 can run in parallel, then merge into T007
- **US1**: T012 can run in parallel with T008-T011
- **Polish**: T024 and T025 can run in parallel before T026

## Parallel Example: User Story 1

```bash
Task: "T008 [US1] Render concrete endpoint handles in org.mwc.debrief.core/src/org/mwc/debrief/core/actions/DragComponent.java"
Task: "T012 [US1] Update Drag Component guidance in org.mwc.debrief.help/docbook/ng_help.xml"
```

## Parallel Example: User Story 2

```bash
Task: "T015 [US2] Exclude hidden or invalid planning endpoints in org.mwc.debrief.legacy/src/Debrief/Wrappers/CompositeTrackWrapper.java"
Task: "T016 [US2] Exclude non-planning plottables in org.mwc.debrief.core/src/org/mwc/debrief/core/actions/DragComponent.java"
```

## Parallel Example: User Story 3

```bash
Task: "T020 [US3] Clear affordance state on mode switch in org.mwc.debrief.core/src/org/mwc/debrief/core/actions/DragComponent.java"
Task: "T022 [US3] Verify cross-editor consistency in org.mwc.cmap.plotViewer/src/org/mwc/cmap/plotViewer/actions/CoreDragAction.java"
```

---

## Implementation Strategy

### MVP First (User Story 1)

1. Complete Phase 1 and Phase 2
2. Deliver Phase 3 (US1) and run T013 validation
3. Demo/review affordance discoverability improvement before expanding scope

### Incremental Delivery

1. Deliver US1 (discoverability baseline)
2. Deliver US2 (false-positive prevention)
3. Deliver US3 (zoom/pan and mode-switch resilience)
4. Complete polish tasks and final validation evidence

### Team Parallelization Strategy

1. One developer handles `CompositeTrackWrapper` endpoint exposure (T005/T015)
2. One developer handles Drag Component overlay and lifecycle tasks (T004/T006/T007/T008-T011/T014/T016-T021)
3. One developer handles docs and UI automation (T003/T012/T024/T026)

---

## Notes

- [P] tasks are limited to work that can proceed without waiting on unfinished same-file edits.
- Story labels map implementation work directly to `spec.md` user stories for traceability.
- Validation tasks reference `quickstart.md` so each story can be demonstrated independently.
