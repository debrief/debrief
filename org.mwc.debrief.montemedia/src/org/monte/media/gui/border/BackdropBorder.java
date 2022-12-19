/*
 * @(#)BackdropBorder.java  1.0  September 24, 2006
 *
 * Copyright (c) 2006 Werner Randelshofer, Goldau, Switzerland.
 * All rights reserved.
 *
 * You may not use, copy or modify this file, except in compliance with the
 * license agreement you entered into with Werner Randelshofer.
 * For details see accompanying license terms. 
 */

package org.monte.media.gui.border;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
/**
 * BackdropBorder has a foreground border and a background border.
 * The foreground border is drawn, when paintBorder() is invoked.
 * The background border can be retrieved using getBackgroundBorder().
 * 
 * @author Werner Randelshofer
 * @version 1.0 September 24, 2006 Created.
 */
public class BackdropBorder implements Border {
    private Border foregroundBorder;
    private Border backgroundBorder;
    
    /** Creates a new instance. */
    public BackdropBorder(Border backdropBorder) {
        this(null, backdropBorder);
    }
    public BackdropBorder(Border foregroundBorder, Border backdropBorder) {
        this.backgroundBorder = backdropBorder;
        this.foregroundBorder = foregroundBorder;
    }

    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        if (foregroundBorder != null) {
            foregroundBorder.paintBorder(c, g, x, y, width, height);
        }
    }
    
    public Border getBackgroundBorder() {
        return backgroundBorder;
    }

    public Insets getBorderInsets(Component c) {
        if (foregroundBorder != null) {
            return foregroundBorder.getBorderInsets(c);
        } else {
            return backgroundBorder.getBorderInsets(c);
        }
    }

    public boolean isBorderOpaque() {
        return false;
    }    
}
