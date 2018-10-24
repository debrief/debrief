/**
 * name : GenTrack
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

var existing = layers.findTrack("ship");
if(existing != null)
	{
	layers.remove(existing);
	}

var area = editor.getArea();
var p1 = area.getTopLeft();
var p2 = area.getBottomRight();
var track = createTrack("ship");
var color = getColor(50, 255, 50);
track.setColor(color);

var xDelta = (p1.getLat() - p2.getLat()) / 50;

layers.add(track);

for(var i=0;i<40;i++)
{
	var d1 = createDate(1000000 + i * 1000 * 60 * 5);
	var p1 = createLocation(p1.getLat() - xDelta * Math.random() * 2.5,
			p1.getLong() + xDelta * Math.random() * 2, 0);
	var f1 = createFix(d1, p1, 2,3);
	track.addFix(f1);
	wait(50);
	layers.fireModified();
}


