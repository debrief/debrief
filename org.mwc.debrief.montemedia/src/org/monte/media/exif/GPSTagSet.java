/*
 * @(#)BaselineTIFFTagSet.java  1.0  2010-07-24
 * 
 * Copyright (c) 2010 Werner Randelshofer, Goldau, Switzerland.
 * All rights reserved.
 *
 * You may not use, copy or modify this file, except in compliance with the
 * license agreement you entered into with Werner Randelshofer.
 * For details see accompanying license terms.
 */
package org.monte.media.exif;

import org.monte.media.tiff.TagSet;
import org.monte.media.tiff.SetValueFormatter;
import org.monte.media.tiff.*;
import static org.monte.media.tiff.TIFFTag.*;

/**
 * Enumeration of GPS EXIF tags.
 * <p>
 * Source:
 * <p>
 * Exchangeable image file format for digital still cameras: EXIF Version 2.2.
 * (April, 2002). Standard of Japan Electronics and Information Technology
 * Industries Association. JEITA CP-3451.
 * <a href="http://www.exif.org/Exif2-2.PDF">http://www.exif.org/Exif2-2.PDF</a>
 * 
 * @author Werner Randelshofer
 * @version 1.0 2010-07-24 Created.
 */
public class GPSTagSet extends TagSet {

    private static GPSTagSet instance;

    private GPSTagSet(TIFFTag[] tags) {
        super("GPS", tags);
    }

    /** Returns a shared instance of a BaselineTIFFTagSet. */
    public static GPSTagSet getInstance() {
        if (instance == null) {
            TIFFTag[] tags = {//
                // Tags Relating to GPS
                new TIFFTag("GPSVersionID", 0x0, BYTE_MASK),
                new TIFFTag("GPSLatitudeRef", 0x1, ASCII_MASK),
                new TIFFTag("GPSLatitude", 0x2, RATIONAL_MASK),
                new TIFFTag("GPSLongitudeRef", 0x3, ASCII_MASK),
                new TIFFTag("GPSLongitude", 0x4, RATIONAL_MASK),
                new TIFFTag("GPSAltitudeRef", 0x5, BYTE_MASK, new SetValueFormatter(//
                        "aboveSeaLevel",0,//
                        "belowSeaLevel",1//
                        )),
                new TIFFTag("GPSAltitude", 0x6, RATIONAL_MASK),
                new TIFFTag("GPSTimeStamp", 0x7, RATIONAL_MASK),
                new TIFFTag("GPSSatellites", 0x8, ASCII_MASK),
                new TIFFTag("GPSStatus", 0x9, ASCII_MASK),
                new TIFFTag("GPSMeasureMode", 0xa, ASCII_MASK),
                new TIFFTag("GPSDOP", 0xb, RATIONAL_MASK),
                new TIFFTag("GPSSpeedRef", 0xc, ASCII_MASK),
                new TIFFTag("GPSTrackRef", 0xe, ASCII_MASK),
                new TIFFTag("GPSTrack", 0xf, RATIONAL_MASK),
                new TIFFTag("GPSImgDirectionRef", 0x10, ASCII_MASK),
                new TIFFTag("GPSImgDirection", 0x011, RATIONAL_MASK),
                new TIFFTag("GPSMapDatum", 0x12, ASCII_MASK),
                new TIFFTag("GPSDestLatitudeRef", 0x13, ASCII_MASK),
                new TIFFTag("GPSDestLatitude", 0x14, RATIONAL_MASK),
                new TIFFTag("GPSDestLongitudeRef", 0x15, ASCII_MASK),
                new TIFFTag("GPSDestLongitude", 0x16, RATIONAL_MASK),
                new TIFFTag("GPSDestBearingRef", 0x17, ASCII_MASK),
                new TIFFTag("GPSDestBearing", 0x18, RATIONAL_MASK),
                new TIFFTag("GPSDestDistanceRef", 0x19, ASCII_MASK),
                new TIFFTag("GPSDestDistance", 0x1a, RATIONAL_MASK),
                new TIFFTag("GPSProcessingMethod", 0x1b, UNDEFINED_MASK),
                new TIFFTag("GPSAreaInformation", 0x1c, UNDEFINED_MASK),
                new TIFFTag("GPSDateStamp", 0x1d, ASCII_MASK),
                new TIFFTag("GPSDifferential", 0x1e, SHORT_MASK), //
            };
            instance = new GPSTagSet(tags);

        }
        return instance;
    }
}
