/*******************************************************************************
. * Debrief - the Open Source Maritime Analysis Application
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
import java.beans.PropertyVetoException;

import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.mwc.debrief.pepys.model.AbstractConfiguration;
import org.mwc.debrief.pepys.model.ModelConfiguration;
import org.mwc.debrief.pepys.model.TypeDomain;
import org.mwc.debrief.pepys.model.bean.Comment;
import org.mwc.debrief.pepys.model.bean.Contact;
import org.mwc.debrief.pepys.model.bean.State;
import org.mwc.debrief.pepys.model.db.PostgresDatabaseConnection;
import org.mwc.debrief.pepys.model.db.SqliteDatabaseConnection;
import org.mwc.debrief.pepys.model.tree.TreeNode;
import org.mwc.debrief.pepys.view.PepysImportView;

import MWC.GenericData.HiResDate;
import MWC.GenericData.TimePeriod;

public class PepysImportPresenter {

	public static void main(final String[] args) {
		final Display display = new Display();
		final Shell shell = new Shell(display);
		try {
			//new SqliteDatabaseConnection().createInstance();
			new PostgresDatabaseConnection().createInstance();
		} catch (PropertyVetoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		new PepysImportPresenter(shell);

		shell.pack();
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	public PepysImportPresenter(final Shell parent) {
		final AbstractConfiguration model = new ModelConfiguration();

		model.addDatafileTypeFilter(new TypeDomain(State.class, "States", true));
		model.addDatafileTypeFilter(new TypeDomain(Contact.class, "Contacts", true));
		model.addDatafileTypeFilter(new TypeDomain(Comment.class, "Comments", true));
		final PepysImportView view = new PepysImportView(model, parent);

		addDataTypeFilters(model, view);
		addDatabindings(model, view);
	}

	protected void addDatabindings(final AbstractConfiguration model, final PepysImportView view) {

		view.getStartDate().addSelectionListener(new SelectionListener() {

			public void setStartTime(final AbstractConfiguration model) {
				final HiResDate start = new HiResDate(view.getStartDate().getSelection());
				final HiResDate oldStart = model.getTimePeriod().getStartDTG();
				model.setTimePeriod(new TimePeriod.BaseTimePeriod(HiResDate.copyOnlyDate(start, oldStart),
						model.getTimePeriod().getEndDTG()));
			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent sel) {
				setStartTime(model);
			}

			@Override
			public void widgetSelected(final SelectionEvent sel) {
				setStartTime(model);
			}
		});

		view.getEndDate().addSelectionListener(new SelectionListener() {

			public void setEndTime(final AbstractConfiguration model) {
				final HiResDate end = new HiResDate(view.getEndDate().getSelection());
				final HiResDate oldEnd = model.getTimePeriod().getEndDTG();
				model.setTimePeriod(new TimePeriod.BaseTimePeriod(model.getTimePeriod().getStartDTG(),
						HiResDate.copyOnlyDate(end, oldEnd)));
			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent sel) {
				setEndTime(model);
			}

			@Override
			public void widgetSelected(final SelectionEvent sel) {
				setEndTime(model);
			}
		});

		view.getStartTime().addSelectionListener(new SelectionListener() {

			public void setStartTime(final AbstractConfiguration model) {
				final HiResDate start = new HiResDate(view.getStartTime().getSelection());
				final HiResDate oldStart = model.getTimePeriod().getStartDTG();
				model.setTimePeriod(new TimePeriod.BaseTimePeriod(HiResDate.copyOnlyTime(start, oldStart),
						model.getTimePeriod().getEndDTG()));
			}

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				setStartTime(model);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent sel) {
				setStartTime(model);
			}
		});

		view.getEndTime().addSelectionListener(new SelectionListener() {

			public void setEndTime(final AbstractConfiguration model) {
				final HiResDate end = new HiResDate(view.getEndTime().getSelection());
				final HiResDate oldEnd = model.getTimePeriod().getEndDTG();
				model.setTimePeriod(new TimePeriod.BaseTimePeriod(model.getTimePeriod().getStartDTG(),
						HiResDate.copyOnlyTime(end, oldEnd)));
			}

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				setEndTime(model);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				setEndTime(model);
			}
		});

		model.addPropertyChangeListener(new PropertyChangeListener() {

			@Override
			public void propertyChange(final PropertyChangeEvent evt) {
				if (ModelConfiguration.PERIOD_PROPERTY.equals(evt.getPropertyName())
						&& !evt.getOldValue().equals(evt.getNewValue())) {
					view.getStartDate().setSelection(model.getTimePeriod().getStartDTG().getDate());
				}
			}
		});

		model.addPropertyChangeListener(new PropertyChangeListener() {

			@Override
			public void propertyChange(final PropertyChangeEvent evt) {
				if (ModelConfiguration.TREE_MODEL.equals(evt.getPropertyName())) {
					view.getTree().setInput(model.getTreeModel());
				}
			}
		});

		view.getApplyButton().addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(final Event event) {
				switch (event.type) {
				case SWT.Selection:
					try {
						model.apply();
					} catch (final Exception e) {
						e.printStackTrace();
						final MessageBox messageBox = new MessageBox(view.getParent(), SWT.ERROR | SWT.OK);
						messageBox.setMessage(e.toString());
						messageBox.setText("Error retrieving information from Database");
						messageBox.open();
					}
					break;
				}
			}
		});

		view.getImportButton().addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(final Event event) {
				switch (event.type) {
				case SWT.Selection:
					model.doImport();
					break;
				}
			}
		});

		view.getTree().addCheckStateListener(new ICheckStateListener() {

			@Override
			public void checkStateChanged(final CheckStateChangedEvent event) {
				if (event.getChecked()) {
					view.getTree().setSubtreeChecked(event.getElement(), true);
					((TreeNode) event.getElement()).setCheckedRecursive(event.getChecked());
				}
			}
		});

		view.getTree().addCheckStateListener(new ICheckStateListener() {

			@Override
			public void checkStateChanged(final CheckStateChangedEvent event) {
				if (event.getElement() instanceof TreeNode) {
					((TreeNode) event.getElement()).setChecked(event.getChecked());
				}
			}
		});

		view.getTree().setInput(model.getTreeModel());
	}

	private void addDataTypeFilters(final AbstractConfiguration _model, final PepysImportView _view) {
		final Composite composite = _view.getDataTypesComposite();

		for (final TypeDomain type : _model.getDatafileTypeFilters()) {
			final Button typeButton = new Button(composite, SWT.CHECK);
			typeButton.setText(type.getName());
			typeButton.setSelection(type.isChecked());
			type.removeAllPropertyChangeListeners();
			type.addPropertyChangeListener(new PropertyChangeListener() {

				@Override
				public void propertyChange(final PropertyChangeEvent evt) {
					typeButton.setSelection(type.isChecked());
				}
			});

			typeButton.addSelectionListener(new SelectionListener() {

				@Override
				public void widgetDefaultSelected(final SelectionEvent event) {
					type.setChecked(typeButton.getSelection());
				}

				@Override
				public void widgetSelected(final SelectionEvent event) {
					type.setChecked(typeButton.getSelection());
				}
			});
		}
	}
}
