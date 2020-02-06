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

package com.borlander.rac525791.dashboard;

import java.text.NumberFormat;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.StackLayout;

import com.borlander.rac525791.dashboard.layout.DashboardUIModel;
import com.borlander.rac525791.draw2d.ext.InvisibleRectangle;

public class DashboardFigure extends InvisibleRectangle {
	private static final String DEGREE = "\u00B0";
	private static NumberFormat ourFormatter;

	private static NumberFormat getCourseFormatter() {
		if (ourFormatter == null) {
			ourFormatter = NumberFormat.getInstance();
			ourFormatter.setMinimumIntegerDigits(3);
		}
		return ourFormatter;
	}

	private PointersLayer myPointersLayer;
	private ControlTextLayer myControlTextLayer;
	private ControlUnitsLayer myControlUnitsLayer;
	private TextLayer myTextLayer;

	private final DashboardUIModel myUiModel;

	public DashboardFigure() {
		myUiModel = new DashboardUIModel();
		setLayoutManager(new StackLayout());

		this.add(createBackgroundLayer());
		this.add(createPointersLayer());
		this.add(createControlTextLayer());
		this.add(createTextLayer());
		this.add(createUnitsLayer());
	}

	private IFigure createBackgroundLayer() {
		return new BackgroundLayer(myUiModel);
	}

	private IFigure createControlTextLayer() {
		myControlTextLayer = new ControlTextLayer(myUiModel);
		return myControlTextLayer;
	}

	private IFigure createPointersLayer() {
		myPointersLayer = new PointersLayer(myUiModel);
		return myPointersLayer;
	}

	private IFigure createTextLayer() {
		myTextLayer = new TextLayer(myUiModel);
		return myTextLayer;
	}

	private IFigure createUnitsLayer() {
		myControlUnitsLayer = new ControlUnitsLayer(myUiModel);
		return myControlUnitsLayer;
	}

	public void dispose() {
		myUiModel.dispose();
	}

	private String formatCourse(final int course) {
		return getCourseFormatter().format(course) + DEGREE;
	}

	private ScaledControlPointersLayer getDepthArrows() {
		return myPointersLayer.getDepthArrows();
	}

	private ControlPointersLayer getDirectionArrows() {
		return myPointersLayer.getDirectionArrows();
	}

	private ScaledControlPointersLayer getSpeedArrows() {
		return myPointersLayer.getSpeedArrows();
	}

	public void setDemandedDepth(final int value) {
		getDepthArrows().setDemandedValue(value);
		updateDepthMultiplier();
	}

	public void setDemandedDirection(final int value) {
		getDirectionArrows().setDemandedValue(value);
	}

	public void setDemandedSpeed(final int value) {
		getSpeedArrows().setDemandedValue(value);
		updateSpeedMultiplier();
	}

	public void setDepth(final int value) {
		getDepthArrows().setActualValue(value);
		myControlTextLayer.setDepth(value);
		updateDepthMultiplier();
	}

	public void setDepthUnits(final String units) {
		myControlUnitsLayer.setDepthUnits(units);
	}

	public void setDirection(final int value) {
		getDirectionArrows().setActualValue(value);
		myTextLayer.setCenterText(formatCourse(value));
	}

	public void setIgnoreDemandedDepth(final boolean ignore) {
		getDepthArrows().setIgnoreDemandedValue(ignore);
	}

	public void setIgnoreDemandedDirection(final boolean ignore) {
		getDirectionArrows().setIgnoreDemandedValue(ignore);
	}

	public void setIgnoreDemandedSpeed(final boolean ignore) {
		getSpeedArrows().setIgnoreDemandedValue(ignore);
	}

	public void setSpeed(final int value) {
		getSpeedArrows().setActualValue(value);
		myControlTextLayer.setSpeed(value);
		updateSpeedMultiplier();
	}

	public void setSpeedUnits(final String units) {
		myControlUnitsLayer.setSpeedUnits(units);
	}

	public void setVesselName(final String name) {
		myTextLayer.setLeftText(name);
	}

	public void setVesselStatus(final String status) {
		myTextLayer.setRightText(status);
	}

	private void updateDepthMultiplier() {
		myControlUnitsLayer.setDepthMultiplier(getDepthArrows().getMultiplier());
	}

	public void updateDepthOnThreshold(final boolean isOnThreshold) {
		getDepthArrows().updateShowRedSector(isOnThreshold);
		myControlTextLayer.updateDepthGradient(isOnThreshold);
	}

	public void updateDirectionOnThreshold(final boolean isOnThreshold) {
		getDirectionArrows().updateShowRedSector(isOnThreshold);
	}

	private void updateSpeedMultiplier() {
		myControlUnitsLayer.setSpeedMultiplier(getSpeedArrows().getMultiplier());
	}

	public void updateSpeedOnThreshold(final boolean isOnThreshold) {
		getSpeedArrows().updateShowRedSector(isOnThreshold);
		myControlTextLayer.updateSpeedGradient(isOnThreshold);
	}

}
