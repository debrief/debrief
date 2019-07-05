package org.mwc.debrief.lite.gui.custom.narratives;

import java.net.URL;

import javax.swing.ImageIcon;

public class Utils
{
  public static ImageIcon getIcon(final String image)
  {
    final URL imageIcon = Utils.class.getClassLoader().getResource(image);
    ImageIcon icon = null;
    try
    {
      icon = new ImageIcon(imageIcon);
    }
    catch (final Exception e)
    {
      throw new IllegalArgumentException("Icon missing:" + image);
    }
    return icon;
  }
}
