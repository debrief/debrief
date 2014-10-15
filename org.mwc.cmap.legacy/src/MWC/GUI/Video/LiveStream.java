/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */


package MWC.GUI.Video;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.text.ParseException;
import java.util.StringTokenizer;

import javax.media.Buffer;
import javax.media.Control;
import javax.media.Format;
import javax.media.MediaLocator;
import javax.media.format.RGBFormat;
import javax.media.format.VideoFormat;
import javax.media.protocol.BufferTransferHandler;
import javax.media.protocol.ContentDescriptor;
import javax.media.protocol.PushBufferStream;

import MWC.Utilities.ReaderWriter.XML.MWCXMLReader;

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
    public LiveStream(final MediaLocator locator)
    {
      try
      {
          parseLocator(locator);
      } catch (final Exception e) {
          System.err.println(e);
      }
      size = new Dimension(width, height);
      try {
          robot = new Robot();
      } catch (final AWTException awe) {
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

    protected void parseLocator(final MediaLocator locator) throws ParseException {
      String rem = locator.getRemainder();
      // Strip off starting slashes
      while (rem.startsWith("/") && rem.length() > 1)
      {
          rem = rem.substring(1);
      }

      final StringTokenizer st = new StringTokenizer(rem, "/");
      if (st.hasMoreTokens()) {
        // Parse the position
        final String position = st.nextToken();
        final StringTokenizer nums = new StringTokenizer(position, ",");
        final String stX = nums.nextToken();
        final String stY = nums.nextToken();
        final String stW = nums.nextToken();
        final String stH = nums.nextToken();
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
        final String stFPS = st.nextToken();
        frameRate = new Double(MWCXMLReader.readThisDouble(stFPS)).floatValue();
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

    public void read(final Buffer buffer) throws IOException {
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

        final long tNow = System.currentTimeMillis();

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
          final long wait = nextDue - tNow;

          // is this +ve?
          if(wait > 0)
          {
            try
            {
              Thread.sleep(wait);
            }
            catch(final Exception e)
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
          final long diff = tNow - firstTime;

          // store the time stamp
          buffer.setTimeStamp((diff) * 1000000 );
        }

        // and finally move forward the next time stamp
        nextDue += delta;

        // get the image
        final BufferedImage bi = robot.createScreenCapture(new Rectangle(x, y, width, height));

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

    public void setTransferHandler(final BufferTransferHandler transferHandler) {
	synchronized (this) {
	    this.transferHandler = transferHandler;
	    notifyAll();
	}
    }

    void start(final boolean started1) {
	synchronized ( this ) {
	    this.started = started1;
	    if (started1 && !thread.isAlive()) {
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
            catch (final InterruptedException ie)
            {
		        }
		      } // while
        }

	      if (started && transferHandler != null)
        {
		      transferHandler.transferData(this);
		      try
          {
		        Thread.sleep( 10 );
		      }
          catch (final InterruptedException ise)
          {
		      }
	      }
	    } // while (started)
    } // run

    // Controls

    public Object [] getControls() {
	    return controls;
    }

    @SuppressWarnings("rawtypes")
		public Object getControl(final String controlType) {
       try {
          final Class  cls = Class.forName(controlType);
          final Object cs[] = getControls();
          for (int i = 0; i < cs.length; i++) {
             if (cls.isInstance(cs[i]))
                return cs[i];
          }
          return null;

       } catch (final Exception e) {   // no such controlType or such control
         return null;
       }
    }
}
