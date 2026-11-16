# Debrief NG: manual smoke-test checklist

A walkthrough of the Eclipse RCP product (Debrief NG), for acceptance checks such as a phase's manual walkthrough in [docs/dependencies/plan.md](../dependencies/plan.md).

It replaces the 22 RCPTT scripts in `org.mwc.debrief.ui_test`, which were retired in Phase E (see [phase-e-options.md](../dependencies/phase-e-options.md)). Each item below is one of those scripts, with the files it used and what it checked. The scripts had not run in CI and were no longer in the build. They remain in git history before this change.

**Before you start:** do *File → New → Debrief Project* and tick *Add Debrief Samples*. That creates the `demo` project with `sample_data/`. When closing plots, choose *Don't Save*.

## Opening data

- [ ] **Open `sample.dpf`.**
  - It opens in the plot editor.
  - *Outline* lists `Annotations (0 items)`, `Misc (5 items)`, `Track:COLLINGWOOD`, `Track:NELSON` and `Narratives (19 items)`.
- [ ] **New plot, then add data.**
  - *New → Debrief Plot* (`plot1.dpf`).
  - Drag `boat1.rep` onto it and answer the *Select track mode* dialog.
  - Then drag `sample.dpf` onto it.
  - *Outline* shows `Track:NELSON`, `Track:COLLINGWOOD` and *Natural Earth*, and the editor tab reads `*plot1.dpf`.
- [ ] **Create a new plot.**
  - *New → Debrief Plot*, through the wizard.
  - *Outline* has 3 items, including `Grid` and `Scale`.
  - Selecting the grid shows `Color (R:0 G:255 B:0)` in *Properties*.
  - *File → Save* works.

## Preferences

- [ ] **The Maritime Analysis preference page** is listed (4th) in the preferences tree.
- [ ] **Default track mode.**
  - On that page the choices are *DR Track*, *OTG Track* and *Ask user*, with *Ask user* selected by default.
  - Opening `boat1.rep` then asks *Select track mode*, offering Dead Reckoning or Over The Ground.

## Main toolbar

- [ ] **The toolbar items are present and enabled** with `boat1t.dpf` open:
  - Save; Open Prefs (gears icon);
  - Fit to window; Natural Earth;
  - Drag Component, Drag Track Segment and Drag Whole Feature;
  - Measure range bearing; Furthest On Circles; Manually select target layer.

## Chart and drawing features (`boat1t.dpf`)

- [ ] **Add a drawing feature.** Using the *Adding new drawing feature* dialog, the chart features layer goes from 1 item (`Scale`) to 2.
- [ ] **Hide a chart feature.** On *Time Display (Absolute)*, toggle *Visible* off and on in *Outline*; *Properties* follows.
- [ ] **Modify a chart feature.** Add *Chart Features → Local Grid*, zoom out, and check it is drawn.
- [ ] **Remove a chart feature.** Add a rectangle, then *Delete New rectangle*; the layer goes back to 1 item (`Scale`).

## Controlling what you view (`sample.dpf`)

- [ ] **Time formats.** In *Time Controller*, each *DTG Format* changes the time display at the start time:
  - `yy/MM/dd HH:mm` → `95/12/12 05:00`
  - `mm:ss.SSS` → `00:00.000`
  - `HHmm.ss` → `0500.00`
  - `HHmm` → `0500`
  - `ddHHmm` → `120500`
  - `ddHHmm:ss` → `120500:00`
- [ ] **Time Controller toolbar.**
  - The buttons are enabled: edit properties, filter to period, time bar, and the view menu.
  - The painter is *Normal* (selected), and *Snail* is available.
- [ ] **Step through time.**
  - The time display is green on black.
  - Start, end, small and large steps forward and backward, and auto-play all move the vessels.
- [ ] **Hide a shape.**
  - Select `NOGO DELTA` in *Outline*, then *Hide selected items*: *Properties* shows `Visible No`.
  - *Reveal selected items* sets it back to `Yes`.
- [ ] **Hide a whole layer.** As above, with several items selected (*Multiple items/Visible*).
- [ ] **Track Tote.**
  - Make NELSON primary and COLLINGWOOD secondary.
  - The tote has 3 columns (Attribute, NELSON, COLLINGWOOD) and shows Range.
  - *Remove this track* takes a track out.
  - *Stacked Charts* opens.

## Editing data (`sample.dpf`)

- [ ] **Edit a property, then undo.**
  - Select `NOGO DELTA`, set *Label color* to red `(R:255 G:0 B:0)`.
  - *Edit → Undo NOGO DELTA Label color* restores it.
  - Zoom in, zoom out and *Fit to window* work.
- [ ] **Edit a position, then undo.** Change `Some label`'s *Location* in *Properties*, then *Edit → Undo Some label Location*.

## Sensor data (`sen_tracks.rep`)

For each item: open `sen_tracks.rep`, choose a track mode, then drag `sen_ssk_sensor.dsf` and `sen_frig_sensor.dsf` from *Navigator* onto the plot and step through the import wizard.

- [ ] **Add sensor data.** *Outline* shows `Track:Frigate/Sensors (5 items)`. The sensors take their default colours: blue `(R:0 G:100 B:189)` and red `(R:224 G:28 B:62)`.
- [ ] **Format sensor data.** Change sensor colours in *Properties* (to red and to yellow); the plot follows.
- [ ] **Plot lock.** *Make Primary* on one track and *Make Secondary* on the other; *Track Tote* shows both, with the same time value (78937) for Frigate and New_SSK.
