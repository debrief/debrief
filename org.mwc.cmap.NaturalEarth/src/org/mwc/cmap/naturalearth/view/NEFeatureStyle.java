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
	private int _textHeight;
	private int _textStyle;
	private String _textFont;

	public NEFeatureStyle(String featureType, String filename, boolean visible, Color fillCol,
			Color lineCol)
	{
		_featureType = featureType;
		_filename = filename;
		_lineCol = lineCol;
		_fillCol = fillCol;
		_isVisible = visible;
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

	public void setTextHeight(int textHeight)
	{
		_textHeight  = textHeight;
	}

	public void setTextStyle(int textStyle)
	{
		_textStyle = textStyle;
	}

	public void setTextFont(String textFont)
	{
		_textFont = textFont;
	}

	public int getTextHeight()
	{
		return _textHeight;
	}

	public int getTextStyle()
	{
		return _textStyle;
	}

	public String getTextFont()
	{
		return _textFont;
	}

	public void setTextColor(Color textCol)
	{
		_textCol = textCol;
	}
	
	

}
