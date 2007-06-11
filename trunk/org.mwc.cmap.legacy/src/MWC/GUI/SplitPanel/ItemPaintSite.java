 /*
 * Copyright (c) 1997 Borland International, Inc. All Rights Reserved.
 * 
 * This SOURCE CODE FILE, which has been provided by Borland as part
 * of a Borland product for use ONLY by licensed users of the product,
 * includes CONFIDENTIAL and PROPRIETARY information of Borland.  
 *
 * USE OF THIS SOFTWARE IS GOVERNED BY THE TERMS AND CONDITIONS 
 * OF THE LICENSE STATEMENT AND LIMITED WARRANTY FURNISHED WITH
 * THE PRODUCT.
 *
 * IN PARTICULAR, YOU WILL INDEMNIFY AND HOLD BORLAND, ITS RELATED
 * COMPANIES AND ITS SUPPLIERS, HARMLESS FROM AND AGAINST ANY CLAIMS
 * OR LIABILITIES ARISING OUT OF THE USE, REPRODUCTION, OR DISTRIBUTION
 * OF YOUR PROGRAMS, INCLUDING ANY CLAIMS OR LIABILITIES ARISING OUT OF
 * OR RESULTING FROM THE USE, MODIFICATION, OR DISTRIBUTION OF PROGRAMS
 * OR FILES CREATED FROM, BASED ON, AND/OR DERIVED FROM THIS SOURCE
 * CODE FILE.
 * 
 */

//------------------------------------------------------------------------------
// Copyright (c) 1996, 1996 Borland International, Inc. All Rights Reserved.
//------------------------------------------------------------------------------

package MWC.GUI.SplitPanel;


import java.awt.*;

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
