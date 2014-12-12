package org.mwc.cmap.naturalearth.model;

import java.util.ArrayList;

import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

import MWC.GUI.Editable;
import MWC.GUI.Editable.EditorType;

public class NEFeature implements Editable
{
	private String name;
	
	public static enum FeatureType{
		Polygon, Line, Symbol, Label
	}

	public NEFeature(String name)
	{
		this.name = name;
	}

	public String getName()
	{
		return name;
	}

	@Override
	public boolean hasEditor()
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public EditorType getInfo()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public ArrayList<Polygon> getPolygons()
	{
		return null;
	}

	public ArrayList<Point> getSymbols()
	{
		return null;
	}

	public ArrayList<LabelObject> getLabels()
	{
		return null;
	}

	public ArrayList<LineString> getLines()
	{
		return null;
	}
}