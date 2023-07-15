/*
 * @(#)EnumValueFormatter.java  1.0  2010-03-22
 * 
 * Copyright (c) 2010 Werner Randelshofer, Goldau, Switzerland.
 * All rights reserved.
 *
 * You may not use, copy or modify this file, except in compliance with the
 * license agreement you entered into with Werner Randelshofer.
 * For details see accompanying license terms.
 */
package org.monte.media.tiff;

import java.util.HashMap;

/**
 * Formats integer values as an enumeration.
 *
 * @author Werner Randelshofer
 * @version 1.0 2010-03-22 Created.
 */
public class EnumValueFormatter implements ValueFormatter  {

    private HashMap<Integer, String> enumMap;
Enum eum;
    /** Creates a new enumeration.
     * The enumeration consists of a list of String=Integer pairs.
     */
    public EnumValueFormatter(Object... enumeration) {
        enumMap = new HashMap<Integer, String>();
        for (int i = 0; i < enumeration.length; i += 2) {
            String value = (String) enumeration[i];
            Integer key = (Integer) enumeration[i + 1];
            if (enumMap.containsKey(key)) {
            enumMap.put(key, enumMap.get(key)+", "+value);
            } else {
            enumMap.put(key, value);
            }
        }
    }

    @Override
    public Object format(Object value) {
        if (value instanceof Number) {
            int intValue = ((Number)value).intValue();
        if (enumMap.containsKey(intValue))  {
            return enumMap.get(intValue);
        }
            }
        return value;
    }
    @Override
    public Object prettyFormat(Object value) {
        if (value instanceof Number) {
            int intValue = ((Number) value).intValue();
            if (enumMap.containsKey(intValue)) {
                return "0x" + Integer.toHexString(intValue) + " [" + enumMap.get(intValue) + "]";
            }
        }
        return value;
    }
    @Override
    public String descriptionFormat(Object value) {
        if (value instanceof Number) {
            int intValue = ((Number) value).intValue();
            if (enumMap.containsKey(intValue)) {
                return enumMap.get(intValue);
            }
        }
        return null;
    }
}
