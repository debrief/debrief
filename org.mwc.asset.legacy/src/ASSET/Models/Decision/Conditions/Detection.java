/*******************************************************************************
 * Debrief - the Open Source Maritime Analysis Application
 * http://debrief.info
 *
 * (C) 2000-2020, Deep Blue C Technology Ltd
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html)
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *******************************************************************************/

package ASSET.Models.Decision.Conditions;

import ASSET.Models.Decision.TargetType;
import ASSET.Models.Detection.DetectionEvent;
import ASSET.Models.Detection.DetectionList;
import ASSET.Participants.Category;
import ASSET.Participants.Status;
import ASSET.Scenario.ScenarioActivityMonitor;
import MWC.GUI.Editable;
import MWC.GenericData.WorldDistance;

public class Detection extends Condition.CoreCondition {
	////////////////////////////////////////////////////
	// member objects
	////////////////////////////////////////////////////

	/**
	 * ************************************************* editor support
	 * *************************************************
	 */

	static public class DetectionInfo extends Editable.EditorType {

		/**
		 * constructor for editable details of a set of Layers
		 *
		 * @param data the Layers themselves
		 */
		public DetectionInfo(final Detection data) {
			super(data, data.getName(), "Detection Condition");
		}

		/**
		 * editable GUI properties for our participant
		 *
		 * @return property descriptions
		 */
		@Override
		public java.beans.PropertyDescriptor[] getPropertyDescriptors() {
			try {
				final java.beans.PropertyDescriptor[] res = { prop("Name", "the name of this condition"),
						prop("TargetType", "the type of target we're looking for"),
						prop("RangeThreshold", "the minimum range at which to trigger detection"), };
				return res;
			} catch (final java.beans.IntrospectionException e) {
				return super.getPropertyDescriptors();
			}
		}
	}

	////////////////////////////////////////////////////
	// constructor
	////////////////////////////////////////////////////

	/**
	 * the TargetType of target we're looking for
	 */
	private TargetType _theTargetType = null;

	/**
	 * a minimum range to trigger
	 */
	private WorldDistance _rangeThreshold = null;

	/**
	 * @param theTargetType the type of target we're looking for
	 * @param theThreshold  the threshold range at which detections are valid (or
	 *                      null for all detections)
	 */
	public Detection(final TargetType theTargetType, final WorldDistance theThreshold) {
		super("Detection");

		this._theTargetType = theTargetType;
		_rangeThreshold = theThreshold;
	}

	/**
	 * get the editor for this item
	 *
	 * @return the BeanInfo data for this editable object
	 */
	@Override
	public Editable.EditorType getInfo() {
		if (_myEditor == null)
			_myEditor = new DetectionInfo(this);

		return _myEditor;
	}

	public WorldDistance getRangeThreshold() {
		return _rangeThreshold;
	}

	public TargetType getTargetType() {
		return _theTargetType;
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

	////////////////////////////////////////////////////
	// editor fields
	////////////////////////////////////////////////////

	@Override
	public void restart() {
		// don't bother, we don't react to it.
	}

	public void setRangeThreshold(final WorldDistance rangeThreshold) {
		this._rangeThreshold = rangeThreshold;
	}

	public void setTargetType(final TargetType theTargetType) {
		this._theTargetType = theTargetType;
	}

	////////////////////////////////////////////////////
	// condition fields
	////////////////////////////////////////////////////
	@Override
	public Object test(final Status status, final DetectionList detections, final long time,
			final ScenarioActivityMonitor monitor) {

		DetectionEvent res = null;

		double theStrength = -1;

		// loop through the detections
		final int len = detections.size();
		for (int i = 0; i < len; i++) {
			DetectionEvent de = detections.getDetection(i);
			final Category tt = de.getTargetType();
			// see if this matches our category
			if (_theTargetType.matches(tt)) {
				// are we checking range?
				if (_rangeThreshold != null) {
					// yes, checking range. Do we know his range?
					if (de.getRange() != null) {
						final double rng_degs = de.getRange().getValueIn(WorldDistance.DEGS);

						// is this within the threshold?
						if (rng_degs <= _rangeThreshold.getValueIn(WorldDistance.DEGS)) {
							// ok, continue as we are
						} else {
							// outside range, ignore this contact
							de = null;
						}
					} else {
						// we don't know his range, ignore this contact
						de = null;
					}
				}

				// do we still have a target? We may have ditched it if
				// it's out of range
				if (de != null) {
					// not checking range, just continue

					// what's the strength of this detection?
					final float thisStrength = de.getStrength().floatValue();

					// yes, it's ok, is this the strongest?
					if (theStrength == -1) {
						res = de;
						theStrength = thisStrength;
					} else if (thisStrength > theStrength) {
						res = de;
						theStrength = thisStrength;
					}
				}
			}
		}

		// ok, done now
		return res;
	}

}
