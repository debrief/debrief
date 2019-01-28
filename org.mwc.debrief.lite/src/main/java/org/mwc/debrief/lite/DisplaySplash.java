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

/**
 * @author Ayesha <ayesha.ma@gmail.com>
 *
 */
import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.SplashScreen;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JProgressBar;
import javax.swing.Timer;

import org.mwc.debrief.lite.util.Utils;

public class DisplaySplash extends JFrame implements ActionListener,Runnable {
  private SplashScreen splash;
  private int numTasks;
  private static String debriefVersion;

  static {
    debriefVersion = Utils.readManifestVersion();
  }
  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  static void renderVersion(Graphics2D g) {
    g.setComposite(AlphaComposite.Clear);
    g.fillRect(120,140,600,150);
    g.setPaintMode();
    g.setColor(Color.BLACK);
    if(debriefVersion==null) {
      debriefVersion="Dev Mode";
    }
    g.drawString(debriefVersion, 330, 170);
  }
  
  static void renderSplashFrame(Graphics2D g, String message) {
    g.setComposite(AlphaComposite.Clear);
    g.fillRect(0,140,300,80);
    g.setPaintMode();
    g.setColor(Color.RED);
    g.drawString(message, 20, 170);
  }
  public DisplaySplash(int numTasks) {
    super();
    this.numTasks=numTasks;

    splash = SplashScreen.getSplashScreen();
    if (splash == null) {
      System.out.println("SplashScreen.getSplashScreen() returned null");
      return;
    }
    if(splash.isVisible()) {
      Graphics2D g = splash.createGraphics();
      if (g == null) {
        System.out.println("g is null");
        return;
      }
      System.out.println("version updated:"+debriefVersion);
      renderVersion(splash.createGraphics());
    }
    

  }

  public void updateMessage(String message) {
    numTasks --;
    if(splash.isVisible()) {
      Graphics2D g = splash.createGraphics();
      if (g == null) {
        System.out.println("g is null");
        return;
      }
      System.out.println("MEssage updated:"+message);
      renderSplashFrame(g, message);
    }
  }
  public void actionPerformed(ActionEvent ae) {
    System.exit(0);
  }

  private void closeSplash() {
    try {
      if(splash.isVisible()) {
        splash.close();
      }
    }catch(Exception e) {
      //ignore
    }
    toFront();
  }
  @Override
  public void run()
  {
    String[] tasks = {"Loading map content","Initializing Debrief Lite","Creating map pane","Initializing the screen","Done.."};
    Graphics2D g = splash.createGraphics();
    if (g == null) {
      System.out.println("g is null");
      return;
    }

    for(int i=0;i<tasks.length;i++) {
      try {
        renderSplashFrame(g,tasks[i]);
        splash.update();
        Thread.sleep(900);
        
      }
      catch(InterruptedException e) {
      }
    }

  }

}