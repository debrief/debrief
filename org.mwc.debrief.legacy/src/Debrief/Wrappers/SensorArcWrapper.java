/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 */
package Debrief.Wrappers;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Enumeration;

import Debrief.Wrappers.SensorArcContactWrapper.SensorArcValue;
import MWC.GUI.CanvasType;
import MWC.GUI.Editable;
import MWC.GUI.FireReformatted;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.MessageProvider;
import MWC.GUI.MovingPlottable;
import MWC.GUI.Plottables;
import MWC.GenericData.HiResDate;
import MWC.GenericData.TimePeriod;
import MWC.GenericData.TimePeriod.BaseTimePeriod;
import MWC.GenericData.WatchableList;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldLocation;

public class SensorArcWrapper extends Plottables implements Cloneable,
		MovingPlottable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * more optimisatons
	 */
	transient private SensorArcContactWrapper nearestContact;

	private String _myName;

	/**
	 * our editor
	 */
	protected transient MWC.GUI.Editable.EditorType _myEditor;

	private int _lineWidth;

	private TrackWrapper _myHost;

	private BaseTimePeriod _timePeriod;


	// //////////////////////////////////////
	// constructors
	/**
	 * ////////////////////////////////////////
	 */
	public SensorArcWrapper(final String title)
	{
		_myName = title;
	}

	// //////////////////////////////////////
	// member methods to meet plain wrapper responsibilities
	// //////////////////////////////////////

	/**
	 * the real getBounds object, which uses properties of the parent
	 */
	public final MWC.GenericData.WorldArea getBounds()
	{
		// we no longer just return the bounds of the track, because a portion
		// of the track may have been made invisible.
		// instead, we will pass through the full dataset and find the outer
		// bounds of the visible area
		WorldArea res = null;

		if (!getVisible())
		{
			// hey, we're invisible, return null
		}
		else
		{
			final Enumeration<Editable> it = this.elements();
			while (it.hasMoreElements())
			{
				final SensorArcContactWrapper fw = (SensorArcContactWrapper) it
						.nextElement();

				// is this point visible?
				if (fw.getVisible())
				{

					// has our data been initialised?
					if (res == null)
					{
						// no, initialise it
						final WorldLocation startOfLine = fw.getCalculatedOrigin(_myHost);

						// we may not have a sensor-data origin, since the
						// sensor may be out of the time period of the track
						if (startOfLine != null)
							res = new WorldArea(startOfLine, startOfLine);
					}
					else
					{
						// yes, extend to include the new area
						res.extend(fw.getCalculatedOrigin(_myHost));
					}

				}
			}
		}

		return res;
	}

	/**
	 * getInfo
	 * 
	 * @return the returned MWC.GUI.Editable.EditorType
	 */
	public final MWC.GUI.Editable.EditorType getInfo()
	{
		if (_myEditor == null)
			_myEditor = new SensorInfo(this);

		return _myEditor;
	}

	/**
	 * add
	 * 
	 * @param plottable
	 *          parameter for add
	 */
	public final void add(final MWC.GUI.Editable plottable)
	{
		// check it's a sensor contact entry
		if (plottable instanceof SensorArcContactWrapper)
		{
			super.add(plottable);

			final SensorArcContactWrapper scw = (SensorArcContactWrapper) plottable;

			// maintain our time period
			if (_timePeriod == null)
				_timePeriod = new MWC.GenericData.TimePeriod.BaseTimePeriod(
						scw.getStartDTG(), scw.getEndDTG());
			else
			{
				if (scw.getDTG() != null)
				{
					_timePeriod.extend(scw.getDTG());
				}
			}

			// and tell the contact about us
			scw.setSensor(this);
		}
	}

	public final void append(final Layer theLayer)
	{
		if (theLayer instanceof SensorArcWrapper)
		{
			final SensorArcWrapper other = (SensorArcWrapper) theLayer;

			final Enumeration<Editable> it = other.elements();
			while (it.hasMoreElements())
			{
				final SensorArcContactWrapper fw = (SensorArcContactWrapper) it
						.nextElement();
				this.add(fw);
			}

			// and clear him out...
			other.removeAllElements();
		}
	}

	public boolean hasOrderedChildren()
	{
		return false;
	}

	// ///////////////////////////////////////
	// other member functions
	// ///////////////////////////////////////

	/**
   */

	public final String toString()
	{
		return "Coverage Arc:" + getName() + " (" + size() + " items)";
	}

	/**
	 * how far away are we from this point? or return null if it can't be
	 * calculated
	 */
	public final double rangeFrom(final WorldLocation other)
	{
		double res = INVALID_RANGE;

		// if we have a nearest contact, see how far away it is.
		if (nearestContact != null)
			res = nearestContact.rangeFrom(other);

		return res;
	}

	// /////////////////////////////////////////////////////////////////
	// support for WatchableList interface (required for Snail Trail plotting)
	// //////////////////////////////////////////////////////////////////

	/**
	 * get the watchable in this list nearest to the specified DTG - we take most
	 * of this processing from the similar method in TrackWrappper. If the DTG is
	 * after our end, return our last point
	 * 
	 * @param DTG
	 *          the DTG to search for
	 * @return the nearest Watchable
	 */
	public final MWC.GenericData.Watchable[] getNearestTo(final HiResDate DTG)
	{

		/**
		 * we need to end up with a watchable, not a fix, so we need to work our way
		 * through the fixes
		 */
		MWC.GenericData.Watchable[] res = new MWC.GenericData.Watchable[] {};

		// check that we do actually contain some data
		if (size() == 0)
			return res;

		// see if this DTG is inside our data range
		// in which case we will just return null
		final SensorArcContactWrapper theFirst = (SensorArcContactWrapper) first();
		final SensorArcContactWrapper theLast = (SensorArcContactWrapper) last();

		if ((DTG.greaterThanOrEqualTo(theFirst.getDTG()))
				&& (DTG.lessThanOrEqualTo(theLast.getDTG())))
		{
			// yes it's inside our data range, find the first fix
			// after the indicated point

			// see if we have to create our local temporary fix
			if (nearestContact == null)
			{
				nearestContact = new SensorArcContactWrapper(null, DTG, null,
						new ArrayList<SensorArcValue>(), null, 0, getName());
			}
			else
				nearestContact.setDTG(DTG);

			// get the data..
			final java.util.Vector<SensorArcContactWrapper> list = new java.util.Vector<SensorArcContactWrapper>(
					0, 1);
			boolean finished = false;

			final Enumeration<Editable> it = this.elements();
			while ((it.hasMoreElements()) && (!finished))
			{
				final SensorArcContactWrapper scw = (SensorArcContactWrapper) it
						.nextElement();
				final HiResDate thisDate = scw.getTime();
				if (thisDate.lessThan(DTG))
				{
					// before it, ignore!
				}
				else if (thisDate.greaterThan(DTG))
				{
					// hey, it's a possible - if we haven't found an exact
					// match
					if (list.size() == 0)
					{
						list.add(scw);
					}
					else
					{
						// hey, we're finished!
						finished = true;
					}
				}
				else
				{
					// hey, it must be at the same time!
					list.add(scw);
				}

			}

			if (list.size() > 0)
			{
				final MWC.GenericData.Watchable[] dummy = new MWC.GenericData.Watchable[] { null };
				res = list.toArray(dummy);
			}
		}
		else if (DTG.greaterThanOrEqualTo(theLast.getDTG()))
		{
			// is it after the last one? If so, just plot the last one. This
			// helps us when we're doing snail trails.
			final java.util.Vector<SensorArcContactWrapper> list = new java.util.Vector<SensorArcContactWrapper>(
					0, 1);
			list.add(theLast);
			final MWC.GenericData.Watchable[] dummy = new MWC.GenericData.Watchable[] { null };
			res = list.toArray(dummy);
		}

		return res;

	}


	// //////////////////////////////////////////////////////////////////////////
	// embedded class, used for editing the projection
	// //////////////////////////////////////////////////////////////////////////
	/**
	 * the definition of what is editable about this object
	 */
	public final class SensorInfo extends Editable.EditorType
	{

		/**
		 * constructor for editable details of a set of Layers
		 * 
		 * @param data
		 *          the Layers themselves
		 */
		public SensorInfo(final SensorArcWrapper data)
		{
			super(data, data.getName(), "Sensor");
		}

		/**
		 * The things about these Layers which are editable. We don't really use
		 * this list, since we have our own custom editor anyway
		 * 
		 * @return property descriptions
		 */
		public final PropertyDescriptor[] getPropertyDescriptors()
		{
			try
			{
				final PropertyDescriptor[] res = {
						prop("Name", "the name for this sensor"),
						prop("Visible", "whether this sensor data is visible"),
						prop("LineThickness", "the thickness to draw these sensor lines")};

				res[2]
						.setPropertyEditorClass(MWC.GUI.Properties.LineWidthPropertyEditor.class);

				return res;
			}
			catch (final IntrospectionException e)
			{
				e.printStackTrace();
				return super.getPropertyDescriptors();
			}
		}
	}

	// ////////////////////////////////////////////////////
	// nested class for testing
	// /////////////////////////////////////////////////////

	static public final class testSensors extends junit.framework.TestCase
	{
		static public final String TEST_ALL_TEST_TYPE = "UNIT";

		public testSensors(final String val)
		{
			super(val);
		}

		public final void testValues()
		{

		}

	}

	public static void main(final String[] args)
	{
		final testSensors ts = new testSensors("Ian");
		ts.testValues();
	}

	public Editable getSampleGriddable()
	{
		Editable res = null;

		// check we have an item before we edit it
		final Enumeration<Editable> eles = this.elements();
		if (eles.hasMoreElements())
			res = eles.nextElement();
		return res;
	}

	/**
	 * perform a merge of the supplied tracks.
	 * 
	 * @param target
	 *          the final recipient of the other items
	 * @param theLayers
	 * @param parent
	 *          the parent tracks for the supplied items
	 * @param subjects
	 *          the actual selected items
	 * @return sufficient information to undo the merge
	 */
	public static int mergeSensors(final Editable targetE,
			final Layers theLayers, final Layer parent, final Editable[] subjects)
	{
		final SensorArcWrapper target = (SensorArcWrapper) targetE;

		for (int i = 0; i < subjects.length; i++)
		{
			final SensorArcWrapper sensor = (SensorArcWrapper) subjects[i];
			if (sensor != target)
			{
				// ok, append the items in this layer to the target
				target.append(sensor);
				parent.removeElement(sensor);
			}
		}

		return MessageProvider.OK;
	}

	@Override
	public void paint(CanvasType canvas, long time)
	{
		if (!getVisible())
			return;

		// remember the current line width
		final float oldLineWidth = canvas.getLineWidth();

		// set the line width
		canvas.setLineWidth(getLineThickness());

		// trigger our child sensor contact data items to plot themselves
		final Enumeration<Editable> it = this.elements();
		while (it.hasMoreElements())
		{
			final SensorArcContactWrapper con = (SensorArcContactWrapper) it
					.nextElement();

			HiResDate dtg = new HiResDate(time);

			if (con.getStartDTG() != null && dtg.lessThan(con.getStartDTG()))
				continue;
			if (con.getEndDTG() != null && dtg.greaterThanOrEqualTo(con.getEndDTG()))
				continue;
			// ok, plot it - and don't make it keep it simple, lets really go
			// for it man!
			con.setDTG(dtg);
			con.paint(_myHost, canvas, false);
		}

		// and restore the line width
		canvas.setLineWidth(oldLineWidth);

	}

	@Override
	public void paint(CanvasType dest)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public String getName()
	{
		return _myName;
	}

	@Override
	public boolean hasEditor()
	{
		return true;
	}

	/**
	 * the line thickness (convenience wrapper around width)
	 * 
	 * @return
	 */
	public final int getLineThickness()
	{
		return _lineWidth;
	}

	/**
	 * the line thickness (convenience wrapper around width)
	 */
	@FireReformatted
	public final void setLineThickness(final int val)
	{
		_lineWidth = val;
	}

	/**
	 * set our host track
	 */
	public void setHost(final TrackWrapper host)
	{
		_myHost = host;
	}

	public WatchableList getHost()
	{
		return _myHost;
	}
	
	public void trimTo(TimePeriod period)
	{
		java.util.SortedSet<Editable> newList = new java.util.TreeSet<Editable>();

		final Enumeration<Editable> it = this.elements();
		while (it.hasMoreElements())
		{
			final SensorArcContactWrapper thisE = (SensorArcContactWrapper) it
					.nextElement();
			if (period.contains(thisE.getTime()))
			{
				newList.add(thisE);
			}
		}

		// ditch the current items
		removeAllElements();
		
		// ok, copy over the matching items
		super.getData().addAll(newList);
	}
}
