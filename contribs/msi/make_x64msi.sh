#!/bin/bash
#Path to the folder with wixl executables
WIXLDIR=/usr/bin/
#Path to the folder with the compiled Debrief sources
DEBRIEFDIR=org.mwc.debrief.product/target/products/DebriefNG/win32/win32/x86_64/DebriefNG/
#Path to the main wixl harvest folder
SOURCEDIR=contribs/msi/src/
#Path to the wixl resouces folder
RESOURCESDIR=contribs/msi/resources/
#Path to the wixl working folder
WORKDIR=contribs/msi/

echo "Clearing the sources folder."
rm -rf ${SOURCEDIR}
mkdir ${SOURCEDIR}
echo "Done."

echo "Updating version"
version=$(grep "product.*version"  org.mwc.debrief.product/debriefng.product  | sed 's/^.*version="\([^"]*\)".*$/\1/')
sed -i "s/versionReplacement/$version/g" ${WORKDIR}Debrief64.wxs
echo "Done."

echo "Copying Debrief sources to the wixl harvest folder."
cp -r ${DEBRIEFDIR}* ${SOURCEDIR}
echo "Done."

echo "Moving required files to the Resources folder."
mv -f ${SOURCEDIR}DebriefNG.exe ${RESOURCESDIR}
mv -f ${SOURCEDIR}DebriefNG.pdf ${RESOURCESDIR}
mv -f ${SOURCEDIR}DebriefNG_TMA_Tutorial.pdf ${RESOURCESDIR}
mv -f ${SOURCEDIR}IntroductionToDebrief_1-3.pdf ${RESOURCESDIR}
mv -f ${SOURCEDIR}plugins/org.eclipse.ui.ide_3.14.0.v20180517-1842.jar ${RESOURCESDIR}
mv -f ${SOURCEDIR}p2/org.eclipse.equinox.p2.core/cache/binary/org.mwc.cmap.combined.feature_root_1.0.363 ${RESOURCESDIR}

echo "Done."

echo "Creating the wixl harvest file."
find ${SOURCEDIR} | ${WIXLDIR}wixl-heat --var var.SourceDir -p ${SOURCEDIR} --component-group main --directory-ref INSTALLDIR > ${WORKDIR}harvest.wxs
echo "Done."

echo "Creating the x64 Debrief MSI file."
${WIXLDIR}wixl -v -a x64 ${WORKDIR}harvest.wxs ${WORKDIR}Debrief64.wxs -D SourceDir=${SOURCEDIR} -D ResourcesDir=${RESOURCESDIR} -o ${WORKDIR}Debrief64.msi
echo "Done."

echo "Forcing exit failure"
exit 1

FILE=${WORKDIR}Debrief64.msi
if [ -f "$FILE" ]; then
    echo "$FILE successfully created"
else
    echo "PROBLEM: $FILE not created, exiting"
    exit 1
fi

echo "Moving the msi file"
mv contribs/msi/Debrief64.msi org.mwc.debrief.product/target/products/DebriefNG-Windows64Bit.msi
echo "Done."
echo "MSI 64 bits has been created successfully."
