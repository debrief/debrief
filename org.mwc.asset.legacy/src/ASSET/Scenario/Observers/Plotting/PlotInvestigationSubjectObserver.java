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

import java.awt.Point;
import java.util.Iterator;
import java.util.Vector;

import ASSET.NetworkParticipant;
import ASSET.ParticipantType;
import ASSET.ScenarioType;
import ASSET.GUI.Workbench.Plotters.ScenarioParticipantWrapper;
import ASSET.Models.DecisionType;
import ASSET.Models.Decision.BehaviourList;
import ASSET.Models.Decision.TargetType;
import ASSET.Models.Decision.Tactical.Investigate;
import ASSET.Participants.Status;
import ASSET.Scenario.Observers.CoreObserver;
import MWC.GUI.CanvasType;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldLocation;

public class PlotInvestigationSubjectObserver extends CoreObserver {
	private final TargetType _watchType;

	/***************************************************************
	 * member variables
	 ***************************************************************/

	/***************************************************************
	 * constructor
	 ***************************************************************/

	/**
	 * create a detection observer
	 *
	 * @param watchVessel the type of vessel we are monitoring
	 * @param name        the name of this observer
	 * @param isActive    whether this is observer is active
	 */

	public PlotInvestigationSubjectObserver(final TargetType watchVessel, final String name, final boolean isActive) {
		super(name, isActive);

		_watchType = watchVessel;
	}

	/**
	 * ************************************************************ member methods
	 * *************************************************************
	 */

	@Override
	protected void addListeners(final ScenarioType scenario) {
	}

	/**
	 * is this one we're looking for? If so, get plotting
	 *
	 * @param dest     the screen
	 * @param decision the decision to look at
	 * @param myId     who I am
	 */
	private void checkThisBehaviour(final CanvasType dest, final DecisionType decision, final Integer myId) {
		if (decision instanceof Investigate) {
			// get my location
			final NetworkParticipant me = _myScenario.getThisParticipant(myId);
			final Status myStat = me.getStatus();
			final WorldLocation loc = myStat.getLocation();

			// get the investigation-related bits
			final Investigate inv = (Investigate) decision;
			final Integer tgtId = inv.getCurrentTarget();
			if (tgtId != null) {
				final NetworkParticipant theTarget = _myScenario.getThisParticipant(tgtId);
				if (theTarget != null) {
					final Status hisStat = theTarget.getStatus();
					final WorldLocation hisLoc = hisStat.getLocation();

					final Point pt1 = new Point(dest.toScreen(loc));
					final Point pt2 = new Point(dest.toScreen(hisLoc));

					// get my color
					final String force = me.getCategory().getForce();
					dest.setColor(ScenarioParticipantWrapper.getColorFor(force));
					dest.setLineStyle(CanvasType.DOTTED);

					dest.drawLine(pt1.x, pt1.y, pt2.x, pt2.y);
				}
			}
		}
	}

	protected void doPlot(final CanvasType dest, final DecisionType decision, final Integer myId) {
		if (decision instanceof BehaviourList) {
			final BehaviourList list = (BehaviourList) decision;
			final Vector<DecisionType> models = list.getModels();
			final Iterator<DecisionType> dec = models.iterator();
			while (dec.hasNext()) {
				final DecisionType thisD = dec.next();
				doPlot(dest, thisD, myId);
			}
		}

		checkThisBehaviour(dest, decision, myId);

	}

	/**
	 * find the data area occupied by this item
	 */
	@Override
	public WorldArea getBounds() {
		return null;
	}

	@Override
	public EditorType getInfo() {
		return null;
	}

	public TargetType getWatchType() {
		return _watchType;
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

	/**
	 * whether this is a significant attribute, displayed by default
	 *
	 * @return yes/no
	 */
	public boolean isSignificant() {
		return false;
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

		final Integer[] parts = _myScenario.getListOfParticipants();
		for (int i = 0; i < parts.length; i++) {
			final Integer theId = parts[i];
			final ParticipantType thisP = _myScenario.getThisParticipant(theId);
			final DecisionType dm = thisP.getDecisionModel();
			doPlot(dest, dm, theId);
		}

		// // loop
		//
		// // loop through our selected vessels
		// Vector<ParticipantType> parts = this.getWatchedVessels();
		// for (Iterator<ParticipantType> iterator = parts.iterator(); iterator
		// .hasNext();)
		// {
		// ParticipantType thisP = iterator.next();
		//
		// // right, have a look at it's sensors
		// SensorList sensors = thisP.getSensorFit();
		//
		// Collection<SensorType> sensorList = sensors.getSensors();
		// Iterator<SensorType> looper = sensorList.iterator();
		// while (looper.hasNext())
		// {
		// SensorType thisS = looper.next();
		//
		// // is it simple enought to plot?
		// if (thisS instanceof TypedCookieSensor)
		// {
		// TypedCookieSensor pcs = (TypedCookieSensor) thisS;
		// // get the detection ranges
		//
		// Vector<TypedRangeDoublet> ranges = pcs.getRanges();
		//
		// for (Iterator<TypedRangeDoublet> iterator2 = ranges.iterator(); iterator2
		// .hasNext();)
		// {
		// TypedRangeDoublet thisRange = iterator2.next();
		//
		// WorldDistance range = thisRange.getRange();
		//
		// // converto to screen coords
		// WorldLocation tl1 = thisP.getStatus().getLocation().add(
		// new WorldVector(MWC.Algorithms.Conversions.Degs2Rads(270),
		// range, null));
		// WorldLocation tl2 = thisP.getStatus().getLocation().add(
		// new WorldVector(MWC.Algorithms.Conversions.Degs2Rads(0),
		// range, null));
		// WorldLocation br1 = thisP.getStatus().getLocation().add(
		// new WorldVector(MWC.Algorithms.Conversions.Degs2Rads(90),
		// range, null));
		// WorldLocation br2 = thisP.getStatus().getLocation().add(
		// new WorldVector(MWC.Algorithms.Conversions.Degs2Rads(180),
		// range, null));
		//
		// WorldLocation tlW = new WorldLocation(tl2.getLat(), tl1.getLong(), 0);
		// WorldLocation brW = new WorldLocation(br2.getLat(), br1.getLong(), 0);
		//
		// Point tl = new Point(dest.toScreen(tlW));
		// Point br = dest.toScreen(brW);
		//
		// boolean doSolid = false;
		//
		// // decide if this sensor is in contact
		// DetectionList dList = pcs.getDetectionsFor(thisRange);
		// if (dList != null)
		// {
		// if (dList.size() > 0)
		// {
		// DetectionEvent lastD = dList.lastElement();
		// // is it at the current time?
		// if (lastD.getTime() == _tNow)
		// {
		// doSolid = true;
		// }
		// }
		// }
		//
		// // sort out the color
		// dest.setColor(ScenarioParticipantWrapper.getColorFor(thisP
		// .getCategory().getForce()));
		//
		// if (doSolid)
		// dest.setLineStyle(CanvasType.SOLID);
		// else
		// dest.setLineStyle(CanvasType.DOTTED);
		//
		// if (_shadeCircle)
		// {
		// if (dest instanceof ExtendedCanvasType)
		// {
		// ExtendedCanvasType et = (ExtendedCanvasType) dest;
		// et.semiFillOval(tl.x, tl.y, br.x - tl.x, br.y - tl.y);
		// }
		// else
		// dest.fillOval(tl.x, tl.y, br.x - tl.x, br.y - tl.y);
		// }
		// dest.drawOval(tl.x, tl.y, br.x - tl.x, br.y - tl.y);
		//
		// dest.setLineStyle(CanvasType.SOLID);
		//
		// if (_showNames)
		// {
		// // collate a string of the types this ring represents
		// StringBuilder sb = new StringBuilder();
		// Vector<String> theItems = thisRange.getMyTypes();
		// if (theItems != null)
		// {
		// Iterator<String> iter = theItems.iterator();
		// while (iter.hasNext())
		// {
		// String thisT = iter.next();
		// sb.append(thisT + " ");
		// }
		//
		// if (sb.length() > 0)
		// {
		// String theStr = sb.toString();
		// int wit = dest.getStringWidth(null, theStr);
		// dest.drawText(theStr, tl.x + (br.x - tl.x) / 2 - wit / 2,
		// br.y + dest.getStringHeight(null));
		// }
		// }
		// }
		// }
		// }
		// }
		// }
	}

	@Override
	protected void performCloseProcessing(final ScenarioType scenario) {
	}

	@Override
	protected void performSetupProcessing(final ScenarioType scenario) {
	}

	/**
	 * Determine how far away we are from this point. or return null if it can't be
	 * calculated
	 */
	@Override
	public double rangeFrom(final WorldLocation other) {
		return -1;
	}

	@Override
	protected void removeListeners(final ScenarioType scenario) {
	}

	// ////////////////////////////////////////////////
	// accessors
	// ////////////////////////////////////////////////

}
