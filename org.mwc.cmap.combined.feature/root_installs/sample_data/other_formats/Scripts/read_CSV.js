loadModule('/System/Resources', false);
loadModule('/Debrief/Core'); // date
loadModule('/Debrief/Spatial'); // location
loadModule('/Debrief/Tactical'); // for plot, duration, date

// get project instance
var project = getProject("sample_data");

if (!project.exists())
{
	print("Can't find project");
	exit(0);
}

// create a file within the project
// @type
var file = openFile("workspace://sample_data/other_formats/CSV_EXCHANGE_SAMPLE.csv");

if (!file.exists())
{
	print("Can't find file");
	exit(0);
}

var track = null;

// ok, now loop through te code
var nextLine;
var ctr = 0;
while ((nextLine = readLine(file)) != null)
{
	ctr++;
	if (ctr > 2)
	{
		var partsOfStr = nextLine.split(',');
		if (track == null)
		{
			track = createTrack(partsOfStr[3])
		}

		// location
		let dLat = parseFloat(partsOfStr[0]);
		let dLong = parseFloat(partsOfStr[1]);
		let location = createLocation(dLat, dLong, 0);
		// dtg
		let dtgStr = partsOfStr[2];
		let yrs = dtgStr.substring(0, 4);
		let mons = dtgStr.substring(4, 6) - 1;
		let days = dtgStr.substring(6, 8);
		let hrs = dtgStr.substring(9, 11);
		let mins = dtgStr.substring(11, 13);
		let secs = dtgStr.substring(13, 15);
		let dtg = createDateCalendarFormat(yrs, mons, days, hrs, mins, secs);

		let course = partsOfStr[11];
		let speed = partsOfStr[12];
		let fix = createFix(dtg, location, course, speed);
		track.addFix(fix);
	}
}

if (track != null)
{
	// ok get somewhere to add it to
	let plot = getActivePlot();
	let layers = plot.getLayers();
	layers.add(track);
	layers.fireModified()
}
