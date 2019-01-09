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
package Debrief.ReaderWriter.Replay;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import Debrief.GUI.Frames.Application;
import Debrief.ReaderWriter.Replay.ImportReplay.ProvidesModeSelector.ImportSettings;
import Debrief.Wrappers.DynamicShapeLayer;
import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.IDynamicShapeWrapper;
import Debrief.Wrappers.LabelWrapper;
import Debrief.Wrappers.SensorContactWrapper;
import Debrief.Wrappers.SensorWrapper;
import Debrief.Wrappers.ShapeWrapper;
import Debrief.Wrappers.TMAContactWrapper;
import Debrief.Wrappers.TMAWrapper;
import Debrief.Wrappers.TrackWrapper;
import Debrief.Wrappers.DynamicTrackShapes.DynamicTrackShapeSetWrapper;
import Debrief.Wrappers.DynamicTrackShapes.DynamicTrackShapeWrapper;
import Debrief.Wrappers.Track.LightweightTrackWrapper;
import Debrief.Wrappers.Track.TrackSegment;
import MWC.GUI.BaseLayer;
import MWC.GUI.Editable;
import MWC.GUI.ExportLayerAsSingleItem;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.Layers.INewItemListener;
import MWC.GUI.PlainWrapper;
import MWC.GUI.Plottable;
import MWC.GUI.ToolParent;
import MWC.GUI.Properties.DebriefColors;
import MWC.GUI.Properties.LineStylePropertyEditor;
import MWC.GUI.Shapes.PlainShape;
import MWC.GUI.Shapes.Symbols.SymbolFactory;
import MWC.GUI.Tools.Action;
import MWC.GenericData.HiResDate;
import MWC.GenericData.Watchable;
import MWC.TacticalData.NarrativeEntry;
import MWC.TacticalData.NarrativeWrapper;
import MWC.Utilities.ReaderWriter.ExtensibleLineImporter;
import MWC.Utilities.ReaderWriter.PlainImporterBase;
import MWC.Utilities.ReaderWriter.PlainLineImporter;
import MWC.Utilities.ReaderWriter.PlainLineImporter.ImportRequiresFinalisation;
import MWC.Utilities.ReaderWriter.ReaderMonitor;
import MWC.Utilities.TextFormatting.DebriefFormatDateTime;

/**
 * class to read in a complete replay file. The class knows of the types of data in Replay format,
 * and users the correct import filters accordingly.
 */

public class ImportReplay extends PlainImporterBase
{

  final static class doublet
  {
    public final String label;

    public final Color color;

    public doublet(final String theLabel, final Color theColor)
    {
      label = theLabel;
      color = theColor;
    }
  }

  /**
   * interface which we use to implement class capable of formatting a set of layers once they've
   * been read in
   */
  public static interface LayersFormatter
  {
    public void formatLayers(List<Layer> newLayers);
  }

  /**
   * interface for class that is able to retrieve the import mode from the user
   * 
   * @author ianmayo
   * @see ImportReplay.TRACK_IMPORT_MODE
   */
  public static interface ProvidesModeSelector
  {
    public static class ImportSettings
    {
      /**
       * whether to use DR/OTG
       * 
       */
      public final String importMode;

      /**
       * frequency to resample incoming data
       * 
       */
      public final Long sampleFrequency;

      public ImportSettings(final String mode, final Long freq)
      {
        importMode = mode;
        sampleFrequency = freq;

        if (ImportReplay.IMPORT_AS_DR.equals(mode))
        {
          if (freq != null)
          {
            // this isn't allowed, throw a wobbly
            throw new RuntimeException(
                "Resample frequency not supported for DR import, since essential data would be lost");
          }
        }
      }

    }

    /**
     * find out the sample frequency for adding this data
     * 
     * @param trackName
     * @return
     */
    public Long getSelectedImportFrequency(final String trackName);

    /**
     * find out how the user wants to import the new REP file
     * 
     * @param trackName
     * @return
     */
    public ImportSettings getSelectedImportMode(final String trackName);
  }

  /**
   * interface for helpers that may be able to provide extra REP file importers
   * 
   */
  public static interface RepImportHelper
  {
    /**
     * provide an importer, if there is a suitable one
     * 
     * @param line
     *          line we're trying to import
     * @return a matching importer, if there is one.
     */
    public ExtensibleLineImporter canImport(final String line);
  }

  // ///////////////////////////////////////////////////////////////////////////////////////////
  // testing for this class
  // ////////////////////////////////////////////////////////////////////////////////////////////////
  static public final class testImport extends junit.framework.TestCase
  {
    public static class TestParent implements ToolParent, ProvidesModeSelector
    {
      final ImportSettings settings;
      final Long freq;

      public TestParent(final String mode, final Long freq)
      {
        settings = new ImportSettings(mode, freq);
        this.freq = freq;
      }

      @Override
      public void addActionToBuffer(final Action theAction)
      {

      }

      @Override
      public Map<String, String> getPropertiesLike(final String pattern)
      {
        return null;
      }

      @Override
      public String getProperty(final String name)
      {
        if (name.equals(ImportReplay.TRACK_IMPORT_MODE))
        {
          return settings.importMode;
        }
        else if (name.equals(ImportReplay.RESAMPLE_FREQUENCY))
        {
          return "" + settings.sampleFrequency;
        }
        else
        {
          return null;
        }
      }

      @Override
      public Long getSelectedImportFrequency(final String trackName)
      {
        return null;
      }

      @Override
      public ImportSettings getSelectedImportMode(final String trackName)
      {
        return settings;
      }

      @Override
      public void logError(final int status, final String text,
          final Exception e)
      {

      }

      @Override
      public void logError(final int status, final String text,
          final Exception e, final boolean revealLog)
      {
        logError(status, text, e);
      }

      @Override
      public void logStack(final int status, final String text)
      {
        logError(status, "Stack requested:" + text, null);
      }

      @Override
      public void restoreCursor()
      {

      }

      @Override
      public void setCursor(final int theCursor)
      {

      }

      @Override
      public void setProperty(final String name, final String value)
      {

      }
    }

    static public final String TEST_ALL_TEST_TYPE = "UNIT";

    private String fileName = "test.rep";

    boolean fileFinished = false;

    boolean allFilesFinished = false;

    public testImport(final String val)
    {
      super(val);
      final String fileRoot = "../org.mwc.debrief.legacy/src";

      //
      assertNotNull("Check data directory is configured", fileRoot);
      fileName = fileRoot + File.separator + fileName;

      // and check the file exists
      final File iFile = new File(fileName);
      assertTrue("Test file not found", iFile.exists());
    }

    private final void doReadRep(final String mode, final Long freq,
        final int LAYER_COUNT, final int NUM_FIXES, final boolean checkArea)
    {
      File testFile = null;

      // specify the parent object - so our processing can retrieve the
      // OTG setting
      ImportReplay.initialise(new TestParent(mode, freq));

      // can we load it directly
      testFile = new File(fileName);

      if (!testFile.exists())
      {

        // first try to get the URL of the image
        final java.lang.ClassLoader loader = getClass().getClassLoader();
        if (loader != null)
        {
          final java.net.URL imLoc = loader.getResource(fileName);
          if (imLoc != null)
          {
            testFile = new File(imLoc.getFile());
          }
        }
        else
        {
          fail("Failed to find class loader");
        }
      }

      // did we find it?
      assertTrue("Failed to find file:" + fileName, testFile.exists());

      // ok, now try to read it in
      final MWC.GUI.Layers _theLayers = new MWC.GUI.Layers();
      final File[] _theFiles = new File[]
      {testFile};

      // add the REP importer
      MWC.Utilities.ReaderWriter.ImportManager.addImporter(
          new Debrief.ReaderWriter.Replay.ImportReplay());

      // get our thread to import this
      final MWC.Utilities.ReaderWriter.ImportManager.BaseImportCaller reader =
          new MWC.Utilities.ReaderWriter.ImportManager.BaseImportCaller(
              _theFiles, _theLayers)
          {
            // handle completion of the full import process
            @Override
            public void allFilesFinished(final File[] fNames,
                final Layers newData)
            {
              allFilesFinished = true;
            }

            // handle the completion of each file
            @Override
            public void fileFinished(final File fName, final Layers newData)
            {
              fileFinished = true;
            }
          };

      // and start it running
      reader.start();

      // wait for the results
      while (reader.isAlive())
      {
        try
        {
          Thread.sleep(100);
        }
        catch (final java.lang.InterruptedException e)
        {
        }
      }

      // check it went ok
      assertTrue("File finished received", fileFinished);
      assertTrue("All Files finished received", allFilesFinished);

      assertEquals("Count of layers", LAYER_COUNT, _theLayers.size());

      final TrackWrapper track = (TrackWrapper) _theLayers.elementAt(0);
      final int numFixes = track.numFixes();
      assertEquals("got correct num fixes", NUM_FIXES, numFixes);

      if (checkArea)
      {
        // area of coverage
        final MWC.GenericData.WorldArea area = _theLayers.elementAt(0)
            .getBounds();
        super.assertEquals("tl lat of first layer", area.getTopLeft().getLat(),
            11.92276, 0.01);
        super.assertEquals("tl long of first layer", area.getTopLeft()
            .getLong(), -11.59394, 0.01);
        super.assertEquals("tl depth of first layer", area.getTopLeft()
            .getDepth(), 0, 0.00001);

        super.assertEquals("br lat of first layer", area.getBottomRight()
            .getLat(), 11.89421, 0.001);
        super.assertEquals("br long of first layer", area.getBottomRight()
            .getLong(), -11.59376, 0.00001);
        super.assertEquals("br depth of first layer", area.getBottomRight()
            .getDepth(), 0, 0.00001);
      }

      // check those narrative lines got read in
      final NarrativeWrapper narratives = (NarrativeWrapper) _theLayers
          .elementAt(2);
      assertEquals("have read in both narrative entries", 2, narratives.size());
    }

    private final static String shape_file =
        "../org.mwc.cmap.combined.feature/root_installs/sample_data/shapes.rep";
    private final static String boat_file =
        "../org.mwc.cmap.combined.feature/root_installs/sample_data/boat1.rep";
    public void testReadShapes() throws InterruptedException, IOException
    {
      final Layers tLayers = new Layers();

      // start off with the ownship track
      final File boatFile = new File(shape_file);
      assertTrue(boatFile.exists());
      final InputStream bs = new FileInputStream(boatFile);

      final ImportReplay trackImporter = new ImportReplay();
      ImportReplay.initialise(new ImportReplay.testImport.TestParent(
          ImportReplay.IMPORT_AS_OTG, 0L));
      trackImporter.importThis(shape_file, bs, tLayers);
      
       assertEquals("read in track", 14, tLayers.size());
      
       TrackWrapper track = (TrackWrapper) tLayers.findLayer("NEL STYLE");
       assertNotNull("found track", track);
      
       // get the positions
       Enumeration<Editable> pIter = track.getPositionIterator();
       FixWrapper f1 = (FixWrapper) pIter.nextElement();
       assertNotNull("found item", f1);
       assertEquals("got label", "120500", f1.getLabel());
       FixWrapper f2 = (FixWrapper) pIter.nextElement();
       assertEquals("got label", "Override label 1", f2.getLabel());
       assertEquals("no comment", null, f2.getComment());
       FixWrapper f3 = (FixWrapper) pIter.nextElement();
       assertEquals("got label", "Override label 2", f3.getLabel());
       assertEquals("no comment", null, f3.getComment());
       FixWrapper f4 = (FixWrapper) pIter.nextElement();
       assertEquals("got label", "Override label 3", f4.getLabel());
       assertEquals("no comment", "comment 3", f4.getComment());
       FixWrapper f5 = (FixWrapper) pIter.nextElement();
       assertEquals("got label", "0504", f5.getLabel());
       assertEquals("no comment", "comment 4", f5.getComment());
    }

    public final void testDRimport()
    {
      doReadRep(ImportReplay.IMPORT_AS_DR, null, 3, 25, true);
    }

    public final void testOTGimport1()
    {
      doReadRep(ImportReplay.IMPORT_AS_OTG, 0L, 3, 25, true);
    }

    public final void testOTGimport2()
    {
      doReadRep(ImportReplay.IMPORT_AS_OTG, 300000L, 3, 5, false);
    }

    public final void testOTGimport3()
    {
      doReadRep(ImportReplay.IMPORT_AS_OTG, Long.MAX_VALUE, 3, 1, false);
    }

    public void testTrailingComment()
    {
      final String test1 = " LABEL // COMMENT";
      assertEquals("LABEL", getLabel(test1));
      assertEquals("COMMENT", getComment(test1));

      final String test2 = " LABEL";
      assertEquals("LABEL", getLabel(test2));
      assertEquals(null, getComment(test2));

      final String test3 = " LABEL//";
      assertEquals("LABEL", getLabel(test3));
      assertEquals(null, getComment(test3));

      final String test4 = "//COMMENT";
      assertEquals(null, getLabel(test4));
      assertEquals("COMMENT", getComment(test4));

      final String test5 = "";
      assertEquals(null, getLabel(test5));
      assertEquals(null, getComment(test5));

      final String test6 = null;
      assertEquals(null, getLabel(test6));
      assertEquals(null, getComment(test6));
    }

    public final void testParseSymbology()
    {
      final String test =
          ";TEXT: CA[LAYER=Special_Layer] 21.42 0 0 N 21.88 0 0 W Other layer";
      final String test2 =
          ";TEXT: CA[LAYER=Special_Layer,TEST_ON=OFF] 21.42 0 0 N 21.88 0 0 W Other layer";

      final ImportReplay ir = new ImportReplay();

      assertEquals("String not found", null, ir.getThisSymProperty(test,
          "LAYDER"));
      assertEquals("String found", "Special_Layer", ir.getThisSymProperty(test,
          "LAYER"));

      assertEquals("String not found", null, ir.getThisSymProperty(test2,
          "LAYDER"));
      assertEquals("String found", "Special_Layer", ir.getThisSymProperty(test2,
          "LAYER"));
      assertEquals("String found", "OFF", ir.getThisSymProperty(test2,
          "TEST_ON"));
    }

    public final void testParseSymbologyColor()
    {
      final String test1 = ";TEXT: 5j 21.42 0 0 N 21.88 0 0 W Other buoy";

      final LabelWrapper label1 = (LabelWrapper) new ImportLabel().readThisLine(
          test1);

      // correct symbol
      assertEquals("sym type correct", "Kingpin", label1.getSymbolType());
      assertEquals("color correct", DebriefColors.LIGHT_GREEN, label1
          .getColor());

      final String test2 = ";TEXT: 4G 21.42 0 0 N 21.88 0 0 W Other buoy";

      final LabelWrapper label2 = (LabelWrapper) new ImportLabel().readThisLine(
          test2);

      // correct symbol
      assertEquals("sym type correct", "Hidar", label2.getSymbolType());
      assertEquals("color correct", DebriefColors.PURPLE, label2.getColor());
    }
    
    //verify layers get added and shapes get added on paste
    //verify that layers are removed when undone
    public final void testPasteRepShapes() {
      String textToPaste =
";LINE: @B 20 50 0 N 21 10 0 W 22 0 0 N 21 10 0 W test line\r\n"+
";VECTOR: @C 21.6 12 0 N 21.5 11 0 W 5000 270 test vector\r\n"+
";CIRCLE: @D    21.8 0 0 N 21.0 0 0 W 2000 test circle\r\n"+
";TEXT: @E 21.7 0 0 N 21.5 0 0 W test text\r\n"+ 
";TEXT: WB 21.72 0 0 N 21.52 0 0 W wreck symbol\r\n"+ 
";TEXT: CA[LAYER=Special_Layer] 21.42 0 0 N 21.88 0 0 W Other layer\r\n"+
";TEXT: CA[LAYER=Other_Special_Layer] 21.22 0 0 N 21.88 0 0 W Other layer 3\r\n"+
";ELLIPSE: @F 951212 060200 21.8 0 0 N 21.5 0 0 W 45.0 5000 3000 test ellipse\r\n"+
";POLY: @GA30 21.9 0 0 N 21.5 0 0 W 22 0 0 N 21.8 0 0 W 22.1 0 0 N 21.5 0 0 W test poly\r\n"+
";POLYLINE: @C 21.1 0 0 N 21.5 0 0 W 21.2 0 0 N 21.8 0 0 W 21.3 0 0 N 21.5 0 0 W test polyline\r\n"+
";NARRATIVE:  951212 050200 NEL_STYLE comment 3\r\n";
      ImportReplay testImporter = new ImportReplay();
      Layers tmpLayers = new Layers();
      Layers dest = new Layers();
      testImporter.setLayers(tmpLayers);
      int totalLines = textToPaste.split("\\r?\\n").length;
      testImporter.importThis(textToPaste,totalLines);
      ImportReplay.injectContent(tmpLayers, dest, true);
      assertElementsInLayers((Layer)tmpLayers.findLayer(ANNOTATION_LAYER), 8);
      assertElementsInLayers(tmpLayers.findLayer("Special_Layer"),1);
      assertElementsInLayers(tmpLayers.findLayer("Other_Special_Layer"),1);
      assertElementsInLayers(tmpLayers.findLayer(NARRATIVE_LAYER),1);
      //test undo
      ImportReplay.injectContent(dest, tmpLayers, false);
      //first load a shapes rep or someother rep file, add more shapes to it and see
      //if they are added to the correct layer.
      //verify undo
      assertNull(((Layer)tmpLayers.findLayer(ANNOTATION_LAYER)));
      assertNull(tmpLayers.findLayer("Special_Layer"));
      assertNull(tmpLayers.findLayer("Other_Special_Layer"));
      assertNull(tmpLayers.findLayer(NARRATIVE_LAYER));
    }
    //verify dynamic layers get added on paste
    public final void testPasteRepDynamicShapes() {
      String textToPaste =";DYNAMIC_RECT: @A \"Dynamic A\" 951212 051000.000 22 00 0 N 21 00 0 W 21 50 0 N 20 50 0 W dynamic A rect 1\r\n"+
    ";DYNAMIC_CIRCLE: @A \"Dynamic A\" 951212 052100.000 21 00 0 N 20 53 0 W 2000 dynamic A circ 12\r\n"+
          ";DYNAMIC_POLY: @A \"Dynamic A\" 951212 052600.000 20 35 0 N 21 02 0 W 20 35 0 N 20 55 0 W 20 42 0 N 20 52 0 W 20 45 0 N 21 00 0 W  dynamic A POLY 170";
      ImportReplay testImporter = new ImportReplay();
      Layers tmpLayers = new Layers();
      Layers dest = new Layers();
      testImporter.setLayers(tmpLayers);
      int totalLines = textToPaste.split("\\r?\\n").length;
      testImporter.importThis(textToPaste,totalLines);
      ImportReplay.injectContent(tmpLayers, dest, true);
      assertElementsInLayers((Layer)tmpLayers.findLayer("Dynamic A"), 3);
      ImportReplay.injectContent(dest, tmpLayers, false);
      assertNull(((Layer)tmpLayers.findLayer("Dynamic A")));
      
    }
    
    //test if previously loaded layers remains after pasting new content 
    public void testPasteRepExistingFile(){
      String textToPaste =
          ";LINE: @B 20 50 0 N 21 10 0 W 22 0 0 N 21 10 0 W test line\r\n"+
          ";VECTOR: @C 21.6 12 0 N 21.5 11 0 W 5000 270 test vector\r\n"+
          ";CIRCLE: @D    21.8 0 0 N 21.0 0 0 W 2000 test circle\r\n"+
          ";TEXT: @E 21.7 0 0 N 21.5 0 0 W test text\r\n"+ 
          ";TEXT: WB 21.72 0 0 N 21.52 0 0 W wreck symbol\r\n"+ 
          ";TEXT: CA[LAYER=Special_Layer] 21.42 0 0 N 21.88 0 0 W Other layer\r\n"+
          ";TEXT: CA[LAYER=Other_Special_Layer] 21.22 0 0 N 21.88 0 0 W Other layer 3\r\n"+
          ";ELLIPSE: @F 951212 060200 21.8 0 0 N 21.5 0 0 W 45.0 5000 3000 test ellipse\r\n"+
          ";POLY: @GA30 21.9 0 0 N 21.5 0 0 W 22 0 0 N 21.8 0 0 W 22.1 0 0 N 21.5 0 0 W test poly\r\n"+
          ";POLYLINE: @C 21.1 0 0 N 21.5 0 0 W 21.2 0 0 N 21.8 0 0 W 21.3 0 0 N 21.5 0 0 W test polyline\r\n"+
          ";NARRATIVE:  951212 050200 NEL_STYLE comment 3\r\n";
      try {
        final Layers tLayers = new Layers();

        // start off with the ownship track
        final File boatFile = new File(boat_file);
        assertTrue(boatFile.exists());
        final InputStream bs = new FileInputStream(boatFile);
        final ImportReplay trackImporter = new ImportReplay();
        ImportReplay.initialise(new ImportReplay.testImport.TestParent(
            ImportReplay.IMPORT_AS_OTG, 0L));
        int totalLines = textToPaste.split("\\r?\\n").length;
        trackImporter.importThis(shape_file, bs, tLayers);
        assertNotNull((Layer)tLayers.findLayer("NELSON"));
        Layers tmpLayers = new Layers();
        trackImporter.setLayers(tmpLayers);
        trackImporter.importThis(textToPaste,totalLines);
        ImportReplay.injectContent(tmpLayers,tLayers, true);
        assertNotNull((Layer)tLayers.findLayer("NELSON"));
        assertElementsInLayers((Layer)tLayers.findLayer(ANNOTATION_LAYER), 8);
        assertElementsInLayers(tLayers.findLayer("Special_Layer"),1);
        assertElementsInLayers(tLayers.findLayer("Other_Special_Layer"),1);
        assertElementsInLayers(tLayers.findLayer(NARRATIVE_LAYER),1);
        //test undo
        ImportReplay.injectContent(tmpLayers, tLayers, false);
        //first load a shapes rep or someother rep file, add more shapes to it and see
        //if they are added to the correct layer.
        //verify undo
        assertNull(((Layer)tLayers.findLayer(ANNOTATION_LAYER)));
        assertNull(tLayers.findLayer("Special_Layer"));
        assertNull(tLayers.findLayer("Other_Special_Layer"));
        assertNull(tLayers.findLayer(NARRATIVE_LAYER));
        
      }catch(Exception e) {
        e.printStackTrace();
        fail(e.getMessage());
      }
    }
    
    
    private void assertElementsInLayers(final Layer layer,final int count) {
      assertTrue(layer.elements().hasMoreElements());
      Enumeration<Editable> elements = layer.elements();
      int lineCount=0;
      while(elements.hasMoreElements()){
        elements.nextElement();
       lineCount++;
      }
      assertEquals(lineCount,count);
    }
    public void testIsContentImportable() {
      String textToPaste =
          ";LINE: @B 20 50 0 N 21 10 0 W 22 0 0 N 21 10 0 W test line\r\n"+
          ";VECTOR: @C 21.6 12 0 N 21.5 11 0 W 5000 270 test vector\r\n"+
          ";CIRCLE: @D    21.8 0 0 N 21.0 0 0 W 2000 test circle\r\n"+
          ";TEXT: @E 21.7 0 0 N 21.5 0 0 W test text\r\n"+ 
          ";TEXT: WB 21.72 0 0 N 21.52 0 0 W wreck symbol\r\n"+ 
          ";TEXT: CA[LAYER=Special_Layer] 21.42 0 0 N 21.88 0 0 W Other layer\r\n"+
          ";TEXT: CA[LAYER=Other_Special_Layer] 21.22 0 0 N 21.88 0 0 W Other layer 3\r\n"+
          ";ELLIPSE: @F 951212 060200 21.8 0 0 N 21.5 0 0 W 45.0 5000 3000 test ellipse\r\n"+
          ";POLY: @GA30 21.9 0 0 N 21.5 0 0 W 22 0 0 N 21.8 0 0 W 22.1 0 0 N 21.5 0 0 W test poly\r\n"+
          ";POLYLINE: @C 21.1 0 0 N 21.5 0 0 W 21.2 0 0 N 21.8 0 0 W 21.3 0 0 N 21.5 0 0 W test polyline\r\n"+
          ";NARRATIVE:  951212 050200 NEL_STYLE comment 3\r\n";
      assertTrue(isContentImportable(textToPaste));
      textToPaste =";DYNAMIC_RECT: @A \"Dynamic A\" 951212 051000.000 22 00 0 N 21 00 0 W 21 50 0 N 20 50 0 W dynamic A rect 1\r\n"+
          ";DYNAMIC_CIRCLE: @A \"Dynamic A\" 951212 052100.000 21 00 0 N 20 53 0 W 2000 dynamic A circ 12\r\n"+
                ";DYNAMIC_POLY: @A \"Dynamic A\" 951212 052600.000 20 35 0 N 21 02 0 W 20 35 0 N 20 55 0 W 20 42 0 N 20 52 0 W 20 45 0 N 21 00 0 W  dynamic A POLY 170";
      assertTrue(isContentImportable(textToPaste));
      textToPaste = "@B 20 50 0 N 21 10 0 W 22 0 0 N 21 10 0 W test line ;VECTOR: @C 21.6 12 0 N 21.5 11 0 W 5000 270 test vector";
      assertFalse(isContentImportable(textToPaste));
      textToPaste = "";
      assertFalse(isContentImportable(textToPaste));
      textToPaste = "951212 050000.000 \"NEL STYLE\"   @C      22 12 10.63 N 21 31 52.37 W 269.7   2.0      0 ";
      assertTrue(isContentImportable(textToPaste));
      textToPaste = "19951212 050000.000 \"NEL STYLE\"   @C      22 12 10.63 N 21 31 52.37 W 269.7   2.0      0 ";
      assertTrue(isContentImportable(textToPaste));
    }
  }

  private static Vector<PlainLineImporter> _coreImporters;

  private static ArrayList<ExtensibleLineImporter> _extensionImporters;

  static private Vector<doublet> colors; // list of Replay colours

  static public final String NARRATIVE_LAYER = NarrativeEntry.NARRATIVE_LAYER;

  static public final String ANNOTATION_LAYER = "Annotations";

  /**
   * the prefs provider
   * 
   */
  private static ToolParent _myParent;

  /**
   * function to initialise the list of importers
   */
  private synchronized static void checkImporters()
  {
    if (_coreImporters == null)
    {
      // create the array of import handlers, by
      _coreImporters = new Vector<PlainLineImporter>(0, 1);

      // adding handler we (currently) know of
      _coreImporters.addElement(new ImportCircle());
      _coreImporters.addElement(new ImportRectangle());
      _coreImporters.addElement(new ImportLine());
      _coreImporters.addElement(new ImportVector());
      _coreImporters.addElement(new ImportEllipse());
      //_coreImporters.addElement(new ImportEllipse2());
      _coreImporters.addElement(new ImportPeriodText());
      _coreImporters.addElement(new ImportTimeText());
      _coreImporters.addElement(new ImportFixFormatter());
      _coreImporters.addElement(new ImportNameAtEndFormatter());
      _coreImporters.addElement(new ImportHideLayerFormatter());
      _coreImporters.addElement(new ImportLabel());
      _coreImporters.addElement(new ImportWheel());
      _coreImporters.addElement(new ImportBearing());
      _coreImporters.addElement(new ImportNarrative());
      _coreImporters.addElement(new ImportNarrative2());
      _coreImporters.addElement(new ImportSensor());
      _coreImporters.addElement(new ImportSensor2());
      _coreImporters.addElement(new ImportSensor3());
      _coreImporters.addElement(new ImportTMA_Pos());
      _coreImporters.addElement(new ImportTMA_RngBrg());
      _coreImporters.addElement(new ImportPolygon());
      _coreImporters.addElement(new ImportPolyline());
      // note that we don't rely on ImportFix for importing Replay fixes, since
      // they are handled by the ImportReplay method. We are including it in
      // this list so that we can use it as an exporter
      _coreImporters.addElement(new ImportFix());

      _coreImporters.addElement(new ImportDynamicRectangle());
      _coreImporters.addElement(new ImportDynamicCircle());
      _coreImporters.addElement(new ImportDynamicPolygon());

      _coreImporters.addElement(new ImportSensorArc());
    }
  }

  /**
   * format a date using our format
   */
  public static String formatThis(final HiResDate val)
  {
    final String res = DebriefFormatDateTime.toStringHiRes(val);
    return res;
  }

  /**
   * initialise the tool, so that it knows where to get its prefs details
   * 
   * @param theParent
   */
  public static void initialise(final ToolParent theParent)
  {
    _myParent = theParent;
  }

  private synchronized static void initialiseColours()
  {
    // create a list of colours
    if (colors == null)
    {
      colors = new Vector<doublet>(0, 1);
      colors.addElement(new doublet("@", Color.white));
      colors.addElement(new doublet("A", DebriefColors.BLUE));
      colors.addElement(new doublet("B", DebriefColors.GREEN));
      colors.addElement(new doublet("C", DebriefColors.RED));

      colors.addElement(new doublet("D", DebriefColors.YELLOW));
      colors.addElement(new doublet("E", DebriefColors.MAGENTA));
      colors.addElement(new doublet("F", DebriefColors.ORANGE));
      colors.addElement(new doublet("G", DebriefColors.PURPLE));
      colors.addElement(new doublet("H", DebriefColors.CYAN));
      colors.addElement(new doublet("I", DebriefColors.BROWN));
      colors.addElement(new doublet("J", DebriefColors.LIGHT_GREEN));
      colors.addElement(new doublet("K", DebriefColors.PINK));
      colors.addElement(new doublet("L", DebriefColors.GOLD));
      colors.addElement(new doublet("M", DebriefColors.LIGHT_GRAY));
      colors.addElement(new doublet("N", DebriefColors.GRAY));
      colors.addElement(new doublet("O", DebriefColors.DARK_GRAY));
      colors.addElement(new doublet("P", DebriefColors.WHITE));
      colors.addElement(new doublet("Q", DebriefColors.BLACK));
      colors.addElement(new doublet("R", DebriefColors.MEDIUM_BLUE));
      colors.addElement(new doublet("S", DebriefColors.DARK_BLUE));
    }
  }

  /**
   * the list of formatting objects we know about
   */
  private final LayersFormatter[] _myFormatters =
  {new FormatTracks()};
  /**
   * a list of the sensors we've imported
   * 
   */
  private final HashMap<TrackWrapper, Vector<SensorWrapper>> _pendingSensors =
      new HashMap<TrackWrapper, Vector<SensorWrapper>>();

  /**
   * a list of any exiting tracks that got modified (so we can tell people they've moved at the end
   * of hte operation
   */
  private final List<TrackWrapper> _existingTracksThatMoved =
      new ArrayList<TrackWrapper>();

  /**
   * remember how the user wants this REP data imported
   * 
   */
  private ImportSettings _importSettings;

  /*****************************************************************************
   * member methods
   ****************************************************************************/

  private final Map<String, Long> _lastImportedItem =
      new HashMap<String, Long>();

  /**
   * the property name we use for importing tracks (DR/ATG)
   * 
   */
  public final static String TRACK_IMPORT_MODE = "TRACK_IMPORT_MODE";

  public final static String RESAMPLE_FREQUENCY = "RESAMPLE_FREQUENCY";

  /**
   * the property values for importing modes
   * 
   */
  public final static String IMPORT_AS_DR = "DR_IMPORT";

  public final static String IMPORT_AS_OTG = "OTG_IMPORT";

  public final static String ASK_THE_AUDIENCE = "ASK_AUDIENCE";

  /**
   * provide an extra set of importers
   * 
   * @param importers
   */
  public static void addExtraImporters(
      final List<ExtensibleLineImporter> importers)
  {
    // do we have a holder?
    if (_extensionImporters == null)
    {
      _extensionImporters = new ArrayList<ExtensibleLineImporter>();
    }

    // now add the new ones
    for (final ExtensibleLineImporter importer : importers)
    {
      _extensionImporters.add(importer);
    }
  }

  static public Color replayColorFor(final int index)
  {
    Color res = null;

    // check we have the colours
    initialiseColours();

    final int theIndex = index % colors.size();

    res = colors.elementAt(theIndex).color;

    return res;
  }

  static public Color replayColorFor(final String theSym)
  {
    Color res = null;
    final String colorVal = theSym.substring(1, 2);

    final String upperSym = colorVal.toUpperCase();

    // check we have the colours
    initialiseColours();

    // step through our list of colours
    final java.util.Enumeration<doublet> iter = colors.elements();
    while (iter.hasMoreElements())
    {
      final doublet db = iter.nextElement();
      if (db.label.equals(upperSym))
      {
        res = db.color;
        break;
      }
    }

    // if label not found, make it RED
    if (res == null)
    {
      res = Color.red;
    }

    return res;
  }

  public static String replayFillStyleFor(final String theSym)
  {
    String res = null;
    if (theSym.length() >= 5)
    {
      res = theSym.substring(4, 5);
    }
    return res;
  }

  static public int replayLineStyleFor(final String theSym)
  {
    int res = 0;
    final String theStyle = theSym.substring(0, 1);

    if (theStyle.equals("@"))
    {
      res = MWC.GUI.CanvasType.SOLID;
    }
    else if (theStyle.equals("A"))
    {
      res = MWC.GUI.CanvasType.DOTTED;
    }
    else if (theStyle.equals("B"))
    {
      res = MWC.GUI.CanvasType.DOT_DASH;
    }
    else if (theStyle.equals("C"))
    {
      res = MWC.GUI.CanvasType.SHORT_DASHES;
    }
    else if (theStyle.equals("D"))
    {
      res = MWC.GUI.CanvasType.LONG_DASHES;
    }
    else if (theStyle.equals("E"))
    {
      res = MWC.GUI.CanvasType.UNCONNECTED;
    }

    return res;
  }

  private static int replayLineThicknesFor(final String theSym)
  {
    int res = 0;
    final String theThicknes = theSym.substring(0, 1);

    try
    {
      res = new Integer(theThicknes);
    }
    catch (final NumberFormatException e)
    {
      // ignore; res = 0
    }

    return res;
  }

  static public String replaySymbolFor(final Color theCol,
      final String theSymbol)
  {
    String res = null;

    // step through our list of colours
    final java.util.Enumeration<doublet> iter = colors.elements();
    while (iter.hasMoreElements())
    {
      final doublet db = iter.nextElement();
      if (db.color.equals(theCol))
      {
        res = db.label;
        continue;
      }
    }

    String symTxt;
    if (theSymbol == null)
    {
      symTxt = "@";
    }
    else
    {
      symTxt = SymbolFactory.findIdForSymbolType(theSymbol);
    }

    // label not found, make it RED
    if (res == null)
    {
      res = "A";
    }

    res = symTxt + res;

    return res;

  }

  static public String replaySymbolForLineStyle(final int style)
  {
    switch (style)
    {
      case MWC.GUI.CanvasType.DOTTED:
        return "A";

      case MWC.GUI.CanvasType.DOT_DASH:
        return "B";

      case MWC.GUI.CanvasType.SHORT_DASHES:
        return "C";

      case MWC.GUI.CanvasType.LONG_DASHES:
        return "D";

      case MWC.GUI.CanvasType.UNCONNECTED:
        return "E";

      default:
        break;
    }
    return "@";
  }

  public final static String replayTrackSymbolFor(final String theSym)
  {
    String res = null;
    final String symbolVal = theSym.substring(0, 1);

    res = SymbolFactory.createSymbolFromId(symbolVal);

    // did we manage to find it?
    if (res == null)
    {
      res = SymbolFactory.createSymbolFromId(SymbolFactory.DEFAULT_SYMBOL_TYPE);
    }
    else if (res == SymbolFactory.UNKNOWN)
    {
      // TODO: instance of itself?
      final ImportReplay importer = new ImportReplay();
      // In case we have an unknown symbol, we test if it is a SVG
      final String svgSymbol = importer.getSVGSymbol(theSym);
      if (svgSymbol != null)
      {
        res = SymbolFactory.SVG_FORMAT_PREFIX + ":" + svgSymbol;
      }
    }

    return res;
  }

  /**
   * ImporterReplays examines the symbol to see if a SVG Icon is specified.
   * 
   * @param thisOne
   * @return the name of the layer to use
   */
  final private String getSVGSymbol(final String sym)
  {
    // what are we looking for?
    final String SYMBOL_PREFIX = "SYMBOL";

    // check the symbology
    return getThisSymProperty(sym, SYMBOL_PREFIX);
  }

  /**
   * constructor, initialise Vector with the list of non-Fix items which we will be reading in
   */
  public ImportReplay()
  {
    _myTypes = new String[]
    {".rep", ".dsf", ".dtf"};

    checkImporters();

    initialiseColours();
  }

  @Override
  public final boolean canImportThisFile(final String theFile)
  {
    boolean res = false;
    String theSuffix = null;
    final int pos = theFile.lastIndexOf(".");
    theSuffix = theFile.substring(pos, theFile.length());

    for (int i = 0; i < _myTypes.length; i++)
    {
      if (theSuffix.equalsIgnoreCase(_myTypes[i]))
      {
        res = true;
        break;
      }
    }

    return res;
  }

  public void clearPendingSensorList()
  {
    _pendingSensors.clear();
  }

  /**
   * convert the item to text, add it to the block we're building up
   */
  @Override
  public final void exportThis(final Plottable item)
  {

    // check it's real
    if (item == null)
    {
      throw new IllegalArgumentException("duff wrapper");
    }

    checkImporters();

    // just see if it is a track which we are trying to export
    if ((item instanceof Layer) && !(item instanceof ExportLayerAsSingleItem))
    {
      // ok, work through the layer
      final Layer tw = (Layer) item;
      // ha-ha! export the points one at a time
      final java.util.Enumeration<Editable> iter = tw.elements();
      while (iter.hasMoreElements())
      {
        final Plottable pt = (Plottable) iter.nextElement();
        exportThis(pt);
      }
      // ta-da! done.
    }
    else
    {
      // check we have some importers
      if (_coreImporters != null)
      {
        final Enumeration<PlainLineImporter> iter = _coreImporters.elements();

        // step though our importers, to see if any will 'do the deal;
        while (iter.hasMoreElements())
        {
          final PlainLineImporter thisImporter = iter.nextElement();

          if (thisImporter.canExportThis(item))
          {
            // export it, add it to the data we're building up
            final String thisLine = thisImporter.exportThis(item);
            addThisToExport(thisLine);

            // ok, we can drop out of the loop
            break;
          }
        }
      }
    }
  }

  @Override
  public final void exportThis(final String val)
  {
    if (val != null)
    {
      final java.awt.datatransfer.Clipboard cl = java.awt.Toolkit
          .getDefaultToolkit().getSystemClipboard();
      final java.awt.datatransfer.StringSelection ss =
          new java.awt.datatransfer.StringSelection(val);
      cl.setContents(ss, ss);
    }
  }

  /**
   * some importers may need to finalize, if there is end of import processing to conduct. Trigger
   * that here
   */
  private void finaliseImporters()
  {
    if (_extensionImporters != null)
    {
      for (final ExtensibleLineImporter importer : _extensionImporters)
      {
        if (importer instanceof PlainLineImporter.ImportRequiresFinalisation)
        {
          final PlainLineImporter.ImportRequiresFinalisation fin =
              (ImportRequiresFinalisation) importer;
          fin.finalise();
        }
      }
    }
    if (_coreImporters != null)
    {
      for (final PlainLineImporter importer : _coreImporters)
      {
        if (importer instanceof PlainLineImporter.ImportRequiresFinalisation)
        {
          final PlainLineImporter.ImportRequiresFinalisation fin =
              (ImportRequiresFinalisation) importer;
          fin.finalise();
        }
      }
    }
  }

  private PlainLineImporter getImporterFor(final String rawString)
  {
    PlainLineImporter res = null;

    // trim any leading whitespace
    final String theLine = rawString.trim();

    // check it's not an empty line
    if (theLine.length() > 0)
    {
      // so, determine if this is a comment
      if (theLine.charAt(0) == ';')
      {
        // get the type for this comment
        final StringTokenizer st = new StringTokenizer(theLine);
        final String type = st.nextToken();

        // do we have any extension importers?
        if (_extensionImporters != null)
        {
          for (final ExtensibleLineImporter importer : _extensionImporters)
          {
            final String thisType = importer.getYourType();
            if (thisType.equals(type))
            {
              // ok, we also have to initialise it with this layers object
              importer.setLayers(getLayers());

              // remember it
              res = importer;

              // done
              break;
            }
          }
        }

        // did it work?
        if (res == null)
        {
          // nope, try the core ones

          // look through types of import handler
          final Enumeration<PlainLineImporter> iter = _coreImporters.elements();

          // cycle through my types
          while (iter.hasMoreElements())
          {
            final PlainLineImporter thisImporter = iter.nextElement();

            // get the handler correct type?
            final String thisType = thisImporter.getYourType();

            if (thisType == null)
            {
              MWC.Utilities.Errors.Trace.trace("null returned by: "
                  + thisImporter);
              return null;
            }

            // does this one fit?
            if (thisType.equals(type))
            {
              res = thisImporter;
              break;
            }
          }
        }
      }
      else
      {
        res = new ImportFix();
      }
    }

    // done
    return res;
  }

  public Vector<SensorWrapper> getPendingSensors()
  {

    final Vector<SensorWrapper> res = new Vector<SensorWrapper>();
    final Iterator<TrackWrapper> tIter = _pendingSensors.keySet().iterator();
    while (tIter.hasNext())
    {
      final TrackWrapper trackWrapper = tIter.next();
      res.addAll(_pendingSensors.get(trackWrapper));
    }
    return res;
  }

  /**
   * utility method to extract a label from a composite end of line block that optionally includes a
   * comment ;SENSOR2: 20090722 041434.000 NONSUCH @B NULL 59.3 300.8 49.96 NULL LABEL // COMMENT
   */
  final public static String getLabel(final String content)
  {
    final String res;
    if (content != null)
    {
      if (content.contains("//"))
      {
        final String[] strings = content.trim().split("//");
        final String first = strings[0].trim();
        if (first.length() > 0)
        {
          res = first;
        }
        else
        {
          res = null;
        }
      }
      else
      {
        final String txt = content.trim();
        if (txt.length() > 0)
        {
          res = txt;
        }
        else
        {
          res = null;
        }
      }
    }
    else
    {
      res = null;
    }

    return res;
  }

  /**
   * utility method to extract a label from a composite end of line block that optionally includes a
   * comment ;SENSOR2: 20090722 041434.000 NONSUCH @B NULL 59.3 300.8 49.96 NULL LABEL // COMMENT
   */
  final public static String getComment(final String content)
  {
    final String res;
    if (content != null && content.contains("//"))
    {
      String[] strings = content.trim().split("//");
      if (strings.length > 1)
      {
        res = strings[1].trim();
      }
      else
      {
        res = null;
      }
    }
    else
    {
      res = null;
    }

    return res;
  }

  /**
   * utility method to extract formatted property values from a symbology line, such as: ;TEXT:
   * CA[LAYER=Special_Layer,SYMBOL=missile] 21.42 0 0 N 21.88 0 0 W Other layer
   * 
   * @param symbology
   * @param property_name
   * @return
   */
  final private String getThisSymProperty(final String symbology,
      final String property_name)
  {
    int indexFirstBracket = symbology.indexOf('[');
    int indexLastBracket = symbology.indexOf(']');
    if (indexFirstBracket < 0 || indexLastBracket < 0)
    {
      return null;
    }
    final String regexp = "(?<NAME>.*?)\\=(?<VALUE>[^,]*),?";
    final Matcher m = Pattern.compile(regexp).matcher(symbology.substring(
        indexFirstBracket + 1, indexLastBracket));

    // did we find any?
    while (m.find())
    {
      // get the name of this property
      final String property = m.group("NAME");

      // does it match?
      if (property.equals(property_name))
      {
        // yes, get the value
        final String value = m.group("VALUE");

        // done, return it.
        return value;
      }
    }
    return null;
  }
  
  private final static void addElementsToExistingLayer(final Layer layerToAddTo, final Editable newItems)
  {
    // special handling. If we're adding a track to a track, we'll add the individual
    // elements, not the segments
    if (layerToAddTo instanceof LightweightTrackWrapper
        && newItems instanceof LightweightTrackWrapper)
    {
      final LightweightTrackWrapper track = (TrackWrapper) newItems;
      final Enumeration<Editable> positions = track.getPositionIterator();
      while (positions.hasMoreElements())
      {
        layerToAddTo.add(positions.nextElement());
      }
    }
    else
    {
      final Enumeration<Editable> tempElements = ((Layer) newItems).elements();
      while (tempElements.hasMoreElements())
      {
        Editable elem = tempElements.nextElement();
        layerToAddTo.add(elem);
      }
    }
  }
  
  public final static void injectContent(final Layers from,final Layers destination,final boolean performAdd) {
    Enumeration<Editable> tempElements = from.elements();
    //now add to the plot's layers object
    while(tempElements.hasMoreElements()) {
      Editable l = tempElements.nextElement();
      Layer existingLayer = destination.findLayer(l.getName());
      if(existingLayer==null) {
        Layer layerToAdd = (Layer)l;
        if(performAdd) {
          destination.addThisLayer(layerToAdd);
        }
        else {
          destination.removeThisLayer(layerToAdd);
        }
      }
      else {
        //get elements in the templayer for an already existing layer and add to it.
        if(performAdd) {
          addElementsToExistingLayer(existingLayer,l);
        }
        else {
          if(l instanceof Layer) {
            //dont directly delete a layer, may be there were some elements previously existing
            Enumeration<Editable> elements = ((Layer)l).elements();
            Layer destLayer = destination.findLayer(l.getName());
            while(elements.hasMoreElements()) {
              destination.removeThisEditable(destLayer, elements.nextElement());
            }
            //in case no more elements are left delete the layer
            if(destLayer.elements()==null || !destLayer.elements().hasMoreElements()) {
              destination.removeThisLayer(destLayer);
            }
          }
        }
      }
    }
    destination.fireExtended();
  }

  public final void importThis(final String text, final int numLines) {
    final InputStream stream = new ByteArrayInputStream(text.getBytes());
    importRep(null,stream,numLines);
  }

  @Override
  public final void importThis(final String fName, InputStream is) {
    final int numLines = countLinesFor(fName);
    importRep(fName,is,numLines);
  }
  /**
   * import data from this stream
   */
  private final void importRep(final String fName,final InputStream is,final int numLines)
  {
    // declare linecounter
    int lineCounter = 0;
    final Reader reader;
    BufferedReader br = null;
    String thisLine = null;
    try
    {
        reader = new InputStreamReader(is);
        final String nameToUse;
        if(fName != null)
        {
          nameToUse = fName;
        }
        else
        {
          nameToUse = "Pasted REP content";
        }
        br = new ReaderMonitor(reader, numLines, nameToUse);
        // check stream is valid
        if (is.available() > 0)
        {

          // clear the output list
          _newLayers.clear();
          _existingTracksThatMoved.clear();

          // clear the input settings
          _importSettings = null;
          _lastImportedItem.clear();

          thisLine = br.readLine();

          // loop through the lines
          while (thisLine != null)
          {
            // keep line counter
            lineCounter++;

            // catch import problems
            readLine(thisLine);

            // read another line
            thisLine = br.readLine();
          }

          // see if any importers need to finalise
          finaliseImporters();

          // lastly have a go at formatting these tracks
          for (int k = 0; k < _myFormatters.length; k++)
          {
            _myFormatters[k].formatLayers(_newLayers);
          }

          // see if we've modified any existing tracks
          final Iterator<TrackWrapper> tIter =
              _existingTracksThatMoved.iterator();
          while (tIter.hasNext())
          {
            final TrackWrapper track = tIter.next();
            track.sortOutRelativePositions();
          }
       }
    }
    catch (final java.lang.NumberFormatException e)
    {
      // produce the error message
      MWC.Utilities.Errors.Trace.trace(e);
      // show the message dialog
      super.readError(fName, lineCounter, "Number format error", thisLine);
    }
    catch (final IOException e)
    {
      // produce the error message
      MWC.Utilities.Errors.Trace.trace(e);
      // show the message dialog
      super.readError(fName, lineCounter, "Unknown read error:" + e, thisLine);
    }
    catch (final java.util.NoSuchElementException e)
    {
      // produce the error message
      MWC.Utilities.Errors.Trace.trace(e);
      // show the message dialog
      super.readError(fName, lineCounter, "Missing field error", thisLine);
    }
    catch (final ParseException e)
    {
      // produce the error message
      MWC.Utilities.Errors.Trace.trace(e);
      // show the message dialog
      super.readError(fName, lineCounter, "Date format error", thisLine);
    }
    finally
    {
      try
      {
        br.close();
      }
      catch (IOException e)
      {
        MWC.Utilities.Errors.Trace.trace(e);
      }
    }
  }

  private void proccessShapeWrapper(final PlainLineImporter thisOne,
      final Object thisObject)
  {
    final ShapeWrapper shapeWrapper = (ShapeWrapper) thisObject;
    final String symbology = thisOne.getSymbology();
    if (symbology != null && !symbology.isEmpty() && symbology.length() > 2)
    {
      shapeWrapper.getShape().setLineStyle(ImportReplay.replayLineStyleFor(
          symbology.substring(2)));
      if (symbology.length() > 3)
      {
        shapeWrapper.getShape().setLineWidth(ImportReplay.replayLineThicknesFor(
            symbology.substring(3)));
      }
      if (symbology.length() >= 5)
      {
        final PlainShape shape = shapeWrapper.getShape();
        final String fillType = symbology.substring(4, 5);
        if ("1".equals(fillType))
        {
          shape.setFilled(true);
        }
        else if ("2".equals(fillType))
        {
          shape.setFilled(true);
          shape.setSemiTransparent(true);
        }
        else
        {
          shape.setFilled(false);
          shape.setSemiTransparent(false);
        }
      }
    }
  }

  private HiResDate processContactWrapper(final TMAContactWrapper sw)
  {
    final HiResDate res = sw.getTime();

    TMAWrapper thisWrapper = null;

    // do we have a sensor capable of handling this contact?
    final String solutionName = sw.getSolutionName();

    String trackName = sw.getTrackName();
    Object val = getLayerFor(trackName);

    // if we failed to get the trackname, try shortening it -
    // it may have been mangled by BabelFish
    if (val == null)
    {
      val = getLayerFor(trackName = trackName.substring(6));
    }

    // did we get anything?
    // is this indeed a sensor?
    if (val == null || !(val instanceof TrackWrapper))
    {
      return res;
    }

    // so, we've found a track - see if it holds this solution
    final TrackWrapper theTrack = (TrackWrapper) val;
    final Enumeration<Editable> iter = theTrack.getSolutions().elements();

    // step through this track's solutions
    if (iter != null)
    {
      while (iter.hasMoreElements())
      {
        final TMAWrapper sensorw = (TMAWrapper) iter.nextElement();

        // is this our sensor?
        if (sensorw.getName().equals(solutionName))
        {
          // cool, drop out
          thisWrapper = sensorw;
          break;
        }
      } // looping through the sensors
    } // whether there are any sensors

    // did we find it?
    if (thisWrapper == null)
    {
      // then create it
      thisWrapper = new TMAWrapper(solutionName);

      // set it's colour to the colour of the first data point
      thisWrapper.setColor(sw.getColor());

      // also set it's name
      thisWrapper.setTrackName(sw.getTrackName());

      theTrack.add(thisWrapper);
    }

    // so, we now have the wrapper. have a look to see if the colour
    // of this data item is the same
    // as the sensor - in which case we will erase the colour for
    // this data item so that it
    // always takes the colour of it's parent
    if (sw.getColor().equals(thisWrapper.getColor()))
    {
      // clear the colour - so it takes it form it's parent
      sw.setColor(null);
    }

    // lastly inform the sensor contact of it's parent
    sw.setTMATrack(thisWrapper);

    // now add the new contact to this sensor
    thisWrapper.add(sw);

    return res;
  }

  private HiResDate processDynamicShapeWrapper(
      final DynamicTrackShapeWrapper sw)
  {
    final HiResDate res = null;

    DynamicTrackShapeSetWrapper thisShape = null;

    // do we have a sensor capable of handling this contact?
    final String sensorName = sw.getSensorName();
    String trackName = sw.getTrackName();
    Object val = getLayerFor(trackName);

    // if we failed to get the trackname, try shortening it -
    // it may have been mangled by BabelFish
    if (val == null)
    {
      val = getLayerFor(trackName = trackName.substring(6));
    }

    // did we get anything?
    // is this indeed a sensor?
    if (val == null || !(val instanceof TrackWrapper))
    {
      return res;
    }

    // so, we've found a track - see if it holds this shape
    final TrackWrapper theTrack = (TrackWrapper) val;
    final Enumeration<Editable> iter = theTrack.getDynamicShapes().elements();

    // step through this track' shape
    if (iter != null)
    {
      while (iter.hasMoreElements())
      {
        final DynamicTrackShapeSetWrapper shape =
            (DynamicTrackShapeSetWrapper) iter.nextElement();

        // is this our sensor?
        if (shape.getName().equals(sensorName))
        {
          // cool, drop out
          thisShape = shape;
          break;
        }
      } // looping through the sensors
    } // whether there are any sensors

    // did we find it?
    if (thisShape == null)
    {
      // then create it
      thisShape = new DynamicTrackShapeSetWrapper(sensorName);

      theTrack.add(thisShape);
    }

    // now add the new contact to this sensor
    thisShape.add(sw);

    return res;
  }

  private HiResDate processReplayFix(final ReplayFix rf)
  {
    final HiResDate res = rf.theFix.getTime();

    // do we have a target layer?
    String targetLayer = targetLayerFor(rf.theSymbology);

    if (targetLayer != null)
    {
      // does this exist?
      Layer target = getLayerFor(targetLayer, true);

      final BaseLayer folder;
      if (target == null)
      {
        // ok, generate the layer
        folder = new BaseLayer();
        folder.setName(targetLayer);
        addLayer(folder);
      }
      else if (target instanceof BaseLayer)
      {
        folder = (BaseLayer) target;
      }
      else
      {
        // ok, slight renaming needed
        folder = new BaseLayer();
        folder.setName(targetLayer + "_1");
        addLayer(folder);
      }

      // ok, does it contain the track
      final LightweightTrackWrapper track;
      Editable found = folder.find(rf.theTrackName);
      if (found != null && found instanceof LightweightTrackWrapper)
      {
        track = (LightweightTrackWrapper) found;
      }
      else
      {
        final Color thisColor = replayColorFor(rf.theSymbology);
        track = new LightweightTrackWrapper(rf.theTrackName, true, true,
            thisColor, LineStylePropertyEditor.SOLID);

        // set the sym type for the track
        final String theSymType = replayTrackSymbolFor(rf.theSymbology);
        track.setSymbolType(theSymType);
        folder.add(track);
      }

      // create the wrapper for this annotation
      final FixWrapper thisFix = new FixWrapper(rf.theFix);
      track.add(thisFix);
    }
    else
    {

      // ok, are we re-sampling the data?
      if (_importSettings != null)
      {
        // ok, we're processing this one. Remember it.

        // are we in OTG mode?
        if (ImportReplay.IMPORT_AS_OTG.equals(_importSettings.importMode))
        {
          final Long lastTime = _lastImportedItem.get(rf.theTrackName);

          final Long sampleFreq = _importSettings.sampleFrequency;

          final boolean isMax = (Long.MAX_VALUE == sampleFreq);

          // ok, are we due to import this one?
          if (lastTime == null || (!isMax && res.getDate().getTime() >= lastTime
              + sampleFreq))
          {
            // ok, carry on
          }
          else
          {
            return null;
          }
        }
      }

      final long thisTime = res.getDate().getTime();
      _lastImportedItem.put(rf.theTrackName, thisTime);

      // find the track name
      final String theTrack = rf.theTrackName;
      final Color thisColor = replayColorFor(rf.theSymbology);

      // create the wrapper for this annotation
      final FixWrapper thisFix = new FixWrapper(rf.theFix);

      // overwrite the label, if there's one there
      if (rf.label != null)
      {
        thisFix.setLabel(rf.label);
        thisFix.setUserLabelSupplied(true);
      }
      else
      {
        thisFix.resetName();
      }
      if (rf.comment != null)
      {
        thisFix.setComment(rf.comment);
      }

      // keep track of the wrapper for this track
      // is there a layer for this track?
      TrackWrapper trkWrapper = (TrackWrapper) getLayerFor(theTrack);

      // have we found the layer?
      if (trkWrapper != null)
      {
        // ok, remember that we've changed this track
        if (!_existingTracksThatMoved.contains(trkWrapper))
        {
          _existingTracksThatMoved.add(trkWrapper);

          // ok, this must be the first fix for this new track

          // ask the user if he wants it resampled.
          if (_myParent instanceof ProvidesModeSelector
              && _importSettings == null)
          {
            final ProvidesModeSelector selector =
                (ProvidesModeSelector) _myParent;
            final Long freq = selector.getSelectedImportFrequency(theTrack);
            if (freq == null)
            {
              // ok, skip the data
              _importSettings = new ImportSettings(ImportReplay.IMPORT_AS_OTG,
                  Long.MAX_VALUE);
            }
            else
            {
              _importSettings = new ImportSettings(ImportReplay.IMPORT_AS_OTG,
                  freq);
            }
          }
        }
      }
      else
      {
        // ok, see if we're importing it as DR or ATG (or ask the audience)
        String importMode = null;
        String freqStr = null;
        if (_myParent != null)
        {
          importMode = _myParent.getProperty(TRACK_IMPORT_MODE);
          freqStr = _myParent.getProperty(RESAMPLE_FREQUENCY);
        }
        else
        {
          // prob in a headless test.
          importMode = IMPORT_AS_OTG;
          freqStr = "0";
        }

        final Long importFreq;
        if (freqStr != null && freqStr.length() > 0 && !freqStr.equals("null"))
        {
          importFreq = Long.valueOf(freqStr);
        }
        else
        {
          importFreq = null;
        }

        // catch a missing import mode
        if (importMode == null || importFreq == null)
        {
          // belt & braces it is then...
          importMode = ImportReplay.ASK_THE_AUDIENCE;
        }

        if (importMode.equals(ImportReplay.ASK_THE_AUDIENCE))
        {
          if (_myParent instanceof ProvidesModeSelector)
          {
            final ProvidesModeSelector selector =
                (ProvidesModeSelector) _myParent;
            _importSettings = selector.getSelectedImportMode(theTrack);
            if (_importSettings != null)
            {
              importMode = _importSettings.importMode;
            }
            else
            {
              importMode = null;
            }
          }
        }
        else
        {
          // don't pass a frequency for DR import, since it will be ignored
          final Long theFreq;
          if (importMode.equals(IMPORT_AS_DR))
          {
            theFreq = null;
          }
          else
          {
            theFreq = importFreq;
          }

          // create the artificial import settings
          _importSettings = new ImportSettings(importMode, theFreq);
        }

        TrackSegment initialLayer = null;

        // has the user cancelled?
        if (importMode == null)
        {
          // and drop out of the whole affair
          throw new RuntimeException("User cancelled import");
        }
        else if (importMode.equals(ImportReplay.IMPORT_AS_OTG))
        {
          initialLayer = new TrackSegment(TrackSegment.ABSOLUTE);
        }
        else if (importMode.equals(ImportReplay.IMPORT_AS_DR))
        {
          initialLayer = new TrackSegment(TrackSegment.RELATIVE);
        }

        // now create the wrapper
        trkWrapper = new TrackWrapper();

        // give it the data container
        trkWrapper.add(initialLayer);

        // get the colour for this track
        trkWrapper.setColor(thisColor);
        trkWrapper.setSymbolColor(thisColor);

        // set the sym type for the track
        final String theSymType = replayTrackSymbolFor(rf.theSymbology);
        trkWrapper.setSymbolType(theSymType);

        // store the track-specific data
        trkWrapper.setName(theTrack);

        // add our new layer to the Layers object
        addLayer(trkWrapper);

        // see if there is any formatting to be done
        // lastly - see if the layers object has some formatters
        final Iterator<INewItemListener> newIiter = getLayers()
            .getNewItemListeners().iterator();
        while (newIiter.hasNext())
        {
          final INewItemListener newI = newIiter.next();
          newI.newItem(trkWrapper, null, null);
        }
      }

      // Note: line style & thickness only (currently) apply to whole tracks,
      // so we will effectively just use the last value read in.
      if (rf.theSymbology != null && rf.theSymbology.length() > 2)
      {
        trkWrapper.setLineStyle(ImportReplay.replayLineStyleFor(rf.theSymbology
            .substring(2)));
        if (rf.theSymbology.length() > 3)
        {
          trkWrapper.setLineThickness(ImportReplay.replayLineThicknesFor(
              rf.theSymbology.substring(3)));
        }
      }

      // ok, are we

      // add the fix to the track
      trkWrapper.addFix(thisFix);

      // let's also tell the fix about it's track
      thisFix.setTrackWrapper(trkWrapper);

      // also, see if this fix is specifying a different colour to use
      if (thisColor != trkWrapper.getColor())
      {
        // give this fix it's unique colour
        thisFix.setColor(thisColor);
      }

      // lastly - see if the layers object has some formatters
      final Iterator<INewItemListener> newIiter = getLayers()
          .getNewItemListeners().iterator();
      while (newIiter.hasNext())
      {
        final Layers.INewItemListener newI = newIiter.next();
        newI.newItem(trkWrapper, thisFix, rf.theSymbology);
      }
    }
    return res;

  }

  private HiResDate processSensorContactWrapper(final SensorContactWrapper sw)
  {
    final HiResDate res = sw.getTime();

    SensorWrapper thisSensor = null;

    // do we have a sensor capable of handling this contact?
    final String sensorName = sw.getSensorName();
    String trackName = sw.getTrackName();
    Object val = getLayerFor(trackName);

    // if we failed to get the trackname, try shortening it -
    // it may have been mangled by BabelFish
    if (val == null)
    {
      if (trackName.length() > 6)
      {
        final String tmpTrackName = trackName.substring(0, 6);
        val = getLayerFor(tmpTrackName);

        if (val != null)
        {
          // ok, adopt this track name
          trackName = tmpTrackName;
        }
      }
    }

    // so, we've found a track - see if it holds this sensor
    // SPECIAL HANDLING: it may actually still be a null. This is ok,
    // because post-load we invite the user to select
    // the parent track for these "null" tracks.
    final TrackWrapper theTrack = (TrackWrapper) val;

    Vector<SensorWrapper> thisTrackSensors = _pendingSensors.get(theTrack);

    if (thisTrackSensors == null)
    {
      thisTrackSensors = new Vector<SensorWrapper>();
      _pendingSensors.put(theTrack, thisTrackSensors);
    }

    final Iterator<SensorWrapper> iter = thisTrackSensors.iterator();

    // step through this track' sensors
    if (iter != null)
    {
      while (iter.hasNext())
      {
        final SensorWrapper sensorw = iter.next();

        // is this our sensor?
        if (sensorw.getName().equals(sensorName))
        {
          // cool, drop out
          thisSensor = sensorw;
          break;
        }
      } // looping through the sensors
    } // whether there are any sensors

    // did we find it?
    if (thisSensor == null)
    {
      // then create it
      thisSensor = new SensorWrapper(sensorName);

      // set it's colour to the colour of the first data point
      thisSensor.setColor(sw.getColor());

      // also set it's name
      thisSensor.setTrackName(sw.getTrackName());

      // oh, and the track
      thisSensor.setHost(theTrack);

      // ok, now remember it
      thisTrackSensors.add(thisSensor);
    }

    // so, we now have the wrapper. have a look to see if the colour
    // of this data item is the same
    // as the sensor - in which case we will erase the colour for
    // this data item so that it always takes the colour of it's parent
    if (sw.getColor().equals(thisSensor.getColor()))
    {
      // clear the colour - so it takes it form it's parent
      sw.setColor(null);
    }

    // now add the new contact to this sensor
    thisSensor.add(sw);

    return res;
  }

  /**
   * parse this line
   * 
   * @param theLine
   *          the line to parse
   * @throws ParseException 
   */
  public HiResDate readLine(final String theLine) throws IOException, ParseException
  {
    HiResDate res = null;

    // ok, trim any leading/trailing whitespace
    final String line = theLine.trim();

    // is this line invalid
    if (line.length() <= 0)
    {
      return null;
    }

    // what type of item is this?
    final PlainLineImporter thisOne = getImporterFor(line);

    // check that we have found an importer
    if (thisOne == null)
    {
      // just check it wasn't a comment
      if (line.startsWith(";;"))
      {
        // don't bother, it's just a comment
      }
      else
      {
        MWC.Utilities.Errors.Trace.trace("Annotation type not recognised for:"
            + line);
      }
      return null;
    }

    // now read it in.
    final Object thisObject = thisOne.readThisLine(line);

    // see if we are going to do any special processing

    // is this a fix?
    if (thisObject instanceof ReplayFix)
    {
      res = processReplayFix((ReplayFix) thisObject);
    }
    else if (thisObject instanceof IDynamicShapeWrapper)
    {
      proccessShapeWrapper(thisOne, thisObject);
      final IDynamicShapeWrapper thisWrapper = (IDynamicShapeWrapper) thisObject;
      final String trackName = thisWrapper.getTrackName();
      DynamicShapeLayer dsl = (DynamicShapeLayer) getLayerFor(trackName);
      if (dsl == null)
      {
        dsl = new DynamicShapeLayer();
        dsl.setName(trackName);
        addLayer(dsl);
      }
      addToLayer(thisWrapper, dsl);
    }
    else if (thisObject instanceof SensorContactWrapper)
    {
      res = processSensorContactWrapper((SensorContactWrapper) thisObject);
    }
    else if (thisObject instanceof DynamicTrackShapeWrapper)
    {
      res = processDynamicShapeWrapper((DynamicTrackShapeWrapper) thisObject);
    }
    else if (thisObject instanceof TMAContactWrapper)
    {
      res = processContactWrapper((TMAContactWrapper) thisObject);
    }
    else if (thisObject instanceof NarrativeEntry)
    {
      final NarrativeEntry entry = (NarrativeEntry) thisObject;

      // remember the dtg
      res = entry.getDTG();

      // have we got a narrative wrapper?
      Layer dest = getLayerFor(NARRATIVE_LAYER);
      if (dest == null)
      {
        dest = new NarrativeWrapper(NARRATIVE_LAYER);
        addLayer(dest);
      }

      // ok, can we provide a track color for it?
      final String source = entry.getTrackName();
      final Layer host = getLayerFor(source);
      if (host instanceof TrackWrapper)
      {
        final TrackWrapper tw = (TrackWrapper) host;
        entry.setColor(tw.getColor());
      }

      addToLayer(entry, dest);
    }
    else if (thisObject instanceof INewItemListener)
    {
      getLayers().addNewItemListener((INewItemListener) thisObject);
    }

    // ////////
    // PlainWrapper is our "fallback" operator, so it's important to leave it
    // to last
    // ////////
    else if (thisObject instanceof PlainWrapper)
    {

      // create the wrapper for this annotation
      final PlainWrapper thisWrapper = (PlainWrapper) thisObject;

      // remember the dtg
      if (thisWrapper instanceof Watchable)
      {
        final Watchable wat = (Watchable) thisWrapper;
        res = wat.getTime();
      }

      if (thisObject instanceof ShapeWrapper)
      {
        proccessShapeWrapper(thisOne, thisObject);
      }

      // see if the shape symbology specifies a layer
      String targetLayer = targetLayerFor(thisOne.getSymbology());

      if (targetLayer == null)
      {
        targetLayer = ANNOTATION_LAYER;
      }

      // ok, get that layer
      Layer dest = getLayerFor(targetLayer);

      // does it exist?
      if (dest == null)
      {
        // nope, create it
        dest = createLayer(targetLayer);
        addLayer(dest);
      }

      addToLayer(thisWrapper, dest);

    }

    return res;
  }

  /**
   * actually store the sensor data in its parent object
   * 
   */
  public void storePendingSensors()
  {
    // ok, actually add the sensors to their parents now
    final Iterator<TrackWrapper> tIter = _pendingSensors.keySet().iterator();
    while (tIter.hasNext())
    {
      final TrackWrapper trackWrapper = tIter.next();
      final Iterator<SensorWrapper> iter = _pendingSensors.get(trackWrapper)
          .iterator();
      while (iter.hasNext())
      {
        final SensorWrapper thisS = iter.next();

        // find the track
        final TrackWrapper parent = thisS.getHost();

        // now formally add the sensor, if we can
        if (parent != null)
        {
          parent.add(thisS);
        }
        else
        {
          // SPECIAL HANDLING - the user may have declined
          // to assign the sensor to a track, in which case
          // we just skip the assignent
          Application.logError2(ToolParent.INFO,
              "Not storing sensor, track not found", null);
        }
      }
    }

  }

  /**
   * examine the sybmology, to see if a target layer is specified. If it isn't just put it into the
   * annotations layer
   * 
   * @param thisOne
   * @return the name of the layer to use
   */
  final private String targetLayerFor(final String sym)
  {
    // what are we looking for?
    final String LAYER_PREFIX = "LAYER";

    // check the symbology
    return getThisSymProperty(sym, LAYER_PREFIX);
  }
  final public static boolean isContentImportable(final String content) {
    if(content == null || content.isEmpty()) {
      return false;
    }
    boolean proceed=true;
    String[] lines = content.split("\\r?\\n");
    int lineCount = 0;
    int maxLines = Math.min(6, lines.length);
    while(lineCount<maxLines && proceed) {
      String line = lines[lineCount];
      if(line.startsWith(";") && !line.startsWith(";;")) {
        StringTokenizer lineTokens = new StringTokenizer(line);
        if(lineTokens.hasMoreTokens()) {
          String firstWord = lineTokens.nextToken();
          String regex = "^;[A-Z1-9_]{3,40}+:$";
          Pattern pattern = Pattern.compile(regex);
          Matcher match = pattern.matcher(firstWord);
          if(!match.matches()) {
            proceed=false;
          }
        }
      }
      else {
        StringTokenizer lineTokens = new StringTokenizer(line);
        if(lineTokens.hasMoreTokens()) {
          String firstWord = lineTokens.nextToken();
          if(!(firstWord.matches("\\d{6}+") || firstWord.matches("\\d{8}+"))) {
            proceed=false;
          }
        }
        
      }
      lineCount++;
    }
    return proceed;
  }

}
