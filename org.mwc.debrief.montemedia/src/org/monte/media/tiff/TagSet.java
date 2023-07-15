/*
 * @(#)TagSet.java  1.0  2010-07-24
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
 * A class representing a set of TIFF tags. Each tag in the set must have a
 * unique number (this is a limitation of the TIFF specification itself).
 * <p>
 * This class and its subclasses are responsible for mapping between raw tag
 * numbers and TIFFTag objects, which contain additional information about each
 * tag, such as the tag's name, legal data types, and mnemonic names for some or
 * all of its data values.
 *
 * @author Werner Randelshofer
 * @version 1.0 2010-07-24 Created.
 */
public abstract class TagSet {

    private HashMap<Integer, TIFFTag> tagsByNumber = new HashMap<Integer, TIFFTag>();
    private String name;

    public TagSet(String name, TIFFTag[] tags) {
        this.name = name;
        for (TIFFTag tag : tags) {
            tag.setTagSet(this);
            tagsByNumber.put(tag.getNumber(), tag);
        }
    }

    /** Returns the TIFFTag from this set that is associated with the given
     * tag number.
     * <br>
     * Returns a TIFFTag with name "unknown" if the tag is not defined.
     */
    public TIFFTag getTag(int tagNumber) {
        TIFFTag tag=tagsByNumber.get(tagNumber);
        if (tag==null) {
            synchronized (this) {
                tag=tagsByNumber.get(tagNumber);
                if (tag==null) {
                    tag=new TIFFTag("unknown",tagNumber,TIFFTag.ALL_MASK,null);
                    tagsByNumber.put(tagNumber, tag);
                }
            }
        }
        return tag;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }
}
