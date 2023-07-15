/*
 * @(#)Demultiplexer.java  1.0  2011-02-19
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
 * A {@code Demultiplexer} takes a data source with multiplexed media
 * as an input and outputs the media in individual tracks.
 * 
 * @author Werner Randelshofer
 * @version 1.0 2011-02-19 Created.
 */
public interface Demultiplexer {
    /** Returns the tracks. */
    public Track[] getTracks();
  
    /** Closes the Demultiplexer. */
    public void close() throws IOException;
}
