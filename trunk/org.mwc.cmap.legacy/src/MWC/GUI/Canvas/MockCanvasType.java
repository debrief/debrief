package MWC.GUI.Canvas;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.image.ImageObserver;
import java.util.Enumeration;

import MWC.Algorithms.PlainProjection;
import MWC.GUI.CanvasType;
import MWC.GenericData.WorldLocation;

public class MockCanvasType implements CanvasType
{

    public void addPainter(final PaintListener listener)
    {
        // TODO Auto-generated method stub

    }

    public void drawArc(final int x, final int y, final int width, final int height, final int startAngle,
            final int arcAngle)
    {
        // TODO Auto-generated method stub

    }

    public boolean drawImage(final Image img, final int x, final int y, final int width, final int height,
            final ImageObserver observer)
    {
        // TODO Auto-generated method stub
        return false;
    }

    public void drawLine(final int startX, final int startY, final int endX, final int endY)
    {
        // TODO Auto-generated method stub

    }

    public void drawOval(final int x, final int y, final int width, final int height)
    {
        // TODO Auto-generated method stub

    }


    public void drawPolygon(final int[] points, final int[] points2, final int points3)
    {
        // TODO Auto-generated method stub

    }

    public void drawPolyline(final int[] points, final int[] points2, final int points3)
    {
        // TODO Auto-generated method stub

    }
    
    public void drawPolyline(final int[] points)
    {        
    }    

    public void drawRect(final int x1, final int y1, final int wid, final int height)
    {
        // TODO Auto-generated method stub

    }

    public void drawText(final String str, final int x, final int y)
    {
        // TODO Auto-generated method stub

    }

    public void drawText(final Font theFont, final String theStr, final int x, final int y)
    {
        // TODO Auto-generated method stub

    }

    public void endDraw(final Object theVal)
    {
        // TODO Auto-generated method stub

    }

    public void fillArc(final int x, final int y, final int width, final int height, final int startAngle,
            final int arcAngle)
    {
        // TODO Auto-generated method stub

    }
    
    public void fillArc(final int x, final int y, final int width, final int height, final int startAngle,
            final int arcAngle, final int alpha)
    {
        // TODO Auto-generated method stub

    }

    public void fillOval(final int x, final int y, final int width, final int height)
    {
        // TODO Auto-generated method stub

    }

    public void fillPolygon(final int[] points, final int[] points2, final int points3)
    {
        // TODO Auto-generated method stub

    }


    public void fillRect(final int x, final int y, final int wid, final int height)
    {
        // TODO Auto-generated method stub

    }


    public Color getBackgroundColor()
    {
        // TODO Auto-generated method stub
        return null;
    }


    public Graphics getGraphicsTemp()
    {
        // TODO Auto-generated method stub
        return null;
    }


    public float getLineWidth()
    {
        // TODO Auto-generated method stub
        return 0;
    }


    public Enumeration<PaintListener> getPainters()
    {
        // TODO Auto-generated method stub
        return null;
    }


    public PlainProjection getProjection()
    {
        // TODO Auto-generated method stub
        return null;
    }


    public Dimension getSize()
    {
        // TODO Auto-generated method stub
        return null;
    }


    public int getStringHeight(final Font theFont)
    {
        // TODO Auto-generated method stub
        return 0;
    }


    public int getStringWidth(final Font theFont, final String theString)
    {
        // TODO Auto-generated method stub
        return 0;
    }


    public void removePainter(final PaintListener listener)
    {
        // TODO Auto-generated method stub

    }


    public void rescale()
    {
        // TODO Auto-generated method stub

    }


    public void setBackgroundColor(final Color theColor)
    {
        // TODO Auto-generated method stub

    }


    public void setColor(final Color theCol)
    {
        // TODO Auto-generated method stub

    }


    public void setLineStyle(final int style)
    {
        // TODO Auto-generated method stub

    }


    public void setLineWidth(final float width)
    {
        // TODO Auto-generated method stub

    }


    public void setProjection(final PlainProjection val)
    {
        // TODO Auto-generated method stub

    }


    public void setTooltipHandler(final TooltipHandler handler)
    {
        // TODO Auto-generated method stub

    }


    public void startDraw(final Object theVal)
    {
        // TODO Auto-generated method stub

    }


    public Point toScreen(final WorldLocation val)
    {
        return new Point(1,2);
    }


    public WorldLocation toWorld(final Point val)
    {
        // TODO Auto-generated method stub
        return null;
    }


    public void updateMe()
    {
        // TODO Auto-generated method stub

    }

	@Override
	public void drawText(final String str, final int x, final int y, final float rotate) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setFont(final Font theFont)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void drawText(String str, int x, int y, float rotate, boolean above)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void semiFillPolygon(int[] xPoints, int[] yPoints, int nPoints) 
	{
		// TODO Auto-generated method stub
		
	}

}
