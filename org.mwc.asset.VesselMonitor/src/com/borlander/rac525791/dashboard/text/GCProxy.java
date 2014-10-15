/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package com.borlander.rac525791.dashboard.text;

import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Shell;

public class GCProxy {
	private GC myGC;
	
	public Point getExtent(String text, Font font){
		return getGC(font).stringExtent(text);
	}
	
	public int getAverageCharWidth(Font font){
		return getGC(font).getFontMetrics().getAverageCharWidth();
	}

	public void dispose() {
		if (myGC != null) {
			myGC.dispose();
			myGC = null;
		}
	}

	private GC getGC(Font font) {
		if (myGC == null || myGC.isDisposed()) {
			myGC = new GC(new Shell());
	//		System.out.println("creating new GC");
		}
		myGC.setFont(font);
		return myGC;
	}
	
}