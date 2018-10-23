/*
 * name : Geometry playground
 * description : create some geometric shapes
 */
loadModule("/Debrief", false);

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
	for(i=0;i<10;i++)
	{
		for(j=0;j<20;j++)
		{
			var color = getColor(20 * i, 255 - 10 * j, 200); 
			var startP = centre.add(createVector(i * 5000, (j + 6) * 10));
			var endP = centre.add(createVector(j * 5000, (i + 2) * 10));
			var shape = createLine(startP, endP, "name" + (i * 10 + j), color);
			shapes.add(shape);
			wait(50);
			layers.fireModified();
		}
	}
	layers.fireModified();
	
}