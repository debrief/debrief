# Implementation Plan: Draggable Track Point Affordance

**Branch**: `002-drag-point-affordance` | **Date**: 2026-09-04 | **Spec**: [spec.md](./spec.md)
**Input**: Feature specification from `/specs/002-drag-point-affordance/spec.md`

## Summary

Add a clear visual affordance for draggable planning-track points when the Drag Component tool is active, so users can quickly identify valid drag targets before interacting. The implementation will reuse existing planning-leg hotspot discovery and drag mechanics, adding a mode-scoped rendering layer for affordance markers without changing file formats or core drag semantics.

## Technical Context

**Language/Version**: Java 11 (Tycho-built Eclipse RCP plugins)  
**Primary Dependencies**: `org.mwc.debrief.core` drag actions, `org.mwc.debrief.legacy` planning-track wrappers, `org.mwc.cmap.plotViewer` SWT chart canvas and drag mode lifecycle  
**Storage**: Existing in-memory track model with current REP/DPF persistence; no storage schema changes  
**Testing**: Tycho JUnit suites (`org.mwc.debrief.test2`), RCPTT UI suites (`org.mwc.debrief.ui_test`), plus manual scenario validation with sample planning tracks  
**Target Platform**: Desktop Eclipse RCP application (Windows/Linux/macOS)  
**Project Type**: Multi-module desktop rich-client application (plugin architecture)  
**Performance Goals**: Affordance visibility updates by next repaint during mode/hover/view changes and no perceptible slowdown during standard planning-leg dragging  
**Constraints**: Show affordances only for actually draggable planning-track points, only in Drag Component mode, preserve undo/redo and planning-leg recalculation behavior  
**Scale/Scope**: Planning charts with many visible legs (targeting normal analyst workloads) and synchronized mode behavior across open chart editors

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

Scope note: `.specify/memory/constitution.md` governs this repository's documentation work. For this product feature plan, principles are applied to planning rigor, evidence quality, and scope control.

### Pre-Phase 0 Gate Review

| Principle | Status | Notes |
|-----------|--------|-------|
| I. Navigability Over Comprehensiveness | PASS | Plan and artifacts point to exact modules and methods that control draggable planning points. |
| II. Signal Over Ceremony | PASS | Deliverables focus on actionable behavior and acceptance expectations. |
| III. Logic and Algorithms Are Primary | PASS | Work is centered on existing drag logic and spatial rendering behavior, not generic UI boilerplate. |
| IV. Explore Before Asking | PASS | Code inspection completed for drag mode flow, hotspot discovery, and planning segment handling. |
| V. Document What Is, Not What Should Be | PASS | Design reuses existing drag contracts and only adds affordance signaling around current behavior. |

### Post-Phase 1 Gate Re-Check

| Principle | Status | Notes |
|-----------|--------|-------|
| I. Navigability Over Comprehensiveness | PASS | `research.md`, `data-model.md`, `quickstart.md`, and `contracts/` link implementation touchpoints and validation paths. |
| II. Signal Over Ceremony | PASS | Contract and validation steps remain concise and test-focused. |
| III. Logic and Algorithms Are Primary | PASS | Design preserves existing planning-leg drag mechanics and course recalculation logic. |
| IV. Explore Before Asking | PASS | No unresolved clarifications remain after repository research. |
| V. Document What Is, Not What Should Be | PASS | Proposed behavior is bounded to measurable affordance outcomes from the approved spec. |

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
    └── DragComponent.java            # Drag mode lifecycle and drag-component hover/paint behavior

org.mwc.debrief.legacy/
└── src/Debrief/Wrappers/
    ├── CompositeTrackWrapper.java    # Planning-leg draggable endpoint discovery
    └── Track/PlanningSegment.java    # Planning-leg endpoint semantics and recalc context

org.mwc.cmap.legacy/
└── src/MWC/GUI/Shapes/
    └── HasDraggableComponents.java   # Draggable component contract (reference for compatibility)

org.mwc.debrief.ui_test/
└── test-cases/                       # Optional UI automation extension for affordance validation

org.mwc.debrief.test2/
└── src/org/mwc/debrief/test/
    └── AllTests.java                 # Existing suite used for regression confidence
```

**Structure Decision**: Implement within existing drag action and planning wrapper modules to keep behavior close to current hotspot detection and drag execution paths, with optional UI automation additions in the existing RCPTT test area.

## Complexity Tracking

No constitution violations requiring mitigation.
