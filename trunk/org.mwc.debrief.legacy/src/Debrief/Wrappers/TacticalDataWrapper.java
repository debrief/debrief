// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: TacticalDataWrapper.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.2 $
// $Log: TacticalDataWrapper.java,v $
// Revision 1.2  2005/06/07 08:38:31  Ian.Mayo
// Provide efficiency to stop millions of popup menu items representing hidden tma solutions.
//
// Revision 1.1  2005/06/06 14:45:08  Ian.Mayo
// Refactor how we support tma & sensor data
//

package Debrief.Wrappers;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;

import Debrief.GUI.Tote.Painters.SnailDrawTacticalContact;
import Debrief.GUI.Tote.Painters.SnailDrawTacticalContact.PlottableWrapperWithTimeAndOverrideableColor;
import MWC.GUI.Editable;
import MWC.GUI.FireReformatted;
import MWC.GUI.Plottable;
import MWC.GUI.Properties.TimeFrequencyPropertyEditor;
import MWC.GenericData.HiResDate;
import MWC.GenericData.Watchable;
import MWC.GenericData.WorldArea;

abstract public class TacticalDataWrapper extends MWC.GUI.PlainWrapper
		implements MWC.GUI.Layer, SnailDrawTacticalContact.HostedList {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// //////////////////////////////////////
	// member variables
	// //////////////////////////////////////
	/**
	 * the name of this sensor
	 */
	private String _myName = "blank sensor";

	/**
	 * how often items are displayed
	 * 
	 */
	private HiResDate _lastVisibleFrequency = new HiResDate(0,
			TimeFrequencyPropertyEditor.SHOW_ALL_FREQUENCY);
	/**
	 * the track representing our host vessel
	 */
	private String _myTrackName;

	/**
	 * our editor
	 */
	protected transient MWC.GUI.Editable.EditorType _myEditor;

	/**
	 * our list of contacts
	 */
	protected final java.util.SortedSet<Editable> _myContacts;

	/**
	 * the track of our host
	 */
	protected transient MWC.GenericData.WatchableList _myHost;

	/**
	 * manage the start/stop times for this period
	 */
	protected MWC.GenericData.TimePeriod _timePeriod;

	/**
	 * local copy of the last fix found, to speed up some getNearestTo(...)
	 * operations
	 */
	protected transient MWC.GenericData.Watchable[] lastContact;

	/**
	 * the DTG last searched for in getNearestTo()
	 */
	protected transient HiResDate lastDTG;

	/**
	 * thickness of line to draw
	 */
	private int _lineWidth = 1;

	// //////////////////////////////////////
	// constructors
	/**
	 * ////////////////////////////////////////
	 */
	public TacticalDataWrapper(final String title) {
		_myName = title;
		_myContacts = new java.util.TreeSet<Editable>();
		setVisible(false);
	}

	public HiResDate getVisibleFrequency() {
		return _lastVisibleFrequency;
	}

	/**
	 * change how frequently our data items are dislayed
	 * 
	 * @param visibleFrequency
	 */
	@FireReformatted
	public void setVisibleFrequency(HiResDate visibleFrequency) {
		_lastVisibleFrequency = visibleFrequency;

		setFixes(new ObjectManipulator() {
			public void apply(Object subject, boolean value) {
				PlottableWrapperWithTimeAndOverrideableColor pl = (PlottableWrapperWithTimeAndOverrideableColor) subject;
				pl.setVisible(value);
			}
		}, visibleFrequency);

	}


	/**
	 * switch the sample rate of this track to the supplied frequency
	 * 
	 * @param theVal
	 */
	public void decimate(HiResDate theVal)
	{
		Vector<PlottableWrapperWithTimeAndOverrideableColor> newItems = new Vector<PlottableWrapperWithTimeAndOverrideableColor>();

		long startTime = this.getStartDTG().getMicros();
		long endTime = this.getEndDTG().getMicros();

		Enumeration<Editable> _cuts = elements();
		PlottableWrapperWithTimeAndOverrideableColor _last = (PlottableWrapperWithTimeAndOverrideableColor) _cuts.nextElement();
		
		// check we've got a single point
		if(_last == null)
			return;
		
		PlottableWrapperWithTimeAndOverrideableColor _next = (PlottableWrapperWithTimeAndOverrideableColor) _cuts.nextElement();
		
		// have we got two?
		if(_next == null)
			return;

		// right - sort out what time period we're working through
		for (long tNow = startTime; tNow <= endTime; tNow += theVal.getMicros())
		{
			if(_next == null)
			{
				// hey, this really shouldn't have happened, prob ignore it
			}
			
			// loop through to find this item. are we looking before the first item?
			while ((_next != null)&&(_next.getDTG().getMicros() < tNow))
			{
				_last = _next;
				_next = null;
				if(_cuts.hasMoreElements())
   				_next = (PlottableWrapperWithTimeAndOverrideableColor) _cuts.nextElement();
			}

			// have we overshot?
			if (_next == null)
			{
				// bugger, drop out
			}
			else
			{
				// right, we appear to be either side of the relevant item
				// interpolate the values
				// go for the freq first
				LinearInterpolator interp = new LinearInterpolator((Watchable)_last,(Watchable) _next, tNow);
				
				// create a new data value
				PlottableWrapperWithTimeAndOverrideableColor newItem = createItem(_last, _next, interp, tNow);

				// and store it.
				newItems.add(newItem);
			}
		}
		
		// ditch our positions
		_myContacts.clear();

		// store the new sensor items
		for (Iterator<PlottableWrapperWithTimeAndOverrideableColor> iterator = newItems.iterator(); iterator
				.hasNext();)
		{
			PlottableWrapperWithTimeAndOverrideableColor fix = iterator.next();
			this.add(fix);
		}
	}
	
	abstract protected PlottableWrapperWithTimeAndOverrideableColor createItem(
			PlottableWrapperWithTimeAndOverrideableColor last,
			PlottableWrapperWithTimeAndOverrideableColor next, 
			LinearInterpolator interp2, long tNow);

	
	/**
	 * instruct this object to clear itself out, ready for ditching
	 */
	public final void closeMe() {
		// do the parent
		super.closeMe();

		// and the fixes
		// first ask them to close themselves
		final java.util.Iterator<Editable> it = _myContacts.iterator();
		while (it.hasNext()) {
			final Object val = it.next();
			if (val instanceof MWC.GUI.PlainWrapper) {
				final MWC.GUI.PlainWrapper pw = (MWC.GUI.PlainWrapper) val;
				pw.closeMe();
			}
		}

		// now ditch them
		_myContacts.clear();

		// and the other stuff
		_myEditor = null;
		_myHost = null;
		_myTrackName = null;

	}

	// //////////////////////////////////////
	// member methods to meet plain wrapper responsibilities
	// //////////////////////////////////////

	/**
   *
   *
   */
	public final String getName() {
		return _myName;
	}

	/**
	 * setName
	 * 
	 * @param name
	 *            parameter for setName
	 */
	public final void setName(final String name) {
		_myName = name;
	}

	/**
	 * the line thickness (convenience wrapper around width)
	 * 
	 * @return
	 */
	public final int getLineThickness() {
		return _lineWidth;
	}

	/**
	 * the line thickness (convenience wrapper around width)
	 */
	public final void setLineThickness(final int val) {
		_lineWidth = val;
	}

	/**
	 * getTrack
	 * 
	 * @return the returned TrackWrapper
	 */
	public final String getTrackName() {
		return _myTrackName;
	}

	/**
	 * setTrack
	 * 
	 * @param name
	 *            parameter for setTrack
	 */
	@FireReformatted	
	public final void setTrackName(final String name) {
		_myTrackName = name;
	}

	/**
	 * set our host track
	 */
	public void setHost(final MWC.GenericData.WatchableList host) {
		_myHost = host;
	}

	/**
	 * set our host track
	 */
	public final MWC.GenericData.WatchableList getHost() {
		return _myHost;
	}

	/**
	 * hasEditor
	 * 
	 * @return the returned boolean
	 */
	public final boolean hasEditor() {
		return true;
	}

	/**
	 * how far away are we from this point? or return -1 if it can't be
	 * calculated We don't search through our child objects, the searching
	 * algorithms do that for themselves, since we are a layer
	 */
	public double rangeFrom(final MWC.GenericData.WorldLocation other) {
		double res = -1;
		if (!getVisible()) {
			// hey, we're invisible, return null
		} else {
			final WorldArea area = this.getBounds();

			// did we find a range? we may not have done if all of the
			// individual sensor lines are not visible
			if (area != null)
				res = area.rangeFrom(other);
		}
		return res;
	}

	/**
	 * paint
	 * 
	 * @param canvas
	 *            parameter for paint
	 */
	public final void paint(final MWC.GUI.CanvasType canvas) {
		if (!getVisible())
			return;

		// remember the current line width
		float oldLineWidth = canvas.getLineWidth();

		// set the line width
		canvas.setLineWidth(_lineWidth);

		// trigger our child sensor contact data items to plot themselves
		final java.util.Iterator<Editable> it = _myContacts.iterator();
		while (it.hasNext()) {
			Object next = it.next();
			final PlottableWrapperWithTimeAndOverrideableColor con = (PlottableWrapperWithTimeAndOverrideableColor) next;

			// ok, plot it - and don't make it keep it simple, lets really go
			// for it man!
			con.paint(_myHost, canvas, false);
		}

		// and restore the line width
		canvas.setLineWidth(oldLineWidth);
	}

	// //////////////////////////////////////
	// member methods to meet Layer responsibilities
	/**
	 * ////////////////////////////////////////
	 */

	public final java.util.Enumeration<Editable> elements() {
		return new IteratorWrapper(_myContacts.iterator());
	}

	/**
	 * removeElement
	 * 
	 * @param plottable
	 *            parameter for removeElement
	 */
	public final void removeElement(final MWC.GUI.Editable plottable) {
		_myContacts.remove(plottable);
	}

	/**
	 * append
	 * 
	 * @param layer
	 *            parameter for append
	 */
	public final void append(final MWC.GUI.Layer layer) {
		// don't bother
	}

	/**
	 * exportShape
	 */
	public final void exportShape() {
		// don't bother
	}

	// ///////////////////////////////////////
	// other member functions
	// ///////////////////////////////////////

	public final int compareTo(final Plottable o) {
		int res = 0;
		if (o instanceof TacticalDataWrapper) {
			// check they're both of the same class (one might be sensor whilst
			// the other's tma)
			if (o.getClass().toString().equals(this.getClass().toString())) {
				// compare the names
				final TacticalDataWrapper sw = (TacticalDataWrapper) o;
				res = sw.getName().compareTo(this.getName());
			} else {
				// they're different types, point that out.
				res = -1;
			}

		} else {
			// just put it after us
			res = -1;
		}
		return res;
	}

	// /////////////////////////////////////////////////////////////////
	// support for WatchableList interface (required for Snail Trail plotting)
	// //////////////////////////////////////////////////////////////////

	/**
	 * get the start DTG of this list
	 * 
	 * @return the start DTG, or -1 if not time-related
	 */
	public final HiResDate getStartDTG() {
		return _timePeriod.getStartDTG();
	}

	/**
	 * get the end DTG of this list
	 * 
	 * @return the end DTG, or -1 if not time-related
	 */
	public final HiResDate getEndDTG() {
		return _timePeriod.getEndDTG();
	}

	/**
	 * return the set of items which fall inside the indicated period
	 */
	public final java.util.Collection<Editable> getItemsBetween(
			final HiResDate start, final HiResDate end) {
		// see if we have _any_ points in range
		if ((getStartDTG().greaterThan(end)) || (getEndDTG().lessThan(start)))
			return null;

		// hey, we can do this on our own!
		java.util.Vector<Editable> res = new java.util.Vector<Editable>(0, 1);
		final java.util.Iterator<Editable> it = _myContacts.iterator();
		boolean finished = false;
		while ((it.hasNext()) && (!finished)) {
			final PlottableWrapperWithTimeAndOverrideableColor scw = (PlottableWrapperWithTimeAndOverrideableColor) it
					.next();
			final HiResDate thisD = scw.getTime();
			if (thisD.lessThan(start)) {
				// hey, ditch it!
			} else if (thisD.greaterThan(end)) {
				// hey, ditch it, we must be past the end
				finished = true;
			} else {
				// this must be in range
				res.add(scw);
			}
		}

		if (res.size() == 0)
			res = null;

		return res;
	}

	protected interface ObjectManipulator {
		void apply(Object subject, boolean value);
	}

	/**
	 * the setter function which passes through the track
	 */
	private void setFixes(final ObjectManipulator setter, final HiResDate theVal) {
		final long freq = theVal.getMicros();

		// briefly check if we are revealing/hiding all times (ie if freq is 1
		// or 0)
		if (freq == TimeFrequencyPropertyEditor.SHOW_ALL_FREQUENCY) {
			// show all of the labels
			final Enumeration<Editable> iter = elements();
			while (iter.hasMoreElements()) {
				setter.apply(iter.nextElement(), true);
			}
		} else {
			// no, we're not just blindly doing all of them. do them at the
			// correct
			// frequency

			// hide all of the labels/symbols first
			final Enumeration<Editable> enumA = elements();
			while (enumA.hasMoreElements()) {
				setter.apply(enumA.nextElement(), false);
			}

			if (freq == 0) {
				// we can ignore this, since we have just hidden all of the
				// points
			} else {

				// pass through the track setting the values

				// sort out the start and finish times
				long start_time = getStartDTG().getMicros();
				final long end_time = getEndDTG().getMicros();

				// first check that there is a valid time period between start
				// time
				// and end time
				if (start_time + freq < end_time) {
					long num = start_time / freq;

					// we need to add one to the quotient if it has rounded down
					if (start_time % freq == 0) {
						// start is at our freq, so we don't need to increment
						// it
					} else {
						num++;
					}

					// calculate new start time
					start_time = num * freq;
				} else {
					// there is not one of our 'intervals' between the start and
					// the end,
					// so use the start time
				}

				while (start_time <= end_time) {
					// right, increment the start time by one, because we were
					// getting the
					// fix immediately before the requested time
					final HiResDate thisDTG = new HiResDate(0, start_time);
					final MWC.GenericData.Watchable[] list = this
							.getNearestTo(thisDTG);
					// check we found some
					if (list.length > 0) {
						setter.apply(list[0], true);
					}
					// produce the next time step
					start_time += freq;
				}
			}

		}
	}

	/**
	 * filter the list to the specified time period.
	 */
	public final void filterListTo(final HiResDate start, final HiResDate end) {
		final Iterator<Editable> contactWrappers = _myContacts.iterator();
		while (contactWrappers.hasNext()) {

			/**
			 * note, we had trouble with unpredicable behaviour when testing
			 * this one. We overcame the problem by initialising the dates in
			 * the two Gregorian calendars.
			 */
			final PlottableWrapperWithTimeAndOverrideableColor fw = (PlottableWrapperWithTimeAndOverrideableColor) contactWrappers
					.next();
			final HiResDate dtg = fw.getDTG();
			if ((dtg.greaterThanOrEqualTo(start))
					&& (dtg.lessThanOrEqualTo(end))) {
				fw.setVisible(true);
			} else {
				fw.setVisible(false);
			}
		}

		// do we have any property listeners?
		if (getSupport() != null) {
			final Debrief.GUI.Tote.StepControl.somePeriod newPeriod = new Debrief.GUI.Tote.StepControl.somePeriod(
					start, end);
			getSupport().firePropertyChange(
					MWC.GenericData.WatchableList.FILTERED_PROPERTY, null,
					newPeriod);
		}

	}

	/**
	 * find out the symbol to use for plotting this list in Snail mode
	 */
	public final MWC.GUI.Shapes.Symbols.PlainSymbol getSnailShape() {
		return null;
	}

	
	/**
	 * a specialist linear interpolator class that allows quick (repeated)
	 * interpolations for a set of similar items
	 * 
	 * @author ianmayo
	 * 
	 */
	protected class LinearInterpolator
	{

		private double _startTime;
		private double _desiredTime;
		private long _timeDelta;

		/**
		 * Prepare the temporal domain data
		 */
		public LinearInterpolator(Watchable startValue, Watchable endValue,
				double desiredTime)
		{
			_desiredTime = desiredTime;
			_startTime = startValue.getTime().getMicros();
			_timeDelta = endValue.getTime().getMicros()
					- startValue.getTime().getMicros();
		}

		/**
		 * Return the interpolated value in the supplied domain.
		 */
		public double interp(double startVariable, double endVariable)
		{
			double gradient = (endVariable - startVariable) / (_timeDelta);
			return startVariable + (_desiredTime - _startTime) * gradient;
		}
	}
	// //////////////////////////////////////////////////////////////////
	// embedded class to allow us to pass the local iterator (Iterator) used
	// internally
	// outside as an Enumeration
	// /////////////////////////////////////////////////////////////////
	/**
   *
   */
	public static final class IteratorWrapper implements
			java.util.Enumeration<Editable> {
		/**
		 * java.util.Iterator _val
		 */
		private final java.util.Iterator<Editable> _val;

		/**
		 * <init>
		 * 
		 * @param iterator
		 *            parameter for <init>
		 */
		public IteratorWrapper(final java.util.Iterator<Editable> iterator) {
			_val = iterator;
		}

		/**
		 * hasMoreElements
		 * 
		 * @return the returned boolean
		 */
		public final boolean hasMoreElements() {
			return _val.hasNext();

		}

		/**
		 * nextElement
		 * 
		 * @return the returned Object
		 */
		public final Editable nextElement() {
			return _val.next();
		}
	}

	// //////////////////////////////////////////////////////////////////////////
	// embedded class, used for editing the projection
	// //////////////////////////////////////////////////////////////////////////

}
