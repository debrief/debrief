/* =======================================
 * JFreeChart : a Java Chart Class Library
 * =======================================
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
 * --------------
 * DateTitle.java
 * --------------
 * (C) Copyright 2000-2002, by David Berry and Contributors.
 *
 * Original Author:  David Berry;
 * Contributor(s):   David Gilbert (for Simba Management Limited);
 *
 * $Id: DateTitle.java,v 1.1.1.1 2003/07/17 10:06:22 Ian.Mayo Exp $
 *
 * Changes (from 18-Sep-2001)
 * --------------------------
 * 18-Sep-2001 : Added standard header (DG);
 * 09-Jan-2002 : Updated Javadoc comments (DG);
 * 07-Feb-2002 : Changed blank space around title from Insets --> Spacer, to allow for relative
 *               or absolute spacing (DG);
 * 26-Sep-2002 : Fixed errors reported by Checkstyle (DG);
 *
 */

package com.jrefinery.chart;

import java.awt.Font;
import java.awt.Paint;
import java.awt.Color;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * DateTitle (an extension of TextTitle) is a simple convenience class to
 * easily add the text of the date to a chart.  Keep in mind that a chart can
 * have several titles, and that they can appear at the top, left, right or
 * bottom of the chart - a DateTitle will commonly appear at the bottom of a
 * chart (although you can place it anywhere).
 * <P>
 * By specifying the locale, dates are formatted to the correct standard for
 * the given Locale. For example, a date would appear as "January 17, 2000" in
 * the US, but "17 January 2000" in most European locales.
 *
 * @author DB
 */
public class DateTitle extends TextTitle {

    /**
     * Constructs a new DateTitle that displays the current date in the default
     * (LONG) format for the locale, positioned to the bottom right of the
     * chart.
     * <P>
     * The color will be black in 12 point, plain Helvetica font (maps to Arial
     * on Win32 systems without Helvetica).
     */
    public DateTitle() {

        this(DateFormat.LONG);

    }

    /**
     * Constructs a new DateTitle with the specified style (for the default locale).
     * <P>
     * The date style should be one of:  SHORT, MEDIUM, LONG or FULL (defined in
     * java.util.DateFormat).
     *
     * @param style  the date style.
     */
    public DateTitle(int style) {
        this(style, Locale.getDefault(),
             new Font("Dialog", Font.PLAIN, 12), Color.black);
    }

    /**
     * Constructs a new DateTitle object with the specified attributes and the
     * following defaults:
     * <P>
     * location = BOTTOM, alignment = RIGHT, insets = new Insets(2, 2, 2, 2).
     * <P>
     * The date style should be one of:  SHORT, MEDIUM, LONG or FULL (defined in
     * java.util.DateFormat).
     * <P>
     * For the locale, you can use Locale.getDefault() for the default locale.
     *
     * @param style  the date style.
     * @param locale  the locale.
     * @param font  the font.
     * @param paint  the text color.
     */
    public DateTitle(int style, Locale locale, Font font, Paint paint) {

        this(style, locale, font, paint,
             AbstractTitle.BOTTOM,
             AbstractTitle.RIGHT,
             AbstractTitle.MIDDLE,
             AbstractTitle.DEFAULT_SPACER);
    }

    /**
     * Constructs a new DateTitle with the specified attributes.
     * <P>
     * The date style should be one of:  SHORT, MEDIUM, LONG or FULL (defined in
     * java.util.DateFormat).
     * <P>
     * For the locale, you can use Locale.getDefault() for the default locale.
     *
     * @param style  the date style.
     * @param locale  the locale.
     * @param font  the font.
     * @param paint  the text color.
     * @param position  the relative location of this title (use constants in AbstractTitle).
     * @param horizontalAlignment  the horizontal text alignment of this title (use constants
     *                             in AbstractTitle).
     * @param verticalAlignment  the vertical text alignment of this title (use constants in
     *                           AbstractTitle).
     * @param spacer  determines the blank space around the outside of the title.
     */
    public DateTitle(int style, Locale locale, Font font, Paint paint,
                     int position, int horizontalAlignment, int verticalAlignment,
                     Spacer spacer) {

        super(DateFormat.getDateInstance(style, locale).format(new Date()),
              font, paint,
              position, horizontalAlignment, verticalAlignment,
              spacer);

    }

    /**
     * Set the format of the date.
     * <P>
     * The date style should be one of:  SHORT, MEDIUM, LONG or FULL (defined in
     * java.util.DateFormat).
     * <P>
     * For the locale, you can use Locale.getDefault() for the default locale.
     *
     * @param style  the date style.
     * @param locale  the locale.
     */
    public void setDateFormat(int style, Locale locale) {

        setText(DateFormat.getDateInstance(style, locale).format(new Date()));

    }

}
