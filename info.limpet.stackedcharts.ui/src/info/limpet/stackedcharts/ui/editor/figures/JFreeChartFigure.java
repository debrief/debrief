package info.limpet.stackedcharts.ui.editor.figures;

import info.limpet.stackedcharts.model.Chart;
import info.limpet.stackedcharts.ui.view.ChartBuilder;

import java.awt.image.BufferedImage;
import java.awt.image.DirectColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.WritableRaster;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;
import org.jfree.chart.JFreeChart;

public class JFreeChartFigure extends Figure
{
  final Chart chart;

  public JFreeChartFigure(Chart chart)
  {
    this.chart = chart;
    setPreferredSize(20, 20);

  }

  @Override
  protected void paintFigure(Graphics graphics)
  {
    super.paintFigure(graphics);
    Rectangle clientArea = getClientArea();
    
    JFreeChart freeChart = ChartBuilder.build(chart);
    BufferedImage image = freeChart.createBufferedImage(clientArea.width, clientArea.height);
   
    Image srcImage = new Image(Display.getCurrent(), convertToSWT(image));
    graphics.drawImage(srcImage, clientArea.x , clientArea.y);
    srcImage.dispose();

  }

  //from SWT doc site
  static ImageData convertToSWT(BufferedImage bufferedImage)
  {
    if (bufferedImage.getColorModel() instanceof DirectColorModel)
    {
      DirectColorModel colorModel =
          (DirectColorModel) bufferedImage.getColorModel();
      PaletteData palette =
          new PaletteData(colorModel.getRedMask(), colorModel.getGreenMask(),
              colorModel.getBlueMask());
      ImageData data =
          new ImageData(bufferedImage.getWidth(), bufferedImage.getHeight(),
              colorModel.getPixelSize(), palette);
      for (int y = 0; y < data.height; y++)
      {
        for (int x = 0; x < data.width; x++)
        {
          int rgb = bufferedImage.getRGB(x, y);
          int pixel =
              palette.getPixel(new RGB((rgb >> 16) & 0xFF, (rgb >> 8) & 0xFF,
                  rgb & 0xFF));
          data.setPixel(x, y, pixel);
          if (colorModel.hasAlpha())
          {
            data.setAlpha(x, y, (rgb >> 24) & 0xFF);
          }
        }
      }
      return data;
    }
    else if (bufferedImage.getColorModel() instanceof IndexColorModel)
    {
      IndexColorModel colorModel =
          (IndexColorModel) bufferedImage.getColorModel();
      int size = colorModel.getMapSize();
      byte[] reds = new byte[size];
      byte[] greens = new byte[size];
      byte[] blues = new byte[size];
      colorModel.getReds(reds);
      colorModel.getGreens(greens);
      colorModel.getBlues(blues);
      RGB[] rgbs = new RGB[size];
      for (int i = 0; i < rgbs.length; i++)
      {
        rgbs[i] = new RGB(reds[i] & 0xFF, greens[i] & 0xFF, blues[i] & 0xFF);
      }
      PaletteData palette = new PaletteData(rgbs);
      ImageData data =
          new ImageData(bufferedImage.getWidth(), bufferedImage.getHeight(),
              colorModel.getPixelSize(), palette);
      data.transparentPixel = colorModel.getTransparentPixel();
      WritableRaster raster = bufferedImage.getRaster();
      int[] pixelArray = new int[1];
      for (int y = 0; y < data.height; y++)
      {
        for (int x = 0; x < data.width; x++)
        {
          raster.getPixel(x, y, pixelArray);
          data.setPixel(x, y, pixelArray[0]);
        }
      }
      return data;
    }
    return null;
  }
}
