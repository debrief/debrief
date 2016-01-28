package org.mwc.debrief.dis.diagnostics;

import org.mwc.debrief.dis.core.IDISPreferences;

public class TestPrefs implements IDISPreferences
{
  final private boolean _reusePlot;

  public TestPrefs(boolean reusePlot, String path)
  {
    _reusePlot = reusePlot;
  }

  @Override
  public boolean reusePlot()
  {
    return _reusePlot;
  }

}