/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)

 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
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
