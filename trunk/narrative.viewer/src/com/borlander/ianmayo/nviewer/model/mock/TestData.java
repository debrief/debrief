package com.borlander.ianmayo.nviewer.model.mock;

public class TestData extends MockEntryWrapper {
	public TestData() {
		addEntry("Vehicle a", "Movement", "Some long text here (for V-a)");
		addEntry("Vehicle b", "Movement", "Some another long text here (for V-b)");
		addEntry("Vehicle c", "Movement", "Small text about V-c");
		addEntry("Vehicle d", "Event A", "Some long text here about V-d");
		addEntry("Observer a1", "Event A", "Some long text here, subject is Observer a1");
		addEntry("Observer a1", "Event B", "Some long text here, subject is Observer a1 listening for event B");
		addEntry("Observer a2", "Event B", "Subject is Observer a2 listening for event B");
		addEntry("Observer a2", "Event A", "Subject is Observer a2 but now listening for event A");
	}
}
