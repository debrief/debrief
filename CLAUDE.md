# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Debrief is an Eclipse RCP (Rich Client Platform) maritime analysis application. Built with Tycho/Maven for building Eclipse plugins and features. Java 11 target.

## Build Commands

```bash
# Full build
mvn clean install

# Build without tests
mvn clean install -DskipTests

# Build specific module
mvn clean install -pl org.mwc.debrief.core

# Run tests for specific module
mvn test -pl org.mwc.debrief.test2
```

## Test Architecture

Unit tests use JUnit and are organized in test suites:
- **Main test suite**: `org.mwc.debrief.test2/src/org/mwc/debrief/test/AllTests.java`
- **Asset tests**: `org.mwc.asset.test/src/org/mwc/asset/test/AllTests.java`
- **Lite tests**: `org.mwc.debrief.lite/src/test/java/org/mwc/debrief/lite/tests/AllTests.java`

Tests run via Tycho Surefire with UI harness. Test classes follow pattern `**/AllTests.class` or `**/*TestSuite*.class`.

UI tests use RCPTT (RCP Testing Tool) in `org.mwc.debrief.ui_test/`.

## Project Structure

Key module namespaces:
- `org.mwc.debrief.*` - Main Debrief application plugins
- `org.mwc.cmap.*` - Core mapping/charting framework
- `org.mwc.asset.*` - ASSET simulation framework
- `MWC.*` - Legacy core utilities (in org.mwc.cmap.legacy)
- `Debrief.*` - Legacy Debrief code (in org.mwc.debrief.legacy)

Core plugins:
- `org.mwc.debrief.core` - Main Debrief plugin, Eclipse integration
- `org.mwc.cmap.legacy` - Core data types: `MWC.GenericData.*` (WorldLocation, WorldSpeed, etc.), `MWC.GUI.*` (shapes, layers, canvas)
- `org.mwc.debrief.legacy` - Track wrappers, file readers/writers, GUI components
- `org.mwc.cmap.plotViewer` - Plot/chart rendering
- `org.mwc.debrief.track_shift` - TMA and bearing analysis tools

## Key Domain Classes

Tracks and positions:
- `Debrief.Wrappers.TrackWrapper` - Main track container
- `Debrief.Wrappers.FixWrapper` - Single position fix
- `Debrief.Wrappers.SensorWrapper/SensorContactWrapper` - Sensor data

Data types (in `MWC.GenericData`):
- `WorldLocation` - Lat/lon/depth position
- `WorldSpeed`, `WorldDistance`, `WorldArea`, `WorldVector`

File formats: REP (Replay), DPF (Debrief Plot File/XML), GPX, NMEA, various military formats

## Target Platform

Eclipse target platform defined in `org.mwc.debrief.targetplatforms/eclipse-latest.target`. Based on Eclipse 2021-12 with GEF, EMF, Nebula widgets.

## Architecture Notes

- Eclipse plugin architecture with extension points for importers, editors, views
- SWT/JFace for UI, JFreeChart for plotting
- Heavy use of property sheets and Eclipse Outline view for editing
- Context operations in `org.mwc.debrief.core/src/org/mwc/debrief/core/ContextOperations/`
