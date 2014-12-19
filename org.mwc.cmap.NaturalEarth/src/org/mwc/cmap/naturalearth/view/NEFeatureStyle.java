package org.mwc.cmap.naturalearth.view;

import java.awt.Color;
import java.awt.Font;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.util.HashMap;

import org.mwc.cmap.naturalearth.data.CachedNaturalEarthFile;
import org.mwc.cmap.naturalearth.wrapper.NELayer.HasCreatedDate;

import MWC.GUI.CanvasType;
import MWC.GUI.Editable;
import MWC.GUI.Plottable;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldLocation;

public class NEFeatureStyle implements Plottable, HasCreatedDate
{
	
	private String _filename;
	private String _folder;

	private Color _lineCol;
	private Color _fillCol;
	private Color _textCol;
	private boolean _isVisible;
	private int _textHeight;
	private int _textStyle;
	private String _textFont;
	

	private static transient HashMap<String, Font> _fontCache = new HashMap<String, Font>();

	
	/** visibility settings
	 * 
	 */
	private boolean showPolygons = true;
	private boolean showLines = true;
	private boolean showPoints = true;
	private boolean showLabels = true;
	
	/** the editor settings for this object
	 * 
	 */
	private transient StyleInfo _myEditor;
	
	final private long _created;
	
	/** our dataset
	 * 
	 */
	private transient CachedNaturalEarthFile _data;
	private Font _font;

	public NEFeatureStyle(String folder, String filename, boolean visible,
			Color fillCol, Color lineCol)
	{
		this();
		if(folder != null)
			_folder = folder;
		else
			_folder = null;
		_filename = filename;
		_lineCol = lineCol;
		_fillCol = fillCol;
		_isVisible = visible;
	}

	public NEFeatureStyle()
	{
		_created = System.currentTimeMillis();
	}

	public String getFolderName()
	{
		String res = null;
		if(_folder != null)
			res = _folder;
		else
			res = _filename;
		return res;
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

	/** store the actual data that this feature will render
	 * 
	 * @param data
	 */
	public void setData(CachedNaturalEarthFile data)
	{
		_data = data;
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
		return _filename;
	}
	
	@Override
	public String toString()
	{
		return getName();
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

	public CachedNaturalEarthFile getData()
	{
		return _data;
	}

	public boolean isLoaded()
	{
		return _data != null;
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

	@Override
	public int compareTo(Plottable o)
	{
		int res = -1;
		HasCreatedDate him = (HasCreatedDate) o;
		
		if(getCreated() < him.getCreated())
			res = 1;
		
		return res;
	}

	@Override
	public void paint(CanvasType dest)
	{
	}

	@Override
	public WorldArea getBounds()
	{
		return null;
	}

	@Override
	public boolean getVisible()
	{
		return isVisible();
	}

	@Override
	public double rangeFrom(WorldLocation other)
	{
		return 0;
	}

	@Override
	public long getCreated()
	{
		return _created;
	}

	public void setTextFont(Font font)
	{
		_font = font;
	}

	public Font getFont()
	{
		if(_font == null)
		{
			_font = fontFor(getTextHeight(), getTextStyle(),
				getTextFont());
		}

		return _font;
	}


	private Font fontFor(int height, int style, String family)
	{
		String descriptor = family + "_" + height + "_" + style;
		Font font = _fontCache.get(descriptor);
		if (font == null)
		{
			font = new Font(family, style, height);
			_fontCache.put(descriptor, font);
		}
		return font;
	}

	public void setFolderName(String value)
	{
		_folder = value;
	}

	public void setFileName(String value)
	{
		_filename = value;
	}

}
