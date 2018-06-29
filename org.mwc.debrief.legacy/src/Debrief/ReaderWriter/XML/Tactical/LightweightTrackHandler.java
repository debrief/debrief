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
import java.util.Iterator;

import org.w3c.dom.Element;

import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.Track.LightweightTrack;
import MWC.GUI.Plottable;
import MWC.Utilities.ReaderWriter.XML.Util.ColourHandler;

public abstract class LightweightTrackHandler extends MWC.Utilities.ReaderWriter.XML.MWCXMLReader
{

  private static final String NAME = "Name";
  private static final String VISIBLE = "Visible";
  private static final String MY_NAME = "LightweightTrack";
  private static final String COLOR = "Color";
  private static final String SHOW_NAME = "NameVisible";
  private static final String LINE_STYLE = "LineStyle";

  protected static void exportTrackObject(
      final LightweightTrack track, final org.w3c.dom.Element parent,
      final org.w3c.dom.Document doc)
  {

    Element trk = doc.createElement(MY_NAME);
    trk.setAttribute(NAME, toXML(track.getName()));
    trk.setAttribute(VISIBLE, writeThis(track.getVisible()));
    trk.setAttribute(SHOW_NAME, writeThis(track.getNameVisible()));
    trk.setAttribute(LINE_STYLE, writeThis(track.getLineStyle()));
    
    Color hisColor = track.getCustomColor();
    if(hisColor != null)
    {
      ColourHandler.exportColour(hisColor, trk, doc, COLOR);
    }


    final Iterator<FixWrapper> allItems = track.iterator();
    while (allItems.hasNext())
    {
      final FixWrapper next = (FixWrapper) allItems.next();
      FixHandler.exportFix(next, trk, doc);
    }
    
    parent.appendChild(trk);
  }

  private ArrayList<FixWrapper> _fixes = new ArrayList<FixWrapper>();
  protected Color _color;
  protected boolean _visible;
  protected String _name;
  private boolean _nameVisible;
  protected int _lineStyle;
 
  protected LightweightTrackHandler()
  {
    // inform our parent what type of class we are
    super(MY_NAME);

    addHandler(new FixHandler()
    {
      @Override
      public void addPlottable(Plottable plottable)
      {
        _fixes.add((FixWrapper) plottable);
      }
    });

    addHandler(new ColourHandler(COLOR)
    {
      @Override
      public void setColour(final java.awt.Color res)
      {
        _color = res;
      }
    });
    addAttributeHandler(new HandleIntegerAttribute(LINE_STYLE)
    {
      @Override
      public void setValue(final String name, final int value)
      {
        _lineStyle= value;
      }
    });

    addAttributeHandler(new HandleBooleanAttribute(VISIBLE)
    {
      @Override
      public void setValue(final String name, final boolean val)
      {
        _visible = val;
      }
    });
    addAttributeHandler(new HandleBooleanAttribute(SHOW_NAME)
    {
      @Override
      public void setValue(final String name, final boolean val)
      {
        _nameVisible = val;
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

  }

  @Override
  public void elementClosed()
  {
    // ok, generate the object
    LightweightTrack track = new LightweightTrack(_name, _visible, _nameVisible, _color, _lineStyle);
    track.setVisible(_visible);
    track.setNameVisible(_nameVisible);
    
    for(FixWrapper t: _fixes)
    {
      track.add(t);
    }
    
    _fixes.clear();
    _color = null;
    
    storeTrack(track);

  }
  
  public abstract void storeTrack(LightweightTrack track);

}