// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: Plottables.java,v $
// @author $Author: ian.mayo $
// @version $Revision: 1.9 $
// $Log: Plottables.java,v $
// Revision 1.9  2007/06/01 10:45:03  ian.mayo
// Sort by name rather than hash value.  Tidy how we recalc bounds
//
// Revision 1.8  2007/05/29 08:31:08  ian.mayo
// Provide fallback for how plottables are compared
//
// Revision 1.7  2006/10/03 08:22:57  Ian.Mayo
// Use better compareTo methods
//
// Revision 1.6  2006/05/25 14:10:42  Ian.Mayo
// Make plottables comparable
//
// Revision 1.5  2004/10/07 14:23:22  Ian.Mayo
// Reflect fact that enum is now keyword - change our usage to enumer
//
// Revision 1.4  2004/09/27 10:52:32  Ian.Mayo
// Decide whether to plot data item by comparing against visible data area rather than the area the user has just dragged
//
// Revision 1.3  2004/09/06 14:04:43  Ian.Mayo
// Switch to supporting editables in Layer Manager, and showing icon for any editables which have one
//
// Revision 1.2  2004/05/25 15:45:45  Ian.Mayo
// Commit updates from home
//
// Revision 1.1.1.1  2004/03/04 20:31:14  ian
// no message
//
// Revision 1.1.1.1  2003/07/17 10:07:04  Ian.Mayo
// Initial import
//
// Revision 1.6  2003-06-11 16:01:01+01  ian_mayo
// Tidy javadoc comments
//
// Revision 1.5  2003-02-07 09:49:05+00  ian_mayo
// rationalise unnecessary to da comments (that's do really)
//
// Revision 1.4  2002-11-25 11:11:26+00  ian_mayo
// Add concept of PlottablesType
//
// Revision 1.3  2002-07-12 15:46:56+01  ian_mayo
// Use constant to represent error value
//
// Revision 1.2  2002-05-28 09:25:35+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:15:14+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-11 14:02:31+01  ian_mayo
// Initial revision
//
// Revision 1.0  2001-07-17 08:46:34+01  administrator
// Initial revision
//
// Revision 1.2  2001-07-16 15:37:51+01  novatech
// only add the new plottable if it is non-null
//
// Revision 1.1  2001-01-03 13:43:08+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 21:43:00  ianmayo
// initial version
//
// Revision 1.10  2000-11-08 11:49:44+00  ian_mayo
// reflect change in status of TrackWrapper to Layer
//
// Revision 1.9  2000-04-19 11:37:37+01  ian_mayo
// optimise area calculation
//
// Revision 1.8  2000-03-07 10:11:38+00  ian_mayo
// optimisation, add myArea parameter to speed up getBounds operations
//
// Revision 1.7  2000-02-02 14:26:25+00  ian_mayo
// only bother with it if a projection has been set
//
// Revision 1.6  2000-01-20 10:14:16+00  ian_mayo
// after experiments/ d-lines
//
// Revision 1.5  1999-11-29 10:53:47+00  ian_mayo
// add getVector() method
//
// Revision 1.4  1999-11-26 15:45:04+00  ian_mayo
// adding toString method
//
// Revision 1.3  1999-11-25 13:33:47+00  ian_mayo
// inserted comment as reminder
//
// Revision 1.2  1999-11-15 15:42:37+00  ian_mayo
// checking whether shape & label is in the current data area
//
// Revision 1.1  1999-10-12 15:37:10+01  ian_mayo
// Initial revision
//
// Revision 1.5  1999-08-17 08:28:57+01  administrator
// added append function
//
// Revision 1.4  1999-08-17 08:15:44+01  administrator
// allow removal of elements
//
// Revision 1.2  1999-08-04 09:45:29+01  administrator
// minor mods, tidying up
//
// Revision 1.1  1999-07-27 10:50:51+01  administrator
// Initial revision
//
// Revision 1.3  1999-07-27 09:28:07+01  administrator
// tidying up
//
// Revision 1.2  1999-07-12 08:09:19+01  administrator
// Property editing added
//
// Revision 1.1  1999-07-07 11:10:09+01  administrator
// Initial revision
//
// Revision 1.1  1999-06-16 15:38:01+01  sm11td
// Initial revision
//
// Revision 1.4  1999-06-01 16:49:17+01  sm11td
// Reading in tracks aswell as fixes, commenting large portions of source code
//
// Revision 1.3  1999-02-04 08:02:30+00  sm11td
// Plotting to canvas, scaling canvas,
//
// Revision 1.2  1999-02-01 16:08:52+00  sm11td
// creating new sessions & panes, starting import management
//
// Revision 1.1  1999-01-31 13:33:15+00  sm11td
// Initial revision
//

package MWC.GUI;

import java.io.Serializable;
import java.util.Collection;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

import junit.framework.Assert;
import MWC.GUI.Chart.Painters.Grid4WPainter;
import MWC.GUI.Chart.Painters.GridPainter;
import MWC.GUI.Chart.Painters.ScalePainter;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldLocation;

/**
 * a list of Plottables
 */
public class Plottables implements Plottable, Serializable, PlottablesType
{

	static final long serialVersionUID = 4094060714021604632L;

	/**
	 * the actual list of plottables
	 */
	// private TreeSet<Editable> _thePlottables;
	private TreeSet<Editable> _thePlottables;

	// protected com.sun.java.util.collections.Hashset _thePlottables;
	/**
	 * the name of this list
	 */
	private String _theName;

	/**
	 * specify if this layer is currently visible or not
	 */
	private boolean _visible;

	/**
	 * remember the area covered by the plottables
	 */
	private WorldArea _myArea;

	// //////////////////////////////////////////////////////////////
	// constructor
	// //////////////////////////////////////////////////////////////
	public Plottables()
	{
		_thePlottables = new TreeSet<Editable>(new CompareEditables());
		_visible = true;
	}

	/**
	 * embedded class that knows how to compare two editables
	 * 
	 * @author ian.mayo
	 */
	public static class CompareEditables implements Comparator<Editable>,
			Serializable
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@SuppressWarnings("unchecked")
		public int compare(Editable p1, Editable p2)
		{
			int res;

			// just do our special check for items that should get plotted first
			if (p1 == p2)
				res = 0;
			else if (p1 instanceof PlotMeFirst)
			{
				res = -1;
			}
			else if (p2 instanceof PlotMeFirst)
			{
				res = 1;
			}
			else if (p1 instanceof Comparable)
			{
				// yup, let them go for it
				Comparable<Editable> c1 = (Comparable<Editable>) p1;
				res = c1.compareTo(p2);
			}
			else
				res = p1.getName().compareTo(p2.getName());

			return res;
		}
	}

	// //////////////////////////////////////////////////////////////
	// member functions
	// //////////////////////////////////////////////////////////////

	/**
	 * return the list of elements in this plottable
	 */
	public Enumeration<Editable> elements()
	{
		return new IteratorWrapper(_thePlottables.iterator());
	}

	/**
	 * return the current size of this list
	 */
	public int size()
	{
		return _thePlottables.size();
	}

	/**
	 * paint this list to the canvas
	 */
	public void paint(CanvasType dest)
	{
		// see if I am visible
		if (!getVisible())
			return;

		Iterator<Editable> enumer = _thePlottables.iterator();

		// note, we used to only test it the subject was in the data area,
		// but that left some items outside the user-dragged area not being visible.
		// - instead we calculate the visible data-area from the current screen
		// area, and
		// compare against that
		WorldArea wa = dest.getProjection().getVisibleDataArea();

		// drop out if we don't have a data area for the projection
		if (wa == null)
		{
			dest.getProjection().zoom(0.0);
			wa = dest.getProjection().getVisibleDataArea();
		}

		while (enumer.hasNext())
		{
			Object next = enumer.next();
			if (next instanceof Plottable)
			{
				Plottable thisP = (Plottable) next;

				// is this plottable visible
				if (thisP.getVisible())
				{

					// see if this plottable is within the data area
					WorldArea wp = thisP.getBounds();

					if (wp != null)
					{
						// it has an area, see if it is in view
						if (wp.overlaps(wa))
							thisP.paint(dest);
					}
					else
					{
						// it doesn't have an area, so plot it anyway
						thisP.paint(dest);
					}
				}
			}
		}
	}

	/**
	 * get the area covered by this list
	 */
	public MWC.GenericData.WorldArea getBounds()
	{
		// do we need to recalculate?
		if (_myArea == null)
		{
			// yup, get on with it...
			_myArea = recalculateAreaCovered();
		}

		return _myArea;

	}

	/**
	 * reset the bounds of this object
	 * 
	 */
	public void clearBounds()
	{
		_myArea = null;
	}

	/**
	 * method to recalculate the area covered by the plottables
	 */
	private WorldArea recalculateAreaCovered()
	{
		// so, step through the array, and calculate the area
		WorldArea res = null;

		Iterator<Editable> enumer = _thePlottables.iterator();
		while (enumer.hasNext())
		{
			Object nextOne = enumer.next();
			if (nextOne instanceof Plottable)
			{
				Plottable thisOne = (Plottable) nextOne;

				if (res == null)
				{
					WorldArea thisA = thisOne.getBounds();
					if (thisA != null)
						res = new WorldArea(thisA);
				}
				else
				{
					WorldArea thisA = thisOne.getBounds();
					if (thisA != null)
						res.extend(thisOne.getBounds());
				}
			}
		}

		return res;

	}

	/**
	 * return the name of the plottable
	 */
	public String getName()
	{
		return _theName;
	}

	/**
	 * convenience function, to describe this plottable as a string
	 */
	public String toString()
	{
		return getName() + " (" + size() + " items)";
	}

	/**
	 * set the name of the plottable
	 */
	public void setName(String theName)
	{
		_theName = theName;
	}

	/**
	 * does this item have an editor?
	 */
	public boolean hasEditor()
	{
		return false;
	}

	/**
	 * get the editing information for this type
	 */
	public Editable.EditorType getInfo()
	{
		return null;
	}

	/**
	 * it this item currently visible?
	 */
	public boolean getVisible()
	{
		return _visible;
	}

	/**
	 * set the visible flag for this layer
	 */
	public void setVisible(boolean visible)
	{
		_visible = visible;
	}

	/**
	 * Determine how far away we are from this point. or return INVALID_RANGE if
	 * it can't be calculated
	 */
	public double rangeFrom(WorldLocation other)
	{
		return INVALID_RANGE;
	}

	/**
	 * add the plottable to this list
	 */
	public void add(Editable thePlottable)
	{
		// check the creation worked
		if (thePlottable == null)
			return;

		// right, add it.
		_thePlottables.add(thePlottable);

		// hmm, if it's got bounds, let's clear the world area - that's
		// if we've got a world area... It may have already been cleared...
		if (_myArea != null)
		{
			if (thePlottable instanceof Plottable)
			{
				Plottable thePlot = (Plottable) thePlottable;
				WorldArea wa = thePlot.getBounds();
				if (wa != null)
				{
					/*
					 * don't just extend it, cause a recalculation if(_myArea == null)
					 * _myArea = new WorldArea(wa); else _myArea.extend(wa);
					 */
					_myArea = null;
				}
			}
		}
	}

	/**
	 * remove this area
	 */
	public void removeElement(Editable p)
	{
		// double check we've got it.
		boolean worked = _thePlottables.remove(p);

		if (!worked)
		{
			System.err.println("Failed trying to remove " + p + " from " + this);
		}

		// don't recalculate the area just yet, defer it until
		// we have removed all of the elements we intend to
		_myArea = null;
	}

	/**
	 * clera the list
	 */
	public void removeAllElements()
	{
		_thePlottables.clear();

		// clear the area
		_myArea = null;
	}

	/**
	 * append the other list of plottables to this one
	 */
	public void append(PlottablesType other)
	{
		Enumeration<Editable> enumer = other.elements();
		while (enumer.hasMoreElements())
		{
			Plottable p = (Plottable) enumer.nextElement();
			add(p);
		}
	}

	public int compareTo(Plottable arg0)
	{
		Plottable other = (Plottable) arg0;
		return getName().compareTo(other.getName());

		// final int res;
		// Plottable other = (Plottable) arg0;
		// int myCode = hashCode();
		// int otherCode = other.hashCode();
		// if(myCode < otherCode)
		// res = -1;
		// else if(myCode > otherCode)
		// res = 1;
		// else
		// res = 0;
		// return res;
	}

	/**
	 * @return the first item in our list
	 */
	public Plottable first()
	{
		return (Plottable) _thePlottables.first();
	}

	/**
	 * @return the last item in our list
	 */
	public Plottable last()
	{
		return (Plottable) _thePlottables.last();
	}

	/**
	 * @return all items in our list greater or equal to fromElement
	 */
	public SortedSet<Editable> tailSet(Editable fromElement)
	{
		return _thePlottables.tailSet(fromElement);
	}

	/**
	 * @return all items in our list less than toElement
	 */
	public SortedSet<Editable> headSet(Editable toElement)
	{
		return _thePlottables.headSet(toElement);
	}

	/**
	 * @return all items in our list greater or equal to fromElement and less than
	 *         toElement
	 */
	public SortedSet<Editable> subSet(Editable fromElement, Editable toElement)
	{
		return _thePlottables.subSet(fromElement, toElement);
	}

	/**
	 * return our data, expressed as a collection
	 * 
	 * @return
	 */
	public Collection<Editable> getData()
	{
		return _thePlottables;
	}

	public static final class IteratorWrapper implements
			java.util.Enumeration<Editable>
	{
		private final Iterator<Editable> _val;

		public IteratorWrapper(final Iterator<Editable> iterator)
		{
			_val = iterator;
		}

		public final boolean hasMoreElements()
		{
			return _val.hasNext();

		}

		public final Editable nextElement()
		{
			return _val.next();
		}
	}

	/**
	 * marker interface to indicate that this plottable should get plotted before
	 * the others.
	 * 
	 * @author ian
	 * 
	 */
	public static interface PlotMeFirst
	{
	}

	// ////////////////////////////////////////////////////////////////////////////////////////////////
	// testing for this class
	// ////////////////////////////////////////////////////////////////////////////////////////////////
	static public class Grid4WTest extends junit.framework.TestCase
	{
		static public final String TEST_ALL_TEST_TYPE = "UNIT";

		public Grid4WTest(String val)
		{
			super(val);
		}

		public void testAddRemove()
		{
			Plottables pl = new Plottables();
			Assert.assertEquals("Empty list", pl.size(), 0);
			GridPainter cp = new GridPainter();
			pl.add(cp);
			Assert.assertEquals("non-empty list", pl.size(), 1);
			pl.removeElement(cp);
			Assert.assertEquals("list", pl.size(), 0);

			ScalePainter sp = new ScalePainter();
			pl.add(sp);
			Assert.assertEquals("non-empty list", pl.size(), 1);
			pl.removeElement(sp);
			Assert.assertEquals("list empty", pl.size(), 0);
			Grid4WPainter c4p = new Grid4WPainter(null);
			pl.add(c4p);
			Assert.assertEquals("non-empty list", pl.size(), 1);
			pl.removeElement(c4p);
			Assert.assertEquals("list", pl.size(), 0);
		}

	}

}
