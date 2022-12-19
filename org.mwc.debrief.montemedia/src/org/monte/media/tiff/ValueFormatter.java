/*
 * @(#)ValueFormatter.java  1.0  2010-03-22
 * 
 * Copyright (c) 2010 Werner Randelshofer, Goldau, Switzerland.
 * All rights reserved.
 *
 * You may not use, copy or modify this file, except in compliance with the
 * license agreement you entered into with Werner Randelshofer.
 * For details see accompanying license terms.
 */

package org.monte.media.tiff;

/**
 * ValueFormatter.
 *
 * @author Werner Randelshofer
 * @version 1.0 2010-03-22 Created.
 */
public interface ValueFormatter {
    /** Formats the specified value.
     * If the value is of the desired type, it is replaced by an object
     * which can be handled easier. For example, an integer value by a descriptive
     * String.
     */
    public Object format(Object value);
    /** Formats the specified value in a human readable format. */
    public Object prettyFormat(Object value);
    /** Describes the data. Returns null if no description is available. */
    public String descriptionFormat(Object data);
}
