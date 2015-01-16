package org.mwc.cmap.naturalearth.wrapper;

import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.map.FeatureLayer;
import org.geotools.styling.Style;
import org.mwc.cmap.naturalearth.view.NEFeatureGroup;
import org.mwc.cmap.naturalearth.view.NEFeatureStore;
import org.mwc.cmap.naturalearth.view.NEFeatureStyle;

public class NEFeatureLayer extends FeatureLayer
{

	private NEFeatureStyle neFeatureStyle;

	public NEFeatureLayer(NEFeatureStyle neFeatureStyle,
			SimpleFeatureSource featureSource, Style sld)
	{
		super(featureSource, sld);
		this.neFeatureStyle = neFeatureStyle;
		setVisible(neFeatureStyle.isVisible());
	}

	@Override
	public boolean isVisible()
	{
		NEFeatureGroup parent = neFeatureStyle.getParent();
		if (parent != null)
		{
			if (!parent.getVisible())
			{
				return false;
			}
			NEFeatureStore fs = parent.getParent();
			if (fs != null && !fs.getVisible())
			{
				return false;
			}
		}
		return neFeatureStyle.isVisible();
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
