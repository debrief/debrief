
/*******************************************************************************
 * Debrief - the Open Source Maritime Analysis Application
 * http://debrief.info
 *  
 * (C) 2000-2020, Deep Blue C Technology Ltd
 *  
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html)
 *  
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 *******************************************************************************/
  static final long serialVersionUID = 1;

  /**
   * the size of the symbol
   */
  protected final int wid = 4;

  /**
   * <init>
   *
   */
  public CircleSymbol(){
    super();
     // construct the symbol from a sequence of metafile commands,
     // if we think it is really necessary...
  }
  
  @Override
  public PlainSymbol create()
  {
    return new CircleSymbol();
  }
  /**
   * getBounds
   *
   * @return the returned java.awt.Dimension
   */
  public java.awt.Dimension getBounds()
  {
    final int sWid = (int)(wid * getScaleVal());
    return new java.awt.Dimension(2 * sWid, 2 * sWid);
  }

  /**
   * getType
   *
   * @return the returned String
   */
  public String getType()
  {
    return "Circle";
  }

  /** get this symbol as a sequence of lines.
   * The
   *
   * @return a collection of paths.  Each path is a collection of java.awt.Point objects.
   */
  public Vector<Vector<Point2D>> getCoordinates() {
  	final Vector<Vector<Point2D>> res = new Vector<Vector<Point2D>>(0,1);

    // now the circle
    final Vector<Point2D> circle = new Vector<Point2D>(0,1);

    // work our way around the circle, adding the pts
    final int NUM_SEGMENTS = 30;
    for (int i=0; i<=NUM_SEGMENTS; i++)
    {
      // produce the current bearing
      final double this_brg = (360.0 / NUM_SEGMENTS * i) / 180.0 * Math.PI;

      final Point2D newP = new Point2D.Double(Math.sin(this_brg) * wid/2, Math.cos(this_brg) * wid/2);

      circle.add(newP);
    }

    // store the circle
    res.add(circle);

    return res;
  }

  public void paint(final CanvasType dest, final WorldLocation centre)
  {
    paint(dest, centre, 0.0);
  }

  /**
   * paint
   *
   * @param dest parameter for paint
   * @param theLocation the place where we paint it
   */
  public void paint(final CanvasType dest, final WorldLocation theLocation, final double direction)
  {
    // set the colour
    dest.setColor(getColor());

    // create our centre point
    final java.awt.Point centre = dest.toScreen(theLocation);
    
    // handle unable to gen screen coords (if off visible area)
    if(centre == null)
      return;

    // calculate the scaled width
    final int sWid = (int)(wid * getScaleVal());

    // draw our square at the set radius around the centre
    if(getFillSymbol())
      dest.fillOval(centre.x - sWid, centre.y - sWid,  sWid * 2,  sWid * 2);
    else
      dest.drawOval(centre.x - sWid, centre.y - sWid,  sWid * 2,  sWid * 2);
  }


}



