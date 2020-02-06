
package MWC.GUI.Swing.Spinner;

/*******************************************************************************
 * Debrief - the Open Source Maritime Analysis Application
 * http://debrief.info
 *  
 * (C) 2000-2020, Deep Blue C Technology Ltd
 *  
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html)
 *  
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 *******************************************************************************/
public class Spinner extends JPanel {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
		String ac = null;
    JComponent component;
    JScrollBar scroller;

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
    public Spinner(final JComponent comp) {
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
	final GridBagLayout gbl = new GridBagLayout();
	final GridBagConstraints gbc = new GridBagConstraints();

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
    public void addSpinListener(final SpinListener listener) {
	final SpinListener l = listener;
	scroller.addAdjustmentListener(new AdjustmentListener(){
		public void adjustmentValueChanged(final AdjustmentEvent ae){
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


    public void setToolTipText(final String tip) {
	component.setToolTipText(tip);
	scroller.setToolTipText(tip);
    }


    /**
     * Set the action command for this spin control.
     *
     * @param cmd The action command to set.
     */
    public void setActionCommand(final String cmd) {
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
