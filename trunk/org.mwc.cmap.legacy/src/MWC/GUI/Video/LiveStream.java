/*
 * @(#)LiveStream.java	1.2 01/03/02
 *
 * Copyright (c) 1999-2001 Sun Microsystems, Inc. All Rights Reserved.
 *
 * Sun grants you ("Licensee") a non-exclusive, royalty free, license to use,
 * modify and redistribute this software in source and binary code form,
 * provided that i) this copyright notice and license appear on all copies of
 * the software; and ii) Licensee does not utilize the software in a manner
 * which is disparaging to Sun.
 *
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING ANY
 * IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR
 * NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN AND ITS LICENSORS SHALL NOT BE
 * LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING
 * OR DISTRIBUTING THE SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL SUN OR ITS
 * LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT,
 * INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER
 * CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF
 * OR INABILITY TO USE SOFTWARE, EVEN IF SUN HAS BEEN ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGES.
 *
 * This software is not designed or intended for use in on-line control of
 * aircraft, air traffic, aircraft navigation or aircraft communications; or in
 * the design, construction, operation or maintenance of any nuclear
 * facility. Licensee represents and warrants that it will not use or
 * redistribute the Software for such purposes.
 */


package MWC.GUI.Video;

import java.awt.*;
import java.awt.image.BufferedImage;
import javax.media.*;
import javax.media.format.*;
import javax.media.protocol.*;
import java.io.IOException;
import java.util.StringTokenizer;

public class LiveStream implements PushBufferStream, Runnable {

    protected ContentDescriptor cd = new ContentDescriptor(ContentDescriptor.RAW);
    protected int maxDataLength;
    protected int [] data;
    protected Dimension size;
    protected RGBFormat _myFormat;
    protected boolean started;
    protected Thread thread;
    protected float frameRate = 1f;
    protected BufferTransferHandler transferHandler;
    protected Control [] controls = new Control[0];
    protected int x, y, width, height;

    // the robot we use to take snapshots of the screen
    protected Robot robot = null;

    // ongoing values to keep track of our timer
    int seqNo = 0;
    long firstTime = 0;
    long nextDue = 0;
    long delta = 0;

    //////////////////////////////////////
    public LiveStream(MediaLocator locator)
    {
      try
      {
          parseLocator(locator);
      } catch (Exception e) {
          System.err.println(e);
      }
      size = new Dimension(width, height);
      try {
          robot = new Robot();
      } catch (AWTException awe) {
          throw new RuntimeException("");
      }
    	maxDataLength = size.width * size.height * 3;
	    _myFormat = new RGBFormat(size,                 // dimensions
                                maxDataLength,        // max length of a data chunk
			    	                    Format.intArray,      // type of the data
				                        frameRate,            // frame rate
                                32,                   // bits per pixel
                                0xFF0000,             // red mask
                                0xFF00,               // blue mask
                                0xFF,                 // green mask
                                1,                    // pixel stride
                                size.width,           // line stride
                                VideoFormat.FALSE,    // flipped
                                Format.NOT_SPECIFIED); // endian

      // generate the data
      data = new int[maxDataLength];
      thread = new Thread(this, "Screen Grabber");
    }

    protected void parseLocator(MediaLocator locator) {
      String rem = locator.getRemainder();
      // Strip off starting slashes
      while (rem.startsWith("/") && rem.length() > 1)
      {
          rem = rem.substring(1);
      }

      StringTokenizer st = new StringTokenizer(rem, "/");
      if (st.hasMoreTokens()) {
        // Parse the position
        String position = st.nextToken();
        StringTokenizer nums = new StringTokenizer(position, ",");
        String stX = nums.nextToken();
        String stY = nums.nextToken();
        String stW = nums.nextToken();
        String stH = nums.nextToken();
        x = Integer.parseInt(stX);
        y = Integer.parseInt(stY);
        width = Integer.parseInt(stW);
        height = Integer.parseInt(stH);

        // NOW THIS IS MAD!  we get twisted data when we use an odd width!
        // so, if the width is odd, trim it by one!
        if((width % 2) == 1)
        {
          System.out.println("note, shrinking video size to even width");
          width -= 1;
        }

	    }
	    if (st.hasMoreTokens()) {
        // Parse the frame rate
        String stFPS = st.nextToken();
        frameRate = (Double.valueOf(stFPS)).floatValue();
	    }
    }

    /***************************************************************************
     * SourceStream
     ***************************************************************************/

    public ContentDescriptor getContentDescriptor() {
	return cd;
    }

    public long getContentLength() {
	return LENGTH_UNKNOWN;
    }

    public boolean endOfStream() {
	return false;
    }

    /***************************************************************************
     * PushBufferStream
     ***************************************************************************/


    public Format getFormat() {
    	return _myFormat;
    }

    public void read(Buffer buffer) throws IOException {
	    synchronized (this) {
	      Object outdata = buffer.getData();

        // check that output object has been created
	      if (outdata == null || !(outdata.getClass() == Format.intArray) ||
		        ((int[])outdata).length < maxDataLength)
        {
          outdata = new int[maxDataLength];
          buffer.setData(outdata);
  	    }
        buffer.setFormat( _myFormat );

        long tNow = System.currentTimeMillis();

        if(nextDue == 0)
        {
          // this is our first one, first calculate the delta (millis)
          delta = (long)(1000f / frameRate);

          // set it to the current time anyway
          nextDue = tNow;
        }
        else
        {
          // see if we need a pause
          long wait = nextDue - tNow;

          // is this +ve?
          if(wait > 0)
          {
            try
            {
              Thread.currentThread().sleep(wait);
            }
            catch(Exception e)
            {
              e.printStackTrace();
            }
          }
        }

        // create the time stamp, be measuring how long it is since the recording started
        ////

        // have we just started?
        if(firstTime == 0)
        {
          // remember the time
          firstTime = tNow;
          // indicate this is the first frame
          buffer.setTimeStamp(0);
        }
        else
        {
          // work out how long has elapsed
          long diff = tNow - firstTime;

          // store the time stamp
          buffer.setTimeStamp((diff) * 1000000 );
        }

        // and finally move forward the next time stamp
        nextDue += delta;

        // get the image
        BufferedImage bi = robot.createScreenCapture(new Rectangle(x, y, width, height));

        // convert to RGB
        bi.getRGB(0, 0, width, height,(int[])outdata, 0, width);

        // configure the rest of hte output parameters
        buffer.setSequenceNumber( seqNo );
        buffer.setLength(maxDataLength);
        buffer.setFlags(Buffer.FLAG_KEY_FRAME);
        buffer.setHeader( null );
        seqNo++;
    	}
    }

    public void setTransferHandler(BufferTransferHandler transferHandler) {
	synchronized (this) {
	    this.transferHandler = transferHandler;
	    notifyAll();
	}
    }

    void start(boolean started) {
	synchronized ( this ) {
	    this.started = started;
	    if (started && !thread.isAlive()) {
		thread = new Thread(this);
		thread.start();
	    }
	    notifyAll();
	}
    }

    /***************************************************************************
     * Runnable
     ***************************************************************************/

    public void run()
    {
	    while (started)
      {
  	    synchronized (this)
        {
		      while (transferHandler == null && started)
          {
		        try
            {
			        wait(1000);
		        }
            catch (InterruptedException ie)
            {
		        }
		      } // while
        }

	      if (started && transferHandler != null)
        {
		      transferHandler.transferData(this);
		      try
          {
		        Thread.currentThread().sleep( 10 );
		      }
          catch (InterruptedException ise)
          {
		      }
	      }
	    } // while (started)
    } // run

    // Controls

    public Object [] getControls() {
	    return controls;
    }

    public Object getControl(String controlType) {
       try {
          Class  cls = Class.forName(controlType);
          Object cs[] = getControls();
          for (int i = 0; i < cs.length; i++) {
             if (cls.isInstance(cs[i]))
                return cs[i];
          }
          return null;

       } catch (Exception e) {   // no such controlType or such control
         return null;
       }
    }
}
