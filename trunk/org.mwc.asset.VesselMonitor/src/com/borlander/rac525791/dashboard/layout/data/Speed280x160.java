package com.borlander.rac525791.dashboard.layout.data;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;

import com.borlander.rac525791.dashboard.layout.ControlUIModel;

class Speed280x160 implements ControlUIModel {

	public Point getControlCenter() {
		return new Point(54, 103);
	}

	public Dimension getUnitsAndMultipliersSize() {
		return new Dimension(28, 9);
	}

	public Point getUnitsPosition() {
		return new Point(3, 13);
	}

	public int getRedSectorRadius() {
		return 36;
	}
	
	public Dimension getZeroMark() {
		return new Dimension(-10, 38);
	}
	
	public Dimension getMaximumMark() {
		return new Dimension(26, -31);
	}
	
	public boolean isFullCircleMapped() {
		return false;
	}
	
	public Dimension getValueTextSize() {
		return new Dimension(16, 14);
	}
	
	public Point getValueTextPosition() {
		return new Point(65 - 54, 96 - 103);
	}
	
	
}

