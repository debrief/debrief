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
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 */
package org.mwc.debrief.gndmanager.ui;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.EditorPart;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.operations.DebriefActionWrapper;
import org.mwc.cmap.plotViewer.actions.CoreEditorAction;
import org.mwc.cmap.plotViewer.actions.IChartBasedEditor;
import org.mwc.debrief.gndmanager.Activator;
import org.mwc.debrief.gndmanager.Tracks.TrackStoreWrapper;

import MWC.GUI.BaseLayer;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.PlainChart;
import MWC.GUI.Plottable;
import MWC.GUI.ToolParent;
import MWC.GUI.Tools.Palette.PlainCreate;
import MWC.GUI.Tools.Palette.PlainCreate.CreateLabelAction;
import MWC.GenericData.TimePeriod;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldLocation;
import MWC.TacticalData.temporal.TimeProvider;

/**
 * @author ian.mayo
 * 
 */
public class InsertTrackStore extends CoreEditorAction
{

	public static ToolParent _theParent = null;

	/**
	 * whether this item is a top-level layer
	 */
	private final boolean _isTopLevelLayer;

	public InsertTrackStore(final boolean isLayer)
	{
		_isTopLevelLayer = isLayer;
	}

	public InsertTrackStore()
	{
		// tell our parent that we want to be inserted as a top-level layer
		this(true);
	}

	/**
	 * @return
	 */
	protected Plottable getPlottable(final PlainChart theChart)
	{
		final TrackStoreWrapper res = new TrackStoreWrapper(Activator.getDefault().getPreferenceStore().getString(TrackStoreWrapper.COUCHDB_LOCATION),
				Activator.getDefault().getPreferenceStore().getString(TrackStoreWrapper.ES_LOCATION));
		
		// TODO: IAN - set the time period to that currently visible
		final IChartBasedEditor editor = getEditor();
		
		if(editor instanceof EditorPart)
		{
			if(editor instanceof IAdaptable)
			{
				final IAdaptable ad = (IAdaptable) editor;
				final TimeProvider prov = (TimeProvider) ad.getAdapter(TimeProvider.class);
				if(prov != null)
				{
					final TimePeriod thePeriod = prov.getPeriod();
					res.filterListTo(thePeriod.getStartDTG(), thePeriod.getEndDTG());
				}
			}
		}
		
		return res;
	}

	/**
	 * ok, store who the parent is for the operation
	 * 
	 * @param theParent
	 */
	public static void init(final ToolParent theParent)
	{
		_theParent = theParent;
	}

	/**
	 * convenience method to return the centre of hte visible area, at the surface
	 * 
	 * @param theChart
	 * @return
	 */
	protected static WorldLocation getCentre(final PlainChart theChart)
	{
		// right, what's the area we're looking at
		final WorldArea wa = theChart.getCanvas().getProjection().getVisibleDataArea();

		// get centre of area (at zero depth)
		final WorldLocation centre = wa.getCentreAtSurface();

		return centre;
	}

	/**
	 * and execute..
	 */
	protected void execute()
	{
		final PlainChart theChart = getChart();
		
		// find out the required time period

		final CreateLabelAction res = createAction(theChart);

		// did we get an action?
		if (res != null)
		{
			// do we know the layer?
			Layer layer = res.getLayer();

			// is it null? in which case we're adding a new layer
			if (layer == null)
			{
				// try to get the new plottable
				final Plottable pl = res.getNewFeature();
				if (pl instanceof Layer)
					layer = (Layer) pl;
				else
				{
					CorePlugin
							.logError(
									Status.ERROR,
									"WE WERE EXPECTING THE NEW FEATURE TO BE A LAYER - in CoreInsertChartFeature",
									null);
				}
			}

			// and the data?
			final Layers data = res.getLayers();

			// ok, now wrap the action
			final DebriefActionWrapper daw = new DebriefActionWrapper(res, data, layer);

			// and add it to our buffer (which will execute it anyway)
			CorePlugin.run(daw);
		}
	}

	protected final CreateLabelAction createAction(final PlainChart theChart)
	{
		CreateLabelAction res = null;
		final WorldArea wa = theChart.getDataArea();

		// see if we have an area defined
		if (wa != null)
		{
			// ok, get our layer name
			final String myLayer = getLayerName();

			// drop out if we don't have a target layer (the user may have cancelled)
			if (myLayer == null)
				return null;

			// ok - get the object we're going to insert
			final Plottable thePlottable = getPlottable(theChart);

			if (thePlottable != null)
			{

				// lastly, get the data
				final Layers theData = theChart.getLayers();

				// aah, and the misc layer, in which we will store the shape
				Layer theLayer = null;

				// hmm, do we want to insert ourselves as a layer?
				if (!_isTopLevelLayer)
				{
					theLayer = theData.findLayer(myLayer);

					// did we find it?
					if (theLayer == null)
					{
						// nope, better create it.
						theLayer = new BaseLayer();
						theLayer.setName(myLayer);
						theData.addThisLayer(theLayer);
					}
				}

				// and put it into an action (so we can undo it)
				res = new PlainCreate.CreateLabelAction(null, theLayer,
						theChart.getLayers(), thePlottable)
				{

					public void execute()
					{
						// generate the object
						super.execute();

						// ok, now open the properties window
						try
						{
							PlatformUI.getWorkbench().getActiveWorkbenchWindow()
									.getActivePage().showView(IPageLayout.ID_PROP_SHEET);
						}
						catch (final PartInitException e)
						{
							CorePlugin.logError(Status.WARNING,
									"Failed to open properties view", e);
						}

						// find the editor
						final IChartBasedEditor editor = getEditor();

						// highlight the editor
						PlatformUI.getWorkbench().getActiveWorkbenchWindow()
								.getActivePage().activate((IWorkbenchPart) editor);

						// select the shape
						editor.selectPlottable(_theShape, _theLayer);
					}
				};
			}

		}
		else
		{
			// we haven't got an area, inform the user
			CorePlugin
					.showMessage(
							"Create Feature",
							"Sorry, we can't create a shape until the area is defined.  Try adding a coastline first");
		}

		return res;
	}

	/**
	 * @return
	 */
	protected String getLayerName()
	{
		return "Track Store";
	}

}
