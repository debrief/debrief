/*
 * @(#)PassThroughCodec
 * 
 * Copyright (c) 2011 Werner Randelshofer, Goldau, Switzerland.
 * All rights reserved.
 * 
 * You may not use, copy or modify this file, except in compliance with the
 * license agreement you entered into with Werner Randelshofer.
 * For details see accompanying license terms. 
 */
package org.monte.media.converter;

import org.monte.media.AbstractCodec;
import org.monte.media.Buffer;
import org.monte.media.BufferFlag;
import org.monte.media.Format;
import org.monte.media.math.Rational;

/**
 * {@code PassThroughCodec} passes through all buffers.
 *
 * @author Werner Randelshofer
 * @version $Id: PassThroughCodec.java 299 2013-01-03 07:40:18Z werner $
 */
public class PassThroughCodec extends AbstractCodec {

    public PassThroughCodec() {
        super(new Format[]{
                    new Format(), //
                },
                new Format[]{
                    new Format(), //
                });
        name = "Pass Through";
    }

    @Override
    public Format setInputFormat(Format f) {
        Format fNew= super.setInputFormat(f);
        outputFormat=fNew;
        return fNew;
    }
    

    
    @Override
    public int process(Buffer in, Buffer out) {
        out.setMetaTo(in);
        out.setDataTo(in);
        return CODEC_OK;
    }
}
