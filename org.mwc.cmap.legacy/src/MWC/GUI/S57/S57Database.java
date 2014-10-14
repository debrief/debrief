/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package MWC.GUI.S57;

import java.awt.Color;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import MWC.GUI.Editable;
import MWC.GUI.S57.features.AreaFeature;
import MWC.GUI.S57.features.LineFeature;
import MWC.GUI.S57.features.PointFeature;
import MWC.GUI.S57.features.S57Feature;
import MWC.GUI.S57.features.PointFeature.PointPainter;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldLocation;

import com.bbn.openmap.layer.vpf.MutableInt;
import com.bbn.openmap.util.Debug;

public class S57Database
{

	private static HashMap<Integer, VectorRecord> _listVI = new HashMap<Integer, VectorRecord>();

	private static HashMap<Integer, VectorRecord> _listVC = new HashMap<Integer, VectorRecord>();

	private static HashMap<Integer, VectorRecord> _listVE = new HashMap<Integer, VectorRecord>();

	private static HashMap<Integer, VectorRecord> _listVF = new HashMap<Integer, VectorRecord>();

	/**
	 * 2-d coordinate multiplication factor
	 */
	private double _2dFactor;

	/**
	 * 3-d coordinate multiplication factor
	 */
	private double _3dFactor;

	/**
	 * keep track of how the loading's going
	 */
	private final boolean _dsidLoaded = false;

	private final boolean _dssiLoaded = false;

	private final boolean _dspmLoaded = false;


	/**
	 * the vector that's currently being built up.
	 */
	private VectorRecord _pendingVectorRecord;

	/**
	 * our vectors
	 */
	private final HashMap<Integer, VectorRecord> _myVectors = new HashMap<Integer, VectorRecord>();

	/**
	 * the feature that's currently being built up
	 */
	private FeatureRecord _pendingFeatureRecord;

	/**
	 * our features
	 */
	private final HashMap<Integer, FeatureRecord> _myFeatures = new HashMap<Integer, FeatureRecord>();

	public HashMap<String, Editable> _items = new HashMap<String, Editable>();

	private WorldArea _area = null;

	public void extend(final WorldLocation loc)
	{
		if (_area == null)
			_area = new WorldArea(loc, loc);
		else
			_area.extend(loc);
	}

	public WorldArea getArea()
	{
		return _area;
	}

	public Collection<Editable> getFeatures()
	{
		return _items.values();
	}

	public void addFeature(final S57Feature feature)
	{
		_items.put(feature.getName(), feature);
	}

	public S57Feature getFeature(final String name)
	{
		return (S57Feature) _items.get(name);
	}

	public void loadDatabase(final String filename, final boolean fspt_repeating)
	{
		// sort out where the file is
		DDFModule oModule;

		try
		{

			oModule = new DDFModule(filename);

			/* -------------------------------------------------------------------- */
			/* Loop reading records till there are none left. */
			/* -------------------------------------------------------------------- */
			DDFRecord poRecord;

			while ((poRecord = oModule.readRecord()) != null)
			{
				// do we have a vector open?
				if (_pendingVectorRecord != null)
				{
					// yup, better store it!
					_myVectors.put(_pendingVectorRecord._rcid, _pendingVectorRecord);

					// and clear the pointer
					_pendingVectorRecord = null;
				}

				// do we have a feature open?
				if (_pendingFeatureRecord != null)
				{
					// yup, better store it!
					_myFeatures.put(_pendingFeatureRecord._rcid, _pendingFeatureRecord);

					// ok, tidy up this feature
					closeFeature(_pendingFeatureRecord);

					// and clear the pointer
					_pendingFeatureRecord = null;
				}

				/* ------------------------------------------------------------ */
				/* Loop over each field in this particular record. */
				/* ------------------------------------------------------------ */
				final Iterator<DDFField> iter = poRecord.iterator();
				while (iter.hasNext())
				{
					final DDFField nextField = (DDFField) iter.next();
					loadNextField(nextField);
				}
			}

		}
		catch (final IOException ioe)
		{
			Debug.error(ioe.getMessage());
			ioe.printStackTrace();
		}

	}

	protected HashMap<Integer, VectorRecord> vectorListFor(final int rcnm)
	{
		HashMap<Integer, VectorRecord> _map = null;

		switch (rcnm)
		{
		case 110:
		{
			_map = _listVI;
			break;
		}
		case 120:
		{
			_map = _listVC;
			break;
		}
		case 130:
		{
			_map = _listVE;
			break;
		}
		case 140:
		{
			_map = _listVF;
			break;
		}
		}
		return _map;
	}

	abstract private static class StoreFeature
	{
		final protected String _type;
		final protected Color _color;
		final protected boolean _visible;
		final protected int _objl;
		public StoreFeature(final int objl, final String type, final Color color, final boolean visible)
		{
			_type = type;
			_color = color;
			_visible = visible;
			_objl  = objl;
		}
		abstract public void store(FeatureRecord feaure);
		public boolean canDo(final int objl)
		{
			return objl == _objl;
		}
		
	}
	
	private class StorePoints extends StoreFeature
	{		
		final PointPainter _painter;
		public StorePoints(final int objl, final String type, final Color color, final boolean visible,
				final PointPainter painter)
		{
			super(objl, type, color, visible);
			_painter = painter;
		}

		public StorePoints(final int objl, final String type, final Color color, final boolean visible)
		{
			this(objl, type, color, visible,new PointFeature.LabelPainter(type));
		}
		
		public void store(final FeatureRecord feature)
		{
			final String TYPE = _type;
			PointFeature cl = (PointFeature) getFeature(TYPE);
			if (cl == null)
			{
				cl = new PointFeature(TYPE, PointFeature.DEFAULT_SCALE, _color, _painter);
				cl.setVisible(_visible);
				addFeature(cl);
			}
			storePoints(feature, cl);			
		}		
	}
	
	
	private class StoreLine extends StoreFeature
	{		
		public StoreLine(final int objl, final String type, final Color color, final boolean visible)
		{
			super(objl, type, color, visible);
		}
		public void store(final FeatureRecord feature)
		{
			final String TYPE = _type;
			LineFeature cl = (LineFeature) getFeature(TYPE);
			if (cl == null)
			{
				cl = new LineFeature(TYPE, LineFeature.DEFAULT_SCALE, _color);
				cl.setVisible(_visible);
				addFeature(cl);
			}
			storeLine(feature, cl);			
		}		
	}	

	private class StoreArea extends StoreLine
	{		
		public StoreArea(final int objl, final String type, final Color color, final boolean visible)
		{
			super(objl, type, color, visible);
		}
		public void store(final FeatureRecord feature)
		{
			final String TYPE = _type;
			AreaFeature cl = (AreaFeature) getFeature(TYPE);
			if (cl == null)
			{
				cl = new AreaFeature(TYPE, LineFeature.DEFAULT_SCALE, _color);
				cl.setVisible(_visible);
				addFeature(cl);
			}
			storeLine(feature, cl);			
		}		
	}	

		
	
	
	private Vector<StoreFeature> _closers = null;
	
	private void closeFeature(final FeatureRecord feature)
	{
		if(_closers == null)
		{
			_closers = new Vector<StoreFeature>(0,1);
			_closers.add(new StoreArea(1, "Administrative Area", Color.white, false));
			_closers.add(new StorePoints(3, "Anchor Berth", Color.orange, true));
			_closers.add(new StoreArea(4, "Anchorage Area", Color.orange, true));
			_closers.add(new StorePoints(4, "Beacon, Cardinal", Color.orange, true));
			_closers.add(new StorePoints(5, "Beacon, Installation", Color.orange, true));
			_closers.add(new StorePoints(6, "Beacon, isolated danger", Color.orange, true));
			_closers.add(new StorePoints(7, "Beacon, lateral", Color.orange, true));
			_closers.add(new StorePoints(8, "Beacon, safe water", Color.orange, true));
			_closers.add(new StorePoints(9, "Beacon, special purpose/general", Color.orange, true));
			_closers.add(new StorePoints(14, "Buoy, Cardinal", Color.orange, true));
			_closers.add(new StorePoints(15, "Buoy, Installation", Color.orange, true));
			_closers.add(new StorePoints(16, "Buoy, isolated danger", Color.orange, true));
			_closers.add(new StorePoints(17, "Buoy, lateral", Color.orange, true));
			_closers.add(new StorePoints(18, "Buoy, safe water", Color.orange, true));
			_closers.add(new StorePoints(19, "Buoy, special purpose/general", Color.orange, true));
			_closers.add(new StoreLine(23, "Canal", Color.yellow, true));
			_closers.add(new StoreLine(24, "Canal bank", Color.yellow, true));
			_closers.add(new StoreLine(30, "Coastline", Color.yellow, false));
			_closers.add(new StoreLine(31, "Coastal Zone", Color.yellow, true));
			_closers.add(new StoreLine(32, "Coastal Area", Color.yellow, true));
			_closers.add(new StoreLine(42, "Depth Area", Color.GRAY, false));
			_closers.add(new StoreLine(43, "Depth Contour", Color.DARK_GRAY, false));
			_closers.add(new StorePoints(65, "Hulk", Color.orange, true));
			_closers.add(new StoreArea(71, "Land Area", new Color(204,197,93), true));
			_closers.add(new StoreLine(72, "Land Elevation", new Color(204,197,93), true));
			_closers.add(new StoreLine(73, "Land Region", new Color(204,197,93), true));
			_closers.add(new StorePoints(74, "Landmark", Color.orange, false));
			_closers.add(new StorePoints(75, "Light", Color.orange, false));
			_closers.add(new StorePoints(84, "Mooring/warping facility", Color.orange, false));
			_closers.add(new StorePoints(86, "Obstruction", Color.orange, false));
			_closers.add(new StorePoints(87, "Offshore Platform", Color.orange, false));
			_closers.add(new StorePoints(87, "Offshore production area", Color.orange, false));
			_closers.add(new StorePoints(91, "Pilot boarding place", Color.orange, false));
			_closers.add(new StorePoints(103, "Radar transponder point", Color.orange, false));
			_closers.add(new StorePoints(104, "Radio calling-in point", Color.orange, false));
			_closers.add(new StorePoints(109, "Recommended track", Color.orange, false));
			_closers.add(new StoreArea(112, "Restricted area", new Color(196,112,119), false));
			_closers.add(new StoreArea(119, "Sea area/named water area", new Color(116,112,199), false));
			_closers.add(new StoreArea(121, "Seabed Area", new Color(116,112,119), false));
			_closers.add(new StorePoints(124, "Signal station, warning", Color.orange, false));
			_closers.add(new StorePoints(129, "Soundings", new Color(136,152,139), false, new PointFeature.DepthPainter()));
			_closers.add(new StoreArea(135, "Territorial sea area", Color.gray, false));
			_closers.add(new StorePoints(144, "Top Mark", Color.orange, false));
			_closers.add(new StorePoints(153, "Underwater Rock", Color.orange, false));
			_closers.add(new StoreArea(154, "Unsurveyed area", Color.gray, false));
			_closers.add(new StorePoints(159, "Wreck", Color.orange, false));
			_closers.add(new StoreArea(301, "Compilation scale of data", Color.gray, false));
			_closers.add(new StoreArea(302, "Coverage", Color.gray, false));
			_closers.add(new StoreArea(306, "Navigational System of Marks", Color.gray, false));
			_closers.add(new StoreArea(308, "Quality of data", Color.white, false));
		}
		
		boolean found = false;
		
		for (final Iterator<StoreFeature> iter = _closers.iterator(); iter.hasNext();)
		{
			final StoreFeature storer = (StoreFeature) iter.next();
			if(storer.canDo(feature._objl))
			{
				storer.store(feature);
				found = true;
				break;
			}
		}
		
		if(!found)
			System.err.println("Not found handler for:" + feature._objl);
		
	}

//	private void storeArea(FeatureRecord feature, LineFeature cl)
//	{
//		// right, I presume this is a line-based feature
//		assert (feature._prim == 3);
//
//		// right, what are the names?
//		Vector<FeatureRecord.FeatureToSpatialPointer> names = feature._spatialPointers;
//		for (Iterator<FeatureRecord.FeatureToSpatialPointer> iter = names.iterator(); iter
//				.hasNext();)
//		{
//			FeatureRecord.FeatureToSpatialPointer element = (FeatureRecord.FeatureToSpatialPointer) iter
//					.next();
//
//			// try to find it...
//			HashMap<Integer, VectorRecord> list = vectorListFor(element._rcnm);
//
//			if (element._rcnm <= 130)
//			{
//				VectorRecord rec = list.get(element._rcid);
//
//				Vector<WorldLocation> theList = new Vector<WorldLocation>(0, 1);
//
//				// theList.addAll(rec.coords);
//				populateThisList(theList, rec);
//
//				cl.addLine(theList);
//			}
//		}
//	}

	void storeLine(final FeatureRecord feature, final LineFeature cl)
	{
		// right, I presume this is a line-based feature
		// assert (feature._prim == 2);

		// right, what are the names?
		final Vector<FeatureRecord.FeatureToSpatialPointer> names = feature._spatialPointers;
		for (final Iterator<FeatureRecord.FeatureToSpatialPointer> iter = names.iterator(); iter
				.hasNext();)
		{
			final FeatureRecord.FeatureToSpatialPointer element = (FeatureRecord.FeatureToSpatialPointer) iter
					.next();

			// try to find it...
			final HashMap<Integer, VectorRecord> list = vectorListFor(element._rcnm);
			final VectorRecord rec = list.get(element._rcid);

			final Vector<WorldLocation> theList = new Vector<WorldLocation>(0, 1);

			populateThisList(theList, rec);

			cl.addLine(theList);
		}
	}

//	private void store129(FeatureRecord feature)
//	{
//		String myType = "Soundings";
//	}

	void storePoints(final FeatureRecord feature, final PointFeature myType)
	{
		// right, I presume this is a line-based feature
	//	assert (feature._prim == 1);

		// right, what are the names?
		final Vector<FeatureRecord.FeatureToSpatialPointer> names = feature._spatialPointers;
		for (final Iterator<FeatureRecord.FeatureToSpatialPointer> iter = names.iterator(); iter
				.hasNext();)
		{
			final FeatureRecord.FeatureToSpatialPointer element = (FeatureRecord.FeatureToSpatialPointer) iter
					.next();
			// try to find it...
			final HashMap<Integer, VectorRecord> list = vectorListFor(element._rcnm);

			final VectorRecord rec = list.get(element._rcid);

			if (rec == null)
			{
				System.err.println("vector not found for + name:" + element._rcnm + " id:"
						+ element._rcid);
			}
			else
			{
				final Vector<WorldLocation> theList = new Vector<WorldLocation>(0, 1);

				populateThisList(theList, rec);

				myType.add(theList);
			}

		}
	}



	private void populateThisList(final Vector<WorldLocation> tgt, final VectorRecord host)
	{
		// add my coordinates first
		if (host.coords != null)
			tgt.addAll(host.coords);

		final Vector<VectorRecord.VectorPointer> pointers = host._pointers;
		if (pointers != null)
		{
			for (final Iterator<VectorRecord.VectorPointer> iter = pointers.iterator(); iter.hasNext();)
			{
				final VectorRecord.VectorPointer nextP = (VectorRecord.VectorPointer) iter.next();

				// get the vector
				final HashMap<Integer, VectorRecord> nextList = vectorListFor(nextP._rcnm1);
				final VectorRecord nextV = nextList.get(nextP._rcid1);

				if (nextV != host)
				{

					if (nextV.coords != null)
					{
						// extract the coords
						tgt.addAll(nextV.coords);
					}

					// process the rest
	//				Vector nextPointers = nextV._pointers;

					// and store them
					populateThisList(tgt, nextV);
				}
			}
		}
	}

//	private void store119(FeatureRecord feature)
//	{
//		 System.out.println("storing named sea!");
//	}
//
//	private void store1(FeatureRecord feature)
//	{
//		final String TYPE = "Administrative Area";
//		LineFeature cl = (LineFeature) getFeature(TYPE);
//		if (cl == null)
//		{
//			cl = new LineFeature(TYPE, null, null, Color.green);
//			cl.setVisible(false);
//			addFeature(cl);
//		}
//
//		storeLine(feature, cl);
//	}	
	
//	private void store42(FeatureRecord feature)
//	{
//		final String TYPE = "Depth Area";
//		LineFeature cl = (LineFeature) getFeature(TYPE);
//		if (cl == null)
//		{
//			cl = new LineFeature(TYPE, null, null, Color.yellow);
//			cl.setVisible(false);
//			addFeature(cl);
//		}
//
//		storeLine(feature, cl);
//	}

//	private void store121(FeatureRecord feature)
//	{
//		// System.out.println("storing seabed area!");
//	}
//
//	private void store43(FeatureRecord feature)
//	{
//		// hey, depth contours. go for it!
//		// System.out.println("storing depth contour!!");
//	}
//
//	private void ignoreFeature(FeatureRecord feature)
//	{
//		// hey, just ignore it...
//	}

	private void loadDSPM(final DDFField field)
	{
		final Vector<HashMap<String, Object>> fields = loadFields(field);
		for (final Iterator<HashMap<String, Object>> iterator = fields.iterator(); iterator.hasNext();)
		{
			final HashMap<String, Object> res = (HashMap<String, Object>) iterator.next();
			final Integer factor2d = (Integer) res.get("COMF");
			final Integer factor3d = (Integer) res.get("SOMF");

			_2dFactor = factor2d.doubleValue();
			_3dFactor = factor3d.doubleValue();
		}
	}

	private void loadDSSI(final DDFField field)
	{
		System.out.println("==== loading dssi");
		final Vector<HashMap<String, Object>> fields = loadFields(field);
		for (final Iterator<HashMap<String, Object>> iterator = fields.iterator(); iterator.hasNext();)
		{
			final HashMap<String, Object> res = (HashMap<String, Object>) iterator.next();
			final Iterator<String> iter = res.keySet().iterator();
			System.out.println("stats loaded");
			while (iter.hasNext())
			{
				final String thisKey = (String) iter.next();
				System.out.println(" " + thisKey + " = " + res.get(thisKey));
			}
		}
	}

	private void loadDSID(final DDFField field)
	{
		System.out.println("==== loading dsid");
		final Vector<HashMap<String, Object>> fields = loadFields(field);
		for (final Iterator<HashMap<String, Object>> iterator = fields.iterator(); iterator.hasNext();)
		{
			@SuppressWarnings("unused")
			final
			HashMap<String, Object> res = (HashMap<String, Object>) iterator.next();
		}
	}

	private Vector<HashMap<String, Object>> loadFields(final DDFField field)
	{
		final Vector<HashMap<String, Object>> res = new Vector<HashMap<String, Object>>(0, 1);

		final DDFFieldDefinition poFieldDefn = field.getFieldDefn();

		byte[] pachFieldData = field.getData();
		int nBytesRemaining = field.getDataSize();
		final int subfieldCount = poFieldDefn.getSubfieldCount();
		final int repeatCount = field.getRepeatCount();
		/* -------------------------------------------------------- */
		/* Loop over the repeat count for this fields */
		/* subfields. The repeat count will almost */
		/* always be one. */
		/* -------------------------------------------------------- */
		for (int iRepeat = 0; iRepeat < repeatCount; iRepeat++)
		{

			final HashMap<String, Object> thisMap = new HashMap<String, Object>();
			res.add(thisMap);

			/* -------------------------------------------------------- */
			/* Loop over all the subfields of this field, advancing */
			/* the data pointer as we consume data. */
			/* -------------------------------------------------------- */
			for (int iSF = 0; iSF < subfieldCount; iSF++)
			{

				final DDFSubfieldDefinition poSFDefn = poFieldDefn.getSubfieldDefn(iSF);
				final int nBytesConsumed = loadSubfield(thisMap, poSFDefn, pachFieldData,
						nBytesRemaining);
				nBytesRemaining -= nBytesConsumed;
				final byte[] tempData = new byte[pachFieldData.length - nBytesConsumed];
				System.arraycopy(pachFieldData, nBytesConsumed, tempData, 0, tempData.length);
				pachFieldData = tempData;
			}
		}

		return res;
	}

	private void loadNextField(final DDFField field)
	{
		final DDFFieldDefinition poFieldDefn = field.getFieldDefn();

		final String fieldName = poFieldDefn.getName();
		if (!specialHandling(fieldName, field))
		{

			// Report general information about the field.
			// dOut(" Field " + poFieldDefn.getName() + ": " +
			// poFieldDefn.getDescription());

			// Get pointer to this fields raw data. We will move through
			// it consuming data as we report subfield values.

			byte[] pachFieldData = field.getData();
			int nBytesRemaining = field.getDataSize();

			/* -------------------------------------------------------- */
			/* Loop over the repeat count for this fields */
			/* subfields. The repeat count will almost */
			/* always be one. */
			/* -------------------------------------------------------- */
			for (int iRepeat = 0; iRepeat < field.getRepeatCount(); iRepeat++)
			{
				/* -------------------------------------------------------- */
				/* Loop over all the subfields of this field, advancing */
				/* the data pointer as we consume data. */
				/* -------------------------------------------------------- */
				for (int iSF = 0; iSF < poFieldDefn.getSubfieldCount(); iSF++)
				{

					final DDFSubfieldDefinition poSFDefn = poFieldDefn.getSubfieldDefn(iSF);
					final int nBytesConsumed = viewSubfield(poSFDefn, pachFieldData, nBytesRemaining);
					nBytesRemaining -= nBytesConsumed;
					final byte[] tempData = new byte[pachFieldData.length - nBytesConsumed];
					System.arraycopy(pachFieldData, nBytesConsumed, tempData, 0, tempData.length);
					pachFieldData = tempData;
				}
			}
		}
	}

	protected int viewSubfield(final DDFSubfieldDefinition poSFDefn, final byte[] pachFieldData,
			final int nBytesRemaining)
	{

		final MutableInt nBytesConsumed = new MutableInt();

		final DDFDataType ddfdt = poSFDefn.getType();

		if (ddfdt == DDFDataType.DDFInt)
		{
			Debug.output("        " + poSFDefn.getName() + " = "
					+ poSFDefn.extractIntData(pachFieldData, nBytesRemaining, nBytesConsumed));
		}
		else if (ddfdt == DDFDataType.DDFFloat)
		{
			Debug.output("        " + poSFDefn.getName() + " = "
					+ poSFDefn.extractFloatData(pachFieldData, nBytesRemaining, nBytesConsumed));
		}
		else if (ddfdt == DDFDataType.DDFString)
		{
			Debug.output("        " + poSFDefn.getName() + " = "
					+ poSFDefn.extractStringData(pachFieldData, nBytesRemaining, nBytesConsumed));
		}
		else if (ddfdt == DDFDataType.DDFBinaryString)
		{
			Debug.output("        " + poSFDefn.getName() + " = "
					+ poSFDefn.extractStringData(pachFieldData, nBytesRemaining, nBytesConsumed));
		}

		return nBytesConsumed.value;
	}

	protected int loadSubfield(final HashMap<String, Object> dest,
			final DDFSubfieldDefinition poSFDefn, final byte[] pachFieldData, final int nBytesRemaining)
	{

		final MutableInt nBytesConsumed = new MutableInt();

		final DDFDataType ddfdt = poSFDefn.getType();

		final String fieldName = poSFDefn.getName();

		if (ddfdt == DDFDataType.DDFInt)
		{
			dest.put(fieldName, poSFDefn.extractIntData(pachFieldData, nBytesRemaining,
					nBytesConsumed));
		}
		else if (ddfdt == DDFDataType.DDFFloat)
		{
			dest.put(fieldName, poSFDefn.extractFloatData(pachFieldData, nBytesRemaining,
					nBytesConsumed));
		}
		else if (ddfdt == DDFDataType.DDFString)
		{
			dest.put(fieldName, poSFDefn.extractStringData(pachFieldData, nBytesRemaining,
					nBytesConsumed));
		}
		else if (ddfdt == DDFDataType.DDFBinaryString)
		{
			dest.put(fieldName, poSFDefn.extractStringData(pachFieldData, nBytesRemaining,
					nBytesConsumed));
		}

		return nBytesConsumed.value;
	}

	private boolean specialHandling(final String type, final DDFField field)
	{
		boolean res = false;
		if (!_dsidLoaded)
		{
			if (type.equals("DSID"))
			{
				res = true;
				loadDSID(field);
			}
		}

		if (!_dssiLoaded)
		{
			if (type.equals("DSSI"))
			{
				res = true;
				loadDSSI(field);
			}
		}

		if (!_dspmLoaded)
		{
			if (type.equals("DSPM"))
			{
				res = true;
				loadDSPM(field);
			}
		}

		if (type.equals("VRID"))
		{
			res = true;
			loadVRID(field);
		}
		else if (type.equals("SG3D"))
		{
			res = true;
			loadSG3D(field);
		}
		else if (type.equals("SG2D"))
		{
			res = true;
			loadSG2D(field);
		}
		else if (type.equals("ATTV"))
		{
			res = true;
			loadATTV(field);
		}
		else if (type.equals("VRPT"))
		{
			res = true;
			loadVRPT(field);
		}
		else if (type.equals("FRID"))
		{
			res = true;
			loadFRID(field);
		}
		else if (type.equals("FOID"))
		{
			res = true;
			loadFOID(field);
		}
		else if (type.equals("ATTF"))
		{
			res = true;
			loadATTF(field);
		}
		else if (type.equals("FSPT"))
		{
			res = true;
			loadFSPT(field);
		}
		else if (type.equals("FFPT"))
		{
			res = true;
			loadFFPT(field);
		}

		if (type.equals("0001"))
			res = true;

		if (!res)
		{
			System.out.println("\\ not handled:" + type);
			System.exit(0);
		}

		return res;
	}

	private int getInt(final Object val)
	{
		final Integer res = (Integer) val;
		return res.intValue();
	}

	private void loadFSPT(final DDFField field)
	{

		final Vector<HashMap<String, Object>> res = loadFields(field);

		for (final Iterator<HashMap<String, Object>> iterator = res.iterator(); iterator.hasNext();)
		{
			final HashMap<String, Object> nextFSPT = (HashMap<String, Object>) iterator.next();
			final String name = (String) nextFSPT.get("NAME");
			final int ornt = getInt(nextFSPT.get("ORNT"));
			final int usage = getInt(nextFSPT.get("USAG"));
			final int mask = getInt(nextFSPT.get("MASK"));

			if (_pendingFeatureRecord == null)
				System.err.println("NO PENDING VECTOR WAITING");

			_pendingFeatureRecord.addFeatureToSpatialPointer(name, ornt, usage, mask);
		}
	}

	private void loadFFPT(final DDFField field)
	{

		final Vector<HashMap<String, Object>> res = loadFields(field);

		for (final Iterator<HashMap<String, Object>> iterator = res.iterator(); iterator.hasNext();)
		{
			final HashMap<String, Object> nextFSPT = (HashMap<String, Object>) iterator.next();
			final String lnam = (String) nextFSPT.get("LNAM");
			final int rind = getInt(nextFSPT.get("RIND"));
			final String comt = (String) nextFSPT.get("COMT");

			if (_pendingFeatureRecord == null)
				System.err.println("NO PENDING VECTOR WAITING");

			_pendingFeatureRecord.addFeatureToFeaturePointer(lnam, rind, comt);
		}
	}

	private void loadATTF(final DDFField field)
	{
		final Vector<HashMap<String, Object>> fields = loadFields(field);
		for (final Iterator<HashMap<String, Object>> iterator = fields.iterator(); iterator.hasNext();)
		{
			final HashMap<String, Object> res = (HashMap<String, Object>) iterator.next();
			final int attl = getInt(res.get("ATTL"));
			final String atvl = (String) res.get("ATVL");

			if (_pendingFeatureRecord == null)
				System.err.println("NO PENDING FEATURE WAITING");

			_pendingFeatureRecord.addFeatureAttribute(attl, atvl);
		}
	}

	private void loadFOID(final DDFField field)
	{
		final Vector<HashMap<String, Object>> fields = loadFields(field);
		for (final Iterator<HashMap<String, Object>> iterator = fields.iterator(); iterator.hasNext();)
		{
			final HashMap<String, Object> res = (HashMap<String, Object>) iterator.next();
			final int fidn = getInt(res.get("FIDN"));
			final int fids = getInt(res.get("FIDS"));

			if (_pendingFeatureRecord == null)
				System.err.println("NO PENDING FEATURE WAITING");

			_pendingFeatureRecord.setFeatureID(fidn, fids);
		}
	}

	private void loadFRID(final DDFField field)
	{
		final Vector<HashMap<String, Object>> fields = loadFields(field);
		for (final Iterator<HashMap<String, Object>> iterator = fields.iterator(); iterator.hasNext();)
		{
			final HashMap<String, Object> res = (HashMap<String, Object>) iterator.next();
			final int rcnm = getInt(res.get("RCNM"));
			final int rcid = getInt(res.get("RCID"));
			final int prim = getInt(res.get("PRIM"));
			final int grup = getInt(res.get("GRUP"));
			final int objl = getInt(res.get("OBJL"));

			if (objl == 30)
				System.err.println("  found a coastline!");

			if (_pendingFeatureRecord != null)
				System.err.println("PENDING FEATURE NOT CLOSED!!!!");

			_pendingFeatureRecord = new FeatureRecord(rcnm, rcid, prim, grup, objl);
		}
	}

	private void loadATTV(final DDFField field)
	{
		final Vector<HashMap<String, Object>> fields = loadFields(field);
		for (final Iterator<HashMap<String, Object>> iterator = fields.iterator(); iterator.hasNext();)
		{
			final HashMap<String, Object> res = (HashMap<String, Object>) iterator.next();
			final int attl = getInt(res.get("ATTL"));
			final String atvl = (String) res.get("ATVL");

			if (_pendingVectorRecord == null)
				System.err.println("NO PENDING VECTOR WAITING");

			_pendingVectorRecord.addVectorAttributes(attl, atvl);
		}
	}

	private void loadVRPT(final DDFField field)
	{
		final Vector<HashMap<String, Object>> fields = loadFields(field);
		for (final Iterator<HashMap<String, Object>> iterator = fields.iterator(); iterator.hasNext();)
		{
			final HashMap<String, Object> res = (HashMap<String, Object>) iterator.next();
			final String name = (String) res.get("NAME");
			final int usage = getInt(res.get("USAG"));
			final int topi = getInt(res.get("TOPI"));
			final int mask = getInt(res.get("MASK"));

			if (_pendingVectorRecord == null)
				System.err.println("NO PENDING VECTOR WAITING");

			_pendingVectorRecord.addVectorPointer(name, usage, topi, mask);
		}
	}

	private void loadSG3D(final DDFField field)
	{
		final Vector<HashMap<String, Object>> fields = loadFields(field);
		for (final Iterator<HashMap<String, Object>> iterator = fields.iterator(); iterator.hasNext();)
		{
			final HashMap<String, Object> res = (HashMap<String, Object>) iterator.next();
			final int x = getInt(res.get("XCOO"));
			final int y = getInt(res.get("YCOO"));
			final int z = getInt(res.get("VE3D"));

			_pendingVectorRecord.addPoint(x, y, z, _2dFactor, _3dFactor);
		}
	}

	private void loadSG2D(final DDFField field)
	{
		final Vector<HashMap<String, Object>> fields = loadFields(field);
		for (final Iterator<HashMap<String, Object>> iterator = fields.iterator(); iterator.hasNext();)
		{
			final HashMap<String, Object> res = (HashMap<String, Object>) iterator.next();
			final int x = getInt(res.get("XCOO"));
			final int y = getInt(res.get("YCOO"));
			final int z = 0;

			_pendingVectorRecord.addPoint(x, y, z, _2dFactor, _3dFactor);
		}
	}

	private void loadVRID(final DDFField field)
	{
		final Vector<HashMap<String, Object>> fields = loadFields(field);
		for (final Iterator<HashMap<String, Object>> iterator = fields.iterator(); iterator.hasNext();)
		{
			final HashMap<String, Object> res = (HashMap<String, Object>) iterator.next();
			final int rcnm = getInt(res.get("RCNM"));
			final int rcid = getInt(res.get("RCID"));
			if (_pendingVectorRecord != null)
				System.err.println("PENDING VECTOR NOT CLOSED!!!!");

			_pendingVectorRecord = new VectorRecord(rcnm, rcid);
		}
	}

	private class VectorRecord
	{
		private final int _rcnm;

		int _rcid;

		Vector<WorldLocation> coords;

		Vector<VectorPointer> _pointers = new Vector<VectorPointer>();

		private final Vector<VectorAttribute> _attributes = new Vector<VectorAttribute>();

		public class VectorAttribute
		{
			public VectorAttribute(final int attl, final String atvl)
			{
			}
		}

		public class VectorPointer
		{
			final int _rcnm1;

			final int _rcid1;

			public VectorPointer(final String name, final int usage, final int topi, final int mask)
			{
				final int rcnm = name.charAt(0);
				final int rcid = name.charAt(1) + name.charAt(2) * 256 + name.charAt(3) * 256 * 256
						+ name.charAt(4) * 256 * 256 * 256;

				_rcnm1 = rcnm;
				_rcid1 = rcid;
			}
		}

		public VectorRecord(final int rcnm, final int rcid)
		{
			_rcnm = rcnm;
			_rcid = rcid;

			final HashMap<Integer, VectorRecord> _map = vectorListFor(_rcnm);

			_map.put(_rcid, this);
		}

		public void addVectorPointer(final String name, final int usage, final int topi, final int mask)
		{
			final VectorPointer vp = new VectorPointer(name, usage, topi, mask);
			_pointers.add(vp);

		}

		public void addVectorAttributes(final int attl, final String atvl)
		{
			_attributes.add(new VectorAttribute(attl, atvl));
		}

		public void addPoint(final int x, final int y, final int z, final double _2d, final double _3d)
		{
			// do the scaling
			final double X = x / _2d;
			final double Y = y / _2d;
			final double Z = z / _3d;
			final WorldLocation newLoc = new WorldLocation(Y, X, Z);

			if (coords == null)
				coords = new Vector<WorldLocation>();
			coords.add(newLoc);

			extend(newLoc);
		}
	}

	private static class FeatureRecord
	{
		final int _rcid;

		final int _objl;

		private final Vector<FeatureAttribute> _attributes = new Vector<FeatureAttribute>(0, 1);

		Vector<FeatureToSpatialPointer> _spatialPointers = new Vector<FeatureToSpatialPointer>(
				0, 1);

		private final Vector<FeatureToFeaturePointer> _featurePointers = new Vector<FeatureToFeaturePointer>(
				0, 1);

		public static class FeatureAttribute
		{
			public FeatureAttribute(final int attl, final String atvl)
			{
			}
		}

		public static class FeatureToSpatialPointer
		{
			int _rcnm;

			final int _rcid;

			public FeatureToSpatialPointer(final String name, final int ornt, final int usage, final int mask)
			{
				_rcnm = name.charAt(0);

				// HACK! We should be reading chars as chars, we read them as
				// bytes. Values over 128 get made -ve, which doesn't work.
				if (_rcnm == 8218)
					_rcnm = 130;

				final int v1 = name.charAt(1);
				final int v2 = name.charAt(2);
				final int v3 = name.charAt(3);
				final int v4 = name.charAt(4);
				_rcid = v1 + v2 * 256 + v3 * 256 * 256 + v4 * 256 * 256 * 256;

				if (_rcid > 40000)
					System.err.println("too much!!");
			}
		}

		public static class FeatureToFeaturePointer
		{
			public FeatureToFeaturePointer(final String lnam, final int rind, final String comt)
			{
			}
		}

		public FeatureRecord(final int rcnm, final int rcid, final int prim, final int grup, final int objl)
		{
			_rcid = rcid;
			_objl = objl;
		}

		public void addFeatureToFeaturePointer(final String lnam, final int rind, final String comt)
		{
			_featurePointers.add(new FeatureToFeaturePointer(lnam, rind, comt));
		}

		public void addFeatureToSpatialPointer(final String name, final int ornt, final int usage, final int mask)
		{
			_spatialPointers.add(new FeatureToSpatialPointer(name, ornt, usage, mask));
		}

		public void addFeatureAttribute(final int attl, final String atvl)
		{
			final FeatureAttribute fa = new FeatureAttribute(attl, atvl);
			_attributes.add(fa);
		}

		public void setFeatureID(final int fidn, final int fids)
		{
		}

	}

}
