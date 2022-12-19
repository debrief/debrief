/*
 * @(#)MPOImageReaderSpi.java  1.0  2010-01-05
 * 
 * Copyright (c) 2010 Werner Randelshofer, Goldau, Switzerland.
 * All rights reserved.
 *
 * You may not use, copy or modify this file, except in compliance with the
 * license agreement you entered into with Werner Randelshofer.
 * For details see accompanying license terms.
 */
package org.monte.media.mpo;

import java.io.IOException;
import java.util.Locale;
import javax.imageio.ImageReader;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.stream.ImageInputStream;

/**
 * ImageIO service provider interface for images in the MultiPicture Object
 * format (MPO).
 * <p>
 * See: <a href="http://www.cipa.jp/english/hyoujunka/kikaku/pdf/DC-007_E.pdf">MPO Format Specification</a>.
 *
 * @author Werner Randelshofer
 * @version 1.0 2010-01-05 Created.
 */
public class MPOImageReaderSpi extends ImageReaderSpi {

    public MPOImageReaderSpi() {
        super("Werner Randelshofer",//vendor name
                "1.0",//version
                new String[]{"MPO"},//names
                new String[]{"mpo"},//suffixes,
                new String[]{"image/mpo"},// MIMETypes,
                "org.monte.media.mpo.MPOImageReader",// readerClassName,
                new Class[]{ImageInputStream.class},// inputTypes,
                null,// writerSpiNames,
                false,// supportsStandardStreamMetadataFormat,
                null,// nativeStreamMetadataFormatName,
                null,// nativeStreamMetadataFormatClassName,
                null,// extraStreamMetadataFormatNames,
                null,// extraStreamMetadataFormatClassNames,
                false,// supportsStandardImageMetadataFormat,
                 "com_sun_media_imageio_plugins_tiff_image_1.0",// nativeImageMetadataFormatName,
                null,// nativeImageMetadataFormatClassName,
                null,// extraImageMetadataFormatNames,
                null// extraImageMetadataFormatClassNames
                );
    }

    @Override
    public boolean canDecodeInput(Object source) throws IOException {
        if (source instanceof ImageInputStream) {
            ImageInputStream in = (ImageInputStream) source;
            in.mark();

            // Check if file starts with a JFIF SOI magic (0xffd8=-40)
            if (in.readShort() != -40) {
                in.reset();
                return false;
            }
            in.reset();
            return true;
        }
        return false;
    }

    @Override
    public ImageReader createReaderInstance(Object extension) throws IOException {
        return new MPOImageReader(this);
    }

    @Override
    public String getDescription(Locale locale) {
        return "MPO Image Reader";
    }
}
