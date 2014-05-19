package org.mwc.cmap.media.xuggle.impl;

import java.awt.image.BufferedImage;

public interface ThreadUINotifier {
	
	void applyPause();
	
	void applyStop();
	
	void updateFrame(BufferedImage image, long position, long seek);
}
