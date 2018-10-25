/**
 * name : shade battery level
 * toolbar :  do Shade
 * io : None
 */

loadModule("/Debrief/Core", false);
loadModule("/Debrief/Tactical", false);
loadModule("/System/UI", false);


function updateBattery(level, elapsed, speed,  depth)
{
	var percentPerKtPerSec = 0.0015;
	var pdDepth = 25;
	var chargePerSec = 0.07;
	
	var usage;
	if(depth < pdDepth)
		{
		usage = - elapsed * chargePerSec;
		}
	else
		{
		usage = elapsed * percentPerKtPerSec * speed;
		}
	
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
var level = showInputDialog("Initial level?", 45);

if(track == null)
	exit();

// remember the start time
var tLast = track.getStartDTG().getDate().getTime();

// ok, work through the track
var iter = track.getPositionIterator();

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

	  // ahade according to level
	  var proportion = (level / 100.0) * 255;
	  var color = getColor(proportion, 0, 255 - proportion);
	  pos.setColor(color);
	  
	  // remember this time
	  tLast = thisTime;

	}

layers.fireModified();
