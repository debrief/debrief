package org.mwc.cmap.naturalearth.view;

import java.util.ArrayList;
import java.util.List;

import MWC.GenericData.WorldLocation;

public class NEFeatureStyle extends NEFeature
{
	
	private static final long serialVersionUID = 1L;
	private List<String> _fileNames = new ArrayList<String>();
	
	public NEFeatureStyle(String name)
	{
		super(name);
	}

	@Override
	public double rangeFrom(WorldLocation other)
	{
		return 0;
	}

	public List<String> getFileNames()
	{
		return _fileNames;
	}

	public void setFileNames(List<String> fileNames)
	{
		this._fileNames = fileNames;
	}
	
}
