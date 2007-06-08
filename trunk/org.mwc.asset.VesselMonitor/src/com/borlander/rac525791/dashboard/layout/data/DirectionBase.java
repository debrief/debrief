package com.borlander.rac525791.dashboard.layout.data;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;

import com.borlander.rac525791.dashboard.layout.ControlUIModel;

abstract class DirectionBase implements ControlUIModel {

	public final boolean isFullCircleMapped() {
		return true;
	}

	public final Dimension getMaximumMark() {
		throw new UnsupportedOperationException("Not supported for Direction control");
	}

	public final Dimension getUnitsAndMultipliersSize() {
		throw new UnsupportedOperationException("Not supported for Direction control");
	}

	public final Point getUnitsPosition() {
		throw new UnsupportedOperationException("Not supported for Direction control");
	}

	public final Point getValueTextPosition() {
		throw new UnsupportedOperationException("Not supported for Direction control");
	}

	public final Dimension getValueTextSize() {
		throw new UnsupportedOperationException("Not supported for Direction control");
	}

	public final Dimension getZeroMark() {
		throw new UnsupportedOperationException("Not supported for Direction control");
	}

}
