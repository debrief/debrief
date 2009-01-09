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
 * --------------------
 * ThermometerDemo.java
 * --------------------
 * (C) Copyright 2002, by Australian Antarctic Division and Contributors.
 *
 * Original Author:  Bryan Scott (for Australian Antarctic Division).
 * Contributor(s):   David Gilbert (for Simba Management Limited);
 *
 * $Id: ThermometerDemo.java,v 1.1.1.1 2003/07/17 10:06:36 Ian.Mayo Exp $
 *
 * Changes (since 24-Apr-2002)
 * ---------------------------
 * 24-Apr-2002 : added standard source header (DG);
 * 17-Sep-2002 : fixed errors reported by Checkstyle 2.3 (DG);
 *
 */
package com.jrefinery.chart.demo;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.text.DecimalFormat;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import com.jrefinery.chart.JFreeChart;
import com.jrefinery.chart.ChartPanel;
import com.jrefinery.chart.MeterPlot;
import com.jrefinery.chart.JThermometer;
import com.jrefinery.data.DefaultMeterDataset;

/**
 * A demonstration application for the thermometer plot.
 *
 * @author BRS
 */
public class ThermometerDemo extends JPanel {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

		/** Options for the value label position combo box. */
    protected static final String[] OPTIONS = {"None", "Right", "Bulb"};

    /** The dataset. */
    private DefaultMeterDataset data = new DefaultMeterDataset();

    /** The meter plot (dial). */
    private MeterPlot meterplot = new MeterPlot(data);

    /** The meter chart (dial). */
    private JFreeChart meterchart = new JFreeChart("Meter Chart", JFreeChart.DEFAULT_TITLE_FONT,
                                                   meterplot, false);

    /** The meter panel. */
    private ChartPanel panelMeter = new ChartPanel(meterchart);

    /** Panel 1. */
    private JPanel jPanel1 = new JPanel();

    /** Increment button for thermometer ?. */
    private JButton butUp3 = new JButton();

    /** Decrement button for thermometer ?. */
    private JButton butDown3 = new JButton();

    /** Panel 2. */
    private JPanel jPanel2 = new JPanel();

    /** Borderlayout 2. */
    private BorderLayout borderLayout2 = new BorderLayout();

    /** Panel 3. */
    private JPanel jPanel3 = new JPanel();

    /** Borderlayout 3. */
    private BorderLayout borderLayout3 = new BorderLayout();

    /** Panel 4. */
    private JPanel jPanel4 = new JPanel();

    /** Decrement button for thermometer ?. */
    private JButton butDown2 = new JButton();

    /** Increment button for thermometer ?. */
    private JButton butUp2 = new JButton();

    /** Panel 5. */
    private JPanel jPanel5 = new JPanel();

    /** Grid layout 1. */
    private GridLayout gridLayout1 = new GridLayout();

    /** Panel 6. */
    private JPanel jPanel6 = new JPanel();

    /** Increment button for thermometer ?. */
    private JButton butUp1 = new JButton();

    /** Decrement button for thermometer ?. */
    private JButton butDown1 = new JButton();

    /** Thermometer 1. */
    private JThermometer thermo1 = new JThermometer();

    /** Thermometer 2. */
    private JThermometer thermo2 = new JThermometer();

    /** Thermometer 2. */
    private JThermometer thermo3 = new JThermometer();

    /** Array of thermometers. */
    private JThermometer[] thermo = new JThermometer[3];

    /** Borderlayout 1. */
    private BorderLayout borderLayout1 = new BorderLayout();

    /** Panel 7. */
    private JPanel jPanel7 = new JPanel();

    /** Panel 8. */
    private JPanel jPanel8 = new JPanel();

    /** Panel 9. */
    private JPanel jPanel9 = new JPanel();

    /** Grid layout 2. */
    private GridLayout gridLayout2 = new GridLayout();

    /** Grid layout 3. */
    private GridLayout gridLayout3 = new GridLayout();

    /** Grid layout 4. */
    private GridLayout gridLayout4 = new GridLayout();

    /** Combo box 1 for value label position. */
    private JComboBox pickShow1 = new JComboBox(OPTIONS);

    /** Combo box 2 for value label position. */
    private JComboBox pickShow2 = new JComboBox(OPTIONS);

    /** Combo box 3 for value label position. */
    private JComboBox pickShow3 = new JComboBox(OPTIONS);

    /** An array of combo boxes. */
    private JComboBox[] pickShow = new JComboBox[3];

    /** Panel 10. */
    private JPanel jPanel10 = new JPanel();

    /** Borderlayout 4. */
    private BorderLayout borderLayout4 = new BorderLayout();

    /** Panel 11. */
    private JPanel jPanel11 = new JPanel();

    /** Decrement button for thermometer ?. */
    private JButton butDown4 = new JButton();

    /** Increment button for thermometer ?. */
    private JButton butUp4 = new JButton();

    /**
     * Default constructor.
     */
    public ThermometerDemo() {
        try {
            jbInit();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Initialises the class.
     *
     * @throws Exception for any exception.
     */
    void jbInit() throws Exception {

        data.setRange(new Double(-20), new Double(20));
        thermo[0] = thermo1;
        thermo[1] = thermo2;
        thermo[2] = thermo3;

        thermo[0].setValue(0.0);
        thermo[1].setValue(0.2);
        thermo[2].setValue(0.3);

        thermo[0].setBackground(Color.white);
        thermo[2].setBackground(Color.white);

        thermo[0].setOutlinePaint(null);
        thermo[1].setOutlinePaint(null);
        thermo[2].setOutlinePaint(null);

        thermo[0].setUnits(0);
        thermo[1].setUnits(1);
        thermo[2].setUnits(2);

        //thermo[0].setFont(new Font("SansSerif", Font.BOLD, 20));
        thermo[0].setShowValueLines(true);
        thermo[0].setFollowDataInSubranges(true);
        thermo[1].setValueLocation(1);

        thermo[1].setForeground(Color.blue);
        thermo[2].setForeground(Color.pink);

        thermo[0].setRange(-10.0, 40.0);
        thermo[0].setSubrangeInfo(0, -50.0,  20.0, -10.0, 22.0);
        thermo[0].setSubrangeInfo(1,  20.0,  24.0,  18.0, 26.0);
        thermo[0].setSubrangeInfo(2,  24.0, 100.0,  22.0, 40.0);

        thermo[0].addTitle("Sea Water Temp");
        thermo[1].addTitle("Air Temp", new Font("SansSerif", Font.PLAIN, 16));
        thermo[2].addTitle("Ship Temp", new Font("SansSerif", Font.ITALIC + Font.BOLD, 20));

        thermo[1].setValueFormat(new DecimalFormat("#0.0"));
        thermo[2].setValueFormat(new DecimalFormat("#0.00"));

        pickShow[0] = pickShow1;
        pickShow[1] = pickShow2;
        pickShow[2] = pickShow3;

        this.setLayout(gridLayout1);
        butDown3.setText("<");
        butDown3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setValue(2, -1);
            }
        });
        butUp3.setText(">");
        butUp3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setValue(2, 1);
            }
        });
        jPanel1.setLayout(borderLayout2);
        jPanel3.setLayout(borderLayout3);
        butDown2.setText("<");
        butDown2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setValue(1, -1);
            }
        });
        butUp2.setText(">");
        butUp2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setValue(1, 1);
            }
        });
        butUp1.setText(">");
        butUp1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setValue(0, 1);
            }
        });
        butDown1.setText("<");
        butDown1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setValue(0, -1);
            }
        });
        jPanel5.setLayout(borderLayout1);
        pickShow1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setShowValue(0);
            }
        });
        pickShow2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setShowValue(1);
            }
        });
        pickShow3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setShowValue(2);
            }
        });

        jPanel9.setLayout(gridLayout2);
        gridLayout2.setColumns(1);
        jPanel8.setLayout(gridLayout3);
        jPanel7.setLayout(gridLayout4);
        jPanel5.setBorder(BorderFactory.createEtchedBorder());
        jPanel3.setBorder(BorderFactory.createEtchedBorder());
        jPanel1.setBorder(BorderFactory.createEtchedBorder());
        jPanel6.setBackground(Color.white);
        jPanel2.setBackground(Color.white);
        jPanel9.setBackground(Color.white);
        jPanel10.setLayout(borderLayout4);
        butDown4.setText("<");
        butDown4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setMeterValue(-1.1);
            }
        });
        butUp4.setText(">");
        butUp4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setMeterValue(1.1);
            }
        });
        jPanel1.add(thermo3,  BorderLayout.CENTER);
        jPanel1.add(jPanel2, BorderLayout.SOUTH);
        jPanel2.add(butDown3, null);
        jPanel2.add(butUp3, null);
        jPanel1.add(jPanel9,  BorderLayout.NORTH);
        jPanel9.add(pickShow3, null);
        this.add(jPanel10, null);
        jPanel10.add(jPanel11, BorderLayout.SOUTH);
        jPanel11.add(butDown4, null);
        jPanel11.add(butUp4, null);
        jPanel4.add(butDown2, null);
        jPanel4.add(butUp2, null);
        jPanel3.add(jPanel8, BorderLayout.NORTH);
        jPanel8.add(pickShow2, null);
        jPanel3.add(thermo2, BorderLayout.CENTER);
        jPanel3.add(jPanel4, BorderLayout.SOUTH);
        this.add(jPanel5, null);
        jPanel5.add(thermo1,  BorderLayout.CENTER);
        jPanel5.add(jPanel6, BorderLayout.SOUTH);
        jPanel6.add(butDown1, null);
        jPanel6.add(butUp1, null);
        jPanel5.add(jPanel7, BorderLayout.NORTH);
        jPanel7.add(pickShow1, null);
        this.add(jPanel3, null);
        this.add(jPanel1, null);
        jPanel10.add(panelMeter, BorderLayout.CENTER);
    }

    /**
     * Starting point for the demo application.
     *
     * @param args  ignored.
     */
    public static void main(String[] args) {

        final ThermometerDemo panel = new ThermometerDemo();

        JFrame frame = new JFrame();
        frame.getContentPane().setLayout(new BorderLayout(5, 5));
        frame.setDefaultCloseOperation(3);
        frame.setTitle("Thermometer Test");
        frame.getContentPane().add(panel, BorderLayout.CENTER);
        frame.setSize(700, 400);
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setLocation((d.width - frame.getSize().width) / 2,
                          (d.height - frame.getSize().height) / 2);
        frame.setVisible(true);

    }

    /**
     * Sets the value of one of the thermometers.
     *
     * @param thermometer  the thermometer index.
     * @param value  the value.
     */
    void setValue(int thermometer, double value) {
        if ((thermometer >= 0) && (thermometer < 3)) {
            try {
                thermo[thermometer].setValue(thermo[thermometer].getValue().doubleValue() + value);
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * Sets the meter value.
     *
     * @param value  the value.
     */
    void setMeterValue(double value) {
        try {
            double newValue = value;
            if (data.isValueValid()) {
                newValue += data.getValue().doubleValue();
            }
            data.setValue(new Double(newValue));
        }
        catch (Exception ex) {
            System.err.println(ex.getMessage());
        }
    }

    /**
     * Sets the value label position for one of the thermometers.
     *
     * @param thermometer  the thermometer index.
     */
    void setShowValue(int thermometer) {
        if ((thermometer >= 0) && (thermometer < 3)) {
            thermo[thermometer].setValueLocation(pickShow[thermometer].getSelectedIndex());
        }
    }

}
