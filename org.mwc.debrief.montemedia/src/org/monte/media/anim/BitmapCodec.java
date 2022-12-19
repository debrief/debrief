/*
 * @(#)BitmapCodec.java  1.0  2011-09-04
 * 
 * Copyright (c) 2011 Werner Randelshofer, Goldau, Switzerland.
 * All rights reserved.
 * 
 * You may not use, copy or modify this file, except in compliance with the
 * license agreement you entered into with Werner Randelshofer.
 * For details see accompanying license terms.
 */
package org.monte.media.anim;

import org.monte.media.AbstractVideoCodec;
import org.monte.media.Buffer;
import org.monte.media.Format;
import org.monte.media.image.BitmapImage;
import java.awt.image.BufferedImage;
import static org.monte.media.anim.AmigaVideoFormatKeys.*;
import static org.monte.media.BufferFlag.*;

/**
 * Converts BufferedImage to BitmapImage. 
 *
 * @author Werner Randelshofer
 * @version 1.0 2011-09-04 Created.
 */
public class BitmapCodec extends AbstractVideoCodec {

    public BitmapCodec() {
        super(new Format[]{
                    new Format(MediaTypeKey, MediaType.VIDEO, MimeTypeKey, MIME_ANIM,
                    EncodingKey, ENCODING_ANIM_OP5, DataClassKey, byte[].class, FixedFrameRateKey, false), //
                },
                new Format[]{
                    new Format(MediaTypeKey, MediaType.VIDEO, MimeTypeKey, MIME_JAVA, 
                            EncodingKey, ENCODING_BUFFERED_IMAGE, FixedFrameRateKey, false), //
                });
        name="ILBM Codec";
    }

    @Override
    public int process(Buffer in, Buffer out) {
        out.setMetaTo(in);
        if (in.isFlag(DISCARD)) {
            return CODEC_OK;
        }
        out.format=outputFormat;

        BufferedImage pixmap = (BufferedImage) in.data;
        Format vf = (Format) outputFormat;
        BitmapImage bitmap = out.data instanceof BitmapImage ? (BitmapImage) out.data : null;
        if (bitmap == null || bitmap.getWidth() != vf.get(WidthKey)
                || bitmap.getHeight() != vf.get(HeightKey) || bitmap.getDepth() != vf.get(DepthKey)) {
            bitmap = new BitmapImage(vf.get(WidthKey), vf.get(HeightKey), vf.get(DepthKey), pixmap.getColorModel());
            out.data = bitmap;
        }
        bitmap.setPlanarColorModel(pixmap.getColorModel());
        bitmap.convertFromChunky(pixmap);


        return CODEC_OK;
    }
}
