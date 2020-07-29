
package Debrief.ReaderWriter.XML.Tactical;

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
import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.TrackWrapper;
import MWC.GUI.Plottable;
import MWC.Utilities.ReaderWriter.XML.PlottableExporter;
import MWC.Utilities.ReaderWriter.XML.Util.ColourHandler;
import MWC.Utilities.ReaderWriter.XML.Util.FontHandler;

public abstract class HeavyWeightTrackHandler extends MWC.Utilities.ReaderWriter.XML.MWCXMLReader
		implements PlottableExporter {

	private static final String NAME = "Name";
	private static final String VISIBLE = "Visible";
	private static final String MY_NAME = "HeavyWeightTrack";
	private static final String COLOR = "Color";
	private static final String SHOW_NAME = "NameVisible";
	private static final String LINE_STYLE = "LineStyle";
	private static final String LINE_THICKNESS = "LineThickness";

	private final ArrayList<FixWrapper> _fixes = new ArrayList<FixWrapper>();

	protected Color _color;
	protected boolean _visible;
	protected String _name;
	private boolean _nameVisible;
	protected int _lineStyle;
	protected Integer _lineWidth;
	protected Font _font;

	protected HeavyWeightTrackHandler() {
		// inform our parent what type of class we are
		super(MY_NAME);

		addHandler(new FixHandler() {
			@Override
			public void addPlottable(final Plottable plottable) {
				_fixes.add((FixWrapper) plottable);
			}
		});
		addAttributeHandler(new HandleIntegerAttribute(LINE_THICKNESS) {
			@Override
			public void setValue(final String name, final int value) {
				_lineWidth = value;
			}
		});

		addHandler(new ColourHandler(COLOR) {
			@Override
			public void setColour(final Color res) {
				_color = res;
			}
		});
		addHandler(new FontHandler() {

			@Override
			public void setFont(final Font res) {
				_font = res;
			}
		});
		addAttributeHandler(new HandleIntegerAttribute(LINE_STYLE) {
			@Override
			public void setValue(final String name, final int value) {
				_lineStyle = value;
			}
		});

		addAttributeHandler(new HandleBooleanAttribute(VISIBLE) {
			@Override
			public void setValue(final String name, final boolean val) {
				_visible = val;
			}
		});
		addAttributeHandler(new HandleBooleanAttribute(SHOW_NAME) {
			@Override
			public void setValue(final String name, final boolean val) {
				_nameVisible = val;
			}
		});
		addAttributeHandler(new HandleAttribute(NAME) {
			@Override
			public void setValue(final String name, final String value) {
				_name = value;
			}
		});

	}

	@Override
	public void elementClosed() {
		// ok, generate the object
		final TrackWrapper track = new TrackWrapper();
		track.setName(_name);
		track.setColor(_color);
		track.setLineStyle(_lineStyle);
		track.setVisible(_visible);
		track.setNameVisible(_nameVisible);
		_fixes.forEach(fix->track.add(fix));
		if (_font != null) {
			track.setTrackFont(_font);
		}
		if (_lineWidth != null) {
			track.setLineThickness(_lineWidth);
		}
		_fixes.clear();
		_color = null;
		_font = null;
		_lineWidth = null;

		storeTrack(track);

	}

	@Override
	public void exportThisPlottable(final Plottable plottable, final Element parent, final Document doc) {
		final TrackWrapper track = (TrackWrapper) plottable;

		final Element trk = doc.createElement(MY_NAME);
		trk.setAttribute(NAME, toXML(track.getName()));
		trk.setAttribute(VISIBLE, writeThis(track.getVisible()));
		trk.setAttribute(SHOW_NAME, writeThis(track.getNameVisible()));
		trk.setAttribute(LINE_STYLE, writeThis(track.getLineStyle()));
		trk.setAttribute(LINE_THICKNESS, writeThis(track.getLineThickness()));

		final Color hisColor = track.getCustomColor();
		if (hisColor != null) {
			ColourHandler.exportColour(hisColor, trk, doc, COLOR);
		}

		final Font font = track.getTrackFont();
		if (font != null) {
			FontHandler.exportFont(font, trk, doc);
		}
		FixWrapper[] fixes = track.getFixes();
		for(FixWrapper fix:fixes) {
			FixHandler.exportFix(fix, trk, doc);
		}
		parent.appendChild(trk);
	}

	public abstract void storeTrack(TrackWrapper track);

}