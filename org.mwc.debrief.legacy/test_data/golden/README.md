# Golden files

Committed output that tests compare against, so that a library upgrade which changes what Debrief writes fails a test. The tests use `Debrief.Testing.GoldenFile`.

| File | Written by | Checks |
|---|---|---|
| `lite/sample_dpf_saved.dpf` | `org.mwc.debrief.lite` `FileRoundTripTest.testDpfRoundTrip` | Lite's save of `sample.dpf` |
| `lite/boat1_rep_saved.dpf` | `FileRoundTripTest.testRepRoundTrip` | Lite's save after importing `boat1.rep` |
| `geojson/sample_dpf.json` | `GenerateGeoJSON.GenerateGeoJSONTest.testGeoJSONGolden` | GeoJSON (Jackson 2) for the tracks in `sample.dpf`, as canonical JSON |
| `xyplot/time_series_memento.xml` | `XYPlotStateXml.TestMe.testSavedMementoStillLoads` | An XY plot memento (XStream) that later library versions must still read |

Fields that change from run to run are replaced with `MASKED`: the plot's `Created` time and the version date in its details note.

## Regenerating

Do this only when a change in the output is intended. Run the tests with the environment variable set:

```bash
DEBRIEF_GOLDEN_REGENERATE=true xvfb-run -a mvn clean install
```

The system property `-Ddebrief.golden.regenerate=true` does the same where it reaches the test JVM. Each golden test then writes its output here and passes.

Review the diff before committing. In the pull request, say which golden files changed and why. A change nobody can explain is a regression.

`-Ddebrief.golden.dir=<path>` points the tests at a different directory.
