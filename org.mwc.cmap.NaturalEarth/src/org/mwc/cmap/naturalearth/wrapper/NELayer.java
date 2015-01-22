package org.mwc.cmap.naturalearth.wrapper;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IStatus;
import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.map.FeatureLayer;
import org.geotools.map.MapContent;
import org.geotools.map.MapViewport;
import org.geotools.renderer.lite.RendererUtilities;
import org.geotools.styling.FeatureTypeStyle;
import org.geotools.styling.PolygonSymbolizer;
import org.geotools.styling.Rule;
import org.geotools.styling.SLD;
import org.geotools.styling.Style;
import org.geotools.styling.Symbolizer;
import org.mwc.cmap.gt2plot.data.GeoToolsLayer;
import org.mwc.cmap.naturalearth.Activator;
import org.mwc.cmap.naturalearth.NaturalearthUtil;
import org.mwc.cmap.naturalearth.view.NEFeatureRoot;

import MWC.GUI.BaseLayer;
import MWC.GUI.CanvasType;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Plottable;
import MWC.GUI.Shapes.ChartBoundsWrapper;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldLocation;

public class NELayer extends GeoToolsLayer implements BaseLayer.ProvidesRange
{

	private static final long serialVersionUID = 1L;
	
	private static final int BATHY_HEIGHT = 20;

	/** from StreamingRenderer - Tolerance used to compare doubles for equality */
  private static final double TOLERANCE = 1e-6;


	private NEFeatureRoot _myFeatures;
	//private Layers _theLayers;

	private List<FeatureLayer> _gtLayers = new ArrayList<FeatureLayer>();
	
	private Map<String, Color> _bathyKeys = new LinkedHashMap<String, Color>();

	private double _maxScale = 0;
	private double _minScale = Double.MAX_VALUE;

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
		
		_gtLayers.clear();
	}


	@Override
	public void setMap(MapContent map)
	{
		// store the map object.  
		_myMap = map;
		
		// ok, now sort out which dataset we're looking at
		if (getVisible())
		{
			// ok, find the root folder
			File rootFolder = Activator.getDefault().getRootFolder();
			if (rootFolder == null) {
				return;
			}
			
			// and now find the list of shapefiles
			List<String> fileNames = Activator.getDefault().getShapeFiles(rootFolder);
			_bathyKeys.clear();
			_maxScale = 0;
			_minScale = Double.MAX_VALUE;
			for (String fileName : fileNames)
			{
				// ok, get a pointer to the data
				SimpleFeatureSource featureSource = getFeatureSource(fileName);
				
				// did we find it?
				if (featureSource != null)
				{
					// ok, now sort out the style
					String sldName = fileName.substring(0, fileName.length() - 3) + "sld";
					Style sld;
					File sldFile = new File(sldName);
					
					if (sldFile.isFile())
					{
						// ok, we can produce the styling from the file
						sld = NaturalearthUtil.loadStyle(sldName);
					}
					else
					{
						// just give it some default styling
						sld = NaturalearthUtil.createStyle2(featureSource);
					}
					loadBathyKey(fileName, sld);
					// wrap the GT data
					FeatureLayer layer = new NEFeatureLayer(_myFeatures, fileName, featureSource, sld);
					_myMap.addLayer(layer);
					_gtLayers.add(layer);
				}
			}
		}
		
		// NOTE: we now need to push the NE layers to the bottom of the GeoTools stack.
		// - we require the GT Tiffs to sit above NE layers.
		
		List<org.geotools.map.Layer> otherLayers = new ArrayList<org.geotools.map.Layer>();
		List<org.geotools.map.Layer> layers = _myMap.layers();
		
		// find a list of the non-Natural Earth layers
		for (org.geotools.map.Layer layer : layers)
		{
			if (!(layer instanceof NEFeatureLayer))
			{
				otherLayers.add(layer);
			}
		}
		
		// move the non-NE layers to the top of the stack
		int destinationPosition = layers.size() - 1;
		for (org.geotools.map.Layer layer : otherLayers)
		{
			int sourcePosition = layers.indexOf(layer);
			_myMap.moveLayer(sourcePosition, destinationPosition);
		}
	}

	public void loadBathyKey(String fileName, Style sld)
	{
		if (fileName.contains(Activator.NE_10M_BATHYMETRY_ALL)
				&& sld != null)
		{
			int length = "ne_10m_bathymetry_A_".length();
			String name = new File(fileName).getName();
			if (name.length() > length)
			{
				name = name.substring(length, name.length() - 4);
				FeatureTypeStyle[] typeStyles = sld.featureTypeStyles()
						.toArray(new FeatureTypeStyle[0]);
				if (typeStyles.length > 0)
				{
					double maxScale = SLD.maxScale(typeStyles[0]);
					if (maxScale > _maxScale)
					{
						_maxScale = maxScale;
					}
					double minScale = SLD.minScale(typeStyles[0]);
					if (minScale < _minScale)
					{
						_minScale = minScale;
					}
				}
				for (FeatureTypeStyle typeStyle : typeStyles)
				{
					List<Rule> rules = typeStyle.rules();
					for (Rule rule : rules)
					{
						Symbolizer[] syms = rule.getSymbolizers();
						for (Symbolizer sym : syms)
						{
							if (sym instanceof PolygonSymbolizer)
							{
								Color color = SLD.color(((PolygonSymbolizer) sym).getFill());
								_bathyKeys.put(name, color);
								break;
							}
						}
					}
				}
			}
		}
	}

	@Override
	protected org.geotools.map.Layer loadLayer(File openFile)
	{
		return null;
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

	public NEFeatureRoot getStore()
	{
		return _myFeatures;
	}

	@Override
	public void paint(CanvasType dest)
	{
		if (dest != null && getVisible() && _bathyKeys.size() > 0) {
			Dimension sArea = dest.getProjection().getScreenArea();
//			WorldArea wArea = dest.getProjection().getDataArea();
//			
//			final double sWidPixels =  sArea.width;
//			final double sWidInches = sWidPixels / 72d;  // presume 72 dpi
//			final double sWidM = sWidInches * 2.54 / 100;
//			final double sWidDegs = Conversions.m2Degs(sWidM);
//			final double dWidDegs = wArea.getWidth(); 
//			
//			double _scaleFactor = dWidDegs / sWidDegs;
			
			MapViewport viewport = _myMap.getViewport();
			double scaleDenominator = RendererUtilities.calculateOGCScale(viewport.getBounds(),
						(int) viewport.getScreenArea().getWidth(), null);

			if ( (_minScale - TOLERANCE) <= scaleDenominator && (_maxScale + TOLERANCE) > scaleDenominator )
			{
				int height = sArea.height;
				Set<String> depths = _bathyKeys.keySet();
				Font font = new Font("Arial", Font.PLAIN, 9);
				for (String depth : depths)
				{
					height = height - BATHY_HEIGHT;
					dest.setColor(_bathyKeys.get(depth));
					dest.fillRect(0, height, BATHY_HEIGHT, BATHY_HEIGHT);
					dest.setColor(Color.BLACK);
					dest.drawText(font, depth, BATHY_HEIGHT + 5, height + 12);
				}
				dest.drawText(font, "Depth (m)", 5, height - 10);
			}
		}
	}

}
