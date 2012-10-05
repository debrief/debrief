package com.planetmayo.debrief.satc.views;

import java.text.SimpleDateFormat;
import java.util.Collection;

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
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import com.planetmayo.debrief.satc.SATC_Activator;
import com.planetmayo.debrief.satc.model.generator.BoundedStatesListener;
import com.planetmayo.debrief.satc.model.generator.TrackGenerator;
import com.planetmayo.debrief.satc.model.states.BaseRange;
import com.planetmayo.debrief.satc.model.states.BaseRange.IncompatibleStateException;
import com.planetmayo.debrief.satc.model.states.BoundedState;

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

public class TrackStatesView extends ViewPart
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
			if (_myData != null)
				res = _myData.toArray();
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
	private TableViewer viewer;
	private TrackGenerator _generator;
	private BoundedStatesListener _stateListener;
	private SimpleDateFormat _df;

	/**
	 * The constructor.
	 */
	public TrackStatesView()
	{
		// ok, start listening
		_stateListener = new BoundedStatesListener()
		{

			@Override
			public void statesBounded(Collection<BoundedState> newStates)
			{
				viewer.setInput(newStates);
			}

			@Override
			public void incompatibleStatesIdentified(IncompatibleStateException e)
			{
				MessageDialog.openInformation(Display.getDefault().getActiveShell(),
						"Bounding states", "Incompatible states found");
			}
		};

		_df = new SimpleDateFormat("hh:mm:ss");

	}

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

		// ok, sort out the columns
		TableViewerColumn col1 = new TableViewerColumn(viewer, SWT.NONE);
		col1.getColumn().setText("Time");
		col1.getColumn().setWidth(100);
		col1.setLabelProvider(new ColumnLabelProvider()
		{
			@Override
			public String getText(Object element)
			{
				BoundedState bs = (BoundedState) element;
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

		// Create the help context id for the viewer's control
		PlatformUI.getWorkbench().getHelpSystem()
				.setHelp(viewer.getControl(), "com.planetmayo.debrief.satc.viewer");
		contributeToActionBars();

		// hey, see if there's a track generator to listen to
		_generator = SATC_Activator.getDefault().getMockEngine().getGenerator();

		// did it work?
		if (_generator != null)
		{
			_generator.addBoundedStateListener(_stateListener);
		}
	}

	@Override
	public void dispose()
	{
		_generator.removeBoundedStateListener(_stateListener);

		super.dispose();
	}

	private void fillLocalPullDown(IMenuManager manager)
	{
	}

	private void fillLocalToolBar(IToolBarManager manager)
	{
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