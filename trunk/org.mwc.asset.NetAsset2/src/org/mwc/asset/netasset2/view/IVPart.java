package org.mwc.asset.netasset2.view;

import org.eclipse.swt.events.SelectionListener;

public interface IVPart
{

	String getDemDepth();

	void setActSpeed(String val);

	void setActCourse(String val);

	void setActDepth(String val);

	void setSubmitListener(SelectionListener listener);

	String getDemSpeed();

	String getDemCourse();

	void setEnabled(boolean val);

	void setParticipant(String name);

}
