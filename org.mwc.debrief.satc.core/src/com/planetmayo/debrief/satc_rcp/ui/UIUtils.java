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

package com.planetmayo.debrief.satc_rcp.ui;

import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.conversion.IConverter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;

public class UIUtils {

	public static UpdateValueStrategy converterStrategy(final IConverter converter) {
		if (converter == null) {
			return null;
		}
		final UpdateValueStrategy strategy = new UpdateValueStrategy();
		strategy.setConverter(converter);
		return strategy;
	}

	public static Composite createEmptyComposite(final Composite parent, final Layout layout, final Object layoutData) {
		final Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayoutData(layoutData);
		composite.setLayout(layout);
		return composite;
	}

	public static GridLayout createGridLayoutWithoutMargins(final int numColumns, final boolean makeColumnsEqualWidth) {
		final GridLayout gridLayout = new GridLayout(numColumns, makeColumnsEqualWidth);
		gridLayout.marginHeight = 0;
		gridLayout.marginWidth = 0;
		return gridLayout;
	}

	public static Label createLabel(final Composite parent, final int style, final String text,
			final Object layoutData) {
		final Label label = new Label(parent, style);
		label.setText(text);
		label.setLayoutData(layoutData);
		return label;
	}

	public static Label createLabel(final Composite parent, final String text, final Object layoutData) {
		return createLabel(parent, SWT.NONE, text, layoutData);
	}

	public static Composite createScrolledBody(final ScrolledComposite parent, final int style) {
		return new Composite(parent, style) {

			@Override
			public void layout(final boolean changed, final boolean all) {
				super.layout(changed, all);
				parent.setMinSize(computeSize(SWT.DEFAULT, SWT.DEFAULT));
			}

			@Override
			public void layout(final Control[] changed, final int flags) {
				super.layout(changed, flags);
				parent.setMinSize(computeSize(SWT.DEFAULT, SWT.DEFAULT));
			}
		};
	}

	public static Label createSpacer(final Composite parent, final Object layoutData) {
		return createLabel(parent, "", layoutData);
	}

	public static void setEnabled(final Control ctrl, final boolean enabled) {
		if (ctrl instanceof Composite) {
			final Composite comp = (Composite) ctrl;
			for (final Control c : comp.getChildren())
				setEnabled(c, enabled);
		} else {
			ctrl.setEnabled(enabled);
		}
	}
}
