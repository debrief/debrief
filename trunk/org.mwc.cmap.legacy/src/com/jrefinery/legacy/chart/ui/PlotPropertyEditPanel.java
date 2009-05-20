/* ======================================
 * JFreeChart : a free Java chart library
 * ======================================
 *
 * Project Info:  http://www.object-refinery.com/jfreechart/index.html
 * Project Lead:  David Gilbert (david.gilbert@object-refinery.com);
 *
 * (C) Copyright 2000-2002, by Simba Management Limited;
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
 * --------------------------
 * PlotPropertyEditPanel.java
 * --------------------------
 * (C) Copyright 2000-2002, by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   Andrzej Porebski;
 *
 * $Id: PlotPropertyEditPanel.java,v 1.1.1.1 2003/07/17 10:06:47 Ian.Mayo Exp $
 *
 * Changes (from 24-Aug-2001)
 * --------------------------
 * 24-Aug-2001 : Added standard source header. Fixed DOS encoding problem (DG);
 * 07-Nov-2001 : Separated the JCommon Class Library classes, JFreeChart now requires
 *               jcommon.jar (DG);
 * 21-Nov-2001 : Allowed for null axes (DG);
 * 27-Aug-2002 : Small update to get existing axis properties working again (DG);
 *
 */

package com.jrefinery.legacy.chart.ui;

import java.awt.Paint;
import java.awt.Color;
import java.awt.Stroke;
import java.awt.BasicStroke;
import java.awt.Insets;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JOptionPane;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JColorChooser;
import javax.swing.BorderFactory;
import com.jrefinery.layout.LCBLayout;
import com.jrefinery.legacy.chart.Axis;
import com.jrefinery.legacy.chart.CategoryPlot;
import com.jrefinery.legacy.chart.Plot;
import com.jrefinery.legacy.chart.XYPlot;
import com.jrefinery.ui.PaintSample;
import com.jrefinery.ui.StrokeSample;
import com.jrefinery.ui.StrokeChooserPanel;
import com.jrefinery.ui.InsetsChooserPanel;
import com.jrefinery.ui.InsetsTextField;

/**
 * A panel for editing the properties of a Plot.
 *
 * @author DG
 */
public class PlotPropertyEditPanel extends JPanel implements ActionListener {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

		/** The paint (color) used to fill the background of the plot. */
    private PaintSample backgroundPaintSample;

    /** The stroke (pen) used to draw the outline of the plot. */
    private StrokeSample outlineStrokeSample;

    /** The paint (color) used to draw the outline of the plot. */
    private PaintSample outlinePaintSample;

    /** A panel used to display/edit the properties of the domain axis (if any). */
    private AxisPropertyEditPanel domainAxisPropertyPanel;

    /** A panel used to display/edit the properties of the range axis (if any).*/
    private AxisPropertyEditPanel rangeAxisPropertyPanel;

    /** An array of stroke samples to choose from. */
    private StrokeSample[] availableStrokeSamples;

    /** The insets for the plot. */
    private Insets plotInsets;

    /** The insets text field. */
    private InsetsTextField insetsTextField;

    /**
     * Standard constructor - constructs a panel for editing the properties of
     * the specified plot.
     * <P>
     * In designing the panel, we need to be aware that subclasses of Plot will
     * need to implement subclasses of PlotPropertyEditPanel - so we need to
     * leave one or two 'slots' where the subclasses can extend the user
     * interface.
     *
     * @param plot  the plot, which should be changed.
     */
    public PlotPropertyEditPanel(Plot plot) {

        plotInsets = plot.getInsets();
        backgroundPaintSample = new PaintSample(plot.getBackgroundPaint());
        outlineStrokeSample = new StrokeSample(plot.getOutlineStroke());
        outlinePaintSample = new PaintSample(plot.getOutlinePaint());

        setLayout(new BorderLayout());

        availableStrokeSamples = new StrokeSample[3];
        availableStrokeSamples[0] = new StrokeSample(new BasicStroke(1.0f));
        availableStrokeSamples[1] = new StrokeSample(new BasicStroke(2.0f));
        availableStrokeSamples[2] = new StrokeSample(new BasicStroke(3.0f));

        // create a panel for the settings...
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
                            BorderFactory.createEtchedBorder(),
                            plot.getPlotType() + ":"));

        JPanel general = new JPanel(new BorderLayout());
        general.setBorder(BorderFactory.createTitledBorder(
                              BorderFactory.createEtchedBorder(), "General:"));

        JPanel interior = new JPanel(new LCBLayout(4));
        interior.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));

        interior.add(new JLabel("Insets:"));
        JButton button = new JButton("Edit...");
        button.setActionCommand("Insets");
        button.addActionListener(this);

        insetsTextField = new InsetsTextField(plotInsets);
        insetsTextField.setEnabled(false);
        interior.add(insetsTextField);
        interior.add(button);

        interior.add(new JLabel("Outline stroke:"));
        button = new JButton("Select...");
        button.setActionCommand("OutlineStroke");
        button.addActionListener(this);
        interior.add(outlineStrokeSample);
        interior.add(button);

        interior.add(new JLabel("Outline paint:"));
        button = new JButton("Select...");
        button.setActionCommand("OutlinePaint");
        button.addActionListener(this);
        interior.add(outlinePaintSample);
        interior.add(button);

        interior.add(new JLabel("Background paint:"));
        button = new JButton("Select...");
        button.setActionCommand("BackgroundPaint");
        button.addActionListener(this);
        interior.add(backgroundPaintSample);
        interior.add(button);

        general.add(interior, BorderLayout.NORTH);

        JPanel appearance = new JPanel(new BorderLayout());
        appearance.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        appearance.add(general, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));

        Axis domainAxis = null;
        if (plot instanceof CategoryPlot) {
            domainAxis = ((CategoryPlot) plot).getDomainAxis();
        }
        else if (plot instanceof XYPlot) {
            domainAxis = ((XYPlot) plot).getDomainAxis();
        }
        domainAxisPropertyPanel = AxisPropertyEditPanel.getInstance(domainAxis);
        if (domainAxisPropertyPanel != null) {
            domainAxisPropertyPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
            tabs.add("Domain Axis", domainAxisPropertyPanel);
        }

        Axis rangeAxis = null;
        if (plot instanceof CategoryPlot) {
            rangeAxis = ((CategoryPlot) plot).getRangeAxis();
        }
        else if (plot instanceof XYPlot) {
            rangeAxis = ((XYPlot) plot).getRangeAxis();
        }
        rangeAxisPropertyPanel = AxisPropertyEditPanel.getInstance(rangeAxis);
        if (rangeAxisPropertyPanel != null) {
            rangeAxisPropertyPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
            tabs.add("Range Axis", rangeAxisPropertyPanel);
        }
        tabs.add("Appearance", appearance);
        panel.add(tabs);

        add(panel);
    }

    /**
     * Returns the current plot insets.
     * @return the current plot insets.
     */
    public Insets getPlotInsets() {
        if (plotInsets == null) {
            plotInsets = new Insets(0, 0, 0, 0);
        }
        return plotInsets;
    }

    /**
     * Returns the current background paint.
     * @return the current background paint.
     */
    public Paint getBackgroundPaint() {
        return backgroundPaintSample.getPaint();
    }

    /**
     * Returns the current outline stroke.
     * @return the current outline stroke.
     */
    public Stroke getOutlineStroke() {
        return outlineStrokeSample.getStroke();
    }

    /**
     * Returns the current outline paint.
     * @return the current outline paint.
     */
    public Paint getOutlinePaint() {
        return outlinePaintSample.getPaint();
    }

    /**
     * Returns a reference to the panel for editing the properties of the
     * domain axis.
     *
     * @return a reference to a panel.
     */
    public AxisPropertyEditPanel getDomainAxisPropertyEditPanel() {
        return domainAxisPropertyPanel;
    }

    /**
     * Returns a reference to the panel for editing the properties of the
     * range axis.
     *
     * @return a reference to a panel.
     */
    public AxisPropertyEditPanel getRangeAxisPropertyEditPanel() {
        return rangeAxisPropertyPanel;
    }

    /**
     * Handles user actions generated within the panel.
     * @param event     the event
     */
    public void actionPerformed(ActionEvent event) {
        String command = event.getActionCommand();
        if (command.equals("BackgroundPaint")) {
            attemptBackgroundPaintSelection();
        }
        else if (command.equals("OutlineStroke")) {
            attemptOutlineStrokeSelection();
        }
        else if (command.equals("OutlinePaint")) {
            attemptOutlinePaintSelection();
        }
        else if (command.equals("Insets")) {
            editInsets();
        }

    }

    /**
     * Allow the user to change the background paint.
     */
    private void attemptBackgroundPaintSelection() {
        Color c;
        c = JColorChooser.showDialog(this, "Background Color", Color.blue);
        if (c != null) {
            backgroundPaintSample.setPaint(c);
        }
    }

    /**
     * Allow the user to change the outline stroke.
     */
    private void attemptOutlineStrokeSelection() {
        StrokeChooserPanel panel = new StrokeChooserPanel(null, availableStrokeSamples);
        int result = JOptionPane.showConfirmDialog(this, panel,
            "Stroke Selection",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            outlineStrokeSample.setStroke(panel.getSelectedStroke());
        }
    }

    /**
     * Allow the user to change the outline paint.  We use JColorChooser, so
     * the user can only choose colors (a subset of all possible paints).
     */
    private void attemptOutlinePaintSelection() {
        Color c;
        c = JColorChooser.showDialog(this, "Outline Color", Color.blue);
        if (c != null) {
            outlinePaintSample.setPaint(c);
        }
    }

    /**
     * Allow the user to edit the individual insets' values.
     */
    private void editInsets() {
        InsetsChooserPanel panel = new InsetsChooserPanel(plotInsets);
        int result = JOptionPane.showConfirmDialog(this, panel, "Edit Insets",
                                                   JOptionPane.OK_CANCEL_OPTION,
                                                   JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            plotInsets = panel.getInsets();
            insetsTextField.setInsets(plotInsets);
        }

    }

    /**
     * Updates the plot properties to match the properties defined on the panel.
     *
     * @param plot  The plot.
     */
    public void updatePlotProperties(Plot plot) {

        // set the plot properties...
        plot.setOutlinePaint(getOutlinePaint());
        plot.setOutlineStroke(getOutlineStroke());
        plot.setBackgroundPaint(getBackgroundPaint());
        plot.setInsets(getPlotInsets());

        // then the axis properties...
        if (this.domainAxisPropertyPanel != null) {
            Axis domainAxis = null;
            if (plot instanceof CategoryPlot) {
                CategoryPlot p = (CategoryPlot) plot;
                domainAxis = p.getDomainAxis();
            }
            else if (plot instanceof XYPlot) {
                XYPlot p = (XYPlot) plot;
                domainAxis = p.getDomainAxis();
            }
            if (domainAxis != null) {
                this.domainAxisPropertyPanel.setAxisProperties(domainAxis);
            }
        }

        if (this.rangeAxisPropertyPanel != null) {
            Axis rangeAxis = null;
            if (plot instanceof CategoryPlot) {
                CategoryPlot p = (CategoryPlot) plot;
                rangeAxis = p.getRangeAxis();
            }
            else if (plot instanceof XYPlot) {
                XYPlot p = (XYPlot) plot;
                rangeAxis = p.getRangeAxis();
            }
            if (rangeAxis != null) {
                this.rangeAxisPropertyPanel.setAxisProperties(rangeAxis);
            }
        }

    }

}
