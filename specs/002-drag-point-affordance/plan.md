# Implementation Plan: Draggable Track Point Affordance

**Branch**: `002-drag-point-affordance` | **Date**: 2026-09-04 | **Spec**: [spec.md](./spec.md)
**Input**: Feature specification from `/specs/002-drag-point-affordance/spec.md`

## Summary

Add a high-contrast endpoint handle for each eligible visible planning-segment end when the Drag Component tool is active, so users can identify valid drag targets before interacting. The implementation will share endpoint eligibility with existing planning-leg hotspot discovery, add a mode-scoped rendering layer, and keep marker state local to each chart editor without changing file formats or core drag semantics.

## Technical Context

**Language/Version**: Java 11 (Tycho-built Eclipse RCP plugins)  
**Primary Dependencies**: `org.mwc.debrief.core` drag actions, `org.mwc.debrief.legacy` planning-track wrappers, `org.mwc.cmap.plotViewer` SWT chart canvas and drag mode lifecycle  
**Storage**: Existing in-memory track model with current REP/DPF persistence; no storage schema changes  
**Testing**: Tycho JUnit suites (`org.mwc.debrief.test2`), RCPTT UI suites (`org.mwc.debrief.ui_test`), plus manual scenario validation with sample planning tracks  
**Target Platform**: Desktop Eclipse RCP application (Windows/Linux/macOS)  
**Project Type**: Multi-module desktop rich-client application (plugin architecture)  
**Performance Goals**: Affordance visibility updates by next repaint during mode/hover/view changes and no perceptible slowdown during standard planning-leg dragging  
**Constraints**: Show handles only for visible planning-segment ends eligible under the existing Drag Component hit-test rules, only in Drag Component mode, preserve hit tolerance/undo/redo/recalculation, and avoid shared mutable marker state across chart editors
**Scale/Scope**: Planning charts with many visible legs (targeting normal analyst workloads) and synchronized mode behavior across open chart editors

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

Scope note: `.specify/memory/constitution.md` governs this repository's documentation work. For this product feature plan, principles are applied to planning rigor, evidence quality, and scope control.

### Pre-Phase 0 Gate Review

| Principle | Status | Notes |
|-----------|--------|-------|
| I. Navigability Over Comprehensiveness | PASS | Plan and artifacts point to exact modules and methods that control draggable planning points. |
| II. Signal Over Ceremony | PASS | Deliverables focus on actionable behavior and acceptance expectations. |
| III. Logic and Algorithms Are Primary | PASS | Work is centered on existing drag eligibility and spatial rendering behavior, not generic UI boilerplate. |
| IV. Explore Before Asking | PASS | Code inspection completed for drag mode flow, hotspot discovery, and planning segment handling. |
| V. Document What Is, Not What Should Be | PASS | Existing endpoint and visibility behavior is recorded; the new handle is explicitly bounded as proposed behavior. |

### Post-Phase 1 Gate Re-Check

| Principle | Status | Notes |
|-----------|--------|-------|
| I. Navigability Over Comprehensiveness | PASS | `research.md`, `data-model.md`, `quickstart.md`, and `contracts/` link implementation touchpoints and validation paths. |
| II. Signal Over Ceremony | PASS | Contract and validation steps remain concise and test-focused. |
| III. Logic and Algorithms Are Primary | PASS | Design preserves existing planning-leg hit testing, course recalculation, and drag mechanics while adding a spatial marker layer. |
| IV. Explore Before Asking | PASS | Eligibility, marker appearance, endpoint scope, and per-editor lifecycle are resolved from code inspection and explicit design decisions. |
| V. Document What Is, Not What Should Be | PASS | Existing behavior is separated from proposed handle rendering; no unsupported lock model is assumed. |

## Project Structure

### Documentation (this feature)

```text
specs/002-drag-point-affordance/
├── plan.md
├── research.md
├── data-model.md
├── quickstart.md
├── contracts/
│   └── drag-component-affordance.md
└── tasks.md
```

### Source Code (repository root)

```text
org.mwc.debrief.core/
└── src/org/mwc/debrief/core/actions/
    ├── DragComponent.java            # Drag mode, endpoint handles, and hover/drag paint behavior
    └── drag/CoreDragOperation.java   # Existing drag-operation base; preserve lifecycle assumptions

org.mwc.debrief.legacy/
└── src/Debrief/Wrappers/
    ├── CompositeTrackWrapper.java    # Planning-leg draggable endpoint discovery
    └── Track/PlanningSegment.java    # Planning-leg endpoint semantics and recalc context

org.mwc.cmap.legacy/
└── src/MWC/GUI/Shapes/
    └── HasDraggableComponents.java   # Draggable component contract (reference for compatibility)

org.mwc.cmap.plotViewer/
└── src/org/mwc/cmap/plotViewer/
    ├── actions/CoreDragAction.java   # Shared mode switch; must not share marker state between charts
    └── editors/chart/SWTChart.java   # Chart mode assignment and repaint lifecycle

org.mwc.debrief.ui_test/
└── test-cases/                       # Optional UI automation extension for affordance validation

org.mwc.debrief.test2/
└── src/org/mwc/debrief/test/
    └── AllTests.java                 # Existing suite used for regression confidence
```

**Structure Decision**: Add a planning-endpoint enumeration path beside the existing nearest-endpoint hit test, reuse the same eligibility predicate for both paths, and render handles through per-chart Drag Component state. Update shared mode switching only as needed to guarantee per-editor ownership. Add UI automation only for observable lifecycle behavior; keep exact marker geometry and eligibility checks covered by manual/automated validation close to the owning modules.

## Complexity Tracking

No constitution violations requiring mitigation.
