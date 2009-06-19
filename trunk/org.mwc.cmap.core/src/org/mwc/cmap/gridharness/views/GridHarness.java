package org.mwc.cmap.gridharness.views;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Date;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.List;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.mwc.cmap.gridharness.data.samples.ObservationList;
import org.mwc.cmap.gridharness.data.samples.PositionList;


/**
 * This sample class demonstrates how to plug-in a new workbench view. The view
 * shows data obtained from the model. The sample creates a dummy model on the
 * fly, but a real implementation would connect to the model available either in
 * this or another plug-in (e.g. the workspace). The view is connected to the
 * model using a content provider.
 * <p>
 * The view uses a label provider to define how model objects should be
 * presented in the view. Each view can present the same model objects using
 * different labels and icons, if needed. Alternatively, a single label provider
 * can be shared between views in order to ensure that objects of the same type
 * are presented in the same way everywhere.
 * <p>
 */

public class GridHarness extends ViewPart {
	private TableViewer viewer;
	private Action subtleChange;
	private Action clearList;
	private List _myList;

	private/*
			 * The content provider class is responsible for providing objects
			 * to the view. It can wrap existing objects in adapters or simply
			 * return objects as-is. These objects may be sensitive to the
			 * current input of the view, or ignore it and always show the same
			 * content (like Task List, for example).
			 */
	class ViewContentProvider implements IStructuredContentProvider {
		public void inputChanged(Viewer v, Object oldInput, Object newInput) {
		}

		public void dispose() {
		}

		public Object[] getElements(Object parent) {
			Object[] res = new Object[4];
			PropertyChangeListener pcl = new PropertyChangeListener() {
				public void propertyChange(PropertyChangeEvent evt) {
					propertyChanged(evt);
				}
			};
			res[0] = PositionList.getShortSample(pcl);
			res[1] = PositionList.getLongSample(pcl);
			res[2] = ObservationList.getShortSample(pcl);
			res[3] = ObservationList.getLongSample(pcl);
			return res;
		}
	}

	class ViewLabelProvider extends LabelProvider implements
			ITableLabelProvider {
		public String getColumnText(Object obj, int index) {
			return getText(obj);
		}

		public Image getColumnImage(Object obj, int index) {
			return getImage(obj);
		}

		public Image getImage(Object obj) {
			return PlatformUI.getWorkbench().getSharedImages().getImage(
					ISharedImages.IMG_OBJ_ELEMENT);
		}
	}

	class NameSorter extends ViewerSorter {
	}

	/**
	 * The constructor.
	 */
	public GridHarness() {
	}

	@SuppressWarnings("deprecation")
	public void propertyChanged(PropertyChangeEvent event) {
		String txt = new Date().toGMTString() + ":" + event.getNewValue();
		_myList.add(txt);
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	public void createPartControl(Composite parent) {
		// do the layout
		Composite holder = new Composite(parent, SWT.NONE);
		holder.setLayout(new FillLayout(SWT.HORIZONTAL));

		viewer = new TableViewer(holder, SWT.MULTI | SWT.H_SCROLL
				| SWT.V_SCROLL | SWT.BORDER);
		viewer.setContentProvider(new ViewContentProvider());
		viewer.setLabelProvider(new ViewLabelProvider());
		viewer.setSorter(new NameSorter());
		viewer.setInput(getViewSite());

		_myList = new List(holder, SWT.BORDER);

		// Create the help context id for the viewer's control
		PlatformUI.getWorkbench().getHelpSystem().setHelp(viewer.getControl(),
				"com.pml.GridHarness.viewer");
		makeActions();
		contributeToActionBars();

		// setup as selection provider
		getSite().setSelectionProvider(viewer);
	}

	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalPullDown(IMenuManager manager) {
		manager.add(subtleChange);
		manager.add(new Separator());
		manager.add(clearList);
	}

	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(subtleChange);
		manager.add(clearList);
	}

	private void makeActions() {
		subtleChange = new Action() {
			public void run() {
				makeSubtleChange();
			}
		};
		subtleChange.setText("Subtle Change");
		subtleChange
				.setToolTipText("Make a subtle change to the selected object");
		clearList = new Action() {
			public void run() {
				_myList.removeAll();
			}
		};
		clearList.setText("Clear Log");
		clearList.setToolTipText("Clear the log listing");
	}

	protected void makeSubtleChange() {
		// ok, get the selected item
		ISelection current = viewer.getSelection();

		if (current != null) {
			IStructuredSelection sel = (IStructuredSelection) current;
			Object selected = sel.getFirstElement();
			if (selected instanceof ObservationList) {
				changeObs((ObservationList) selected);
			} else if (selected instanceof PositionList) {
				changePos((PositionList) selected);
			}
		}

	}

	private void changePos(PositionList selected) {
		selected.makeSubtleChange();
	}

	private void changeObs(ObservationList selected) {
		selected.makeSubtleChange();
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		viewer.getControl().setFocus();
	}
}