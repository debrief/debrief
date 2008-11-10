package MWC.GUI.Swing.Spinner;

/* All import statements... */
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollBar;




/**
 * Spinner is a very simple and effective implementation of a spinner
 * control. It uses lightweight components and is able to 'spin' any
 * JComponent.
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
public class Spinner extends JPanel {
    private String ac = null;
    private JComponent component;
    private JScrollBar scroller;

    /**
     * Create a Spinner for a given component. The height of the spinner
     * will be the preferred height of the component. Thus, spinners
     * look best on JTextFields, or single line JLabels, etc.
     * The Spinner created will not change the contents of the component
     * when 'spun', a SpinListener should be registered for this.
     * @see #addSpinListener
     *
     * @param comp The component to turn into a Spinner.
     */
    public Spinner(JComponent comp) {
	super();
	this.component = comp;

	// Create
	scroller =
	    new JScrollBar(JScrollBar.VERTICAL, 1, 0, 0, 2);
	scroller.setPreferredSize(new
	    Dimension(scroller.getPreferredSize().width,
		      comp.getPreferredSize().height));
	this.add(comp); this.add(scroller);


	// Layout
	GridBagLayout gbl = new GridBagLayout();
	GridBagConstraints gbc = new GridBagConstraints();

	gbc.ipadx = 0; gbc.ipady = 0;
	gbc.insets = new Insets(0, 0, 0, 0);
	gbc.gridy = 0;
	gbc.gridx = 0;
	gbc.fill = GridBagConstraints.HORIZONTAL;
	gbc.weightx = 1.0;
	gbl.setConstraints(comp, gbc);

	gbc.gridx = 1;
	gbc.fill = GridBagConstraints.NONE;
	gbc.weightx = 0.0;
	gbl.setConstraints(scroller, gbc);

	this.setLayout(gbl);
    }


    /**
     * Adds a SpinListener to this spin control.
     *
     * @param listener The SpinListener to be added.
     */
    public void addSpinListener(SpinListener listener) {
	final SpinListener l = listener;
	scroller.addAdjustmentListener(new AdjustmentListener(){
		public void adjustmentValueChanged(AdjustmentEvent ae){
		    switch (ae.getValue()) {
		    case 0:
			l.spinnerSpunUp(new SpinEvent(scroller,
						      ac, component));
			break;
		    case 2:
			l.spinnerSpunDown(new SpinEvent(scroller,
							ac, component));
			break;
		    }
		    ae.getAdjustable().setValue(1);
		}
	    });
    }


    public void setToolTipText(String tip) {
	component.setToolTipText(tip);
	scroller.setToolTipText(tip);
    }


    /**
     * Set the action command for this spin control.
     *
     * @param cmd The action command to set.
     */
    public void setActionCommand(String cmd) {
	this.ac = cmd;
    }


    /**
     * Get the component that this spin control is made out of.
     *
     * @return The component being 'spun'.
     */
    public JComponent getComponent() {
	return this.component;
    }
}
