/*
 * @(#)PBMImageReaderSpi.java 
 * 
 * Copyright (c) 2012 Werner Randelshofer, Goldau, Switzerland.
 * All rights reserved.
 *
 * You may not use, copy or modify this file, except in compliance with the
 * license agreement you entered into with Werner Randelshofer.
 * For details see accompanying license terms.
 */
package org.monte.media.pbm;

import org.monte.media.iff.IFFParser;
import java.io.IOException;
import java.util.Locale;
import javax.imageio.ImageReader;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.stream.ImageInputStream;

/**
 * ImageIO service provider interface for images in the Amiga IFF Interleaved
 * Bitmap image format (ILBM).
 *
 * @author Werner Randelshofer
 * @version $Id: PBMImageReaderSpi.java 299 2013-01-03 07:40:18Z werner $
 */
public class PBMImageReaderSpi extends ImageReaderSpi {
    protected final static int FORM_ID = IFFParser.stringToID("FORM");
    protected final static int CAT_ID = IFFParser.stringToID("CAT ");
    protected final static int LIST_ID = IFFParser.stringToID("LIST");
    protected final static int PBM_ID = IFFParser.stringToID("PBM ");

    public PBMImageReaderSpi() {
        super("Werner Randelshofer",//vendor name
                "1.0",//version
                new String[]{"PBM"},//names
                new String[]{"pbm","PBM","lbm","LBM"},//suffixes,
                new String[]{"image/pbm"},// MIMETypes,
                "org.monte.media.pbm.PBMImageReader",// readerClassName,
                new Class[]{ImageInputStream.class},// inputTypes,
                null,// writerSpiNames,
                false,// supportsStandardStreamMetadataFormat,
                null,// nativeStreamMetadataFormatName,
                null,// nativeStreamMetadataFormatClassName,
                null,// extraStreamMetadataFormatNames,
                null,// extraStreamMetadataFormatClassNames,
                false,// supportsStandardImageMetadataFormat,
                null,// nativeImageMetadataFormatName,
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

            // Check if file starts with "FORM", "CAT " or "LIST"
            int fileID = in.readInt();
            if (fileID != FORM_ID && fileID!=CAT_ID&&fileID!=LIST_ID) {
                in.reset();
                return false;
            }
            // Check if file content is "ILBM" or "ANIM"
            int contentSize = in.readInt();
            int contentID = in.readInt();
            if (contentID != PBM_ID ) {
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
        return new PBMImageReader(this);
    }

    @Override
    public String getDescription(Locale locale) {
        return "PBM Packed Bitmap";
    }
}
