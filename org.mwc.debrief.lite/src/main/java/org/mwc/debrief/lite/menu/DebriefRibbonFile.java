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
import java.io.IOException;

import javax.swing.AbstractAction;

import org.geotools.swing.JMapPane;
import org.mwc.debrief.lite.map.GeoToolMapRenderer;
import org.pushingpixels.flamingo.api.ribbon.JRibbon;
import org.pushingpixels.flamingo.api.ribbon.JRibbonBand;
import org.pushingpixels.flamingo.api.ribbon.RibbonElementPriority;
import org.pushingpixels.flamingo.api.ribbon.RibbonTask;

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

  private static class NewFileAction extends AbstractAction
  {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(final ActionEvent e)
    {
      System.out.println("Action clicked");

    }
  }

  protected static void addFileTab(final JRibbon ribbon,
      final GeoToolMapRenderer _geoMapRenderer)
  {

    final JRibbonBand fileMenu = new JRibbonBand("File", null);
    MenuUtils.addCommand("New", "images/16/new.png", new NewFileAction(),
        fileMenu, RibbonElementPriority.MEDIUM);
    MenuUtils.addCommand("New (default plot)", "images/16/new.png",
        new NewFileAction(), fileMenu, RibbonElementPriority.MEDIUM);
    MenuUtils.addCommand("Open Plot", "images/16/open.png", new NewFileAction(),
        fileMenu, RibbonElementPriority.MEDIUM);
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
        new NewFileAction(), importMenu, RibbonElementPriority.MEDIUM);
    importMenu.setResizePolicies(MenuUtils.getStandardRestrictivePolicies(
        importMenu));
    MenuUtils.addCommand("Copy Plot to PNG", "images/16/import.png",
        new CopyPlotAsPNG(_geoMapRenderer), importMenu,
        RibbonElementPriority.MEDIUM);
    fileMenu.setPreferredSize(new Dimension(150, 50));
    importMenu.setPreferredSize(new Dimension(50, 50));
    final RibbonTask fileTask = new RibbonTask("File", fileMenu, importMenu,
        exitMenu);
    fileMenu.setPreferredSize(new Dimension(50, 50));
    ribbon.addTask(fileTask);
  }
}
