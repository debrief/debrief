package org.mwc.cmap.naturalearth.model;

import java.beans.BeanInfo;
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

	@Override
	public String toString()
	{
		return getName();
	}

	public NEFeatureStyle getStyle()
	{
		return _style;
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
				final PropertyDescriptor[] myRes =
				{
				};

				return myRes;
		}
		
		@Override
		public final BeanInfo[] getAdditionalBeanInfo()
		{
			// get our shape back
			final NEFeature sp = (NEFeature) super.getData();
			final NEFeatureStyle ps = sp._style;
			if (sp instanceof MWC.GUI.Editable)
			{
				final MWC.GUI.Editable et = (MWC.GUI.Editable) ps;
				if (et.hasEditor() == true)
				{
					final BeanInfo[] res =
					{ et.getInfo() };
					return res;
				}
			}
			return null;
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

	@Override
	public boolean getVisible()
	{
		return _style.isVisible();
	}

	@Override
	public void setVisible(boolean val)
	{
		_style.setVisible(val);
	}



}
