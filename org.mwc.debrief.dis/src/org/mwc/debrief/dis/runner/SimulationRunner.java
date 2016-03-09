package org.mwc.debrief.dis.runner;

import java.io.File;
import java.io.IOException;

import org.mwc.debrief.dis.ui.preferences.DebriefDISSimulatorPrefs;

public class SimulationRunner
{

  final private DebriefDISSimulatorPrefs _prefs;
  private Process _process;

  public SimulationRunner(DebriefDISSimulatorPrefs simPrefs)
  {
    _prefs = simPrefs;
  }

  public void run(final String inFile)
  {
    final File input = new File(inFile);
    final File parent = input.getParentFile();

    if (!input.exists())
    {
      System.err.println("Input file not found");
    }
    else if (!parent.isDirectory())
    {
      System.err.println("Unable to find parent directory");
    }
    else
    {
      final String[] exeCmd = new String[]
      {_prefs.getExePath(), inFile};

      // fire up the processs
      try
      {
        _process = Runtime.getRuntime().exec(exeCmd, null, parent);
      }
      catch (IOException e1)
      {
        // TODO Auto-generated catch block
        e1.printStackTrace();
      }
    }
  }

  public void stop()
  {
    if (_process != null)
    {
      _process.destroy();
      _process = null;
    }
  }

}
