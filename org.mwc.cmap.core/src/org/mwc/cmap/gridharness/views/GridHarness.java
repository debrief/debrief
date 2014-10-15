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
		public void inputChanged(final Viewer v, final Object oldInput, final Object newInput) {
		}

		public void dispose() {
		}

		public Object[] getElements(final Object parent) {
			final Object[] res = new Object[4];
			final PropertyChangeListener pcl = new PropertyChangeListener() {
				public void propertyChange(final PropertyChangeEvent evt) {
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
		public String getColumnText(final Object obj, final int index) {
			return getText(obj);
		}

		public Image getColumnImage(final Object obj, final int index) {
			return getImage(obj);
		}

		public Image getImage(final Object obj) {
			return PlatformUI.getWorkbench().getSharedImages().getImage(
					ISharedImages.IMG_OBJ_ELEMENT);
		}
	}

	static class NameSorter extends ViewerSorter {
	}

	/**
	 * The constructor.
	 */
	public GridHarness() {
	}

	@SuppressWarnings("deprecation")
	public void propertyChanged(final PropertyChangeEvent event) {
		final String txt = new Date().toGMTString() + ":" + event.getNewValue();
		_myList.add(txt);
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	public void createPartControl(final Composite parent) {
		// do the layout
		final Composite holder = new Composite(parent, SWT.NONE);
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
		final IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalPullDown(final IMenuManager manager) {
		manager.add(subtleChange);
		manager.add(new Separator());
		manager.add(clearList);
	}

	private void fillLocalToolBar(final IToolBarManager manager) {
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
		final ISelection current = viewer.getSelection();

		if (current != null) {
			final IStructuredSelection sel = (IStructuredSelection) current;
			final Object selected = sel.getFirstElement();
			if (selected instanceof ObservationList) {
				changeObs((ObservationList) selected);
			} else if (selected instanceof PositionList) {
				changePos((PositionList) selected);
			}
		}

	}

	private void changePos(final PositionList selected) {
		selected.makeSubtleChange();
	}

	private void changeObs(final ObservationList selected) {
		selected.makeSubtleChange();
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		viewer.getControl().setFocus();
	}
}