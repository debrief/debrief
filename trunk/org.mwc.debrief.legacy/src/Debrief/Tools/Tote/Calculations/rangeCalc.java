package Debrief.Tools.Tote.Calculations;

// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: rangeCalc.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.9 $
// $Log: rangeCalc.java,v $
// Revision 1.9  2006/08/08 13:55:22  Ian.Mayo
// Refactor range calc out to Conversions
//
// Revision 1.8  2006/06/16 07:57:48  Ian.Mayo
// Make things over-rideable (for SWT slant-range calculator)
//
// Revision 1.7  2006/03/16 16:01:08  Ian.Mayo
// Override not-applicable statement
//
// Revision 1.6  2005/09/16 10:09:19  Ian.Mayo
// Extend, to allow over-riding preferences in Eclipse framework
//
// Revision 1.5  2005/06/17 13:20:30  Ian.Mayo
// Keep track of user-preference in range units
//
// Revision 1.4  2005/06/15 14:33:31  Ian.Mayo
// Allow the prefs provider to be overridden
//
// Revision 1.3  2005/05/26 14:59:31  Ian.Mayo
// Minor tidying
//
// Revision 1.2  2004/11/25 10:24:39  Ian.Mayo
// Switch to Hi Res dates
//
// Revision 1.1.1.2  2003/07/21 14:49:15  Ian.Mayo
// Re-import Java files to keep correct line spacing
//
// Revision 1.7  2003-06-30 13:51:42+01  ian_mayo
// handle the KYD units
//
// Revision 1.6  2003-03-19 15:37:06+00  ian_mayo
// improvements according to IntelliJ inspector
//
// Revision 1.5  2003-02-10 16:27:49+00  ian_mayo
// Reflect name change of get wrappable data
//
// Revision 1.4  2003-02-07 15:36:10+00  ian_mayo
// Add accessor flag to indicate is this calculation needs special processing (where data crosses through zero)
//
// Revision 1.3  2002-12-16 15:40:20+00  ian_mayo
// Reflect change in location for units labels
//
// Revision 1.2  2002-05-28 09:25:12+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:11:42+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-23 12:28:34+01  ian_mayo
// Initial revision
//
// Revision 1.1  2001-10-02 09:31:12+01  administrator
// Allow use of different range units
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
// Revision 1.1  2000-09-14 10:25:03+01  ian_mayo
// Initial revision
//
// Revision 1.3  2000-05-19 11:23:43+01  ian_mayo
// provided n/a result string when secondary watchable not present
//
// Revision 1.2  2000-03-07 14:48:12+00  ian_mayo
// optimised algorithms
//
// Revision 1.1  1999-10-12 15:34:20+01  ian_mayo
// Initial revision
//
// Revision 1.1  1999-08-26 10:03:00+01  administrator
// Initial revision
//

import java.text.DecimalFormat;

import MWC.GUI.ToolParent;
import MWC.GenericData.HiResDate;
import MWC.GenericData.Watchable;

public class rangeCalc extends plainCalc
{

	/**
	 * remember what units the user prefers
	 */
	static String _myUnits = null;

	protected static final java.text.NumberFormat _decFormatter = new java.text.DecimalFormat(
			"0.00");

	// ///////////////////////////////////////////////////////////
	// constructor
	// //////////////////////////////////////////////////////////
	public rangeCalc()
	{
		super(new DecimalFormat("000"), "Range", "yards");
	}

	// ///////////////////////////////////////////////////////////
	// member functions
	// //////////////////////////////////////////////////////////

	public double calculate(Watchable primary, Watchable secondary,
			HiResDate thisTime)
	{
		double range = 0.0;
		double theRng = 0.0;
		if ((primary != null) && (secondary != null) && (primary != secondary))
		{
			range = primary.getLocation().rangeFrom(secondary.getLocation());

			// we output the range value according to the currently selected range
			// units
			String theUnits = getUnits();

			theRng = convertRange(range, theUnits);

		}
		return theRng;
	}
	
	/** convert the range to the supplied units
	 * 
	 * @param range range (in degrees)
	 * @param theUnits target units
	 * @return converted value
	 */
	public static final double convertRange(double range, String theUnits)
	{
		double theRng = 0;
		// do the units conversion
		if (theUnits.equals(MWC.GUI.Properties.UnitsPropertyEditor.YDS_UNITS)
				|| theUnits
						.equals(MWC.GUI.Properties.UnitsPropertyEditor.OLD_YDS_UNITS))
		{
			theRng = MWC.Algorithms.Conversions.Degs2Yds(range);
		}
		else if (theUnits
				.equals(MWC.GUI.Properties.UnitsPropertyEditor.KYD_UNITS))
		{
			theRng = MWC.Algorithms.Conversions.Degs2Yds(range) / 1000.0;
		}
		else if (theUnits
				.equals(MWC.GUI.Properties.UnitsPropertyEditor.METRES_UNITS))
		{
			theRng = MWC.Algorithms.Conversions.Degs2m(range);
		}
		else if (theUnits.equals(MWC.GUI.Properties.UnitsPropertyEditor.KM_UNITS))
		{
			theRng = MWC.Algorithms.Conversions.Degs2Km(range);
		}
		else if (theUnits.equals(MWC.GUI.Properties.UnitsPropertyEditor.NM_UNITS))
		{
			theRng = MWC.Algorithms.Conversions.Degs2Nm(range);
		}
		else
		{
			MWC.Utilities.Errors.Trace
					.trace("Range/Bearing units in properties file may be corrupt");
		}
		return theRng;
	}

	/**
	 * does this calculation require special bearing handling (prevent wrapping
	 * through 360 degs)
	 */
	public final boolean isWrappableData()
	{
		return false;
	}

	public String update(Watchable primary, Watchable secondary,
			HiResDate time)
	{
		String res = null;

		java.text.NumberFormat formatter = _myPattern;

		// we know that the default (parent) formatter (yds) has no decimal places,
		// we
		// want to if we are working in km or nm
		if (getUnits().equals(MWC.GUI.Properties.UnitsPropertyEditor.NM_UNITS)
				|| getUnits().equals(MWC.GUI.Properties.UnitsPropertyEditor.KM_UNITS)
				|| getUnits().equals(MWC.GUI.Properties.UnitsPropertyEditor.KYD_UNITS))
			formatter = _decFormatter;

		// do we have sufficient data for a calc?
		if ((primary != null) && (secondary != null) && (primary != secondary))
		{
			// ok, produce the string using our calculation and our formatter
			res = formatter.format(calculate(primary, secondary, time));
		}
		else
			res = NOT_APPLICABLE;

		return res;
	}

	static ToolParent _prefsProvider = null;

	/**
	 * allow external class to specify where we get our prefs from
	 * 
	 * @param theParent
	 */
	public static void init(ToolParent theParent)
	{
		_prefsProvider = theParent;
	}

	protected final static String getMyUnits()
	{
		if (_prefsProvider != null)
		{
			_myUnits = _prefsProvider
					.getProperty(MWC.GUI.Properties.UnitsPropertyEditor.UNITS_PROPERTY);

			if (_myUnits == "")
				_myUnits = null;
		}
		else
		{
			if (_myUnits == null)
			{
				_myUnits = Debrief.GUI.Frames.Application
						.getThisProperty(MWC.GUI.Properties.UnitsPropertyEditor.UNITS_PROPERTY);
			}
		}
		if (_myUnits == null)
			_myUnits = MWC.GUI.Properties.UnitsPropertyEditor.YDS_UNITS;

		return _myUnits;		
	}
	
	/**
	 * ok, find out what units to use for the range
	 * 
	 * @return
	 */
	public final String getUnits()
	{
		return getMyUnits();
	}

}
