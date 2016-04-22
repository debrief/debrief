/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 */

package org.mwc.debrief.core.editors.painters.highlighters;

import java.awt.Color;
import java.awt.Point;

import MWC.GUI.CanvasType;
import MWC.GUI.Editable;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldLocation;

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
	public final void highlightIt(final MWC.Algorithms.PlainProjection proj, final CanvasType dest,
			final MWC.GenericData.WatchableList list, final MWC.GenericData.Watchable watch, final boolean isPrimary)
	{
		// check that our graphics context is still valid -
		// we can't, so we will just have to trap any exceptions it raises
		try
		{

			// sort out if this is an item that we plot
			if (watch instanceof Editable.DoNotHighlightMe)
			{
				// hey, don't bother...
				return;
			}

			boolean isPainted = false;

			// do we have know the list for this symbol?
			if (list != null)
			{
				// retrieve the symbol
				final MWC.GUI.Shapes.Symbols.PlainSymbol sym = list.getSnailShape();

				WorldLocation centre = null;

				if (sym != null)
				{

					// find the centre of the area
					centre = watch.getBounds().getCentre();

					// store the size
					final double size = sym.getScaleVal();

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
				final int myIntSize = 5;
				//
				// int rectSize = (int)(3d * _mySize);

				// set the highlight colour
				dest.setColor(_myColor);
				// get the current area of the watchable
				final WorldArea wa = watch.getBounds();
				// convert to screen coordinates
				final Point tl = proj.toScreen(wa.getTopLeft());

				final int tlx = tl.x;
				final int tly = tl.y;

				final Point br = proj.toScreen(wa.getBottomRight());
				// get the width
				final int x = tlx - myIntSize;
				final int y = tly - myIntSize;
				final int wid = (br.x - tlx) + (myIntSize * 2);
				final int ht = (br.y - tly) + (myIntSize * 2);

				// plot the rectangle
				dest.drawRect(x, y, wid, ht);
			}
			
      // and the array centre
      SWTPlotHighlighter.RectangleHighlight.plotArrayCentre(dest, watch, null, 5);

		}
		catch (final IllegalStateException e)
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
			catch (final Exception e)
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
