package info.limpet.stackedcharts.ui.view;

import info.limpet.stackedcharts.ui.editor.Activator;

import java.awt.Dimension;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.SystemFlavorMap;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.geom.Rectangle2D;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.graphics.Rectangle;
import org.freehep.graphicsbase.util.UserProperties;
import org.freehep.graphicsio.emf.EMFGraphics2D;
import org.freehep.graphicsio.pdf.PDFGraphics2D;
import org.jfree.ui.Drawable;

public class DrawableWMFTransfer implements Transferable
{

  public static final DataFlavor EMF_FLAVOR = new DataFlavor("image/emf",
      "Enhanced Meta File");

  static
  {
    // EMF graphics clipboard format
    try
    {
      final SystemFlavorMap sfm =
          (SystemFlavorMap) SystemFlavorMap.getDefaultFlavorMap();
      sfm.addFlavorForUnencodedNative("ENHMETAFILE", EMF_FLAVOR);// seems to be a key command!!
      sfm.addUnencodedNativeForFlavor(EMF_FLAVOR, "ENHMETAFILE");// seems to be a key command!!
    }
    catch (final Exception e)
    {
      System.err.println("[WMFTransfer,static initializer] Error "
          + e.getClass().getName() + ", " + e.getMessage());
    }
  }

  public static final DataFlavor PDF_FLAVOR = new DataFlavor("application/pdf",
      "PDF");

  static
  {
    // PDF graphics clipboard format
    try
    {
      final SystemFlavorMap sfm =
          (SystemFlavorMap) SystemFlavorMap.getDefaultFlavorMap();
      sfm.addFlavorForUnencodedNative("PDF", PDF_FLAVOR);// seems to be a key command!!
      sfm.addUnencodedNativeForFlavor(PDF_FLAVOR, "PDF");// seems to be a key command!!
    }
    catch (final Exception e)
    {
      System.err.println("[PDFTransfer,static initializer] Error "
          + e.getClass().getName() + ", " + e.getMessage());
    }
  }

  private static DataFlavor[] supportedFlavors =
  {EMF_FLAVOR, PDF_FLAVOR};

  private final Drawable _drawable;

  private final Rectangle _bounds;

  public DrawableWMFTransfer(final Drawable drawable, final Rectangle bounds)
  {
    _drawable = drawable;
    _bounds = bounds;
  }

  @Override
  public Object getTransferData(final DataFlavor flavor)
      throws UnsupportedFlavorException, IOException
  {
    if (flavor.equals(EMF_FLAVOR))
    {
      Activator.getDefault().getLog().log(
          new Status(IStatus.INFO, "Mime type image/emf requested", null));
      final ByteArrayOutputStream out = new ByteArrayOutputStream();

      final EMFGraphics2D g2d =
          new EMFGraphics2D(out, new Dimension(_bounds.width, _bounds.height));
      g2d.startExport();
      _drawable.draw(g2d, new Rectangle2D.Double(0, 0, _bounds.width,
          _bounds.height));

      // Cleanup
      g2d.endExport();

      return new ByteArrayInputStream(out.toByteArray());
    }
    else if (flavor.equals(PDF_FLAVOR))
    {
      Activator.getDefault().getLog()
          .log(
              new Status(IStatus.INFO, "Mime type application/pdf requested",
                  null));
      final ByteArrayOutputStream out = new ByteArrayOutputStream();

      final PDFGraphics2D g2d =
          new PDFGraphics2D(out, new Dimension(_bounds.width, _bounds.height));
      final UserProperties properties = new UserProperties();
      properties.setProperty(PDFGraphics2D.PAGE_SIZE,
          PDFGraphics2D.CUSTOM_PAGE_SIZE);
      properties.setProperty(PDFGraphics2D.CUSTOM_PAGE_SIZE,
          new java.awt.Dimension(_bounds.width, _bounds.height));
      g2d.setProperties(properties);
      g2d.startExport();
      _drawable.draw(g2d, new Rectangle2D.Double(0, 0, _bounds.width,
          _bounds.height));

      // Cleanup
      g2d.endExport();
      return new ByteArrayInputStream(out.toByteArray());
    }
    else
      throw new UnsupportedFlavorException(flavor);
  }

  @Override
  public DataFlavor[] getTransferDataFlavors()
  {
    return supportedFlavors;
  }

  @Override
  public boolean isDataFlavorSupported(final DataFlavor flavor)
  {
    for (final DataFlavor f : supportedFlavors)
    {
      if (f.equals(flavor))
        return true;
    }
    return false;
  }
}