/**
 * 
 */
package org.mwc.debrief.core.actions;

import java.util.Vector;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.DataTypes.TrackData.TrackDataProvider;
import org.mwc.cmap.plotViewer.editors.chart.CoreTracker;
import org.mwc.cmap.plotViewer.editors.chart.SWTCanvas;
import org.mwc.cmap.plotViewer.editors.chart.SWTChart.PlotMouseDragger;
import org.mwc.debrief.core.DebriefPlugin;
import org.mwc.debrief.core.actions.drag.CoreDragOperation;
import org.mwc.debrief.core.actions.drag.RotateDragMode;
import org.mwc.debrief.core.actions.drag.ShearDragMode;
import org.mwc.debrief.core.actions.drag.StretchDragMode;

import Debrief.Wrappers.TrackWrapper;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.Shapes.DraggableItem;
import MWC.GUI.Shapes.DraggableItem.LocationConstruct;
import MWC.GenericData.WorldVector;

/**
 * @author ian.mayo
 */
public class DragSegment extends DragFeature
{

	/**
	 * combination of action & operation, passed to UI inclusion
	 * 
	 * @author Ian Mayo
	 * 
	 */
	public static class DragMode extends Action implements
			DragFeature.DragOperation
	{
		public DragMode(final String title, final String tip)
		{
			super(title, IAction.AS_RADIO_BUTTON);
			this.setToolTipText(tip);
		}

		@Override
		public void apply(final DraggableItem item, final WorldVector offset)
		{
			item.shift(offset);
		}

		/**
		 * implement per-action hotspot generation, since some modes have unique
		 * processing requirements
		 * 
		 * @param thisLayer
		 * @param cursorLoc
		 * @param cursorPos
		 * @param currentNearest
		 * @param parentLayer
		 */
		public void findNearest(final Layer thisLayer,
				final MWC.GenericData.WorldLocation cursorLoc, final java.awt.Point cursorPos,
				final LocationConstruct currentNearest, final Layer parentLayer, final Layers theLayers)
		{
			// we only act on track wrappers, check if this is one
			if (thisLayer instanceof TrackWrapper)
			{
				final TrackWrapper thisTrack = (TrackWrapper) thisLayer;
				// find it's nearest segment
				thisTrack.findNearestSegmentHotspotFor(cursorLoc, cursorPos,
						currentNearest);
			}
		}

		@Override
		public void run()
		{
			_currentDragMode = this;
			super.run();
		}
	}

	/**
	 * custom drag mode, for working with track segments. It elects to use the
	 * currently selected DragMode
	 * 
	 * @author Ian Mayo
	 * 
	 */
	public class DragSegmentMode extends DragFeature.DragFeatureMode
	{

		@Override
		public void doMouseDrag(final Point pt, final int JITTER, final Layers theLayers,
				final SWTCanvas theCanvas)
		{

			// let the parent do the leg-work
			super.doMouseDrag(pt, JITTER, theLayers, theCanvas);

			// cool, is it a track that we've just dragged?
			if (_parentLayer instanceof TrackWrapper)
			{
				// if the current editor is a track data provider,
				// tell it that we've shifted
				final IWorkbench wb = PlatformUI.getWorkbench();
				final IWorkbenchWindow win = wb.getActiveWorkbenchWindow();
				final IWorkbenchPage page = win.getActivePage();
				final IEditorPart editor = page.getActiveEditor();
				final TrackDataProvider dataMgr = (TrackDataProvider) editor
						.getAdapter(TrackDataProvider.class);
				// is it one of ours?
				if (dataMgr != null)
				{
					{
						dataMgr.fireTrackShift((TrackWrapper) _parentLayer);
					}
				}
			}
						
			// if it's a CoreDragOperation it may have some progress text
			if(_hoverTarget instanceof CoreDragOperation)
			{
				final CoreDragOperation cdo = (CoreDragOperation) _hoverTarget;
				final String msg = cdo.getDragMsg();
				if(msg != null)
					CoreTracker.write(msg);
			}
		}

		@Override
		public DragOperation getOperation()
		{
			return _currentDragMode;
		}
	}

	public static interface IconProvider
	{
		public Cursor getHotspotCursor();
	}

	protected static DragMode _currentDragMode;

	private static Vector<Action> _dragModes;

	public DragSegment()
	{

	}

	public static Vector<Action> getDragModes()
	{
		if (_dragModes == null)
		{
			_dragModes = new Vector<Action>();
			final org.mwc.debrief.core.actions.DragSegment.DragMode translate = new DragMode(
					"Translate", "Translate whole track");
			final org.mwc.debrief.core.actions.DragSegment.DragMode rotate = new RotateDragMode();
			final org.mwc.debrief.core.actions.DragSegment.DragMode shear = new ShearDragMode();
			final org.mwc.debrief.core.actions.DragSegment.DragMode stretch = new StretchDragMode();

			_dragModes.add(translate);
			_dragModes.add(rotate);
			_dragModes.add(stretch);
			_dragModes.add(shear);

			// and initiate the drag
			shear.setChecked(true);
			shear.run();

		}
		return _dragModes;
	}

	@Override
	protected void execute()
	{
		// ok, fire our parent
		super.execute();

		// now, try to open the stacked dots view
		try
		{
			final IWorkbench wb = PlatformUI.getWorkbench();
			final IWorkbenchWindow win = wb.getActiveWorkbenchWindow();
			final IWorkbenchPage page = win.getActivePage();
			page.showView(CorePlugin.STACKED_DOTS);
		}
		catch (final PartInitException e)
		{
			CorePlugin.logError(IStatus.ERROR, "Failed to open stacked dots", e);
		}

	}

	/**
	 * simpler test that just supports tracks
	 * 
	 */
	@Override
	public void findNearest(final Layer thisLayer,
			final MWC.GenericData.WorldLocation cursorLoc, final java.awt.Point cursorPos,
			final LocationConstruct currentNearest, final Layer parentLayer, final Layers theLayers)
	{
		if (_currentDragMode != null)
			_currentDragMode.findNearest(thisLayer, cursorLoc, cursorPos,
					currentNearest, parentLayer, theLayers);
	}

	@Override
	public Cursor getDragCursor()
	{
		return new Cursor(Display.getDefault(), DebriefPlugin.getImageDescriptor(
				"icons/SelectFeatureHitDown.ico").getImageData(), 4, 2);
	}

	@Override
	public PlotMouseDragger getDragMode()
	{
		return new DragSegmentMode();
	}

	@Override
	public Cursor getHotspotCursor(final DraggableItem hoverTarget)
	{
		Cursor res = null;
		if (hoverTarget instanceof IconProvider)
		{
			final IconProvider iconP = (IconProvider) hoverTarget;
			res = iconP.getHotspotCursor();
		}

		if (res == null)
			res = super.getHotspotCursor(hoverTarget);

		return res;
	}
}
