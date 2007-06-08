package com.borlander.rac525791.dashboard.rotatable;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Graphics;
import org.eclipse.swt.graphics.Color;

import com.borlander.rac525791.draw2d.ext.PolygonDecoration;

public class FilledDecoration extends PolygonDecoration {
	private Color myFillColor = ColorConstants.black;
	
	public void setFillColor(Color fillColor){
		myFillColor = fillColor;
	}
	
	@Override
	protected void fillShape(Graphics g) {
		g.pushState();
		g.setBackgroundColor(myFillColor);
		super.fillShape(g);
		g.popState();
	}
}
