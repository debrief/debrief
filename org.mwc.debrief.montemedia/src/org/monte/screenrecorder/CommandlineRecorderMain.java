/*
 * @(#)CommandlineRecorderMain.java  1.0  2011-08-05
 * 
 * Copyright (c) 2011 Werner Randelshofer, Goldau, Switzerland.
 * All rights reserved.
 * 
 * You may not use, copy or modify this file, except in compliance with the
 * license agreement you entered into with Werner Randelshofer.
 * For details see accompanying license terms.
 */
package org.monte.screenrecorder;

import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.nio.ByteOrder;
import static org.monte.media.AudioFormatKeys.ByteOrderKey;
import static org.monte.media.AudioFormatKeys.ChannelsKey;
import static org.monte.media.AudioFormatKeys.ENCODING_QUICKTIME_TWOS_PCM;
import static org.monte.media.AudioFormatKeys.SampleRateKey;
import static org.monte.media.AudioFormatKeys.SampleSizeInBitsKey;
import static org.monte.media.AudioFormatKeys.SignedKey;
import org.monte.media.Format;
import org.monte.media.FormatKeys;
import org.monte.media.FormatKeys.MediaType;

import static org.monte.media.FormatKeys.EncodingKey;
import static org.monte.media.FormatKeys.FrameRateKey;
import static org.monte.media.FormatKeys.MIME_AVI;
import static org.monte.media.FormatKeys.MIME_QUICKTIME;
import static org.monte.media.FormatKeys.MediaTypeKey;
import static org.monte.media.FormatKeys.MimeTypeKey;
import static org.monte.media.VideoFormatKeys.COMPRESSOR_NAME_QUICKTIME_ANIMATION;
import static org.monte.media.VideoFormatKeys.CompressorNameKey;
import static org.monte.media.VideoFormatKeys.DepthKey;
import static org.monte.media.VideoFormatKeys.ENCODING_QUICKTIME_ANIMATION;
import org.monte.media.math.Rational;
import static org.monte.screenrecorder.ScreenRecorder.ENCODING_BLACK_CURSOR;

/**
 * {@code CommandlineRecorderMain}.
 *
 * @author Werner Randelshofer
 * @version 1.0 2011-08-05 Created.
 */
public class CommandlineRecorderMain {

    /**
     * FIXME - Add commandline arguments for recording time.
     * 
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        GraphicsConfiguration gc = GraphicsEnvironment//
                .getLocalGraphicsEnvironment()//
                .getDefaultScreenDevice()//
                .getDefaultConfiguration();
        // FIXME - Implement me
        ScreenRecorder sr = new ScreenRecorder(
                gc, null,
                // the file format
                new Format(MediaTypeKey, MediaType.FILE,
                MimeTypeKey, MIME_QUICKTIME),
                //
                // the output format for screen capture
                new Format(MediaTypeKey, MediaType.VIDEO,
                EncodingKey, ENCODING_QUICKTIME_ANIMATION,
                CompressorNameKey, COMPRESSOR_NAME_QUICKTIME_ANIMATION,
                DepthKey, 24, FrameRateKey, new Rational(15, 1)),
                //
                // the output format for mouse capture 
                new Format(MediaTypeKey, MediaType.VIDEO,
                EncodingKey, ENCODING_BLACK_CURSOR,
                FrameRateKey, new Rational(30, 1)),
                //
                // the output format for audio capture 
                /*new Format(MediaTypeKey, MediaType.AUDIO,
                EncodingKey, ENCODING_QUICKTIME_TWOS_PCM,
                FrameRateKey, new Rational(48000, 1),
                SampleSizeInBitsKey, 16,
                ChannelsKey, 2, SampleRateKey, new Rational(48000, 1),
                SignedKey, true, ByteOrderKey, ByteOrder.BIG_ENDIAN)*/ null);
        sr.start();
        Thread.sleep(5000);
        sr.stop();
    }
}
