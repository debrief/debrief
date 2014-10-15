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
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package Debrief.Wrappers.Track;

import java.beans.MethodDescriptor;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;

import Debrief.Wrappers.SensorContactWrapper;
import Debrief.Wrappers.SensorWrapper;
import MWC.GUI.BaseLayer;
import MWC.GUI.Editable;
import MWC.GUI.FireExtended;

/**
 * class that includes functionality to split it's children if/when they're more
 * than 15 mins from the previous child
 * 
 * @author ianmayo
 * 
 */
public class SplittableLayer extends BaseLayer
{
	public class SplittableInfo extends BaseLayer.LayerInfo
	{

		public SplittableInfo(final SplittableLayer data)
		{
			super(data);
		}

		@Override
		@SuppressWarnings("rawtypes")
		public MethodDescriptor[] getMethodDescriptors()
		{
			// just add the reset color field first
			final Class c = SplittableLayer.class;
			final MethodDescriptor mds[] = super.getMethodDescriptors();

			final MethodDescriptor newMeds[] = new MethodDescriptor[]
			{ method(c, "AutoSplitTracks", null, "Automatically separate into tracks") };

			final MethodDescriptor resMeds[] = new MethodDescriptor[mds.length
					+ newMeds.length];
			System.arraycopy(mds, 0, resMeds, 0, mds.length);
			System.arraycopy(newMeds, 0, resMeds, mds.length, newMeds.length);

			return resMeds;
		}

	}

	private static final long DELTA_LIMIT = 1000 * 60 * 30; // 30 mins

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public SplittableLayer(final boolean orderedChildren)
	{
		super(orderedChildren);
	}

	/** whether this type of BaseLayer is able to have shapes added to it
	 * 
	 * @return
	 */
	@Override
	public boolean canTakeShapes()
	{
		return false;
	}
	
	@FireExtended
	public void AutoSplitTracks()
	{
		// remember any layers we want to add/remove after we've looped through
		final Vector<SensorWrapper> toAdd = new Vector<SensorWrapper>(0, 1);
		final Vector<SensorWrapper> toDitch = new Vector<SensorWrapper>(0, 1);

		// ok, loop through my children, splitting them
		final Enumeration<Editable> iter = this.elements();
		while (iter.hasMoreElements())
		{
			final SensorWrapper thisWrapper = (SensorWrapper) iter.nextElement();

			// get ready to store these items
			final Vector<SensorWrapper> childLayers = new Vector<SensorWrapper>();

			SensorWrapper newTarget = new SensorWrapper(thisWrapper);
			childLayers.add(newTarget);

			// ok, start looping through this wrapper
			SensorContactWrapper lastCut = null;
			final Enumeration<Editable> theseCuts = thisWrapper.elements();
			while (theseCuts.hasMoreElements())
			{
				final SensorContactWrapper thisCut = (SensorContactWrapper) theseCuts
						.nextElement();

				// do we have a last one
				boolean newTrack = false;
				if (lastCut != null)
				{
					// what's the delta
					final long delta = thisCut.getTime().getDate().getTime()
							- lastCut.getTime().getDate().getTime();
					if (delta > DELTA_LIMIT)
					{
						// ok, we've got to split this track into multiple ones
						newTrack = true;
					}
				}

				lastCut = thisCut;

				if (newTrack)
				{
					// ok, create a new target layer
					newTarget = new SensorWrapper(thisWrapper);
					childLayers.add(newTarget);
				}

				// and add this point to the new target
				newTarget.add(thisCut);

			}

			// ok we've been through this track. better break out the children
			if (childLayers.size() > 1)
			{
				// yup, something new got created

				// ditch the current layer from the parent
				toDitch.add(thisWrapper);

				// and insert the new ones
				final Iterator<SensorWrapper> newOnes = childLayers.iterator();
				int ctr = 1;
				while (newOnes.hasNext())
				{
					final SensorWrapper thisOne = newOnes.next();
					thisOne.setName(thisWrapper.getName() + "_" + ctr++);
					toAdd.add(thisOne);
				}
			}

		}
		// we've looped through, now do the tidying
		final Iterator<SensorWrapper> ditches = toDitch.iterator();
		while (ditches.hasNext())
		{
			final SensorWrapper sensorWrapper = ditches.next();
			this.removeElement(sensorWrapper);
		}
		final Iterator<SensorWrapper> adds = toAdd.iterator();
		while (adds.hasNext())
		{
			final SensorWrapper sensorWrapper = adds.next();
			this.add(sensorWrapper);
		}

	}

	@Override
	public Editable.EditorType getInfo()
	{
		if (_myEditor == null)
			_myEditor = new SplittableInfo(this);

		return _myEditor;
	}

}
