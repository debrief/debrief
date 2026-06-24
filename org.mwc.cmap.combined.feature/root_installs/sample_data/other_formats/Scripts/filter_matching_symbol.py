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



plot = getActivePlot(); # get the current plot
if plot is not None: # did we find a Debrief plot?

	layers = plot.getLayers(); # get the layers for the plot
	tracks = layers.getTracks();

	# collate a list of symbols
	symbols = [];
	leng = len(tracks); # find number of tracks
	
	# do we hj
	if leng == 0:
	
		showInfoDialog("No tracks found", "Filter symbols");
		sys.exit()
	
	
	for track in tracks: # loop through tracks
	
		symbol = track.getSymbolType(); # get the symbol type
		symbols.append(symbol); # add this symbol to the list
	

	# find the unique list of symbols
	symbols = set(symbols);

	# ask the user which one to filter
	chosenSym = showSelectionDialog(symbols, "Choose symbol to match", "Filter to matching symbol");

	if chosenSym is None: # did one get chosen?
	
		sys.exit(); # ok, drop out.
	

	# now loop through, and hide any that don't match
	for track in tracks:
		symbol = track.getSymbolType(); # get its symbol

		isVis = symbol == chosenSym; # does this match the chosen one?
		track.setVisible(isVis); # set visibility accordingly
	

	layers.fireModified();

			
