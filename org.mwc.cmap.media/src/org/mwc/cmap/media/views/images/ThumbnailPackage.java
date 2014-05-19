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
