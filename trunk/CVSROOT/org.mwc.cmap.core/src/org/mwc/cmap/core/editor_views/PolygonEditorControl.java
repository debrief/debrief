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
	private Composite btnHolder;
	private Button addBtn;
	private Button deleteBtn;
	private Label tmpLabel;
	private Composite pointEditorHolder;
	private ListViewer pointList;
	private Button downBtn;
	private Button moveUp;
	private Label instructions;

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
				topHolder.setLayout(topHolderLayout);
				GridData topHolderLData = new GridData();
				topHolderLData.horizontalAlignment = GridData.FILL;
				topHolderLData.verticalAlignment = GridData.FILL;
				topHolderLData.grabExcessHorizontalSpace = true;
				topHolder.setLayoutData(topHolderLData);
				{
					instructions = new Label(topHolder, SWT.WRAP | SWT.BORDER);
					instructions.setText("here are the instructions on how to edit a polygon");
					RowData instructionsLData = new RowData();
					instructions.setLayoutData(instructionsLData);
				}
				{
					btnHolder = new Composite(topHolder, SWT.NONE);
					GridLayout btnHolderLayout = new GridLayout();
					btnHolderLayout.numColumns = 2;
					btnHolder.setLayout(btnHolderLayout);
					RowData btnHolderLData = new RowData();
					btnHolder.setLayoutData(btnHolderLData);
					{
						moveUp = new Button(btnHolder, SWT.PUSH | SWT.CENTER);
						GridData moveUpLData = new GridData();
						moveUpLData.verticalAlignment = GridData.FILL;
						moveUpLData.horizontalAlignment = GridData.FILL;
						moveUp.setLayoutData(moveUpLData);
						moveUp.setText("Up");
					}
					{
						addBtn = new Button(btnHolder, SWT.PUSH | SWT.CENTER);
						GridData addBtnLData = new GridData();
						addBtnLData.verticalAlignment = GridData.FILL;
						addBtnLData.horizontalAlignment = GridData.FILL;
						addBtn.setLayoutData(addBtnLData);
						addBtn.setText("Add");
					}
					{
						deleteBtn = new Button(btnHolder, SWT.PUSH | SWT.CENTER);
						GridData deleteBtnLData = new GridData();
						deleteBtnLData.horizontalAlignment = GridData.FILL;
						deleteBtnLData.verticalAlignment = GridData.FILL;
						deleteBtn.setLayoutData(deleteBtnLData);
						deleteBtn.setText("Delete");
					}
					{
						downBtn = new Button(btnHolder, SWT.PUSH | SWT.CENTER);
						GridData downBtnLData = new GridData();
						downBtnLData.verticalAlignment = GridData.FILL;
						downBtnLData.horizontalAlignment = GridData.FILL;
						downBtn.setLayoutData(downBtnLData);
						downBtn.setText("Down");
					}
				}
			}
			{
				pointList = new ListViewer(this, SWT.NONE);
				GridData pointListLData = new GridData();
				pointListLData.horizontalAlignment = GridData.FILL;
				pointListLData.verticalAlignment = GridData.FILL;
				pointListLData.verticalSpan = 2;
				pointListLData.grabExcessHorizontalSpace = true;
				pointListLData.grabExcessVerticalSpace = true;
				pointList.getControl().setLayoutData(pointListLData);
			}
			{
				pointEditorHolder = new Composite(this, SWT.NONE);
				FillLayout pointEditorHolderLayout = new FillLayout(
					org.eclipse.swt.SWT.HORIZONTAL);
				pointEditorHolder.setLayout(pointEditorHolderLayout);
				GridData pointEditorHolderLData = new GridData();
				pointEditorHolderLData.horizontalAlignment = GridData.FILL;
				pointEditorHolderLData.verticalAlignment = GridData.FILL;
				pointEditorHolder.setLayoutData(pointEditorHolderLData);
				{
					tmpLabel = new Label(pointEditorHolder, SWT.SHADOW_OUT | SWT.BORDER);
					tmpLabel.setText("Point editor goes here");
					tmpLabel.setBounds(10, 209, 91, 28);
					tmpLabel.setAlignment(SWT.CENTER);
				}
			}
			this.layout();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
