
package Debrief.ReaderWriter.XML.Shapes;

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

import MWC.Utilities.ReaderWriter.XML.PlottableExporter;
import MWC.Utilities.ReaderWriter.XML.Util.LocationHandler;

abstract public class RectangleHandler extends ShapeHandler implements PlottableExporter {

	private static final String TYPE = "rectangle";
	private static final String TL = "tl";
	private static final String BR = "br";
	private static final String FILLED = "Filled";
	private static final String SEMI_TRANSPARENT = "SemiTransparent";

	MWC.GenericData.WorldLocation _start;
	MWC.GenericData.WorldLocation _end;
	Boolean _filled;
	Boolean _semiTransparent;

	public RectangleHandler() {
		this(TYPE);
	}

	public RectangleHandler(final String type) {
		// inform our parent what type of class we are
		super(type);

		addHandler(new LocationHandler(TL) {
			@Override
			public void setLocation(final MWC.GenericData.WorldLocation res) {
				_start = res;
			}
		});
		addHandler(new LocationHandler(BR) {
			@Override
			public void setLocation(final MWC.GenericData.WorldLocation res) {
				_end = res;
			}
		});

		addAttributeHandler(new HandleBooleanAttribute(FILLED) {
			@Override
			public void setValue(final String name, final boolean value) {
				_filled = new Boolean(value);
			}
		});

		addAttributeHandler(new HandleBooleanAttribute(SEMI_TRANSPARENT) {
			@Override
			public void setValue(final String name, final boolean value) {
				_semiTransparent = new Boolean(value);
			}
		});

	}

	@Override
	public final void exportThisPlottable(final MWC.GUI.Plottable plottable, final org.w3c.dom.Element parent,
			final org.w3c.dom.Document doc) {
		// output the shape related stuff first
		final org.w3c.dom.Element ePlottable = doc.createElement(_myType);

		super.exportThisPlottable(plottable, ePlottable, doc);

		// now our circle related stuff

		// get the circle
		final Debrief.Wrappers.ShapeWrapper sw = (Debrief.Wrappers.ShapeWrapper) plottable;
		final MWC.GUI.Shapes.PlainShape ps = sw.getShape();
		if (ps instanceof MWC.GUI.Shapes.RectangleShape) {
			// export the attributes
			final MWC.GUI.Shapes.RectangleShape cs = (MWC.GUI.Shapes.RectangleShape) ps;
			MWC.Utilities.ReaderWriter.XML.Util.LocationHandler.exportLocation(cs.getCorner_TopLeft(), TL, ePlottable,
					doc);
			MWC.Utilities.ReaderWriter.XML.Util.LocationHandler.exportLocation(cs.getCornerBottomRight(), BR,
					ePlottable, doc);
			ePlottable.setAttribute(FILLED, writeThis(cs.getFilled()));
			ePlottable.setAttribute(SEMI_TRANSPARENT, writeThis(cs.getSemiTransparent()));
		} else {
			throw new java.lang.RuntimeException("wrong shape passed to rectangle exporter");
		}

		// add ourselves to the output
		parent.appendChild(ePlottable);
	}

	@Override
	public final MWC.GUI.Shapes.PlainShape getShape() {
		final MWC.GUI.Shapes.RectangleShape ls = new MWC.GUI.Shapes.RectangleShape(_start, _end);
		if (_filled != null)
			ls.setFilled(_filled.booleanValue());
		if (_semiTransparent != null)
			ls.setSemiTransparent(_semiTransparent.booleanValue());
		return ls;
	}

}