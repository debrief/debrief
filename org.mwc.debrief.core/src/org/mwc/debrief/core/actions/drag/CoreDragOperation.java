/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
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
	final public void paint(final CanvasType dest)
	{
		_segment.paint(dest);
	}


	final public void findNearestHotSpotIn(final Point cursorPos, final WorldLocation cursorLoc,
			final LocationConstruct currentNearest, final Layer parentLayer, final Layers theLayers)
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
			final CoreTMASegment tma = (CoreTMASegment) _segment;
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
		final String dontShowDragStr = CorePlugin.getToolParent().getProperty(
				PrefsPage.PreferenceConstants.DONT_SHOW_DRAG_IN_PROPS);
		final boolean dontShowDrag = Boolean.parseBoolean(dontShowDragStr);
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
		final IWorkbench wb = PlatformUI.getWorkbench();
		final IWorkbenchWindow win = wb.getActiveWorkbenchWindow();
		final IWorkbenchPage page = win.getActivePage();
		final IViewPart view = page.findView(IPageLayout.ID_PROP_SHEET);

		// do we have a properties view open?
		if (view != null)
		{
			final PropertySheet ps = (PropertySheet) view;
			final PropertySheetPage thisPage = (PropertySheetPage) ps.getCurrentPage();

			// and have we found a properties page?
			if (thisPage != null && !thisPage.getControl().isDisposed())
			{
				// wrap the plottable
				final EditableWrapper parentP = new EditableWrapper(parent, null, theLayers);
				final EditableWrapper wrapped = new EditableWrapper(subject, parentP,
						theLayers);
				final ISelection selected = new StructuredSelection(wrapped);

				// tell the properties page to show what we're dragging
				thisPage
						.selectionChanged(win.getActivePage().getActivePart(), selected);

				// and trigger the update
				thisPage.refresh();
			}
		}

	}
	
}
