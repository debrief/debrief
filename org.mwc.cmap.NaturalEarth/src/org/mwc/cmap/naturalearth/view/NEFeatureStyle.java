package org.mwc.cmap.naturalearth.view;

import org.mwc.cmap.naturalearth.wrapper.CachedShapefile;

public class NEFeatureStyle
{
	
	/** the set of data that we will render
	 * 
	 */
	private CachedShapefile _myData = null;

	/** the NE filename that this style applies to
	 * 
	 * @return
	 */
	public String getFileName()
	{
		return "pending";
	}

	/** if this feature is visible
	 * 
	 * @return
	 */
	public boolean isVisible()
	{
		return true;
	}

	/** whether this feature has loaded its data
	 * 
	 * @return
	 */
	public boolean isLoaded()
	{
		return _myData != null;
	}

	/** store the actual data that this feature will render
	 * 
	 * @param data
	 */
	public void setData(CachedShapefile data)
	{
		_myData = data;
	}

}
