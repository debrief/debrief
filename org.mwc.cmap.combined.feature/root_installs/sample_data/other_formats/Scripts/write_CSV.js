loadModule('/System/Resources', false);
loadModule('/Debrief/Core'); // date
loadModule('/Debrief/Spatial'); // location
loadModule('/Debrief/Tactical'); // for plot, duration, date
loadModule("/System/UI", false); // for the input dialogs

// get project instance
var project = getProject("sample_data");

if (!project.exists())
{
	print("Can't find project");
	exit(0);
}


let plot = getActivePlot(); // get the current plot
if (plot != null) // did we find a Debrief plot?
{
	let layers = plot.getLayers(); // get the layers for the plot
	let lNames = layers.getTrackNames(); // get a list of layer names
	let choice = showSelectionDialog(lNames, "Which track?",
			"Choose track to export"); // let the user select a name
	if (choice != null) // did user select something?
	{
	// create a file within the project
		var file = openFile("workspace://sample_data/other_formats/tutorial_" + choice + "_export.csv",
				WRITE, true);

		if (!file.exists())
		{
			print("Can't find file");
			exit(0);
		}

		writeLine(file, ";; dtg, lat, long");

		
		let track = layers.findTrack(choice); // try to get the selected layer
		if (track != null) // did we find it the annotations layer?
		{
			var ctr = 0;
			let fixes = track.getPositionIterator();
			while (fixes.hasMoreElements())
			{
				// @type Debrief.Wrappers.FixWrapper
				let fix = fixes.nextElement();
				// @type MWC.GenericData.WorldLocation
				let loc = fix.getLocation();
				
				let str = fix.getDTG().getDate() + ", " + loc.getLat() + ", " + loc.getLong();
				writeLine(file, str);
				ctr++;
			}
		}

		showInfoDialog(ctr + " lines exported", "Export to CSV");
		closeFile(file);
	}
}
