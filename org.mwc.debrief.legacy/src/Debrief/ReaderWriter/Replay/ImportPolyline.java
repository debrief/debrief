/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
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
		// these polylines  frequently have lots and lots of points. Better switch
		// labels off.
		ps.setShowNodeLabels(false);
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
