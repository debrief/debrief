/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package com.borlander.rac353542.bislider;

/**
 * Instances of this interface are responsible for rendering of the BiSlider's
 * contents (which may be optionally rendered as a colored segmented outlines).
 * 
 */
public interface BiSliderContentsDataProvider {

    /**
     * @return the double value between 0.0 to 1.0. If 0.0, segment will not be
     *         drawn, if 1.0, segment will be completely filled by appropriate
     *         color.
     */
    public double getNormalValueAt(double totalMin, double totalMax, double segmentMin, double segmentMax);

    /**
     * Shared instance which may be used to fill all bislider segments.
     */
    public static final BiSliderContentsDataProvider FILL = new BiSliderContentsDataProvider() {

        public double getNormalValueAt(double totalMin, double totalMax, double segmentMin, double segmentMax) {
            return 1.0;
        }
    };
    /**
     * Shared instance which may be used to avoid colorisation of bislider
     * segments.
     */
    public static final BiSliderContentsDataProvider LEAVE_BLANK = new BiSliderContentsDataProvider() {

        public double getNormalValueAt(double totalMin, double totalMax, double segmentMin, double segmentMax) {
            return 1.0;
        }
    };
    /**
     * Shared instance which may be used to draw segments distributed normally.
     * Implemented primarily for demonstration purpose.
     */
    public static final BiSliderContentsDataProvider NORMAL_DISTRIBUTION = new BiSliderContentsDataProvider() {

        public double getNormalValueAt(double totalMin, double totalMax, double segmentMin, double segmentMax) {
            double arg = (segmentMin + segmentMax) / 2;
            double mean = (totalMin + totalMax) / 2;
            double delta = arg - mean;
            double totalDelta = (totalMax - totalMin) / 2;
            double divisor = totalDelta * totalDelta / (2 * 2) / Math.log(2.0);
            return Math.exp(-delta * delta / divisor);
        }
    };

    /**
     * Each segment will be drawn according to the value of some function in
     * segment's center
     */
    public static abstract class Adapter implements BiSliderContentsDataProvider {

        public double getNormalValueAt(double totalMin, double totalMax, double segmentMin, double segmentMax) {
            return getNormalValueAtPoint(totalMin, totalMax, (segmentMax + segmentMin) / 2);
        }

        public abstract double getNormalValueAtPoint(double totalMin, double totalMax, double point);
    }
}
