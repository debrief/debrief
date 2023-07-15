/*
 * @(#)FileComparator.java  1.0  2011-02-20
 * 
 * Copyright (c) 2011 Werner Randelshofer, Goldau, Switzerland.
 * All rights reserved.
 * 
 * You may not use, copy or modify this file, except in compliance with the
 * license agreement you entered into with Werner Randelshofer.
 * For details see accompanying license terms.
 */

package org.monte.media.imgseq;

import java.io.File;
import java.util.Comparator;

/**
 * {@code FileComparator}.
 *
 * @author Werner Randelshofer
 * @version 1.0 2011-02-20 Created.
 */
public class FileComparator implements Comparator<File> {
private OSXCollator collator=new OSXCollator();

    @Override
    public int compare(File o1, File o2) {
        return collator.compare(o1.getName(), o2.getName());
    }
}
