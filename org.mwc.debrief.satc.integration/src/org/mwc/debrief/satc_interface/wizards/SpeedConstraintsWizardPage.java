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

package org.mwc.debrief.satc_interface.wizards;

import java.beans.PropertyDescriptor;

import org.eclipse.jface.viewers.ISelection;
import org.mwc.cmap.core.wizards.CoreEditableWizardPage;

import com.planetmayo.debrief.satc.model.contributions.SpeedForecastContribution;
import com.planetmayo.debrief.satc.util.GeoSupport;

import MWC.GUI.CanvasType;
import MWC.GUI.Editable;
import MWC.GUI.Plottable;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;

/**
 * The "New" wizard page allows setting the container for the new file as well
 * as the file name. The page will only accept file name without the extension
 * OR with the extension that matches the expected one (xml).
 */

public class SpeedConstraintsWizardPage extends CoreEditableWizardPage {

	public static class SpeedConstraintsObject implements Plottable {
		private final SpeedForecastContribution _speed;

		private boolean hasEstimate;

		public SpeedConstraintsObject(final SpeedForecastContribution speed) {
			this._speed = speed;
		}

		@Override
		public int compareTo(final Plottable arg0) {
			return 0;
		}

		@Override
		public WorldArea getBounds() {
			return null;
		}

		public SpeedForecastContribution getContribution() {
			return _speed;
		}

		public WorldSpeed getEstimate() {
			final Double theEstimate = _speed.getEstimate();
			if (theEstimate != null)
				return new WorldSpeed(GeoSupport.MSec2kts(theEstimate), WorldSpeed.Kts);
			else
				return new WorldSpeed(0, WorldSpeed.Kts);
		}

		@Override
		public EditorType getInfo() {
			return null;
		}

		public WorldSpeed getMaxSpeed() {
			return new WorldSpeed(GeoSupport.MSec2kts(_speed.getMaxSpeed()), WorldSpeed.Kts);
		}

		public WorldSpeed getMinSpeed() {
			return new WorldSpeed(GeoSupport.MSec2kts(_speed.getMinSpeed()), WorldSpeed.Kts);
		}

		@Override
		public String getName() {
			return _speed.getName();
		}

		@Override
		public boolean getVisible() {
			return false;
		}

		@Override
		public boolean hasEditor() {
			return false;
		}

		public boolean isHasEstimate() {
			return hasEstimate;
		}

		@Override
		public void paint(final CanvasType dest) {
		}

		@Override
		public double rangeFrom(final WorldLocation other) {
			return 0;
		}

		public void setEstimate(final WorldSpeed estimate) {
			_speed.setEstimate(estimate.getValueIn(WorldSpeed.M_sec));
		}

		public void setHasEstimate(final boolean hasEstimate) {
			this.hasEstimate = hasEstimate;
		}

		public void setMaxSpeed(final WorldSpeed speed) {
			_speed.setMaxSpeed(speed.getValueIn(WorldSpeed.M_sec));
		}

		public void setMinSpeed(final WorldSpeed speed) {
			_speed.setMinSpeed(speed.getValueIn(WorldSpeed.M_sec));
		}

		public void setName(final String name) {
			_speed.setName(name);
		}

		@Override
		public void setVisible(final boolean val) {
		}

	}

	private final SpeedForecastContribution speed;

	/**
	 * Constructor for SampleNewWizardPage.
	 *
	 * @param pageName
	 */
	public SpeedConstraintsWizardPage(final ISelection selection, final SpeedForecastContribution speed) {
		super(selection, "speedPage", "Add Speed Constraints",
				"If you wish to provide a speed constraint for this straight leg, specify it below",
				"images/scale_wizard.gif", null);
		this.speed = speed;
	}

	@Override
	protected Editable createMe() {
		if (_editable == null) {
			final SpeedConstraintsObject theSpeed = new SpeedConstraintsObject(speed);
			theSpeed.setName("Speed estimate");
			theSpeed.setMinSpeed(new WorldSpeed(2, WorldSpeed.Kts));
			theSpeed.setMaxSpeed(new WorldSpeed(20, WorldSpeed.Kts));
			theSpeed.setEstimate(new WorldSpeed(10, WorldSpeed.Kts));
			_editable = theSpeed;
		}

		return _editable;
	}

	/**
	 * @return
	 */
	@Override
	protected PropertyDescriptor[] getPropertyDescriptors() {
		final PropertyDescriptor[] descriptors = { prop("MinSpeed", "the minimum speed", getEditable()),
				prop("MaxSpeed", "the maximum speed", getEditable()),
				prop("Name", "the name for this contribution", getEditable()),
				prop("HasEstimate", "whether to use an estimate", getEditable()),
				prop("Estimate", "the estimate of speed", getEditable()) };
		return descriptors;
	}

}