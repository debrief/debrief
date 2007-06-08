package com.borlander.rac525791.dashboard.layout;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;

public interface ControlUISuite {
	public Dimension getPreferredSize();

	/**
	 * The same as <code>getPreferredSize</code> but in contrast to untrusted
	 * code from draw2d this method is called by trusted client that NEVER
	 * changes return value.
	 */
	public Dimension getPreferredSizeRO();
	
	public double getTemplatesScale();

	public ControlUIModel getSpeed();

	public ControlUIModel getDepth();

	public ControlUIModel getDirection();

	public DashboardImages getImages();
	
	public DashboardFonts getFonts();
	
	public Rectangle getCourseValueBounds();
	
	public Rectangle getVesselNameBounds();
	
	public Rectangle getVesselStatusBounds();
	
	public static interface ControlAccess {
		ControlUIModel selectControl(ControlUISuite suite);
	}
	
	public void dispose();
	
	public static final ControlAccess SPEED = new ControlAccess(){
		public ControlUIModel selectControl(ControlUISuite suite) {
			return suite.getSpeed();
		}
	};

	public static final ControlAccess DEPTH = new ControlAccess(){
		public ControlUIModel selectControl(ControlUISuite suite) {
			return suite.getDepth();
		}
	};
	
	public static final ControlAccess DIRECTION = new ControlAccess(){
		public ControlUIModel selectControl(ControlUISuite suite) {
			return suite.getDirection();
		}
	};

}
