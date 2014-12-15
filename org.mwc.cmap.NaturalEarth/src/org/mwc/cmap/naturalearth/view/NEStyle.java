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
	
	/** the user-provided name for this set of styles
	 * 
	 * @return
	 */
	public String getName()
	{
		return "pending";
	}
}
