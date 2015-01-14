package org.mwc.cmap.naturalearth.view;

import java.awt.Color;
import java.beans.IntrospectionException;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyDescriptor;
import java.io.File;

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
	private StyleInfo _myEditor;
	
	final private long _created;
	
	/** our dataset
	 * 
	 */
	private CachedNaturalEarthFile _data;
	private NEFeatureGroup parent;
	
	protected PropertyChangeSupport _pSupport = null;

	public NEFeatureStyle()
	{
		this(null, null, null, false, null, null);
	}
	
	public NEFeatureStyle(NEFeatureGroup group, String folder, String filename, boolean visible,
			Color fillCol, Color lineCol)
	{
		if(folder != null)
			_folder = folder;
		else
			_folder = null;
		_filename = filename;
		_lineCol = lineCol;
		_fillCol = fillCol;
		_isVisible = visible;
		_created = System.currentTimeMillis();
		
		showLines = (_lineCol != null);
		showPolygons = (_fillCol != null);
		this.parent = group;
		_pSupport = new PropertyChangeSupport(this);
	}

	/** the NE filename that this style applies to
	 * 
	 * @return
	 */
	public String getFileName()
	{
		final String res;
		if(_folder == null)
			res = _filename + File.separator + _filename;
		else
			res = _folder + File.separator + _filename;
		
		return res;
	}
	
	public String getFolderName()
	{
		return _folder;
	}

	public NEFeatureGroup getParent() {
		return parent;
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
		Color oldProperty = this._fillCol;
		_fillCol = col;
		firePropertyChange("polygonColor", oldProperty, col);
	}
	
	public void setLineColor(Color col)
	{
		Color oldProperty = this._lineCol;
		_lineCol = col;
		firePropertyChange("lineColor", oldProperty, col);
	}

	public Color getTextColor()
	{
		return _textCol;
	}

	public void setTextHeight(int textHeight)
	{
		int oldProperty = this._textHeight;
		_textHeight  = textHeight;
		firePropertyChange("textHeight", oldProperty, textHeight);
	}

	public void setTextStyle(int textStyle)
	{
		int oldProperty = this._textStyle;
		_textStyle = textStyle;
		firePropertyChange("textStyle", oldProperty, textStyle);
	}

	public void setTextFont(String textFont)
	{
		String oldProperty = this._textFont;
		_textFont = textFont;
		firePropertyChange("textFont", oldProperty, textFont);
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
		Color oldProperty = this._textCol;
		_textCol = textCol;
		firePropertyChange("textColor", oldProperty, textCol);
	}
	
	public boolean isShowPolygons()
	{
		return showPolygons;
	}

	public void setShowPolygons(boolean showPolygons)
	{
		boolean oldProperty = this.showPolygons;
		this.showPolygons = showPolygons;
		firePropertyChange("showPolygons", oldProperty, showPolygons);
	}

	public boolean isShowLines()
	{
		return showLines;
	}

	public void setShowLines(boolean showLines)
	{
		boolean oldProperty = this.showLines;
		this.showLines = showLines;
		firePropertyChange("showLines", oldProperty, showLines);
	}

	public boolean isShowPoints()
	{
		return showPoints;
	}

	public void setShowPoints(boolean showPoints)
	{
		boolean oldProperty = this.showPoints;
		this.showPoints = showPoints;
		firePropertyChange("showPoints", oldProperty, showPoints);
	}

	public boolean isShowLabels()
	{
		return showLabels;
	}

	public void setShowLabels(boolean showLabels)
	{
		boolean oldProperty = this.showLabels;
		this.showLabels = showLabels;
		firePropertyChange("showLabels", oldProperty, showLabels);
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

	/**
	 * fire a property change, if we have any listeners
	 * 
	 * @param event_type
	 *          the type of event to fire
	 * @param oldValue
	 *          the old value
	 * @param newValue
	 *          the new value
	 */
	public void firePropertyChange(final String propertyName,
			final Object oldValue, final Object newValue)
	{
		if (_pSupport != null)
		{
			_pSupport.firePropertyChange(propertyName, oldValue, newValue);
		}
	}

	/**
	 * add a listener
	 * 
	 * @param listener
	 *          the new listener
	 */
	public void addListener(final java.beans.PropertyChangeListener listener)
	{
		if (_pSupport == null)
			_pSupport = new PropertyChangeSupport(this);

		_pSupport.addPropertyChangeListener(listener);
	}

	/**
	 * remove a listener
	 */
	public void removeListener(final PropertyChangeListener listener)
	{
		_pSupport.removePropertyChangeListener(listener);
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((_filename == null) ? 0 : _filename.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		NEFeatureStyle other = (NEFeatureStyle) obj;
		if (_filename == null)
		{
			if (other._filename != null)
				return false;
		}
		else if (!_filename.equals(other._filename))
			return false;
		return true;
	}

	public void setParent(NEFeatureGroup parent)
	{
		this.parent = parent;
	}

	public void setFileName(String filename)
	{
		this._filename = filename;
	}

	public void setFolderName(String folder)
	{
		this._folder = folder;
	}
}
