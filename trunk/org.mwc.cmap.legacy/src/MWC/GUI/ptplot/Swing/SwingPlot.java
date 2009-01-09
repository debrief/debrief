package MWC.GUI.ptplot.Swing;

import javax.swing.*;
import MWC.GUI.ptplot.*;
import MWC.GUI.Properties.*;
import MWC.GUI.Properties.Swing.SwingPropertiesPanel;

import java.awt.*;
import java.awt.event.*;

public class SwingPlot extends SwingPropertiesPanel.CloseableJPanel
{

  ///////////////////////////////////////////
	// member variables
	///////////////////////////////////////////


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** the plot we are holding
	 */
	protected JPanel _thePlot;

	/** the tabbed panel we are stored in
	 */
	protected PropertiesPanel _theParent;

  /** the panel containing the buttons
   *
   */
  protected JPanel _buttonPanel;

	///////////////////////////////////////////
	// constructor
	///////////////////////////////////////////
	public SwingPlot(JPanel thePlot,
									 PropertiesPanel theParent)
	{
		super();

		_thePlot = thePlot;
		_theParent = theParent;

		initForm();
	}


	///////////////////////////////////////////
	// member functions
	///////////////////////////////////////////


  protected void initForm()
	{
		//
		setLayout(new BorderLayout());

		// store the track name
		this.setName(_thePlot.getName());

		// the buttons we need
		_buttonPanel = new JPanel();
		_buttonPanel.setLayout(new GridLayout(1,0));

		JButton closeBtn = new JButton("Close");
		closeBtn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				triggerClose();
			}
			});
		JButton fillBtn = new JButton("Fit to Window");
		fillBtn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				fillPlot();
			}
			});

		_buttonPanel.add(closeBtn);
		_buttonPanel.add(fillBtn);

		// put the bits together
		add("Center", _thePlot);
		add("South", _buttonPanel);
	}

  public void closePlot()
  {
    if(_thePlot instanceof MWCPlot)
    {
      MWCPlot myPlot = (MWCPlot)_thePlot;
      myPlot.doClose();
    }
  }

  public void fillPlot()
  {
    if(_thePlot instanceof MWCPlot)
    {
      MWCPlot myPlot = (MWCPlot)_thePlot;
      myPlot.fillPlot();
    }
  }

	public void triggerClose()
	{
    // inform the plot it is being closed
    closePlot();

    // and from the parent
    if(_theParent != null)
  		_theParent.remove((Object)this);

    // finally inform any listeners
    doClose();
	}

}
