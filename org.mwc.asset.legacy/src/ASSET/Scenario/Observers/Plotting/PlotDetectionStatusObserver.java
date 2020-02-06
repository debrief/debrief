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

package ASSET.Scenario.Observers.Plotting;

import java.awt.Color;
import java.awt.Point;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.Enumeration;
import java.util.HashMap;

import ASSET.NetworkParticipant;
import ASSET.ParticipantType;
import ASSET.ScenarioType;
import ASSET.Models.Decision.TargetType;
import ASSET.Models.Detection.DetectionEvent;
import ASSET.Models.Detection.DetectionList;
import ASSET.Participants.Category;
import ASSET.Scenario.Observers.DetectionObserver;
import MWC.GUI.CanvasType;
import MWC.GUI.Editable;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldLocation;

public class PlotDetectionStatusObserver extends DetectionObserver {
	/***************************************************************
	 * member variables
	 ***************************************************************/

	/***************************************************************
	 * constructor
	 ***************************************************************/

	// ////////////////////////////////////////////////////
	// bean info for this class
	// ///////////////////////////////////////////////////
	public class DetectionInfo extends Editable.EditorType {

		public DetectionInfo(final PlotDetectionStatusObserver data, final String name) {
			super(data, name, "Plot sensor performance");
		}

		@Override
		public String getName() {
			return PlotDetectionStatusObserver.this.getName();
		}

		@Override
		public PropertyDescriptor[] getPropertyDescriptors() {
			try {
				final PropertyDescriptor[] res = { prop("Name", "the name of this observer"),
						prop("Active", "whether this listener is active") };
				return res;
			} catch (final IntrospectionException e) {
				System.out.println("::" + e.getMessage());
				return super.getPropertyDescriptors();
			}
		}
	}

	/**
	 * the list of targets - and their detection status
	 *
	 */
	private final HashMap<Integer, Integer> _detectionStates;

	/**
	 * create a detection observer
	 *
	 * @param watchVessel    the type of vessel we are monitoring
	 * @param targetVessel   the type of vessel the monitored vessel is looking for,
	 * @param name           the name of this observer
	 * @param detectionLevel the (optional) detection level required
	 * @param isActive       whether this is observer is active
	 */

	public PlotDetectionStatusObserver(final TargetType watchVessel, final TargetType targetVessel, final String name,
			final Integer detectionLevel, final boolean isActive) {
		super(watchVessel, targetVessel, name, detectionLevel, isActive);

		// get ready...
		_detectionStates = new HashMap<Integer, Integer>();
	}

	/**
	 * return the calculated result for the batch processing
	 *
	 * @return string to be used in results collation
	 */
	@Override
	protected Number getBatchResult() {
		return null;
	}

	/**
	 * find the data area occupied by this item
	 */
	@Override
	public WorldArea getBounds() {
		return null;
	}

	/**
	 * get the editor for this item
	 *
	 * @return the BeanInfo data for this editable object
	 */
	@Override
	public Editable.EditorType getInfo() {
		if (_myEditor == null)
			_myEditor = new DetectionInfo(this, getName());

		return _myEditor;
	}

	/**
	 * get the scenario
	 */
	@Override
	protected ScenarioType getScenario() {
		return _myScenario;
	}

	// ////////////////////////////////////////////////
	// scenario processing/listening
	// ////////////////////////////////////////////////

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

	/***************************************************************
	 * listen for detections
	 ***************************************************************/

	/***************************************************************
	 * handle participants being added/removed
	 ***************************************************************/

	/***************************************************************
	 * plottable props
	 ***************************************************************/

	/**
	 * ok, this vessel matches what we're looking for. start listening to it
	 *
	 * @param newPart
	 */
	@Override
	protected void listenTo(final ParticipantType newPart) {
		super.listenTo(newPart);
	}

	/**
	 * ************************************************************ member methods
	 * *************************************************************
	 */

	@Override
	public void newDetections(final DetectionList detections) {
		super.newDetections(detections);

		// right, see which have these have already been plotted
		final Enumeration<DetectionEvent> numer = detections.elements();
		while (numer.hasMoreElements()) {
			final DetectionEvent de = numer.nextElement();

			// does this match our target category
			final Category thisTargetType = de.getTargetType();
			final TargetType myTargetType = this.getTargetType();
			if (myTargetType.matches(thisTargetType)) {
				// have we already detected it?
				final int tgtId = de.getHost();// de.getTarget();

				final Integer oldVal = _detectionStates.get(tgtId);
				int detectionState = de.getDetectionState();

				if (oldVal != null) {
					// ditch any old state
					_detectionStates.remove(tgtId);

					detectionState = Math.max(oldVal, detectionState);
				}

				_detectionStates.put(tgtId, detectionState);

			}

		}
	}

	/**
	 * paint this object to the specified canvas
	 */
	@Override
	public void paint(final CanvasType dest) {
		if (!this.getVisible())
			return;

		if (_myScenario == null)
			return;

		// cool, here we go.

		// loop through our selected vessels
		final Integer[] parts = _myScenario.getListOfParticipants();
		for (int i = 0; i < parts.length; i++) {
			final Integer thisP = parts[i];

			// sort out where he is
			final NetworkParticipant part = _myScenario.getThisParticipant(thisP);
			final WorldLocation loc = part.getStatus().getLocation();
			final Point pt = dest.toScreen(loc);

			// have we detected him?
			Color hisColor = null;// Color.red;
			if (_detectionStates.containsKey(thisP)) {
				final Integer theState = _detectionStates.get(thisP);

				switch (theState) {
				case (DetectionEvent.CLASSIFIED): {
					hisColor = Color.orange;
					break;
				}
				case (DetectionEvent.DETECTED): {
					hisColor = Color.yellow;
					break;
				}
				case (DetectionEvent.IDENTIFIED): {
					hisColor = Color.green;
					break;
				}
				default: {
					hisColor = null;
				}
				}
			}

			if (hisColor != null) {
				dest.setColor(hisColor);
				dest.fillOval(pt.x - 1, pt.y - 1, 3, 3);
			}
		}

	}

	@Override
	protected void performCloseProcessing(final ScenarioType scenario) {
		super.performCloseProcessing(scenario);
	}

	@Override
	protected void performSetupProcessing(final ScenarioType scenario) {
		super.performSetupProcessing(scenario);
	}

	// ////////////////////////////////////////////////
	// accessors
	// ////////////////////////////////////////////////

	/**
	 * Determine how far away we are from this point. or return null if it can't be
	 * calculated
	 */
	@Override
	public double rangeFrom(final WorldLocation other) {
		return -1;
	}

}
