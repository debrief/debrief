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
package org.mwc.debrief.lite.gui.custom;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;

@SuppressWarnings("serial")
public class ResizablePanel extends JPanel {

    private boolean drag = false;
    private Point dragLocation  = new Point();

    public  ResizablePanel() {
        setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
        setPreferredSize(new Dimension(500, 500));
        final JFrame f = new JFrame("Test");
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                drag = true;
                dragLocation = e.getPoint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                drag = false;
            }
        });
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (drag) {
                    if (dragLocation.getX()> getWidth()-10 && dragLocation.getY()>getHeight()-10) {
                        System.err.println("in");
                        setSize((int)(getWidth()+(e.getPoint().getX()-dragLocation.getX())),
                                (int)(getHeight()+(e.getPoint().getY()-dragLocation.getY())));
                        dragLocation = e.getPoint();
                    }
                }
            }
        });
        f.getContentPane().setLayout(new BorderLayout());
        f.getContentPane().add(this,BorderLayout.CENTER);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.pack();
        f.setVisible(true);
    }

    public static void main(String[] args) {
        new ResizablePanel();
    }

    public void paintComponent(Graphics g) {
        g.setColor(Color.red);
        g.fillRect(0, 0, getWidth(), getHeight());
    }

}