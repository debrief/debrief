/*
 * @(#)BitmapCodec.java  1.0  2011-02-20
 * 
 * Copyright (c) 2011 Werner Randelshofer, Goldau, Switzerland.
 * All rights reserved.
 * 
 * You may not use, copy or modify this file, except in compliance with the
 * license agreement you entered into with Werner Randelshofer.
 * For details see accompanying license terms.
 */
package org.monte.media.image;

import org.monte.media.AbstractVideoCodec;
import org.monte.media.Buffer;
import org.monte.media.Codec;
import org.monte.media.Format;
import org.monte.media.ilbm.ColorCyclingMemoryImageSource;
import org.monte.media.ilbm.ILBMDecoder;
import org.monte.media.pbm.PBMDecoder;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import static org.monte.media.VideoFormatKeys.*;
import static org.monte.media.BufferFlag.*;

/**
 * Decodes media data into a {@code Bitmap}.
 *
 * @author Werner Randelshofer
 * @version 1.0 2011-02-20 Created.
 */
public class BitmapCodec extends AbstractVideoCodec {
  public BitmapCodec() {
        super(new Format[]{
                    new Format(MediaTypeKey, MediaType.VIDEO, MimeTypeKey, MIME_JAVA,
                    EncodingKey, ENCODING_BUFFERED_IMAGE), //
                },
                new Format[]{
                    new Format(MediaTypeKey, MediaType.VIDEO, MimeTypeKey, MIME_JAVA,
                    EncodingKey, ENCODING_BITMAP_IMAGE, DataClassKey, BitmapImage.class), //
                });
    }

    @Override
    public int process(Buffer in, Buffer out) {
        out.setMetaTo(in);
        if (in.isFlag(DISCARD)) {
            return CODEC_OK;
        }
        out.format = outputFormat;
        try {
            out.setFlag(KEYFRAME);

            if (in.data instanceof File) {
                File f = (File) in.data;
                boolean success;
                {
                    InputStream ins = new BufferedInputStream(new FileInputStream(f));
                    try {
                        ILBMDecoder d = new ILBMDecoder(ins);
                        ArrayList<BitmapImage> imgs = d.produceBitmaps();
                        BitmapImage img = imgs.get(0);
                        out.data = img;
                        success = true;
                    } catch (IOException e) {
                        success = false;
                    } finally {
                        ins.close();
                    }
                }
                if (!success) {
                    InputStream ins = new BufferedInputStream(new FileInputStream(f));
                    try {
                        PBMDecoder d = new PBMDecoder(ins);
                        ArrayList<ColorCyclingMemoryImageSource> imgs = d.produce();
                        ColorCyclingMemoryImageSource mis = imgs.get(0);

                        out.data = BitmapImageFactory.toBitmapImage(mis);
                        success = true;
                    } catch (IOException e) {
                        success = false;
                    } finally {
                        ins.close();
                    }
                }
                if (!success) {
                    BufferedImage img = ImageIO.read(f);
                    out.data = BitmapImageFactory.toBitmapImage(img);
                    success = true;
                }
            } else if (in.data instanceof BitmapImage) {
                out.data = in.data;
            } else if (in.data instanceof BufferedImage) {
                out.data = BitmapImageFactory.toBitmapImage((BufferedImage) in.data);
            }
            return CODEC_OK;
        } catch (IOException e) {
            e.printStackTrace();
            out.setFlag(DISCARD);
            return CODEC_FAILED;
        }
    }
}
