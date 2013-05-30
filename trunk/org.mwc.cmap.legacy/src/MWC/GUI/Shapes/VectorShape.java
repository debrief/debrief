package MWC.GUI.Shapes;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;

import MWC.GUI.Editable;
import MWC.GUI.Editable.EditorType;
import MWC.GUI.Shapes.LineShape.LineInfo;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldVector;

public class VectorShape extends LineShape {
	private WorldVector _bearingVector;
	private WorldDistance _distance = new WorldDistance(0,0);
	private EditorType _myEditorLocal;
	private double _bearing;
	
	public VectorShape(WorldLocation start, WorldLocation end)
	{
		super(start, end, "Vector");
	}
	
	public VectorShape(WorldLocation start, double bearing, WorldDistance distance) {
		super(start, new WorldLocation(0d,0d, 0d), "Vector");
		_bearing=bearing;
		_distance=distance;
		calculateEnd();
	}
	
	
	public Double getBearing() {
		return _bearing;
	}

	public void setBearing(Double _bearing) {
		this._bearing = _bearing;
		calculateEnd();
	}


	private void calculateEnd() {
		_bearingVector= new WorldVector(MWC.Algorithms.Conversions.Degs2Rads(_bearing), _distance, new WorldDistance(0,0));
		_end = _start.add(_bearingVector);
	}

	public WorldDistance getDistance() {
		return _distance;
	}


	public void setDistance(WorldDistance _distance) {
		this._distance = _distance;
		calculateEnd();
	}
	
	
	@Override
	public void setLine_Start(WorldLocation loc) {
		super.setLine_Start(loc);
		calculateEnd();
	}


	@Override
	public EditorType getInfo() {
		if (_myEditorLocal == null)
			_myEditorLocal = new VectorInfo(this, getName());

		return _myEditorLocal;
	}



	public class VectorInfo extends Editable.EditorType
	{

		public VectorInfo(LineShape data, String theName)
		{
			super(data, theName, "");
		}

		public PropertyDescriptor[] getPropertyDescriptors()
		{
			try
			{
				PropertyDescriptor[] res =
				{
						prop("Line_Start", "the start of the line", SPATIAL),
						prop("Bearing", "the bearing for the vector", SPATIAL),
						prop("Distance", "the size of the vector", SPATIAL),
						prop("ArrowAtEnd",
								"whether to show an arrow at one end of the line", FORMAT), };

				return res;

			}
			catch (IntrospectionException e)
			{
				return super.getPropertyDescriptors();
			}
		}
	}
}
