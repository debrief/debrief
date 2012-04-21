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

		public SplittableInfo(SplittableLayer data)
		{
			super(data);
		}

		@Override
		@SuppressWarnings("rawtypes")
		public MethodDescriptor[] getMethodDescriptors()
		{
			// just add the reset color field first
			Class c = SplittableLayer.class;
			MethodDescriptor mds[] = super.getMethodDescriptors();

			MethodDescriptor newMeds[] = new MethodDescriptor[]
			{ method(c, "AutoSplitTracks", null, "Automatically separate into tracks") };

			MethodDescriptor resMeds[] = new MethodDescriptor[mds.length
					+ newMeds.length];
			System.arraycopy(mds, 0, resMeds, 0, mds.length);
			System.arraycopy(newMeds, 0, resMeds, mds.length, newMeds.length);

			return resMeds;
		}

	}

	private static final long DELTA_LIMIT = 1000 * 60 * 15; // 15 mins

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public SplittableLayer(boolean b)
	{
		super(b);
	}

	@FireExtended
	public void AutoSplitTracks()
	{
		// remember any layers we want to add/remove after we've looped through
		Vector<SensorWrapper> toAdd = new Vector<SensorWrapper>(0, 1);
		Vector<SensorWrapper> toDitch = new Vector<SensorWrapper>(0, 1);

		// ok, loop through my children, splitting them
		Enumeration<Editable> iter = this.elements();
		while (iter.hasMoreElements())
		{
			SensorWrapper thisWrapper = (SensorWrapper) iter.nextElement();

			// get ready to store these items
			final Vector<SensorWrapper> childLayers = new Vector<SensorWrapper>();

			SensorWrapper newTarget = new SensorWrapper(thisWrapper);
			childLayers.add(newTarget);

			// ok, start looping through this wrapper
			SensorContactWrapper lastCut = null;
			Enumeration<Editable> theseCuts = thisWrapper.elements();
			while (theseCuts.hasMoreElements())
			{
				final SensorContactWrapper thisCut = (SensorContactWrapper) theseCuts
						.nextElement();

				// do we have a last one
				boolean newTrack = false;
				if (lastCut != null)
				{
					// what's the delta
					long delta = thisCut.getTime().getDate().getTime()
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
				Iterator<SensorWrapper> newOnes = childLayers.iterator();
				int ctr = 1;
				while (newOnes.hasNext())
				{
					SensorWrapper thisOne = newOnes.next();
					thisOne.setName(thisWrapper.getName() + "_" + ctr++);
					toAdd.add(thisOne);
				}
			}

		}
		// we've looped through, now do the tidying
		Iterator<SensorWrapper> ditches = toDitch.iterator();
		while (ditches.hasNext())
		{
			SensorWrapper sensorWrapper = ditches.next();
			this.removeElement(sensorWrapper);
		}
		Iterator<SensorWrapper> adds = toAdd.iterator();
		while (adds.hasNext())
		{
			SensorWrapper sensorWrapper = adds.next();
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
