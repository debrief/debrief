package org.mwc.debrief.core.editors;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.mwc.cmap.core.DataTypes.Temporal.TimeControlPreferences;
import org.mwc.debrief.core.gpx.ImportGPX;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import Debrief.ReaderWriter.Replay.ImportReplay;
import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.NarrativeWrapper;
import Debrief.Wrappers.TrackWrapper;
import MWC.Algorithms.PlainProjection;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.Layers.OperateFunction;
import MWC.GenericData.HiResDate;
import MWC.GenericData.SteppingListener;
import MWC.GenericData.TimePeriod;
import MWC.GenericData.Watchable;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;
import MWC.TacticalData.Fix;
import MWC.TacticalData.NarrativeEntry;
import MWC.Utilities.TextFormatting.FormatRNDateTime;
import MWC.Utilities.TextFormatting.GMTDateFormat;

public class CoordinateRecorder implements PropertyChangeListener,
    SteppingListener
{
  private static List<NarrativeEntry> findEntries(final HiResDate startTime,
      final HiResDate endTime, final Layers layers)
  {
    final Layer narr = layers.findLayer(ImportReplay.NARRATIVE_LAYER);
    if (narr != null)
    {
      final TimePeriod.BaseTimePeriod period = new TimePeriod.BaseTimePeriod(
          startTime, endTime);
      final List<NarrativeEntry> res = new ArrayList<NarrativeEntry>();

      final NarrativeWrapper narrW = (NarrativeWrapper) narr;
      final Enumeration<Editable> iter = narrW.elements();
      while (iter.hasMoreElements())
      {
        final NarrativeEntry ne = (NarrativeEntry) iter.nextElement();
        if (period.contains(ne.getDTG()))
        {
          res.add(ne);
        }
      }

      return res;
    }
    return null;
  }

  /**
   * ok, inject our colors as extension objects.
   *
   * @param doc
   *          document we're working on.
   * @return
   */
  private static void injectColors(final Document doc,
      final Map<String, TrackWrapper> trackList)
  {

    // get the tracks
    final NodeList tracks = doc.getElementsByTagName("trk");

    final int len = tracks.getLength();
    for (int i = 0; i < len; i++)
    {
      final Element thisTrack = (Element) tracks.item(i);
      final String name = thisTrack.getElementsByTagName("name").item(0)
          .getTextContent();

      final TrackWrapper track = trackList.get(name);
      final Color color = track.getColor();

      final Element extensions = doc.createElement("extensions");
      final Element col = doc.createElement("color");
      col.setTextContent(color.toString());
      extensions.appendChild(col);
      thisTrack.appendChild(extensions);
    }
  }

  /**
   * ok, inject the plot dimensions as an extension object
   *
   * @param doc
   *          document we're working on.
   * @param dims
   *          the screen dimensions of the Debrief plot
   * @param interval
   *          the interval between auto time steps
   * @return
   */
  private static void injectDimensionsAndInterval(final Document doc,
      final Dimension dims, final long interval, final Element extensions)
  {

    // get the tracks
    final Node tracks = doc.getElementsByTagName("gpx").item(0);

    final Element col = doc.createElement("dimensions");
    col.setAttribute("width", "" + (int) dims.getWidth());
    col.setAttribute("height", "" + (int) dims.getHeight());
    extensions.appendChild(col);
    final Element inter = doc.createElement("interval");
    inter.setAttribute("millis", "" + interval);
    extensions.appendChild(inter);
    tracks.appendChild(extensions);
  }

  /**
   * put the narrative entries into the object
   *
   * @param doc
   * @param entries
   * @param extensions
   * @param worldIntervalMillis
   * @param modelIntervalMillis
   * @param startTime
   */
  private static void injectEntries(final Document doc,
      final List<NarrativeEntry> entries, final Element extensions,
      final long startTime, final long modelIntervalMillis,
      final long worldIntervalMillis)
  {
    if (entries != null && !entries.isEmpty())
    {
      // sort out a scale factor
      final double scale = ((double) worldIntervalMillis)
          / ((double) modelIntervalMillis);

      final SimpleDateFormat df = new GMTDateFormat("ddHHmm.ss");
      final Element dest = doc.createElement("NarrativeEntries");
      for (final NarrativeEntry t : entries)
      {
        final Element entry = doc.createElement("Entry");
        final String timeStr = df.format(t.getDTG().getDate());
        entry.setAttribute("dateStr", timeStr);
        entry.setAttribute("Text", t.getEntry());

        final double elapsed = t.getDTG().getDate().getTime() - startTime;
        final double scaled = elapsed * scale;

        entry.setAttribute("elapsed", "" + (long) scaled);

        dest.appendChild(entry);
      }
      extensions.appendChild(dest);
    }
  }

  private final Layers _myLayers;
  private final PlainProjection _projection;
  final private Map<String, TrackWrapper> _tracks =
      new HashMap<String, TrackWrapper>();

  final private List<String> _times = new ArrayList<String>();

  private boolean _running = false;

  final private TimeControlPreferences _timePrefs;

  private HiResDate _startTime;

  public CoordinateRecorder(final Layers _myLayers,
      final PlainProjection plainProjection,
      final TimeControlPreferences timePreferences)
  {
    this._myLayers = _myLayers;
    _projection = plainProjection;
    _timePrefs = timePreferences;
  }

  private Document loadDocument(final String initialDoc)
  {
    Document doc = null;
    try
    {
      final DocumentBuilder parser = DocumentBuilderFactory.newInstance()
          .newDocumentBuilder();
      final InputStream stream = new ByteArrayInputStream(initialDoc.getBytes(
          StandardCharsets.UTF_8));
      doc = parser.parse(stream);
    }
    catch (final ParserConfigurationException e)
    {
      e.printStackTrace();
    }
    catch (final SAXException e)
    {
      e.printStackTrace();
    }
    catch (final IOException e)
    {
      e.printStackTrace();
    }
    return doc;
  }

  private void outputDocument(final Document doc)
      throws TransformerFactoryConfigurationError
  {
    final DOMSource domSource = new DOMSource(doc);
    final StringWriter writer2 = new StringWriter();
    final StreamResult result = new StreamResult(writer2);
    final TransformerFactory tf = TransformerFactory.newInstance();
    Transformer transformer;
    try
    {
      transformer = tf.newTransformer();
      transformer.setOutputProperty(OutputKeys.INDENT, "yes");
      transformer.transform(domSource, result);
      System.out.println(writer2.toString());
    }
    catch (final TransformerConfigurationException e)
    {
      e.printStackTrace();
    }
    catch (final TransformerException e)
    {
      e.printStackTrace();
    }
  }

  @Override
  public void propertyChange(final PropertyChangeEvent evt)
  {
    if (!_running)
      return;

    final Dimension screen = _projection.getScreenArea();

    // get the new time.
    final HiResDate timeNow = (HiResDate) evt.getNewValue();

    _times.add(FormatRNDateTime.toMediumString(timeNow.getDate().getTime()));

    final OperateFunction outputIt = new OperateFunction()
    {

      @Override
      public void operateOn(final Editable item)
      {
        final TrackWrapper track = (TrackWrapper) item;
        final Watchable[] items = track.getNearestTo(timeNow);
        if (items != null && items.length > 0)
        {
          final FixWrapper fix = (FixWrapper) items[0];

          TrackWrapper match = _tracks.get(track.getName());
          if (match == null)
          {
            match = new TrackWrapper();
            match.setName(track.getName());
            match.setColor(track.getColor());
            _tracks.put(track.getName(), match);
          }

          final Point point = _projection.toScreen(fix.getLocation());

          // swap y axis
          point.setLocation(point.getX(), screen.getHeight() - point.getY());

          final WorldLocation newLoc = new WorldLocation(point.getY(), point
              .getX(), fix.getLocation().getDepth());
          final double courseRads = MWC.Algorithms.Conversions.Degs2Rads(fix
              .getCourseDegs());
          final double speedYps = new WorldSpeed(fix.getSpeed(), WorldSpeed.Kts)
              .getValueIn(WorldSpeed.ft_sec) / 3;
          final Fix fix2 = new Fix(timeNow, newLoc, courseRads, speedYps);
          final FixWrapper fw2 = new FixWrapper(fix2);
          match.addFix(fw2);
        }
      }
    };
    _myLayers.walkVisibleItems(TrackWrapper.class, outputIt);
  }

  @Override
  public void startStepping(final HiResDate now)
  {
    _tracks.clear();
    _times.clear();
    _running = true;
    _startTime = now;
  }

  @Override
  public void stopStepping(final HiResDate now)
  {
    _running = false;

    final List<TrackWrapper> list = new ArrayList<TrackWrapper>();
    list.addAll(_tracks.values());

    // output tracks to a string
    final StringWriter writer = new StringWriter();
    ImportGPX.doExport(list, writer);

    final String initialDoc = writer.toString();

    // load the data as a DOM
    final Document doc = loadDocument(initialDoc);

    if (doc != null)
    {
      // put the colors in, as extensions
      injectColors(doc, _tracks);

      final Element extensions = doc.createElement("extensions");

      // what's the real world time step?
      final long intervalMillis = _timePrefs.getAutoInterval().getMillis();

      // and the model world time step?
      final long modelIntervalMillis = _timePrefs.getSmallStep().getMillis();

      injectDimensionsAndInterval(doc, _projection.getScreenArea(),
          intervalMillis, extensions);

      // also get the narrative entries
      final List<NarrativeEntry> entries = findEntries(_startTime, now,
          _myLayers);
      injectEntries(doc, entries, extensions, _startTime.getDate().getTime(),
          modelIntervalMillis, intervalMillis);

      outputDocument(doc);
    }
  }
}
