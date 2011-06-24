package org.mwc.cmap.core.editor_views;

import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;
import org.mwc.cmap.core.CorePlugin;

public abstract class PolygonEditorControl extends
		org.eclipse.swt.widgets.Composite implements SelectionListener
{
	private Composite topHolder;
	private Composite btnHolder;
	public ListViewer pointList2;
	public Label editorPanel;
	public Button newBtn;
	public Button delBtn;
	public Button pasteBtn;
	public Button upBtn;
	public Label helpLbl;

	// /**
	// * Auto-generated main method to display this
	// * org.eclipse.swt.widgets.Composite inside a new Shell.
	// */
	// public static void main(String[] args) {
	// showGUI();
	// }

	// /**
	// * Auto-generated method to display this
	// * org.eclipse.swt.widgets.Composite inside a new Shell.
	// */
	// public static void showGUI() {
	// Display display = Display.getDefault();
	// Shell shell = new Shell(display);
	// PolygonEditorControl inst = new PolygonEditorControl(shell, SWT.NULL);
	// Point size = inst.getSize();
	// shell.setLayout(new FillLayout());
	// shell.layout();
	// if(size.x == 0 && size.y == 0) {
	// inst.pack();
	// shell.pack();
	// } else {
	// Rectangle shellBounds = shell.computeTrim(0, 0, size.x, size.y);
	// shell.setSize(shellBounds.width, shellBounds.height);
	// }
	// shell.open();
	// while (!shell.isDisposed()) {
	// if (!display.readAndDispatch())
	// display.sleep();
	// }
	// }

	public PolygonEditorControl(org.eclipse.swt.widgets.Composite parent,
			int style)
	{
		super(parent, style);
		initGUI();
		{
		}
		{
			// ok, sort out the images
			pasteBtn.setImage(CorePlugin.getImageFromRegistry("paste.png"));
			upBtn.setImage(CorePlugin.getImageFromRegistry("Up.gif"));
			newBtn.setImage(CorePlugin.getImageFromRegistry("NewPin.gif"));
			delBtn.setImage(CorePlugin.getImageFromRegistry("DeletePin.gif"));
		}
	}

	private void initGUI()
	{
		try
		{
			this.setLayout(new GridLayout());
			{
				topHolder = new Composite(this, SWT.NONE);
				FillLayout topHolderLayout = new FillLayout(
						org.eclipse.swt.SWT.HORIZONTAL);
				GridData topHolderLData = new GridData();
				topHolderLData.horizontalAlignment = GridData.FILL;
				topHolderLData.grabExcessHorizontalSpace = true;
				topHolder.setLayoutData(topHolderLData);
				topHolder.setLayout(topHolderLayout);
				{
					helpLbl = new Label(topHolder, SWT.WRAP);

					helpLbl.setText("helpTxt");
				}
				{
					btnHolder = new Composite(topHolder, SWT.NONE);
					GridLayout btnHolderLayout = new GridLayout();
					btnHolderLayout.makeColumnsEqualWidth = true;
					btnHolderLayout.numColumns = 2;
					btnHolder.setLayout(btnHolderLayout);
					{
						upBtn = new Button(btnHolder, SWT.PUSH | SWT.CENTER);
						GridData upBtnLData = new GridData();
						upBtnLData.horizontalAlignment = GridData.FILL;
						upBtnLData.grabExcessHorizontalSpace = true;
						upBtn.setLayoutData(upBtnLData);
						upBtn.setText("Up");
						upBtn.setToolTipText("Move point up order");
						upBtn.addSelectionListener(this);
					}
					{
						pasteBtn = new Button(btnHolder, SWT.PUSH | SWT.CENTER);
						GridData downBtnLData = new GridData();
						downBtnLData.horizontalAlignment = GridData.FILL;
						downBtnLData.grabExcessHorizontalSpace = true;
						pasteBtn.setLayoutData(downBtnLData);
						pasteBtn.setText("Paste");
						pasteBtn.setToolTipText("Paste location from clipboard");
						pasteBtn.addSelectionListener(this);
					}
					{
						newBtn = new Button(btnHolder, SWT.PUSH | SWT.CENTER);
						GridData newBtnLData = new GridData();
						newBtnLData.horizontalAlignment = GridData.FILL;
						newBtnLData.grabExcessHorizontalSpace = true;
						newBtn.setLayoutData(newBtnLData);
						newBtn.setText("New");
						newBtn.setToolTipText("Add new point");
						newBtn.addSelectionListener(this);
					}
					{
						delBtn = new Button(btnHolder, SWT.PUSH | SWT.CENTER);
						GridData DelBtnLData = new GridData();
						DelBtnLData.horizontalAlignment = GridData.FILL;
						DelBtnLData.grabExcessHorizontalSpace = true;
						delBtn.setLayoutData(DelBtnLData);
						delBtn.setText("Delete");
						delBtn.setToolTipText("Delete current point");
						delBtn.addSelectionListener(this);
					}
				}
			}
			{
				GridData pointList2LData = new GridData();
				pointList2LData.grabExcessHorizontalSpace = true;
				pointList2LData.horizontalAlignment = GridData.FILL;
				pointList2LData.verticalAlignment = GridData.FILL;
				pointList2LData.grabExcessVerticalSpace = true;
				pointList2 = new ListViewer(this, SWT.SINGLE);
				pointList2.getControl().setLayoutData(pointList2LData);
			}
			{
				editorPanel = new Label(this, SWT.NONE);
				GridData editorPanelLData = new GridData();
				editorPanelLData.horizontalAlignment = GridData.FILL;
				editorPanelLData.grabExcessHorizontalSpace = true;
				editorPanelLData.verticalAlignment = GridData.END;
				editorPanel.setLayoutData(editorPanelLData);
				editorPanel.setText("here goes the point editor details");
			}
			this.layout();
		}
		catch (Exception e)
		{
			CorePlugin.logError(Status.ERROR,
					"Problem layout out Polygon editor gui", e);

		}
	}

}
