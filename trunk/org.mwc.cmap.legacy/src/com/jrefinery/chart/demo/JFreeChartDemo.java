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
 * -------------------
 * JFreeChartDemo.java
 * -------------------
 * (C) Copyright 2000-2002, by Simba Management Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   Andrzej Porebski;
 *                   Matthew Wright;
 *                   Serge V. Grachov;
 *                   Bill Kelemen;
 *                   Achilleus Mantzios;
 *                   Bryan Scott;
 *
 * $Id: JFreeChartDemo.java,v 1.1.1.1 2003/07/17 10:06:33 Ian.Mayo Exp $
 *
 * Changes (from 22-Jun-2001)
 * --------------------------
 * 22-Jun-2001 : Modified to use new title code (DG);
 * 23-Jun-2001 : Added null data source chart (DG);
 * 24-Aug-2001 : Fixed DOS encoding problem (DG);
 * 15-Oct-2001 : Data source classes moved to com.jrefinery.data.* (DG);
 * 19-Oct-2001 : Implemented new ChartFactory class (DG);
 * 22-Oct-2001 : Added panes for stacked bar charts and a scatter plot (DG);
 *               Renamed DataSource.java --> Dataset.java etc. (DG);
 * 31-Oct-2001 : Added some negative values to the sample CategoryDataset (DG);
 *               Added 3D-effect bar plots by Serge V. Grachov (DG);
 * 07-Nov-2001 : Separated the JCommon Class Library classes, JFreeChart now
 *               requires jcommon.jar (DG);
 *               New flag in ChartFactory to control whether or not a legend is
 *               added to the chart (DG);
 * 15-Nov-2001 : Changed TimeSeriesDataset to TimeSeriesCollection (DG);
 * 17-Nov-2001 : For pie chart, changed dataset from CategoryDataset to PieDataset (DG);
 * 26-Nov-2001 : Moved property editing, saving and printing to the JFreeChartPanel class (DG);
 * 05-Dec-2001 : Added combined charts contributed by Bill Kelemen (DG);
 * 10-Dec-2001 : Updated exchange rate demo data, and included a demo chart that shows multiple
 *               time series together on one chart.  Removed some redundant code (DG);
 * 12-Dec-2001 : Added Candlestick chart (DG);
 * 23-Jan-2002 : Added a test chart for single series bar charts (DG);
 * 06-Feb-2002 : Added sample wind plot (DG);
 * 15-Mar-2002 : Now using ResourceBundle to fetch strings and other items displayed to the
 *               user.  This will allow for localisation (DG);
 * 09-Apr-2002 : Changed horizontal bar chart to use integer tick units (DG);
 * 19-Apr-2002 : Renamed JRefineryUtilities-->RefineryUtilities (DG);
 * 11-Jun-2002 : Changed createHorizontalStackedBarChart()
 *               --> createStackedHorizontalBarChart() for consistency (DG);
 * 25-Jun-2002 : Removed redundant code (DG);
 * 02-Jul-2002 : Added Gantt chart demo, based on GanttDemo (BRS)
 * 02-Jul-2002 : Added support for usage (null, All, Swing) in resource (BRS).
 * 27-Jul-2002 : Made Resourceclass string public (BRS).
 * 27-Jul-2002 : Move chart creation methods to JFreeChartDemoBase class to allow sharing
 *               with servlet.  Servlet cannot share this class as a number of headless
 *               exceptions are generated. (BRS).
 * 10-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 *
 */

package com.jrefinery.chart.demo;

import java.awt.Dimension;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.event.WindowAdapter;
import java.util.ResourceBundle;
import java.util.Vector;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JScrollPane;
import javax.swing.JOptionPane;
import javax.swing.BorderFactory;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JTextArea;
import javax.swing.JButton;
import com.jrefinery.chart.JFreeChart;
import com.jrefinery.chart.ChartFrame;
import com.jrefinery.chart.ChartPanel;
import com.jrefinery.layout.LCBLayout;
import com.jrefinery.ui.RefineryUtilities;
import com.jrefinery.ui.about.AboutFrame;

/**
 * The main frame in the chart demonstration application.
 *
 * @author DG
 */
public class JFreeChartDemo extends JFrame implements ActionListener, WindowListener {

    /** Exit action command. */
    public static final String EXIT_COMMAND = "EXIT";

    /** About action command. */
    public static final String ABOUT_COMMAND = "ABOUT";

    /** The base demo class. */
    private static final JFreeChartDemoBase DEMO = new JFreeChartDemoBase();

    /** An array of chart commands. */
    private static final String[][] CHART_COMMANDS = DEMO.CHART_COMMANDS;

    /** Localised resources. */
    private ResourceBundle resources;

    /** Chart frames. */
    private ChartFrame[] frame = new ChartFrame[CHART_COMMANDS.length];

    /** Panels. */
    private JPanel[] panels = null;

    /** The preferred size for the frame. */
    public static final Dimension PREFERRED_SIZE = new Dimension(780, 400);

    /** A frame for displaying information about the application. */
    private AboutFrame aboutFrame;

    /** A tabbed pane for displaying sample charts; */
    private JTabbedPane tabbedPane;

    /**
     * Constructs a demonstration application for the JFreeChart Class Library.
     */
    public JFreeChartDemo() {
        super(JFreeChart.INFO.getName() + " " + JFreeChart.INFO.getVersion() + " Demo");
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                dispose();
                System.exit(0);
            }
        });
        resources = DEMO.getResources();
        // set up the menu
        JMenuBar menuBar = createMenuBar(resources);
        setJMenuBar(menuBar);

        JPanel content = new JPanel(new BorderLayout());
        content.add(createTabbedPane(resources));
        setContentPane(content);

    }

    /**
     * Returns the preferred size for the frame.
     *
     * @return the preferred size.
     */
    public Dimension getPreferredSize() {
        return PREFERRED_SIZE;
    }

    /**
     * Handles menu selections by passing control to an appropriate method.
     *
     * @param event  the event.
     */
    public void actionPerformed(ActionEvent event) {

        String command = event.getActionCommand();
        if (command.equals(EXIT_COMMAND)) {
            attemptExit();
        }
        else if (command.equals(ABOUT_COMMAND)) {
            about();
        }
        else {
            /// Loop through available commands to find index to current command.
            int chartnum = -1;
            int i = CHART_COMMANDS.length;
            while (i > 0) {
                --i;
                if (command.equals(CHART_COMMANDS[i][0])) {
                    chartnum = i;
                    i = 0;
                }
            }

            /// check our index is valid
            if ((chartnum >= 0) && (chartnum < frame.length)) {
                /// Check we have not already created chart.
                if (frame[chartnum] == null) {
                    // setup the chart.
                    DEMO.getChart(chartnum);

                    // present it in a frame...
                    String str = resources.getString(CHART_COMMANDS[chartnum][2] + ".title");
                    frame[chartnum] = new ChartFrame(str, DEMO.getChart(chartnum));
                    frame[chartnum].pack();
                    RefineryUtilities.positionFrameRandomly(frame[chartnum]);

                    /// Set panel to zoomable if required
                    try {
                        str = resources.getString(CHART_COMMANDS[chartnum][2] + ".zoom");
                        if ((str != null) && (str.toLowerCase().equals("true"))) {
                            ChartPanel panel =  frame[chartnum].getChartPanel();
                            panel.setMouseZoomable(true);
                            panel.setHorizontalAxisTrace(true);
                            panel.setVerticalAxisTrace(true);
                        }
                    }
                    catch (Exception ex) {
                        /// Filter out messages which for charts which do not have zoom
                        /// specified.
                        if (ex.getMessage().indexOf("MissingResourceException") == 0) {
                            ex.printStackTrace();
                        }
                    }

                    frame[chartnum].show();

                }
                else {
                    frame[chartnum].show();
                    frame[chartnum].requestFocus();
                }
            }
        }
    }

    /**
     * Exits the application, but only if the user agrees.
     */
    private void attemptExit() {

        String title = resources.getString("dialog.exit.title");
        String message = resources.getString("dialog.exit.message");
        int result = JOptionPane.showConfirmDialog(this, message, title,
                                                   JOptionPane.YES_NO_OPTION,
                                                   JOptionPane.QUESTION_MESSAGE);
        if (result == JOptionPane.YES_OPTION) {
            dispose();
            System.exit(0);
        }
    }

    /**
     * Displays information about the application.
     */
    private void about() {

        String title = this.resources.getString("about.title");
        String versionLabel = this.resources.getString("about.version.label");
        if (aboutFrame == null) {
            aboutFrame = new AboutFrame(title, JFreeChart.INFO);
            aboutFrame.pack();
            RefineryUtilities.centerFrameOnScreen(aboutFrame);
        }
        aboutFrame.show();
        aboutFrame.requestFocus();

    }

    /**
     * The starting point for the demonstration application.
     *
     * @param args  ignored.
     */
    public static void main(String[] args) {

        JFreeChartDemo f = new JFreeChartDemo();
        f.pack();
        RefineryUtilities.centerFrameOnScreen(f);
        f.setVisible(true);
    }

    /**
     * Required for WindowListener interface, but not used by this class.
     *
     * @param e  the event.
     */
    public void windowActivated(WindowEvent e) {
    }

    /**
     * Clears the reference to the print preview frames when they are closed.
     *
     * @param e  the event.
     */
    public void windowClosed(WindowEvent e) {

        if (e.getWindow() == this.aboutFrame) {
            aboutFrame = null;
        }

    }

    /**
     * Required for WindowListener interface, but not used by this class.
     *
     * @param e  the event.
     */
    public void windowClosing(WindowEvent e) {
    }

    /**
     * Required for WindowListener interface, but not used by this class.
     *
     * @param e  the event.
     */
    public void windowDeactivated(WindowEvent e) {
    }

    /**
     * Required for WindowListener interface, but not used by this class.
     *
     * @param e  the event.
     */
    public void windowDeiconified(WindowEvent e) {
    }

    /**
     * Required for WindowListener interface, but not used by this class.
     *
     * @param e  the event.
     */
    public void windowIconified(WindowEvent e) {
    }

    /**
     * Required for WindowListener interface, but not used by this class.
     *
     * @param e  the event.
     */
    public void windowOpened(WindowEvent e) {
    }

    /**
     * Creates a menubar.
     *
     * @param resources  localised resources.
     *
     * @return the menu bar.
     */
    private JMenuBar createMenuBar(ResourceBundle resources) {

        // create the menus
        JMenuBar menuBar = new JMenuBar();

        String label;
        Character mnemonic;

        // first the file menu
        label = resources.getString("menu.file");
        mnemonic = (Character) resources.getObject("menu.file.mnemonic");
        JMenu fileMenu = new JMenu(label, true);
        fileMenu.setMnemonic(mnemonic.charValue());

        label = resources.getString("menu.file.exit");
        mnemonic = (Character) resources.getObject("menu.file.exit.mnemonic");
        JMenuItem exitItem = new JMenuItem(label, mnemonic.charValue());
        exitItem.setActionCommand(EXIT_COMMAND);
        exitItem.addActionListener(this);
        fileMenu.add(exitItem);

        // then the help menu
        label = resources.getString("menu.help");
        mnemonic = (Character) resources.getObject("menu.help.mnemonic");
        JMenu helpMenu = new JMenu(label);
        helpMenu.setMnemonic(mnemonic.charValue());

        label = resources.getString("menu.help.about");
        mnemonic = (Character) resources.getObject("menu.help.about.mnemonic");
        JMenuItem aboutItem = new JMenuItem(label, mnemonic.charValue());
        aboutItem.setActionCommand(ABOUT_COMMAND);
        aboutItem.addActionListener(this);
        helpMenu.add(aboutItem);

        // finally, glue together the menu and return it
        menuBar.add(fileMenu);
        menuBar.add(helpMenu);

        return menuBar;

    }

    /**
     * Creates a tabbed pane containing descriptions of the demo charts.
     *
     * @param resources  localised resources.
     *
     * @return a tabbed pane.
     */
    private JTabbedPane createTabbedPane(ResourceBundle resources) {

        Font font = new Font("Dialog", Font.PLAIN, 12);
        JTabbedPane tabs = new JTabbedPane();

        int tab = 1;
        Vector titles = new Vector(0);
        String[] tabTitles;
        String title = null;

        while (tab > 0) {
            try {
                title = resources.getString("tabs." + tab);
                if (title != null) {
                    titles.add(title);
                }
                else {
                    tab = -1;
                }
                ++tab;
            }
            catch (Exception ex) {
                tab = -1;
            }
        }

        if (titles.size() == 0) {
            titles.add("Default");
        }

        tab = titles.size();
        panels = new JPanel[tab];
        tabTitles = new String[tab];

        --tab;
        for (; tab >= 0; --tab) {
            title = titles.get(tab).toString();
            tabTitles[tab] = title;
        }
        titles.removeAllElements();

        for (int i = 0; i < tabTitles.length; ++i) {
            panels[i] = new JPanel();
            panels[i].setLayout(new LCBLayout(20));
            panels[i].setPreferredSize(new Dimension(360, 20));
            panels[i].setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
            tabs.add(tabTitles[i], new JScrollPane(panels[i]));
        }

        String description;
        String buttonText = resources.getString("charts.display");
        JButton b1;

        // Load the CHARTS ...
        String usage = null;
        for (int i = 0; i <= CHART_COMMANDS.length - 1; ++i) {
            try {
                usage = resources.getString(CHART_COMMANDS[i][2] + ".usage");
            }
            catch (Exception ex) {
                usage = null;
            }

            if ((usage == null) || usage.equalsIgnoreCase("All")
                                || usage.equalsIgnoreCase("Swing")) {

                title = resources.getString(CHART_COMMANDS[i][2] + ".title");
                description = resources.getString(CHART_COMMANDS[i][2] + ".description");
                try {
                    tab = Integer.parseInt(resources.getString(CHART_COMMANDS[i][2] + ".tab"));
                    --tab;
                }
                catch (Exception ex) {
                    System.err.println("Demo : Error retrieving tab identifier for chart "
                                       + CHART_COMMANDS[i][2]);
                    System.err.println("Demo : Error = " + ex.getMessage());
                    tab = 0;
                }
                if ((tab < 0) || (tab >= panels.length)) {
                    tab = 0;
                }

                System.out.println("Demo : adding " + CHART_COMMANDS[i][0] + " to panel " + tab);
                panels[tab].add(RefineryUtilities.createJLabel(title, font));
                panels[tab].add(new DescriptionPanel(new JTextArea(description)));
                b1 = RefineryUtilities.createJButton(buttonText, font);
                b1.setActionCommand(CHART_COMMANDS[i][0]);
                b1.addActionListener(this);
                panels[tab].add(b1);
            }
        }

        return tabs;

    }

}
