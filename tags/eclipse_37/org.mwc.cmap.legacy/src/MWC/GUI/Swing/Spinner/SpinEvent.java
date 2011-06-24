package MWC.GUI.Swing.Spinner;

/* All import statements... */
import java.util.EventObject;

import javax.swing.JComponent;



/**
 * An event which is used to dispatch information regarding a Spinner
 * being 'spun'.
 *
 * This code is copyright Kevin Mayer (kmayer@layer9.com) 2001 and can
 * be copied and reproduced freely so long as all original comments in the
 * source code remain in tact. Comments may be added but not removed. If
 * you do modify the spinner classes, I would appreciate seeing your
 * modifications (though this is not compulsory).
 * Basically - here's something for all to use!
 *
 * @author Kevin Mayer
 *         (<A HREF="mailto:kmayer@layer9.com">kmayer@layer9.com</A>)
 * @version (RCS: $Revision: 1.1.1.1 $)
 */
public class SpinEvent extends EventObject {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
		private String command;
    private JComponent component;


    /**
     * Create a new SpinEvent.
     *
     * @param src The source of the event.
     * @param cmd The action command of the Spinner causing the event.
     * @param comp The component that the Spinner is 'spinning'.
     */
    public SpinEvent(Object src, String cmd, JComponent comp) {
	super(src);
	this.command = cmd;
	this.component = comp;
    }


    /**
     * Get the action command of this Spinner that caused this SpinEvent.
     *
     * @return The action command of this SpinEvent.
     */
    public String getActionCommand() {
	return this.command;
    }


    /**
     * Get the component that was 'spun' by the Spinner that
     * caused this event.
     *
     * @return The component that was 'spun'.
     */
    public JComponent getComponent() {
	return this.component;
    }
}
