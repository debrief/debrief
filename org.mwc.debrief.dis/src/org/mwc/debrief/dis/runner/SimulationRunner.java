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

  public void run()
  {
    final File exe = new File(_prefs.getExePath());
    final File input = new File(_prefs.getInputFile());
    final File parent = input.getParentFile();

    if (!exe.exists())
    {
      System.err.println("Executable not found");
    }
    else if (!input.exists())
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
      {_prefs.getExePath(), _prefs.getInputFile()};
//      SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss");
//      final String processName = input.getName() + " " + sdf.format(new Date());
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

//  private MessageConsole findConsole(String name)
//  {
//    ConsolePlugin plugin = ConsolePlugin.getDefault();
//    IConsoleManager conMan = plugin.getConsoleManager();
//    IConsole[] existing = conMan.getConsoles();
//    for (int i = 0; i < existing.length; i++)
//    {
//      if (name.equals(existing[i].getName()))
//      {
//        return (MessageConsole) existing[i];
//      }
//    }
//    // no console found -> create new one
//    MessageConsole newConsole =
//        new MessageConsole(name, CorePlugin
//            .getImageDescriptor("icons/16/Calculator.png"));
//    conMan.addConsoles(new IConsole[]
//    {newConsole});
//    return newConsole;
//  }

  public void stop()
  {
    if (_process != null)
    {
      _process.destroy();
      _process = null;
    }
  }

}
