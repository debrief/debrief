# Debrief 3.2.0

**Debrief now needs Java 21.** This release moves Debrief to a current Eclipse
and Java toolchain, brings back Debrief Lite as a download, and retires the
Scripting perspective. See **Before you upgrade** below.

### Before you upgrade

- **Java 21 is required** (was Java 11). Windows users get it bundled: the
  installer and zip now include Temurin 21.0.13. On **macOS and Linux**,
  install a Java 21 runtime, for example
  [Temurin 21](https://adoptium.net/temurin/releases/?version=21), and make
  sure it is the `java` on your PATH (#5268)
- **The Scripting perspective has been removed.** It ran on EASE, which is no
  longer maintained and does not support current Eclipse and Java releases.
  The JavaScript samples have gone from `sample_data` too (#5271)
- **Updating an existing install** through Help > Check for Updates has not
  been tested across the Java 11 to 21 change. A fresh install is the safe
  route.

### Features

- **Debrief Lite is back** as a release download, `debrief-lite-<version>.zip`.
  Unzip it and run `java -jar debrief_lite.jar` (Java 21) (#5269)
- Debrief is now built on **Eclipse 2026-09** (platform 4.41), up from
  2021-12 (#5268)

### Fixes

- Debrief Lite asks to save before discarding a plot that holds only shapes or
  labels; until now it asked only when there were tracks or narratives (#5273)
- Debrief Lite no longer marks a plot as saved, or fails with an error, when
  saving it fails (#5272)
- On Windows, Debrief no longer offers at first start to exclude itself from
  Microsoft Defender scanning; the prompt came with the newer Eclipse
  platform (#5277)
- The DebriefLegacy jar now identifies itself as "Debrief Legacy" in its
  manifest, not "Debrief Lite" (#5259)
- DIS multicast in the vendored NPS networking code now joins groups through
  the standard Java API; the old reflective workaround fails on Java 17 and
  later (#5268)

### Infrastructure

- Build toolchain upgraded: Tycho 2.7.0 to 5.0.4, JDK 11 to 21, and the build
  now works on Maven 3.10 as well as 3.9 (#5260, #5259, #5264, #5265, #5266,
  #5267)
- Plugins' bundled library jars moved to each bundle's root, which Maven 3.10
  requires (#5266)
- The product is smaller: 56 plugins fewer, and the Linux zip drops from
  470 MB to 416 MB, with EASE and the bundles only it needed gone (#5271)
- Debrief Lite has 19 headless tests in the main build, covering file round
  trips, time stepping and the tote, plot state, and shape insertion, and its
  17 Swing UI tests run in their own CI job (#5269, #5272, #5273)
- The target platform's `eclipse-latest.tpd` source matches the 2026-09
  target (#5274)
