/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)

 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */

package MWC.Utilities.ReaderWriter.XML;

import java.io.CharArrayWriter;
import java.util.Date;
import java.util.Enumeration;
import java.util.TimeZone;
import java.util.Vector;

import org.xml.sax.AttributeList;
import org.xml.sax.DocumentHandler;
import org.xml.sax.HandlerBase;
import org.xml.sax.Parser;
import org.xml.sax.SAXException;

/**
 * @author IAN MAYO
 */
@SuppressWarnings("deprecation")
public class XMLHandler extends HandlerBase
{

  protected Vector<XMLHandler> _myHandlers;
  private final String _myType;
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
  public XMLHandler(final String myType)
  {
    _myType = myType;
    _myHandlers = new Vector<XMLHandler>(0, 1);
    _myAttributeHandlers = new Vector<HandleAttribute>(0, 1);
    RNdateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
  }

  /**
   * the actual data for this type of object
   */
  protected void handleOurselves(final String name, final AttributeList attributes)
  {

    // go through our list of handlers
    final Enumeration<HandleAttribute> enumer = _myAttributeHandlers.elements();
    while (enumer.hasMoreElements())
    {
      final HandleAttribute ha = (HandleAttribute) enumer.nextElement();
      final String val = attributes.getValue(ha.myName);
      if (val != null)
      {
        // handle this next call, since it does occasionally fail
        try
        {
          ////
          ha.setValue(ha.myName, val);
          ////
        }
        catch (final Exception e)
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


  public void addAttributeHandler(final HandleAttribute val)
  {
    _myAttributeHandlers.addElement(val);
  }

  /**
   * remember that we also have this type of handler
   */
  public void addHandler(final XMLHandler handler)
  {
    _myHandlers.addElement(handler);
  }

  /**
   * remove this type of handler from our list
   */
  public void removeHandler(final XMLHandler handler)
  {
    _myHandlers.remove(handler);
  }

  /**
   * see if we can handle this type of data
   */
  public boolean canHandleThis(final String element)
  {
    return element.equals(_myType);
  }

  /**
   * take over the parsing "stack"
   */
  public void handleThis(final Parser parser,
                         final DocumentHandler parent)
  {
    _theParent = parent;
    _theParser = parser;
    parser.setDocumentHandler(this);
  }


  /**
   * process this stream of characters
   */
  public void characters(final char[] ch, final int start, final int length)
    throws SAXException
  {
    // accumulate the contents into a buffer.
    contents.write(ch, start, length);

  }

  /**
   * a new element has started. if it is one of ours, process it, else
   * see if one of our handlers can manage it for us, thanks
   */
  public void startElement(final String name,
                           final AttributeList attributes)
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
        final Enumeration<XMLHandler> enumer = _myHandlers.elements();
        while (enumer.hasMoreElements())
        {
          final XMLHandler hand = (XMLHandler) enumer.nextElement();
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
            catch (final Exception e)
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
  public void endElement(final String name)
    throws SAXException
  {
    // check if it is us which have finished, if so, drop back to our parent
    if (name.equals(this._myType))
    {

      try
      {
        elementClosed();
      }
      catch (final NullPointerException se)
      {
        // output a hopefully useful message
        final String msg = "Trouble parsing element: " + name;
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

  //////////////////////////////////////////////////////////
  //
  /////////////////////////////////////////////////////////
  abstract static public class HandleAttribute
  {
    public String myName;

    public HandleAttribute(final String name)
    {
      myName = name;
    }

    abstract public void setValue(String name, String value);
  }

  abstract static public class HandleDoubleAttribute extends HandleAttribute
  {
    public HandleDoubleAttribute(final String name)
    {
      super(name);
    }

    public void setValue(final String name, final String value)
    {
      try
      {
        final double val = longFormat.parse(value).doubleValue();
        setValue(name, val);
      }
      catch (final java.text.ParseException pe)
      {
        MWC.Utilities.Errors.Trace.trace(pe, "Handler: Whilst reading in " + name + " value of :" + value);
      }

    }

    abstract public void setValue(String name, double value);

  }

  abstract static public class HandleLongAttribute extends HandleAttribute
  {
    public HandleLongAttribute(final String name)
    {
      super(name);
    }

    public void setValue(final String name, final String value)
    {
      final long val = Long.parseLong(value);
      setValue(name, val);
    }

    abstract public void setValue(String name, long value);
  }

  abstract static public class HandleBooleanAttribute extends HandleAttribute
  {
    public HandleBooleanAttribute(final String name)
    {
      super(name);
    }

    public void setValue(final String name, final String value)
    {
      final boolean val = Boolean.getBoolean(value);
      setValue(name, val);
    }

    abstract public void setValue(String name, boolean value);
  }
  //////////////////////////////////////////////////////////////////
  // number formatting used in XML export
  //////////////////////////////////////////////////////////////////

  static public String writeThis(final boolean val)
  {
    if (val)
      return "true";
    else
      return "false";
  }

  static public String writeThis(final Boolean val)
  {
    return writeThis(val.booleanValue());
  }


  static public String writeThis(final int val)
  {
    return Integer.toString(val);
  }

  static public String writeThis(final long val)
  {
    return Long.toString(val);
  }

  static public String writeThis(final double val)
  {
    return shortFormat.format(val);
  }

  static public String writeThis(final Date val)
  {
    return RNdateFormat.format(val);
  }

  static public String writeThisLong(final double val)
  {
    return longFormat.format(val);
  }


}

