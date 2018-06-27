package info.limpet.stackedcharts.ui.editor.figures;

import org.eclipse.draw2d.FlowLayout;
import org.eclipse.draw2d.LayoutManager;
import org.eclipse.draw2d.RectangleFigure;

/**
 * A Draw2D container {@link RectangleFigure} that orders its children in row or in a column based
 * on the {@link #isVertical()} property. It uses internal layout, clients should not attempt to set
 * new {@link LayoutManager}.
 */
public class DirectionalShape extends RectangleFigure
{

  private boolean vertical;

  public DirectionalShape()
  {
    DirectionFlowLayout manager = new DirectionFlowLayout();
    manager.setStretchMinorAxis(true);
    manager.setMinorAlignment(DirectionFlowLayout.ALIGN_CENTER);
    manager.setMajorAlignment(DirectionFlowLayout.ALIGN_CENTER);
    super.setLayoutManager(manager);
    setOutline(false);
  }

  /**
   * @see #setVertical(boolean)
   * @return
   */
  public boolean isVertical()
  {
    return vertical;
  }

  /**
   * @param vertical
   *          when <code>true</code> children will be laid out from bottom to top
   */
  public void setVertical(boolean vertical)
  {
    this.vertical = vertical;
    ((FlowLayout) getLayoutManager()).setHorizontal(!vertical);
  }

  /**
   * Not intended to be called
   */
  @Override
  public void setLayoutManager(LayoutManager manager)
  {
    throw new UnsupportedOperationException("Layout manager is read-only");
  }
}
