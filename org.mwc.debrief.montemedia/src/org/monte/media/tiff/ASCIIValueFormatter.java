/*
 * @(#)IFDEnumFormatter.java  1.0  2010-03-22
 * 
 * Copyright (c) 2010 Werner Randelshofer, Goldau, Switzerland.
 * All rights reserved.
 *
 * You may not use, copy or modify this file, except in compliance with the
 * license agreement you entered into with Werner Randelshofer.
 * For details see accompanying license terms.
 */
package org.monte.media.tiff;

import java.io.UnsupportedEncodingException;

/**
 * Formats byte arrays as string.
 *
 * @author Werner Randelshofer
 * @version 1.0 2010-03-22 Created.
 */
public class ASCIIValueFormatter implements ValueFormatter  {

    /** Creates a new enumeration.
     * The enumeration consists of a list of String=Integer pairs.
     */
    public ASCIIValueFormatter() {
    }

    @Override
    public Object format(Object value) {
        if (value instanceof byte[]) {
            try {
                return new String((byte[]) value, "ASCII");
            } catch (UnsupportedEncodingException ex) {
                throw new InternalError("ASCII not supported");
            }
            }
        return value;
    }
    @Override
    public Object prettyFormat(Object value) {
        return format(value);
    }

    @Override
    public String descriptionFormat(Object data) {
       return null;
    }
}
