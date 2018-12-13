/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2018, Deep Blue C Technology Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 */
package org.mwc.asset.scenariocontroller2;

import java.io.File;

import org.mwc.asset.scenariocontroller2.CoreControllerPresenter.FilesDroppedListener;

public interface ScenarioDisplay
{
  /**
   * make this view the selected view. We've just loaded some data, so tell
   * everybody we're alive
   */
  void activate();

  /**
   * specify handler for drop events
   * 
   * @param listener
   */
  void addFileDropListener(FilesDroppedListener listener);

  /**
   * this is a relative path, produce an absolute path to a relative location
   * in the project directory
   * 
   * @param tgtDir
   *          relative path
   * @return absolute path
   */
  File getProjectPathFor(File tgtDir);

  /**
   * the project folder may have been updated, refresh what's shown
   * 
   */
  void refreshWorkspace();

  /**
   * display the control file name
   * 
   * @param name
   */
  void setControlName(String name);

  /**
   * display the scenario name
   * 
   * @param name
   */
  void setScenarioName(String name);
}
