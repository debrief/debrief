/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package com.borlander.rac525791.dashboard.layout;

import org.eclipse.draw2d.ImageFigure;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Image;

public abstract class SelectableImageFigure extends ImageFigure {
	private final DashboardUIModel mySelector;

	public SelectableImageFigure(DashboardUIModel selector){
		super();
		mySelector = selector;
	}
	
	@Override
	public void setBounds(Rectangle rect) {
		ControlUISuite suite = mySelector.getUISuite(rect.width, rect.height);
		Image image = selectImage(suite.getImages());
		setImage(image);
		super.setBounds(rect);
	}
	
	protected abstract Image selectImage(DashboardImages images);

}
