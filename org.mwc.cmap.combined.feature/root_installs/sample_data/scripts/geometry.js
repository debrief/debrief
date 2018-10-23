/*
 * name : Geometry playground
 * description : create some geometric shapes
 */
loadModule("/Debrief", false);


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
		for(j=0;j<10;j++)
		{
			var startP = centre.add(createVector(i * 5000, (j + 6) * 10));
			var endP = centre.add(createVector(j * 5000, (i + 2) * 10));
			var shape = createLine(startP, endP, "name" + (i * 10 + j));
			shapes.add(shape);
		}
	}
	layers.fireModified();
	
}