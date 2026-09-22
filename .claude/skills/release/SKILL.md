---
name: release
description: Cut a new release of Debrief. Use when asked to "release Debrief", "cut a release", "do a new Debrief version", "bump versions for release", or to create the release tag. Bumps the versions of changed plugins/features, opens the version PR, waits for green CI, drafts release notes, and creates the tag that publishes the GitHub release.
---

# Releasing Debrief

Debrief releases are published by a git tag. `.github/workflows/main.yml` builds
the product on every push, and its `Upload release` step attaches the installers
to a GitHub release whenever the pushed ref is a release tag. **Pushing the tag
is the point of no return** — it publishes a public release.

Everything before the tag is reversible. Treat step 8 as irreversible.

## Version scheme

| Thing | Example | Lives in |
|---|---|---|
| Marketing version | `3.1.45` | `org.mwc.debrief.core/about.mappings` (`0=`), `org.mwc.debrief.product/debriefng.product`, `org.mwc.debrief.product/pom.xml` |
| Bundle version | `1.0.412` | each plugin's `META-INF/MANIFEST.MF` |
| Feature version | `1.1.40` | each feature's `feature.xml` |
| Release tag | `20260922_3_1_45` | git |

`contribs/PyScripts/release_versions.py` owns all of these and keeps them in
lockstep. Never hand-edit a version during a release.

## Procedure

Work through these in order. Steps 2, 3, 6 and 8 are **hard stops**: ask the
user and wait for an explicit yes. Never skip a stop because the change looks
routine.

### 1. Preflight

```bash
git status --short && git rev-parse --abbrev-ref HEAD
git fetch origin develop --tags
python3 -m pytest contribs/PyScripts/tests -q   # pip install pytest if missing
```

Require: on `develop`, clean working tree, up to date with `origin/develop`,
tests green. If any of these fail, stop and say why.

Report the current version (`about.mappings` `0=`) and the baseline release
(`mcp__github__list_releases`, first entry that is neither draft nor
prerelease).

### 2. Plan the bumps — HARD STOP

```bash
python3 contribs/PyScripts/release_versions.py plan --json
```

Show the user a table of every bundle being bumped (`old → new`), the marketing
version change, the proposed tag, **and the list of changed paths that matched
no bundle**. That last list is the one worth reading: a changed plugin missing
from it means a bundle that will ship without a version bump, so P2 clients
never see the update.

Ask the user to confirm the set before anything is written.

### 3. Draft the release notes — HARD STOP

Collect the pull requests merged into `develop` since the baseline tag:

- `mcp__github__get_tag` / `mcp__github__list_commits` to find the baseline
  commit and its date
- `mcp__github__search_pull_requests` with
  `repo:debrief/debrief is:pr is:merged base:develop merged:>=<baseline date>`

Group them under `### Features`, `### Fixes`, `### Infrastructure` and
`### Other`, deciding by, in order: the branch name prefix (`fix/`, `feat/`),
a conventional-commit prefix in the title, then the linked issue's labels. Debrief
PR titles are not consistently formatted, so expect `Other` to be busy — this is
why a human reads the draft.

Write the result to `RELEASE_NOTES.md` at the repo root, starting with a one-line
summary. The workflow publishes this file verbatim as the release body, so it
replaces the previous release's notes each time.

Show the draft and ask the user to approve or edit it.

### 4. Apply and open the PR

```bash
python3 contribs/PyScripts/release_versions.py apply
git diff --stat
```

`apply` edits the manifests, features, product and about box, runs
`tycho-versions-plugin:update-pom` to sync all 63 module poms, then checks that
the marketing version agrees across `about.mappings`, `debriefng.product` and
`org.mwc.debrief.product/pom.xml`. If it exits non-zero, run
`git checkout -- .` and report the error — do not commit a partial bump.

Show the full diff of the version files (not just the stat), then:

```bash
git checkout -b for_release_$(date +%Y%m%d)
git add -A && git commit -m "Increment versions, for release"
git push -u origin for_release_$(date +%Y%m%d)
```

Open the PR to `develop` with `mcp__github__create_pull_request`, using the
release notes as the PR body so they get reviewed alongside the versions.

### 5. Wait for CI

Poll `mcp__github__actions_list` (`list_workflow_runs` for `main.yml`, filtered
to the release branch) until the run completes. The Debrief build is slow —
report status rather than busy-waiting, and end the turn if it will be a while.

If it fails, fetch the job logs, diagnose, and fix on the release branch. **Do
not continue to step 6 on a red build.**

### 6. Merge — HARD STOP

Ask the user to confirm, then merge the PR and:

```bash
git checkout develop && git pull origin develop
```

### 7. Confirm CI on the merge commit

The PR build is not the same commit as the merge commit. Check the run for
`develop` at the new HEAD and require it green before going any further.

### 8. Tag — HARD STOP, THIS PUBLISHES

Say plainly that this step publishes a public release, name the exact tag, and
wait for an explicit yes.

```bash
TAG=$(python3 contribs/PyScripts/release_versions.py tag-name)
git rev-parse --verify "refs/tags/$TAG" && echo "TAG EXISTS - STOP"
git tag -a "$TAG" -m "Debrief $(...marketing version...)"
git push origin "$TAG"
```

Refuse to proceed if the tag already exists. No MCP tool creates tags, so this
is local git.

### 9. Verify

Poll the tag's workflow run. When it finishes, `mcp__github__get_release_by_tag`
and confirm:

- all seven assets are attached: `DebriefNG-{Linux64Bit,MacOSX64Bit,Windows64Bit}.zip`,
  `P2_Repository.zip`, `DebriefLegacy*.jar`, `debrief-lite*.zip` and
  `DebriefNG-Windows64Bit.msi`. There is no 32-bit Windows build — the
  win32/x86 environment is commented out in the root `pom.xml`.
- the body contains the approved release notes

Report the release URL.

## If something goes wrong

- **Before the tag**: close the PR, `git checkout -- .`, nothing is published.
- **After the tag, before the build finishes**: delete the tag
  (`git push origin :refs/tags/$TAG`). GitHub converts the release to a draft,
  and `releases/latest` ignores drafts - so step 2's baseline is correct again
  immediately. Delete the draft too, or it sits on a few GB of assets.
- **After the release is published**: do not delete it. People may already have
  downloaded it. Cut a new patch release instead.
