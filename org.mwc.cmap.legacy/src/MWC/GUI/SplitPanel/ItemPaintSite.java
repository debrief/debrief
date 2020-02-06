 

//------------------------------------------------------------------------------
// Copyright (c) 1996, 1996 Borland International, Inc. All Rights Reserved.
//------------------------------------------------------------------------------

package MWC.GUI.SplitPanel;


import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;

/**
 * This interface is implemented where ItemPainters can be provided with more
 * information about their host containers fonts, margins, colors, etc.
 */
public interface ItemPaintSite
{
  /**
   * Returns the background color for the item being painted.
   */
  public Color getBackground();

  /**
   * Returns the foreground color for the item being painted.
   */
  public Color getForeground();

  /**
   * Returns the font to use for the item being painted.
   */
  public Font getFont();

  /**
   * Returns the alignment setting for the item being painted.
   * @see borland.util.Alignment for alignment settings.
   */
  public int getAlignment();

  /**
   * Returns the item margins for the item being painted.
   */
  public Insets getItemMargins();
}
