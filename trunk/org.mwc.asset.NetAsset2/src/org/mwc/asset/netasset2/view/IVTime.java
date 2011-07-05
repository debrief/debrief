package org.mwc.asset.netasset2.view;

import org.eclipse.swt.events.SelectionListener;

public interface IVTime
{

	void setTime(String string);

	void addStepListener(SelectionListener listener);

	void addPlayListener(SelectionListener listener);

	void addStopListener(SelectionListener listener);

	void setPlayLabel(String text);

}
