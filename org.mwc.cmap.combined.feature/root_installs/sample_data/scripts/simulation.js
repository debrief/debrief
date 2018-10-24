/**
 * name : trial / GenTrack
 * toolbar :  Project Explorer
 */

loadModule("/Debrief/Core", false);
loadModule("/Debrief/Shapes", false);
loadModule("/Debrief/Spatial", false);
loadModule("/Debrief/Tactical", false);

/** utility to introduce pause
 * 
 * @param ms milliseconds wait
 * @returns nothing
 */
function wait(ms) {
	var d = new Date();
	var d2 = null;
	do {
		d2 = new Date();
	} while (d2 - d < ms);
}

var editor = getEditor();
var layers = editor.getLayers();

// @type FooBar.Wrappers.TrackWrapper
var existing = layers.findTrack("ship");

if(existing != null)
	{
	layers.remove(existing);
	}

var area = editor.getArea();
var p1 = area.getTopLeft();
//var p2 = area.getBottomRight();
var track = createTrack("ship");
var color = getColor(50, 255, 50);
track.setColor(color);

// sort out a start time

// find any track that's present
var other = layers.findTrack(null);
var start = other.getStartDTG().getDate().getTime();

//var xDelta = (p1.getLat() - p2.getLat()) / 50;

layers.add(track);

for(var i=0;i<40;i++)
{
	// move time forward
	var d1 = createDate(start + i * 1000 * 60 * 5);
	
	// create new step forward, from the last
	var vector = createVectorKm(1.4 + Math.random() * 0.15,
			50 + Math.random() * 160);
	p1 = p1.add(vector);
	
	var f1 = createFix(d1, p1, 2,3);
	track.addFix(f1);
	wait(50);
	layers.fireModified();
}


