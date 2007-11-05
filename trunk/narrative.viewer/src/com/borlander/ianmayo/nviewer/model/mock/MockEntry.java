package com.borlander.ianmayo.nviewer.model.mock;

import java.util.Date;

import com.borlander.ianmayo.nviewer.model.IEntry;

public class MockEntry implements IEntry {
	private final Date myTime;
	private final String mySource;
	private final String myType;
	private final String myEntry;
	private boolean myIsVisible;

	public MockEntry(Date time, String source, String type, String entry) {
		myTime = time;
		mySource = source;
		myType = type;
		myEntry = entry;
		myIsVisible = true;
	}

	public String getEntry() {
		return myEntry;
	}

	public String getSource() {
		return mySource;
	}

	public long getTime() {
		return myTime.getTime();
	}

	public String getType() {
		return myType;
	}

	public boolean getVisible() {
		return myIsVisible;
	}

	public void setVisible(boolean yesVisisble) {
		myIsVisible = yesVisisble;
	}

}
