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

package org.mwc.cmap.core.editor_views;

import java.awt.Dimension;
import java.awt.Point;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.operations.CMAPOperation;
import org.mwc.cmap.core.property_support.LatLongHelper;
import org.mwc.cmap.core.property_support.LatLongHelper.LatLongPropertySource;

import MWC.Algorithms.PlainProjection;
import MWC.GUI.CanvasType;
import MWC.GUI.CanvasType.PaintListener;
import MWC.GUI.Layers;
import MWC.GUI.Properties.DebriefColors;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldPath;

public class PolygonEditorView extends ViewPart implements ISelectionProvider, PropertyChangeListener {
	abstract public class AbstractPolygonAction extends CMAPOperation {

		/**
		 * the polygon we're operating on
		 *
		 */
		protected WorldPath _path;

		/**
		 * the point we're manipulating
		 *
		 */
		protected WorldLocation _loc;

		public AbstractPolygonAction(final WorldPath path, final WorldLocation loc, final String title) {
			super(title);
			_path = path;
			_loc = loc;
		}

		@Override
		abstract public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException;

		public final void goDown() {
			_path.moveDownward(_loc);
			updateUI();
		}

		public final void goUp() {
			_path.moveUpward(_loc);
			updateUI();
		}

		@Override
		public IStatus redo(final IProgressMonitor monitor, final IAdaptable info) throws ExecutionException {
			return execute(monitor, info);
		}

		@Override
		public abstract IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException;

	}

	protected static class CountingLabelProvider extends LabelProvider {
		int counter = 0;

		@Override
		public String getText(final Object element) {
			return "" + ++counter + ": " + super.getText(element);
		}

		/**
		 * reset the counter
		 */
		public void reset() {
			counter = 0;
		}

	}

	public class DeleteAction extends AbstractPolygonAction {
		private int _index;

		public DeleteAction(final WorldPath path, final WorldLocation loc) {
			super(path, loc, "Delete point in polygon");

		}

		@Override
		public IStatus execute(final IProgressMonitor monitor, final IAdaptable info) throws ExecutionException {
			// right, remember where it is
			_index = _path.indexOf(_loc);

			// now ditch it
			_path.remove(_loc);

			updateUI();
			return Status.OK_STATUS;
		}

		@Override
		public IStatus undo(final IProgressMonitor monitor, final IAdaptable info) throws ExecutionException {
			// ok, put it back in
			_path.insertPointAt(_loc, _index);
			updateUI();

			return Status.OK_STATUS;
		}
	}

	public class MoveDownAction extends AbstractPolygonAction {
		public MoveDownAction(final WorldPath path, final WorldLocation loc) {
			super(path, loc, "Move point downward");
		}

		@Override
		public IStatus execute(final IProgressMonitor monitor, final IAdaptable info) throws ExecutionException {
			goDown();
			updateUI();

			return Status.OK_STATUS;
		}

		@Override
		public IStatus undo(final IProgressMonitor monitor, final IAdaptable info) throws ExecutionException {
			goUp();
			updateUI();

			return Status.OK_STATUS;
		}
	}

	public class MoveUpAction extends AbstractPolygonAction {
		public MoveUpAction(final WorldPath path, final WorldLocation loc) {
			super(path, loc, "Move point upward");
		}

		@Override
		public IStatus execute(final IProgressMonitor monitor, final IAdaptable info) throws ExecutionException {
			goUp();
			updateUI();

			return Status.OK_STATUS;
		}

		@Override
		public IStatus undo(final IProgressMonitor monitor, final IAdaptable info) throws ExecutionException {
			goDown();
			updateUI();

			return Status.OK_STATUS;
		}
	}

	public class NewPointAction extends AbstractPolygonAction {
		public NewPointAction(final WorldPath path, final WorldLocation loc) {
			super(path, loc, "Insert new point");
		}

		@Override
		public IStatus execute(final IProgressMonitor monitor, final IAdaptable info) throws ExecutionException {
			_path.addPoint(_loc);
			updateUI();

			return Status.OK_STATUS;
		}

		@Override
		public IStatus undo(final IProgressMonitor monitor, final IAdaptable info) throws ExecutionException {
			_path.remove(_loc);
			updateUI();

			return Status.OK_STATUS;
		}
	}

	private WorldPath _myPath;

	private final PropertyChangeSupport _pSupport;

	private PolygonEditorControl _myEditor;

	private PaintListener _myPainter;

	private CanvasType _theCanvas;

	private CountingLabelProvider _labeller;

	/**
	 * the people listening to us
	 */
	private Vector<ISelectionChangedListener> _selectionListeners;

	private StructuredSelection _currentSelection;

	/**
	 * constructor...
	 */
	public PolygonEditorView() {
		_pSupport = new PropertyChangeSupport(this);
	}

	// /**
	// * @param thisPoint
	// */
	// private void moveDownward(WorldLocation thisPoint)
	// {
	// MoveDownAction theAction = new MoveDownAction(_myPath, thisPoint);
	// CorePlugin.run(theAction);
	// }

	public void addListener(final PropertyChangeListener listener) {
		_pSupport.addPropertyChangeListener(listener);
	}

	/**
	 * @param centre
	 */
	private void addPoint(final WorldLocation centre) {
		final NewPointAction theAction = new NewPointAction(_myPath, centre);
		CorePlugin.run(theAction);
	}

	@Override
	public void addSelectionChangedListener(final ISelectionChangedListener listener) {
		if (_selectionListeners == null)
			_selectionListeners = new Vector<ISelectionChangedListener>(0, 1);

		// see if we don't already contain it..
		if (!_selectionListeners.contains(listener))
			_selectionListeners.add(listener);
	}

	protected void btnPushed(final SelectionEvent e) {
		final WorldLocation thisPoint = getSelectedPoint();
		ISelection selection = _myEditor.pointList2.getSelection();

		// ok, the user has done something in the editor control. what was it?
		final Object source = e.widget;
		if (source == _myEditor.delBtn) {
			removePoint(thisPoint);
			selection = null;
		} else if (source == _myEditor.upBtn) {
			moveUpward(thisPoint);
		} else if (source == _myEditor.pasteBtn) {
			pasteLoc(thisPoint);
		} else if (source == _myEditor.newBtn) {
			// right, what's the center point?
			final WorldLocation centre = _myPath.getBounds().getCentre();
			addPoint(centre);
		}

		// and restore the seletion
		_myEditor.pointList2.setSelection(selection);

	}

	/**
	 *
	 */
	private void clearOut() {
		if (!_myEditor.isDisposed()) {
			// right - clear the list
			Object thisItem = _myEditor.pointList2.getElementAt(0);
			while (thisItem != null) {
				_myEditor.pointList2.remove(thisItem);
				thisItem = _myEditor.pointList2.getElementAt(0);
			}
			_myEditor.editorPanel.setText("Empty");
		}
	}

	/**
	 * stop listening to any property we're already listening to
	 */
	private void clearPropertyListener() {
		if (_currentSelection != null) {
			final LatLongPropertySource ps = (LatLongPropertySource) _currentSelection.getFirstElement();
			ps.removePropertyChangeListener(this);
			_currentSelection = null;
		}
	}

	@Override
	public void createPartControl(final Composite parent) {
		_myEditor = new PolygonEditorControl(parent, SWT.NONE) {
			@Override
			public void widgetDefaultSelected(final SelectionEvent e) {
			}

			@Override
			public void widgetSelected(final SelectionEvent e) {
				btnPushed(e);
			}
		};

		// ok - we also want to add our new painter: that numbers the items in the
		// list
		_myPainter = new CanvasType.PaintListener() {
			@Override
			public WorldArea getDataArea() {
				return null;
			}

			@Override
			public String getName() {
				return null;
			}

			@Override
			public void paintMe(final CanvasType dest) {
				// ok - draw it.
				paintPolygon(dest);
			}

			@Override
			public void resizedEvent(final PlainProjection theProj, final Dimension newScreenArea) {
			}
		};

		// try to sort out a fancy painter thingy
		_labeller = new CountingLabelProvider();
		_myEditor.pointList2.setLabelProvider(_labeller);
		final IDoubleClickListener _dblClickListener = new IDoubleClickListener() {

			@Override
			public void doubleClick(final DoubleClickEvent event) {
				doubleClicked(event);
			}
		};
		_myEditor.pointList2.addDoubleClickListener(_dblClickListener);
		_myEditor.helpLbl.setText("Manipulate your points using the buttons to the right, "
				+ " by double-clicking on a point to change it in the Properties view, "
				+ "by dragging in Drag Component mouse mode," + "or by pasting from the clipboard.");

		// say that we're a selection provider
		getSite().setSelectionProvider(this);
	}

	/**
	 * final tidy up before we go home
	 */
	@Override
	public void dispose() {
		// stop listening
		clearPropertyListener();

		// just check we still haven't got a painter defined
		stopPainting();

		// and let the parent do it's bits
		super.dispose();
	}

	protected void doubleClicked(final DoubleClickEvent event) {
		final StructuredSelection sel = (StructuredSelection) event.getSelection();
		final WorldLocation loc = (WorldLocation) sel.getFirstElement();
		editThisInProperties(loc);
	}

	/**
	 * @param asSelection
	 */
	private void editThisInProperties(final WorldLocation loc) {
		// are we already listening to something?
		clearPropertyListener();

		final LatLongPropertySource source = new LatLongHelper.LatLongPropertySource(loc);
		source.addPropertyChangeListener(this);
		// ok, better store it.
		final StructuredSelection newSelection = new StructuredSelection(source);
		setSelection(newSelection);
	}

	protected WorldLocation getSelectedPoint() {
		WorldLocation res = null;

		final StructuredSelection curr = (StructuredSelection) _myEditor.pointList2.getSelection();
		final Object first = curr.getFirstElement();
		if (first != null) {
			res = (WorldLocation) first;
		}
		return res;
	}

	@Override
	public ISelection getSelection() {
		return null;// _currentSelection;
	}

	/**
	 * @param thisPoint
	 */
	private void moveUpward(final WorldLocation thisPoint) {
		final MoveUpAction theAction = new MoveUpAction(_myPath, thisPoint);
		CorePlugin.run(theAction);
	}

	/**
	 * @param dest
	 */
	void paintPolygon(final CanvasType dest) {
		// ok - draw the polygon
		int counter = 2;
		dest.setColor(DebriefColors.GRAY);
		final Collection<WorldLocation> pts = _myPath.getPoints();
		Point lastP = null;
		Point startP = null;
		for (final Iterator<WorldLocation> iter = pts.iterator(); iter.hasNext();) {
			final WorldLocation thisL = iter.next();
			if (lastP == null) {
				// ignore, we won't plot it - but remember where we came from..
				lastP = new Point(dest.toScreen(thisL));
				startP = lastP;

				// mark the first point
				dest.drawText("1", startP.x, startP.y);
			} else {
				// get drawing
				final Point thisP = dest.toScreen(thisL);
				dest.drawLine(lastP.x, lastP.y, thisP.x, thisP.y);

				// mark point
				dest.drawText("" + counter, thisP.x, thisP.y);
				counter++;

				// and remember the location
				lastP = new Point(thisP);
			}

		}

		if ((lastP != null) && (startP != null)) {
			// right, take us back to the beginning
			dest.drawLine(lastP.x, lastP.y, startP.x, startP.y);
		}
	}

	/**
	 * user is trying to paste location off clipboard
	 *
	 * @param thisPoint
	 */
	private void pasteLoc(final WorldLocation thisPoint) {
		// is a location selected?
		if (thisPoint != null) {

			// is there a location on the clipboard?
			// right, see what's on the clipboard
			// right, copy the location to the clipboard
			final Clipboard clip = CorePlugin.getDefault().getClipboard();
			final Object val = clip.getContents(TextTransfer.getInstance());
			if (val != null) {
				final String txt = (String) val;
				// cool, get the text
				final WorldLocation loc = CorePlugin.fromClipboard(txt);
				if (loc != null) {
					// create the output value
					thisPoint.copy(loc);

					// and fire some kind of update...
					updateUI();
				} else {
					CorePlugin.showMessage("Paste location",
							"Sorry the clipboard text is not in the right format." + "\nContents:" + txt);
				}
			}
		}
	}

	@Override
	public void propertyChange(final PropertyChangeEvent evt) {
		// right, get the location that's changed
		final WorldLocation original = (WorldLocation) evt.getOldValue();
		final WorldLocation newLoc = (WorldLocation) evt.getNewValue();

		// update the old one
		original.copy(newLoc);

		// and update the UI
		updateUI();
	}

	public void removeListener(final PropertyChangeListener listener) {
		_pSupport.removePropertyChangeListener(listener);
	}

	/**
	 * @param thisPoint
	 */
	private void removePoint(final WorldLocation thisPoint) {
		final DeleteAction theAction = new DeleteAction(_myPath, thisPoint);
		CorePlugin.run(theAction);
	}

	@Override
	public void removeSelectionChangedListener(final ISelectionChangedListener listener) {
		_selectionListeners.remove(listener);
	}

	@Override
	public void setFocus() {
	}

	public void setPolygon(final WorldPath thePath) {
		_myPath = thePath;
		updateUI();

		// ok, see if we have a canvas open
		final IWorkbench wb = PlatformUI.getWorkbench();
		final IWorkbenchWindow win = wb.getActiveWorkbenchWindow();
		final IWorkbenchPage page = win.getActivePage();
		if (page != null) {
			final IEditorPart editor = page.getActiveEditor();

			// hmm, see if this editor has some layers we can fire a reformatted at
			final CanvasType newCanvas = editor.getAdapter(CanvasType.class);
			if (newCanvas != null) {
				// right, ditch the old canvas, if we have one
				if (_theCanvas != null) {
					_theCanvas.removePainter(_myPainter);
					_theCanvas = null;
				}

				// remember the new one
				_theCanvas = newCanvas;

				// and start painting to it.
				_theCanvas.addPainter(_myPainter);
			}
		}
	}

	@Override
	public void setSelection(final ISelection selection) {
		_currentSelection = (StructuredSelection) selection;
		if (_selectionListeners != null) {
			final SelectionChangedEvent sEvent = new SelectionChangedEvent(this, selection);
			for (final Iterator<ISelectionChangedListener> stepper = _selectionListeners.iterator(); stepper
					.hasNext();) {
				final ISelectionChangedListener thisL = stepper.next();
				if (thisL != null) {
					thisL.selectionChanged(sEvent);
				}
			}
		}
	}

	/**
	 * we've moved away from editing this item. ditch gash
	 */
	public void stopPainting() {
		if (_theCanvas != null) {
			_theCanvas.removePainter(_myPainter);

			// also trigger a repaint
			_theCanvas.updateMe();
			_theCanvas = null;
		}

		// hey, also remove the bits
		clearOut();

	}

	/**
	 * ok, we've received some data. store it.
	 */
	void updateUI() {
		// remove the current list items = we're going to put them back in, in a mo.
		clearOut();

		if (!_myEditor.isDisposed()) {
			// first the count
			_myEditor.editorPanel.setText(_myPath.getPoints().size() + " Points");

			final Object[] thePts = _myPath.getPoints().toArray();
			_myEditor.pointList2.add(thePts);

			final IWorkbench wb = PlatformUI.getWorkbench();
			final IWorkbenchWindow win = wb.getActiveWorkbenchWindow();
			final IWorkbenchPage page = win.getActivePage();
			final IEditorPart editor = page.getActiveEditor();

			// hmm, see if this editor has some layers we can fire a reformatted at
			final Layers theLayers = editor.getAdapter(Layers.class);
			if (theLayers != null) {
				theLayers.fireReformatted(null);
			}

			// and reset the counter
			_labeller.reset();
		}
	}

}
