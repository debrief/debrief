package com.planetmayo.debrief.satc_rcp.views;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.TreeSet;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.dialogs.MessageDialog;
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

import com.planetmayo.debrief.satc.model.generator.BoundedStatesListener;
import com.planetmayo.debrief.satc.model.generator.TrackGenerator;
import com.planetmayo.debrief.satc.model.states.BaseRange;
import com.planetmayo.debrief.satc.model.states.BaseRange.IncompatibleStateException;
import com.planetmayo.debrief.satc.model.states.BoundedState;

/**
 * view that monitors the current set of bounded states
 * 
 * @author ian
 * 
 */
public class TrackStatesView extends CoreView implements BoundedStatesListener
{

	class NameSorter extends ViewerSorter
	{
	}

	class ViewContentProvider implements IStructuredContentProvider
	{
		private TreeSet<BoundedState> _myData;

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
			_myData = (TreeSet<BoundedState>) newInput;
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
	private TableViewer viewer;
	private SimpleDateFormat _df = new SimpleDateFormat("MMM/dd HH:mm:ss");

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
				BaseRange<?> loc = bs.getLocation();
				if (loc != null)
					res = loc.getConstraintSummary();
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
				BaseRange<?> loc = bs.getSpeed();
				if (loc != null)
					res = loc.getConstraintSummary();
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
				BaseRange<?> loc = bs.getCourse();
				if (loc != null)
					res = loc.getConstraintSummary();
				else
					res = "n/a";

				return res;
			}
		});

		makeActions();
		contributeToActionBars();

		/** and listen out for track generators
		 * 
		 */
		setupMonitor();
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

	private void fillLocalPullDown(IMenuManager manager)
	{
	}

	private void fillLocalToolBar(IToolBarManager manager)
	{
		manager.add(_debugMode);
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	@Override
	public void setFocus()
	{
		viewer.getControl().setFocus();
	}

	@Override
	public void statesBounded(Collection<BoundedState> newStates)
	{
		if ((newStates == null) || (newStates.isEmpty()))
			viewer.setInput(null);
		else
			viewer.setInput(newStates);
	}

	@Override
	public void incompatibleStatesIdentified(IncompatibleStateException e)
	{
		// TODO: switch UI to be a composite view, with a label above the table.
		// the label is normally hidden , and shown when
		// when we get incompatible states. Show the message in that label, and
		// disable the table

		viewer.setInput(null);
//		
//		MessageDialog.openInformation(Display.getDefault().getActiveShell(),
//				"Bounding states", "Incompatible states found");
	}

	@Override
	public void debugStatesBounded(Collection<BoundedState> newStates)
	{
		// are we tracking all states?
		if (_debugMode.isChecked())
		{
			// yes - pass on the good news
			if ((newStates == null) || (newStates.isEmpty()))
				statesBounded(null);
			else
				statesBounded(newStates);
		}
	}

	@Override
	protected void stopListeningTo(TrackGenerator genny)
	{
		genny.removeBoundedStateListener(this);
	}

	@Override
	protected void startListeningTo(TrackGenerator genny)
	{
		genny.addBoundedStateListener(this);
	}
}