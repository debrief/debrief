/*
 * @(#)BoundedRangeInputStream.java  1.1  2011-08-23
 *
 * Copyright (c) 1999-2011 Werner Randelshofer, Goldau, Switzerland.
 * All rights reserved.
 *
 * You may not use, copy or modify this file, except in compliance with the
 * license agreement you entered into with Werner Randelshofer.
 * For details see accompanying license terms.
 */
package org.monte.media.io;

import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.EventListenerList;
import javax.swing.BoundedRangeModel;
import java.io.*;

/**
 * This input stream implements the BoundedRangeModel and allows
 * the observation of the input reading process.
 * 
 * @author  Werner Randelshofer, Hausmatt 10, CH-6405 Goldau, Switzerland.
 * @version  1.1 2011-08-23 Set value works now. 
 * <br>1.0  1999-10-19
 */
public class BoundedRangeInputStream
        extends FilterInputStream
        implements BoundedRangeModel {

    private int nread = 0;
    private int size = 0;
    private boolean valueIsAdjusting;
    /**
     * Only one ChangeEvent is needed per model instance since the
     * event's only (read-only) state is the source property.  The source
     * of events generated here is always "this".
     */
    protected transient ChangeEvent changeEvent_ = null;
    /** The listeners waiting for model changes. */
    protected EventListenerList listenerList_ = new EventListenerList();

    /**
     * Create a new instance.
     */
    public BoundedRangeInputStream(InputStream in) {
        super(in);
        try {
            size = in.available();
        } catch (IOException ioe) {
            size = 0;
        }
    }

    /**
     * Overrides <code>FilterInputStream.read</code> 
     * to update the value after the read.
     */
    public int read()
            throws IOException {
        int c = in.read();
        if (c >= 0) {
            incrementValue(1);
        }
        return c;
    }

    /**
     * Overrides <code>FilterInputStream.read</code> 
     * to update the value after the read.
     */
    public int read(byte b[])
            throws IOException {
        int nr = in.read(b);
        incrementValue(nr);
        return nr;
    }

    /**
     * Overrides <code>FilterInputStream.read</code> 
     * to update the value after the read.
     */
    public int read(byte b[], int off, int len)
            throws IOException {
        int nr = in.read(b, off, len);
        incrementValue(nr);
        return nr;
    }

    /**
     * Overrides <code>FilterInputStream.skip</code> 
     * to update the value after the skip.
     */
    public long skip(long n) throws IOException {
        long nr = in.skip(n);
        incrementValue((int) nr);
        return nr;
    }

    /**
     * Overrides <code>FilterInputStream.reset</code> 
     * to reset the progress monitor as well as the stream.
     */
    @Override
    public synchronized void reset()
            throws IOException {
        in.reset();
        nread = size - in.available();
        fireStateChanged();
    }

    /**
     * Increments the extent by the indicated value.
     * Negative Increments are ignored.
     *
     * @param  inc  The incrementValue value.
     */
    private void incrementValue(int inc) {
        if (inc > 0) {
            nread += inc;
            if (nread > size) {
                size = nread;
            }
            fireStateChanged();
        }
    }

    /**
     * Returns the minimum acceptable value.
     *
     * @return the value of the minimum property
     * @see #setMinimum
     */
    @Override
    public int getMinimum() {
        return 0;
    }

    /**
     * Ignored: The minimum of an input stream is always zero.
     *
     * Sets the model's minimum to <I>newMinimum</I>.   The 
     * other three properties may be changed as well, to ensure 
     * that:
     * <pre>
     * minimum <= value <= value+extent <= maximum
     * </pre>
     * <p>
     * Notifies any listeners if the model changes.
     *
     * @param newMinimum the model's new minimum
     * @see #getMinimum
     * @see #addChangeListener
     */
    @Override
    public void setMinimum(int newMinimum) {
    }

    /**
     * Returns the model's maximum.  Note that the upper
     * limit on the model's value is (maximum - extent).
     *
     * @return the value of the maximum property.
     * @see #setMaximum
     * @see #setExtent
     */
    @Override
    public int getMaximum() {
        return size;
    }

    /**
     * Ignored: The maximum of an input stream can not be changed.
    #
     * Sets the model's maximum to <I>newMaximum</I>. The other 
     * three properties may be changed as well, to ensure that
     * <pre>
     * minimum <= value <= value+extent <= maximum
     * </pre>
     * <p>
     * Notifies any listeners if the model changes.
     *
     * @param newMaximum the model's new maximum
     * @see #getMaximum
     * @see #addChangeListener
     */
    @Override
    public void setMaximum(int newMaximum) {
        size = newMaximum;
        fireStateChanged();
    }

    /**
     * Returns the current read position.
     *
     * Returns the model's current value.  Note that the upper
     * limit on the model's value is <code>maximum - extent</code> 
     * and the lower limit is <code>minimum</code>.
     *
     * @return  the model's value
     * @see     #setValue
     */
    public int getValue() {
        return nread;
    }

    /**
     * Ignored: The value is always zero.
     *
     * Sets the model's current value to <code>newValue</code> if <code>newValue</code>
     * satisfies the model's constraints. Those constraints are:
     * <pre>
     * minimum <= value <= value+extent <= maximum
     * </pre>
     * Otherwise, if <code>newValue</code> is less than <code>minimum</code> 
     * it's set to <code>minimum</code>, if its greater than 
     * <code>maximum</code> then it's set to <code>maximum</code>, and 
     * if it's greater than <code>value+extent</code> then it's set to 
     * <code>value+extent</code>.
     * <p>
     * When a BoundedRange model is used with a scrollbar the value
     * specifies the origin of the scrollbar knob (aka the "thumb" or
     * "elevator").  The value usually represents the origin of the 
     * visible part of the object being scrolled.
     * <p>
     * Notifies any listeners if the model changes.
     *
     * @param newValue the model's new value
     * @see #getValue
     */
    @Override
    public void setValue(int newValue) {
        nread=newValue;
    }

    /**
     * This attribute indicates that any upcoming changes to the value
     * of the model should be considered a single event. This attribute
     * will be set to true at the start of a series of changes to the value,
     * and will be set to false when the value has finished changing.  Normally
     * this allows a listener to only take action when the final value change in
     * committed, instead of having to do updates for all intermediate values.
     * <p>
     * Sliders and scrollbars use this property when a drag is underway.
     * 
     * @param b true if the upcoming changes to the value property are part of a series
     */
    public void setValueIsAdjusting(boolean b) {
        valueIsAdjusting = b;
    }

    /**
     * Returns true if the current changes to the value property are part 
     * of a series of changes.
     * 
     * @return the valueIsAdjustingProperty.  
     * @see #setValueIsAdjusting
     */
    public boolean getValueIsAdjusting() {
        return valueIsAdjusting;
    }

    /**
     * Returns the model's extent, the length of the inner range that
     * begins at the model's value.  
     *
     * @return  the value of the model's extent property
     * @see     #setExtent
     * @see     #setValue
     */
    public int getExtent() {
        return 0;
    }

    /**
     * Ignored: The extent is always zero.
     *
     * The extent depends on the current read position.
     *
     * Sets the model's extent.  The <I>newExtent</I> is forced to 
     * be greater than or equal to zero and less than or equal to
     * maximum - value.   
     * <p>
     * When a BoundedRange model is used with a scrollbar the extent
     * defines the length of the scrollbar knob (aka the "thumb" or
     * "elevator").  The extent usually represents how much of the 
     * object being scrolled is visible. When used with a slider,
     * the extent determines how much the value can "jump", for
     * example when the user presses PgUp or PgDn.
     * <p>
     * Notifies any listeners if the model changes.
     *
     * @param  newExtent the model's new extent
     * @see #getExtent
     * @see #setValue
     */
    public void setExtent(int newExtent) {
    }

    /**
     * Ignored: All values depend on the input stream.
     *
     * This method sets all of the model's data with a single method call.
     * The method results in a single change event being generated. This is
     * convenient when you need to adjust all the model data simulaneously and
     * do not want individual change events to occur.
     *
     * @param value  an int giving the current value 
     * @param extent an int giving the amount by which the value can "jump"
     * @param min    an int giving the minimum value
     * @param max    an int giving the maximum value
     * @param adjusting a boolean, true if a series of changes are in
     *                    progress
     * 
     * @see #setValue
     * @see #setExtent
     * @see #setMinimum
     * @see #setMaximum
     * @see #setValueIsAdjusting
     */
    public void setRangeProperties(int value, int extent, int min, int max, boolean adjusting) {
    }

    /**
     * Adds a ChangeListener to the model's listener list.
     *
     * @param l the ChangeListener to add
     * @see #removeChangeListener
     */
    public void addChangeListener(ChangeListener l) {
        listenerList_.add(ChangeListener.class, l);
    }

    /**
     * Removes a ChangeListener.
     *
     * @param l the ChangeListener to remove
     * @see #addChangeListener
     * @see BoundedRangeModel#removeChangeListener
     */
    public void removeChangeListener(ChangeListener l) {
        listenerList_.remove(ChangeListener.class, l);
    }

    /** 
     * Run each ChangeListeners stateChanged() method.
     * 
     * @see #setRangeProperties
     * @see EventListenerList
     */
    protected void fireStateChanged() {
        Object[] listeners = listenerList_.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == ChangeListener.class) {
                if (changeEvent_ == null) {
                    changeEvent_ = new ChangeEvent(this);
                }
                ((ChangeListener) listeners[i + 1]).stateChanged(changeEvent_);
            }
        }
    }
}
