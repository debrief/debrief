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
}
