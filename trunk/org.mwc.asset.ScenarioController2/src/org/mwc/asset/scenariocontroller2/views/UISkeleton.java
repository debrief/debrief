package org.mwc.asset.scenariocontroller2.views;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;


/**
* This code was edited or generated using CloudGarden's Jigloo
* SWT/Swing GUI Builder, which is free for non-commercial
* use. If Jigloo is being used commercially (ie, by a corporation,
* company or business for any purpose whatever) then you
* should purchase a license for each developer using Jigloo.
* Please visit www.cloudgarden.com for details.
* Use of Jigloo implies acceptance of these licensing terms.
* A COMMERCIAL LICENSE HAS NOT BEEN PURCHASED FOR
* THIS MACHINE, SO JIGLOO OR THIS CODE CANNOT BE USED
* LEGALLY FOR ANY CORPORATE OR COMMERCIAL PURPOSE.
*/
public class UISkeleton extends org.eclipse.swt.widgets.Composite {
	private Composite filenameHolder;
	private Label scenarioLbl;
	private Label controlLabel;
	private TabItem multipleTab;
	private TabItem singleTab;
	private TabFolder scenarioTabs;
	private Label controlVal;
	private Label scenarioVal;

	/**
	* Auto-generated main method to display this 
	* org.eclipse.swt.widgets.Composite inside a new Shell.
	*/
	public static void main(String[] args) {
		showGUI();
	}
		
	/**
	* Auto-generated method to display this 
	* org.eclipse.swt.widgets.Composite inside a new Shell.
	*/
	public static void showGUI() {
		Display display = Display.getDefault();
		Shell shell = new Shell(display);
		UISkeleton inst = new UISkeleton(shell, SWT.NULL);
		Point size = inst.getSize();
		shell.setLayout(new FillLayout());
		shell.layout();
		if(size.x == 0 && size.y == 0) {
			inst.pack();
			shell.pack();
		} else {
			Rectangle shellBounds = shell.computeTrim(0, 0, size.x, size.y);
			shell.setSize(shellBounds.width, shellBounds.height);
		}
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
	}

	public UISkeleton(org.eclipse.swt.widgets.Composite parent, int style) {
		super(parent, style);
		initGUI();
	}

	private void initGUI() {
		try {
			FormLayout thisLayout = new FormLayout();
			this.setLayout(thisLayout);
			this.setSize(217, 163);
			{
				filenameHolder = new Composite(this, SWT.NONE);
				GridLayout filenameHolderLayout = new GridLayout();
				filenameHolderLayout.numColumns=3;
				filenameHolderLayout.makeColumnsEqualWidth = true;
				FormData filenameHolderLData = new FormData();
				filenameHolderLData.width = 214;
				filenameHolderLData.height = 47;
				filenameHolderLData.left =  new FormAttachment(16, 1000, 0);
				filenameHolderLData.right =  new FormAttachment(1000, 1000, 0);
				filenameHolderLData.top =  new FormAttachment(21, 1000, 0);
				filenameHolder.setLayoutData(filenameHolderLData);
				filenameHolder.setLayout(filenameHolderLayout);				
				{
					scenarioLbl = new Label(filenameHolder, SWT.NONE);
					GridData scenarioLblLData = new GridData();
					scenarioLblLData.horizontalAlignment = GridData.FILL;
					scenarioLbl.setLayoutData(scenarioLblLData);
					scenarioLbl.setText("Scenario");
					scenarioLbl.setAlignment(SWT.RIGHT);
				}
				{
					scenarioVal = new Label(filenameHolder, SWT.NONE);
					GridData scenarioValLData = new GridData();
					scenarioValLData.horizontalSpan = 2;
					scenarioValLData.horizontalAlignment = GridData.FILL;
					scenarioValLData.grabExcessHorizontalSpace = true;
					scenarioVal.setLayoutData(scenarioValLData);
					scenarioVal.setText("[pending]");
				}
				{
					controlLabel = new Label(filenameHolder, SWT.NONE);
					GridData label1LData = new GridData();
					label1LData.horizontalAlignment = GridData.FILL;
					controlLabel.setLayoutData(label1LData);
					controlLabel.setText("Control file");
					controlLabel.setAlignment(SWT.RIGHT);
				}
				{
					controlVal = new Label(filenameHolder, SWT.NONE);
					GridData label2LData = new GridData();
					label2LData.horizontalSpan = 2;
					label2LData.grabExcessHorizontalSpace = true;
					label2LData.horizontalAlignment = GridData.FILL;
					controlVal.setLayoutData(label2LData);
					controlVal.setText("[pending]");
				}
			}
			{
				scenarioTabs = new TabFolder(this, SWT.NONE);
				{
					singleTab = new TabItem(scenarioTabs, SWT.NONE);
					singleTab.setText("Single Scenario");
				}
				{
					multipleTab = new TabItem(scenarioTabs, SWT.NONE);
					multipleTab.setText("Multiple Scenarios");
				}
				FormData scenarioTabsLData = new FormData();
				scenarioTabsLData.width = 204;
				scenarioTabsLData.left =  new FormAttachment(10, 1000, 0);
				scenarioTabsLData.right =  new FormAttachment(990, 1000, 0);
				scenarioTabsLData.bottom =  new FormAttachment(1000, 1000, 0);
				scenarioTabsLData.height = 79;
				scenarioTabsLData.top =  new FormAttachment(0, 1000, 56);
				scenarioTabs.setLayoutData(scenarioTabsLData);
				scenarioTabs.setSelection(0);
			}
			this.layout();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public Label getScenarioVal() {
		return scenarioVal;
	}
	
	public Label getControlVal() {
		return controlVal;
	}
	
	public Composite getControl() {
		return this;
	}
	
	public TabFolder getScenarioTabs() {
		return scenarioTabs;
	}

}
