package org.mwc.cmap.core.operations;

import java.io.*;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.*;
import org.eclipse.jface.action.*;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.ui.*;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.operations.RightClickCutCopyAdaptor.EditableTransfer;

import MWC.GUI.*;

public class RightClickPasteAdaptor
{

	// /////////////////////////////////
	// member variables
	// ////////////////////////////////

	// /////////////////////////////////
	// constructor
	// ////////////////////////////////

	static public void getDropdownListFor(IMenuManager manager, Editable destination,
			Layer[] updateLayer, Layer[] parentLayer, Layers theLayers, Clipboard _clipboard)
	{

		// is the plottable a layer
		if ((destination instanceof MWC.GUI.Layer) || (destination == null))
		{
			EditableTransfer transfer = EditableTransfer.getInstance();
			Editable[] tr = (Editable[]) _clipboard.getContents(transfer);

			if (!(tr instanceof Editable[]))
				return;

			// see if there is currently a plottable on the clipboard
			if (tr != null)
			{
				try
				{
					// extract the plottable
					Editable[] theDataList = (Editable[]) tr;
					PasteItem paster = null;

					// see if all of the selected items are layers - or not
					boolean allLayers = true;
					for (int i = 0; i < theDataList.length; i++)
					{
						Editable editable = theDataList[i];
						if(!(editable instanceof Layer))
						{
							allLayers = false;
							continue;
						}
					}
					
					// so, are we just dealing with layers?
					if (allLayers)
					{
				//		MWC.GUI.Layer clipLayer = (MWC.GUI.Layer) theData;

						// create the menu items
						paster = new PasteLayer(theDataList, _clipboard, (Layer) destination, theLayers);
					}
					else
					{
						// just check that there isn't a null destination
						if (destination != null)
						{
							// create the menu items
							paster = new PasteItem(theDataList, _clipboard, (Layer) destination,
									theLayers);
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
				catch (Exception e)
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

		public PasteItem(Editable[] items, Clipboard clipboard, Layer theDestination,
				Layers theLayers)
		{
			// remember stuff
			// try to take a fresh clone of the data item
			_data = cloneThese(items);
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
			AbstractOperation myOperation = new AbstractOperation(getText())
			{
				Editable[] _theSubjects;

				public IStatus execute(IProgressMonitor monitor, IAdaptable info)
						throws ExecutionException
				{
					// copy in the new data
					EditableTransfer transfer = EditableTransfer.getInstance();
					_theSubjects = (Editable[]) _myClipboard.getContents(transfer);

					// paste the new data in it's Destination
					for (int i = 0; i < _theSubjects.length; i++)
					{
						Editable editable = _theSubjects[i];
						_theDestination.add(editable);
					}

					// inform the listeners
					_theLayers.fireExtended();

					return Status.OK_STATUS;
				}

				public IStatus redo(IProgressMonitor monitor, IAdaptable info)
						throws ExecutionException
				{
					// paste the new data in it's Destination
					for (int i = 0; i < _theSubjects.length; i++)
					{
						Editable editable = _theSubjects[i];
						_theDestination.add(editable);
					}

					// inform the listeners
					_theLayers.fireExtended();

					return Status.OK_STATUS;
				}

				public IStatus undo(IProgressMonitor monitor, IAdaptable info)
						throws ExecutionException
				{
					// paste the new data in it's Destination
					for (int i = 0; i < _theSubjects.length; i++)
					{
						Editable editable = _theSubjects[i];
						_theDestination.removeElement(editable);
					}

					// inform the listeners
					_theLayers.fireExtended();
					
					return Status.OK_STATUS;
				}
			};
			// put in the global context, for some reason
			myOperation.addContext(CorePlugin.CMAP_CONTEXT);
			CorePlugin.run(myOperation);
		}

		public String toString()
		{
			String res = "";
			if (_data.length > 1)
				res += _data.length + " items from Clipboard";
			else
				res += _data[0].getName();
			return res;
		}

	}

	// ////////////////////////////////////////////
	// clone items, using "Serializable" interface
	// ///////////////////////////////////////////////

	/**
	 * create duplicates of this series of items
	 */
	static public Editable[] cloneThese(Editable[] items)
	{
		Editable[] res = new Editable[items.length];
		for (int i = 0; i < items.length; i++)
		{
			Editable thisOne = items[i];
			res[i] = cloneThis(thisOne);
		}
		return res;
	}

	/**
	 * duplicate this item
	 * 
	 * @param item
	 * @return
	 */
	static public Editable cloneThis(Editable item)
	{
		Editable res = null;
		try
		{
			java.io.ByteArrayOutputStream bas = new ByteArrayOutputStream();
			java.io.ObjectOutputStream oos = new ObjectOutputStream(bas);
			oos.writeObject(item);
			// get closure
			oos.close();
			bas.close();

			// now get the item
			byte[] bt = bas.toByteArray();

			// and read it back in as a new item
			java.io.ByteArrayInputStream bis = new ByteArrayInputStream(bt);

			// create the reader
			java.io.ObjectInputStream iis = new ObjectInputStream(bis);

			// and read it in
			Object oj = iis.readObject();

			// get more closure
			bis.close();
			iis.close();

			if (oj instanceof Editable)
			{
				res = (Editable) oj;
			}
		}
		catch (Exception e)
		{
			MWC.Utilities.Errors.Trace.trace(e);
		}
		return res;
	}

	public static class PasteLayer extends PasteItem
	{

		public PasteLayer(Editable[] items, Clipboard clipboard, Layer theDestination,
				Layers theLayers)
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
			AbstractOperation myOperation = new AbstractOperation(getText())
			{
				public IStatus execute(IProgressMonitor monitor, IAdaptable info)
						throws ExecutionException
				{
					for (int i = 0; i < _data.length; i++)
					{
						Editable thisItem = _data[i];

						// copy in the new data
						// do we have a destination layer?
						if (_theDestination != null)
						{
							// extract the layer
							Layer newLayer = (Layer) thisItem;

							// add it to the target layer
							_theDestination.add(newLayer);
						}
						else
						{
							// extract the layer
							Layer newLayer = (Layer) thisItem;

							// add it to the top level
							_theLayers.addThisLayer(newLayer);
						}

					}
					// inform the listeners
					_theLayers.fireExtended();

					return Status.OK_STATUS;
				}

				public IStatus redo(IProgressMonitor monitor, IAdaptable info)
						throws ExecutionException
				{
					for (int i = 0; i < _data.length; i++)
					{
						Editable thisItem = _data[i];
						
						// copy in the new data
						// do we have a destination layer?
						if (_theDestination != null)
						{
							// extract the layer
							Layer newLayer = (Layer) thisItem;

							// add it to the target layer
							_theDestination.add(newLayer);
						}
						else
						{
							// extract the layer
							Layer newLayer = (Layer) thisItem;

							// add it to the top level
							_theLayers.addThisLayer(newLayer);
						}
					}
					// inform the listeners
					_theLayers.fireExtended();

					return Status.OK_STATUS;
				}

				public IStatus undo(IProgressMonitor monitor, IAdaptable info)
						throws ExecutionException
				{
					for (int i = 0; i < _data.length; i++)
					{
						Editable thisItem = _data[i];
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
					
					return Status.OK_STATUS;
				}
			};
			// put in the global context, for some reason
			myOperation.addContext(CorePlugin.CMAP_CONTEXT);
			CorePlugin.run(myOperation);
		}

	}

}
