/*
* Created by IntelliJ IDEA.
* User: Ian.Mayo
* Date: May 14, 2002
* Time: 12:26:13 PM
* To change template for new class use
* Code Style | Class Templates options (Tools | IDE Options).
*/
package MWC.GUI.Chart.Painters;

import MWC.GUI.*;
import MWC.GUI.ETOPO.ETOPOWrapper;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldLocation;

import java.awt.*;
import java.awt.image.ImageObserver;
import java.beans.PropertyEditorSupport;
import java.io.File;
import java.util.Enumeration;
import java.util.Vector;

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
  public ETOPOPainter(String pathName, Layers parentLayers)
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

    File testFile = new File(thePath);

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

  ////////////////////////////////////
  // layer support methods
  public String getName()
  {
    return "ETOPO Bathy Data";
  }

  public void paint(CanvasType dest)
  {
    if (getVisible())
      _etopo.doPaint(dest);
  }

  public WorldArea getBounds()
  {
    return null;
  }

  public void add(Plottable point)
  {
    // ignore
  }

  public void append(Layer other)
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

  public void setVisible(boolean isVisible)
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
  public void setShowLand(boolean val)
  {
    _etopo.setShowLand(val);
  }


  public boolean hasEditor()
  {
    return true;
  }

  public double rangeFrom(WorldLocation other)
  {
    return INVALID_RANGE;
  }

  public void removeElement(Plottable point)
  {
  }

  /**
   * where the key is plotted
   */
  public Integer getKeyLocation()
  {
    return _etopo.getKeyLocation();
  }

  public void setKeyLocation(Integer val)
  {
    _etopo.setKeyLocation(val);
  }

  /**
   * the colour of the key
   */
  public void setColor(Color val)
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

  public void setLineThickness(int thickness)
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

		public boolean imageUpdate(Image img, int infoflags,
                               int x, int y, int width, int height)
    {
      return false;
    }

    public ETOPOInfo(ETOPOPainter data)
    {
      super(data, data.getName(), "");
    }

    public java.beans.PropertyDescriptor[] getPropertyDescriptors()
    {
      try
      {
        java.beans.PropertyDescriptor[] res = {
          prop("Visible", "whether this layer is visible"),
          prop("KeyLocation", "the current location of the color-key"),
          prop("Color", "the color of the color-key"),
          prop("ShowLand", "whether to shade land-data"),
          prop("LineThickness", "the thickness to plot the scale border"),
          //          prop("GreenBase", "the base value of the green colour"),
          //          prop("GreenComponent", "the proportion of blue to make green"),
          //          prop("BlueComponent", "the blue colour range"),
        };
        res[1].setPropertyEditorClass(KeyLocationPropertyEditor.class);
        res[4].setPropertyEditorClass(MWC.GUI.Properties.LineWidthPropertyEditor.class);

        return res;
      }
      catch (java.beans.IntrospectionException e)
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

    public void setValue(Object p1)
    {
      if (p1 instanceof Integer)
      {
        _myLineLocation = (Integer) p1;
      }
      if (p1 instanceof String)
      {
        String val = (String) p1;
        setAsText(val);
      }
    }

    public void setAsText(String val)
    {
      for (int i = 0; i < getTags().length; i++)
      {
        String thisStr = getTags()[i];
        if (thisStr.equals(val))
          _myLineLocation = new Integer(i);
      }
    }

    public String getAsText()
    {
      String res = null;
      int index = _myLineLocation.intValue();
      res = getTags()[index];
      return res;
    }
  }


}
