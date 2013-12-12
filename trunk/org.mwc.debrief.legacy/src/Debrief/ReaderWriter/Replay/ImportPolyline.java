package Debrief.ReaderWriter.Replay;

import java.util.Vector;

import MWC.GUI.Shapes.PolygonShape;
import MWC.GUI.Shapes.PolygonShape.PolygonNode;

final class ImportPolyline extends ImportPolygon
{
	/**
	 * the type for this string
	 */
	private final String _myType = ";POLYLINE:";
	
	@Override
	public String getYourType()
	{
		return _myType;
	}
	
	@Override
	protected PolygonShape createShape(final Vector<PolygonNode> nodes)
	{
		final PolygonShape ps = new PolygonShape(nodes);
		ps.setClosed(false);
		return ps;
	}
	
	@Override
	protected boolean canExport(final PolygonShape ps)
	{
		return !ps.getClosed();
	}
}
