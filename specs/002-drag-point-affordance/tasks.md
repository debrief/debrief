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
- [ ] T002 Confirm validation scenarios and expected outcomes are current in `specs/002-drag-point-affordance/quickstart.md`
- [ ] T003 [P] Create RCPTT placeholder scenario file `org.mwc.debrief.ui_test/test-cases/toolbar/drag_component_affordance_visibility.test` aligned to `specs/002-drag-point-affordance/quickstart.md`

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Build shared drag-affordance infrastructure required by all stories.

**CRITICAL**: Complete this phase before starting any user story tasks.

- [ ] T004 Add runtime affordance state fields and lifecycle reset hooks in `org.mwc.debrief.core/src/org/mwc/debrief/core/actions/DragComponent.java`
- [ ] T005 [P] Add planning-endpoint collection helper(s) for affordance rendering in `org.mwc.debrief.legacy/src/Debrief/Wrappers/CompositeTrackWrapper.java`
- [ ] T006 [P] Add Drag Component helper methods that derive eligible affordance candidates from layers and mode state in `org.mwc.debrief.core/src/org/mwc/debrief/core/actions/DragComponent.java`
- [ ] T007 Wire repaint invalidation for affordance updates on mode entry/exit and pointer movement in `org.mwc.debrief.core/src/org/mwc/debrief/core/actions/DragComponent.java`

**Checkpoint**: Foundation complete; user stories can now be implemented.

---

## Phase 3: User Story 1 - Recognize Draggable Points Quickly (Priority: P1) MVP

**Goal**: Users can immediately identify draggable planning-track points when Drag Component mode is active.

**Independent Test**: Load a planning track, activate Drag Component, and verify draggable endpoints are visibly marked before any drag starts.

### Implementation for User Story 1

- [ ] T008 [US1] Render default draggable-point affordance markers for eligible planning endpoints in `org.mwc.debrief.core/src/org/mwc/debrief/core/actions/DragComponent.java`
- [ ] T009 [US1] Add hover-state marker emphasis synchronized with `_hoverComponent` updates in `org.mwc.debrief.core/src/org/mwc/debrief/core/actions/DragComponent.java`
- [ ] T010 [US1] Preserve visible affordance feedback during active drag preview/commit flow in `org.mwc.debrief.core/src/org/mwc/debrief/core/actions/DragComponent.java`
- [ ] T011 [US1] Guard marker rendering for off-screen/invalid coordinate conversions in `org.mwc.debrief.core/src/org/mwc/debrief/core/actions/DragComponent.java`
- [ ] T012 [P] [US1] Update user-facing Drag Component guidance to describe draggable-point affordances in `org.mwc.debrief.help/docbook/ng_help.xml`
- [ ] T013 [US1] Execute US1 scenario validation and record pass/fail notes in `specs/002-drag-point-affordance/quickstart.md`

**Checkpoint**: User Story 1 is complete and independently verifiable.

---

## Phase 4: User Story 2 - Avoid Confusion With Non-Draggable Elements (Priority: P2)

**Goal**: Users can distinguish draggable planning points from non-draggable points and unrelated chart elements.

**Independent Test**: With mixed visible content, verify only truly draggable planning points show affordances and non-draggable targets never appear draggable.

### Implementation for User Story 2

- [ ] T014 [US2] Restrict affordance candidate enumeration to planning-track draggable endpoints only in `org.mwc.debrief.core/src/org/mwc/debrief/core/actions/DragComponent.java`
- [ ] T015 [US2] Exclude hidden or non-editable planning segments from affordance output in `org.mwc.debrief.legacy/src/Debrief/Wrappers/CompositeTrackWrapper.java` and `org.mwc.debrief.core/src/org/mwc/debrief/core/actions/DragComponent.java`
- [ ] T016 [US2] Ensure non-planning `TrackWrapper` content and other plottables do not receive affordance markers in `org.mwc.debrief.core/src/org/mwc/debrief/core/actions/DragComponent.java`
- [ ] T017 [US2] Align hit-cursor transitions with affordance eligibility so false targets never show point-hit cursor in `org.mwc.debrief.core/src/org/mwc/debrief/core/actions/DragComponent.java`
- [ ] T018 [US2] Execute US2 scenario validation and record false-positive checks in `specs/002-drag-point-affordance/quickstart.md`

**Checkpoint**: User Story 2 is complete and independently verifiable.

---

## Phase 5: User Story 3 - Preserve Visibility Across Normal Working Conditions (Priority: P3)

**Goal**: Affordances remain useful during zoom, pan, and rapid tool switching.

**Independent Test**: While Drag Component is active, zoom/pan and switch tools repeatedly; verify affordances stay aligned when active and disappear when inactive.

### Implementation for User Story 3

- [ ] T019 [US3] Recompute affordance screen geometry on each repaint so markers stay aligned through zoom/pan in `org.mwc.debrief.core/src/org/mwc/debrief/core/actions/DragComponent.java`
- [ ] T020 [US3] Clear affordance state on mode switch by extending drag-mode lifecycle cleanup in `org.mwc.debrief.core/src/org/mwc/debrief/core/actions/DragComponent.java`
- [ ] T021 [US3] Harden drag affordance redraw paths for rapid tool switching and null/disposing editor states in `org.mwc.debrief.core/src/org/mwc/debrief/core/actions/DragComponent.java`
- [ ] T022 [US3] Verify cross-editor consistency of Drag Component affordance behavior with shared mode switching flow in `org.mwc.cmap.plotViewer/src/org/mwc/cmap/plotViewer/actions/CoreDragAction.java` and `org.mwc.debrief.core/src/org/mwc/debrief/core/actions/DragComponent.java`
- [ ] T023 [US3] Execute US3 scenario validation and record zoom/pan and mode-transition outcomes in `specs/002-drag-point-affordance/quickstart.md`

**Checkpoint**: User Story 3 is complete and independently verifiable.

---

## Phase 6: Polish & Cross-Cutting Concerns

**Purpose**: Final hardening, regression coverage, and release-readiness evidence.

- [ ] T024 [P] Add RCPTT assertions for affordance visibility lifecycle in `org.mwc.debrief.ui_test/test-cases/toolbar/drag_component_affordance_visibility.test`
- [ ] T025 [P] Add or extend planning-track drag regression coverage in `org.mwc.debrief.legacy/src/Debrief/Wrappers/Track/TrackWrapper_Test.java`
- [ ] T026 Run full quickstart validation pass and update final evidence links in `specs/002-drag-point-affordance/quickstart.md` and `specs/002-drag-point-affordance/contracts/drag-component-affordance.md`

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
Task: "T008 [US1] Render default draggable-point affordance markers in org.mwc.debrief.core/src/org/mwc/debrief/core/actions/DragComponent.java"
Task: "T012 [US1] Update Drag Component guidance in org.mwc.debrief.help/docbook/ng_help.xml"
```

## Parallel Example: User Story 2

```bash
Task: "T015 [US2] Exclude hidden or non-editable planning segments in org.mwc.debrief.legacy/src/Debrief/Wrappers/CompositeTrackWrapper.java"
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
