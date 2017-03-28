package MWC.GUI.ptplot.Swing;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import MWC.GUI.JFreeChart.StepperChartPanel;
import MWC.GUI.JFreeChart.StepperXYPlot;
import MWC.GUI.Properties.PropertiesPanel;
import MWC.GUI.Properties.Swing.SwingPropertiesPanel;

public class SwingPlot extends SwingPropertiesPanel.CloseableJPanel
{

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  ///////////////////////////////////////////
	// member variables
	///////////////////////////////////////////


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
  }

  public void fillPlot()
  {
    if(_thePlot instanceof StepperChartPanel)
    {
      StepperChartPanel myPlot = (StepperChartPanel)_thePlot;
      StepperXYPlot plot = (StepperXYPlot) myPlot.getChart().getPlot();
      plot.zoom(0.0);
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
