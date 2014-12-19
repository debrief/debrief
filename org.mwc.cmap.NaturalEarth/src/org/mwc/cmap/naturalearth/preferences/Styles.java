package org.mwc.cmap.naturalearth.preferences;

import java.util.ArrayList;
import java.util.List;

import org.mwc.cmap.naturalearth.view.NEFeatureStore;

public class Styles
{

	private List<NEFeatureStore> styles = new ArrayList<NEFeatureStore>();
	
	public Styles()
	{
		super();
		// FIXME initialize from preferences
		styles.add(new NEFeatureStore("default"));
		styles.add(new NEFeatureStore("Black & White"));
		styles.add(new NEFeatureStore("With Culture"));
	}

	public List<NEFeatureStore> getStyles()
	{
		return styles;
	}
	
}
