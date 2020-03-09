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
package org.mwc.debrief.core.wizards.sensorarc;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.eclipse.jface.wizard.Wizard;
import org.mwc.debrief.core.wizards.dynshapes.DynamicShapeBaseWizardPage;
import org.mwc.debrief.core.wizards.dynshapes.DynamicShapeStylingPage;
import org.mwc.debrief.core.wizards.dynshapes.DynamicShapeTimingsWizardPage;

import Debrief.ReaderWriter.Replay.ImportReplay;
import Debrief.Wrappers.DynamicTrackShapes.DynamicTrackCoverageWrapper;
import Debrief.Wrappers.DynamicTrackShapes.DynamicTrackCoverageWrapper.DynamicCoverageShape;
import Debrief.Wrappers.DynamicTrackShapes.DynamicTrackShapeWrapper;
import Debrief.Wrappers.DynamicTrackShapes.DynamicTrackShapeWrapper.DynamicShape;
import MWC.GenericData.HiResDate;

/**
 *
 * This wizard collects parameters required for creating a new sensor arc.
 *
 * @author Ayesha <ayesha.ma@gmail.com>
 *
 */
public class NewSensorArcWizard extends Wizard {
	public static final String SHAPE_NAME = "Sensor Arc";
	private final DynamicShapeTimingsWizardPage _timingsPage;
	private final SensorArcBoundsWizardPage _boundsPage;
	private final DynamicShapeStylingPage _stylingPage;
	private final String _selectedTrack;

	private DynamicTrackShapeWrapper dynamicShape;

	public NewSensorArcWizard(final String selectedTrack, final Date startTime, final Date endTime) {
		_timingsPage = new DynamicShapeTimingsWizardPage(DynamicShapeBaseWizardPage.TIMINGS_PAGE, SHAPE_NAME, startTime,
				endTime);
		_boundsPage = new SensorArcBoundsWizardPage(DynamicShapeBaseWizardPage.BOUNDS_PAGE);
		_stylingPage = new DynamicShapeStylingPage(DynamicShapeBaseWizardPage.STYLING_PAGE, SHAPE_NAME);
		_selectedTrack = selectedTrack;

	}

	@Override
	public void addPages() {
		addPage(_timingsPage);
		addPage(_boundsPage);
		addPage(_stylingPage);
	}

	public DynamicTrackShapeWrapper getDynamicShapeWrapper() {
		return dynamicShape;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		final Date startTime = _timingsPage.getStartTime();
		final Date endTime = _timingsPage.getEndTime();
		final int arcStart = _boundsPage.getArcStart();
		final int arcEnd = _boundsPage.getArcEnd();
		final int innerRadius = _boundsPage.getInnerRadius();
		final int outerRadius = _boundsPage.getOuterRadius();
		final String trackName = _selectedTrack;
		final String symbology = _stylingPage.getSymbology();
		final String arcName = _stylingPage.getShapeLabel();
		// create the object here and return it to command.
		HiResDate startDtg = null;
		HiResDate endDtg = null;
		if (startTime != null) {
			startDtg = new HiResDate(startTime);
		}
		if (endTime != null) {
			endDtg = new HiResDate(endTime);
		}
		Color theColor = null;
		theColor = ImportReplay.replayColorFor(symbology);
		final List<DynamicShape> values = new ArrayList<DynamicShape>();
		values.add(new DynamicCoverageShape(arcStart, arcEnd, innerRadius, outerRadius));
		final int theStyle = ImportReplay.replayLineStyleFor(symbology);
		final DynamicTrackShapeWrapper data = new DynamicTrackCoverageWrapper(trackName, startDtg, endDtg, values,
				theColor, theStyle, arcName);
		final String fillStyle = ImportReplay.replayFillStyleFor(symbology);
		if (fillStyle != null) {
			if ("1".equals(fillStyle)) {
				data.setSemiTransparent(false);
			} else if ("2".equals(fillStyle)) {
				data.setSemiTransparent(true);
			}
		}
		dynamicShape = data;
		return true;
	}

}
