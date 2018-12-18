/**
 * name: filter to only show tracks with matching symbol
 * 
 */
loadModule("/System/UI", false); // for the input dialogs
loadModule('/Debrief/Core'); // for plot, duration, date

/** function to return the unique entries in the provided list
 * from here: https://coderwall.com/p/nilaba/simple-pure-javascript-array-unique-method-with-5-lines-of-code
 */
Array.prototype.unique = function() {
  return this.filter(function (value, index, self) { 
    return self.indexOf(value) === index;
  });
}

let plot = getActivePlot(); // get the current plot
if (plot != null) // did we find a Debrief plot?
{
	let layers = plot.getLayers(); // get the layers for the plot
	let tracks = layers.getTracks();

	// collate a unique list of symbols
	var symbols =
	[];
	let len = tracks.length;
	for (var i = 0; i < len; i++)
	{
		let track = tracks[i];  // get this track
		let symbol = track.getSymbolType();  // get the symbol type

		// add this symbol to the list
		symbols[i] = symbol;
	}

	// find the unique list of symbols
	symbols = symbols.unique();
	
	// ask the user which one to filter
	let chosenSym = showSelectionDialog(symbols, "Choose symbol to match", "Filter to matching symbol");

	if (chosenSym == null)
	{
		exit(0); // ok, drop out.
	}
	
	// now loop through, and hide any that don't match
	for (var i = 0; i < len; i++)
	{
		let track = tracks[i];
		let symbol = track.getSymbolType();
		
		let isVis = symbol == chosenSym;
		track.setVisible(isVis);
	}

	layers.fireModified();
}