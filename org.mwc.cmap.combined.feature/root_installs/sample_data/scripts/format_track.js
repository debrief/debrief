/**
 * name : Format track (JS) 
 * popup : Debrief.Wrappers.Track.LightweightTrackWrapper
 */

loadModule("/System/UI", false);
loadModule("/Debrief Factory", false);

var selection = getSelection();

var editable = selection.getFirstElement();
var track = editable.getEditable();

var minutesStr = showInputDialog("How many minutes apart for symbols?",
		"10");
var minutes = parseInt(minutesStr);
var interval = createDate(1000 * 60 * minutes)
track.setSymbolFrequency(interval);

var layers = getEditor().getLayers();
layers.fireModified();

// Debrief.Wrappers.Track.LightweightTrackWrapper
