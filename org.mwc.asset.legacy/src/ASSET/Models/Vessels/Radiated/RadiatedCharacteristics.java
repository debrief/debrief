
package ASSET.Models.Vessels.Radiated;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.Comparator;
import java.util.Iterator;
import java.util.TreeSet;
import java.util.Vector;

import ASSET.Models.MWCModel;
import ASSET.Models.Environment.CoreEnvironment;
import MWC.GUI.Editable;

/*******************************************************************************
 * Debrief - the Open Source Maritime Analysis Application http://debrief.info
 *
 * (C) 2000-2020, Deep Blue C Technology Ltd
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html)
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.
 *******************************************************************************/

public class RadiatedCharacteristics implements MWC.GUI.Editable, java.io.Serializable {

	//////////////////////////////////////////////////////////////////////
	// member variables
	//////////////////////////////////////////////////////////////////////

	/**
	 * class which stores a list of noise components for a particular bearing
	 */
	static class BearingSet extends TreeSet<NoiseComponent> {
		/**
		 *
		 */
		private static final long serialVersionUID = 1L;

		static double interpolateBetween(final NoiseComponent before, final NoiseComponent after,
				final double atSpeed) {
			double res = 0;

			// just check if we have the same noise component twice
			if (before == after) {
				res = before.getValue();
			} else {
				// we'll have to interpolate
				// find the value delta
				final double vDelta = after.getValue() - before.getValue();

				// find the speed delta
				final double sDelta = after.getSpeed() - before.getSpeed();

				final double proportion = (atSpeed - before.getSpeed()) / sDelta;

				res = before.getValue() + proportion * vDelta;

			}

			return res;
		}

		////////////////////////////////////////////////////
		// constructor
		////////////////////////////////////////////////////

		/**
		 * the bearing which this set is for
		 */
		private double _myBearing;

		public BearingSet(final double myBearing) {
			super(new Comparator<NoiseComponent>() {
				/**
				 * compare the speeds of the supplied items
				 */
				@Override
				public int compare(final NoiseComponent first, final NoiseComponent second) {
					int res = 0;
					if (first.getSpeed() < second.getSpeed())
						res = -1;
					else if (first.getSpeed() > second.getSpeed())
						res = 1;
					else
						res = 0;

					return res;
				}
			});

			_myBearing = myBearing;
		}

		/**
		 * retrieve the bearing for this set of data
		 *
		 * @return the bearing for this data
		 */
		public double getBearing() {
			return _myBearing;
		}

		public double valueFor(final double speed) {
			// find the noise value at this particular speed
			double res = -1;

			final Iterator<NoiseComponent> it = this.iterator();
			NoiseComponent before = null;
			NoiseComponent after = null;
			while (it.hasNext()) {
				final NoiseComponent thisN = it.next();

				// just initialise our values
				if (before == null) {
					before = thisN;
				}

				// we've already got some data, see if we've passed our required value yet
				if (thisN.getSpeed() >= speed) {
					// yes, we've passed what we're looking for, and we have components
					// either side of it
					after = thisN;
					break;
				}
				// no, we still haven't reached the required speed, this value
				// becomes the "before" value
				before = thisN;
			}

			// just check we found a value (to handle where the requested speed is
			// greater than those supplied
			if (after == null) {
				after = before;
			}

			// do the interpolation
			res = interpolateBetween(before, after, speed);

			return res;
		}
	}

	//////////////////////////////////////////////////////////////////////
	// interface for mediums we handle
	//////////////////////////////////////////////////////////////////////
	static public interface Medium extends MWCModel {
		public String getName();

		public double radiatedEnergyFor(ASSET.Participants.Status status, double absBearingDegs);

		public double reflectedEnergyFor(ASSET.Participants.Status status, double absBearingDegs);
	}

	static public class NoiseComponent {
		////////////////////////////////////////////////////
		// member components
		////////////////////////////////////////////////////
		private final double _bearing;
		private final double _speed;
		private final double _value;

		////////////////////////////////////////////////////
		// constructor
		////////////////////////////////////////////////////

		/**
		 * constructor for a noise component
		 *
		 * @param bearing - the relative bearing (+/- 180 degs)
		 * @param speed   - the speed at which this datum was recorded (kts)
		 * @param value   - the radiated noise (medium-specific units)
		 */
		public NoiseComponent(final double bearing, final double speed, final double value) {
			_bearing = bearing;
			_speed = speed;
			_value = value;
		}

		/**
		 * @return the relative bearing (+/- 180 degs)
		 */
		public double getBearing() {
			return _bearing;
		}

		/**
		 * @return the speed at which this datum was recorded (kts)
		 */
		public double getSpeed() {
			return _speed;
		}

		/**
		 * radiated noise value
		 *
		 * @return the radiated noise (medium-specific units)
		 */
		public double getValue() {
			return _value;
		}
	}

	////////////////////////////////////////////////////
	// class which builds up a list of noise components, and is able to interpolate
	//////////////////////////////////////////////////// between them
	// todo - create import/export handlers for these.
	////////////////////////////////////////////////////
	public static class NoiseSignature extends TreeSet<BearingSet> {
		////////////////////////////////////////////////////
		// member objects
		////////////////////////////////////////////////////

		////////////////////////////////////////////////////
		// constructor
		////////////////////////////////////////////////////

		/**
		 *
		 */
		private static final long serialVersionUID = 1L;

		public NoiseSignature() {
			super(new Comparator<BearingSet>() {
				@Override
				public int compare(final BearingSet first, final BearingSet second) {
					int res = 0;
					if (first.getBearing() < second.getBearing())
						res = -1;
					else if (first.getBearing() > second.getBearing())
						res = 1;
					else
						res = 0;

					return res;
				}
			});
		}

		////////////////////////////////////////////////////
		// member methods
		////////////////////////////////////////////////////

		/**
		 * add the indicated noise component to our data
		 */
		public void addComponent(final NoiseComponent component) {

			BearingSet thisList = null;

			// retrieve the bearing
			final double brg = component.getBearing();

			// find out if we already store data for this bearing
			final Iterator<BearingSet> it = this.iterator();
			while (it.hasNext()) {
				final BearingSet bs = it.next();
				if (bs.getBearing() == brg) {
					thisList = bs;
					break;
				}
			}

			// did we find it?
			if (thisList == null) {
				thisList = new BearingSet(brg);

				// put this list into the big object
				this.add(thisList);
			}

			// now add the component
			thisList.add(component);
		}

		public double getValueFor(final double bearing, final double speed) {

			final Iterator<BearingSet> it = this.iterator();
			BearingSet nearestBearing = null;
			double nearestDelta = -1;
			while (it.hasNext()) {
				final BearingSet thisN = it.next();

				// how far is this data value from our required one?
				final double thisDelta = Math.abs(thisN.getBearing() - bearing);

				// just initialise our values
				if (nearestBearing == null) {
					nearestBearing = thisN;
					nearestDelta = thisDelta;
				} else {
					// see if this delta is nearer than our existing one
					if (thisDelta < nearestDelta) {
						nearestDelta = thisDelta;
						nearestBearing = thisN;
					} else {
						// we must have passed it, drop out
						break;
					}
				}
			}

			double res = 0;

			if (nearestBearing != null) {
				// get the value from this list
				res = nearestBearing.valueFor(speed);
			}

			// done
			return res;

		}
	}

	/**
	 * ************************************************* radiated noise levels
	 * *************************************************
	 */
	static public class RadiatedInfo extends MWC.GUI.Editable.EditorType {

		/**
		 * constructor for editable details of a set of Layers
		 *
		 * @param data the Layers themselves
		 */
		public RadiatedInfo(final RadiatedCharacteristics data) {
			super(data, data.getName(), data.getName());
		}

		/**
		 * return a description of this bean, also specifies the custom editor we use
		 *
		 * @return the BeanDescriptor
		 */
		@Override
		public java.beans.BeanDescriptor getBeanDescriptor() {
			final java.beans.BeanDescriptor bp = new java.beans.BeanDescriptor(RadiatedCharacteristics.class,
					ASSET.GUI.Editors.RadiatedNoiseViewer.class);
			bp.setDisplayName(super.getData().toString());
			return bp;
		}

		/**
		 * editable GUI properties for our participant
		 *
		 * @return property descriptions
		 */
		@Override
		public PropertyDescriptor[] getPropertyDescriptors() {
			try {
				final PropertyDescriptor[] res = { prop("Name", "the name of this set of radiated characteristics"), };
				return res;
			} catch (final IntrospectionException e) {
				e.printStackTrace();
				return super.getPropertyDescriptors();
			}
		}
	}

	//////////////////////////////////////////////////////////////////////
	// member methods
	//////////////////////////////////////////////////////////////////////

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * the set of mediums we know about
	 */
	private final Medium[] _myMediums = new Medium[CoreEnvironment.MAX_NUM_MEDIUMS];

	/**
	 * the editor for radiated noise levels
	 */
	private MWC.GUI.Editable.EditorType _myEditor = null;

	/**
	 * the type of noise we represent
	 */
	private String _myNoiseType;

	public RadiatedCharacteristics() {
		this("Radiated Noise");
	}

	RadiatedCharacteristics(final String type) {
		_myNoiseType = type;
	}

	public void add(final int index, final Medium medium) {
		// try to remove any existing medium
		_myMediums[index] = medium;
	}

	/**
	 * get the editor for this item
	 *
	 * @return the BeanInfo data for this editable object
	 */
	@Override
	public Editable.EditorType getInfo() {
		if (_myEditor == null)
			_myEditor = new RadiatedInfo(this);

		return _myEditor;
	}

	public Medium getMedium(final Integer index) {
		return _myMediums[index.intValue()];
	}

	public java.util.Collection<Medium> getMediums() {
		final Vector<Medium> res = new Vector<Medium>();
		for (int i = 0; i < _myMediums.length; i++) {
			final Medium medium = _myMediums[i];
			if (medium != null)
				res.add(medium);
		}
		return res;
	}

	/****************************************************
	 * editor support
	 ***************************************************/
	/**
	 * the name of this object
	 *
	 * @return the name of this editable object
	 */
	@Override
	public String getName() {
		return _myNoiseType;
	}

	/**
	 * whether there is any edit information for this item this is a convenience
	 * function to save creating the EditorType data first
	 *
	 * @return yes/no
	 */
	@Override
	public boolean hasEditor() {
		return true;
	}

	public double radiatedEnergyFor(final int medium, final ASSET.Participants.Status status,
			final double absBearingDegs) {
		double res = 0;

		if (radiatesThis(medium)) {
			final Medium thisMed = _myMediums[medium];
			if (thisMed != null)
				res = thisMed.radiatedEnergyFor(status, absBearingDegs);
		} else
			res = 0;

		return res;
	}

	public boolean radiatesThis(final int medium) {
		// see if the vessel itself radiates this
		final boolean vesselRadiates = _myMediums[medium] != null;

		// and return it
		return vesselRadiates;
	}

	public void setName(final String name) {
		_myNoiseType = name;
	}

	@Override
	public String toString() {
		return getName();
	}

}