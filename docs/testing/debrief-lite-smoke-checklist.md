# Debrief Lite: manual smoke-test checklist

A walkthrough of Debrief Lite, for acceptance checks such as Phase D2's manual walkthrough in [docs/dependencies/plan.md](../dependencies/plan.md) (Radiance 3 → 9, swingx removed). Run it on **Windows, Linux and macOS**.

Items marked **(automated)** are covered by Lite's UI tests (`org.mwc.debrief.lite/src/test/java/org/mwc/debrief/lite/tests/`), which CI runs on Linux under xvfb on every pull request. On Windows and macOS check them all; on Linux, a glance is enough. Items marked **Radiance 9** are where the new library behaves differently, or where the tests don't look.

**Before you start:** take the Debrief Lite zip from a `develop` build (or a PR's *Review builds* comment), unzip it, and run `java -jar debrief_lite.jar` with Java 21. The sample files are in the zip's `data/tracks/` folder. When closing a plot, choose not to save.

## Start-up

- [ ] **Lite starts** with the *Lite* tab selected, an empty map and the coastline. **(automated: `TestLiteLaunch`, screenshot)**
- [ ] **No error dialogs at start-up.**
  - On Linux only, one `Taskbar` exception on the console is expected (a Radiance 9 bug; harmless).
  - On Windows and macOS there should be none.
- [ ] **The window icon** shows in the title bar and the OS taskbar or dock (Windows, macOS).
- [ ] **Look and feel (Radiance 9).** Fonts are readable and the ribbon tabs, bands and side panels are drawn without gaps or overlaps. Compare with the screenshots in `org.mwc.debrief.legacy/test_data/golden/lite-window/`.

## Disabled and enabled icons (Radiance 9)

- [ ] **With no plot loaded,** on the *Time* tab:
  - the play controls, *Format*, *Properties* and the display-mode toggles are greyed light grey (not black), and clearly look disabled;
  - the *Normal* and *Square* toggles show as selected but disabled.
- [ ] **After opening a plot** (next section), the same buttons show in full colour and respond.
- [ ] **Hover and press** a few buttons on each tab: the icon keeps its colours and shape (no black or washed-out icons).
- [ ] **Play / Stop.** Press *Play*: the icon changes to *Stop* and is drawn cleanly; press again to return to *Play*. Repeat a few times.

## File tab

- [ ] **Open `data/tracks/Orion.rep`** (*Open → Replay*). The track appears and the map zooms to it. **(automated: `TestFileRibbon`)**
- [ ] **Import** `NMEA_TRIAL.log` (*Import → NMEA*) and `nisida_sample.txt` (*Import → Nisida*): each adds data to the plot.
- [ ] **Save popup (Radiance 9).**
  - Clicking *Save*'s **text** opens its popup (*Save*, *Save As*), not a save.
  - The popup's icons are laid out evenly, none clipped.
  - *Save As* writes a `.dpf`; *Close* then *Open → Plot* reopens it with the same tracks. **(Save As: automated, `TestMenus`)**
- [ ] **Export → Clipboard** with a track selected: the clipboard holds the track's text.
- [ ] **Close** asks to save when the plot has changed.

## Time tab

- [ ] **Format** shows its icon **with its label beside it** (changed in D2: previously below). Its menu lists the time formats; choosing one changes the time display.
- [ ] **Play** steps time forward; *Back*, *Forward*, *Rewind*, *Fast Forward*, *Behind* and *End* move as expected; the time slider follows. **(automated, headless: `TimeAndToteTest`)**
- [ ] **Display Mode:** *Normal* and *Snail* switch the trail style; *Properties* opens the snail properties.
- [ ] **Highlighter:** *Square* and *Symbol* switch the highlight; *Properties* opens its properties.
- [ ] **Filter to time:** the range slider narrows what the plot shows.
- [ ] **Tooltips** on the play controls appear and are readable (the play button's tooltip is a rich tooltip titled *Timer*).

## View tab

- [ ] **Mouse mode:** *Pan*, *Zoom In* and *Range/Bearing* toggles work on the map; only one is selected at a time.
- [ ] **Zoom Out** and **Fit to Window** work.
- [ ] **Background:** the transparency slider changes the chart's transparency.

## Insert tab

- [ ] **Decorations:** add *Scale* and *Grid*; both draw and appear in the outline. **(automated: `TestInsertRibbon`)**
- [ ] **Shapes:** add a *Label*, *Polygon*, *Ellipse*, *Rectangle*, *Circle*, *Arc* and *Line*; each appears on the map and in the outline.
- [ ] **Active Layer:** the layer chooser lists the plot's layers.
- [ ] **Reference Data:** *Coastline* and *Natural Earth* toggle their layers.

## Lite tab

- [ ] **Undo / Redo** reverse and repeat the last shape insertion.
- [ ] **Help** opens `ReadMe.pdf` in the system's PDF viewer (with no viewer, a *Failed to find help file* message).
- [ ] **Exit** asks to save a changed plot, then closes.

## Side panels (swingx removed)

- [ ] **Outline and Properties panels** collapse and expand with their title bars; the animation is smooth and the content is redrawn afterwards.
- [ ] **Collapsed titles** read vertically and are not clipped.
- [ ] **Dragging** a panel's edge resizes it; it can't be dragged past its limits.
- [ ] **Tote** **(automated, headless: `TimeAndToteTest`)**: select a track as primary and another as secondary; the tote fills in and updates while time plays.

## Dialogs and popups (Radiance 9)

- [ ] **Each dialog met above** (file choosers, save prompt, properties editors, Format menu) uses the Radiance look, fits its contents and closes cleanly.
- [ ] **Popups** (Save, Format) open next to their button, stay on screen near the window edge, and close when you click elsewhere.

## Record the result

Note the OS, Java version and date, and any item that failed, in the phase's pull request or issue.
