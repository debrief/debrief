/*
 * @(#)TIFFField.java  1.0  2010-07-25
 * 
 * Copyright (c) 2010 Werner Randelshofer, Goldau, Switzerland.
 * All rights reserved.
 *
 * You may not use, copy or modify this file, except in compliance with the
 * license agreement you entered into with Werner Randelshofer.
 * For details see accompanying license terms.
 */
package org.monte.media.tiff;

import java.awt.image.BufferedImage;

/**
 * A field in a {@link TIFFDirectory}.
 *
 * @author Werner Randelshofer
 * @version 1.0 2010-07-25 Created.
 */
public class TIFFField extends TIFFNode {

    /** The data of this field. */
    private Object data;

    public TIFFField(TIFFTag tag, Object data) {
        super(tag);
        this.data = data;
    }

    public TIFFField(TIFFTag tag, Object data, IFDEntry entry) {
        super(tag);
        this.data = data;
        this.ifdEntry = entry;
    }

    /** Returns a description of the field. If known. */
    public String getDescription() {
        return getTag().getDescription(getData());
    }

    public IFDDataType getType() {
        if (ifdEntry != null) {
            return IFDDataType.valueOf(ifdEntry.getTypeNumber());
        } else {
            return getTag().getType(data);
        }
    }

    public long getCount() {
        if (ifdEntry != null) {
            return ifdEntry.getCount();
        } else if (data instanceof Object[]) {
            return ((Object[]) data).length;
        } else {
            return 1;
        }
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    @Override
    public String toString() {
        if (data==null) return super.toString();
        return "TIFFField "+tag+"="+ data.toString();
    }
}
