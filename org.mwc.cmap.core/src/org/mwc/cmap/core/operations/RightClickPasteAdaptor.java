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

package org.mwc.cmap.core.operations;

import java.awt.Color;
import java.util.Enumeration;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.operations.RightClickCutCopyAdaptor.EditableTransfer;

import Debrief.Wrappers.LabelWrapper;
import Debrief.Wrappers.SensorWrapper;
import Debrief.Wrappers.DynamicTrackShapes.DynamicTrackShapeSetWrapper;
import MWC.GUI.BaseLayer;
import MWC.GUI.CanEnumerate;
import MWC.GUI.DynamicLayer;
import MWC.GUI.DynamicPlottable;
import MWC.GUI.Editable;
import MWC.GUI.HasEditables;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.Renamable;
import MWC.GUI.RestrictedBaseLayer;
import MWC.GUI.Tools.Operations.RightClickPasteAdaptor.NeedsTidyingOnPaste;
import MWC.GenericData.WorldLocation;
import junit.framework.TestCase;

public class RightClickPasteAdaptor {

	public static class PasteItem extends Action {
		protected final Editable[] _data;

		protected final Layer _theDestination;

		protected final Layers _theLayers;

		public PasteItem(final Editable[] items, final Layer theDestination, final Layers theLayers) {
			// remember stuff
			// try to take a fresh clone of the data item
			_data = items;
			_theDestination = theDestination;
			_theLayers = theLayers;

			setText("Paste " + toString());
		}

		/**
		 *
		 */
		@Override
		public void run() {
			final AbstractOperation myOperation = new AbstractOperation(getText()) {
				@Override
				public IStatus execute(final IProgressMonitor monitor, final IAdaptable info)
						throws ExecutionException {
					// and let redo do the rest
					return redo(monitor, info);
				}

				@Override
				public IStatus redo(final IProgressMonitor monitor, final IAdaptable info) throws ExecutionException {
					// paste the new data in it's Destination
					for (int i = 0; i < _data.length; i++) {
						final Editable editable = _data[i];

						renameIfNecessary(editable, _theDestination);

						// does it need tidying?
						if (editable instanceof NeedsTidyingOnPaste) {
							final NeedsTidyingOnPaste np = (NeedsTidyingOnPaste) editable;
							np.tidyUpOnPaste(_theLayers);
						}

						// add it to the target layer
						_theDestination.add(editable);
					}

					// inform the listeners
					_theLayers.fireExtended(null, _theDestination);

					return Status.OK_STATUS;
				}

				@Override
				public IStatus undo(final IProgressMonitor monitor, final IAdaptable info) throws ExecutionException {
					// paste the new data in it's Destination
					for (int i = 0; i < _data.length; i++) {
						final Editable editable = _data[i];
						_theDestination.removeElement(editable);
					}

					// inform the listeners
					_theLayers.fireExtended(null, _theDestination);

					return Status.OK_STATUS;
				}
			};
			if (CorePlugin.getUndoContext() != null) {
				myOperation.addContext(CorePlugin.getUndoContext());
			}
			CorePlugin.run(myOperation);
		}

		@Override
		public String toString() {
			String res = "";
			if (_data.length > 1)
				res += _data.length + " items from Clipboard";
			else {
				if (_data[0] != null)
					res += _data[0].getName();
			}
			return res;
		}

	}

	public static class PasteLayer extends PasteItem {
		public PasteLayer(final Editable[] items, final Clipboard clipboard, final Layer theDestination,
				final Layers theLayers) {
			super(items, theDestination, theLayers);
		}

		/**
		 *
		 */
		@Override
		public void run() {
			final AbstractOperation myOperation = new AbstractOperation(getText()) {
				@Override
				public IStatus execute(final IProgressMonitor monitor, final IAdaptable info)
						throws ExecutionException {
					for (int i = 0; i < _data.length; i++) {
						final Editable thisItem = _data[i];

						// extract the layer
						final Layer newLayer = (Layer) thisItem;

						// copy in the new data
						// do we have a destination layer?
						if (_theDestination != null) {
							renameIfNecessary(thisItem, _theDestination);

							// add it to the target layer
							_theDestination.add(newLayer);
						} else {
							// see if the target already contains an item wtih this name (if we can rename
							// it)
							renameIfNecessary(thisItem, _theLayers);

							// add it to the top level
							_theLayers.addThisLayer(newLayer);
						}

						// does it need tidying?
						if (thisItem instanceof NeedsTidyingOnPaste) {
							final NeedsTidyingOnPaste np = (NeedsTidyingOnPaste) thisItem;
							np.tidyUpOnPaste(_theLayers);
						}
					}

					// inform the listeners
					_theLayers.fireExtended();

					return Status.OK_STATUS;
				}

				@Override
				public IStatus redo(final IProgressMonitor monitor, final IAdaptable info) throws ExecutionException {
					return execute(monitor, info);
				}

				@Override
				public IStatus undo(final IProgressMonitor monitor, final IAdaptable info) throws ExecutionException {
					for (int i = 0; i < _data.length; i++) {
						final Editable thisItem = _data[i];
						// remove the item from it's new parent
						// do we have a destination layer?
						if (_theDestination != null) {
							// add it to this layer
							_theDestination.removeElement(thisItem);
							_theLayers.fireExtended(null, _theDestination);
						} else {
							// just remove it from the top level
							_theLayers.removeThisLayer((Layer) thisItem);
							_theLayers.fireExtended();
						}
					}

					return Status.OK_STATUS;
				}
			};
			if (CorePlugin.getUndoContext() != null) {
				myOperation.addContext(CorePlugin.getUndoContext());
			}
			CorePlugin.run(myOperation);
		}

		@Override
		public String toString() {
			String res = "";
			if (_data.length > 1)
				res += _data.length + " layers from Clipboard";
			else
				res += _data[0].getName();
			return res;
		}

	}

	public static class TestPaste extends TestCase {
		public void testCreateAction() {
			final Layers layers = new Layers();
			final BaseLayer layer1 = new BaseLayer();
			layer1.setName("name");
			final BaseLayer layer2 = new BaseLayer();
			layer2.setName("name");
			final BaseLayer layer3 = new BaseLayer();
			layer3.setName("name");
			final Editable[] items = new Editable[] { layer1, layer2, layer3 };
			final Clipboard clipboard = null;
			final Layer destination = null;

			final PasteItem action = createAction(destination, layers, clipboard, items);

			assertEquals("Check empty", 0, layers.size());

			action.run();

			assertEquals("Check not empty", 3, layers.size());
		}

		public void testDynamicInValid() {
			final Layers layers = new Layers();
			final BaseLayer layer1 = new BaseLayer();
			layer1.setName("name");
			final BaseLayer layer2 = new BaseLayer();
			layer2.setName("name");
			final BaseLayer layer3 = new BaseLayer();
			layer3.setName("name");
			final Editable[] items = new Editable[] { layer1, layer2, layer3 };
			final Clipboard clipboard = null;
			final DynamicLayer destination = new DynamicLayer();

			final PasteItem action = createAction(destination, layers, clipboard, items);

			assertNull("failed to create action", action);
		}

		public void testDynamicValid() {
			final Layers layers = new Layers();
			final DynamicTrackShapeSetWrapper layer1 = new DynamicTrackShapeSetWrapper("title");
			layer1.setName("name");
			final Editable[] items = new Editable[] { layer1 };
			final Clipboard clipboard = null;
			final DynamicLayer destination = new DynamicLayer();
			final PasteItem action = createAction(destination, layers, clipboard, items);
			assertNull("failed to create action", action);
		}

		public void testLabels() {
			final Layers layers = new Layers();
			final BaseLayer shapes = new BaseLayer();
			final LabelWrapper label = new LabelWrapper("label", new WorldLocation(1d, 1d, 1d), Color.red);
			final LabelWrapper label2 = new LabelWrapper("label", new WorldLocation(1d, 1d, 1d), Color.red);
			final LabelWrapper label3 = new LabelWrapper("label", new WorldLocation(1d, 1d, 1d), Color.red);
			final Editable[] items = new Editable[] { label, label2, label3 };
			final Layer destination = shapes;
			final PasteItem paste = new PasteItem(items, destination, layers);

			assertEquals("starts empty", 0, shapes.size());

			paste.run();

			assertEquals("now not empty", 3, shapes.size());

			final Enumeration<Editable> numer = shapes.elements();
			final Editable i1 = numer.nextElement();
			final Editable i2 = numer.nextElement();
			final Editable i3 = numer.nextElement();

			assertEquals("was renamed", "Copy of Copy of label", i1.getName());
			assertEquals("was renamed", "Copy of label", i2.getName());
			assertEquals("was renamed", "label", i3.getName());
		}

		public void testLayers() {
			final Layers layers = new Layers();
			final BaseLayer layer1 = new BaseLayer();
			layer1.setName("name");
			final BaseLayer layer2 = new BaseLayer();
			layer2.setName("name");
			final BaseLayer layer3 = new BaseLayer();
			layer3.setName("name");
			final Editable[] items = new Editable[] { layer1, layer2, layer3 };
			final Clipboard clipboard = null;
			final Layer destination = null;
			final PasteItem paste = new PasteLayer(items, clipboard, destination, layers);

			assertEquals("starts empty", 0, layers.size());

			paste.run();

			assertEquals("now not empty", 3, layers.size());

			final Enumeration<Editable> numer = layers.elements();
			final Editable i1 = numer.nextElement();
			final Editable i2 = numer.nextElement();
			final Editable i3 = numer.nextElement();

			assertEquals("was renamed", "Copy of Copy of name", i3.getName());
			assertEquals("was renamed", "Copy of name", i2.getName());
			assertEquals("was renamed", "name", i1.getName());
		}
	}

	private static final String DUPLICATE_PREFIX = "Copy of ";

	/**
	 * see if this layer contains an item with the specified name
	 *
	 * @param name        name we're checking against.
	 * @param destination layer we're looking at
	 *
	 * @return
	 */
	private static boolean containsThis(final String name, final CanEnumerate destination) {
		final Enumeration<Editable> enumeration = destination.elements();
		while (enumeration.hasMoreElements()) {
			final Editable next = enumeration.nextElement();
			if (next.getName() != null && next.getName().equals(name)) {
				return true;
			}
		}
		return false;
	}

	public static PasteItem createAction(final Editable destination, final Layers theLayers, final Clipboard _clipboard,
			final Editable[] theDataList) {
		PasteItem paster = null;
		// see if all of the selected items are layers - or not
		boolean allLayers = true;
		boolean restrictedLayer = false;
		for (int i = 0; i < theDataList.length; i++) {
			final Editable editable = theDataList[i];
			if(editable instanceof RestrictedBaseLayer<?>){
				restrictedLayer = true;
				continue;
			}
			if (!(editable instanceof Layer)) {
				allLayers = false;
				continue;
			}

			// just check that it we're not trying to drop a layer onto a layer
			if ((editable instanceof BaseLayer) && (destination instanceof BaseLayer)) {
				// nope, we don't allow a baselayer to be dropped onto a baselayer
				return paster;
			}

		}
		//if we are trying to paste sensors or solutions then it is a restricted layer object,
		// we dont allow it to be added to layers object
		if(restrictedLayer && destination == null) {
			paster = null;
		// so, are we just dealing with layers?
		}else if (allLayers) {
			// create the menu items
			paster = new PasteLayer(theDataList, _clipboard, (Layer) destination, theLayers);
		} else {
			// just check that there isn't a null destination
			if (destination != null) {
				// just check that the layers are compliant (that we're not trying to paste a
				// dynamic
				// plottable
				// into a non-compliant layer
				if (!(destination instanceof DynamicLayer) || theDataList[0] instanceof DynamicPlottable) {
					// create the menu items
					paster = new PasteItem(theDataList, (Layer) destination, theLayers);

					// formatting
					paster.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages()
							.getImageDescriptor(ISharedImages.IMG_TOOL_PASTE));
					paster.setActionDefinitionId(ActionFactory.PASTE.getCommandId());
				}
			}
		}
		return paster;
	}

	// member functions
	// ////////////////////////////////

	// /////////////////////////////////
	// nested classes
	// ////////////////////////////////

	static public void getDropdownListFor(final IMenuManager manager, final Editable destination,
			final Layer[] updateLayer, final HasEditables[] parentLayer, final Layers theLayers,
			final Clipboard _clipboard) {

		// is the plottable a layer
		if ((destination instanceof MWC.GUI.Layer) || (destination == null)) {
			final EditableTransfer transfer = EditableTransfer.getInstance();
			final Editable[] tr = (Editable[]) _clipboard.getContents(transfer);

			// see if there is currently a plottable on the clipboard
			if (tr != null) {
				try {
					// extract the plottable
					PasteItem paster = null;

					paster = createAction(destination, theLayers, _clipboard, tr);

					// did we find one?
					if (paster != null) {
						// add to the menu
						manager.add(new Separator());
						manager.add(paster);
					}
				} catch (final Exception e) {
					MWC.Utilities.Errors.Trace.trace(e);
				}
			}
		}

	} // /////////////////////////////////

	// ////////////////////////////////////////////
	// clone items, using "Serializable" interface
	// ///////////////////////////////////////////////

	/**
	 * helper method, to find if an item with this name already exists. If it does,
	 * we'll prepend the duplicate phrase
	 *
	 * @param editable    the item we're going to add
	 * @param enumeration the destination for the add operation
	 */
	private static void renameIfNecessary(final Editable editable, final CanEnumerate destination) {
		if (editable instanceof Renamable) {
			String hisName = editable.getName();
			while (containsThis(hisName, destination)) {
				hisName = DUPLICATE_PREFIX + hisName;
			}

			// did it change?
			if (!hisName.equals(editable.getName())) {
				((Renamable) editable).setName(hisName);
			}
		}
	}

}
