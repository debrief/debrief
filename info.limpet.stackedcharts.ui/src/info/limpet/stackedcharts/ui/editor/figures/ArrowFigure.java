package info.limpet.stackedcharts.ui.editor.figures;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

public class ArrowFigure extends Figure
{

  private static final int ARROW_HEAD_LENGTH = 10;
  private static final int ARROW_HEAD_HALF_WIDTH = 8;

  private boolean horizontal;

  public ArrowFigure(boolean horizontal)
  {
    this.setHorizontal(horizontal);
    Color color = Display.getDefault().getSystemColor(SWT.COLOR_DARK_GRAY);
    setBackgroundColor(color);
    setForegroundColor(color);
  }

  @Override
  protected void paintFigure(Graphics graphics)
  {
    super.paintFigure(graphics);
    Rectangle clientArea = getClientArea();
    Point top = clientArea.getTop();
    Point right = clientArea.getRight();
    final int oldWid = graphics.getLineWidth();
    graphics.setLineWidth(3);

    PointList points = new PointList();
    if (isHorizontal())
    {
      graphics.drawLine(clientArea.getLeft(), right);
      points.addPoint(right);
      points.addPoint(right.getCopy().translate(-ARROW_HEAD_LENGTH,
          -ARROW_HEAD_HALF_WIDTH));
      points.addPoint(right.getCopy().translate(-ARROW_HEAD_LENGTH,
          ARROW_HEAD_HALF_WIDTH));
    }
    else
    {
      graphics.drawLine(clientArea.getBottom(), top);
      points.addPoint(top);
      points.addPoint(top.getCopy().translate(ARROW_HEAD_HALF_WIDTH,
          ARROW_HEAD_LENGTH));
      points.addPoint(top.getCopy().translate(-ARROW_HEAD_HALF_WIDTH,
          ARROW_HEAD_LENGTH));
    }
    graphics.fillPolygon(points);

    graphics.setLineWidth(oldWid);

  }

  public boolean isHorizontal()
  {
    return horizontal;
  }

  public void setHorizontal(boolean horizontal)
  {
    this.horizontal = horizontal;
    if (horizontal)
    {
      setPreferredSize(-1, 20);
    }
    else
    {
      setPreferredSize(20, -1);
    }
    repaint();
  }
}
