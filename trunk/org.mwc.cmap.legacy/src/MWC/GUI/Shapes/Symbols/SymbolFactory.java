// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: SymbolFactory.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.7 $
// $Log: SymbolFactory.java,v $
// Revision 1.7  2004/11/09 10:16:45  Ian.Mayo
// Provide MPA symbol support
//
// Revision 1.6  2004/08/31 09:38:13  Ian.Mayo
// Rename inner static tests to match signature **Test to make automated testing more consistent
//
// Revision 1.5  2004/08/25 11:22:15  Ian.Mayo
// Remove main methods which just run junit tests
//
// Revision 1.4  2004/08/20 13:34:13  Ian.Mayo
// Implement inspection recommendations to overcome hidden parent objects, let CoreDecision handle the activity bits.
//
// Revision 1.3  2004/05/25 15:37:28  Ian.Mayo
// Commit updates from home
//
// Revision 1.1.1.1  2004/03/04 20:31:23  ian
// no message
//
// Revision 1.2  2003/11/21 09:09:31  Ian.Mayo
// Include missing symbol types
//
// Revision 1.1.1.1  2003/07/17 10:07:34  Ian.Mayo
// Initial import
//
// Revision 1.10  2003-02-14 11:15:26+00  ian_mayo
// Updated (tidied) list of symbol type char identifiers
//
// Revision 1.9  2003-02-14 10:18:10+00  ian_mayo
// Provide support for single-letter symbol definitions (as used in Replay file)
//
// Revision 1.8  2003-02-08 16:50:50+00  ian_mayo
// Added catch-all for general sonar buoy
//
// Revision 1.7  2002-10-30 15:36:17+00  ian_mayo
// minor tidying
//
// Revision 1.6  2002-10-11 08:35:03+01  ian_mayo
// minor tidying
//
// Revision 1.5  2002-09-24 11:00:09+01  ian_mayo
// Add Torpedo model
//
// Revision 1.4  2002-05-28 09:25:53+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:14:21+01  ian_mayo
// Initial revision
//
// Revision 1.3  2002-04-26 16:02:40+01  ian_mayo
// Add the missile symbol
//
// Revision 1.2  2002-04-22 13:29:06+01  ian_mayo
// Put square constructor back in
//
// Revision 1.1  2002-04-11 14:01:05+01  ian_mayo
// Initial revision
//
// Revision 1.1  2001-09-25 16:33:41+01  administrator
// Give symbol handler a second chance
//
// Revision 1.0  2001-07-17 08:43:14+01  administrator
// Initial revision
//
// Revision 1.6  2001-01-21 21:37:44+00  novatech
// make Square a type of buoy symbol
//
// Revision 1.5  2001-01-18 13:21:09+00  novatech
// add minesweeper, move kingpin grouping
//
// Revision 1.4  2001-01-18 09:27:53+00  novatech
// put datum into correct group
//
// Revision 1.3  2001-01-17 13:26:39+00  novatech
// All property editors which return a subset of the full range of symbols
//
// Revision 1.2  2001-01-16 19:27:00+00  novatech
// tidied up, now up and running
//
// Revision 1.1  2001-01-03 13:42:15+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 21:49:16  ianmayo
// initial version
//
// Revision 1.2  2000-11-17 09:06:34+00  ian_mayo
// white space only
//
// Revision 1.1  1999-10-12 15:36:38+01  ian_mayo
// Initial revision
//
// Revision 1.1  1999-07-27 10:50:38+01  administrator
// Initial revision
//
// Revision 1.1  1999-07-07 11:10:05+01  administrator
// Initial revision
//
// Revision 1.1  1999-06-16 15:37:58+01  sm11td
// Initial revision
//
// Revision 1.1  1999-01-31 13:33:03+00  sm11td
// Initial revision
//

package MWC.GUI.Shapes.Symbols;

import java.util.Iterator;

/**
 * note Currently using Factory.  Potentially switch to Abstract
 * Factory, only reading in required shapes from file as
 * they are used
 * patterns Factory (just receive name of shape, let classes
 * produce actual shape)
 */
public class SymbolFactory
{

  //////////////////////////////////////////////////
  // member variables
  //////////////////////////////////////////////////

  public static final String SUBMARINE = "Submarine";

	public static final String DEFAULT_SYMBOL_TYPE = "S";

  /**
   */
  private static SymbolFactory _theFactory;
  /**
   * String[] _theList
   */
  private static String[] _theList;
  /**
   * the list of names of buoys
   */
  private static String[] _theBuoyList;
  /**
   * the list of names of vessels
   */
  private static String[] _theVesselList;
  /**
   * java.util.HashMap _theSymbols
   */
  private java.util.HashMap<String, Class<?>> _theSymbols;

  /**
   * the list of one character identifiers used for import/export to/from
   * Replay File Format
   */
  private static java.util.HashMap<String, String> _theVesselIds;

  //////////////////////////////////////////////////
  // constructor
  /**
   * //////////////////////////////////////////////////
   */
  private SymbolFactory()
  {
  }


  //////////////////////////////////////////////////
  // static functions
  //////////////////////////////////////////////////



  /**
   * create a symbol using the given identifier,
   * else return null
   */
  static public PlainSymbol createSymbol(String symbolType)
  {

    if (symbolType == null)
      return null;

    // check we have our factory
    checkFactory();

    // get the class for this symbol
    Class<?> symClass = (Class<?>) _theFactory._theSymbols.get(symbolType);

    // did we find it?
    if (symClass == null)
    {
      // ok, try it the long way
      java.util.Iterator<String> it = _theFactory._theSymbols.keySet().iterator();
      while (it.hasNext())
      {
        String thisKey = (String) it.next();
        if (thisKey != null)
        {
          if (thisKey.toUpperCase().equals(symbolType.toUpperCase()))
          {
            symClass = (Class<?>) _theFactory._theSymbols.get(thisKey);
            break;
          }
        }
      }
    }

    PlainSymbol res = null;
    if (symClass != null)
    {
      try
      {
        // create it
        Object newSym = symClass.newInstance();

        // convert to correct type
        res = (PlainSymbol) newSym;
      }
      catch (Exception ill)
      {
        //
        MWC.Utilities.Errors.Trace.trace(ill, "Failed to create new symbol, unable to create new instance");
      }
    }
    else
    {
      MWC.Utilities.Errors.Trace.trace("Failed to create symbol, string type:" + symbolType + " not found");
    }

    return res;
  }

  /**
   * create a symbol using the given identifier,
   * else return null
   */
  static public String createSymbolFromId(String charAsString)
  {
    String res = null;

    // check we have our factory
    checkFactory();

    // ok, try it the long way
    java.util.Iterator<String> it = _theVesselIds.values().iterator();
    Iterator<String> keyIterator = _theVesselIds.keySet().iterator();
    while (it.hasNext())
    {
      String thisKey = (String) it.next();
      String thisSymbolName = (String) keyIterator.next();

      if (thisKey != null)
      {
        if (thisKey.toUpperCase().equals(charAsString))
        {
          res = thisSymbolName;
          break;
        }
      }
    }

    return res;
  }

  public static String findIdForSymbolType(String type)
  {
    String res = null;

    checkFactory();

    // first try using the default case
    res = (String) _theVesselIds.get(type);

    // do we need to try a case-insensitive comparison
    if (res == null)
    {
      String typeUpper = type.toUpperCase();
      Iterator<String> iter = _theVesselIds.keySet().iterator();
      while (iter.hasNext())
      {
        String thisKey = (String) iter.next();
        if (thisKey.toUpperCase().equals(typeUpper))
        {
          res = findIdForSymbolType(thisKey);
          break;
        }
      }
    }

    return res;
  }


  /**
   * return a list of symbols available
   */
  static public String[] getSymbolList()
  {
    checkFactory();

    return _theList;
  }

  /**
   * return a list of buoy symbols available
   */
  static public String[] getBuoySymbolList()
  {
    checkFactory();

    return _theBuoyList;
  }

  /**
   * return a list of buoy symbols available
   */
  static public String[] getVesselSymbolList()
  {
    checkFactory();

    return _theVesselList;
  }

  /**
   * checkFactory
   */
  static private void checkFactory()
  {
    if (_theFactory == null)
    {
      _theFactory = new SymbolFactory();
      _theFactory._theSymbols = new java.util.HashMap<String, Class<?>>();

      /////////////////////////////////////////
      // first the vessels
      /////////////////////////////////////////
      java.util.HashMap<String, Class<?>> vessels = new java.util.HashMap<String, Class<?>>();
      vessels.put("Helicopter", MWC.GUI.Shapes.Symbols.Vessels.HelicopterSym.class);
      vessels.put("Aircraft", MWC.GUI.Shapes.Symbols.Vessels.AircraftSym.class);
      vessels.put("ScaledVessel", MWC.GUI.Shapes.Symbols.Vessels.ScaledVesselSym.class);
      vessels.put("ScaledSubmarine", MWC.GUI.Shapes.Symbols.Vessels.ScaledSubmarineSym.class);
      vessels.put(SUBMARINE, MWC.GUI.Shapes.Symbols.Vessels.SubmergedSub.class);
      vessels.put("Missile", MWC.GUI.Shapes.Symbols.Vessels.MissileSym.class);
      vessels.put("Torpedo", MWC.GUI.Shapes.Symbols.Vessels.TorpedoSym.class);
      vessels.put("Carrier", MWC.GUI.Shapes.Symbols.Vessels.AircraftCarrierSym.class);
      vessels.put("Cruiser", MWC.GUI.Shapes.Symbols.Vessels.CruiserSym.class);
      vessels.put("Destroyer", MWC.GUI.Shapes.Symbols.Vessels.DestroyerSym.class);
      vessels.put("Frigate", MWC.GUI.Shapes.Symbols.Vessels.FrigateSym.class);
      vessels.put("TA Frigate", MWC.GUI.Shapes.Symbols.Vessels.TAFrigateSym.class);
      vessels.put("Fishing_Vessel", MWC.GUI.Shapes.Symbols.Vessels.FishingVesselSym.class);
      vessels.put("Merchant", MWC.GUI.Shapes.Symbols.Vessels.MerchantSym.class);
      vessels.put("Unknown", MWC.GUI.Shapes.Symbols.Vessels.UnknownSym.class);
      vessels.put("Minesweeper", MWC.GUI.Shapes.Symbols.Vessels.MinesweeperSym.class);
      vessels.put("Merchant", MWC.GUI.Shapes.Symbols.Vessels.MerchantSym.class);
      vessels.put("Troop_Carrier", MWC.GUI.Shapes.Symbols.Vessels.TroopCarrierSym.class);

      vessels.put("Oiler", MWC.GUI.Shapes.Symbols.Vessels.FishingVesselSym.class);

      
      // add some other (A SSET related) items
      vessels.put("MPA", MWC.GUI.Shapes.Symbols.Vessels.AircraftSym.class);


      _theVesselIds = new java.util.HashMap<String, String>();
      _theVesselIds.put("Helicopter", "H");
      _theVesselIds.put("Aircraft", "A");
      _theVesselIds.put(SUBMARINE, "S");
      _theVesselIds.put("Torpedo", "P");
      _theVesselIds.put("Carrier", "C");
      _theVesselIds.put("Cruiser", "U");
      _theVesselIds.put("Destroyer", "D");
      _theVesselIds.put("Frigate", "F");
      _theVesselIds.put("TA Frigate", "T");
      _theVesselIds.put("Fishing Vessel", "V");
      _theVesselIds.put("Merchant", "M");
      _theVesselIds.put("Minesweeper", "N");
      _theVesselIds.put("Troop_Carrier", "R");
      _theVesselIds.put("Unknown", "@");

      // and another (A SSET related entry)
      _theVesselIds.put("MPA", "A");



      /////////////////////////////////////////
      // now the buoys
      /////////////////////////////////////////
      java.util.HashMap<String, Class<?>> buoys = new java.util.HashMap<String, Class<?>>();
      buoys.put("Active", MWC.GUI.Shapes.Symbols.Buoys.ActiveSym.class);
      buoys.put("Difar", MWC.GUI.Shapes.Symbols.Buoys.DifarSym.class);
      buoys.put("Lofar", MWC.GUI.Shapes.Symbols.Buoys.LofarSym.class);
      buoys.put("Barra", MWC.GUI.Shapes.Symbols.Buoys.BarraSym.class);
      buoys.put("Kingpin", MWC.GUI.Shapes.Symbols.Buoys.KingpinSym.class);
      buoys.put("Square", MWC.GUI.Shapes.Symbols.Geog.SquareSymbol.class);

      // just put in the difar sym as a general sonar buoy symbol
      buoys.put("Sonar_Buoy", MWC.GUI.Shapes.Symbols.Buoys.LofarSym.class);

      /////////////////////////////////////////
      // put the other assorted items directly into the main list
      /////////////////////////////////////////
      _theFactory._theSymbols.put("FilledSquare", MWC.GUI.Shapes.Symbols.Geog.FilledSquareSymbol.class);
      _theFactory._theSymbols.put("FilledCircle", MWC.GUI.Shapes.Symbols.Geog.FilledCircleSymbol.class);
      _theFactory._theSymbols.put("Square", MWC.GUI.Shapes.Symbols.Geog.SquareSymbol.class);
      _theFactory._theSymbols.put("Circle", MWC.GUI.Shapes.Symbols.Geog.CircleSymbol.class);
      _theFactory._theSymbols.put("Cross", MWC.GUI.Shapes.Symbols.Geog.CrossSymbol.class);
      _theFactory._theSymbols.put("Reference Position", MWC.GUI.Shapes.Symbols.Geog.ReferenceSym.class);
      _theFactory._theSymbols.put("Datum", MWC.GUI.Shapes.Symbols.Geog.DatumSym.class);
      _theFactory._theSymbols.put("Missile", MWC.GUI.Shapes.Symbols.Vessels.MissileSym.class);

      // put the sub-lists into the main list
      _theFactory._theSymbols.putAll(vessels);
      _theFactory._theSymbols.putAll(buoys);


      // collate list of symbol names from our list
      String[] sampler = new String[]{"dummy1"};

      // convert the big vector to the list
      java.util.SortedSet<String> sortedKeys = new java.util.TreeSet<String>(_theFactory._theSymbols.keySet());
      _theList = (String[]) sortedKeys.toArray(sampler);

      // now convert the vessel  vector to the list
      sortedKeys = new java.util.TreeSet<String>(vessels.keySet());
      _theVesselList = (String[]) sortedKeys.toArray(sampler);

      // now convert the buoy vector to the list
      sortedKeys = new java.util.TreeSet<String>(buoys.keySet());
      _theBuoyList = (String[]) sortedKeys.toArray(sampler);

    }
  }

  //////////////////////////////////////////////////
  // member functions
  //////////////////////////////////////////////////

  /**
   * *************************************************
   * testing
   * *************************************************
   */
  public static class SymFactoryTest extends junit.framework.TestCase
  {

    static public final String TEST_ALL_TEST_TYPE = "UNIT";

    public SymFactoryTest(final String val)
    {
      super(val);
    }

    /**
     * test the string swapping
     */
    public void testFindFromChar()
    {
      String cA = "A";
      String ps = SymbolFactory.createSymbolFromId(cA);
      assertEquals("aircraft symbol returned", ps, "Aircraft");

      String newChar = SymbolFactory.findIdForSymbolType("Aircraft");
      assertEquals("correct sym found", newChar, cA);

      newChar = SymbolFactory.findIdForSymbolType("AIRCRAFT");
      assertEquals("correct sym found", newChar, cA);


    }

  }


}


