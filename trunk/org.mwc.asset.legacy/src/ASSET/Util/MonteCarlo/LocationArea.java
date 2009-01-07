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
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldLocation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * variance class which dictates that random locations be produced within a specified rectangle
 */
public final class LocationArea implements XMLObject
{
  /**
   * the area within which the new locations are created
   */
  private WorldArea _myArea;

  /**
   * the pattern in which our random instances are created
   */
  private int _myRandomModel;

  /**
   * the current location we're creating
   */
  private WorldLocation _currentLoc;


  /**
   * constructor - read ourselves in from the element
   *
   * @param element
   */
  public LocationArea(final Element element)
  {
    // read ourselves in from this node
    WorldArea myArea = XMLVariance.readInAreaFromXML(element);

    // and get the number pattern
    int myRandomModel = XMLVariance.readRandomNumberModel(element);

    // and store the data
    initialise(myArea, myRandomModel);
  }


  /**
   * private constructor - used for tests
   *
   * @param myArea
   * @param myModel
   */
  LocationArea(WorldArea myArea, int myModel)
  {
    initialise(myArea, myModel);
  }

  /**
   * private initialiser
   * - we use this so that we can create a location area without having to pass in the elements
   *
   * @param myArea
   * @param myRandomModel
   */
  private void initialise(WorldArea myArea, int myRandomModel)
  {
    _myArea = myArea;
    _myRandomModel = myRandomModel;
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
      else if (thisElement.getNodeName().equals("relativeLocation"))
      {
        currentInstance.removeChild(thisElement);
        break;
      }
      else if (thisElement.getNodeName().equals("longLocation"))
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
    _currentLoc = XMLVariance.generateRandomLocationInArea(_myArea, _myRandomModel);
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
    return "un-named Location Area variance";
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
  public static class LocAreaTest extends SupportTesting
  {
    public LocAreaTest(String s)
    {
      super(s);
    }

    public void testArea()
    {

      WorldLocation wa = new WorldLocation(8, 0, 0);
      WorldLocation wb = new WorldLocation(10, 2, 0);
      WorldArea myArea = new WorldArea(wa, wb);
      int myModel = RandomGenerator.NORMAL;
      LocationArea la = new LocationArea(myArea, myModel);

      // and get the locations
      int limit = 100;
      for (int i = 0; i < limit; i++)
      {
        la.newPermutation();
        WorldLocation thisLoc = la.thisPermutation();
        assertTrue("generated location isn't inside indicated area", myArea.contains(thisLoc));
      }
    }

  }

}
