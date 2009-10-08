package Debrief.Tools.Tote.Calculations;

// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: relBearingCalc.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.4 $
// $Log: relBearingCalc.java,v $
// Revision 1.4  2006/03/16 16:01:08  Ian.Mayo
// Override not-applicable statement
//
// Revision 1.3  2005/09/08 11:01:01  Ian.Mayo
// Always check value of preference parameter. Users will expect changing the pref to reflect in app immediately
//
// Revision 1.2  2004/11/25 10:24:39  Ian.Mayo
// Switch to Hi Res dates
//
// Revision 1.1.1.2  2003/07/21 14:49:16  Ian.Mayo
// Re-import Java files to keep correct line spacing
//
// Revision 1.7  2003-05-13 16:04:20+01  ian_mayo
// Allow UK/US respective formatting of rel bearing
//
// Revision 1.6  2003-05-13 11:50:09+01  ian_mayo
// Provide support for plotting bearing in US as well as UK format
//
// Revision 1.5  2003-03-19 15:37:23+00  ian_mayo
// improvements according to IntelliJ inspector
//
// Revision 1.4  2003-02-10 16:27:48+00  ian_mayo
// Reflect name change of get wrappable data
//
// Revision 1.3  2003-02-07 15:36:10+00  ian_mayo
// Add accessor flag to indicate is this calculation needs special processing (where data crosses through zero)
//
// Revision 1.2  2002-05-28 09:25:12+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:11:42+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-23 12:28:35+01  ian_mayo
// Initial revision
//
// Revision 1.0  2001-07-17 08:41:12+01  administrator
// Initial revision
//
// Revision 1.1  2001-01-03 13:40:25+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 20:49:05  ianmayo
// initial import of files
//
// Revision 1.2  2000-09-18 09:13:41+01  ian_mayo
// clip results to +/- 180 degs
//
// Revision 1.1  2000-09-14 10:25:03+01  ian_mayo
// Initial revision
//
// Revision 1.1  2000-09-14 08:42:26+01  ian_mayo
// Initial revision
//

import java.text.DecimalFormat;
import java.text.NumberFormat;

import Debrief.GUI.Frames.Application;
import MWC.Algorithms.Conversions;
import MWC.GenericData.HiResDate;
import MWC.GenericData.Watchable;

/**
 * Calculate the bearing from the primary vessel to the secondary (for use in
 * the tote)
 */
public class relBearingCalc extends plainCalc
{
	/**
	 * string to use to represent UK bearing format
	 */
	public static final String UK_REL_BEARING_FORMAT = "UK";

	/**
	 * string to use to represent US bearing format
	 */
	public static final String US_REL_BEARING_FORMAT = "US";

	/**
	 * name of the string indicating bearing format
	 */
	public static final String REL_BEARING_FORMAT = "REL_BEARING_FORMAT";

	/**
	 * the bearing format to use
	 */
	private static Boolean UK_FORMAT = null;

	// ///////////////////////////////////////////////////////////
	// constructor
	// //////////////////////////////////////////////////////////
	/**
	 * constructor, initialise formatter
	 */
	public relBearingCalc()
	{
		super(new DecimalFormat("000.0"), "Rel Brg", "degs");
	}

	public relBearingCalc(NumberFormat pattern, String myTitle, String myUnits)
	{
		super(pattern, myTitle, myUnits);
	}

	// ///////////////////////////////////////////////////////////
	// member functions
	// //////////////////////////////////////////////////////////

	/**
	 * accessor class to find out if we want to use the US format for showing
	 * relative data (0..360 versus R180-G180)
	 * 
	 * @return yes/no
	 */
	public static boolean useUKFormat()
	{
		String theVal = Application.getThisProperty(REL_BEARING_FORMAT);

		if (theVal != null)
		{
			if (theVal.equals(UK_REL_BEARING_FORMAT))
			{
				UK_FORMAT = new Boolean(true);
			}
			else
			{
				UK_FORMAT = new Boolean(false);
			}
		}
		else
		{
			UK_FORMAT = new Boolean(true);
		}

		return UK_FORMAT.booleanValue();
	}

	public double calculate(Watchable primary, Watchable secondary,
			HiResDate thisTime)
	{
		double res = 0.0;
		if ((secondary != null) && (primary != null) && (secondary != primary))
		{
			// find
			double brg = secondary.getLocation().bearingFrom(primary.getLocation());
			brg = Conversions.clipRadians(brg);
			brg = Conversions.Rads2Degs(brg);
			double course = primary.getCourse();

			// find the course
			course = Conversions.Rads2Degs(course);

			// find the relative bearing
			course = brg - course;

			// do we trim the value to -180 to +180?
			if (useUKFormat())
			{
				if (course > 180)
					course -= 360;
				if (course < -180)
					course += 360;
			}
			else
			{
				// hey, we're in US. Check we're always +ve
				if (course < 0)
					course += 360;
			}

			res = course;
		}
		return res;
	}

	/**
	 * does this calculation require special bearing handling (prevent wrapping
	 * through 360 degs)
	 */
	public boolean isWrappableData()
	{
		return true;
	}

	/**
	 * produce our calculation from the Watchables
	 * 
	 * @param primary
	 *          primary watchable
	 * @param secondary
	 *          secondary watchable
	 * @return string representation of calculated value
	 */
	public String update(Watchable primary, Watchable secondary, HiResDate time)
	{
		String res = null;
		if ((primary != null) && (secondary != null) && (primary != secondary))
		{

			// get the value
			double theValue = calculate(primary, secondary, time);
			
			// and convert to string
			if (useUKFormat())
				res = MWC.Utilities.TextFormatting.FormatRelativeBearing
						.toString(theValue);
			else
				res = _myPattern.format(theValue);
		}
		else
			res = NOT_APPLICABLE;

		return res;
	}

}
