/*
 * @(#)ImageSequenceTrack.java  1.0  2011-02-20
 * 
 * Copyright (c) 2011 Werner Randelshofer, Goldau, Switzerland.
 * All rights reserved.
 * 
 * You may not use, copy or modify this file, except in compliance with the
 * license agreement you entered into with Werner Randelshofer.
 * For details see accompanying license terms.
 */

package org.monte.media.imgseq;

import org.monte.media.Buffer;
import org.monte.media.Track;
import org.monte.media.math.Rational;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Arrays;
import static org.monte.media.BufferFlag.*;

/**
 * {@code ImageSequenceTrack}.
 *
 * @author Werner Randelshofer
 * @version 1.0 2011-02-20 Created.
 */
public class ImageSequenceTrack implements Track {


    private File[] files;
    private int position;
    private long duration=1;
    private long timeScale=30;

    public ImageSequenceTrack(File dir, FileFilter filter) throws IOException {
        files = dir.listFiles(filter);
        Arrays.sort(files, new FileComparator());
    }

    public ImageSequenceTrack(File[] files) throws IOException {
        this.files = files.clone();
    }

    public long getFileDuration() {
        return duration;
    }
    public void setFileDuration(long newValue) {
        this.duration=newValue;
    }
    public long getTimeScale() {
        return timeScale;
    }
    public void setTimeScale(long newValue) {
        this.timeScale=newValue;
    }


    @Override
    public long getSampleCount() {
        return files.length;
    }

    @Override
    public void setPosition(long pos) {
        this.position=(int)pos;
    }

    @Override
    public long getPosition() {
        return position;
    }

    @Override
    public void read(Buffer buf) throws IOException {
        if (position>=files.length) {
            buf.setFlagsTo(DISCARD);
            return;
        }
        buf.clearFlags();
        buf.data=files[position];
        buf.sampleDuration=new Rational(duration,timeScale);
        position++;
    }
}
