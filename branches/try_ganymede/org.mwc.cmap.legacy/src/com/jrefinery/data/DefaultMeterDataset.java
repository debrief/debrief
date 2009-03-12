/* ============================================
 * JFreeChart : a free Java chart class library
 * ============================================
 *
 * Project Info:  http://www.object-refinery.com/jfreechart/index.html
 * Project Lead:  David Gilbert (david.gilbert@object-refinery.com);
 *
 * (C) Copyright 2000-2002, by Simba Management Limited and Contributors.
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 *
 * ------------------------
 * DefaultMeterDataset.java
 * ------------------------
 * (C) Copyright 2002, by Hari and Contributors.
 *
 * Original Author:  Hari;
 * Contributor(s):   David Gilbert (for Simba Management Limited);
 *
 * $Id: DefaultMeterDataset.java,v 1.1.1.1 2003/07/17 10:06:51 Ian.Mayo Exp $
 *
 * Changes
 * -------
 * 02-Apr-2002 : Version 1, based on code contributed by Hari (DG);
 * 16-Apr-2002 : Updated to the latest version from Hari (DG);
 *
 */

package com.jrefinery.data;

/**
 * A default implementation of the MeterDataset interface.
 *
 * @author Hari
 */
public class DefaultMeterDataset extends AbstractDataset implements MeterDataset {

    /** The default adjustment. */
    private static final double DEFAULT_ADJ = 1.0;

    /** The current value. */
    private Number value;

    /** The lower bound of the overall range. */
    private Number min;

    /** The upper bound of the overall range. */
    private Number max;

    /** The lower bound of the 'normal' range. */
    private Number minNormal;

    /** The upper bound of the 'normal' range. */
    private Number maxNormal;

    /** The lower bound of the 'warning' range. */
    private Number minWarning;

    /** The upper bound of the 'warning' range. */
    private Number maxWarning;

    /** The lower bound of the 'critical' range. */
    private Number minCritical;

    /** The upper bound of the 'critical' range. */
    private Number maxCritical;

    /** The border type. */
    private int borderType;

    /** The units. */
    private String units;

    /**
     * Default constructor.
     */
    public DefaultMeterDataset() {
        this(new Double(0), new Double(0), null, null);
    }

    /**
     * Creates a new dataset.
     *
     * @param min  the minimum value.
     * @param max  the maximum value.
     * @param value  the current value.
     * @param units  the unit description.
     */
    public DefaultMeterDataset(Number min, Number max, Number value, String units) {
        this(min, max, value, units, null, null, null, null, null, null, FULL_DATA);
    }

    /**
     * Creates a new dataset.
     *
     * @param min  the lower bound for the overall range.
     * @param max  the upper bound for the overall range.
     * @param value  the current value.
     * @param units  the unit description.
     * @param minCritical  the minimum critical value.
     * @param maxCritical  the maximum critical value.
     * @param minWarning  the minimum warning value.
     * @param maxWarning  the maximum warning value.
     * @param minNormal  the minimum normal value.
     * @param maxNormal  the maximum normal value.
     * @param borderType  the border type.
     */
    public DefaultMeterDataset(Number min, Number max, Number value,
                               String units,
                               Number minCritical, Number maxCritical,
                               Number minWarning, Number maxWarning,
                               Number minNormal, Number maxNormal,
                               int borderType) {

        setRange(min, max);
        setValue(value);
        setUnits(units);
        setCriticalRange(minCritical, maxCritical);
        setWarningRange(minWarning, maxWarning);
        setNormalRange(minNormal, maxNormal);
        setBorderType(borderType);

    }

    /**
     * Returns true if the value is valid, and false otherwise.
     *
     * @return boolean.
     */
    public boolean isValueValid() {
        return (value != null);
    }

    /**
     * Returns the value.
     *
     * @return the value.
     */
    public Number getValue() {
        return value;
    }

    /**
     * Sets the value.
     *
     * @param value  the new value.
     */
    public void setValue(double value) {
        setValue(new Double(value));
    }

    /**
     * Sets the value for the dataset.
     *
     * @param value  the new value.
     */
    public void setValue(Number value) {

        if (value != null && min != null && max != null) {
            if (value.doubleValue() < min.doubleValue()
                    || value.doubleValue() > max.doubleValue()) {

                throw new IllegalArgumentException("Value is out of range for min/max");

            }
        }
        this.value = value;
        if (value != null && min != null && max != null) {
                if (min.doubleValue() == max.doubleValue()) {
                        min = new Double(value.doubleValue() - DEFAULT_ADJ);
                    max = new Double(value.doubleValue() + DEFAULT_ADJ);
                }
        }
        fireDatasetChanged();

    }

    /**
     * Returns the minimum value.
     *
     * @return the minimum value.
     */
    public Number getMinimumValue() {
        return min;
    }

    /**
     * Returns the maximum value.
     *
     * @return the maximum value.
     */
    public Number getMaximumValue() {
        return max;
    }

    /**
     * Returns the minimum normal value.
     *
     * @return the minimum normal value.
     */
    public Number getMinimumNormalValue() {
        return minNormal;
    }

    /**
     * Returns the maximum normal value.
     *
     * @return the maximum normal value.
     */
    public Number getMaximumNormalValue() {
        return maxNormal;
    }

    /**
     * Returns the minimum warning value.
     *
     * @return the minimum warning value.
     */
    public Number getMinimumWarningValue() {
        return minWarning;
    }

    /**
     * Returns the maximum warning value.
     *
     * @return the maximum warning value.
     */
    public Number getMaximumWarningValue() {
        return maxWarning;
    }

    /**
     * Returns the minimum critical value.
     *
     * @return the minimum critical value.
     */
    public Number getMinimumCriticalValue() {
        return minCritical;
    }

    /**
     * Returns the maximum critical value.
     *
     * @return the maximum critical value.
     */
    public Number getMaximumCriticalValue() {
        return maxCritical;
    }

    /**
     * Sets the range for the dataset.  Registered listeners are notified of the change.
     *
     * @param min  the new minimum.
     * @param max  the new maximum.
     */
    public void setRange(Number min, Number max) {

        if (min == null || max == null) {
            throw new IllegalArgumentException("Min/Max should not be null");
        }

        // swap min and max if necessary...
        if (min.doubleValue() > max.doubleValue()) {
            Number temp = min;
            min = max;
            max = temp;
        }

        if (this.value != null) {
            if (min.doubleValue() == max.doubleValue()) {
                min = new Double(value.doubleValue() - DEFAULT_ADJ);
                max = new Double(value.doubleValue() + DEFAULT_ADJ);
            }
        }
        this.min = min;
        this.max = max;
        fireDatasetChanged();

    }

    /**
     * Sets the normal range for the dataset.  Registered listeners are
     * notified of the change.
     *
     * @param minNormal  the new minimum.
     * @param maxNormal  the new maximum.
     */
    public void setNormalRange(Number minNormal, Number maxNormal) {

        this.minNormal = minNormal;
        this.maxNormal = maxNormal;

        if (this.minNormal != null && this.minNormal.doubleValue() < this.min.doubleValue()) {
            this.min = this.minNormal;
        }
        if (this.maxNormal != null && this.maxNormal.doubleValue() > this.max.doubleValue()) {
            this.max = this.maxNormal;
        }
        fireDatasetChanged();
    }

    /**
     * Sets the warning range for the dataset.  Registered listeners are
     * notified of the change.
     *
     * @param minWarning  the new minimum.
     * @param maxWarning  the new maximum.
     */
    public void setWarningRange(Number minWarning, Number maxWarning) {

        this.minWarning = minWarning;
        this.maxWarning = maxWarning;

        if (this.minWarning != null && this.minWarning.doubleValue() < this.min.doubleValue()) {
            this.min = this.minWarning;
        }
        if (this.maxWarning != null && this.maxWarning.doubleValue() > this.max.doubleValue()) {
            this.max = this.maxWarning;
        }
        fireDatasetChanged();

    }

    /**
     * Sets the critical range for the dataset.  Registered listeners are
     * notified of the change.
     *
     * @param minCritical  the new minimum.
     * @param maxCritical  the new maximum.
     */
    public void setCriticalRange(Number minCritical, Number maxCritical) {

        this.minCritical = minCritical;
        this.maxCritical = maxCritical;

        if (this.minCritical != null && this.minCritical.doubleValue() < this.min.doubleValue()) {
            this.min = this.minCritical;
        }
        if (this.maxCritical != null && this.maxCritical.doubleValue() > this.max.doubleValue()) {
            this.max = this.maxCritical;
        }
        fireDatasetChanged();

    }

    /**
     * Returns the measurement units for the data.
     *
     * @return The measurement units.
     */
    public String getUnits() {
        return units;
    }

    /**
     * Sets the measurement unit description.
     *
     * @param units  the new description.
     */
    public void setUnits(String units) {
        this.units = units;
        fireDatasetChanged();
    }

    /**
     * Returns the border type.
     *
     * @return the border type.
     */
    public int getBorderType() {
        return borderType;
    }

    /**
     * Sets the border type.
     *
     * @param borderType the new border type.
     */
    public void setBorderType(int borderType) {
        this.borderType = borderType;
        fireDatasetChanged();
    }

}
