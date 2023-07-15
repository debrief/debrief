/**
 * @(#)Main.java  
 *
 * Copyright (c) 2013 Werner Randelshofer, Goldau, Switzerland.
 * All rights reserved.
 *
 * You may not use, copy or modify this file, except in compliance with the
 * license agreement you entered into with Werner Randelshofer.
 * For details see accompanying license terms.
 */
package org.monte.quicktimedemo;

import org.monte.quicktimedemo.*;
import org.monte.media.quicktime.QuickTimeReader;
import org.monte.media.Buffer;
import org.monte.media.math.Rational;
import org.monte.media.quicktime.QuickTimeWriter;
import org.monte.media.Format;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;
import java.io.*;
import java.util.Random;
import static org.monte.media.VideoFormatKeys.*;

/**
 * Demonstrates the use of {@link QuickTimeReader} and {@link QuickTimeWriter}.
 *
 * @author Werner Randelshofer
 * @version $Id: Main.java 307 2013-01-06 11:06:05Z werner $
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        System.out.println("QuickTimeDemo "+Main.class.getPackage().getImplementationVersion());
        System.out.println("This is a demo of the Monte Media library.");
        System.out.println("Copyright © Werner Randelshofer. All Rights Reserved.");
        System.out.println("License: Creative Commons Attribution 3.0.");
        System.out.println();
        
        try {
            test(new File("quicktimedemo-jpg.mov"), new Format(EncodingKey, ENCODING_QUICKTIME_JPEG, DepthKey, 24, QualityKey, 1f));
            test(new File("quicktimedemo-jpg-q0.5.mov"), new Format(EncodingKey, ENCODING_QUICKTIME_JPEG, DepthKey, 24, QualityKey, 0.5f));
            test(new File("quicktimedemo-png.mov"), new Format(EncodingKey, ENCODING_QUICKTIME_PNG, DepthKey, 24));
            test(new File("quicktimedemo-raw24.mov"), new Format(EncodingKey, ENCODING_QUICKTIME_RAW, DepthKey, 24));
            test(new File("quicktimedemo-raw8.mov"), new Format(EncodingKey, ENCODING_QUICKTIME_RAW, DepthKey, 8));
            test(new File("quicktimedemo-anim8.mov"), new Format(EncodingKey, ENCODING_QUICKTIME_ANIMATION, DepthKey, 8));
            test(new File("quicktimedemo-tscc8.mov"), new Format(EncodingKey, ENCODING_AVI_TECHSMITH_SCREEN_CAPTURE, DepthKey, 8));
            test(new File("quicktimedemo-tscc24.mov"), new Format(EncodingKey, ENCODING_AVI_TECHSMITH_SCREEN_CAPTURE, DepthKey, 24));
            //test(new File("quicktimedemo-rle4.mov"), QuickTimeOutputStreamOLD.QuickTimeVideoFormat.RLE, 4, 1f);

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private static void test(File file, Format format) throws IOException {
        testWriting(file,format);
        try {
        testReading(file);
        } catch (UnsupportedOperationException e) {
            e.printStackTrace();
        }
    }
    private static void testWriting(File file, Format format) throws IOException {
        System.out.println("Writing " + file);

        // Make the format more specific
        format = format.prepend(MediaTypeKey, MediaType.VIDEO, //
                FrameRateKey, new Rational(30, 1),//
                WidthKey, 320, //
                HeightKey, 160);

        // Create a buffered image for this format
        BufferedImage img = createImage(format);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        QuickTimeWriter out = null;
        try {
            // Create the writer
            out = new QuickTimeWriter(file);

            // Add a track to the writer
            out.addTrack(format);
            out.setVideoColorTable(0, img.getColorModel());

            // initialize the animation
            Random rnd = new Random(0); // use seed 0 to get reproducable output
            g.setBackground(Color.WHITE);
            g.clearRect(0, 0, img.getWidth(), img.getHeight());
            
            for (int i = 0; i < 100; i++) {
                // Create an animation frame
                g.setColor(new Color(rnd.nextInt()));
                g.fillOval(rnd.nextInt(img.getWidth() - 30), rnd.nextInt(img.getHeight() - 30), 30, 30);
                
                // write it to the writer
                out.write(0, img, 1);
            }

        } finally {
            // Close the writer
            if (out != null) {
                out.close();
            }
            
            // Dispose the graphics object
            g.dispose();
        }
    }

    private static void testReading(File file) throws IOException {
        System.out.println("Reading " + file);
        QuickTimeReader in = null;

        try {
            // Create the reader
            in = new QuickTimeReader(file);
            
            // Look for the first video track
            int track = 0;
            while (track < in.getTrackCount()
                    && in.getFormat(track).get(MediaTypeKey) != MediaType.VIDEO) {
                track++;
            }

            // Read images from the track
            BufferedImage img = null;
            do {
                img = in.read(track, img);
                
                //...to do: do something with the image...
                
            } while (img != null);

        } finally {
            // Close the rader
            if (in != null) {
                in.close();
            }
        }
    }

    /** Creates a buffered image of the specified depth with a random color palette.*/
    private static BufferedImage createImage(Format format) {
        int depth = format.get(DepthKey);
        int width = format.get(WidthKey);
        int height = format.get(HeightKey);

        Random rnd = new Random(0); // use seed 0 to get reproducable output
        BufferedImage img;
        switch (depth) {
            case 24:
            default: {
                img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
                break;
            }
            case 8: {
                byte[] red = new byte[256];
                byte[] green = new byte[256];
                byte[] blue = new byte[256];
                for (int i = 0; i < 255; i++) {
                    red[i] = (byte) rnd.nextInt(256);
                    green[i] = (byte) rnd.nextInt(256);
                    blue[i] = (byte) rnd.nextInt(256);
                }
                rnd.setSeed(0); // set back to 0 for reproducable output
                IndexColorModel palette = new IndexColorModel(8, 256, red, green, blue);
                img = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_INDEXED, palette);
                break;
            }
            case 4: {
                byte[] red = new byte[16];
                byte[] green = new byte[16];
                byte[] blue = new byte[16];
                for (int i = 0; i < 15; i++) {
                    red[i] = (byte) rnd.nextInt(16);
                    green[i] = (byte) rnd.nextInt(16);
                    blue[i] = (byte) rnd.nextInt(16);
                }
                rnd.setSeed(0); // set back to 0 for reproducable output
                IndexColorModel palette = new IndexColorModel(4, 16, red, green, blue);
                img = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_INDEXED, palette);
                break;
            }
        }
        return img;
    }
}
