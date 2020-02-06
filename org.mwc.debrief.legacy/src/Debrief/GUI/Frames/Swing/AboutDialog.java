/*******************************************************************************
 * Debrief - the Open Source Maritime Analysis Application
 * http://debrief.info
 *  
 * (C) 2000-2020, Deep Blue C Technology Ltd
 *  
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html)
 *  
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 *******************************************************************************/


package Debrief.GUI.Frames.Swing;

import java.awt.*;

import javax.swing.BorderFactory;

/** Class to show About... dialog
 *
 * @author  Ian.Mayo
 * @version 
 */
public final class AboutDialog extends javax.swing.JDialog
{
  //////////////////////////////////////////////////
  // member variables
  //////////////////////////////////////////////////

  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private javax.swing.JTextArea _mainMessage;
  private javax.swing.JLabel _titleLbl;
  private javax.swing.JPanel jPanel1;
  private javax.swing.JButton _okBtn;

  //////////////////////////////////////////////////
  // constructor
  //////////////////////////////////////////////////

  /** Creates new form AboutDialog
   *
   * @param title the text to show in the title bar of the dialog
   * @param message the message to show
   */
  private AboutDialog(final String title,
                      final String message)
  {

    this.setModal(true);
    this.setResizable(false);

    initComponents();

    _titleLbl.setText(title);
    _mainMessage.setText(message);
    pack();
    // and locate the panel
    final Dimension area = Toolkit.getDefaultToolkit().getScreenSize();
    final Dimension mySize = this.getSize();
    final Point centre = new Point(area.width / 2 - mySize.width / 2, area.height / 2 - mySize.height / 2);
    this.setLocation(centre);
  }

  //////////////////////////////////////////////////
  // member methods
  //////////////////////////////////////////////////

  /** This method is called from within the constructor to
   * initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is
   * always regenerated by the FormEditor.
   */
  private void initComponents()
  {//GEN-BEGIN:initComponents
    _mainMessage = new javax.swing.JTextArea();
    _titleLbl = new javax.swing.JLabel();
    jPanel1 = new javax.swing.JPanel();
    _okBtn = new javax.swing.JButton();
    addWindowListener(new java.awt.event.WindowAdapter()
    {
      public void windowClosing(final java.awt.event.WindowEvent evt)
      {
        closeDialog(evt);
      }
    }
    );

    _mainMessage.setWrapStyleWord(true);
    _mainMessage.setPreferredSize(new java.awt.Dimension(200, 100));
    _mainMessage.setLineWrap(true);
    _mainMessage.setEditable(false);
    _mainMessage.setText("text waiting");
    _mainMessage.setBackground(new java.awt.Color(204, 204, 204));
    _mainMessage.setMargin(new java.awt.Insets(5, 5, 5, 5));


    getContentPane().add(_mainMessage, java.awt.BorderLayout.CENTER);

    _titleLbl.setText("label waiting");
    _titleLbl.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);


    getContentPane().add(_titleLbl, java.awt.BorderLayout.NORTH);


    _okBtn.setPreferredSize(new java.awt.Dimension(73, 27));
    _okBtn.setText("OK");
    _okBtn.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(final java.awt.event.ActionEvent evt)
      {
        okPressed(evt);
      }
    }
    );

    jPanel1.add(_okBtn);

    _mainMessage.setBorder(BorderFactory.createEtchedBorder());
    _titleLbl.setBorder(BorderFactory.createEtchedBorder());
    jPanel1.setBorder(BorderFactory.createEtchedBorder());

    getContentPane().add(jPanel1, java.awt.BorderLayout.SOUTH);

  }//GEN-END:initComponents

  void okPressed(final java.awt.event.ActionEvent evt)
  {//GEN-FIRST:event_okPressed
// Add your handling code here:
    setVisible(false);
    dispose();
  }//GEN-LAST:event_okPressed

  /** Closes the dialog */
  void closeDialog(final java.awt.event.WindowEvent evt)
  {//GEN-FIRST:event_closeDialog
    setVisible(false);
    dispose();
  }//GEN-LAST:event_closeDialog

  /**
   * @param args the command line arguments
   */
  @SuppressWarnings("deprecation")
	public static void main(final String[] args)
  {
    new AboutDialog(
      "some title",
      "some main message" + System.getProperties().getProperty("line.separator") + " new line").show();
  }

  @SuppressWarnings("deprecation")
	public static void showIt(final java.awt.Frame parent,
                            final String title,
                            final String message)
  {
    new AboutDialog(
      title,
      message).show();
  }

}