package com.borlander.rac353542.bislider;

/**
 * Main read-only interface for access the current state of BiSlider. The user
 * is not intended to implement this interface directly, but use one of default
 * implementation instead.
 */
public interface BiSliderDataModel {

    /**
     * @return the least value which may be selected using this slider. The
     *         label for this value is placed at the left side of horizontal
     *         BISlider and at the bottom of vertical one.
     */
    public double getTotalMinimum();

    /**
     * @return the maximum value which may be selected using this slider. The
     *         label for this value is placed at the right side of horizontal
     *         BISlider and at the top of vertical one.
     */
    public double getTotalMaximum();

    /**
     * Convenience method. Fully equivalent to the
     * <code>getTotalMaximum() - getTotalMinimum()</code>
     */
    public double getTotalDelta();

    /**
     * @return the start of the range currently selected by user.
     */
    public double getUserMinimum();

    /**
     * @return the end of the range currently selected by user.
     */
    public double getUserMaximum();

    /**
     * Convenience method. Fully equivalent to the
     * <code>getUserMaximum() - getUserMinimum()</code>
     */
    public double getUserDelta();

    /**
     * @return the length of single segment
     */
    public double getSegmentLength();

    /**
     * Specifies precision of this data model.
     * <p>
     * NOTE: In contrast to other model parameters, this one specifies the
     * metadata for the whole set of datas that may be represented by this
     * model. Thus, the return value for this method can not be changed during
     * the whole lifecycle of model.
     * 
     * @return the precision of this data model, that is, the minimum delta
     *         between 2 values that should be considered as different.
     * 
     */
    public double getPrecision();

    /**
     * Register given listener to be notified on changes in the ui model.
     */
    public void addListener(Listener listener);

    /**
     * Unregister given listener from change notifications. It is safe to
     * <b>call</b> this method during change notification.
     */
    public void removeListener(Listener listener);

    public static interface Listener {

        public void dataModelChanged(BiSliderDataModel dataModel, boolean moreChangesExpectedInNearFuture);
    }

    /**
     * Extends the read-only <code>BiSliderDataModel</code> interface with
     * write operations. You may need this interface only if you want to provide
     * custom inplementation of data model.
     */
    public static interface Writable extends BiSliderDataModel {

        /**
         * Atomically changes both minimum and maximum user values. It is
         * different to set values separately due to different validation
         * strategy. In particlular, in case if currentMin &lt; currentMax &lt;
         * newMin &lt; newMax, then separate setting of the
         * <code>setUserMinimum(newMin); setUserMaximum(newMax)</code> will
         * select the range of [currentMinimum, newMaximum].
         */
        public void setUserRange(double userMin, double userMax);

        public void setUserMinimum(double userMinimum);

        public void setUserMaximum(double userMaximum);

        public void setSegmentCount(int segmentsCount);
        
        public void setSegmentLength(double segmentLength);
        
        public void startCompositeUpdate();
        
        public void finishCompositeUpdate();
    }
}
