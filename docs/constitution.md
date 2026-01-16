# Constitution: Legacy Debrief LLM Documentation Project

## Purpose

Establish governing principles for creating documentation that makes the legacy Debrief codebase navigable by LLMs for emergency hotfixes and knowledge extraction.

---

## Documentation Quality

**Navigability over comprehensiveness**: Documentation exists to help an LLM find and understand the right code, not to catalogue everything.

**Signal over ceremony**: Prefer concise, direct statements. Every sentence should help the reader act.

**Consistent terminology**: Use domain terms identically throughout. Ambiguity in naming creates ambiguity in understanding.

**Entry points before internals**: Always answer "where do I start?" before explaining how things work inside.

---

## Content Boundaries

**Logic and algorithms are primary**: Computational behaviour must be understood for safe modification and faithful porting.

**Spatial rendering requires fidelity**: The map plot must be reproduced accurately in future Debrief; rendering documentation is detailed.

**UI is excluded**: Future Debrief has an entirely new interface. Documenting widgets and dialogs has no value.

**Framework is excluded**: Eclipse RCP patterns are not needed for hotfixes and won't be reimplemented.

---

## Methodology

**Explore before asking**: Attempt to understand from code inspection first. Interview fills gaps, not replaces investigation.

**State confidence explicitly**: Distinguish what is verified from code, inferred from context, or uncertain and needing review.

**Never invent**: If behaviour is unclear, say so. Guessing causes more harm than admitting ignorance.

**Batch questions, minimise interruption**: Collect uncertainties per module, then conduct focused interviews.

---

## Legacy Codebase Respect

**Document what is, not what should be**: The code has worked for 25 years. Describe it accurately; save opinions for DebriefNG.

**Capture the why**: Design rationale and historical context are as valuable as behaviour descriptions.

**Non-destructive exploration**: Documentation work does not modify source code.

**Accuracy over completeness**: Incomplete documentation can be extended; incorrect documentation causes harm.

---

## Amendments

This constitution may be amended by agreement. Log amendments with date and rationale.

| Date | Amendment | Rationale |
|------|-----------|-----------|
| | | |
