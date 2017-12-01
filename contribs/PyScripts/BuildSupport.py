'''
Created on 29 Nov 2017

@author: Ian
'''

if __name__ == '__main__':
    pass

import json,urllib,shlex, os, string, xml.etree.ElementTree as ET
from subprocess import Popen, PIPE
from sets import Set
from datetime import datetime

def get_exitcode_stdout_stderr(cmd):
    """
    Execute the external command and get its exitcode, stdout and stderr.
    """
    args = shlex.split(cmd)

    proc = Popen(args, stdout=PIPE, stderr=PIPE)
    out, err = proc.communicate()
    exitcode = proc.returncode
    #
    return exitcode, out, err

# for the given string, find the last "." separate number, 
# increment it, and return it
def incrVersion(versionString):
	# find the last "." item
	dotIndex = versionString.rfind(".")
	
	# get the suffix
	numberStr = versionString[dotIndex+1:]
	numberVal = int(numberStr)
	
	# generate the new value                
	newNumberStr = str(numberVal+1)
	
	return string.replace(versionString, numberStr, newNumberStr)

# increment the version in the supplied feature
def updateFeature(featurePath):
    # read file into xml
	print "Updating " + featurePath

	tree = ET.parse(featurePath)
	root = tree.getroot()
	curVer = root.attrib["version"]
	
	newVer = incrVersion(curVer)
	
	# ok, we're not going to write via the XML API, since it reformats
	# the text.  Instaed, read in the file into one string
	inFile = open(featurePath)
	contents = inFile.read()
	contents = string.replace(contents, curVer, str(newVer))
	
	outFile = open(featurePath,"w")
	outFile.write(contents)	
	
# increment the version in the supplied plugin
def updatePlugin(plugin, filePath, fieldName):
    # print "Updating:" + plugin

    # read in the file as lines
    x = []
    filePath = plugin + filePath
    if os.path.exists(filePath): 
        with open(filePath) as inFile:
            for l in inFile:
                x.append(l)  

        index = 0;
        for thisLine in x:
            if thisLine.startswith(fieldName):
            	
            	newLine = incrVersion(thisLine) 
                
                # replace the string in the list
                x[index] = newLine + "\n"
                
                print "Updated:" + plugin + " from " + thisLine.strip() + " to " + newLine

            # remember the line number
            index = index + 1;
                
        # lastly, write the strings to file
        fh = open(filePath, "w")
        
        fh.writelines(x)
        #for item in x:
        #    fh.write("%s" % item)
        
        fh.close()
        
# increment the mappings file
def updateMappings(filePath):
	print "Updating " + filePath
	# read in the file as lines
	x = []
	if os.path.exists(filePath): 
		# read the file into a list of strings
		with open(filePath) as inFile:
		    for l in inFile:
		        x.append(l)  
		
		# remember the current row index
		index = 0;
		
		# loop through the strings
		for thisLine in x:
			# is it a comment?
			if(not thisLine.startswith("#")):			
				# no, so we can process it
				
				# which row is it?
				lineNum = int(thisLine[0])
				if lineNum == 0:
					newVer = incrVersion(thisLine) + "\n"
				elif lineNum == 1:
					# generate date
					dt = datetime.now()
					strg = dt.strftime('%Y%m%d')
					newVer = "1=" + strg + "\n"
				elif lineNum == 2:
					# generate date
					dt = datetime.now()
					strg = dt.strftime('%Y-%m-%d')
					newVer = "2=" + strg
		
				# replace the string with this new one
				x[index] = newVer
		
			# increment the row counter
			index = index + 1
        		
        # lastly, write the strings to file
        fh = open(filePath, "w")
        fh.writelines(x)
        fh.close()        


url = "https://api.github.com/repos/debrief/debrief/releases/latest"
data = urllib.urlopen(url).read()
jData = json.loads(data)

# get the tag

tag = jData['tag_name']
# tag = "20171107_3_0_414"

# get commits since that tag
## here's a working version of the command:
## git diff --name-only 20171107_3_0_414..HEAD

# note: we don't need to change the directory if we're called from the root folder
# os.chdir("../../")

cmd = "git diff --name-only " + tag + "..HEAD" 
exitcode, out, err = get_exitcode_stdout_stderr(cmd)

if err:
    print "have errors:"
    print err
    exit
    
# keep track of the plugins to update    
plugins = Set()

# keep track of the features to update
features = Set()

# ok, now loop through those folders
paths = out.split("\n")
for path in paths:
    # do we have a path
    if path:
        # print path
        # extract the first bit
        items = path.split("/")
        plugins.add(items[0])
        
        if("org.mwc.asset" in path):
            features.add("org.mwc.asset.core.feature")
        if("org.mwc.cmap" in path):
            features.add("org.mwc.cmap.combined.feature")
        if("org.mwc.debrief" in path):
            features.add("org.mwc.debrief.combined.feature")

# print plugins
# print features

# ok, now increment the plugins
for plugin_id in plugins:
    updatePlugin(plugin_id,"/META-INF/MANIFEST.MF", 'Bundle-Version')

# and the features
for feature_id in features:
    updateFeature(feature_id + "/feature.xml")

# lastly the product version
updateFeature("org.mwc.debrief.product/debriefng.product")

# and the mappings
updateMappings("org.mwc.debrief.core/about.mappings")