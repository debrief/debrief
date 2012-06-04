package org.mwc.cmap.gt2plot.data;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

import org.geotools.coverage.grid.io.AbstractGridCoverage2DReader;
import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.feature.simple.SimpleFeatureImpl;
import org.geotools.gce.image.WorldImageFormat;
import org.geotools.map.GridReaderLayer;
import org.geotools.map.Layer;
import org.geotools.styling.RasterSymbolizer;
import org.geotools.styling.SLD;
import org.geotools.styling.Style;
import org.geotools.styling.StyleFactoryImpl;
import org.opengis.feature.Property;
import org.opengis.parameter.GeneralParameterValue;
import org.opengis.referencing.FactoryException;

import MWC.GUI.ExternallyManagedDataLayer;
import MWC.GUI.Layers;
import MWC.GUI.Properties.LocationPropertyEditor;
import MWC.GUI.Shapes.ChartFolio;
import MWC.GUI.Shapes.ChartBoundsWrapper;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldLocation;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.MultiPolygon;

public class WorldImageLayer extends GeoToolsLayer
{
	public final static String RASTER_FILE = "rasterExtents_ARCS_Export";


	public WorldImageLayer(String layerName, String fileName)
	{
		super(ChartBoundsWrapper.WORLDIMAGE_TYPE, layerName, fileName);
	}
	
	protected Layer loadLayer(File openFile)
	{
		Layer res = null;
		WorldImageFormat format = new WorldImageFormat();
		AbstractGridCoverage2DReader tiffReader = format.getReader(openFile);
		if (tiffReader != null)
		{
			StyleFactoryImpl sf = new StyleFactoryImpl();
			RasterSymbolizer symbolizer = sf.getDefaultRasterSymbolizer();
			Style defaultStyle = SLD.wrapSymbolizers(symbolizer);

			
			GeneralParameterValue[] params = null;
			
			res = new GridReaderLayer(tiffReader, defaultStyle, params);
			
			
		}
		return res;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static MWC.GUI.Layer read(String fileName)
	{

		MWC.GUI.Layer res = null;
		File openFile = new File(fileName);
		if (openFile != null && openFile.exists())
		{
			// sort out the name of the map
			String coverageName = fileName;
			final int dotIndex = coverageName.lastIndexOf(".");
			coverageName = (dotIndex == -1) ? coverageName : coverageName.substring(
					0, dotIndex);
			final int pathIndex = coverageName.lastIndexOf(File.separator);
			if (pathIndex > 0)
				coverageName = coverageName.substring(pathIndex + 1,
						coverageName.length());

			// also create a layer wrapper
			res = new ExternallyManagedDataLayer(ChartBoundsWrapper.WORLDIMAGE_TYPE,
					coverageName, fileName);
		}
		return res;

	}
	



	public static class RasterExtentHelper
	{
		public static MWC.GUI.Layer loadRasters(String fileName, Layers parent)
		{
			// and sort out the parent folder name
			File theFile = new File(fileName);
			String parentPath = theFile.getParent();
			int slasher = parentPath.lastIndexOf(File.separator);
			String folderName = parentPath.substring(slasher+1);
			
			MWC.GUI.Layer res = new ChartFolio(false, Color.red);
			res.setName("Chart lib:" + folderName);
			
			loadExtentsFor(res, fileName, parent);
			
			return res;
		}
		

		protected static void loadExtentsFor(MWC.GUI.Layer extents, String fileName, Layers parent)
		{
			// ok, get the extents for this file

			// ok, populate from this file
			FileDataStore store;
			try
			{
				File openFile = new File(fileName);
				store = FileDataStoreFinder.getDataStore(openFile);
				SimpleFeatureSource featureSource = store.getFeatureSource();

				// sort out the parent path
				final String parentPath = openFile.getParent();
				
				// hey, can we parse it?
				SimpleFeatureCollection fs = featureSource.getFeatures();
				SimpleFeatureIterator fiter = fs.features();
				while (fiter.hasNext())
				{
					// get ready to load this feature
					WorldArea area = null;
					String name = null;

					SimpleFeatureImpl thisF = (SimpleFeatureImpl) fiter.next();
					Collection<? extends Property> values = thisF.getValue();
					Iterator<? extends Property> iter = values.iterator();
					while (iter.hasNext())
					{
						Property thisProp = iter.next();
						String propName = thisProp.getName().toString();

						// is this the geometry?
						if (propName.equals("the_geom"))
						{
							area = getGeometry(thisProp.getValue());
						}
						else if (propName.equals("Name"))
						{
							name = thisProp.getValue().toString();
						}
					}

					// are we done?
					if ((area != null) && (name != null))
					{
						// generate the filename
						String path = parentPath + File.separator + name + ".tif";
						
						ChartBoundsWrapper cw = new ChartBoundsWrapper(name, area.getTopLeft(), area.getBottomRight(), Color.red, path );
						cw.setLayers(parent);
						cw.setLabelLocation(LocationPropertyEditor.CENTRE);
						cw.setLabelVisible(false);
						extents.add(cw);
					}
				}
				// ok, lastly check we have the correct projection files
				ProjSidecarGenerator.addPrj(parentPath, "EPSG:3395");

			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			catch (FactoryException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}

		private static WorldArea getGeometry(Object value)
		{
			WorldArea res = null;
			if (value instanceof MultiPolygon)
			{
				MultiPolygon mp = (MultiPolygon) value;
				Geometry bound = mp.getBoundary();
				Coordinate[] coords = bound.getCoordinates();
				if (coords != null)
				{

					for (int i = 0; i < coords.length; i++)
					{
						Coordinate coordinate = coords[i];
						WorldLocation newL = new WorldLocation(coordinate.y, coordinate.x,
								coordinate.z);
						if (res == null)
							res = new WorldArea(newL, newL);
						else
							res.extend(newL);
					}
				}
			}
			return res;
		}
		
	}
}
