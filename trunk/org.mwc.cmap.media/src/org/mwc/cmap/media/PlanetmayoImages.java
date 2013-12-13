package org.mwc.cmap.media;

import org.eclipse.jface.resource.ImageDescriptor;


public enum PlanetmayoImages {
	STOP("icons/transport_icon_stop_1.png"),
	PLAY("icons/transport_icon_play_1.png"),
	PAUSE("icons/transport_icon_pause.png"),
	OPEN("icons/open.png"),
	REFRESH("icons/refresh.png"),
	TRANSPARENT("icons/transparent.gif"),
	HIGHLIGHTED_IMAGE("icons/image-highlight.png"),
	SELECTED_IMAGE("icons/image-select.png"),
	VIEW_FULLSIZE("icons/view_image.png"),
	VIEW_THUMBNAILS("icons/view_thumbnail.png"),	
	UNKNOWN("icons/unknown.png");
	
	private final String path;
	
	private PlanetmayoImages(String path) {
		this.path = path;
	}
	
	public ImageDescriptor getImage() {
		return Activator.getImageDescriptor(path);
	}
}
