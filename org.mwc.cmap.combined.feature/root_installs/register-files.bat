
ECHO CREATING DEBREIF REGISTRY

SET WORKING_DIRECTORY=%~dp0
SET PLUGIN_ICON_PATH=%WORKING_DIRECTORY%/file_icons

ECHO Debrief Installed at %WORKING_DIRECTORY%

REG DELETE HKEY_CLASSES_ROOT\debriefTrackFile /F
ASSOC .rep=debriefTrackFile
ftype debriefTrackFile=%WORKING_DIRECTORY%DebriefNG.exe --launcher.openFile %%1 %%*
REG ADD HKEY_CLASSES_ROOT\debriefTrackFile\DefaultIcon  /T REG_SZ /D "%PLUGIN_ICON_PATH%/track_file.ico"

REG DELETE HKEY_CLASSES_ROOT\debriefSensorFile /F
ftype debriefSensorFile=%WORKING_DIRECTORY%DebriefNG.exe --launcher.openFile %%1 %%*
REG ADD HKEY_CLASSES_ROOT\debriefSensorFile\DefaultIcon  /T REG_SZ /D "%PLUGIN_ICON_PATH%/sensor_file.ico"

REG DELETE HKEY_CLASSES_ROOT\debriefPlotFile /F
ASSOC .dpf=debriefPlotFile
ftype debriefPlotFile=%WORKING_DIRECTORY%DebriefNG.exe --launcher.openFile %%1 %%*
REG ADD HKEY_CLASSES_ROOT\debriefPlotFile\DefaultIcon  /T REG_SZ /D "%PLUGIN_ICON_PATH%/plot_file.ico"

REG DELETE HKEY_CLASSES_ROOT\debriefXMLFile /F
ASSOC .xml=debriefXMLFile
ftype debriefXMLFile=%WORKING_DIRECTORY%DebriefNG.exe --launcher.openFile %%1 %%*
REG ADD HKEY_CLASSES_ROOT\debriefXMLFile\DefaultIcon  /T REG_SZ /D "%PLUGIN_ICON_PATH%/plot_file.ico"
 