/*
 * @(#)StateEvent.java  1.0  1999-10-19
 *
 * Copyright (c) 1999 Werner Randelshofer, Goldau, Switzerland.
 * All rights reserved.
 *
 * You may not use, copy or modify this file, except in compliance with the
 * license agreement you entered into with Werner Randelshofer.
 * For details see accompanying license terms.
 */
package org.monte.media;

import java.util.EventObject;
/**
 * Event for state changes.
 *
 * @author  Werner Randelshofer, Hausmatt 10, CH-6405 Goldau, Switzerland
 * @version    1.0  1999-10-19
 */
public class StateEvent
extends EventObject {
    /**
     * State.
     */
    private int state_;
    
    public StateEvent(Object source, int state) {
        super(source);
        state_ = state;
    }
    
    public int getNewState() {
        return state_;
    }
    
    public String toString() {
        return getClass().getName() + "[source=" + getSource() + ",state=" + state_ + "]";
        
    }
}
