package org.mwc.cmap.naturalearth.model;

import java.util.Iterator;

import org.mwc.cmap.naturalearth.view.NEFeatureStyle;
import org.mwc.cmap.naturalearth.wrapper.CachedFeature;
import org.mwc.cmap.naturalearth.wrapper.CachedShapefile;

public class NEFeature
{

	private NEFeatureStyle _style;
	
	private CachedShapefile _feature;

	public NEFeature(NEFeatureStyle style)
	{
		_style = style;
	}
	
	public NEFeatureStyle getStyle()
	{
		// TODO Auto-generated method stub
		return _style;
	}

	public boolean isLoaded()
	{
		return _feature != null;
	}
	
	public void setFeature(CachedShapefile feature)
	{
		_feature = feature;
	}
	
	public Iterator<CachedFeature> iterator()
	{
		return _feature.iterator();
	}

}
