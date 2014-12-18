package org.mwc.cmap.naturalearth.model;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;

import org.mwc.cmap.gt2plot.data.CachedNauticalEarthFile;
import org.mwc.cmap.naturalearth.view.NEFeatureStyle;

import MWC.GUI.CanvasType;
import MWC.GUI.Editable;
import MWC.GUI.Plottable;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldLocation;

public class NEFeature implements Plottable
{

	/** our style
	 * 
	 */
	private NEFeatureStyle _style;
	
	/** our dataset
	 * 
	 */
	private CachedNauticalEarthFile _feature;

	private FeatureInfo _myEditor;

	final private long _created;

	
	
	public NEFeature(NEFeatureStyle style)
	{
		_style = style;
		_created = System.currentTimeMillis();
	}
	
	
	
	public boolean getShowPolygons()
	{
		return _style.isShowLabels();
	}



	public void setShowPolygons(boolean showPolygons)
	{
		_style.setShowPolygons(showPolygons);
	}



	public boolean getShowLines()
	{
		return _style.isShowLabels();
	}



	public void setShowLines(boolean showLines)
	{
		_style.setShowLines(showLines);
	}



	public boolean getShowPoints()
	{
		return _style.isShowLabels();
	}



	public void setShowPoints(boolean showPoints)
	{
		_style.setShowPoints(showPoints);
	}



	public boolean getShowLabels()
	{
		return _style.isShowLabels();
	}



	public void setShowLabels(boolean showLabels)
	{
		_style.setShowLabels(showLabels);
	}



	@Override
	public String toString()
	{
		return getName();
	}

	public NEFeatureStyle getStyle()
	{
		return _style;
	}
	
	public boolean getVisible()
	{
		return _style.isVisible();
	}
	
	public void setVisible(boolean val)
	{
		_style.setVisible(val);
	}

	public boolean isLoaded()
	{
		return _feature != null;
	}
	
	public void setDataSource(CachedNauticalEarthFile feature)
	{
		_feature = feature;
	}
	
	public CachedNauticalEarthFile getData()
	{
		return _feature;
	}

	@Override
	public String getName()
	{
		return _style.getFileName();
	}

	@Override
	public boolean hasEditor()
	{
		return true;
	}

	@Override
	public Editable.EditorType getInfo()
	{
		if (_myEditor == null)
			_myEditor = new FeatureInfo(this, this.getName());
		
		return _myEditor;
	}
	
	// ////////////////////////////////////////////////////
	// bean info for this class
	// ///////////////////////////////////////////////////
	public final class FeatureInfo extends Editable.EditorType
	{

		public FeatureInfo(final NEFeature data, final String theName)
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
						prop("ShowPolygons", "if the layer is visible", FORMAT),
						prop("ShowLines", "if the layer is visible", FORMAT),
						prop("ShowPoints", "if the layer is visible", FORMAT),
						prop("ShowLabels", "if the layer is visible", FORMAT)
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
		NEFeature other = (NEFeature) o;
		long hisTime = other._created;
		final int res;
		
		if(_created > hisTime)
			res = 1;
		else 
			res = -1;
		
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
	public double rangeFrom(WorldLocation other)
	{
		return Plottable.INVALID_RANGE;
	}



}
