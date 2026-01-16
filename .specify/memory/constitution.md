<!--
============================================================================
SYNC IMPACT REPORT
============================================================================
Version change: 0.0.0 → 1.0.0 (initial ratification)
Modified principles: N/A (initial)
Added sections:
  - Core Principles (5 principles derived from docs/constitution.md)
  - Content Boundaries
  - Methodology
  - Governance
Removed sections: N/A (initial)
Templates requiring updates:
  - .specify/templates/plan-template.md: ✅ compatible (no changes needed)
  - .specify/templates/spec-template.md: ✅ compatible (no changes needed)
  - .specify/templates/tasks-template.md: ✅ compatible (no changes needed)
Follow-up TODOs: None
============================================================================
-->

# Legacy Debrief LLM Documentation Project Constitution

## Core Principles

### I. Navigability Over Comprehensiveness

Documentation exists to help an LLM find and understand the right code, not to catalogue everything.

**Rationale**: The goal is enabling emergency hotfixes and knowledge extraction, not exhaustive coverage. An LLM that can quickly locate relevant code is more valuable than one with access to complete but unnavigable documentation.

### II. Signal Over Ceremony

Prefer concise, direct statements. Every sentence MUST help the reader act.

**Rules**:
- Use consistent terminology throughout; ambiguity in naming creates ambiguity in understanding
- Answer "where do I start?" before explaining internals
- Strip filler words and hedging language

### III. Logic and Algorithms Are Primary

Computational behaviour MUST be documented for safe modification and faithful porting.

**Content boundaries**:
- Spatial rendering requires fidelity—the map plot MUST be reproduced accurately in future Debrief
- UI is EXCLUDED—future Debrief has an entirely new interface; documenting widgets has no value
- Eclipse RCP framework patterns are EXCLUDED—not needed for hotfixes, won't be reimplemented

### IV. Explore Before Asking

Attempt to understand from code inspection first. Interviews fill gaps, not replace investigation.

**Methodology**:
- State confidence explicitly: distinguish verified-from-code vs inferred-from-context vs uncertain
- NEVER invent—if behaviour is unclear, say so; guessing causes more harm than admitting ignorance
- Batch questions, minimise interruption—collect uncertainties per module, then conduct focused interviews

### V. Document What Is, Not What Should Be

The code has worked for 25 years. Describe it accurately; save opinions for DebriefNG.

**Rules**:
- Capture the "why"—design rationale and historical context are as valuable as behaviour descriptions
- Non-destructive exploration—documentation work does NOT modify source code
- Accuracy over completeness—incomplete documentation can be extended; incorrect documentation causes harm

## Content Boundaries

The following content types are IN SCOPE:
- Logic and algorithms (computational behaviour)
- Spatial rendering (map plot, canvas operations)
- Data structures and domain models
- File format parsing and generation
- Design rationale and historical context

The following content types are OUT OF SCOPE:
- UI widgets, dialogs, Eclipse views (future Debrief has new interface)
- Eclipse RCP patterns (not needed for hotfixes)
- Framework boilerplate (OSGi, extension points)

## Methodology

### Confidence Levels

All documentation MUST use explicit confidence markers:

| Marker | Meaning |
|--------|---------|
| **VERIFIED** | Confirmed by code inspection or test execution |
| **INFERRED** | Deduced from context, naming, or patterns |
| **UNCERTAIN** | Requires review; behaviour not fully understood |

### Interview Protocol

1. Exhaust code-based investigation first
2. Collect uncertainties per module
3. Batch questions into focused interview sessions
4. Document answers with source attribution

## Governance

This constitution governs all documentation work on the Legacy Debrief LLM Documentation Project.

**Amendment procedure**:
1. Propose amendment with rationale
2. Document in amendment log below
3. Increment version per semantic versioning rules

**Versioning policy**:
- MAJOR: Backward-incompatible principle removal or redefinition
- MINOR: New principle or section added
- PATCH: Clarifications, wording, typo fixes

**Compliance**: All documentation PRs MUST verify alignment with these principles before merge.

### Amendment Log

| Date | Version | Amendment | Rationale |
|------|---------|-----------|-----------|
| 2026-01-09 | 1.0.0 | Initial ratification | Derived from docs/constitution.md |

**Version**: 1.0.0 | **Ratified**: 2026-01-09 | **Last Amended**: 2026-01-09
