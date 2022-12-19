/*
 * @(#)MovieControl.java  1.0 January 10, 2007
 *
 * Copyright (c) 2007 Werner Randelshofer, Goldau, Switzerland.
 * All rights reserved.
 *
 * You may not use, copy or modify this file, except in compliance with the
 * license agreement you entered into with Werner Randelshofer.
 * For details see accompanying license terms. 
 */

package org.monte.media;

import java.awt.Component;

/**
 * A @code MovieControl} can be used to control a movie using a user interface.
 *
 * @author Werner Randelshofer
 * @version 1.0 January 10, 2007 Created.
 */
public interface MovieControl {
    public void setPlayer(Player player);
    public void setVisible(boolean newValue);
    public Component getComponent();
    public void setEnabled(boolean b);
}
