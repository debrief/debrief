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

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.LayoutManager;
import org.eclipse.draw2d.geometry.Point;

import com.borlander.rac525791.dashboard.layout.BaseDashboardLayout;
import com.borlander.rac525791.dashboard.layout.ControlUIModel;
import com.borlander.rac525791.dashboard.layout.ControlUISuite;
import com.borlander.rac525791.dashboard.layout.DashboardUIModel;
import com.borlander.rac525791.dashboard.rotatable.AngleMapper;
import com.borlander.rac525791.dashboard.rotatable.RedSector;
import com.borlander.rac525791.draw2d.ext.RotatableDecorationExt;

/**
 * It is not a layer because it just creates and manages figures that are
 * children of another layer.
 *
 * Semantic grouping (red-sector, demanded arrow, actual arrow) differs from
 * figure-containment grouping because we have 3 separate groups for each of the
 * controls but only single numbers layer and we need to paint numbers above all
 * of 3 red sectors but below all arrows.
 */
public class ControlPointersLayer {
	public static interface Factory {
		public RotatableDecorationExt createActualArrow();

		public AngleMapper createAngleMapper();

		public RotatableDecorationExt createDemandedArrow();

		public RedSector createRedSector(AngleMapper mapper);
	}

	private class Layout extends BaseDashboardLayout {
		private final ControlUISuite.ControlAccess myControl;
		private final Point TEMP = new Point();

		public Layout(final DashboardUIModel uiModel, final ControlUISuite.ControlAccess control) {
			super(uiModel);
			myControl = control;
		}

		@Override
		public void layout(final IFigure container) {

			final ControlUISuite suite = getSuite(container);
			final ControlUIModel positions = myControl.selectControl(suite);
			final double templatesScale = suite.getTemplatesScale();

			placeAtTopLeft(container, TEMP);
			TEMP.translate(positions.getControlCenter());
			myArrow.setLocation(TEMP);
			myArrow.setScale(templatesScale, templatesScale);

			myDemandedPointer.setLocation(TEMP);
			myDemandedPointer.setScale(templatesScale, templatesScale);

			myRedSector.setCenterLocation(TEMP);
			myRedSector.setRadius(positions.getRedSectorRadius());

			if (!positions.isFullCircleMapped()) {
				myMapper.setAnglesRange(positions.getZeroMark(), positions.getMaximumMark());
				// angles may change - reset pointers rotation
				myArrow.setRotation(myMapper.computeAngle(myActualValue));
				myDemandedPointer.setRotation(myMapper.computeAngle(myDemandedValue));
			}
		}

	}

	private final LayoutManager myLayouter;

	final RotatableDecorationExt myArrow;

	final RotatableDecorationExt myDemandedPointer;

	final AngleMapper myMapper;

	final RedSector myRedSector;

	int myActualValue;

	int myDemandedValue;

	public ControlPointersLayer(final Factory factory, final DashboardUIModel uiModel,
			final ControlUISuite.ControlAccess control) {
		myLayouter = new Layout(uiModel, control);
		myMapper = factory.createAngleMapper();
		myArrow = factory.createActualArrow();
		myRedSector = factory.createRedSector(myMapper);
		myDemandedPointer = factory.createDemandedArrow();
	}

	private double computeAngle(final int value) {
		return myMapper.computeAngle(value);
	}

	/**
	 * Intentionally just IFigure.
	 */
	public IFigure getActualArrow() {
		return myArrow;
	}

	/**
	 * Intentionally just IFigure.
	 */
	public IFigure getDemandedPointer() {
		return myDemandedPointer;
	}

	/**
	 * Intentionally returned as IFigure -- all that caller can do with it is to add
	 * this figure to some container.
	 */
	public IFigure getRedSector() {
		return myRedSector;
	}

	public void layoutGroup(final IFigure container) {
		myLayouter.layout(container);
	}

	public void setActualValue(final int value) {
		if (myActualValue != value) {
			myActualValue = value;
			myArrow.setRotation(computeAngle(myActualValue));
			myRedSector.setActualValue(myActualValue);
		}
	}

	public void setDemandedValue(final int value) {
		if (myDemandedValue != value) {
			myDemandedValue = value;
			myDemandedPointer.setRotation(computeAngle(myDemandedValue));
			myRedSector.setDemandedValue(myDemandedValue);
		}
	}

	public void setIgnoreDemandedValue(final boolean ignore) {
		myDemandedPointer.setVisible(!ignore);
	}

	public void updateShowRedSector(final boolean isOnThreshold) {
		myRedSector.setVisible(!isOnThreshold);
	}

}
