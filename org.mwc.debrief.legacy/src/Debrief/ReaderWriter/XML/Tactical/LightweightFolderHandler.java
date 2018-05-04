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
package Debrief.ReaderWriter.XML.Tactical;

/**
 * Title:        Debrief 2000
 * Description:  Debrief 2000 Track Analysis Software
 * Copyright:    Copyright (c) 2000
 * Company:      MWC
 * @author Ian Mayo
 * @version 1.0
 */

import java.awt.Color;
import java.util.ArrayList;
import java.util.Enumeration;

import org.w3c.dom.Element;

import Debrief.Wrappers.Track.LightweightTrack;
import Debrief.Wrappers.Track.LightweightTrackFolder;
import MWC.GUI.Editable;
import MWC.GUI.Layers;
import MWC.Utilities.ReaderWriter.XML.Util.ColourHandler;

public class LightweightFolderHandler extends MWC.Utilities.ReaderWriter.XML.MWCXMLReader
{
	private static final String FOLDER = "LightweightFolder";

  private static final String SHOW_NAMES = "ShowNames";

  private static final String NAME = "Name";

  private static final String COLOR = "Color";

	private boolean _showName;

  protected Color _color;

  protected ArrayList<LightweightTrack> _tracks = new ArrayList<LightweightTrack>();

  private String _name;

  private Layers _myLayers;
	
	public LightweightFolderHandler(final Layers layers)
	{
		// inform our parent what type of class we are
		super(FOLDER);

		_myLayers = layers;
		
    addAttributeHandler(new HandleBooleanAttribute(SHOW_NAMES)
    {
      @Override
      public void setValue(final String name, final boolean val)
      {
        _showName = val;
      }
    });
		addAttributeHandler(new HandleAttribute(NAME)
		{
      @Override
      public void setValue(String name, String value)
      {
        _name = value;
      }
		});
		addHandler(new ColourHandler(COLOR)
    {
      @Override
      public void setColour(Color res)
      {
        _color = res;
      }
    });
		addHandler(new LightweightTrackHandler(){

      @Override
      public void storeTrack(LightweightTrack track)
      {
        _tracks.add(track);
      }
		  
		});
	}
	
	@Override
  public void elementClosed()
  {
	  LightweightTrackFolder folder = new LightweightTrackFolder(_name);
	  folder.setShowName(_showName);
	  folder.setColor(_color);

	  for(LightweightTrack t: _tracks)
	  {
	    folder.add(t);
	  }

	  _myLayers.addThisLayer(folder);
  }


  public static void exportThisFolder(final org.w3c.dom.Document doc, final Element parent,
			final LightweightTrackFolder folder)
	{
		final Element folderE = doc.createElement(FOLDER);

		folderE.setAttribute(NAME, folder.getName());
		folderE.setAttribute(SHOW_NAMES, writeThis(folder.isShowName()));
		
		ColourHandler.exportColour(folder.getColor(), folderE, doc, COLOR);
		
		Enumeration<Editable> items = folder.elements();
		while(items.hasMoreElements())
		{
		  LightweightTrack track = (LightweightTrack) items.nextElement();
		  LightweightTrackHandler.exportTrackObject(track, folderE, doc);
		}
		
		parent.appendChild(folderE);
	}

}