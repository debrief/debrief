package com.borlander.rac353542.bislider;

import java.text.NumberFormat;

/**
 * Instances of this interface are responsible for converting of double values
 * into user-friendly labels.
 */
public interface BiSliderLabelProvider {

    /**
     * @return label for given double value, <code>null</code> or empty values
     *         (including all-space values) are ignored.
     */
    public String getLabel(double value);

    /**
     * Most common instance, which converts double value to string according to
     * user local defaults.
     */
    public static final BiSliderLabelProvider TO_STRING = new BiSliderLabelProvider() {

        public final NumberFormat myFormat = NumberFormat.getNumberInstance();

        public String getLabel(double value) {
            return myFormat.format(value);
        }
    };
    /**
     * Trivial implememtation, which does not provide labels for any values.
     */
    public static final BiSliderLabelProvider DUMMY = new BiSliderLabelProvider() {

        public String getLabel(double value) {
            return "";
        }
    };
}
