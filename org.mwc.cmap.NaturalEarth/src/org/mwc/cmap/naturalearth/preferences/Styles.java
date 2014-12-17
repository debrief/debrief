package org.mwc.cmap.naturalearth.preferences;

import java.util.ArrayList;
import java.util.List;

import org.mwc.cmap.naturalearth.view.NEStyle;

public class Styles
{

	private List<NEStyle> styles = new ArrayList<NEStyle>();
	
	public Styles()
	{
		super();
		// FIXME initialize from preferences
		styles.add(new NEStyle("default"));
		styles.add(new NEStyle("Black & White"));
		styles.add(new NEStyle("With Culture"));
	}

	public List<NEStyle> getStyles()
	{
		return styles;
	}
	
}
