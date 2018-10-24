/**
 * name : trial / Format track (JS) 
 * popup : enableFor(Debrief.Wrappers.TrackWrapper)
 */

loadModule("/System/UI", false);
loadModule("/Debrief/Core", false);
loadModule("/Debrief/Spatial", false);

var selection = getSelection();

var minutesStr = showInputDialog("How many minutes apart for symbols?",
"10");
var minutes = parseInt(minutesStr);
var interval = createDate(1000 * 60 * minutes)

var items = selection.toArray();
var length = items.length;

for (var i = 0; i < length; i++) {
    var editable = items[i];    
    var track = editable.getEditable();
	track.setSymbolFrequency(interval);
}

var layers = getEditor().getLayers();
layers.fireModified();

