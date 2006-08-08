/*
 * Created by IntelliJ IDEA.
 * User: Ian.Mayo
 * Date: 11-Jun-02
 * Time: 11:01:12
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package ASSET.GUI.Painters;

import ASSET.Models.Environment.EnvironmentType;
import ASSET.Util.SupportTesting;
import MWC.GUI.CanvasType;
import MWC.GUI.Chart.Painters.SpatialRasterPainter;
import MWC.GUI.Editable;
import MWC.GUI.Properties.BoundedInteger;
import MWC.GenericData.WorldLocation;

import java.awt.*;

public class NoiseSourcePainter extends SpatialRasterPainter implements NoiseSource
{
  ///////////////////////////////////////////////////
  // member variables
  ///////////////////////////////////////////////////
  /**
   * origin for this noise source
   */
  private WorldLocation _myOrigin = new WorldLocation(0, 0, 0);

  /**
   * noise level for this noise source
   */
  private double _sourceLevel = 130;

  /**
   * our editor
   */
  private transient NoiseInfo _myEditor = null;

  /**
   * our environment
   */
  private EnvironmentType _myEnvironment = null;

  /**
   * the medium we plot
   */
  private int _myMedium = 1;

  ///////////////////////////////////////////////////
  // constructor
  ///////////////////////////////////////////////////
  private NoiseSourcePainter(final EnvironmentType theEnv, final int medium)
  {
    super("Noise Source Painter");
    _myEnvironment = theEnv;
    _myMedium = medium;
  }

  public NoiseSourcePainter(final WorldLocation origin, final double sourceLevel, final EnvironmentType theEnv,
                            final int medium)
  {
    this(theEnv, medium);
    _myOrigin = origin;
    _sourceLevel = sourceLevel;
  }

  ///////////////////////////////////////////////////
  // member methods
  ///////////////////////////////////////////////////


  public void paint(final CanvasType dest)
  {
    super.paint(dest);

    // just put a cross at the origin
    final Point pt = dest.toScreen(_myOrigin);
    dest.setColor(Color.white);
    dest.drawLine(pt.x, pt.y - 1, pt.x, pt.y + 1);
    dest.drawLine(pt.x - 1, pt.y, pt.x + 1, pt.y);
  }

  public int getValueAt(final WorldLocation location)
  {
    // work out the noise dissipation from this origin
    int res = 0;
    res = (int) _myEnvironment.getResultantEnergyAt(_myMedium, _myOrigin, location, _sourceLevel);
    return res;
  }

  /**
   * whether the data has been loaded yet
   */
  public boolean isDataLoaded()
  {
    return true;
  }

  /**
   * provide the delta for the data  (in degrees)
   */
  public double getGridDelta()
  {
    return MWC.Algorithms.Conversions.Nm2Degs(5);
  }


  ///////////////////////////////////////////////////
  // editor support
  ///////////////////////////////////////////////////
  public boolean hasEditor()
  {
    return true;
  }

  public Editable.EditorType getInfo()
  {
    if (_myEditor == null)
    {
      _myEditor = new NoiseInfo(this);
    }
    return _myEditor;
  }

  public WorldLocation getOrigin()
  {
    return _myOrigin;
  }

  public void setOrigin(final WorldLocation origin)
  {
    this._myOrigin = origin;
  }

  public BoundedInteger getSourceLevel()
  {
    return new BoundedInteger((int) _sourceLevel, 0, 250);
  }

  public void setSourceLevel(final BoundedInteger sourceLevel)
  {
    this._sourceLevel = (double) sourceLevel.getCurrent();
  }

  /////////////////////////////////////////////////////////////
  // info class
  ////////////////////////////////////////////////////////////
  public class NoiseInfo extends Editable.EditorType implements java.io.Serializable
  {

    public NoiseInfo(final NoiseSourcePainter data)
    {
      super(data, data.getName(), "Edit");
    }

    public java.beans.PropertyDescriptor[] getPropertyDescriptors()
    {
      try
      {
        final java.beans.PropertyDescriptor[] res = {
          prop("Visible", "whether this layer is visible"),
          prop("Origin", "the origin of the noise source"),
          prop("Name", "the name of this noise source"),
          prop("SourceLevel", "the source level of the noise source"),
        };
        return res;
      }
      catch (java.beans.IntrospectionException e)
      {
        return super.getPropertyDescriptors();
      }
    }
  }


  //////////////////////////////////////////////////
  // add testing code
  //////////////////////////////////////////////////
  public static class PainterTest extends SupportTesting.EditableTesting
  {
    /**
     * get an object which we can test
     *
     * @return Editable object which we can check the properties for
     */
    public Editable getEditable()
    {
      return new NoiseSourcePainter(null, EnvironmentType.BROADBAND_PASSIVE);
    }
  }

}
