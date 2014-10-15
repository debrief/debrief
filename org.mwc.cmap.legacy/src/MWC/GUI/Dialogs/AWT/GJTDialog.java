/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package MWC.GUI.Dialogs.AWT;

import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Point;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class GJTDialog extends Dialog {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected DialogClient client;
	protected boolean      centered;
	public void setVisible(final boolean visible) {
		pack();

		if(centered) {
			final Dimension frameSize = getParent().getSize();
			final Point frameLoc  = getParent().getLocation();
			final Dimension mySize    = getSize();
			int x,y;

			x = frameLoc.x + (frameSize.width/2) -
							(mySize.width/2);
			y = frameLoc.y + (frameSize.height/2) -
							(mySize.height/2);

			setBounds(x,y,getSize().width,getSize().height);
		}
		super.setVisible(visible);
	}
	
	public GJTDialog(final Frame frame, final String title, 
					final DialogClient aClient, final boolean modal) {
		this(frame, title, aClient, true, modal);
	}
	public GJTDialog(final Frame frame, final String title,
					final DialogClient aClient, final boolean centered,
					final boolean modal) {
		super(frame, title, modal);

		setClient(aClient);
		setCentered(centered);

		final WindowAdapter wa = new myWindowAdapter();		
		addWindowListener(wa);
	}
	public void setCentered(final boolean centered) {
		this.centered = centered;
	}
	public void setClient(final DialogClient client) {
		this.client = client;
	}
	public void dispose() {
		final Frame f = Util.getFrame(this);

		super.dispose();

		f.toFront();

		if(client != null)
			client.dialogDismissed(this);
	}


	protected class myWindowAdapter extends WindowAdapter
	{
		public void windowClosing(final WindowEvent event) {
			dispose();
			if(client != null)
				client.dialogCancelled(GJTDialog.this);
		}		
	}
	


}
