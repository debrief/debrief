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

Notes on `org.mwc.debrief.legacy`:

- **Eclipse January.** The committed `January.jar` was an unversioned 2017
  build. It has exactly the same 169 classes as `org.eclipse.january` 2.0.2.
- **slf4j.** The committed slf4j pair was mismatched: api 1.5.6 with a
  repackaged simple 1.7.25. Both are now 1.7.36, which matches
  `org.mwc.cmap.media`.
- **`sf` jars.** The manifest listed `sf-2.0.3.jar` and `sf-geojson-2.0.4.jar`,
  which did not exist and that nothing imports. Those entries are gone.

Still to move, one plugin per PR: `cmap.legacy`, `cmap.core` and
`cmap.jfreechart`. `cmap.geotools` waits for the GeoTools upgrade.

## Kept in the repo

These have no Maven Central coordinates, or none that match. They stay
committed:

| Jar | Plugin | Why |
|---|---|---|
| `nanoxml-2.2.1.jar` | `debrief.legacy` | NanoXML 2.2.1 (2002) was never published to Central |
