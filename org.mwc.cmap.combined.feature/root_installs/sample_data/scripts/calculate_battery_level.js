/**
 * name : demo/shade battery level
 * toolbar :  do Shade
 * io : one
 */

/** Aim: to shade track according to battery level,
 * and produce plot of ongoing battery level.
 * Note: scenario is SSK & Frigate. Note depth profile
 * of SSK, with with 2 PD runs, where it
 * can charge batteries,
 * 
 */

loadModule("/Debrief/Core", false);
loadModule("/Debrief/Tactical", false);
loadModule("/System/UI", false);
loadModule('/Charting', false);

// encode how to calculate battery level
function updateBattery(level, elapsed, speed,  depth)
{
	var percentPerKtPerSec = 0.0015;
	var pdDepth = 25;
	var chargePerSec = 0.07;
	var usage;
	if(depth < pdDepth)
		{
		// -ve, since represents charging
		usage = - elapsed * chargePerSec;
		}
	else
		{
		usage = elapsed * percentPerKtPerSec * speed;
		}
	
	// new battery level
	var res = level - usage;
	
	res = Math.min(100, res);
	res = Math.max(0, res);
	
	return res;
}

var editor = getEditor();
var layers = editor.getLayers();
var tracks = layers.getTracks(); 

// @type Debrief.Wrappers.TrackWrapper
var track = showSelectionDialog(tracks,"Select subject track", "Update battery level");
var level = showInputDialog("Initial level?", 95);

if(track == null)
	exit();

// remember the start time
var tLast = track.getStartDTG().getDate().getTime();

// ok, work through the track
var iter = track.getPositionIterator();

// declare graph
figure("Battery Level");
clear();
series("Level", "%");

// collate points for graph
var times = [];
var levels = [];

var ctr = 0;
// loop through the positions
while(iter.hasMoreElements())
	{
	  // get the next position
	  // @type Debrief.Wrappers.FixWrapper
	  var pos = iter.nextElement();
	  var thisTime = pos.getDTG().getDate().getTime();
	  
	  // calculate  new level
	  var elapsed = (thisTime - tLast) / 1000.0;
	  var depth = pos.getDepth();
	  var speed = pos.getSpeed();
	  var level = updateBattery(level, elapsed, speed, depth);

	  // shade according to level
	  var proportion = (level / 100.0) * 255;
	  var color = getColor(proportion, 0, 255 - proportion);
	  pos.setColor(color);
	  
	  ctr++;
	  
	  // store plot points
	  times.push(ctr);
	  levels.push(level);
	  
	  print(thisTime + ", " + level);
	  
	  // remember this time
	  tLast = thisTime;
	}

plot(times, levels);
layers.fireModified();

// note: track (and x-y plot) updated with
// shades according to level.  Note 
// graph of battery level (below).
