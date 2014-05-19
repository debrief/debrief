package org.mwc.asset.netasset2.time;

import org.eclipse.swt.events.SelectionListener;

public interface IVTimeControl
{

	String PAUSE = "Pause";
	String PLAY = "Play";

	void addStepListener(SelectionListener listener);

	void addPlayListener(SelectionListener listener);

	void addStopListener(SelectionListener listener);
	
	void addFasterListener(SelectionListener listener);
	void addSlowerListener(SelectionListener listener);

	void setPlayLabel(String text);

	void setEnabled(boolean val);

}
