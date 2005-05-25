// Copyright MWC 1999, Debrief 3 Project
// $RCSfile$
// @author $Author$
// @version $Revision$
// $Log$
// Revision 1.5  2005-05-25 14:18:17  Ian.Mayo
// Refactor to provide more useful SWT GC wrapper (hopefully suitable for buffered images)
//
// Revision 1.4  2005/05/24 13:26:42  Ian.Mayo
// Start including double-click support.
//
// Revision 1.3  2005/05/24 07:35:57  Ian.Mayo
// Ignore anti-alias bits, sort out text-writing in filling areas
//
// Revision 1.2  2005/05/20 15:34:44  Ian.Mayo
// Hey, practically working!
//
// Revision 1.1  2005/05/20 13:45:03  Ian.Mayo
// Start doing chart
//
//

package org.mwc.cmap.plotViewer.editors.chart;

import java.awt.Color;
import java.awt.Dimension;
import java.util.Enumeration;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.mwc.cmap.core.property_support.ColorHelper;

import MWC.GUI.CanvasType;

/**
 * Swing implementation of a canvas.
 */
public class SWTCanvas extends SWTCanvasAdapter
{

	// ///////////////////////////////////////////////////////////
	// member variables
	// //////////////////////////////////////////////////////////

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	org.eclipse.swt.widgets.Canvas _myCanvas = null;
	

	/**
	 * our double-buffering safe copy.
	 */
	private transient Image _dblBuff;	

	// ///////////////////////////////////////////////////////////
	// constructor
	// //////////////////////////////////////////////////////////

	/**
	 * default constructor.
	 */
	public SWTCanvas(Composite parent)
	{
		super();
		
		_myCanvas = new Canvas(parent, SWT.NO_BACKGROUND);

		// add handler to catch canvas resizes
		_myCanvas.addControlListener(new ControlAdapter()
		{

			public void controlResized(final ControlEvent e)
			{
				Point pt = _myCanvas.getSize();
				Dimension dim = new Dimension(pt.x, pt.y);
				setScreenSize(dim);
			}
		});

		// switch on tooltips for this panel
		_myCanvas.setToolTipText("blank");

		// setup our own painter
		_myCanvas.addPaintListener(new org.eclipse.swt.events.PaintListener()
		{

			public void paintControl(PaintEvent e)
			{
				repaintMe(e);
			}
		});
		
		_myCanvas.setBackground(ColorHelper.getColor(java.awt.Color.black));
	}


	// ////////////////////////////////////////////////////
	// screen redraw related
	// ////////////////////////////////////////////////////
	
	protected void repaintMe(PaintEvent pe)
	{

		// get the graphics destination
		GC gc = pe.gc;

		// put double-buffering code in here.
		if (_dblBuff == null)
		{
			// ok, create the new image
			Point theSize = _myCanvas.getSize();

			if ((theSize.x == 0) || (theSize.y == 0))
				return;

			_dblBuff = new Image(Display.getCurrent(), theSize.x, theSize.y);
			GC theDest = new GC(_dblBuff);

			// and paint into it
			paintPlot(theDest);

		}

		// finally put the required bits of the target image onto the screen
		gc.drawImage(_dblBuff, pe.x, pe.y, pe.width, pe.height, pe.x, pe.y,
				pe.width, pe.height);

	}


	/**
	 * the real paint function, called when it's not satisfactory to just paint in
	 * our safe double-buffered image.
	 * 
	 * @param g1
	 */
	private void paintPlot(GC g1)
	{

		// prepare the ground (remember the graphics dest for a start)
		startDraw(g1);

		// go through our painters
		final Enumeration enumer = _thePainters.elements();
		while (enumer.hasMoreElements())
		{
			final CanvasType.PaintListener thisPainter = (CanvasType.PaintListener) enumer
					.nextElement();

			// check the screen has been defined
			final Dimension area = this.getProjection().getScreenArea();
			if ((area == null) || (area.getWidth() <= 0) || (area.getHeight() <= 0))
			{
				return;
			}

			// it must be ok
			thisPainter.paintMe(this);
		}

		// all finished, close it now
		endDraw(null);
	}	
	// ///////////////////////////////////////////////////////////
	// member functions
	// //////////////////////////////////////////////////////////

	// ///////////////////////////////////////////////////////////
	// projection related
	// //////////////////////////////////////////////////////////
	/**
	 * handler for a screen resize - inform our projection of the resize then
	 * inform the painters.
	 */
	protected void setScreenSize(final java.awt.Dimension p1)
	{
		super.setScreenSize(p1);
		
		Dimension theDim = p1;
		// check if this is a real resize
		if ((_theSize == null) || (!_theSize.equals(p1)))
		{
			// inform our parent
			_myCanvas.setSize(p1.width, p1.height);

			// erase the double buffer, (if we have one)
			// since it is now invalid
			if (_dblBuff != null)
			{
				_dblBuff.dispose();
				_dblBuff = null;
			}

			// inform the listeners that we have resized
			final Enumeration enumer = _thePainters.elements();
			while (enumer.hasMoreElements())
			{
				final CanvasType.PaintListener thisPainter = (CanvasType.PaintListener) enumer
						.nextElement();
				thisPainter.resizedEvent(_theProjection, p1);
			}

		}
	}

	// ///////////////////////////////////////////////////////////
	// graphics plotting related
	// //////////////////////////////////////////////////////////

	/**
	 * first repaint the plot, then trigger a screen update
	 */
	public final void updateMe()
	{
		if (_dblBuff != null)
		{
			_dblBuff.dispose();
			_dblBuff = null;
		}

		_myCanvas.redraw();
	}

	/**
	 * provide close method, clear elements.
	 */
	public final void close()
	{
		_dblBuff = null;
	}

	public String getName()
	{
		// TODO Auto-generated method stub
		return "SWT Canvas";
	}

	public void redraw(int x, int y, int width, int height, boolean b)
	{
		_myCanvas.redraw(x, y, width, height, b);
	}

	public void addControlListener(ControlAdapter adapter)
	{
		_myCanvas.addControlListener(adapter);
	}

	public void addMouseMoveListener(MouseMoveListener listener)
	{
		_myCanvas.addMouseMoveListener(listener);
	}

	public void addMouseListener(MouseListener listener)
	{
		_myCanvas.addMouseListener(listener);
	}

	public Control getCanvas()
	{
		return _myCanvas;
	}

}
