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
 * ----------------
 * CompassDemo.java
 * ----------------
 * (C) Copyright 2002, by the Australian Antarctic Division and Contributors.
 *
 * Original Author:  Bryan Scott (for the Australian Antarctic Division);
 * Contributor(s):   David Gilbert (for Simba Management Limited);
 *
 * $Id: CompassDemo.java,v 1.1.1.1 2003/07/17 10:06:32 Ian.Mayo Exp $
 *
 * Changes
 * -------
 * 25-Sep-2002 : Version 1, contributed by Bryan Scott (DG);
 * 10-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 *
 */

package com.jrefinery.chart.demo;

import java.awt.Toolkit;
import java.awt.Insets;
import java.awt.Dimension;
import java.awt.Color;
import java.awt.Stroke;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.SwingConstants;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.border.TitledBorder;
import com.jrefinery.data.DefaultMeterDataset;
import com.jrefinery.chart.JFreeChart;
import com.jrefinery.chart.ChartPanel;
import com.jrefinery.chart.CompassPlot;
import com.jrefinery.ui.Spinner;

/**
 * A demo application showing how to use the CompassPlot class.
 *
 * @author BS
 */
public class CompassDemo extends JPanel {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

		/** The available needle types. */
    public static final String[] NEEDLE_TYPES
        = { "Pointer", "Line", "Long", "Pin", "Plum", "Pointer", "Ship", "Wind", "Arrow" };

    /** Dataset 1. */
    DefaultMeterDataset compassData = new DefaultMeterDataset();

    /** Dataset 2. */
    DefaultMeterDataset shipData = new DefaultMeterDataset();

    /** The compass plot. */
    private CompassPlot compassPlot = new CompassPlot(compassData);

    /** The chart. */
    private JFreeChart compassChart = new JFreeChart("Compass Test",
                                                     JFreeChart.DEFAULT_TITLE_FONT,
                                                     compassPlot, false);

    /** The chart panel. */
    private ChartPanel panelCompass = new ChartPanel(compassChart);

    /** A grid layout. */
    private GridLayout gridLayout1 = new GridLayout();

    /** A panel. */
    private JPanel panelCompassHolder = new JPanel();

    /** A border layout. */
    private BorderLayout borderLayout = new BorderLayout();

    /** A panel. */
    private JPanel jPanel12 = new JPanel();

    /** A checkbox. */
    private JCheckBox checkWindNull = new JCheckBox();

    /** A checkbox. */
    private JCheckBox checkShipNull = new JCheckBox();

   // SpinnerNumberModel modelWind = new SpinnerNumberModel(0, -1, 361, 1);
   // SpinnerNumberModel modelShip = new SpinnerNumberModel(0, -1, 361, 1);
   // JSpinner spinWind = new JSpinner(modelWind);
   // JSpinner spinShip = new JSpinner(modelShip);

    /** The wind spinner control. */
    private Spinner spinWind = new Spinner(270);

    /** The ship spinner control. */
    private Spinner spinShip = new Spinner(45);

    /** A panel. */
    private JPanel jPanel1 = new JPanel();

    /** A combo box. */
    private JComboBox pick1Pointer = new JComboBox(NEEDLE_TYPES);

    /** A panel. */
    private JPanel jPanel2 = new JPanel();

    /** A combo box. */
    private JComboBox pick2Pointer = new JComboBox(NEEDLE_TYPES);

    /** A titled border. */
    private TitledBorder titledBorder1;

    /** A titled border. */
    private TitledBorder titledBorder2;

    /** A grid bag layout. */
    private GridBagLayout gridBagLayout1 = new GridBagLayout();

    /** A grid bag layout. */
    private GridBagLayout gridBagLayout2 = new GridBagLayout();

    /** A titled border. */
    private TitledBorder titledBorder3;

    /** A grid layout. */
    private GridLayout gridLayout2 = new GridLayout();

    /**
     * Default constructor.
     */
    public CompassDemo() {
        try {
            compassData.setRange(new Double(0.0), new Double(360.0));
            shipData.setRange(new Double(0.0), new Double(360.0));
            compassPlot.addData(shipData);
            compassPlot.setSeriesNeedle(0, 7);
            compassPlot.setSeriesNeedle(1, 5);
            compassPlot.setSeriesPaint(0, Color.blue);
            compassPlot.setSeriesOutlinePaint(0, Color.blue);
            compassPlot.setSeriesPaint(1, Color.red);
            compassPlot.setSeriesOutlineStroke(new Stroke[]{new BasicStroke(3)});
            pick1Pointer.setSelectedIndex(7);
            pick2Pointer.setSelectedIndex(5);
            jbInit();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Initialises the user interface.
     *
     * @throws Exception if there are any exceptions.
     */
    void jbInit() throws Exception {
        titledBorder1 = new TitledBorder("");
        titledBorder2 = new TitledBorder("");
        titledBorder3 = new TitledBorder("");
        this.setLayout(gridLayout1);
        panelCompassHolder.setLayout(borderLayout);
        checkWindNull.setHorizontalTextPosition(SwingConstants.LEADING);
        checkWindNull.setText("Null");
        checkWindNull.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                checkWindNullActionPerformed(e);
            }
        });
        checkShipNull.setHorizontalTextPosition(SwingConstants.LEFT);
        checkShipNull.setText("Null");
        checkShipNull.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                checkShipNullActionPerformed(e);
            }
        });

        spinShip.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                Spinner spinner = (Spinner) evt.getSource();
                //shipData.setValue((new Double(((Integer)spinner.getValue()).intValue())));
                shipData.setValue((double) spinner.getValue());
            }
        });

        spinWind.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                Spinner spinner = (Spinner) evt.getSource();
                // compassData.setValue((new Double(((Integer)spinner.getValue()).intValue())));
                compassData.setValue((double) spinner.getValue());
            }
        });
        jPanel12.setLayout(gridLayout2);
        jPanel2.setBorder(titledBorder1);
        jPanel2.setLayout(gridBagLayout2);
        jPanel1.setBorder(titledBorder2);
        jPanel1.setLayout(gridBagLayout1);
        titledBorder1.setTitle("Second Pointer");
        titledBorder2.setTitle("First Pointer");
        titledBorder3.setTitle("Plot Options");
        pick2Pointer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                pick2PointerActionPerformed(e);
            }
        });
        pick1Pointer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                pick1PointerActionPerformed(e);
            }
        });
        this.add(panelCompassHolder, null);
        panelCompassHolder.add(jPanel12, BorderLayout.SOUTH);
        jPanel12.add(jPanel1, null);

        jPanel1.add(pick1Pointer,
                    new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0,
                                           GridBagConstraints.CENTER,
                                           GridBagConstraints.HORIZONTAL,
                                           new Insets(0, 0, 0, 0),
                                           0, 0));

        jPanel1.add(checkWindNull,
                    new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                                           GridBagConstraints.CENTER,
                                           GridBagConstraints.NONE,
                                           new Insets(0, 0, 0, 0),
                                           0, 0));

        jPanel1.add(spinWind,
                    new GridBagConstraints(2, 0, 1, 1, 1.0, 0.0,
                                           GridBagConstraints.CENTER,
                                           GridBagConstraints.BOTH,
                                           new Insets(0, 0, 0, 0),
                                           0, 0));

        jPanel12.add(jPanel2, null);

        jPanel2.add(pick2Pointer,
                    new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0,
                                           GridBagConstraints.CENTER,
                                           GridBagConstraints.HORIZONTAL,
                                           new Insets(0, 0, 0, 0),
                                           0, 0));

        jPanel2.add(checkShipNull,
                    new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                                           GridBagConstraints.CENTER,
                                           GridBagConstraints.NONE,
                                           new Insets(0, 0, 0, 0),
                                           0, 0));

        jPanel2.add(spinShip,
                    new GridBagConstraints(2, 0, 1, 1, 1.0, 0.0,
                    GridBagConstraints.CENTER,
                    GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 0),
                    0, 0));

        panelCompassHolder.add(panelCompass, BorderLayout.CENTER);

    }

    /**
     * Entry point for the demo application.
     *
     * @param args  ignored.
     */
    public static void main(String[] args) {

        final CompassDemo panel = new CompassDemo();

        JFrame frame = new JFrame();
        frame.getContentPane().setLayout(new BorderLayout(5, 5));
        frame.setDefaultCloseOperation(3);
        frame.setTitle("Compass Demo");
        frame.getContentPane().add(panel, BorderLayout.CENTER);
        frame.setSize(700, 400);
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setLocation((d.width - frame.getSize().width) / 2,
                          (d.height - frame.getSize().height) / 2);
        frame.setVisible(true);
    }

    /**
     * Updates the data.
     *
     * @param value  the value.
     */
    public void adjustData(double value) {

        Number val = compassData.getValue();
        double newVal = value;

        if (val != null) {
            newVal += val.doubleValue();
        }

        if (newVal > 360) {
            newVal = 0;
        }

        if (newVal < 0) {
            newVal = 360;
        }

        compassData.setValue(new Double(newVal));

    }

    /**
     * Handles an action event.
     *
     * @param e  the event.
     */
    void checkWindNullActionPerformed(ActionEvent e) {

        if (checkWindNull.isSelected()) {
            compassData.setValue(null);
            spinWind.setEnabled(false);
        }
        else {
            //  compassData.setValue((new Double(((Integer)spinWind.getValue()).intValue())));
            compassData.setValue((double) spinWind.getValue());
            spinWind.setEnabled(true);
        }
    }

    /**
     * Handles an action event.
     *
     * @param e  the event.
     */
    void checkShipNullActionPerformed(ActionEvent e) {

        if (checkShipNull.isSelected()) {
            shipData.setValue(null);
            spinShip.setEnabled(false);
        }
        else {
           // shipData.setValue((new Double(((Integer)spinShip.getValue()).intValue())));
           shipData.setValue((double) spinShip.getValue());
           spinShip.setEnabled(true);
        }
    }

    /**
     * Handles an action event.
     *
     * @param e  the event.
     */
    void pick2PointerActionPerformed(ActionEvent e) {
        compassPlot.setSeriesNeedle(1, pick2Pointer.getSelectedIndex());
        compassPlot.setSeriesPaint(1, Color.red);
       // compassPlot.setSeriesOutlineStroke(new Stroke[] {new BasicStroke(3)});
    }

    /**
     * Handles an action event.
     *
     * @param e  the event.
     */
    void pick1PointerActionPerformed(ActionEvent e) {
        compassPlot.setSeriesNeedle(0, pick1Pointer.getSelectedIndex());
        compassPlot.setSeriesPaint(0, Color.blue);
        compassPlot.setSeriesOutlinePaint(0, Color.blue);
    }

}
