package info.limpet.stackedcharts.ui.editor.parts;

import info.limpet.stackedcharts.ui.editor.StackedchartsImages;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.gef.editpolicies.NonResizableEditPolicy;
import org.eclipse.swt.graphics.Image;

public class StylingEditPart extends AbstractGraphicalEditPart
{

  /**
   * Standard Eclipse icon, source: http://eclipse-icons.i24.cc/eclipse-icons-07.html
   */
  private static final Image IMAGE = StackedchartsImages.getImage(StackedchartsImages.DESC_PAINT);

  @Override
  protected IFigure createFigure()
  {
    Label label = new Label(IMAGE);
    label.setToolTip(new Label("Click to view style properties"));
    return label;
  }

  @Override
  protected void createEditPolicies()
  {
    installEditPolicy(EditPolicy.PRIMARY_DRAG_ROLE,
        new NonResizableEditPolicy());
  }

}
