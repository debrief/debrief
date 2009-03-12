/* ======================================
 * JFreeChart : a free Java chart library
 * ======================================
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
 * You should have received a copy of the GNU Lesser General Public License along with this library;
 * if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston,
 * MA 02111-1307, USA.
 *
 * ---------------------------
 * TitlePropertyEditPanel.java
 * ---------------------------
 * (C) Copyright 2000-2002, by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: TitlePropertyEditPanel.java,v 1.1.1.1 2003/07/17 10:06:47 Ian.Mayo Exp $
 *
 * Changes (from 24-Aug-2001)
 * --------------------------
 * 24-Aug-2001 : Added standard source headaer. Fixed DOS encoding problem (DG);
 * 07-Nov-2001 : Separated the JCommon Class Library classes, JFreeChart now requires
 *               jcommon.jar (DG);
 * 31-Jan-2002 : Removed Title.java and StandardTitle.java.  Disabled some methods in this class
 *               until support for AbstractTitle is added (DG);
 *
 */

package com.jrefinery.chart.ui;

import java.awt.Font;
import java.awt.Paint;
import java.awt.Color;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JPanel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JColorChooser;
import javax.swing.BorderFactory;
import com.jrefinery.chart.AbstractTitle;
import com.jrefinery.layout.LCBLayout;
import com.jrefinery.ui.PaintSample;
import com.jrefinery.ui.FontDisplayField;
import com.jrefinery.ui.FontChooserPanel;

/**
 * A panel for editing the properties of a chart title.
 *
 * @author DG
 */
public class TitlePropertyEditPanel extends JPanel implements ActionListener {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

		/** A field for displaying/editing the title text. */
    private JTextField titleField;

    /** The font used to draw the title. */
    private Font titleFont;

    /** A field for displaying a description of the title font. */
    private JTextField fontfield;

    /** The paint (color) used to draw the title. */
    private PaintSample titlePaint;

    /**
     * Standard constructor: builds a panel for displaying/editing the
     * properties of the specified title.
     *
     * @param title  the title, which should be changed.
     */
    public TitlePropertyEditPanel(AbstractTitle title) {

        setLayout(new BorderLayout());

        JPanel general = new JPanel(new BorderLayout());
        general.setBorder(BorderFactory.createTitledBorder(
                              BorderFactory.createEtchedBorder(), "General:"));

        JPanel interior = new JPanel(new LCBLayout(3));
        interior.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
        JLabel titleLabel = new JLabel("Text:");

        interior.add(titleLabel);
        interior.add(titleField);
        interior.add(new JPanel());
        interior.add(new JLabel("Font:"));

        fontfield = new FontDisplayField(titleFont);
        JButton b = new JButton("Select...");
        b.setActionCommand("SelectFont");
        b.addActionListener(this);

        interior.add(fontfield);
        interior.add(b);

        interior.add(new JLabel("Color:"));

        b = new JButton("Select...");
        b.setActionCommand("SelectPaint");
        b.addActionListener(this);
        interior.add(titlePaint);
        interior.add(b);

        general.add(interior);
        add(general, BorderLayout.NORTH);

    }

    /**
     * Returns the title entered in the panel.
     *
     * @return the title entered in the panel.
     */
    public String getTitle() {
        return titleField.getText();
    }

    /**
     * Returns the font selected in the panel.
     *
     * @return the font selected in the panel.
     */
    public Font getTitleFont() {
        return titleFont;
    }

    /**
     * Returns the paint selected in the panel.
     *
     * @return the paint selected in the panel.
     */
    public Paint getTitlePaint() {
        return titlePaint.getPaint();
    }

    /**
     * Handles button clicks by passing control to an appropriate handler method.
     *
     * @param event  the event
     */
    public void actionPerformed(ActionEvent event) {

        String command = event.getActionCommand();

        if (command.equals("SelectFont")) {
            attemptFontSelection();
        }
        else if (command.equals("SelectPaint")) {
            attemptPaintSelection();
        }

    }

    /**
     * Presents a font selection dialog to the user.
     */
    public void attemptFontSelection() {

        FontChooserPanel panel = new FontChooserPanel(titleFont);
        int result = JOptionPane.showConfirmDialog(this, panel, "Font Selection",
                                                   JOptionPane.OK_CANCEL_OPTION,
                                                   JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            titleFont = panel.getSelectedFont();
            fontfield.setText(titleFont.getFontName() + " " + titleFont.getSize());
        }

    }

    /**
     * Allow the user the opportunity to select a Paint object.  For now, we
     * just use the standard color chooser - all colors are Paint objects, but
     * not all Paint objects are colors (later we can implement a more general
     * Paint chooser).
     */
    public void attemptPaintSelection() {
        Color c = JColorChooser.showDialog(this, "Title Color", Color.blue);
        if (c != null) {
            titlePaint.setPaint(c);
        }
    }

    /**
     * Sets the properties of the specified title to match the properties
     * defined on this panel.
     *
     * @param title  an AbstractTitle.
     */
    public void setTitleProperties(AbstractTitle title) {
        if (title instanceof AbstractTitle) {
            // only supports StandardTitle at present
            //StandardTitle standard = (StandardTitle)title;
            //standard.setTitle(this.getTitle());
            //standard.setTitleFont(this.getTitleFont());
            //standard.setTitlePaint(this.getTitlePaint());
        }
        else {
            // raise an exception - not a recognised title class
        }
    }

}
