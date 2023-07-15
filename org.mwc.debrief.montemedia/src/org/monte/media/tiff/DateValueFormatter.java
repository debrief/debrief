/*
 * @(#)DateValueFormatter.java  1.0  2010-07-24
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
 * DateValueFormatter.
 *
 * @author Werner Randelshofer
 * @version 1.0 2010-07-24 Created.
 */
public class DateValueFormatter implements ValueFormatter {

    public DateValueFormatter() {
    }

    @Override
    public Object format(Object value) {
        return value;
    }

    @Override
    public Object prettyFormat(Object value) {
        return value;
    }

    @Override
    public String descriptionFormat(Object data) {
       return null;
    }
}
