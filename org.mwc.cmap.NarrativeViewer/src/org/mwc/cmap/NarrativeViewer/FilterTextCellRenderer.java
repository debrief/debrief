/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 */
package org.mwc.cmap.NarrativeViewer;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.StyledString.Styler;
import org.eclipse.nebula.widgets.grid.GridColumn;
import org.eclipse.nebula.widgets.grid.GridItem;
import org.eclipse.nebula.widgets.grid.internal.CheckBoxRenderer;
import org.eclipse.nebula.widgets.grid.internal.DefaultCellRenderer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.TextLayout;
import org.eclipse.swt.graphics.TextStyle;
import org.eclipse.swt.widgets.Display;

/**
 * A new cell renderer instance able to deal with styled text information
 */
public abstract class FilterTextCellRenderer extends DefaultCellRenderer
{

  static final Color MATCH_YELLOW = new Color(Display.getDefault(), 255, 251, 204);
  int leftMargin = 4;

  int rightMargin = 4;

  int topMargin = 0;

  int bottomMargin = 0;

  int textTopMargin = 1;

  int textBottomMargin = 2;

  private int insideMargin = 3;

  int treeIndent = 20;

  private CheckBoxRenderer checkRenderer;

  private TextLayout textLayout;

  private final Styler STYLER;

  /**
   * A new cell renderer instance able to deal with styled text information
   */
  public FilterTextCellRenderer()
  {
    

    STYLER = new Styler()
    {

      @Override
      public void applyStyles(TextStyle textStyle)
      {
        textStyle.background = MATCH_YELLOW;

      }
    };
  }

  
  
  protected abstract String getFilterText();

  protected StyledString getFilterStyledString(String text)
  {
    String filterText = getFilterText();
    if (filterText != null && !filterText.trim().isEmpty())
    {
      Pattern pattern = Pattern.compile(filterText, Pattern.CASE_INSENSITIVE);
      Matcher matcher = pattern.matcher(text);
      boolean found = false;
      StyledString string = new StyledString();

      int lastindex = 0;
      while (matcher.find())
      {
        found = true;
        if (lastindex != matcher.start())
        {
          string.append(text.substring(lastindex, matcher.start()));
        }
        string.append(text.substring(matcher.start(), matcher.end()), STYLER);
        lastindex = matcher.end();
      }
      if (lastindex < text.length())
        string.append(text.substring(lastindex));

      if (!found)
      {
        return null;
      }
      else
      {
        return string;
      }

    }
    return null;
  }

  public Point computeSize(GC gc, int wHint, int hHint, Object value)
  {
    GridItem item = (GridItem) value;

    gc.setFont(item.getFont(getColumn()));

    int x = 0;

    x += leftMargin;

    if (isCheck())
    {
      x += checkRenderer.getBounds().width + insideMargin;
    }

    int y = 0;

    Image image = item.getImage(getColumn());
    if (image != null)
    {
      y = topMargin + image.getBounds().height + bottomMargin;

      x += image.getBounds().width + insideMargin;
    }

    //
    // x += gc.stringExtent(item.getText(getColumn())).x + rightMargin;
    //
    // y = Math.max(y,topMargin + gc.getFontMetrics().getHeight() + bottomMargin);

    int textHeight = 0;
    if (!isWordWrap())
    {
      x += gc.textExtent(item.getText(getColumn())).x + rightMargin;

      textHeight =
          topMargin + textTopMargin + gc.getFontMetrics().getHeight()
              + textBottomMargin + bottomMargin;
    }
    else
    {
      int plainTextWidth;
      if (wHint == SWT.DEFAULT)
        plainTextWidth = gc.textExtent(item.getText(getColumn())).x;
      else
        plainTextWidth = wHint - x - rightMargin;

      TextLayout currTextLayout = new TextLayout(gc.getDevice());
      currTextLayout.setFont(gc.getFont());
      currTextLayout.setText(item.getText(getColumn()));
      currTextLayout.setAlignment(getAlignment());
      currTextLayout.setWidth(plainTextWidth < 1 ? 1 : plainTextWidth);

      x += plainTextWidth + rightMargin;

      textHeight += topMargin + textTopMargin;
      for (int cnt = 0; cnt < currTextLayout.getLineCount(); cnt++)
        textHeight += currTextLayout.getLineBounds(cnt).height;
      textHeight += textBottomMargin + bottomMargin;

      currTextLayout.dispose();
    }

    y = Math.max(y, textHeight);

    return new Point(x, y);
  }

  /**
   * {@inheritDoc}
   */
  public void paint(GC gc, Object value)
  {

    GridItem item = (GridItem) value;

    gc.setFont(item.getFont(getColumn()));

    boolean drawAsSelected = isSelected();

    boolean drawBackground = true;

    if (isCellSelected())
    {
      drawAsSelected = true;// (!isCellFocus());
    }

    if (drawAsSelected)
    {
      gc.setBackground(getDisplay().getSystemColor(SWT.COLOR_LIST_SELECTION));
      gc.setForeground(getDisplay().getSystemColor(
          SWT.COLOR_LIST_SELECTION_TEXT));
    }
    else
    {
      if (item.getParent().isEnabled())
      {
        Color back = item.getBackground(getColumn());

        if (back != null)
        {
          gc.setBackground(back);
        }
        else
        {
          drawBackground = false;
        }
      }
      else
      {
        gc.setBackground(getDisplay().getSystemColor(
            SWT.COLOR_WIDGET_BACKGROUND));
      }
      gc.setForeground(item.getForeground(getColumn()));
    }

    if (drawBackground)
      gc.fillRectangle(getBounds().x, getBounds().y, getBounds().width,
          getBounds().height);

    int x = leftMargin;

    if (isCheck())
    {
      checkRenderer.setChecked(item.getChecked(getColumn()));
      checkRenderer.setGrayed(item.getGrayed(getColumn()));
      if (!item.getParent().isEnabled())
      {
        checkRenderer.setGrayed(true);
      }
      checkRenderer.setHover(getHoverDetail().equals("check"));

      if (isCenteredCheckBoxOnly(item))
      {
        // Special logic if this column only has a checkbox and is centered
        checkRenderer.setBounds(getBounds().x
            + ((getBounds().width - checkRenderer.getBounds().width) / 2),
            (getBounds().height - checkRenderer.getBounds().height) / 2
                + getBounds().y, checkRenderer.getBounds().width, checkRenderer
                .getBounds().height);
      }
      else
      {
        checkRenderer.setBounds(getBounds().x + x,
            (getBounds().height - checkRenderer.getBounds().height) / 2
                + getBounds().y, checkRenderer.getBounds().width, checkRenderer
                .getBounds().height);

        x += checkRenderer.getBounds().width + insideMargin;
      }

      checkRenderer.paint(gc, null);
    }

    Image image = item.getImage(getColumn());
    if (image != null)
    {
      int y = getBounds().y;

      y += (getBounds().height - image.getBounds().height) / 2;

      gc.drawImage(image, getBounds().x + x, y);

      x += image.getBounds().width + insideMargin;
    }

    int width = getBounds().width - x - rightMargin;

    if (drawAsSelected)
    {
      gc.setForeground(getDisplay().getSystemColor(
          SWT.COLOR_LIST_SELECTION_TEXT));
    }
    else
    {
      gc.setForeground(item.getForeground(getColumn()));
    }

    if (!isWordWrap())
    {
      if (textLayout == null)
      {
        textLayout = new TextLayout(gc.getDevice());
        item.getParent().addDisposeListener(new DisposeListener()
        {
          public void widgetDisposed(DisposeEvent e)
          {
            if(textLayout!=null)
              textLayout.dispose();
          }
        });
      }
      textLayout.setFont(gc.getFont());
      String text = item.getText(getColumn());
      textLayout.setText(text);
      textLayout.setAlignment(getAlignment());
      textLayout.setWidth(width < 1 ? 1 : width);

      StyledString styledString = getFilterStyledString(text);
      if (styledString != null)
      {
        StyleRange[] styleRanges = styledString.getStyleRanges();

        textLayout.setText(text);
        textLayout.setFont(item.getFont(getColumn()));

        for (int i = 0; i < styleRanges.length; i++)
        {
          StyleRange curr = prepareStyleRange(styleRanges[i], true);
          textLayout.setStyle(curr, curr.start, curr.start + curr.length - 1);
        }
      }

      textLayout.draw(gc, getBounds().x + x, getBounds().y + textTopMargin
          + topMargin);
      textLayout.dispose();
      textLayout = null;
    }
    else
    {
      if (textLayout == null)
      {
        textLayout = new TextLayout(gc.getDevice());
        item.getParent().addDisposeListener(new DisposeListener()
        {
          public void widgetDisposed(DisposeEvent e)
          {
            if(textLayout!=null)
              textLayout.dispose();
          }
        });
      }

      textLayout.setFont(gc.getFont());
      String text = item.getText(getColumn());
      textLayout.setText(text);
      textLayout.setAlignment(getAlignment());
      textLayout.setWidth(width < 1 ? 1 : width);
      StyledString styledString = getFilterStyledString(text);
      if (styledString != null)
      {
        StyleRange[] styleRanges = styledString.getStyleRanges();

        textLayout.setText(text);
        textLayout.setFont(item.getFont(getColumn()));

        for (int i = 0; i < styleRanges.length; i++)
        {
          StyleRange curr = prepareStyleRange(styleRanges[i], true);
          textLayout.setStyle(curr, curr.start, curr.start + curr.length - 1);
        }
      }

      if (item.getParent().isAutoHeight())
      {
        // Look through all columns (except this one) to get the max height needed for this item
        int columnCount = item.getParent().getColumnCount();
        int maxHeight =
            textLayout.getBounds().height + textTopMargin + textBottomMargin;
        for (int i = 0; i < columnCount; i++)
        {
          GridColumn column = item.getParent().getColumn(i);
          if (i != getColumn() && column.getWordWrap())
          {
            int height =
                column.getCellRenderer().computeSize(gc, column.getWidth(),
                    SWT.DEFAULT, item).y;
            maxHeight = Math.max(maxHeight, height);
          }
        }

        // Also look at the row header if necessary
        if (item.getParent().isWordWrapHeader())
        {
          int height =
              item.getParent().getRowHeaderRenderer().computeSize(gc,
                  SWT.DEFAULT, SWT.DEFAULT, item).y;
          maxHeight = Math.max(maxHeight, height);
        }

        if (maxHeight != item.getHeight())
        {
          item.setHeight(maxHeight);
        }
      }
      textLayout.draw(gc, getBounds().x + x, getBounds().y + textTopMargin
          + topMargin);
      textLayout.dispose();
      textLayout = null;
    }

    if (item.getParent().getLinesVisible())
    {
      if (isCellSelected())
      {
        // XXX: should be user definable?
        gc.setForeground(getDisplay().getSystemColor(
            SWT.COLOR_WIDGET_DARK_SHADOW));
      }
      else
      {
        gc.setForeground(item.getParent().getLineColor());
      }
      gc.drawLine(getBounds().x, getBounds().y + getBounds().height,
          getBounds().x + getBounds().width - 1, getBounds().y
              + getBounds().height);
      gc.drawLine(getBounds().x + getBounds().width - 1, getBounds().y,
          getBounds().x + getBounds().width - 1, getBounds().y
              + getBounds().height);
    }

    if (isCellFocus())
    {
      Rectangle focusRect =
          new Rectangle(getBounds().x, getBounds().y, getBounds().width - 1,
              getBounds().height);

      gc.setForeground(getDisplay().getSystemColor(SWT.COLOR_LIST_FOREGROUND));
      gc.drawRectangle(focusRect);

      if (isFocus())
      {
        focusRect.x++;
        focusRect.width -= 2;
        focusRect.y++;
        focusRect.height -= 2;

        gc.drawRectangle(focusRect);
      }
    }
    return;

  }

  private StyleRange prepareStyleRange(StyleRange styleRange,
      boolean applyColors)
  {
    // if no colors apply or font is set, create a clone and clear the
    // colors and font
    if (!applyColors
        && (styleRange.foreground != null || styleRange.background != null))
    {
      styleRange = (StyleRange) styleRange.clone();
      if (!applyColors)
      {
        styleRange.foreground = null;
        styleRange.background = null;
      }
    }
    return styleRange;
  }

  private boolean isCenteredCheckBoxOnly(GridItem item)
  {
    return !isTree() && item.getImage(getColumn()) == null
        && item.getText(getColumn()).equals("") && getAlignment() == SWT.CENTER;
  }
}