package com.borlander.rac353542.bislider;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;


public abstract class PointerDrawerBase implements PointerDrawer {
    public void paintPointer(GC gc, Point p) {
        paintPointer(gc, p.x, p.y, null);
    }

    public void paintPointer(GC gc, Point p, String optionalLabel) {
        paintPointer(gc, p.x, p.y, optionalLabel);
    }

}
