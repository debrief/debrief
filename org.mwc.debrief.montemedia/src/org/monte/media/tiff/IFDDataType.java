/*
 * @(#)IFDDataType.java  1.0  2009-12-27
 * 
 * Copyright (c) 2009 Werner Randelshofer, Goldau, Switzerland.
 * All rights reserved.
 *
 * You may not use, copy or modify this file, except in compliance with the
 * license agreement you entered into with Werner Randelshofer.
 * For details see accompanying license terms.
 */
package org.monte.media.tiff;

import java.util.HashMap;

/**
 * Enumeration of TIFF IFD data types.
 * <p>
 * Sources:
 * <p>
 * TIFF TM Revision 6.0. Final — June 3, 1992.<br>
 * Adobe Systems Inc.<br>
 * <a href="http://www.exif.org/specifications.html">http://www.exif.org/specifications.html</a>
 * <p>
 * Adobe PageMaker® 6.0 TIFF Technical Notes - September 14, 1995<br>
 * Adobe Systems Inc.<br>
 * <a href="http://www.alternatiff.com/resources/TIFFPM6.pdf">http://www.alternatiff.com/resources/TIFFPM6.pdf</a>
 *
 *
 * @author werni
 */
public enum IFDDataType {

    /** 8-bit byte that contains a 7-bit ASCII code; the last byte
     * must be NUL (binary zero).
     * Represented by a String object in Java.
     */
    ASCII(2),
    //
    /** 8-bit unsigned integer.
     * Represented by a Short object in Java.
     */
    BYTE(1),
    //
    /** 16-bit (2-byte) unsigned integer.
     * Represented by an Int object in Java.
     */
    SHORT(3),
    //
    /** 32-bit (4-byte) unsigned integer.
     * Represented by a Long object in Java.
     */
    LONG(4),
    //
    /** Two LONGs: the first represents the numerator of a fraction; the second,
     * the denominator.
     * Represented by a Rational object in Java.
     */
    RATIONAL(5),
    //
    /** An 8-bit signed (twos-complement) integer.
     * Represented by a Byte object in Java.
     */
    SBYTE(6),
    //
    /** An 8-bit byte that may contain anything, depending on the definition of
     * the field.
     * Represented by a Byte object in Java.
     */
    UNDEFINED(7),
    //
    /**A 16-bit (2-byte) signed (twos-complement) integer.
     * Represented by a Short object in Java.
     */
    SSHORT(8),
    //
    /**A 32-bit (4-byte) signed (twos-complement) integer.
     * Represented by an Int object in Java.
     */
    SLONG(9),
    //
    /** Two SLONG’s: the first represents the numerator of a fraction, the 
     * second the denominator.
     * Represented by a Rational object in Java.
     */
    SRATIONAL(10),
    //
    /**Single precision (4-byte) IEEE format.
     * Represented by a Float object in Java.
     */
    FLOAT(11),
    //
    /** Double precision (8-byte) IEEE format.
     * Represented by a Double object in Java.
     */
    DOUBLE(12),
    /** 32-bit (4-byte) unsigned integer pointing to another IFD,
     * as defined in TIFF Tech Note 1 in TIFF Specification Supplement 1.
     * Represented by a Long object in Java.
     */
    IFD(13)
    ;
    //
    private final int typeNumber;
    private final static HashMap<Integer, IFDDataType> valueToFieldType = new HashMap<Integer, IFDDataType>();

    static {
        for (IFDDataType t : IFDDataType.values()) {
            valueToFieldType.put(t.getTypeNumber(), t);
        }
    }

    private IFDDataType(int typeNumber) {
        this.typeNumber = typeNumber;
    }

    public int getTypeNumber() {
        return typeNumber;
    }

    /** Gets the tag for the specified value. */
    public static IFDDataType valueOf(int typeNumber) {
        return valueToFieldType.get(typeNumber);
    }

}
