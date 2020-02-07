/*******************************************************************************
 * Debrief - the Open Source Maritime Analysis Application
 * http://debrief.info
 *  
 * (C) 2000-2020, Deep Blue C Technology Ltd
 *  
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html)
 *  
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 *******************************************************************************/
loadModule('/System/Resources', false);
loadModule('/Debrief/Core'); // date
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
		var file = openFile("workspace://sample_data/other_formats/tutorial_"
				+ choice + "_export.csv", WRITE, true);

		if (!file.exists())
		{
			print("Can't find file");
			exit(0);
		}

		writeLine(file, ";; elapsed (secs), course rate (degs/sec)");

		let track = layers.findTrack(choice); // try to get the selected layer
		if (track != null) // did we find it the annotations layer?
		{
			var ctr = 0;
			let fixes = track.getPositionIterator(); // to loop through positions
			var lastCourse = null;
			var lastTime = null;
			var firstTime = null;
			while (fixes.hasMoreElements()) // while there are more elements
			{
				// @type Debrief.Wrappers.FixWrapper
				let fix = fixes.nextElement(); // get the next element

				let thisCourse = fix.getCourseDegs();
				let thisTime = fix.getDTG().getDate().getTime();

				if (firstTime == null)
				{
					firstTime = thisTime; // remember the start time
				}

				if (lastCourse != null)
				{
					let courseDelta = thisCourse - lastCourse;
					let timeDelta = (thisTime - lastTime) / 1000.0;
					let courseRate = courseDelta / timeDelta;
					let elapsed = (thisTime - firstTime) / 1000.0;
					let str = elapsed + ", " + courseRate;
					writeLine(file, str);
				}

				lastCourse = thisCourse;
				lastTime = thisTime;

				ctr++;
			}
		}

		showInfoDialog(ctr + " lines exported", "Export to CSV");
		closeFile(file);
	}
}
