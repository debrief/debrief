package com.borlander.rac353542.bislider;

import org.eclipse.swt.graphics.RGB;

/**
 * Provides BiSlider implementation with options affecting its appearance. User
 * may provide system with its own implementation or use default one provided in
 * the <code>DefaultBiSliderUIModel</code>.
 */
public interface BiSliderUIModel {

    public static interface Listener {

        public void uiModelChanged(BiSliderUIModel uiModel);
    }

    /**
     * Register given listener to be notified on changes in the ui model.
     */
    public void addListener(Listener listener);

    /**
     * Unregister given listener from change notifications. It is safe to
     * <b>call</b> this method during change notification.
     */
    public void removeListener(Listener listener);

    /**
     * @return <code>true</code> if BiSlider should be rendered vertically,
     *         with labels at the left and/or right side, <code>false</code>
     *         otherwise
     */
    public boolean isVertical();

    /**
     * @return the radius of the corners of rounded rectangle representing
     *         BiSlider's outline. If 0, control will be rendered as rectangle.
     */
    public int getArcRadius();

    /**
     * @return <code>true</code> if bislider should be rendered with labels
     *         above or at the left side, depending on the
     *         <code>isVertical</code> configuration option.
     */
    public boolean hasLabelsAboveOrLeft();

    /**
     * @return <code>true</code> if bislider should be rendered with labels
     *         below or at the rights side, depending on the
     *         <code>isVertical</code> configuration option.
     */
    public boolean hasLabelsBelowOrRight();
    
    /**
     * @return <code>true</code> if labels should be drawn vertically.
     */
    public boolean isVerticalLabels();

    /**
     * Defines the gap between control outline and environment. Control uses
     * this gap used to render its labels. I.e, this gap will be added to the
     * prefferred width for vertical and to the preffered height if BiSlider is
     * horizontal. This gap will be added only to that side(s) containing labels
     * depending on the value of <code>hasLabelsAboveOrLeft</code> and
     * <code>hasLabelsBelowOrRight</code> options.
     */
    public int getLabelInsets();

    /**
     * Defines the gap between not-labeled edge of control and control's
     * environment. I.e, this gap will be added to the prefferred height for
     * vertical and to the preffered width if BiSlider is horizontal.
     */
    public int getNonLabelInsets();

    /**
     * If BiSlider is configured to show colored segmented contents, this method
     * allows user to define the color for the least valued segment.
     */
    public RGB getMinimumRGB();

    /**
     * If BiSlider is configured to show colored segmented contents, this method
     * allows user to define the color for the max valued segment.
     */
    public RGB getMaximumRGB();

    /**
     * If BiSlider is configured to show colored segmented contents, this method
     * allows user to define the colors for all segments between the least and
     * max one, which are set explicitly.
     */
    public ColorInterpolation getColorInterpolation();

    /**
     * If BiSlider is configured to show colored segmented contents, this method
     * allows user to define the color for the segments which were rejected by
     * current user's selection.
     */
    public RGB getNotColoredSegmentRGB();

    /**
     * Defines the relative size of colored segments painted as BiSlider's
     * contents.
     */
    public BiSliderContentsDataProvider getContentsDataProvider();

    /**
     * Defines the foreground color of the BiSlider. Both control outline and
     * labels are rendered using this color.
     */
    public RGB getBiSliderForegroundRGB();

    /**
     * Defines the label provider used to draw all control labels.
     */
    public BiSliderLabelProvider getLabelProvider();

}
