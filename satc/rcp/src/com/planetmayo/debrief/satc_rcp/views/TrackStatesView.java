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
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.part.ViewPart;

import com.planetmayo.debrief.satc.model.generator.IBoundsManager;
import com.planetmayo.debrief.satc.model.generator.IConstrainSpaceListener;
import com.planetmayo.debrief.satc.model.generator.ISolver;
import com.planetmayo.debrief.satc.model.states.BaseRange.IncompatibleStateException;
import com.planetmayo.debrief.satc.model.states.BoundedState;
import com.planetmayo.debrief.satc.model.states.CourseRange;
import com.planetmayo.debrief.satc.model.states.LocationRange;
import com.planetmayo.debrief.satc.model.states.SpeedRange;
import com.planetmayo.debrief.satc.util.GeoSupport;
import com.planetmayo.debrief.satc_rcp.SATC_Activator;
import com.planetmayo.debrief.satc_rcp.ui.UIListener;
import com.vividsolutions.jts.geom.Geometry;

/**
 * view that monitors the current set of bounded states
 * 
 * @author ian
 * 
 */
public class TrackStatesView extends ViewPart implements IConstrainSpaceListener
{

	class NameSorter extends ViewerSorter
	{
	}

	class ViewContentProvider implements IStructuredContentProvider
	{
		private Collection<BoundedState> _myData;

		@Override
		public void dispose()
		{
		}

		@Override
		public Object[] getElements(Object parent)
		{
			Object[] res;
			if ((_myData != null) && (!_myData.isEmpty()))
			{
				// try getting it as a set
				res = _myData.toArray();
			}
			else
				res = null;
			return res;
		}

		@SuppressWarnings("unchecked")
		@Override
		public void inputChanged(Viewer v, Object oldInput, Object newInput)
		{
			_myData = (Collection<BoundedState>) newInput;
		}

	}

	class ViewLabelProvider extends LabelProvider implements ITableLabelProvider
	{

		@Override
		public Image getColumnImage(Object obj, int index)
		{
			return getImage(obj);
		}

		@Override
		public String getColumnText(Object obj, int index)
		{
			BoundedState bs = (BoundedState) obj;
			return bs.getTime().toString();
			// return getText(obj);
		}

		@Override
		public Image getImage(Object obj)
		{
			return null;
			// return PlatformUI.getWorkbench().getSharedImages()
			// .getImage(ISharedImages.IMG_OBJ_ELEMENT);
		}
	}

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "com.planetmayo.debrief.satc.views.TrackStatesView";

	private ISolver solver;

	private TableViewer viewer;
	private SimpleDateFormat _df = new SimpleDateFormat("MMM/dd HH:mm:ss");

	private IConstrainSpaceListener constrainSpaceListener;
	/**
	 * let user indicate whether we wish to display intermediate bounded states
	 * 
	 */
	private Action _debugMode;

	private void contributeToActionBars()
	{
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	@Override
	public void createPartControl(Composite parent)
	{
		solver = SATC_Activator.getDefault().getService(
				ISolver.class, true);
		viewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		viewer.setContentProvider(new ViewContentProvider());
		viewer.setLabelProvider(new ViewLabelProvider());
		viewer.setSorter(new NameSorter());
		viewer.setInput(null);
		viewer.getTable().setHeaderVisible(true);
		viewer.setSorter(new ViewerSorter()
		{

			@Override
			public int compare(Viewer viewer, Object e1, Object e2)
			{
				// TODO Auto-generated method stub
				BoundedState c1 = (BoundedState) e1;
				BoundedState c2 = (BoundedState) e2;
				return c1.compareTo(c2);
			}
		});

		// ok, sort out the columns
		TableViewerColumn col1 = new TableViewerColumn(viewer, SWT.NONE);
		col1.getColumn().setText("Time");
		col1.getColumn().setWidth(200);
		col1.setLabelProvider(new ColumnLabelProvider()
		{
			@Override
			public String getText(Object element)
			{
				BoundedState bs = (BoundedState) element;
				// return bs.getTime().toString();
				return _df.format(bs.getTime());
			}
		});

		// ok, sort out the columns
		TableViewerColumn col2 = new TableViewerColumn(viewer, SWT.NONE);
		col2.getColumn().setText("Location");
		col2.getColumn().setWidth(100);
		col2.setLabelProvider(new ColumnLabelProvider()
		{
			@Override
			public String getText(Object element)
			{
				String res;
				BoundedState bs = (BoundedState) element;
				LocationRange loc = bs.getLocation();
				if (loc != null)
				{
					Geometry myArea = loc.getGeometry();
					Geometry theBoundary = myArea.convexHull();
					double theArea = theBoundary.getArea();
					res = myArea.getCoordinates().length + "pts "
							+ (int) (theArea * 100000);
				}
				else
					res = "n/a";

				return res;
			}
		});

		// ok, sort out the columns
		TableViewerColumn col3 = new TableViewerColumn(viewer, SWT.NONE);
		col3.getColumn().setText("Speed");
		col3.getColumn().setWidth(100);
		col3.setLabelProvider(new ColumnLabelProvider()
		{
			@Override
			public String getText(Object element)
			{
				String res;
				BoundedState bs = (BoundedState) element;
				SpeedRange range = bs.getSpeed();
				if (range != null)
					res = (int) GeoSupport.MSec2kts(range.getMin()) + " - "
							+ (int) GeoSupport.MSec2kts(range.getMax());
				else
					res = "n/a";

				return res;
			}
		});

		// ok, sort out the columns
		TableViewerColumn col4 = new TableViewerColumn(viewer, SWT.NONE);
		col4.getColumn().setText("Course");
		col4.getColumn().setWidth(100);
		col4.setLabelProvider(new ColumnLabelProvider()
		{
			@Override
			public String getText(Object element)
			{
				String res;
				BoundedState bs = (BoundedState) element;
				CourseRange range = bs.getCourse();
				if (range != null)
					res = (int) Math.toDegrees(range.getMin()) + " - "
							+ (int) Math.toDegrees(range.getMax());
				else
					res = "n/a";

				return res;
			}
		});

		TableViewerColumn col5 = new TableViewerColumn(viewer, SWT.NONE);
		col5.getColumn().setText("Leg");
		col5.getColumn().setWidth(100);
		col5.setLabelProvider(new ColumnLabelProvider()
		{
			@Override
			public String getText(Object element)
			{
				String res;
				BoundedState bs = (BoundedState) element;
				String leg = bs.getMemberOf();
				if (leg != null)
					res = leg;
				else
					res = " ";

				return res;
			}
		});

		makeActions();
		contributeToActionBars();

		constrainSpaceListener = UIListener.wrap(parent.getDisplay(), 
				IConstrainSpaceListener.class, this);
		solver.getBoundsManager().addConstrainSpaceListener(constrainSpaceListener);
	}

	@Override
	public void dispose()
	{
		solver.getBoundsManager().removeConstrainSpaceListener(constrainSpaceListener);
		super.dispose();
	}

	@Override
	public void statesBounded(IBoundsManager boundsManager)
	{
		viewer.setInput(solver.getProblemSpace().states());
	}

	@Override
	public void restarted(IBoundsManager boundsManager)
	{
		viewer.setInput(null);
	}

	@Override
	public void error(IBoundsManager boundsManager, IncompatibleStateException ex)
	{
		// TODO: switch UI to be a composite view, with a label above the table.
		// the label is normally hidden , and shown when
		// when we get incompatible states. Show the message in that label, and
		// disable the table

		viewer.setInput(null);
		//
		// MessageDialog.openInformation(Display.getDefault().getActiveShell(),
		// "Bounding states", "Incompatible states found");
	}

	@Override
	public void stepped(IBoundsManager boundsManager, int thisStep, int totalSteps)
	{
		if (_debugMode.isChecked())
		{
			viewer.setInput(solver.getProblemSpace().states());
		}
	}

	private void fillLocalPullDown(IMenuManager manager)
	{
	}

	private void fillLocalToolBar(IToolBarManager manager)
	{
		manager.add(_debugMode);
	}

	private void makeActions()
	{
		_debugMode = new Action("Debug Mode", SWT.TOGGLE)
		{
		};
		_debugMode.setText("Debug Mode");
		_debugMode.setChecked(false);
		_debugMode
				.setToolTipText("Track all states (including application of each Contribution)");
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	@Override
	public void setFocus()
	{
		viewer.getControl().setFocus();
	}

}