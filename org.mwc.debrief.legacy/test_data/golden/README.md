# Golden files

Committed output that tests compare against, so that a library upgrade which changes what Debrief writes fails a test. The tests use `Debrief.Testing.GoldenFile`.

| File | Written by | Checks |
|---|---|---|
| `lite/sample_dpf_saved.dpf` | `org.mwc.debrief.lite` `FileRoundTripTest.testDpfRoundTrip` | Lite's save of `sample.dpf` |
| `lite/boat1_rep_saved.dpf` | `FileRoundTripTest.testRepRoundTrip` | Lite's save after importing `boat1.rep` |
| `geojson/sample_dpf.json` | `GenerateGeoJSON.GenerateGeoJSONTest.testGeoJSONGolden` | GeoJSON (Jackson 2) for the tracks in `sample.dpf`, as canonical JSON |
| `xyplot/time_series_memento.xml` | `XYPlotStateXml.TestMe.testSavedMementoStillLoads` | An XY plot memento (XStream) that later library versions must still read |
| `rendering/*.png` | `org.mwc.debrief.lite` `MapRenderingTest` | Map rendering, headless: the coastline shapefile (GeoTools), `uk_track.rep` over it, `sample.dpf` at two sizes and `shapes.rep`, drawn as Lite's map pane draws them. Compared with `Debrief.Testing.GoldenImage`, with a per-pixel tolerance |
| `lite-window/*.png` | `org.mwc.debrief.lite` `TestMainWindowScreenshots` (Lite UI tests: only with `-DliteUiTests=true`, under a display) | Lite's main window at 1280x800: at startup, on each ribbon tab, and with `boat1.rep` loaded on the Time tab. Up to 0.5% of pixels may differ |
| `rcp/*.png` | `org.mwc.debrief.ui.test` `MapScreenshotTest` (Debrief NG UI tests: only with `-DrcpUiTests=true`, under a display) | The RCP plot editor's chart canvas at 1400x900, with `sample.dpf` and with `boat1.rep` imported over the ground, each fitted to the window. Same tolerances as `rendering/` |

Fields that change from run to run are replaced with `MASKED`: the plot's `Created` time and the version date in its details note.

## Images

A screenshot test passes when few enough pixels differ: a pixel differs when a colour channel is off by more than 48 (of 255), and up to 0.2% of pixels may differ (`MapRenderingTest.testDetectsSmallShift` checks that a 3-pixel pan still fails). That absorbs font rasterising and antialiasing differences between machines, but not a moved track, a missing layer or a changed style.

On a failure the test writes the actual image and a diff (different pixels in red) to `target/golden-failures/` in its plugin (`org.mwc.debrief.lite` or `org.mwc.debrief.ui.test`). CI uploads them as the `golden-failures-*` artifact.

The references are made on CI's Linux runner, because fonts differ between machines. To regenerate them, take the actual images from that artifact, or run the regeneration below on Ubuntu 24.04.

macOS rasterises text differently enough to fail those references, so on a Mac `GoldenImage` uses its own set, in a `macos/` folder beside each Linux set (for example `rendering/macos/sample_dpf_800x600.png`), with the same tolerances. CI doesn't run on macOS, so these are kept up to date by hand: when a Linux reference is regenerated for an intended change, regenerate the macOS one on a Mac too (the command below, without `xvfb-run`), and compare the two before committing. Regenerating on a Mac writes only the `macos/` images, but it rewrites the text golden files too: check `git status` and keep only the changes you meant.

## Regenerating

Do this only when a change in the output is intended. Run the tests with the environment variable set:

```bash
DEBRIEF_GOLDEN_REGENERATE=true xvfb-run -a mvn clean install
```

The system property `-Ddebrief.golden.regenerate=true` does the same where it reaches the test JVM. Each golden test then writes its output here and passes.

Review the diff before committing. In the pull request, say which golden files changed and why. A change nobody can explain is a regression.

`-Ddebrief.golden.dir=<path>` points the tests at a different directory.
