/**
 * 
 */
package org.mwc.debrief.core.creators.chartFeatures;

import org.eclipse.core.runtime.Status;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.operations.DebriefActionWrapper;
import org.mwc.cmap.plotViewer.actions.CoreEditorAction;
import org.mwc.cmap.plotViewer.actions.IChartBasedEditor;
import org.mwc.debrief.core.preferences.PrefsPage;

import MWC.GUI.BaseLayer;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.PlainChart;
import MWC.GUI.Plottable;
import MWC.GUI.ToolParent;
import MWC.GUI.Tools.Palette.PlainCreate;
import MWC.GUI.Tools.Palette.PlainCreate.CreateLabelAction;
import MWC.GenericData.WorldArea;

/**
 * @author ian.mayo
 */
abstract public class CoreInsertChartFeature extends CoreEditorAction
{

	public static ToolParent _theParent = null;

	/**
	 * whether this item is a top-level layer
	 */
	private final boolean _isTopLevelLayer;

	public CoreInsertChartFeature()
	{
		this(false);
	}

	public CoreInsertChartFeature(boolean isLayer)
	{
		_isTopLevelLayer = isLayer;
	}

	/**
	 * ok, store who the parent is for the operation
	 * 
	 * @param theParent
	 */
	public static void init(ToolParent theParent)
	{
		_theParent = theParent;
	}

	/**
	 * and execute..
	 */
	protected void execute()
	{
		final PlainChart theChart = getChart();

		CreateLabelAction res = createAction(theChart);

		// did we get an action?
		if (res != null)
		{
			// do we know the layer?
			Layer layer = res.getLayer();

			// is it null? in which case we're adding a new layer
			if (layer == null)
			{
				// try to get the new plottable
				Plottable pl = res.getNewFeature();
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
			Layers data = res.getLayers();

			// ok, now wrap the action
			DebriefActionWrapper daw = new DebriefActionWrapper(res, data, layer);

			// and add it to our buffer (which will execute it anyway)
			CorePlugin.run(daw);
		}
	}

	protected final CreateLabelAction createAction(PlainChart theChart)
	{
		CreateLabelAction res = null;
		WorldArea wa = theChart.getDataArea();

		// see if we have an area defined
		if (wa != null)
		{
			// ok, get our layer name
			final String myLayer = getLayerName();

			// drop out if we don't have a target layer (the user may have cancelled)
			if (myLayer == null)
				return null;

			// ok - get the object we're going to insert
			Plottable thePlottable = getPlottable(theChart);

			if (thePlottable != null)
			{

				// lastly, get the data
				Layers theData = theChart.getLayers();

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

						// right, does the user want me to auto-select the newly created
						// item?
						String autoSelectStr = CorePlugin.getToolParent().getProperty(
								PrefsPage.PreferenceConstants.AUTO_SELECT);
						boolean autoSelect = Boolean.parseBoolean(autoSelectStr);
						if (autoSelect)
						{

							// ok, now open the properties window
							try
							{
								PlatformUI.getWorkbench().getActiveWorkbenchWindow()
										.getActivePage().showView(IPageLayout.ID_PROP_SHEET);
							}
							catch (PartInitException e)
							{
								CorePlugin.logError(Status.WARNING,
										"Failed to open properties view", e);
							}

							// find the editor
							IChartBasedEditor editor = getEditor();

							// highlight the editor
							PlatformUI.getWorkbench().getActiveWorkbenchWindow()
									.getActivePage().activate((IWorkbenchPart) editor);

							// select the shape
							editor.selectPlottable(_theShape, _theLayer);
						}
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
	 * ok, create whatever we're after
	 * 
	 * @param theChart
	 * @return
	 */
	abstract protected Plottable getPlottable(PlainChart theChart);

	/**
	 * @return
	 */
	protected String getLayerName()
	{
		return Layers.CHART_FEATURES;
	}
}