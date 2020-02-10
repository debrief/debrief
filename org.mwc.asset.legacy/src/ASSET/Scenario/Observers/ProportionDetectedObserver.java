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

package ASSET.Scenario.Observers;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.HashSet;

import ASSET.ScenarioType;
import ASSET.Models.Decision.TargetType;
import ASSET.Models.Detection.DetectionEvent;
import ASSET.Util.SupportTesting;
import MWC.GUI.Editable;

public class ProportionDetectedObserver extends ASSET.Scenario.Observers.DetectionObserver {
	/***************************************************************
	 * member variables
	 ***************************************************************/

	/**
	 * ************************************************************ embedded class
	 * which lets us keep track of initial detections
	 * *************************************************************
	 */
	private class EntryHolder {
		private final int _target;
		private final int _host;

		public EntryHolder(final int tgt, final int host) {
			_target = tgt;
			_host = host;
		}

		@Override
		public boolean equals(final Object obj) {
			boolean res = false;
			final EntryHolder eh = (EntryHolder) obj;
			res = ((_target == eh._target) && (_host == eh._host));
			return res;
		}

		@Override
		public int hashCode() {
			return _target * 1000 + _host;
		}

	}

	/**
	 * ************************************************************ a gui class to
	 * show progress of this monitor
	 * *************************************************************
	 */

	// ////////////////////////////////////////////////////////////////////////////////////////////////
	// testing for this class
	// ////////////////////////////////////////////////////////////////////////////////////////////////
	public static class PropDetectedTest extends SupportTesting.EditableTesting {
		static public final String TEST_ALL_TEST_TYPE = "UNIT";

		public PropDetectedTest(final String val) {
			super(val);
		}

		/**
		 * get an object which we can test
		 *
		 * @return Editable object which we can check the properties for
		 */
		@Override
		public Editable getEditable() {
			final MWC.GUI.Editable ed = new ProportionDetectedObserver(null, null, "how many", new Integer(2), true);
			return ed;
		}
	}

	// ////////////////////////////////////////////////////
	// bean info for this class
	// ///////////////////////////////////////////////////
	public class ProportionInfo extends Editable.EditorType {

		public ProportionInfo(final ProportionDetectedObserver data, final String name) {
			super(data, name, "");
		}

		@Override
		public String getName() {
			return ProportionDetectedObserver.this.getName();
		}

		@Override
		public PropertyDescriptor[] getPropertyDescriptors() {
			try {
				final PropertyDescriptor[] res = { prop("Name", "the name of this observer"),
						prop("NumDetected", "the number of targets detected"),
						prop("NumTargets", "the number of targets in the scenario"),
						prop("Active", "whether this listener is active"), };
				return res;
			} catch (final IntrospectionException e) {
				e.printStackTrace();
				return super.getPropertyDescriptors();
			}
		}
	}

	/**
	 * a running count of how many targets are in the scenario
	 */
	private int _numTargets = 0;

	private final HashSet<EntryHolder> _validDetections;

	/***************************************************************
	 * member methods
	 ***************************************************************/

	/**
	 * ************************************************************ constructor
	 * *************************************************************
	 */
	public ProportionDetectedObserver(final TargetType watchVessel, final TargetType targetVessel, final String myName,
			final Integer detectionLevel, final boolean isActive) {
		super(watchVessel, targetVessel, myName, detectionLevel, isActive);

		_validDetections = new HashSet<EntryHolder>();
	}

	// ////////////////////////////////////////////////
	// batch processing results
	// ////////////////////////////////////////////////
	/**
	 * return the calculated result for the batch processing
	 *
	 * @return string to be used in results collation
	 */
	@Override
	protected Number getBatchResult() {
		return new Double(getProportionDetected());
	}

	/**
	 * get the editor for this item
	 *
	 * @return the BeanInfo data for this editable object
	 */
	@Override
	public Editable.EditorType getInfo() {
		if (_myEditor == null)
			_myEditor = new ProportionInfo(this, getName());

		return _myEditor;
	}

	/**
	 * how many targets have we deleted?
	 */
	@Override
	public int getNumDetected() {
		int res = 0;
		if (_myDetections != null)
			res = _myDetections.size();

		return res;
	}

	/**
	 * get the total number of targets in the scenario
	 */
	public int getNumTargets() {
		return _numTargets;
	}

	/**
	 * return the proportion of targets which have been detected
	 *
	 * @return 0..1 representing how many detected
	 */
	public double getProportionDetected() {
		double res = 0;
		if (_validDetections != null) {
			if (_numTargets > 0) {
				res = _validDetections.size() / (double) _numTargets;
			}
		}
		return res;
	}

	/***************************************************************
	 * plottable properties
	 ***************************************************************/
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

	/**
	 * the indicated participant has been added to the scenario
	 */
	@Override
	public void newParticipant(final int index) {
		super.newParticipant(index);

		// is this of our target type?
		if (super.getTargetType().matches(_myScenario.getThisParticipant(index).getCategory())) {
			_numTargets++;
		}
	}

	@Override
	protected void performSetupProcessing(final ScenarioType scenario) {
		super.performSetupProcessing(scenario);

		// clear our local list
		_validDetections.clear();

		// right, do we have any targets?
		if (_numTargets == 0) {
			// nope, better process them ourselves
			final Integer[] theParts = scenario.getListOfParticipants();
			for (int i = 0; i < theParts.length; i++) {
				final int thisP = theParts[i];
				newParticipant(thisP);
			}
		}
	}

	/**
	 * set the total number of targets in the scenario
	 */
	public void setNumTargets(final int numTargets) {
		_numTargets = numTargets;
	}

	/**
	 * valid detection happened, process it
	 */
	@Override
	protected void validDetection(final DetectionEvent detection) {
		// let the parent do it's bit
		super.validDetection(detection);

		// remove this target
		final int tgt = detection.getTarget();

		// get sensor which has made the detection
		final int host = detection.getHost();

		final EntryHolder eh = new EntryHolder(tgt, host);

		if ((_validDetections == null) || (_validDetections.contains(eh))) {
		} else {
			// create combined entry
			_validDetections.add(eh);
		}

		final double currentProp = 100 * getProportionDetected();

		// tell the attribute helper
		getAttributeHelper().newData(this.getScenario(), detection.getTime(), ((int) currentProp) + "%");

	}

}
