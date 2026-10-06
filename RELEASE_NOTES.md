# Debrief 3.1.46

Sensor arcs on planning tracks, plus a security and correctness release that
fixes the High-severity findings from a whole-repository code review. Some
analysis results change: see **Behaviour changes** below.

### Features

- Sensor arcs can now be attached to planning tracks: by REP `;SENSORARC`, by
  the right-click **Insert new Sensor Arc** action, or with the toolbar Sensor
  arc tool. They follow each leg (including the closing leg) as the plan is
  edited, move with the track's start time, appear in the Outline, and are kept
  when the plot is saved. **Add new sensor** is no longer offered on planning
  tracks (#5256)

### Fixes

- Security hardening of data import (#5252):
  - XML files (DPF, KML, GPX) are parsed safely: DOCTYPE declarations are
    rejected, closing off XML external entity (XXE) attacks
  - archive extraction (KMZ, PPTX templates) blocks zip-slip paths and caps
    extracted size
  - clipboard paste only accepts known Debrief data types
  - PDFBox and FontBox upgraded to 2.0.37
- Bearing, unit and angle-wrapping maths corrected, including cross-track
  distances away from the equator, zig detection, and Antares course/speed
  units (#5252)
- Malformed lines in REP and other imports are reported in one warning at the
  end of import, instead of being silently skipped or aborting the import
  (#5252)
- Undo after Resample and Split Tracks restores the original fixes and legs
  (#5252)
- Items with the same time (TMA cuts, sensor arcs, segments) are no longer lost
  from sorted lists (#5252)
- The Outline view no longer fails to populate with "Comparison method violates
  its general contract" after merging tracks with many TMA legs (#5238)
- The User Guide PDF ships with its cover page again (#5239)

### Behaviour changes

- **Zig and ambiguity results change.** The old code could split a steady leg;
  for example `legs_20.rep` now gives 4 zigs / 23 legs instead of 6 / 25.
  Re-run affected analyses.
- **Cross-track distances away from the equator change.** They were wrong by a
  factor of 1/cos(latitude).
- **Antares imports** now convert course and speed from degrees and knots.
- **Stricter number parsing.** Values with trailing text, `NaN`, and values
  such as `1,234` that could use either separator are rejected and reported.
  European decimals such as `22,5` still work.
- **Two-digit REP years** are read in a fixed 1950–2049 window, no longer
  depending on the machine's date.
- **Size limits:** PPTX templates 64 MB, KMZ files 64 MB, csv.gz files 1 GB.
- In the Outline, items without a time now sort after timed items rather than
  being interleaved by name.

### Infrastructure

- A local build no longer modifies tracked files; the scripting help is no
  longer regenerated (with degraded output) during the build (#5253)
- CI: the Windows JRE download is checksum-verified, msitools comes from the
  distribution package, and the PR build comment only reports on the PR's
  current head (#5252)

### Other

- Whole-repository code review findings register added under
  `docs/code-review/` (#5240)
- Claude Code rule files and spec-kit commands updated (#5241)
