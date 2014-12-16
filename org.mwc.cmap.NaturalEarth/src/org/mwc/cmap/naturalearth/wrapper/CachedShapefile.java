package org.mwc.cmap.naturalearth.wrapper;

import java.io.IOException;
import java.util.ArrayList;

import org.eclipse.core.runtime.Status;
import org.mwc.cmap.naturalearth.Activator;

import com.bbn.openmap.layer.shape.ESRIPointRecord;
import com.bbn.openmap.layer.shape.ESRIPoly;
import com.bbn.openmap.layer.shape.ESRIPolygonRecord;
import com.bbn.openmap.layer.shape.ESRIRecord;
import com.bbn.openmap.layer.shape.ShapeFile;
import com.bbn.openmap.layer.shape.ESRIPoly.ESRIFloatPoly;

import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldPath;

public class CachedShapefile
{

	private ArrayList<WorldPath> _polygons;
	private final String _filename;

	public CachedShapefile(String fName)
	{
		_filename = fName;
	}

	protected void load()
	{
		// load ourselves from this datafile
		try
		{

			ShapeFile sf = new ShapeFile(_filename);
			ESRIRecord record = sf.getNextRecord();
			while (record != null)
			{
				if (record instanceof ESRIPointRecord)
				{
					// TODO WorldLocation
					double lat = ((ESRIPointRecord) record).getY();
					double lon = ((ESRIPointRecord) record).getX();
					System.out.println("point record: " + lat + ", " + lon);
				}
				else if (record instanceof ESRIPolygonRecord)
				{
					ESRIPolygonRecord polygon = (ESRIPolygonRecord) record;
					ArrayList<WorldPath> polyList = readPolygon(polygon);
					if(_polygons == null)
						_polygons = new ArrayList<WorldPath>();
					
					_polygons.addAll(polyList);
				}
				else
				{
					System.out.println("record: " + record.getClass().getName());
				}
				record = sf.getNextRecord();
			}
		}
		catch (IOException e)
		{
			Activator.logError(Status.ERROR, "Whilst loading " + _filename, e, false);
		}

	}

	public ArrayList<WorldPath> getPolygons()
	{
		if (_polygons == null)
		{
			load();
		}

		return _polygons;
	}

	public ArrayList<WorldPath> getPoints()
	{
		return null;
	}

	public ArrayList<WorldPath> getPaths()
	{
		return null;
	}

	private ArrayList<WorldPath> readPolygon(ESRIPolygonRecord record)
	{
		ArrayList<WorldPath> polyList = new ArrayList<WorldPath>();

		ESRIPoly[] polygons = record.polygons;
		int nPolys = polygons.length;
		if (nPolys <= 0)
			return null;

		for (int i = 0; i < nPolys; i++)
		{
			// these points are in RADIAN lat,lon order!...
			ESRIPoly thisPoly = polygons[i];
			ESRIFloatPoly floatP = (ESRIFloatPoly) thisPoly;
			double[] pts = floatP.getRadians();

			WorldPath wp = new WorldPath();

			for (int j = 0; j < pts.length; j++)
			{
				wp.addPoint(new WorldLocation(pts[j], pts[++j], 0));
			}

			polyList.add(wp);
		}

		return polyList;
	}

}
