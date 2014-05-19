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


import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;

/**
 * The ItemPainter interface defines a single item painter for non-edit painting.
 */
public interface ItemPainter
{
  /**
   * This is the default state.
   */
  public static final int DEFAULT = 0x0000;

  /**
   * Set if this item is disabled.
   */
  public static final int DISABLED = 0x0001;

  /**
   * Set if this item has input focus.
   */
  public static final int FOCUSED = 0x0002;

  /**
   * Set if this item is selected or checked.
   */
  public static final int SELECTED = 0x0004;

  /**
   * Set if this item has an unknown selected state (overrides selected).
   */
  public static final int INDETERMINATE = 0x0008;

  /**
   * Set if this item contents are open (otherwise closed).
   */
  public static final int OPENED = 0x0010;

  /**
   * Set if this item's owning window is inactive / not focused.
   */
  public static final int INACTIVE = 0x0020;

  /**
   * Returns the preferred size of the ItemPainter.
   * @param data The data object to use for size calculation.
   * @param graphics The Graphics object to use for size calculation.
   * @param state The current state of the object.
   * @param site The ItemPaintSite with information about fonts, margins, etc.
   * @return The calculated Dimension object representing the preferred size of this ItemPainter.
   */
  public Dimension getPreferredSize(Object data, Graphics graphics, int state, ItemPaintSite site);

  /**
   * Paints the data Object within the Rectangle bounds, using passed Graphics
   * and state information.
   * @param data The data object to paint.
   * @param graphics The Graphics object to paint to.
   * @param bounds The Rectangle extents to paint in.
   * @param state The current state information for the data object.
   * @param site The ItemPaintSite with information about fonts, margins, etc.
   */
  public void paint(Object data, Graphics graphics, Rectangle bounds, int state, ItemPaintSite site);
}
