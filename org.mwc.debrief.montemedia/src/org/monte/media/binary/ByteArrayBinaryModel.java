/*
 * @(#)ByteArrayBinaryModel.java  2.0  2010-04-09
 *
 * Copyright (c) 1999-2010 Werner Randelshofer, Goldau, Switzerland.
 * All rights reserved.
 *
 * You may not use, copy or modify this file, except in compliance with the
 * license agreement you entered into with Werner Randelshofer.
 * For details see accompanying license terms.
 */
package org.monte.media.binary;

import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

/**
 * Model for untyped binary data.
 *
 * @author  Werner Randelshofer, Hausmatt 10, CH-6405 Goldau, Switzerland
 * @version  1.1 2010-04-09 Refactored and renamed from BinaryModel to ByteArrayBinaryModel.
 * <br>1.0  1999-10-19
 */
public class ByteArrayBinaryModel implements BinaryModel {
    // The data is stored in runs of 256 bytes. So we do not
    // need a contiguous area of memory.

    /** Table of elements. */
    private Vector elemTable;
    /** Number of bytes in the model. */
    private long length;
    /** Size of an element. */
    private int elemSize = 1024;

    public ByteArrayBinaryModel() {
        elemTable = new Vector();
        length = 0;
    }

    public ByteArrayBinaryModel(byte[] data) {
        elemTable = new Vector();
        if (data == null || data.length == 0) {
            length = 0;
        } else {
            elemTable.addElement(data);
            length = elemSize = data.length;
        }
    }

    public ByteArrayBinaryModel(InputStream in)
            throws IOException {
        this();

        //in = new BufferedInputStream(in);

        byte[] elem = new byte[elemSize];
        int elemLen = 0;
        while (true) {
            int readLen = in.read(elem, elemLen, elemSize - elemLen);
            if (readLen == -1) {
                elemTable.addElement(elem);
                length += elemLen;
                break;
            }
            elemLen += readLen;
            if (elemLen == elemSize) {
                elemTable.addElement(elem);
                length += elemSize;
                elem = new byte[elemSize];
                elemLen = 0;
            }
        }
    }

    public long getLength() {
        return length;
    }

    /**
    Gets a sequence of bytes and copies them into the supplied byte array.

    @param offset the starting offset >= 0
    @param len the number of bytes >= 0 && <= size - offset
    @param target the target array to copy into
    @exception ArrayIndexOutOfBoundsException  Thrown if the area covered by
    the arguments is not contained in the model.
     */
    @Override
    public int getBytes(long offset, int len, byte[] target) {
        int off = (int) offset;
        if (len + offset > length) {
            len = (int) (length - offset);
        }

        // Compute the index of the element
        int index = off / elemSize;

        // Get the element.
        byte[] elem = (byte[]) elemTable.elementAt(index);

        // Count the number of bytes we transfer
        int count = 0;

        // Current index within the element
        int i = off % elemSize;

        // Copy until we are finished
        while (count < len) {
            if (i == elem.length) {
                elem = (byte[]) elemTable.elementAt(++index);
                i = 0;
            }
            target[count++] = elem[i++];
        }
        return count;
    }

    @Override
    public void close() {
        elemTable=null;
        length=0;
    }
}
