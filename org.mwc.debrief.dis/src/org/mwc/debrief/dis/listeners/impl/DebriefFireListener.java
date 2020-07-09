package org.mwc.debrief.dis.listeners.impl;

import java.awt.Color;

import org.mwc.debrief.dis.listeners.IDISFireListener;

import Debrief.Wrappers.LabelWrapper;
import MWC.GUI.BaseLayer;
import MWC.GUI.Layer;
import MWC.GUI.Plottable;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldLocation;
import MWC.TacticalData.NarrativeEntry;
import MWC.TacticalData.NarrativeWrapper;
import MWC.Utilities.ReaderWriter.XML.LayerHandler;

public class DebriefFireListener extends DebriefCoreListener implements IDISFireListener {

	final private String MY_LAYER = "WPN RELEASE";

	public DebriefFireListener(final IDISContext context) {
		super(context);
	}

	@Override
	public void add(final long time, final short eid, final int hisId, final String hisName, final int tgtId,
			final String tgtName, final double dLat, final double dLon, final double depth) {
		final String message = "Launch of new weapon from:" + hisName + " at:" + tgtName;

		// create the text marker
		addNewItem(eid, MY_LAYER, new ListenerHelper() {

			@Override
			public Plottable createItem() {
				final WorldLocation newLoc = new WorldLocation(dLat, dLon, depth);
				final Color theColor = colorFor(eid, hisName);
				return new LabelWrapper(message, newLoc, theColor);
			}

			@Override
			public Layer createLayer() {
				final Layer newB = new BaseLayer();
				newB.setName(MY_LAYER);
				return newB;
			}
		});

		// and the narrative entry
		addNewItem(eid, LayerHandler.NARRATIVE_LAYER, new ListenerHelper() {

			@Override
			public Plottable createItem() {
				final NarrativeEntry newE = new NarrativeEntry(hisName, MY_LAYER, new HiResDate(time), message);
				final Color theColor = colorFor(eid, hisName);
				newE.setColor(theColor);
				return newE;
			}

			@Override
			public Layer createLayer() {
				return new NarrativeWrapper(LayerHandler.NARRATIVE_LAYER);
			}
		});
	}

}
