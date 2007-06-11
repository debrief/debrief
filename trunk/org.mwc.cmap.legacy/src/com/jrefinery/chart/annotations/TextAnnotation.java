/* ==========================================
 * JFreeChart : a free chart library for Java
 * ==========================================
 *
 * Project Info:  http://www.object-refinery.com/jfreechart/index.html
 * Project Lead:  David Gilbert (david.gilbert@object-refinery.com);
 *
 * (C) Copyright 2000-2002, by Simba Management Limited and Contributors.
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 *
 * -------------------
 * TextAnnotation.java
 * -------------------
 * (C) Copyright 2002, by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: TextAnnotation.java,v 1.1.1.1 2003/07/17 10:06:30 Ian.Mayo Exp $
 *
 * Changes:
 * --------
 * 28-Aug-2002 : Version 1 (DG);
 *
 */

package com.jrefinery.chart.annotations;

import java.awt.Font;
import java.awt.Paint;
import java.awt.Color;

/**
 * A base class for text annotations.
 */
public class TextAnnotation {

    /** The default font. */
    public static Font DEFAULT_FONT = new Font("SansSerif", Font.PLAIN, 10);

    /** The default paint. */
    public static Paint DEFAULT_PAINT = Color.black;

    /** The text. */
    protected String text;

    /** The font. */
    protected Font font;

    /** The paint. */
    protected Paint paint;

    /**
     * Constructs a text annotation.
     */
    protected TextAnnotation(String text, Font font, Paint paint) {
        this.text = text;
        this.font = font;
        this.paint = paint;
    }

}
