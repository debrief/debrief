package MWC.Algorithms.Projections;

// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: Mercator2.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.2 $
// $Log: Mercator2.java,v $
// Revision 1.2  2004/05/24 16:28:57  Ian.Mayo
// Commit updates from home
//
// Revision 1.1.1.1  2004/03/04 20:31:13  ian
// no message
//
// Revision 1.1.1.1  2003/07/17 10:07:00  Ian.Mayo
// Initial import
//
// Revision 1.3  2003-02-07 09:49:13+00  ian_mayo
// rationalise unnecessary to da comments (that's do really)
//
// Revision 1.2  2002-05-28 09:25:33+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:15:17+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-11 14:02:36+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-01-25 13:29:45+00  administrator
// Make data and screen area objects private, so that we can track their use
//
// Revision 1.0  2001-07-17 08:46:59+01  administrator
// Initial revision
//
// Revision 1.1  2001-01-03 13:43:12+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 21:42:33  ianmayo
// initial version
//
// Revision 1.3  2000-10-31 15:43:04+00  ian_mayo
// perform tidying up to keep JBuilder happy
//
// Revision 1.2  2000-02-03 15:07:50+00  ian_mayo
// First issue to Devron (modified files are mostly related to WMF)
//
// Revision 1.1  1999-10-12 15:37:36+01  ian_mayo
// Initial revision
//
// Revision 1.2  1999-09-14 10:18:57+01  administrator
// early version
//
// Revision 1.1  1999-08-26 10:04:41+01  administrator
// Initial revision
//

import java.io.*;
import java.beans.*;
import java.awt.*;
import MWC.GenericData.*;
import MWC.Algorithms.*;
import MWC.GUI.*;


public class Mercator2 extends PlainProjection implements Serializable,
                                                          Editable
{
  /////////////////////////////////////////////////////////////
  // member variables
  ////////////////////////////////////////////////////////////

  // the current ellipse
  protected Ellipse _myEllipse;

  //////////////////////////////////////////////////////////
  // characteristics of the current ellipsoied
  //////////////////////////////////////////////////////////
  protected double _e; // current eccentricity
  protected double _a; // current equatorial radius

  ///////////////////////////////////////////////////////////
  // terms which are dependent on the current ellipsoid
  ///////////////////////////////////////////////////////////
  protected double _A0;
  protected double _A2;
  protected double _A4;
  protected double _A6;

  protected double _e2;
  protected double _e4;
  protected double _e6;

  protected double _t1;

  ///////////////////////////////////////////////////////////
  // terms which are dependent on the current screen resolution
  ///////////////////////////////////////////////////////////
  protected double _mp; // one meridional part for this
                        // screen size & data area
  protected double _mpLowerLat; /** one meridional part for the lower
                                  * limit of latitude */
  protected double _lgTerm; // length of meridional part

  protected double _scaleVal;

  final protected double _1_over_60; // a 60th (in rads)

  /////////////////////////////////////////////////////////////
  // constructor
  ////////////////////////////////////////////////////////////
  public Mercator2()
  {
    super("Mercator");

    // calculate our constants
    _1_over_60 = Conversions.Degs2Rads(1/60.0);

    setEllipse(new Ellipse());

  }

  /////////////////////////////////////////////////////////////
  // member functions
  ////////////////////////////////////////////////////////////
  public void setEllipse(Ellipse val)
  {
    // store it
    _myEllipse = val;

    _e = val.getEccentricity();
    _a = val.getRadius();

    // fill out the rest of our terms (powers of eccentricity)
    _e2 = Math.pow(_e, 2);
    _e4 = Math.pow(_e, 4);
    _e6 = Math.pow(_e, 6);

    // and now the big A numbers
    _A0 = 1 - 1/4   * _e2 -
              3/64  * _e4 -
              5/256 * _e6;

    _A2 = 3/8 * (         _e2 +
                    1/2 * _e4 +
                 15/128 * _e6);

    _A4 = 15/256 * (_e4 + 3/4 * _e6);
    _A6 = 35/3072 * _e6;

    _t1 = _a * Math.sin(_1_over_60);
  }

  protected double length(double val)
  {
    double lat = Conversions.Degs2Rads(val);

    double res;
    res = _a * (_A0 * lat -
          _A2 * Math.sin(2 * lat) +
          _A4 * Math.sin(4 * lat) -
          _A6 * Math.sin(6 * lat));
    return res;
  }

  protected double mParts(double val)
  {

    double lat = Conversions.Degs2Rads(val);

    double res;

    res = 10800 / Math.PI *
          (Math.log(Math.tan(Math.PI/4 + lat/2)) -
           _e2 * Math.sin(lat) -
           1/3 * _e4 * Math.sin(Math.pow(lat, 3)) -
           1/5 * _e6 * Math.sin(Math.pow(lat, 5)));

    return res;
  }

  protected double lg(double val1,
                      double val2)
  {
    double lat1 = Conversions.Degs2Rads(val1);
    double lat2 = Conversions.Degs2Rads(val2);

    double res;

    double t2 = 1 - (_e2 / 4) * (1 + 3 * Math.cos(lat1 + lat2));

    res = _t1 * t2;

    return res;
  }

  public String getName()
  {
    return null;
  }

  public boolean hasEditor()
  {
    return false;
  }

  public Editable.EditorType getInfo()
  {
    return null;
  }


  public java.awt.Point toScreen(WorldLocation val)
  {
    java.awt.Point res = null;

    if(_scaleVal == 0)
      return res;

    WorldArea wa = getDataArea();
    Dimension sz = getScreenArea();

    double x = (val.getLong() - wa.getTopLeft().getLong()) * _scaleVal;

    double y = (mParts(val.getLat()) - _mpLowerLat) * _mp;

    // invert the y axis
    y = sz.height - y;

    res = new Point((int)x, (int)y);

    return res;
  }

  public WorldLocation toWorld(java.awt.Point val)
  {
    WorldLocation res = null;

    if(_scaleVal == 0)
      return res;

    WorldArea wa = getDataArea();
    Dimension sz = getScreenArea();

    // invert y axis
    double y = sz.height - val.y;

    double lng = val.x / _scaleVal + wa.getTopLeft().getLong();

    double mLat = y / _mp + _mpLowerLat;

    /** now steve's new bit
     */
    double lat2 = -80;
    double latOld = lat2;
    boolean foundLat = false;
    while((lat2 < 80) && (foundLat == false))
    {
      double mp = mParts(lat2);
      if(mp > mLat)
      {
        foundLat = true;
      }
      else
      {
        latOld = lat2;
        lat2 = lat2 + 1;
      }
    }

    if(foundLat == false)
    {
      return new WorldLocation(0,0,0);
    }

    double co = 0;
    double dLat = (lat2 - latOld) / 10.0;
    while(dLat > 0.00000001)
    {
      foundLat = false;
      if((co < 10) && (foundLat == false))
      {
        lat2 = latOld + dLat;
        co = co + 1;
        if(((long)mParts(lat2) * 10000) >= ((long)mLat * 10000))
        {
          foundLat = true;
          dLat = (lat2 - latOld) / 10;
          co = 0;
        }
        else
        {
          latOld = lat2;
        }
      }
    }

    res = new WorldLocation(lat2, lng, 0);

    return res;
  }

  /**
   *
   */
  public void setScreenArea(java.awt.Dimension theArea)
  {
    super.setScreenArea(theArea);

    rescale();
  }

  /**
   *
   */
  public void setDataArea(WorldArea theArea)
  {
    super.setDataArea(theArea);

    rescale();
  }

  public void zoom(double value)
  {
    if(value != 0.0)
    {
      _scaleVal /= value;
    }
    else
    {
      rescale();
    }
  }

  protected void rescale()
  {
    WorldArea wa = super.getDataArea();
    Dimension sz = super.getScreenArea();

    if((wa == null) |
       (sz == null))
      return;

    // find the y scale factor
    double dy =  getScreenArea().height / (getDataArea().getHeight());// * _dataBorder);

    // find the maximum of these
    _scaleVal = dy;// Math.min(dx, dy);

    // calculate the length of one meridional part at this lat
    _mp = sz.height / (mParts(wa.getTopLeft().getLat()) -
                      mParts(wa.getBottomRight().getLat()));

    // calculate the length of a meridional part at this lat
    _mpLowerLat = mParts(wa.getBottomRight().getLat());

    // calculate the length of the meridional part
    _lgTerm = lg(wa.getBottomRight().getLat(), wa.getTopLeft().getLat()) / 60;
  }

  /////////////////////////////////////////////////////////////
  // nested class
  ////////////////////////////////////////////////////////////
  protected class Ellipse implements Serializable
  {
    final protected double _eccent;
    final protected double _radius; // nautical metres


    public Ellipse()
    {
      _eccent = 0.0818191908;
      _radius = 6378137;
    }

    public double getEccentricity()
    {
      return _eccent;
    }

    public double getRadius()
    {
      return _radius;
    }
  }



}
