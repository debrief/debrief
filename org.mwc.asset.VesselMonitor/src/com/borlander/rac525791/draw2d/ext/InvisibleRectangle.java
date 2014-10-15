/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
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
