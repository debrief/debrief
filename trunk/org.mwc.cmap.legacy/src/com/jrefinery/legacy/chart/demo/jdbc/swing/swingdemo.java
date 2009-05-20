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
 * swingdemo.java
 * --------------
 * (C) Copyright 2000-2002, by Bryan Scott and Contributors.
 *
 * Original Author:  Bryan Scott;
 * Contributor(s):   David Gilbert (for Simba Management Limited);
 *
 *
 * Changes
 * -------
 * 14-Mar-2002 : Version 1 contributed by Bryan Scott (DG);
 * 18-May-2002 : Changed to reflect changes in jdbc datasets (BS);
 * 25-Jun-2002 : Removed unnecessary imports (DG);
 *
 */

package com.jrefinery.legacy.chart.demo.jdbc.swing;

import com.jrefinery.legacy.chart.AbstractTitle;
import com.jrefinery.legacy.chart.Axis;
import com.jrefinery.legacy.chart.ChartFactory;
import com.jrefinery.legacy.chart.ChartPanel;
import com.jrefinery.legacy.chart.JFreeChart;
import com.jrefinery.legacy.chart.Legend;
import com.jrefinery.legacy.chart.TextTitle;
import com.jrefinery.legacy.chart.VerticalNumberAxis;
import com.jrefinery.legacy.chart.XYPlot;
import com.jrefinery.legacy.data.JdbcXYDataset;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.Statement;
import java.util.Vector;
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.SwingConstants;
import javax.swing.BorderFactory;

/**
 * A demonstration of a swing application which display chart plots from a JDBC
 * data source.
 */
public class swingdemo extends JFrame {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

		/** Is this running standalone */
    public boolean isStandalone = false;

    /** Available parameters to plot */
    private final static String[] PARAMETERS = {
        "A_TEMP_P", "A_TEMP_S", "BAR", "BAR_T",
        "BIO_RAD", "BIO_TEMP", "DEPTH", "FLU_FLOW",
        "FLU_VALUE", "GPS_COG", "GPS_SOG", "HUMID_P",
        "HUMID_S", "LICOR_R", "PITCH_MAX", "PITCH_MIN",
        "ROLL_MAX", "ROLL_MIN", "SH", "SOLAR_R_P",
        "SOLAR_R_S", "SS", "TSG_CNDUCT", "TSG_FLOW",
        "TSG_SALIN", "TSG_TEMP", "UV", "WDPA",
        "WDPT", "WDSA", "WDST", "WSPA",
        "WSPT", "WSSA", "WSST", "W_TEMP",
        "W_TEMP_HI"};

    /// Chart Stuff
    JFreeChart chart;

    /// JDBC Connection details
    protected Connection conn;
    protected String currentURL = null;
    protected String currentDriver = null;
    protected String currentSchema = "marine";
    protected String currentUser = "dataaccess";
    protected String currentPassword = "dataaccess";
    protected Statement generalStmt;
    protected JdbcXYDataset chartData;
    final static char alphaStart = 'b';

    /** Currently selected list of parameters to plot */
    Vector<String> chartParameters = new Vector<String>();

    /**  Process management - The currently running update thread */
    protected Thread workingThread;

    /// GUI Components
    JPanel panelHeader = new JPanel();
    JPanel panelGraph = new JPanel();
    JLabel labelCurrentVoyage = new JLabel();
    JLabel textComments = new JLabel();
    JLabel labelChartPickText = new JLabel();
    JLabel jLabel1 = new JLabel();
    JLabel jLabel2 = new JLabel();
    JButton butAction = new JButton();
    JComboBox pickVoyage = new JComboBox();
    JComboBox pickChartItems = new JComboBox();
    BorderLayout borderLayout2 = new BorderLayout();
    GridLayout gridLayout1 = new GridLayout();

    /** Construct the Frame */
    public swingdemo() {
        super();
        try {
            jbInit();
            setupGlassPane();
            setDefaultCloseOperation(3);
            setupChart();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *  The main program for the swingdemo class
     *
     * @param  args  The command line arguments
     */
    public static void main(String[] args) {
        swingdemo frame = new swingdemo();
        frame.isStandalone = true;
        frame.processCommandOptions(args);
        frame.start();
    }

    /**
     * Component initialization
     *
     * @exception  Exception  Description of the Exception
     */
    private void jbInit() throws Exception {
        this.setTitle("JFreeChart Demo");
        this.setSize(new Dimension(666, 539));
        butAction.setToolTipText("Perform Action");
        butAction.setText("Action");
        butAction.addActionListener(
            new java.awt.event.ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    butAction_actionPerformed();
                }
            });
        labelCurrentVoyage.setHorizontalAlignment(SwingConstants.TRAILING);
        labelCurrentVoyage.setText("Current Voyage : ");
        panelHeader.setLayout(gridLayout1);
        textComments.setToolTipText("Comments from last update");
        panelGraph.setLayout(borderLayout2);
        labelChartPickText.setHorizontalAlignment(SwingConstants.RIGHT);
        labelChartPickText.setText("Add / Remove from graph : ");
        gridLayout1.setRows(2);
        this.getContentPane().add(panelHeader, BorderLayout.NORTH);
        panelHeader.add(labelCurrentVoyage, null);
        panelHeader.add(pickVoyage, null);
        panelHeader.add(textComments, null);
        panelHeader.add(jLabel1, null);
        panelHeader.add(labelChartPickText, null);
        panelHeader.add(pickChartItems, null);
        panelHeader.add(jLabel2, null);
        panelHeader.add(butAction, null);
        this.getContentPane().add(panelGraph, BorderLayout.CENTER);

    }

    /**  Set an create the basic chart */
    protected void setupChart() {
        chart = ChartFactory.createTimeSeriesChart("Voyage : Not Specified",
            null, "Records Per Day", null, true);
        Legend legend = chart.getLegend();
        legend.setAnchor(Legend.EAST);

        chart.setBackgroundPaint(new GradientPaint(0, 0, Color.white, 0, 1000, Color.blue));
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setBorder(BorderFactory.createCompoundBorder(
                                 BorderFactory.createEmptyBorder(4, 4, 4, 4),
                                 BorderFactory.createLineBorder(Color.darkGray, 1)));

        VerticalNumberAxis vnAxis1 = (VerticalNumberAxis) chart.getXYPlot().getRangeAxis();
        vnAxis1.setAutoRangeIncludesZero(false);

        panelGraph.add(chartPanel, BorderLayout.CENTER);
    }

    /**  Populate the contents of the JComboBox's for voyage and data code
     *   selection.
     *   Alternatively these could be populated from an SQL source as well.
     */
    protected void setupPickLists() {

        /// Populate the data
        pickVoyage.addItem("200102040");

        pickChartItems.addItem(" ");
        for (int i = 0; i < PARAMETERS.length; ++i) {
            pickChartItems.addItem(PARAMETERS[i]);
        }

    }

    /**
     *  Display a status message
     *
     * @param  message  message to be displayed
     */
    protected void statusMessage(String message) {
        System.out.println("JFreeChart JDBC Swing demo : " + message);
    }

    /**
     *  A seperate thread which is used to query the database and update the
     *  chart data.
     *  This allows the GUI components to respond (actually just repaint) while a query
     *  is being executed.
     *
     */
    protected class RefreshChartDataThread extends Thread {

        /**  Main processing method for the RefreshChartDataThread object */
        public void run() {
            String query = null;
            String voyage = null;
            String code = null;
            int i = 0;

            if (conn == null) {
                return;
            }

            setBusy(true);

            code = pickChartItems.getSelectedItem().toString();

            if (chartParameters.contains(code)) {
                chartParameters.removeElement(code);
            }
            else
            if (!code.equals(" ")) {
                chartParameters.addElement(code);
            }
            pickChartItems.setSelectedIndex(0);

            voyage = pickVoyage.getSelectedItem().toString();
            query = "select a.timestamp, a.record_count as Track ";

            for (i = 0; i < chartParameters.size(); ++i) {
                query += "," + ((char) (alphaStart + i)) + ".record_count as " + chartParameters.get(i).toString();
            }

            query += " from " + currentSchema + "summary_track a ";
            for (i = 0; i < chartParameters.size(); ++i) {
                query += ", " + currentSchema + "summary_data " + ((char) (alphaStart + i));
            }

            query += " where a.set_code = " + voyage
                  + " and a.timestamp < (select max(timestamp) from "
                  + currentSchema + "summary_track where set_code = " + voyage + ")"
                  + " and a.timestamp > (select min(timestamp) from "
                  + currentSchema + "summary_track where set_code = " + voyage + ")";

            for (i = 0; i < chartParameters.size(); ++i) {
                query += " and a.set_code  = " + ((char) (alphaStart + i)) + ".set_code  "
                      + " and a.timestamp = " + ((char) (alphaStart + i)) + ".timestamp "
                      + " and " + ((char) (alphaStart + i)) + ".obs_code = '" + chartParameters.get(i).toString() + "'";
            }

            System.out.println(query);
            try {
                chartData.executeQuery(query);
                chart.getPlot().setDataset(chartData);
                ArrayList<AbstractTitle> titles = new ArrayList<AbstractTitle>();
                TextTitle subtitle = new TextTitle("Voyage " + voyage, new Font("SansSerif", Font.BOLD, 12));
                titles.add(subtitle);
                chart.setTitles(titles);

                XYPlot chartPlot = chart.getXYPlot();
                Axis chartAxis = chartPlot.getDomainAxis();
                chartAxis.configure();
                chartAxis = chartPlot.getRangeAxis();
                chartAxis.configure();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            setBusy(false);
        }
    }

    /**
     *  Process action button inputs
     */
    void butAction_actionPerformed() {
        setBusy(true);
        if (workingThread != null) {
            if (workingThread.isAlive()) {
                return;
            }
        }

        workingThread = null;
        statusMessage("Starting graphic refresh");
        workingThread = new RefreshChartDataThread();

        if (workingThread != null) {
            workingThread.start();
        }
    }


    /**
     * Start running the frame.  Basically does the following
     * 1. set the size of the frame and display it.
     * 2. Attempt to establish database connection
     */
    public void start() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameSize = this.getSize();

        if (frameSize.height > screenSize.height) {
            frameSize.height = screenSize.height;
        }
        if (frameSize.width > screenSize.width) {
            frameSize.width = screenSize.width;
        }

        this.setSize(frameSize);
        this.setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);
        this.setVisible(true);

        setBusy(true);

        if (conn == null) {
            getDBMSConnection();
        }

        if (conn != null) {
            setupPickLists();
            chartData = new JdbcXYDataset(conn);
            setBusy(false);
        }
        else {
            if (isStandalone) {
                System.exit( 0 );
            }
            else {
                this.setVisible(false);
                this.dispose();
            }
        }
    }


    /**
     * Set up the glasspane to disallow input and display the busy cursor.
     *
     * @see    setBusy
     */
    private void setupGlassPane() {
        Component glassPane;
        glassPane = this.getGlassPane();
        glassPane.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        glassPane.addMouseListener(
            new java.awt.event.MouseListener() {
                public void mouseExited(MouseEvent e) {
                }

                public void mouseEntered(MouseEvent e) {
                }

                public void mouseReleased(MouseEvent e) {
                }

                public void mousePressed(MouseEvent e) {
                }

                public void mouseClicked(MouseEvent e) {
                }
            }
        );
    }

    /**
     * Set the glasspane as active or not.
     *
     * @param  busy  whether to enable or disable the glasspane
     */
    public void setBusy(boolean busy) {
        this.getGlassPane().setVisible(busy);
    }

    /** get the connection to the database */
    protected void getDBMSConnection() {
        Connection connTemp = conn;
        conn = null;

        dbConnection connData = new dbConnection(false);

        connData.setDriver(currentDriver);
        connData.setUserName(currentUser);
        connData.setURL(currentURL);
        connData.setPassword(currentPassword);
        connData.setSchema(currentSchema);

        conn = connData.showDialog();

        /// get the extra parameters needed
        if (conn != null) {
            currentSchema = connData.getSchema();
            currentURL = connData.getURL();
            currentUser = connData.getUserName();
            currentPassword = connData.getPassword();
        }
        else {
            conn = connTemp;
        }

        /// Reset connData to null to allow to be garbage collected
        connData = null;

        /// Process extra parameters needed
    }

    /**
     *  Process command line args
     *
     * @param  args  command line args
     */
    public void processCommandOptions(String args[]) {
        for (int i = 0; i < args.length; i++) {
            if (args[i].charAt(0) == '-') {
                if (args[i].length() < 2) {
                    args[i] = "-?";
                }
                int option = args[i].charAt(1);
                switch (option) {
                    case 'U':
                        currentUser = args[i].substring(2);
                        break;
                    case 'P':
                        currentPassword = args[i].substring(2);
                        break;
                    case 'C':
                        currentURL = args[i].substring(2);
                        break;
                    case 'D':
                        currentDriver = args[i].substring(2);
                        break;
                    case 'T':
                        currentSchema = args[i].substring(2);
                        if ((currentSchema != null) && (currentSchema.length() > 0)) {
                            if (!currentSchema.endsWith(".")) {
                                 currentSchema += ".";
                            }
                        }
                        break;
                    case '?':
                    default:
                        System.out.println("Usage: ");
                        System.out.println("java " + this.getClass().toString()
                             + " [-Ddriver] [-Cconnect] [-Tschema] [-Uuser] [-Ppassword]");
                        return;
                }
            }
        }

    }

}
