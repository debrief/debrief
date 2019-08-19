/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2018, Deep Blue C Technology Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 */
package org.mwc.debrief.lite.menu;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.geotools.swing.JMapPane;
import org.mwc.debrief.lite.DebriefLiteApp;
import org.mwc.debrief.lite.map.GeoToolMapRenderer;
import org.mwc.debrief.lite.util.DoSave;
import org.mwc.debrief.lite.util.DoSaveAs;
import org.pushingpixels.flamingo.api.common.FlamingoCommand;
import org.pushingpixels.flamingo.api.common.JCommandButton;
import org.pushingpixels.flamingo.api.common.JCommandMenuButton;
import org.pushingpixels.flamingo.api.common.icon.ImageWrapperResizableIcon;
import org.pushingpixels.flamingo.api.common.popup.JCommandPopupMenu;
import org.pushingpixels.flamingo.api.common.popup.JPopupPanel;
import org.pushingpixels.flamingo.api.common.popup.PopupPanelCallback;
import org.pushingpixels.flamingo.api.ribbon.JRibbon;
import org.pushingpixels.flamingo.api.ribbon.JRibbonBand;
import org.pushingpixels.flamingo.api.ribbon.JRibbonFrame;
import org.pushingpixels.flamingo.api.ribbon.RibbonElementPriority;
import org.pushingpixels.flamingo.api.ribbon.RibbonTask;

import Debrief.GUI.Frames.Application;
import Debrief.GUI.Frames.Session;
import Debrief.ReaderWriter.XML.DebriefXMLReaderWriter;
import MWC.GUI.ToolParent;

public class DebriefRibbonFile
{

  private static class CopyPlotAsPNG extends AbstractAction
  {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private final GeoToolMapRenderer mapRenderer;

    public CopyPlotAsPNG(final GeoToolMapRenderer _geoMapRenderer)
    {
      mapRenderer = _geoMapRenderer;
    }

    @Override
    public void actionPerformed(final ActionEvent e)
    {
      final JMapPane map = (JMapPane) mapRenderer.getMap();
      org.mwc.debrief.lite.util.ClipboardUtils.copyToClipboard(map);
    }
  }

  @SuppressWarnings("unused")
  private static class ImportFileAction extends AbstractAction
  {

    private static final String TYPE_REP="rep";
    private static final String TYPE_DPF="dpf";
    private static final String TYPE_NMEA="log";
    private static final String TYPE_TIF="tif";
    
    private String importFileType;
    protected ImportFileAction(String type) {
      importFileType = type;
    }
    /**
     *
     */
    private static final long serialVersionUID = -804226120198968206L;

    @Override
    public void actionPerformed(final ActionEvent e)
    {
      final String initialFileLocation = DebriefLiteApp.getDefault()
          .getProperty(LAST_FILE_OPEN_LOCATION);
      final File fileToOpen ;
      if(TYPE_REP.equals(importFileType)) {
        fileToOpen = showOpenDialog(initialFileLocation, new String[]
        {"rep","dsf","dtf"}, "Debrief replay file (*.rep, *.dsf, *.dtf)");
      }
      else if(TYPE_NMEA.equals(importFileType))
      {
        fileToOpen = showOpenDialog(initialFileLocation, new String[]
            {"log"}, "NMEA File (*.log)");
      }
      else if(TYPE_TIF.contentEquals(importFileType))
      {
        fileToOpen = showOpenDialog(initialFileLocation, new String[]
            {"tif"}, "TIF file (*.tif)");
      }
      else
      {
        fileToOpen = showOpenDialog(initialFileLocation, new String[]
        {"dpf"}, "Debrief plot file (*.dpf)");
      }
      if (fileToOpen != null)
      {
        if(TYPE_REP.equalsIgnoreCase(importFileType))
        {
          DebriefLiteApp.openRepFile(fileToOpen);
        }
        else if(TYPE_NMEA.equalsIgnoreCase(importFileType))
        {
          DebriefLiteApp.openNMEAFile(fileToOpen);
        }
        else if(TYPE_TIF.equalsIgnoreCase(importFileType))
        {
          DebriefLiteApp.handleImportTIFFile(fileToOpen);
        }
        else {
          DebriefLiteApp.openPlotFile(fileToOpen);
        }
        DebriefLiteApp.getDefault().setProperty(LAST_FILE_OPEN_LOCATION,
            fileToOpen.getParentFile().getAbsolutePath());
      }
    }
  }

  private static class NewFileAction extends AbstractAction
  {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private final JFrame _theFrame;
    private final Session _session;
    private final Runnable _doReset;
    private final boolean _close;

    public NewFileAction(final JFrame theFrame, final Session session,
        final Runnable doReset, final boolean close)
    {
      _theFrame = theFrame;
      _session = session;
      _doReset = doReset;
      _close = close;
    }

    @Override
    public void actionPerformed(final ActionEvent e)
    {
      // ask user whether to save, if file is dirty.
      if (DebriefLiteApp.isDirty())
      {
        final int res = JOptionPane.showConfirmDialog(_theFrame, _close
            ? "Do you want to save the plot before closing?"
            : "Save changes before creating new file?");
        if (res == JOptionPane.OK_OPTION)
        {
          if (DebriefLiteApp.currentFileName != null
              && DebriefLiteApp.currentFileName.endsWith(".rep"))
          {
            final File f = new File(DebriefLiteApp.currentFileName);
            final String newname = f.getName().substring(0, f.getName()
                .lastIndexOf(".rep"));
            final String newFileName = DoSaveAs.showSaveDialog(f
                .getParentFile(), newname);
            if (newFileName == null || newFileName.length() == 0)
            {
              // drop out, don't do reset.
              return;
            }
            DebriefLiteApp.currentFileName.replaceAll(".rep", ".dpf");
            DebriefRibbonFile.saveChanges(newFileName, _session, _theFrame);
          }
          else if (DebriefLiteApp.currentFileName == null)
          {
            // ok, we have to do a save-as operation
            final DoSaveAs saveAs = new DoSaveAs(_session, _theFrame);
            saveAs.actionPerformed(e);
          }
          else
          {
            // ok, it's a DPF file. we can juse save it
            DebriefRibbonFile.saveChanges(DebriefLiteApp.currentFileName,
                _session, _theFrame);
          }

          _doReset.run();
        }
        else if (res == JOptionPane.NO_OPTION)
        {
          _doReset.run();
        }
        else
        {
          // do nothing
        }
      }
      else
      {
        _doReset.run();
      }
    }
  }

  private static class OpenPlotAction extends AbstractAction
  {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private final JFrame _theFrame;
    private final Session _session;
    private final Runnable _doReset;
    private final boolean isRepFile;

    public OpenPlotAction(final JFrame theFrame, final Session session,
        final Runnable doReset, final boolean isRep)
    {
      _theFrame = theFrame;
      _session = session;
      _doReset = doReset;
      isRepFile = isRep;
    }

    @Override
    public void actionPerformed(final ActionEvent e)
    {

      final String[] fileTypes = isRepFile ? new String[]
      {"rep"} : new String[]
      {"dpf", "xml"};
      final String descr = isRepFile ? "Debrief Replay File"
          : "Debrief Plot Files";
      if (DebriefLiteApp.isDirty())
      {
        final int res = JOptionPane.showConfirmDialog(null,
            "Save changes before opening new plot?");
        if (res == JOptionPane.OK_OPTION)
        {
          if (DebriefLiteApp.currentFileName != null
              && DebriefLiteApp.currentFileName.endsWith(".rep"))
          {
            final File f = new File(DebriefLiteApp.currentFileName);
            final String newname = f.getName().substring(0, f.getName()
                .lastIndexOf(".rep"));
            final String newFileName = DoSaveAs.showSaveDialog(f
                .getParentFile(), newname);
            if (newFileName == null || newFileName.length() == 0)
            {
              // drop out, don't do reset.
              return;
            }
            DebriefLiteApp.currentFileName.replaceAll(".rep", ".dpf");
            DebriefRibbonFile.saveChanges(newFileName, _session, _theFrame);
          }
          else if (DebriefLiteApp.currentFileName == null)
          {
            // ok, we have to do a save-as operation
            final DoSaveAs saveAs = new DoSaveAs(_session, _theFrame);
            saveAs.actionPerformed(e);
          }
          else
          {
            // ok, it's a DPF file. we can juse save it
            DebriefRibbonFile.saveChanges(DebriefLiteApp.currentFileName,
                _session, _theFrame);
          }
          doFileOpen(fileTypes, descr, isRepFile, _doReset);
        }
        else if (res == JOptionPane.NO_OPTION)
        {
          doFileOpen(fileTypes, descr, isRepFile, _doReset);
        }
        else
        {
          // do nothing
        }
      }
      else
      {
        doFileOpen(fileTypes, descr, isRepFile, _doReset);
      }
    }

  }

  private static class SavePopupMenu extends JCommandPopupMenu
  {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public SavePopupMenu(final Session session, final JRibbonFrame theFrame)
    {
      final Image saveImage = MenuUtils.createImage("icons/16/save.png");
      final ImageWrapperResizableIcon imageIcon = ImageWrapperResizableIcon
          .getIcon(saveImage, new Dimension(16, 16));
      final JCommandMenuButton saveButton = new JCommandMenuButton("Save",
          imageIcon);
      saveButton.setName("save");
      saveButton.getActionModel().addActionListener(new DoSave(session,
          theFrame));
      addMenuButton(saveButton);
      final Image saveAsImage = MenuUtils.createImage("icons/16/save-as.png");
      final ImageWrapperResizableIcon imageIcon2 = ImageWrapperResizableIcon
          .getIcon(saveAsImage, new Dimension(16, 16));
      final JCommandMenuButton saveAsButton = new JCommandMenuButton("Save As",
          imageIcon2);
      saveAsButton.setName("saveas");
      saveAsButton.getActionModel().addActionListener(new DoSaveAs(session,
          theFrame));
      addMenuButton(saveAsButton);
    }
  }

  private static final String LAST_FILE_OPEN_LOCATION =
      "last_fileopen_location";

  public static FlamingoCommand closeButton;

  private static RibbonTask fileTask;

  protected static void addFileTab(final JRibbon ribbon,
      final GeoToolMapRenderer geoMapRenderer, final Session session,
      final Runnable resetAction)
  {

    final JRibbonBand fileMenu = new JRibbonBand("File", null);
    MenuUtils.addCommand("New", "icons/24/new.png", new NewFileAction(ribbon
        .getRibbonFrame(), session, resetAction, false), fileMenu,
        RibbonElementPriority.TOP);
    MenuUtils.addCommand("Open", "icons/24/open.png", new OpenPlotAction(ribbon
        .getRibbonFrame(), session, resetAction, false), fileMenu,
        RibbonElementPriority.TOP);
    final SavePopupMenu savePopup = new SavePopupMenu(session, ribbon
        .getRibbonFrame());
    MenuUtils.addCommand("Save", "icons/24/save.png", new DoSave(session, ribbon
        .getRibbonFrame()), fileMenu, RibbonElementPriority.TOP,
        new PopupPanelCallback()
        {
          @Override
          public JPopupPanel getPopupPanel(final JCommandButton commandButton)
          {
            return savePopup;
          }
        });
    closeButton = MenuUtils.addCommand("Close", "icons/24/close.png",
        new NewFileAction(ribbon.getRibbonFrame(), session, resetAction, true),
        fileMenu, RibbonElementPriority.TOP);
    closeButton.setEnabled(false);
    fileMenu.setResizePolicies(MenuUtils.getStandardRestrictivePolicies(
        fileMenu));

    final JRibbonBand importMenu = new JRibbonBand("Import", null);
    MenuUtils.addCommand("Replay", "icons/24/import.png",
        new ImportFileAction(ImportFileAction.TYPE_REP), importMenu, RibbonElementPriority.TOP);
    MenuUtils.addCommand("Plot", "icons/24/plot_file.png",
        new ImportFileAction(".dpf"), importMenu, RibbonElementPriority.TOP);
    MenuUtils.addCommand("NMEA", "icons/24/pulse.png",
        new ImportFileAction(ImportFileAction.TYPE_NMEA), importMenu, RibbonElementPriority.TOP);
    MenuUtils.addCommand("TIF", "icons/24/map.png",
        new ImportFileAction(ImportFileAction.TYPE_TIF), importMenu, RibbonElementPriority.TOP);
    
    importMenu.setResizePolicies(MenuUtils.getStandardRestrictivePolicies(
        importMenu));

    final JRibbonBand exportMenu = new JRibbonBand("Export", null);

    MenuUtils.addCommand("Clipboard", "icons/24/export_gpx.png", new CopyPlotAsPNG(
        geoMapRenderer), exportMenu, RibbonElementPriority.TOP);
    exportMenu.setResizePolicies(MenuUtils.getStandardRestrictivePolicies(
        exportMenu));
    fileMenu.setPreferredSize(new Dimension(150, 50));
    importMenu.setPreferredSize(new Dimension(50, 50));
    fileTask = new RibbonTask("File", fileMenu, importMenu, exportMenu);
    fileMenu.setPreferredSize(new Dimension(50, 50));
    ribbon.addTask(fileTask);
  }

  public static void doFileOpen(final String[] fileTypes, final String descr,
      final boolean isRepFile, final Runnable doReset)
  {
    // load the new selected file
    final String initialFileLocation = DebriefLiteApp.getDefault().getProperty(
        LAST_FILE_OPEN_LOCATION);
    final File fileToOpen = showOpenDialog(initialFileLocation, fileTypes,
        descr);
    if (fileToOpen != null)
    {
      doReset.run();
      if (isRepFile)
      {
        DebriefLiteApp.openRepFile(fileToOpen);
      }
      else
      {
        
        DebriefLiteApp.openPlotFile(fileToOpen);
      }
      DebriefLiteApp.getDefault().setProperty(LAST_FILE_OPEN_LOCATION,
          fileToOpen.getParentFile().getAbsolutePath());
    }
  }

  public static RibbonTask getFileTask()
  {
    return fileTask;
  }

  public static void saveChanges(final String currentFileName,
      final Session session, final JFrame theFrame)
  {
    final File targetFile = new File(currentFileName);
    if ((targetFile != null && targetFile.exists() && targetFile.canWrite())
        || (targetFile != null && !targetFile.exists() && targetFile
            .getParentFile().canWrite()))
    {
      // export to this file.
      // if it already exists, check with rename/cancel
      OutputStream stream = null;
      try
      {
        stream = new FileOutputStream(targetFile.getAbsolutePath());
        DebriefXMLReaderWriter.exportThis(session, stream);
        // remember the last file save location
        DebriefLiteApp.getDefault().setProperty(DoSaveAs.LAST_FILE_LOCATION,
            targetFile.getParentFile().getAbsolutePath());
      }
      catch (final FileNotFoundException e1)
      {
        Application.logError2(ToolParent.ERROR, "Can't find file", e1);
      }
      finally
      {
        try
        {
          stream.close();
          DebriefLiteApp.currentFileName = targetFile.getAbsolutePath();
          DebriefLiteApp.setDirty(false);

        }
        catch (final IOException e1)
        {
          // ignore
        }
      }
    }

  }

  public static File showOpenDialog(final String openDir,
      final String[] fileTypes, final String descr)
  {

    final JFileChooser fileChooser = new JFileChooser();
    if (openDir != null)
    {
      fileChooser.setCurrentDirectory(new File(openDir));
    }
    final FileNameExtensionFilter restrict = new FileNameExtensionFilter(descr,
        fileTypes);
    fileChooser.setFileFilter(restrict);
    fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
    fileChooser.setMultiSelectionEnabled(false);
    final int res = fileChooser.showOpenDialog(null);
    if (res == JFileChooser.APPROVE_OPTION)
    {
      return fileChooser.getSelectedFile();
    }

    return null;
  }
}
