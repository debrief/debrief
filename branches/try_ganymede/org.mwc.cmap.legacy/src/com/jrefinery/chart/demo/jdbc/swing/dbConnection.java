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
 * ------------------
 * dbConnection.java
 * ------------------
 * (C) Copyright 2000-2002, by Bryan Scott.
 *
 * Original Author:  Bryan Scott;
 * Contributor(s):   -;
 *
 *
 * Changes
 * -------
 * 14-Mar-2002 : Version 1 contributed by Bryan Scott (DG);
 * 25-Jun-2002 : Updated import statements (DG);
 *
 */

package com.jrefinery.chart.demo.jdbc.swing;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.JPasswordField;
import javax.swing.JOptionPane;
import javax.swing.BoxLayout;

/**
 * This class creates a dialog box with default connection parameters to connect to
 * a JDBC compliant database. Extra fields and labels can be passed in to cater for
 * other optional parameters
 */

public class dbConnection {

    static String[] ConnectOptionNames = { "Connect", "Cancel" };
    static String   ConnectTitle = "Connection Information";
    String username_  = "marine";
    String driver_    = null;
    String conn_      = null;
    String schema_    = "";

    Dimension   origin = new Dimension(0, 0);
    JPanel      connectionPanel;

    JLabel userNameLabel;
    JTextField userNameField;

    JLabel passwordLabel;
    JPasswordField passwordField;

    JLabel serverLabel;
    JComboBox serverField;

    JLabel driverLabel;
    JComboBox driverField;

    JLabel schemaLabel;
    JTextField schemaField;

    JLabel extraLabels[];
    JTextField extraFields[];

    boolean gotParameters = false;

    public dbConnection() {
        this("Connection Information", null, null, null, new String[0], new String[0], true);
    }

    public dbConnection(boolean activate) {
        this("Connection Information", null, null, null, new String[0], new String[0], activate);
    }

    public dbConnection(String Header, boolean activate) {
        this(Header, null, null, null, new String[0], new String[0], activate);
    }

    public dbConnection (String Header, String username, String url, String driver) {
        this(Header, username, url, driver, new String[0], new String[0], true);
    }

    public dbConnection (String Header, String username, String url, String driver, boolean activate) {
        this(Header, username, url, driver, new String[0], new String[0], activate);
    }

    public dbConnection(String Header, String labels[], String fields[]){
        this(Header, null, null, null, labels, fields, true);
    }

    public dbConnection (String Header, String labels[], String fields[], boolean activate )  {
        this(Header, null, null, null, labels, fields, activate);
    }

    public dbConnection (String Header, String username, String url, String driver,
                         String labels[], String fields[], boolean activate )  {
        ConnectTitle = Header;
        username_  = username;
        driver_    = driver;
        conn_      = url;

        extraLabels  = new JLabel[ labels.length ];
        extraFields  = new JTextField[ labels.length ];

        for ( int i = 0; i < labels.length; i++ )  {
            extraLabels[i] = new JLabel( labels[i], JLabel.RIGHT );
            extraFields[i] = new JTextField( fields[i] );
        }
        jbinit();
        if (activate)
            activateConnectionDialog();
    }

    private void jbinit() {
        /// Create the labels and text fields.
        userNameLabel = new JLabel("User name: ", JLabel.RIGHT);
        userNameField = new JTextField();

        passwordLabel = new JLabel("Password: ", JLabel.RIGHT);
        passwordField = new JPasswordField();

        serverLabel   = new JLabel("Database URL: ", JLabel.RIGHT);
        serverField   = new JComboBox();
        serverField.setEditable( true );

        serverField.addItem("jdbc:oracle:thin:@server:1521:PROD");

        driverLabel   = new JLabel("Driver: ", JLabel.RIGHT);
        driverField   = new JComboBox();
        driverField.setEditable( true );

        driverField.addItem( "oracle.jdbc.driver.OracleDriver" );
        driverField.addItem( "postgresql.Driver" );
        driverField.addItem( "com.informix.jdbc.IfxDriver" );

        schemaLabel = new JLabel("Schema: ", JLabel.RIGHT);
        schemaField = new JTextField();
    }

    /**
     * Brings up a JDialog using JOptionPane containing the connectionPanel.
     * If the user clicks on the 'Connect' button the connection is reset.
     */
    public void activateConnectionDialog() {
        int i = 0;
        boolean found = false;

        gotParameters = false;

        /// Set the defaults
        if (username_ != null)
            userNameField.setText(username_);

        if (conn_ != null) {
            found = false;
            for (i = 0; i < serverField.getItemCount(); ++i) {
                if (conn_.equals(serverField.getItemAt(i).toString())) {
                    found = true;
                    serverField.setSelectedIndex(i);
                    i = serverField.getItemCount();
                }
            }
            if (!found) {
                serverField.addItem(conn_);
                serverField.setSelectedIndex(serverField.getItemCount()-1);
            }
        }

        if (driver_ != null) {
            found = false;
            for (i = 0; i < driverField.getItemCount(); ++i) {
                if (driver_.equals(driverField.getItemAt(i).toString())) {
                    found = true;
                    driverField.setSelectedIndex(i);
                    i = driverField.getItemCount();
                }
            }
            if (!found) {
                driverField.addItem(driver_);
                driverField.setSelectedIndex(driverField.getItemCount()-1);
            }
        }

        schemaField.setText(schema_);

        connectionPanel = new JPanel(false);
        connectionPanel.setLayout( new BoxLayout(connectionPanel, BoxLayout.X_AXIS) );

         JPanel namePanel = new JPanel(false);
         namePanel.setLayout(new GridLayout(0, 1));
         namePanel.add(userNameLabel);
         namePanel.add(passwordLabel);
         namePanel.add(serverLabel);
         namePanel.add(driverLabel);
         namePanel.add(schemaLabel);

         //System.out.println( "Adding extra labels - number to add is " + extraLabels.length );

         for ( i = 0; i < extraLabels.length; i++ ) {
            namePanel.add( extraLabels[i] );
         }

         JPanel fieldPanel = new JPanel(false);
         fieldPanel.setLayout(new GridLayout(0, 1));
         fieldPanel.add(userNameField);
         fieldPanel.add(passwordField);
         fieldPanel.add(serverField);
         fieldPanel.add(driverField);
         fieldPanel.add(schemaField);

         for ( i = 0; i < extraFields.length; i++ ) {
            fieldPanel.add( extraFields[i] );
         }

         connectionPanel.add(namePanel);
         connectionPanel.add(fieldPanel);

         if (JOptionPane.showOptionDialog(
                         JOptionPane.getRootFrame(),
                         connectionPanel,
                         ConnectTitle,
                         JOptionPane.DEFAULT_OPTION,
                         JOptionPane.INFORMATION_MESSAGE,
                         null,
                         ConnectOptionNames,
                         ConnectOptionNames[0]
             ) == 0)  {
            gotParameters = true;
            username_  = userNameField.getText();
            driver_    = driverField.getSelectedItem().toString();
            conn_      = serverField.getSelectedItem().toString();
         } else {
         }
     }

    public Connection showDialog() {
      int i = 0;
      Connection conn = null;
      while ((i < 3) && (conn == null)) {
        ++i;
        activateConnectionDialog();

        if (gotParameters() != false) {
          /// load the JDBC driver and attempt to connect
          try {
            Class.forName(driverField.getSelectedItem().toString());
            conn = DriverManager.getConnection(
                serverField.getSelectedItem().toString(),
                this.userNameField.getText(),
                getPassword() );
          } catch (ClassNotFoundException nf) {
            System.err.println(" JDBC driver not found - check CLASSPATH!");
          } catch (SQLException sqle) {
            System.err.println(" Unable to connect to the specified database!");
          }
        } else {
          /// we must have pressed cancel, so exit loop
          i = 4;
        }
      }
      return conn;
    }

    public void setDefaults(String driver  , String user  , String url,
                            String password, String schema ) {
        setDriver(driver);
        setUserName(user);
        setURL(url);
        setPassword(password);
        setSchema(schema);
    }

    public String getUserName() {
        return this.userNameField.getText();
    }

    public String getPassword() {
        return new String(passwordField.getPassword());
    }

    public String getURL() {
        return serverField.getSelectedItem().toString();
    }

    public String getDriver() {
        return driverField.getSelectedItem().toString();
    }

    public String getSchema() {
        schema_ = schemaField.getText();

        if ((schema_ == null) || (schema_.length() < 1)) {
            schema_ = getUserName();
        }

        if (!schema_.endsWith(".") && (schema_.length() > 0))
            schema_ += ".";

        schemaField.setText(schema_);
        return schema_;
    }

    public void setSchema(String schema) {
      if (schema != null) {
        schemaField.setText(schema);
        getSchema();
      }
    }

    public void setDriver(String driver) {
        boolean found = false;

        if (driver == null)
            return;

        for (int i = 0; i < driverField.getItemCount(); ++i) {
            if (driverField.getItemAt(i).toString().toLowerCase().equals(driver.toLowerCase())) {
                driverField.setSelectedIndex(i);
                found = true;
                i = driverField.getItemCount();
            }
        }

        if (!found) {
            driverField.addItem(driver);
            driverField.setSelectedItem(driver);
        }

        driver_ = driver;
    }

    public void setURL(String url) {
        boolean found = false;

        if (url == null)
            return;

        for (int i = 0; i < serverField.getItemCount(); ++i) {
            if (serverField.getItemAt(i).toString().toLowerCase().equals(url.toLowerCase())) {
                serverField.setSelectedIndex(i);
                found = true;
                i = serverField.getItemCount();
            }
        }

        if (!found) {
            serverField.addItem(url);
            serverField.setSelectedItem(url);
        }

        conn_ = url;

    }

    public void setUserName(String name) {
        if (name == null)
            return;

        username_  = name;
        userNameField.setText(name);
    }

    public void setPassword(String password) {
        if (password == null)
            passwordField.setText("");
        else
            passwordField.setText(password);
    }


    public String getField( int i ) {
        return extraFields[i].getText();
    }

    public boolean gotParameters() {
        return gotParameters;
    }

    public static void main(String s[])  {
        new dbConnection();
    }
}

/*
  Life is what happens to you while your busy making other plans.

  John Lennon "Beautiful Boy" 1979
 */
