/*
 * Created by IntelliJ IDEA.
 * User: administrator
 * Date: Jan 24, 2002
 * Time: 10:57:01 AM
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package MWC.GUI.Canvas.Swing;

import MWC.GUI.Layers;
import MWC.GUI.Layer;
import MWC.GUI.Plottables;
import MWC.GUI.CanvasType;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.geom.Rectangle2D;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Date;
import java.util.Set;
import java.util.Iterator;
import java.text.SimpleDateFormat;

public class LayeredCanvas extends javax.swing.JComponent
{

  public HashMap myImages;
  public Layers myLayers;

  boolean first = false;

  public LayeredCanvas()
  {

  }

  public void paint(Graphics g)
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


      Image ti = this.createImage(this.getWidth(), this.getHeight());
      Graphics g2 = ti.getGraphics();
      // paint the background
 //     g2.setColor(Color.black);
 //     g2.fillRect(0,0,this.getWidth()-1, this.getHeight()-1);

      Image i1 = paintThis( 20, 40, Color.green);
      Image i2 = paintThis( 120, 140, Color.orange);
      Image i3 = paintThis( 220, 240, Color.cyan);

      g.drawImage(i1, 0,0, this);
      g.drawImage(i2, 0,0, this);
      g.drawImage(i3, 0,0, this);
    }
  }

  private Image paintThis(int x, int y, Color col)
  {
//    Image ti = this.createImage(this.getWidth(), this.getHeight());
//    Graphics2D g2 = (Graphics2D)ti.getGraphics();
//
//    // Clear image with transparent alpha by drawing a rectangle
//    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR, 0.0f));
//    Rectangle2D.Double rect = new Rectangle2D.Double(0,0,this.getWidth(), this.getHeight());
//    g2.fill(rect);

    BufferedImage ti = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_ARGB);
    Graphics2D g2 = ti.createGraphics();

    // paint the background
    paintThis(g2, x, y, col);
    g2.dispose();
    return ti;
  }

  private void paintThis(Graphics g2, int x, int y, Color col)
  {
    g2.setColor(col);
    g2.fillRect(x, y, 20, 20);
  }


  public static void main(String[] args)
  {
    LayeredCanvas canvas = new LayeredCanvas();

    JFrame jr = new JFrame("test");
    jr.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    jr.setSize(400, 400);
    jr.getContentPane().setLayout(new BorderLayout());
    jr.getContentPane().add("Center", canvas);
    jr.setVisible(true);
  }

}
