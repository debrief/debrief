/*
 * Created by IntelliJ IDEA.
 * User: Ian.Mayo
 * Date: Apr 11, 2002
 * Time: 11:40:15 AM
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package MWC.GUI.Java3d;

import MWC.GUI.Editable;
import MWC.GUI.Properties.BoundedInteger;
import MWC.GUI.Properties.TimeIntervalPropertyEditor;
import MWC.GenericData.Duration;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;

/**
 * set of options for how to present 3D plot
 */
public class WorldPlottingOptions implements Editable
{
  ///////////////////////////
  // member variables
  ///////////////////////////

  private boolean showDropBars = true;
  private boolean showComplexModels = false;
  private boolean showSeaSurface = true;
  private boolean showFoggyOcean = true;
  private boolean showSnailTrail = true;
  private boolean showBathySurface = true;
  private boolean showBathyLines = false;
  private boolean showVesselStatuses = false;
  private long snailTrailLength = 1000 * 60 * 60 * 5;
  private boolean showCoastline = false;
  private int _depthScale = 0;
  private int _textSize = 50;
  private double modelStretch = 1d;
  private MWC.GenericData.WorldDistance _gridDelta = new MWC.GenericData.WorldDistance(1, MWC.GenericData.WorldDistance.NM);


  private int _snailLineWidth = 1;

  // my editor
  Editable.EditorType _myEditor = null;

  //////////////////////////////////////////////////
  // property names for the editor
  //////////////////////////////////////////////////
  public final static String SNAIL_LINE_WIDTH = "SnailLineWidth";
  private static final String MY_PANEL_NAME = "3D View Options";


  ///////////////////////////
  // constructor
  ///////////////////////////
  public WorldPlottingOptions()
  {
  }

  ///////////////////////////
  // member methods
  ///////////////////////////
  public void addListener(java.beans.PropertyChangeListener listener)
  {
    if (_myEditor == null)
      _myEditor = new WorldInfo(this);

    _myEditor.addPropertyChangeListener(listener);
  }

  public void removeListener(java.beans.PropertyChangeListener listener)
  {
    if (_myEditor != null)
      _myEditor.removePropertyChangeListener(listener);
  }


  ///////////////////////////
  // bean accessors
  ///////////////////////////
  public Integer getModelStretch()
  {
    return new Integer((int) modelStretch);
  }

  public void setModelStretch(Integer modelStretch)
  {
    this.modelStretch = modelStretch.intValue();
  }

  public BoundedInteger getTextSize()
  {
    return new BoundedInteger(_textSize, 12, 90);
  }

  public void setTextSize(BoundedInteger textSize)
  {
    this._textSize = textSize.getCurrent();
  }

  public MWC.GenericData.WorldDistance getGridDelta()
  {
    return _gridDelta;
  }

  public void setGridDelta(MWC.GenericData.WorldDistance gridDelta)
  {
    _gridDelta = gridDelta;
  }

  public boolean getShowVesselStatuses()
  {
    return showVesselStatuses;
  }

  public void setShowVesselStatuses(boolean showVesselStatuses)
  {
    this.showVesselStatuses = showVesselStatuses;
  }

  public BoundedInteger getDepthStretch()
  {
    return new BoundedInteger(_depthScale, 1, 30);
  }

  public void setDepthStretch(BoundedInteger val)
  {
    _depthScale = val.getCurrent();
  }

  public BoundedInteger getSnailLineWidth()
  {
    return new BoundedInteger(_snailLineWidth, 1, 6);
  }

  public void setSnailLineWidth(BoundedInteger width)
  {
    _snailLineWidth = width.getCurrent();
  }

  public boolean getShowBathySurface()
  {
    return showBathySurface;
  }

  public void setShowBathySurface(boolean showBathy)
  {
    this.showBathySurface = showBathy;
  }

  public boolean getShowBathyLines()
  {
    return showBathyLines;
  }

  public void setShowBathyLines(boolean showBathy)
  {
    this.showBathyLines = showBathy;
  }

  public boolean getShowCoastline()
  {
    return showCoastline;
  }

  public void setShowCoastline(boolean showCoastline)
  {
    this.showCoastline = showCoastline;
  }

  public boolean getShowComplexModels()
  {
    return showComplexModels;
  }

  public void setShowComplexModels(boolean showComplexModels)
  {
    this.showComplexModels = showComplexModels;
  }

  public boolean getShowDropBars()
  {
    return showDropBars;
  }

  public void setShowDropBars(boolean showDropBars)
  {
    this.showDropBars = showDropBars;
  }

  public boolean getShowFoggyOcean()
  {
    return showFoggyOcean;
  }

  public void setShowFoggyOcean(boolean showFoggyOcean)
  {
    this.showFoggyOcean = showFoggyOcean;
  }

  public boolean getShowSeaSurface()
  {
    return showSeaSurface;
  }

  public void setShowSeaSurface(boolean showSeaSurface)
  {
    this.showSeaSurface = showSeaSurface;
  }

  public boolean getShowSnailTrail()
  {
    return showSnailTrail;
  }

  public void setShowSnailTrail(boolean showSnailTrail)
  {
    this.showSnailTrail = showSnailTrail;
  }

  /**
   * length of trail to draw
   */
  public void setSnailTrailLength(Duration len)
  {
    snailTrailLength = (long) len.getValueIn(Duration.MILLISECONDS);
  }

  public Duration getSnailTrailLength()
  {
    return new Duration(snailTrailLength, Duration.MILLISECONDS);
  }

  public void setSnailTrailLength(long snailTrailLength)
  {
    this.snailTrailLength = snailTrailLength;
  }

  ///////////////////////////
  // method support
  ///////////////////////////

  ///////////////////////////
  // editor support
  ///////////////////////////
  public Editable.EditorType getInfo()
  {
    if (_myEditor == null)
      _myEditor = new WorldInfo(this);
    return _myEditor;
  }

  public String getName()
  {
    return MY_PANEL_NAME;
  }

  public boolean hasEditor()
  {
    return true;
  }

  public String toString()
  {
    return getName();
  }

  //////////////////////////////////////////////////////
  // bean info for this class
  /////////////////////////////////////////////////////
  public class WorldInfo extends Editable.EditorType
  {

    public WorldInfo(WorldPlottingOptions data)
    {
      super(data, MY_PANEL_NAME, "");
    }

    public PropertyDescriptor[] getPropertyDescriptors()
    {
      try
      {
        PropertyDescriptor[] res = {
          prop("ModelStretch", "the stretch applied to models"),
          prop("ShowCoastline", "whether to show the coastline"),
          //          prop("ShowComplexModels", "whether to show complex participant models"),
          prop("ShowBathySurface", "whether to show the bathymetric floor (as a continuous surface)"),
          prop("ShowBathyLines", "whether to show the bathymetric floor (as a grid of lines)"),
          prop("ShowDropBars", "whether to show bars from position to sea-level"),
          prop("ShowFoggyOcean", "whether to make the ocean slightly opaque"),
          prop("ShowSeaSurface", "whether to show the sea surface"),
          prop("ShowSnailTrail", "whether to show the snail trails"),
          prop("SnailTrailLength", "the length of snail trail to plot"),
          prop("DepthStretch", "whether to exaggerate the depth scale"),
          prop(SNAIL_LINE_WIDTH, "width of the snail-trail line"),
          prop("ShowVesselStatuses", "whether to show the current vessel statuses"),
          prop("GridDelta", "the separation to use for grid-lines"),
          prop("TextSize", "the font size to use for the text"),
        };

        res[0].setPropertyEditorClass(MagFactorEditor.class);
        return res;
      }
      catch (IntrospectionException e)
      {
        //return super.getPropertyDescriptors();
        e.printStackTrace();
        return super.getPropertyDescriptors();
      }
    }

  }

  public static class MagFactorEditor extends TimeIntervalPropertyEditor
  {

    /**
     * test labels for the available frequencies
     */
    private String _stringTags[] =
      {
        "1/1",
        "2/1",
        "5/1",
        "10/1",
        "20/1",
        "50/1"};

    /**
     * values (in millis) for the selectable frequencies
     */
    private int _magnifications[] =
      {
        1,
        2,
        5,
        10,
        20,
        50};

    public String[] getTags()
    {
      return _stringTags;
    }

    public int[] getValues()
    {
      return _magnifications;
    }
    
    /**
     * Initialise the value of this editable object (expecting to receive
     * either an Integer or a String
     *
     * @param p1 the value to use
     */
    public void setValue(Object p1)
    {
      if (p1 instanceof Integer)
      {
      	setAsText("" + p1 + "/1");
      }
      if (p1 instanceof String)
      {
        String val = (String) p1;
        setAsText(val);
      }
    }    
  }
}
