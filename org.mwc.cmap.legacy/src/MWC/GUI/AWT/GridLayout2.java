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
package MWC.GUI.AWT;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Insets;

// Grid Layout which allows components of differrent sizes
public class GridLayout2 extends GridLayout 
{
  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public GridLayout2() {
    this(1, 0, 0, 0);
  }

  public GridLayout2(final int rows, final int cols) {
    this(rows, cols, 0, 0);
  }

  public GridLayout2(final int rows, final int cols, final int hgap, final int vgap) {
    super(rows, cols, hgap, vgap);
  }

  public Dimension preferredLayoutSize(final Container parent) {
	//System.err.println("preferredLayoutSize");
    synchronized (parent.getTreeLock()) {
      final Insets insets = parent.getInsets();
      final int ncomponents = parent.getComponentCount();
      int nrows = getRows();
      int ncols = getColumns();
      if (nrows > 0) {
        ncols = (ncomponents + nrows - 1) / nrows;
      } 
      else {
        nrows = (ncomponents + ncols - 1) / ncols;
      }
      final int[] w = new int[ncols];
      final int[] h = new int[nrows];
      for (int i = 0; i < ncomponents; i ++) {
        final int r = i / ncols;
        final int c = i % ncols;
        final Component comp = parent.getComponent(i);
        final Dimension d = comp.getPreferredSize();
        if (w[c] < d.width) {
          w[c] = d.width;
        }
        if (h[r] < d.height) {
          h[r] = d.height;
        }
      }
      int nw = 0;
      for (int j = 0; j < ncols; j ++) {
        nw += w[j];
      }
      int nh = 0;
      for (int i = 0; i < nrows; i ++) {
        nh += h[i];
      }
      return new Dimension(insets.left + insets.right + nw + (ncols-1)*getHgap(), 
          insets.top + insets.bottom + nh + (nrows-1)*getVgap());
    }
  }

  public Dimension minimumLayoutSize(final Container parent) {
	System.err.println("minimumLayoutSize");
    synchronized (parent.getTreeLock()) {
      final Insets insets = parent.getInsets();
      final int ncomponents = parent.getComponentCount();
      int nrows = getRows();
      int ncols = getColumns();
      if (nrows > 0) {
        ncols = (ncomponents + nrows - 1) / nrows;
      } 
      else {
        nrows = (ncomponents + ncols - 1) / ncols;
      }
      final int[] w = new int[ncols];
      final int[] h = new int[nrows];
      for (int i = 0; i < ncomponents; i ++) {
        final int r = i / ncols;
        final int c = i % ncols;
        final Component comp = parent.getComponent(i);
        final Dimension d = comp.getMinimumSize();
        if (w[c] < d.width) {
          w[c] = d.width;
        }
        if (h[r] < d.height) {
          h[r] = d.height;
        }
      }
      int nw = 0;
      for (int j = 0; j < ncols; j ++) {
        nw += w[j];
      }
      int nh = 0;
      for (int i = 0; i < nrows; i ++) {
        nh += h[i];
      }
      return new Dimension(insets.left + insets.right + nw + (ncols-1)*getHgap(), 
          insets.top + insets.bottom + nh + (nrows-1)*getVgap());
    }
  }

  public void layoutContainer(final Container parent) {
    //System.err.println("layoutContainer");
    synchronized (parent.getTreeLock()) {
      final Insets insets = parent.getInsets();
      final int ncomponents = parent.getComponentCount();
      int nrows = getRows();
      int ncols = getColumns();
      if (ncomponents == 0) {
        return;
      }
      if (nrows > 0) {
        ncols = (ncomponents + nrows - 1) / nrows;
      } 
      else {
        nrows = (ncomponents + ncols - 1) / ncols;
      }
      final int hgap = getHgap();
      final int vgap = getVgap();
	  // scaling factors      
      final Dimension pd = preferredLayoutSize(parent);
      final double sw = (1.0 * parent.getWidth()) / pd.width;
      final double sh = (1.0 * parent.getHeight()) / pd.height;
      // scale
      final int[] w = new int[ncols];
      final int[] h = new int[nrows];
      for (int i = 0; i < ncomponents; i ++) {
        final int r = i / ncols;
        final int c = i % ncols;
        final Component comp = parent.getComponent(i);
        final Dimension d = comp.getPreferredSize();
        d.width = (int) (sw * d.width);
        d.height = (int) (sh * d.height);
        if (w[c] < d.width) {
          w[c] = d.width;
        }
        if (h[r] < d.height) {
          h[r] = d.height;
        }
      }
      for (int c = 0, x = insets.left; c < ncols; c ++) {
        for (int r = 0, y = insets.top; r < nrows; r ++) {
          final int i = r * ncols + c;
          if (i < ncomponents) {
            parent.getComponent(i).setBounds(x, y, w[c], h[r]);
          }
          y += h[r] + vgap;
        }
        x += w[c] + hgap;
      }
    }
  }  
}
