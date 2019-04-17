package org.mwc.debrief.lite.util;

import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.BufferedImage;

import java.io.IOException;

import javax.swing.JComponent;

public class ClipboardUtils
{
  public static void copyToClipboard(JComponent component)
  {
    final BufferedImage img = new BufferedImage(component.getWidth(), component
        .getHeight(), BufferedImage.TYPE_INT_ARGB);
    final Graphics g = img.getGraphics();
    component.paint(g);
    g.dispose();

    if (img != null)
    {
      final Transferable t = new Transferable()
      {

        @Override
        public Object getTransferData(final DataFlavor flavor)
            throws UnsupportedFlavorException, IOException
        {
          if (isDataFlavorSupported(flavor))
          {
            return img;
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
        public void lostOwnership(java.awt.datatransfer.Clipboard clipboard,
            Transferable contents)
        {
          System.out.println("Copy to PNG: Lost Ownership");
        }

      };
      
      final Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
      cb.setContents(t, co);
    }
  }
}
