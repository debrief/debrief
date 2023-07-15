/*
 * @(#)ImageTagSet.java  1.0  2010-07-25
 * 
 * Copyright (c) 2010 Werner Randelshofer, Goldau, Switzerland.
 * All rights reserved.
 *
 * You may not use, copy or modify this file, except in compliance with the
 * license agreement you entered into with Werner Randelshofer.
 * For details see accompanying license terms.
 */

package org.monte.media.exif;

import org.monte.media.tiff.TIFFTag;
import org.monte.media.tiff.TagSet;

/**
 * ImageTagSet.
 *
 * @author Werner Randelshofer
 * @version 1.0 2010-07-25 Created.
 */
public class ImageTagSet extends TagSet {
    private static ImageTagSet instance;

    public static ImageTagSet getInstance() {
        if (instance==null) {
            instance=new ImageTagSet();
        }
        return instance;
    }



    private ImageTagSet() {
        super("Image",new TIFFTag[0]);
    }

}
