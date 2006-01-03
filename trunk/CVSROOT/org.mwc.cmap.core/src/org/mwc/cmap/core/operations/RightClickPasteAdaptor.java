package org.mwc.cmap.core.operations;

import java.io.*;

import org.eclipse.jface.action.*;
import org.eclipse.swt.dnd.Clipboard;
import org.mwc.cmap.core.operations.RightClickCutCopyAdaptor.EditableTransfer;

import MWC.GUI.*;
import MWC.GUI.Editable.EditorType;

public class RightClickPasteAdaptor
{

	// /////////////////////////////////
	// member variables
	// ////////////////////////////////

	// /////////////////////////////////
	// constructor
	// ////////////////////////////////

static public void getDropdownListFor(IMenuManager manager, EditorType editor,
			Layer updateLayer, Layer parentLayer, Layers theLayers, Clipboard _clipboard)
	{
		Editable destination = (Editable) editor.getData();
		
	  // is the plottable a layer
    if ((destination instanceof MWC.GUI.Layer) || (destination == null))
    {
    	EditableTransfer transfer = EditableTransfer.getInstance();
    	Editable[] tr = (Editable[]) _clipboard.getContents(transfer);
    	
      if(!(tr instanceof Editable[]))
      	return;
    
      // see if there is currently a plottable on the clipboard
      if (tr  != null)
      {
          try
          {
            // extract the plottable
            Editable[] theDataList = (Editable[]) tr;
            Editable theData = theDataList[0];
            PasteItem paster = null;

              // see if it is a layer or not
              if (theData instanceof MWC.GUI.Layer)
              {

//                MWC.GUI.Layer clipLayer = (MWC.GUI.Layer) theData;

                // create the menu items
//                paster = new PasteLayer(clipLayer,
//                                        _clipboard,
//                                        (Layer) destination,
//                                        theLayers);
              }
              else
              {
                // just check that there isn't a null destination
                if (destination != null)
                {
                  // create the menu items
                  paster = new PasteItem(theDataList,
                                         _clipboard,
                                         (Layer) destination,
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

    
	}	// /////////////////////////////////
	// member functions
	// ////////////////////////////////

	// /////////////////////////////////
	// nested classes
	// ////////////////////////////////


	public  static class PasteItem extends Action
	{
		Editable[] _data;

		Clipboard _myClipboard;

		Layer _theDestination;
		Layers _theLayers;
		

		public PasteItem(Editable[] items, Clipboard clipboard, Layer theDestination,	Layers theLayers)
		{
			// remember stuff
			// try to take a fresh clone of the data item
			_data = cloneThese(items);
			_myClipboard = clipboard;
			_theDestination = theDestination;
			_theLayers = theLayers;

			// formatting
			super.setText("Paste " + toString());

		}

		/**
		 * 
		 */
		public void run()
		{
			execute();
		}

		public boolean isUndoable()
		{
			return true;
		}

		public boolean isRedoable()
		{
			return true;
		}

		public String toString()
		{
			String res = "";
			if(_data.length>1)
				res += _data.length + " items from Clipboard";
			else
				res += _data[0].getName();
			return res;
		}

		public void undo()
		{
			// remove the item from it's new parent
//			_theDestination.removeElement(_data);
//
//			_theLayers.fireModified((Layer) _data);
		}

		public void execute()
		{
			// copy in the new data
			EditableTransfer transfer = EditableTransfer.getInstance();
			Editable[] res = (Editable[]) _myClipboard.getContents(transfer);

			// paste the new data in it's Destination
			for (int i = 0; i < res.length; i++)
			{
				Editable editable = res[i];
				_theDestination.add(editable);
			}

			// inform the listeners
			_theLayers.fireExtended();

		}

	}

	// ////////////////////////////////////////////
	// clone items, using "Serializable" interface
	// ///////////////////////////////////////////////
	
	/** create duplicates of this series of items 
	 * 
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
	
  /** duplicate this item
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

//	public static class PasteLayer extends PasteItem
//	{
//
//		public PasteLayer(Layer data, Clipboard clipboard, Layer theDestination,
//			 Layers theLayers)
//		{
//			super(new Editable[]{data}, clipboard, theDestination, theLayers);
//		}
//
//		public String toString()
//		{
//			String res = "Paste ";
//			if(_data.length>1)
//				res += _data.length + " layers from Clipboard";
//			else
//				res += _data[0].getName();
//			return res;
//		}
//
//		public void undo()
//		{
//			// remove the item from it's new parent
//			// do we have a destination layer?
////			if (super._theDestination != null)
////			{
////				// add it to this layer
////				_theDestination.removeElement(_data);
////				_theLayers.fireModified(_theDestination);
////			}
////			else
////			{
////				// just remove it from the top level
////				_theLayers.removeThisLayer((Layer) _data);
////				_theLayers.fireModified((Layer) _data);
////			}
//
//		}
//
//		public void execute()
//		{
//			// do we have a destination layer?
//			if (super._theDestination != null)
//				// add it to this layer
//				_theDestination.add(_data);
//			else
//			{
//				// see if there is already a track of this name at the top level
//				if (_theLayers.findLayer(_data.getName()) == null)
//				{
//					// just add it
//					_theLayers.addThisLayerDoNotResize((Layer) _data);
//				}
//				else
//				{
//					// adjust the name
//					Layer newLayer = (Layer) _data;
//
//					String theName = newLayer.getName();
//
//					// does the layer end in a digit?
//					char id = theName.charAt(theName.length() - 1);
//					String idStr = new String("" + id);
//					int val = 1;
//
//					String newName = null;
//					try
//					{
//						val = Integer.parseInt(idStr);
//						newName = theName.substring(0, theName.length() - 2) + " " + val;
//
//						while (_theLayers.findLayer(newName) != null)
//						{
//							val++;
//							newName = theName.substring(0, theName.length() - 2) + " " + val;
//						}
//					}
//					catch (java.lang.NumberFormatException f)
//					{
//						newName = theName + " " + val;
//						while (_theLayers.findLayer(newName) != null)
//						{
//							val++;
//							newName = theName + " " + val;
//						}
//					}
//
//					// ignore, there isn't a number, just add a 1
//					newLayer.setName(newName);
//
//					// just drop it in at the top level
//					_theLayers.addThisLayerDoNotResize((Layer) _data);
//				}
//			}
//
//			_theLayers.fireModified(null);
//
//		}
//
//	}

}
