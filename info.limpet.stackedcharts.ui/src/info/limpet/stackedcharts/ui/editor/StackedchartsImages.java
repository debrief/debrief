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
package info.limpet.stackedcharts.ui.editor;

import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;

public class StackedchartsImages {
	public final static String GEF_PATH = "icons/gef/"; //$NON-NLS-1$

	private final static ImageRegistry PLUGIN_REGISTRY = new ImageRegistry();

	public static final ImageDescriptor DESC_ADD = create(GEF_PATH, "add.png"); //$NON-NLS-1$

	public static final ImageDescriptor DESC_AXIS = create(GEF_PATH, "axis.png"); //$NON-NLS-1$

	public static final ImageDescriptor DESC_CHART = create(GEF_PATH, "chart.png"); //$NON-NLS-1$

	public static final ImageDescriptor DESC_CHARTSET = create(GEF_PATH, "chartset.png"); //$NON-NLS-1$

	public static final ImageDescriptor DESC_SCATTERSET = create(GEF_PATH, "scatterset.png"); //$NON-NLS-1$
	public static final ImageDescriptor DESC_DELETE = create(GEF_PATH, "delete.png"); //$NON-NLS-1$
	public static final ImageDescriptor DESC_PAINT = create(GEF_PATH, "paint.png"); //$NON-NLS-1$
	public static final ImageDescriptor DESC_DATASET = create(GEF_PATH, "dataset.png"); //$NON-NLS-1$ //FIXME UPDATE
																						// DATASET ICON

	private static ImageDescriptor create(final String prefix, final String name) {
		return ImageDescriptor.createFromURL(makeImageURL(prefix, name));
	}

	public static Image getImage(final ImageDescriptor desc) {
		final String key = String.valueOf(desc.hashCode());
		Image image = PLUGIN_REGISTRY.get(key);
		if (image == null) {
			image = desc.createImage();
			PLUGIN_REGISTRY.put(key, image);
		}
		return image;
	}

	private static URL makeImageURL(final String prefix, final String name) {
		final String path = "$nl$/" + prefix + name; //$NON-NLS-1$
		return FileLocator.find(Activator.getDefault().getBundle(), new Path(path), null);
	}

	private StackedchartsImages() {
	}

}
