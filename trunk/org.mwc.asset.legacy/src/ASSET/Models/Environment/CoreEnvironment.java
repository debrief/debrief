/*
 * Created by IntelliJ IDEA.
 * User: Ian.Mayo
 * Date: 06-Jun-02
 * Time: 12:07:36
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package ASSET.Models.Environment;

import ASSET.Models.MWCModel;
import ASSET.Models.Sensor.Lookup.OpticLookupSensor;
import ASSET.Models.Sensor.Lookup.RadarLookupSensor;
import ASSET.Models.Sensor.Lookup.MADLookupSensor;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldVector;

abstract public class CoreEnvironment implements EnvironmentType, java.io.Serializable, MWCModel
{

	private String _myName;

  /**
   * store the maximum number of mediums we will need to work with
   */
  public static final int MAX_NUM_MEDIUMS = 10;

  /**
   * the set of mediums we know about
   */
  private MediumType[] _myMediums = new MediumType[MAX_NUM_MEDIUMS];


  //////////////////////////////////////////////////
  // lookup sensor tables
  //////////////////////////////////////////////////

  /** our description of radar lookup environment
   *
   */
  private RadarLookupSensor.RadarEnvironment _radarEnvironment;

  /** our description of the visual lookup environment
   *
   */
  private OpticLookupSensor.OpticEnvironment _opticEnvironment;

  /** our description of the MAD environment
   *
   */
  private MADLookupSensor.MADEnvironment _madEnvironment;

  ////////////////////////////////////////////////////////////
  // constructor
  ////////////////////////////////////////////////////////////

  public CoreEnvironment()
  {
    _myMediums[EnvironmentType.BROADBAND_PASSIVE] = new ASSET.Models.Environment.Mediums.BroadbandMedium();
    _myName = "Core";
  }

  ////////////////////////////////////////////////////////////
  // member methods
  ////////////////////////////////////////////////////////////


  public String getName()
  {
    return _myName;
  }

  public void setName(String name)
  {
  	_myName = name;
  }
  
  public double getResultantEnergyAt(final int medium,
                                     final WorldLocation origin,
                                     final WorldLocation destination,
                                     final double sourceLevel)
  {
    double res = INVALID_RESULT;

    // retrieve this medium
    final MediumType thisEnv = _myMediums[medium];

    // did we find it?
    if (thisEnv != null)
    {
      // calculate the resultant energy
      res = thisEnv.getResultantEnergyAt(origin, destination, sourceLevel);
    }

    return res;

  }

  /**
   * get the loss in indicated medium between the indicated points
   *
   * @param medium      the medium we're looking at
   * @param origin      the source of the energy
   * @param destination the destination we're looking at
   * @return the loss between the points
   */
  public double getLossBetween(int medium,
                               WorldLocation origin,
                               WorldLocation destination)
  {
    double res = INVALID_RESULT;

    // retrieve this medium
    final MediumType thisEnv = _myMediums[medium];

    // did we find it?
    if (thisEnv != null)
    {
      // calculate the resultant energy
      res = thisEnv.getLossBetween(origin, destination);
    }

    return res;
  }

  /**
   * get the background noise in the indicated bearing from the indicated location
   *
   * @param medium   the medium we're looking at
   * @param origin   where we are at the moment
   * @param brg_degs the direction we're looking at
   * @return the background noise
   */
  public double getBkgndNoise(int medium, WorldLocation origin, double brg_degs)
  {
    double res = INVALID_RESULT;

    // retrieve this medium
    final MediumType thisEnv = _myMediums[medium];

    // did we find it?
    if (thisEnv != null)
    {
      // calculate the resultant energy
      res = thisEnv.getBkgndNoise(origin, brg_degs);
    }

    return res;
  }

  //////////////////////////////////////////////////
  // lookup support
  //////////////////////////////////////////////////

  /** get the optic lookup tables for this environment
   *
   * @return
   */
  public OpticLookupSensor.OpticEnvironment getOpticEnvironment()
  {
    return _opticEnvironment;
  }

  /** set the optic lookup tables for this environment
   *
   * @param opticEnvironment
   */
  public void setOpticEnvironment(OpticLookupSensor.OpticEnvironment opticEnvironment)
  {
    this._opticEnvironment = opticEnvironment;
  }

  /** get the radar lookup tables for this environment
   *
   * @return
   */
  public RadarLookupSensor.RadarEnvironment getRadarEnvironment()
  {
    return _radarEnvironment;
  }

  /** set the radar lookup tables for this environment
   *
   * @param radarEnvironment
   */
  public void setRadarEnvironment(RadarLookupSensor.RadarEnvironment radarEnvironment)
  {
    this._radarEnvironment = radarEnvironment;
  }

  /** get the MAD lookup table
   *
   * @return
   */
  public MADLookupSensor.MADEnvironment getMADEnvironment()
  {
    return _madEnvironment;
  }

  /** set the MAD lookup table
   *
   * @param madEnvironment
   */
  public void setMADEnvironment(MADLookupSensor.MADEnvironment madEnvironment)
  {
    this._madEnvironment = madEnvironment;
  }


  ////////////////////////////////////////////////////////////
  // other env factors
  ////////////////////////////////////////////////////////////

  /**
   * get the light level at this location
   *
   * @param time     the time we're talking about
   * @param location the location we're talking about
   * @return the current light level
   */
  public int getLightLevelFor(long time, WorldLocation location)
  {
    return EnvironmentType.DUSK;
  }

  /**
   * get the version details for this model.
   * <pre>
   * $Log: CoreEnvironment.java,v $
   * Revision 1.2  2006/09/11 15:15:01  Ian.Mayo
   * Give environments a name
   *
   * Revision 1.1  2006/08/08 14:21:43  Ian.Mayo
   * Second import
   *
   * Revision 1.1  2006/08/07 12:25:51  Ian.Mayo
   * First versions
   *
   * Revision 1.9  2004/11/02 20:51:43  ian
   * Implement MAD sensors
   *
   * Revision 1.8  2004/10/27 15:06:06  Ian.Mayo
   * Reflect changed signature of lookup environments
   *
   * Revision 1.7  2004/10/26 14:45:39  Ian.Mayo
   * Store lookup tables here.
   *
   * Revision 1.6  2004/10/20 15:14:48  Ian.Mayo
   * Switch to array of mediums, instead of inefficient HashMap
   *
   * Revision 1.5  2004/05/24 16:01:06  Ian.Mayo
   * Commit updates from home
   * <p/>
   * Revision 1.2  2004/03/25 22:47:16  ian
   * Reflect new simple environment constructor
   * <p/>
   * Revision 1.1.1.1  2004/03/04 20:30:53  ian
   * no message
   * <p/>
   * Revision 1.4  2004/02/18 08:49:23  Ian.Mayo
   * Sync from home
   * <p/>
   * Revision 1.2  2003/11/07 14:40:31  Ian.Mayo
   * Include templated javadoc
   * <p/>
   * <p/>
   * </pre>
   */
  public String getVersion()
  {
    return "$Date$";
  }


  public static void main(String[] args)
  {
    final WorldLocation origin = new WorldLocation(0, 0, 0);
    final WorldLocation destination = origin.add(new WorldVector(0,
                                                                 MWC.Algorithms.Conversions.Yds2Degs(20000), 0));
    final CoreEnvironment ce = new SimpleEnvironment(1, 1, 1);
    final double res = ce.getResultantEnergyAt(1, origin, destination, 200);
    System.out.println("res is:" + res);
  }
}
