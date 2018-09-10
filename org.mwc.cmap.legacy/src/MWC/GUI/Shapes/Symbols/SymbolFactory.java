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


/**
 * note Currently using Factory. Potentially switch to Abstract Factory, only reading in required
 * shapes from file as they are used patterns Factory (just receive name of shape, let classes
 * produce actual shape)
 */
public final class SymbolFactory
{

  public static final String KINGPIN = "Kingpin";

  public static final String BARRA = "Barra";

  public static final String LOFAR = "Lofar";

  public static final String DIFAR = "Difar";

  public static final String ACTIVE = "Active";

  public static final String HIDAR = "Hidar";

  public static final String MPA = "MPA";

  public static final String DESTROYER = "Destroyer";

  public static final String UNKNOWN = "Unknown";

  public static final String TROOP_CARRIER = "Troop_Carrier";

  public static final String CRUISER = "Cruiser";

  public static final String CARRIER = "Carrier";

  public static final String TA_FRIGATE = "TA Frigate";

  public static final String MINESWEEPER = "Minesweeper";

  public static final String FRIGATE = "Frigate";

  public static final String AIRCRAFT = "Aircraft";

  public static final String HELICOPTER = "Helicopter";

  public static final String FILLED_CIRCLE = "FilledCircle";

  public static final String FILLED_SQUARE = "FilledSquare";

  public static final String FISHING_VESSEL = "Fishing_Vessel";

  public static final String SCALED_FRIGATE = "ScaledFrigate";

  public static final String TORPEDO = "Torpedo";

  public static final String SCALED_SUBMARINE = "ScaledSubmarine";

  public static final String SQUARE = "Square";

  public static final String CIRCLE = "Circle";

  public static final String CROSS = "Cross";

  public static final String REFERENCE_POSITION = "Reference Position";

  public static final String WRECK = "Wreck";

  public static final String DATUM = "Datum";

  public static final String MERCHANT = "Merchant";

  public static final String SUBMARINE = "Submarine";

  public static final String DEFAULT_SYMBOL_TYPE = "S";

  /**
   * Where we are going to store the icons
   */
  public static final String SVG_FOLDER = "svg_symbols";

  /**
   * Value to be used in the custom format SVG_FORMAT_PREFIX:<File Name>
   */
  public static final String SVG_FORMAT_PREFIX = "svg";

  public static final String SVG_EXTENSION = ".svg";

  public static final String COASTGUARD_LAW_ENFORCEMENT_SVG = SVG_FORMAT_PREFIX + ":coastguard_law_enforcement";

  public static final String BOTTOMED_MINE_SVG = SVG_FORMAT_PREFIX + ":bottomed_mine";
  
  public static final String FLOATING_MINE_SVG = SVG_FORMAT_PREFIX + ":floating_mine";
  
  public static final String SPLASH_POINT_SVG = SVG_FORMAT_PREFIX + ":splash_point";
  
  public static final String VECTOR_SVG = SVG_FORMAT_PREFIX + ":vector_2";
  
  public static final String ANOMALY_SVG = SVG_FORMAT_PREFIX + ":anomaly";
  
  public static final String BUOY_1_SVG = SVG_FORMAT_PREFIX + ":buoy_1";
  
  public static final String BUOY_2_SVG = SVG_FORMAT_PREFIX + ":buoy_2";
  
  public static final String CLEARED_SVG = SVG_FORMAT_PREFIX + ":cleared";
  
  public static final String COUNTERMEASURE_SVG = SVG_FORMAT_PREFIX + ":countermeasure";
  
  public static final String DECOY_SVG = SVG_FORMAT_PREFIX + ":decoy";
  
  public static final String DECOY_AW_SVG = SVG_FORMAT_PREFIX + ":decoy_aw";
  
  public static final String DECOY_UW_SVG = SVG_FORMAT_PREFIX + ":decoy_uw";
  
  public static final String DROP_POINT_SVG = SVG_FORMAT_PREFIX + ":drop_point";
  
  public static final String ENEMY_AIR_SVG = SVG_FORMAT_PREFIX + ":enemy_air";
  
  public static final String ENEMY_SUBSURFACE_SVG = SVG_FORMAT_PREFIX + ":enemy_subsurface";
  
  public static final String ENEMY_SURFACE_SVG = SVG_FORMAT_PREFIX + ":enemy_surface";

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
   * list with the svg names with the following format. svg:Cruiser
   */
  private static String[] _theSVGList;
  /**
   * java.util.HashMap _theSymbols
   */
  private java.util.HashMap<String, PlainSymbol> _theSymbols;

  /**
   * the list of one character identifiers used for import/export to/from Replay File Format
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
   * create a symbol using the given identifier, else return null
   */
  static public PlainSymbol createSymbol(final String symbolType)
  {

    if (symbolType == null)
      return null;

    // check we have our factory
    checkFactory();

    // get the class for this symbonPointsl
    PlainSymbol symClass = _theFactory._theSymbols.get(symbolType);

    // did we find it?
    if (symClass == null)
    {
      // ok, try it the long way
      final java.util.Iterator<String> it = _theFactory._theSymbols.keySet()
          .iterator();
      while (it.hasNext())
      {
        final String thisKey = (String) it.next();
        if (thisKey != null)
        {
          if (thisKey.toUpperCase().equals(symbolType.toUpperCase()))
          {
            symClass = _theFactory._theSymbols.get(thisKey);
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
        res = symClass.create();
      }
      catch (final Exception ill)
      {
        //
        MWC.Utilities.Errors.Trace.trace(ill,
            "Failed to create new symbol, unable to create new instance");
      }
    }
    else
    {
      MWC.Utilities.Errors.Trace.trace("Failed to create symbol, string type:"
          + symbolType + " not found");
    }

    return res;
  }

  /**
   * create a symbol using the given identifier, else return null
   */
  static public String createSymbolFromId(final String charAsString)
  {
    String res = null;

    // check we have our factory
    checkFactory();

    // ok, try it the long way
    final java.util.Iterator<String> it = _theVesselIds.values().iterator();
    final java.util.Iterator<String> keyIterator = _theVesselIds.keySet().iterator();
    while (it.hasNext())
    {
      final String thisKey = (String) it.next();
      final String thisSymbolName = (String) keyIterator.next();

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

  public static String findIdForSymbolType(final String type)
  {
    String res = null;

    checkFactory();

    // first try using the default case
    res = (String) _theVesselIds.get(type);

    // do we need to try a case-insensitive comparison
    if (res == null)
    {
      final String typeUpper = type.toUpperCase();
      final java.util.Iterator<String> iter = _theVesselIds.keySet().iterator();
      while (iter.hasNext())
      {
        final String thisKey = (String) iter.next();
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
   * return a list of SVG icons available
   */
  static public String[] getSVGList()
  {
    checkFactory();

    return _theSVGList;
  }

  /**
   * checkFactory
   */
  static private void checkFactory()
  {
    if (_theFactory == null)
    {
      _theFactory = new SymbolFactory();
      _theFactory._theSymbols = new java.util.HashMap<String, PlainSymbol>();

      /////////////////////////////////////////
      // first the vessels
      /////////////////////////////////////////
      final java.util.HashMap<String, PlainSymbol> vessels = new java.util.HashMap<String, PlainSymbol>();
      vessels.put(HELICOPTER, new MWC.GUI.Shapes.Symbols.Vessels.HelicopterSym());
      vessels.put(AIRCRAFT, new MWC.GUI.Shapes.Symbols.Vessels.AircraftSym());
      vessels.put("ScaledAmphib", new MWC.GUI.Shapes.Symbols.Vessels.ScaledAmphibSym());
      vessels.put("ScaledContainer", new MWC.GUI.Shapes.Symbols.Vessels.ScaledContainerSym());
      vessels.put(SCALED_FRIGATE, new MWC.GUI.Shapes.Symbols.Vessels.ScaledFrigateSym());
      vessels.put("ScaledLPG", new MWC.GUI.Shapes.Symbols.Vessels.ScaledLPGSym());
      vessels.put("ScaledMerchant", new MWC.GUI.Shapes.Symbols.Vessels.ScaledMerchantSym());
      vessels.put(SCALED_SUBMARINE, new MWC.GUI.Shapes.Symbols.Vessels.ScaledSubmarineSym());
      vessels.put("ScaledVessel", new MWC.GUI.Shapes.Symbols.Vessels.ScaledVesselSym());
      vessels.put(SUBMARINE, new MWC.GUI.Shapes.Symbols.Vessels.SubmergedSub());
      vessels.put("Missile", new MWC.GUI.Shapes.Symbols.Vessels.UnknownSym("Missile"));
      vessels.put(TORPEDO, new MWC.GUI.Shapes.Symbols.Vessels.UnknownSym(TORPEDO));
      vessels.put(CARRIER, new MWC.GUI.Shapes.Symbols.Vessels.AircraftCarrierSym());
      vessels.put(CRUISER, new MWC.GUI.Shapes.Symbols.Vessels.CruiserSym());
      vessels.put(DESTROYER, new MWC.GUI.Shapes.Symbols.Vessels.DestroyerSym());
      vessels.put(FRIGATE, new MWC.GUI.Shapes.Symbols.Vessels.FrigateSym());
      vessels.put(TA_FRIGATE, new MWC.GUI.Shapes.Symbols.Vessels.TAFrigateSym());
      vessels.put(FISHING_VESSEL, new MWC.GUI.Shapes.Symbols.Vessels.FishingVesselSym());
      vessels.put(MERCHANT, new MWC.GUI.Shapes.Symbols.Vessels.MerchantSym());
      vessels.put(UNKNOWN, new MWC.GUI.Shapes.Symbols.Vessels.UnknownSym());
      vessels.put(MINESWEEPER, new MWC.GUI.Shapes.Symbols.Vessels.MinesweeperSym());
      vessels.put(TROOP_CARRIER, new MWC.GUI.Shapes.Symbols.Vessels.TroopCarrierSym());

      vessels.put("Oiler", new MWC.GUI.Shapes.Symbols.Vessels.FishingVesselSym());


      // add some other (A SSET related) items
      vessels.put(MPA, new MWC.GUI.Shapes.Symbols.Vessels.AircraftSym(MPA));

      _theVesselIds = new java.util.HashMap<String, String>();
      _theVesselIds.put(UNKNOWN, "@");
      _theVesselIds.put(AIRCRAFT, "A");
      _theVesselIds.put(CARRIER, "C");
      _theVesselIds.put(DESTROYER, "D");
      _theVesselIds.put(FILLED_SQUARE, "E");
      _theVesselIds.put(FRIGATE, "F");
      _theVesselIds.put(FILLED_CIRCLE, "G");
      _theVesselIds.put(HELICOPTER, "H");
      _theVesselIds.put(CROSS, "I");
      _theVesselIds.put(SQUARE, "J");
      _theVesselIds.put(DATUM, "K");
      _theVesselIds.put(REFERENCE_POSITION, "L");
      _theVesselIds.put(MERCHANT, "M");
      _theVesselIds.put(MINESWEEPER, "N");
      _theVesselIds.put(TORPEDO, "P");
      _theVesselIds.put(CIRCLE, "Q");
      _theVesselIds.put(TROOP_CARRIER, "R");
      _theVesselIds.put(SUBMARINE, "S");
      _theVesselIds.put(TA_FRIGATE, "T");
      _theVesselIds.put(CRUISER, "U");
      _theVesselIds.put(FISHING_VESSEL, "V");
      _theVesselIds.put(WRECK, "W");

      // and the buoys
      _theVesselIds.put(ACTIVE, "0");
      _theVesselIds.put(DIFAR, "1");
      _theVesselIds.put(LOFAR, "2");
      _theVesselIds.put(BARRA, "3");
      _theVesselIds.put(HIDAR, "4");
      _theVesselIds.put(KINGPIN, "5");

      /////////////////////////////////////////
      // now the buoys
      /////////////////////////////////////////
      final java.util.HashMap<String, PlainSymbol> buoys = new java.util.HashMap<String, PlainSymbol>();
      buoys.put(ACTIVE, new MWC.GUI.Shapes.Symbols.Buoys.ActiveSym());
      buoys.put(DIFAR, new MWC.GUI.Shapes.Symbols.Buoys.DifarSym());
      buoys.put(LOFAR, new MWC.GUI.Shapes.Symbols.Buoys.LofarSym(LOFAR));
      buoys.put(BARRA, new MWC.GUI.Shapes.Symbols.Buoys.BarraSym());
      buoys.put(KINGPIN, new MWC.GUI.Shapes.Symbols.Buoys.KingpinSym());
      buoys.put(HIDAR, new MWC.GUI.Shapes.Symbols.Buoys.HidarSym());
      buoys.put(SQUARE, new MWC.GUI.Shapes.Symbols.Geog.SquareSymbol());

      // just put in the difar sym as a general sonar buoy symbol
      buoys.put("Sonar_Buoy", new MWC.GUI.Shapes.Symbols.Buoys.LofarSym("Sonar_Buoy"));

      /////////////////////////////////////////
      // Now we add the svg items.
      /////////////////////////////////////////
      final java.util.HashMap<String, PlainSymbol> svgIcons = new java.util.HashMap<>();
      svgIcons.put(COASTGUARD_LAW_ENFORCEMENT_SVG, new MWC.GUI.Shapes.Symbols.SVG.SVGShape(COASTGUARD_LAW_ENFORCEMENT_SVG));
      svgIcons.put(FLOATING_MINE_SVG, new MWC.GUI.Shapes.Symbols.SVG.SVGShape(FLOATING_MINE_SVG));
      svgIcons.put(SPLASH_POINT_SVG, new MWC.GUI.Shapes.Symbols.SVG.SVGShape(SPLASH_POINT_SVG));
      svgIcons.put(VECTOR_SVG, new MWC.GUI.Shapes.Symbols.SVG.SVGShape(VECTOR_SVG));
      svgIcons.put(ANOMALY_SVG, new MWC.GUI.Shapes.Symbols.SVG.SVGShape(ANOMALY_SVG));
      svgIcons.put(BUOY_1_SVG, new MWC.GUI.Shapes.Symbols.SVG.SVGShape(BUOY_1_SVG));

      /////////////////////////////////////////
      // put the other assorted items directly into the main list
      /////////////////////////////////////////
      _theFactory._theSymbols.put(FILLED_SQUARE, new MWC.GUI.Shapes.Symbols.Geog.FilledSquareSymbol());
      _theFactory._theSymbols.put(FILLED_CIRCLE, new MWC.GUI.Shapes.Symbols.Geog.FilledCircleSymbol());
      _theFactory._theSymbols.put(SQUARE, new MWC.GUI.Shapes.Symbols.Geog.SquareSymbol());
      _theFactory._theSymbols.put(CIRCLE, new MWC.GUI.Shapes.Symbols.Geog.CircleSymbol());
      _theFactory._theSymbols.put(CROSS, new MWC.GUI.Shapes.Symbols.Geog.CrossSymbol());
      _theFactory._theSymbols.put(REFERENCE_POSITION, new MWC.GUI.Shapes.Symbols.Geog.ReferenceSym());
      _theFactory._theSymbols.put(WRECK, new MWC.GUI.Shapes.Symbols.Geog.WreckSym());
      _theFactory._theSymbols.put(DATUM, new MWC.GUI.Shapes.Symbols.Geog.DatumSym());

      // put the sub-lists into the main list
      _theFactory._theSymbols.putAll(vessels);
      _theFactory._theSymbols.putAll(buoys);
      _theFactory._theSymbols.putAll(svgIcons);

      // collate list of symbol names from our list
      final String[] sampler = new String[]
      {"dummy1"};

      // convert the big vector to the list
      java.util.SortedSet<String> sortedKeys = new java.util.TreeSet<String>(
          _theFactory._theSymbols.keySet());
      _theList = (String[]) sortedKeys.toArray(sampler);

      // now convert the vessel vector to the list
      sortedKeys = new java.util.TreeSet<String>(vessels.keySet());
      _theVesselList = (String[]) sortedKeys.toArray(sampler);

      // now convert the buoy vector to the list
      sortedKeys = new java.util.TreeSet<String>(buoys.keySet());
      _theBuoyList = (String[]) sortedKeys.toArray(sampler);

      // now convert the svg icons to the list
      sortedKeys = new java.util.TreeSet<String>(svgIcons.keySet());
      _theSVGList = (String[]) sortedKeys.toArray(sampler);
    }
  }

  //////////////////////////////////////////////////
  // member functions
  //////////////////////////////////////////////////

  /**
   * ************************************************* testing
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
      final String cA = "A";
      final String ps = SymbolFactory.createSymbolFromId(cA);
      assertEquals("aircraft symbol returned", ps, AIRCRAFT);

      String newChar = SymbolFactory.findIdForSymbolType(AIRCRAFT);
      assertEquals("correct sym found", newChar, cA);

      newChar = SymbolFactory.findIdForSymbolType("AIRCRAFT");
      assertEquals("correct sym found", newChar, cA);

    }

  }

}
