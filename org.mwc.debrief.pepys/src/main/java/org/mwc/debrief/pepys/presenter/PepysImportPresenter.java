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

package org.mwc.debrief.pepys.presenter;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.mwc.debrief.pepys.model.AbstractConfiguration;
import org.mwc.debrief.pepys.model.ModelConfiguration;
import org.mwc.debrief.pepys.model.TypeDomain;
import org.mwc.debrief.pepys.model.bean.Comments;
import org.mwc.debrief.pepys.model.bean.Contacts;
import org.mwc.debrief.pepys.model.bean.FilterableBean;
import org.mwc.debrief.pepys.model.bean.States;
import org.mwc.debrief.pepys.view.PepysImportView;

import MWC.GenericData.HiResDate;
import MWC.GenericData.TimePeriod;

public class PepysImportPresenter {

	public PepysImportPresenter(final Shell parent) {
		AbstractConfiguration model = new ModelConfiguration();
		
		model.addDatafileTypeFilter(new TypeDomain(States.class, "States", true));
		model.addDatafileTypeFilter(new TypeDomain(Contacts.class, "Contacts", true));
		model.addDatafileTypeFilter(new TypeDomain(Comments.class, "Comments", true));
		PepysImportView view = new PepysImportView(model, parent);

		addDataTypeFilters(model, view);
		addDatabindings(model, view);
	}

	private void addDataTypeFilters(final AbstractConfiguration _model, final PepysImportView _view) {
		final Composite composite = _view.getDataTypesComposite();
		composite.setLayout(new FillLayout(SWT.VERTICAL));
		
		for (TypeDomain type : _model.getDatafileTypeFilters()) {
			final Button typeButton = new Button(composite, SWT.CHECK);
			typeButton.setText(type.getName());
			type.removeAllPropertyChangeListeners();
			type.addPropertyChangeListener(new PropertyChangeListener() {
				
				@Override
				public void propertyChange(PropertyChangeEvent evt) {
					typeButton.setSelection(type.isChecked());
				}
			});
			
			typeButton.addSelectionListener(new SelectionListener() {
				
				@Override
				public void widgetSelected(SelectionEvent event) {
					type.setChecked(typeButton.getSelection());
				}
				
				@Override
				public void widgetDefaultSelected(SelectionEvent event) {
					type.setChecked(typeButton.getSelection());
				}
			});
		}
		
	}

	protected void addDatabindings(final AbstractConfiguration model, final PepysImportView view) {

		view.getStartDate().addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent sel) {
				setStartTime(model);
			}

			public void setStartTime(final AbstractConfiguration model) {
				final HiResDate start = new HiResDate(view.getStartDate().getSelection());
				model.setTimePeriod(new TimePeriod.BaseTimePeriod(start, model.getTimePeriod().getEndDTG()));
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent sel) {
				setStartTime(model);
			}
		});
		model.addPropertyChangeListener(new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				view.getStartDate().setSelection(model.getTimePeriod().getStartDTG().getDate());
			}
		});

		view.getTree().setInput(model.getTreeModel());
	}

	public static void main(String[] args) {
		Display display = new Display();
		Shell shell = new Shell(display);

		new PepysImportPresenter(shell);

		shell.pack();
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}
}
