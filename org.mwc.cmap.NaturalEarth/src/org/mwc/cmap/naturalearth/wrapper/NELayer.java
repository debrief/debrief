package org.mwc.cmap.naturalearth.wrapper;

import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.map.FeatureLayer;
import org.geotools.map.MapContent;
import org.geotools.styling.Style;
import org.mwc.cmap.gt2plot.data.GeoToolsLayer;
import org.mwc.cmap.naturalearth.Activator;
import org.mwc.cmap.naturalearth.NaturalearthUtil;
import org.mwc.cmap.naturalearth.view.NEFeatureGroup;
import org.mwc.cmap.naturalearth.view.NEFeatureStore;
import org.mwc.cmap.naturalearth.view.NEFeatureStyle;
import org.mwc.cmap.naturalearth.view.NEResolution;

import MWC.Algorithms.Conversions;
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

	private NEFeatureStore _myFeatures;
	private Layers _theLayers;

	List<FeatureLayer> _gtLayers = new ArrayList<FeatureLayer>();

	private NEResolution _currentRes;

	private double _scaleFactor;

	public NELayer(NEFeatureStore features)
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
			layer.dispose();
			_myMap.removeLayer(layer);
		}
//		if (_myMap != null) {
//			_myMap.dispose();
//		}
		_gtLayers.clear();
		_myMap = null;
	}

	@Override
	public void setMap(MapContent map)
	{
		clearMap();
		_myMap = map;

		if (getVisible())
		{
			NEResolution thisR = _myFeatures.resolutionFor(_scaleFactor);
			
			if (thisR != _currentRes)
			{
				if (_currentRes != null)
				{
					_currentRes.setActive(false);
				}

				if (thisR != null)
				{
					thisR.setActive(true);
				}

				// remember this resolution
				_currentRes = thisR;

				// hmm, we also have to tell the layer manager that we have updated
				if (_theLayers != null)
					_theLayers.fireReformatted(this);
			}
			if (thisR != null)
			{
				NEFeatureGroup group = thisR;
				configureLayers(group);
			}
		}

	}

	@Override
	protected org.geotools.map.Layer loadLayer(File openFile)
	{
		return null;
	}

	private void configureLayers(NEFeatureGroup group)
	{
		if (group.getVisible())
		{

			Enumeration<Editable> children = group.elements();
			while (children.hasMoreElements())
			{
				Editable thisE = children.nextElement();

				// aah just check if this is actually a group
				if (thisE instanceof NEFeatureGroup)
				{
					NEFeatureGroup child = (NEFeatureGroup) thisE;
					if (child.getVisible())
						configureLayers(child);
				}
				else
				{
					NEFeatureStyle feature = (NEFeatureStyle) thisE;
					if (!feature.isVisible())
						continue;

					NEFeatureStyle style = feature;

					String fileName = feature.getFileName();
					String rootPath = Activator.getDefault().getLibraryPath();
					if (rootPath == null)
					{
						Activator.logError(IStatus.INFO, fileName + "DATA_FOLDER isn't set", null);
						continue;
					}
					if (fileName == null)
					{
						Activator.logError(IStatus.INFO, fileName + "style.getFileName() is null", null);
						continue;
					}
					fileName = rootPath + File.separator + fileName + ".shp";
					final File openFile = new File(fileName);
					if (!openFile.isFile())
					{
						Activator.logError(IStatus.INFO, fileName + " doesn't exist", null);
						continue;
					}
					SimpleFeatureSource featureSource;
					SimpleFeatureCollection features;
					try
					{
						FileDataStore store = FileDataStoreFinder.getDataStore(openFile);
						
						featureSource = store.getFeatureSource();
						//Filter filter = ECQL.toFilter("BBOX(the_geom, -180, -80, 180, 84)");
						//features = featureSource.getFeatures( filter );
						//-180.0000, -80.0000, 180.0000, 84.0000
						features = featureSource.getFeatures();
						//reprojectingFeatures = new ReprojectingFeatureCollection(features, CRS.decode("EPSG:4326"));
					}
					catch (IOException e)
					{
						Activator.logError(IStatus.INFO, "Can't load " + openFile.getAbsolutePath(), e);
						continue;
					}
					catch (Exception e)
					{
						Activator.logError(IStatus.INFO, "grabFeaturesInBoundingBox issue in " + openFile.getAbsolutePath(), e);
						continue;
					}

					Style sld = NaturalearthUtil.createStyle2(featureSource, style);
					FeatureLayer layer = new FeatureLayer(features, sld);
					_gtLayers.add(layer);
					_myMap.addLayer(layer);
				}
			}
		}
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
	public void setName(String val)
	{
	}

	@Override
	public String getName()
	{
		return NATURAL_EARTH;
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
	public void removeElement(Editable point)
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

		return dataPath.exists();
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
	public void viewPortChange(Dimension sArea, WorldArea wArea)
	{
		final double sWidPixels =  sArea.width;
		final double sWidInches = sWidPixels / 72d;  // presume 72 dpi
		final double sWidM = sWidInches * 2.54 / 100;
		final double sWidDegs = Conversions.m2Degs(sWidM);
		final double dWidDegs = wArea.getWidth(); 
		
		// store the scale factor, for when we redraw
		_scaleFactor = dWidDegs / sWidDegs;
	}

}
