package com.borlander.rac525791.dashboard.rotatable;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;

public enum Quadrant {
	LEFT_TOP { 
		public void point(Rectangle r, Point p){ p.setLocation(r.x, r.y); } 
		public Quadrant opposite() {return RIGHT_BOTTOM; } 
		public Quadrant flip() {return RIGHT_TOP; }
	}, 
	LEFT_BOTTOM { 
		public void point(Rectangle r, Point p){ p.setLocation(r.x, r.y + r.width); } 
		public Quadrant opposite() {return RIGHT_TOP; }
		public Quadrant flip() {return RIGHT_BOTTOM; }
	},
	RIGHT_TOP { 
		public void point(Rectangle r, Point p){ p.setLocation(r.x + r.width, r.y); } 
		public Quadrant opposite() {return LEFT_BOTTOM; }
		public Quadrant flip() {return LEFT_TOP; }
	},
	RIGHT_BOTTOM { 
		public void point(Rectangle r, Point p){ p.setLocation(r.x + r.width, r.y + r.height); } 
		public Quadrant opposite() {return RIGHT_TOP; }
		public Quadrant flip() {return RIGHT_TOP; }
	};
	
	public static Quadrant valueOfAngle(double angle){
		if (angle > 0){
			return angle >= Math.PI / 2 ? LEFT_BOTTOM : RIGHT_BOTTOM; 
		} 
		return angle >= -Math.PI / 2 ? RIGHT_TOP : LEFT_TOP;
	}
	
	public Quadrant opposite(){
		throw notSupported();
	}
	
	public void point(Rectangle r, Point p){
		throw notSupported();
	}
	
	public Quadrant flip(){
		throw notSupported();
	}
	
	public void oppositePoint(Rectangle r, Point p){
		opposite().point(r, p);
	}
	
	private UnsupportedOperationException notSupported(){
		return new UnsupportedOperationException("Implementation should override");
	}
	
}