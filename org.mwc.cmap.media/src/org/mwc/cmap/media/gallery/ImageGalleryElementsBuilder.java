package org.mwc.cmap.media.gallery;

import org.eclipse.swt.graphics.Image;

public interface ImageGalleryElementsBuilder<T, I> {
	
	String buildLabel(T image);
	
	Image buildImage(I image);
	
	void disposeImage(I i);
	
	void disposeMeta(T t);
}
