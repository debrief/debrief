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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.services.IDisposable;
import org.mwc.cmap.media.PlanetmayoImages;

public class BackgroundImages implements IDisposable {

	private final ImageGallery<?, ?> gallery;
	private final Image transparent;
	private final Image highlightedSource;
	private final Image selectedSource;
	private final Map<Point, Image> highlightedImages = new HashMap<Point, Image>();
	private final Map<Point, Image> selectedImages = new HashMap<Point, Image>();

	public BackgroundImages(final ImageGallery<?, ?> gallery) {
		this.gallery = gallery;
		transparent = PlanetmayoImages.TRANSPARENT.getImage().createImage();
		highlightedSource = PlanetmayoImages.HIGHLIGHTED_IMAGE.getImage().createImage();
		selectedSource = PlanetmayoImages.SELECTED_IMAGE.getImage().createImage();
	}

	private Image createFromSource(final Point size, final Image source) {
		final Image newImage = new Image(gallery.getMainComposite().getDisplay(), size.x, size.y);
		final GC gc = new GC(newImage);
		gc.drawImage(source, 0, 0, 3, 3, 0, 0, 3, 3);
		gc.drawImage(source, 0, 112, 3, 3, 0, size.y - 3, 3, 3);
		gc.drawImage(source, 97, 0, 3, 3, size.x - 3, 0, 3, 3);
		gc.drawImage(source, 97, 112, 3, 3, size.x - 3, size.y - 3, 3, 3);
		gc.drawImage(source, 0, 3, 3, 109, 0, 3, 3, size.y - 6);
		gc.drawImage(source, 97, 3, 3, 109, size.x - 3, 3, 3, size.y - 6);
		gc.drawImage(source, 3, 0, 94, 3, 3, 0, size.x - 6, 3);
		gc.drawImage(source, 3, 112, 94, 3, 3, size.y - 3, size.x - 6, 3);
		gc.drawImage(source, 3, 3, 94, 109, 3, 3, size.x - 6, size.y - 6);
		gc.dispose();
		return newImage;
	}

	@Override
	public void dispose() {
		transparent.dispose();
		highlightedSource.dispose();
		selectedSource.dispose();
		for (final Image image : highlightedImages.values()) {
			image.dispose();
		}
		for (final Image image : selectedImages.values()) {
			image.dispose();
		}
	}

	public Image getHighlightedImage(final Point size) {
		if (highlightedImages.containsKey(size)) {
			return highlightedImages.get(size);
		}
		final Image newImage = createFromSource(size, highlightedSource);
		highlightedImages.put(size, newImage);
		return newImage;
	}

	public Image getSelectedImage(final Point size) {
		if (selectedImages.containsKey(size)) {
			return selectedImages.get(size);
		}
		final Image newImage = createFromSource(size, selectedSource);
		selectedImages.put(size, newImage);
		return newImage;
	}

	public Image getTransparent() {
		return transparent;
	}
}
