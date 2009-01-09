/*
 * XMLHandler.java
 *
 * Created on 04 October 2000, 11:34
 */

package MWC.Utilities.ReaderWriter.XML;

import org.xml.sax.*;

import java.io.CharArrayWriter;
import java.util.Date;
import java.util.Enumeration;
import java.util.TimeZone;
import java.util.Vector;

/**
 * @author IAN MAYO
 */
@SuppressWarnings("deprecation")
public class XMLHandler extends HandlerBase
{

  protected Vector<XMLHandler> _myHandlers;
  private String _myType;
  protected Parser _theParser;
  protected DocumentHandler _theParent;

  protected Vector<HandleAttribute> _myAttributeHandlers;

  // Buffer for collecting data from
  // the "characters" SAX event.
  protected CharArrayWriter contents = new CharArrayWriter();

  /**
   * date formatter to be used by child classes
   */
  static public java.text.DateFormat RNdateFormat =
    new java.text.SimpleDateFormat("yyMMdd HHmmss.SSS");

  /**
   * number formatter used by our "writeThis" methods
   */
  static private java.text.DecimalFormat shortFormat = new java.text.DecimalFormat("0.000");
  static java.text.DecimalFormat longFormat = new java.text.DecimalFormat("0.0000000");

  //////////////////////////////////////////////////
  // constructor
  /////////////////////////////////////////////////
  public XMLHandler(String myType)
  {
    _myType = myType;
    _myHandlers = new Vector<XMLHandler>(0, 1);
    _myAttributeHandlers = new Vector<HandleAttribute>(0, 1);
    RNdateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
  }

  /**
   * the actual data for this type of object
   */
  protected void handleOurselves(String name, AttributeList attributes)
  {

    // go through our list of handlers
    Enumeration<HandleAttribute> enumer = _myAttributeHandlers.elements();
    while (enumer.hasMoreElements())
    {
      HandleAttribute ha = (HandleAttribute) enumer.nextElement();
      String val = attributes.getValue(ha.myName);
      if (val != null)
      {
        // handle this next call, since it does occasionally fail
        try
        {
          ////
          ha.setValue(ha.myName, val);
          ////
        }
        catch (Exception e)
        {
          MWC.Utilities.Errors.Trace.trace(e, "Trouble handling attribute: " + ha.myName + " for:" + _myType);
        }
      }
      else
      {
        // let's not bother about parameters not being found, they're mostly optional anyway
        // NO, KEEP IT IN! to give us some hints if somebody's application isn't working too well.
        //  MWC.Utilities.Errors.Trace.trace("parameter not found for:" + ha.myName + " in element " + name);
      }
    }
  }


  public void addAttributeHandler(HandleAttribute val)
  {
    _myAttributeHandlers.addElement(val);
  }

  /**
   * remember that we also have this type of handler
   */
  public void addHandler(XMLHandler handler)
  {
    _myHandlers.addElement(handler);
  }

  /**
   * remove this type of handler from our list
   */
  public void removeHandler(XMLHandler handler)
  {
    _myHandlers.remove(handler);
  }

  /**
   * see if we can handle this type of data
   */
  public boolean canHandleThis(String element)
  {
    return element.equals(_myType);
  }

  /**
   * take over the parsing "stack"
   */
  public void handleThis(Parser parser,
                         DocumentHandler parent)
  {
    _theParent = parent;
    _theParser = parser;
    parser.setDocumentHandler(this);
  }


  /**
   * process this stream of characters
   */
  public void characters(char[] ch, int start, int length)
    throws SAXException
  {
    // accumulate the contents into a buffer.
    contents.write(ch, start, length);

  }

  /**
   * a new element has started. if it is one of ours, process it, else
   * see if one of our handlers can manage it for us, thanks
   */
  public void startElement(String name,
                           AttributeList attributes)
    throws SAXException
  {
    boolean handled = false;

    // check we are handling a session
    if (canHandleThis(name))
    {
      // hooray it's one of ours!
      handleOurselves(name, attributes);
      handled = true;
    }
    else
    // see if we have a handler for this object
      if (_myHandlers != null)
      {
        Enumeration<XMLHandler> enumer = _myHandlers.elements();
        while (enumer.hasMoreElements())
        {
          XMLHandler hand = (XMLHandler) enumer.nextElement();
          if (hand.canHandleThis(name))
          {
            hand.startElement(name, attributes);

            ////////////////
            // wrap this, it's hard to diagnose errors which appear here
            ////////////////

            try
            {
              hand.handleThis(_theParser, this);
            }
            catch (Exception e)
            {
              MWC.Utilities.Errors.Trace.trace(e, "Trouble handling attribute:" + name);
            }

            ////////////////
            // ok, continue
            ////////////////
            handled = true;
            break;
          }
        }
      }

    if (!handled)
      MWC.Utilities.Errors.Trace.trace("XMLHandler failed to find handler for:" + name);
  }

  /**
   * we have reached the end of an element.  See if it is our element - so
   * we should drop out, else let's continue
   */
  public void endElement(String name)
    throws SAXException
  {
    // check if it is us which have finished, if so, drop back to our parent
    if (name.equals(this._myType))
    {

      try
      {
        elementClosed();
      }
      catch (NullPointerException se)
      {
        // output a hopefully useful message
        String msg = "Trouble parsing element: " + name;
        MWC.Utilities.Errors.Trace.trace(se, msg);

        // and continue back up the stack
        throw se;
      }

      // element has finished, drop back
      _theParser.setDocumentHandler(_theParent);
    }
  }

  public void elementClosed()
  {
    // don't bother
  }

  public boolean booleanValueOf(String val)
  {
    boolean res = false;
    if (val.equals("TRUE"))
      res = true;

    return res;

  }

  //////////////////////////////////////////////////////////
  //
  /////////////////////////////////////////////////////////
  abstract static public class HandleAttribute
  {
    public String myName;

    public HandleAttribute(String name)
    {
      myName = name;
    }

    abstract public void setValue(String name, String value);
  }

  abstract static public class HandleDoubleAttribute extends HandleAttribute
  {
    public HandleDoubleAttribute(String name)
    {
      super(name);
    }

    public void setValue(String name, String value)
    {
      try
      {
        double val = longFormat.parse(value).doubleValue();
        setValue(name, val);
      }
      catch (java.text.ParseException pe)
      {
        MWC.Utilities.Errors.Trace.trace(pe, "Handler: Whilst reading in " + name + " value of :" + value);
      }

    }

    abstract public void setValue(String name, double value);

  }

  abstract static public class HandleLongAttribute extends HandleAttribute
  {
    public HandleLongAttribute(String name)
    {
      super(name);
    }

    public void setValue(String name, String value)
    {
      long val = Long.parseLong(value);
      setValue(name, val);
    }

    abstract public void setValue(String name, long value);
  }

  abstract static public class HandleBooleanAttribute extends HandleAttribute
  {
    public HandleBooleanAttribute(String name)
    {
      super(name);
    }

    public void setValue(String name, String value)
    {
      boolean val = Boolean.getBoolean(value);
      setValue(name, val);
    }

    abstract public void setValue(String name, boolean value);
  }
  //////////////////////////////////////////////////////////////////
  // number formatting used in XML export
  //////////////////////////////////////////////////////////////////

  static public String writeThis(boolean val)
  {
    if (val)
      return "true";
    else
      return "false";
  }

  static public String writeThis(Boolean val)
  {
    return writeThis(val.booleanValue());
  }


  static public String writeThis(int val)
  {
    return Integer.toString(val);
  }

  static public String writeThis(long val)
  {
    return Long.toString(val);
  }

  static public String writeThis(double val)
  {
    return shortFormat.format(val);
  }

  static public String writeThis(Date val)
  {
    return RNdateFormat.format(val);
  }

  static public String writeThisLong(double val)
  {
    return longFormat.format(val);
  }


}

