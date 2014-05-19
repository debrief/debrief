package com.borlander.rac525791.dashboard.rotatable;

import java.util.LinkedList;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Color;

import com.borlander.rac525791.draw2d.ext.PolygonDecoration;
import com.borlander.rac525791.draw2d.ext.RotatableDecorationExt;

public class CompositeDecoration extends Figure implements RotatableDecorationExt {
	private final LinkedList<PolygonDecoration> myDecorations;
	
	public CompositeDecoration(PolygonDecoration...decorations){
		this(null, decorations);
	}
	
	public CompositeDecoration(Color shadowColor, PolygonDecoration...decorations){
		myDecorations = new LinkedList<PolygonDecoration>();
		if (shadowColor != null){
			for (PolygonDecoration next : decorations){
				FilledDecoration nextShadow = new FilledDecoration();
				nextShadow.setTemplate(next.getTemplateCopy());
				nextShadow.setFillColor(shadowColor);
				nextShadow.setFill(true);
				
				addDecoration(nextShadow);
			}
		}
		
		for (PolygonDecoration next : decorations){
			addDecoration(next);
		}
	}
	
	@Override
	public void paint(Graphics graphics) {
		for (PolygonDecoration next : myDecorations){
			((Figure)next).paint(graphics);
		}
	}
	
	@Override
	public Rectangle getBounds() {
		Rectangle max = null;
		if (myDecorations != null){
			for (PolygonDecoration next : myDecorations){
				if (max == null){
					max = new Rectangle(next.getBounds());
				} else {
					max.union(next.getBounds());
				}
			}
		}
		return max == null ? super.getBounds() : max;
	}
	
	@Override
	public void setBounds(Rectangle rect) {
		super.setBounds(rect);
		for (PolygonDecoration next : myDecorations){
			next.setBounds(rect);
		}
	}
	
	public void setRotation(double angle){
		for (PolygonDecoration next : myDecorations){
			next.setRotation(angle);
		}
	}
	
	public void setLocation(Point p) {
		for (PolygonDecoration next : myDecorations){
			next.setLocation(p);
		}
	}
	
	public void setReferencePoint(Point p) {
		for (PolygonDecoration next : myDecorations){
			next.setReferencePoint(p);
		}
	}
	
	@Override
	public void setForegroundColor(Color fg) {
		for (PolygonDecoration next : myDecorations){
			next.setForegroundColor(fg);
		}
	}

	@Override
	public void setBackgroundColor(Color bg) {
		for (PolygonDecoration next : myDecorations){
			next.setBackgroundColor(bg);
		}
	}
	
	public void setScale(double xScale, double yScale) {
		for (PolygonDecoration next : myDecorations){
			next.setScale(xScale, yScale);
		}
	}
	
	private void addDecoration(PolygonDecoration decoration){
		myDecorations.add(decoration);
		this.add(decoration);
	}

}