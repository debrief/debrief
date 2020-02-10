@rem ***************************************************************************
@rem Debrief - the Open Source Maritime Analysis Application
@rem http://debrief.info
@rem  
@rem (C) 2000-2020, Deep Blue C Technology Ltd
@rem  
@rem This library is free software; you can redistribute it and/or
@rem modify it under the terms of the Eclipse Public License v1.0
@rem (http://www.eclipse.org/legal/epl-v10.html)
@rem  
@rem This library is distributed in the hope that it will be useful,
@rem but WITHOUT ANY WARRANTY; without even the implied warranty of
@rem MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
@rem ***************************************************************************
@ECHO CREATING DEBRIEF REGISTRY
@ECHO OFF
SET ICON_PATH=%~dp0\file_icons
SET FILE_COMMAND="%~dp0\DebriefNG.exe --launcher.openFile %%1 %%*"

SET FILE_APP=HKEY_CURRENT_USER\Software\Classes\Applications
SET FILE_EXT=HKEY_CURRENT_USER\Software\Microsoft\Windows\CurrentVersion\Explorer\FileExts

:: Plot files Registration. (.dpf)
SET APP=DPF.exe
SET EXT=.dpf

REG DELETE %FILE_APP%\%APP% /F
REG DELETE %FILE_EXT%\%EXT% /F

REG ADD %FILE_APP%\%APP% /v FriendlyTypeName /t REG_SZ /d  "Debrief Plot"
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

REG ADD %FILE_APP%\%APP% /v FriendlyTypeName /t REG_SZ /d  "Debrief Sensor Data"
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

REG ADD %FILE_APP%\%APP% /v FriendlyTypeName /t REG_SZ /d  "Debrief Track Data"
REG ADD %FILE_APP%\%APP%\shell\open\command /t REG_SZ /d  %FILE_COMMAND%
REG ADD %FILE_APP%\%APP%\DefaultIcon /t REG_SZ /d  %ICON_PATH%\track_file.ico
REG ADD %FILE_EXT%\%EXT% /v \"Application\" /t REG_SZ /d %APP% /F
REG ADD %FILE_EXT%\%EXT%\OpenWithList /v "a" /t REG_SZ /d %APP% /F
REG ADD %FILE_EXT%\%EXT%\UserChoice /v "ProgId" /t REG_SZ /d Applications\%APP% /F
::End Track File registration
