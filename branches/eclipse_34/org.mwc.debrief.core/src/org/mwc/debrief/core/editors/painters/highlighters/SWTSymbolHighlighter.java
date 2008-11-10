/*
 * SymbolHighlighter.java
 *
 * Created on 29 September 2000, 10:36
 */

package org.mwc.debrief.core.editors.painters.highlighters;

import java.awt.*;

import Debrief.GUI.Tote.Painters.SnailPainter.DoNotHighlightMe;
import MWC.GUI.*;
import MWC.GenericData.*;

public final class SWTSymbolHighlighter implements SWTPlotHighlighter, Editable
{

	private Color _myColor = Color.white;

	private double _mySize = MWC.GUI.Shapes.Symbols.SymbolScalePropertyEditor.LARGE;

	/**
	 * Draw a highlight around this watchable
	 * 
	 * @param proj
	 *          the current projection
	 * @param dest
	 *          the place to draw this highlight
	 * @param watch
	 *          the current data point
	 */
	public final void highlightIt(MWC.Algorithms.PlainProjection proj, CanvasType dest,
			Debrief.Tools.Tote.WatchableList list, Debrief.Tools.Tote.Watchable watch)
	{
		// check that our graphics context is still valid -
		// we can't, so we will just have to trap any exceptions it raises
		try
		{

			// sort out if this is an item that we plot
			if (watch instanceof DoNotHighlightMe)
			{
				// hey, don't bother...
				return;
			}

			boolean isPainted = false;

			// do we have know the list for this symbol?
			if (list != null)
			{
				// retrieve the symbol
				MWC.GUI.Shapes.Symbols.PlainSymbol sym = list.getSnailShape();

				WorldLocation centre = null;

				if (sym != null)
				{

					// find the centre of the area
					centre = watch.getBounds().getCentre();

					// store the size
					double size = sym.getScaleVal();

					// use our size
					sym.setScaleVal(_mySize);

					// do the plotting
					sym.paint(dest, centre, watch.getCourse());

					// restore the size
					sym.setScaleVal(size);

					// make a note that we've successfully highlighted this item
					isPainted = true;

				}
			}

			// paint this symbol if we haven't already managed to do it
			if (!isPainted)
			{
				// no symbol, make do with a rectangle
				Rectangle _areaCovered = null;

				int myIntSize = 5;
				//
				// int rectSize = (int)(3d * _mySize);

				// set the highlight colour
				dest.setColor(_myColor);
				// get the current area of the watchable
				WorldArea wa = watch.getBounds();
				// convert to screen coordinates
				Point tl = proj.toScreen(wa.getTopLeft());

				int tlx = tl.x;
				int tly = tl.y;

				Point br = proj.toScreen(wa.getBottomRight());
				// get the width
				int x = tlx - myIntSize;
				int y = tly - myIntSize;
				int wid = (br.x - tlx) + (myIntSize * 2);
				int ht = (br.y - tly) + (myIntSize * 2);

				// represent this area as a rectangle
				java.awt.Rectangle thisR = new Rectangle(x, y, wid, ht);

				// keep track of the area covered
				if (_areaCovered == null)
					_areaCovered = thisR;
				else
					_areaCovered.add(thisR);

				// plot the rectangle
				dest.drawRect(x, y, wid, ht);
			}
		}
		catch (IllegalStateException e)
		{
			MWC.Utilities.Errors.Trace.trace(e);
		}

	}

	/**
	 * the name of this object
	 * 
	 * @return the name of this editable object
	 */
	public final String getName()
	{
		return "Symbol Highlight";
	}

	/**
	 * the name of this object
	 * 
	 * @return the name of this editable object
	 */
	public final String toString()
	{
		return getName();
	}

	/**
	 * whether there is any edit information for this item this is a convenience
	 * function to save creating the EditorType data first
	 * 
	 * @return yes/no
	 */
	public final boolean hasEditor()
	{
		return true;
	}

	/**
	 * get the editor for this item
	 * 
	 * @return the BeanInfo data for this editable object
	 */
	public final Editable.EditorType getInfo()
	{
		return new SymbolHighlightInfo(this);
	}

	/**
	 * change the colour of the highlight
	 * 
	 * @param val
	 *          the new colour
	 */
	public final void setColor(final Color val)
	{
		_myColor = val;
	}

	/**
	 * change the size of the highlight to plot
	 * 
	 * @param val
	 *          the new size (stored with its constraints)
	 */
	public final void setScale(final double val)
	{
		_mySize = val;
	}

	/**
	 * return the current highlight colour
	 * 
	 * @return the colour
	 */
	public final Color getColor()
	{
		return _myColor;
	}

	/**
	 * return the current size of the highlight
	 * 
	 * @return current size, stored with it's constraints
	 */
	public final double getScale()
	{
		return _mySize;
	}

	// ///////////////////////////////////////////////////////////
	// nested class describing how to edit this class
	// //////////////////////////////////////////////////////////
	/**
	 * the set of editable details for the painter
	 */
	public static final class SymbolHighlightInfo extends Editable.EditorType
	{

		/**
		 * constructor for editable
		 * 
		 * @param data
		 *          the object we are editing
		 */
		public SymbolHighlightInfo(final SWTSymbolHighlighter data)
		{
			super(data, "Symbol Highlight", "");
		}

		/**
		 * the set of descriptions for this object
		 * 
		 * @return the properties
		 */
		public final java.beans.PropertyDescriptor[] getPropertyDescriptors()
		{
			try
			{
				final java.beans.PropertyDescriptor[] res = { prop("Scale",
						"scale to paint symbol"), };
				res[0]
						.setPropertyEditorClass(MWC.GUI.Shapes.Symbols.SymbolScalePropertyEditor.class);
				return res;
			}
			catch (Exception e)
			{
				MWC.Utilities.Errors.Trace.trace(e);
				return super.getPropertyDescriptors();
			}

		}
	}

	// ////////////////////////////////////////////////////////////////////////////////////////////////
	// testing for this class
	// ////////////////////////////////////////////////////////////////////////////////////////////////
	static public final class testMe extends junit.framework.TestCase
	{
		static public final String TEST_ALL_TEST_TYPE = "UNIT";

		public testMe(final String val)
		{
			super(val);
		}

		public final void testMyParams()
		{
			Editable ed = new SWTSymbolHighlighter();
			Editable.editableTesterSupport.testParams(ed, this);
			ed = null;
		}
	}
}
