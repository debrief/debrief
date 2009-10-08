package Debrief.Tools.Tote;

// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: toteCalculation.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.2 $
// $Log: toteCalculation.java,v $
// Revision 1.2  2004/11/25 10:24:43  Ian.Mayo
// Switch to Hi Res dates
//
// Revision 1.1.1.2  2003/07/21 14:49:09  Ian.Mayo
// Re-import Java files to keep correct line spacing
//
// Revision 1.5  2003-02-10 16:27:56+00  ian_mayo
// Reflect name change of get wrappable data
//
// Revision 1.4  2003-02-07 15:36:07+00  ian_mayo
// Add accessor flag to indicate is this calculation needs special processing (where data crosses through zero)
//
// Revision 1.3  2003-02-05 15:56:14+00  ian_mayo
// minor tidying to comment
//
// Revision 1.2  2002-05-28 09:25:11+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:11:45+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-23 12:28:38+01  ian_mayo
// Initial revision
//
// Revision 1.0  2001-07-17 08:41:13+01  administrator
// Initial revision
//
// Revision 1.2  2001-01-17 09:48:00+00  novatech
// remove unnecessary import statements
//
// Revision 1.1  2001-01-03 13:40:26+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 20:49:04  ianmayo
// initial import of files
//
// Revision 1.1  2000-09-26 10:58:46+01  ian_mayo
// Initial revision
//
// Revision 1.2  2000-02-03 15:04:50+00  ian_mayo
// First issue to DEVRON (2 feb)
//
// Revision 1.1  1999-10-12 15:34:24+01  ian_mayo
// Initial revision
//
// Revision 1.1  1999-08-04 10:53:02+01  administrator
// Initial revision
//

import MWC.GenericData.HiResDate;
import MWC.GenericData.Watchable;

import java.text.*;

public interface toteCalculation
{
  /////////////////////////////////////////////////////////////
  // member functions
  ////////////////////////////////////////////////////////////

	/** calculate a fresh set of results for this data, return
	 * as a string
	 */
  public String update(Watchable primary, Watchable secondary, HiResDate thisTime);

  /** produce the calculated value as a double
   */
  public double calculate(Watchable primary, Watchable secondary, HiResDate thisTime);

	/** @param format format (java.text.NumberFormat) we use to display the results
	 */
  public void setPattern(NumberFormat format);

	/** @return the title to show above the row
	 */
  public String getTitle();

	/** @return the units to show for the tow
	 */
  public String getUnits();

  /** does this calculation require special bearing handling (prevent wrapping through 180 or 360 degs)
   *
   */
  public boolean isWrappableData();

}
