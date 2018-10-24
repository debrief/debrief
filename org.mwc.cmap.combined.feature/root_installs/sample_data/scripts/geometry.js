/*
 * name : Geometry playground
 * description : create some geometric shapes
 */
loadModule("/Debrief/Core", false);
loadModule("/Debrief/Spatial", false);
loadModule("/Debrief/Shapes", false);

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

if (editor == null) {
	print("Can't retrieve editor");
} else {
	// find the track names
	var layers = editor.getLayers();
	var shapes = layers.createLayer("Shapes");
	var centre = editor.getCentre();
	for(i=0;i<12;i++)
	{
		for(j=0;j<12;j++)
		{
			var ctr = i * 12 + j;
			var color = getColor(20 * i, 255 - 10 * j, 200); 
			var startP = centre.add(createVector(i * 5000, (j + 6) * 10));
			var endP = centre.add(createVector(j * 5000, (i + 2) * 10));
			var line = createLine(startP, endP, "name" + ctr, color);
			shapes.add(line);

			var centre = centre.add(createVector((j + 4) * 1000, (j + 2) * 100));
			var distance = createDistance(2 + i * 6);
			var circle = createCircle(centre, distance, "circle:"+ctr, color);
			shapes.add(circle);
			
			wait(50);
			layers.fireModified();
		}
	}
	layers.fireModified();
	
}