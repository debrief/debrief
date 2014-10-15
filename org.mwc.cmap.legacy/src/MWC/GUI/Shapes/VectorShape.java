/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package MWC.GUI.Shapes;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;

import MWC.Algorithms.Conversions;
import MWC.GUI.Editable;
import MWC.GUI.PlainWrapper;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldVector;

public class VectorShape extends LineShape
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private WorldDistance _distance = new WorldDistance(2, WorldDistance.NM);
	private EditorType _myEditorLocal;
	private double _bearingDegs = 45;

	public VectorShape(final WorldLocation start, final double bearing, final WorldDistance distance)
	{
		super(start, new WorldLocation(0d, 0d, 0d), "Vector");
		_bearingDegs = bearing;
		_distance = distance;
		calculateEnd();
	}

	public Double getBearing()
	{
		return _bearingDegs;
	}

	public void setBearing(final Double _bearing)
	{
		this._bearingDegs = _bearing;
		calculateEnd();
	}

	@Override
	public void shift(final WorldLocation feature, final WorldVector vector)
	{
		// ok, has the start point been dragged?
		if (feature.equals(_start))
		{
			// ok, just apply it
			_start.addToMe(vector);
			
			// and recalculate
			calculateEnd();

		}
		else
		{
			// we're working with the trailing end!
			feature.addToMe(vector);

			// sort out the new vector back to the start
			final WorldVector newV = feature.subtract(_start);

			// and store the components
			_bearingDegs = Conversions.Rads2Degs(newV.getBearing());
			_distance = new WorldDistance(newV.getRange(), WorldDistance.DEGS);

			firePropertyChange(PlainWrapper.LOCATION_CHANGED, null, null);
		}
	}

	private void calculateEnd()
	{
		final WorldVector _bearingVector = new WorldVector(
				MWC.Algorithms.Conversions.Degs2Rads(_bearingDegs), _distance,
				new WorldDistance(0, 0));
		_end = _start.add(_bearingVector);

		firePropertyChange(PlainWrapper.LOCATION_CHANGED, null, null);
	}

	public WorldDistance getDistance()
	{
		return _distance;
	}

	public void setDistance(final WorldDistance _distance)
	{
		this._distance = _distance;
		calculateEnd();
	}

	@Override
	public void setLine_Start(final WorldLocation loc)
	{
		super.setLine_Start(loc);
		calculateEnd();
	}

	@Override
	public void setLineEnd(final WorldLocation loc)
	{
		// note: we make the line end an editable property so that users can switch
		// between relative (vector) & absolute values
		super.setLineEnd(loc);

		// ok, sort out the offset
		final WorldVector vec = super._end.subtract(super._start);
		_bearingDegs = Conversions.Rads2Degs(vec.getBearing());
		_distance = new WorldDistance(vec.getRange(), WorldDistance.DEGS);
		calculateEnd();
	}

	@Override
	public EditorType getInfo()
	{
		if (_myEditorLocal == null)
			_myEditorLocal = new VectorInfo(this, getName());

		return _myEditorLocal;
	}

	public class VectorInfo extends Editable.EditorType
	{

		public VectorInfo(final LineShape data, final String theName)
		{
			super(data, theName, "");
		}

		public PropertyDescriptor[] getPropertyDescriptors()
		{
			try
			{
				final PropertyDescriptor[] res =
				{
						prop("Line_Start", "the start of the line", SPATIAL),
						prop("Bearing", "the bearing for the vector", SPATIAL),
						prop("Distance", "the size of the vector", SPATIAL),
						prop("LineEnd", "the end of the line", SPATIAL),
						prop("ArrowAtEnd",
								"whether to show an arrow at one end of the line", FORMAT), };

				return res;

			}
			catch (final IntrospectionException e)
			{
				return super.getPropertyDescriptors();
			}
		}
	}
}
