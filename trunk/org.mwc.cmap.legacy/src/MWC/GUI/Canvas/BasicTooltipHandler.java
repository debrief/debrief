// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: BasicTooltipHandler.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.5 $
// $Log: BasicTooltipHandler.java,v $
// Revision 1.5  2004/10/07 14:23:06  Ian.Mayo
// Reflect fact that enum is now keyword - change our usage to enumer
//
// Revision 1.4  2004/09/06 14:04:33  Ian.Mayo
// Switch to supporting editables in Layer Manager, and showing icon for any editables which have one
//
// Revision 1.3  2004/09/03 15:13:20  Ian.Mayo
// Reflect refactored plottable getElements
//
// Revision 1.2  2004/05/25 14:43:51  Ian.Mayo
// Commit updates from home
//
// Revision 1.1.1.1  2004/03/04 20:31:14  ian
// no message
//
// Revision 1.1.1.1  2003/07/17 10:07:06  Ian.Mayo
// Initial import
//
// Revision 1.5  2003-06-25 09:37:11+01  ian_mayo
// Comply with inspection, minor tidying of line output
//
// Revision 1.4  2003-06-25 08:50:15+01  ian_mayo
// Respect multi-line text labels
//
// Revision 1.3  2002-07-12 15:46:59+01  ian_mayo
// Use constant to represent error value
//
// Revision 1.2  2002-05-28 09:25:38+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:15:09+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-11 14:02:19+01  ian_mayo
// Initial revision
//
// Revision 1.3  2001-08-31 10:00:23+01  administrator
// Handle problem of "findingNearest" whilst we're still reading in data
//
// Revision 1.2  2001-08-29 19:21:48+01  administrator
// Handle problem of trying to find-nearest whilst we're still reading in data.  No need to synchronise methods though, since we don't want to slow the process down for the rest of of the time, just handle the errors.
//
// Revision 1.1  2001-08-21 15:17:13+01  administrator
// Use clever, recursive searching algorithm
//
// Revision 1.0  2001-07-17 08:46:32+01  administrator
// Initial revision
//
// Revision 1.2  2001-01-03 16:00:10+00  novatech
// include check that valid elements are returned from layer
//
// Revision 1.1  2001-01-03 13:43:03+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 21:43:05  ianmayo
// initial version
//
// Revision 1.2  2000-11-02 16:44:36+00  ian_mayo
// changing Layer into Interface, replaced by BaseLayer
//
// Revision 1.1  1999-12-13 11:27:14+00  ian_mayo
// Initial revision
//
package MWC.GUI.Canvas;

import java.awt.Point;

import MWC.GUI.CanvasType;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.Plottable;
import MWC.GUI.Shapes.TextLabel;
import MWC.GenericData.WorldLocation;

/**
 * simple implementation of tooltip handler. Provides support for multi-line
 * tooltips when applicable
 */
public final class BasicTooltipHandler implements CanvasType.TooltipHandler
{
	// ///////////////////////////////////////////////////////////
	// member variables
	// //////////////////////////////////////////////////////////
	/**
	 * the data we are working with.
	 */
	private final MWC.GUI.Layers _theLayers;

	// ///////////////////////////////////////////////////////////
	// constructor
	// //////////////////////////////////////////////////////////

	/**
	 * constructor, takes the layers we are currently working on.
	 * 
	 * @param theLayers
	 *          Layers object representing our data
	 */
	public BasicTooltipHandler(final Layers theLayers)
	{
		_theLayers = theLayers;
	}

	// ///////////////////////////////////////////////////////////
	// member functions
	// //////////////////////////////////////////////////////////

	public final String getString(final WorldLocation loc, final Point point)
	{
		String res = null;

		// drop out if we don't know our layers objects
		if(_theLayers == null)
			return res;
		
		final dataStruct nearest = new dataStruct(loc);

		// find the nearest editable item
		final int num = _theLayers.size();
		for (int i = 0; i < num; i++)
		{
			final Layer thisL = _theLayers.elementAt(i);
			if (thisL.getVisible())
			{
				try
				{
					findNearest(thisL, nearest);
				}
				catch (NullPointerException e)
				{
					// we sometimes get errors here, no need to propagate them
					// juse ignore them
				}
			}
		}

		// did we find one?
		if (nearest.getNearest() != null)
		{
			// is this a multi-line text label?
			final Plottable plt = nearest.getNearest();
			if (plt instanceof CanvasType.MultiLineTooltipProvider)
			{
				final CanvasType.MultiLineTooltipProvider provider = (CanvasType.MultiLineTooltipProvider) plt;
				res = "<html><font face=\"sansserif\">"; // start text

				String multiLineText = provider.getMultiLineName();

				// convert to HTML breaks
				multiLineText = toHTML(multiLineText);

				res += multiLineText; // the data
				res += "</font></html>"; // end text
			}
			else
			{
				res = nearest.getNearest().getName();
			}
		}
		else
			res = "";

		return res;
	}

	/**
	 * replace newline characters with the long equivalent.
	 * 
	 * @param val
	 *          the text as a normal Java string
	 * @return the text in XML form
	 */
	private static String toHTML(final String val)
	{
		String res = new String();

		if (val != null)
		{
			int start = 0;
			int newlineAt;

			final String HTML_MARKER = "<BR>";

			while ((newlineAt = val.indexOf(TextLabel.NEWLINE_MARKER, start)) > 0)
			{
				res += val.substring(start, newlineAt) + HTML_MARKER;
				start = newlineAt + TextLabel.NEWLINE_MARKER.length();
			}

			// did we find any?
			// if we did, we have to append the last line
			if (res.length() > 0)
			{
				// yes, we've found some - append the last line
				res += val.substring(start);
			}
			else
			{
				// no - we've not found anything, just take a copy of the line
				res = val;
			}
		}

		return res;

	}

	/**
	 * recursive method to find nearest item in this layer (or any layer it
	 * contains).
	 * 
	 * @param pl
	 *          the one we want to be near to
	 * @param nearest
	 *          the place to put the results
	 */
	private void findNearest(final Plottable pl, final dataStruct nearest)
	{
		if (pl.getVisible())
		{
			// is this a layer?
			if (pl instanceof Layer)
			{
				// do the check for this item
				// NOTE: we only check the distance of elements, not whole tracks
				// nearest.compare(pl);

				// we need to call ourselves for each layer inside it
				final Layer l = (Layer) pl;

				final java.util.Enumeration<Editable> enumer = l.elements();
				while (enumer.hasMoreElements())
				{
					Editable next = enumer.nextElement();
					if (next instanceof Plottable)
					{
						final Plottable this_plottable = (Plottable) next;
						findNearest(this_plottable, nearest);
					}
				}
			}
			else
			{
				// so, we've got to a node - compare it to our current data
				nearest.compare(pl);
			}
		}
	}

	/**
	 * class to store ongoing results of nearest search.
	 */
	private static final class dataStruct
	{
		/**
		 * the nearest distance so far.
		 */
		private double distance = -1;
		/**
		 * the nearest distance so far.
		 */
		private Plottable object = null;
		/**
		 * the location we're looking for.
		 */
		private WorldLocation location = null;

		/**
		 * constructor.
		 * 
		 * @param loc
		 *          location we're searching for
		 */
		public dataStruct(final WorldLocation loc)
		{
			location = loc;
		}

		/**
		 * compare method, is this new plottable nearest than the current one.
		 * 
		 * @param newPl
		 *          the item we're looking at
		 */
		private final void compare(final Plottable newPl)
		{
			// is it even visible?
			if (!newPl.getVisible())
				return;

			// calculate the range
			final double range = newPl.rangeFrom(location);

			// did it produce a valid range?
			if (range == Plottable.INVALID_RANGE)
			{
				return;
			}

			// so, we have a valid range. is it our first?
			if (distance == Plottable.INVALID_RANGE)
			{
				object = newPl;
				distance = range;
			}
			else
			{
				if (range < distance)
				{
					distance = range;
					object = newPl;
				}
			}
		}

		/**
		 * accessor method to get the results.
		 * 
		 * @return the nearest item
		 */
		public final Plottable getNearest()
		{
			return object;
		}
	}
}
