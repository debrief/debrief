/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
// $RCSfile: ImportArgosFix.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.3 $
// $Log: ImportArgosFix.java,v $
// Revision 1.3  2005/12/13 09:04:32  Ian.Mayo
// Tidying - as recommended by Eclipse
//
// Revision 1.2  2004/11/25 10:24:11  Ian.Mayo
// Switch to Hi Res dates
//
// Revision 1.1.1.2  2003/07/21 14:47:38  Ian.Mayo
// Re-import Java files to keep correct line spacing
//
// Revision 1.3  2003-03-19 15:37:43+00  ian_mayo
// improvements according to IntelliJ inspector
//
// Revision 1.2  2002-05-28 12:28:08+01  ian_mayo
// after update
//
// Revision 1.1  2002-05-28 09:12:14+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-23 12:29:43+01  ian_mayo
// Initial revision
//
// Revision 1.0  2001-07-17 08:41:34+01  administrator
// Initial revision
//
// Revision 1.1  2001-01-03 13:40:47+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 20:46:41  ianmayo
// initial import of files
//
// Revision 1.2  2000-06-06 12:43:10+01  ian_mayo
// handle passing through midnight
//
// Revision 1.1  2000-06-05 14:20:33+01  ian_mayo
// Initial revision
//
package Debrief.ReaderWriter.PCArgos;

import java.text.ParseException;
import java.util.*;

import Debrief.ReaderWriter.Replay.ReplayFix;
import MWC.GenericData.*;
import MWC.TacticalData.Fix;
import MWC.Utilities.ReaderWriter.PlainLineImporter;
import MWC.Utilities.ReaderWriter.XML.MWCXMLReader;

/** import a fix from a line of text (in PCArgos format)
 */
final class ImportArgosFix implements PlainLineImporter
{

	private WorldLocation _origin = null;
	private long _dtg = 0;
	private Hashtable<String, Fix> _lastPoints = null;
	private long _freq = 0;
	
	public final void setParameters(final WorldLocation origin,
														final long DTG,
														final Hashtable<String, Fix> lastPoints,
														final long freq)
	{
		_origin = origin;
		_dtg = DTG;
		_lastPoints = lastPoints;
		_freq = freq;
	}
	
  /**
   */
  public final Object readThisLine(final String theLine){
    
    // declare local variables
    WorldLocation theLoc;
		double x;
		double y;
		double z;
		HiResDate dtg;
    
    // parse the line         
// ?? A01210000  -164.20 36235.19  -113.40     3.58    -1.15     0.15    -0.04     0.08     0.04
		
    // first the trackname
		final String trk =theLine.substring(2, 6);
		final String theDTG = theLine.substring(6, 12);

		// sort out this time as elapsed seconds		
		final int hrs = Integer.parseInt(theDTG.substring(0,2));
		final int mins = Integer.parseInt(theDTG.substring(2,4));
		final int secs = Integer.parseInt(theDTG.substring(4,6));
		long val = (hrs * 60 * 60) +
							 (mins * 60) +
							 (secs);
		
		// convert it to millis
		val *= 1000;

		// and add it to our date origin
		val += _dtg;		
		
		// what is the last dtg for this track?
		final Fix fx = (Fix)_lastPoints.get(trk);
		if(fx != null)
		{
			// get the time
			final HiResDate lastDTG = fx.getTime();
			
			// see if we have passed through midnight,
			if(val < lastDTG.getDate().getTime())
			{			
				// ooh, we must have stepped back in time.  No, let's assume
				// that this new DTG is for the next day, by adding
				// 24 hours (as millis) to the dtg
				val += 1 * 24 * 60 * 60 * 1000;
			}
			
			// are we too soon?
			if(val < lastDTG.getDate().getTime() + _freq)
			{
				// yes, return
				return null;
			}
			// no, continue
		}
		else
		{
			// this is the first item we have found for this track, use it
		}
		
		final String location_details = theLine.substring(12, theLine.length());
		final StringTokenizer st = new StringTokenizer(location_details);
		
		
		final String theX = st.nextToken();
		final String theY = st.nextToken();
		final String theZ = st.nextToken();

		try
		{
			x = MWCXMLReader.readThisDouble(theX);
			y = MWCXMLReader.readThisDouble(theY);
			z = MWCXMLReader.readThisDouble(theZ);
	
			
			// calc the bearing
			final double brg = Math.atan2(y,x);
			final double rng = Math.sqrt(x*x + y*y);
			final WorldVector offset = new WorldVector(brg, MWC.Algorithms.Conversions.Yds2Degs(rng), z);
			theLoc = _origin.add(offset);
	
		    dtg = new HiResDate(val, 0);
		
		
		    // create the fix ready to store it
		    final Fix res = new Fix(dtg, theLoc, 0.0, 0.0);
				
		    final ReplayFix rf = new ReplayFix();
		    rf.theFix = res;
		    rf.theTrackName = trk;
		    rf.theSymbology = "@@";
		    	    
		    return rf;
		}
		catch(final ParseException pe)
		{
			MWC.Utilities.Errors.Trace.trace(pe,
					"Whilst reading ArgosFix coordinates");
			return null;
		}
  }
  public final String getYourType(){
    return null;
  }
	
	/** export the specified shape as a string
	 * @return the shape in String form
	 * @param theWrapper the Shape we are exporting
	 */	
	public final String exportThis(final MWC.GUI.Plottable theWrapper)
	{
		return null;
	}

	/** indicate if you can export this type of object
	 * @param val the object to test
	 * @return boolean saying whether you can do it
	 */
	public final boolean canExportThis(final Object val)
	{
		return false;
	}
	
}








