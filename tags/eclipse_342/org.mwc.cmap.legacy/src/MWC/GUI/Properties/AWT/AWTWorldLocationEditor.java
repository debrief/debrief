package MWC.GUI.Properties.AWT;

import java.awt.*;
import java.awt.event.*;
import MWC.GenericData.WorldLocation;
import MWC.Utilities.TextFormatting.*;

public class AWTWorldLocationEditor extends Dialog implements ActionListener
{
  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/////////////////////////////////////////////////////////////
  // constructor
  ////////////////////////////////////////////////////////////
	Button _okBtn;
	Button _cancelBtn;
	TextField _latDegs;
	TextField _latMins;
	Checkbox _northBtn;
	Checkbox _southBtn;
	TextField _longDegs;
	TextField _longMins;
	Checkbox _eastBtn;
	Checkbox _westBtn;
	
  WorldLocation _result;
	WorldLocation _initial;
	
  /////////////////////////////////////////////////////////////
  // constructor
  ////////////////////////////////////////////////////////////
	public AWTWorldLocationEditor(WorldLocation val, Frame parent)
	{
		super(parent, true);
				
		// build the GUI
		initGUI();	
		
		// size it
		this.setSize(510, 150);		
		
		// store the initial position
		_initial = val;
		
		// initialise the data
		initData();
					
	}
	
  /////////////////////////////////////////////////////////////
  // member functions
  ////////////////////////////////////////////////////////////
	static public WorldLocation doEdit(WorldLocation val)
	{
		Frame parent = new Frame("scrap");
		WorldLocation res = new WorldLocation(val);
		AWTWorldLocationEditor aw = new AWTWorldLocationEditor(res, parent);
		aw.setVisible(true);
		res = aw.getResult();
		parent.dispose(); 
		return res;
	}

	/** return the current value of the field
	 */
	protected WorldLocation getResult()
	{
		return _result;
	}
	
	
	/** initialise the text boxes
	 */
	protected void initData()
	{
		// initialise the results parameter
		_result = _initial;
		
		MWC.Utilities.TextFormatting.BriefFormatLocation.brokenDown _lat
			= new BriefFormatLocation.brokenDown(_result.getLat(), true);
		MWC.Utilities.TextFormatting.BriefFormatLocation.brokenDown _long
			= new BriefFormatLocation.brokenDown(_result.getLong(), false);

		
		_latDegs.setText("" + _lat.deg);
		_latMins.setText("" + (_lat.min + _lat.sec / (60.0 * 60.0)));
		
		if(_lat.hem == 'N')
			_northBtn.setState(true);
		else
			_southBtn.setState(true);

		_longDegs.setText("" + _long.deg);
		_longMins.setText("" + (_long.min + _long.sec / (60.0 * 60.0)));
		
		if(_long.hem == 'E')
			_eastBtn.setState(true);
		else
			_westBtn.setState(true);
		
	}
	
	/** create the GUI
	 */
	protected void initGUI()
	{
		// create the holders
		Panel btnHolder = new Panel();
		Panel dataHolder = new Panel();
		Panel latHolder = new Panel();
		Panel latBtnHolder = new Panel();
		Panel longHolder = new Panel();
		Panel longBtnHolder = new Panel();
		
		// create the ok/cancel buttons
		_okBtn = new Button("OK");
		_okBtn.addActionListener(this);
		_cancelBtn = new Button("Cancel");
		_cancelBtn.addActionListener(this);
		btnHolder.add(_okBtn);
		btnHolder.add(_cancelBtn);
		
		// handle the window closing
		this.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e)
			{
				doWindowClosing(e);
			}			
			});
		
		// latitude group
		CheckboxGroup latGrp = new CheckboxGroup();		
		_latDegs = new TextField("  ");
		_latMins = new TextField("  ");
		_northBtn = new Checkbox("North", latGrp, true);
		_southBtn = new Checkbox("South", latGrp, false);
		GridLayout gl1 = new GridLayout(0, 2);
		latHolder.setLayout(gl1);
		latHolder.add(new Label("Lat"));
		latHolder.add(new Label(" "));
		latHolder.add(new Label("Degs"));
		latHolder.add(_latDegs);
		latHolder.add(new Label("Mins"));
		latHolder.add(_latMins);
		latHolder.add(new Label("Hemi"));
		latBtnHolder.add(_northBtn);
		latBtnHolder.add(_southBtn);
		latHolder.add(latBtnHolder);
		
		// longitude group
		CheckboxGroup longGrp = new CheckboxGroup();		
		_longDegs = new TextField("  ");
		_longMins = new TextField("  ");
		_eastBtn = new Checkbox("East", longGrp, true);
		_westBtn = new Checkbox("West", longGrp, false);
		GridLayout gl2 = new GridLayout(0, 2);
		longHolder.setLayout(gl2);
		longHolder.add(new Label("long"));
		longHolder.add(new Label(" "));
		longHolder.add(new Label("Degs"));
		longHolder.add(_longDegs);
		longHolder.add(new Label("Mins"));
		longHolder.add(_longMins);
		longHolder.add(new Label("Hemi"));
		longBtnHolder.add(_eastBtn);
		longBtnHolder.add(_westBtn);
		longHolder.add(longBtnHolder);															

		GridLayout gl3 = new GridLayout(0, 2);
		dataHolder.setLayout(gl3);
		dataHolder.add(latHolder);
		dataHolder.add(longHolder);
		
		this.setLayout(new BorderLayout());
		this.add(dataHolder, "Center");
		this.add(btnHolder, "South");
		
	}
	


	public void actionPerformed(ActionEvent p1)
	{
		// see what value it was
		if(p1.getSource() == _cancelBtn)
		{
			initData();
		}
		else if(p1.getSource() == _okBtn)
		{
			// extract the data values
			double latV = Double.valueOf(_latDegs.getText()).doubleValue() +
										Double.valueOf(_latMins.getText()).doubleValue() / 60.0;
			
			if(_southBtn.getState())
				latV = latV * -1.0;

			// extract the data values
			double longV = Double.valueOf(_longDegs.getText()).doubleValue() +
										Double.valueOf(_longMins.getText()).doubleValue() / 60.0;
			 
			if(_westBtn.getState())
				longV = longV * -1.0;
			
			_result = new WorldLocation(latV, longV, _result.getDepth());
		}
		
		setVisible(false);
	}
	
	
	// also handle the window being closed
	public void doWindowClosing(WindowEvent e)
	{
		// process the cancel event
		initData();
	}
}
