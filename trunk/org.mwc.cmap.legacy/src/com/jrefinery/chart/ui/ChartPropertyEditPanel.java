/* ======================================
 * JFreeChart : a free Java chart library
 * ======================================
 *
 * Project Info:  http://www.object-refinery.com/jfreechart/index.html
 * Project Lead:  David Gilbert (david.gilbert@object-refinery.com);
 *
 * This file...
 * $Id: ChartPropertyEditPanel.java,v 1.1.1.1 2003/07/17 10:06:46 Ian.Mayo Exp $
 *
 * Original Author:  David Gilbert;
 * Contributor(s):   -;
 *
 * (C) Copyright 2000, 2001, Simba Management Limited;
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
 * Changes (from 22-Jun-2001)
 * --------------------------
 * 22-Jun-2001 : Disabled title panel, as it doesn't support the new title code (DG);
 * 24-Aug-2001 : Fixed DOS encoding problem (DG);
 * 07-Nov-2001 : Separated the JCommon Class Library classes, JFreeChart now requires
 *               jcommon.jar (DG);
 * 21-Nov-2001 : Allowed for null legend (DG);
 *
 */

package com.jrefinery.chart.ui;

import java.awt.Paint;
import java.awt.Color;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JCheckBox;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JColorChooser;
import javax.swing.BorderFactory;
import com.jrefinery.chart.JFreeChart;
import com.jrefinery.chart.Legend;
import com.jrefinery.chart.Plot;
import com.jrefinery.layout.LCBLayout;
import com.jrefinery.ui.PaintSample;

/**
 * A panel for editing chart properties (includes subpanels for the title,
 * legend and plot).
 *
 * @author DG
 */
public class ChartPropertyEditPanel extends JPanel implements ActionListener {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

		/** A panel for displaying/editing the properties of the title. */
    private TitlePropertyEditPanel titlePropertiesPanel;

    /** A panel for displaying/editing the properties of the legend. */
    private LegendPropertyEditPanel legendPropertiesPanel;

    /** A panel for displaying/editing the properties of the plot. */
    private PlotPropertyEditPanel plotPropertiesPanel;

    /** A checkbox indicating whether or not the chart is drawn with
     *  anti-aliasing.
     */
    private JCheckBox antialias;

    /** The chart background color. */
    private PaintSample background;

    /**
     * Standard constructor - the property panel is made up of a number of
     * sub-panels that are displayed in the tabbed pane.
     *
     * @param chart  the chart, whichs properties should be changed.
     */
    public ChartPropertyEditPanel(JFreeChart chart) {
        setLayout(new BorderLayout());

        JPanel other = new JPanel(new BorderLayout());
        other.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));

        JPanel general = new JPanel(new BorderLayout());
        general.setBorder(BorderFactory.createTitledBorder(
                              BorderFactory.createEtchedBorder(), "General:"));

        JPanel interior = new JPanel(new LCBLayout(6));
        interior.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));

        antialias = new JCheckBox("Draw anti-aliased");
        antialias.setSelected(chart.getAntiAlias());
        interior.add(antialias);
        interior.add(new JLabel(""));
        interior.add(new JLabel(""));
        interior.add(new JLabel("Background paint:"));
        background = new PaintSample(chart.getBackgroundPaint());
        interior.add(background);
        JButton button = new JButton("Select...");
        button.setActionCommand("BackgroundPaint");
        button.addActionListener(this);
        interior.add(button);

        interior.add(new JLabel("Series Paint:"));
        JTextField info = new JTextField("No editor implemented");
        info.setEnabled(false);
        interior.add(info);
        button = new JButton("Edit...");
        button.setEnabled(false);
        interior.add(button);

        interior.add(new JLabel("Series Stroke:"));
        info = new JTextField("No editor implemented");
        info.setEnabled(false);
        interior.add(info);
        button = new JButton("Edit...");
        button.setEnabled(false);
        interior.add(button);

        interior.add(new JLabel("Series Outline Paint:"));
        info = new JTextField("No editor implemented");
        info.setEnabled(false);
        interior.add(info);
        button = new JButton("Edit...");
        button.setEnabled(false);
        interior.add(button);

        interior.add(new JLabel("Series Outline Stroke:"));
        info = new JTextField("No editor implemented");
        info.setEnabled(false);
        interior.add(info);
        button = new JButton("Edit...");
        button.setEnabled(false);
        interior.add(button);

        general.add(interior, BorderLayout.NORTH);
        other.add(general, BorderLayout.NORTH);

        JPanel parts = new JPanel(new BorderLayout());

        //Title title = chart.getTitle();
        Legend legend = chart.getLegend();
        Plot plot = chart.getPlot();

        JTabbedPane tabs = new JTabbedPane();

        //StandardTitle t = (StandardTitle)title;
        //titlePropertiesPanel = new TitlePropertyEditPanel(t);
        //titlePropertiesPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        //tabs.addTab("Title", titlePropertiesPanel);

        if (legend != null) {
            legendPropertiesPanel = new LegendPropertyEditPanel(legend);
            legendPropertiesPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
            tabs.addTab("Legend", legendPropertiesPanel);
        }

        plotPropertiesPanel = new PlotPropertyEditPanel(plot);
        plotPropertiesPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        tabs.addTab("Plot", plotPropertiesPanel);

        tabs.add("Other", other);
        parts.add(tabs, BorderLayout.NORTH);
        add(parts);
    }

    /**
     * Returns a reference to the title property sub-panel.
     *
     * @return a reference to the title property sub-panel.
     */
    public TitlePropertyEditPanel getTitlePropertyEditPanel() {
        return titlePropertiesPanel;
    }

    /**
     * Returns a reference to the legend property sub-panel.
     *
     * @return a reference to the legend property sub-panel.
     */
    public LegendPropertyEditPanel getLegendPropertyEditPanel() {
        return legendPropertiesPanel;
    }

    /**
     * Returns a reference to the plot property sub-panel.
     *
     * @return a reference to the plot property sub-panel.
     */
    public PlotPropertyEditPanel getPlotPropertyEditPanel() {
        return plotPropertiesPanel;
    }

    /**
     * Returns the current setting of the anti-alias flag.
     *
     * @return <code>true</code> if anti-aliasing is enabled.
     */
    public boolean getAntiAlias() {
        return antialias.isSelected();
    }

    /**
     * Returns the current background paint.
     *
     * @return the current background paint.
     */
    public Paint getBackgroundPaint() {
        return background.getPaint();
    }

    /**
     * Handles user interactions with the panel.
     *
     * @param event  a BackgroundPaint action.
     */
    public void actionPerformed(ActionEvent event) {
        String command = event.getActionCommand();
        if (command.equals("BackgroundPaint")) {
            attemptModifyBackgroundPaint();
        }
    }

    /**
     * Allows the user the opportunity to select a new background paint.  Uses
     * JColorChooser, so we are only allowing a subset of all Paint objects to
     * be selected (fix later).
     */
    private void attemptModifyBackgroundPaint() {
        Color c;
        c = JColorChooser.showDialog(this, "Background Color", Color.blue);
        if (c != null) {
            background.setPaint(c);
        }
    }

    /**
     * Updates the properties of a chart to match the properties defined on the
     * panel.
     *
     * @param chart  the chart.
     */
    public void updateChartProperties(JFreeChart chart) {

        if (legendPropertiesPanel != null) {
            legendPropertiesPanel.setLegendProperties(chart.getLegend());
        }

        plotPropertiesPanel.updatePlotProperties(chart.getPlot());

        chart.setAntiAlias(getAntiAlias());
        chart.setBackgroundPaint(getBackgroundPaint());
    }

}
