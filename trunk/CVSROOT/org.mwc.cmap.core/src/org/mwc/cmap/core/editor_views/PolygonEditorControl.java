package org.mwc.cmap.core.editor_views;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;


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
public class PolygonEditorControl extends org.eclipse.swt.widgets.Composite {
	private Composite topHolder;
	private Label introLabel;
	private Composite btnHolder;
	private Button moveDownBtn;
	private Button deleteBtn;
	private List thePoints;
	private Button newPtBtn;
	private Button moveUpBtn;

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
		PolygonEditorControl inst = new PolygonEditorControl(shell, SWT.NULL);
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

	public PolygonEditorControl(org.eclipse.swt.widgets.Composite parent, int style) {
		super(parent, style);
		initGUI();
	}

	private void initGUI() {
		try {
			GridLayout thisLayout = new GridLayout();
			thisLayout.makeColumnsEqualWidth = true;
			this.setLayout(thisLayout);
			this.setSize(266, 245);
			{
				topHolder = new Composite(this, SWT.NONE);
				FillLayout topHolderLayout = new FillLayout(org.eclipse.swt.SWT.HORIZONTAL);
				GridData topHolderLData = new GridData();
				topHolderLData.grabExcessHorizontalSpace = true;
				topHolderLData.horizontalAlignment = GridData.FILL;
				topHolderLData.verticalAlignment = GridData.BEGINNING;
				topHolder.setLayoutData(topHolderLData);
				topHolder.setLayout(topHolderLayout);
				{
					introLabel = new Label(topHolder, SWT.WRAP);
					GridData introLabelLData = new GridData();
					introLabelLData.horizontalAlignment = GridData.FILL;
					introLabelLData.grabExcessHorizontalSpace = true;
					introLabel.setLayoutData(introLabelLData);
					introLabel.setText("how to use this control...");
				}
				{
					btnHolder = new Composite(topHolder, SWT.BORDER);
					GridLayout btnHolderLayout = new GridLayout();
					btnHolderLayout.makeColumnsEqualWidth = true;
					btnHolderLayout.numColumns = 2;
					GridData btnHolderLData = new GridData();
					btnHolderLData.horizontalAlignment = GridData.END;
					btnHolder.setLayoutData(btnHolderLData);
					btnHolder.setLayout(btnHolderLayout);
					{
						moveUpBtn = new Button(btnHolder, SWT.PUSH | SWT.CENTER);
						GridData moveUpBtnLData = new GridData();
						moveUpBtnLData.horizontalAlignment = GridData.FILL;
						moveUpBtnLData.verticalAlignment = GridData.FILL;
						moveUpBtn.setLayoutData(moveUpBtnLData);
						moveUpBtn.setText("Up");
					}
					{
						moveDownBtn = new Button(btnHolder, SWT.PUSH | SWT.CENTER);
						GridData moveDownBtnLData = new GridData();
						moveDownBtnLData.horizontalAlignment = GridData.FILL;
						moveDownBtnLData.verticalAlignment = GridData.FILL;
						moveDownBtn.setLayoutData(moveDownBtnLData);
						moveDownBtn.setText("Down");
					}
					{
						newPtBtn = new Button(btnHolder, SWT.PUSH | SWT.CENTER);
						GridData newPtBtnLData = new GridData();
						newPtBtnLData.horizontalAlignment = GridData.FILL;
						newPtBtnLData.verticalAlignment = GridData.FILL;
						newPtBtn.setLayoutData(newPtBtnLData);
						newPtBtn.setText("New");
					}
					{
						deleteBtn = new Button(btnHolder, SWT.PUSH | SWT.CENTER);
						GridData deleteBtnLData = new GridData();
						deleteBtnLData.horizontalAlignment = GridData.FILL;
						deleteBtnLData.verticalAlignment = GridData.FILL;
						deleteBtn.setLayoutData(deleteBtnLData);
						deleteBtn.setText("Delete");
					}
				}
			}
			{
				GridData thePointsLData = new GridData();
				thePointsLData.horizontalAlignment = GridData.FILL;
				thePointsLData.grabExcessHorizontalSpace = true;
				thePoints = new List(this, SWT.NONE);
				thePoints.setLayoutData(thePointsLData);
			}
			this.layout();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
