/*****************************************************************************
 *                 Teseract Software, LLP Copyright(c)2001
 *                               Java Source
 *
 * This source is licensed under the GNU LGPL v2.1
 * Please read http://www.gnu.org/copyleft/lgpl.html for more information
 *
 ****************************************************************************/

package org.j3d.geom.overlay;

// Standard imports
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.font.TextAttribute;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;

import javax.media.j3d.Canvas3D;

// Application specific imports
// none

/**
 * An overlay that renders a text label.
 * <P>
 *
 * The text is placed with the baseline at 3/4 of the height of the label.
 *
 * @author David Yazel, Justin Couch
 * @version $Revision: 1.1.1.1 $
 */
public class LabelOverlay extends OverlayBase
{
    /** If the user doesn't supply a font, use this one */
    private static final Font DEFAULT_FONT =
        new Font("Helvetica", Font.PLAIN, 14);

    /** If the user does not supply a colour, use this one */
    private static final Color DEFAULT_COLOR = Color.red;

    /** The string we are using to draw the text with */
    private AttributedString text;

    /** The number of characters the user wants to be painted */
    private int visibleLength;

    /** The font to render the text in */
    private Font font;

    /** The colour to use to render the font with */
    private Color color;

    /**
     * Create a new, simple label overlay that does not contain any text.
     *
     * @param canvas The canvas for this overlay to live on
     * @param space The area of the canvas (in screen coords) for the label
     */
    public LabelOverlay(Canvas3D canvas, Rectangle space)
    {
        this(canvas, space, "");
    }

    /**
     * Create a label overlay that displays the given text on the given
     * screen space.
     *
     * @param canvas The canvas for this overlay to live on
     * @param space The area of the canvas (in screen coords) for the label
     */
    public LabelOverlay(Canvas3D canvas, Rectangle space, String text)
    {
        this(canvas, space, text, DEFAULT_FONT, DEFAULT_COLOR, null);
    }

    /**
     * Create a customised label overlay that uses the given attributes of
     * font and colour styles.
     *
     * @param canvas The canvas for this overlay to live on
     * @param space The area of the canvas (in screen coords) for the label
     */
    public LabelOverlay(Canvas3D canvas,
                        Rectangle space,
                        String str,
                        Font font,
                        Color color) {
        this(canvas, space, str, font, color, null);
    }

    /**
     * Create a customised label overlay that includes a specialised update
     * manager to control when items are updated.
     *
     * @param canvas The canvas for this overlay to live on
     * @param space The area of the canvas (in screen coords) for the label
     */
    public LabelOverlay(Canvas3D canvas,
                        Rectangle space,
                        String str,
                        Font font,
                        Color color,
                        UpdateManager manager)
    {
        super(canvas, space, manager);
        this.font = font;
        this.color = color;
        setText(str);

        visibleLength = str.length();
    }

    /**
     * Create a new label overlay that uses the given text with attributes
     * for rendering.
     *
     * @param canvas The canvas for this overlay to live on
     * @param space The area of the canvas (in screen coords) for the label
     */
    public LabelOverlay(Canvas3D canvas, Rectangle space, AttributedString text)
    {
        this(canvas, space, text, (UpdateManager)null);
    }

    /**
     *
     *
     * @param canvas The canvas for this overlay to live on
     * @param space The area of the canvas (in screen coords) for the label
     */
    public LabelOverlay(Canvas3D canvas,
                        Rectangle space,
                        AttributedString text,
                        UpdateManager manager)
    {
        super(canvas, space, manager);
        setText(text);
        setBackgroundColor(new Color(0, 0, 0, 0));

        visibleLength = text.getIterator().getEndIndex();
    }

    public void paint(Graphics2D g)
    {
        if(text == null)
            return;

        synchronized(text)
        {
            AttributedCharacterIterator char_itr =
                text.getIterator(null, 0, visibleLength);

            if(char_itr.getEndIndex() > 0)
            {
                g.setColor(color);
                g.drawString(char_itr, 0, (overlayBounds.height * 3) / 4);
            }
        }
    }

    public void setColor(Color c)
    {
        if(!color.equals(c))
        {
            color = c;
            if(text != null)
            {
                text.addAttribute(TextAttribute.FOREGROUND, color);
                repaint();
            }
        }
    }

    public void setFont(Font f)
    {
        if(!font.equals(f))
        {
            font = f;
            if(text != null)
            {
                text.addAttribute(TextAttribute.FONT, font);
                repaint();
            }
        }
    }

    /**
     * Set the text to the new string. The visible length does not change so if
     * you want to change the length, you should reset the visible length too
     *
     * @param str The new string to use
     */
    public void setText(String str)
    {
        if(str != null)
            setText(createAttributedString(str, font, color));
    }

    /**
     * Set the text to the new string. The visible length does not change so if
     * you want to change the length, you should reset the visible length too.
     * If the param reference is null, the request is ignored. To remove a string
     * from being rendered, set the visible length to zero.
     *
     * @param text The new string to use
     */
    public void setText(AttributedString text)
    {
        if(text != null)
            this.text = text;
    }

    /**
     * Get the number of characters that are rendered from the given string.
     *
     * @return The current number of visble characters
     */
    public int getVisibleLength()
    {
        return visibleLength;
    }

    /**
     * Set the number of visible characters to the given size. A size of zero
     * effectively stops the string being rendered.
     *
     * @param length The number of characters to be shown
     */
    public void setVisibleLength(int length)
    {
        visibleLength = (length < 0) ? 0 : length;
    }

    private AttributedString createAttributedString(String text1,
                                                    Font font1,
                                                    Color color1)
    {
        AttributedString attributedString = null;
        if(text1.length() > 0)
        {
            attributedString = new AttributedString(text1);
            attributedString.addAttribute(TextAttribute.FONT, font1);
            attributedString.addAttribute(TextAttribute.FOREGROUND, color1);
        }

        return attributedString;
    }
}
