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

import java.util.concurrent.atomic.AtomicBoolean;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;

import Debrief.Wrappers.Extensions.Measurements.CoreDataset;
import Debrief.Wrappers.Extensions.Measurements.DataFolder;
import Debrief.Wrappers.Extensions.Measurements.DataItem;
/**
 * Title:        Debrief 2000
 * Description:  Debrief 2000 Track Analysis Software
 * Copyright:    Copyright (c) 2000
 * Company:      MWC
 * @author Ian Mayo
 * @version 1.0
 */

abstract public class DataFolderHandler extends
    MWC.Utilities.ReaderWriter.XML.MWCXMLReader
{

  private static final String NAME = "Name";
  private static final String MY_TYPE = "DataFolder";

  public static void exportThisFolder(final DataFolder folder,
      final Element parent, final Document doc)
  {
    final Element df = doc.createElement(MY_TYPE);

    // attributes
    df.setAttribute(NAME, folder.getName());

    for (final DataItem child : folder)
    {
      if (child instanceof DataFolder)
      {
        final DataFolder childFolder = (DataFolder) child;
        exportThisFolder(childFolder, df, doc);
      }
      else if (child instanceof CoreDataset)
      {
        final CoreDataset childD = (CoreDataset) child;
        DatasetHandler.exportThisDataset(childD, df, doc);
      }
    }

    // now children
    parent.appendChild(df);
  }

  private DataFolder _folder;

  /**
   * use flag to allow recursive data-folders. This will prevent us trying to handle a child folder
   * ourselves
   */
  private final AtomicBoolean inuse = new AtomicBoolean(false);

  public DataFolderHandler()
  {
    super(MY_TYPE);

    addAttributeHandler(new HandleAttribute(NAME)
    {
      @Override
      public void setValue(final String name, final String value)
      {
        _folder.setName(value);
      }
    });
    addHandler(new DatasetHandler()
    {
      @Override
      public void addDataset(final CoreDataset dataset)
      {
        _folder.add(dataset);
      }
    });
  }

  abstract public void addFolder(DataFolder data);

  @Override
  public boolean canHandleThis(final String element)
  {
    return super.canHandleThis(element) && !inuse.get();
  }

  @Override
  public final void elementClosed()
  {
    addFolder(_folder);

    _folder = null;
    inuse.set(false);
  }

  @Override
  public final void handleOurselves(final String name, final Attributes atts)
  {
    _folder = new DataFolder();
    inuse.set(true);
    addHandler(new DataFolderHandler()
    {
      @Override
      public void addFolder(final DataFolder folder)
      {
        _folder.add(folder);
      }
    });
    // let parent get started
    super.handleOurselves(name, atts);
  }

}