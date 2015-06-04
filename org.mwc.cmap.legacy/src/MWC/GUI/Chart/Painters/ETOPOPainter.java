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
package MWC.GUI.Chart.Painters;

import java.awt.Color;
import java.awt.Image;
import java.awt.image.ImageObserver;
import java.beans.PropertyEditorSupport;
import java.io.File;
import java.util.Enumeration;
import java.util.Vector;

import MWC.GUI.BaseLayer;
import MWC.GUI.CanvasType;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.Plottable;
import MWC.GUI.ETOPO.ETOPOWrapper;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldLocation;

public class ETOPOPainter extends BaseLayer implements Layer.BackgroundLayer
{

  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	/**
   * our visible flag
   */
  private boolean _isVisible = true;


  /**
   * our editor
   */
  transient private Editable.EditorType _myEditor;

  /**
   * our data object
   */
  private static ETOPOWrapper _etopo = null;

  ///////////////////////////////////
  // constructor
  ///////////////////////////////////
  public ETOPOPainter(final String pathName, final Layers parentLayers)
  {
    if (_etopo == null)
      _etopo = new ETOPOWrapper(pathName, parentLayers, this);

    super.setBuffered(true);
  }

  //////////////////////////////////////////////////
  // static accessor to see if our data-file is there
  //////////////////////////////////////////////////
  static public boolean dataFileExists(final String etopo_path)
  {
    boolean res = false;

    final String thePath = etopo_path + "//" + "Etopo5";

    final File testFile = new File(thePath);

    if (testFile.exists())
      res = true;

    return res;
  }

  ////////////////////////////////////
  // member methods
  ///////////////////////////////////

  /**
   * accessor to let other classes load the etopo data
   */
  public ETOPOWrapper getETOPO()
  {
    return _etopo;
  }

	/** whether this type of BaseLayer is able to have shapes added to it
	 * 
	 * @return
	 */
	@Override
	public boolean canTakeShapes()
	{
		return false;
	}
  ////////////////////////////////////
  // layer support methods
  public String getName()
  {
    return "ETOPO Bathy Data";
  }

  public void paint(final CanvasType dest)
  {
    if (getVisible())
      _etopo.doPaint(dest);
  }

  public WorldArea getBounds()
  {
    return null;
  }

  public void add(final Plottable point)
  {
    // ignore
  }

  public void append(final Layer other)
  {
    // ignore
  }

  public void exportShape()
  {
    // ignore
  }

  public Enumeration<Editable> elements()
  {
    return new Vector<Editable>(0, 1).elements();
  }

  public Editable.EditorType getInfo()
  {
    if (_myEditor == null)
      _myEditor = new ETOPOInfo(this);

    return _myEditor;
  }

  public boolean getVisible()
  {
    return _isVisible;
  }

  public void setVisible(final boolean isVisible)
  {
    _isVisible = isVisible;
  }

  /**
   * accessor for whether to show land
   */
  public boolean getShowLand()
  {
    return _etopo.getShowLand();
  }

  /**
   * setter for whether to show land
   */
  public void setShowLand(final boolean val)
  {
    _etopo.setShowLand(val);
  }


  public boolean hasEditor()
  {
    return true;
  }

  public double rangeFrom(final WorldLocation other)
  {
    return INVALID_RANGE;
  }

  public void removeElement(final Plottable point)
  {
  }

  /**
   * where the key is plotted
   */
  public Integer getKeyLocation()
  {
    return _etopo.getKeyLocation();
  }

  public void setKeyLocation(final Integer val)
  {
    _etopo.setKeyLocation(val);
  }

  /**
   * the colour of the key
   */
  public void setColor(final Color val)
  {
    _etopo.setColor(val);
  }

  /**
   * retrieve the colour of the key
   */
  public Color getColor()
  {
    return _etopo.getColor();
  }
//
//
//  public double getBlueComponent()
//  {
//    return ETOPOWrapper.BLUE_MULTIPLIER;
//  }
//
//  public void setBlueComponent(double BLUE_MULTIPLIER)
//  {
//    ETOPOWrapper.BLUE_MULTIPLIER = BLUE_MULTIPLIER;
//  }
//
//
//  public double getGreenComponent()
//  {
//    return ETOPOWrapper.GREEN_MULTIPLIER;
//  }
//
//  public void setGreenComponent(double GREEN_MULTIPLIER)
//  {
//    ETOPOWrapper.GREEN_MULTIPLIER = GREEN_MULTIPLIER;
//  }
//
//  public double getGreenBase()
//  {
//    return ETOPOWrapper.GREEN_BASE_VALUE;
//  }
//
//  public void setGreenBase(double GREEN_BASE_VALUE)
//  {
//    ETOPOWrapper.GREEN_BASE_VALUE = GREEN_BASE_VALUE;
//  }


  public int getLineThickness()
  {
    return _etopo.getThickness();
  }

  public void setLineThickness(final int thickness)
  {
    _etopo.setThickness(thickness);
  }


  /////////////////////////////////////////////////////////////
  // info class
  ////////////////////////////////////////////////////////////
  public class ETOPOInfo extends Editable.EditorType implements java.io.Serializable, ImageObserver
  {
    /**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public boolean imageUpdate(final Image img, final int infoflags,
                               final int x, final int y, final int width, final int height)
    {
      return false;
    }

    public ETOPOInfo(final ETOPOPainter data)
    {
      super(data, data.getName(), "");
    }

    public java.beans.PropertyDescriptor[] getPropertyDescriptors()
    {
      try
      {
        final java.beans.PropertyDescriptor[] res = {
          prop("Visible", "whether this layer is visible"),
          displayProp("KeyLocation", "Key location", "the current location of the color-key"),
          prop("Color", "the color of the color-key"),
          displayProp("ShowLand", "Show land", "whether to shade land-data"),
          displayProp("LineThickness", "Line thickness", "the thickness to plot the scale border"),
          //          prop("GreenBase", "the base value of the green colour"),
          //          prop("GreenComponent", "the proportion of blue to make green"),
          //          prop("BlueComponent", "the blue colour range"),
        };
        res[1].setPropertyEditorClass(KeyLocationPropertyEditor.class);
        res[4].setPropertyEditorClass(MWC.GUI.Properties.LineWidthPropertyEditor.class);

        return res;
      }
      catch (final java.beans.IntrospectionException e)
      {
        return super.getPropertyDescriptors();
      }
    }
  }


  /**
   * custom editor providing the selection of locations for the depth scale key
   */
  public static class KeyLocationPropertyEditor extends PropertyEditorSupport
  {

    final static public int NOT_SHOWN = 0;
    final static public int LEFT = 1;
    final static public int RIGHT = 2;
    final static public int TOP = 3;
    final static public int BOTTOM = 4;

    /**
     * the list of tags we display
     */
    String[] _myTags;

    protected Integer _myLineLocation;

    public String[] getTags()
    {
      if (_myTags == null)
      {
        _myTags = new String[]{"Not Shown",
                               "Left",
                               "Right",
                               "Top",
                               "Bottom"};
      }
      return _myTags;
    }

    public Object getValue()
    {
      return _myLineLocation;
    }

    public void setValue(final Object p1)
    {
      if (p1 instanceof Integer)
      {
        _myLineLocation = (Integer) p1;
      }
      if (p1 instanceof String)
      {
        final String val = (String) p1;
        setAsText(val);
      }
    }

    public void setAsText(final String val)
    {
      for (int i = 0; i < getTags().length; i++)
      {
        final String thisStr = getTags()[i];
        if (thisStr.equals(val))
          _myLineLocation = new Integer(i);
      }
    }

    public String getAsText()
    {
      String res = null;
      final int index = _myLineLocation.intValue();
      res = getTags()[index];
      return res;
    }
  }


}
