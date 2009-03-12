package com.borlander.rac525791.draw2d.ext;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.Shape;

public class InvisibleRectangle extends Shape {
	private final boolean myUseLocalCoordinates;

	public InvisibleRectangle(){
		this(false);
	}	
	
	public InvisibleRectangle(boolean useLocalCoordinates){
		myUseLocalCoordinates = useLocalCoordinates;
	}
	
	@Override
	protected void outlineShape(Graphics graphics) {
		//invisible
	}

	@Override
	protected void fillShape(Graphics graphics) {
		//invisible
	}
	
	@Override
	protected boolean useLocalCoordinates() {
		return myUseLocalCoordinates;
	}
}
