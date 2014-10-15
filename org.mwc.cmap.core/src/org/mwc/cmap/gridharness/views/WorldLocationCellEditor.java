/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 */
package org.mwc.cmap.gridharness.views;

import java.text.ParseException;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.nebula.widgets.formattedtext.FormattedText;
import org.eclipse.nebula.widgets.formattedtext.MaskFormatter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.gridharness.data.base60.Sexagesimal;
import org.mwc.cmap.gridharness.data.base60.SexagesimalFormat;

import MWC.GenericData.WorldLocation;


public class WorldLocationCellEditor extends CellEditor implements MultiControlCellEditor {

	private FormattedText myLatitude;

	private FormattedText myLongitude;

	public WorldLocationCellEditor(final Composite parent) {
		super(parent);
	}

	@Override
	protected Control createControl(final Composite parent) {
		final Composite panel = new Composite(parent, SWT.NONE);
		final RowLayout rows = new RowLayout();
		rows.marginLeft = rows.marginRight = 0;
		rows.marginTop = rows.marginBottom = 0;
		rows.fill = false;
		rows.spacing = 0;
		rows.pack = false;
		panel.setLayout(rows);

		myLatitude = new FormattedText(panel, SWT.BORDER);
		myLongitude = new FormattedText(panel, SWT.BORDER);

		myLatitude.setFormatter(new IgnoreTabsMaskFormatter(getFormat().getNebulaPattern(false)));
		myLongitude.setFormatter(new IgnoreTabsMaskFormatter(getFormat().getNebulaPattern(true)));

		final KeyAdapter enterEscapeKeyHandler = new KeyAdapter() {

			@Override
			public void keyPressed(final KeyEvent e) {
				//Text cell editor hooks keyPressed to call keyReleased as well
				WorldLocationCellEditor.this.keyReleaseOccured(e);
			}
		};

		final TraverseListener enterEscapeTraverseHandler = new TraverseListener() {

			public void keyTraversed(final TraverseEvent e) {
				if (e.detail == SWT.TRAVERSE_ESCAPE || e.detail == SWT.TRAVERSE_RETURN) {
					e.doit = false;
					return;
				}
			}
		};
		myLatitude.getControl().addTraverseListener(enterEscapeTraverseHandler);
		myLongitude.getControl().addTraverseListener(enterEscapeTraverseHandler);
		myLatitude.getControl().addKeyListener(enterEscapeKeyHandler);
		myLongitude.getControl().addKeyListener(enterEscapeKeyHandler);

		new MultiControlFocusHandler(myLatitude.getControl(), myLongitude.getControl()) {

			@Override
			protected void focusReallyLost(final FocusEvent e) {
				WorldLocationCellEditor.this.focusLost();
			}
		};

		return panel;
	}

	public Control getLastControl() {
		return myLongitude.getControl();
	}

	@Override
	protected void doSetFocus() {
		myLatitude.getControl().setFocus();
	}

	@Override
	protected Object doGetValue() {
		if (!myLatitude.isValid() || !myLongitude.isValid()) {
			return null;
		}
		final String latitudeString = (String) myLatitude.getFormatter().getDisplayString();
		final String longitudeString = (String) myLongitude.getFormatter().getDisplayString();

		Sexagesimal latitude;
		Sexagesimal longitude;
		try {
			latitude = getFormat().parse(latitudeString, false);
			longitude = getFormat().parse(longitudeString, true);
		} catch (final ParseException e) {
			//thats ok, formatter does not know the hemisphere characters
			return null;
		}

		final WorldLocation location = new WorldLocation(latitude.getCombinedDegrees(), longitude.getCombinedDegrees(),0);
		return location;
	}

	@Override
	protected void doSetValue(final Object value) {
		final WorldLocation location = (WorldLocation) value;
		final Sexagesimal latitude = getFormat().parseDouble(location.getLat());
		final Sexagesimal longitude = getFormat().parseDouble(location.getLong());

		myLatitude.setValue(getFormat().format(latitude, false));
		myLongitude.setValue(getFormat().format(longitude, true));
	}

	private SexagesimalFormat getFormat() {
		//intentionally reevaluated each time
		return CorePlugin.getDefault().getLocationFormat();
	}

	private static class IgnoreTabsMaskFormatter extends MaskFormatter {

		public IgnoreTabsMaskFormatter(final String mask) {
			super(mask);
		}

		@Override
		public void verifyText(final VerifyEvent e) {
			if (ignore) {
				return;
			}
			if (e.keyCode == SWT.TAB) {
				e.doit = false;
				return;
			}
			super.verifyText(e);
		}
	}
}
