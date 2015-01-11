package org.mwc.cmap.naturalearth.wrapper;

import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.feature.FeatureCollection;
import org.geotools.map.FeatureLayer;
import org.geotools.styling.Style;
import org.mwc.cmap.naturalearth.view.NEFeatureStyle;

public class NEFeatureLayer extends FeatureLayer
{

	private NEFeatureStyle neFeatureStyle;

	public NEFeatureLayer(NEFeatureStyle neFeatureStyle,
			SimpleFeatureCollection features, Style sld)
	{
		super(features, sld);
		this.neFeatureStyle = neFeatureStyle;
		setVisible(neFeatureStyle.isVisible());
	}

	@Override
	public boolean isVisible()
	{
		return neFeatureStyle.isVisible();
	}
}
