/*
 * name : Create ring of points
 * description : sample code to create ring of points
 * toolbar : Outline
 * image : platform:/plugin/org.mwc.debrief.scripting/icons/circle.png
 */
loadModule("/System/UI", false);
loadModule("/Debrief", false);

var name = "Ring";

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
	var rStr = showInputDialog("Ring radius (km)?", "20");
	var radius = parseInt(rStr);
	var name = "Ring";
	
	// find the track names
	var layers = editor.getLayers();
	var tracks = layers.getTracks(); 

    var selection = showSelectionDialog(tracks,"Select subject track", "Add buoy ring");
    	
	var centre = editor.getCentre();
	var red = getColor(255, 0, 0);
	var blue = getColor(0, 0, 255);
	if (layers != null) {
		var annotations = layers.createLayer("Annotations");

		var kingPin = createLabel(centre, blue);
		kingPin.setName("King Pin");
		annotations.add(kingPin);
		
		var redShade = 5 + (200.0 * Math.random());
		print(redShade);

		for (i = 0; i < 360; i += 20) {
			var vector = createVectorKm(radius, i);
			var pos = centre.add(vector);
			var label = createLabel(pos, red);
			label.setName(name + " " + (i));
			annotations.add(label);
			// artificial delay
			wait(100);
			label.setColor(getColor(redShade, 55, (i % 25) * 2));
			layers.fireModified();
		}
		editor.fitToWindow();
		layers.fireModified();
	} else {
		print("layers not found");
	}
}
