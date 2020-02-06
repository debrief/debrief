/*******************************************************************************
 * Debrief - the Open Source Maritime Analysis Application
 * http://debrief.info
 *
 * (C) 2000-2020, Deep Blue C Technology Ltd
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html)
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *******************************************************************************/

package com.planetmayo.debrief.satc_rcp.views;

import java.text.SimpleDateFormat;
import java.util.Collection;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.part.ViewPart;

import com.planetmayo.debrief.satc.model.generator.IBoundsManager;
import com.planetmayo.debrief.satc.model.generator.IConstrainSpaceListener;
import com.planetmayo.debrief.satc.model.generator.ISolver;
import com.planetmayo.debrief.satc.model.manager.ISolversManager;
import com.planetmayo.debrief.satc.model.manager.ISolversManagerListener;
import com.planetmayo.debrief.satc.model.states.BaseRange.IncompatibleStateException;
import com.planetmayo.debrief.satc.model.states.BoundedState;
import com.planetmayo.debrief.satc.model.states.CourseRange;
import com.planetmayo.debrief.satc.model.states.LocationRange;
import com.planetmayo.debrief.satc.model.states.SpeedRange;
import com.planetmayo.debrief.satc.util.GeoSupport;
import com.planetmayo.debrief.satc_rcp.SATC_Activator;
import com.planetmayo.debrief.satc_rcp.ui.UIListener;
import com.vividsolutions.jts.geom.Geometry;

import MWC.Utilities.TextFormatting.GMTDateFormat;

/**
 * view that monitors the current set of bounded states
 *
 * @author ian
 *
 */
public class TrackStatesView extends ViewPart implements IConstrainSpaceListener {

	class NameSorter extends ViewerSorter {
	}

	class ViewContentProvider implements IStructuredContentProvider {
		private Collection<BoundedState> _myData;

		@Override
		public void dispose() {
		}

		@Override
		public Object[] getElements(final Object parent) {
			Object[] res;
			if ((_myData != null) && (!_myData.isEmpty())) {
				// try getting it as a set
				res = _myData.toArray();
			} else
				res = null;
			return res;
		}

		@SuppressWarnings("unchecked")
		@Override
		public void inputChanged(final Viewer v, final Object oldInput, final Object newInput) {
			_myData = (Collection<BoundedState>) newInput;
		}

	}

	class ViewLabelProvider extends LabelProvider implements ITableLabelProvider {

		@Override
		public Image getColumnImage(final Object obj, final int index) {
			return getImage(obj);
		}

		@Override
		public String getColumnText(final Object obj, final int index) {
			final BoundedState bs = (BoundedState) obj;
			return bs.getTime().toString();
			// return getText(obj);
		}

		@Override
		public Image getImage(final Object obj) {
			return null;
			// return PlatformUI.getWorkbench().getSharedImages()
			// .getImage(ISharedImages.IMG_OBJ_ELEMENT);
		}
	}

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "com.planetmayo.debrief.satc.views.TrackStatesView";

	private ISolversManager _solversManager;
	private ISolver _activeSolver;

	private TableViewer viewer;
	private final SimpleDateFormat _df = new GMTDateFormat("MMM/dd HH:mm:ss");

	private IConstrainSpaceListener constrainSpaceListener;
	private ISolversManagerListener solversManagerListener;
	/**
	 * let user indicate whether we wish to display intermediate bounded states
	 *
	 */
	private Action _debugMode;
	private Action _testSolverAction;
	private Action _testSetActive;

	private ISolver _testSolver;

	private void contributeToActionBars() {
		final IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize it.
	 */
	@Override
	public void createPartControl(final Composite parent) {
		_solversManager = SATC_Activator.getDefault().getService(ISolversManager.class, true);

		viewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		viewer.setContentProvider(new ViewContentProvider());
		viewer.setLabelProvider(new ViewLabelProvider());
		viewer.setSorter(new NameSorter());
		viewer.setInput(null);
		viewer.getTable().setHeaderVisible(true);
		viewer.setSorter(new ViewerSorter() {

			@Override
			public int compare(final Viewer viewer, final Object e1, final Object e2) {
				final BoundedState c1 = (BoundedState) e1;
				final BoundedState c2 = (BoundedState) e2;
				return c1.compareTo(c2);
			}
		});

		// ok, sort out the columns
		final TableViewerColumn col1 = new TableViewerColumn(viewer, SWT.NONE);
		col1.getColumn().setText("Time");
		col1.getColumn().setWidth(200);
		col1.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(final Object element) {
				final BoundedState bs = (BoundedState) element;
				// return bs.getTime().toString();
				return _df.format(bs.getTime());
			}
		});

		// ok, sort out the columns
		final TableViewerColumn col2 = new TableViewerColumn(viewer, SWT.NONE);
		col2.getColumn().setText("Location");
		col2.getColumn().setWidth(100);
		col2.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(final Object element) {
				String res;
				final BoundedState bs = (BoundedState) element;
				final LocationRange loc = bs.getLocation();
				if (loc != null) {
					final Geometry myArea = loc.getGeometry();
					final Geometry theBoundary = myArea.convexHull();
					final double theArea = theBoundary.getArea();
					res = myArea.getCoordinates().length + "pts " + (int) (theArea * 100000);
				} else
					res = "n/a";

				return res;
			}
		});

		// ok, sort out the columns
		final TableViewerColumn col3 = new TableViewerColumn(viewer, SWT.NONE);
		col3.getColumn().setText("Speed");
		col3.getColumn().setWidth(100);
		col3.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(final Object element) {
				String res;
				final BoundedState bs = (BoundedState) element;
				final SpeedRange range = bs.getSpeed();
				if (range != null)
					res = (int) GeoSupport.MSec2kts(range.getMin()) + " - " + (int) GeoSupport.MSec2kts(range.getMax());
				else
					res = "n/a";

				return res;
			}
		});

		// ok, sort out the columns
		final TableViewerColumn col4 = new TableViewerColumn(viewer, SWT.NONE);
		col4.getColumn().setText("Course");
		col4.getColumn().setWidth(100);
		col4.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(final Object element) {
				String res;
				final BoundedState bs = (BoundedState) element;
				final CourseRange range = bs.getCourse();
				if (range != null)
					res = (int) Math.toDegrees(range.getMin()) + " - " + (int) Math.toDegrees(range.getMax());
				else
					res = "n/a";

				return res;
			}
		});

		final TableViewerColumn col5 = new TableViewerColumn(viewer, SWT.NONE);
		col5.getColumn().setText("Leg");
		col5.getColumn().setWidth(100);
		col5.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(final Object element) {
				String res;
				final BoundedState bs = (BoundedState) element;
				final String leg = bs.getMemberOf();
				if (leg != null)
					res = leg;
				else
					res = " ";

				return res;
			}
		});

		makeActions();
		contributeToActionBars();

		initListeners(parent.getDisplay());
		_solversManager.addSolversManagerListener(solversManagerListener);
		setActiveSolver(_solversManager.getActiveSolver());
	}

	@Override
	public void dispose() {
		_solversManager.removeSolverManagerListener(solversManagerListener);
		if (_activeSolver != null) {
			_activeSolver.getBoundsManager().removeConstrainSpaceListener(constrainSpaceListener);
		}
		super.dispose();
	}

	@Override
	public void error(final IBoundsManager boundsManager, final IncompatibleStateException ex) {
		// TODO: switch UI to be a composite view, with a label above the table.
		// the label is normally hidden , and shown when
		// when we get incompatible states. Show the message in that label, and
		// disable the table

		viewer.setInput(null);
		//
		// MessageDialog.openInformation(Display.getDefault().getActiveShell(),
		// "Bounding states", "Incompatible states found");
	}

	private void fillLocalPullDown(final IMenuManager manager) {
	}

	private void fillLocalToolBar(final IToolBarManager manager) {
		manager.add(_testSetActive);
		manager.add(_testSolverAction);
		manager.add(_debugMode);
	}

	private void initListeners(final Display display) {
		solversManagerListener = new ISolversManagerListener() {

			@Override
			public void activeSolverChanged(final ISolver activeSolver) {
				setActiveSolver(activeSolver);
			}

			@Override
			public void solverCreated(final ISolver solver) {
			}
		};
		solversManagerListener = UIListener.wrap(display, ISolversManagerListener.class, solversManagerListener);
		constrainSpaceListener = UIListener.wrap(display, IConstrainSpaceListener.class, this);
	}

	private void makeActions() {
		_debugMode = new Action("Debug Mode", SWT.TOGGLE) {
		};
		_debugMode.setText("Debug Mode");
		_debugMode.setChecked(false);
		_debugMode.setToolTipText("Track all states (including application of each Contribution)");
		_testSetActive = new Action("Set active") {

			@Override
			public void run() {
				_solversManager.setActiveSolver(_testSolver);
			}
		};
		_testSetActive.setEnabled(false);
		_testSolverAction = new Action("Create Test solver") {
			@Override
			public void run() {
				_testSolver = _solversManager.createSolver("Test solver from another view");
				_testSetActive.setEnabled(true);
			}
		};
	}

	@Override
	public void restarted(final IBoundsManager boundsManager) {
		viewer.setInput(null);
	}

	private void setActiveSolver(final ISolver solver) {
		if (_activeSolver != null) {
			_activeSolver.getBoundsManager().removeConstrainSpaceListener(constrainSpaceListener);
		}
		viewer.setInput(null);
		_activeSolver = solver;
		if (_activeSolver != null) {
			_activeSolver.getBoundsManager().addConstrainSpaceListener(constrainSpaceListener);
		}
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	@Override
	public void setFocus() {
		viewer.getControl().setFocus();
	}

	@Override
	public void statesBounded(final IBoundsManager boundsManager) {
		viewer.setInput(_activeSolver.getProblemSpace().states());
	}

	@Override
	public void stepped(final IBoundsManager boundsManager, final int thisStep, final int totalSteps) {
		if (_debugMode.isChecked()) {
			viewer.setInput(_activeSolver.getProblemSpace().states());
		}
	}

}