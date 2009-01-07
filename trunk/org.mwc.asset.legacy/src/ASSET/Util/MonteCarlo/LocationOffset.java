/*
 * Created by IntelliJ IDEA.
 * User: administrator
 * Date: Oct 31, 2001
 * Time: 1:23:24 PM
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package ASSET.Util.MonteCarlo;

import ASSET.Util.RandomGenerator;
import ASSET.Util.SupportTesting;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldVector;
import MWC.Utilities.ReaderWriter.XML.Util.WorldDistanceHandler;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * variance class which dictates that random locations be produced within a specified range of an origin
 */
public final class LocationOffset implements XMLObject
{
  /**
   * the origin for the new locations
   */
  private WorldLocation _myOrigin;

  /**
   * the range to stretch the positions out to
   */
  private WorldDistance _myRange;

  /**
   * the pattern in which our random instances are created
   */
  protected int _myRandomModel;

  /**
   * the current location we're creating
   */
  private WorldLocation _currentLoc;
  private static final String OFFSET_NAME = "OffsetDistance";
  private final String LOCATION_NAME = "Origin";


  /**
   * constructor - read ourselves in from the element
   *
   * @param element
   */
  public LocationOffset(final Element element)
  {
    // read ourselves in from this node
    WorldLocation myArea = XMLVariance.readInLocationFromXML(element, LOCATION_NAME);

    // and the distance offset
    WorldDistance myDistance = WorldDistanceHandler.extractWorldDistance(element, OFFSET_NAME);

    // and get the number pattern
    int myRandomModel = XMLVariance.readRandomNumberModel(element);

    // and store the data
    initialise(myArea, myDistance, myRandomModel);
  }


  /**
   * private constructor - used for tests
   *
   * @param myArea
   * @param myModel
   */
  LocationOffset(WorldLocation myArea, WorldDistance range, int myModel)
  {
    initialise(myArea, range, myModel);
  }

  /**
   * private initialiser
   * - we use this so that we can create a location area without having to pass in the elements
   *
   * @param myArea
   * @param myRandomModel
   */
  private void initialise(WorldLocation myArea, WorldDistance myRange, int myRandomModel)
  {
    _myOrigin = myArea;
    _myRandomModel = myRandomModel;
    _myRange = myRange;
  }

  /**
   * perform our update to the supplied element
   */
  public final String execute(final Element currentInstance,
                              final Document parentDocument)
  {
    // ok, here we go..
    // we're going to replace the object with one of our permutations
    String res = null;

    // create the new permutation we need
    newPermutation();

    // check that this element is a world area
    // generate a new location
    Element newLocation = parentDocument.createElement("shortLocation");
    newLocation.setAttribute("Lat", "" + _currentLoc.getLat());
    newLocation.setAttribute("Long", "" + _currentLoc.getLong());

    // first remove the existing location
    int num = currentInstance.getChildNodes().getLength();
    for (int i = 0; i < num; i++)
    {
      Node thisElement = currentInstance.getChildNodes().item(i);
      if (thisElement.getNodeName().equals("shortLocation"))
      {
        currentInstance.removeChild(thisElement);
        break;
      }
      else if (thisElement.getNodeName().equals("shortLocation"))
      {
        currentInstance.removeChild(thisElement);
        break;
      }
    }

    // ok, insert our new item
    currentInstance.insertBefore(newLocation, null);

    // convert the new value to a string, to be used in the hashing value
    res = ScenarioGenerator.writeToString(newLocation);

    // done
    return res;

  }


  /**
   * retrieve the current permutation
   *
   * @return
   */
  WorldLocation thisPermutation()
  {
    return _currentLoc;
  }

  /**
   * generate a new permutation
   */
  void newPermutation()
  {
    // ok, generate new location
    double bearing = RandomGenerator.nextRandom() * 360;
    double range = RandomGenerator.nextRandom() * _myRange.getValueIn(WorldDistance.DEGS);
    WorldVector offset = new WorldVector(MWC.Algorithms.Conversions.Degs2Rads(bearing),
                                         range, 0);
    _currentLoc = _myOrigin.add(offset);
  }


  /**
   * randomise ourselves
   */
  public final void randomise()
  {
    newPermutation();
  }

  /**
   * return the name of this variable
   */
  public final String getName()
  {
    return "un-named Location offset variance";
  }

  public final String getCurValueIn(final Element object)
  {
    return "Empty";
  }

  /**
   * return the last value used for this attribute
   */
  public final String getCurValue()
  {
    return _currentLoc.toString();
  }

  /**
   * merge ourselves with the supplied object
   */
  public final void merge(final XMLObject other)
  {
  }


  //////////////////////////////////////////////////
  // property testing
  //////////////////////////////////////////////////
  public static class LocOffsetTest extends SupportTesting
  {
    public LocOffsetTest(String s)
    {
      super(s);
    }

    public void testArea()
    {

      WorldDistance myDist = new WorldDistance(1, WorldDistance.DEGS);
      WorldLocation myOrigin = new WorldLocation(8, 0, 0);
      int myModel = RandomGenerator.NORMAL;
      LocationOffset la = new LocationOffset(myOrigin, myDist, myModel);

      // and get the locations
      int limit = 100;
      for (int i = 0; i < limit; i++)
      {
        la.newPermutation();
        WorldLocation thisLoc = la.thisPermutation();
        WorldVector error = thisLoc.subtract(myOrigin);
        double rngDegs = error.getRange();
        assertTrue("generated location isn't inside indicated area", rngDegs < 1);
      }
    }

  }

}
