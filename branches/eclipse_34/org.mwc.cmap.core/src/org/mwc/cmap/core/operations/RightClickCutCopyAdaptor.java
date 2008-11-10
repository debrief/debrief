package org.mwc.cmap.core.operations;

import java.io.*;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.*;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.*;
import org.eclipse.swt.dnd.*;
import org.eclipse.ui.*;
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
		// do we have any editables?
		if (editables.length == 0)
			return;

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
			// if (updateLayer instanceof MWC.GUI.Layers)
			// {
			// // create the Actions
			// // cutter = new CutLayer(data, _clipboard, updateLayer, theLayers,
			// // updateLayer);
			// }
			// else if (updateLayer == null)
			// {
			// // create the Actions
			// // cutter = new CutLayer(data, _clipboard, (Layer) data, theLayers,
			// // updateLayer);
			// }
			// else
			{
				// first the cut action
				cutter = new CutItem(editables, _clipboard, parentLayers, theLayers, updateLayers);

				// now the copy action
				copier = new CopyItem(editables, _clipboard, parentLayers, theLayers,
						updateLayers);

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

		protected Layers _theLayers;
		
		protected Object _oldContents;

		protected Layer[] _updateLayer;

		public CutItem(Editable[] data, Clipboard clipboard, Layer[] theParent,
				Layers theLayers, Layer[] updateLayer)
		{
			// remember parameters
			_data = data;
			_myClipboard = clipboard;
			_theParent = theParent;
			_theLayers = theLayers;
			_updateLayer = updateLayer;

			// formatting
			super.setText("Cut " + toString());
			
			super.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_TOOL_CUT));
			
		}

		// remember what used to be on the clipboard
		protected void rememberPreviousContents()
		{
			// copy in the new data
			EditableTransfer transfer = EditableTransfer.getInstance();
			_oldContents =  _myClipboard.getContents(transfer);
		}
		

		// restore the previous contents of the clipboard
		protected void restorePreviousContents()
		{
			// copy in the new data
			EditableTransfer transfer = EditableTransfer.getInstance();
			_myClipboard.setContents(new Object[] { _oldContents }, new Transfer[] { transfer });					
			
			// and forget what we're holding
			_oldContents = null;
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
					doCut();
					return Status.OK_STATUS;
				}

			
				public IStatus redo(IProgressMonitor monitor, IAdaptable info)
						throws ExecutionException
				{
					doCut();
					return Status.OK_STATUS;
				}

				public IStatus undo(IProgressMonitor monitor, IAdaptable info)
						throws ExecutionException
				{
					// ok, place our items back in their layers

					boolean multipleLayersModified = false;
					Layer lastLayerModified = null;

					for (int i = 0; i < _data.length; i++)
					{
						Editable thisE = _data[i];
						Layer parentLayer = _theParent[i];

						// is the parent the data object itself?
						if (parentLayer == thisE)
						{
							// no, it must be the top layers object
							_theLayers.addThisLayer((Layer) thisE);

							// so, we know we've got to remove items from multiple layers
							multipleLayersModified = true;
						}
						else
						{
							// remove the new data from it's parent
							parentLayer.add(thisE);

							// let's see if we're editing multiple layers
							if (!multipleLayersModified)
							{
								if (lastLayerModified == null)
									lastLayerModified = parentLayer;
								else
								{
									if (lastLayerModified != parentLayer)
										multipleLayersModified = true;
								}
							}
						}

					}
					
					// and fire an update
					if (multipleLayersModified)
						_theLayers.fireExtended();
					else
						_theLayers.fireExtended(null, lastLayerModified);
					
					// and restore the previous contents
					restorePreviousContents();

					return Status.OK_STATUS;
				}
				
				/** the cut operation is common for execute and redo operations - so factor it out to here...
				 * 
				 */
				private void doCut()
				{
					boolean multipleLayersModified = false;
					Layer lastLayerModified = null;

					// remember the previous contents
					rememberPreviousContents();
					
					// copy in the new data
					EditableTransfer transfer = EditableTransfer.getInstance();
					_myClipboard.setContents(new Object[] { _data }, new Transfer[] { transfer });

					for (int i = 0; i < _data.length; i++)
					{
						Editable thisE = _data[i];
						Layer parentLayer = _theParent[i];

						// is the parent the data object itself?
						if (parentLayer == thisE)
						{
							// no, it must be the top layers object
							_theLayers.removeThisLayer((Layer) thisE);

							// so, we know we've got to remove items from multiple layers
							multipleLayersModified = true;
						}
						else
						{
							// remove the new data from it's parent
							parentLayer.removeElement(thisE);

							// let's see if we're editing multiple layers
							if (!multipleLayersModified)
							{
								if (lastLayerModified == null)
									lastLayerModified = parentLayer;
								else
								{
									if (lastLayerModified != parentLayer)
										multipleLayersModified = true;
								}
							}
						}
					}

					if (multipleLayersModified)
						_theLayers.fireExtended();
					else
						_theLayers.fireExtended(null, lastLayerModified);
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
				res += _data.length + " selected items";
			else
				res += _data[0].getName();
			return res;
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
			super.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_TOOL_COPY));
			
			
		}

		public String toString()
		{
			String res = "Copy ";
			if (_data.length > 1)
				res += _data.length + " selected items";
			else
				res += _data[0].getName();
			return res;
		}

		
		public void run()
		{
			AbstractOperation myOperation = new AbstractOperation(getText())
			{
				public IStatus execute(IProgressMonitor monitor, IAdaptable info)
						throws ExecutionException
				{
					
					// we stick a pointer to the ACTUAL item on the clipboard - we
					// clone this item when we do a PASTE, so that multiple paste
					// operations can be performed
					
					doCopy();

					return Status.OK_STATUS;
				}

				public IStatus redo(IProgressMonitor monitor, IAdaptable info)
						throws ExecutionException
				{
					
					doCopy();

					return Status.OK_STATUS;
				}



				public IStatus undo(IProgressMonitor monitor, IAdaptable info)
						throws ExecutionException
				{
					// just restore the previous clipboard contents
					restorePreviousContents();

					return Status.OK_STATUS;
				}
				
				/** the Copy bit is common to execute and redo methods - so factor it out to here...
				 * 
				 */
				private void doCopy()
				{
					// remember the old contents
					rememberPreviousContents();
					
					// we stick a pointer to the ACTUAL item on the clipboard - we
					// clone this item when we do a PASTE, so that multiple paste
					// operations can be performed
					
					// copy in the new data
					EditableTransfer transfer = EditableTransfer.getInstance();
					_myClipboard.setContents(new Object[] { _data }, new Transfer[] { transfer });
				}				
			};
			// put in the global context, for some reason
			myOperation.addContext(CorePlugin.CMAP_CONTEXT);
			CorePlugin.run(myOperation);			
		}

		public void execute()
		{

			// store the old data
//			storeOld();

			// we stick a pointer to the ACTUAL item on the clipboard - we
			// clone this item when we do a PASTE, so that multiple paste
			// operations can be performed

			// copy in the new data
			EditableTransfer transfer = EditableTransfer.getInstance();
			_myClipboard.setContents(new Object[] { _data }, new Transfer[] { transfer });
		}
	}

}
