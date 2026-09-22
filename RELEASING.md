# Releasing Debrief

A Debrief release is published by pushing a git tag. `.github/workflows/main.yml`
builds the product on every push; when the pushed ref is a release tag it also
creates a GitHub release and attaches the installers.

**Pushing the tag is the point of no return.** Everything before it can be
undone by closing a pull request.

## The quick way

In Claude Code, run `/release`. The skill in `.claude/skills/release/` walks the
whole process and stops for your confirmation before planning the bumps, before
publishing the release notes, before merging, and before the tag.

## The manual way

### 1. Preview what will change

From a clean `develop` that is up to date with `origin`:

```bash
git fetch origin develop --tags
python3 contribs/PyScripts/release_versions.py plan
```

This prints every bundle that changed since the last published release, the
`old → new` version for each file it will touch, the new Debrief version, the
release tag it will use, and — importantly — any changed paths that belong to
no bundle. Read that last list: a plugin missing a bump ships without a version
change, so P2 clients never see the update.

### 2. Apply

```bash
./update_versions.sh          # == release_versions.py apply
```

This:

- increments `Bundle-Version` in each changed plugin's `META-INF/MANIFEST.MF`
- increments the `version` of each feature that ships a bumped plugin
- increments the Debrief version in `org.mwc.debrief.product/debriefng.product`
  and `org.mwc.debrief.core/about.mappings` (with today's date in the two date
  fields)
- runs `tycho-versions-plugin:update-pom` to sync all 63 module poms
- checks that the Debrief version agrees across `about.mappings`,
  `debriefng.product` and `org.mwc.debrief.product/pom.xml`

That last check matters more than it looks: `org.mwc.debrief.product/pom.xml`
copies `product-${project.version}.zip` to `P2_Repository.zip`, so a pom left
behind by a skipped pom sync silently breaks a release asset.

If it exits non-zero, run `git checkout -- .` and start again — do not commit a
partial bump.

`org.mwc.debrief.core` is always bumped, whether or not it changed: it carries
`about.mappings`, and the bundle has to move for Eclipse to pick up the new
About box on update.

### 3. Write the release notes

Rewrite `RELEASE_NOTES.md` with the changes since the last release. This file is
published verbatim as the GitHub release body.

### 4. Pull request

```bash
git checkout -b for_release_$(date +%Y%m%d)
git add -A && git commit -m "Increment versions, for release"
git push -u origin for_release_$(date +%Y%m%d)
```

Open a PR to `develop`, wait for the Debrief CI build to pass, and merge.

### 5. Tag

Only once the build is green on `develop` at the merge commit:

```bash
git checkout develop && git pull origin develop
TAG=$(python3 contribs/PyScripts/release_versions.py tag-name)
git tag -a "$TAG" -m "Debrief $TAG"
git push origin "$TAG"
```

The build then publishes a release carrying the four platform zips,
`P2_Repository.zip`, the DebriefLegacy jar, the Debrief Lite zip and the Windows
MSI, with `RELEASE_NOTES.md` as its body.

Only release-shaped tags publish — `YYYYMMDD_N_N_N`, e.g. `20250307_3_1_44`. A
scratch tag pushed to trigger a build will not ship a release.

## Version scheme

| Thing | Example | Lives in |
|---|---|---|
| Debrief version | `3.1.45` | `about.mappings` (`0=`), `debriefng.product`, `org.mwc.debrief.product/pom.xml` |
| Bundle version | `1.0.412` | each plugin's `META-INF/MANIFEST.MF` |
| Feature version | `1.1.40` | each feature's `feature.xml` |
| Release tag | `20260922_3_1_45` | git |

`contribs/PyScripts/release_versions.py` owns all of these and keeps them in
step. Do not hand-edit a version during a release; if you already have, run
`./update_pom.sh` to bring the poms back into line.

## Tests

```bash
pip install pytest        # once
python3 -m pytest contribs/PyScripts/tests
```

Worth running before any release. Several of these tests exist because the
previous script got the case wrong — for example it incremented `0.14.1` to
`0.24.2`, and rewrote a feature's `<?xml version="1.0"?>` declaration.

## Recovering

| When | What to do |
|---|---|
| Before the tag | Close the PR, `git checkout -- .`. Nothing is published. |
| Tag pushed, build still running | `git push origin :refs/tags/$TAG` and delete the release. Racy but usually works. |
| Release published | Leave it. People may already have downloaded it — cut a new patch release instead. |
