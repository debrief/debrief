package ASSET.Models.Detection.Lookup;

/**
 * Title:        ASSET Simulator
 * Description:  Advanced Scenario Simulator for Evaluation of Tactics
 * Copyright:    Copyright (c) 2004
 * Company:      PlanetMayo Ltd
 * @author Ian Mayo
 * @version 1.0
 */


/** class to return a double value based on a supplied combination of vessel category and bearing
 *
 */
public class BearingCategoryLookup

{
  /** the set of sigmas for this lookup table
   *
    */
//  private double[][] _sigmaTable;

  /** constructor - setting the lookup table as we go..
   *
   * @param sigmaTable the table of sigmas to use
   */
  public BearingCategoryLookup(double[][] sigmaTable)
  {
 //   this._sigmaTable = sigmaTable;
  }

  /** our table of values
   *
   * @param vesselType
   * @param bearing
   * @return
   */
  public double getValue(String vesselType, double bearing)
  {
    double res = 0d;
    return res;
  }



}
