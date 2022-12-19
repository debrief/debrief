/*
 * @(#)BinaryModel.java  2.0  2010-04-09
 *
 * Copyright (c) 1999-2010 Werner Randelshofer, Goldau, Switzerland.
 * All rights reserved.
 *
 * You may not use, copy or modify this file, except in compliance with the
 * license agreement you entered into with Werner Randelshofer.
 * For details see accompanying license terms.
 */
package org.monte.media.binary;

/**
 * Model for untyped binary data.
 *
 * @author  Werner Randelshofer, Hausmatt 10, CH-6405 Goldau, Switzerland
 * @version  2.1 2010-04-09 Refactored into an interface.
 * <br>1.0  1999-10-19
 */
public interface BinaryModel {
    /** Returns the total length of the binary data. */
    public long getLength();
    /**
    Gets a sequence of bytes and copies them into the supplied byte array.

    @param off the starting offset >= 0
    @param len the number of bytes >= 0 && <= size - offset
    @param target the target array to copy into
    @exception ArrayIndexOutOfBoundsException  Thrown if the area covered by
    the arguments is not contained in the model.
     */
    public int getBytes(long off, int len, byte[] target);

    /** Closes the model and disposes all resources. */
    public void close();
}
