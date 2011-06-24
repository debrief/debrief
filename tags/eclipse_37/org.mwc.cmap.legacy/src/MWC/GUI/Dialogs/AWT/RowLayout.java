package MWC.GUI.Dialogs.AWT;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;

public class RowLayout implements LayoutManager {
    static private int _defaultGap = 5;

    private int gap;
    private Orientation verticalOrientation;
    private Orientation horizontalOrientation;

    public RowLayout() {
        this(Orientation.CENTER, 
             Orientation.CENTER, _defaultGap);
    }
    public RowLayout(int gap) {
        this(Orientation.CENTER, Orientation.CENTER, gap);
    }
    public RowLayout(Orientation horizontalOrient, 
                     Orientation verticalOrient) {
        this(horizontalOrient, verticalOrient, _defaultGap);
    }
    public RowLayout(Orientation horizontalOrient, 
                     Orientation verticalOrient, int gap) {
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

    public void addLayoutComponent(String name, Component comp) {
    }
    public void removeLayoutComponent(Component comp) {
    }

    public Dimension preferredLayoutSize(Container target) {
        Insets    insets      = target.getInsets();
        Dimension dim         = new Dimension(0,0);
        int       ncomponents = target.getComponentCount();
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
    public Dimension minimumLayoutSize(Container target) {
        Insets    insets      = target.getInsets();
        Dimension dim         = new Dimension(0,0);
        int       ncomponents = target.getComponentCount();
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
    public void layoutContainer(Container target) {
        Insets    insets      = target.getInsets();
        int       ncomponents = target.getComponentCount();
        int       top         = 0;
        int       left        = insets.left;
        Dimension tps         = target.getPreferredSize();
        Dimension targetSize  = target.getSize();
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
