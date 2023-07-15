/*
 * @(#)TIFFTag.java  1.0  2010-07-24
 * 
 * Copyright (c) 2010 Werner Randelshofer, Goldau, Switzerland.
 * All rights reserved.
 *
 * You may not use, copy or modify this file, except in compliance with the
 * license agreement you entered into with Werner Randelshofer.
 * For details see accompanying license terms.
 */
package org.monte.media.tiff;

import org.monte.media.math.Rational;

/**
 * A class defining the notion of a TIFF tag. A TIFF tag is a key that may
 * appear in an Image File Directory (IFD). In the IFD each tag has some data
 * associated with it, which may consist of zero or more values of a given data
 * type. The combination of a tag and a value is known as an IFD Entry or TIFF
 * Field.
 * <p>
 * The actual tag values used in the root IFD of a standard ("baseline") tiff
 * stream are defined in the {@link BaselineTagSet} class.
 * <p>
 *
 * @author Werner Randelshofer
 * @version 1.0 2010-07-24 Created.
 */
public class TIFFTag {

    public final static int ASCII_MASK = 1 << IFDDataType.ASCII.getTypeNumber();
    public final static int BYTE_MASK = 1 << IFDDataType.BYTE.getTypeNumber();
    public final static int DOUBLE_MASK = 1 << IFDDataType.DOUBLE.getTypeNumber();
    public final static int FLOAT_MASK = 1 << IFDDataType.FLOAT.getTypeNumber();
    public final static int IFD_MASK = 1 << IFDDataType.IFD.getTypeNumber();
    public final static int LONG_MASK = 1 << IFDDataType.LONG.getTypeNumber();
    public final static int SHORT_MASK = 1 << IFDDataType.SHORT.getTypeNumber();
    public final static int RATIONAL_MASK = 1 << IFDDataType.RATIONAL.getTypeNumber();
    public final static int SBYTE_MASK = 1 << IFDDataType.BYTE.getTypeNumber();
    public final static int SLONG_MASK = 1 << IFDDataType.SLONG.getTypeNumber();
    public final static int SSHORT_MASK = 1 << IFDDataType.SSHORT.getTypeNumber();
    public final static int SRATIONAL_MASK = 1 << IFDDataType.SRATIONAL.getTypeNumber();
    public final static int UNDEFINED_MASK = 1 << IFDDataType.UNDEFINED.getTypeNumber();
    public final static int ALL_MASK = -1;
    private String name;
    private int number;
    private int dataTypes;
    private TagSet tagSet;
    private ValueFormatter formatter;

    /**
     * Constructs a TIFFTag with a given name, tag number, set of legal data types,
     * and TagSet to which it refers. The tagSet parameter will generally be
     * non-null only if this TIFFTag corresponds to a pointer to a TIFF IFD. In this
     * case tagSet will represent the set of TIFFTags which appear in the IFD
     * pointed to. A TIFFTag represents an IFD pointer if and only if tagSet is
     * non-null or the data type TIFF_IFD_POINTER is legal.
     * <p>
     * If there are mnemonic names to be associated with the legal data values for the
     * tag, addValueName() should be called on the new instance for each name.
     * <p>
     * See the documentation for getDataTypes() for an explanation of how the set of data types is to be converted into a bit mask.
     * @param name the name of the tag; may be null.
     * @param number the number used to represent the tag.
     * @param dataTypes a bit mask indicating the set of legal data types for this tag.
     * @param formatter a ValueFormatter for formatting data values.
     */
    public TIFFTag(String name,
            int number,
            int dataTypes,
            ValueFormatter formatter) {
        this.name = name;
        this.number = number;
        this.dataTypes = dataTypes;
        this.formatter = formatter;
    }

    /**
     * Constructs a TIFFTag with a given name, tag number, set of legal data types,
     * and TagSet to which it refers. The tagSet parameter will generally be
     * non-null only if this TIFFTag corresponds to a pointer to a TIFF IFD. In this
     * case tagSet will represent the set of TIFFTags which appear in the IFD
     * pointed to. A TIFFTag represents an IFD pointer if and only if tagSet is
     * non-null or the data type TIFF_IFD_POINTER is legal.
     * <p>
     * If there are mnemonic names to be associated with the legal data values for the
     * tag, addValueName() should be called on the new instance for each name.
     * <p>
     * See the documentation for getDataTypes() for an explanation of how the set of data types is to be converted into a bit mask.
     * @param name the name of the tag; may be null.
     * @param number the number used to represent the tag.
     * @param dataTypes a bit mask indicating the set of legal data types for this tag.
     */
    public TIFFTag(String name,
            int number,
            int dataTypes) {
        this(name, number, dataTypes, null);
    }

    /**
     * @param tagSet the TagSet to which this tag belongs; may be null.
     */
    /* package */ void setTagSet(TagSet tagSet) {
        this.tagSet = tagSet;
    }

    /** Returns the integer used to represent the tag. */
    public int getNumber() {
        return number;
    }

    /** Returns the name of the tag, or null if the name is not known. */
    public String getName() {
        return name;
    }

    public boolean isSynthetic() {
        return number < 0;
    }

    public IFDDataType getType(Object data) {
        int m = dataTypes;

        if (data != null && data.getClass().isArray()) {
            data = ((Object[]) data)[0];
        }

        for (int i = 0; i < 32; i++) {
            if ((m & 1) == 1) {
                switch (IFDDataType.valueOf(i)) {
                    case ASCII:
                        if (data == null
                                || (data instanceof String)) {
                            return IFDDataType.ASCII;
                        }
                        break;
                    case BYTE:
                        if (data == null
                                || (data instanceof Short)) {
                            return IFDDataType.BYTE;
                        }
                        break;
                    case DOUBLE:
                        if (data == null
                                || (data instanceof Double)) {
                            return IFDDataType.DOUBLE;
                        }
                        break;
                    case FLOAT:
                        if (data == null
                                || (data instanceof Float)) {
                            return IFDDataType.FLOAT;
                        }
                        break;
                    case IFD:
                        if (data == null
                                || (data instanceof Long)) {
                            return IFDDataType.IFD;
                        }
                        break;
                    case LONG:
                        if (data == null
                                || (data instanceof Long)) {
                            return IFDDataType.LONG;
                        }
                        break;
                    case RATIONAL:
                        if (data == null
                                || (data instanceof Rational)) {
                            return IFDDataType.RATIONAL;
                        }
                        break;
                    case SBYTE:
                        if (data == null
                                || (data instanceof Byte)) {
                            return IFDDataType.SBYTE;
                        }
                        break;
                    case SHORT:
                        if (data == null
                                || (data instanceof Integer)) {
                            return IFDDataType.SHORT;
                        }
                        break;
                    case SLONG:
                        if (data == null
                                || (data instanceof Integer)) {
                            return IFDDataType.SLONG;
                        }
                        break;
                    case SRATIONAL:
                        if (data == null
                                || (data instanceof Rational)) {
                            return IFDDataType.SRATIONAL;
                        }
                        break;
                    case SSHORT:
                        if (data == null
                                || (data instanceof Short)) {
                            return IFDDataType.SSHORT;
                        }
                        break;
                    case UNDEFINED:
                        if (data == null
                                || (data instanceof Byte)) {
                            return IFDDataType.UNDEFINED;
                        }
                        break;
                }

                return IFDDataType.valueOf(i);
            }
            m >>= 1;
        }
        return IFDDataType.UNDEFINED;
    }

    public Object prettyFormat(Object data) {
        return (formatter == null) ? data : formatter.prettyFormat(data);
    }

    public Object format(Object data) {
        return (formatter == null) ? data : formatter.format(data);
    }

    @Override
    public String toString() {
        return name;
    }

    public String getDescription(Object data) {
        return (formatter == null) ? null : formatter.descriptionFormat(data);
    }
}
