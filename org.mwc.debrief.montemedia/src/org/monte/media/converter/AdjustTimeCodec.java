/*
 * @(#)AdjustTimeCodec
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
 * Adjusts the time stamp of the media.
 *
 * @author Werner Randelshofer
 * @version $Id: AdjustTimeCodec.java 299 2013-01-03 07:40:18Z werner $
 */
public class AdjustTimeCodec extends AbstractCodec {

    private Rational mediaTime=new Rational(0);

    public AdjustTimeCodec() {
        super(new Format[]{
                    new Format(), //
                },
                new Format[]{
                    new Format(), //
                });
        name = "Adjust Time";
    }

    public Rational getMediaTime() {
        return mediaTime;
    }

    public void setMediaTime(Rational mediaTime) {
        this.mediaTime = mediaTime;
    }

    @Override
    public Format setInputFormat(Format f) {
        Format fNew = super.setInputFormat(f);
        outputFormat = fNew;
        return fNew;
    }

    @Override
    public int process(Buffer in, Buffer out) {
        out.setMetaTo(in);
        out.setDataTo(in);

            if (mediaTime != null) {
                out.timeStamp = mediaTime;
                mediaTime = mediaTime.add(out.sampleDuration.multiply(out.sampleCount));
            }

        return CODEC_OK;
    }
}
