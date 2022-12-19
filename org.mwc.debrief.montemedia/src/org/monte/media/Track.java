/*
 * @(#)Track.java  1.0  2011-02-20
 * 
 * Copyright (c) 2011 Werner Randelshofer, Goldau, Switzerland.
 * All rights reserved.
 * 
 * You may not use, copy or modify this file, except in compliance with the
 * license agreement you entered into with Werner Randelshofer.
 * For details see accompanying license terms.
 */

package org.monte.media;

import java.io.IOException;

/**
 * A {@code Track} refers to media data that can be interpreted in a time
 * coordinate system.
 * <p>
 * A track is the output of a {@link Demultiplexer}.
 *
 * @author Werner Randelshofer
 * @version 1.0 2011-02-20 Created.
 */
public interface Track {
    /** Returns the numbers of samples in this track. */
    public long getSampleCount();
/** Sets the read position. */
    public void setPosition(long pos);
    /** Gets the read position. */
    public long getPosition();

    /** Reads a sample from the input stream.
     * If the end of the track is reached, the discard-flag in the buffer is set
     * to true.
     *
     * @param buf The buffer for the sample.
     */
    public void read(Buffer buf) throws IOException;
}
