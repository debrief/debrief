package org.mwc.cmap.media.gallery;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;

public class SimpleImageGallery<T> extends ImageGallery<T, Image> {

	public SimpleImageGallery(Composite parent, int thumbnailWidth,	int thumbnailHeight) {
		super(parent, thumbnailWidth, thumbnailHeight);
	}

	public SimpleImageGallery(Composite parent) {
		super(parent);
	}
}
