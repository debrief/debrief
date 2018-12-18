loadModule("/System/UI", false); // for the input dialogs
loadModule('/Debrief/Core'); // for plot, duration, date
let plot = getActivePlot(); // get the current plot
if (plot != null) // did we find a Debrief plot?
{
	let layers = plot.getLayers(); // get the layers for the plot
	let lNames = layers.getLayerNames(); // get a list of layer names
	let choice = showSelectionDialog(lNames, "Which layer",
			"Choose ellipse layer"); // let the user select a name
	if (choice != null) // did user select something?
	{
		let anns = layers.findLayer(choice); // try to get the selected layer
		if (anns != null) // did we find it the annotations layer?
		{
			// ok, sort out the duration
			let mins = showInputDialog("How many minutes?", "10",
					"Set ellipse duration");
			let duration = createDuration(mins, DUR_MINUTES);
			// now loop through the layer
			let numer = anns.elements(); // retreive the elements in this layer
			while (numer.hasMoreElements()) // loop through the items in this layer
			{
				let item = numer.nextElement(); // get the next element
				// @type java.lang.String
				let name = item.toString();  // get the string label for the element
				if (name.startsWith("Ellipse")) // see if it starts with "Ellipse"
				{
					let start = item.getStartDTG();  // retrieve the start date
					let newTime = (start.getDate().getTime() + duration
							.getValueIn(DUR_MILLISECONDS));  // calculate the new end time
					item.setEndDTG(createDate(newTime));  // set the new end time
				}
			}
		}
	}
	layers.fireModified();
}