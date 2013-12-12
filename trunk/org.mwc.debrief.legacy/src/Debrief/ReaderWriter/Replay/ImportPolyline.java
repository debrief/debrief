package Debrief.ReaderWriter.Replay;

import java.util.Vector;

import junit.framework.TestCase;

import Debrief.Wrappers.ShapeWrapper;

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
	
	public static class TestImportPolyline extends TestCase {
		
		public void testExport()
		{
			final String line = ";POLYLINE: @@ 49 43 49.08 N 004 10 11.60 E 49 38 25.80 N 004 23 58.02 E label";
			final ImportPolyline ip = new ImportPolyline();
			final ShapeWrapper sw = (ShapeWrapper) ip.readThisLine(line);
			assertEquals(line, ip.exportThis(sw));
		}
	}

}
