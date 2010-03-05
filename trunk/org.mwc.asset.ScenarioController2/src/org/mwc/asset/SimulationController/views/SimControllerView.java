package org.mwc.asset.SimulationController.views;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.mwc.asset.SimulationController.SimControllerUI;


import ASSET.Scenario.LiveScenario.ISimulationQue;


public class SimControllerView extends ViewPart {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "com.pml.simcontroller.views.SimControllerView"; //$NON-NLS-1$

	private SimControllerUI myUI;

	/**
	 * The constructor.
	 */
	public SimControllerView() {
		System.err.println("in sim controller view constructor");
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	public void createPartControl(Composite parent) {
		myUI = new SimControllerUI(parent, null);
		SelectionProvider selectionProvider = new SelectionProvider();
		getSite().setSelectionProvider(selectionProvider);
		myUI.setSelectionProvider(selectionProvider);
	}

	@Override
	public void setFocus() {
		myUI.setFocus();
	}

	public void setData(ISimulationQue data) {
		myUI.setInput(data);
	}

}