
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

import java.awt.Color;
import java.awt.Font;

import org.w3c.dom.Element;

import MWC.GUI.Defaults;
import MWC.Utilities.ReaderWriter.XML.MWCXMLReader;
import MWC.Utilities.ReaderWriter.XML.PlottableExporter;
import MWC.Utilities.ReaderWriter.XML.Util.ColourHandler;
import MWC.Utilities.ReaderWriter.XML.Util.FontHandler;

abstract public class ScaleHandler extends MWCXMLReader implements PlottableExporter {

	private static final String DISPLAY_UNITS = "DisplayUnits";
	private static final String LOCATION = "Location";
	private static final String SCALE_STEP = "ScaleStep";
	private static final String SCALE_MAX = "ScaleMax";
	private static final String AUTO_MODE = "AutoMode";
	private static final String BACKGROUND = "Background";
	private static final String SEMI_TRANSPARENT = "SemiTransparent";
	private static final String VISIBLE = "Visible";
	private static final String FILL_BACKGROUND = "FillBackground";
	java.awt.Color _theColor;
	boolean _isVisible;
	long _ScaleMax;
	long _ScaleStep;
	boolean _AutoMode;
	String _LabelLocation;
	String _displayUnits;
	private java.awt.Font _myFont = Defaults.getFont();

	private boolean _fillBackground = false;

	private Color _background = Color.white;

	private boolean _semiTransparent = false;

	MWC.GUI.Properties.DiagonalLocationPropertyEditor _dp = new MWC.GUI.Properties.DiagonalLocationPropertyEditor();

	MWC.GUI.Properties.UnitsPropertyEditor _up = new MWC.GUI.Properties.UnitsPropertyEditor();

	public ScaleHandler() {
		// inform our parent what type of class we are
		super("scale");

		addAttributeHandler(new HandleBooleanAttribute(VISIBLE) {
			@Override
			public void setValue(final String name, final boolean value) {
				_isVisible = value;
			}
		});
		addAttributeHandler(new HandleBooleanAttribute(SEMI_TRANSPARENT) {
			@Override
			public void setValue(final String name, final boolean value) {
				_semiTransparent = value;
			}
		});
		addAttributeHandler(new HandleBooleanAttribute(FILL_BACKGROUND) {
			@Override
			public void setValue(final String name, final boolean value) {
				_fillBackground = value;
			}
		});
		addHandler(new ColourHandler(BACKGROUND) {
			@Override
			public void setColour(final Color value) {
				_background = value;
			}
		});
		addHandler(new FontHandler() {
			@Override
			public void setFont(final Font value) {
				_myFont = value;
			}
		});
		addAttributeHandler(new HandleBooleanAttribute(AUTO_MODE) {
			@Override
			public void setValue(final String name, final boolean value) {
				_AutoMode = value;
			}
		});
		addAttributeHandler(new HandleAttribute(SCALE_MAX) {
			@Override
			public void setValue(final String name, final String value) {
				_ScaleMax = Long.valueOf(value).longValue();
			}
		});
		addAttributeHandler(new HandleAttribute(SCALE_STEP) {
			@Override
			public void setValue(final String name, final String value) {
				_ScaleStep = Long.valueOf(value).longValue();
			}
		});
		addAttributeHandler(new HandleAttribute(LOCATION) {
			@Override
			public void setValue(final String name, final String value) {
				_LabelLocation = value;
			}
		});
		addAttributeHandler(new HandleAttribute(DISPLAY_UNITS) {
			@Override
			public void setValue(final String name, final String value) {
				_displayUnits = value;
			}
		});
		addHandler(new ColourHandler() {
			@Override
			public void setColour(final java.awt.Color color) {
				_theColor = color;
			}
		});

	}

	abstract public void addPlottable(MWC.GUI.Plottable plottable);

	@Override
	public void elementClosed() {
		// create a Scale from this data
		final MWC.GUI.Chart.Painters.ScalePainter csp = new MWC.GUI.Chart.Painters.ScalePainter();
		csp.setColor(_theColor);
		csp.setVisible(_isVisible);
		csp.setAutoMode(_AutoMode);
		csp.setScaleMax(new Long(_ScaleMax));
		csp.setScaleStep(new Long(_ScaleStep));
		csp.setFillBackground(_fillBackground);
		csp.setSemiTransparent(_semiTransparent);
		if (_background != null) {
			csp.setBackground(_background);
		}
		if (_myFont != null) {
			csp.setFont(_myFont);
		}
		if (_LabelLocation != null) {
			_dp.setAsText(_LabelLocation);
			csp.setLocation((Integer) _dp.getValue());
		}
		if (_displayUnits != null)
			csp.setDisplayUnits(_displayUnits);

		addPlottable(csp);

		// reset our variables
		_theColor = null;
		_isVisible = false;
		_LabelLocation = null;
		_displayUnits = null;
		_myFont = null;
		_background = null;
		_fillBackground = false;
		_semiTransparent = false;

	}

	@Override
	public void exportThisPlottable(final MWC.GUI.Plottable plottable, final org.w3c.dom.Element parent,
			final org.w3c.dom.Document doc) {

		final MWC.GUI.Chart.Painters.ScalePainter csp = (MWC.GUI.Chart.Painters.ScalePainter) plottable;
		final Element scale = doc.createElement("scale");

		// do the visibility
		scale.setAttribute(VISIBLE, writeThis(csp.getVisible()));
		scale.setAttribute("Name", "World Default");
		scale.setAttribute(SCALE_MAX, writeThis(csp.getScaleMax().longValue()));
		scale.setAttribute(SCALE_STEP, writeThis(csp.getScaleStep().longValue()));
		scale.setAttribute(AUTO_MODE, writeThis(csp.getAutoMode()));
		scale.setAttribute(FILL_BACKGROUND, writeThis(csp.isFillBackground()));
		scale.setAttribute(SEMI_TRANSPARENT, writeThis(csp.isSemiTransparent()));
		FontHandler.exportFont(csp.getFont(), scale, doc);
		ColourHandler.exportColour(csp.getBackground(), scale, doc, BACKGROUND);

		// and the units
		scale.setAttribute(DISPLAY_UNITS, csp.getDisplayUnits());

		// and the scale location
		_dp.setValue(csp.getLocation());
		final String tmp = _dp.getAsAbbreviatedText();
		scale.setAttribute(LOCATION, tmp);

		// do the colour
		ColourHandler.exportColour(csp.getColor(), scale, doc);

		parent.appendChild(scale);
	}

}