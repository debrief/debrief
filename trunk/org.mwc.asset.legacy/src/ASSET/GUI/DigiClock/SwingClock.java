/*
 * Desciption:
 * User: administrator
 * Date: Nov 9, 2001
 * Time: 10:16:52 AM
 */
package ASSET.GUI.DigiClock;

import javax.swing.*;
import java.awt.*;
import java.util.Date;
import java.util.TimeZone;
import java.text.SimpleDateFormat;

public class SwingClock extends javax.swing.JComponent
{
  /***************************************************************
   *  member variables
   ***************************************************************/
  /** our double buffer
   *
   */
  Image Buffer;

  /** our list of digit images
   *
   */
  private Image[] Digits;

  /** the graphics buffer
   *
   */
  private Graphics gBuffer;

  /** our text formatter
   *
   */
  private java.text.SimpleDateFormat _dateF = null;

  /***************************************************************
   *  constructor
   ***************************************************************/
  /**
   * Default JComponent constructor.  This constructor does
   * no initialization beyond calling the Container constructor.
   * For example, the initial layout manager is null.
   */
  public SwingClock()
  {
    super();

    setSize(204, 54);

    _dateF = new SimpleDateFormat("hhmmss");
    _dateF.setTimeZone(TimeZone.getTimeZone("GMT"));

    init();
  }
  /***************************************************************
   *  member methods
   ***************************************************************/

  private void init()
  {
    Digits = new Image[12];

    //we load our digit images
    for (int i = 0; i < 10; i++)
      Digits[i] = loadImage("images/digits/" + i + ".gif");

    Digits[10] = loadImage("images/digits/dot_p.gif");
    Digits[11] = loadImage("images/digits/dot_a.gif");

    //create off-screen image we can draw to
//    Buffer = createImage(205,54);
//    gBuffer = Buffer.getGraphics();
  }

  private Image loadImage(String img)
  {
    ImageIcon io = new ImageIcon(getClass().getClassLoader().getResource(img));

    Image image = io.getImage();
    MediaTracker tracker = new MediaTracker(this);
    tracker.addImage(image, 0);
    try
    {
      tracker.waitForID(0);
    }
    catch (InterruptedException e)
    {
    }
    return image;
  }

  /** paint the clock
   */
  public void paint(Graphics g)
  {

    //create a new time object
    Date today = new Date();

    //get the seconds, minutes and hours
 /*   int sec = today.getSeconds();
    int min = today.getMinutes();
    int hour = today.getHours();

    String secStr, minStr, hourStr;

    //copy the time integers to our string objects
    //if values are less than 10 we add a 0
    if (hour < 10)
      hourStr = "0" + hour;
    else
      hourStr = "" + hour;

    if (min < 10)
      minStr = "0" + min;
    else
      minStr = "" + min;

    if (sec < 10)
      secStr = "0" + sec;
    else
      secStr = "" + sec;
                         */
    String timeStr = _dateF.format(today);

    gBuffer = g;

    //fill the background
    gBuffer.setColor(new Color(105, 0, 0));
    Dimension siz = this.getSize();
    gBuffer.fillRect(0, 0, siz.width, siz.height);

    //draw a thin frame
    gBuffer.setColor(new Color(255, 105, 40));
    gBuffer.drawRect(0, 0, siz.width - 1, siz.height - 1);

    //we draw our digits from our method drawDigit
//    drawDigit(Integer.parseInt(hourStr.substring(0, 1)), 5);
//    drawDigit(Integer.parseInt(hourStr.substring(1, 2)), 35);
//
//    drawDigit(Integer.parseInt(minStr.substring(0, 1)), 72);
//    drawDigit(Integer.parseInt(minStr.substring(1, 2)), 102);
//
//    drawDigit(Integer.parseInt(secStr.substring(0, 1)), 139);
//    drawDigit(Integer.parseInt(secStr.substring(1, 2)), 169);

    drawDigit(Integer.parseInt(timeStr.substring(0, 1)), 5);
    drawDigit(Integer.parseInt(timeStr.substring(1, 2)), 35);

    drawDigit(Integer.parseInt(timeStr.substring(2, 3)), 72);
    drawDigit(Integer.parseInt(timeStr.substring(3, 4)), 102);

    drawDigit(Integer.parseInt(timeStr.substring(4, 5)), 139);
    drawDigit(Integer.parseInt(timeStr.substring(5, 6)), 169);

    //the blinking dots
    gBuffer.drawImage(Digits[10], 65, 5, this);
    gBuffer.drawImage(Digits[10], 132, 5, this);


    //copy the buffer to the screen (no flickering!)
  //  g.drawImage(Buffer, 0, 0, this);




    super.paint(g);

    g.setColor(Color.red);
    g.drawLine(2,3,4,5);
    g.drawLine(12,33,44,65);
  }

  private void drawDigit(int digit, int x)
  {
    gBuffer.drawImage(Digits[digit], x, 5, this);
  }

  /** set the time
   *
   */
  public void setTime(long time)
  {

  }
}
