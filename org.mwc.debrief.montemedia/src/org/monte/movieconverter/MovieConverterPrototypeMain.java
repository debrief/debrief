/*
 * @(#)MovieConverterPrototypeMain.java  1.0  2011-09-03
 * 
 * Copyright (c) 2011 Werner Randelshofer, Goldau, Switzerland.
 * All rights reserved.
 * 
 * You may not use, copy or modify this file, except in compliance with the
 * license agreement you entered into with Werner Randelshofer.
 * For details see accompanying license terms.
 */
package org.monte.movieconverter;

import org.monte.media.Buffer;

/**
 * {@code MovieConverterPrototypeMain}.
 *
 * @author Werner Randelshofer
 * @version 1.0 2011-09-03 Created.
 */
public class MovieConverterPrototypeMain {
    private static class MovieReader {
        
        // Direct access to samples
        // Is this consistent with the write methods?
        public long getSampleCount(int track) { return 1;}
        public void readSample(int track, long sampleIndex, Buffer buf) {}
        public void readSamples(int track, long sampleIndex, int sampleCount, Buffer buf) {}
        public long movieTimeToSample(long time) {return 1;}
        public long timeToSample(int track, long time) {return 1;}
        public long sampleToTime(int track, long sample) {return 1;}
        public long sampleToMovieTime(long sample) {return 1;}
        
        
        // Timed access to samples
        public int getTrackCount() {return 1;}
        public long getMovieDuration() {return 1;}
        public long getMovieTimeScale() { return 1;}
        public void setMovieStartTime(long time) {}
        public void setMovieEndTime(long time) {}
        public void setMovieTime(long time) {}
        public long getTimeScale(int track) { return 1;}
        public long getDuration(int track) {return 1;}
        public long getStartTime(int track) {return 1;}
        public void read(int track, Buffer buf) {}
    }
}
