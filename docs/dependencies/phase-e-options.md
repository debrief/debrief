# Phase E: test tooling options

For the owner's decision at Gate D (see [plan.md](plan.md), Phase E). Facts are as of November 2026, on `develop`.

## Decisions (owner, November 2026)

1. **RCPTT: retire the runner, keep the scripts as a checklist, and plan a new small automated RCP UI suite** (SWTBot or current RCPTT) covering the main paths, written against the current product. It should be in place before the GeoTools upgrade (Phase D1), which changes the RCP map.
2. **JUnit: full migration, to JUnit 6** (the owner chose 6.1.3 over 5.14.4: the same Jupiter API, and the current line). **Done**, in one PR rather than the four stages planned below:
   - **Inline tests out of production code.** 353 test classes moved to a `test/` folder in each plugin, in the same package (334 nested in production classes, 19 whole files). Members the tests used were widened from `private` to package-private. Test-support code that tests in other plugins share (`Editable.editableTesterSupport`, `WatchableList.TestWatchables`, `TimeController.doTests`, the golden-file helpers) stays in production but no longer uses JUnit: it checks with `MWC.Utilities.Errors.Testing.Checks`. Developer `main()` methods that ran tests by hand were removed.
   - **Converted to Jupiter** with OpenRewrite (`MigrateJUnitTestCase`, `JUnit4to5Migration`), then scripts for what it missed: JUnit 3 named constructors removed, `@Test` added to classes on custom `TestCase` bases, and assertion messages moved last using the argument types javac reported.
   - **Runner.** `maven-surefire-plugin` runs each plugin's `test/` folder with the classpath Tycho computes (root `pom.xml`). The `test2` and `asset.test` suite bundles are gone. Lite's Ant build uses `<junitlauncher>` and compiles its tests apart from `debrief_lite.jar`. `org.mwc.debrief.editable.test` stays an OSGi test plugin, with JUnit 6 from the target platform.
   - **JUnit 4 and hamcrest are gone**: from `cmap.legacy` (its re-export and `libs/`), the target platform, the shipped features and products, Lite's `lib/`, and Dependabot's rules.
   - **More tests run.** Every class the old `test2` suite listed still runs. Tests that were in no suite (or, for ASSET, in no build) now run too. Those that fail were already broken and had never run, so they are `@Disabled` with the reason, for someone to fix or delete.
3. **Stale test libraries: done.** jmock deleted; mockito-all removed, with its two users rewritten. The RCPTT runner is deleted and its scripts are now [a manual checklist](../testing/debrief-ng-smoke-checklist.md).

### JUnit migration: the stages as planned

The plan was four PRs (run both engines, move the tests out, convert, remove JUnit 4). The owner asked for one; see the decision above.

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
- **mockito-all** (1.8.5 when this was written; Dependabot later took it to 1.10.19, from 2014), fetched into `cmap.core`, has two users:
  - `track_shift/.../magic/OptimiseTest.java` (a test);
  - `asset.ScenarioController2/.../MultiScenarioPresenter.java`, which is production code in ASSET (no longer released).
  
  `cmap.core` exports 16+ Mockito packages to every bundle. The fix is one of:
  - move to `mockito-core` 5.x. That needs `byte-buddy` and `objenesis` too, and Mockito 5 needs Java 11+, which is fine here;
  - remove the two uses and the jar.
  
  Recommend the second if the ASSET presenter's use is test scaffolding, which its imports (`doAnswer`, `anyObject`) suggest.

**Recommendation:** Do both in one small PR after your answer on question 1, since it may delete `ui_test` in the same pass.
