/**
 * @(#) D3Splash.java	1.0	Tuesday, July 20, 1999  8:28:06 o'clock AM PDT
 * 
 * Copyright: Your Company all rights are reserved.
 */

package Debrief.GUI.Frames.AWT;

import java.awt.*;

public final class D3Splash extends Window
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// Required for Visual Designer support
	public D3Splash(Frame parent)
	{
    
    super(parent);

    MediaTracker mt = new MediaTracker(this);
    String srv = "";
    imageD = Toolkit.getDefaultToolkit().getImage(srv + "d.gif");
    mt.addImage(imageD, 0);
    imageK = Toolkit.getDefaultToolkit().getImage(srv + "k.gif");
    mt.addImage(imageK, 1);
    image2 = Toolkit.getDefaultToolkit().getImage(srv + "two.gif");
    mt.addImage(image2, 1);
    crest = Toolkit.getDefaultToolkit().getImage(srv + "d3logo.gif");
    mt.addImage(crest, 1);
    try
    {
      mt.waitForAll();
    }
    catch(Exception e)
    {
      MWC.Utilities.Errors.Trace.trace(e);
    }
		initForm();

	}
  
  private final Image imageD;
  Image imageE;
  Image imageB;
  Image imageR;
  Image imageI;
  private final Image imageK;
  Image imageF;
  private final Image image2;
  private final Image crest;

  private void initForm()
	{

  	this.setLayout( new java.awt.FlowLayout());
    
		this.setSize( 590, 230 );
    this.setBackground(Color.lightGray);
    
    Rectangle sz = this.getBounds();
    Dimension ssz = Toolkit.getDefaultToolkit().getScreenSize();
    setLocation((ssz.width - sz.width)/2,
                (ssz.height - sz.height)/2);
	}

  
  private Image off;
  private Graphics offG;
  private String theStr="Loading editors....";
  
  public final void paint(Graphics p2)
  {
    Dimension sz = this.getSize();
    int x = 5;
    int y = 60;
 //   if(off == null)
    {
      off = this.createImage(sz.width, sz.height);
      offG = off.getGraphics();
      offG.setFont(new Font("Sans Serif", Font.PLAIN, 14));
      offG.drawString("Debrief 3 - another great MWC product from MAL", 30,20);
      offG.drawString(theStr, 30,40);
      
      offG.drawImage(crest, x, y, this);
      offG.draw3DRect(3,3,sz.width-7,sz.height-7,false);
    }
    x += crest.getWidth(this);
    if(image2 != null)
    {
      int ht = crest.getHeight(this);
      int ht2 = imageD.getHeight(this);
      y = y + (ht - ht2)/2 + 5;
      
      offG.drawImage(imageD, x, y, this);
      x += imageD.getWidth(this);
      offG.drawImage(image2, x, y, this);
      x += image2.getWidth(this);
      offG.drawImage(imageK, x, y, this);
    }
    p2.drawImage(off, 0, 0, this);
  }
  
  

  public final void update(final Graphics p1)
  {
    paint(p1);
  }


  public final void setVisible(final boolean p1)
  {
    super.setVisible(p1);
    
    try
    {
      Thread.sleep(2000);
      
      theStr = "Loading Coastline";
          
    }
    catch(Exception e)
    {
    }
    
  }
  
  
}

