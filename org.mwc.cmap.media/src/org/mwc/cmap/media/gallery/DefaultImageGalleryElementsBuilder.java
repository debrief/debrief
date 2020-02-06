/*******************************************************************************
 * Debrief - the Open Source Maritime Analysis Application
 * http://debrief.info
 *
 * (C) 2000-2020, Deep Blue C Technology Ltd
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html)
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *******************************************************************************/

package org.mwc.cmap.media.gallery;

import org.eclipse.swt.graphics.Image;

public class DefaultImageGalleryElementsBuilder<T, I> implements ImageGalleryElementsBuilder<T, I> {

	@Override
	public Image buildImage(final I image) {
		if (image instanceof Image) {
			return (Image) image;
		}
		return null;
	}

	@Override
	public String buildLabel(final T image) {
		return image.toString();
	}

	@Override
	public void disposeImage(final I image) {
		if (image instanceof Image) {
			((Image) image).dispose();
		}
	}

	@Override
	public void disposeMeta(final T t) {

	}
}