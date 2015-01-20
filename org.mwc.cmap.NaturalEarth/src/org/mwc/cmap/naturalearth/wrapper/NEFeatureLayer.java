package org.mwc.cmap.naturalearth.wrapper;

import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.map.FeatureLayer;
import org.geotools.styling.Style;
import org.mwc.cmap.naturalearth.view.NEFeature;

public class NEFeatureLayer extends FeatureLayer
{

	private NEFeature neFeature;
	private String fileName;

	public NEFeatureLayer(NEFeature neFeature,
			String fileName, SimpleFeatureSource featureSource, Style sld)
	{
		super(featureSource, sld);
		this.neFeature = neFeature;
		this.fileName = fileName;
	}

	@Override
	public boolean isVisible()
	{
		NEFeature parent = neFeature.getParent();
		while (parent != null)
		{
			if (!parent.getVisible())
			{
				return false;
			}
			parent = parent.getParent();
		}
		return neFeature.getVisible();
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((fileName == null) ? 0 : fileName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		NEFeatureLayer other = (NEFeatureLayer) obj;
		if (fileName == null)
		{
			if (other.fileName != null)
				return false;
		}
		else if (!fileName.equals(other.fileName))
			return false;
		return true;
	}

}
