/*******************************************************************************
 * Debrief - the Open Source Maritime Analysis Application
 * http://debrief.info
 *  
 * (C) 2000-2020, Deep Blue C Technology Ltd
 *  
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html)
 *  
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 *******************************************************************************/
package info.limpet.stackedcharts.ui.editor.commands;

import info.limpet.stackedcharts.model.Dataset;
import info.limpet.stackedcharts.model.DependentAxis;

import org.eclipse.gef.commands.Command;

public class AddDatasetsToAxisCommand extends Command
{
  private final Dataset[] datasets;
  private final DependentAxis parent;

  public AddDatasetsToAxisCommand(DependentAxis parent, Dataset... datasets)
  {
    this.datasets = datasets;
    this.parent = parent;
  }

  @Override
  public void execute()
  {
    for (Dataset ds : datasets)
    {
      parent.getDatasets().add(ds);
    }
  }

  @Override
  public void undo()
  {
    for (Dataset ds : datasets)
    {
      parent.getDatasets().remove(ds);
    }
  }
}
