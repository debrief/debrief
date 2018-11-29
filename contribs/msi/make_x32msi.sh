#!/bin/bash
#Path to the folder with wixl executables
WIXLDIR=/usr/bin/
#Path to the folder with the compiled Debrief sources
DEBRIEFDIR=org.mwc.debrief.product/target/products/DebriefNG/win32/win32/x86/DebriefNG/
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

echo "Updating version"
version=$(grep "product.*version"  org.mwc.debrief.product/debriefng.product  | sed 's/^.*version="\([^"]*\)".*$/\1/')
sed -i "s/versionReplacement/$version/g" ${WORKDIR}Debrief32.wxs
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

echo "Creating the x86 Debrief MSI file."
${WIXLDIR}wixl -v ${WORKDIR}harvest.wxs ${WORKDIR}Debrief32.wxs -D SourceDir=${SOURCEDIR} -D ResourcesDir=${RESOURCESDIR} -o ${WORKDIR}Debrief32.msi

mv msi/Debrief32.msi org.mwc.debrief.product/target/products/DebriefNG-Windows32Bit.msi
echo "Done."
echo "MSI 32 bits has been created successfully."
