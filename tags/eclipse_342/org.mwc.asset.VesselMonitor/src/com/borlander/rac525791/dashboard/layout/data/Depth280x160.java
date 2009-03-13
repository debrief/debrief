package com.borlander.rac525791.dashboard.layout.data;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;

import com.borlander.rac525791.dashboard.layout.ControlUIModel;

class Depth280x160 implements ControlUIModel {
	
	public Point getControlCenter() {
		return new Point(225, 103);
	}

	public Dimension getUnitsAndMultipliersSize() {
		return new Dimension(28, 9);
	}

	public Point getUnitsPosition() {
		return new Point(-26, 13);
	}

	public int getRedSectorRadius() {
		return 36;
	}
	
	public Dimension getZeroMark() {
		return new Dimension(-25, -32);
	}

	public Dimension getMaximumMark() {
		return new Dimension(9, 38);
	}
	
	public boolean isFullCircleMapped() {
		return false;
	}
	
	public Dimension getValueTextSize() {
		return new Dimension(17, 14);
	}
	
	public Point getValueTextPosition() {
		return new Point(199 - 225, 96 - 103);
	}

}
