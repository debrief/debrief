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

package org.mwc.cmap.core.property_support.lengtheditor.views;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;
import org.mwc.cmap.core.property_support.lengtheditor.LengthPropertyDescriptor;

import MWC.GenericData.WorldDistance.ArrayLength;
import MWC.Utilities.ReaderWriter.XML.MWCXMLReader;

public class TestViewPart extends ViewPart {

	private class PropertySource implements IPropertySource {

		@Override
		public Object getEditableValue() {
			return myValue.toString();
		}

		@Override
		public IPropertyDescriptor[] getPropertyDescriptors() {
			return DESCRIPTORS;
		}

		@Override
		public Object getPropertyValue(final Object id) {
			if (PROPERTY_ID.equals(id)) {
				return myValue.toString();
			}
			return null;
		}

		@Override
		public boolean isPropertySet(final Object id) {
			return false;
		}

		@Override
		public void resetPropertyValue(final Object id) {
			setNewValue(new ArrayLength(0));
		}

		@Override
		public void setPropertyValue(final Object id, final Object value) {
			if (PROPERTY_ID.equals(id)) {
				try {
					if (value instanceof String) {
						final double thisD = MWCXMLReader.readThisDouble((String) value);
						setNewValue(new ArrayLength(thisD));
					} else if (value instanceof ArrayLength)
						setNewValue((ArrayLength) value);
				} catch (final Exception e) {
					// nothing
				}
			}

		}
	}

	private static String PROPERTY_ID = "value";//$NON-NLS-1$

	private static final TextPropertyDescriptor LENGTH_PROP_DESC = new LengthPropertyDescriptor(PROPERTY_ID, "Length");

	private static final IPropertyDescriptor[] DESCRIPTORS = { LENGTH_PROP_DESC };

	// init default value
	private ArrayLength myValue = new ArrayLength(0);

	private Label myLabel;

	private PropertySource myPS;

	@Override
	public void createPartControl(final Composite parent) {
		myLabel = new Label(parent, SWT.NONE);
		myLabel.setText(myValue.toString());
	}

	private PropertySource getPropertySource() {
		if (myPS == null) {
			myPS = new PropertySource();
		}
		return myPS;
	}

	@Override
	public void init(final IViewSite site) throws PartInitException {
		super.init(site);
		getViewSite().setSelectionProvider(new ISelectionProvider() {

			@Override
			public void addSelectionChangedListener(final ISelectionChangedListener listener) {
				// nothing
			}

			@Override
			public ISelection getSelection() {
				return new StructuredSelection(getPropertySource());
			}

			@Override
			public void removeSelectionChangedListener(final ISelectionChangedListener listener) {
				// nothing
			}

			@Override
			public void setSelection(final ISelection selection) {
				// nothing
			}
		});
	}

	@Override
	public void setFocus() {
		myLabel.setFocus();
	}

	public void setNewValue(final ArrayLength d) {
		myValue = d;
		myLabel.setText(myValue.toString());
	}
}