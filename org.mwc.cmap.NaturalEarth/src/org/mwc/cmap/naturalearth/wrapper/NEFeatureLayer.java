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
package org.mwc.cmap.naturalearth.wrapper;

import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.map.FeatureLayer;
import org.geotools.styling.Style;
import org.mwc.cmap.naturalearth.view.NEFeature;

public class NEFeatureLayer extends FeatureLayer {

	private final NEFeature neFeature;
	private final String fileName;

	public NEFeatureLayer(final NEFeature neFeature, final String fileName, final SimpleFeatureSource featureSource,
			final Style sld) {
		super(featureSource, sld);
		this.neFeature = neFeature;
		this.fileName = fileName;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final NEFeatureLayer other = (NEFeatureLayer) obj;
		if (fileName == null) {
			if (other.fileName != null)
				return false;
		} else if (!fileName.equals(other.fileName))
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((fileName == null) ? 0 : fileName.hashCode());
		return result;
	}

	@Override
	public boolean isVisible() {
		NEFeature parent = neFeature.getParent();
		while (parent != null) {
			if (!parent.getVisible()) {
				return false;
			}
			parent = parent.getParent();
		}
		return neFeature.getVisible();
	}

}
