package org.mwc.asset.netasset2.time;

import org.eclipse.swt.events.SelectionListener;

public interface IVTime
{

	String PAUSE = "Pause";
	String PLAY = "Play";

	void setTime(String string);

	void addStepListener(SelectionListener listener);

	void addPlayListener(SelectionListener listener);

	void addStopListener(SelectionListener listener);

	void setPlayLabel(String text);

	void setEnabled(boolean val);

}
