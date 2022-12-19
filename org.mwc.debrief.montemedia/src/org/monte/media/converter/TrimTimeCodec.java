/*
 * @(#)TrimCodec
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
 * {@code PassThroughCodec} passes through all buffers in the specified time
 * range.
 *
 * @author Werner Randelshofer
 * @version $Id: TrimTimeCodec.java 299 2013-01-03 07:40:18Z werner $
 */
public class TrimTimeCodec extends AbstractCodec {

    private Rational startTime;
    private Rational endTime;

    public TrimTimeCodec() {
        super(new Format[]{
                    new Format(), //
                },
                new Format[]{
                    new Format(), //
                });
        name = "Trim Time";
    }

    /**
     * Sets the start time of the buffers.
     *
     * @param newValue Start time. Specify null, to pass through all buffers.
     */
    public void setStartTime(Rational newValue) {
        startTime = newValue;
    }

    public Rational getStartTime() {
        return startTime;
    }

    public Rational getEndTime() {
        return endTime;
    }

    /**
     * Sets the end time of the buffers.
     *
     * @param newValue Start time. Specify null, to pass through all buffers.
     */
    public void setEndTime(Rational newValue) {
        this.endTime = newValue;
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

        Rational bufStartTS = out.timeStamp;
        Rational bufEndTS = out.timeStamp.add(out.sampleDuration.multiply(out.sampleCount));

        if (!out.isFlag(BufferFlag.DISCARD)
                && startTime != null) {
            if (bufEndTS.compareTo(startTime) <= 0) {
                // Buffer is fully outside time range
                out.setFlag(BufferFlag.DISCARD);
            } else if (bufStartTS.compareTo(startTime) < 0) {
                // Buffer is partially outside time range
                if (out.data instanceof byte[]) {
                    int removeCount = (startTime.subtract(bufStartTS)).divide(out.sampleDuration).intValue();
                    removeCount = Math.max(0, Math.min(removeCount, out.sampleCount - 1));
                    int sampleSize = (out.length - out.offset) / out.sampleCount;
                    out.offset += removeCount * sampleSize;
                    out.length -= removeCount * sampleSize;
                    out.timeStamp = out.timeStamp.add(out.sampleDuration.multiply(removeCount));
                    out.sampleCount = out.sampleCount - removeCount;
                }
            } else {
                // Buffer is fully inside time range
            }
        }
        if (!out.isFlag(BufferFlag.DISCARD)
                && endTime != null) {
            if (bufStartTS.compareTo(endTime) >= 0) {
                // Buffer is fully outside time range
                out.setFlag(BufferFlag.DISCARD);
            } else if (bufEndTS.compareTo(endTime) > 0) {
                // Buffer is partially outside time range
                int removeCount = (bufEndTS.subtract(endTime)).divide(out.sampleDuration).intValue();
                removeCount = Math.max(0, Math.min(removeCount, out.sampleCount - 1));
                int sampleSize = (out.length - out.offset) / out.sampleCount;
                out.length -= removeCount * sampleSize;
                out.sampleCount = out.sampleCount - removeCount;
            } else {
                // Buffer is fully inside time range
            }
        }
        if (!out.isFlag(BufferFlag.DISCARD)) {
            out.setDataTo(in);
        }

        return CODEC_OK;
    }
}
