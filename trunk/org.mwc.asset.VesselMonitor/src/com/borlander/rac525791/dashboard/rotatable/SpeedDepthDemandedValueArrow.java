package com.borlander.rac525791.dashboard.rotatable;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.swt.graphics.Color;

import com.borlander.rac525791.draw2d.ext.PolygonDecoration;

public class SpeedDepthDemandedValueArrow extends CompositeDecoration {
	public static final double EXPECTED_LENGTH = 36;
	private static final Point CENTER = new Point(36, 0);
	private static final int RADIUS = 1;
	public static final Color LIGHT_GREEN = new Color(null, 191, 255, 180);
	public static final Color DARK_GREEN = new Color(null, 92, 255, 64);
	
	public SpeedDepthDemandedValueArrow(){
		super(createLine(), createCircle());
	}
	
	private static PolygonDecoration createCircle(){
		return new CircleDecoration(CENTER, RADIUS, DARK_GREEN, LIGHT_GREEN);
	}
	
	private static PolygonDecoration createLine(){
		return createLine(CENTER.x, LIGHT_GREEN, DARK_GREEN);
	}
	
	private static PolygonDecoration createLine(int distance, Color far, Color center){
		GradientDecoration result = new GradientDecoration();
		PointList template = new PointList();
		template.addPoint(distance, 0); 
		template.addPoint(0, 0); 
		template.addPoint(distance, 0);
		
		result.setTemplate(template);
		result.setGradient(0, far, 1, center);
		
		return result;
	}

}
