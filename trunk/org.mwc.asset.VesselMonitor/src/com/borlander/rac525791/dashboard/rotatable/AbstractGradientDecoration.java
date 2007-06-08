package com.borlander.rac525791.dashboard.rotatable;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Pattern;

import com.borlander.rac525791.draw2d.ext.PolygonDecoration;

public abstract class AbstractGradientDecoration extends PolygonDecoration {
	private final Rectangle CACHED_BOUNDS = new Rectangle();
	private Pattern myPattern;
	private Color myShadowColor;
	private Quadrant myQuadrant = Quadrant.LEFT_TOP;

	protected abstract Pattern createPattern();
	
	public AbstractGradientDecoration(){
		setFill(true);
	}

	public void setShadowColor(Color color){
		myShadowColor = color;
	}
	
	@Override
	public void setRotation(double angle) {
		super.setRotation(angle);
		myQuadrant = Quadrant.valueOfAngle(angle);
	}
	
	@Override
	public void paint(Graphics graphics) {
		int old = graphics.getAntialias();
		graphics.setAntialias(SWT.ON);
		super.paint(graphics);
		graphics.setAntialias(old);
	}

	public void dispose(){
		if (myPattern != null){
			myPattern.dispose();
		}
	}
	
	protected final Quadrant getQuadrant(){
		return myQuadrant;
	}

	@Override
	protected void fillShape(Graphics g) {
		fillShadow(g);
		
		Pattern pattern = getPattern(getBounds());
		g.pushState();
		g.setBackgroundPattern(pattern);
		super.fillShape(g);
		g.popState();
	}
	
	protected void fillShadow(Graphics g){
		Color shadowColor = getShadowColor();
		if (shadowColor != null){
			final int DX = 2;
			final int DY = getShadowDY();
			g.pushState();
			g.setBackgroundColor(shadowColor);
			PointList pointListRef = getPoints();
			pointListRef.performTranslate(DX, DY);
			g.fillPolygon(pointListRef);
			pointListRef.performTranslate(-DX, -DY);
			g.popState();
		}
	}
	
	private int getShadowDY(){
		Quadrant quadrant = getQuadrant();
		return (quadrant == Quadrant.RIGHT_BOTTOM || quadrant == Quadrant.LEFT_TOP) ? -2 : 2;
	}
	
	@Override
	protected void outlineShape(Graphics g) {
		g.pushState();
		g.setForegroundPattern(getPattern(getBounds()));
		super.outlineShape(g);
		g.popState();
	}
	
	protected final Pattern getPattern(Rectangle loacalBounds){
		if (myPattern != null && !myPattern.isDisposed() && CACHED_BOUNDS.equals(loacalBounds)){
			return myPattern;
		}
		CACHED_BOUNDS.setBounds(loacalBounds);
		if (myPattern != null){
			myPattern.dispose();
		}
		myPattern = createPattern();
		return myPattern;
	}
	
	private Color getShadowColor(){
		return myShadowColor;
	}
	
}