/*
 * Created by IntelliJ IDEA.
 * User: administrator
 * Date: Jan 24, 2002
 * Time: 10:57:01 AM
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package MWC.GUI.Canvas.Swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;

import MWC.GUI.Layers;

public class LayeredCanvas extends javax.swing.JComponent
{

  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
  public Layers myLayers;

  boolean first = false;

  public LayeredCanvas()
  {

  }

  public void paint(final Graphics g)
  {
    super.paint(g);

    // paint the background
    g.setColor(Color.black);
    g.fillRect(0,0,this.getWidth()-1, this.getHeight()-1);

    if(first)
    {
      g.setColor(Color.red);
      g.drawLine(200, 200, 100, 240);
      first = false;
    }
    else
    {


      final Image i1 = paintThis( 20, 40, Color.green);
      final Image i2 = paintThis( 120, 140, Color.orange);
      final Image i3 = paintThis( 220, 240, Color.cyan);

      g.drawImage(i1, 0,0, this);
      g.drawImage(i2, 0,0, this);
      g.drawImage(i3, 0,0, this);
    }
  }

  private Image paintThis(final int x, final int y, final Color col)
  {
//    Image ti = this.createImage(this.getWidth(), this.getHeight());
//    Graphics2D g2 = (Graphics2D)ti.getGraphics();
//
//    // Clear image with transparent alpha by drawing a rectangle
//    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR, 0.0f));
//    Rectangle2D.Double rect = new Rectangle2D.Double(0,0,this.getWidth(), this.getHeight());
//    g2.fill(rect);

    final BufferedImage ti = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_ARGB);
    final Graphics2D g2 = ti.createGraphics();

    // paint the background
    paintThis(g2, x, y, col);
    g2.dispose();
    return ti;
  }

  private void paintThis(final Graphics g2, final int x, final int y, final Color col)
  {
    g2.setColor(col);
    g2.fillRect(x, y, 20, 20);
  }


  public static void main(final String[] args)
  {
    final LayeredCanvas canvas = new LayeredCanvas();

    final JFrame jr = new JFrame("test");
    jr.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    jr.setSize(400, 400);
    jr.getContentPane().setLayout(new BorderLayout());
    jr.getContentPane().add("Center", canvas);
    jr.setVisible(true);
  }

}
