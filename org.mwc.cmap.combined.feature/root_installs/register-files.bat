ECHO CREATING DEBRIEF REGISTRY

SET ICON_PATH=%~dp0\file_icons
SET FILE_COMMAND="%~dp0\DebriefNG.exe --launcher.openFile %%1 %%*"

SET FILE_APP=HKEY_CURRENT_USER\Software\Classes\Applications
SET FILE_EXT=HKEY_CURRENT_USER\Software\Microsoft\Windows\CurrentVersion\Explorer\FileExts

:: Plot files Registration. (.dpf)
SET APP=DPF.exe
SET EXT=.dpf

REG DELETE %FILE_APP%\%APP% /F
REG DELETE %FILE_EXT%\%EXT% /F

REG ADD %FILE_APP%\%APP% /v FriendlyTypeName /t REG_SZ /d  "plot file"
REG ADD %FILE_APP%\%APP%\shell\open\command /t REG_SZ /d  %FILE_COMMAND%
REG ADD %FILE_APP%\%APP%\DefaultIcon /t REG_SZ /d  %ICON_PATH%\plot_file.ico
REG ADD %FILE_EXT%\%EXT% /v \"Application\" /t REG_SZ /d %APP% /F
REG ADD %FILE_EXT%\%EXT%\OpenWithList /v "a" /t REG_SZ /d %APP% /F
REG ADD %FILE_EXT%\%EXT%\UserChoice /v "ProgId" /t REG_SZ /d Applications\%APP% /F
::END Plot files registration

:: Sensor files Registration. (.dsf)

SET APP=DSF.exe
SET EXT=.dsf

REG DELETE %FILE_APP%\%APP% /F
REG DELETE %FILE_EXT%\%EXT% /F

REG ADD %FILE_APP%\%APP% /v FriendlyTypeName /t REG_SZ /d  "sensor file"
REG ADD %FILE_APP%\%APP%\DefaultIcon /t REG_SZ /d  %ICON_PATH%\sensor_file.ico
REG ADD %FILE_EXT%\%EXT% /v \"Application\" /t REG_SZ /d %APP% /F
REG ADD %FILE_EXT%\%EXT%\OpenWithList /v "a" /t REG_SZ /d %APP% /F
REG ADD %FILE_EXT%\%EXT%\UserChoice /v "ProgId" /t REG_SZ /d Applications\%APP% /F
::END Sensor files registration.

::Track File registration  (.rep)
SET APP=REP.exe
SET EXT=.rep

REG DELETE %FILE_APP%\%APP% /F
REG DELETE %FILE_EXT%\%EXT% /F

REG ADD %FILE_APP%\%APP% /v FriendlyTypeName /t REG_SZ /d  "track file"
REG ADD %FILE_APP%\%APP%\shell\open\command /t REG_SZ /d  %FILE_COMMAND%
REG ADD %FILE_APP%\%APP%\DefaultIcon /t REG_SZ /d  %ICON_PATH%\track_file.ico
REG ADD %FILE_EXT%\%EXT% /v \"Application\" /t REG_SZ /d %APP% /F
REG ADD %FILE_EXT%\%EXT%\OpenWithList /v "a" /t REG_SZ /d %APP% /F
REG ADD %FILE_EXT%\%EXT%\UserChoice /v "ProgId" /t REG_SZ /d Applications\%APP% /F

::End Track File registration

:: Adding Debrief.exe as a XML OpenWith application list.
REG ADD %FILE_EXT%\.xml\OpenWithList /v "a" /t REG_SZ /d %APP% /F
REG ADD %FILE_EXT%\.xml\OpenWithList /v "MRUList" /t REG_SZ /d a /F
REG ADD %FILE_EXT%\.xml\UserChoice /v "ProgId" /t REG_SZ /d Applications\%APP% /F
::End