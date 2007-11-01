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

    @Override
    public void addPainter(PaintListener listener)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void drawArc(int x, int y, int width, int height, int startAngle,
            int arcAngle)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean drawImage(Image img, int x, int y, int width, int height,
            ImageObserver observer)
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void drawLine(int startX, int startY, int endX, int endY)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void drawOval(int x, int y, int width, int height)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void drawPolygon(int[] points, int[] points2, int points3)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void drawPolyline(int[] points, int[] points2, int points3)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void drawRect(int x1, int y1, int wid, int height)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void drawText(String str, int x, int y)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void drawText(Font theFont, String theStr, int x, int y)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void endDraw(Object theVal)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void fillArc(int x, int y, int width, int height, int startAngle,
            int arcAngle)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void fillOval(int x, int y, int width, int height)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void fillPolygon(int[] points, int[] points2, int points3)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void fillRect(int x, int y, int wid, int height)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public Color getBackgroundColor()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Graphics getGraphicsTemp()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public float getLineWidth()
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public Enumeration<PaintListener> getPainters()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public PlainProjection getProjection()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Dimension getSize()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getStringHeight(Font theFont)
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getStringWidth(Font theFont, String theString)
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void removePainter(PaintListener listener)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void rescale()
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void setBackgroundColor(Color theColor)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void setColor(Color theCol)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void setLineStyle(int style)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void setLineWidth(float width)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void setProjection(PlainProjection val)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void setTooltipHandler(TooltipHandler handler)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void startDraw(Object theVal)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public Point toScreen(WorldLocation val)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public WorldLocation toWorld(Point val)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void updateMe()
    {
        // TODO Auto-generated method stub

    }

}
