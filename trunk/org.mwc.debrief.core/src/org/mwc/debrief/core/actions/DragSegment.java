/**
 * 
 */
package org.mwc.debrief.core.actions;


import java.util.Vector;

import org.eclipse.core.runtime.Status;
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
import org.mwc.cmap.plotViewer.editors.chart.SWTCanvas;
import org.mwc.cmap.plotViewer.editors.chart.SWTChart.PlotMouseDragger;
import org.mwc.debrief.core.DebriefPlugin;
import org.mwc.debrief.core.actions.drag.FreeDragMode;

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
	
	public static class DragMode extends Action implements DragFeature.DragOperation
	{
		public DragMode(String title)
		{
			super(title, IAction.AS_RADIO_BUTTON);
		}

		@Override
		public void run() {
			_currentDragMode = this;
			super.run();
		}

		@Override
		public void apply(DraggableItem item, WorldVector offset) {
			item.shift(offset);
			System.err.println("doing:" + this.getText());
		}
		
		public void findNearest(Layer thisLayer,
				MWC.GenericData.WorldLocation cursorLoc, java.awt.Point cursorPos,
				LocationConstruct currentNearest, Layer parentLayer)
		{
			// we only act on track wrappers, check if this is one
			if (thisLayer instanceof TrackWrapper)
			{
				TrackWrapper thisTrack = (TrackWrapper) thisLayer;
				// find it's nearest segment
				thisTrack.findNearestSegmentHotspotFor(cursorLoc, cursorPos,
						currentNearest);
			}
		}
	}
	
	private static Vector<Action> _dragModes;
	
	public static Vector<Action> getDragModes(){
		if(_dragModes == null)
		{
			_dragModes = new Vector<Action>();
			org.mwc.debrief.core.actions.DragSegment.DragMode keepCourse = new FreeDragMode();
			org.mwc.debrief.core.actions.DragSegment.DragMode keepSpeed = new DragMode("spd");
			org.mwc.debrief.core.actions.DragSegment.DragMode keepRange = new DragMode("rng");
			org.mwc.debrief.core.actions.DragSegment.DragMode translate = new DragMode("[]");
			
			_dragModes.add(keepCourse);
			_dragModes.add(keepSpeed);
			_dragModes.add(keepRange);
			_dragModes.add(translate);
			
			// and initiate the drag
		//	translate.setChecked(true);
			translate.run();
			
		}
		return _dragModes;
	}

	public DragSegment()
	{

	}
	
	protected static DragMode _currentDragMode;
	
	protected void execute()
	{
		// ok, fire our parent
		super.execute();

		// now, try to open the stacked dots view
		try
		{
			IWorkbench wb = PlatformUI.getWorkbench();
			IWorkbenchWindow win = wb.getActiveWorkbenchWindow();
			IWorkbenchPage page = win.getActivePage();
			page.showView(CorePlugin.STACKED_DOTS);

			System.err.println("run mode is:" + _currentDragMode.getText());
			
		}
		catch (PartInitException e)
		{
			CorePlugin.logError(Status.ERROR, "Failed to open stacked dots", e);
		}

		
	}
	
	/** simpler test that just supports tracks
	 * 
	 */
	public void findNearest(Layer thisLayer,
			MWC.GenericData.WorldLocation cursorLoc, java.awt.Point cursorPos,
			LocationConstruct currentNearest, Layer parentLayer)
	{
		_currentDragMode.findNearest(thisLayer, cursorLoc, cursorPos, currentNearest, parentLayer);
	}

	@Override
	public Cursor getDragCursor()
	{
		return new Cursor(Display.getDefault(), DebriefPlugin
				.getImageDescriptor("icons/SelectFeatureHitDown.ico").getImageData(), 4,
				2);
	}
	
	
	
	@Override
	public PlotMouseDragger getDragMode() {
		return new DragSegmentMode();
	}



	public class DragSegmentMode extends DragFeature.DragFeatureMode
	{

		@Override
		public void doMouseDrag(Point pt, int JITTER, Layers theLayers,
				SWTCanvas theCanvas) {
			
			// let the parent do the leg-work
			super.doMouseDrag(pt, JITTER, theLayers, theCanvas);
			
			// cool, is it a track that we've just dragged?
			if (_hoverTarget instanceof TrackWrapper)
			{
				// if the current editor is a track data provider,
				// tell it that we've shifted
				IWorkbench wb = PlatformUI.getWorkbench();
				IWorkbenchWindow win = wb.getActiveWorkbenchWindow();
				IWorkbenchPage page = win.getActivePage();
				IEditorPart editor = page.getActiveEditor();
				TrackDataProvider dataMgr = (TrackDataProvider) editor
						.getAdapter(TrackDataProvider.class);
				// is it one of ours?
				if (dataMgr != null)
				{
					{
						dataMgr.fireTrackShift((TrackWrapper) _hoverTarget);
					}
				}
			}
		}

		@Override
		public DragOperation getOperation() {
			return _currentDragMode;
		}
	}
}
