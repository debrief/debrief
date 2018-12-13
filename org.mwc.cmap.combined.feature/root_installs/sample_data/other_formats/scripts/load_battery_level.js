/**
 * name : demo/load battery level
 * toolbar :  Script Explorer
 * io : one
 */

/**
 * Aim: to shade track according to battery level, and produce plot of ongoing
 * battery level. Note: scenario is SSK & Frigate. Note depth profile of SSK,
 * with with 2 PD runs, where it can charge batteries,
 * 
 */

loadModule("/Debrief/Core", false);
loadModule("/Debrief/Tactical", false);
loadModule("/System/UI", false);
loadModule('/Charting', false);
loadModule('/System/Resources', false);

var bTimes = [];
var bLevels = [];
var bLoaded = false;

// lookup battery level at specified time
function retrieveBattery(inTime)
{
  if (!bLoaded)
  {
    // get project instance
    var project = getProject("Debrief_Workspace");

    if (!project.exists())
    {
      print("Can't find project");
      exit(0);
    }

    // create a file within the project
    // @type 
    var file = openFile("workspace://Debrief_Workspace/sample_data/scripts/battery_levels.csv");

    if (!file.exists())
    {
      print("Can't find file");
      exit(0);
    }

    print("about to read lines");

    // ok, now loop through te code
    var nextLine;
    while ((nextLine = readLine(file)) != null)
    {
      var partsOfStr = nextLine.split(',');
      bTimes.push(parseFloat(partsOfStr[0]));
      bLevels.push(parseFloat(partsOfStr[1]));
    }

    bLoaded = true;
  }

  var res = 0;
  if (bLoaded)
  {

    var arrayLength = bTimes.length;
    for (var i = 0; i < arrayLength; i++)
    {
      var thisTime = bTimes[i];
      if (thisTime >= inTime)
      {
        res = bLevels[i];
        return res;
        break;
      }
    }
  }

  return res;
}

var editor = getEditor();
var layers = editor.getLayers();
var tracks = layers.getTracks();

// @type Debrief.Wrappers.TrackWrapper
var track = showSelectionDialog(tracks, "Select subject track",
    "Shade battery level");

if (track == null)
  exit();

// remember the start time
var tLast = track.getStartDTG().getDate().getTime();

// ok, work through the track
var iter = track.getPositionIterator();

// declare graph
figure("Battery Level");
series("Level", "%");

// collate points for graph
var times = [];
var levels = [];

var ctr = 0;
// loop through the positions
while (iter.hasMoreElements())
{
  // get the next position
  // @type Debrief.Wrappers.FixWrapper
  var pos = iter.nextElement();
  var thisTime = pos.getDTG().getDate().getTime();

  // calculate new level
  var level = retrieveBattery(thisTime);

  // shade according to level
  var proportion = (level / 100.0) * 255;
  var color = getColor(proportion, 0, 255 - proportion);
  pos.setColor(color);

  ctr++;

  // store plot points
  times.push(ctr);
  levels.push(level);

  // remember this time
  tLast = thisTime;
}

plot(times, levels);
layers.fireModified();