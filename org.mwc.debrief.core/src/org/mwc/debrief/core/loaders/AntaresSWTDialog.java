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
package org.mwc.debrief.core.loaders;

import java.util.Calendar;
import java.util.stream.IntStream;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class AntaresSWTDialog extends TitleAreaDialog {

	private Text trackNameText;

	private Text yearText;
	private Combo monthCombo;
	private int month;

	private int year;
	private String trackName;

	public AntaresSWTDialog(final Shell parentShell) {
		super(parentShell);
	}

	@Override
	public void create() {
		super.create();
		setTitle("Antares Import Dialog");
		setMessage("Please, indicate the name of the track, month and year");
	}

	@Override
	protected Control createDialogArea(final Composite parent) {
		final Composite area = (Composite) super.createDialogArea(parent);
		final Composite container = new Composite(area, SWT.NONE);
		container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		final GridLayout layout = new GridLayout(2, false);
		container.setLayout(layout);

		createTrackName(container);
		createMonth(container);
		createYear(container);

		return area;
	}

	private void createMonth(final Composite container) {
		final Label monthLabel = new Label(container, SWT.NONE);
		monthLabel.setText("Month");

		final GridData dataFirstName = new GridData();
		dataFirstName.grabExcessHorizontalSpace = true;
		dataFirstName.horizontalAlignment = GridData.FILL;

		monthCombo = new Combo(container, SWT.BORDER);
		monthCombo.setItems(IntStream.range(1, 13).mapToObj(i -> String.format("%01d", i)).toArray(String[]::new));
		monthCombo.select(Calendar.getInstance().get(Calendar.MONTH));
		monthCombo.setLayoutData(dataFirstName);
	}

	private void createTrackName(final Composite container) {
		final Label trackNameLabel = new Label(container, SWT.NONE);
		trackNameLabel.setText("Track Name");

		final GridData dataTrackName = new GridData();
		dataTrackName.grabExcessHorizontalSpace = true;
		dataTrackName.horizontalAlignment = GridData.FILL;

		trackNameText = new Text(container, SWT.BORDER);
		trackNameText.setLayoutData(dataTrackName);
	}

	private void createYear(final Composite container) {
		final Label yearLabel = new Label(container, SWT.NONE);
		yearLabel.setText("Year");

		final GridData dataYearName = new GridData();
		dataYearName.grabExcessHorizontalSpace = true;
		dataYearName.horizontalAlignment = GridData.FILL;

		yearText = new Text(container, SWT.BORDER);
		yearText.setText(Calendar.getInstance().get(Calendar.YEAR) + "");
		yearText.setLayoutData(dataYearName);
	}

	public int getMonth() {
		return month;
	}

	public String getTrackName() {
		return trackName;
	}

	public int getYear() {
		return year;
	}

	@Override
	protected boolean isResizable() {
		return true;
	}

	@Override
	protected void okPressed() {
		storeValues();
		super.okPressed();
	}

	private void storeValues() {
		trackName = trackNameText.getText();
		year = Integer.parseInt(yearText.getText());
		month = monthCombo.getSelectionIndex();
	}

}
