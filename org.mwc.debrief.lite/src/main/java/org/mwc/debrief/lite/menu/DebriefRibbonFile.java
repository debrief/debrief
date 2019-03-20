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
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

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

  
  private static class OpenPlotAction extends AbstractAction
  {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private final JFrame _theFrame;
    private final Session _session;
    private Runnable _doReset;
    private boolean isRepFile;
    
    public OpenPlotAction(final JFrame theFrame, final Session session,
        final Runnable doReset,final boolean isRep)
    {
      _theFrame = theFrame;
      _session = session;
      _doReset = doReset;
      isRepFile = isRep;
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
      
      final String[] fileTypes = isRepFile?new String[] {"rep"}:new String[] {"dpf","xml"};
      final String descr = isRepFile?"Debrief Replay File":"Debrief Plot Files";
      if (DebriefLiteApp.isDirty())
      {
        int res = JOptionPane.showConfirmDialog(null,
            "Save changes before opening new plot?");
        if (res == JOptionPane.OK_OPTION)
        {
          if (DebriefLiteApp.currentFileName != null
              && DebriefLiteApp.currentFileName.endsWith(".rep"))
          {
            String newFileName = DebriefLiteApp.currentFileName.replaceAll(
                ".rep", ".dpf");
            DebriefRibbonFile.saveChanges(newFileName, _session, _theFrame);
          }
          else
          {
            DebriefRibbonFile.saveChanges(DebriefLiteApp.currentFileName,
                _session, _theFrame);
          }
          doFileOpen(fileTypes,descr,isRepFile,_doReset);
          
        }
        else if (res == JOptionPane.NO_OPTION)
        {
          doFileOpen(fileTypes,descr,isRepFile,_doReset);
        }
        else
        {
          // do nothing
        }
      }
      else
      {
        doFileOpen(fileTypes,descr,isRepFile,_doReset);
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
    private Runnable _doReset;
    
    public NewFileAction(final JFrame theFrame, final Session session,
        final Runnable doReset)
    {
      _theFrame = theFrame;
      _session = session;
      _doReset = doReset;
    }

    @Override
    public void actionPerformed(final ActionEvent e)
    {
      // ask user whether to save, if file is dirty.
      if (DebriefLiteApp.isDirty())
      {
        final int res = JOptionPane.showConfirmDialog(null,
            "Save changes before creating new file?");
        if (res == JOptionPane.OK_OPTION)
        {
          if (DebriefLiteApp.currentFileName != null
              && DebriefLiteApp.currentFileName.endsWith(".rep"))
          {
            final File f = new File(DebriefLiteApp.currentFileName);
            final String newname = f.getName().substring(0,f.getName().lastIndexOf(".rep"));
            final String newFileName = DoSaveAs.showSaveDialog(f.getParentFile(), newname);
            if(newFileName == null || newFileName.length() ==0)
            {
              // drop out, don't do reset.
              return;
            }
            DebriefLiteApp.currentFileName.replaceAll(
                ".rep", ".dpf");
            DebriefRibbonFile.saveChanges(newFileName, _session, _theFrame);
          }
          else if(DebriefLiteApp.currentFileName == null)
          {
            // ok, we have to do a save-as operation
            DoSaveAs saveAs = new DoSaveAs(_session, _theFrame);
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

  
  

  protected static void addFileTab(final JRibbon ribbon,
      final GeoToolMapRenderer geoMapRenderer, final Session session, final Runnable resetAction)
  {

    final JRibbonBand fileMenu = new JRibbonBand("File", null);
    MenuUtils.addCommand("New", "icons/16/new.png", new NewFileAction(
        (JFrame) ribbon.getRibbonFrame(), session, resetAction), fileMenu,
        RibbonElementPriority.MEDIUM);
    MenuUtils.addCommand("Open Plot", "icons/16/open.png", new OpenPlotAction((JFrame)ribbon.getRibbonFrame(),session,resetAction,false),
        fileMenu, RibbonElementPriority.MEDIUM);
    
    fileMenu.startGroup();
    MenuUtils.addCommand("Save", "icons/16/save.png",
        new DoSave(session,ribbon.getRibbonFrame()), fileMenu, RibbonElementPriority.MEDIUM);
    MenuUtils.addCommand("Save as", "icons/16/save-as.png",
        new DoSaveAs(session,ribbon.getRibbonFrame()), fileMenu, RibbonElementPriority.MEDIUM);
    fileMenu.setResizePolicies(MenuUtils.getStandardRestrictivePolicies(
        fileMenu));
    final JRibbonBand exitMenu = new JRibbonBand("Exit", null);
    MenuUtils.addCommand("Exit", "icons/16/exit.png", new AbstractAction()
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
    MenuUtils.addCommand("Import Replay", "icons/16/import.png",
        new OpenPlotAction((JFrame)ribbon.getRibbonFrame(),session,resetAction,true), importMenu, RibbonElementPriority.MEDIUM);
    importMenu.setResizePolicies(MenuUtils.getStandardRestrictivePolicies(
        importMenu));
    MenuUtils.addCommand("Copy Plot to PNG", "icons/16/import.png",
        new CopyPlotAsPNG(geoMapRenderer), importMenu,
        RibbonElementPriority.MEDIUM);
    fileMenu.setPreferredSize(new Dimension(150, 50));
    importMenu.setPreferredSize(new Dimension(50, 50));
    final RibbonTask fileTask = new RibbonTask("File", fileMenu, importMenu,
        exitMenu);
    fileMenu.setPreferredSize(new Dimension(50, 50));
    ribbon.addTask(fileTask);
  }




  public static void doFileOpen(String[] fileTypes, String descr, boolean isRepFile,Runnable doReset)
  {
    //load the new selected file
    final File fileToOpen = showOpenDialog(fileTypes,descr);
    if(fileToOpen !=null) {
      doReset.run();
      if(isRepFile) {
        DebriefLiteApp.openRepFile(fileToOpen);
      }
      else{
        DebriefLiteApp.openPlotFile(fileToOpen);
      }
    }
  }




  public static File showOpenDialog(final String[] fileTypes,final String descr)
  {
    JFileChooser fileChooser = new JFileChooser();
    FileNameExtensionFilter restrict = new FileNameExtensionFilter(descr, fileTypes); 
    fileChooser.setFileFilter(restrict); 
    fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
    fileChooser.setMultiSelectionEnabled(false);
    int res = fileChooser.showOpenDialog(null);
    if(res == JFileChooser.APPROVE_OPTION) {
      return fileChooser.getSelectedFile();
    }
    
    return null;
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
