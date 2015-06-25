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
package org.mwc.cmap.core.operations;

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
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.operations.RightClickCutCopyAdaptor.EditableTransfer;

import MWC.GUI.BaseLayer;
import MWC.GUI.DynamicLayer;
import MWC.GUI.DynamicPlottable;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers;

public class RightClickPasteAdaptor
{

	// /////////////////////////////////
	// member variables
	// ////////////////////////////////

	// /////////////////////////////////
	// constructor
	// ////////////////////////////////

	static public void getDropdownListFor(final IMenuManager manager, final Editable destination,
			final Layer[] updateLayer, final Layer[] parentLayer, final Layers theLayers, final Clipboard _clipboard)
	{

		// is the plottable a layer
		if ((destination instanceof MWC.GUI.Layer) || (destination == null))
		{
			final EditableTransfer transfer = EditableTransfer.getInstance();
			final Editable[] tr = (Editable[]) _clipboard.getContents(transfer);

			// see if there is currently a plottable on the clipboard
			if (tr != null)
			{
				try
				{
					// extract the plottable
					final Editable[] theDataList = (Editable[]) tr;
					PasteItem paster = null;

					// see if all of the selected items are layers - or not
					boolean allLayers = true;
					for (int i = 0; i < theDataList.length; i++)
					{
						final Editable editable = theDataList[i];
						if(!(editable instanceof Layer))
						{
							allLayers = false;
							continue;
						}
						
						// just check that it we're not trying to drop a layer onto a layer
						if((editable instanceof BaseLayer) &&(destination instanceof BaseLayer))
						{
							// nope, we don't allow a baselayer to be dropped onto a baselayer
							return;
						}
						
					}
					
					// so, are we just dealing with layers?
					if (allLayers)
					{
						// create the menu items
						paster = new PasteLayer(theDataList, _clipboard, (Layer) destination, theLayers);
					}
					else
					{
						// just check that there isn't a null destination
						if (destination != null)
						{
							// just check that the layers are compliant (that we're not trying to paste a dynamic plottable 
							// into a non-compliant layer
							if(tr[0] instanceof DynamicPlottable)
							{
								if(destination instanceof DynamicLayer)
								{
									// create the menu items
									paster = new PasteItem(theDataList, _clipboard, (Layer) destination,
											theLayers);
								}
							}
							else
							{
							// create the menu items
							paster = new PasteItem(theDataList, _clipboard, (Layer) destination,
									theLayers);
							}
						}
					}

					// did we find one?
					if (paster != null)
					{
						// add to the menu
						manager.add(new Separator());
						manager.add(paster);
					}
				}
				catch (final Exception e)
				{
					MWC.Utilities.Errors.Trace.trace(e);
				}
			}
		}

	} // /////////////////////////////////

	// member functions
	// ////////////////////////////////

	// /////////////////////////////////
	// nested classes
	// ////////////////////////////////

	public static class PasteItem extends Action
	{
		protected Editable[] _data;

		protected Clipboard _myClipboard;

		protected Layer _theDestination;

		protected Layers _theLayers;

		public PasteItem(final Editable[] items, final Clipboard clipboard, final Layer theDestination,
				final Layers theLayers)
		{
			// remember stuff
			// try to take a fresh clone of the data item
			_data = items;
			_myClipboard = clipboard;
			_theDestination = theDestination;
			_theLayers = theLayers;

			// formatting
			super.setText("Paste " + toString());
			super.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_TOOL_PASTE));
			
		}

		/**
		 * 
		 */
		public void run()
		{
			final AbstractOperation myOperation = new AbstractOperation(getText())
			{
				public IStatus execute(final IProgressMonitor monitor, final IAdaptable info)
						throws ExecutionException
				{
					// and let redo do the rest
					return redo(monitor, info);
				}

				public IStatus redo(final IProgressMonitor monitor, final IAdaptable info)
						throws ExecutionException
				{
					// paste the new data in it's Destination
					for (int i = 0; i < _data.length; i++)
					{
						final Editable editable = _data[i];
						_theDestination.add(editable);
					}

					// inform the listeners
					_theLayers.fireExtended();

					// now clear the clipboard
			//		_myClipboard.clearContents();

					return Status.OK_STATUS;
				}

				public IStatus undo(final IProgressMonitor monitor, final IAdaptable info)
						throws ExecutionException
				{
					// paste the new data in it's Destination
					for (int i = 0; i < _data.length; i++)
					{
						final Editable editable = _data[i];
						_theDestination.removeElement(editable);
					}

					// inform the listeners
					_theLayers.fireExtended();
					
					// put the contents back in the clipbard
					final EditableTransfer transfer = EditableTransfer.getInstance();
					_myClipboard.setContents(_data,new Transfer[]{transfer});
					
					return Status.OK_STATUS;
				}
			};
			if (CorePlugin.getUndoContext() != null) {
				myOperation.addContext(CorePlugin.getUndoContext());
			}
			CorePlugin.run(myOperation);
		}

		public String toString()
		{
			String res = "";
			if (_data.length > 1)
				res += _data.length + " items from Clipboard";
			else
			{
				if(_data[0] != null)
		  		res += _data[0].getName();
			}
			return res;
		}

	}

	// ////////////////////////////////////////////
	// clone items, using "Serializable" interface
	// ///////////////////////////////////////////////

	public static class PasteLayer extends PasteItem
	{

		public PasteLayer(final Editable[] items, final Clipboard clipboard, final Layer theDestination,
				final Layers theLayers)
		{
			super(items , clipboard, theDestination, theLayers);
		}

		public String toString()
		{
			String res = "";
			if (_data.length > 1)
				res += _data.length + " layers from Clipboard";
			else
				res += _data[0].getName();
			return res;
		}


		/**
		 * 
		 */
		public void run()
		{
			final AbstractOperation myOperation = new AbstractOperation(getText())
			{
				public IStatus execute(final IProgressMonitor monitor, final IAdaptable info)
						throws ExecutionException
				{
					for (int i = 0; i < _data.length; i++)
					{
						final Editable thisItem = _data[i];

						// copy in the new data
						// do we have a destination layer?
						if (_theDestination != null)
						{
							// extract the layer
							final Layer newLayer = (Layer) thisItem;

							// add it to the target layer
							_theDestination.add(newLayer);
						}
						else
						{
							// extract the layer
							final Layer newLayer = (Layer) thisItem;

							// add it to the top level
							_theLayers.addThisLayer(newLayer);
						}

					}
					// inform the listeners
					_theLayers.fireExtended();
					
					// now clear the clipboard
					_myClipboard.clearContents();

					return Status.OK_STATUS;
				}

				public IStatus redo(final IProgressMonitor monitor, final IAdaptable info)
						throws ExecutionException
				{
					return execute(monitor,info);
				}

				public IStatus undo(final IProgressMonitor monitor, final IAdaptable info)
						throws ExecutionException
				{
					for (int i = 0; i < _data.length; i++)
					{
						final Editable thisItem = _data[i];
						// remove the item from it's new parent
						// do we have a destination layer?
						if (_theDestination != null)
						{
							// add it to this layer
							_theDestination.removeElement(thisItem);
							_theLayers.fireExtended(null, _theDestination);
						}
						else
						{
							// just remove it from the top level
							_theLayers.removeThisLayer((Layer) thisItem);
							_theLayers.fireExtended();
						}
					}
					
					// put the contents back in the clipbard
					final EditableTransfer transfer = EditableTransfer.getInstance();
					_myClipboard.setContents(_data,new Transfer[]{transfer});

					return Status.OK_STATUS;
				}
			};
			if (CorePlugin.getUndoContext() != null) {
				myOperation.addContext(CorePlugin.getUndoContext());
			}
			CorePlugin.run(myOperation);
		}

	}

}
