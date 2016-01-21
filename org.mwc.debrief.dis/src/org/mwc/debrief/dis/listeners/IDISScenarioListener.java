package org.mwc.debrief.dis.listeners;

public interface IDISScenarioListener
{
  /**
   * we're starting a new scenario
   */
  void restart();

  /**
   * we've been told that the scenario has completed. we may wish to modify the UI or data
   * accordingly.
   */
  void complete();
}