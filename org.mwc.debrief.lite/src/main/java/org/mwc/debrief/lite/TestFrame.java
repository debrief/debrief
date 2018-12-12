/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2018, Deep Blue C Technology Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 */
package org.mwc.debrief.lite;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class TestFrame extends JFrame
{

  /**
   *
   */
  private static final long serialVersionUID = 4843482661897806344L;

  /**
   * Launch the application.
   */
  public static void main(final String[] args)
  {
    EventQueue.invokeLater(new Runnable()
    {
      @Override
      public void run()
      {
        try
        {
          final TestFrame frame = new TestFrame();
          frame.setVisible(true);

          frame.displayHello();
        }
        catch (final Exception e)
        {
          e.printStackTrace();
        }
      }
    });
  }

  private final JPanel contentPane;

  /**
   * Create the frame.
   */
  public TestFrame()
  {
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setBounds(100, 100, 450, 300);
    contentPane = new JPanel();
    contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
    contentPane.setLayout(new BorderLayout(0, 0));
    setContentPane(contentPane);

  }

  /**
   * displays hello world
   */
  private void displayHello()
  {
    JOptionPane.showMessageDialog(this, "Hello World", "Hello",
        JOptionPane.DEFAULT_OPTION);
  }

}
