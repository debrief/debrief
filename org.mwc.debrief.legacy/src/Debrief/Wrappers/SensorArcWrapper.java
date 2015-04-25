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

import java.awt.Color;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.SortedSet;

import Debrief.GUI.Tote.Painters.SnailDrawTacticalContact.PlottableWrapperWithTimeAndOverrideableColor;
import MWC.GUI.Editable;
import MWC.GUI.FireReformatted;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.MessageProvider;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;

@SuppressWarnings("serial")
public class SensorArcWrapper extends TacticalDataWrapper implements Cloneable
{
	/**
	 * more optimisatons
	 */
	transient private SensorArcContactWrapper nearestContact;

	/**
	 * the (optional) sensor offset value, indicating the forward/backward offset
	 * compared to the attack datum of the platform.
	 */
	private WorldDistance.ArrayLength _sensorOffset = new WorldDistance.ArrayLength(
			0);

	/**
	 * the (optional) indicator for whether the centre of this sensor is in a
	 * straight line fwd/backward of the attack datum, or whether it's a dragged
	 * sensor that follows the track of it's host platform (like a towed array).
	 */
	private boolean _wormInHole = true;

	// //////////////////////////////////////
	// constructors
	/**
	 * ////////////////////////////////////////
	 */
	public SensorArcWrapper(final String title)
	{
		super(title);
	}

	/**
	 * create a copy of the supplied sensor wrapper
	 * 
	 * @param other
	 *          wrapper to copy
	 */
	public SensorArcWrapper(final SensorArcWrapper other)
	{
		super(other.getName());
		this.setTrackName(other.getTrackName());
		this.setHost(other.getHost());
		this.setColor(other.getColor());
		this.setVisible(other.getVisible());
		this.setWormInHole(other.getWormInHole());
		this.setSensorOffset(other.getSensorOffset());
		this.setLineThickness(other.getLineThickness());
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
			final java.util.Iterator<Editable> it = this._myContacts.iterator();
			while (it.hasNext())
			{
				final SensorArcContactWrapper fw = (SensorArcContactWrapper) it.next();

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
			_myContacts.add(plottable);

			final SensorArcContactWrapper scw = (SensorArcContactWrapper) plottable;

			// maintain our time period
			if (_timePeriod == null)
				_timePeriod = new MWC.GenericData.TimePeriod.BaseTimePeriod(
						scw.getDTG(), scw.getDTG());
			else
				_timePeriod.extend(scw.getDTG());

			// and tell the contact about us
			scw.setSensor(this);
		}
	}

	public final void append(final Layer theLayer)
	{
		if (theLayer instanceof SensorArcWrapper)
		{
			final SensorArcWrapper other = (SensorArcWrapper) theLayer;
			final SortedSet<Editable> otherC = other._myContacts;
			for (final Iterator<Editable> iterator = otherC.iterator(); iterator.hasNext();)
			{
				final SensorArcContactWrapper thisC = (SensorArcContactWrapper) iterator.next();
				this.add(thisC);
			}

			// and clear him out...
			otherC.clear();
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
		return "SensorArc:" + getName() + " (" + _myContacts.size() + " items)";
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
		MWC.GenericData.Watchable[] res = new MWC.GenericData.Watchable[]
		{};

		// check that we do actually contain some data
		if (_myContacts.size() == 0)
			return res;

		// see if this is the DTG we have just requestsed
		if ((DTG.equals(lastDTG)) && (lastContact != null))
		{
			res = lastContact;
		}
		else
		{
			// see if this DTG is inside our data range
			// in which case we will just return null
			final SensorArcContactWrapper theFirst = (SensorArcContactWrapper) _myContacts
					.first();
			final SensorArcContactWrapper theLast = (SensorArcContactWrapper) _myContacts
					.last();

			if ((DTG.greaterThanOrEqualTo(theFirst.getDTG()))
					&& (DTG.lessThanOrEqualTo(theLast.getDTG())))
			{
				// yes it's inside our data range, find the first fix
				// after the indicated point

				// see if we have to create our local temporary fix
				if (nearestContact == null)
				{
					nearestContact = new SensorArcContactWrapper(null, DTG, 
	        		null, 
	        		0, 0, 0, 0,
	        		0, 0, 0, 0,
	        		false,
	        		null,
	        		0, getName());
				}
				else
					nearestContact.setDTG(DTG);

				// get the data..
				final java.util.Vector<SensorArcContactWrapper> list = new java.util.Vector<SensorArcContactWrapper>(
						0, 1);
				boolean finished = false;
				final java.util.Iterator<Editable> it = _myContacts.iterator();
				while ((it.hasNext()) && (!finished))
				{
					final SensorArcContactWrapper scw = (SensorArcContactWrapper) it.next();
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
					final MWC.GenericData.Watchable[] dummy = new MWC.GenericData.Watchable[]
					{ null };
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
				final MWC.GenericData.Watchable[] dummy = new MWC.GenericData.Watchable[]
				{ null };
				res = list.toArray(dummy);
			}

			// and remember this fix
			lastContact = res;
			lastDTG = DTG;
		}

		return res;

	}

	/**
	 * get the parent's color Note: we're wrapping the color paramter with
	 * defaultColor so that we can provide more understable attribute names in
	 * property editor
	 * 
	 * @return
	 */
	public Color getDefaultColor()
	{
		return super.getColor();
	}

	/**
	 * just pass the property onto the parent
	 * 
	 * @param defaultColor
	 */
	@FireReformatted
	public void setDefaultColor(final Color defaultColor)
	{
		super.setColor(defaultColor);
	}

	// ///////////////////////////////////////////
	// constructor
	// ///////////////////////////////////////////

	public Boolean getWormInHole()
	{
		return _wormInHole;
	}

	public void setWormInHole(final Boolean wormInHole)
	{
		// see if this is a changed setting
		if (wormInHole != _wormInHole)
		{
			// remember the new value
			_wormInHole = wormInHole;

			// we've got to recalculate our positions now, really.
			clearChildOffsets();
		}
	}

	public WorldDistance.ArrayLength getSensorOffset()
	{
		return _sensorOffset;
	}

	public void setSensorOffset(final WorldDistance.ArrayLength sensorOffset)
	{
		_sensorOffset = sensorOffset;

		if (_sensorOffset != null)
		{
			clearChildOffsets();
		}
	}

	/**
	 * our parent has changed, clear data that depends on it
	 * 
	 */
	private void clearChildOffsets()
	{
		// we also need to reset the origins on our child elements, since
		// the offset will have changed
		final java.util.Iterator<Editable> it = this._myContacts.iterator();
		while (it.hasNext())
		{
			final SensorArcContactWrapper fw = (SensorArcContactWrapper) it.next();
			fw.clearCalculatedOrigin();

			// and tell it we're the boss
			fw.setSensor(this);
		}
	}

	/**
	 * override the parent method - since we want to reset the origin for our
	 * child sensor data items
	 */
	public void setHost(final TrackWrapper host)
	{
		super.setHost(host);

		// and clear offsets
		clearChildOffsets();
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
				final PropertyDescriptor[] res =
				{
						prop("Name", "the name for this sensor"),
						prop("Visible", "whether this sensor data is visible"),
						prop("LineThickness", "the thickness to draw these sensor lines"),
						prop("DefaultColor",
								"the default colour to plot this set of sensor data"),
						prop("SensorOffset",
								"the forward/backward offset (m) of this sensor from the attack datum"),
						prop(
								"WormInHole",
								"whether the origin of this sensor is offset in straight line, or back along the host track"),
			 };

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
	public static int mergeSensors(final Editable targetE, final Layers theLayers,
			final Layer parent, final Editable[] subjects)
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
	protected PlottableWrapperWithTimeAndOverrideableColor createItem(
			PlottableWrapperWithTimeAndOverrideableColor last,
			PlottableWrapperWithTimeAndOverrideableColor next,
			LinearInterpolator interp2, long tNow)
	{
		// TODO Auto-generated method stub
		return null;
	}

}
