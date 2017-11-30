'''
Created on 29 Nov 2017

@author: Ian
'''

if __name__ == '__main__':
    pass

import json,urllib,shlex, os
from subprocess import Popen, PIPE
from sets import Set

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

def updatePlugin(plugin):
	print "Updating:" + plugin
	
	# read in the file as lines
	
	# find the line with Bundle-version
	
	# find the last id
	
	# increment the number
	
	# replace the string
	
	# write the lines back to file
	


url = "https://api.github.com/repos/debrief/debrief/releases/latest"
data = urllib.urlopen(url).read()
jData = json.loads(data)

# get the tag

tag = jData['tag_name']
# tag = "20171107_3_0_414"

# get commits since that tag
## here's a working version of the command:
## git diff --name-only 20171107_3_0_414..HEAD

os.chdir("../../")

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
        
        if("org.mwc.cmap" in path):
            features.add("org.mwc.cmap.combined.feature")
        if("org.mwc.debrief" in path):
            features.add("org.mwc.debrief.combined.feature")

# print plugins
# print features

# ok, now increment the plugins
for plugin_id in plugins:
	updatePlugin(plugin_id)
	
# and the features

# lastly the product version