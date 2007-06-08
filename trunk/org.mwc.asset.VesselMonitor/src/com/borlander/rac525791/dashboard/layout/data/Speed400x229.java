package com.borlander.rac525791.dashboard.layout.data;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;

import com.borlander.rac525791.dashboard.layout.ControlUIModel;

class Speed400x229 implements ControlUIModel {

	public Point getControlCenter() {
		return new Point(79, 148);
	}

	public Dimension getUnitsAndMultipliersSize() {
		return new Dimension(38, 13);
	}

	public Point getUnitsPosition() {
		return new Point(-1, 20);
	}

	public int getRedSectorRadius() {
		return 53;
	}
	
	public Dimension getZeroMark() {
		return new Dimension(-17, 55);
	}
	
	public Dimension getMaximumMark() {
		return new Dimension(35, -46);
	}
	
	public boolean isFullCircleMapped() {
		return false;
	}
	
	public Dimension getValueTextSize() {
		return new Dimension(23, 21);
	}
	
	public Point getValueTextPosition() {
		return new Point(92 - 79, 137 - 148);
	}
	
	
}

