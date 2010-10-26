package org.mwc.cmap.core.operations;

import java.io.*;
import java.util.Enumeration;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.runtime.*;
import org.eclipse.jface.action.*;
import org.eclipse.swt.dnd.*;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.*;
import org.mwc.cmap.core.CorePlugin;

import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.SensorContactWrapper;
import Debrief.Wrappers.SensorWrapper;
import Debrief.Wrappers.TMAContactWrapper;
import Debrief.Wrappers.TMAWrapper;
import Debrief.Wrappers.TrackWrapper;
import MWC.GUI.*;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldVector;
import MWC.TacticalData.Fix;

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
					CorePlugin.logError(Status.ERROR,
							"Whilst converting from native to java", e);
				}
				return myData;
			}

			return null;
		}

		protected String[] getTypeNames()
		{
			return new String[]
			{ MYTYPENAME };
		}

		protected int[] getTypeIds()
		{
			return new int[]
			{ MYTYPEID };
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
	static public void getDropdownListFor(IMenuManager manager,
			Editable[] editables, Layer[] updateLayers, Layer[] parentLayers,
			Layers theLayers, Clipboard _clipboard)
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

			// first the cut action
			cutter = new CutItem(editables, _clipboard, parentLayers, theLayers,
					updateLayers);

			// now the copy action
			// hey, it it cloneable?
			// if (editables[0] instanceof Cloneable)
			// {
			copier = new CopyItem(editables, _clipboard, parentLayers, theLayers,
					updateLayers);
			// }

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

			// and the icon
			setImageIcon();

		}

		protected void setImageIcon()
		{
			super.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages()
					.getImageDescriptor(ISharedImages.IMG_TOOL_CUT));
		}

		// remember what used to be on the clipboard
		protected void rememberPreviousContents()
		{
			// copy in the new data
			EditableTransfer transfer = EditableTransfer.getInstance();
			_oldContents = _myClipboard.getContents(transfer);
		}

		// restore the previous contents of the clipboard
		protected void restorePreviousContents()
		{
			// just check that there were some previous contents
			if (_oldContents != null)
			{
				// copy in the new data
				EditableTransfer transfer = EditableTransfer.getInstance();
				_myClipboard.setContents(new Object[]
				{ _oldContents }, new Transfer[]
				{ transfer });
			}
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
						if (parentLayer == null)
						{
							// no, it must be the top layers object
							_theLayers.addThisLayer((Layer) thisE);

							// so, we know we've got to remove items from multiple layers
							multipleLayersModified = true;
						}
						else
						{
							// replace the data it's parent
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

				/**
				 * the cut operation is common for execute and redo operations - so
				 * factor it out to here...
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
					_myClipboard.setContents(new Object[]
					{ _data }, new Transfer[]
					{ transfer });

					for (int i = 0; i < _data.length; i++)
					{
						Editable thisE = _data[i];
						Layer parentLayer = _theParent[i];

						// is the parent the data object itself?
						if (parentLayer == null)
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
		private final String _originalName;

		public CopyItem(Editable[] data, Clipboard clipboard, Layer[] theParent,
				Layers theLayers, Layer[] updateLayer)
		{
			super(data, clipboard, theParent, theLayers, updateLayer);

			_originalName = data[0].getName();

			super.setText(toString());
			super.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages()
					.getImageDescriptor(ISharedImages.IMG_TOOL_COPY));

		}

		public String toString()
		{
			String res = "Copy ";
			if (_data.length > 1)
				res += _data.length + " selected items";
			else
				res += _originalName;
			return res;
		}

		public void run()
		{
			AbstractOperation myOperation = new AbstractOperation(getText())
			{
				public IStatus execute(IProgressMonitor monitor, IAdaptable info)
						throws ExecutionException
				{

					// we stick a CLONE on the clipboard - we
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

				/**
				 * the Copy bit is common to execute and redo methods - so factor it out
				 * to here...
				 * 
				 */
				private void doCopy()
				{
					// remember the old contents
					rememberPreviousContents();

					_data = cloneThese(_data);

					// we stick a pointer to the ACTUAL item on the clipboard - we
					// clone this item when we do a PASTE, so that multiple paste
					// operations can be performed

					// copy in the new data
					EditableTransfer transfer = EditableTransfer.getInstance();
					_myClipboard.setContents(new Object[]
					{ _data }, new Transfer[]
					{ transfer });
				}
			};
			// put in the global context, for some reason
			myOperation.addContext(CorePlugin.CMAP_CONTEXT);
			CorePlugin.run(myOperation);
		}

		public void execute()
		{

			// store the old data
			// storeOld();

			// we stick a pointer to the ACTUAL item on the clipboard - we
			// clone this item when we do a PASTE, so that multiple paste
			// operations can be performed

			// copy in the new data
			EditableTransfer transfer = EditableTransfer.getInstance();
			_myClipboard.setContents(new Object[]
			{ _data }, new Transfer[]
			{ transfer });
		}
	}

	/**
	 * create duplicates of this series of items
	 */
	static public Editable[] cloneThese(Editable[] items)
	{
		Editable[] res = new Editable[items.length];
		for (int i = 0; i < items.length; i++)
		{
			Editable thisOne = items[i];
			Editable clonedItem = cloneThis(thisOne);
			res[i] = clonedItem;

			// see if we can rename it
			if (clonedItem instanceof Layer)
			{
				Layer thisL = (Layer) clonedItem;
				thisL.setName("Copy of " + clonedItem.getName());
			}
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

	// ////////////////////////////////////////////////////////////////////////////////////////////////
	// testing for this class
	// ////////////////////////////////////////////////////////////////////////////////////////////////
	static public final class testCutPaste extends junit.framework.TestCase
	{

		private void doUndo()
		{
			IOperationHistory history = CorePlugin.getHistory();
			try
			{
				history.undo(CorePlugin.CMAP_CONTEXT, null, null);
			}
			catch (ExecutionException e)
			{
				CorePlugin.logError(Status.ERROR, "Problem with undo for test", e);
				assertTrue("threw assertion", e == null);
			}
		}

		private boolean isPositionThere(final TrackWrapper tw, final FixWrapper fw2)
		{
			boolean itemFound;
			Enumeration<Editable> enumer = tw.getPositions();
			itemFound = false;
			while (enumer.hasMoreElements())
			{
				Editable ee = enumer.nextElement();
				if (ee.equals(fw2))
				{
					itemFound = true;
					continue;
				}
			}
			return itemFound;
		}

		private boolean isSensorThere(final TrackWrapper tw,
				final SensorContactWrapper scwa1)
		{
			boolean itemFound;
			Enumeration<SensorWrapper> enumer = tw.getSensors();
			itemFound = false;
			while (enumer.hasMoreElements())
			{
				SensorWrapper ee = enumer.nextElement();
				Enumeration<Editable> contacts = ee.elements();
				while (contacts.hasMoreElements())
				{
					Editable thisC = contacts.nextElement();
					if (thisC.equals(scwa1))
					{
						itemFound = true;
						continue;
					}
				}
			}
			return itemFound;
		}

		private boolean isContactThere(final TrackWrapper tw,
				final TMAContactWrapper scwa1)
		{
			boolean itemFound;
			Enumeration<TMAWrapper> enumer = tw.getSolutions();
			itemFound = false;
			while (enumer.hasMoreElements())
			{
				TMAWrapper ee = enumer.nextElement();
				Enumeration<Editable> contacts = ee.elements();
				while (contacts.hasMoreElements())
				{
					Editable thisC = contacts.nextElement();
					if (thisC.equals(scwa1))
					{
						itemFound = true;
						continue;
					}
				}
			}
			return itemFound;
		}
		
		private static class MyCutItem extends CutItem
		{

			public MyCutItem(Editable[] data, Clipboard clipboard, Layer[] theParent,
					Layers theLayers, Layer[] updateLayer)
			{
				super(data, clipboard, theParent, theLayers, updateLayer);
			}

			@Override
			protected void setImageIcon()
			{
				// don't bother, we haven't got enough platform running
			}

			
			
		}

		public void testCut()
		{
			// create the data
			final TrackWrapper tw = new TrackWrapper();

			final WorldLocation loc_1 = new WorldLocation(0, 0, 0);
			final FixWrapper fw1 = new FixWrapper(new Fix(new HiResDate(100, 10000),
					loc_1.add(new WorldVector(33, new WorldDistance(100,
							WorldDistance.METRES), null)), 10, 110));
			fw1.setLabel("fw1");
			final FixWrapper fw2 = new FixWrapper(new Fix(new HiResDate(200, 20000),
					loc_1.add(new WorldVector(33, new WorldDistance(200,
							WorldDistance.METRES), null)), 20, 120));
			fw2.setLabel("fw2");
			final FixWrapper fw3 = new FixWrapper(new Fix(new HiResDate(300, 30000),
					loc_1.add(new WorldVector(33, new WorldDistance(300,
							WorldDistance.METRES), null)), 30, 130));
			fw3.setLabel("fw3");
			final FixWrapper fw4 = new FixWrapper(new Fix(new HiResDate(400, 40000),
					loc_1.add(new WorldVector(33, new WorldDistance(400,
							WorldDistance.METRES), null)), 40, 140));
			fw4.setLabel("fw4");
			final FixWrapper fw5 = new FixWrapper(new Fix(new HiResDate(500, 50000),
					loc_1.add(new WorldVector(33, new WorldDistance(500,
							WorldDistance.METRES), null)), 50, 150));
			fw5.setLabel("fw5");
			tw.addFix(fw1);
			tw.addFix(fw2);
			tw.addFix(fw3);
			tw.addFix(fw4);
			tw.addFix(fw5);
			// also give it some sensor data
			SensorWrapper swa = new SensorWrapper("title one");
			SensorContactWrapper scwa1 = new SensorContactWrapper("aaa",
					new HiResDate(150, 0), null, 0, null, null, null, 0, null);
			SensorContactWrapper scwa2 = new SensorContactWrapper("bbb",
					new HiResDate(180, 0), null, 0, null, null, null, 0, null);
			SensorContactWrapper scwa3 = new SensorContactWrapper("ccc",
					new HiResDate(250, 0), null, 0, null, null, null, 0, null);
			swa.add(scwa1);
			swa.add(scwa2);
			swa.add(scwa3);
			tw.add(swa);
			SensorWrapper sw = new SensorWrapper("title two");
			SensorContactWrapper scw1 = new SensorContactWrapper("ddd",
					new HiResDate(260, 0), null, 0, null, null, null, 0, null);
			SensorContactWrapper scw2 = new SensorContactWrapper("eee",
					new HiResDate(280, 0), null, 0, null, null, null, 0, null);
			SensorContactWrapper scw3 = new SensorContactWrapper("fff",
					new HiResDate(350, 0), null, 0, null, null, null, 0, null);
			sw.add(scw1);
			sw.add(scw2);
			sw.add(scw3);
			tw.add(sw);

			TMAWrapper mwa = new TMAWrapper("bb");
			TMAContactWrapper tcwa1 = new TMAContactWrapper("aaa", "bbb",
					new HiResDate(130), null, 0, 0, 0, null, null, null, null);
			TMAContactWrapper tcwa2 = new TMAContactWrapper("bbb", "bbb",
					new HiResDate(190), null, 0, 0, 0, null, null, null, null);
			TMAContactWrapper tcwa3 = new TMAContactWrapper("ccc", "bbb",
					new HiResDate(230), null, 0, 0, 0, null, null, null, null);
			mwa.add(tcwa1);
			mwa.add(tcwa2);
			mwa.add(tcwa3);
			tw.add(mwa);
			TMAWrapper mw = new TMAWrapper("cc");
			TMAContactWrapper tcw1 = new TMAContactWrapper("ddd", "bbb",
					new HiResDate(230), null, 0, 0, 0, null, null, null, null);
			TMAContactWrapper tcw2 = new TMAContactWrapper("eee", "bbb",
					new HiResDate(330), null, 0, 0, 0, null, null, null, null);
			TMAContactWrapper tcw3 = new TMAContactWrapper("fff", "bbb",
					new HiResDate(390), null, 0, 0, 0, null, null, null, null);
			mw.add(tcw1);
			mw.add(tcw2);
			mw.add(tcw3);
			tw.add(mw);

			// now fiddle with it
			Layers updateLayers = new Layers();
			updateLayers.addThisLayer(tw);
			final Clipboard clipboard = new Clipboard(Display.getDefault());
			Layer[] parentLayer = new Layer[]
			{ tw };
			CutItem ci = new MyCutItem(new Editable[]
			{ fw2 }, clipboard, parentLayer, updateLayers, parentLayer);
			// check our item's in there
			assertTrue("item there before op", isPositionThere(tw, fw2));
			assertTrue("item there before op", isPositionThere(tw, fw3));

			// now do the cut
			ci.run();
			assertFalse("item gone after op", isPositionThere(tw, fw2));
			assertTrue("item there after op", isPositionThere(tw, fw3));

			doUndo();
			assertTrue("item back again after op", isPositionThere(tw, fw2));

			// now let's try two items
			parentLayer = new Layer[]
			{ tw, tw };
			CutItem c2 = new MyCutItem(new Editable[]
			{ fw2, fw4 }, clipboard, parentLayer, updateLayers, parentLayer);
			assertTrue("item there before op", isPositionThere(tw, fw2));
			assertTrue("item there before op", isPositionThere(tw, fw3));
			assertTrue("item there before op", isPositionThere(tw, fw4));
			// now do the cut
			c2.run();
			assertFalse("item gone after op", isPositionThere(tw, fw2));
			assertTrue("item there after op", isPositionThere(tw, fw3));
			assertFalse("item gone after op", isPositionThere(tw, fw4));

			doUndo();
			assertTrue("item back again after op", isPositionThere(tw, fw2));
			assertTrue("item still there after op", isPositionThere(tw, fw3));
			assertTrue("item back again after op", isPositionThere(tw, fw4));

			// right, now let's try to delete a sensor item
			parentLayer = new Layer[]
			{ swa };
			CutItem c3 = new MyCutItem(new Editable[]
			{ scwa1 }, clipboard, parentLayer, updateLayers, parentLayer);
			assertTrue("item there before op", isSensorThere(tw, scwa1));
			assertTrue("item there before op", isSensorThere(tw, scwa2));
			assertTrue("item there before op", isSensorThere(tw, scwa3));
			c3.run();
			assertFalse("item not there after op", isSensorThere(tw, scwa1));
			assertTrue("item there after op", isSensorThere(tw, scwa2));
			assertTrue("item there after op", isSensorThere(tw, scwa3));
			doUndo();
			assertTrue("item back again after op", isSensorThere(tw, scwa1));
			assertTrue("item back again after op", isSensorThere(tw, scwa2));
			assertTrue("item back again after op", isSensorThere(tw, scwa3));
			// now let's try two items
			parentLayer = new Layer[]
			{ swa, swa };
			c3 = new MyCutItem(new Editable[]
			{ scwa1, scwa2 }, clipboard, parentLayer, updateLayers, parentLayer);
			assertTrue("item there before op", isSensorThere(tw, scwa1));
			assertTrue("item there before op", isSensorThere(tw, scwa2));
			assertTrue("item there before op", isSensorThere(tw, scwa3));
			c3.run();
			assertFalse("item not there after op", isSensorThere(tw, scwa1));
			assertFalse("item not there after op", isSensorThere(tw, scwa2));
			assertTrue("item there after op", isSensorThere(tw, scwa3));
			doUndo();
			assertTrue("item back again after op", isSensorThere(tw, scwa1));
			assertTrue("item back again after op", isSensorThere(tw, scwa2));
			assertTrue("item back again after op", isSensorThere(tw, scwa3));
			// now let's try two items in different layers
			parentLayer = new Layer[]
			{ swa, sw };
			c3 = new MyCutItem(new Editable[]
			{ scwa1, scw2 }, clipboard, parentLayer, updateLayers, parentLayer);
			assertTrue("item there before op", isSensorThere(tw, scwa1));
			assertTrue("item there before op", isSensorThere(tw, scw2));
			assertTrue("item there before op", isSensorThere(tw, scwa3));
			c3.run();
			assertFalse("item not there after op", isSensorThere(tw, scwa1));
			assertFalse("item not there after op", isSensorThere(tw, scw2));
			assertTrue("item there after op", isSensorThere(tw, scwa3));
			doUndo();
			assertTrue("item back again after op", isSensorThere(tw, scwa1));
			assertTrue("item back again after op", isSensorThere(tw, scw2));
			assertTrue("item back again after op", isSensorThere(tw, scwa3));

			// //////////////////////////
			// now for TMA!

			// right, now let's try to delete a sensor item
			parentLayer = new Layer[]
			{ mwa };
			c3 = new MyCutItem(new Editable[]
			{ tcwa1 }, clipboard, parentLayer, updateLayers, parentLayer);
			assertTrue("item there before op", isContactThere(tw, tcwa1));
			assertTrue("item there before op", isContactThere(tw, tcwa2));
			assertTrue("item there before op", isContactThere(tw, tcwa3));
			c3.run();
			assertFalse("item not there after op", isContactThere(tw, tcwa1));
			assertTrue("item there after op", isContactThere(tw, tcwa2));
			assertTrue("item there after op", isContactThere(tw, tcwa3));
			doUndo();
			assertTrue("item back again after op", isContactThere(tw, tcwa1));
			assertTrue("item back again after op", isContactThere(tw, tcwa2));
			assertTrue("item back again after op", isContactThere(tw, tcwa3));
			// now let's try two items
			parentLayer = new Layer[]
			{ mwa, mwa };
			c3 = new MyCutItem(new Editable[]
			{ tcwa1, tcwa2 }, clipboard, parentLayer, updateLayers, parentLayer);
			assertTrue("item there before op", isContactThere(tw, tcwa1));
			assertTrue("item there before op", isContactThere(tw, tcwa2));
			assertTrue("item there before op", isContactThere(tw, tcwa3));
			c3.run();
			assertFalse("item not there after op", isContactThere(tw, tcwa1));
			assertFalse("item not there after op", isContactThere(tw, tcwa2));
			assertTrue("item there after op", isContactThere(tw, tcwa3));
			doUndo();
			assertTrue("item back again after op", isContactThere(tw, tcwa1));
			assertTrue("item back again after op", isContactThere(tw, tcwa2));
			assertTrue("item back again after op", isContactThere(tw, tcwa3));
			// now let's try two items in different layers
			parentLayer = new Layer[]
			{ mwa, mw };
			c3 = new MyCutItem(new Editable[]
			{ tcwa1, tcw2 }, clipboard, parentLayer, updateLayers, parentLayer);
			assertTrue("item there before op", isContactThere(tw, tcwa1));
			assertTrue("item there before op", isContactThere(tw, tcw2));
			assertTrue("item there before op", isContactThere(tw, tcwa3));
			c3.run();
			assertFalse("item not there after op", isContactThere(tw, tcwa1));
			assertFalse("item not there after op", isContactThere(tw, tcw2));
			assertTrue("item there after op", isContactThere(tw, tcwa3));
			doUndo();
			assertTrue("item back again after op", isContactThere(tw, tcwa1));
			assertTrue("item back again after op", isContactThere(tw, tcw2));
			assertTrue("item back again after op", isContactThere(tw, tcwa3));

		}

	}
}
