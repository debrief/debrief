package org.mwc.cmap.naturalearth.view;

import org.mwc.cmap.naturalearth.model.NEFeature.FeatureType;

public class NEStyle
{
	final private String _name;
	final private boolean _hasPolygons;
	final private boolean _hasLines;
	final private boolean _hasSymbols;
	final private boolean _hasLabels;
	
	public NEStyle(final String name, boolean hasPolygons, boolean hasLines, boolean hasSymbols, boolean hasLabels)
	{
		_name = name;
		_hasPolygons = hasPolygons;
		_hasLines = hasLines;
		_hasSymbols = hasSymbols;
		_hasLabels = hasLabels;
	}

	public String getName()
	{
		return _name;
	}
	
	public boolean has(FeatureType fType)
	{
		switch(fType)
		{
		case Polygon:
			return _hasPolygons;			
		case Line:
			return _hasLines;
		case Symbol:
			return _hasSymbols;
		case Label:
			return _hasLabels;
			
		}
		
		return _hasPolygons;
	}

	public NEStyle getPolyStyle()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public Object getLineStyle()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public Object getSymbolStyle()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public Object getLabelStyle()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public Double getminS()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public Double getMaxS()
	{
		// TODO Auto-generated method stub
		return null;
	}
}
