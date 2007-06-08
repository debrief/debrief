package com.borlander.rac525791.dashboard.layout.data;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.widgets.Display;

import com.borlander.rac525791.dashboard.layout.ControlUIModel;
import com.borlander.rac525791.dashboard.layout.ControlUISuite;
import com.borlander.rac525791.dashboard.layout.DashboardFonts;
import com.borlander.rac525791.dashboard.layout.DashboardImages;

public class SuiteImpl implements ControlUISuite {
	private final ControlUIModel myDepth;

	private final ControlUIModel mySpeed;

	private final ControlUIModel myDirection;
	
	private final Dimension myPrefSize;
	
	private final DashboardImages myImages;

	private final DashboardFonts myFonts;

	private final Rectangle myCourseValueBounds;

	private final Rectangle myNameBounds;

	private final Rectangle myStatusBounds;

	private SuiteImpl(int width, int height, Rectangle courseValueBounds, TextPanesConfig panes, String path, ControlUIModel speed, ControlUIModel depth, ControlUIModel direction, DashboardFonts fonts) {
		myCourseValueBounds = courseValueBounds;
		myFonts = fonts;
		myPrefSize = new Dimension(width, height);
		myImages = new DashboardImagesImpl(Display.getCurrent(), path, true);
		
		myDepth = depth; 
		mySpeed = speed;
		myDirection = direction;

		//position panes symmetrically against control center 
		int pad = panes.getPadding();
		int top = panes.getTop();
		int bottom = panes.getBottom();
		int speedCenter = mySpeed.getControlCenter().x;
		int depthCenter = myDepth.getControlCenter().x;
		myNameBounds = new Rectangle(pad, top, (speedCenter - pad) * 2, bottom - top);
		myStatusBounds = new Rectangle(width - pad - (width - pad - depthCenter) * 2, top, (width - pad - depthCenter) * 2, bottom - top);
	}
	
	public double getTemplatesScale() {
		return myPrefSize.width / 280.0;
	}
	
	public Rectangle getCourseValueBounds() {
		return myCourseValueBounds;
	}
	
	public Rectangle getVesselNameBounds() {
		return myNameBounds;
	}
	
	public Rectangle getVesselStatusBounds() {
		return myStatusBounds;
	}
	
	public DashboardImages getImages() {
		return myImages;
	}
	
	public DashboardFonts getFonts() {
		return myFonts;
	}

	public Dimension getPreferredSize() {
		// XXX: can we cache it? does draw2d layouting change it?
		return myPrefSize.getCopy();
	}
	
	public Dimension getPreferredSizeRO() {
		return myPrefSize;
	}

	public ControlUIModel getDepth() {
		return myDepth;
	}

	public ControlUIModel getDirection() {
		return myDirection;
	}

	public ControlUIModel getSpeed() {
		return mySpeed;
	}
	
	public void dispose() {
		myImages.dispose();
		myFonts.dispose();
	}
	
	public static ControlUISuite create280x160() {
		return new SuiteImpl(//
				280, 160, // 
				new Rectangle(114, 17, 52, 12), //
				panes(4, 17, 45), // 
				"images/280x160", // 
				new Speed280x160(), //
				new Depth280x160(), //
				new Direction280x160(), // 
				fonts(6, 8, 7));
	}

	public static ControlUISuite create320x183() {
		return new SuiteImpl(//
				320, 183, //
				new Rectangle(130, 19, 59, 16), //
				panes(4, 19, 52), //
				"images/320x183", //
				new Speed320x183(), // 
				new Depth320x183(), //
				new Direction320x183(), //
				fonts(7, 9, 8));
	}

	public static ControlUISuite create360x206() {
		return new SuiteImpl(//
				360, 206, //
				new Rectangle(146, 22, 67, 16), //
				panes(4, 22, 58), //
				"images/360x206", //
				new Speed360x206(), //
				new Depth360x206(), //
				new Direction360x206(), //
				fonts(7, 10, 9));
	}

	public static ControlUISuite create400x229() {
		return new SuiteImpl(//
				400, 229, //
				new Rectangle(163, 25, 73, 20), //
				panes(6, 26, 65), 
				"images/400x229", //
				new Speed400x229(), //
				new Depth400x229(), //
				new Direction400x229(), //
				fonts(8, 12, 10));
	}

	private static DashboardFontsImpl fonts(int text, int value, int units){
		return new DashboardFontsImpl(text, value, units);
	}
	
	private static TextPanesConfig panes(int pad, int top, int bottom){
		return new TextPanesConfig(pad, top, bottom);
	}
	
	private static class TextPanesConfig {
		private final int myPadding;
		private final int myTop;
		private final int myBottom;

		public TextPanesConfig(int padding, int top, int bottom){
			myPadding = padding;
			myTop = top;
			myBottom = bottom;
		}
		
		public int getBottom() {
			return myBottom;
		}
		
		public int getPadding() {
			return myPadding;
		}
		
		public int getTop() {
			return myTop;
		}
	}

}
