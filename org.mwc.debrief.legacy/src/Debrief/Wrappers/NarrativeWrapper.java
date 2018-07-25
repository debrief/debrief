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
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 */
// $RCSfile: NarrativeWrapper.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.12 $
// $Log: NarrativeWrapper.java,v $
// Revision 1.12  2006/09/25 14:51:15  Ian.Mayo
// Respect new "has children" property of Layers
//
// Revision 1.11  2006/08/08 13:42:48  Ian.Mayo
// Refactoring to make more narratives versatile across Debrief & ASSET
//
// Revision 1.10  2006/08/08 12:55:31  Ian.Mayo
// Restructure loading narrative entries (so we can see it from CMAP)
//
// Revision 1.9  2006/07/17 11:07:09  Ian.Mayo
// Provide export functionality
//
// Revision 1.8  2005/05/12 14:11:46  Ian.Mayo
// Allow import of typed-narrative entry
//
// Revision 1.7  2004/11/25 10:24:47  Ian.Mayo
// Switch to Hi Res dates
//
// Revision 1.6  2004/09/10 09:11:27  Ian.Mayo
// Correct prior mistaken implementation of add(Editable) - we should have just changed the signature of add(Plottable) et al
//
// Revision 1.5  2004/09/09 10:51:55  Ian.Mayo
// Provide missing methods from Layers structure.  Don't know why they had been missing for so long.  Poss disconnect between ASSET/Debrief development trees
//
// Revision 1.4  2004/09/09 10:23:12  Ian.Mayo
// Reflect method name change in Layer interface
//
// Revision 1.3  2004/02/25 09:26:56  Ian.Mayo
// Make narrative bits serializable to they can be cut from the layer manager
//
// Revision 1.2  2003/07/25 11:40:20  Ian.Mayo
// Use correct range from value
//
// Revision 1.1.1.2  2003/07/21 14:49:24  Ian.Mayo
// Re-import Java files to keep correct line spacing
//
// Revision 1.7  2003-07-01 14:13:00+01  ian_mayo
// Correct comparator so that we do allow multiple narrative entries at the same DTG
//
// Revision 1.6  2003-03-19 15:36:53+00  ian_mayo
// improvements according to IntelliJ inspector
//
// Revision 1.5  2002-10-30 16:27:27+00  ian_mayo
// tidy up (shorten) display names of editables
//
// Revision 1.4  2002-10-28 09:04:31+00  ian_mayo
// provide support for variable thickness of lines in tracks, etc
//
// Revision 1.3  2002-10-01 15:41:42+01  ian_mayo
// make final methods & classes final (following IDEA analysis)
//
// Revision 1.2  2002-05-28 09:25:13+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:11:39+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-23 12:28:25+01  ian_mayo
// Initial revision
//
// Revision 1.2  2001-08-29 19:17:26+01  administrator
// Reflect package change of PlainWrapper
//
// Revision 1.1  2001-07-31 16:37:21+01  administrator
// show the length of the narrative list when we get its name
//
// Revision 1.0  2001-07-17 08:41:10+01  administrator
// Initial revision
//
// Revision 1.3  2001-07-16 15:02:10+01  novatech
// provide methods to meet new Plottable signature (setVisible)
//
// Revision 1.2  2001-07-09 14:02:47+01  novatech
// let NarrativeWrapper handle the stepper control
//
// Revision 1.1  2001-07-06 16:00:27+01  novatech
// Initial revision
//

package Debrief.Wrappers;

import java.beans.BeanDescriptor;
import java.beans.IntrospectionException;
import java.beans.MethodDescriptor;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyDescriptor;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.Vector;
import java.util.concurrent.ConcurrentSkipListSet;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import Debrief.ReaderWriter.Replay.ImportReplay;
import Debrief.ReaderWriter.XML.Tactical.NarrativeHandler;
import MWC.GUI.Editable;
import MWC.GUI.GriddableSeriesMarker;
import MWC.GUI.Layers;
import MWC.GUI.Plottable;
import MWC.GUI.TimeStampedDataItem;
import MWC.GenericData.HiResDate;
import MWC.GenericData.TimePeriod;
import MWC.TacticalData.IRollingNarrativeProvider;
import MWC.TacticalData.NarrativeEntry;
import MWC.Utilities.ReaderWriter.XML.MWCXMLReaderWriter;
import MWC.Utilities.TextFormatting.GMTDateFormat;
import junit.framework.TestCase;

public final class NarrativeWrapper extends MWC.GUI.PlainWrapper implements
    MWC.GUI.Layer, IRollingNarrativeProvider, GriddableSeriesMarker
{

  /*
   * embedded class to allow us to pass the local iterator (Iterator) used internally outside as an
   * Enumeration
   */
  protected static final class IteratorWrapper implements
      java.util.Enumeration<Editable>
  {
    private final java.util.Iterator<Editable> _val;

    public IteratorWrapper(final java.util.Iterator<Editable> iterator)
    {
      _val = iterator;
    }

    @Override
    public final boolean hasMoreElements()
    {
      return _val.hasNext();

    }

    @Override
    public final Editable nextElement()
    {
      return _val.next();
    }
  }

  /**
   * the definition of what is editable about this object
   */
  public final class NarrativeInfo extends MWC.GUI.Editable.EditorType
  {

    /**
     * constructor for editable details of a set of Layers
     *
     * @param data
     *          the Layers themselves
     */
    public NarrativeInfo(final NarrativeWrapper data)
    {
      super(data, data.getName(), "Narrative");
    }

    /**
     * return a description of this bean, also specifies the custom editor we use
     *
     * @return the BeanDescriptor
     */
    @Override
    public final BeanDescriptor getBeanDescriptor()
    {
      final BeanDescriptor bp = new BeanDescriptor(NarrativeWrapper.class,
          Debrief.GUI.Panels.NarrativeViewer.class);
      bp.setDisplayName("Narrative Viewer");
      return bp;
    }

    @Override
    public final MethodDescriptor[] getMethodDescriptors()
    {
      // just add the reset color field first
      final Class<NarrativeWrapper> c = NarrativeWrapper.class;

      final MethodDescriptor[] mds =
      {method(c, "exportShape", null, "Export Shape")};

      return mds;
    }

    /**
     * The things about these Layers which are editable. We don't really use this list, since we
     * have our own custom editor anyway
     *
     * @return property descriptions
     */
    @Override
    public final PropertyDescriptor[] getPropertyDescriptors()
    {
      try
      {
        final PropertyDescriptor[] res =
        {prop("Name", "the name for this narrative"),};

        return res;
      }
      catch (final IntrospectionException e)
      {
        return super.getPropertyDescriptors();
      }
    }
  }

  public static class TestMe extends TestCase
  {
    
    
    public void testGridding()
    {
      NarrativeWrapper narrative = new NarrativeWrapper("Test narrative");
      NarrativeEntry entry = new NarrativeEntry("some track", new HiResDate(2000000), "Some entry text");
      
      narrative.add(entry);
      
      assertEquals("correct size", 1, narrative.size());
      
      NarrativeEntry dummy = (NarrativeEntry) narrative.makeCopy(entry);
      assertEquals(dummy.getDTG(), entry.getDTG());
      assertEquals(dummy.getColor(), entry.getColor());
      assertEquals(entry.getSource(), dummy.getSource());
      assertEquals(entry.getTrackName(), dummy.getTrackName());
      assertEquals(entry.getVisible(), dummy.getVisible());
      
      // we force the entry to be different, since identical copies get silently dropped.
      assertTrue(!entry.getEntry().equals(dummy.getEntry()));
      
      narrative.add(dummy);
      assertEquals("correct size", 2, narrative.size());
      
      // try to delete the enty
      narrative.removeElement(entry);
      assertEquals("correct size", 1, narrative.size());
      
    }
    /**
     * note: the following test was produced to overcome a specific issue. The narrative wrapper was
     * successfully ignoring duplicate entries, except after a file had been save/restored.
     *
     * @throws ParserConfigurationException
     * @throws TransformerException
     * @throws SAXException
     */
    public void testDuplicates() throws ParserConfigurationException,
        TransformerException, SAXException
    {
      final NarrativeWrapper narr = new NarrativeWrapper("Some title");
      assertEquals("empty", 0, narr.size());

      final NarrativeEntry n1 = new NarrativeEntry("track", new HiResDate(3000),
          "some entry");
      narr.add(n1);

      assertEquals("has one", 1, narr.size());

      final NarrativeEntry n2 = new NarrativeEntry("track", new HiResDate(3100),
          "some entry");
      narr.add(n2);

      assertEquals("has two", 2, narr.size());

      final NarrativeEntry n3 = new NarrativeEntry("track", new HiResDate(3100),
          "some entry");
      narr.add(n3);

      assertEquals("still has two", 2, narr.size());

      // make tiny change
      final NarrativeEntry n4 = new NarrativeEntry("track", new HiResDate(3100),
          "some entry.");
      narr.add(n4);

      assertEquals("now has three", 3, narr.size());

      // hmm, we need to export then reload, to check for matching hashcode
      final Document doc = DocumentBuilderFactory.newInstance()
          .newDocumentBuilder().newDocument();
      final org.w3c.dom.Element plt = doc.createElement("narrative");
      plt.setAttribute("Name", ImportReplay.NARRATIVE_LAYER);
      doc.appendChild(plt);
      NarrativeHandler.EntryHandler.exportEntry(n4, plt, doc);

      // output to String
      final TransformerFactory tf = TransformerFactory.newInstance();
      final Transformer transformer = tf.newTransformer();
      transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
      final StringWriter writer = new StringWriter();
      transformer.transform(new DOMSource(doc), new StreamResult(writer));
      final String output = writer.getBuffer().toString().replaceAll("\n|\r",
          "");

      // ok, put it into an input strean
      final InputStream stream = new ByteArrayInputStream(output.getBytes(
          StandardCharsets.UTF_8));

      // ok, re-import it
      final Layers parent = new Layers();
      final NarrativeHandler handler = new NarrativeHandler(parent);

      MWCXMLReaderWriter.importThis(handler, "some name", stream);

      // get the contents
      final NarrativeWrapper narrLayer = (NarrativeWrapper) parent.findLayer(
          ImportReplay.NARRATIVE_LAYER);

      final NarrativeEntry theEntry = (NarrativeEntry) narrLayer.elements()
          .nextElement();

      narr.add(theEntry);

      assertEquals("still has three", 3, narr.size());

    }
  }

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  /**
   * property type to signify data being added or removed
   */
  public final static String CONTENTS_CHANGED = "CONTENTS_CHANGED";

  @SuppressWarnings("deprecation")
  public static NarrativeWrapper createDummyData(final String title,
      final int len)
  {
    final NarrativeWrapper res = new NarrativeWrapper(title);
    final Date newDate = new Date(2005, 06, (int) (Math.random() * 12),
        (int) (Math.random() * 13), 33);
    for (int i = 0; i < len; i++)
    {
      String entryTxt = "entry number " + i + " for narrative:" + title;

      if (Math.random() > 0.9)
      {
        entryTxt += "\n and more...";
      }
      final NarrativeEntry ne = new NarrativeEntry(title, "type_" + (int) (Math
          .random() * 5), new HiResDate(newDate.getTime() + i * 10000, 0),
          entryTxt);

      res.add(ne);
    }

    return res;
  }

  private String _myName = "blank";
  /**
   * where we store our narrative data
   */
  private final ConcurrentSkipListSet<Editable> _myEntries;

  /**
   * our editor
   */
  transient private MWC.GUI.Editable.EditorType _myEditor;

  /**
   * the line width to draw
   */
  private int _lineWidth = 1;

  /**
   * anybody listening to this narrative data
   *
   */
  private transient Vector<INarrativeListener> _myListeners;

  private final PropertyChangeListener _dateChangeListener;

  /**
   * constructor, of course.
   *
   * @param title
   */
  public NarrativeWrapper(final String title)
  {
    _dateChangeListener = new PropertyChangeListener()
    {
      @Override
      public void propertyChange(final PropertyChangeEvent evt)
      {
        // double-check it's the date
        if (evt.getPropertyName().equals(NarrativeEntry.DTG))
        {
          // ok, remove this entry
          _myEntries.remove(evt.getSource());

          // and replace it
          _myEntries.add((Editable) evt.getSource());
        }
      }
    };
    _myEntries = new ConcurrentSkipListSet<Editable>();
    _myName = title;
  }

  @Override
  public final void add(final MWC.GUI.Editable editable)
  {
    // check it's a narrative entry
    if (editable instanceof NarrativeEntry)
    {
      // listen for date changes, since we'll have to re-order
      editable.getInfo().addPropertyChangeListener(NarrativeEntry.DTG,
          _dateChangeListener);

      _myEntries.add(editable);

      // and inform anybody who happens to be listening
      getSupport().firePropertyChange(CONTENTS_CHANGED, null, this);

      // also tell the listeners
      if (_myListeners != null)
      {
        for (final Iterator<INarrativeListener> iter = _myListeners
            .iterator(); iter.hasNext();)
        {
          final INarrativeListener thisL = iter.next();
          thisL.newEntry((NarrativeEntry) editable);
        }
      }
    }
  }

  @Override
  public void addNarrativeListener(final String category,
      final INarrativeListener listener)
  {
    if (_myListeners == null)
      _myListeners = new Vector<INarrativeListener>(1, 1);

    _myListeners.add(listener);
  }

  @Override
  public final void append(final MWC.GUI.Layer layer)
  {
    // don't bother
  }

  @Override
  public void doSave(final String message)
  {
    throw new RuntimeException(
        "should not have called manual save for Sensor Wrapper");
  }

  @Override
  public final java.util.Enumeration<Editable> elements()
  {
    return new IteratorWrapper(_myEntries.iterator());
  }

  @Override
  public final void exportShape()
  {
    MWC.Utilities.ReaderWriter.ImportManager.exportThis(this);
  }

  /**
   * filter the list to the specified time period
   */
  public void filterListTo(final TimePeriod period)
  {
    boolean updated = false;
    final Enumeration<Editable> enumer = elements();
    while (enumer.hasMoreElements())
    {
      final Plottable thisP = (Plottable) enumer.nextElement();
      if (thisP instanceof NarrativeEntry)
      {
        final NarrativeEntry wl = (NarrativeEntry) thisP;

        final boolean isVisible = period.contains(wl.getDTG());

        if (!updated && wl.getVisible() != isVisible)
        {
          updated = true;
        }

        wl.setVisible(isVisible);
      }
    }

    // ok, has there been a change?
    if (updated)
    {
      // and the narrative listeners, if we have one
      if (_myListeners != null)
      {
        for (final Iterator<INarrativeListener> iter = _myListeners
            .iterator(); iter.hasNext();)
        {
          final INarrativeListener thisL = iter.next();
          thisL.filtered();
        }
      }

    }
  }

  @Override
  public final MWC.GenericData.WorldArea getBounds()
  {
    return null;
  }

  public final java.util.AbstractCollection<Editable> getData()
  {
    return _myEntries;
  }

  /**
   * convenience function to find the narrative entry immediately before the supplied dtg.
   *
   * @param dtg
   *          the time to find an entry for
   * @return
   */
  public NarrativeEntry getEntryNearestTo(final HiResDate dtg)
  {
    NarrativeEntry res = null;

    // ahh, do we have data?
    if (!_myEntries.isEmpty())
    {
      final NarrativeEntry firstN = (NarrativeEntry) _myEntries.first();
      final NarrativeEntry lastN = (NarrativeEntry) _myEntries.last();
      // just see if this dtg is outside our time period
      if (dtg.lessThan(firstN.getDTG()))
      {
        // hmm, off the start of the plot
        res = null;
      }
      else if (dtg.greaterThan(lastN.getDTG()))
      {
        res = (NarrativeEntry) _myEntries.last();
      }
      else
      {

        // create an object to use for comparisons
        final NarrativeEntry toTest = new NarrativeEntry("", dtg, " ");

        // and retrieve all items before this one
        final SortedSet<Editable> before = _myEntries.headSet(toTest);

        // did we find any?
        if (before != null)
          res = (NarrativeEntry) before.last();
      }
    }

    return res;
  }

  @Override
  public final MWC.GUI.Editable.EditorType getInfo()
  {
    if (_myEditor == null)
      _myEditor = new NarrativeInfo(this);

    return _myEditor;
  }

  /**
   * the line thickness (convenience wrapper around width)
   *
   * @return
   */
  @Override
  public int getLineThickness()
  {
    return _lineWidth;
  }

  @Override
  public final String getName()
  {
    return _myName;
  }

  /**
   * ok, retrieve the back-history
   *
   * @param categories
   * @return
   */
  @Override
  public NarrativeEntry[] getNarrativeHistory(final String[] categories)
  {
    NarrativeEntry[] res = new NarrativeEntry[]
    {};
    // ok, cn
    final Vector<NarrativeEntry> theNarrs = new Vector<NarrativeEntry>(10, 10);
    final Iterator<Editable> iter = getData().iterator();
    while (iter.hasNext())
    {
      final NarrativeEntry ne = (NarrativeEntry) iter.next();
      theNarrs.add(ne);
    }
    res = theNarrs.toArray(res);

    return res;
  }

  @Override
  public Editable getSampleGriddable()
  {
    Editable res = null;

    // check we have an item before we edit it
    final Enumeration<Editable> eles = this.elements();
    if (eles.hasMoreElements())
      res = eles.nextElement();
    return res;
  }

  /**
   * find the time period covered by this narrative data
   *
   * @return the inclusive time period
   */
  public TimePeriod getTimePeriod()
  {
    TimePeriod res = null;

    final HiResDate start = ((NarrativeEntry) _myEntries.first()).getDTG();
    final HiResDate end = ((NarrativeEntry) _myEntries.last()).getDTG();

    res = new TimePeriod.BaseTimePeriod(start, end);

    return res;
  }

  @Override
  public final boolean hasEditor()
  {
    return true;
  }

  @Override
  public boolean hasOrderedChildren()
  {
    return true;
  }

  @Override
  public TimeStampedDataItem makeCopy(final TimeStampedDataItem item)
  {
    if (false == item instanceof NarrativeEntry)
    {
      throw new IllegalArgumentException(
          "I am expecting the Observation's, don't know how to copy " + item);
    }

    final NarrativeEntry template = (NarrativeEntry) item;

    // note, we silently drop duplicate narratives. So, consciously ensure the
    // new entry has unique text
    final SimpleDateFormat sdf = new GMTDateFormat("HH:mm:ss");
    final String pending = "Pending_" + sdf.format(new Date());

    final NarrativeEntry result = new NarrativeEntry(template.getTrackName(),
        template.getType(), new HiResDate(template.getDTG()), pending);
    result.setVisible(template.getVisible());
    return result;
  }

  @Override
  public final void paint(final MWC.GUI.CanvasType canvas)
  {
    // don't bother
  }

  @Override
  public final void removeElement(final MWC.GUI.Editable editable)
  {
    // check it's a narrative entry
    if (editable instanceof NarrativeEntry)
    {
      _myEntries.remove(editable);

      // and inform anybody who happens to be listening
      getSupport().firePropertyChange(CONTENTS_CHANGED, null, this);

      // stop listening to it
      editable.getInfo().removePropertyChangeListener(NarrativeEntry.DTG,
          _dateChangeListener);

      // and the narrative listeners, if we have one
      if (_myListeners != null)
      {
        for (final Iterator<INarrativeListener> iter = _myListeners
            .iterator(); iter.hasNext();)
        {
          final INarrativeListener thisL = iter.next();
          thisL.entryRemoved((NarrativeEntry) editable);
        }
      }
    }
  }

  @Override
  public void removeNarrativeListener(final String category,
      final INarrativeListener listener)
  {
    _myListeners.remove(listener);
  }

  @Override
  public boolean requiresManualSave()
  {
    return false;
  }

  /**
   * the line thickness (convenience wrapper around width)
   */
  public void setLineThickness(final int val)
  {
    _lineWidth = val;
  }

  @Override
  public final void setName(final String name)
  {
    _myName = name;
  }

  /**
   * how many entries do we have?
   *
   */
  @Override
  public int size()
  {
    return _myEntries.size();
  }

  @Override
  public boolean supportsAddRemove()
  {
    return true;
  }

  @Override
  public final String toString()
  {
    return getName() + " (" + _myEntries.size() + " items)";
  }

}
