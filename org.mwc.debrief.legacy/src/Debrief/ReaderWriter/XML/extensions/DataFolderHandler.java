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
package Debrief.ReaderWriter.XML.extensions;

/**
 * Title:        Debrief 2000
 * Description:  Debrief 2000 Track Analysis Software
 * Copyright:    Copyright (c) 2000
 * Company:      MWC
 * @author Ian Mayo
 * @version 1.0
 */

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;

import Debrief.Wrappers.Extensions.Measurements.CoreDataset;
import Debrief.Wrappers.Extensions.Measurements.DataFolder;
import Debrief.Wrappers.Extensions.Measurements.DataItem;

abstract public class DataFolderHandler extends
    MWC.Utilities.ReaderWriter.XML.MWCXMLReader
{

  private static final String MY_TYPE = "DataFolder";
  private DataFolder _folder;
  final int level;
  
  public DataFolderHandler(int levelsRemaining)
  {
    super(MY_TYPE);
    
    level = levelsRemaining;

    addAttributeHandler(new HandleAttribute("Name")
    {
      public void setValue(final String name, final String value)
      {
        _folder.setName(value);
      }
    });
    addHandler(new DatasetHandler()
    {
      @Override
      public void addDataset(CoreDataset dataset)
      {
        System.out.println("adding dataset:" + dataset.getName() + " to folder:" + _folder.getName() + " at level:" + level);
        _folder.add(dataset);
      }
    });
    if (levelsRemaining > 0)
    {
      System.out.println("Declaring handler at level: " + level);
      addHandler(new DataFolderHandler(--levelsRemaining)
      {
        @Override
        public void addFolder(DataFolder folder)
        {
          System.out.println("adding " + folder.getName() + " into " + _folder.getName());
          _folder.add(folder);
        }
      });
    }
  }

  public final void handleOurselves(final String name, final Attributes atts)
  {
    _folder = new DataFolder();

    // let parent get started
    super.handleOurselves(name, atts);
  }

  public final void elementClosed()
  {
    System.out.println("Adding folder: " + _folder.getName() + " at level:" + level);
    
    addFolder(_folder);

    _folder = null;
  }

  abstract public void addFolder(DataFolder data);

  public static void exportThisFolder(DataFolder folder, Element parent,
      Document doc)
  {
    Element df = doc.createElement("DataFolder");

    // attributes
    df.setAttribute("Name", folder.getName());

    for (DataItem child : folder)
    {
      if (child instanceof DataFolder)
      {
        DataFolder childFolder = (DataFolder) child;
        exportThisFolder(childFolder, df, doc);
      }
      else if (child instanceof CoreDataset)
      {
        CoreDataset childD = (CoreDataset) child;
        DatasetHandler.exportThisDataset(childD, df, doc);
      }
    }

    // now children
    parent.appendChild(df);
  }

}