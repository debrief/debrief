package org.mwc.cmap.TimeController.views;

import java.io.IOException;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.mwc.cmap.TimeController.TimeControllerPlugin;
import org.mwc.cmap.core.CorePlugin;

/**
 * does the actual animation
 * @author Ayesha
 *
 */
public class AnimatedGif
{
  private static ImageLoader loader = new ImageLoader();
  private Thread animateThread;
  private Image image;
  private ImageData[] imageDataArray;
  private Label lbl;
  private boolean cancel;
  private Rectangle bounds;
  public AnimatedGif(Label lbl,String filename) {
    this.lbl = lbl;
    try
    {
      imageDataArray = loader.load(FileLocator.openStream(TimeControllerPlugin.getDefault().getBundle(), new Path(filename), false));
    }
    catch (IOException e)
    {
      CorePlugin.logError(IStatus.ERROR, "Could not find pulsating gif", e);
    }
  }

  public void resume(Rectangle newBounds) {
    this.cancel=false;
    bounds = newBounds;
  }
  public void animate() {
    final Display display = lbl.getDisplay();
    final GC shellGC = new GC(lbl);
    final Color shellBackground = lbl.getBackground();
    bounds = lbl.getBounds();
    //when animate is called, restart thread
    cancel=false;
    if(imageDataArray.length>1) {
      animateThread = new Thread("Animate") {
        @SuppressWarnings("unused")
        public void run() {
          //run only if not cancel.
          
          if(!cancel) {
            Image offScreenImage = new Image(display, loader.logicalScreenWidth, loader.logicalScreenHeight);
            GC offScreenImageGC = new GC(offScreenImage);
            offScreenImageGC.setBackground(shellBackground);
            offScreenImageGC.fillRectangle(0, 0, loader.logicalScreenWidth, loader.logicalScreenHeight);

            try {
              /* Create the first image and draw it on the off-screen image. */
              int imageDataIndex = 0;  
              ImageData imageData = imageDataArray[imageDataIndex];
              offScreenImageGC.setBackground(shellBackground);
              if (image != null && !image.isDisposed()) image.dispose();
              image = new Image(display, imageData);
              offScreenImageGC.drawImage(
                  image,
                  0,
                  0,
                  imageData.width,
                  imageData.height,
                  imageData.x,
                  imageData.y,
                  imageData.width,
                  imageData.height);

              /* Now loop through the images, creating and drawing each one
               * on the off-screen image before drawing it on the shell. */
              int repeatCount = loader.repeatCount;
              while (loader.repeatCount == 0 || repeatCount > 0 ) {
                switch (imageData.disposalMethod) {
                  case SWT.DM_FILL_BACKGROUND:
                    /* Fill with the background color before drawing. */
                    // original version was similar to:
                    // http://www.java2s.com/Tutorial/Java/0300__SWT-2D-Graphics/DisplayananimatedGIF.htm
                    offScreenImageGC.setBackground(shellBackground);
                    offScreenImageGC.fillRectangle(imageData.x, imageData.y, imageData.width, imageData.height);
                    break;
                  case SWT.DM_FILL_PREVIOUS:
                    /* Restore the previous image before drawing. */
                    offScreenImageGC.drawImage(
                        image,
                        0,
                        0,
                        imageData.width,
                        imageData.height,
                        imageData.x,
                        imageData.y,
                        imageData.width,
                        imageData.height);
                     break;
                   default: 
                     break;
                }

                imageDataIndex = (imageDataIndex + 1) % imageDataArray.length;
                imageData = imageDataArray[imageDataIndex];
                image.dispose();
                image = new Image(display, imageData);
                offScreenImageGC.drawImage(
                    image,
                    0,
                    0,
                    imageData.width,
                    imageData.height,
                    imageData.x,
                    imageData.y,
                    imageData.width,
                    imageData.height);

                /* Draw the off-screen image to the shell. */
                shellGC.drawImage(offScreenImage, bounds.width-imageData.width-5, bounds.height/2-imageData.height/2);
               
                /* Sleep for the specified delay time (adding commonly-used slow-down fudge factors). */
                try {
                  int ms = imageData.delayTime * 10;
                  if (ms < 20) ms += 30;
                  if (ms < 30) ms += 10;
                  Thread.sleep(ms);
                } catch (InterruptedException e) {
                }

                /* If we have just drawn the last image, decrement the repeat count and start again. */
                if (imageDataIndex == imageDataArray.length - 1) repeatCount--;
              }
            } catch (SWTException ex) {
              System.err.println("There was an error animating the GIF");
            } finally {
              if (offScreenImage != null && !offScreenImage.isDisposed()) offScreenImage.dispose();
              if (offScreenImageGC != null && !offScreenImageGC.isDisposed()) offScreenImageGC.dispose();
              if (image != null && !image.isDisposed()) image.dispose();
            } 
          }
          }
      };
      animateThread.start();
    }
  }

  public void cancel() {
    this.cancel = true;    
  }

  public Image getImage()
  {
    return image;
  }


}