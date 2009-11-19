/*
 * PlotHighlighter.java
 *
 * Created on 29 September 2000, 10:36
 */

package Debrief.GUI.Tote.Painters.Highlighters;

import MWC.GUI.*;
import java.awt.*;
import MWC.GenericData.*;
import MWC.GUI.Canvas.Swing.SwingCanvas;
import MWC.GUI.PlainWrapper.InterpolatedData;
import MWC.GUI.Properties.BoundedInteger;

/**
 * Interface for classes which are able to draw a highlight at a particular
 * point in time
 * 
 * @author IAN MAYO
 * @version 1
 */
public interface PlotHighlighter extends Editable {

	/**
	 * Draw a highlight around this watchable
	 * 
	 * @param proj
	 *            the current projection
	 * @param dest
	 *            the place to draw this highlight
	 * @param watch
	 *            the current data point
	 *            @param isPrimary whether this is the current primary track
	 */
	void highlightIt(final MWC.Algorithms.PlainProjection proj,
			final java.awt.Graphics dest,
			final MWC.GenericData.WatchableList list,
			final MWC.GenericData.Watchable watch, final boolean isPrimary);

	// ////////////////////////////////////////////////////////////////////
	// embedded class which just shows rectangular highlight around current
	// point
	// ////////////////////////////////////////////////////////////////////
	/**
	 * A simple (rectangular) highlighter
	 */
	public final class RectangleHighlight implements PlotHighlighter {

		private Color _myColor = Color.white;
		private int _mySize = 5;

		/**
		 * Draw a highlight around this watchable
		 * 
		 * @param proj
		 *            the current projection
		 * @param dest
		 *            the place to draw this highlight
		 * @param watch
		 *            the current data point
		 */
		public final void highlightIt(MWC.Algorithms.PlainProjection proj,
				java.awt.Graphics dest, MWC.GenericData.WatchableList list,
				MWC.GenericData.Watchable watch, final boolean isPrimary) {
			// check that our graphics context is still valid -
			// we can't, so we will just have to trap any exceptions it raises
			try {

				Rectangle _areaCovered = null;

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
				int x = tlx - _mySize;
				int y = tly - _mySize;
				int wid = (br.x - tlx) + _mySize * 2;
				int ht = (br.y - tly) + _mySize * 2;

				// represent this area as a rectangle
				java.awt.Rectangle thisR = new Rectangle(x, y, wid, ht);

				// keep track of the area covered
				if (_areaCovered == null)
					_areaCovered = thisR;
				else
					_areaCovered.add(thisR);

				// hmm - implemented plotting the cursor differently if we're
				// looking at interpolated data
				Stroke oldStroke = null;

				// right, lets have a look
				if (watch instanceof InterpolatedData) {
					final java.awt.Graphics2D g2 = (java.awt.Graphics2D) dest;
					// right, remember what the previous cursor was
					oldStroke = g2.getStroke();

					// set the new one
					final java.awt.BasicStroke stk = SwingCanvas
							.getStrokeFor(CanvasType.DOTTED);
					g2.setStroke(stk);
				}

				// plot the rectangle
				dest.drawRect(x, y, wid, ht);

				// and restore the old cursor if we have to
				if (oldStroke != null) {
					final java.awt.Graphics2D g2 = (java.awt.Graphics2D) dest;
					g2.setStroke(oldStroke);
					oldStroke = null;
				}

			} catch (IllegalStateException e) {
				MWC.Utilities.Errors.Trace.trace(e);
			}

		}

		/**
		 * the name of this object
		 * 
		 * @return the name of this editable object
		 */
		public final String getName() {
			return "Default Highlight";
		}

		/**
		 * the name of this object
		 * 
		 * @return the name of this editable object
		 */
		public final String toString() {
			return getName();
		}

		/**
		 * whether there is any edit information for this item this is a
		 * convenience function to save creating the EditorType data first
		 * 
		 * @return yes/no
		 */
		public final boolean hasEditor() {
			return true;
		}

		/**
		 * get the editor for this item
		 * 
		 * @return the BeanInfo data for this editable object
		 */
		public final Editable.EditorType getInfo() {
			return new RectangleHighlightInfo(this);
		}

		/**
		 * change the colour of the highlight
		 * 
		 * @param val
		 *            the new colour
		 */
		public final void setColor(final Color val) {
			_myColor = val;
		}

		/**
		 * change the size of the highlight to plot
		 * 
		 * @param val
		 *            the new size (stored with its constraints)
		 */
		public final void setSize(final BoundedInteger val) {
			_mySize = val.getCurrent();
		}

		/**
		 * return the current highlight colour
		 * 
		 * @return the colour
		 */
		public final Color getColor() {
			return _myColor;
		}

		/**
		 * return the current size of the highlight
		 * 
		 * @return current size, stored with it's constraints
		 */
		public final BoundedInteger getSize() {
			return new BoundedInteger(_mySize, 1, 20);
		}

		// ///////////////////////////////////////////////////////////
		// nested class describing how to edit this class
		// //////////////////////////////////////////////////////////
		/**
		 * the set of editable details for the painter
		 */
		public final class RectangleHighlightInfo extends Editable.EditorType {

			/**
			 * constructor for editable
			 * 
			 * @param data
			 *            the object we are editing
			 */
			public RectangleHighlightInfo(final RectangleHighlight data) {
				super(data, "Default Highlight", "");
			}

			/**
			 * the set of descriptions for this object
			 * 
			 * @return the properties
			 */
			public final java.beans.PropertyDescriptor[] getPropertyDescriptors() {
				try {
					final java.beans.PropertyDescriptor[] res = {
							prop("Color", "Color to paint highlight"),
							prop("Size", "size to paint highlight (pixels)"), };
					return res;
				} catch (Exception e) {
					MWC.Utilities.Errors.Trace.trace(e);
					return super.getPropertyDescriptors();
				}

			}
		}

	}

}
