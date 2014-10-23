/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 */
package ASSET.Scenario.Observers;

import java.io.File;

/**
 * Interface implemented by observers which keep track of data across a number of scenario runs
 *
 */
public interface InterScenarioObserverType extends ScenarioObserver
{

  /** initialise the observer, let it create it's output file
   *
   * @param outputDirectory where to place the output file
   */
  public void initialise(File outputDirectory);

  /** indicate that all scenario runs are now complete
   *
   */
  public void finish();

}
