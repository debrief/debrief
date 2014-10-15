/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package MWC.GUI.Dialogs;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;

import javax.swing.ImageIcon;


/** class to show a splash screen whilst other application is loading.  The splash
 * screen closes when instructed or when the parent frame is selected
 */
public class SplashScreen extends Window
{

  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/****************************************************
   * member fields
   ***************************************************/
  /** the image we plot
   *
   */
  final private Image _myImage;
  final private String _myTitle;
  final private Toolkit _myToolkit;
  final private Color _myColor;

  private int imgWidth;
  private int imgHeight;

  private final int _myBorderWidth = 2;


  /****************************************************
   * constructor
   ***************************************************/
  /** constructor
   * image - the image to plot
   * title - title of the splash screen
   */
  public SplashScreen(final Frame parent,
                      final String imageName,
                      final String title,
                      final Color titleColor)
  {
    super(parent);

    // store the parameters
    _myTitle = title;
    _myToolkit = Toolkit.getDefaultToolkit();
    _myColor = titleColor;

    // load the image
    _myImage = loadSplashImage(imageName);

    // show the image
    showSplashScreen();

    //  listen out for splash getting clicked (to close)
    this.addMouseListener(new MouseAdapter()
    {
      /**
       * Invoked when the mouse has been clicked on a component.
       */
      public void mouseClicked(final MouseEvent e)
      {
        setVisible(false);
        dispose();
      }
    });

  }

  /****************************************************
   * member methods
   ***************************************************/
  private Image loadSplashImage(final String imageName)
  {
    Image image = null;
    final URL theURL = getClass().getClassLoader().getResource(imageName);
    if(theURL != null)
    {
      final ImageIcon io = new ImageIcon(theURL);
      image = io.getImage();
      imgWidth = image.getWidth(this);
      imgHeight = image.getHeight(this);
    }
    return image;
  }

  private void showSplashScreen()
  {
    final Dimension screenSize = _myToolkit.getScreenSize();
    final int w = imgWidth + _myBorderWidth * 2;
    final int h = imgHeight + _myBorderWidth * 2;
    final int x = (screenSize.width - w) / 2;
    final int y = (screenSize.height - h) / 2;
    setBounds(x, y, w, h);
    setVisible(true);
  }

  /**
   * Paints the container. This forwards the paint to any lightweight
   * components that are children of this container. If this method is
   * reimplemented, super.paint(g) should be called so that lightweight
   * components are properly rendered. If a child component is entirely
   * clipped by the current clipping setting in g, paint() will not be
   * forwarded to that child.
   *
   * @param g the specified Graphics window
   * @see   java.awt.Component#update(java.awt.Graphics)
   */
  public void paint(final Graphics g)
  {
    g.drawImage(_myImage, _myBorderWidth, _myBorderWidth, imgWidth, imgHeight, this);

    // work out where to put the text
    final Font newF = new Font("SansSerif", Font.BOLD, 15);
    final Font oldF = g.getFont();
    g.setFont(newF);
    g.setColor(_myColor);
    final FontMetrics fm = g.getFontMetrics();
    final int wid = fm.stringWidth(this._myTitle);
    g.drawString(_myTitle, (imgWidth - wid), (imgHeight - 2));

    g.setFont(oldF);
  }

}
