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

import ASSET.Scenario.LiveScenario.ISimulationQue;


public class SimControllerUI extends Composite {

	private static final String TITLE_LABEL_TEXT = Messages.SimControllerUI_0;

	private static final String START_LABEL_TEXT = Messages.SimControllerUI_1;

	private Button myStartButton;

	private ISimulationQue mySimulationQue;

	private SimulationTable myTable;

	public SimControllerUI(Composite parent) {
		super(parent, SWT.BORDER);
		
		parent.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_DARK_RED));

		setLayout(new GridLayout(2, false));

		Label titleLabel = new Label(this, SWT.NONE);
		titleLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		titleLabel.setText(TITLE_LABEL_TEXT);
		
		myStartButton = new Button(this, SWT.PUSH);
		myStartButton.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
		myStartButton.setText(START_LABEL_TEXT);
		myStartButton.setEnabled(false);
		myStartButton.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event event) {
				mySimulationQue.startQue();
			}
		});

		myTable = new SimulationTable(this, null);
		myTable.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
	}

	public void setInput(ISimulationQue input) {
		mySimulationQue = input;
		myStartButton.setEnabled(mySimulationQue != null);
		myTable.setInput(mySimulationQue);
	}

	public void setSelectionProvider(ISelectionProvider selectionProvider) {
		myTable.setSelectionProvider(selectionProvider);
	}
}
