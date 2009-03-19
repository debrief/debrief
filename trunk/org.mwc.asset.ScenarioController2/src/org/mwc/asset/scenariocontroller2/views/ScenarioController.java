package org.mwc.asset.scenariocontroller2.views;

import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

public class ScenarioController extends ViewPart {
	private Action action1;
	private Action action2;
	private DropTarget target;
	private UISkeleton _myUI;

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
		// create our UI
		 _myUI = new UISkeleton(parent, SWT.FILL);

		// let us accept dropped files
		configureFileDropSupport(_myUI);
		
		// fille in the menu bar(s)
		makeActions();
		contributeToActionBars();
	}

	/**
	 * sort out the file-drop target
	 */
	private void configureFileDropSupport(Control _pusher) {
		int dropOperation = DND.DROP_COPY;
		Transfer[] dropTypes = { FileTransfer.getInstance() };

		target = new DropTarget(_pusher, dropOperation);
		target.setTransfer(dropTypes);
		target.addDropListener(new DropTargetListener() {
			public void dragEnter(DropTargetEvent event) {
				if (FileTransfer.getInstance().isSupportedType(
						event.currentDataType)) {
					if (event.detail != DND.DROP_COPY) {
						event.detail = DND.DROP_COPY;
					}
				}
			}

			public void dragLeave(DropTargetEvent event) {
			}

			public void dragOperationChanged(DropTargetEvent event) {
			}

			public void dragOver(DropTargetEvent event) {
			}

			public void dropAccept(DropTargetEvent event) {
			}

			public void drop(DropTargetEvent event) {
				String[] fileNames = null;
				if (FileTransfer.getInstance().isSupportedType(
						event.currentDataType)) {
					fileNames = (String[]) event.data;
				}
				if (fileNames != null) {
					filesDropped(fileNames);
				}
			}

		});

	}

	protected void filesDropped(String[] fileNames) {
		// ok, loop through the files
		for(int i=0;i<fileNames.length;i++)
		{
			String thisName = fileNames[i];
			
			// ok, examine this file
			String firstNode = getFirstNodeName(thisName);
			
			if(firstNode != null)
			{
				if(firstNode.equals("Scenario"))
				{					
					_myUI.getScenarioVal().setText(thisName);
				}
				else if(firstNode.equals("ScenarioController"))
				{
					_myUI.getControlVal().setText(thisName);
				}
			}
		}
	}

	private String getFirstNodeName(String filename)
	{
		String res = null;
		Document thisD = loadFileIntoDom(filename);
		
		Node thisN = thisD.getFirstChild();
		return res;
	}
	
	private Document loadFileIntoDom(String filename)
	{
		Document res = null;

		DocumentBuilder db;
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			db = dbf.newDocumentBuilder();
			res = db.parse(filename);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return res;
	}
	
	private void selectTab(boolean isSingle) {
		if (isSingle)
			_myUI.getScenarioTabs().setSelection(0);
		else
			_myUI.getScenarioTabs().setSelection(1);

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