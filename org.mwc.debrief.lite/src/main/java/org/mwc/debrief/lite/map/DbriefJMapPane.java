package org.mwc.debrief.lite.map;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.util.HashMap;
import java.util.Map;

import org.geotools.map.MapContent;
import org.geotools.renderer.GTRenderer;
import org.geotools.renderer.label.LabelCacheImpl;
import org.geotools.renderer.lite.LabelCache;
import org.geotools.renderer.lite.StreamingRenderer;
import org.geotools.swing.RenderingExecutor;

/**
 * 
 * This class is copied from Geotools source code and has been modified for fixing 'stretching issue'
 * 
 * @author Unni Mana<unnivm@gmail.com>
 *
 */
public class DbriefJMapPane extends DebriefAbstractMapPane {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6798197149019502521L;
	
	private GTRenderer renderer;
    private BufferedImage baseImage;
    private Graphics2D baseImageGraphics;

    public DbriefJMapPane() {
        this(null);
    }
    
    public DbriefJMapPane(MapContent content) {
        this(content, null, null);
    }

    public DbriefJMapPane(MapContent content, RenderingExecutor executor, GTRenderer renderer) {
        super(content, executor);
        doSetRenderer(renderer);
    }
    
    @Override
    public void setMapContent(MapContent content) {
        super.setMapContent(content);
        if (content != null && renderer != null) {
            // If the new map content had layers to draw, and this pane is visible,
            // then the map content will already have been set with the renderer
            //
            if (renderer.getMapContent() != content) { // just check reference equality
                renderer.setMapContent(mapContent);
            }
        }
    }
    
    public GTRenderer getRenderer() {
        if (renderer == null) {
            doSetRenderer(new StreamingRenderer());
        }
        return renderer;
    }
    
    public void setRenderer(GTRenderer renderer) {
        doSetRenderer(renderer);
    }
    
    private void doSetRenderer(GTRenderer newRenderer) {
        if (newRenderer != null) {
            Map<Object, Object> hints = newRenderer.getRendererHints();
            if (hints == null) {
                hints = new HashMap<Object, Object>();
            }

            if (newRenderer instanceof StreamingRenderer) {
                if (hints.containsKey(StreamingRenderer.LABEL_CACHE_KEY)) {
                    labelCache = (LabelCache) hints.get(StreamingRenderer.LABEL_CACHE_KEY);
                } else {
                    labelCache = new LabelCacheImpl();
                    hints.put(StreamingRenderer.LABEL_CACHE_KEY, labelCache);
                }
            }

            newRenderer.setRendererHints(hints);

            if (mapContent != null) {
                newRenderer.setMapContent(mapContent);
            }
        }

        renderer = newRenderer;
    }
    
    public RenderedImage getBaseImage() {
        return this.baseImage;
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (drawingLock.tryLock()) {
            try {
                if (baseImage != null) {
                    Graphics2D g2 = (Graphics2D) g;
                    g2.drawImage(baseImage, imageOrigin.x, imageOrigin.y, null);
                }
            } finally {
                drawingLock.unlock();
            }
        }
    }
    
    @Override
    protected void drawLayers(boolean createNewImage) {
        drawingLock.lock();
        try {
            if (mapContent != null
                    && !mapContent.getViewport().isEmpty()
                    && acceptRepaintRequests.get()) {

                Rectangle r = getVisibleRect();
                if (baseImage == null || createNewImage) {
//                    baseImage =
//                            GraphicsEnvironment.getLocalGraphicsEnvironment()
//                                    .getDefaultScreenDevice()
//                                    .getDefaultConfiguration()
//                                    .createCompatibleImage(
//                                            r.width, r.height, Transparency.TRANSLUCENT);


                	baseImage = new BufferedImage(r.width, r.height, BufferedImage.TYPE_INT_ARGB);
                	
                    if (baseImageGraphics != null) {
                        baseImageGraphics.dispose();
                    }

                    baseImageGraphics = baseImage.createGraphics();
                    clearLabelCache.set(true);

                } else {
                    baseImageGraphics.setBackground(getBackground());
                    baseImageGraphics.clearRect(0, 0, r.width, r.height);
                }

                if (mapContent != null && !mapContent.layers().isEmpty()) {
                	getRenderingExecutor()
                            .submit(mapContent, getRenderer(), baseImageGraphics, this);
                }
            }
        } finally {
            drawingLock.unlock();
        }
    }

}
