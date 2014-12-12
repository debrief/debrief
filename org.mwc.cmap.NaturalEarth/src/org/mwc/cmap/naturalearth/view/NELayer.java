package org.mwc.cmap.naturalearth.view;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.mwc.cmap.naturalearth.Activator;
import org.mwc.cmap.naturalearth.model.LabelObject;
import org.mwc.cmap.naturalearth.model.NEFeature;
import org.mwc.cmap.naturalearth.model.NEFeature.FeatureType;
import org.mwc.cmap.naturalearth.model.NELibrary;
import org.mwc.cmap.naturalearth.model.NEResolutionGroup;

import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

import MWC.GUI.BaseLayer;
import MWC.GUI.CanvasType;

public class NELayer extends BaseLayer
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private NELibrary _myLibrary = null;

	private HashMap<String, NEStyle> _styles = null;

	@Override
	public void paint(CanvasType dest)
	{
		// do we have styles?
		if (_styles == null)
		{
			_styles = Activator.getDefault().getStyleSet();
		}

		if (_styles == null)
		{
			// graciously fail - throw error
			return;
		}

		// do we have data?
		if (_myLibrary == null)
		{
			_myLibrary = initLibrary(_styles);
		}

		if ((_myLibrary != null) && (_styles != null))
		{
			// ok, we can paint

			// what's the scale
			double scale = dest.getProjection().getScreenArea().getWidth()
					/ dest.getProjection().getDataArea().getWidth();

			// get the relevant resolution
			NEResolutionGroup thisRes = resolutionFor(scale);

			if (thisRes != null)
			{
				// ok, first pass the polygons
				for (NEFeature.FeatureType fType : NEFeature.FeatureType.values())
				{
					// loop through the features
					Iterator<NEFeature> features = thisRes.iterator();

					while (features.hasNext())
					{
						NEFeature feature = (NEFeature) features.next();

						// get the style
						NEStyle thisStyle = _styles.get(feature.getName());

						if (thisStyle.has(fType))
						{
							// and paint it
							paintFeature(fType, feature, thisStyle);
						}
					}
				}

			}
		}
	}

	private NEResolutionGroup resolutionFor(double scale)
	{
		NEResolutionGroup res = null;

		// loop through the resolutions
		Iterator<NEResolutionGroup> iter = _myLibrary.values().iterator();
		while (iter.hasNext())
		{
			NEResolutionGroup group = (NEResolutionGroup) iter.next();
			if (group.canHandle(scale))
			{
				res = group;
				break;
			}
		}

		return res;
	}

	private void paintFeature(FeatureType fType, NEFeature feature,
			NEStyle thisStyle)
	{
		switch (fType)
		{
		case Polygon:
			paintPolygons(thisStyle.getPolyStyle(), feature.getPolygons());
			break;
		case Line:
			paintLines(thisStyle.getLineStyle(), feature.getLines());
			break;
		case Symbol:
			paintSymbols(thisStyle.getSymbolStyle(), feature.getSymbols());
			break;
		case Label:
			paintLabels(thisStyle.getLabelStyle(), feature.getLabels());
			break;
		}
	}

	private void paintLabels(Object labelStyle, ArrayList<LabelObject> labels)
	{
		// TODO Auto-generated method stub

	}

	private void paintSymbols(Object symbolStyle, ArrayList<Point> symbols)
	{
		// TODO Auto-generated method stub

	}

	private void paintLines(Object lineStyle, ArrayList<LineString> lines)
	{
		// TODO Auto-generated method stub

	}

	private void paintPolygons(NEStyle thisStyle, ArrayList<Polygon> polygons)
	{
		// TODO Auto-generated method stub

	}

	private NELibrary initLibrary(HashMap<String, NEStyle> styles)
	{
		// collect a series of resolutions
		NELibrary lib = new NELibrary();

		// retrieve the path
		String dataPath = Activator.getDefault().getLibraryPath();

		// retrieve the set of styles

		// loop through our resolutions

		// loop through our "target" files
		Iterator<String> iter = styles.keySet().iterator();
		while (iter.hasNext())
		{
			String thisFolder = (String) iter.next();
			
			NEStyle thisStyle = styles.get(thisFolder);

			// does this file exist in the folder?
			File thisFolderPath = new File(dataPath + File.pathSeparator + thisFolder);

			// TODO: do the check
			if (thisFolderPath.exists())
			{
				// what's the res for this file?
				String thisResStr = extractResolution(thisFolder);

				if (thisResStr != null)
				{
					// do we have an array for this resolution?
					NEResolutionGroup thisRes = _myLibrary.get(thisResStr);
					if (thisRes == null)
					{
						// nope, create one
						thisRes = new NEResolutionGroup(thisStyle.getminS(), thisStyle.getMaxS());						
						
						// store it
						_myLibrary.put(thisResStr, thisRes);
					}
					
					// yes, load it.
				}
			}

		}

		//

		// TODO Auto-generated method stub
		if (lib.size() > 0)
			return lib;
		else
			return null;
	}

	/**
	 * extract the resolution field from a string like this: ne_10m_coastline in
	 * the above, the res string will be 10m
	 * 
	 * @param thisFolder
	 *          the data folder name
	 * @return
	 */
	public static String extractResolution(String thisFolder)
	{
		String res = null;
		String[] items = thisFolder.split("_", 3);
		if (items.length == 3)
		{
			res = items[1];
		}
		return res;
	}

}
