package org.mwc.cmap.core.editor_views;

import java.awt.*;
import java.beans.*;
import java.util.*;

import org.eclipse.jface.viewers.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.*;
import org.eclipse.ui.part.ViewPart;
import org.mwc.cmap.core.property_support.LatLongHelper;
import org.mwc.cmap.core.property_support.LatLongHelper.LatLongPropertySource;

import MWC.Algorithms.PlainProjection;
import MWC.GUI.*;
import MWC.GUI.CanvasType.PaintListener;
import MWC.GenericData.*;

public class PolygonEditorView extends ViewPart implements ISelectionProvider, PropertyChangeListener
{
	private WorldPath _myPath;

	private PropertyChangeSupport _pSupport;

	private PolygonEditorControl _myEditor;

	private PaintListener _myPainter;

	private CanvasType _theCanvas;

	private CountingLabelProvider _labeller;


	/**
	 * the people listening to us
	 */
	private Vector _selectionListeners;
	
	/** constructor...
	 */
	public PolygonEditorView()
	{
		_pSupport = new PropertyChangeSupport(this);
	}

	public void createPartControl(Composite parent)
	{
		_myEditor = new PolygonEditorControl(parent, SWT.NONE)
		{
			public void widgetDefaultSelected(SelectionEvent e)
			{
			}

			public void widgetSelected(SelectionEvent e)
			{
				btnPushed(e);
			}
		};

		// ok - we also want to add our new painter: that numbers the items in the
		// list
		_myPainter = new CanvasType.PaintListener()
		{
			public WorldArea getDataArea()
			{
				return null;
			}

			public String getName()
			{
				return null;
			}

			public void paintMe(CanvasType dest)
			{
				// ok - draw it.
				paintPolygon(dest);
			}

			public void resizedEvent(PlainProjection theProj, Dimension newScreenArea)
			{
			}
		};

		// try to sort out a fancy painter thingy
		_labeller = new CountingLabelProvider();
		_myEditor.pointList2.setLabelProvider(_labeller);
		IDoubleClickListener _dblClickListener = new IDoubleClickListener(){
		
					public void doubleClick(DoubleClickEvent event)
					{
						doubleClicked(event);
					}};
		_myEditor.pointList2.addDoubleClickListener(_dblClickListener);


		// say that we're a selection provider
		getSite().setSelectionProvider(this);		
	}

	protected void doubleClicked(DoubleClickEvent event)
	{
		StructuredSelection sel = (StructuredSelection) event.getSelection();
		WorldLocation loc = (WorldLocation) sel.getFirstElement();
		editThisInProperties(loc);
	}

	protected WorldLocation getSelectedPoint()
	{
		WorldLocation res = null;

		StructuredSelection curr = (StructuredSelection) _myEditor.pointList2.getSelection();
		Object first = curr.getFirstElement();
		if (first != null)
		{
			res = (WorldLocation) first;
		}
		return res;
	}

	protected void btnPushed(SelectionEvent e)
	{
		WorldLocation thisPoint = getSelectedPoint();
		ISelection selection = _myEditor.pointList2.getSelection();

		// ok, the user has done something in the editor control. what was it?
		Object source = e.widget;
		if (source == _myEditor.delBtn)
		{
			_myPath.remove(thisPoint);
			updateUI();
			selection = null;
		}
		else if (source == _myEditor.upBtn)
		{
			_myPath.moveUpward(thisPoint);
			updateUI();
		}
		else if (source == _myEditor.downBtn)
		{
			_myPath.moveDownward(thisPoint);
			updateUI();
		}
		else if (source == _myEditor.newBtn)
		{
			// right, what's the center point?
			WorldLocation centre = _myPath.getBounds().getCentre();
			_myPath.addPoint(centre);
			updateUI();
		}

		// and restore the seletion
		_myEditor.pointList2.setSelection(selection);

	}

	public void setFocus()
	{
	}

	public void setPolygon(WorldPath thePath)
	{
		_myPath = thePath;
		updateUI();

		// ok, see if we have a canvas open
		IWorkbench wb = PlatformUI.getWorkbench();
		IWorkbenchWindow win = wb.getActiveWorkbenchWindow();
		IWorkbenchPage page = win.getActivePage();
		if (page != null)
		{
			IEditorPart editor = page.getActiveEditor();

			// hmm, see if this editor has some layers we can fire a reformatted at
			CanvasType newCanvas = (CanvasType) editor.getAdapter(CanvasType.class);
			if (newCanvas != null)
			{
				// right, ditch the old canvas, if we have one
				if (_theCanvas != null)
				{
					System.out.println("pending painter remaining, removing");
					_theCanvas.removePainter(_myPainter);
					_theCanvas = null;
				}

				// remember the new one
				_theCanvas = newCanvas;

				// and start painting to it.
				_theCanvas.addPainter(_myPainter);
				System.out.println("adding painter");
			}
		}
	}

	public void addListener(PropertyChangeListener listener)
	{
		_pSupport.addPropertyChangeListener(listener);
	}

	public void removeListener(PropertyChangeListener listener)
	{
		_pSupport.removePropertyChangeListener(listener);
	}

	/**
	 * ok, we've received some data. store it.
	 */
	private void updateUI()
	{
		// remove the current list items = we're going to put them back in, in a mo.
		clearOut();

		if (!_myEditor.isDisposed())
		{
			// first the count
			_myEditor.editorPanel.setText(_myPath.getPoints().size() + " Points");

			Object[] thePts = _myPath.getPoints().toArray();
			_myEditor.pointList2.add(thePts);

			IWorkbench wb = PlatformUI.getWorkbench();
			IWorkbenchWindow win = wb.getActiveWorkbenchWindow();
			IWorkbenchPage page = win.getActivePage();
			IEditorPart editor = page.getActiveEditor();

			// hmm, see if this editor has some layers we can fire a reformatted at
			Layers theLayers = (Layers) editor.getAdapter(Layers.class);
			if (theLayers != null)
			{
				theLayers.fireReformatted(null);
			}

			// and reset the counter
			_labeller.reset();
		}
	}

	/**
	 * 
	 */
	private void clearOut()
	{
		if (!_myEditor.isDisposed())
		{
			// right - clear the list
			Object thisItem = _myEditor.pointList2.getElementAt(0);
			while (thisItem != null)
			{
				_myEditor.pointList2.remove(thisItem);
				thisItem = _myEditor.pointList2.getElementAt(0);
			}
			_myEditor.editorPanel.setText("Empty");
		}
	}

	/**
	 * we've moved away from editing this item. ditch gash
	 */
	public void stopPainting()
	{
		if (_theCanvas != null)
		{
			System.out.println("!removing painter..");
			_theCanvas.removePainter(_myPainter);

			// also trigger a repaint
			_theCanvas.updateMe();
			_theCanvas = null;
		}

		// hey, also remove the bits
		clearOut();

	}

	/**
	 * @param dest
	 */
	private void paintPolygon(CanvasType dest)
	{
		// ok - draw the polygon
		int counter = 2;
		dest.setColor(java.awt.Color.WHITE);
		Collection pts = _myPath.getPoints();
		Point lastP = null;
		Point startP = null;
		for (Iterator iter = pts.iterator(); iter.hasNext();)
		{
			WorldLocation thisL = (WorldLocation) iter.next();
			if (lastP == null)
			{
				// ignore, we won't plot it - but remember where we came from..
				lastP = new Point(dest.toScreen(thisL));
				startP = lastP;

				// mark the first point
				dest.drawText("1", startP.x, startP.y);
			}
			else
			{
				// get drawing
				Point thisP = dest.toScreen(thisL);
				dest.drawLine(lastP.x, lastP.y, thisP.x, thisP.y);

				// mark point
				dest.drawText("" + counter, thisP.x, thisP.y);
				counter++;

				// and remember the location
				lastP = new Point(thisP);
			}

		}

		// right, take us back to the beginning
		dest.drawLine(lastP.x, lastP.y, startP.x, startP.y);
	}

	/**
	 * final tidy up before we go home
	 */
	public void dispose()
	{
		// just check we still haven't got a painter defined
		stopPainting();

		// and let the parent do it's bits
		super.dispose();
	}

	private static class CountingLabelProvider extends LabelProvider
	{
		int counter = 0;

		/**
		 * reset the counter
		 */
		public void reset()
		{
			counter = 0;
		}

		public String getText(Object element)
		{
			return "" + ++counter + ": " + super.getText(element);
		}

	}

	public ISelection getSelection()
	{
		// TODO Auto-generated method stub
		return null;
	}


	public void removeSelectionChangedListener(ISelectionChangedListener listener)
	{
		_selectionListeners.remove(listener);
	}

	public void setSelection(ISelection selection)
	{
	}
	

	public void addSelectionChangedListener(ISelectionChangedListener listener)
	{
		if (_selectionListeners == null)
			_selectionListeners = new Vector(0, 1);

		// see if we don't already contain it..
		if (!_selectionListeners.contains(listener))
			_selectionListeners.add(listener);
	}
	
	/**
	 * @param asSelection
	 */
	private void editThisInProperties(WorldLocation loc)
	{
		LatLongPropertySource source = new LatLongHelper.LatLongPropertySource(loc);
		source.addPropertyChangeListener(this);
		StructuredSelection asSelection = new StructuredSelection(source);
		if (_selectionListeners != null)
		{
			SelectionChangedEvent sEvent = new SelectionChangedEvent(this, asSelection);
			for (Iterator stepper = _selectionListeners.iterator(); stepper.hasNext();)
			{
				ISelectionChangedListener thisL = (ISelectionChangedListener) stepper.next();
				if (thisL != null)
				{
					thisL.selectionChanged(sEvent);
				}
			}
		}
	}

	public void propertyChange(PropertyChangeEvent evt)
	{
		System.out.println("location updated");
	}	
}
