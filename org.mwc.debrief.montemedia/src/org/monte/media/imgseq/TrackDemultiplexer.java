/*
 * @(#)TrackDemultiplexer.java  1.0  2011-02-20
 * 
 * Copyright (c) 2011 Werner Randelshofer, Goldau, Switzerland.
 * All rights reserved.
 * 
 * You may not use, copy or modify this file, except in compliance with the
 * license agreement you entered into with Werner Randelshofer.
 * For details see accompanying license terms.
 */
package org.monte.media.imgseq;

import org.monte.media.Demultiplexer;
import org.monte.media.Track;
import java.io.IOException;

/**
 * Can "demultiplex" an array of already demultiplexed tracks.
 *
 * @author Werner Randelshofer
 * @version 1.0 2011-02-20 Created.
 */
public class TrackDemultiplexer implements Demultiplexer {

    private Track[] tracks;

    public TrackDemultiplexer(Track[] tracks) {
        this.tracks = tracks.clone();
    }

    @Override
    public Track[] getTracks() {
        return tracks.clone();
    }

    @Override
    public void close() throws IOException {
    }
}
