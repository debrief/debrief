package com.borlander.rac353542.bislider.impl;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Control;

class DragSupport implements MouseListener, MouseMoveListener {

    public static final int MIN_DISTANCE_TO_BE_DRAGGING = 3;
    private final AreaGate myAreaGate;
    private final DragListener myDragListener;
    private Control myControl;
    private boolean myMayBeDragging;
    private boolean myIsDragging;
    private Point myStartPoint;

    public static interface DragListener {
        public void dragStarted();
        public void mouseDragged(MouseEvent e, Point startPoint);
        public void dragFinished();
    }

    public DragSupport(Control control, AreaGate areaGate, DragListener dragListener) {
        myControl = control;
        myAreaGate = areaGate;
        myDragListener = dragListener;
        control.addMouseListener(this);
        control.addMouseMoveListener(this);
    }

    public void releaseControl() {
        if (myControl != null && !myControl.isDisposed()) {
            myControl.removeMouseListener(this);
            myControl.removeMouseMoveListener(this);
            myControl = null;
        }
    }

    public void mouseDown(MouseEvent e) {
        if (myAreaGate.isInsideArea(e.x, e.y)) {
            draggingStop();
            myMayBeDragging = true;
            myStartPoint = new Point(e.x, e.y);
        }
    }

    public void mouseUp(MouseEvent e) {
        draggingStop();
    }

    public void mouseMove(MouseEvent e) {
        if (!myMayBeDragging) {
            return;
        }
        if (!myIsDragging && isCloseEnough(e)){
            myDragListener.dragStarted();
            myIsDragging = true;
        }
        if (myIsDragging) {
            myDragListener.mouseDragged(e, myStartPoint);
        }
    }

    public void mouseDoubleClick(MouseEvent e) {
        draggingStop();
    }
    
    private void draggingStop() {
        if (myIsDragging){
            myDragListener.dragFinished();
        }
        myIsDragging = false;
        myMayBeDragging = false;
    }

    private boolean isCloseEnough(MouseEvent e) {
        return Math.abs(e.x - myStartPoint.x) <= MIN_DISTANCE_TO_BE_DRAGGING && // 
                Math.abs(e.y - myStartPoint.y) <= MIN_DISTANCE_TO_BE_DRAGGING;
    }
}
