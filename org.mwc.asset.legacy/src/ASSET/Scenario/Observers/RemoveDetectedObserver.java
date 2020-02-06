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

import java.awt.Color;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.Vector;

import ASSET.NetworkParticipant;
import ASSET.ScenarioType;
import ASSET.Models.Decision.TargetType;
import ASSET.Models.Detection.DetectionEvent;
import ASSET.Participants.Category;
import ASSET.Participants.Status;
import ASSET.Util.SupportTesting;
import Debrief.Wrappers.LabelWrapper;
import MWC.GUI.CanvasType;
import MWC.GUI.Editable;
import MWC.GenericData.WorldLocation;

public class RemoveDetectedObserver extends ASSET.Scenario.Observers.DetectionObserver {
	/**
	 * ************************************************************ a gui class to
	 * show progress of this monitor
	 * *************************************************************
	 */

	// ////////////////////////////////////////////////////////////////////////////////////////////////
	// testing for this class
	// ////////////////////////////////////////////////////////////////////////////////////////////////
	public static class RemDetectedTest extends SupportTesting.EditableTesting {
		static public final String TEST_ALL_TEST_TYPE = "UNIT";

		public RemDetectedTest(final String val) {
			super(val);
		}

		/**
		 * get an object which we can test
		 *
		 * @return Editable object which we can check the properties for
		 */
		@Override
		public Editable getEditable() {
			final MWC.GUI.Editable ed = new RemoveDetectedObserver(null, null, "how many", new Integer(2), true);
			return ed;
		}
	}

	// ////////////////////////////////////////////////////
	// bean info for this class
	// ///////////////////////////////////////////////////
	public class RemoverInfo extends Editable.EditorType {

		public RemoverInfo(final RemoveDetectedObserver data, final String name) {
			super(data, name, "");
		}

		@Override
		public String getName() {
			return RemoveDetectedObserver.this.getName();
		}

		@Override
		public PropertyDescriptor[] getPropertyDescriptors() {
			try {
				final PropertyDescriptor[] res = { prop("Name", "the name of this observer"),
						prop("PlotTheDead", "whether to plot locations of dead contacts"),
						prop("Active", "whether this listener is active"), };
				return res;
			} catch (final IntrospectionException e) {
				System.out.println("::" + e.getMessage());
				return super.getPropertyDescriptors();
			}
		}
	}

	/***************************************************************
	 * member variables
	 ***************************************************************/

	protected int _numDitched = 0;

	private Vector<LabelWrapper> _myDeadParts;

	/***************************************************************
	 * member methods
	 ***************************************************************/

	private boolean _plotTheDead = true;

	/**
	 * ************************************************************ constructor
	 * *************************************************************
	 */
	public RemoveDetectedObserver(final TargetType watchVessel, final TargetType targetVessel, final String myName,
			final Integer detectionLevel, final boolean isActive) {
		super(watchVessel, targetVessel, myName, detectionLevel, isActive);

	}

	private void ditchHim(final int tgt) {
		final NetworkParticipant thisPart = getScenario().getThisParticipant(tgt);
		if (thisPart == null)
			return;
		final Status hisStat = thisPart.getStatus();
		if (hisStat == null)
			return;
		final WorldLocation loc = hisStat.getLocation();
		Color hisColor = Category.getColorFor(thisPart.getCategory());
		hisColor = hisColor.darker().darker().darker();
		final LabelWrapper lw = new LabelWrapper(thisPart.getName(), loc, hisColor);
		lw.setSymbolType("Reference Position");

		if (_myDeadParts == null)
			_myDeadParts = new Vector<LabelWrapper>(0, 1);

		_myDeadParts.add(lw);

		getScenario().removeParticipant(tgt);

	}

	/**
	 * get the editor for this item
	 *
	 * @return the BeanInfo data for this editable object
	 */
	@Override
	public Editable.EditorType getInfo() {
		if (_myEditor == null)
			_myEditor = new RemoverInfo(this, getName());

		return _myEditor;
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

	public boolean isPlotTheDead() {
		return _plotTheDead;
	}

	@Override
	public void paint(final CanvasType dest) {
		if (_plotTheDead) {
			if (_myDeadParts != null) {
				final Object[] labels = _myDeadParts.toArray();
				for (int i = 0; i < labels.length; i++) {
					final LabelWrapper labelWrapper = (LabelWrapper) labels[i];
					labelWrapper.paint(dest);
				}
			}
		}
	}

	@Override
	public void performCloseProcessing(final ScenarioType scenario) {
		super.performCloseProcessing(scenario);
	}

	@Override
	protected void performSetupProcessing(final ScenarioType scenario) {
		super.performSetupProcessing(scenario);

		// chuck in the reset operation, so we're ready for this run
		resetMe();
	}

	private void resetMe() {
		_numDitched = 0;
		if (_myDeadParts != null)
			_myDeadParts.removeAllElements();
	}

	@Override
	public void restart(final ScenarioType scenario) {
		super.restart(scenario);

		resetMe();
	}

	public void setPlotTheDead(final boolean plotTheDead) {
		_plotTheDead = plotTheDead;
	}

	/**
	 * valid detection happened, process it
	 */
	@Override
	protected void validDetection(final DetectionEvent detection) {
		// let the parent do it's stuff
		super.validDetection(detection);

		// remove this target
		final int tgt = detection.getTarget();

		ditchHim(tgt);

		_numDitched++;

		// tell the attribute helper
		getAttributeHelper().newData(this.getScenario(), detection.getTime(), _numDitched);
	}
}
