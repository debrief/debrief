package MWC.GUI.Dialogs;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;


/** class to show a splash screen whilst other application is loading.  The splash
 * screen closes when instructed or when the parent frame is selected
 */
public class SplashScreen extends Window
{

  /****************************************************
   * member fields
   ***************************************************/
  /** the image we plot
   *
   */
  final private Image _myImage;
  final private String _myTitle;
  final private Toolkit _myToolkit;
  final private Color _myColor;

  private int imgWidth;
  private int imgHeight;

  private int _myBorderWidth = 2;


  /****************************************************
   * constructor
   ***************************************************/
  /** constructor
   * image - the image to plot
   * title - title of the splash screen
   */
  public SplashScreen(Frame parent,
                      String imageName,
                      String title,
                      Color titleColor)
  {
    super(parent);

    // store the parameters
    _myTitle = title;
    _myToolkit = Toolkit.getDefaultToolkit();
    _myColor = titleColor;

    // load the image
    _myImage = loadSplashImage(imageName);

    // show the image
    showSplashScreen();

    //  listen out for splash getting clicked (to close)
    this.addMouseListener(new MouseAdapter()
    {
      /**
       * Invoked when the mouse has been clicked on a component.
       */
      public void mouseClicked(MouseEvent e)
      {
        setVisible(false);
        dispose();
      }
    });

  }

  /****************************************************
   * member methods
   ***************************************************/
  private Image loadSplashImage(String imageName)
  {
    Image image = null;
    URL theURL = getClass().getClassLoader().getResource(imageName);
    if(theURL != null)
    {
      ImageIcon io = new ImageIcon(theURL);
      image = io.getImage();
      imgWidth = image.getWidth(this);
      imgHeight = image.getHeight(this);
    }
    return image;
  }

  private void showSplashScreen()
  {
    Dimension screenSize = _myToolkit.getScreenSize();
    int w = imgWidth + _myBorderWidth * 2;
    int h = imgHeight + _myBorderWidth * 2;
    int x = (screenSize.width - w) / 2;
    int y = (screenSize.height - h) / 2;
    setBounds(x, y, w, h);
    setVisible(true);
  }

  /**
   * Paints the container. This forwards the paint to any lightweight
   * components that are children of this container. If this method is
   * reimplemented, super.paint(g) should be called so that lightweight
   * components are properly rendered. If a child component is entirely
   * clipped by the current clipping setting in g, paint() will not be
   * forwarded to that child.
   *
   * @param g the specified Graphics window
   * @see   java.awt.Component#update(java.awt.Graphics)
   */
  public void paint(Graphics g)
  {
    g.drawImage(_myImage, _myBorderWidth, _myBorderWidth, imgWidth, imgHeight, this);

    // work out where to put the text
    Font newF = new Font("SansSerif", Font.BOLD, 15);
    Font oldF = g.getFont();
    g.setFont(newF);
    g.setColor(_myColor);
    FontMetrics fm = g.getFontMetrics();
    int wid = fm.stringWidth(this._myTitle);
    g.drawString(_myTitle, (imgWidth - wid), (imgHeight - 2));

    g.setFont(oldF);
  }

}
