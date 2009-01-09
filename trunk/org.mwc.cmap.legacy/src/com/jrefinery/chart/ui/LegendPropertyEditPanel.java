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
 * You should have received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 *
 * ----------------------------
 * LegendPropertyEditPanel.java
 * ----------------------------
 * (C) Copyright 2000-2002, by Simba Management Limited.
 *
 * Original Author:  David Gilbert;
 * Contributor(s):   -;
 *
 * $Id: LegendPropertyEditPanel.java,v 1.1.1.1 2003/07/17 10:06:47 Ian.Mayo Exp $
 *
 * Changes (from 24-Aug-2001)
 * --------------------------
 * 24-Aug-2001 : Added standard source header. Fixed DOS encoding problem (DG);
 * 07-Nov-2001 : Separated the JCommon Class Library classes, JFreeChart now requires
 *               jcommon.jar (DG);
 * 25-Jun-2002 : Revised header, removed redundant code (DG);
 *
 */

package com.jrefinery.chart.ui;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Paint;
import java.awt.Color;
import java.awt.Stroke;
import java.awt.BasicStroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JColorChooser;
import com.jrefinery.chart.Legend;
import com.jrefinery.chart.StandardLegend;
import com.jrefinery.layout.LCBLayout;
import com.jrefinery.ui.StrokeSample;
import com.jrefinery.ui.StrokeChooserPanel;
import com.jrefinery.ui.PaintSample;
import com.jrefinery.ui.FontDisplayField;
import com.jrefinery.ui.FontChooserPanel;

/**
 * A panel for editing the properties of a Legend.
 *
 * @author DG
 */
class LegendPropertyEditPanel extends JPanel implements ActionListener {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

		/** The stroke (pen) used to draw the legend outline. */
    private StrokeSample outlineStroke;

    /** The paint (color) used to draw the legend outline. */
    private PaintSample outlinePaint;

    /** The paint (color) used to fill the legend background. */
    private PaintSample backgroundPaint;

    /** The font used to draw the series names. */
    private Font seriesFont;

    /** The paint (color) used to draw the series names. */
    private PaintSample seriesPaint;

    /** An array of strokes (pens) to select from. */
    private StrokeSample[] availableStrokeSamples;

    /**
     * Standard constructor: builds a panel based on the specified legend.
     *
     * @param legend    the legend, which should be changed.
     */
    public LegendPropertyEditPanel(Legend legend) {

        StandardLegend l = (StandardLegend) legend;
        outlineStroke = new StrokeSample(l.getOutlineStroke());
        outlinePaint = new PaintSample(l.getOutlinePaint());
        backgroundPaint = new PaintSample(l.getBackgroundPaint());
        seriesFont = l.getItemFont();
        seriesPaint = new PaintSample(l.getItemPaint());

        availableStrokeSamples = new StrokeSample[4];
        availableStrokeSamples[0] = new StrokeSample(new BasicStroke(1.0f));
        availableStrokeSamples[1] = new StrokeSample(new BasicStroke(2.0f));
        availableStrokeSamples[2] = new StrokeSample(new BasicStroke(3.0f));
        availableStrokeSamples[3] = new StrokeSample(new BasicStroke(4.0f));

        setLayout(new BorderLayout());

        JPanel general = new JPanel(new BorderLayout());
        general.setBorder(BorderFactory.createTitledBorder(
                              BorderFactory.createEtchedBorder(), "General:"));

        JPanel interior = new JPanel(new LCBLayout(5));
        interior.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));

        interior.add(new JLabel("Outline:"));
        interior.add(outlineStroke);
        JButton button = new JButton("Select...");
        button.setActionCommand("OutlineStroke");
        button.addActionListener(this);
        interior.add(button);

        interior.add(new JLabel("Outline Paint:"));
        button = new JButton("Select...");
        button.setActionCommand("OutlinePaint");
        button.addActionListener(this);
        interior.add(outlinePaint);
        interior.add(button);

        interior.add(new JLabel("Background:"));
        button = new JButton("Select...");
        button.setActionCommand("BackgroundPaint");
        button.addActionListener(this);
        interior.add(backgroundPaint);
        interior.add(button);

        interior.add(new JLabel("Series label font:"));
        button = new JButton("Select...");
        button.setActionCommand("SeriesFont");
        button.addActionListener(this);
        interior.add(new FontDisplayField(seriesFont));
        interior.add(button);

        interior.add(new JLabel("Series label paint:"));
        button = new JButton("Select...");
        button.setActionCommand("SeriesPaint");
        button.addActionListener(this);
        interior.add(seriesPaint);
        interior.add(button);

        general.add(interior);
        add(general, BorderLayout.NORTH);
    }

    /**
     * Returns the current outline stroke.
     * @return the current outline stroke.
     */
    public Stroke getOutlineStroke() {
        return outlineStroke.getStroke();
    }

    /**
     * Returns the current outline paint.
     * @return the current outline paint.
     */
    public Paint getOutlinePaint() {
        return outlinePaint.getPaint();
    }

    /**
     * Returns the current background paint.
     * @return the current background paint.
     */
    public Paint getBackgroundPaint() {
        return backgroundPaint.getPaint();
    }

    /**
     * Returns the current series label font.
     * @return the current series label font.
     */
    public Font getSeriesFont() {
        return seriesFont;
    }

    /**
     * Returns the current series label paint.
     * @return the current series label paint.
     */
    public Paint getSeriesPaint() {
        return seriesPaint.getPaint();
    }

    /**
     * Handles user interactions with the panel.
     *
     * @param event     the event.
     */
    public void actionPerformed(ActionEvent event) {
        String command = event.getActionCommand();
        if (command.equals("OutlineStroke")) {
            attemptModifyOutlineStroke();
        }
        else if (command.equals("OutlinePaint")) {
            attemptModifyOutlinePaint();
        }
        else if (command.equals("BackgroundPaint")) {
            attemptModifyBackgroundPaint();
        }
        else if (command.equals("SeriesFont")) {
            attemptModifySeriesFont();
        }
        else if (command.equals("SeriesPaint")) {
            attemptModifySeriesPaint();
        }
    }

    /**
     * Allows the user the opportunity to change the outline stroke.
     */
    private void attemptModifyOutlineStroke() {
        StrokeChooserPanel panel =
            new StrokeChooserPanel(outlineStroke, availableStrokeSamples);
        int result = JOptionPane.showConfirmDialog(this, panel,
            "Pen/Stroke Selection",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            outlineStroke.setStroke(panel.getSelectedStroke());
        }
    }

    /**
     * Allows the user the opportunity to change the outline paint.
     */
    private void attemptModifyOutlinePaint() {
        Color c;
        c = JColorChooser.showDialog(this, "Outline Color", Color.blue);
        if (c != null) {
            outlinePaint.setPaint(c);
        }
    }

    /**
     * Allows the user the opportunity to change the background paint.
     */
    private void attemptModifyBackgroundPaint() {
        Color c;
        c = JColorChooser.showDialog(this, "Background Color", Color.blue);
        if (c != null) {
            backgroundPaint.setPaint(c);
        }
    }

    /**
     * Allows the user the opportunity to change the series label font.
     */
    public void attemptModifySeriesFont() {

        FontChooserPanel panel = new FontChooserPanel(seriesFont);
        int result = JOptionPane.showConfirmDialog(this, panel,
            "Font Selection",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            seriesFont = panel.getSelectedFont();
            //fontfield.setText(titlefont.getFontName()+" "
            // + titlefont.getSize());
        }

    }

    /**
     * Allows the user the opportunity to change the series label paint.
     */
    private void attemptModifySeriesPaint() {
        Color c;
        c = JColorChooser.showDialog(this, "Series Label Color", Color.blue);
        if (c != null) {
            seriesPaint.setPaint(c);
        }
    }

    /**
     * Sets the properties of the specified legend to match the properties
     * defined on this panel.
     *
     * @param legend    an instance of <code>StandardLegend</code>.
     */
    public void setLegendProperties(Legend legend) {
        if (legend instanceof StandardLegend) {
            // only supports StandardLegend at present
            StandardLegend standard = (StandardLegend) legend;
            standard.setOutlineStroke(this.getOutlineStroke());
            standard.setOutlinePaint(this.getOutlinePaint());
            standard.setBackgroundPaint(this.getBackgroundPaint());
            standard.setItemFont(this.getSeriesFont());
            standard.setItemPaint(this.getSeriesPaint());
        }
        else {
            // raise exception - unrecognised legend
        }
    }

}
