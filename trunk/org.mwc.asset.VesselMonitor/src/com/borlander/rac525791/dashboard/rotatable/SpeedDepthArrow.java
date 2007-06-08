package com.borlander.rac525791.dashboard.rotatable;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.swt.graphics.Color;

import com.borlander.rac525791.draw2d.ext.PolygonDecoration;

public class SpeedDepthArrow extends CompositeDecoration {
	public static final double EXPECTED_LENGTH = 36;
	
	public SpeedDepthArrow(){
		super(createRed(), createPink());
	}

	private static PolygonDecoration createRed() {
		Color DARK = new Color(null, 220, 0, 0);

		PointList redTemplate = new PointList();
		redTemplate.addPoint(-16, -1);
		redTemplate.addPoint(-41, -2);
		redTemplate.addPoint(-41, 2);
		redTemplate.addPoint(-16, 1);
		
		redTemplate.translate(30, 0);
		
		GradientDecoration red = new GradientDecoration();
		red.setTemplate(redTemplate);
		red.setGradient(0, ColorConstants.red, 2, DARK);

		red.setShadowColor(new Color(null, 100, 100, 100));
		
		return red;
	}

	private static PolygonDecoration createPink() {
		Color LIGHT = new Color(null, 255, 200, 200);
		Color PINK = new Color(null, 252, 252, 252);

		PointList pinkTemplate = new PointList();
		pinkTemplate.addPoint(0, -1);
		pinkTemplate.addPoint(-16, -1);
		pinkTemplate.addPoint(-16, 1);
		pinkTemplate.addPoint(0, -1);
		
		pinkTemplate.translate(30, 0);
		
		GradientDecoration pink = new GradientDecoration();
		pink.setTemplate(pinkTemplate);
		
		pink.setGradient(0, PINK, 2, LIGHT);
		
		pink.setShadowColor(new Color(null, 100, 100, 100));
		
		return pink;
	}
}
