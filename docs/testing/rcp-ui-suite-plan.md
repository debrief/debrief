# Plan: a small automated UI suite for Debrief NG

Phase E retired the RCPTT runner and replaced its 22 scripts with a [manual checklist](debrief-ng-smoke-checklist.md). The owner asked for a small automated suite for the Eclipse RCP product (Debrief NG) before Phase D1, because the GeoTools upgrade changes the RCP map ([plan.md](../dependencies/plan.md), [phase-e-options.md](../dependencies/phase-e-options.md)).

This document proposes the tool, the scenarios, how it runs and the order of the work. **Status: proposal, for the owner to approve.**

## What the suite must do

- **Catch what D1 can break in the RCP product.** That means the map drawn in the plot editor, and the paths that feed it: opening plots, importing data, time stepping and the tote. Lite already has map screenshots (`MapRenderingTest`) and a position check (`ProjectionAccuracyTest`), but they don't exercise the RCP editor.
- **Stay small and stable.** 6–8 tests of the main paths, not a port of all 22 scripts. Each failure should point at something real, not at timing.
- **Run in CI on every PR** that changes software, under xvfb, in a separate job like `lite-ui-tests`.

## Tool: SWTBot (recommended) or RCPTT

| | SWTBot 4.3.0 | RCPTT 2.8.2 |
|---|---|---|
| Tests are written in | Java, JUnit 5 (`@ExtendWith(SWTBotJunit5Extension.class)`) | ECL scripts, recorded or hand-written |
| Runs through | Tycho Surefire with the UI harness. This is what `org.mwc.debrief.editable.test` already does. | `rcptt-maven-plugin`, which starts the product as a separate application |
| Fits our stack | Same language, build and runner as the rest of the tests | A second language and runner, which is what we just retired |
| Brittleness | Moderate. Widgets are found by label or id, and waits are explicit. | Moderate to high. Recorded scripts follow UI layout. |
| Screenshot checks | Capture the editor canvas in Java and compare with `GoldenImage` | Possible, but outside our golden-image helper |

**Recommendation: SWTBot.**
- It is plain Java and JUnit, in the build we already have.
- It can reuse `GoldenImage`, so the RCP map gets the same per-pixel check Lite has.
- RCPTT would bring back a second toolchain for a handful of tests.

**Catch: SWTBot pulls in JUnit 4 and hamcrest.**
- Its finder bundles require `org.junit` and `org.hamcrest`, so they come back, but only in the test runtime: the suite's target and its test plugin.
- Features and products don't include them, so nothing ships them.
- `org.eclipse.swtbot.junit5_x` imports only `org.junit.jupiter.api.extension`, with no version range, so it should run on Jupiter 6. Proving that is step 1 of the work below.

## Scenarios

Taken from the checklist and chosen for what D1 and D2 touch. Each uses the bundled sample data.

1. **Open `sample.dpf`.** The plot editor opens, *Outline* lists the five expected layers, and *Fit to window* works.
2. **Map screenshot.** With `sample.dpf` at a fixed editor size and *Fit to window*, capture the chart canvas and compare it with a reference (`GoldenImage`, the same tolerance as Lite). This is the D1 check for the RCP map. A second capture adds the Natural Earth coastline.
3. **Import REP data.** Drag `boat1.rep` onto a new plot and answer *Select track mode*: *Outline* shows the track.
4. **Time stepping.** In *Time Controller*, step forward and back and check the time display at the start time. Two DTG formats are enough.
5. **Track Tote.** Make NELSON primary and COLLINGWOOD secondary: the tote shows both, with a Range row.
6. **Edit and undo.** Change `NOGO DELTA`'s *Label color* in *Properties*, then undo it.
7. **Sensor import.** Open `sen_tracks.rep`, drag `sen_frig_sensor.dsf` onto it and step through the wizard: *Outline* shows the sensor layer.

The rest of the checklist stays manual: preferences, toolbar inventory, chart features, hide and reveal, and format changes.

## How it runs

- **New test plugin** `org.mwc.debrief.ui.test`, packaged as `eclipse-test-plugin`.
  - `tycho-surefire-plugin` runs it with `useUIHarness=true` and `useUIThread=false`.
  - The application is the Debrief NG product's, so the plot editor, views and perspectives are real.
  - Sample data (`org.mwc.cmap.combined.feature/root_installs/sample_data/`) is copied into a temporary workspace project before each class.
- **Target platform:** add the SWTBot 4.3.0 repository, `https://download.eclipse.org/technology/swtbot/releases/latest/`, as a location in a separate *test* target, or in `eclipse-latest.target` with the units used only by the test plugin.
  - The weekly target-bump workflow doesn't touch it, so we update it by hand.
- **CI:** a new `rcp-ui-tests` job in `main.yml`.
  - It builds the plugins and the test plugin with `-DrcpUiTests=true` under `xvfb-run`, at a fixed screen size (1600×1200×24).
  - It uploads the reports and golden-image failures as artifacts.
  - The main build skips the plugin, as Lite UI tests are skipped without `-DliteUiTests=true`.
- **References:** generated in CI, like Lite's, because fonts differ between machines. The job can regenerate them on demand with `DEBRIEF_GOLDEN_REGENERATE=true`.

## Order of work

1. **Spike, 1 PR.**
   - Add SWTBot to the target and create the test plugin with scenario 1 only.
   - Prove that it runs under Tycho Surefire on JUnit 6 and xvfb, locally and in CI.
   - Stop and report if `junit5_x` does not run on Jupiter 6. The fallback is SWTBot's JUnit 4 runner, for this plugin only.
2. **Scenarios 2–4, 1 PR.** Includes the map screenshot and its references.
3. **Scenarios 5–7, 1 PR.**
4. **Docs.** Update CLAUDE.md (Test Architecture), the checklist (mark the automated items) and `plan.md` (Phase E done; the suite is a D1 acceptance check).

## Risks

- **Flaky waits.** SWT events are asynchronous. Use SWTBot's `waitUntil` conditions, never sleeps, and give each scenario a fresh editor.
- **The workbench at startup.** The welcome page, dialogs and perspective switches can block. `-DbuildMode=true` already suppresses Debrief's own dialogs. The test plugin closes *Welcome* and sets the perspective before each class.
- **Rendering differences.** GTK theme and fonts change pixels, so references come from CI only, as for Lite.
- **Cost if it doesn't stabilise.** If the spike can't run reliably within a couple of days' effort, stop. Keep the manual checklist as the D1 acceptance check and record that decision.

## Not in scope

- Porting the remaining checklist items.
- ASSET's RCP views. ASSET is no longer released.
- Windows and macOS runs. CI is Linux only, and the manual walkthrough covers the others.
