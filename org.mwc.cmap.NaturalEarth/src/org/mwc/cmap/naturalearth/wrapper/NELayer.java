package org.mwc.cmap.naturalearth.wrapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.map.FeatureLayer;
import org.geotools.map.MapContent;
import org.geotools.styling.Style;
import org.mwc.cmap.gt2plot.data.GeoToolsLayer;
import org.mwc.cmap.gt2plot.proj.GtProjection;
import org.mwc.cmap.naturalearth.Activator;
import org.mwc.cmap.naturalearth.NaturalearthUtil;
import org.mwc.cmap.naturalearth.view.NEFeature;
import org.mwc.cmap.naturalearth.view.NEFeatureGroup;
import org.mwc.cmap.naturalearth.view.NEFeatureRoot;
import org.mwc.cmap.naturalearth.view.NEFeatureStyle;

import MWC.GUI.BaseLayer;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layer.InterestedInViewportChange;
import MWC.GUI.Layers;
import MWC.GUI.Layers.NeedsToKnowAboutLayers;
import MWC.GUI.Plottable;
import MWC.GUI.Shapes.ChartBoundsWrapper;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldLocation;

public class NELayer extends GeoToolsLayer implements NeedsToKnowAboutLayers, InterestedInViewportChange,  BaseLayer.ProvidesRange
{

	private static final long serialVersionUID = 1L;

	private NEFeatureRoot _myFeatures;
	private Layers _theLayers;

	List<FeatureLayer> _gtLayers = new ArrayList<FeatureLayer>();

	public NELayer(NEFeatureRoot features)
	{
		super(ChartBoundsWrapper.NELAYER_TYPE, NATURAL_EARTH, null);
		setName(NATURAL_EARTH);
		_myFeatures = features;
	}

	@Override
	public void clearMap()
	{
		for (FeatureLayer layer : _gtLayers)
		{
			//layer.dispose();
			_myMap.removeLayer(layer);
		}
//		if (_myMap != null) {
//			_myMap.dispose();
//		}
		_gtLayers.clear();
		//_myMap = null;
	}

	@Override
	public void setMap(MapContent map)
	{
		// store the map object.  
		_myMap = map;
		viewPortChange();
	}

	@Override
	protected org.geotools.map.Layer loadLayer(File openFile)
	{
		return null;
	}

	private void configureLayers(NEFeature feature)
	{
		Enumeration<Editable> children = feature.elements();
		while (children.hasMoreElements())
		{
			Editable thisE = children.nextElement();

			if (thisE instanceof NEFeatureGroup)
			{
				NEFeatureGroup child = (NEFeatureGroup) thisE;
				configureLayers(child);
			}
			else
			{
				NEFeatureStyle style = (NEFeatureStyle) thisE;
				addLayer(style);
			}
		}
	}

	private SimpleFeatureSource getFeatureSource(String fileName)
	{
		final File openFile = new File(fileName);
		if (!openFile.isFile())
		{
			Activator.logError(IStatus.INFO, fileName + " doesn't exist", null);
			return null;
		}
		SimpleFeatureSource featureSource;
		try
		{
			FileDataStore store = FileDataStoreFinder.getDataStore(openFile);
			
			featureSource = store.getFeatureSource();
			//Filter filter = ECQL.toFilter("BBOX(the_geom, -180, -80, 180, 84)");
			//features = featureSource.getFeatures( filter );
			//-180.0000, -80.0000, 180.0000, 84.0000
			//features = featureSource.getFeatures();
			//reprojectingFeatures = new ReprojectingFeatureCollection(features, CRS.decode("EPSG:4326"));
		}
		catch (IOException e)
		{
			Activator.logError(IStatus.INFO, "Can't load " + openFile.getAbsolutePath(), e);
			return null;
		}
		catch (Exception e)
		{
			Activator.logError(IStatus.INFO, "grabFeaturesInBoundingBox issue in " + openFile.getAbsolutePath(), e);
			return null;
		}
		return featureSource;
	}

	@Override
	public boolean getVisible()
	{
		return _myFeatures.getVisible();
	}

	@Override
	public double rangeFrom(WorldLocation other)
	{
		return  Plottable.INVALID_RANGE;
	}

	@Override
	public int compareTo(Plottable o)
	{
		return this.getName().compareTo(o.getName());
	}

	@Override
	public boolean hasEditor()
	{
		return _myFeatures.hasEditor();
	}

	@Override
	public EditorType getInfo()
	{
		return _myFeatures.getInfo();
	}

	@Override
	public void exportShape()
	{
	}

	@Override
	public void append(Layer other)
	{
	}

	@Override
	public WorldArea getBounds()
	{
		return null;
	}

	@Override
	public String toString()
	{
		return getName();
	}

	@Override
	public boolean hasOrderedChildren()
	{
		return true;
	}

	@Override
	public int getLineThickness()
	{
		return 0;
	}

	@Override
	public void add(Editable point)
	{

	}

	@Override
	public Enumeration<Editable> elements()
	{
		return _myFeatures.elements();
	}

	@Override
	public void setVisible(boolean val)
	{
		super.setVisible(val);
		_myFeatures.setVisible(val);
	}

	/**
	 * interface for layer objects that can be ordered, using their created
	 * (imported) date
	 * 
	 * @author ian
	 * 
	 */
	public static interface HasCreatedDate
	{
		public long getCreated();
	}

	public static boolean hasGoodPath()
	{
		final File dataPath = new File(Activator.getDefault().getLibraryPath());

		return dataPath.isDirectory();
	}

	@Override
	public void setLayers(Layers parent)
	{
		_theLayers = parent;
	}

	/** method that gets called when the viewport changes.
	 * We handle this separately to screen refreshes since a viewport
	 * change may lead to loading data at a new resolution.
	 */
	public void viewPortChange()
	{
		// ok, now sort out which dataset we're looking at
		if (getVisible())
		{
			clearMap();
			configureLayers(_myFeatures);
		}
	}


	private FeatureLayer addLayer(NEFeatureStyle style)
	{
		List<String> fileNames = style.getFileNames();
		for (String fileName : fileNames)
		{
			SimpleFeatureSource featureSource = getFeatureSource(fileName);
			if (featureSource != null)
			{
				String sldName = fileName.substring(0, fileName.length() - 3) + "sld";
				Style sld;
				File sldFile = new File(sldName);
				if (sldFile.isFile())
				{
					sld = NaturalearthUtil.loadStyle(sldName);
				}
				else
				{
					sld = NaturalearthUtil.createStyle2(featureSource, style);
				}
				FeatureLayer layer = new NEFeatureLayer(style, featureSource, sld);
				_myMap.addLayer(layer);
				_gtLayers.add(layer);
				return layer;
			}
		}
		return null;
	}

	public NEFeatureRoot getStore()
	{
		return _myFeatures;
	}

}
