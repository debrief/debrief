package MWC.GUI.Dialogs.AWT;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.SystemColor;

class Separator extends Component {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	int   thickness= 2;

	public void paint(final Graphics g) {
		final Dimension size     = getSize();
        final Color highlight = SystemColor.controlLtHighlight;
        final Color shadow   = SystemColor.controlShadow;

		g.setColor(shadow);
		int y = (size.height/2) - (thickness/2);
		while(y < (size.height/2)) {
			g.drawLine(0, y, size.width, y);
			++y;
		}
        g.setColor(highlight);
		y = size.height/2;
		while(y < ((size.height/2) + (thickness/2))) {
			g.drawLine(0, y, size.width, y);
			++y;
		}	
	}
	public Dimension getPreferredSize() {
		final Dimension prefsz = getSize();

		prefsz.height = thickness;
		return prefsz;
	}
}
