/**
 * @(#)DefaultAnimator.java  1.0.1  2008-08-19
 *
 * Copyright (c) 2008 Werner Randelshofer
 * Hausmatt 10, CH-6405 Goldau, Switzerland
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
 * DefaultAnimator.
 *
 * @author Werner Randelshofer
 * @version 1.0.1 2008-08-19 finish method of interpolators was not always called. 
 * <br>1.0 Apr 28, 2008 Created.
 */
public class DefaultAnimator implements Animator {
    protected EventListenerList listenerList = new EventListenerList();
    /**
     * List of active interpolators.
     * Implementation note: This vector is only accessed by the animationThread.
     */
    private Vector activeInterpolators;
    /**
     * List of new interpolators.
     * Implementation note: The dispatcher thread adds items to this list, the
     * animationThread removes items.
     * This queue is used to synchronize the dispatcher thread with the animation
     * thread.
     * Note: the dispatcher thread is not necesseraly the  Event Dispatcher
     * thread. The dispatcher thread is any thread which dispatches interpolators.
     */
    private Vector newInterpolators;
    
    /**
     * We keep a reference to the animationThread to be able to stop it and
     * to be able to wait until it is finished.
     */
    private Thread animationThread;
    private boolean isAnimating;
    private Object lock = new Object();
    protected ChangeEvent changeEvent;
    
    /**
     * The sleep time controls the framerate of the animator.
     * A value of 33 is approximately 30 frames per second.
     */
    private int sleep = 33;
    
    /** Creates a new instance. */
    public DefaultAnimator() {
        activeInterpolators = new Vector();
        newInterpolators = new Vector();
    }
    
    /**
     * Set the lock object, on which the animator synchronizes while
     * animating the interpolators.
     */
    public void setLock(Object lock) {
        this.lock = lock;
    }
    
    public boolean isActive() {
        return animationThread != null;
    }
    
    public void start() {
        stop();
        animationThread = new Thread(this);
        animationThread.start();
    }
    
    public void stop() {
        if (animationThread != null) {
            Thread t = animationThread;
            animationThread = null;
            t.interrupt();
            try {
                t.join();
            } catch (InterruptedException e) {
            }
        }
    }
    
    /**
     * Dispatches an interpolator for the animation thread.
     * This will launch the animation thread if it is not already active.
     */
    public void dispatch(Interpolator interpolator) {
        synchronized (newInterpolators) {
            newInterpolators.addElement(interpolator);
            if (! isActive()) start();
        }
    }
    
    public void animateStep() {
        long now = System.currentTimeMillis();
        
        // Enqueue new interpolators into the activeInterpolators list
        // Avoid enqueuing new interpolators which must be run sequentally
        // with active interpolators.
        OuterLoop: for (int i=0; i < newInterpolators.size(); i++) {
            Interpolator candidate = (Interpolator) newInterpolators.elementAt(i);
            boolean isEnqueueable = true;
            for (int j=0; j < i; j++) {
                Interpolator before = (Interpolator) newInterpolators.elementAt(j);
                if (candidate.isSequential(before)) {
                    isEnqueueable = false;
                    break;
                }
            }
            if (isEnqueueable) {
                for (int j=0; j < activeInterpolators.size(); j++) {
                    Interpolator before = (Interpolator) activeInterpolators.elementAt(j);
                    if (candidate.replaces(before)) {
                        before.finish(now);
                    }
                    if (candidate.isSequential(before)) {
                        isEnqueueable = false;
                        break;
                    }
                }
            }
            if (isEnqueueable) {
                candidate.initialize(now);
                activeInterpolators.addElement(candidate);
                newInterpolators.removeElementAt(i--);
            }
        }
        
        // Animate the active interpolators
        // Remove finished interpolators.
        for (int i=0; i < activeInterpolators.size(); i++) {
            Interpolator active = (Interpolator) activeInterpolators.elementAt(i);
            if (active.isFinished()) {
                activeInterpolators.removeElementAt(i--);
            } else if (active.isElapsed(now)) {
                active.finish(now);
                activeInterpolators.removeElementAt(i--);
            } else {
                active.interpolate(now);
            }
        }
    }
    
    public void run() {
        //fireStateChanged();
        
        // Animation loop.
        // We loop here as long as we have something to do.
        while (Thread.currentThread() == animationThread) {
            synchronized (lock) {
                animateStep();
            }
            
            boolean hasFinished = false;
            synchronized (newInterpolators) {
                if (activeInterpolators.size() == 0 && newInterpolators.size() == 0) {
                    animationThread = null;
                    hasFinished = true;
                }
            }
            if (hasFinished) {
                fireStateChanged();
                return;
            }
            
            try {
                Thread.sleep(sleep); // 30 frames per second
            } catch (InterruptedException e) {
            }
        }
        
        // If we have been stopped, we finish all interpolators.
        // Note: We get here only if method stop() has been called.
        //       If we finish by ourselves, we return from within the while loop
        //       above.
        synchronized (newInterpolators) {
            synchronized (lock) {
                long now = System.currentTimeMillis();
                for (int i=0; i < activeInterpolators.size(); i++) {
                    Interpolator active = (Interpolator) activeInterpolators.elementAt(i);
                        active.finish(now);
                }
                for (int i=0; i < newInterpolators.size(); i++) {
                    Interpolator candidate = (Interpolator) newInterpolators.elementAt(i);
                    candidate.initialize(now);
                    candidate.finish(now);
                }
                activeInterpolators.removeAllElements();
                newInterpolators.removeAllElements();
            }
        }
        
        fireStateChanged();
    }
    public void addChangeListener(ChangeListener listener) {
        listenerList.add(ChangeListener.class, listener);
    }
    
    public void removeChangeListener(ChangeListener listener) {
        listenerList.remove(ChangeListener.class, listener);
    }
    /**
     * Notify all listeners that have registered interest for
     * notification on this event type.
     */
    protected void fireStateChanged() {
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

    public boolean isSynchronous() {
        return false;
    }

}
