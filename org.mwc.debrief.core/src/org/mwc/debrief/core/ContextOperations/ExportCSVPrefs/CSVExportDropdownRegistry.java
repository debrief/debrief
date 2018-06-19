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
package org.mwc.debrief.core.ContextOperations.ExportCSVPrefs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.mwc.cmap.core.CorePlugin;

import junit.framework.TestCase;

public class CSVExportDropdownRegistry implements DropdownProvider
{
  public static class TestMe extends TestCase
  {
    public void testBadines()
    {
      final List<String> list = new ArrayList<String>();

      list.add("//// comment line");
      list.add("a-one");
      list.add("a-two");
      list.add("// SectionB");
      list.add("b-one");
      list.add("//SectionC");
      list.add("c-one");
      list.add("c-two");
      list.add("c-three");

      final Map<String, ArrayList<String>> fields =
          new HashMap<String, ArrayList<String>>();

      assertTrue("should not be empty", fields.keySet().isEmpty());

      processLines(list, fields);

      assertFalse("should not be empty", fields.keySet().isEmpty());

      assertEquals("has 3 sections", 3, fields.keySet().size(), 3);
      assertEquals("section two empty", null, fields.get("SectionA"));
      assertEquals("has 3 lines", 3, fields.get("SectionC").size());
    }

    public void testLines()
    {
      final List<String> list = new ArrayList<String>();

      list.add("//// comment line");
      list.add("//SectionA");
      list.add("a-one");
      list.add("a-two");
      list.add("// SectionB");
      list.add("b-one");
      list.add("//SectionC");
      list.add("c-one");
      list.add("c-two");
      list.add("c-three");

      final Map<String, ArrayList<String>> fields =
          new HashMap<String, ArrayList<String>>();

      assertTrue("should not be empty", fields.keySet().isEmpty());

      processLines(list, fields);

      assertFalse("should not be empty", fields.keySet().isEmpty());

      assertEquals("has 3 sections", 3, fields.keySet().size(), 3);
      assertEquals("has 2 lines", 2, fields.get("SectionA").size());
      assertEquals("has 1 lines", 1, fields.get("SectionB").size());
      assertEquals("has 3 lines", 3, fields.get("SectionC").size());
    }

    public void testLoad()
    {
      final String filename =
          "../org.mwc.cmap.combined.feature/root_installs/sample_data/other_formats/ExportWizard.csv";

      // check it exists
      assertTrue("can't find test data-file", new File(filename).exists());

      final CSVExportDropdownRegistry reg = new CSVExportDropdownRegistry(
          filename);

      // check empty
      assertTrue("list object should be empty", reg.myFields.keySet()
          .isEmpty());

      reg.load();

      assertFalse("no longer empty", reg.myFields.keySet().isEmpty());
      assertEquals("has sections", 11, reg.myFields.keySet().size());

      assertEquals("has 3 classifications", 3, reg.getValuesFor(
          "CLASSIFICATION").size());
    }
  }

  private static CSVExportDropdownRegistry ourInstance;

  public synchronized static CSVExportDropdownRegistry getRegistry()
  {
    if (ourInstance == null)
    {
      ourInstance = new CSVExportDropdownRegistry();
      ourInstance.load();
    }
    return ourInstance;
  }

  private static void processLines(final List<String> lines,
      final Map<String, ArrayList<String>> myFields)
  {
    String thisGroup = null;
    for (final String nextLine : lines)
    {
      // check it's not a comment marker
      if (!nextLine.startsWith("////"))
      {
        if (nextLine.startsWith("//"))
        {
          // new section, store it

          // trim the comment marker
          thisGroup = nextLine.substring(2, nextLine.length());

          // ditch whitespace
          thisGroup = thisGroup.trim();

          // and create the new list
          final ArrayList<String> newGroup = new ArrayList<String>();
          myFields.put(thisGroup, newGroup);
        }
        else
        {
          // do we know our group?
          if (thisGroup != null)
          {
            // must be part of the current group
            final ArrayList<String> group = myFields.get(thisGroup);
            group.add(nextLine);
          }
        }
      }
    }
  }

  private String myFileName;

  private final Map<String, ArrayList<String>> myFields =
      new HashMap<String, ArrayList<String>>();

  public CSVExportDropdownRegistry()
  {
    this(CorePlugin.getDefault().getPreferenceStore().getString(
        ExportCSVPreferencesPage.PreferenceConstants.PATH_TO_CSV));
  }

  public CSVExportDropdownRegistry(final String filename)
  {
    setFileName(filename);
  }

  /**
   * Clear data from registry
   */
  private void clear()
  {
    myFields.clear();
  }

  public String getFileName()
  {
    return myFileName;
  }

  @Override
  public List<String> getValuesFor(final String title)
  {
    return myFields.get(title);
  }

  /**
   * Load data from file
   */
  private void load()
  {
    if (getFileName() == null || getFileName().trim().length() == 0)
    {
      CorePlugin.logError(IStatus.WARNING, Messages.ExportCSVRegistry_EmptyFile,
          null);
      return;
    }

    try
    {
      final BufferedReader br = new BufferedReader(new FileReader(
          getFileName()));
      try
      {
        // build up list of lines in this file
        final List<String> lines = new ArrayList<String>();

        try
        {
          String nextLine = null;
          while ((nextLine = br.readLine()) != null)
          {
            lines.add(nextLine);
          }
        }
        finally
        {
          br.close();
        }

        // and now process those lines
        processLines(lines, myFields);
      }
      catch (final IOException e)
      {
        CorePlugin.logError(IStatus.WARNING,
            Messages.ExportCSVRegistry_ErrorOnReading, e);
      }
    }
    catch (final FileNotFoundException e)
    {
      CorePlugin.logError(IStatus.WARNING,
          Messages.ExportCSVRegistry_FileNotFound, null);
    }

  }

  public void reload()
  {
    clear();
    load();
  }

  public void setFileName(final String fileName)
  {
    myFileName = fileName;
  }
}
