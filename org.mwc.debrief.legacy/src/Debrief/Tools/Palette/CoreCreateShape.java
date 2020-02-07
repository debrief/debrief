/*******************************************************************************
 * Debrief - the Open Source Maritime Analysis Application
 * http://debrief.info
 *
 * (C) 2000-2020, Deep Blue C Technology Ltd
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html)
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *******************************************************************************/

package Debrief.Tools.Palette;

import javax.swing.JComboBox;
import javax.swing.JOptionPane;

import Debrief.Tools.Palette.CreateLabel.GetAction;
import MWC.GUI.BaseLayer;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.PlainWrapper;
import MWC.GUI.ToolParent;
import MWC.GUI.Properties.PropertiesPanel;
import MWC.GUI.Tools.Action;
import MWC.GUI.Tools.PlainTool;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldLocation;

/**
 * @author Ayesha <ayesha.ma@gmail.com>
 *
 */
public abstract class CoreCreateShape extends PlainTool {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	public static final String USER_SELECTED_LAYER_COMMAND = "User-selected Layer";
	protected Layer addedLayer;

	/**
	 * the layers we are going to drop this shape into
	 */
	protected final Layers _theData;

	protected JComboBox<String> selectedLayerSource;

	protected final BoundsProvider _theBounds;

	/////////////////////////////////////////////////////////////
	// constructor
	////////////////////////////////////////////////////////////
	public CoreCreateShape(final ToolParent theParent, final String theName, final String theImage,
			final Layers theData, final BoundsProvider bounds) {
		super(theParent, theName, theImage);
		_theData = theData;
		_theBounds = bounds;
	}

	/**
	 * shared functionality for creating a shape, and putting it into a layer
	 * (possibly including requesting the layer from the user
	 *
	 * @param getAction helper class
	 * @param worldArea area in view
	 * @param theData   the layers
	 * @param thePanel  the current properties panel, which the new item will appear
	 *                  in
	 * @return
	 */
	protected Action commonGetData(final GetAction getAction, final PropertiesPanel thePanel) {
		final WorldArea worldArea = getBounds();
		final Action res;
		boolean userSelected = false;
		if (worldArea != null) {
			// put the label in the centre of the plot (at the surface)
			final WorldLocation centre = worldArea.getCentreAtSurface();

			final PlainWrapper theWrapper = getAction.getItem(centre);

			final Layer theLayer;
			String layerToAddTo = getSelectedLayer();
			final boolean wantsUserSelected = CoreCreateShape.USER_SELECTED_LAYER_COMMAND.equals(layerToAddTo);
			if (wantsUserSelected || Layers.NEW_LAYER_COMMAND.equals(layerToAddTo)) {
				userSelected = true;
				if (wantsUserSelected) {
					layerToAddTo = getLayerName();
				} else {
					final String txt = JOptionPane.showInputDialog(null, "Please enter name", "New Layer",
							JOptionPane.QUESTION_MESSAGE);
					// check there's something there
					if (txt != null && !txt.isEmpty()) {
						layerToAddTo = txt;
						// create base layer
						final Layer newLayer = new BaseLayer();
						newLayer.setName(layerToAddTo);

						// add to layers object
						_theData.addThisLayer(newLayer);
						addedLayer = _theData.findLayer(layerToAddTo);
					}
				}
			}

			// do we know the target layer name?
			if (layerToAddTo != null) {
				theLayer = _theData.findLayer(layerToAddTo);
			} else {
				theLayer = null;
			}

			// do we know the target layer?
			if (theLayer == null) {
				// no, did the user choose to not select a layer?
				if (userSelected) {
					// works for debrief-legacy
					// user cancelled.
					JOptionPane.showMessageDialog(null, "An item can only be created if a parent layer is specified. "
							+ "The item has not been created", "Error", JOptionPane.ERROR_MESSAGE);
					res = null;
				} else {
					// create a default layer, for the item to go into
					final BaseLayer tmpLayer = new BaseLayer();
					tmpLayer.setName("Misc");
					_theData.addThisLayer(tmpLayer);

					// action to put the shape into this new layer
					res = getAction.createLabelAction(thePanel, tmpLayer, theWrapper, _theData);
				}
			} else {
				res = getAction.createLabelAction(thePanel, theLayer, theWrapper, _theData);
			}
		} else {
			// we haven't got an area, inform the user
			MWC.GUI.Dialogs.DialogFactory.showMessage("Create Feature",
					"Sorry, we can't create a shape until the area is defined.  Try adding a coastline first");
			res = null;
		}
		return res;
	}

	/////////////////////////////////////////////////////////////
	// member functions
	////////////////////////////////////////////////////////////
	/**
	 * get the current visible data area
	 *
	 */
	final protected WorldArea getBounds() {
		return _theBounds.getViewport();
	}

	/**
	 * @return
	 */
	final protected String getLayerName() {
		String res = null;
		addedLayer = null;
		// get the non-track layers
		final Layers theLayers = _theData;
		final String[] ourLayers = theLayers.trimmedLayers();
		final ListLayersDialog listDialog = new ListLayersDialog(ourLayers);
		listDialog.setSize(350, 300);
		listDialog.setLocationRelativeTo(null);
		listDialog.setModal(true);
		listDialog.setVisible(true);
		final String selection = listDialog.getSelectedItem();
		// did user say yes?
		if (selection != null) {
			// hmm, is it our add layer command?
			if (selection.equals(Layers.NEW_LAYER_COMMAND)) {
				// better create one. Ask the user
				final String txt = JOptionPane.showInputDialog(null, "Please enter name", "New Layer",
						JOptionPane.QUESTION_MESSAGE);
				// check there's something there
				if (txt != null && !txt.isEmpty()) {
					res = txt;
					// create base layer
					final Layer newLayer = new BaseLayer();
					newLayer.setName(res);
					// add to layers object
					theLayers.addThisLayer(newLayer);
					addedLayer = theLayers.findLayer(txt);
				} else {
					res = null;
				}
			} else {
				res = selection;
			}
		}
		return res;
	}

	protected final String getSelectedLayer() {
		if (selectedLayerSource != null) {
			return (String) selectedLayerSource.getSelectedItem();
		}
		return null;
	}

	/**
	 * used in debrief lite, to get the select layer from ribbon tab
	 *
	 * @param jCombo
	 */
	public final void setSelectedLayerSource(final JComboBox<String> jCombo) {
		selectedLayerSource = jCombo;
	}
}
