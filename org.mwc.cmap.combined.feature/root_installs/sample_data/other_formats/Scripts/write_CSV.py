# 
# Debrief - the Open Source Maritime Analysis Application
# http://debrief.info
#  
# (C) 2000-2020, Deep Blue C Technology Ltd
#  
# This library is free software; you can redistribute it and/or
# modify it under the terms of the Eclipse Public License v1.0
# (http://www.eclipse.org/legal/epl-v10.html)
#  
# This library is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
#

loadModule('/System/Resources');
loadModule('/Debrief/Core'); # date
loadModule('/System/UI'); # for the input dialogs

# get project instance
project = getProject("sample_data");

if not project.exists():
	print("Can't find project");
	exit(0);


plot = getActivePlot();  # get the current plot


if plot is not None:  # did we find a Debrief plot?
	layers = plot.getLayers(); # get the layers for the plot
	lNames = layers.getTrackNames(); # get a list of layer names
	choice = showSelectionDialog(lNames, "Which track?", "Choose track to export");  # let the user select a name
	
	if choice is not None: # did user select something?
		# create a file within the project
		
		try:
			with open("workspace://sample_data/other_formats/tutorial_" + choice + "_export.csv", 'w') as file:
			
				file.write('elapsed (secs), course rate (degs/sec)\n');
				track = layers.findTrack(choice); # try to get the selected layer
				
				if track is not None: # did we find it the annotations layer?
					
					ctr = 0;
					fixes = track.getPositionIterator(); # to loop through positions
					lastCourse = None;
					lastTime = None;
					firstTime = None;
					while fixes.hasMoreElements(): # while there are more elements
					
						# @type Debrief.Wrappers.FixWrapper
						fix = fixes.nextElement(); # get the next element

						thisCourse = fix.getCourseDegs();
						thisTime = fix.getDTG().getDate().getTime();

						if firstTime is None:
							firstTime = thisTime; # remember the start time
						

						if lastCourse is not None:
							courseDelta = thisCourse - lastCourse;
							timeDelta = (thisTime - lastTime) / 1000.0;
							courseRate = courseDelta / timeDelta;
							elapsed = (thisTime - firstTime) / 1000.0;
							stri = str(elapsed) + ", " + str(courseRate) + "\n";
							file.write(stri);
						

						lastCourse = thisCourse;
						lastTime = thisTime;

						ctr+=1;
						

				showInfoDialog(str(ctr) + " lines exported", "Export to CSV");
					
				
				
				
		except OSError:
			print('Can\'t find file')
			sys.exit()
		    
			
			
			
			
			