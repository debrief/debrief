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

package org.mwc.debrief.satc_interface.data.wrappers;

import java.util.Date;

import com.planetmayo.debrief.satc.model.contributions.BaseContribution;

import MWC.GUI.CanvasType;
import MWC.GUI.ExcludeFromRightClickEdit;
import MWC.GUI.Plottable;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldLocation;

public class ContributionWrapper implements Plottable, ExcludeFromRightClickEdit {
	final BaseContribution _myCont;

	protected EditorType _myEditor;

	public ContributionWrapper(final BaseContribution contribution) {
		_myCont = contribution;
	}

	/**
	 * we implement our own sorting so that contributions are grouped in
	 * chronological order
	 *
	 */
	@Override
	public int compareTo(final Plottable arg0) {
		final ContributionWrapper him = (ContributionWrapper) arg0;

		final Date myStart = this.getContribution().getStartDate();
		final Date hisStart = him.getContribution().getStartDate();

		int res = 0;

		if ((myStart != null) && (hisStart != null)) {
			res = myStart.compareTo(hisStart);
		}

		// are they the same time?
		if (res == 0) {
			// yes, ok - user their natural ordering instead
			res = this.getContribution().compareTo(him.getContribution());
		}

		return res;
	}

	public HiResDate get_Start() {
		return new HiResDate(_myCont.getStartDate().getTime());
	}

	@Override
	public WorldArea getBounds() {
		return null;
	}

	public BaseContribution getContribution() {
		return _myCont;
	}

	public HiResDate getEnd() {
		return new HiResDate(_myCont.getFinishDate().getTime());
	}

	@Override
	public EditorType getInfo() {
		return null;
	}

	@Override
	public String getName() {
		return _myCont.getName();
	}

	@Override
	public boolean getVisible() {
		return _myCont.isActive();
	}

	@Override
	public boolean hasEditor() {
		return false;
	}

	@Override
	public void paint(final CanvasType dest) {
	}

	@Override
	public double rangeFrom(final WorldLocation other) {
		return Plottable.INVALID_RANGE;
	}

	public void set_Start(final HiResDate start) {
		_myCont.setStartDate(start.getDate());
	}

	public void setEnd(final HiResDate end) {
		_myCont.setFinishDate(end.getDate());
	}

	public void setName(final String name) {
		_myCont.setName(name);
	}

	@Override
	public void setVisible(final boolean val) {
		_myCont.setActive(val);
	}

	@Override
	public String toString() {
		return getName();
	}
}