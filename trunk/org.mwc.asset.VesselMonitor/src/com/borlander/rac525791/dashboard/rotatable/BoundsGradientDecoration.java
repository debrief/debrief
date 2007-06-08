package com.borlander.rac525791.dashboard.rotatable;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Pattern;


public class BoundsGradientDecoration extends AbstractGradientDecoration {
	private final Point LOCATION = new Point();
	private final Point REFERENCE = new Point();
	private Color myLocationColor;
	private Color myReferenceColor;
	private boolean myFromTopRightToBottomLeft;

	public BoundsGradientDecoration(){
		setReferencePointColor(ColorConstants.blue);
		setLocationColor(ColorConstants.red);
		
		setForegroundColor(ColorConstants.black);
		setBackgroundColor(ColorConstants.black);
	}
	
	@Override
	protected Pattern createPattern() {
		Quadrant quadrant = (myFromTopRightToBottomLeft) ? getQuadrant().flip() : getQuadrant();
		Rectangle localBounds = getBounds();
		quadrant.point(localBounds, LOCATION);
		quadrant.oppositePoint(localBounds, REFERENCE);
		return new Pattern(null, LOCATION.x, LOCATION.y, REFERENCE.x, REFERENCE.y, myLocationColor, myReferenceColor);
	}

	public void setFromTopRightToBottomLeft(boolean value) {
		myFromTopRightToBottomLeft = value;
	}
	
	public void setLocationColor(Color locationColor){
		myLocationColor = locationColor;
	}
	
	public void setReferencePointColor(Color referenceColor){
		myReferenceColor = referenceColor;
	}
	

}
