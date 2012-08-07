package org.mwc.debrief.core.actions.drag;

import java.awt.Point;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.views.properties.PropertySheet;
import org.eclipse.ui.views.properties.PropertySheetPage;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.property_support.EditableWrapper;
import org.mwc.debrief.core.preferences.PrefsPage;

import Debrief.Wrappers.TrackWrapper;
import Debrief.Wrappers.Track.CoreTMASegment;
import Debrief.Wrappers.Track.TrackSegment;
import MWC.GUI.CanvasType;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.Shapes.DraggableItem.LocationConstruct;
import MWC.GenericData.WorldLocation;

/** super-class that introduces ability to extract the status message used when dragging track segments.  This behaviour
 * is then inherited by child classes.
 * 
 * @author ian
 *
 */
public class CoreDragOperation
{
	/** the track segment that gets manipulated
	 * 
	 */
	final protected TrackSegment _segment;
	final private String _myName;

	protected CoreDragOperation(final TrackSegment trackSegment, final String myName)
	{
		_segment = trackSegment;
		_myName = myName;
	}
	

	/** repaint the track segment
	 * 
	 * @param dest
	 */
	final public void paint(CanvasType dest)
	{
		_segment.paint(dest);
	}


	final public void findNearestHotSpotIn(Point cursorPos, WorldLocation cursorLoc,
			LocationConstruct currentNearest, Layer parentLayer, Layers theLayers)
	{
		// we don't support this when dragging tracks
	}
	
	/** get the name of this hot-spot
	 * 
	 * @return
	 */
	final public String getName()
	{
		return _myName;
	}
	/** retrieve the text message representing the last drag
	 * 
	 * @return
	 */
	public String getDragMsg()
	{
		String res = null;
		
		if(_segment instanceof CoreTMASegment)
		{
			CoreTMASegment tma = (CoreTMASegment) _segment;
			res = tma.getDragTextMessage();
		}
		
		return res;
	}


	/**
	 * whether the user wants the live solution data to be shown in the properties
	 * window
	 * 
	 * @return yes/no
	 */
	private static boolean showInProperties()
	{
		String dontShowDragStr = CorePlugin.getToolParent().getProperty(
				PrefsPage.PreferenceConstants.DONT_SHOW_DRAG_IN_PROPS);
		boolean dontShowDrag = Boolean.parseBoolean(dontShowDragStr);
		return !dontShowDrag;
	}

	/**
	 * utility function to stick the supplied item on the properties view, and
	 * then refresh it.
	 * 
	 * @param subject
	 *          what to show as the current selection
	 */
	protected void updatePropsView(final TrackSegment subject,
			final TrackWrapper parent, final Layers theLayers)
	{
		if (!showInProperties())
			return;

		// get the current properties page
		IWorkbench wb = PlatformUI.getWorkbench();
		IWorkbenchWindow win = wb.getActiveWorkbenchWindow();
		final IWorkbenchPage page = win.getActivePage();
		IViewPart view = page.findView(IPageLayout.ID_PROP_SHEET);

		// do we have a properties view open?
		if (view != null)
		{
			PropertySheet ps = (PropertySheet) view;
			PropertySheetPage thisPage = (PropertySheetPage) ps.getCurrentPage();

			// and have we found a properties page?
			if (thisPage != null && !thisPage.getControl().isDisposed())
			{
				// wrap the plottable
				EditableWrapper parentP = new EditableWrapper(parent, null, theLayers);
				EditableWrapper wrapped = new EditableWrapper(subject, parentP,
						theLayers);
				ISelection selected = new StructuredSelection(wrapped);

				// tell the properties page to show what we're dragging
				thisPage
						.selectionChanged(win.getActivePage().getActivePart(), selected);

				// and trigger the update
				thisPage.refresh();
			}
		}

	}
	
}
