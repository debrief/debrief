#!/bin/bash
#Path to the folder with wixl executables
WIXLDIR=/usr/bin/
#Path to the folder with the compiled Debrief sources
DEBRIEFDIR=org.mwc.debrief.product/target/products/DebriefNG/win32/win32/x86_64/DebriefNG/
#Path to the main wixl harvest folder
SOURCEDIR=msi/src/
#Path to the wixl resouces folder
RESOURCESDIR=msi/resources/
#Path to the wixl working folder
WORKDIR=msi/

echo "Clearing the sources folder."
rm -rf ${SOURCEDIR}
mkdir ${SOURCEDIR}
echo "Done."

echo "Copying Debrief sources to the wixl harvest folder."
cp -r ${DEBRIEFDIR}* ${SOURCEDIR}
echo "Done."

echo "Moving required files to the Resources folder."
mv -f ${SOURCEDIR}DebriefNG.exe ${RESOURCESDIR}
mv -f ${SOURCEDIR}DebriefNG.pdf ${RESOURCESDIR}
mv -f ${SOURCEDIR}DebriefNG_TMA_Tutorial.pdf ${RESOURCESDIR}
mv -f ${SOURCEDIR}DebriefNGTutorial.pdf ${RESOURCESDIR}
echo "Done."

echo "Creating the wixl harvest file."
find ${SOURCEDIR} | ${WIXLDIR}wixl-heat --var var.SourceDir -p ${SOURCEDIR} --component-group main --directory-ref INSTALLDIR > ${WORKDIR}harvest.wxs
echo "Done."

echo "Creating the x64 Debrief MSI file."
${WIXLDIR}wixl -a x64 -v ${WORKDIR}Debrief64.wxs ${WORKDIR}harvest.wxs -D SourceDir=${SOURCEDIR} -D ResourcesDir=${RESOURCESDIR} -o ${WORKDIR}Debrief64.msi

mv msi/Debrief32.msi org.mwc.debrief.product/target/products/DebriefNG-Windows64Bit.msi
echo "Done."
echo "MSI 64 bits has been created successfully."
