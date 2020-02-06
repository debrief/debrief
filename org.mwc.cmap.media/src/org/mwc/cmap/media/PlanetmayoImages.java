
package org.mwc.cmap.media;

import org.eclipse.jface.resource.ImageDescriptor;


public enum PlanetmayoImages {
	STOP("icons/24/media_stop.png"),
	PLAY("icons/24/media_play.png"),
	PAUSE("icons/24/media_pause.png"),
	OPEN("icons/16/folder.png"),
	REFRESH("icons/16/repaint.png"),
	TRANSPARENT("icons/transparent.gif"),
	HIGHLIGHTED_IMAGE("icons/image-highlight.png"),
	SELECTED_IMAGE("icons/image-select.png"),
	VIEW_FULLSIZE("icons/16/large-icon-4.png"),
	VIEW_MEDIUM("icons/16/medium-icon.png"),
  VIEW_THUMBNAILS("icons/16/small-icon.png"),
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
