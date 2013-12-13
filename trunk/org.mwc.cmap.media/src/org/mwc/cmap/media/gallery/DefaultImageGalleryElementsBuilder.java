package org.mwc.cmap.media.gallery;

import org.eclipse.swt.graphics.Image;

public class DefaultImageGalleryElementsBuilder<T, I> implements ImageGalleryElementsBuilder<T, I> {

	@Override
	public String buildLabel(T image) {
		return image.toString();
	}

	@Override
	public Image buildImage(I image) {
		if (image instanceof Image) {
			return (Image) image;
		}
		return null;
	}

	@Override
	public void disposeImage(I image) {
		if (image instanceof Image) {
			((Image) image).dispose();
		}
	}

	@Override
	public void disposeMeta(T t) {
		
	}
}