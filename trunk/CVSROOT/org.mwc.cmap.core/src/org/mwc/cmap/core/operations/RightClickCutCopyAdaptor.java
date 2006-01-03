package org.mwc.cmap.core.operations;

import java.io.*;

import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.*;
import org.eclipse.swt.dnd.*;
import org.mwc.cmap.core.CorePlugin;

import MWC.GUI.*;

public class RightClickCutCopyAdaptor
{

	/**
	 * embedded class used to convert our Editable objects to/from clipboard
	 * format
	 * 
	 * @author ian.mayo
	 */
	public static class EditableTransfer extends ByteArrayTransfer
	{

		private static final String MYTYPENAME = "CMAP_EDITABLE";

		public static final int MYTYPEID = registerType(MYTYPENAME);

		/**
		 * singleton instance of ourselves
		 */
		private static EditableTransfer _instance;

		/**
		 * private constructor - so we have to use the 'get instance' method
		 */
		private EditableTransfer()
		{
		}

		/**
		 * accessor, get running.
		 * 
		 * @return
		 */
		public static EditableTransfer getInstance()
		{
			if (_instance == null)
				_instance = new EditableTransfer();

			return _instance;
		}

		/**
		 * ok - convert our object ready to put it on the clipboard
		 * 
		 * @param object
		 * @param transferData
		 */
		public void javaToNative(Object object, TransferData transferData)
		{
			if (object == null || !(object instanceof Editable[]))
				return;

			if (isSupportedType(transferData))
			{
				Editable[] myItem = (Editable[]) object;
				try
				{
					ByteArrayOutputStream out = new ByteArrayOutputStream();
					ObjectOutputStream writeOut = new ObjectOutputStream(out);
					writeOut.writeObject(myItem);
					byte[] buffer = out.toByteArray();
					writeOut.close();

					super.javaToNative(buffer, transferData);

				}
				catch (IOException e)
				{
					CorePlugin.logError(Status.ERROR,
							"Problem converting object to clipboard format: " + object, e);
				}
			}
		}

		/**
		 * ok, extract our object from the clipboard
		 * 
		 * @param transferData
		 * @return
		 */
		public Object nativeToJava(TransferData transferData)
		{

			if (isSupportedType(transferData))
			{

				byte[] buffer = (byte[]) super.nativeToJava(transferData);
				if (buffer == null)
					return null;

				Editable[] myData = null;
				try
				{
					ByteArrayInputStream in = new ByteArrayInputStream(buffer);
					ObjectInputStream readIn = new ObjectInputStream(in);
					myData = (Editable[]) readIn.readObject();
					readIn.close();
				}
				catch (IOException ex)
				{
					CorePlugin.logError(Status.ERROR,
							"Problem converting object to clipboard format", null);
					return null;
				}
				catch (ClassNotFoundException e)
				{
					e.printStackTrace();
				}
				return myData;
			}

			return null;
		}

		protected String[] getTypeNames()
		{
			return new String[] { MYTYPENAME };
		}

		protected int[] getTypeIds()
		{
			return new int[] { MYTYPEID };
		}
	}

	// /////////////////////////////////
	// member variables
	// ////////////////////////////////

	// /////////////////////////////////
	// constructor
	// ////////////////////////////////

	// /////////////////////////////////
	// member functions
	// ////////////////////////////////
	static public void getDropdownListFor(IMenuManager manager, Editable[] editables,
			Layer[] updateLayers, Layer[] parentLayers, Layers theLayers, Clipboard _clipboard)
	{
		// get the editable item
		Editable data = editables[0];

		CutItem cutter = null;
		CopyItem copier = null;

		// just check is trying to operate on the layers object itself
		if (data instanceof MWC.GUI.Layers)
		{
			// do nothing, we can't copy the layers itself
		}
		else
		{

			// is this a layer
//			if (updateLayer instanceof MWC.GUI.Layers)
//			{
//				// create the Actions
//				// cutter = new CutLayer(data, _clipboard, updateLayer, theLayers,
//				// updateLayer);
//			}
//			else if (updateLayer == null)
//			{
//				// create the Actions
//				// cutter = new CutLayer(data, _clipboard, (Layer) data, theLayers,
//				// updateLayer);
//			}
//			else
			{
				// first the cut action
				cutter = new CutItem(editables, _clipboard, parentLayers, theLayers, updateLayers);
				
				// now the copy action
				copier = new CopyItem(editables, _clipboard, parentLayers, theLayers, updateLayers);

			}
			// create the menu items

			// add to the menu
			// menu.addSeparator();
			manager.add(new Separator());
			manager.add(cutter);

			// try the copier
			if (copier != null)
			{
				manager.add(copier);
			}
		}

	}

	// /////////////////////////////////
	// nested classes
	// ////////////////////////////////

	// ////////////////////////////////////////////
	//
	// ///////////////////////////////////////////////
	public static class CutItem extends Action
	{
		protected Editable[] _data;

		protected Clipboard _myClipboard;

		protected Layer[] _theParent;

		// protected Transferable _oldData;

		protected Layers _theLayers;

		protected Layer[] _updateLayer;

		public CutItem(Editable[] data, Clipboard clipboard, Layer[] theParent, Layers theLayers,
				Layer[] updateLayer)
		{
			// remember parameters
			_data = data;
			_myClipboard = clipboard;
			_theParent = theParent;
			_theLayers = theLayers;
			_updateLayer = updateLayer;

			// formatting
			super.setText("Cut " + toString());
		}

		/**
		 * 
		 */
		public void run()
		{
			super.run();

			// ok, go for it
			execute();
		}

		public String toString()
		{
			String res = "";
			if(_data.length>1)
				res += _data.length + " selected items";
			else
				res += _data[0].getName();
			return res;
		}

		public void undo()
		{
//			restoreOldData();
//
//			// is the parent the data object itself?
//			if (_theParent == _data)
//			{
//				_theLayers.addThisLayer((Layer) _data);
//			}
//			else
//			{
//				// put the data item back into it's layer
//				_theParent.add(_data);
//			}
//
//			doUpdate();
		}

		public void execute()
		{
			//
			storeOld();

			// copy in the new data
			EditableTransfer transfer = EditableTransfer.getInstance();
			_myClipboard.setContents(new Object[] { _data }, new Transfer[] { transfer });

				for (int i = 0; i < _data.length; i++)
				{
					Editable thisE= _data[i];
					Layer parentLayer = _theParent[i];
					
					// is the parent the data object itself?
					if (parentLayer == thisE)
					{
						// no, it must be the top layers object
						_theLayers.removeThisLayer((Layer) thisE);
					}
					else
					{
						// remove the new data from it's parent
						parentLayer.removeElement(thisE);
					}				
					
				}
		
			// fire updates
			doUpdate();

			// and put ourselves on the
			doUndoBuffer();
		}

		protected void doUndoBuffer()
		{
			// _theBuffer.add(this);
		}

		protected void storeOld()
		{
			// get the old data
			// _oldData = _myClipboard.getContents(this);
		}

		protected void doUpdate()
		{
			//
			_theLayers.fireExtended();

		}

		protected void restoreOldData()
		{
			// put the old data item back on the clipboard
			// _myClipboard.setContents(_oldData, this);
		}

	}

	// ////////////////////////////////////////////
	//
	// ///////////////////////////////////////////////
	public static class CopyItem extends CutItem
	{
		public CopyItem(Editable[] data, Clipboard clipboard, Layer[] theParent,
				Layers theLayers, Layer[] updateLayer)
		{
			super(data, clipboard, theParent, theLayers, updateLayer);

			super.setText(toString());
		}


		public String toString()
		{
			String res = "Copy ";
			if(_data.length>1)
				res += _data.length + " selected items";
			else
				res += _data[0].getName();
			return res;
		}


		public void undo()
		{
			// trigger the refresh
			doUpdate();
		}

		public void execute()
		{

			// store the old data
			storeOld();

			// we stick a pointer to the ACTUAL item on the clipboard - we
			// clone this item when we do a PASTE, so that multiple paste
			// operations can be performed

			// copy in the new data
			EditableTransfer transfer = EditableTransfer.getInstance();
			_myClipboard.setContents(new Object[] { _data }, new Transfer[] { transfer });
		}
	}

}
