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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.swt.dnd.ByteArrayTransfer;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.ui_support.OutlineNameSorter;

import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.SensorContactWrapper;
import Debrief.Wrappers.SensorWrapper;
import Debrief.Wrappers.TMAContactWrapper;
import Debrief.Wrappers.TMAWrapper;
import Debrief.Wrappers.TrackWrapper;
import Debrief.Wrappers.Track.DynamicInfillSegment;
import MWC.GUI.Editable;
import MWC.GUI.HasEditables;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.NeedsToBeInformedOfRemove;
import MWC.GUI.Plottable;
import MWC.GUI.PlottablesType;
import MWC.GUI.Tools.Operations.RightClickCutCopyAdaptor.IsTransientForChildren;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldVector;
import MWC.TacticalData.Fix;
import MWC.Utilities.Errors.Trace;

public class RightClickCutCopyAdaptor {

	// ////////////////////////////////////////////
	//
	// ///////////////////////////////////////////////
	public static class CopyItem extends CutItem {
		public CopyItem(final Editable[] data, final Clipboard clipboard, final HasEditables[] theParent,
				final Layers theLayers, final Layer[] updateLayer) {
			super(data, clipboard, theParent, theLayers, updateLayer);

			super.setText(toString());
			setActionDefinitionId(ActionFactory.COPY.getCommandId());
			super.setImageDescriptor(
					PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_TOOL_COPY));

		}

		public void execute() {

			// store the old data
			// storeOld();

			// we stick a pointer to the ACTUAL item on the clipboard - we
			// clone this item when we do a PASTE, so that multiple paste
			// operations can be performed

			// copy in the new data
			final EditableTransfer transfer = EditableTransfer.getInstance();
			_myClipboard.setContents(new Object[] { _data }, new Transfer[] { transfer });
		}

		@Override
		public void run() {
			final AbstractOperation myOperation = new AbstractOperation(getText()) {
				/**
				 * the Copy bit is common to execute and redo methods - so factor it out to
				 * here...
				 *
				 */
				private void doCopy() {
					// remember the old contents
					rememberPreviousContents();

					_data = cloneThese(_data);

					// we stick a pointer to the ACTUAL item on the clipboard - we
					// clone this item when we do a PASTE, so that multiple paste
					// operations can be performed

					// copy in the new data
					final EditableTransfer transfer = EditableTransfer.getInstance();
					_myClipboard.setContents(new Object[] { _data }, new Transfer[] { transfer });
				}

				@Override
				public IStatus execute(final IProgressMonitor monitor, final IAdaptable info)
						throws ExecutionException {

					// we stick a CLONE on the clipboard - we
					// clone this item when we do a PASTE, so that multiple paste
					// operations can be performed

					doCopy();

					return Status.OK_STATUS;
				}

				@Override
				public IStatus redo(final IProgressMonitor monitor, final IAdaptable info) throws ExecutionException {

					doCopy();

					return Status.OK_STATUS;
				}

				@Override
				public IStatus undo(final IProgressMonitor monitor, final IAdaptable info) throws ExecutionException {
					// just restore the previous clipboard contents
					restorePreviousContents();

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
			String res = "Copy ";
			if (_data.length > 1) {
				res += _data.length + " selected items";
			} else {
				res += _data[0].getName();
			}
			return res;
		}
	}

	// /////////////////////////////////
	// member variables
	// ////////////////////////////////

	// /////////////////////////////////
	// constructor
	// ////////////////////////////////

	// ////////////////////////////////////////////
	//
	// ///////////////////////////////////////////////
	public static class CutItem extends Action {
		protected Editable[] _data;

		protected Clipboard _myClipboard;

		protected HasEditables[] _theParent;

		protected Layers _theLayers;

		protected Object _oldContents;

		protected Layer[] _updateLayer;

		private AbstractOperation cutOperation;

		public CutItem(final Editable[] data, final Clipboard clipboard, final HasEditables[] theParent,
				final Layers theLayers, final Layer[] updateLayer) {
			// remember parameters
			_data = data;
			_myClipboard = clipboard;
			_theParent = theParent;
			_theLayers = theLayers;
			_updateLayer = updateLayer;

			// formatting
			super.setText("Cut " + toString());
			setActionDefinitionId(ActionFactory.CUT.getCommandId());
			// and the icon
			setImageIcon();

		}

		// remember what used to be on the clipboard
		protected void rememberPreviousContents() {
			if (_myClipboard != null) {
				// copy in the new data
				final EditableTransfer transfer = EditableTransfer.getInstance();
				_oldContents = _myClipboard.getContents(transfer);
			}
		}

		// restore the previous contents of the clipboard
		protected void restorePreviousContents() {
			if (_myClipboard != null) {
				// just check that there were some previous contents
				if (_oldContents != null) {
					// copy in the new data
					final EditableTransfer transfer = EditableTransfer.getInstance();
					_myClipboard.setContents(new Object[] { _oldContents }, new Transfer[] { transfer });
				}
				// and forget what we're holding
				_oldContents = null;
			}

		}

		/**
		 *
		 */
		@Override
		public void run() {
			cutOperation = new AbstractOperation(getText()) {
				private Plottable adjacentItemFor(final Object parentLayer, final Editable thisE) {
					final Plottable res;
					if (parentLayer instanceof PlottablesType) {
						final PlottablesType segs = (PlottablesType) parentLayer;
						final Enumeration<Editable> numer = segs.elements();
						res = findAdjacentEditable(thisE, numer);
					} else if (parentLayer instanceof HasEditables) {
						final HasEditables segs = (HasEditables) parentLayer;
						final Enumeration<Editable> numer = segs.elements();
						res = findAdjacentEditable(thisE, numer);
					} else {
						System.out.println("failed");
						res = null;
					}
					return res;
				}

				/**
				 * the cut operation is common for execute and redo operations - so factor it
				 * out to here...
				 *
				 */
				private void doCut() {
					final Vector<HasEditables> changedLayers = new Vector<HasEditables>();

					// remember the previous contents
					rememberPreviousContents();

					// copy in the new data
					final EditableTransfer transfer = EditableTransfer.getInstance();
					if (_myClipboard != null) {
						_myClipboard.setContents(new Object[] { _data }, new Transfer[] { transfer });
					}

					Plottable toBeSelected = null;

					for (int i = 0; i < _data.length; i++) {
						final Editable thisE = _data[i];
						final HasEditables parentLayer = _theParent[i];

						// is the parent the data object itself?
						if (parentLayer == null) {
							toBeSelected = adjacentItemFor(_theLayers, thisE);

							// no, it must be the top layers object
							_theLayers.removeThisLayer((Layer) thisE);

							// remember the layer, so we can provide
							// the Outline view with what to select (the previous visible layer)
							changedLayers.add((HasEditables) toBeSelected);
						} else {
							// special handling. On some occasions we wish to select
							// the previous item, if it's in a long list.
							toBeSelected = adjacentItemFor(parentLayer, thisE);

							// remove the new data from it's parent
							parentLayer.removeElement(thisE);

							// some objects want to know when they're removed
							if (thisE instanceof NeedsToBeInformedOfRemove) {
								// spread the good news
								((NeedsToBeInformedOfRemove) thisE).beingRemoved();
							}

							// see if we need to track this layer change
							if (!changedLayers.contains(parentLayer)) {
								changedLayers.add(parentLayer);
							}
						}
					}

					if (changedLayers.size() > 1) {
						_theLayers.fireExtended();
					} else if (changedLayers.size() == 1) {
						_theLayers.fireExtended(toBeSelected, changedLayers.firstElement());
					} else {
						// zero layers listed as changed. no 'firing' necessary
					}
				}

				@Override
				public IStatus execute(final IProgressMonitor monitor, final IAdaptable info)
						throws ExecutionException {
					doCut();
					return Status.OK_STATUS;
				}

				private Plottable findAdjacentEditable(final Editable thisE, final Enumeration<Editable> numer) {
					final Plottable res;

					// put them into a list, so we can sort them properly
					final List<Editable> list = new ArrayList<Editable>();
					while (numer.hasMoreElements()) {
						list.add(numer.nextElement());
					}

					final Comparator<Editable> comparator = new OutlineNameSorter.EditableComparer();

					// ok now sort them out
					Collections.sort(list, comparator);

					// find the item that's about to be deleted
					final int indexOf = list.indexOf(thisE);

					// where is it in the list?
					if (indexOf == -1 || list.size() <= 1) {
						// ok, empty list (or there's only us in the list)
						res = null;
					} else if (indexOf == (list.size() - 1) && list.size() > 1) {
						// last item on the list, can't move down. use previous
						final Plottable previous = (Plottable) list.get(list.size() - 2);

						// special handling for dynamic infill. It will get deleted
						// when it's before/after leg get deleted.
						if (previous instanceof DynamicInfillSegment) {
							final DynamicInfillSegment fill = (DynamicInfillSegment) previous;
							res = fill.getBeforeSegment();
						} else {
							res = previous;
						}
					} else {
						// mid-list, just take next item of the list
						final Plottable next = (Plottable) list.get(indexOf + 1);

						// aah, not if it's an infill that's about to be deleted?
						if (next instanceof DynamicInfillSegment) {
							final DynamicInfillSegment fill = (DynamicInfillSegment) next;
							res = fill.getAfterSegment();
						} else {
							res = next;
						}
					}
					return res;
				}

				@Override
				public IStatus redo(final IProgressMonitor monitor, final IAdaptable info) throws ExecutionException {
					doCut();
					return Status.OK_STATUS;
				}

				@Override
				public IStatus undo(final IProgressMonitor monitor, final IAdaptable info) throws ExecutionException {
					// ok, place our items back in their layers

					boolean multipleLayersModified = false;
					HasEditables lastLayerModified = null;

					Plottable toHighlight = null;

					for (int i = 0; i < _data.length; i++) {
						final Editable thisE = _data[i];
						final HasEditables parentLayer = _theParent[i];

						// if this is the first (or only) item being
						// restored - remember it, so we can highlgiht it
						if (toHighlight == null) {
							toHighlight = (Plottable) thisE;
						}

						// is the parent the data object itself?
						if (parentLayer == null) {
							// no, it must be the top layers object
							_theLayers.addThisLayer((Layer) thisE);

							// so, we know we've got to remove items from multiple layers
							multipleLayersModified = true;
						} else {
							// replace the data it's parent
							parentLayer.add(thisE);

							// let's see if we're editing multiple layers
							if (!multipleLayersModified) {
								if (lastLayerModified == null) {
									lastLayerModified = parentLayer;
								} else {
									if (lastLayerModified != parentLayer) {
										multipleLayersModified = true;
									}
								}
							}
						}

					}

					// and fire an update
					if (multipleLayersModified) {
						_theLayers.fireExtended();
					} else {
						_theLayers.fireExtended(toHighlight, lastLayerModified);
					}

					// and restore the previous contents
					restorePreviousContents();

					return Status.OK_STATUS;
				}

			};
			// put in the global context, for some reason
			if(Platform.isRunning()) {
				if (CorePlugin.getUndoContext() != null) {
					cutOperation.addContext(CorePlugin.getUndoContext());
				}
				CorePlugin.run(cutOperation);
			}
			else {
				try {
					cutOperation.execute(null, null);
				} catch (ExecutionException e) {
					Trace.trace("Cut operation failed", true);
				}
			}

		}

		protected void setImageIcon() {
			if(Platform.isRunning()) {
				super.setImageDescriptor(
						PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_TOOL_CUT));
			}
		}

		@Override
		public String toString() {
			String res = "";
			if (_data.length > 1) {
				res += _data.length + " selected items";
			} else {
				res += _data[0].getName();
			}
			return res;
		}

	}

	// /////////////////////////////////
	// nested classes
	// ////////////////////////////////

	// ////////////////////////////////////////////
	//
	// ///////////////////////////////////////////////
	public static class DeleteItem extends CutItem {

		public DeleteItem(final Editable[] data, final HasEditables[] theParent, final Layers theLayers,
				final Layer[] updateLayer) {
			super(data, null, theParent, theLayers, updateLayer);

			// formatting
			super.setText("Delete " + toString());

			setActionDefinitionId(ActionFactory.DELETE.getCommandId());

			// and the icon
			setImageIcon();

		}

		@Override
		protected void setImageIcon() {
			super.setImageDescriptor(
					PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_TOOL_DELETE));
		}

		@Override
		public String toString() {
			String res = "";
			if (_data.length > 1) {
				res += _data.length + " selected items";
			} else {
				res += _data[0].getName();
			}
			return res;
		}

	}

	/**
	 * embedded class used to convert our Editable objects to/from clipboard format
	 *
	 * @author ian.mayo
	 */
	public final static class EditableTransfer extends ByteArrayTransfer {

		private static final String MYTYPENAME = "CMAP_EDITABLE";

		public static final int MYTYPEID = registerType(MYTYPENAME);

		/**
		 * singleton instance of ourselves
		 */
		private static EditableTransfer _instance;

		/**
		 * accessor, get running.
		 *
		 * @return
		 */
		public static EditableTransfer getInstance() {
			if (_instance == null) {
				_instance = new EditableTransfer();
			}

			return _instance;
		}

		/**
		 * private constructor - so we have to use the 'get instance' method
		 */
		private EditableTransfer() {
		}

		@Override
		protected int[] getTypeIds() {
			return new int[] { MYTYPEID };
		}

		@Override
		protected String[] getTypeNames() {
			return new String[] { MYTYPENAME };
		}

		/**
		 * ok - convert our object ready to put it on the clipboard
		 *
		 * @param object
		 * @param transferData
		 */
		@Override
		public void javaToNative(final Object object, final TransferData transferData) {
			if (object == null || !(object instanceof Editable[])) {
				return;
			}

			if (isSupportedType(transferData)) {
				final Editable[] myItem = (Editable[]) object;
				try {
					final ByteArrayOutputStream out = new ByteArrayOutputStream();
					final ObjectOutputStream writeOut = new ObjectOutputStream(out);
					writeOut.writeObject(myItem);
					final byte[] buffer = out.toByteArray();
					writeOut.close();

					super.javaToNative(buffer, transferData);

				} catch (final IOException e) {
					CorePlugin.logError(IStatus.ERROR, "Problem converting object to clipboard format: " + object, e);
				}
			}
		}

		/**
		 * ok, extract our object from the clipboard
		 *
		 * @param transferData
		 * @return
		 */
		@Override
		public Object nativeToJava(final TransferData transferData) {

			if (isSupportedType(transferData)) {

				final byte[] buffer = (byte[]) super.nativeToJava(transferData);
				if (buffer == null) {
					return null;
				}

				Editable[] myData = null;
				try {
					final ByteArrayInputStream in = new ByteArrayInputStream(buffer);
					final ObjectInputStream readIn = new ObjectInputStream(in);
					myData = (Editable[]) readIn.readObject();
					readIn.close();
				} catch (final IOException ex) {
					CorePlugin.logError(IStatus.ERROR, "Problem converting object to clipboard format", null);
					return null;
				} catch (final ClassNotFoundException e) {
					CorePlugin.logError(IStatus.ERROR,
							"Whilst converting from native to java, can't find this class:" + e.getMessage(), null);
				}
				return myData;
			}

			return null;
		}
	}

	// ////////////////////////////////////////////////////////////////////////////////////////////////
	// testing for this class
	// ////////////////////////////////////////////////////////////////////////////////////////////////
	static public final class testCutPaste extends junit.framework.TestCase {

		private static class MyCutItem extends CutItem {

			public MyCutItem(final Editable[] data, final Clipboard clipboard, final Layer[] theParent,
					final Layers theLayers, final Layer[] updateLayer) {
				super(data, clipboard, theParent, theLayers, updateLayer);
			}

			@Override
			protected void setImageIcon() {
				// don't bother, we haven't got enough platform running
			}

		}

		private static void doUndo() {
			final IOperationHistory history = CorePlugin.getHistory();
			try {
				history.undo(CorePlugin.getUndoContext(), null, null);
			} catch (final ExecutionException e) {
				CorePlugin.logError(IStatus.ERROR, "Problem with undo for test", e);
				assertTrue("threw assertion", e == null);
			}
		}

		private static boolean isContactThere(final TrackWrapper tw, final TMAContactWrapper scwa1) {
			boolean itemFound;
			final Enumeration<Editable> enumer = tw.getSolutions().elements();
			itemFound = false;
			while (enumer.hasMoreElements()) {
				final TMAWrapper ee = (TMAWrapper) enumer.nextElement();
				final Enumeration<Editable> contacts = ee.elements();
				while (contacts.hasMoreElements()) {
					final Editable thisC = contacts.nextElement();
					if (thisC.equals(scwa1)) {
						itemFound = true;
						continue;
					}
				}
			}
			return itemFound;
		}

		private static boolean isPositionThere(final TrackWrapper tw, final FixWrapper fw2) {
			boolean itemFound;
			final Enumeration<Editable> enumer = tw.getPositionIterator();
			itemFound = false;
			while (enumer.hasMoreElements()) {
				final Editable ee = enumer.nextElement();
				if (ee.equals(fw2)) {
					itemFound = true;
					continue;
				}
			}
			return itemFound;
		}

		private static boolean isSensorThere(final TrackWrapper tw, final SensorContactWrapper scwa1) {
			boolean itemFound;
			final Enumeration<Editable> enumer = tw.getSensors().elements();
			itemFound = false;
			while (enumer.hasMoreElements()) {
				final SensorWrapper ee = (SensorWrapper) enumer.nextElement();
				final Enumeration<Editable> contacts = ee.elements();
				while (contacts.hasMoreElements()) {
					final Editable thisC = contacts.nextElement();
					if (thisC.equals(scwa1)) {
						itemFound = true;
						continue;
					}
				}
			}
			return itemFound;
		}

		private Clipboard clipboard;
		private Layers updateLayers;
		private FixWrapper fw2,fw1,fw3,fw4,fw5;
		private TrackWrapper tw;
		private SensorWrapper swa,sw;
		private SensorContactWrapper scwa1,scwa2,scwa3,scw1,scw2,scw3;
		private TMAWrapper mwa,mw;
		private TMAContactWrapper tcwa1,tcwa2,tcwa3,tcw1,tcw2,tcw3;
		public void setUp() {
			tw = new TrackWrapper();

			final WorldLocation loc_1 = new WorldLocation(0, 0, 0);
			fw1 = new FixWrapper(new Fix(new HiResDate(100, 10000),
					loc_1.add(new WorldVector(33, new WorldDistance(100, WorldDistance.METRES), null)), 10, 110));
			fw1.setLabel("fw1");
			fw2 = new FixWrapper(new Fix(new HiResDate(200, 20000),
					loc_1.add(new WorldVector(33, new WorldDistance(200, WorldDistance.METRES), null)), 20, 120));
			fw2.setLabel("fw2");
			fw3 = new FixWrapper(new Fix(new HiResDate(300, 30000),
					loc_1.add(new WorldVector(33, new WorldDistance(300, WorldDistance.METRES), null)), 30, 130));
			fw3.setLabel("fw3");
			fw4 = new FixWrapper(new Fix(new HiResDate(400, 40000),
					loc_1.add(new WorldVector(33, new WorldDistance(400, WorldDistance.METRES), null)), 40, 140));
			fw4.setLabel("fw4");
			fw5 = new FixWrapper(new Fix(new HiResDate(500, 50000),
					loc_1.add(new WorldVector(33, new WorldDistance(500, WorldDistance.METRES), null)), 50, 150));
			fw5.setLabel("fw5");
			tw.addFix(fw1);
			tw.addFix(fw2);
			tw.addFix(fw3);
			tw.addFix(fw4);
			tw.addFix(fw5);
			// also give it some sensor data
			swa = new SensorWrapper("title one");
			scwa1 = new SensorContactWrapper("aaa", new HiResDate(150, 0), null, null, null,
					null, null, 0, null);
			scwa2 = new SensorContactWrapper("bbb", new HiResDate(180, 0), null, null, null,
					null, null, 0, null);
			scwa3 = new SensorContactWrapper("ccc", new HiResDate(250, 0), null, null, null,
					null, null, 0, null);
			swa.add(scwa1);
			swa.add(scwa2);
			swa.add(scwa3);
			tw.add(swa);
			sw = new SensorWrapper("title two");
			scw1 = new SensorContactWrapper("ddd", new HiResDate(260, 0), null, null, null,
					null, null, 0, null);
			scw2 = new SensorContactWrapper("eee", new HiResDate(280, 0), null, null, null,
					null, null, 0, null);
			scw3 = new SensorContactWrapper("fff", new HiResDate(350, 0), null, null, null,
					null, null, 0, null);
			sw.add(scw1);
			sw.add(scw2);
			sw.add(scw3);
			tw.add(sw);

			mwa = new TMAWrapper("bb");
			tcwa1 = new TMAContactWrapper("aaa", "bbb", new HiResDate(130), null, 0, 0, 0, null,
					null, null, null);
			tcwa2 = new TMAContactWrapper("bbb", "bbb", new HiResDate(190), null, 0, 0, 0, null,
					null, null, null);
			tcwa3 = new TMAContactWrapper("ccc", "bbb", new HiResDate(230), null, 0, 0, 0, null,
					null, null, null);
			mwa.add(tcwa1);
			mwa.add(tcwa2);
			mwa.add(tcwa3);
			tw.add(mwa);
			mw = new TMAWrapper("cc");
			tcw1 = new TMAContactWrapper("ddd", "bbb", new HiResDate(230), null, 0, 0, 0, null,
					null, null, null);
			tcw2 = new TMAContactWrapper("eee", "bbb", new HiResDate(330), null, 0, 0, 0, null,
					null, null, null);
			tcw3 = new TMAContactWrapper("fff", "bbb", new HiResDate(390), null, 0, 0, 0, null,
					null, null, null);
			mw.add(tcw1);
			mw.add(tcw2);
			mw.add(tcw3);
			tw.add(mw);

			// now fiddle with it
			updateLayers = new Layers();
			updateLayers.addThisLayer(tw);
//			if(!Platform.isRunning()) {
//				clipboard = new Clipboard(Display.getDefault());
//			}
//			else {
//				Display.getDefault().asyncExec(new Runnable() {
//					
//					@Override
//					public void run() {
//						clipboard = new Clipboard(Display.getDefault());
//						
//					}
//				});
//			}

		}

		public void testCutOneItem() {
			clipboard = new Clipboard(Display.getDefault());
			Layer[] parentLayer = new Layer[] { tw };
			final CutItem ci = new MyCutItem(new Editable[] { fw2 }, clipboard, parentLayer, updateLayers, parentLayer);
			// check our item's in there
			assertTrue("item there before op", isPositionThere(tw, fw2));
			assertTrue("item there before op", isPositionThere(tw, fw3));

			// now do the cut
			ci.run();
			assertFalse("item gone after op", isPositionThere(tw, fw2));
			assertTrue("item there after op", isPositionThere(tw, fw3));
		}
		public void testCutTwoItems() {
			clipboard = new Clipboard(Display.getDefault());
			Layer[] parentLayer = new Layer[] { tw, tw };
			final CutItem c2 = new MyCutItem(new Editable[] { fw2, fw4 }, clipboard, parentLayer, updateLayers,
					parentLayer);
			assertTrue("item there before op", isPositionThere(tw, fw2));
			assertTrue("item there before op", isPositionThere(tw, fw3));
			assertTrue("item there before op", isPositionThere(tw, fw4));
			// now do the cut
			c2.run();
			assertFalse("item gone after op", isPositionThere(tw, fw2));
			assertTrue("item there after op", isPositionThere(tw, fw3));
			assertFalse("item gone after op", isPositionThere(tw, fw4));

		}
		public void testCutSensorWrapper() {
			clipboard = new Clipboard(Display.getDefault());
			CutItem c3 = new CutItem(new Editable[] { scwa1 }, clipboard, new HasEditables[] {swa}, updateLayers, new TrackWrapper[] {tw});
			assertTrue("item there before op", isSensorThere(tw, scwa1));
			assertTrue("item there before op", isSensorThere(tw, scwa2));
			assertTrue("item there before op", isSensorThere(tw, scwa3));
			c3.run();
			assertFalse("item not there after op", isSensorThere(tw, scwa1));
			assertTrue("item there after op", isSensorThere(tw, scwa2));
			assertTrue("item there after op", isSensorThere(tw, scwa3));
		}



		public void testCutOneContactWrapper() {
			clipboard = new Clipboard(Display.getDefault());
			CutItem c3 = new CutItem(new Editable[] { tcwa1 }, clipboard, new HasEditables[] {mwa}, updateLayers, new TrackWrapper[] {tw});
			assertTrue("item there before op", isContactThere(tw, tcwa1));
			assertTrue("item there before op", isContactThere(tw, tcwa2));
			assertTrue("item there before op", isContactThere(tw, tcwa3));
			c3.run();
			assertFalse("item not there after op", isContactThere(tw, tcwa1));
			assertTrue("item there after op", isContactThere(tw, tcwa2));
			assertTrue("item there after op", isContactThere(tw, tcwa3));
		}
		public void testCutTwoContactWrappers() {
			clipboard = new Clipboard(Display.getDefault());
			CutItem c3 = new CutItem(new Editable[] { tcwa1, tcwa2 }, clipboard, new HasEditables[] {mwa,mwa}, updateLayers, new TrackWrapper[] {tw});
			assertTrue("item there before op", isContactThere(tw, tcwa1));
			assertTrue("item there before op", isContactThere(tw, tcwa2));
			assertTrue("item there before op", isContactThere(tw, tcwa3));
			c3.run();
			assertFalse("item not there after op", isContactThere(tw, tcwa1));
			assertFalse("item not there after op", isContactThere(tw, tcwa2));
			assertTrue("item there after op", isContactThere(tw, tcwa3));
		}
		public void testCutDiffLayersContacts() {
			clipboard = new Clipboard(Display.getDefault());
			CutItem c3 = new CutItem(new Editable[] { tcwa1, tcw2 }, clipboard, new HasEditables[] {mwa,mw}, updateLayers, new TrackWrapper[] {tw});
			assertTrue("item there before op", isContactThere(tw, tcwa1));
			assertTrue("item there before op", isContactThere(tw, tcwa2));
			assertTrue("item there before op", isContactThere(tw, tcwa3));
			c3.run();
			assertFalse("item not there after op", isContactThere(tw, tcwa1));
			assertFalse("item not there after op", isContactThere(tw, tcw2));
			assertTrue("item there after op", isContactThere(tw, tcwa3));
		}
		public void testCutDiffSensors() {
			clipboard = new Clipboard(Display.getDefault());
			CutItem c3 = new CutItem(new Editable[] { scwa1, scw2 }, clipboard, new HasEditables[] {swa,sw}, updateLayers, new TrackWrapper[] {tw});
			assertTrue("item there before op", isSensorThere(tw, scwa1));
			assertTrue("item there before op", isSensorThere(tw, scw2));
			assertTrue("item there before op", isSensorThere(tw, scwa3));
			c3.run();
			assertFalse("item not there after op", isSensorThere(tw, scwa1));
			assertFalse("item not there after op", isSensorThere(tw, scw2));
			assertTrue("item there after op", isSensorThere(tw, scwa3));
		}



	}

	/**
	 * create duplicates of this series of items
	 */
	static public Editable[] cloneThese(final Editable[] items) {
		final Editable[] res = new Editable[items.length];
		for (int i = 0; i < items.length; i++) {
			final Editable thisOne = items[i];
			final Editable clonedItem = cloneThis(thisOne);
			res[i] = clonedItem;
		}
		return res;
	}

	/**
	 * duplicate this item
	 *
	 * @param item
	 * @return
	 */
	static public Editable cloneThis(final Editable item) {
		Editable res = null;
		try {
			if (item instanceof FixWrapper) {
				return (Editable) ((FixWrapper)item).clone();
			}

			final java.io.ByteArrayOutputStream bas = new ByteArrayOutputStream();
			final java.io.ObjectOutputStream oos = new ObjectOutputStream(bas);
			oos.writeObject(item);
			// get closure
			oos.close();
			bas.close();

			// now get the item
			final byte[] bt = bas.toByteArray();

			// and read it back in as a new item
			final java.io.ByteArrayInputStream bis = new ByteArrayInputStream(bt);

			// create the reader
			final java.io.ObjectInputStream iis = new ObjectInputStream(bis) {

				@Override
				protected Class<?> resolveClass(final ObjectStreamClass desc)
						throws IOException, ClassNotFoundException {
					final String name = desc.getName();
					try {
						return Class.forName(name, false, item.getClass().getClassLoader());
					} catch (final ClassNotFoundException ex) {
						return super.resolveClass(desc);
					}
				}
			};

			// and read it in
			final Object oj = iis.readObject();

			// get more closure
			bis.close();
			iis.close();

			if (oj instanceof Editable) {
				res = (Editable) oj;

				if (item instanceof IsTransientForChildren) {
					final IsTransientForChildren par = (IsTransientForChildren) item;
					par.reconnectChildObjects(res);
				}
			}
		} catch (final Exception e) {
			MWC.Utilities.Errors.Trace.trace(e);
		}
		return res;
	}

	// /////////////////////////////////
	// nested classes
	// ////////////////////////////////

	// /////////////////////////////////
	// member functions
	// ////////////////////////////////
	static public void getDropdownListFor(final IMenuManager manager, final Editable[] editables,
			final Layer[] updateLayers, final HasEditables[] parentLayers, final Layers theLayers,
			final Clipboard _clipboard) {
		// do we have any editables?
		if (editables.length == 0) {
			return;
		}

		// get the editable item
		final Editable data = editables[0];

		CutItem cutter = null;
		CopyItem copier = null;
		DeleteItem deleter = null;

		// just check is trying to operate on the layers object itself
		if (data instanceof MWC.GUI.Layers) {
			// do nothing, we can't copy the layers itself
		} else {

			// first the cut action
			cutter = new CutItem(editables, _clipboard, parentLayers, theLayers, updateLayers);

			// now the copy action
			copier = new CopyItem(editables, _clipboard, parentLayers, theLayers, updateLayers);

			// and the delete
			deleter = new DeleteItem(editables, parentLayers, theLayers, updateLayers);

			// create the menu items

			// add to the menu
			manager.add(new Separator());
			manager.add(cutter);

			// try the copier
			if (copier != null) {
				manager.add(copier);
			}

			manager.add(deleter);
		}

	}
}
