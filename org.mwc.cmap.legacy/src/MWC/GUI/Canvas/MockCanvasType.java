/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
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

    }

    public void drawArc(final int x, final int y, final int width, final int height, final int startAngle,
            final int arcAngle)
    {

    }

    public boolean drawImage(final Image img, final int x, final int y, final int width, final int height,
            final ImageObserver observer)
    {
        return false;
    }

    public void drawLine(final int startX, final int startY, final int endX, final int endY)
    {

    }

    public void drawOval(final int x, final int y, final int width, final int height)
    {

    }


    public void drawPolygon(final int[] points, final int[] points2, final int points3)
    {

    }

    public void drawPolyline(final int[] points, final int[] points2, final int points3)
    {

    }
    
    public void drawPolyline(final int[] points)
    {        
    }    

    public void drawRect(final int x1, final int y1, final int wid, final int height)
    {

    }

    public void drawText(final String str, final int x, final int y)
    {

    }

    public void drawText(final Font theFont, final String theStr, final int x, final int y)
    {

    }

    public void endDraw(final Object theVal)
    {

    }

    public void fillArc(final int x, final int y, final int width, final int height, final int startAngle,
            final int arcAngle)
    {

    }
    
    public void fillArc(final int x, final int y, final int width, final int height, final int startAngle,
            final int arcAngle, final int alpha)
    {

    }

    public void fillOval(final int x, final int y, final int width, final int height)
    {

    }

    public void fillPolygon(final int[] points, final int[] points2, final int points3)
    {

    }


    public void fillRect(final int x, final int y, final int wid, final int height)
    {

    }


    public Color getBackgroundColor()
    {
        return null;
    }


    public Graphics getGraphicsTemp()
    {
        return null;
    }


    public float getLineWidth()
    {
        return 0;
    }


    public Enumeration<PaintListener> getPainters()
    {
        return null;
    }


    public PlainProjection getProjection()
    {
        return null;
    }


    public Dimension getSize()
    {
        return null;
    }


    public int getStringHeight(final Font theFont)
    {
        return 0;
    }


    public int getStringWidth(final Font theFont, final String theString)
    {
        return 0;
    }


    public void removePainter(final PaintListener listener)
    {

    }


    public void rescale()
    {

    }


    public void setBackgroundColor(final Color theColor)
    {

    }


    public void setColor(final Color theCol)
    {

    }


    public void setLineStyle(final int style)
    {

    }


    public void setLineWidth(final float width)
    {

    }


    public void setProjection(final PlainProjection val)
    {

    }


    public void setTooltipHandler(final TooltipHandler handler)
    {

    }


    public void startDraw(final Object theVal)
    {

    }


    public Point toScreen(final WorldLocation val)
    {
        return new Point(1,2);
    }


    public WorldLocation toWorld(final Point val)
    {
        return null;
    }


    public void updateMe()
    {

    }

	@Override
	public void drawText(final String str, final int x, final int y, final float rotate) {
		
	}

	@Override
	public void setFont(final Font theFont)
	{
		
	}

	@Override
	public void drawText(String str, int x, int y, float rotate, boolean above)
	{
		
	}

}
