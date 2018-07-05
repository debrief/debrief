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
package org.mwc.cmap.media;

import org.eclipse.jface.resource.ImageDescriptor;


public enum PlanetmayoImages {
	STOP("icons/24/media_stop.png"),
	PLAY("icons/24/media_play.png"),
	PAUSE("icons/24/media_pause.png"),
	OPEN("icons/open.png"),
	REFRESH("icons/refresh.png"),
	TRANSPARENT("icons/transparent.gif"),
	HIGHLIGHTED_IMAGE("icons/image-highlight.png"),
	SELECTED_IMAGE("icons/image-select.png"),
	VIEW_FULLSIZE("icons/view_image.png"),
	VIEW_THUMBNAILS("icons/view_thumbnail.png"),
	CONTROL_TIME("icons/16/control_time.png"),
	STRETCH("icons/16/fit_to_win.png"),
	UNKNOWN("icons/unknown.png");
	
	private final String path;
	
	private PlanetmayoImages(String path) {
		this.path = path;
	}
	
	public ImageDescriptor getImage() {
		return Activator.getImageDescriptor(path);
	}
}
