
package ASSET.Models.Detection.Lookup;

/*******************************************************************************
 * Debrief - the Open Source Maritime Analysis Application
 * http://debrief.info
 *  
 * (C) 2000-2020, Deep Blue C Technology Ltd
 *  
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html)
 *  
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 *******************************************************************************/


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
