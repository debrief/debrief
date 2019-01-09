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
		/**
		 * Note: format looks like this: Two header lines, then rows of CSV entries. 
		 * # UK TRACK EXCHANGE FORMAT, V1.0 #
		 * Lat,Long,DTG,UnitName,CaseNumber,Type,Flag,Sensor,MajorAxis,SemiMajorAxis,SemiMinorAxis,Course,Speed,Depth,Likelihood,Confidence,SuppliedBy,Provenance,InfoCutoffDate,Purpose,Classification,DistributionStatement
		 * 22.1862861,-21.6978806,19951212T050000Z,NELSON,D-112/12,OILER,UK,S2002,1.0,0.5,0.5,269.7000,2.0000,0.0,Remote,Low,UNIT_ALPHA,NELSON,19951212,For
		 * planning,PUBLIC,"Quite a content."
		 */

		var partsOfStr = nextLine.split(',');
		if (track == null)
		{
			// track not created yet. Go for it.
			track = createTrack(partsOfStr[3])
		}

		// location
		let dLat = parseFloat(partsOfStr[0]);
		let dLong = parseFloat(partsOfStr[1]);
		let location = createLocation(dLat, dLong, 0);
		// dtg components
		let dtgStr = partsOfStr[2];
		let yrs = dtgStr.substring(0, 4);
		let mons = dtgStr.substring(4, 6) - 1;
		let days = dtgStr.substring(6, 8);
		let hrs = dtgStr.substring(9, 11);
		let mins = dtgStr.substring(11, 13);
		let secs = dtgStr.substring(13, 15);
		// date object
		let dtg = createDateCalendarFormat(yrs, mons, days, hrs, mins, secs);
		// course and speed
		let course = partsOfStr[11];
		let speed = partsOfStr[12];
		// create the fix
		let fix = createFix(dtg, location, course, speed);
		// store the fix
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
