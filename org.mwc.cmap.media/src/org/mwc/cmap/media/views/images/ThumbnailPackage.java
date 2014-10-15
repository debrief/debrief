/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 */
package org.mwc.cmap.media.views.images;

import org.eclipse.swt.graphics.Image;

public class ThumbnailPackage {
	
	private final Image scaled;
	private final Image stretched;
	
	public ThumbnailPackage(Image scaled, Image stretched) {
		this.scaled = scaled;
		this.stretched = stretched;
	}
	
	public Image getScaled() {
		return scaled;
	}

	public Image getStretched() {
		return stretched;
	}

	public void dispose() {
		scaled.dispose();
		stretched.dispose();
	}
}
