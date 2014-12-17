package org.mwc.cmap.naturalearth.view;

import java.awt.Color;

import org.mwc.cmap.gt2plot.data.CachedNauticalEarthFile;

public class NEFeatureStyle
{
	
	/** the set of data that we will render
	 * 
	 */
	private CachedNauticalEarthFile _myData = null;
	@SuppressWarnings("unused")
	private String _featureType;
	private String _filename;
	private Color _lineCol;
	private Color _fillCol;
	private Color _textCol;
	private boolean _isVisible;

	public NEFeatureStyle(String featureType, String filename, Color fillCol,
			Color lineCol, Color textCol)
	{
		_featureType = featureType;
		_filename = filename;
		_lineCol = lineCol;
		_fillCol = fillCol;
		_textCol = textCol;
	}

	/** the NE filename that this style applies to
	 * 
	 * @return
	 */
	public String getFileName()
	{
		return _filename;
	}

	/** if this feature is visible
	 * 
	 * @return
	 */
	public boolean isVisible()
	{
		return _isVisible;
	}

	public void setVisible(boolean visible)
	{
		_isVisible = visible;
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
	public void setData(CachedNauticalEarthFile data)
	{
		_myData = data;
	}

	public Color getLineColor()
	{
		return _lineCol;
	}

	public Color getFillColor()
	{
		return _fillCol;
	}


	public Color getTextColor()
	{
		return _textCol;
	}

}
