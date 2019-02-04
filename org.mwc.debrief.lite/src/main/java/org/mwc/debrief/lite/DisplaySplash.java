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
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.SplashScreen;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;

import org.mwc.debrief.lite.util.ManifestUtils;

public class DisplaySplash extends JFrame implements ActionListener, Runnable
{
  private static String debriefVersion;
  static
  {
    debriefVersion = ManifestUtils.readManifestVersion();
  }
  /**
   *
   */
  private static final long serialVersionUID = 1L;

  private static void renderSplashFrame(final Graphics2D g,
      final String message)
  {
    g.setComposite(AlphaComposite.Clear);
    g.fillRect(0, 140, 300, 80);
    g.setPaintMode();
    g.setColor(Color.RED);
    g.drawString(message, 20, 170);
  }

  private static void renderVersion(final Graphics2D g)
  {
    g.setComposite(AlphaComposite.Clear);
    g.fillRect(120, 140, 600, 150);
    g.setPaintMode();
    g.setColor(Color.BLACK);
    if (debriefVersion == null)
    {
      debriefVersion = "Dev Mode";
    }
    g.drawString(debriefVersion, 330, 170);
  }

  private final SplashScreen splash;

  @SuppressWarnings("unused")
  private int numTasks;

  public DisplaySplash(final int numTasks)
  {
    super();
    this.numTasks = numTasks;

    splash = SplashScreen.getSplashScreen();
    if (splash == null)
    {
      System.out.println("SplashScreen.getSplashScreen() returned null");
      return;
    }
    if (splash.isVisible())
    {
      final Graphics2D g = splash.createGraphics();
      if (g == null)
      {
        System.out.println("g is null");
        return;
      }
      System.out.println("version updated:" + debriefVersion);
      renderVersion(splash.createGraphics());
    }
  }

  @Override
  public void actionPerformed(final ActionEvent ae)
  {
    System.exit(0);
  }

  @SuppressWarnings("unused")
  private void closeSplash()
  {
    try
    {
      if (splash != null && splash.isVisible())
      {
        splash.close();
      }
    }
    catch (final Exception e)
    {
      // ignore - we're closing anyway
    }
    toFront();
  }

  @Override
  public void run()
  {
    final long delay = 100L;
    final String[] tasks =
    {"Loading map content", "Initializing Debrief Lite", "Creating map pane",
        "Initializing the screen", "Done.."};
    if (splash != null)
    {
      final Graphics2D g = splash.createGraphics();
      if (g == null)
      {
        System.out.println("g is null");
        return;
      }

      for (int i = 0; i < tasks.length; i++)
      {
        try
        {
          renderSplashFrame(g, tasks[i]);
          splash.update();
          Thread.sleep(delay);
        }
        catch (final InterruptedException e)
        {
        }
      }
    }
    else
    {
      showSplashError();
    }

  }

  private void showSplashError()
  {
    System.err.println(
        "Path for splash screen image should be provided in command path: -splash:src/main/java/images/splash.gif");
  }

  public void updateMessage(final String message)
  {
    numTasks--;
    if (splash != null && splash.isVisible())
    {
      final Graphics2D g = splash.createGraphics();
      if (g == null)
      {
        System.out.println("g is null");
        return;
      }
      System.out.println("Message updated:" + message);
      renderSplashFrame(g, message);
    }
    else
    {
      showSplashError();
    }
  }

}