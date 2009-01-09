/* ======================================
 * JFreeChart : a free Java chart library
 * ======================================
 *
 * Project Info:  http://www.object-refinery.com/jfreechart/index.html
 * Project Lead:  David Gilbert (david.gilbert@object-refinery.com);
 *
 * (C) Copyright 2000, 2001, Simba Management Limited and Contributors.
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
 * --------------------------------
 * NumberAxisPropertyEditPanel.java
 * --------------------------------
 * (C) Copyright 2000, 2001, Simba Management Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: NumberAxisPropertyEditPanel.java,v 1.1.1.1 2003/07/17 10:06:47 Ian.Mayo Exp $
 *
 * Changes (from 24-Aug-2001)
 * --------------------------
 * 24-Aug-2001 : Added standard source header. Fixed DOS encoding problem (DG);
 * 07-Nov-2001 : Separated the JCommon Class Library classes, JFreeChart now requires
 *               jcommon.jar (DG);
 * 12-Dec-2001 : minor change due to bug fix elsewhere (DG);
 *
 */

package com.jrefinery.chart.ui;

import java.awt.Color;
import java.awt.BasicStroke;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JCheckBox;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JColorChooser;
import javax.swing.BorderFactory;
import com.jrefinery.chart.Axis;
import com.jrefinery.chart.NumberAxis;
import com.jrefinery.layout.LCBLayout;
import com.jrefinery.ui.PaintSample;
import com.jrefinery.ui.StrokeSample;
import com.jrefinery.ui.StrokeChooserPanel;

/**
 * A panel for editing the properties of a value axis.
 *
 * @author DG
 */
class NumberAxisPropertyEditPanel extends AxisPropertyEditPanel implements FocusListener {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

		/** A flag that indicates whether or not the axis range is determined
     *  automatically.
     */
    private boolean autoRange;

    /** The lowest value in the axis range. */
    private double minimumValue;

    /** The highest value in the axis range. */
    private double maximumValue;

    /** A checkbox that indicates whether or not the axis range is determined
     *  automatically.
     */
    private JCheckBox autoRangeCheckBox;

    /** A text field for entering the minimum value in the axis range. */
    private JTextField minimumRangeValue;

    /** A text field for entering the maximum value in the axis range. */
    private JTextField maximumRangeValue;

    /** A checkbox that controls whether or not gridlines are showing for the
     *  axis.
     */
    private JCheckBox showGridLinesCheckBox;

    /** The paint selected for drawing the gridlines. */
    private PaintSample gridPaintSample;

    /** The stroke selected for drawing the gridlines. */
    private StrokeSample gridStrokeSample;

    /** An array of stroke samples to choose from (since I haven't written a
     *  decent StrokeChooser component yet).
     */
    private StrokeSample[] availableStrokeSamples;

    /**
     * Standard constructor: builds a property panel for the specified axis.
     *
     * @param axis  the axis, which should be changed.
     */
    public NumberAxisPropertyEditPanel(NumberAxis axis) {

        super(axis);

        autoRange = axis.isAutoRange();
        minimumValue = axis.getMinimumAxisValue();
        maximumValue = axis.getMaximumAxisValue();

        gridPaintSample = new PaintSample(axis.getGridPaint());
        gridStrokeSample = new StrokeSample(axis.getGridStroke());

        availableStrokeSamples = new StrokeSample[3];
        availableStrokeSamples[0] = new StrokeSample(new BasicStroke(1.0f));
        availableStrokeSamples[1] = new StrokeSample(new BasicStroke(2.0f));
        availableStrokeSamples[2] = new StrokeSample(new BasicStroke(3.0f));

        JTabbedPane other = getOtherTabs();

        JPanel range = new JPanel(new LCBLayout(3));
        range.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

        range.add(new JPanel());
        autoRangeCheckBox = new JCheckBox("Auto-adjust range:", autoRange);
        autoRangeCheckBox.setActionCommand("AutoRangeOnOff");
        autoRangeCheckBox.addActionListener(this);
        range.add(autoRangeCheckBox);
        range.add(new JPanel());

        range.add(new JLabel("Minimum range value:"));
        minimumRangeValue = new JTextField(Double.toString(minimumValue));
        minimumRangeValue.setEnabled(!autoRange);
        minimumRangeValue.setActionCommand("MinimumRange");
        minimumRangeValue.addActionListener(this);
        minimumRangeValue.addFocusListener(this);
        range.add(minimumRangeValue);
        range.add(new JPanel());

        range.add(new JLabel("Maximum range value:"));
        maximumRangeValue = new JTextField(Double.toString(maximumValue));
        maximumRangeValue.setEnabled(!autoRange);
        maximumRangeValue.setActionCommand("MaximumRange");
        maximumRangeValue.addActionListener(this);
        maximumRangeValue.addFocusListener(this);
        range.add(maximumRangeValue);
        range.add(new JPanel());

        other.add("Range", range);

        JPanel grid = new JPanel(new LCBLayout(3));
        grid.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

        grid.add(new JPanel());
        showGridLinesCheckBox = new JCheckBox("Show grid lines",
            axis.isGridLinesVisible());
        grid.add(showGridLinesCheckBox);
        grid.add(new JPanel());

        grid.add(new JLabel("Grid stroke:"));
        JButton button = new JButton("Set stroke...");
        button.setActionCommand("GridStroke");
        button.addActionListener(this);
        grid.add(gridStrokeSample);
        grid.add(button);

        grid.add(new JLabel("Grid paint:"));
        button = new JButton("Set paint...");
        button.setActionCommand("GridPaint");
        button.addActionListener(this);
        grid.add(gridPaintSample);
        grid.add(button);

        other.add("Grid", grid);

    }

    /**
     * Returns the current setting of the auto-range property.
     *
     * @return <code>true</code> if auto range is enabled.
     */
    public boolean isAutoRange() {
        return autoRange;
    }

    /**
     * Returns the current setting of the minimum value in the axis range.
     *
     * @return the current setting of the minimum value in the axis range.
     */
    public double getMinimumValue() {
        return minimumValue;
    }

    /**
     * Returns the current setting of the maximum value in the axis range.
     *
     * @return the current setting of the maximum value in the axis range.
     */
    public double getMaximumValue() {
        return maximumValue;
    }

    /**
     * Handles actions from within the property panel.
     * @param event an event.
     */
    public void actionPerformed(ActionEvent event) {
        String command = event.getActionCommand();
        if (command.equals("GridStroke")) {
            attemptGridStrokeSelection();
        }
        else if (command.equals("GridPaint")) {
            attemptGridPaintSelection();
        }
        else if (command.equals("AutoRangeOnOff")) {
            toggleAutoRange();
        }
        else if (command.equals("MinimumRange")) {
            validateMinimum();
        }
        else if (command.equals("MaximumRange")) {
            validateMaximum();
        }
        else {
            // pass to the super-class for handling
            super.actionPerformed(event);
        }
    }

    /**
     * Handle a grid stroke selection.
     */
    private void attemptGridStrokeSelection() {
        StrokeChooserPanel panel = new StrokeChooserPanel(null, availableStrokeSamples);
        int result = JOptionPane.showConfirmDialog(this, panel,
                                                   "Stroke Selection",
                                                   JOptionPane.OK_CANCEL_OPTION,
                                                   JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            gridStrokeSample.setStroke(panel.getSelectedStroke());
        }
    }

    /**
     * Handle a grid paint selection.
     */
    private void attemptGridPaintSelection() {
        Color c;
        c = JColorChooser.showDialog(this, "Grid Color", Color.blue);
        if (c != null) {
            gridPaintSample.setPaint(c);
        }
    }

    /**
     * Does nothing.
     *
     * @param event  the event.
     */
    public void focusGained(FocusEvent event) {
        // don't need to do anything
    }

    /**
     *  Revalidates minimum/maximum range.
     *
     *  @param event  the event.
     */
    public void focusLost(FocusEvent event) {
        if (event.getSource() == minimumRangeValue) {
            validateMinimum();
        }
        else if (event.getSource() == maximumRangeValue) {
            validateMaximum();
        }
    }

    /**
     *  Toggle the auto range setting.
     */
    public void toggleAutoRange() {
        autoRange = autoRangeCheckBox.isSelected();
        if (autoRange) {
            minimumRangeValue.setText(Double.toString(minimumValue));
            minimumRangeValue.setEnabled(false);
            maximumRangeValue.setText(Double.toString(maximumValue));
            maximumRangeValue.setEnabled(false);
        }
        else {
            minimumRangeValue.setEnabled(true);
            maximumRangeValue.setEnabled(true);
        }
    }

    /**
     * Revalidate the range minimum.
     */
    public void validateMinimum() {
        double newMin;
        try {
            newMin = Double.parseDouble(this.minimumRangeValue.getText());
            if (newMin >= maximumValue) {
                newMin = minimumValue;
            }
        }
        catch (NumberFormatException e) {
            newMin = minimumValue;
        }

        minimumValue = newMin;
        minimumRangeValue.setText(Double.toString(minimumValue));
    }

    /**
     * Revalidate the range maximum.
     */
    public void validateMaximum() {
        double newMax;
        try {
            newMax = Double.parseDouble(this.maximumRangeValue.getText());
            if (newMax <= minimumValue) {
                newMax = maximumValue;
            }
        }
        catch (NumberFormatException e) {
            newMax = maximumValue;
        }

        maximumValue = newMax;
        maximumRangeValue.setText(Double.toString(maximumValue));
    }

    /**
     * Sets the properties of the specified axis to match the properties
     * defined on this panel.
     *
     * @param axis  the axis.
     */
    public void setAxisProperties(Axis axis) {
        super.setAxisProperties(axis);
        NumberAxis numberAxis = (NumberAxis) axis;
        numberAxis.setAutoRange(this.autoRange);
        if (!autoRange) {
            numberAxis.setMinimumAxisValue(this.minimumValue);
            numberAxis.setMaximumAxisValue(this.maximumValue);
        }
        numberAxis.setGridLinesVisible(this.showGridLinesCheckBox.isSelected());
        numberAxis.setGridPaint(this.gridPaintSample.getPaint());
        numberAxis.setGridStroke(this.gridStrokeSample.getStroke());
    }

}
