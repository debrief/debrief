package org.mwc.debrief.lite.menu;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.geotools.swing.JMapPane;
import org.mwc.debrief.lite.DebriefLiteApp;
import org.mwc.debrief.lite.map.GeoToolMapRenderer;
import org.mwc.debrief.lite.util.DoSave;
import org.mwc.debrief.lite.util.DoSaveAs;
import org.pushingpixels.flamingo.api.ribbon.JRibbon;
import org.pushingpixels.flamingo.api.ribbon.JRibbonBand;
import org.pushingpixels.flamingo.api.ribbon.RibbonElementPriority;
import org.pushingpixels.flamingo.api.ribbon.RibbonTask;

import Debrief.GUI.Frames.Application;
import Debrief.GUI.Frames.Session;
import Debrief.ReaderWriter.XML.DebriefXMLReaderWriter;

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
      final RenderedImage image = map.getBaseImage();

      if (image != null)
      {
        final Transferable t = new Transferable()
        {

          @Override
          public Object getTransferData(final DataFlavor flavor)
              throws UnsupportedFlavorException, IOException
          {
            if (isDataFlavorSupported(flavor))
            {
              return image;
            }
            return null;
          }

          @Override
          public DataFlavor[] getTransferDataFlavors()
          {
            return new DataFlavor[]
            {DataFlavor.imageFlavor};
          }

          @Override
          public boolean isDataFlavorSupported(final DataFlavor flavor)
          {
            if (flavor == DataFlavor.imageFlavor)
              return true;
            return false;
          }

        };

        final ClipboardOwner co = new ClipboardOwner()
        {

          @Override
          public void lostOwnership(final Clipboard clipboard,
              final Transferable contents)
          {
            System.out.println("Copy to PNG: Lost Ownership");
          }

        };
        final Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
        cb.setContents(t, co);
      }
    }
  }

  // Actions

  private static class TODOAction extends AbstractAction
  {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(final ActionEvent e)
    {
      System.out.println("Not implemented yet");

    }
  }
  
  private static class NewFileAction extends AbstractAction
  {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    
    private JFrame _theFrame;
    private Session _session;
    public NewFileAction(final JFrame theFrame,final Session session)
    {
      _theFrame = theFrame;
      _session = session;
    }

    @Override
    public void actionPerformed(final ActionEvent e)
    {
      //ask user whether to save, if file is dirty.
      if(DebriefLiteApp.isDirty()) {
        int res = JOptionPane.showConfirmDialog(null, "Save changes before creating new file?");
        if(res == JOptionPane.OK_OPTION) {
          if(DebriefLiteApp.currentFileName!=null && DebriefLiteApp.currentFileName.endsWith(".rep")) {
            String newFileName = DebriefLiteApp.currentFileName.replaceAll(".rep", ".dpf");
            DebriefRibbonFile.saveChanges(newFileName,_session,_theFrame);
          }
          else {
            DebriefRibbonFile.saveChanges(DebriefLiteApp.currentFileName,_session,_theFrame);
          }
          DebriefLiteApp.resetPlot();
        }
        else if(res == JOptionPane.NO_OPTION) {
          DebriefLiteApp.resetPlot();
        }
        else {
          //do nothing
        }
      }
      else {
        DebriefLiteApp.resetPlot();
      }
      //open the new file

    }
  }

  
  

  protected static void addFileTab(final JRibbon ribbon,
      final GeoToolMapRenderer geoMapRenderer, final Session session)
  {

    final JRibbonBand fileMenu = new JRibbonBand("File", null);
    MenuUtils.addCommand("New", "images/16/new.png", new NewFileAction((JFrame)ribbon.getRibbonFrame(),session),
        fileMenu, RibbonElementPriority.MEDIUM);
    MenuUtils.addCommand("Open Plot", "images/16/open.png", new TODOAction(),
        fileMenu, RibbonElementPriority.MEDIUM);
    
    fileMenu.startGroup();
    MenuUtils.addCommand("Save", "images/16/save.png",
        new DoSave(session,ribbon.getRibbonFrame()), fileMenu, RibbonElementPriority.MEDIUM);
    MenuUtils.addCommand("Save as", "images/16/save-as.png",
        new DoSaveAs(session,ribbon.getRibbonFrame()), fileMenu, RibbonElementPriority.MEDIUM);
    fileMenu.setResizePolicies(MenuUtils.getStandardRestrictivePolicies(
        fileMenu));
    final JRibbonBand exitMenu = new JRibbonBand("Exit", null);
    MenuUtils.addCommand("Exit", "images/16/exit.png", new AbstractAction()
    {
      /**
       *
       */
      private static final long serialVersionUID = 1L;

      @Override
      public void actionPerformed(final ActionEvent e)
      {
        System.exit(0);
      }
    }, exitMenu, RibbonElementPriority.MEDIUM);
    exitMenu.setResizePolicies(MenuUtils.getStandardRestrictivePolicies(
        exitMenu));

    final JRibbonBand importMenu = new JRibbonBand("Import / Export", null);
    MenuUtils.addCommand("Import Replay", "images/16/import.png",
        new TODOAction(), importMenu, RibbonElementPriority.MEDIUM);
    importMenu.setResizePolicies(MenuUtils.getStandardRestrictivePolicies(
        importMenu));
    MenuUtils.addCommand("Copy Plot to PNG", "images/16/import.png",
        new CopyPlotAsPNG(geoMapRenderer), importMenu,
        RibbonElementPriority.MEDIUM);
    fileMenu.setPreferredSize(new Dimension(150, 50));
    importMenu.setPreferredSize(new Dimension(50, 50));
    final RibbonTask fileTask = new RibbonTask("File", fileMenu, importMenu,
        exitMenu);
    fileMenu.setPreferredSize(new Dimension(50, 50));
    ribbon.addTask(fileTask);
  }




  public static void saveChanges(String currentFileName,Session session,JFrame theFrame)
  {
    File targetFile = new File(currentFileName);
    if((targetFile!=null && targetFile.exists() && targetFile.canWrite()) 
        || (targetFile!=null && !targetFile.exists() && targetFile.getParentFile().canWrite()) ) 
    {        

      //export to this file.
      // if it already exists, check with rename/cancel
      OutputStream stream = null;
      try
      {
        stream = new FileOutputStream(targetFile.getAbsolutePath());
        DebriefXMLReaderWriter.exportThis(session, stream);
      }
      catch (FileNotFoundException e1)
      {
        Application.logError2(Application.ERROR, "Can't find file", e1);
      }
      finally {
        try
        {
          stream.close();
          DebriefLiteApp.currentFileName = targetFile.getAbsolutePath();
          DebriefLiteApp.setDirty(false);
          
        }
        catch (IOException e1)
        {
          //ignore
        }
      }
    }
    
  }
}
