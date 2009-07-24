/**
 * 
 */
package org.mwc.debrief.core.creators.chartFeatures;

import java.util.Enumeration;
import java.util.Vector;

import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.dialogs.ListDialog;

import Debrief.Wrappers.TrackWrapper;
import Debrief.Wrappers.Track.AbsoluteTMASegment;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.PlainChart;
import MWC.GUI.Plottable;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;

/**
 * @author ian.mayo
 */
public class InsertTrackSegment extends CoreInsertChartFeature {

	private static final String NEW_LAYER_COMMAND = "[New Track...]";

	public InsertTrackSegment() {
		// tell the parent we produce a top-level layer
		super(false);
	}

	/**
	 * get a plottable object
	 * 
	 * @param centre
	 * @param theChart
	 * @return
	 */
	protected Plottable getPlottable(PlainChart theChart) {

		// right, what's the area we're looking at
		WorldArea wa = theChart.getDataArea();

		// get centre of area (at zero depth)
		WorldLocation centre = wa.getCentreAtSurface();

		AbsoluteTMASegment seg = new AbsoluteTMASegment(45, new WorldSpeed(10, WorldSpeed.Kts), 
				centre,
				new HiResDate(100000), new HiResDate(300000));

		return seg;

	}

	/**
	 * @return
	 */
	protected String getLayerName() {
		String res = null;

		// ok, get the non-track layers for the current plot

		// get the current plot
		final PlainChart theChart = getChart();

		// get the non-track layers
		Layers theLayers = theChart.getLayers();
		final String[] ourLayers = trimmedTracks(theLayers);

		// popup the layers in a question dialog
		IStructuredContentProvider theVals = new ArrayContentProvider();
		ILabelProvider theLabels = new LabelProvider();

		// collate the dialog
		ListDialog list = new ListDialog(Display.getCurrent().getActiveShell());
		list.setContentProvider(theVals);
		list.setLabelProvider(theLabels);
		list.setInput(ourLayers);
		list.setMessage("Please select target track for this segment");
		list.setTitle("Adding new track segment");
		list.setHelpAvailable(false);

		// open it
		int selection = list.open();

		// did user say yes?
		if (selection != ListDialog.CANCEL) {
			// yup, store it's name
			Object[] val = list.getResult();
			res = val[0].toString();

			// hmm, is it our add layer command?
			if (res == NEW_LAYER_COMMAND) {
				// better create one. Ask the user

				// create input box dialog
				InputDialog inp = new InputDialog(Display.getCurrent()
						.getActiveShell(), "New track",
						"Enter name for new track", "name here...", null);

				// did he cancel?
				if (inp.open() == InputDialog.OK) {
					// get the results
					String txt = inp.getValue();

					// check there's something there
					if (txt.length() > 0) {
						res = txt;
						// create new track
						TrackWrapper tw = new TrackWrapper();
						tw.setName(txt);

						// add to layers object
						theLayers.addThisLayer(tw);
					} else {
						res = null;
					}
				} else {
					res = null;
				}
			}
		}

		return res;
	}

	/**
	 * find the list of tracks
	 * 
	 * @param theLayers
	 *            the list to search through
	 * @return Tracks
	 */
	private String[] trimmedTracks(Layers theLayers) {
		Vector<String> res = new Vector<String>(0, 1);
		Enumeration<Editable> enumer = theLayers.elements();
		while (enumer.hasMoreElements()) {
			Layer thisLayer = (Layer) enumer.nextElement();
			if (thisLayer instanceof TrackWrapper) {
				res.add(thisLayer.getName());
			}
		}

		res.add(NEW_LAYER_COMMAND);

		String[] sampleArray = new String[] { "aa" };
		return res.toArray(sampleArray);
	}

}