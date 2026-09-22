# Debrief 3.1.45

A maintenance release: a new indexed symbol for friendly air tracks, a fix for
GeoTiff layers, and module documentation throughout.

### Features

- Friendly Air SVG symbol can now be given in REP files by the single-character
  symbol code `y`, as well as by name (`[SYMBOL=friend_air]`) (#5234)

### Fixes

- GeoTiff layers can now be located on the plot, and render failures are
  reported instead of being skipped silently (#5230)

### Infrastructure

- Release process automated: version bumping, release tag, published release
  notes (#5231)
- Windows, macOS and Linux builds attached to pull requests (#5232)
- No "build failed" comment on a pull request when its CI run is cancelled (#5233)
- Help build runs `ease_javadoc` only on Unix-like hosts; Lite Ant tests honour
  `-DskipTests` (#5227)

### Other

- README documentation added for the core plugins and key packages (#5214)
- Capability audit, and documentation of Debrief algorithms, the DPF file format, sensor data support,
  sample data and a reimplementation roadmap (#5217, #5218, #5219, #5220, #5221,
  #5224)
- Guide to rendering ambiguous sensor contacts in JS/TS (#5223)
- README typo fix (#5210)
