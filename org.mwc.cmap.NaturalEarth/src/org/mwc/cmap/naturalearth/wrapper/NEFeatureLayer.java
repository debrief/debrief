package org.mwc.cmap.naturalearth.wrapper;

import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.map.FeatureLayer;
import org.geotools.styling.Style;
import org.mwc.cmap.naturalearth.view.NEFeature;
import org.mwc.cmap.naturalearth.view.NEFeatureStyle;

public class NEFeatureLayer extends FeatureLayer
{

	private NEFeatureStyle neFeatureStyle;

	public NEFeatureLayer(NEFeatureStyle neFeatureStyle,
			SimpleFeatureSource featureSource, Style sld)
	{
		super(featureSource, sld);
		this.neFeatureStyle = neFeatureStyle;
	}

	@Override
	public boolean isVisible()
	{
		NEFeature parent = neFeatureStyle.getParent();
		while (parent != null)
		{
			if (!parent.getVisible())
			{
				return false;
			}
			parent = parent.getParent();
		}
		return neFeatureStyle.getVisible();
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((neFeatureStyle == null) ? 0 : neFeatureStyle.hashCode());
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
		if (neFeatureStyle == null)
		{
			if (other.neFeatureStyle != null)
				return false;
		}
		else if (!neFeatureStyle.equals(other.neFeatureStyle))
			return false;
		return true;
	}
}
