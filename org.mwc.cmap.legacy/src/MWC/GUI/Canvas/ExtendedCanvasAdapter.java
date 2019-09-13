package MWC.GUI.Canvas;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;

import MWC.Algorithms.PlainProjection;
import MWC.GUI.ExtendedCanvasType;

public class ExtendedCanvasAdapter extends CanvasAdaptor implements
    ExtendedCanvasType
{
  private final Graphics2D _dest;
  private boolean _xorOn = false;
  
  /**
   * the alpha depth of semi-transparent objects
   * 
   */
  private static final float SEMI_TRANSPARENCY_ALPHA = 0.5f;
  
  public ExtendedCanvasAdapter(PlainProjection proj, Graphics dest,
      Color bkColor)
  {
    super(proj, dest, bkColor);
    
    _dest = (Graphics2D) dest;
  }

  @Override
  public void fillShape(Shape shape)
  {
    _dest.fill(shape);
  }

  @Override
  public void semiFillOval(int x, int y, int width, int height)
  {
    Composite originalComposite = _dest.getComposite();
    _dest.setComposite(makeComposite(SEMI_TRANSPARENCY_ALPHA));
    _dest.fillOval(x, y, width, height);
    _dest.setComposite(originalComposite);
  }

  @Override
  public void semiFillPolygon(int[] xPoints, int[] yPoints, int nPoints)
  {
    Composite originalComposite = _dest.getComposite();
    _dest.setComposite(makeComposite(SEMI_TRANSPARENCY_ALPHA));
    _dest.fillPolygon(xPoints, yPoints, nPoints);
    _dest.setComposite(originalComposite);
  }

  @Override
  public void semiFillArc(int x, int y, int width, int height, int startAngle,
      int arcAngle)
  {
    Composite originalComposite = _dest.getComposite();
    _dest.setComposite(makeComposite(SEMI_TRANSPARENCY_ALPHA));
    _dest.fillArc(x, y, width, height, startAngle, arcAngle);
    _dest.setComposite(originalComposite);
  }

  @Override
  public void semiFillRect(int x, int y, int wid, int height)
  {
    Composite originalComposite = _dest.getComposite();
    _dest.setComposite(makeComposite(SEMI_TRANSPARENCY_ALPHA));
    _dest.fillRect(x, y, wid, height);
    _dest.setComposite(originalComposite);
  }

  @Override
  public void semiFillShape(Shape shape)
  {
    Composite originalComposite = _dest.getComposite();
    _dest.setComposite(makeComposite(SEMI_TRANSPARENCY_ALPHA));
    _dest.fill(shape);
    _dest.setComposite(originalComposite);
  }
  
  private AlphaComposite makeComposite(float alpha) {
    int type = AlphaComposite.SRC_OVER;
    return(AlphaComposite.getInstance(type, alpha));
   }

  @Override
  public void drawLine(int x1, int y1, int x2, int y2, int transparency)
  {
    Composite originalComposite = _dest.getComposite();
    _dest.setComposite(makeComposite(transparency / 255f));
    _dest.drawLine(x1, y1, x2, y2);
    _dest.setComposite(originalComposite);
  }

  @Override
  public void setXORMode(boolean mode)
  {
    _dest.setXORMode(super.getBackgroundColor());
    _xorOn = mode;
  }

  @Override
  public boolean getXORMode()
  {
    return _xorOn;
  }

  @Override
  public void nofillShape(Shape shape)
  {
    if (shape != null)
    {
      _dest.draw(shape);
    }
  }

}
