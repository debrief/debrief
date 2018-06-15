package org.mwc.debrief.core.editors;

import java.awt.Color;
import java.awt.Point;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
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

import org.mwc.debrief.core.gpx.ImportGPX;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.TrackWrapper;
import MWC.Algorithms.PlainProjection;
import MWC.GUI.Editable;
import MWC.GUI.Layers;
import MWC.GUI.Layers.OperateFunction;
import MWC.GenericData.HiResDate;
import MWC.GenericData.SteppingListener;
import MWC.GenericData.Watchable;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;
import MWC.TacticalData.Fix;
import MWC.Utilities.TextFormatting.FormatRNDateTime;

public class CoordinateRecorder implements PropertyChangeListener,
    SteppingListener
{
  private final Layers _myLayers;
  private final PlainProjection _projection;
  final private Map<String, TrackWrapper> _tracks =
      new HashMap<String, TrackWrapper>();
  final private List<String> _times = new ArrayList<String>();
  private boolean _running = false;

  public CoordinateRecorder(final Layers _myLayers,
      final PlainProjection plainProjection)
  {
    this._myLayers = _myLayers;
    _projection = plainProjection;
  }

  private Document injectColors(final String initialDoc)
  {
    // ok, inject our colors as extension objects.
    try
    {
      final DocumentBuilder parser = DocumentBuilderFactory.newInstance()
          .newDocumentBuilder();
      final InputStream stream = new ByteArrayInputStream(initialDoc.getBytes(
          StandardCharsets.UTF_8));

      final Document doc = parser.parse(stream);
      //
      final NodeList tracks = doc.getElementsByTagName("trk");

      final int len = tracks.getLength();
      for (int i = 0; i < len; i++)
      {
        final Element thisTrack = (Element) tracks.item(i);
        final String name = thisTrack.getElementsByTagName("name").item(0)
            .getTextContent();

        final TrackWrapper track = _tracks.get(name);
        final Color color = track.getColor();

        final Element extensions = doc.createElement("extensions");
        final Element col = doc.createElement("color");
        col.setTextContent(color.toString());
        extensions.appendChild(col);
        thisTrack.appendChild(extensions);
      }
      return doc;
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
    return null;
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
  }

  @Override
  public void stopStepping(final HiResDate now)
  {
    _running = false;

    final StringWriter writer = new StringWriter();

    final List<TrackWrapper> list = new ArrayList<TrackWrapper>();
    list.addAll(_tracks.values());

    // output tracks object.
    ImportGPX.doExport(list, writer);

    final String initialDoc = writer.toString();

    final Document doc = injectColors(initialDoc);

    if (doc != null)
    {
      outputDocument(doc);
    }

  }
}
