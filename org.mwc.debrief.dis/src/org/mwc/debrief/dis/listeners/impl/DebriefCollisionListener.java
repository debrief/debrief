package org.mwc.debrief.dis.listeners.impl;

import java.awt.Color;

import org.mwc.debrief.dis.listeners.IDISCollisionListener;

import Debrief.Wrappers.LabelWrapper;
import MWC.GUI.BaseLayer;
import MWC.GUI.Layer;
import MWC.GUI.Plottable;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldLocation;
import MWC.TacticalData.NarrativeEntry;
import MWC.TacticalData.NarrativeWrapper;
import MWC.Utilities.ReaderWriter.XML.LayerHandler;

public class DebriefCollisionListener extends DebriefCoreListener implements IDISCollisionListener {

	final private String COLLISIONS_LAYER = "Collisions";

	public DebriefCollisionListener(final IDISContext context) {
		super(context);
	}

	@Override
	public void add(final long time, final short eid, final int movingId, final String rawMovingName,
			final int recipientId, final String rawRecipientName, final double dLat, final double dLong,
			final double depthM) {
		final String recipientName;

		// special handling - and ID of -1 means the environment
		if (recipientId == -1) {
			recipientName = "Environment";
		} else {
			recipientName = rawRecipientName;
		}

		final String movingName;
		if (movingId == -1) {
			movingName = "Environment";
		} else {
			movingName = rawMovingName;
		}

		final String message = "Collision between platform:" + movingName + " and " + recipientName;

		// and the narrative entry
		addNewItem(eid, LayerHandler.NARRATIVE_LAYER, new ListenerHelper() {

			@Override
			public Plottable createItem() {
				final NarrativeEntry newE = new NarrativeEntry(movingName, "COLLISION", new HiResDate(time), message);
				final Color theColor = colorFor(eid, movingName);
				newE.setColor(theColor);
				return newE;
			}

			@Override
			public Layer createLayer() {
				return new NarrativeWrapper(LayerHandler.NARRATIVE_LAYER);
			}
		});

		// create the text marker
		addNewItem(eid, COLLISIONS_LAYER, new ListenerHelper() {

			@Override
			public Plottable createItem() {
				final WorldLocation newLoc = new WorldLocation(dLat, dLong, depthM);
				final Color theColor = colorFor(eid, movingName);
				return new LabelWrapper(message, newLoc, theColor);
			}

			@Override
			public Layer createLayer() {
				final Layer newB = new BaseLayer();
				newB.setName(COLLISIONS_LAYER);
				return newB;
			}
		});

	}

}
