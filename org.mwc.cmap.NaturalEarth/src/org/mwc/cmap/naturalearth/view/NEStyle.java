package org.mwc.cmap.naturalearth.view;

import java.util.ArrayList;

/* a single Natural Earth style set - containing styles for all features, at all resolutions
 * 
 */
public class NEStyle  extends ArrayList<NEResolution>
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String name;
	
	public NEStyle(String name)
	{
		this.name = name;
	}

	/** the user-provided name for this set of styles
	 * 
	 * @return
	 */
	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}
}
