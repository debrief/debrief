package com.borlander.rac525791.dashboard.layout.data;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;

import com.borlander.rac525791.dashboard.layout.ControlUIModel;

class Depth360x206 implements ControlUIModel {
	
	public Point getControlCenter() {
		return new Point(289, 132);
	}

	public Dimension getUnitsAndMultipliersSize() {
		return new Dimension(32, 11);
	}

	public Point getUnitsPosition() {
		return new Point(-33, 20);

	}
	public int getRedSectorRadius() {
		return 48;
	}
	
	public Dimension getZeroMark() {
		return new Dimension(-32, -40);
	}

	public Dimension getMaximumMark() {
		return new Dimension(13, 50);
	}
	
	public boolean isFullCircleMapped() {
		return false;
	}
	
	public Dimension getValueTextSize() {
		return new Dimension(20, 17);
	}
	
	public Point getValueTextPosition() {
		return new Point(257 - 289, 124 - 132);
	}

}
