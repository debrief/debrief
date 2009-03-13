/*****************************************************************************
 *                        J3D.org Copyright (c) 2001
 *                               Java Source
 *
 * This source is licensed under the GNU LGPL v2.1
 * Please read http://www.gnu.org/copyleft/lgpl.html for more information
 *
 ****************************************************************************/

package org.j3d.geom.overlay;

// Standard imports
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.media.j3d.Canvas3D;

// Application specific imports
// none


/**
 * An overlay allows the user to scribble on the screen, over the top of the
 * 3D display.
 * <P>
 *
 * @author Justin Couch
 * @version $Revision: 1.1.1.1 $
 */
public class ScribbleOverlay extends MouseOverlay
{
    /**
     * A mapping of the color value to lines drawn in that color. Each element
     * of the list is an instance of LineDetails. The line is in drawing order
     * so that we may have a set of LineDetails with the same color more than
     * once in the list.
     */
    private ArrayList<LineDetails> lineList;

    /** The current colour that we are drawing new lines in */
    private Color currentColor;

    /** The current line that we are adding points to */
    private LineData currentLine;

    /** The current set of lines that match the current color */
    private LineDetails currentLineDetails;

    /**
     * Create a new scribble overlay that bases its size on the canvas it
     * overlays. The default line colour is white. If the bounds are null,
     * the overlay works for the entire canvas.
     *
     * @param canvas The canvas that is scribbled on
     * @param bounds The bounds of the canvas to draw on or null
     */
    public ScribbleOverlay(Canvas3D canvas, Rectangle bounds)
    {
        this(canvas, bounds, Color.white);
    }

    /**
     * Create a new scribble overlay using the given line color. If the bounds
     * are null, the overlay works for the entire canvas.
     *
     * @param canvas The canvas that is scribbled on
     * @param bounds The bounds of the canvas to draw on or null
     * @param lineColor The colour to start the first line with
     */
    public ScribbleOverlay(Canvas3D canvas,
                           Rectangle bounds,
                           Color lineColor)
    {
        super(canvas, bounds, true, true, true, true, true);

        lineList = new ArrayList<LineDetails>();
        new Point();

        currentColor = new Color(lineColor.getRed(),
                                 lineColor.getGreen(),
                                 lineColor.getBlue(),
                                 lineColor.getAlpha());

        currentLineDetails = new LineDetails(currentColor);
        lineList.add(currentLineDetails);
    }


    /**
     * Paint the overlay with the given graphics context. All lines are drawn in
     * their alloted colors.
     *
     * @param g The graphics context to paint with
     */
    public void paint(Graphics2D g)
    {
        int i, j;
        int color_total = lineList.size();
        int lines_total;

        for(i = 0; i < color_total; i++)
        {
            LineDetails details = (LineDetails)lineList.get(i);
            lines_total = details.lines.size();

            g.setColor(details.color);
            for(j = 0; j < lines_total; j++)
            {
                LineData data = (LineData)details.lines.get(j);
                g.drawPolyline(data.xPoints, data.yPoints, data.numPoints);
            }
        }
    }

    //------------------------------------------------------------------------
    // Methods for MouseListener events
    //------------------------------------------------------------------------

    /**
     * Process a mouse press event to start a new line in the given color.
     *
     * @param evt The event that caused this method to be called
     */
    public void mousePressed(MouseEvent evt)
    {
        // Create a new line and put these points in as the start
        currentLine = currentLineDetails.addLine();
        Point pt = evt.getPoint();

        currentLine.addPoint(pt.x, pt.y);
    }

    /**
     * Process a mouse release event.
     *
     * @param evt The event that caused this method to be called
     */
    public void mouseReleased(MouseEvent evt)
    {
        Point pt = evt.getPoint();

        currentLine.addPoint(pt.x, pt.y);

        currentLine = null;
        repaint();
    }

    //------------------------------------------------------------------------
    // Methods for MouseMotionListener events
    //------------------------------------------------------------------------

    /**
     * Process a mouse drag event. Adds another segment to the line.
     *
     * @param evt The event that caused this method to be called
     */
    public void mouseDragged(MouseEvent evt)
    {
        Point pt = evt.getPoint();

        currentLine.addPoint(pt.x, pt.y);
    }

    //------------------------------------------------------------------------
    // Local methods
    //------------------------------------------------------------------------

    /**
     * Set the line colour that any new lines will be drawn in. All currently
     * held lines will maintain their alloted color. A value of null will
     * throw an exception.
     *
     * @param color The new color to use
     */
    public void setLineColor(Color color)
    {
        currentColor = new Color(color.getRed(),
                                 color.getGreen(),
                                 color.getBlue(),
                                 color.getAlpha());

        currentLineDetails = new LineDetails(currentColor);
        lineList.add(currentLineDetails);
    }

    /**
     * Clear the current screen. This removes all of the current lines from the
     * active drawn list.
     */
    public void clear()
    {
        lineList.clear();

        currentLineDetails = new LineDetails(currentColor);
        lineList.add(currentLineDetails);
        repaint();
    }
}