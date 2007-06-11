/* ===============
 * JFreeChart Demo
 * ===============
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
 * ---------------------
 * DescriptionPanel.java
 * ---------------------
 * (C) Copyright 2001, 2002, by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: DescriptionPanel.java,v 1.1.1.1 2003/07/17 10:06:32 Ian.Mayo Exp $
 *
 * Changes
 * -------
 * 10-Dec-2001 : Version 1 (DG);
 * 10-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 *
 */

package com.jrefinery.chart.demo;

import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;

/**
 * A panel containing a chart description.
 *
 * @author DG
 */
public class DescriptionPanel extends JPanel {

    /** The preferred size for the panel. */
    public static final Dimension PREFERRED_SIZE = new Dimension(150, 50);

    /**
     * Creates a new panel.
     *
     * @param text  the component containing the text.
     */
    public DescriptionPanel(JTextArea text) {

        setLayout(new BorderLayout());
        text.setLineWrap(true);
        text.setWrapStyleWord(true);
        add(new JScrollPane(text,
                            JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER));

    }

    /**
     * Returns the preferred size.
     *
     * @return the preferred size.
     */
    public Dimension getPreferredSize() {
        return PREFERRED_SIZE;
    }

}
