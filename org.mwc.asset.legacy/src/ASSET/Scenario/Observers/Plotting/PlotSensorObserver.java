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
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

import ASSET.ParticipantType;
import ASSET.ScenarioType;
import ASSET.GUI.Workbench.Plotters.ScenarioParticipantWrapper;
import ASSET.Models.SensorType;
import ASSET.Models.Decision.TargetType;
import ASSET.Models.Detection.DetectionEvent;
import ASSET.Models.Detection.DetectionList;
import ASSET.Models.Sensor.SensorList;
import ASSET.Models.Sensor.Cookie.TypedCookieSensor;
import ASSET.Models.Sensor.Cookie.TypedCookieSensor.TypedRangeDoublet;
import ASSET.Scenario.ScenarioSteppedListener;
import ASSET.Scenario.Observers.DetectionObserver;
import MWC.GUI.CanvasType;
import MWC.GUI.Editable;
import MWC.GUI.ExtendedCanvasType;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldVector;

public class PlotSensorObserver extends DetectionObserver implements ScenarioSteppedListener {
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

		public DetectionInfo(final PlotSensorObserver data, final String name) {
			super(data, name, "Plot sensor performance");
		}

		@Override
		public String getName() {
			return PlotSensorObserver.this.getName();
		}

		@Override
		public PropertyDescriptor[] getPropertyDescriptors() {
			try {
				final PropertyDescriptor[] res = { prop("Name", "the name of this observer"),
						prop("Active", "whether this listener is active"),
						prop("ShadeCircle", "whether to shade in the arc of coverage"),
						prop("ShowNames", "whether to plot text for target types"), };
				return res;
			} catch (final IntrospectionException e) {
				System.out.println("::" + e.getMessage());
				return super.getPropertyDescriptors();
			}
		}
	}

	/**
	 * the last time step
	 *
	 */
	private long _tNow = -1;

	/**
	 * whether to display the target types
	 *
	 */
	private boolean _showNames;

	private boolean _shadeCircle;

	/**
	 * ************************************************************ member methods
	 * *************************************************************
	 */

	/**
	 * create a detection observer
	 *
	 * @param watchVessel    the type of vessel we are monitoring
	 * @param targetVessel   the type of vessel the monitored vessel is looking for,
	 * @param name           the name of this observer
	 * @param detectionLevel the (optional) detection level required
	 * @param isActive       whether this is observer is active
	 * @param showNames
	 * @param shadeCircle
	 */

	public PlotSensorObserver(final TargetType watchVessel, final TargetType targetVessel, final String name,
			final Integer detectionLevel, final boolean isActive, final boolean showNames, final boolean shadeCircle) {
		super(watchVessel, targetVessel, name, detectionLevel, isActive);

		_showNames = showNames;
		_shadeCircle = shadeCircle;
	}

	/**
	 * add any applicable listeners
	 */
	@Override
	protected void addListeners(final ScenarioType scenario) {
		super.addListeners(scenario);

		scenario.addScenarioSteppedListener(this);
	}

	// ////////////////////////////////////////////////
	// scenario processing/listening
	// ////////////////////////////////////////////////

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

	/***************************************************************
	 * listen for detections
	 ***************************************************************/

	/***************************************************************
	 * handle participants being added/removed
	 ***************************************************************/

	/**
	 * get the scenario
	 */
	@Override
	protected ScenarioType getScenario() {
		return _myScenario;
	}

	/***************************************************************
	 * plottable props
	 ***************************************************************/

	public boolean getShowNames() {
		return _showNames;
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

	public boolean isShadeCircle() {
		return _shadeCircle;
	}

	/**
	 * whether this is a significant attribute, displayed by default
	 *
	 * @return yes/no
	 */
	@Override
	public boolean isSignificant() {
		return false;
	}

	/**
	 * ok, this vessel matches what we're looking for. start listening to it
	 *
	 * @param newPart
	 */
	@Override
	protected void listenTo(final ParticipantType newPart) {
	}

	/**
	 * paint this object to the specified canvas
	 */
	@Override
	public void paint(final CanvasType dest) {
		if (!this.getVisible())
			return;

		// loop through our selected vessels
		final Vector<ParticipantType> parts = this.getWatchedVessels();
		for (final Iterator<ParticipantType> iterator = parts.iterator(); iterator.hasNext();) {
			final ParticipantType thisP = iterator.next();

			// check he's alive
			if (!thisP.isAlive())
				continue;

			// right, have a look at it's sensors
			final SensorList sensors = thisP.getSensorFit();

			final Collection<SensorType> sensorList = sensors.getSensors();
			final Iterator<SensorType> looper = sensorList.iterator();
			while (looper.hasNext()) {
				final SensorType thisS = looper.next();

				if (thisS.isWorking()) {
					// is it simple enought to plot?
					if (thisS instanceof TypedCookieSensor) {
						final TypedCookieSensor pcs = (TypedCookieSensor) thisS;
						// get the detection ranges

						final Vector<TypedRangeDoublet> ranges = pcs.getRanges();

						for (final Iterator<TypedRangeDoublet> iterator2 = ranges.iterator(); iterator2.hasNext();) {
							final TypedRangeDoublet thisRange = iterator2.next();

							final WorldDistance range = thisRange.getRange();

							// converto to screen coords
							final WorldLocation tl1 = thisP.getStatus().getLocation()
									.add(new WorldVector(MWC.Algorithms.Conversions.Degs2Rads(270), range, null));
							final WorldLocation tl2 = thisP.getStatus().getLocation()
									.add(new WorldVector(MWC.Algorithms.Conversions.Degs2Rads(0), range, null));
							final WorldLocation br1 = thisP.getStatus().getLocation()
									.add(new WorldVector(MWC.Algorithms.Conversions.Degs2Rads(90), range, null));
							final WorldLocation br2 = thisP.getStatus().getLocation()
									.add(new WorldVector(MWC.Algorithms.Conversions.Degs2Rads(180), range, null));

							final WorldLocation tlW = new WorldLocation(tl2.getLat(), tl1.getLong(), 0);
							final WorldLocation brW = new WorldLocation(br2.getLat(), br1.getLong(), 0);

							final Point tl = new Point(dest.toScreen(tlW));
							final Point br = dest.toScreen(brW);

							boolean doSolid = false;

							// decide if this sensor is in contact
							final DetectionList dList = pcs.getDetectionsFor(thisRange);
							if (dList != null) {
								if (dList.size() > 0) {
									final DetectionEvent lastD = dList.lastElement();
									// is it at the current time?
									if (lastD.getTime() == _tNow) {
										doSolid = true;
									}
								}
							}

							// sort out the color
							dest.setColor(ScenarioParticipantWrapper.getColorFor(thisP.getCategory().getForce()));

							if (doSolid)
								dest.setLineStyle(CanvasType.SOLID);
							else
								dest.setLineStyle(CanvasType.DOTTED);

							if (_shadeCircle) {
								if (dest instanceof ExtendedCanvasType) {
									final ExtendedCanvasType et = (ExtendedCanvasType) dest;
									et.semiFillOval(tl.x, tl.y, br.x - tl.x, br.y - tl.y);
								} else
									dest.fillOval(tl.x, tl.y, br.x - tl.x, br.y - tl.y);
							}
							dest.drawOval(tl.x, tl.y, br.x - tl.x, br.y - tl.y);

							dest.setLineStyle(CanvasType.SOLID);

							if (_showNames) {
								// collate a string of the types this ring represents
								final StringBuilder sb = new StringBuilder();
								final Vector<String> theItems = thisRange.getMyTypes();
								if (theItems != null) {
									final Iterator<String> iter = theItems.iterator();
									while (iter.hasNext()) {
										final String thisT = iter.next();
										sb.append(thisT + " ");
									}

									if (sb.length() > 0) {
										final String theStr = sb.toString();
										final int wit = dest.getStringWidth(null, theStr);
										dest.drawText(theStr, tl.x + (br.x - tl.x) / 2 - wit / 2,
												br.y + dest.getStringHeight(null));
									}
								}
							}
						}
					}
				}
			}
		}
	}

	/**
	 * Determine how far away we are from this point. or return null if it can't be
	 * calculated
	 */
	@Override
	public double rangeFrom(final WorldLocation other) {
		return -1;
	}

	/**
	 * remove any listeners
	 */
	@Override
	protected void removeListeners(final ScenarioType scenario) {
		super.removeListeners(scenario);

		scenario.removeScenarioSteppedListener(this);
	}

	public void setShadeCircle(final boolean shadeCircle) {
		_shadeCircle = shadeCircle;
	}

	// ////////////////////////////////////////////////
	// accessors
	// ////////////////////////////////////////////////

	public void setShowNames(final boolean showNames) {
		_showNames = showNames;
	}

	@Override
	public void step(final ScenarioType scenario, final long newTime) {
		_tNow = newTime;
	}
}
