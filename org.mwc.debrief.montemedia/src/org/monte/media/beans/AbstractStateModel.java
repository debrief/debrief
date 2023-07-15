/*
 * @(#)AbstractStateModel.java  1.1  2002-05-09
 * 
 * Copyright (c) 2002-05-09 Werner Randelshofer, Goldau, Switzerland.
 * All rights reserved.
 *
 * You may not use, copy or modify this file, except in compliance with the
 * license agreement you entered into with Werner Randelshofer.
 * For details see accompanying license terms.
 */

package org.monte.media.beans;

import javax.swing.event.*;
/**
 * Abstract superclass of models that fire state change
 * events to registered ChangeListener's, when their
 * state changes.
 *
 * @author Werner Randelshofer
 * @version 1.0 2002-05-09 Created.
 */
public class AbstractStateModel {
    protected EventListenerList listenerList;
    protected ChangeEvent changeEvent;
    
    /** Creates a new instance of AbstractChangeModel */
    public AbstractStateModel() {
    }
    
    public void addChangeListener(ChangeListener l) {
        if (listenerList == null) {
            listenerList = new EventListenerList();
        }
        listenerList.add(ChangeListener.class, l);
    }
    
    public void removeChangeListener(ChangeListener l) {
        if (listenerList == null) {
            listenerList = new EventListenerList();
        }
        listenerList.remove(ChangeListener.class, l);
    }
    /**
     * Notify all listeners that have registered interest for
     * notification on this event type.
     */
    protected void fireStateChanged() {
        if (listenerList != null) {
            // Guaranteed to return a non-null array
            Object[] listeners = listenerList.getListenerList();
            // Process the listeners last to first, notifying
            // those that are interested in this event
            for (int i = listeners.length-2; i>=0; i-=2) {
                if (listeners[i]==ChangeListener.class) {
                    // Lazily create the event:
                    if (changeEvent == null) {
                        changeEvent = new ChangeEvent(this);
                    }
                    ((ChangeListener)listeners[i+1]).stateChanged(changeEvent);
                }
            }
        }
    }
}
