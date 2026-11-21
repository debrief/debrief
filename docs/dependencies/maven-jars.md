# Third-party jars from Maven Central

Third-party jars that are on Maven Central are no longer committed. The
build fetches them instead.

## How it works

- `org.mwc.debrief.thirdparty` is a build-only module (`pom` packaging). It is
  first in the reactor (`pom.xml`).
- Its `pom.xml` lists each jar as a normal `<dependency>`, with every
  transitive dependency excluded. Only the named jar is used.
- In `generate-resources`, `maven-dependency-plugin:copy` copies each jar into
  the plugin that uses it, under a fixed, version-free name such as
  `org.mwc.debrief.legacy/pdfbox.jar`. The version comes from the
  `<dependency>` entry.
- Each plugin's `.gitignore` lists the copied names.
- The plugin's `MANIFEST.MF` (`Bundle-ClassPath`), `build.properties` and
  `.classpath`, the Ant `<javac>` filesets and Lite's jar list all name the
  same files as before, so nothing else changes. Lite picks the jars up from
  the plugin folders, as it always has.
- **Changing a version** means editing `org.mwc.debrief.thirdparty/pom.xml`
  only. The file name stays the same.
- **Dependabot** watches that pom weekly (`.github/dependabot.yml`).
  - Jars that must move together are grouped, so each family arrives as one
    PR, majors included: POI with xmlbeans, PDFBox, slf4j and Jackson.
  - Other minor and patch bumps come together in one PR. Other major bumps
    come one per PR.
  - Ignored: build plugins in the parent pom; and JFreeChart 1.5 and later, whose package
    and API changes break 9 call sites in `cmap.legacy`.
  - CI checks every PR, and nothing merges automatically.

## Building

- A full build (`mvn clean install`) fetches the jars first.
- To build one plugin, include the module:
  `mvn clean install -pl org.mwc.debrief.thirdparty,org.mwc.debrief.legacy`.
- **Eclipse IDE:** after a fresh clone, run
  `mvn -pl org.mwc.debrief.thirdparty generate-resources` once, then refresh
  the projects. Run it again after a version bump.
- CI's Lite UI job lists its modules by hand. It includes
  `org.mwc.debrief.thirdparty`.

## Moved so far

| Plugin | Jars fetched from Maven Central |
|---|---|
| `org.mwc.debrief.legacy` | pdfbox, fontbox 2.0.37; commons-logging 1.2; commons-cli 1.4; jsoup 1.23.2; xmlunit-core 2.14.0; Eclipse January 2.0.2; slf4j-api, slf4j-simple 1.7.36 |
| `org.mwc.cmap.legacy` | POI 5.5.1 (poi, poi-ooxml, poi-ooxml-lite, poi-scratchpad); xmlbeans 5.3.0; commons-codec 1.20.0, collections4 4.5.0, compress 1.28.0, csv 1.2, io 2.22.0, lang3 3.21.0, math3 3.6.1; curvesapi 1.08; SparseBitSet 1.3; log4j-api 2.26.1; Jackson 2.22.4 (annotations 2.22); joda-time 2.1 (junit 4.13.2 and hamcrest-core 1.3 were removed in Phase E) |
| `org.mwc.cmap.core` | xstream 1.4.21 (mockito-all was removed in Phase E) |
| `org.mwc.cmap.jfreechart` | jfreechart 1.0.19; jcommon 1.0.23 |
| `org.mwc.debrief.core` | JAXB 2 API: jaxb-api 2.3.1 (exported for the DIS plugin; the provider is EclipseLink MOXy, already in the plugin; `javax.activation` comes from the target platform) |

Notes on `org.mwc.debrief.legacy`:

- **Eclipse January.** The committed `January.jar` was an unversioned 2017
  build. It has exactly the same 169 classes as `org.eclipse.january` 2.0.2.
- **slf4j.** The committed slf4j pair was mismatched: api 1.5.6 with a
  repackaged simple 1.7.25. Both are now 1.7.36, which matches
  `org.mwc.cmap.media`.
- **`sf` jars.** The manifest listed `sf-2.0.3.jar` and `sf-geojson-2.0.4.jar`,
  which did not exist and that nothing imports. Those entries are gone.

Notes on `org.mwc.cmap.jfreechart`: the committed `jfreechart-1.0.19.jar` was Central's build plus three demo classes (`org.jfree.chart.demo`), which nothing used. The other 635 classes were byte-identical. The demo package's export is gone.

All four plugins planned for Phase B are done. Since Phase D2, `org.mwc.debrief.lite` takes Radiance (`radiance.version`) the same way.

`cmap.geotools` (Phase D1) has its own module, `org.mwc.cmap.geotools.libs`,
because GeoTools is published to the OSGeo repository
(`repo.osgeo.org`), not Central, and brings about 100 jars. The module names
the GeoTools modules Debrief uses (`geotools.version`) and copies them, with
everything they need at runtime, into `org.mwc.cmap.geotools` under names of
the form `groupId.artifactId.jar`. commons-io and commons-lang3 are left out:
the bundle takes them from `cmap.legacy`. The bundle's `Bundle-ClassPath`,
`Export-Package`, `build.properties` and `.classpath` list every jar, so they
are generated: after a change that adds or drops a jar, run

```bash
mvn -pl org.mwc.cmap.geotools.libs generate-resources
```

```bash
python3 .github/scripts/geotools_manifest.py
```

CI runs the script with `--check`. JAXB 2 (`javax.xml.bind`), which
Debrief's GPX code and the DIS plugin use and which used to come with the
GeoTools 21 jars, is fetched into `org.mwc.debrief.core` instead (table above).

Test-only: JUnit 6 (`junit.version` in the root pom) is a test-scope dependency of every plugin, for its `test/` folder, and `org.mwc.debrief.thirdparty` copies the JUnit API jars into `org.mwc.debrief.lite/test-libs/` for Lite's Ant-built tests. None of them is shipped.

## Kept in the repo

These have no Maven Central coordinates, or none that match. They stay
committed:

| Jar | Plugin | Why |
|---|---|---|
| `nanoxml-2.2.1.jar` | `debrief.legacy` | NanoXML 2.2.1 (2002) was never published to Central |
| `javalite-common-1.2-20120312.200900-220.jar` | `cmap.legacy` | A 2012 snapshot build; no matching release on Central |
| `openmap.jar` | `cmap.legacy` | An unversioned OpenMap build; no artifact on Central matches it |
| `jmf.jar` | `cmap.legacy` | A different JMF build from Central's `javax.media:jmf` 2.1.1e (1,114 classes against 993; only 22 are byte-identical) |
| `vrml97.jar` | `cmap.legacy` | Java3D VRML97 loader; no artifact on Central matches it |
| `flanagan.jar` | `cmap.legacy` | Michael Flanagan's maths library; no artifact on Central matches it |
| `jai_core`, `jai_codec` 1.1.3; `jai_imageio` 1.1 | `cmap.core` | Java Advanced Imaging: Central has POMs only, no jars |
| `imageio-ext-tiff`, `imageio-ext-utilities` 1.1.2 | `cmap.core` | Not on Central (imageio-ext is published to the OSGeo repository) |
| `WMFWriter.jar` | `cmap.core` | No artifact on Central |
| `org.eclipse.nebula.widgets.formattedtext_1.0.0.jar` | `cmap.core` | An old Nebula build; no matching artifact on Central |
| `jfreechart-1.0.19-swt.jar`, `jfreechart-1.0.19-experimental.jar` | `cmap.jfreechart` | No matching artifacts on Central |
