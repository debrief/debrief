
package MWC.Utilities.ReaderWriter.XML.Features;

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

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import MWC.GUI.Layers;
import MWC.GUI.Chart.Painters.ETOPOPainter;
import MWC.GUI.Tools.Palette.CreateTOPO;
import MWC.Utilities.ReaderWriter.XML.MWCXMLReader;
import MWC.Utilities.ReaderWriter.XML.Util.ColourHandler;

public class ETOPOHandler extends MWCXMLReader {

	/**
	 * class which contains list of textual representations of scale locations
	 */
	static MWC.GUI.Chart.Painters.ETOPOPainter.KeyLocationPropertyEditor lp = new MWC.GUI.Chart.Painters.ETOPOPainter.KeyLocationPropertyEditor();

	public static void exportThisPlottable(final MWC.GUI.Plottable plottable, final Element parent,
			final Document doc) {

		final MWC.GUI.Chart.Painters.ETOPOPainter csp = (MWC.GUI.Chart.Painters.ETOPOPainter) plottable;
		final Element etopo = doc.createElement("etopo");

		// do the visibility
		etopo.setAttribute("Visible", writeThis(csp.getVisible()));
		etopo.setAttribute("ShowLand", writeThis(csp.getShowLand()));

		lp.setValue(csp.getKeyLocation());
		etopo.setAttribute("ScaleLocation", lp.getAsText());
		etopo.setAttribute("LineThickness", writeThis(csp.getLineThickness()));

		// do the colour
		ColourHandler.exportColour(csp.getColor(), etopo, doc);

		parent.appendChild(etopo);
	}

	java.awt.Color _theColor;
	boolean _isVisible;
	private final Layers _theLayers;
	Integer _scaleLocation;

	boolean _showLand;

	int _lineThickness = 1;

	public ETOPOHandler(final Layers theLayers) {
		// inform our parent what type of class we are
		super("etopo");

		_theLayers = theLayers;

		addAttributeHandler(new HandleBooleanAttribute("Visible") {
			@Override
			public void setValue(final String name, final boolean value) {
				_isVisible = value;
			}
		});
		addAttributeHandler(new HandleBooleanAttribute("ShowLand") {
			@Override
			public void setValue(final String name, final boolean value) {
				_showLand = value;
			}
		});
		addHandler(new ColourHandler() {
			@Override
			public void setColour(final java.awt.Color color) {
				_theColor = color;
			}
		});
		addAttributeHandler(new HandleAttribute("ScaleLocation") {
			@Override
			public void setValue(final String name, final String val) {
				lp.setAsText(val);
				_scaleLocation = (Integer) lp.getValue();
			}
		});
		addAttributeHandler(new HandleIntegerAttribute("LineThickness") {
			@Override
			public void setValue(final String name, final int val) {
				_lineThickness = val;
			}
		});

	}

	@Override
	public void elementClosed() {
		// create a Grid from this data
		final ETOPOPainter painter = new ETOPOPainter(CreateTOPO.getETOPOPath(), _theLayers);
		painter.setColor(_theColor);
		painter.setVisible(_isVisible);
		painter.setShowLand(_showLand);
		painter.setLineThickness(_lineThickness);

		if (_scaleLocation != null)
			painter.setKeyLocation(_scaleLocation);

		_theLayers.addThisLayer(painter);

		// reset our variables
		_theColor = null;
		_isVisible = false;
		_showLand = true;
		_lineThickness = 1;
	}

}