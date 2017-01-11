package MWC.GUI.Properties;

import java.awt.Color;

/* custom-designed set of Debrief color shades
 * 
 */
public interface DebriefColors
{
  final public static Color BLACK = new Color(26, 26, 26);
  final public static Color DARK_BLUE = new Color(101, 149, 204);
  final public static Color DARK_GRAY = new Color(192, 192, 192);
  final public static Color MEDIUM_BLUE = new Color(165, 191, 221);
  final public static Color LIGHT_GRAY = new Color(237, 237, 237);
  final public static Color WHITE = new Color(255, 255, 254);

  final public static Color RED = new Color(224, 28, 62);
  final public static Color GREEN = new Color(0, 128, 11);
  final public static Color BLUE = new Color(0, 100, 189);

  final public static Color LIGHT_GREEN = new Color(88, 255, 0);
  final public static Color YELLOW = new Color(255, 215, 0);
  final public static Color ORANGE = new Color(255, 150, 0);
  final public static Color BROWN = new Color(153, 102, 0);
  final public static Color CYAN = new Color(0, 255, 255);
  final public static Color PINK = new Color(255, 77, 255);
  final public static Color PURPLE = new Color(161, 0, 230);

  // old colors
  public static Color MAGENTA = Color.magenta;
  public static Color GOLD = new Color(230, 200, 20);
  public static Color GRAY = Color.gray;

  public static Color[] COLORS = new Color[]
  {BLACK, DARK_BLUE, DARK_GRAY, MEDIUM_BLUE, LIGHT_GRAY, WHITE, RED, GREEN,
      BLUE, LIGHT_GREEN, YELLOW, ORANGE, BROWN, CYAN, PINK, PURPLE, MAGENTA,
      GOLD, GRAY};
  
  /** set of colors that could be used to color third party tracks (avoids
   * standard red & blue, and gray shades)
   */
  public static Color[] THIRD_PARTY_COLORS = new Color[]
  {DARK_BLUE, MEDIUM_BLUE, GREEN, LIGHT_GREEN, YELLOW, ORANGE, BROWN, CYAN,
      PINK, PURPLE, MAGENTA, GOLD};
}
