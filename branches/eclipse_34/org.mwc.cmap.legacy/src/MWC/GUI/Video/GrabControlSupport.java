// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: GrabControlSupport.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.2 $
// $Log: GrabControlSupport.java,v $
// Revision 1.2  2004/05/25 15:37:15  Ian.Mayo
// Commit updates from home
//
// Revision 1.1.1.1  2004/03/04 20:31:26  ian
// no message
//
// Revision 1.1.1.1  2003/07/17 10:07:48  Ian.Mayo
// Initial import
//
// Revision 1.2  2002-05-28 09:26:03+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:14:00+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-05-23 13:15:57+01  ian
// end of 3d development
//
// Revision 1.1  2002-04-11 13:03:31+01  ian_mayo
// Initial revision
//
// Revision 1.3  2001-07-30 15:39:15+01  administrator
// remove references to data compresssion, and remove testing lines
//
// Revision 1.2  2001-07-27 17:09:11+01  administrator
// experimenting with intermediary step
//
// Revision 1.1  2001-07-24 16:59:29+01  administrator
// improved error message
//
// Revision 1.0  2001-07-24 15:13:47+01  administrator
// Initial revision
//

package MWC.GUI.Video;

import javax.media.format.*;
import javax.media.protocol.*;
import javax.media.*;
import javax.media.datasink.*;
import javax.media.protocol.DataSource;

import javax.swing.*;
import java.awt.*;

import java.util.*;

public class GrabControlSupport
{
  /** the frame rate for the recording
   *
   */
  private float _myFrameRate;

  /** the screen area we will be recording
   *
   */
  private Rectangle _myArea;

  /** the destination for the recording
   *
   */
  private String _myDestination = "file:debrief.avi";

  /** my procesor
   *
   */
  public Processor _myProcessor = null;

  /** my file writer
   *
   */
  protected DataSink _myWriter = null;

  /** my data producer
   *
   */
  protected JDataSource _mySource = null;

  /** the type of output file we are producing
   *
   */
  protected String _myFileType = FileTypeDescriptor.MSVIDEO;


  private MediaLocator getSourceLocator()
  {
    MediaLocator res = null;
    // do we have the necessary data?
    if(_myArea != null)
    {
      String val = "screen:/";
      // produce a string representing the area and frame rate
      val += _myArea.x + "," + _myArea.y + "," + _myArea.width + "," + _myArea.height + "/" + _myFrameRate;
      res = new MediaLocator(val);
    }
    return res;
  }


  /** get the destination for the data
   *
   */
  private MediaLocator getDestinationLocator()
  {
    String thisDest = new String(_myDestination);

    // check the destination path
    if(_myDestination.startsWith("file"))
    {
      // that's ok, it's in the correct format
    }
    else
    {
      // prepend the file URL indicator
      thisDest = "file:" + thisDest;
    }

    MediaLocator res = new MediaLocator(thisDest);
    return res;
  }

  /** start recording
   *
   */
  public void configure() throws java.io.FileNotFoundException, java.io.IOException, javax.media.MediaException
  {
      // do we have a source?
      if(_mySource == null)
      {
        // create the source
        _mySource = new JDataSource();

        // provide the screen area and frame rate
        _mySource.setLocator(getSourceLocator());

        // force the initialisation
        Object[] the_streams = _mySource.getStreams();
        LiveStream str = (LiveStream)  the_streams[0];

        // connect to the source
        _mySource.connect();
      }

      // check we're connected
      if(!_mySource.connected)
        _mySource.connect();

      // do we know the format?
      Object[] the_streams = _mySource.getStreams();
      LiveStream str = (LiveStream)  the_streams[0];
      Format my_format = str.getFormat();
      Format[] formats = new Format[1];
      formats[0] = my_format;

      Format[] inter_format  = new Format[]{new VideoFormat(VideoFormat.RGB)};

      // prepare the output file
      FileTypeDescriptor output_format = new FileTypeDescriptor(_myFileType);
      ContentDescriptor cd_out = new ContentDescriptor(VideoFormat.JPEG);

      // create the processor model
      ProcessorModel theModel = new ProcessorModel(_mySource,
                                                   inter_format,
                                                   output_format);

      // now get ready for the output
      _myProcessor = Manager.createRealizedProcessor(theModel);

      // finally the destination
      MediaLocator destination = getDestinationLocator();
      DataSource source = _myProcessor.getDataOutput(); // take the data from our compressor
      _myWriter = Manager.createDataSink(source, destination);
      _myWriter.addDataSinkListener(new DataSinkListener()
      {
        public void dataSinkUpdate(DataSinkEvent event)
        {
          _myWriter.close();
        }
      });

      // open and start the writer, to check it works ok
      _myWriter.open();
      _myWriter.start();
      // ok, we're ready now, just wait for the configure!

  }

  public void start()
  {
    try
    {
      // check everything's ok
      if(_mySource == null)
      {
        System.out.println("oops, need to configure!");
        configure();
      }

      if(!_mySource.connected)
      {
        System.out.println("oops, need to re-connect");
        _mySource.connect();
      }

      // ok, lets go!
      _myProcessor.start();
    }
    catch(java.io.IOException e)
    {
      MWC.Utilities.Errors.Trace.trace(e, "Failed to open video file writer, is file already open?");
    }
    catch(javax.media.MediaException me)
    {
      MWC.Utilities.Errors.Trace.trace(me, "Failed to configure video formats.  Is correct version of JMF installed?");
    }
  }

  /** stop recording
   *
   */
  public void stop()
  {
    if(_myProcessor == null)
    {
      // hey, we're not even running!
      return;
    }

    // stop the processor
    _myProcessor.stop();
    _myProcessor.close();

    // and close the source
    _mySource.disconnect();

    // note, we close the writer by listening out to the relevant request (above)

  }

  /** set the file type for the processing
   *
   */
  public void setFileType(String fileType)
  {
    _myFileType = fileType;
  }

  /** set the frame rate for the recording
   *
   */
  public void setFrameRate(float val)
  {
    // check if this will change what we're doing
    if(val != _myFrameRate)
    {
      _myFrameRate = val;
      _mySource = null;
    }
  }

  /** get the frame rate for the recording
   *
   */
  public float getFrameRate()
  {
    return _myFrameRate;
  }

  /** set the Rectangle which we will be recording
   *
   */
  public void setArea(Rectangle area)
  {
    // check if this will change what we're doing
    if(!area.equals(_myArea))
    {
      _myArea = area;
      _mySource = null;
    }
  }

  /** get the area we will be recording
   *
   */
  public Rectangle getArea()
  {
    return _myArea;
  }

  /** set the destination for the recording
   *
   */
  public void setDestination(String path)
  {
    // see if this changes how we write our data
    if(!path.equals(_myDestination))
    {
      _myDestination = path;
      _myWriter = null;
    }
  }

  /** get the destination for the recording
   *
   */
  public String getDescription()
  {
    return _myDestination;
  }

}
