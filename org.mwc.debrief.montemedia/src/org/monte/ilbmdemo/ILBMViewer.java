/*
 * @(#)ILBMViewer.java  
 * 
 * Copyright (c) 2012 Werner Randelshofer, Goldau, Switzerland.
 * All rights reserved.
 * 
 * You may not use, copy or modify this file, except in compliance with the
 * license agreement you entered into with Werner Randelshofer.
 * For details see accompanying license terms.
 */
package org.monte.ilbmdemo;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import org.monte.media.gui.Worker;
import org.monte.media.ilbm.ColorCyclingMemoryImageSource;
import org.monte.media.ilbm.ILBMDecoder;
import org.monte.media.io.ByteArrayImageInputStream;

/**
 * ILBMViewer.
 *
 * @author Werner Randelshofer
 * @version $Id: ILBMViewer.java 296 2013-01-03 07:37:38Z werner $
 */
public class ILBMViewer extends javax.swing.JPanel {

    private class Handler implements DropTargetListener {

        /**
         * Called when a drag operation has encountered the
         * <code>DropTarget</code>. <P>
         *
         * @param dtde the <code>DropTargetDragEvent</code>
         */
        @Override
        public void dragEnter(DropTargetDragEvent event) {
            if (event.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                event.acceptDrag(DnDConstants.ACTION_COPY);
            } else {
                event.rejectDrag();
            }
        }

        /**
         * The drag operation has departed the
         * <code>DropTarget</code> without dropping. <P>
         *
         * @param dte the <code>DropTargetEvent</code>
         */
        @Override
        public void dragExit(DropTargetEvent event) {
            // Nothing to do
        }

        /**
         * Called when a drag operation is ongoing on the
         * <code>DropTarget</code>. <P>
         *
         * @param dtde the <code>DropTargetDragEvent</code>
         */
        @Override
        public void dragOver(DropTargetDragEvent event) {
            if (event.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                event.acceptDrag(DnDConstants.ACTION_COPY);
            } else {
                event.rejectDrag();
            }
        }

        /**
         * The drag operation has terminated with a drop on this
         * <code>DropTarget</code>. This method is responsible for undertaking
         * the transfer of the data associated with the gesture. The
         * <code>DropTargetDropEvent</code> provides a means to obtain a
         * <code>Transferable</code> object that represents the data object(s)
         * to be transfered.<P> From this method, the
         * <code>DropTargetListener</code> shall accept or reject the drop via
         * the acceptDrop(int dropAction) or rejectDrop() methods of the
         * <code>DropTargetDropEvent</code> parameter. <P> Subsequent to
         * acceptDrop(), but not before,
         * <code>DropTargetDropEvent</code>'s getTransferable() method may be
         * invoked, and data transfer may be performed via the returned
         * <code>Transferable</code>'s getTransferData() method. <P> At the
         * completion of a drop, an implementation of this method is required to
         * signal the success/failure of the drop by passing an appropriate
         * <code>boolean</code> to the
         * <code>DropTargetDropEvent</code>'s dropComplete(boolean success)
         * method. <P> Note: The actual processing of the data transfer is not
         * required to finish before this method returns. It may be deferred
         * until later. <P>
         *
         * @param dtde the <code>DropTargetDropEvent</code>
         */
        @Override
        @SuppressWarnings("unchecked")
        public void drop(DropTargetDropEvent event) {
            if (event.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                event.acceptDrop(DnDConstants.ACTION_COPY);

                try {
                    List<File> files = (List<File>) event.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);

                    showILBMImages(files);

                } catch (IOException e) {
                    JOptionPane.showConfirmDialog(
                            ILBMViewer.this,
                            "Could not access the dropped data.",
                            "ILBMViewer: Drop Failed",
                            JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE);
                } catch (UnsupportedFlavorException e) {
                    JOptionPane.showConfirmDialog(
                            ILBMViewer.this,
                            "Unsupported data flavor.",
                            "ILBMViewer: Drop Failed",
                            JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                event.rejectDrop();
            }
        }

        /**
         * Called if the user has modified the current drop gesture. <P>
         *
         * @param dtde the <code>DropTargetDragEvent</code>
         */
        @Override
        public void dropActionChanged(DropTargetDragEvent event) {
            // Nothing to do
        }
    }
    private Handler handler = new Handler();

    /**
     * Creates new form ILBMViewer
     */
    public ILBMViewer() {
        initComponents();
        new DropTarget(this, handler);
        new DropTarget(label, handler);
    }

    protected BufferedImage getAmigaPicture(File f) throws IOException {
        return ImageIO.read(f);
    }

    protected BufferedImage getAmigaPictureViaByteArray(File f) throws IOException {
        return getAmigaPicture(getData(f));
    }

    protected byte[] getData(File f) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        FileInputStream in = new FileInputStream(f);
        try {
            byte[] buf = new byte[512];
            for (int len = in.read(buf); len != -1; len = in.read(buf)) {
                out.write(buf, 0, len);
            }
        } finally {
            in.close();
        }
        return out.toByteArray();
    }

    protected BufferedImage getAmigaPicture(byte[] data) throws IOException {
        return ImageIO.read(new ByteArrayImageInputStream(data));
    }

    protected Image getAmigaPictureWithColorCycling(File f) throws IOException {
        FileInputStream in = new FileInputStream(f);
        try {
            ColorCyclingMemoryImageSource ccmis = new ILBMDecoder(in).produce().get(0);
            if (ccmis.isColorCyclingAvailable()) {
                ccmis.start();
            }
            return Toolkit.getDefaultToolkit().createImage(ccmis);
        } finally {
            in.close();
        }
    }

    public void showILBMImages(final List<File> files) {
        label.setEnabled(false);
        if (label.getIcon() instanceof ImageIcon) {
            ImageIcon icon = (ImageIcon) label.getIcon();
            label.setIcon(null);
            label.setDisabledIcon(null);
            icon.getImage().flush();
        }
        new Worker<Image>() {
            @Override
            protected Image construct() throws Exception {
                for (File f : files) {
                    //return getAmigaPicture(f);
                    //return getAmigaPictureViaByteArray(f);
                    return getAmigaPictureWithColorCycling(f);
                }
                return null;
            }

            @Override
            protected void done(Image value) {
                if (value == null) {
                    failed(new IOException("Could not load image."));
                    return;
                }
                label.setText(null);
                ImageIcon icon = new ImageIcon(value);
                label.setIcon(icon);
                label.setDisabledIcon(icon);
                SwingUtilities.getWindowAncestor(ILBMViewer.this).pack();
            }

            @Override
            protected void failed(Throwable error) {
                label.setText("<html><b>Error</b><br>" + error.getMessage());
                SwingUtilities.getWindowAncestor(ILBMViewer.this).pack();
            }

            @Override
            protected void finished() {
                label.setEnabled(true);
            }
        }.start();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        label = new javax.swing.JLabel();

        setLayout(new java.awt.BorderLayout());

        label.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        label.setText("Drop ILBM file here.");
        add(label, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JFrame f = new JFrame("ILBM Image Viewer");
                f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                f.add(new ILBMViewer());
                f.setSize(200, 200);
                f.setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel label;
    // End of variables declaration//GEN-END:variables
}
