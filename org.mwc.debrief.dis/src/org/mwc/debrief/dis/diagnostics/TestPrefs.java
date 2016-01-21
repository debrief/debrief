package org.mwc.debrief.dis.diagnostics;

import org.mwc.debrief.dis.core.IDISPreferences;

public class TestPrefs implements IDISPreferences
{
	final private boolean _reusePlot;
	final private String _path;

	public TestPrefs(boolean reusePlot, String path)
	{
		_reusePlot = reusePlot;
		_path = path;
	}

	@Override
	public boolean reusePlot()
	{
		return _reusePlot;
	}

	@Override
	public String inputFile()
	{
		return _path;
	}
}