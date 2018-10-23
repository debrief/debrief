/*
 * name : Create ring of points
 * description : sample code to create ring of points
 */
loadModule("/Debrief Factory", false);
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
	var centre = editor.getCentre();
	var red = getColor(255, 0, 0);
	var blue = getColor(0, 0, 255);
	var layers = editor.getLayers();
	if (layers != null) {
		var annotations = layers.createLayer("Annotations");

		var kingPin = createLabel(centre, blue);
		kingPin.setName("King Pin");
		annotations.add(kingPin);

		for (i = 0; i < 360; i += 20) {
			var vector = createVector(20000, i);
			var pos = centre.add(vector);
			var label = createLabel(pos, red);
			label.setName(name + " " + (i));
			annotations.add(label);
			wait(100);
			label.setColor(getColor(255, 55, (i % 25) * 2));
			layers.fireModified();
		}
		layers.fireModified();
	} else {
		print("layers not found");
	}
}