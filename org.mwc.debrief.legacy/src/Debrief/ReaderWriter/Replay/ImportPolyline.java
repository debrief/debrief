/*******************************************************************************
 * Debrief - the Open Source Maritime Analysis Application
 * http://debrief.info
 *
 * (C) 2000-2020, Deep Blue C Technology Ltd
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html)
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *******************************************************************************/

package Debrief.ReaderWriter.Replay;

import java.util.Vector;

import MWC.GUI.Shapes.PolygonShape;
import MWC.GUI.Shapes.PolygonShape.PolygonNode;

final class ImportPolyline extends ImportPolygon {

	/**
	 * the type for this string
	 */
	private final String _myType = ";POLYLINE:";

	@Override
	protected boolean canExport(final PolygonShape ps) {
		return !ps.getClosed();
	}

	@Override
	protected PolygonShape createShape(final Vector<PolygonNode> nodes) {
		final PolygonShape ps = new PolygonShape(nodes);
		ps.setClosed(false);
		// these polylines frequently have lots and lots of points. Better switch
		// labels off.
		ps.setShowNodeLabels(false);
		return ps;
	}

	@Override
	public String getYourType() {
		return _myType;
	}

}
