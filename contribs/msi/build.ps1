#Debrief x64 MSI build script for WIX toolset
#Author: O.Pohorielov
#2019

Param(
[string]$Version
)

#Setting the build folder variable.
$BuildDir = ".\"
#Setting the JRE folder variable.
$JreDir = "$BuildDir\jdk8u212-b03-jre"
#Setting the sources folder variable.
$SourceDir = "$BuildDir\DebriefNG"

#Setting the product version variable. Could be overridden by parameter.
If ($Version -eq ""){$Version  = "3.0.480"}

#Setting the Wix variables.
$Heat = "$Env:Wix" + "bin\heat.exe"
$Candle = "$Env:Wix" + "bin\candle.exe"
$Light = "$Env:Wix" + "bin\light.exe"

Function Check-File {
Param($File,$Errmsg)
If (!(Test-Path "$File"))
    {
    Write-Host "$Errmsg" -ForegroundColor red
    Exit
    }
}

Write-Host "Debrief x64 MSI build script started."

Write-Host "`nDownloading archive with sources from the GitHub..." #(disabled by the customer's request)
[Net.ServicePointManager]::SecurityProtocol = "tls12, tls11, tls"
#Invoke-WebRequest -Uri "https://github.com/debrief/debrief/releases/latest/DebriefNG-Windows64Bit.zip" -OutFile "$BuildDir\DebriefNG-Windows64Bit.zip"

#Checking the arhive availiblity. 
Check-File -File  "$BuildDir\DebriefNG-Windows64Bit.zip" -Errmsg "Archive with the Debrief sources not found! Exiting."
Write-Host "Done." -ForegroundColor Green

#Download and exctration of the JRE archive from the GitHub
If (!(Test-Path "$JreDir"))
    {
    If (!(Test-Path "$BuildDir\OpenJDK8U-jre_x64_windows_hotspot_8u212b03.zip"))
        {
        Write-Host "`nDownloading JRE archive from the GitHub..."
        Invoke-WebRequest -Uri "https://github.com/AdoptOpenJDK/openjdk8-binaries/releases/download/jdk8u212-b03/OpenJDK8U-jre_x64_windows_hotspot_8u212b03.zip" -OutFile "$BuildDir\OpenJDK8U-jre_x64_windows_hotspot_8u212b03.zip"
        Write-Host "Done." -ForegroundColor Green
        Write-Host "`nExtracting the JRE archive content..."
        Expand-Archive -Path "$BuildDir\OpenJDK8U-jre_x64_windows_hotspot_8u212b03.zip" -DestinationPath $BuildDir -Force
        Write-Host "Done." -ForegroundColor Green
        }
    Else
        {
        Write-Host "`nExtracting the JRE archive content..."
        Expand-Archive -Path "$BuildDir\OpenJDK8U-jre_x64_windows_hotspot_8u212b03.zip" -DestinationPath $BuildDir -Force
        Write-Host "Done." -ForegroundColor Green
        }
    }

Write-Host "`nClearing the build folder..."
If (Test-Path "$SourceDir"){Remove-Item -Path "$SourceDir" -Recurse}
If (Test-Path "$BuildDir\harvest.wxs"){Remove-Item -Path "$BuildDir\harvest.wxs"}
If (Test-Path "$BuildDir\harvest.wixobj"){Remove-Item -Path "$BuildDir\harvest.wixobj"}
If (Test-Path "$BuildDir\DebriefNG64.wixobj"){Remove-Item -Path "$BuildDir\DebriefNG64.wixobj"}
If (Test-Path "$BuildDir\DebriefNG64.msi"){Remove-Item -Path "$BuildDir\DebriefNG64.msi"}
If (Test-Path "$BuildDir\build.log"){Remove-Item -Path "$BuildDir\build.log"}
Write-Host "Done." -ForegroundColor Green

Write-Host "`nExtracting the Debrief archive content..."
Expand-Archive -Path "$BuildDir\DebriefNG-Windows64Bit.zip" -DestinationPath $BuildDir -Force
Write-Host "Done." -ForegroundColor Green

Write-Host "`nCopying the JRE to the sources folder..."
Check-File -File  "$JreDir" -Errmsg "JRE folder not found! Exiting."
New-Item -Path "$SourceDir" -Name "jre" -ItemType Directory -force
Copy-Item -Path "$JreDir\*" -Destination "$SourceDir\jre" -Recurse
Write-Host "Done." -ForegroundColor Green

Write-Host "`nGenerating the Wix harvest file..."
Start-Process -FilePath "$Heat" -ArgumentList "dir $SourceDir -var var.SourceDir -cg main -nologo -dr INSTALLDIR -t $BuildDir\filter.xlst -gg -srd -sreg -out $BuildDir\harvest.wxs"  -Wait -WindowStyle Hidden -RedirectStandardOutput "$BuildDir\build.log"
Get-Content -Path "$BuildDir\build.log"
Check-File -File  "$BuildDir\harvest.wxs" -Errmsg "Wix harvest file generation failed! Exiting."
Write-Host "Done." -ForegroundColor Green

Write-Host "`nBuilding the Wix object files..."
Start-Process -FilePath "$Candle" -ArgumentList "$BuildDir\harvest.wxs -out $BuildDir\harvest.wixobj -nologo -dSourceDir=$SourceDir -ext WixUIExtension" -Wait -WindowStyle Hidden -RedirectStandardOutput "$BuildDir\build.log"
Get-Content -Path "$BuildDir\build.log"
Check-File -File  "$BuildDir\harvest.wixobj" -Errmsg "Wix object file building failed! Exiting."
Start-Process -FilePath "$Candle" -ArgumentList "$BuildDir\DebriefNG64.wxs -out $BuildDir\DebriefNG64.wixobj -nologo -dSourceDir=$SourceDir -dBuildDir=$BuildDir -dVersion=$Version -ext WixUIExtension" -Wait -WindowStyle Hidden -RedirectStandardOutput "$BuildDir\build.log"
Get-Content -Path "$BuildDir\build.log"
Check-File -File  "$BuildDir\DebriefNG64.wixobj" -Errmsg "Wix object file building failed! Exiting."
Write-Host "Done." -ForegroundColor Green

Write-Host "`nBuilding the Debrief MSI..."
Start-Process -FilePath "$Light" -ArgumentList "-out $BuildDir\DebriefNG64.msi -b $BuildDir -nologo -spdb $BuildDir\DebriefNG64.wixobj -spdb $BuildDir\harvest.wixobj -ext WixUIExtension" -Wait -WindowStyle Hidden -RedirectStandardOutput "$BuildDir\build.log"
Get-Content -Path "$BuildDir\build.log"
Check-File -File  "$BuildDir\DebriefNG64.msi" -Errmsg "MSI build failed! Exiting."
Write-Host "Done." -ForegroundColor Green

Write-Host "`nAll operations completed successfully." 