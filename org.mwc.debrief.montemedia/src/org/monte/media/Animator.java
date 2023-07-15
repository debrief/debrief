/*
 * @(#)Animator.java  3.0  2008-04-28
 *
 * Copyright (c) 2003-2008 Werner Randelshofer
 * Hausmatt 10, Goldau, CH-6405, Switzerland.
 * All rights reserved.
 *
 * The copyright of this software is owned by Werner Randelshofer. 
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * Werner Randelshofer. For details see accompanying license terms. 
 */

package org.monte.media;

import java.util.*;
import javax.swing.event.*;
/**
 * Animator executes multiple Interpolator's on a worker thread.
 *
 * @author  Werner Randelshofer
 * @version 3.0 2008-04-28 Turned class into an interface. 
 * <br>2.0 2007-11-15 Upgraded to Java 1.5.
 * <br>1.1 2007-09-09 Added support for  
 * <br>1.1 2007-08-26 Added support for interpolators which 
 * replace interpolators already in the execution queue of the Animator. 
 * <br>1.0.2 2006-10-02 Use 30 frames per second. 
 * <br>1.0.1 2006-02-21 Use 24 frames per second.
 * <br>1.0 December 22, 2003 Created.
 */
public interface Animator extends Runnable {
    /**
     * Set the lock object, on which the animator synchronizes while
     * animating the interpolators.
     */
    public void setLock(Object lock);
    public boolean isActive();
    public void start();
    
    public void stop();
    
    /**
     * Dispatches an interpolator for the animation thread.
     * This will launch the animation thread if it is not already active.
     */
    public void dispatch(Interpolator interpolator);
    
    public void animateStep();
    
    public void run();
    public void addChangeListener(ChangeListener listener);
    
    public void removeChangeListener(ChangeListener listener);
    
    public boolean isSynchronous();
}
