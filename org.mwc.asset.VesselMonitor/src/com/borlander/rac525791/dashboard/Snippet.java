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

package com.borlander.rac525791.dashboard;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Slider;
import org.eclipse.swt.widgets.Text;

import com.borlander.rac525791.dashboard.data.DashboardDataModel;

public class Snippet extends Composite {

	private static class CheckBoxListener implements SelectionListener {
		private final DataChange myChange;

		public CheckBoxListener(final DataChange change) {
			myChange = change;
		}

		@Override
		public void widgetDefaultSelected(final SelectionEvent e) {
			widgetSelected(e);
		}

		@Override
		public void widgetSelected(final SelectionEvent e) {
			final Button widget = (Button) e.widget;
			myChange.apply(widget.getSelection());
		}

	}

	private static class ComboListener implements SelectionListener {
		private final DataChange myChange;

		public ComboListener(final DataChange change) {
			myChange = change;
		}

		@Override
		public void widgetDefaultSelected(final SelectionEvent e) {
			widgetSelected(e);
		}

		@Override
		public void widgetSelected(final SelectionEvent e) {
			final Combo widget = (Combo) e.widget;
			myChange.apply(widget.getItem(widget.getSelectionIndex()));
		}

	}

	private static abstract class DataChange {
		private final DashboardDataModel myModel;

		public DataChange(final DashboardDataModel model) {
			myModel = model;
		}

		public void apply(final boolean value) {
			//
		}

		public void apply(final int value) {
			//
		}

		public void apply(final String value) {
			//
		}

		protected DashboardDataModel getDataModel() {
			return myModel;
		}
	}

	private static class SliderListener implements SelectionListener {
		private final DataChange myChange;

		public SliderListener(final DataChange change) {
			myChange = change;
		}

		@Override
		public void widgetDefaultSelected(final SelectionEvent e) {
			throw new UnsupportedOperationException("Slider should not call this");
		}

		@Override
		public void widgetSelected(final SelectionEvent e) {
			final Slider widget = (Slider) e.widget;
			myChange.apply(widget.getSelection());
		}

	}

	private static class TextListener implements ModifyListener {
		private final DataChange myChange;

		public TextListener(final DataChange change) {
			myChange = change;
		}

		@Override
		public void modifyText(final ModifyEvent e) {
			final Text widget = (Text) e.widget;
			String text = widget.getText();
			if (text == null) {
				text = "";
			}
			myChange.apply(text);
		}
	}

	private static void decorateShell(final Shell shell) {
		shell.setText("Vessel Dashboard Demo");
		shell.setLayout(new FillLayout(SWT.VERTICAL));
		new Snippet(shell);
		shell.setSize(600, 800);
	}

	public static void main(final String[] args) {
		final Display display = new Display();
		final Shell shell = new Shell(display);
		decorateShell(shell);
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}

	public Snippet(final Composite parent) {
		super(parent, SWT.NONE);
		setLayout(new GridLayout(2, false));

		final Dashboard dashboard = createDashboardInSash();
		final DashboardDataModel dataModel = dashboard.getDataModel();

		createLabeledText("Vessel Name:", "HMS \"Victory\" -- one of the best ships ever", new DataChange(dataModel) {
			@Override
			public void apply(final String value) {
				getDataModel().setVesselName(value);
			}
		});

		createLabeledText("Vessel Status:", "Battle of Trafalgar (Do you know who will win?)",
				new DataChange(dataModel) {
					@Override
					public void apply(final String value) {
						getDataModel().setVesselStatus(value);
					}
				});

		createLabeledSlider("Actual Course:", 0, 360, 90, new DataChange(dataModel) {
			@Override
			public void apply(final int value) {
				getDataModel().setActualDirection(value);
			}
		});

		createLabeledSlider("Demanded Course:", 0, 360, 135, new DataChange(dataModel) {
			@Override
			public void apply(final int value) {
				getDataModel().setDemandedDirection(value);
			}
		});

		createLabeledCheckBox("Ignore Demanded Course:", new DataChange(dataModel) {
			@Override
			public void apply(final boolean value) {
				getDataModel().setIgnoreDemandedDirection(value);
			}
		});

		createLabeledSlider("Allowed Course Threshold:", 0, 10, 5, new DataChange(dataModel) {
			@Override
			public void apply(final int value) {
				getDataModel().setDirectionThreshold(value);
			}
		});

		createLabeledSlider("Actual Speed:", 0, 1000, 350, new DataChange(dataModel) {
			@Override
			public void apply(final int value) {
				getDataModel().setActualSpeed(value);
			}
		});

		createLabeledSlider("Demanded Speed:", 0, 1000, 350, new DataChange(dataModel) {
			@Override
			public void apply(final int value) {
				getDataModel().setDemandedSpeed(value);
			}
		});

		createLabeledCheckBox("Ignore Demanded Speed:", new DataChange(dataModel) {
			@Override
			public void apply(final boolean value) {
				getDataModel().setIgnoreDemandedSpeed(value);
			}
		});

		createLabeledSlider("Allowed Speed Threshold:", 0, 100, 20, new DataChange(dataModel) {
			@Override
			public void apply(final int value) {
				getDataModel().setSpeedThreshold(value);
			}
		});

		createLabeledSlider("Actual Depth:", 0, 1000, 700, new DataChange(dataModel) {
			@Override
			public void apply(final int value) {
				getDataModel().setActualDepth(value);
			}
		});

		createLabeledSlider("Demanded Depth:", 0, 1000, 900, new DataChange(dataModel) {
			@Override
			public void apply(final int value) {
				getDataModel().setDemandedDepth(value);
			}
		});

		createLabeledCheckBox("Ignore Demanded Depth:", new DataChange(dataModel) {
			@Override
			public void apply(final boolean value) {
				getDataModel().setIgnoreDemandedDepth(value);
			}
		});

		createLabeledSlider("Allowed Depth Threshold:", 0, 100, 80, new DataChange(dataModel) {
			@Override
			public void apply(final int value) {
				getDataModel().setDepthThreshold(value);
			}
		});

		createLabeledCombo("Vertical units:", new String[] { "depth", "alt" }, new DataChange(dataModel) {
			@Override
			public void apply(final String value) {
				getDataModel().setDepthUnits(value);
			}
		});

		createLabeledCombo("Horizontal units:", new String[] { "km/h", "mph", "m/sec" }, new DataChange(dataModel) {
			@Override
			public void apply(final String value) {
				getDataModel().setSpeedUnits(value);
			}
		});

	}

	protected Dashboard createDashboardInGrid() {
		final Group dashboardPanel = new Group(this, SWT.NONE);
		dashboardPanel.setLayout(new GridLayout(1, false));
		dashboardPanel.setText("Sample");
		final Dashboard dashboard = new Dashboard(dashboardPanel);
		final GridData dashGD = new GridData();
		dashGD.horizontalAlignment = GridData.FILL;
		dashGD.verticalAlignment = GridData.FILL;
		dashGD.grabExcessHorizontalSpace = true;
		dashGD.grabExcessVerticalSpace = true;
		dashboard.setLayoutData(dashGD);

		final GridData panelGD = new GridData();
		panelGD.horizontalAlignment = GridData.FILL;
		panelGD.verticalAlignment = GridData.FILL;
		panelGD.horizontalSpan = 2;
		panelGD.grabExcessHorizontalSpace = true;
		panelGD.grabExcessVerticalSpace = true;
		dashboardPanel.setLayoutData(panelGD);

		return dashboard;
	}

	protected Dashboard createDashboardInSash() {
		final Group dashboardPanel = new Group(this, SWT.NONE);
		dashboardPanel.setLayout(new GridLayout(1, true));
		dashboardPanel.setText("Sample");

		final SashForm form = new SashForm(dashboardPanel, SWT.HORIZONTAL | SWT.BORDER);
		final SashForm topForm = new SashForm(form, SWT.VERTICAL | SWT.BORDER);
		new Composite(form, SWT.NONE);
		form.setWeights(new int[] { 9, 1 });

		final Dashboard dashboard = new Dashboard(topForm);
		new Composite(topForm, SWT.NONE);
		topForm.setWeights(new int[] { 9, 1 });

		final GridData panelGD = new GridData();
		panelGD.horizontalAlignment = GridData.FILL;
		panelGD.verticalAlignment = GridData.FILL;
		panelGD.horizontalSpan = 2;
		panelGD.grabExcessHorizontalSpace = true;
		panelGD.grabExcessVerticalSpace = true;
		dashboardPanel.setLayoutData(panelGD);

		final GridData formGD = new GridData();
		formGD.horizontalAlignment = GridData.FILL;
		formGD.verticalAlignment = GridData.FILL;
		formGD.grabExcessHorizontalSpace = true;
		formGD.grabExcessVerticalSpace = true;
		form.setLayoutData(formGD);

		return dashboard;
	}

	private Label createLabel(final String text) {
		final Label label = new Label(this, SWT.NONE);
		label.setText(text);
		label.setLayoutData(leftGD());
		return label;
	}

	private Button createLabeledCheckBox(final String label, final DataChange change) {
		createLabel(label);
		final Button result = new Button(this, SWT.CHECK);
		result.setLayoutData(rightGD());
		result.addSelectionListener(new CheckBoxListener(change));
		return result;
	}

	private Combo createLabeledCombo(final String label, final String[] values, final DataChange change) {
		createLabel(label);

		final Combo result = new Combo(this, SWT.READ_ONLY | SWT.DROP_DOWN | SWT.BORDER);
		result.setLayoutData(rightGD());
		result.setItems(values);

		result.addSelectionListener(new ComboListener(change));
		result.select(0);

		change.apply(values[0]);
		return result;
	}

	private Slider createLabeledSlider(final String label, final int min, final int max, final int initial,
			final DataChange change) {
		createLabel(label);

		final Composite group = new Composite(this, SWT.NONE);
		group.setLayout(new GridLayout(2, false));
		group.setLayoutData(rightGD());

		final Slider slider = new Slider(group, SWT.HORIZONTAL);
		final GridData sliderGD = new GridData();
		sliderGD.grabExcessHorizontalSpace = true;
		sliderGD.horizontalAlignment = GridData.FILL;
		slider.setLayoutData(sliderGD);

		final Label value = new Label(group, SWT.READ_ONLY);
		final GridData valueGD = new GridData();
		valueGD.horizontalAlignment = GridData.FILL;
		valueGD.widthHint = 40;
		value.setLayoutData(valueGD);

		slider.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				value.setText("[" + slider.getSelection() + "]");
			}
		});

		slider.setIncrement(1);

		/*
		 * I do not understand why I need to add 10, but slider with maximum of 360
		 * selects 350 at the rightmost position
		 */
		slider.setMaximum(max + 10);
		slider.setMinimum(min);
		slider.setSelection(initial);
		slider.addSelectionListener(new SliderListener(change));
		change.apply(initial);
		value.setText("[" + slider.getSelection() + "]");
		return slider;
	}

	private Text createLabeledText(final String label, final String text, final DataChange change) {
		createLabel(label);
		final Text result = new Text(this, SWT.SINGLE | SWT.BORDER);
		result.setLayoutData(rightGD());

		result.addModifyListener(new TextListener(change));
		result.setText(text);
		return result;
	}

	private GridData leftGD() {
		final GridData left = new GridData();
		left.horizontalAlignment = GridData.BEGINNING;
		left.verticalAlignment = GridData.CENTER;
		return left;
	}

	private GridData rightGD() {
		final GridData right = new GridData();
		right.horizontalAlignment = GridData.FILL;
		right.verticalAlignment = GridData.CENTER;
		right.grabExcessHorizontalSpace = true;
		return right;
	}

}
