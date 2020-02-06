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

// $RCSfile: CreateLabel.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.3 $
// $Log: CreateLabel.java,v $
// Revision 1.3  2005/12/13 09:04:52  Ian.Mayo
// Tidying - as recommended by Eclipse
//
// Revision 1.2  2005/07/08 14:18:34  Ian.Mayo
// Make utility methods more accessible
//
// Revision 1.1.1.2  2003/07/21 14:48:45  Ian.Mayo
// Re-import Java files to keep correct line spacing
//
// Revision 1.5  2003-03-19 15:37:25+00  ian_mayo
// improvements according to IntelliJ inspector
//
// Revision 1.4  2003-01-22 12:03:15+00  ian_mayo
// Create default location (centre) at zero depth
//
// Revision 1.3  2002-05-28 09:25:09+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:11:48+01  ian_mayo
// Initial revision
//
// Revision 1.2  2002-05-08 16:05:36+01  ian_mayo
// Fire extended event after creation
//
// Revision 1.1  2002-04-23 12:28:49+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-01-24 14:23:37+00  administrator
// Reflect change in Layers reformat and modified events which take an indication of which layer has been modified - a step towards per-layer graphics repaints
//
// Revision 1.0  2001-07-17 08:41:16+01  administrator
// Initial revision
//
// Revision 1.1  2001-01-03 13:40:27+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 20:48:56  ianmayo
// initial import of files
//
// Revision 1.6  2000-11-24 10:54:28+00  ian_mayo
// handle the image for the button, and check that we have a working plot (with bounds) before we add shape
//
// Revision 1.5  2000-11-22 10:51:16+00  ian_mayo
// stop it being abstract, make use of it
//
// Revision 1.4  2000-11-02 16:45:50+00  ian_mayo
// changing Layer into Interface, replaced by BaseLayer, also changed TrackWrapper so that it implements Layer,  and as we read in files, we put them into track and add Track to Layers, not to Layer then Layers
//
// Revision 1.3  1999-11-26 15:51:40+00  ian_mayo
// tidying up
//
// Revision 1.2  1999-11-11 18:23:03+00  ian_mayo
// new classes, to allow creation of shapes from palette
//

package Debrief.Tools.Palette;

import Debrief.Wrappers.LabelWrapper;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.PlainWrapper;
import MWC.GUI.ToolParent;
import MWC.GUI.Properties.PropertiesPanel;
import MWC.GUI.Tools.Action;
import MWC.GenericData.WorldLocation;

public class CreateLabel extends CoreCreateShape {

	///////////////////////////////////////////////////////
	// store action information
	///////////////////////////////////////////////////////
	static public final class CreateLabelAction implements Action {
		/**
		 * the panel we are going to show the initial editor in
		 */
		final PropertiesPanel _thePanel;
		final Layer _theLayer;
		final Debrief.Wrappers.LabelWrapper _theShape;
		final private Layers _theData;
		final private Layer addedLayer;

		public CreateLabelAction(final PropertiesPanel thePanel, final Layer theLayer, final LabelWrapper theShape,
				final Layers theData, final Layer newLayer) {
			_thePanel = thePanel;
			_theLayer = theLayer;
			_theShape = theShape;
			_theData = theData;
			addedLayer = newLayer;
		}

		/**
		 * make it so!
		 */
		@Override
		public final void execute() {
			if (addedLayer != null) {
				final Layer layer = _theData.findLayer(addedLayer.getName());
				if (layer == null) {
					_theData.addThisLayer(addedLayer);
				}

			}
			// add the Shape to the layer, and put it
			// in the property editor
			_theLayer.add(_theShape);

			if (_thePanel != null)
				_thePanel.addEditor(_theShape.getInfo(), _theLayer);

			// and fire the extended event
			_theData.fireExtended(_theShape, _theLayer);
		}

		/**
		 * specify is this is an operation which can be redone
		 */
		@Override
		public final boolean isRedoable() {
			return true;
		}

		/**
		 * specify is this is an operation which can be undone
		 */
		@Override
		public final boolean isUndoable() {
			return true;
		}

		/**
		 * return string describing this operation
		 *
		 * @return String describing this operation
		 */
		@Override
		public final String toString() {
			return "New shape:" + _theShape.getName();
		}

		/**
		 * take the shape away from the layer
		 */
		@Override
		public final void undo() {
			if (addedLayer != null) {
				_theData.removeThisLayer(addedLayer);
			} else {
				_theLayer.removeElement(_theShape);
			}
			// and fire the extended event
			_theData.fireExtended();
		}
	}

	/**
	 * helper class, to allow common code to be used for both shapes & labels
	 *
	 * @author ian
	 *
	 */
	public static interface GetAction {
		/**
		 * create the action that puts the item into a layer
		 *
		 * @param thePanel
		 * @param theLayer
		 * @param theItem
		 * @param theData
		 * @return
		 */
		Action createLabelAction(final PropertiesPanel thePanel, final Layer theLayer, final PlainWrapper theItem,
				final Layers theData);

		/**
		 * create a new instance of the correct type
		 *
		 * @param centre
		 * @return
		 */
		PlainWrapper getItem(final WorldLocation centre);
	}

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/////////////////////////////////////////////////////////////
	// member variables
	////////////////////////////////////////////////////////////
	/**
	 * the properties panel
	 */
	private final PropertiesPanel _thePanel;

	/////////////////////////////////////////////////////////////
	// constructor
	////////////////////////////////////////////////////////////
	/**
	 * constructor for label
	 *
	 * @param theParent parent where we can change cursor
	 * @param thePanel  panel
	 */
	public CreateLabel(final ToolParent theParent, final PropertiesPanel thePanel, final Layers theData,
			final BoundsProvider bounds, final String theName, final String theImage) {
		super(theParent, theName, theImage, theData, bounds);

		_thePanel = thePanel;
	}

	@Override
	public final Action getData() {
		final GetAction getAction = new GetAction() {

			@Override
			public Action createLabelAction(final PropertiesPanel thePanel, final Layer theLayer,
					final PlainWrapper theItem, final Layers theData) {
				return new CreateLabelAction(_thePanel, theLayer, (LabelWrapper) theItem, _theData, addedLayer);
			}

			@Override
			public PlainWrapper getItem(final WorldLocation centre) {
				return new LabelWrapper("blank label", centre, MWC.GUI.Properties.DebriefColors.ORANGE);
			}
		};
		return commonGetData(getAction, _thePanel);
	}

}
