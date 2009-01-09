package MWC.Algorithms.Projections;

import java.awt.*;
import java.lang.Math;
import MWC.GenericData.*;
import MWC.Algorithms.*;

public class JMapTransformMercator extends PlainProjection
{
  
  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected Rectangle screen_;                   // screen space rectangle
  protected double scale_;                       // default scale factor  (screen width)/(map width)
	protected double centerMap_, centerScreen_;    // map center coordinate (lon) and the screen center (x)
	protected double latScale_;                    // x scale factor.. based on y with Mercator
  protected double ox_, oy_;                     // upper left hand coordinates of the map taken from _mpc
  protected double x1_, x2_, y1_, y2_;           // map coordinates
  
  final double PI = 3.1415927;
  double yscale_;
    
    public JMapTransformMercator()
    {
      super("Mercator");
    }
  
  public void setDataArea(WorldArea theArea)
  {
    super.setDataArea(theArea);
    x1_ = theArea.getTopLeft().getLong();
    y1_ = theArea.getTopLeft().getLat();
    x2_ = theArea.getBottomRight().getLong();
    y2_ = theArea.getBottomRight().getLat();
    
    resetData();
  }

  public void setScreenArea(Dimension screen)
  {
    super.setScreenArea(screen);
    
    screen_ = new Rectangle(0,0,screen.width,screen.height); 
    
    resetData();
  }

    
  protected void resetData()
  {
    if((super.getScreenArea() == null) |
       (super.getDataArea() == null))
      return;
    
	  ox_ = x1_;
	  oy_ = y1_;
  	centerMap_ = x1_-(x1_-x2_)/2.0;
  	centerScreen_ = (double)screen_.width/2.0;
//  	double sx = (double)screen_.width/(0.67*(x2_-x1_));
  	double sx = (double)screen_.width/(x2_-x1_);
  	double sy = (double)screen_.height/(y1_-y2_);
  	if(sx<sy)scale_ = sx;
  	else scale_ = sy;
  	yscale_ = sy;

  }
    
    
    
  public WorldLocation getPoint(int x, int y){
  	latScale_ = Math.cos(((oy_-(double)y/scale_)/180.0)*PI);
   	return new WorldLocation(centerMap_-(x-centerScreen_)/(latScale_*scale_),
                             (oy_-((double)y/scale_)),
                             0);
	}
    
  public Point getPoint(double x, double y){
       	latScale_ = Math.cos((y/180.0)*PI);
        return new Point((int)((centerMap_-x)*latScale_*-1 *scale_+centerScreen_),
                         (int)((oy_-y)*scale_));
  }

  public WorldLocation toWorld(Point val)
  {
    WorldLocation res = getPoint(val.x,
                                 val.y);
    return res;
  }

  public Point toScreen(WorldLocation val)
  {
    Point res = getPoint(val.getLong(),
                         val.getLat());
    return res;
  }

  public void zoom(double value)
  {
  }

}

