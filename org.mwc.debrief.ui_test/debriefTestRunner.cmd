SET AUT=D:\Debrief\DebriefNG
SET RUNNER=D:\kl\rcptt\runner\2.4\eclipse
SET PROJECT=.

REM Path to directory with test results, default is C:\Users\User\results
SET RESULTS=%PROJECT%\..\results

IF NOT EXIST %RESULTS% GOTO NORESULTS
RMDIR /S /Q %RESULTS%

:NORESULTS
md %RESULTS%

java -Xmx1000m -jar %RUNNER%\plugins\org.eclipse.equinox.launcher_1.5.0.v20180512-1130.jar ^
 -application org.eclipse.rcptt.runner.headless ^
 -data %RESULTS%/runner-workspace/ ^
 -aut %AUT% ^
 -autVM "C:\Program Files\Java\jdk1.8.0_60" ^
 -autWsPrefix %RESULTS%/aut-workspace ^
 -autConsolePrefix %RESULTS%/aut-output ^
 -htmlReport %RESULTS%/report.html ^
 -junitReport %RESULTS%/report.xml ^
 -import %PROJECT% ^
 -suites TEST_SAMPLE_DATA;TEST_CHART_AND_DRAWING_FEATURES ^
 -testOptions "testExecTimeout=900;jobHangTimeout=30000"