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
#URL of the jre to use
JRE_URL=https://github.com/AdoptOpenJDK/openjdk11-binaries/releases/download/jdk-11.0.5%2B10/OpenJDK11U-jre_x64_windows_hotspot_11.0.5_10.zip

echo "Updating version"
version=$(grep "product.*version"  org.mwc.debrief.product/debriefng.product  | sed 's/^.*version="\([^"]*\)".*$/\1/')
sed -i "s/versionReplacement/$version/g" ${WORKDIR}Debrief64.wxs
echo "Done."

if [ ! -d "${SOURCEDIR}jre" ]; then
    echo "Downloading JRE"
    wget -O jre.zip ${JRE_URL}
    unzip -q jre.zip -d ${SOURCEDIR}
    mv ${SOURCEDIR}jdk* ${SOURCEDIR}jre
    rm jre.zip
    echo "Done."
else
    echo "Previous JRE has been detected in the source directory"
fi

echo "Copying Debrief sources to the wixl harvest folder."
cp -r ${DEBRIEFDIR}* ${SOURCEDIR}
echo "Done."

echo "Moving required files to the Resources folder."
mv -f ${SOURCEDIR}DebriefNG.exe ${RESOURCESDIR}
mv -f ${SOURCEDIR}DebriefNG.pdf ${RESOURCESDIR}
mv -f ${SOURCEDIR}DebriefNG_TMA_Tutorial.pdf ${RESOURCESDIR}
mv -f ${SOURCEDIR}IntroductionToDebrief_1-3.pdf ${RESOURCESDIR}
echo "Done."

echo "Creating the wixl harvest file."
find ${SOURCEDIR} | ${WIXLDIR}wixl-heat --var var.SourceDir -p ${SOURCEDIR} --component-group main --directory-ref INSTALLDIR > ${WORKDIR}harvest.wxs
echo "Done."

# cat ${WORKDIR}harvest.wxs

echo "Creating the x64 Debrief MSI file."
${WIXLDIR}wixl -a x64 -v ${WORKDIR}Debrief64.wxs ${WORKDIR}harvest.wxs -D SourceDir=${SOURCEDIR} -D ResourcesDir=${RESOURCESDIR} -o ${WORKDIR}Debrief64.msi
echo "Done."

FILE=${WORKDIR}Debrief64.msi
if [ -f "$FILE" ]; then
    echo "$FILE successfully created"
else 
    echo "PROBLEM: $FILE not created, exiting"
    exit $?
fi

echo "Moving the msi file"
mv contribs/msi/Debrief64.msi org.mwc.debrief.product/target/products/DebriefNG-Windows64Bit.msi
echo "Done."
echo "MSI 64 bits has been created successfully."
