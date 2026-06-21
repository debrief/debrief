# Feature Specification: Document Debrief Algorithms and Tools for Migration

**Feature Branch**: `001-document-debrief-algorithms`
**Created**: 2026-02-07
**Status**: Draft
**Input**: User description: "Follow the guidance of the files in the debrief-future docs/tool-migration folder (and sub-folders) to document the algorithms and tools in Debrief, ready for re-implementation in debrief-future."

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Discover and Inventory All Migrateable Tools (Priority: P1)

A migration engineer needs a complete catalogue of every tool and algorithm in the legacy Debrief codebase that is a candidate for re-implementation in debrief-future. The engineer reviews the legacy Java source across four primary package roots (`org.mwc.debrief.core`, `org.mwc.debrief.track_shift`, `org.mwc.cmap.plotViewer`, and `Debrief/`) and produces a structured discovery report. Each tool entry includes its name, category, originating class, complexity rating, legacy trigger type (how users invoke it in Eclipse RCP), selection context, whether it shows intermediate UI, and a one-line description. The report also provides a UX integration mapping table showing how each legacy trigger type maps to Future Debrief surfaces and flags gaps that need new UX design.

**Why this priority**: Without a complete inventory, there is no reliable basis for planning, prioritising, or tracking the migration. This is the foundational deliverable that all subsequent phases depend on.

**Independent Test**: Can be fully tested by verifying the discovery report contains an entry for every tool-bearing class found via a scan of the four package roots, and that every entry has all required columns populated. Delivers a single authoritative inventory that the team can review and triage.

**Acceptance Scenarios**:

1. **Given** the legacy Debrief codebase, **When** the migration engineer scans all four package roots for classes matching tool identification patterns (classes ending in Tool/Action/Analyzer/Calculator/Operation, classes implementing IAction/AbstractAction, and classes with algorithmic method signatures), **Then** a discovery report is produced at `_tool-migration/discovery-report.md` listing every identified tool with all required columns: Name, Category, Java Class, Complexity, Legacy Trigger, Selection Context, Has Intermediate UI, Description, and Status.
2. **Given** the completed inventory, **When** each tool is assessed for complexity, **Then** every tool is rated Low, Medium, or High based on five factors: algorithm sophistication, dependencies, state management, I/O shape, and UI coupling.
3. **Given** the completed inventory, **When** the trigger type summary is compiled, **Then** the report includes a count of tools per legacy trigger type and a UX integration mapping table that maps each trigger type to Future Debrief surfaces (MCP/LLM Tool, VS Code Command, Webview Panel, Context Menu) and explicitly flags gaps (especially drag-drop and wizard).
4. **Given** the completed inventory, **When** triage is complete, **Then** every tool is marked as Ready, Needs Review, or Out of Scope, and a summary section lists tools grouped by status.

---

### User Story 2 - Capture Golden Input/Output Examples (Priority: P2)

A migration engineer needs to capture the exact input and output behaviour of each legacy tool as JSON fixture pairs (golden I/O files). For each tool marked "Ready" in the discovery report, the engineer either runs the tool via a Java capture harness or manually constructs representative examples by reading the source code. These golden files serve as the definitive test oracle for verifying that re-implementations in any target language produce identical results.

**Why this priority**: Golden I/O files are the bridge between legacy behaviour and future implementations. Without them, there is no objective way to verify that a re-implemented tool produces correct results. They also serve as concrete documentation of each tool's behaviour.

**Independent Test**: Can be tested by verifying that each Ready tool has at least the minimum number of golden example pairs (1 for Low-complexity, 3 for Medium, 4+ for High), that all files parse as valid JSON, that floating-point values use full precision, that timestamps are UTC, and that collection ordering is deterministic.

**Acceptance Scenarios**:

1. **Given** a Low-complexity tool marked Ready, **When** golden I/O is captured, **Then** at least one example pair (`{tool-name}.basic.input.json` and `{tool-name}.basic.output.json`) exists at `_tool-migration/tools/{category}/`.
2. **Given** a Medium-complexity tool marked Ready, **When** golden I/O is captured, **Then** at least three example pairs exist (basic, edge, and complex cases).
3. **Given** a High-complexity tool marked Ready, **When** golden I/O is captured, **Then** at least four example pairs exist (basic, edge-1, edge-2, and complex).
4. **Given** any captured golden file, **When** validated, **Then** floating-point values have full precision (no rounding), timestamps use ISO 8601 with UTC (Z suffix), coordinates follow GeoJSON convention ([longitude, latitude]), and collections have deterministic ordering.
5. **Given** a tool that is too tightly coupled to Eclipse UI to run in isolation, **When** golden I/O is needed, **Then** examples are manually constructed from source code analysis, and the tool's status is updated to note that manual construction was used.

---

### User Story 3 - Author Language-Neutral Tool Specifications (Priority: P3)

A migration engineer writes a complete, language-neutral specification for each tool that has golden I/O captured. Each spec follows a mandatory 9-section template (Metadata, MCP, Inputs, Outputs, Algorithm, Edge Cases, Examples, Changelog, References) and describes the tool's behaviour entirely in pseudocode with no language-specific constructs. The spec is detailed enough that a developer implementing the tool in any programming language can produce a correct implementation that passes the golden I/O tests.

**Why this priority**: The specs are the primary deliverable that enables re-implementation. They must be accurate, complete, and language-neutral to support multiple target platforms (Python, TypeScript). This phase depends on the discovery report (P1) for knowing which tools to spec and golden I/O (P2) for validating the algorithm descriptions.

**Independent Test**: Can be tested by validating each spec against the Phase 4 validation checklist: all 9 sections present, pseudocode uses only approved keywords, result subtypes match naming patterns, golden examples are referenced, edge cases table has at least 5 entries, and the MCP section is clear enough for an LLM to decide when to invoke the tool.

**Acceptance Scenarios**:

1. **Given** a tool with captured golden I/O, **When** a spec is authored, **Then** the spec file exists at `_tool-migration/tools/{category}/{tool-name}.1.0.md` with YAML frontmatter containing `name`, `version`, `category`, `status`, and `migrated_from`.
2. **Given** a completed spec, **When** the Algorithm section is reviewed, **Then** it contains pseudocode using only approved keywords (FUNCTION, FOR EACH, IF/ELSE, WHILE, RETURN) with no Java, Python, or TypeScript syntax.
3. **Given** a completed spec, **When** the Outputs section is reviewed, **Then** it uses the correct ToolResponse envelope structure, result subtypes match the pattern `^[a-z_]+/[a-z_]+$` (underscores not hyphens, lowercase only), and response builder functions are used correctly.
4. **Given** a completed spec, **When** the Edge Cases section is reviewed, **Then** it contains at least 5 entries covering boundary conditions such as empty input, invalid input type, missing required properties, null optional values, and no matching features.

---

### User Story 4 - Validate Specs Against Quality Checklist (Priority: P4)

A migration engineer validates each completed spec against a formal checklist to ensure it meets all quality requirements before considering it "spec-complete". Validation catches missing sections, language-specific constructs in pseudocode, naming convention violations, and missing golden example references.

**Why this priority**: Quality gates prevent incomplete or inconsistent specs from entering the re-implementation pipeline, where errors would be much more costly to discover and fix.

**Independent Test**: Can be tested by running the validation checklist against each spec and confirming either a pass on all items or a documented list of failures with specific remediation actions.

**Acceptance Scenarios**:

1. **Given** a completed spec, **When** the validation checklist is applied, **Then** each of the 11 checklist items is evaluated as pass or fail with specific evidence.
2. **Given** a spec that fails validation, **When** issues are identified, **Then** the spec is updated to address each failure and re-validated until all items pass.
3. **Given** all validated specs, **When** the discovery report is updated, **Then** the status of each tool reflects its current state (Ready, Spec-Complete, Needs Review, Out of Scope).

---

### Edge Cases

- What happens when a legacy tool is too tightly coupled to Eclipse RCP to extract any algorithm? It is marked "Out of Scope" in the discovery report with an explanation, and the team considers re-implementing from domain knowledge rather than source extraction.
- What happens when a tool has known bugs that the golden I/O would preserve? The spec is flagged with `status: needs-review` and the known issues are documented in the spec's Edge Cases and Changelog sections.
- What happens when a tool depends on external data (databases, files on disk) that cannot be supplied in isolation? External dependencies are mocked in the capture harness, and the spec documents what was mocked and how.
- What happens when floating-point differences arise between the Java golden output and a target language? The verification process uses an epsilon tolerance of 1e-9, and precision requirements are documented in the spec.
- What happens when a legacy trigger type (e.g., drag-drop) has no equivalent in Future Debrief? The tool is listed in a "Tools Requiring New UX Mechanisms" section with a proposed alternative interaction pattern.
- What happens when two legacy classes implement variants of the same logical tool? They are documented as separate entries in the discovery report with a note linking them, and the spec author decides whether to merge them into one spec or keep them separate.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: The process MUST produce a discovery report at `_tool-migration/discovery-report.md` listing every migrateable tool found in the legacy Debrief codebase.
- **FR-002**: Each tool entry in the discovery report MUST include: Name (kebab-case), Category (domain path), Java Class (fully-qualified), Complexity (Low/Medium/High), Legacy Trigger type, Selection Context, Has Intermediate UI (Yes/No), Description (one-line), and Status (Ready/Needs Review/Out of Scope).
- **FR-003**: The discovery report MUST include a trigger type summary table counting tools per trigger type, plus a UX integration mapping table that maps each legacy trigger type to Future Debrief surfaces and flags gaps.
- **FR-004**: The discovery report MUST include a "Tools Requiring New UX Mechanisms" section listing tools whose legacy trigger type has no clean Future Debrief equivalent, with a proposed alternative interaction.
- **FR-005**: Tool names MUST follow kebab-case naming derived from Java class names (e.g., `SplitTracksIntoLegs` becomes `split-tracks-into-legs`).
- **FR-006**: Tool categories MUST use a hierarchical path structure (e.g., `track/analysis`, `sensor/calibration`, `dataset/export`) and MUST be refined during discovery based on actual tool groupings.
- **FR-007**: Complexity assessment MUST consider five factors: algorithm sophistication, dependencies, state management, I/O shape, and UI coupling.
- **FR-008**: Golden I/O files MUST be produced for every tool marked Ready, with minimum example counts by complexity: Low = 1, Medium = 3, High = 4+.
- **FR-009**: Golden I/O files MUST use full floating-point precision, UTC timestamps (ISO 8601), GeoJSON coordinate convention ([longitude, latitude]), and deterministic collection ordering.
- **FR-010**: Tool specifications MUST follow the mandatory 9-section template: Metadata (YAML frontmatter), MCP, Inputs, Outputs, Algorithm, Edge Cases, Examples, Changelog, References.
- **FR-011**: Algorithm pseudocode MUST use only approved keywords (FUNCTION, FOR EACH, IF/ELSE, WHILE, RETURN) and MUST NOT contain any language-specific syntax.
- **FR-012**: Result subtypes in specs MUST match the pattern `^[a-z_]+/[a-z_]+$` (lowercase, underscores, two segments).
- **FR-013**: Each spec MUST reference its golden example files and MUST include at least 5 edge case entries.
- **FR-014**: Each spec MUST include a `migrated_from` metadata field referencing the fully-qualified legacy Java class name.
- **FR-015**: Output files MUST use the ToolResponse envelope structure for outputs, with required annotations: `debrief:resultType`, `debrief:sourceFeatures`, and `debrief:label`.
- **FR-016**: All deliverables MUST be staged in a `_tool-migration/` directory at the repo root, structured for direct copy into `debrief-future/`.
- **FR-017**: The process MUST follow a priority order: Low-complexity tools first, then Medium, then High, with related tools within a category batched together.
- **FR-018**: Pure UI plumbing classes (dialog launchers, view factories, preference pages, menu/toolbar wiring) MUST be excluded from the discovery report, but any parameters they gather MUST be captured as tool inputs.

### Key Entities

- **Discovery Report**: A structured Markdown inventory of all migrateable tools found in the legacy codebase. Contains summary tables, full inventory, trigger type mapping, and triage sections.
- **Tool Specification**: A 9-section Markdown document describing one tool's behaviour in language-neutral terms. Contains metadata, MCP description, input/output schemas, pseudocode algorithm, edge cases, examples, changelog, and references.
- **Golden I/O Pair**: A matched pair of JSON files (`{tool-name}.{case}.input.json` and `{tool-name}.{case}.output.json`) capturing the exact input and output of a legacy tool execution. Serves as the definitive test oracle for re-implementations.
- **ToolResponse Envelope**: The standardised JSON output structure for all Future Debrief tools, containing an array of annotated content items with provenance metadata (result type, source features, human-readable label).
- **Tool Category**: A hierarchical classification path (e.g., `track/styling`, `sensor/calibration`) organising tools by domain. Categories include: track/styling, track/analysis, track/manipulation, track/measurement, sensor/calibration, sensor/analysis, dataset/export, spatial/geometry, narrative/formatting.
- **Legacy Trigger Type**: Classification of how a tool is invoked in legacy Eclipse RCP. Types include: context-menu, toolbar-button, menu-bar, drag-drop, property-edit, wizard, key-binding, auto/listener, view-action, bulk/batch.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: 100% of tool-bearing classes across the four primary package roots are catalogued in the discovery report, with no tool class missed.
- **SC-002**: Every tool entry in the discovery report has all 9 required columns populated (no blank or placeholder values).
- **SC-003**: Every tool marked "Ready" has golden I/O files meeting the minimum count for its complexity level (1/3/4+ examples).
- **SC-004**: 100% of authored specs pass all 11 items on the Phase 4 validation checklist on the first or second review pass.
- **SC-005**: A developer unfamiliar with the legacy codebase can read any completed spec and understand what the tool does, what inputs it takes, what outputs it produces, and what algorithm it follows, without needing to consult the Java source.
- **SC-006**: All golden I/O files parse as valid JSON and floating-point values match to within 1e-9 epsilon tolerance when cross-checked.
- **SC-007**: The UX integration mapping table covers all 10 legacy trigger types with explicit gap/no-gap assessment for each Future Debrief surface.
- **SC-008**: Tools are processed in priority order (Low-complexity first, batched by category), enabling incremental progress and early validation of the migration process.

## Assumptions

- The legacy Debrief codebase at `debrief/debrief` is the authoritative source for all tool behaviour. Where the source code and any external documentation disagree, the source code takes precedence.
- The four already-migrated tools in `debrief-future/shared/tools/track/styling/` (set-track-color, apply-symbol-style, label-interval, symbol-interval) serve as reference examples for tone, detail level, and structure.
- Tools that are purely Eclipse RCP UI plumbing (dialog launchers, view factories, preference pages) are excluded, but any parameters those dialogs gather are captured as tool inputs in the spec.
- The `_tool-migration/` staging directory is a temporary location. Final deliverables will be copied to `debrief-future/shared/tools/` and `debrief-future/docs/tool-migration/` after review.
- The initial category hierarchy (track/styling, track/analysis, track/manipulation, etc.) is a starting hypothesis that will be refined during discovery based on actual tool groupings.
- Manual golden I/O construction (reading the source and building expected outputs by hand) is acceptable for tools that cannot run in isolation, and is preferred over skipping those tools entirely.
- The `/tool.discover`, `/tool.spec`, and `/tool.verify` agents referenced in the SRD are not available in the legacy repo; all work is performed using inline instructions from the LEGACY-REPO-TASK.md document.
