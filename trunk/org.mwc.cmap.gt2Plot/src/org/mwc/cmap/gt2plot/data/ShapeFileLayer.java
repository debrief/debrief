package org.mwc.cmap.gt2plot.data;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.eclipse.core.runtime.Status;
import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.map.FeatureLayer;
import org.geotools.map.Layer;
import org.geotools.styling.Style;
import org.geotools.swt.utils.Utils;
import org.mwc.cmap.core.CorePlugin;

import MWC.GUI.ExternallyManagedDataLayer;
import MWC.GUI.Shapes.ChartBoundsWrapper;

public class ShapeFileLayer extends GeoToolsLayer
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ShapeFileLayer(String layerName, String fileName)
	{
		super(ChartBoundsWrapper.SHAPEFILE_TYPE, layerName, fileName);
	}

	protected Layer loadLayer(File openFile)
	{
		Layer res = null;

		// nope, just load it
		FileDataStore store;
		try
		{
			if (openFile.exists())
			{
				store = FileDataStoreFinder.getDataStore(openFile);
				SimpleFeatureSource featureSource = store.getFeatureSource();
				Style style = Utils.createStyle(openFile, featureSource);
				res = new FeatureLayer(featureSource, style);
			}
			else
			{
				System.err.println("can't find this file");
			}
		}
		catch(FileNotFoundException f)
		{
			CorePlugin.logError(Status.ERROR, "Can't find the shape file", f);
			CorePlugin.showMessage("Load ShapeFile", "Sorry, can't find the requested shapefile:\n" + openFile.getName());
		}
		catch (IOException e)
		{
			CorePlugin.logError(Status.ERROR, "Trouble loading shape file", e);
		}
		catch (Exception e)
		{
			System.err.println("Surely it will get caught!!!");
		}
		return res;
	}

	public static MWC.GUI.Layer read(String fileName)
	{
		MWC.GUI.Layer res = null;
		File openFile = new File(fileName);
		if (openFile != null && openFile.exists())
		{
			// sort out the name of the map
			String coverageName = ChartBoundsWrapper.getCoverageName(fileName);

			// represent it as a normal shapefile
			res = new ExternallyManagedDataLayer(ChartBoundsWrapper.SHAPEFILE_TYPE,
					coverageName, fileName);
		}
		return res;
	}

}
