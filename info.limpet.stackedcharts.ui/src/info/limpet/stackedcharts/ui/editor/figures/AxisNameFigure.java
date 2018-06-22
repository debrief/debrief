package info.limpet.stackedcharts.ui.editor.figures;

import org.eclipse.draw2d.ActionListener;
import org.eclipse.draw2d.Button;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Display;

import info.limpet.stackedcharts.ui.editor.Activator;
import info.limpet.stackedcharts.ui.editor.StackedchartsImages;

public class AxisNameFigure extends DirectionalShape
{
  private static volatile Font boldFont;
  private DirectionalLabel nameLabel;

  public AxisNameFigure(ActionListener deleteHandler)
  {


    add(new Label(StackedchartsImages.getImage(StackedchartsImages.DESC_AXIS)));
    nameLabel = new DirectionalLabel(Activator.FONT_10);
    nameLabel.setTextAlignment(PositionConstants.TOP);

    add(nameLabel);
    
    Button button = new Button(StackedchartsImages.getImage(StackedchartsImages.DESC_DELETE));
    button.setToolTip(new Label("Remove this axis from the chart"));
    button.addActionListener(deleteHandler);
    add(button);

  }

  public void setName(String name)
  {
    
    nameLabel.setText(name);
    // cache font for AxisNameFigure
    if (boldFont == null)
    {
      FontData fontData = nameLabel.getFont().getFontData()[0];
      boldFont =
          new Font(Display.getCurrent(), new FontData(fontData.getName(),
              fontData.getHeight(), SWT.BOLD));
    }

    nameLabel.setFont(boldFont);
  }
  
  @Override
  public void setFont(Font f)
  {
    nameLabel.setFont(boldFont);
  }
  
  public void setVertical(boolean vertical) {
    super.setVertical(vertical);
    nameLabel.setVertical(vertical);
  }

}
