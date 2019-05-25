Param(
[string]$Version
)


#Setting the build folder variable.
$BuildDir = "C:\Debfrief"

#Setting the product version variable. Could be overridden by parameter.
If ($Version -eq ""){$Version  = "1.0.0"}

#Setting the Wix variables.
$Heat = "$Env:Wix" + "bin\heat.exe"
$Candle = "$Env:Wix" + "bin\candle.exe"
$Light = "$Env:Wix" + "bin\light.exe"

Write-Host "Debrief x64 MSI build script started."

Write-Host "`nDownloading archive with sources from the GitHub..." #(disabled by the customer's request)
[Net.ServicePointManager]::SecurityProtocol = "tls12, tls11, tls"
#Invoke-WebRequest -Uri "https://github.com/debrief/debrief/releases/latest/DebriefNG-Windows64Bit.zip" -OutFile "$BuildDir\DebriefNG-Windows64Bit.zip"

#Checking the arhive availiblity. 
If (!(Test-Path "$BuildDir\DebriefNG-Windows64Bit.zip"))
    {
    Write-Host "Archive with the Debrief sources not found! Exiting." -ForegroundColor Red 
    Exit
    }
Write-Host "Done." -ForegroundColor Green

Write-Host "`nClearing the build folder..."
If (Test-Path "$BuildDir\DebriefNG"){Remove-Item -Path "$BuildDir\DebriefNG" -Recurse}
If (Test-Path "$BuildDir\harvest.wxs"){Remove-Item -Path "$BuildDir\harvest.wxs"}
If (Test-Path "$BuildDir\harvest.wixobj"){Remove-Item -Path "$BuildDir\harvest.wixobj"}
If (Test-Path "$BuildDir\DebriefNG64.wixobj"){Remove-Item -Path "$BuildDir\DebriefNG64.wixobj"}
If (Test-Path "$BuildDir\DebriefNG64.msi"){Remove-Item -Path "$BuildDir\DebriefNG64.msi"}
Write-Host "Done." -ForegroundColor Green

Write-Host "`nExtracting the archive content..."
Expand-Archive -Path "$BuildDir\DebriefNG-Windows64Bit.zip" -DestinationPath $BuildDir -Force
Write-Host "Done." -ForegroundColor Green

Write-Host "`nGenerating the Wix harvest file..."
Start-Process -FilePath "$Heat" -ArgumentList "dir $BuildDir\DebriefNG -var var.SourceDir -cg main -nologo -dr INSTALLDIR -t $BuildDir\filter.xlst -gg -srd -sreg -out $BuildDir\harvest.wxs" -Wait -WindowStyle Hidden
Write-Host "Done." -ForegroundColor Green

Write-Host "`nBuilding the Wix object files..."
Start-Process -FilePath "$Candle" -ArgumentList "$BuildDir\harvest.wxs -out $BuildDir\harvest.wixobj -nologo -dSourceDir=$BuildDir\DebriefNG -ext WixUIExtension" -Wait -WindowStyle Hidden
Start-Process -FilePath "$Candle" -ArgumentList "$BuildDir\DebriefNG64.wxs -out $BuildDir\DebriefNG64.wixobj -nologo -dSourceDir=$BuildDir\DebriefNG -dBuildDir=$BuildDir -dVersion=$Version -ext WixUIExtension" -Wait -WindowStyle Hidden
Write-Host "Done." -ForegroundColor Green

Write-Host "`nBuilding the Debrief MSI..."
Start-Process -FilePath "$Light" -ArgumentList "-out $BuildDir\DebriefNG64.msi -b $BuildDir -nologo -spdb $BuildDir\DebriefNG64.wixobj -spdb $BuildDir\harvest.wixobj -ext WixUIExtension" -Wait -WindowStyle Hidden
#Checking the MSI availability. 
If (!(Test-Path "$BuildDir\DebriefNG64.msi"))
    {
    Write-Host "MSI build failed! Exiting." -ForegroundColor red
    Exit
    }
Write-Host "Done." -ForegroundColor Green

Write-Host "`nAll operations completed successfully." 
