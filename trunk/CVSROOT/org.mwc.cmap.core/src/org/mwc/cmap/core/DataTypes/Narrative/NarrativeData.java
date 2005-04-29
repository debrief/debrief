/**
 * 
 */
package org.mwc.cmap.core.DataTypes.Narrative;

import java.beans.*;
import java.beans.PropertyChangeSupport;
import java.util.Date;

import MWC.GenericData.HiResDate;
import MWC.Utilities.TextFormatting.DebriefFormatDateTime;

/**
 * @author ian.mayo
 */
public class NarrativeData
{

	// //////////////////////////////////////
	// member variables
	// //////////////////////////////////////
	private String _myName = "blank";

	/**
	 * where we store our narrative data
	 */
	private final java.util.TreeSet _myEntries;

	/**
	 * the line width to draw
	 */
	private int _lineWidth = 1;

	/**
	 * property change support
	 */
	private PropertyChangeSupport _pSupport = null;

	// //////////////////////////////////////
	// constructors
	// //////////////////////////////////////
	/**
	 * create a new narrative
	 * @param title
	 */
	public NarrativeData(final String title)
	{
		_myEntries = new java.util.TreeSet();
		_myName = title;
	}

	// //////////////////////////////////////
	// member methods to meet plain wrapper responsibilities
	// //////////////////////////////////////

	public static NarrativeData createDummyData(String title, int len)
	{
		NarrativeData res = new NarrativeData(title);
		Date newDate = new Date(2005, 06, (int) (Math.random() * 12), (int) (Math
				.random() * 13), 33);
		for (int i = 0; i < len; i++)
		{
			NarrativeEntry ne = new NarrativeEntry(title, new HiResDate(newDate
					.getTime()
					+ i * 1000, 0), "type_" + (int) (Math.random() * 5), "entry number "
					+ i + " for narrative:" + title);
			res.add(ne);
		}

		return res;
	}

	public final String getName()
	{
		return _myName;
	}

	public final void setName(final String name)
	{
		_myName = name;
	}

	// //////////////////////////////
	// property change support
	// //////////////////////////////
	
	public void addPropertyChangeListener(PropertyChangeListener listener)
	{
		if(_pSupport == null)
			_pSupport = new PropertyChangeSupport(this);
		
		_pSupport.addPropertyChangeListener(listener);
	}
	
	public void removePropertyChangeListener(PropertyChangeListener listener)
	{
		_pSupport.removePropertyChangeListener(listener);
	}

	// //////////////////////////////////////
	// member methods to meet Layer responsibilities
	// //////////////////////////////////////

	public final void removeElement(final NarrativeEntry entry)
	{
		_myEntries.remove(entry);
	}

	public final void add(final NarrativeEntry entry)
	{
		_myEntries.add(entry);
	}

	// ///////////////////////////////////////
	// other member functions
	// ///////////////////////////////////////

	public final String toString()
	{
		return getName() + " (" + _myEntries.size() + " items)";
	}

	public final java.util.AbstractCollection getData()
	{
		return _myEntries;
	}

	// ///////////////////////////////////////
	// child class which stores a single narrative entry
	// ///////////////////////////////////////
	static public final class NarrativeEntry implements java.lang.Comparable
	{
		// ///////////////////////////////////////////
		// member variables
		// ///////////////////////////////////////////
		final String _track;

		final HiResDate _DTG;

		final String _entryType;

		final String _entry;

		String _DTGString = null;

		// ///////////////////////////////////////////
		// constructor
		// ///////////////////////////////////////////
		public NarrativeEntry(final String track, final HiResDate DTG,
				final String entryType, final String entry)
		{
			_track = track;
			_DTG = DTG;
			_entryType = entryType;
			_entry = entry;
		}

		public NarrativeEntry(final String track, final HiResDate DTG,
				final String entry)
		{
			this(track, DTG, "n/a", entry);
		}

		// ///////////////////////////////////////////
		// accessor methods
		// ///////////////////////////////////////////
		public final String getTrackName()
		{
			return _track;
		}

		public final String getEntry()
		{
			return _entry;
		}

		public final HiResDate getDTG()
		{
			return _DTG;
		}
		
		public final String getEntryType()
		{
			return _entryType;
		}

		public final String getDTGString()
		{
			if (_DTGString == null)
				_DTGString = DebriefFormatDateTime.toStringHiRes(_DTG);

			return _DTGString;
		}

		/**
		 * member function to meet requirements of comparable interface *
		 */
		public final int compareTo(final Object o)
		{
			final NarrativeEntry other = (NarrativeEntry) o;
			int res = 0;

			// are we looking at the same item?
			if (o.equals(this))
			{
				res = 0;
			}
			else if (_DTG.lessThan(other._DTG))
				res = -1;
			else
				res = 1;

			return res;

		}

		/**
		 * get the name of this entry, using the formatted DTG
		 */
		public final String getName()
		{
			return DebriefFormatDateTime.toStringHiRes(_DTG);
		}

		public final String toString()
		{
			return getName();
		}

	}

}
