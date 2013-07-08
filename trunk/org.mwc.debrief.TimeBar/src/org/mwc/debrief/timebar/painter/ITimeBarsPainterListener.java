package org.mwc.debrief.timebar.painter;

import java.util.Date;

public interface ITimeBarsPainterListener 
{
	public void chartDoubleClicked(Date clickedAt);
	
	public void eventDoubleClicked(Object eventEntry);
	
	public void eventSelected(Object eventEntry);
}
