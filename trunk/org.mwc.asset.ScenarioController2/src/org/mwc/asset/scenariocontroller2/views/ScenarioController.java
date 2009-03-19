package org.mwc.asset.scenariocontroller2.views;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

public class ScenarioController extends ViewPart {
	private Action action1;
	private Action action2;
	private TabFolder _myTabs;
	private TabItem _singleScenTab;
	private TabItem _multiScenTab;

	/**
	 * The constructor.
	 */
	public ScenarioController() {
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	public void createPartControl(Composite parent) {
		// create the two tabs
		_myTabs = new TabFolder(parent, SWT.None);
		createMultipleTabs();

		// populate the single scenario tab
		populateSingleScenario(_myTabs, _singleScenTab);

		makeActions();
		contributeToActionBars();
	}

	private void populateSingleScenario(TabFolder tabs, TabItem myTab) {
		Composite sC = new Composite(tabs, SWT.None);
		myTab.setControl(sC);
		sC.setLayout(new FormLayout());

		// first the labels
		Label scenLbl = new Label(sC, SWT.RIGHT);
		scenLbl.setText("Scenario:");
		FormData fd = new FormData();
		fd.top = new FormAttachment(0, 10);
		fd.left = new FormAttachment(0, 5);
		fd.bottom = new FormAttachment(10, 0);
		fd.right = new FormAttachment(40, 0);
		scenLbl.setLayoutData(fd);

		Label contLbl = new Label(sC, SWT.RIGHT);
		contLbl.setText("Controller:");
		fd = new FormData();
		fd.top = new FormAttachment(scenLbl, 10);
		fd.left = new FormAttachment(0, 5);
		fd.bottom = new FormAttachment(20, 0);
		fd.right = new FormAttachment(40, 0);
		contLbl.setLayoutData(fd);

		// now the text boxes
		Label scenVal = new Label(sC, SWT.BORDER | SWT.SINGLE);
		scenVal.setText("[pending]          ");		
		fd = new FormData();
		fd.top = new FormAttachment(scenLbl, 0, SWT.TOP);
		fd.left = new FormAttachment(scenLbl, 10);
		scenVal.setLayoutData(fd);

		Label contVal = new Label(sC, SWT.BORDER | SWT.SINGLE);
		contVal.setText("[pending]          ");
		fd = new FormData();
		fd.top = new FormAttachment(contLbl, 0, SWT.TOP);
		fd.left = new FormAttachment(contLbl, 10);
		contVal.setLayoutData(fd);

		// now the file selector buttons
		Button scenBtn = new Button(sC, SWT.NONE);
		scenBtn.setText("...");
		fd = new FormData();
		fd.top = new FormAttachment(scenVal, 0, SWT.TOP);
		fd.left = new FormAttachment(scenVal, 10);
		scenBtn.setLayoutData(fd);

		Button contBtn = new Button(sC, SWT.NONE);
		contBtn.setText("...");
		fd = new FormData();
		fd.top = new FormAttachment(contVal, 0, SWT.TOP);
		fd.left = new FormAttachment(contVal, 10);
		contBtn.setLayoutData(fd);
		
		// and the load button
		Button loadBtn = new Button(sC, SWT.RIGHT);
		loadBtn.setText("Load");
		fd = new FormData();
		fd.top = new FormAttachment(contLbl, 10);
//		fd.left = new FormAttachment(0, 5);
//		fd.bottom = new FormAttachment(30, 0);
		fd.right = new FormAttachment(40, 0);
		loadBtn.setLayoutData(fd);
	}

	private void createMultipleTabs() {
		_singleScenTab = new TabItem(_myTabs, SWT.NONE);
		_singleScenTab.setText("Single Scenario");
		_multiScenTab = new TabItem(_myTabs, SWT.NONE);
		_multiScenTab.setText("Multi Scenario");
	}

	private void selectTab(boolean isSingle) {
		if (isSingle)
			_myTabs.setSelection(_singleScenTab);
		else
			_myTabs.setSelection(_multiScenTab);

	}

	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalPullDown(IMenuManager manager) {
		manager.add(action1);
		manager.add(new Separator());
		manager.add(action2);
	}

	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(action1);
		manager.add(action2);
	}

	private void makeActions() {
		action1 = new Action() {
			public void run() {
				showMessage("Action 1 executed");
			}
		};
		action1.setText("Action 1");
		action1.setToolTipText("Action 1 tooltip");
		action1.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages()
				.getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));

		action2 = new Action() {
			public void run() {
				showMessage("Action 2 executed");
			}
		};
		action2.setText("Action 2");
		action2.setToolTipText("Action 2 tooltip");
		action2.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages()
				.getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
	}

	private void showMessage(String message) {
		MessageDialog.openInformation(Display.getCurrent().getActiveShell(),
				"Scenario controller", message);
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		// viewer.getControl().setFocus();
	}
}