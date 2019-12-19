#!/bin/bash

compositeArtifactsFile="compositeArtifacts.xml"
compositeContentFile="compositeContent.xml"
p2index="p2.index"
unzipLocation="updates"

checkIfFileExist() {
        if [ ! -f "$1" ]; then
                echo "$1 not found"
                exit 1
        fi;
}

checkDirectory() {
        checkIfFileExist "$compositeArtifactsFile"
        checkIfFileExist "$compositeContentFile"
        checkIfFileExist "$p2index"

        local zipFiles=(`ls | grep .zip$ | sort -r | head -2`)
        local zipFilesLen=${#zipFiles[@]}

        if [ "$zipFilesLen" -eq 0 ]; then
                echo "Fail: need to be one zip file in the current directory"
                exit 1
        fi;

        if [ ! "$zipFilesLen" -eq 1 ]; then 
                echo "Fail: only one zip is allowed in the current directory"
                echo "${zipFiles[*]}"
                exit 1
        fi;

}

getCurrentDate() {
        currentDate=`date +"%Y_%m_%d__%H_%M_%S"`
        echo "$currentDate"
}

exitIfIsEmptyString() {
        if [ -z "$1" ]; then
                echo "$2 tag not found"
                exit 1
        fi;
}

# Add a new subnode inside the /repository/children node
# 
# $1 - Xml file path
# $2 - Location atribute value of the new subnode
updateXMLFile() {
        local tempFile="temp.xml"
        local backupFile="$1-backup.xml"
        local resultFile="result.xml"
        local startRepositoryTag=`grep -n "<repository " "$1" | cut -f1 -d:`
        local endRepositoryTag=`grep -n "</repository>" "$1" | cut -f1 -d:`
        local initialTotalLines=`wc -l "$1" | awk '{ print $1}'`

        exitIfIsEmptyString  "$startRepositoryTag" "<repository "
        exitIfIsEmptyString "$endRepositoryTag" "</repository>"
       
        #copy the file
        # cp "$1" "$backupFile"

        cat "$1" | sed -n "${startRepositoryTag}, ${endRepositoryTag}p" > "$tempFile"

        local childrenTagIndex=`grep -n "<children " "$tempFile" | cut -f1 -d:`
        local endChildrenTagIndex=`grep -n "</children>" "$tempFile" | cut -f1 -d:`

        exitIfIsEmptyString "$childrenTagIndex" "<children"
        exitIfIsEmptyString "$endChildrenTagIndex" "</children"

        local valueToBeCopied=$((startRepositoryTag + childrenTagIndex - 2))
        cat "$1" | sed -n "1, ${valueToBeCopied}p" > "$resultFile"

        old_IFS=$IFS
        IFS=$'\n'
        local lines=($(cat "$tempFile")) # array
        IFS=$old_IFS

        local totalLines=${#lines[@]}

        local childrenLine="${lines[$childrenTagIndex-1]}"
        local sizeValue=`sed "s/.* size='\(.*\)'.*/\1/" <<< "$childrenLine"`
        
        if [ "$sizeValue" == "$childrenLine" ]; then
                echo "Size atr not found on the repositor/children tag"
                exit 1
        fi;
 
        sizeValue=$((sizeValue + 1))
        local updatedLine=`echo "$childrenLine" | sed -E "s/(size=')[^']+'/\1$sizeValue'/"`

        echo "$updatedLine" >> "$resultFile"
        for ((i="$childrenTagIndex";i<"$endChildrenTagIndex"-1;i++))
        do
                echo "${lines[$i]}" >> "$resultFile"
        done

        local leftSpaces=`awk -F'[^ ^\t]' '{print length($1)}' <<< "${lines[$childrenTagIndex]}"`

        local newNode="<child location='$2'>"
        local lengthNewNode=`echo "$newNode" | wc -c`

        local totalPadding=$((leftSpaces + lengthNewNode -1))

        if [ $((endChildrenTagIndex - childrenTagIndex)) -eq 1 ]; then
                totalPadding=$((totalPadding + leftSpaces))
        fi;

        printf "%${totalPadding}s\n"  "$newNode" >> "$resultFile"

        for ((i=="$endChildrenTagIndex";i<"$totalLines"-1;i++)) {
                echo "${lines[$i]}" >> "$resultFile"
        }

        cat "$1" | sed -n "${endRepositoryTag}, ${initialTotalLines}p" >> "$resultFile"
        mv "$resultFile" "$1"
        
        rm "$tempFile"
}


checkDirectory

zipFiles=(`ls | grep .zip$ | sort -r | head -2`)
zipFile="${zipFiles[0]}" # find result is on this format './[file_name]'


currentDate="$(getCurrentDate)" 
newName="$currentDate.zip"

echo "Valid directory, doing update"

unzipFolder="$unzipLocation/$currentDate"

updateXMLFile "$compositeContentFile" "$unzipFolder"
updateXMLFile "$compositeArtifactsFile" "$unzipFolder"

echo "Unpacking repository"

# check if the destination directory is a dir
if [ ! -d "$unzipFolder" ]; then
        mkdir -p "$unzipFolder"
fi;

unzip -q "$zipFile" -d "$unzipFolder"

echo "Deleting the zip file"
rm "$zipFile"

echo "== COMPLETE =="