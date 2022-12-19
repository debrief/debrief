/*
 * @(#)ColorCyclePlayer.java  1.0  2010-08-04
 * 
 * Copyright (c) 2010 Werner Randelshofer, Goldau, Switzerland.
 * All rights reserved.
 *
 * You may not use, copy or modify this file, except in compliance with the
 * license agreement you entered into with Werner Randelshofer.
 * For details see accompanying license terms.
 */
package org.monte.media;

/**
 * A {@link Player} which supports a second layer of animation by cycling colors
 * in the color palette of the current image in the video track.
 * <p>
 * Color cycling is provided in a separate layer on top of the video track.
 * It can be performed independently of video playback.
 * </p>
 *
 * @author Werner Randelshofer
 * @version 1.0 2010-08-04 Created.
 */
public interface ColorCyclePlayer extends Player {

    /** Returns true if color cycling is started. */
    public boolean isColorCyclingStarted();

    /** Starts/Stops color cycling. */
    public void setColorCyclingStarted(boolean b);

    /** Returns true if color cycling is available. */
    public boolean isColorCyclingAvailable();

    /** Sets whether colors are blended during color cycling. */
    public void setBlendedColorCycling(boolean newValue);

    /** Returns true if colors are blended during color cycling. */
    public boolean isBlendedColorCycling();
}
