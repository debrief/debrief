// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: TextLabel.java,v $
// @author $Author: ian.mayo $
// @version $Revision: 1.11 $
// $Log: TextLabel.java,v $
// Revision 1.11  2007/03/12 11:36:15  ian.mayo
// Shrink default font size to 9 pixels
//
// Revision 1.10  2007/01/15 15:51:54  ian.mayo
// Increase line separation, improve location of center location
//
// Revision 1.9  2006/11/28 10:49:05  Ian.Mayo
// Improved label locations (all appear to be working)
//
// Revision 1.8  2006/11/28 10:36:40  Ian.Mayo
// Left label locations working fine.
//
// Revision 1.7  2006/11/28 08:37:42  Ian.Mayo
// Switch to modern line-break process, minor improvements to object layout
//
// Revision 1.6  2006/11/16 08:40:05  Ian.Mayo
// Improve text-positioning algorithm
//
// Revision 1.5  2006/11/13 12:21:28  Ian.Mayo
// Adjust offsets (for SWT)
//
// Revision 1.4  2006/04/21 07:48:38  Ian.Mayo
// Make things draggable
//
// Revision 1.3  2005/05/25 08:38:50  Ian.Mayo
// Minor tidying from Eclipse
//
// Revision 1.2  2004/05/25 15:37:20  Ian.Mayo
// Commit updates from home
//
// Revision 1.1.1.1  2004/03/04 20:31:22  ian
// no message
//
// Revision 1.1.1.1  2003/07/17 10:07:34  Ian.Mayo
// Initial import
//
// Revision 1.11  2003-07-03 14:59:50+01  ian_mayo
// Reflect new signature of PlainShape constructor, where we don't need to set the default colour
//
// Revision 1.10  2003-06-25 08:50:59+01  ian_mayo
// Only plot if we are visible
//
// Revision 1.9  2003-06-04 10:19:08+01  ian_mayo
// Make newline marker public
//
// Revision 1.8  2003-05-30 11:14:20+01  ian_mayo
// Correctly handled single-line labels
//
// Revision 1.7  2003-05-30 10:59:32+01  ian_mayo
// Multi-line editing complete
//
// Revision 1.6  2003-05-30 09:50:08+01  ian_mayo
// Part way through implementation.  Splitting into lines, but need to start lining them up properly
//
// Revision 1.5  2003-03-14 16:01:04+00  ian_mayo
// Use a single central plain font instead of creating one for each label
//
// Revision 1.4  2003-02-07 15:36:47+00  ian_mayo
// Implement unused "Get data points" method from parent class
//
// Revision 1.3  2002-12-16 15:22:44+00  ian_mayo
// Use the font, if we have one
//
// Revision 1.2  2002-05-28 09:25:51+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:14:24+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-11 14:01:09+01  ian_mayo
// Initial revision
//
// Revision 1.3  2002-03-19 11:04:04+00  administrator
// Add a "type" property to indicate type of shape (label, rectangle, etc)
//
// Revision 1.2  2002-03-13 19:39:18+00  administrator
// Only draw if we are visible
//
// Revision 1.1  2002-02-25 13:20:15+00  administrator
// Provide means of not fixing Anchor point at initial creation
//
// Revision 1.0  2001-07-17 08:43:16+01  administrator
// Initial revision
//
// Revision 1.1  2001-01-03 13:42:21+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 21:49:19  ianmayo
// initial version
//
// Revision 1.7  2000-11-17 08:49:52+00  ian_mayo
// tidying up
//
// Revision 1.6  2000-08-07 12:22:27+01  ian_mayo
// white space only
//
// Revision 1.5  2000-05-23 13:37:43+01  ian_mayo
// switch to Arial format
//
// Revision 1.4  2000-04-19 11:41:34+01  ian_mayo
// initialise font parameter
//
// Revision 1.3  1999-10-21 10:19:52+01  ian_mayo
// inserted comments & handling of CENTRE location
//
// Revision 1.2  1999-10-14 11:59:20+01  ian_mayo
// added property support and location editing
//
// Revision 1.1  1999-10-12 15:36:39+01  ian_mayo
// Initial revision
//
// Revision 1.2  1999-07-27 12:08:05+01  administrator
// changed font setting
//
// Revision 1.1  1999-07-27 10:50:39+01  administrator
// Initial revision
//
// Revision 1.4  1999-07-23 14:03:48+01  administrator
// Updating MWC utilities, & catching up on changes (removed deprecated code from PtPlot)
//
// Revision 1.3  1999-07-19 12:39:41+01  administrator
// Added painting to a metafile
//
// Revision 1.2  1999-07-16 10:01:44+01  administrator
// Nearing end of phase 2
//
// Revision 1.1  1999-07-07 11:10:05+01  administrator
// Initial revision
//
// Revision 1.1  1999-06-16 15:37:58+01  sm11td
// Initial revision
//
// Revision 1.1  1999-02-04 08:02:32+00  sm11td
// Initial revision
//
// Revision 1.2  1999-02-01 16:08:46+00  sm11td
// creating new sessions & panes, starting import management
//
// Revision 1.1  1999-01-31 13:33:03+00  sm11td
// Initial revision
//

package MWC.GUI.Shapes;

import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.util.Collection;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.JLabel;

import MWC.GUI.CanvasType;
import MWC.GUI.Editable;
import MWC.GUI.Properties.LocationPropertyEditor;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldVector;

/**
 * This class represents a line of text located in geographic coordinates. The
 * user is able to edit the Color of the label through the inherited PlainShape
 * functionality, and is able to edit the font, the text itself, and the
 * relative location through this property The label may be positioned relative
 * to a precise coordinate, or may be positioned relative to a shape -->
 * consequently when the label is drawn in a relative position, it is drawn
 * relative to that part of the shape (you get it really).
 */
public class TextLabel extends PlainShape implements Editable
{

	// ////////////////////////////////////////////////
	// member variables
	// ////////////////////////////////////////////////

	/**
	 * keep track of versions
	 */
	static final long serialVersionUID = 1;

	/**
	 * the fond to use
	 */
	private Font _theFont;

	/**
	 * the string to display
	 */
	private String _theString;

	/**
	 * the origin of this label
	 */
	private WorldLocation _theLocation;

	/**
	 * the location of the label, in relation to some fixed point.
	 * 
	 * @see MWC.GUI.Properties.LocationPropertyEditor for more details
	 */
	private int _theRelativeLocation;

	/**
	 * a fixed offset to apply to the text - this allows a "no go" area around the
	 * text anchor, such as when we use a symbol
	 */
	private java.awt.Dimension _theFixedOffset = null;

	/**
	 * the shape which provides us with it's dynamic anchor point
	 */
	private PlainShape _theShape;

	/**
	 * the plain font we use as a base
	 */
	static private final Font _plainFont = new Font("Sans Serif", Font.PLAIN, 9);

	/**
	 * the newline character combination to use
	 */
	public static final String NEWLINE_MARKER = "\n";

	/**
	 * the width of the string, the last time it was calculated
	 */
	private int _myWidth = UNKNOWN_WIDTH;

	/**
	 * keep an invalid value - so we know when it's unset
	 */
	static private final int UNKNOWN_WIDTH = -1;

	// ////////////////////////////////////////////////
	// constructor
	// ////////////////////////////////////////////////

	/**
	 * constructor which is used when we have a single, fixed centre
	 */
	public TextLabel(WorldLocation theLocation, String theString)
	{
		super(0, 1, "Text");

		_theString = theString;
		_theLocation = theLocation;
		_theRelativeLocation = LocationPropertyEditor.LEFT;
		_theFont = _plainFont;
		_theFixedOffset = new java.awt.Dimension(0, 0);
	}

	/**
	 * constructor which is used when we the shape may want to update it's anchor
	 * point
	 */
	public TextLabel(PlainShape theShape, String theString)
	{
		super(0, 1, "Text");

		_theString = theString;
		_theShape = theShape;
		_theRelativeLocation = LocationPropertyEditor.LEFT;
		_theFont = new Font("Sans Serif", 12, 12);
		_theFixedOffset = new java.awt.Dimension(0, 0);
	}

	// public TextLabel()
	// {
	// super(0, Color.red, 1);
	//
	// _theRelativeLocation = LocationPropertyEditor.LEFT;
	// _theFont = new Font("Sans Serif", 12, 12);
	// _theFixedOffset = new java.awt.Dimension(0,0);
	// }

	// ////////////////////////////////////////////////
	// member functions
	// ////////////////////////////////////////////////

	public void setRelativeLocation(Integer val)
	{
		_theRelativeLocation = val.intValue();
	}

	public Integer getRelativeLocation()
	{
		return new Integer(_theRelativeLocation);
	}

	public void setString(String theString)
	{
		_theString = theString;

		// clear our string width cached value
		_myWidth = UNKNOWN_WIDTH;
	}

	public String getString()
	{
		return _theString;
	}

	public Font getFont()
	{
		return _theFont;
	}

	public void setFont(Font theFont)
	{
		_theFont = theFont;
	}

	private static int getNumberOfLinesIn(String baseLine)
	{
		int _numLines = 1;
		int start = 0;
		int newlineAt;

		// break it up into a Vector of substrings,
		// one substring for each line.

		while ((newlineAt = baseLine.indexOf(NEWLINE_MARKER, start)) > 0)
		{
			_numLines++;
			start = newlineAt + 2;
		}

		return _numLines;
	}

	private static String getLongestLineIn(String baseLine)
	{
		String longestLine = null;

		String sub;

		// break it up into a Vector of substrings,
		// one substring for each line.
		StringTokenizer t = new StringTokenizer(baseLine, "\n");
		int num_lines = t.countTokens();
		for (int i = 0; i < num_lines; i++)
		{
			sub = t.nextToken().trim();
			if (longestLine == null)
			{
				longestLine = sub;
			}
			else
			{
				if (sub.length() > longestLine.length())
				{
					longestLine = sub;
				}
			}
		}

		return longestLine;

	}

	/**
	 * support class which handles plotting a multi-line label
	 * 
	 * @param dest
	 * @param theStr
	 * @param theFont
	 */
	private void paintMultiLine(CanvasType dest, String theStr, Font theFont, Point thePoint)
	{
		int _numLines = 0;
		Vector<String> _subStrings = new Vector<String>();

		// break it up into a Vector of substrings,
		// one substring for each line.

		// break it up into a Vector of substrings,
		// one substring for each line.
		StringTokenizer t = new StringTokenizer(theStr, "\n");
		_numLines = t.countTokens();
		for (int i = 0; i < _numLines; i++)
		{
			_subStrings.add(t.nextToken());
//	Note: we used to do a 'trim' of the text to ditch any 
//        stray chars. We don't do it now, so users can use padding spaces			
//			_subStrings.add(t.nextToken().trim());
		}
		
		int lineHeight = dest.getStringHeight(getFont());

		// SWT - increase the gap between the lines
		// add 10% line spacing
		if(_numLines > 1)
			lineHeight = (int) (lineHeight * 1.3d);

		int ypos = thePoint.y;
		int xpos = thePoint.x;

		int HorizonalAlignment = JLabel.CENTER;

		switch (HorizonalAlignment)
		{
		case JLabel.CENTER:
		{
			// Using the size of the component (based on the size
			// of the largest line), figure out how to adjust each
			// line so that the text looks centered
			if (_numLines > 1)
			{
				for (int i = 0; i < _numLines; i++)
				{
					String subString = (String) _subStrings.elementAt(i);

					// Calculate the width of this portion of the string
					// and use it to figure out the left-right centering.
					int lineWidth = dest.getStringWidth(getFont(), subString);
//					System.out.println("wid of|" + subString + "| is:" + lineWidth);

					xpos = thePoint.x;// - (lineWidth) / 2;
					xpos += (_myWidth - lineWidth) / 2;

					dest.drawText(theFont, subString, xpos, ypos);
					ypos += lineHeight;
				}
			}
			else
			// single line of text
			{
				xpos = thePoint.x;// - (lineWidth) / 2;
				
				// move to the right, by this 1/2 of the distance between
				// this widht and the longest width

				dest.drawText(theFont, theStr, xpos, ypos);
			}
			break;
		}

		case JLabel.RIGHT:
		{
			// Just set xpos = 0 for each line
			if (_numLines > 1)
			{
				for (int i = 0; i < _numLines; i++)
				{
					String subString = (String) _subStrings.elementAt(i);
					dest.drawText(subString, 0, ypos);
					ypos += lineHeight;
				}
			}
			else
			{
				dest.drawText(theStr, 0, ypos);
			}
			break;
		}

		case JLabel.LEFT:
		{
			// Using the size of the component, adjust each line
			// to the right so that the ends of them all line up

			if (_numLines > 1)
			{
					for (int i = 0; i < _numLines; i++)
				{
					String subString = (String) _subStrings.elementAt(i);

					// trim off the white-space
					subString = subString.trim();

					// Calculate the width of this portion of the string
					// and adjust its X position accordingly.
					// int lineWidth = dest.getStringWidth(getFont(), subString);
					// xpos = labelWidth - insets.right - lineWidth;

					dest.drawText(subString, xpos, ypos);
					ypos += lineHeight;
				}
			}
			else
			{
				// xpos = labelWidth - insets.right - _maxLineWidth;
				dest.drawText(theStr, xpos, ypos);
			}
			break;
		} // case RIGHT:
		}

	}

	public void paint(CanvasType dest)
	{
		// check if we're visible
		if (!getVisible())
			return;

		if(getString().trim().length()==0)
			return;
		
		// get an updated centre, if we want to!
		if (_theShape != null)
		{
			_theLocation = _theShape.getAnchor(_theRelativeLocation);
		}

		// convert the location
		java.awt.Point theOrigin = dest.toScreen(_theLocation);

		// sort out the color
		Color myColor = getColor();
		dest.setColor(myColor);

		// determine the height of the font in screen coordinates
		int lineHeight = dest.getStringHeight(getFont());
		int numLines = getNumberOfLinesIn(getString());
		int blockHeight = lineHeight * numLines;
		blockHeight += (int) (((double)lineHeight * 0.2d ) * (numLines - 1));
		String longestLine = getLongestLineIn(getString().trim());

		// note, we cache the string width, to reduce computation
		// if(_myWidth == UNKNOWN_WIDTH)
		_myWidth = dest.getStringWidth(getFont(), longestLine);

		Point offset = getOffset(_myWidth, lineHeight, blockHeight);

		// offset the central location
		theOrigin.translate(offset.x, offset.y);

		if (_theFont != null)
		{
			this.paintMultiLine(dest, _theString, _theFont, theOrigin);
		}
		else
		{
			MWC.Utilities.Errors.Trace.trace("Problem with painting label - font not found.");
		}

	}

	// allow an external class to set a border around the anchor
	public void setFixedOffset(java.awt.Dimension offset)
	{
		_theFixedOffset = offset;
	}

	/**
	 * get the shape as a series of WorldLocation points. Joined up, these form a
	 * representation of the shape
	 */
	public Collection<WorldLocation> getDataPoints()
	{
		return null;
	}

	/**
	 * calculate the offset to use to put us the indicated height and width away
	 * from the anchor point
	 */
	public Point getOffset(int wid, int lineHeight, int blockHeight)
	{
		Point res = null;

//		System.out.println("for text:" + getString() +  " width is:" + wid + " line ht is:" + lineHeight + " block ht is:" + blockHeight + " offset ht:" + _theFixedOffset.height);

		int verticalBalance = lineHeight - 3;
		int horizBalance = 2;

		// where are we to be positioned
		switch (_theRelativeLocation)
		{
		case LocationPropertyEditor.LEFT:
			res = new Point(-(wid + horizBalance), -(blockHeight/2));
//			res = new Point(-(wid + horizBalance), verticalBalance);
			res.translate(-_theFixedOffset.width / 2,   verticalBalance);
			break;
		case LocationPropertyEditor.RIGHT:
//			res = new Point(wid/2 + horizBalance, -(blockHeight / 2));
			res = new Point(horizBalance, -(blockHeight / 2));
			res.translate(_theFixedOffset.width / 2,  verticalBalance);
			break;
		case LocationPropertyEditor.TOP:
			res = new Point(-wid / 2 - horizBalance / 2, -(blockHeight - lineHeight));
			res.translate(0,  -_theFixedOffset.height - verticalBalance );
			break;
		case LocationPropertyEditor.BOTTOM:
			res = new Point(-wid / 2 - horizBalance / 2, 2 * lineHeight);
			res.translate(0, _theFixedOffset.height - verticalBalance);
			break;
		case LocationPropertyEditor.CENTRE:
			res = new Point(-wid / 2, -(blockHeight / 2));
//			res = new Point(-wid / 2, -(blockHeight / 2) - lineHeight);
//			res = new Point(0, -(blockHeight / 2) - lineHeight);
			res.translate(0, _theFixedOffset.height + verticalBalance);
			break;
		}

//		System.out.println("res is:" + res);
		
		return res;
	}

	public void setLocation(MWC.GenericData.WorldLocation loc)
	{
		_theLocation = loc;
	}

	public WorldLocation getLocation()
	{
		return _theLocation;
	}

	public MWC.GenericData.WorldArea getBounds()
	{
		return new MWC.GenericData.WorldArea(_theLocation, _theLocation);
	}

	/**
	 * get the range from the indicated world location - making this abstract
	 * allows for individual shapes to have 'hit-spots' in various locations.
	 */
	public double rangeFrom(WorldLocation point)
	{
		return _theLocation.rangeFrom(point);
	}

	public boolean hasEditor()
	{
		return false;
	}

	public Editable.EditorType getInfo()
	{
		return null;
	}

	/**
	 * get the 'anchor point' for any labels attached to this shape
	 */
	public MWC.GenericData.WorldLocation getAnchor()
	{
		return _theLocation;
	}

	/**
	 * ok, shift this shape
	 * 
	 * @param vector
	 */
	public void shift(WorldVector vector)
	{
		setLocation(getLocation().add(vector));
	}

}
