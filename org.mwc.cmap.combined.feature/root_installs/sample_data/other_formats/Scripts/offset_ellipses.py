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

loadModule('/System/UI'); # for the input dialogs
loadModule('/Debrief/Core'); # for plot, duration, date

plot = getActivePlot();  # get the current plot

if plot is not None:  # did we find a Debrief plot?
	layers = plot.getLayers(); # get the layers for the plot
	lNames = layers.getTrackNames(); # get a list of layer names
	choice = showSelectionDialog(lNames, "Which track?", "Choose track to export");  # let the user select a name
	
	if choice is not None: # did user select something?
	
		anns = layers.findLayer(choice); # try to get the selected layer
		
		if anns is not None: # did we find it the annotations layer?
			# ok, sort out the duration
			mins = showInputDialog("How many minutes?", "10", "Set ellipse duration");
			
			if mins is not None:
				duration = createDuration(int(mins), DUR_MINUTES);
				
				# now loop through the layer
		
				numer = anns.elements(); # retreive the elements in this layer
				
				
				while numer.hasMoreElements(): # loop through the items in this layer
					item = numer.nextElement(); # get the next element
					# @type java.lang.String
					name = item.toString();  # get the string label for the element
					if name.startswith("Ellipse"): # see if it starts with "Ellipse"
					
						start = item.getStartDTG();  # retrieve the start date
						newTime = (start.getDate().getTime() + duration.getValueIn(DUR_MILLISECONDS));  # calculate the new end time
						item.setEndDTG(createDate(newTime));  # set the new end time
				
			
	layers.fireModified();
			
			
			
			
