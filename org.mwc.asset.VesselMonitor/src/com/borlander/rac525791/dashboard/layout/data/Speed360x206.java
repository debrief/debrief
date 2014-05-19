package com.borlander.rac525791.dashboard.layout.data;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;

import com.borlander.rac525791.dashboard.layout.ControlUIModel;

class Speed360x206 implements ControlUIModel {

	public Point getControlCenter() {
		return new Point(71, 132);
	}

	public Dimension getUnitsAndMultipliersSize() {
		return new Dimension(33, 11);
	}

	public Point getUnitsPosition() {
		return new Point(0, 18);
	}

	public int getRedSectorRadius() {
		return 48;
	}
	
	public Dimension getZeroMark() {
		return new Dimension(-16, 50);
	}
	
	public Dimension getMaximumMark() {
		return new Dimension(32, -41);
	}
	
	public boolean isFullCircleMapped() {
		return false;
	}
	
	public Dimension getValueTextSize() {
		return new Dimension(20, 17);
	}
	
	public Point getValueTextPosition() {
		return new Point(83 - 71, 124 - 132);
	}
	
	
}

