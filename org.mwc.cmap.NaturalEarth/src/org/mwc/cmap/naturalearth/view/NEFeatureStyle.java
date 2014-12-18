package org.mwc.cmap.naturalearth.view;

import java.awt.Color;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;

import org.mwc.cmap.naturalearth.data.CachedNaturalEarthFile;

import MWC.GUI.Editable;

public class NEFeatureStyle implements Editable
{
	
	/** the set of data that we will render
	 * 
	 */
	private CachedNaturalEarthFile _myData = null;
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
	
	
	/** visibility settings
	 * 
	 */
	private boolean showPolygons = true;
	private boolean showLines = true;
	private boolean showPoints = true;
	private boolean showLabels = true;
	private StyleInfo _myEditor;

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
	public void setData(CachedNaturalEarthFile data)
	{
		_myData = data;
	}

	public Color getLineColor()
	{
		return _lineCol;
	}

	public Color getPolygonColor()
	{
		return _fillCol;
	}

	public void setPolygonColor(Color col)
	{
		_fillCol = col;
	}
	
	public void setLineColor(Color col)
	{
		_lineCol = col;
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
	
	public boolean isShowPolygons()
	{
		return showPolygons;
	}

	public void setShowPolygons(boolean showPolygons)
	{
		this.showPolygons = showPolygons;
	}

	public boolean isShowLines()
	{
		return showLines;
	}

	public void setShowLines(boolean showLines)
	{
		this.showLines = showLines;
	}

	public boolean isShowPoints()
	{
		return showPoints;
	}

	public void setShowPoints(boolean showPoints)
	{
		this.showPoints = showPoints;
	}

	public boolean isShowLabels()
	{
		return showLabels;
	}

	public void setShowLabels(boolean showLabels)
	{
		this.showLabels = showLabels;
	}

	@Override
	public String getName()
	{
		return toString();
	}

	@Override
	public boolean hasEditor()
	{
		return true;
	}

	@Override
	public EditorType getInfo()
	{
		if(_myEditor == null)
			_myEditor = new StyleInfo(this, getName());
		return _myEditor;
	}

	// ////////////////////////////////////////////////////
	// bean info for this class
	// ///////////////////////////////////////////////////
	public final class StyleInfo extends Editable.EditorType
	{

		public StyleInfo(final NEFeatureStyle data, final String theName)
		{
			super(data, theName, "Natural Earth");
		}

		/**
		 * whether the normal editable properties should be combined with the
		 * additional editable properties into a single list. This is typically used
		 * for a composite object which has two lists of editable properties but
		 * which is seen by the user as a single object To be overwritten to change
		 * it
		 */
		@Override
		public final boolean combinePropertyLists()
		{
			return true;
		}

		@Override
		public final PropertyDescriptor[] getPropertyDescriptors()
		{
			try
			{
				final PropertyDescriptor[] myRes =
				{
						prop("Visible", "if the layer is visible", FORMAT),
						prop("ShowPolygons", "if the polygons are visible", FORMAT),
						prop("ShowLines", "if the polygons are visible", FORMAT),
						prop("ShowPoints", "if the polygons are visible", FORMAT),
						prop("ShowLabels", "if the polygons are visible", FORMAT),
						prop("PolygonColor", "if the polygons are visible", FORMAT),
						prop("LineColor", "if the polygons are visible", FORMAT),
						prop("TextColor", "if the polygons are visible", FORMAT),
				};

				return myRes;

			}
			catch (final IntrospectionException e)
			{
				e.printStackTrace();
				return super.getPropertyDescriptors();
			}
		}	
	}	

}
