package ASSET.Util.XML.Movement;

import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldSpeed;
import MWC.Utilities.ReaderWriter.XML.Util.WorldDistanceHandler;
import MWC.Utilities.ReaderWriter.XML.Util.WorldSpeedHandler;

/**
 * Handler for vehicles capable of manouvering in three dimensions
 */
abstract public class ThreeDimMovementCharsHandler extends MovementCharsHandler
{
  private final static String type = "ThreeDimMovementCharacteristicsType";

  protected final static String MIN_Height = "MinHeight";
  protected final static String MAX_Height = "MaxHeight";
  protected final static String DEFAULT_CLIMB_RATE = "DefaultClimbRate";
  protected final static String DEFAULT_DIVE_RATE = "DefaultDiveRate";

  protected WorldDistance _minHeight = null;
  protected WorldDistance _maxHeight = null;
  protected WorldSpeed _climbRate = null;
  protected WorldSpeed _diveRate = null;

  public ThreeDimMovementCharsHandler()
  {
    this(type);
  }

  public ThreeDimMovementCharsHandler(final String theType)
  {
    super(theType);

    addHandler(new WorldDistanceHandler(MIN_Height)
    {
      public void setWorldDistance(WorldDistance res)
      {
        _minHeight = res;
      }
    });

    addHandler(new WorldDistanceHandler(MAX_Height)
    {
      public void setWorldDistance(WorldDistance res)
      {
        _maxHeight = res;
      }
    });

    addHandler(new WorldSpeedHandler(DEFAULT_CLIMB_RATE)
    {
      public void setSpeed(WorldSpeed res)
      {
        _climbRate = res;
      }
    });

    addHandler(new WorldSpeedHandler(DEFAULT_DIVE_RATE)
    {
      public void setSpeed(WorldSpeed res)
      {
        _diveRate = res;
      }
    });

  }


}
