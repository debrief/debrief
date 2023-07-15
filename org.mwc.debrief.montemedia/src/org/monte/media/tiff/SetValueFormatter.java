/*
 * @(#)IFDEnum.java  1.0  2010-03-22
 * 
 * Copyright (c) 2010 Werner Randelshofer, Goldau, Switzerland.
 * All rights reserved.
 *
 * You may not use, copy or modify this file, except in compliance with the
 * license agreement you entered into with Werner Randelshofer.
 * For details see accompanying license terms.
 */
package org.monte.media.tiff;

import java.util.HashSet;
import java.util.LinkedList;

/**
 * Formats integer values as a set.
 *
 * @author Werner Randelshofer
 * @version 1.0 2010-03-22 Created.
 */
public class SetValueFormatter implements ValueFormatter {

    /** Defines an entry of the set. */
    private class Entry {
        public Entry(String name, int bits) {
            this.name=name;
            this.bits=bits;
            this.mask=bits;
        }
        public Entry(String name, int bits, int mask) {
            this.name=name;
            this.bits=bits;
            this.mask=mask;
        }
        /* the bits that must be set. */
        int bits;
        /* a mask which is considered for the bits. */
        int mask;
        /* the descriptive name of the value. */
        String name;
    }
    private LinkedList<Entry> setDefinition;

    /** Creates a new enumeration.
     * The enumeration consists of a list of String=Integer or
     * String=Integer Integer pairs.
     * <p>
     * If only one integer is provided, it specifies the bits which must
     * be set.
     * If two integers are provided, the second value specifies a bit mask.
     */
    public SetValueFormatter(Object... set) {
        setDefinition = new LinkedList<Entry>();
        for (int i = 0; i < set.length; ) {
            if (i<set.length-2 && (set[i+2] instanceof Integer)) {
            setDefinition.add(new Entry((String) set[i], (Integer) set[i+1], (Integer) set[i+2]));
            i+=3;
            } else {
            setDefinition.add(new Entry((String) set[i], (Integer) set[i+1]));
            i+=2;
            }
        }
    }

    @Override
    public Object format(Object value) {
        if (value instanceof Number) {
            HashSet<String> setValue=new HashSet<String>();
            int intValue = ((Number) value).intValue();
            for (Entry elem :setDefinition) {
                if ((elem.mask&intValue)==elem.bits) {
                   setValue.add(elem.name);
                }
            }
            return setValue;
        }
        return value;
    }

    @Override
    public Object prettyFormat(Object value) {
        if (value instanceof Number) {
            int intValue = ((Number) value).intValue();
            StringBuilder prettyValue=new StringBuilder();
            for (Entry elem :setDefinition) {
                if ((elem.mask&intValue)==elem.bits) {
                    if (prettyValue.length()>0) {
                        prettyValue.append(',');
                    }
                   prettyValue.append(elem.name);
                }
            }
            prettyValue.insert(0, " {");
            prettyValue.insert(0,Integer.toHexString(intValue));
            prettyValue.insert(0, "0x");
            prettyValue.append("}");
            return prettyValue.toString();
        }
        return value;
    }
    @Override
    public String descriptionFormat(Object value) {
        if (value instanceof Number) {
            int intValue = ((Number) value).intValue();
            StringBuilder prettyValue=new StringBuilder();
            for (Entry elem :setDefinition) {
                if ((elem.mask&intValue)==elem.bits) {
                    if (prettyValue.length()>0) {
                        prettyValue.append(',');
                    }
                   prettyValue.append(elem.name);
                }
            }
            return prettyValue.toString();
        }
        return null;
    }
}
