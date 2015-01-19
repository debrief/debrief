package org.mwc.cmap.naturalearth.wrapper;

import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.map.FeatureLayer;
import org.geotools.styling.Style;
import org.mwc.cmap.naturalearth.view.NEFeature;

public class NEFeatureLayer extends FeatureLayer
{

	private NEFeature neFeature;

	public NEFeatureLayer(NEFeature neFeature,
			SimpleFeatureSource featureSource, Style sld)
	{
		super(featureSource, sld);
		this.neFeature = neFeature;
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

}
