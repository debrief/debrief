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
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package MWC.GUI.Dialogs.AWT;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;

public class RowLayout implements LayoutManager {
    static private int _defaultGap = 5;

    private final int gap;
    private final Orientation verticalOrientation;
    private final Orientation horizontalOrientation;

    public RowLayout() {
        this(Orientation.CENTER, 
             Orientation.CENTER, _defaultGap);
    }
    public RowLayout(final int gap) {
        this(Orientation.CENTER, Orientation.CENTER, gap);
    }
    public RowLayout(final Orientation horizontalOrient, 
                     final Orientation verticalOrient) {
        this(horizontalOrient, verticalOrient, _defaultGap);
    }
    public RowLayout(final Orientation horizontalOrient, 
                     final Orientation verticalOrient, final int gap) {
        if(gap < 0 ||
            (horizontalOrient != Orientation.LEFT   &&
            horizontalOrient != Orientation.CENTER &&
            horizontalOrient != Orientation.RIGHT) ||

            (verticalOrient   != Orientation.TOP    &&
            verticalOrient   != Orientation.CENTER &&
            verticalOrient   != Orientation.BOTTOM)) {
			throw new IllegalArgumentException(
						"bad gap or orientation");
		}
        this.gap                   = gap;
        this.verticalOrientation   = verticalOrient;
        this.horizontalOrientation = horizontalOrient;
    }

    public void addLayoutComponent(final String name, final Component comp) {
    }
    public void removeLayoutComponent(final Component comp) {
    }

    public Dimension preferredLayoutSize(final Container target) {
        final Insets    insets      = target.getInsets();
        final Dimension dim         = new Dimension(0,0);
        final int       ncomponents = target.getComponentCount();
        Component comp;
        Dimension d;

        for (int i = 0 ; i < ncomponents ; i++) {
            comp = target.getComponent(i);

            if(comp.isVisible()) {
                d = comp.getPreferredSize();

                dim.width  += d.width;
                dim.height  = Math.max(d.height, dim.height);

                if(i > 0) dim.width += gap;
            }
        }
        dim.width  += insets.left + insets.right;
        dim.height += insets.top + insets.bottom;

        return dim;
    }
    public Dimension minimumLayoutSize(final Container target) {
        final Insets    insets      = target.getInsets();
        final Dimension dim         = new Dimension(0,0);
        final int       ncomponents = target.getComponentCount();
        Component comp;
        Dimension d;

        for (int i = 0 ; i < ncomponents ; i++) {
            comp = target.getComponent(i);

            if(comp.isVisible()) {
                d = comp.getMinimumSize();

                dim.width  += d.width;
                dim.height  = Math.max(d.height, dim.height);

                if(i > 0) dim.width += gap;
            }
        }
        dim.width  += insets.left + insets.right;
        dim.height += insets.top + insets.bottom;

        return dim;
    }
    public void layoutContainer(final Container target) {
        final Insets    insets      = target.getInsets();
        final int       ncomponents = target.getComponentCount();
        int       top         = 0;
        int       left        = insets.left;
        final Dimension tps         = target.getPreferredSize();
        final Dimension targetSize  = target.getSize();
        Component comp;
        Dimension ps;

        if(horizontalOrientation == Orientation.CENTER)
            left = left + (targetSize.width/2) - (tps.width/2);
        if(horizontalOrientation == Orientation.RIGHT)
            left = left + targetSize.width - tps.width;
        
        for (int i = 0 ; i < ncomponents ; i++) {
            comp = target.getComponent(i);

            if(comp.isVisible()) {
                ps  = comp.getPreferredSize();

                if(verticalOrientation == Orientation.CENTER)
                    top = (targetSize.height/2) - (ps.height/2);
                else if(verticalOrientation == Orientation.TOP)
                    top = insets.top;
                else if(
                    verticalOrientation == Orientation.BOTTOM)
                    top = targetSize.height - 
                          ps.height - insets.bottom;

                comp.setBounds(left,top,ps.width,ps.height);
                left += ps.width + gap;
            }
        }
    }
}
