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
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.mwc.asset.SimulationController;

import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.mwc.asset.SimulationController.table.SimulationTable;

import ASSET.GUI.CommandLine.NewScenarioListener;
import ASSET.Scenario.LiveScenario.ISimulationQue;

public class SimControllerUI extends Composite
{

	private static final String TITLE_LABEL_TEXT = Messages.SimControllerUI_0;

	private static final String START_LABEL_TEXT = Messages.SimControllerUI_1;

	private final Button myStartButton;

	private ISimulationQue mySimulationQue;

	private final SimulationTable myTable;

	public SimControllerUI(final Composite parent, final NewScenarioListener listener)
	{
		super(parent, SWT.BORDER);

		parent.setBackground(Display.getDefault()
				.getSystemColor(SWT.COLOR_DARK_RED));

		setLayout(new GridLayout(2, false));

		final Label titleLabel = new Label(this, SWT.NONE);
		titleLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		titleLabel.setText(TITLE_LABEL_TEXT);

		myStartButton = new Button(this, SWT.PUSH);
		myStartButton.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
				false));
		myStartButton.setText(START_LABEL_TEXT);
		myStartButton.setEnabled(false);
		myStartButton.addListener(SWT.Selection, new Listener()
		{

			public void handleEvent(final Event event)
			{
				mySimulationQue.startQue(listener);
			}
		});

		myTable = new SimulationTable(this, null);
		myTable.getControl().setLayoutData(
				new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
	}

	public void setInput(final ISimulationQue input)
	{
		mySimulationQue = input;
		myStartButton.setEnabled(mySimulationQue != null);
		myTable.setInput(mySimulationQue);
	}

	public void setSelectionProvider(final ISelectionProvider selectionProvider)
	{
		myTable.setSelectionProvider(selectionProvider);
	}
}
