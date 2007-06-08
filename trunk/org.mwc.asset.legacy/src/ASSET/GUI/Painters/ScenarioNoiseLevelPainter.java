/*
 * Created by IntelliJ IDEA.
 * User: Ian.Mayo
 * Date: 06-Jun-02
 * Time: 14:53:48
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package ASSET.GUI.Painters;

import ASSET.Models.Environment.CoreEnvironment;
import ASSET.Models.Environment.EnvironmentType;
import ASSET.Models.Environment.SimpleEnvironment;
import ASSET.Models.Mediums.BroadbandRadNoise;
import ASSET.Models.Sensor.Initial.InitialSensor;
import ASSET.Models.Vessels.SSN;
import ASSET.Participants.Status;
import ASSET.Scenario.ScenarioSteppedListener;
import ASSET.Util.SupportTesting;
import MWC.GUI.CanvasType;
import MWC.GUI.Chart.Painters.SpatialRasterPainter;
import MWC.GUI.*;
import MWC.GUI.Properties.BoundedInteger;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;

import java.awt.*;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.Iterator;
import java.util.Vector;

/**
 * **************************************************************
 * painter support
 * **************************************************************
 */
public class ScenarioNoiseLevelPainter extends SpatialRasterPainter implements NoiseSource, ScenarioSteppedListener
{
  ///////////////////////////////////////////////////
  // member variables
  ///////////////////////////////////////////////////

  /**
   * the bit which does the painting
   */
  private PainterComponent _acoustic = null;

  /**
   * the environment we are calculating for
   */
  private EnvironmentType _myEnvironment;

  /**
   * the medium we are painting
   */
  private int _myMedium;


  /**
   * the colour to paint the key
   */
  protected final Color _myColor = Color.darkGray;

  /**
   * somebody to tell us where the participants are
   */
  private StatusProvider _provider = null;


  /**
   * our editor
   */
  private transient ScenarioNoiseInfo _myEditor = null;

  /**
   * whether we should re-plot after each scenario movement
   */
  private boolean _liveUpdate = false;

  /**
   * the set of layers we are being painted into
   */
  private Layers _theLayers = null;


  /**
   * background noise level in this environment
   */
  private double _BackgroundNoiseLevel = 65;

  /**
   * *************************************************
   * * constructor
   * **************************************************
   */

  public ScenarioNoiseLevelPainter(final EnvironmentType theEnv,
                                   final StatusProvider provider,
                                   final int medium,
                                   final Layers theLayers)
  {
    super("Acoustic Painter");
    _acoustic = new PainterComponent(){

			protected void assignPixel(int width, int thisValue, int x_coord, int y_coord)
			{
				// TODO Auto-generated method stub
				
			}

			protected void checkImageValid(int width, int height)
			{
				// TODO Auto-generated method stub
				
			}

			protected void paintTheImage(CanvasType dest, int width, int height)
			{
				// TODO Auto-generated method stub
				
			}

			protected void updatePixelColors(SpatialRasterPainter parent, int width, int height, int min_height, int max_height, CanvasType dest)
			{
				// TODO Auto-generated method stub
				
			}

			public int convertColor(int red, int green, int blue)
			{
				// TODO Auto-generated method stub
				return 0;
			}};
    super.setBuffered(true);
    super.setVisible(false);
    _myEnvironment = theEnv;
    _provider = provider;
    _myMedium = medium;
    _theLayers = theLayers;

    // switch off contours
    super.setContoursVisible(false);
  }

  /**
   * *************************************************
   * interface for classes which can provide the list
   * of statuses we plot
   * *************************************************
   */
  public static interface StatusProvider
  {
    /**
     * return a list of ParticipantStatus objects
     */
    public Vector getStatuses(int theMedium);

    /**
     * listen out for status changes
     */
    public void addScenarioChangeListener(ASSET.Scenario.ScenarioSteppedListener listener);

    /**
     * stop listening for status changes
     */
    public void removeScenarioChangeListener(ASSET.Scenario.ScenarioSteppedListener listener);
  }


  /**
   * *************************************************
   * * member variables
   * **************************************************
   */

  public void paint(final CanvasType dest)
  {
    final Vector statuses = _provider.getStatuses(_myMedium);

    // did we find any?
    if (statuses.size() > 0)
      super.paint(dest);
  }

  public WorldArea getBounds()
  {
    final WorldArea res = null;
    return res;
  }

  public boolean hasEditor()
  {
    return true;
  }

  public Editable.EditorType getInfo()
  {
    if (_myEditor == null)
    {
      _myEditor = new ScenarioNoiseInfo(this);
    }
    return _myEditor;
  }

  public boolean getLiveUpdate()
  {
    return _liveUpdate;
  }

  public void setLiveUpdate(final boolean liveUpdate)
  {
    this._liveUpdate = liveUpdate;

    // listen to parent as necessary
    if (liveUpdate)
      _provider.addScenarioChangeListener(this);
    else
      _provider.removeScenarioChangeListener(this);
  }

  public BoundedInteger getBackgroundNoise()
  {
    return new BoundedInteger((int) _BackgroundNoiseLevel, 0, 150);
  }

  public void setBackgroundNoise(final BoundedInteger backgroundNoiseLevel)
  {
    this._BackgroundNoiseLevel = (double) backgroundNoiseLevel.getCurrent();
  }

  /**
   * provide the delta for the data  (in degrees)
   */
  public double getGridDelta()
  {
    return MWC.Algorithms.Conversions.Nm2Degs(5);
  }

  /**
   * whether the data has been loaded yet
   */
  public boolean isDataLoaded()
  {
    return true;
  }

  public int getValueAt(final WorldLocation location)
  {
    double res = 0;
    final Vector stats = _provider.getStatuses(_myMedium);

    if (stats != null)
    {
      final Iterator it = stats.iterator();
      while (it.hasNext())
      {
        final ParticipantStatus ps = (ParticipantStatus) it.next();
        final WorldLocation origin = ps.location;
        final double sourceLevel = ps.sourceLevel;

        // find out the resultant noise at this point
        final double resultant = _myEnvironment.getResultantEnergyAt(_myMedium, origin, location, sourceLevel);

        // build a cumulative total
        res = InitialSensor.SensorUtils.powerSum(resultant, res);
      }
    }

    // finally add in the background noise level
    res = InitialSensor.SensorUtils.powerSum(_BackgroundNoiseLevel, res);

    return (int) res;
  }

  /**
   * the scenario has restarted, reset
   */
  public void restart()
  {
  }

  /**
   * the scenario has stepped forward
   */
  public void step(long newTime)
  {
    // somehow, we have to update ourselves...
    _theLayers.fireModified(this);
  }

  /////////////////////////////////////////////////////////////
  // info class
  ////////////////////////////////////////////////////////////
  public class ScenarioNoiseInfo extends MWC.GUI.Editable.EditorType implements java.io.Serializable
  {

    public ScenarioNoiseInfo(final ScenarioNoiseLevelPainter data)
    {
      super(data, data.getName(), "Edit");
    }

    public PropertyDescriptor[] getPropertyDescriptors()
    {
      try
      {
        final PropertyDescriptor[] res = {
          prop("Name", "name of this painter"),
          prop("Visible", "whether this layer is visible"),
          prop("BathyRes", "the resolution of the grid plotted"),
          prop("LiveUpdate", "update after each scenario change"),
          prop("BackgroundNoise", "the background noise level in this environemnt"),
        };
        return res;
      }
      catch (IntrospectionException e)
      {
        System.err.println("Problem declaring editable properties:" + e.getMessage());
        return super.getPropertyDescriptors();
      }
    }
  }


  /**
   * class to store the combination of source level and location
   */
  public static class ParticipantStatus
  {
    public double sourceLevel;
    public WorldLocation location;
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////
  // testing for this class
  //////////////////////////////////////////////////////////////////////////////////////////////////
  public static class NoiseLevelTest extends SupportTesting.EditableTesting
  {
    static public final String TEST_ALL_TEST_TYPE = "UNIT";


    /**
     * get an object which we can test
     *
     * @return Editable object which we can check the properties for
     */
    public Editable getEditable()
    {
      final StatusProvider sp = new StatusProvider()
      {
        public void addScenarioChangeListener(ScenarioSteppedListener listener)
        {
        }

        public Vector getStatuses(int theMedium)
        {
          return null;
        }

        public void removeScenarioChangeListener(ScenarioSteppedListener listener)
        {
        }
      };

      return new ScenarioNoiseLevelPainter(null, sp, 0, null);
    }

    public void testQuick()
    {
      final SSN ssn = new SSN(12);
      final Status stat = new Status(12, 0);
      stat.setLocation(new WorldLocation(0, 0, 0));
      stat.setCourse(0);
      stat.setSpeed(new WorldSpeed(0, WorldSpeed.M_sec));
      ssn.setStatus(stat);

      final StatusProvider sp = new StatusProvider()
      {
        public void addScenarioChangeListener(ScenarioSteppedListener listener)
        {
        }

        /**
         * return a list of ParticipantStatus objects
         */
        public Vector getStatuses(int theMedium)
        {
          final Vector res = new Vector(0, 1);
          final ParticipantStatus ps = new ParticipantStatus();
          ps.location = ssn.getStatus().getLocation();
          ps.sourceLevel = ssn.getRadiatedNoiseFor(EnvironmentType.BROADBAND_PASSIVE, 0);
          res.add(ps);
          return res;
        }

        public void removeScenarioChangeListener(ScenarioSteppedListener listener)
        {
        }
      };

      final CoreEnvironment cs = new SimpleEnvironment(1, 1, 1);

      final ScenarioNoiseLevelPainter snp = new ScenarioNoiseLevelPainter(cs, sp, EnvironmentType.BROADBAND_PASSIVE, null);

      // over-ride the radiated noise levels
      ssn.getRadiatedChars().add(ASSET.Models.Environment.EnvironmentType.BROADBAND_PASSIVE, new ASSET.Models.Mediums.BroadbandRadNoise(SSN.DEFAULT_BB_NOISE));
      ssn.getRadiatedChars().add(ASSET.Models.Environment.EnvironmentType.VISUAL, new ASSET.Models.Mediums.Optic(2, new WorldDistance(2, WorldDistance.METRES)));

      // check that the noise is as expected
      double thisNoise = snp.getValueAt(ssn.getStatus().getLocation());
      assertEquals("Default noise at zero speed", SSN.DEFAULT_BB_NOISE, thisNoise, 0.001);

      // over-ride the rad-noise and re-check
      final double newRadNoise = 150;
      final BroadbandRadNoise bRad = (BroadbandRadNoise) ssn.getRadiatedChars().getMedium(new Integer(EnvironmentType.BROADBAND_PASSIVE));
      bRad.setBaseNoiseLevel(newRadNoise);
      thisNoise = snp.getValueAt(ssn.getStatus().getLocation());
      assertEquals("Default noise at zero speed", newRadNoise, thisNoise, 0.001);


    }
  }

}