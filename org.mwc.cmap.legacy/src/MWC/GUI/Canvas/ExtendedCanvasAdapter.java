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
  /**
   * the alpha depth of semi-transparent objects
   *
   */
  private static final float SEMI_TRANSPARENCY_ALPHA = 0.5f;
  private final Graphics2D _dest;

  private boolean _xorOn = false;

  public ExtendedCanvasAdapter(final PlainProjection proj, final Graphics dest,
      final Color bkColor)
  {
    super(proj, dest, bkColor);

    _dest = (Graphics2D) dest;
  }

  @Override
  public void drawLine(final int x1, final int y1, final int x2, final int y2,
      final int transparency)
  {
    final Composite originalComposite = _dest.getComposite();
    _dest.setComposite(makeComposite(transparency / 255f));
    _dest.drawLine(x1, y1, x2, y2);
    _dest.setComposite(originalComposite);
  }

  @Override
  public void fillShape(final Shape shape)
  {
    _dest.fill(shape);
  }

  @Override
  public boolean getXORMode()
  {
    return _xorOn;
  }

  private AlphaComposite makeComposite(final float alpha)
  {
    final int type = AlphaComposite.SRC_OVER;
    return (AlphaComposite.getInstance(type, alpha));
  }

  @Override
  public void nofillShape(final Shape shape)
  {
    if (shape != null)
    {
      _dest.draw(shape);
    }
  }

  @Override
  public void semiFillArc(final int x, final int y, final int width,
      final int height, final int startAngle, final int arcAngle)
  {
    final Composite originalComposite = _dest.getComposite();
    _dest.setComposite(makeComposite(SEMI_TRANSPARENCY_ALPHA));
    _dest.fillArc(x, y, width, height, startAngle, arcAngle);
    _dest.setComposite(originalComposite);
  }

  @Override
  public void semiFillOval(final int x, final int y, final int width,
      final int height)
  {
    final Composite originalComposite = _dest.getComposite();
    _dest.setComposite(makeComposite(SEMI_TRANSPARENCY_ALPHA));
    _dest.fillOval(x, y, width, height);
    _dest.setComposite(originalComposite);
  }

  @Override
  public void semiFillPolygon(final int[] xPoints, final int[] yPoints,
      final int nPoints)
  {
    final Composite originalComposite = _dest.getComposite();
    _dest.setComposite(makeComposite(SEMI_TRANSPARENCY_ALPHA));
    _dest.fillPolygon(xPoints, yPoints, nPoints);
    _dest.setComposite(originalComposite);
  }

  @Override
  public void semiFillRect(final int x, final int y, final int wid,
      final int height)
  {
    final Composite originalComposite = _dest.getComposite();
    _dest.setComposite(makeComposite(SEMI_TRANSPARENCY_ALPHA));
    _dest.fillRect(x, y, wid, height);
    _dest.setComposite(originalComposite);
  }

  @Override
  public void semiFillShape(final Shape shape)
  {
    final Composite originalComposite = _dest.getComposite();
    _dest.setComposite(makeComposite(SEMI_TRANSPARENCY_ALPHA));
    _dest.fill(shape);
    _dest.setComposite(originalComposite);
  }

  @Override
  public void setXORMode(final boolean mode)
  {
    _dest.setXORMode(super.getBackgroundColor());
    _xorOn = mode;
  }

}
