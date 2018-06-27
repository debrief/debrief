package info.limpet.stackedcharts.ui.editor.figures;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.draw2d.FlowLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;

/**
 * An extension of the standard {@link FlowLayout}. The only difference is that when the layout is
 * vertical, children need to be arranged bottom-to-top, instead of top-to-bottom. This is achieved
 * by iterating the children collection in reverse order in the {@link #layout(IFigure)}
 * method.</br>
 * Arranging bottom-to-top is desired in order to achieve reading consistency with the Left-To-Right
 * directional language preference.
 */
public class DirectionFlowLayout extends FlowLayout
{
  /**
   * Need to subclass the protected inner class WorkingData, otherwise won't be able to access it.
   * Reference: http://stackoverflow.com/a/17610709/1649388
   */
  protected class DirectionalWorkingData extends WorkingData
  {
  }

  /**
   * Most of the code is copied from the superclass, the only change is iterating the children in
   * reverse order when layout is vertical.
   * 
   * @see org.eclipse.draw2d.LayoutManager#layout(IFigure)
   */
  public void layout(IFigure parent)
  {
    data = new DirectionalWorkingData();
    Rectangle relativeArea = parent.getClientArea();
    data.area = transposer.t(relativeArea);

    // iterate the children in reverse order in case of vertical layout
    @SuppressWarnings({"unchecked", "rawtypes"})
    List<?> childrenCopy = new ArrayList(parent.getChildren());
    if (!isHorizontal())
    {
      Collections.reverse(childrenCopy);
    }
    Iterator<?> iterator = childrenCopy.iterator();
    int dx;

    // Calculate the hints to be passed to children
    int wHint = -1;
    int hHint = -1;
    if (isHorizontal())
      wHint = parent.getClientArea().width;
    else
      hHint = parent.getClientArea().height;

    initVariables(parent);
    initRow();
    while (iterator.hasNext())
    {
      IFigure f = (IFigure) iterator.next();
      Dimension pref = transposer.t(getChildSize(f, wHint, hHint));
      Rectangle r = new Rectangle(0, 0, pref.width, pref.height);

      if (data.rowCount > 0)
      {
        if (data.rowWidth + pref.width > data.maxWidth)
          layoutRow(parent);
      }
      r.x = data.rowX;
      r.y = data.rowY;
      dx = r.width + getMinorSpacing();
      data.rowX += dx;
      data.rowWidth += dx;
      data.rowHeight = Math.max(data.rowHeight, r.height);
      data.row[data.rowCount] = f;
      data.bounds[data.rowCount] = r;
      data.rowCount++;
    }
    if (data.rowCount != 0)
      layoutRow(parent);
    data = null;
  }

}
