/*
 * @(#)DataSource.java	1.2 01/03/02
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

import javax.media.Time;
import javax.media.MediaLocator;
import javax.media.protocol.*;
import java.io.IOException;

/**
 * This DataSource captures live frames from the screen.
 * You can specify the location, size and frame rate in the
 * URL string as follows:
 * screen://x,y,width,height/framespersecond
 * Eg:
 *    screen://20,40,160,120/12.5
 * Note:
 *    Requires JDK 1.3+ to compile and run
 */

public class JDataSource extends PushBufferDataSource {

    protected Object [] controls = new Object[0];
    protected boolean started = false;
    protected String contentType = "raw";
    protected boolean connected = false;
    protected Time duration = DURATION_UNBOUNDED;
    protected LiveStream [] streams = null;
    protected LiveStream stream = null;

    public JDataSource() {
    }

    public String getContentType()
    {
      if (!connected)
      {
        System.err.println("Error: DataSource not connected");
        return null;
      }
      return contentType;
    }

    public void connect() throws IOException {
      if (connected)
       return;
      connected = true;
    }

    public void disconnect() {
    	try
      {
        if (started)
           stop();
      } catch (IOException e)
      {}
      connected = false;
    }

    public void start() throws IOException {
	    // we need to throw error if connect() has not been called
      if (!connected)
        throw new java.lang.Error("DataSource must be connected before it can be started");
      if (started)
        return;
	    started = true;
    	stream.start(true);
    }

    public void stop() throws IOException {
    	if ((!connected) || (!started))
	      return;
	    started = false;
	    stream.start(false);
    }

    public Object [] getControls() {
	    return controls;
    }

    public Object getControl(String controlType) {
	    return null;
    }

    public Time getDuration() {
	    return duration;
    }

    public PushBufferStream [] getStreams()
    {
      if (streams == null)
      {
          streams = new LiveStream[1];
          stream = streams[0] = new LiveStream(getLocator());
	    }
	    return streams;
    }
}
