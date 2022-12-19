/*
 * @(#)ANIMDemultiplexer.java  1.0  2011-02-20
 * 
 * Copyright (c) 2011 Werner Randelshofer, Goldau, Switzerland.
 * All rights reserved.
 * 
 * You may not use, copy or modify this file, except in compliance with the
 * license agreement you entered into with Werner Randelshofer.
 * For details see accompanying license terms.
 */
package org.monte.media.anim;

import org.monte.media.Demultiplexer;
import org.monte.media.Track;
import java.io.File;
import java.io.IOException;

/**
 * {@code ANIMDemultiplexer}.
 *
 * @author Werner Randelshofer
 * @version 1.0 2011-02-20 Created.
 */
public class ANIMDemultiplexer extends ANIMReader implements Demultiplexer {

    private Track[] tracks;

    public ANIMDemultiplexer(File file) throws IOException {
        super(file);
    }

    @Override
    public Track[] getTracks() {
        if (tracks == null) {
            tracks = new Track[]{new ANIMTrack(this)};
        }
        return tracks.clone();
    }
}
