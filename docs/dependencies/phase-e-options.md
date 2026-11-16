# Phase E: test tooling options

For the owner's decision at Gate D (see [plan.md](plan.md), Phase E). Facts are as of November 2026, on `develop`.

## Decisions (owner, November 2026)

1. **RCPTT: retire the runner, keep the scripts as a checklist, and plan a new small automated RCP UI suite** (SWTBot or current RCPTT) covering the main paths, written against the current product. It should be in place before the GeoTools upgrade (Phase D1), which changes the RCP map.
2. **JUnit: full migration to JUnit 5.** This means moving the inline tests out of production classes first, then converting about 280 test classes. It is done in stages (below).
3. **Stale test libraries:** delete jmock; remove or replace mockito-all 1.8.5.

### JUnit 5 migration: stages

Each stage is its own PR or set of PRs, and CI stays green throughout.

1. **Run both.** Add the JUnit Platform with the vintage and Jupiter engines:
   - in Tycho Surefire (Jupiter and Platform bundles from the Eclipse release);
   - in Lite's Ant build (`<junit>` → `<junitlauncher>`).
   
   Existing tests run unchanged; new tests can be Jupiter.
2. **Move inline tests out of production code**, one plugin at a time, starting with the smaller ones and ending with `debrief.legacy` (51 files):
   - each `TestMe` inner class moves to a test class in a test source folder;
   - `junit.framework` leaves the bundles' `uses:` clauses and Lite's shipped `lib/`.
3. **Convert to Jupiter**, plugin by plugin: `TestCase` → `@Test`, `assertEquals` argument order kept, suites → `@Suite` or Surefire includes. The golden-file and screenshot helpers (`GoldenFile`, `GoldenImage`) move to JUnit 5 assertions.
4. **Remove JUnit 4**: the vintage engine, `junit.jar`, `hamcrest-core.jar`, and their Dependabot rules.

The options and reasons follow, as written for the decision.

## Summary

| Decision | Options | Recommendation |
|---|---|---|
| 1. RCPTT runner | A. Retire it. B. Revive it on the RCPTT Maven plugin, in CI. C. Retire the runner, keep the test scripts as a manual checklist | **C** |
| 2. JUnit | A. Stay on JUnit 4.13.2. B. Add JUnit 5 for new tests, old ones unchanged (vintage engine). C. Migrate everything to JUnit 5 | **A now**, B when someone needs it |
| 3. Unused or stale test libraries | Delete jmock; replace or drop mockito-all 1.8.5 | **Do it** (small, no decision needed beyond yes) |

## 1. RCPTT runner (`org.mwc.debrief.ui_test`)

### What is there

- **22 RCPTT test scripts** (`*.test`) in 5 suites: sample data, chart and drawing features, viewing data, sensor analysis, and controlling the view. They drive Debrief NG (the Eclipse RCP product) through its UI.
- **A committed RCPTT runner**: `rcptt-runner/`, 959 files, 161 MB, a 2.5.0 **snapshot** build. It is the largest third-party payload left in the repo. The dependency scan reports it separately because nobody ships it.
- **The module is not built.** It is commented out of the root `pom.xml`, and no workflow runs it. Its `pom.xml` names `rcptt-maven-plugin` 2.4.2 and the snapshot repository. `debriefTestRunner.cmd` hard-codes Windows paths and a Java 8 JDK.
- The scripts were last changed in March 2025 (removing SATC tests). Nothing shows when they last passed.

### What else now covers the UI

- **Debrief Lite**: Swing UI tests (ribbons, menus, launch) and the main-window screenshots, run in CI under xvfb.
- **Both products**: headless tests of file round trips, map rendering screenshots and projection accuracy.
- **Debrief NG (RCP)**: no automated UI test runs today. The RCP-side unit tests run under Tycho Surefire, but nothing clicks through the Eclipse UI.

### Options

**A. Retire.** Delete `org.mwc.debrief.ui_test`.
- Removes 161 MB and 959 files, and the scan's largest separate report.
- Loses the only description of an RCP walkthrough.

**B. Revive.** Fetch the runner through the current RCPTT Maven plugin (no committed runner) and add a CI job that builds the product and runs the suites under xvfb.
- Gains real RCP UI regression tests. That matters most before Phase D1 (GeoTools), which changes the RCP map too.
- The cost is unknown and probably high. The scripts have not run against Eclipse 4.41 or Java 21. RCPTT tests are brittle against UI changes, and every failure needs someone who reads the ECL scripting language.
- It adds a slow job, building the full product and then driving it, to every PR.

**C. Retire the runner, keep the scripts as a checklist.**
- Delete `rcptt-runner/`, the `pom.xml` and the `.cmd`.
- Turn the 22 test names and their steps into a short manual smoke-test list in `docs/`, used for the owner's walkthrough at each phase's acceptance. The plan already asks for manual walkthroughs (D2: "the owner does a manual walkthrough on all three operating systems").
- It gets A's savings and keeps the knowledge of what to check.

**Recommendation: C.** The runner hasn't run for a long time and isn't in the build, so it protects nothing today. Reviving it (B) is a project of its own with an uncertain outcome. If RCP UI regressions become a real worry, a fresh start is likely cheaper than reviving these scripts. That could be a few SWTBot or RCPTT tests of the main paths, written against the current product.

**Question for you:** Is unattended UI testing of Debrief NG (RCP) worth a new investment? If not, C. If yes, C now, then plan a small fresh suite rather than B.

## 2. JUnit

### Where it stands

- **JUnit 4.13.2** (the last 4.x release, 2021) with **hamcrest-core 1.3**, fetched from Maven Central since Phase B. It has no open vulnerabilities: CVE-2020-15250 was fixed in 4.13.1. Dependabot ignores hamcrest majors, which JUnit 4 can't use.
- **Most tests are JUnit 3 style.** 263 Java files use `TestCase` or `junit.framework`, and 20 use JUnit 4 annotations. None use JUnit 5.
- **103 production source files contain their own tests** as inner classes (for example `TestMe`), mostly in `debrief.legacy` (51), `cmap.legacy` (17) and `debrief.core` (14). As a result:
  - `junit.framework` is on the production classpath and in several bundles' `Export-Package` `uses:` clauses;
  - JUnit ships inside Debrief Lite's `lib/`.
- **Runners:**
  - Tycho Surefire runs the RCP suites (`AllTests`, `*TestSuite*`).
  - Ant's `<junit>` task runs Lite's headless and UI tests. That task only understands JUnit 3 and 4.

### Options

**A. Stay on JUnit 4.13.2.**
- No work.
- JUnit 4 is in maintenance only, but it is stable and has no open CVEs. Every runner here supports it.

**B. Add JUnit 5 alongside, for new tests only.**
- The JUnit Platform with the vintage engine runs the existing tests unchanged. New tests can use Jupiter.
- Needs the Jupiter and Platform bundles in the target platform (Eclipse releases ship them) and Tycho Surefire set to the JUnit 5 provider.
- Needs Lite's Ant build moved from `<junit>` to `<junitlauncher>`, so the CI wiring changes too.
- A few days of work. The benefit (nicer assertions, parameterised tests) only arrives when people write new tests.

**C. Migrate everything to JUnit 5.**
- Means rewriting about 280 test classes. The inline tests in production code would need to move out first, or JUnit 5 would become a production dependency.
- Large, and it doesn't change what is tested.

**Recommendation: A now, B when there is a reason.** The plan's original line ("JUnit 4.12 → JUnit 5 with the vintage engine") predates Phase B. JUnit is now 4.13.2 and comes from Maven Central, so the security case for moving has gone. Do B when a new piece of work wants Jupiter features, or if JUnit 4 picks up an unfixed vulnerability. C is not worth it.

Moving the inline tests out of production classes would be a separate, worthwhile clean-up. It would stop JUnit shipping in Lite and simplify the bundles' exports. It is a large change across about 100 files, though, best done package by package alongside other work there.

**Question for you:** Stay on JUnit 4 (A) for now?

## 3. Stale test libraries

- **jmock 2.5.1** (`jmock-legacy`, `jmock-junit4`) is declared in the parent `pom.xml` for Tycho Surefire, but no Java file imports `org.jmock`. Delete both, and drop the Dependabot ignore rule for `org.jmock:*`.
- **mockito-all 1.8.5** (2010), fetched into `cmap.core`, has two users:
  - `track_shift/.../magic/OptimiseTest.java` (a test);
  - `asset.ScenarioController2/.../MultiScenarioPresenter.java`, which is production code in ASSET (no longer released).
  
  `cmap.core` exports 16+ Mockito packages to every bundle. The fix is one of:
  - move to `mockito-core` 5.x. That needs `byte-buddy` and `objenesis` too, and Mockito 5 needs Java 11+, which is fine here;
  - remove the two uses and the jar.
  
  Recommend the second if the ASSET presenter's use is test scaffolding, which its imports (`doAnswer`, `anyObject`) suggest.

**Recommendation:** Do both in one small PR after your answer on question 1, since it may delete `ui_test` in the same pass.
