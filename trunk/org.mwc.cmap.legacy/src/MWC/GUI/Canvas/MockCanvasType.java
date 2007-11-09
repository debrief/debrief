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

    public void addPainter(PaintListener listener)
    {
        // TODO Auto-generated method stub

    }

    public void drawArc(int x, int y, int width, int height, int startAngle,
            int arcAngle)
    {
        // TODO Auto-generated method stub

    }

    public boolean drawImage(Image img, int x, int y, int width, int height,
            ImageObserver observer)
    {
        // TODO Auto-generated method stub
        return false;
    }

    public void drawLine(int startX, int startY, int endX, int endY)
    {
        // TODO Auto-generated method stub

    }

    public void drawOval(int x, int y, int width, int height)
    {
        // TODO Auto-generated method stub

    }


    public void drawPolygon(int[] points, int[] points2, int points3)
    {
        // TODO Auto-generated method stub

    }

    public void drawPolyline(int[] points, int[] points2, int points3)
    {
        // TODO Auto-generated method stub

    }
    
    public void drawPolyline(int[] points)
    {        
    }    

    public void drawRect(int x1, int y1, int wid, int height)
    {
        // TODO Auto-generated method stub

    }

    public void drawText(String str, int x, int y)
    {
        // TODO Auto-generated method stub

    }

    public void drawText(Font theFont, String theStr, int x, int y)
    {
        // TODO Auto-generated method stub

    }

    public void endDraw(Object theVal)
    {
        // TODO Auto-generated method stub

    }

    public void fillArc(int x, int y, int width, int height, int startAngle,
            int arcAngle)
    {
        // TODO Auto-generated method stub

    }

    public void fillOval(int x, int y, int width, int height)
    {
        // TODO Auto-generated method stub

    }

    public void fillPolygon(int[] points, int[] points2, int points3)
    {
        // TODO Auto-generated method stub

    }


    public void fillRect(int x, int y, int wid, int height)
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


    public int getStringHeight(Font theFont)
    {
        // TODO Auto-generated method stub
        return 0;
    }


    public int getStringWidth(Font theFont, String theString)
    {
        // TODO Auto-generated method stub
        return 0;
    }


    public void removePainter(PaintListener listener)
    {
        // TODO Auto-generated method stub

    }


    public void rescale()
    {
        // TODO Auto-generated method stub

    }


    public void setBackgroundColor(Color theColor)
    {
        // TODO Auto-generated method stub

    }


    public void setColor(Color theCol)
    {
        // TODO Auto-generated method stub

    }


    public void setLineStyle(int style)
    {
        // TODO Auto-generated method stub

    }


    public void setLineWidth(float width)
    {
        // TODO Auto-generated method stub

    }


    public void setProjection(PlainProjection val)
    {
        // TODO Auto-generated method stub

    }


    public void setTooltipHandler(TooltipHandler handler)
    {
        // TODO Auto-generated method stub

    }


    public void startDraw(Object theVal)
    {
        // TODO Auto-generated method stub

    }


    public Point toScreen(WorldLocation val)
    {
        return new Point(1,2);
    }


    public WorldLocation toWorld(Point val)
    {
        // TODO Auto-generated method stub
        return null;
    }


    public void updateMe()
    {
        // TODO Auto-generated method stub

    }

}
