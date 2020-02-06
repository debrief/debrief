package org.mwc.debrief.dis.listeners.impl;

import java.awt.Color;
import java.util.Iterator;

import Debrief.Wrappers.TrackWrapper;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.Layers.INewItemListener;
import MWC.GUI.Plottable;
import MWC.GUI.Properties.DebriefColors;

public class DebriefCoreListener {
	/**
	 * helper interface, that provides the data for adding new items
	 *
	 * @author ian
	 *
	 */
	public static interface ListenerHelper {
		/**
		 * create the parent item
		 *
		 * @return
		 */
		Plottable createItem();

		/**
		 * create the parent layer
		 *
		 * @return
		 */
		Layer createLayer();
	}

	protected final java.awt.Color[] defaultColors = new java.awt.Color[] { DebriefColors.RED, DebriefColors.GREEN,
			DebriefColors.BLUE, DebriefColors.CYAN, DebriefColors.MAGENTA, DebriefColors.ORANGE, DebriefColors.PINK };

	protected final IDISContext _context;

	public DebriefCoreListener(final IDISContext context) {
		_context = context;
	}

	/**
	 * add this item to the layer with the specified name
	 *
	 * @param eid
	 * @param layerName
	 * @param item
	 */
	protected void addNewItem(final short eid, final String layerName, final ListenerHelper helper) {
		final Layer destination = getLayer(eid, layerName, helper);

		final Plottable item = helper.createItem();

		destination.add(item);

		final Layer finalLayer = destination;

		// should we try any formatting?
		final Iterator<INewItemListener> iter = _context.getNewItemListeners();
		if (iter != null) {
			while (iter.hasNext()) {
				final Layers.INewItemListener newI = iter.next();
				newI.newItem(finalLayer, item, null);
			}
		}

	}

	/**
	 * get the default color for this name
	 *
	 * @param name
	 * @return
	 */
	protected Color colorFor(final short exerciseId, final String name) {
		Color res;

		final Layer layer = _context.findLayer(exerciseId, name);

		if (layer != null && layer instanceof TrackWrapper) {
			final TrackWrapper track = (TrackWrapper) layer;
			res = track.getColor();
		} else {
			// ok, get the hashmap
			final int index = Math.abs(name.hashCode()) % defaultColors.length;
			res = defaultColors[index];
		}

		return res;

	}

	protected Layer getLayer(final short exerciseId, final String name, final ListenerHelper helper) {
		// find the narratives layer
		Layer nLayer = _context.findLayer(exerciseId, name);
		if (nLayer == null) {
			nLayer = helper.createLayer();
			nLayer.setName(name);

			// and store it
			_context.addThisLayer(nLayer);

			// share the news
			final Iterator<INewItemListener> iter = _context.getNewItemListeners();
			if (iter != null) {
				while (iter.hasNext()) {
					final Layers.INewItemListener newI = iter.next();
					newI.newItem(nLayer, null, null);
				}
			}
		}
		return nLayer;
	}

}