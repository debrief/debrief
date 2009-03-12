package MWC.GUI.Shapes;

// This example is from _Java Examples in a Nutshell_. (http://www.oreilly.com)
// Copyright (c) 1997 by David Flanagan
// This example is provided WITHOUT ANY WARRANTY either expressed or implied.
// You may study, use, modify, and distribute it for non-commercial purposes.
// For any commercial use, see http://www.davidflanagan.com/javaexamples

import java.awt.*;
import java.util.*;

/**
 * A custom component that displays multiple lines of text with specified
 * margins and alignment.  In Java 1.1, we could extend Component instead
 * of Canvas, making this a more efficient "Lightweight component"
 */
public class MultiLineLabel extends Canvas {
  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// User-specified attributes
  protected String label;             // The label, not broken into lines
  protected int margin_width;         // Left and right margins
  protected int margin_height;        // Top and bottom margins
  protected int alignment;            // The alignment of the text.
  public static final int LEFT = 0, CENTER = 1, RIGHT = 2; // alignment values
  // Computed state values
  protected int num_lines;            // The number of lines
  protected String[] lines;           // The label, broken into lines
  protected int[] line_widths;        // How wide each line is
  protected int max_width;            // The width of the widest line
  protected int line_height;          // Total height of the font
  protected int line_ascent;          // Font height above baseline
  protected boolean measured = false; // Have the lines been measured?

  // Here are five versions of the constructor
  public MultiLineLabel(String label, int margin_width,
                        int margin_height, int alignment) {
    this.label = label;                 // Remember all the properties
    this.margin_width = margin_width;
    this.margin_height = margin_height;
    this.alignment = alignment;
    newLabel();                         // Break the label up into lines
  }
  public MultiLineLabel(String label, int margin_width, int margin_height) {
    this(label, margin_width, margin_height, LEFT);
  }
  public MultiLineLabel(String label, int alignment) {
    this(label, 10, 10, alignment);
  }
  public MultiLineLabel(String label) { this(label, 10, 10, LEFT); }
  public MultiLineLabel() { this(""); }

  // Methods to set and query the various attributes of the component
  // Note that some query methods are inherited from the superclass.
  public void setLabel(String label) {
    this.label = label;
    newLabel();               // Break the label into lines
    measured = false;         // Note that we need to measure lines
    repaint();                // Request a redraw
  }
  public void setFont(Font f) {
    super.setFont(f);         // tell our superclass about the new font
    measured = false;         // Note that we need to remeasure lines
    repaint();                // Request a redraw
  }
  public void setForeground(Color c) {
    super.setForeground(c);   // tell our superclass about the new color
    repaint();                // Request a redraw (size is unchanged)
  }
  public void setAlignment(int a) { alignment = a; repaint(); }
  public void setMarginWidth(int mw) { margin_width = mw; repaint(); }
  public void setMarginHeight(int mh) { margin_height = mh; repaint(); }
  public String getLabel() { return label; }
  public int getAlignment() { return alignment; }
  public int getMarginWidth() { return margin_width; }
  public int getMarginHeight() { return margin_height; }

  /**
   * This method is called by a layout manager when it wants to
   * know how big we'd like to be.  In Java 1.1, getPreferredSize() is
   * the preferred version of this method.  We use this deprecated version
   * so that this component can interoperate with 1.0 components.
   */
  public Dimension preferredSize() {
    if (!measured) measure();
    return new Dimension(max_width + 2*margin_width,
                         num_lines * line_height + 2*margin_height);
  }

  /**
   * This method is called when the layout manager wants to know
   * the bare minimum amount of space we need to get by.
   * For Java 1.1, we'd use getMinimumSize().
   */
  public Dimension minimumSize() { return preferredSize(); }

  /**
   * This method draws the label (same method that applets use).
   * Note that it handles the margins and the alignment, but that
   * it doesn't have to worry about the color or font--the superclass
   * takes care of setting those in the Graphics object we're passed.
   */
  @SuppressWarnings("deprecation")
	public void paint(Graphics g) {
    int x, y;
    Dimension size = this.size();  // use getSize() in Java 1.1
    if (!measured) measure();
    y = line_ascent + (size.height - num_lines * line_height)/2;
    for(int i = 0; i < num_lines; i++, y += line_height) {
      switch(alignment) {
      default:
      case LEFT:    x = margin_width; break;
      case CENTER:  x = (size.width - line_widths[i])/2; break;
      case RIGHT:   x = size.width - margin_width - line_widths[i]; break;
      }
      g.drawString(lines[i], x, y);
    }
  }

  /** This internal method breaks a specified label up into an array of lines.
   *  It uses the StringTokenizer utility class. */
  protected synchronized void newLabel() {
    StringTokenizer t = new StringTokenizer(label, "\n");
    num_lines = t.countTokens();
    lines = new String[num_lines];
    line_widths = new int[num_lines];
    for(int i = 0; i < num_lines; i++) lines[i] = t.nextToken();
  }

  /** This internal method figures out how the font is, and how wide each
   *  line of the label is, and how wide the widest line is. */
  @SuppressWarnings("deprecation")
	protected synchronized void measure() {
    FontMetrics fm = this.getToolkit().getFontMetrics(this.getFont());
    line_height = fm.getHeight();
    line_ascent = fm.getAscent();
    max_width = 0;
    for(int i = 0; i < num_lines; i++) {
      line_widths[i] = fm.stringWidth(lines[i]);
      if (line_widths[i] > max_width) max_width = line_widths[i];
    }
    measured = true;
  }
}
