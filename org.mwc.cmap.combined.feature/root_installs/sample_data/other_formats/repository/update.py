import datetime
import os
import xml.etree.cElementTree as ET
import sys
from zipfile import ZipFile

# see if this filename is in the array of filenames
def checkPresent(arr, fileName, curResult):
    # don't bother testing if we've already failed
    if curResult is False:
        return False
    if fileName not in arr:
        print (fileName + " is missing")
        return False
    else:
        return True

def checkFolder():
    # do some checks on this folder
    arr = os.listdir()

    # initialise success flag
    result = True

    result = checkPresent(arr, "compositeContent.xml", result)
    result = checkPresent(arr, "compositeArtifacts.xml", result)
    result = checkPresent(arr, "p2.index", result)

    if result is True:
        zipFound = findZipFile()
        if zipFound is None:
            print("Zip-file not found in folder")
            result = False

    return result

def getDTG():
    now = datetime.datetime.now()
    return now.strftime("%Y_%m_%d__%H_%M_%S")

# see if there is a zip=file in the current folder
def findZipFile():
    arr = os.listdir()
    for file in arr:
        if(file.endswith(".zip")):
            return file

# update the children element
def updateChildren(contentFile, newFile):
    tree = ET.parse(contentFile)

    # check root
    root = tree.getroot() 
    root_tag = root.tag
    if root_tag != "repository":
        print("# Invalid root tag in " + contentFile + " Quitting")
        sys.exit

    children = root.find("./children")

    # increment counter
    curSize = children.attrib["size"]
    children.attrib["size"] = str(int(curSize) + 1)

    # insert new child element
    newChild = ET.SubElement(children, "child")
    newChild.tail = '\n'
    newChild.set("location", "updates/" + newFile)

    # write to file
    tree.write(contentFile, short_empty_elements=True)

    # prepend processing instructions
    with open(contentFile, 'r') as file:
        str1 = "<?xml version='1.0' encoding='UTF-8'?>\n"
        str2 = "<?compositeMetadataRepository version='1.0.0'?>\n"
        data = file.read()
        data = str2 + data
        data = str1 + data

    with open(contentFile, 'w') as file:
        file.write(data)

def unpackZip(filename, dtg, folder):
    # Create a ZipFile Object and load sample.zip in it
    with ZipFile(filename, 'r') as zipObj:
        # Extract all the contents of zip file in current directory
        zipObj.extractall(folder + "/" + dtg)

# check things are ok
allOk = checkFolder()

if allOk:
    
    print( "Valid folder, doing update")

    # retrieve zip file, we've already established that it's present
    zipFile = (findZipFile())

    dtg = getDTG()
    newName = dtg + ".zip"

    print("Renaming zip file with DTG")

    # rename file
    os.rename(zipFile,  newName)  

    print("Updating XML metadata")

    # parse category files
    updateChildren("compositeContent.xml", dtg)
    updateChildren("compositeArtifacts.xml", dtg)

    print("Unpacking repository")

    # unpack the zip into the updates folder
    unpackZip(newName, dtg, "updates")

    print("Deleting .zip file")

    # lastly, delete the zip file
    os.remove(newName)

    print("== COMPLETE ==")

