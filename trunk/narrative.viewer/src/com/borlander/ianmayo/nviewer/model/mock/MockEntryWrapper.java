package com.borlander.ianmayo.nviewer.model.mock;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.borlander.ianmayo.nviewer.model.IEntry;
import com.borlander.ianmayo.nviewer.model.IEntryWrapper;

public class MockEntryWrapper implements IEntryWrapper {
	private final List<IEntry> myEntries = new ArrayList<IEntry>();

	public void addEntry(IEntry entry) {
		myEntries.add(entry);
	}

	public void addEntry(String source, String type, String entry) {
		addEntry(new MockEntry(new Date(), source, type, entry));
	}

	public IEntry[] getEntries() {
		return (IEntry[]) myEntries.toArray(new IEntry[myEntries.size()]);
	}
}
