loadModule("/System/UI", false); // for the input dialog
loadModule('/Debrief/Core'); // for plot, duration, date
var plot = getActivePlot();
if (plot != null)
{
	var layers = plot.getLayers();
	var lNames = layers.getLayerNames();
	var choice = showSelectionDialog(lNames, "Which layer", "Choose ellipse layer");
	let anns = layers.findLayer(choice);
	// did we find it the annotations layer?
	if (anns != null)
	{
		// ok, sort out the duration
		let mins = showInputDialog("How many minutes?", "10",
				"Set ellipse duration");
		let duration = createDuration(mins, DUR_MINUTES);

		// now loop through the layer
		let numer = anns.elements();
		while (numer.hasMoreElements())
		{
			let item = numer.nextElement();
			// @type java.lang.String
			let name = item.toString();
			if (name.startsWith("Ellipse"))
			{
				let start = item.getStartDTG();
				let end = createDate((start.getDate().getTime() + duration
						.getValueIn(DUR_MILLISECONDS)));
				item.setEndDTG(end);
			}
		}
	}
	layers.fireModified();
}