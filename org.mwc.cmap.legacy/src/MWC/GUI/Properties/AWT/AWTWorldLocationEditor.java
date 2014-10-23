/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 */
package MWC.GUI.Properties.AWT;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Checkbox;
import java.awt.CheckboxGroup;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.ParseException;

import MWC.GenericData.WorldLocation;
import MWC.Utilities.ReaderWriter.XML.MWCXMLReader;
import MWC.Utilities.TextFormatting.BriefFormatLocation;

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
	public AWTWorldLocationEditor(final WorldLocation val, final Frame parent)
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
	static public WorldLocation doEdit(final WorldLocation val)
	{
		final Frame parent = new Frame("scrap");
		WorldLocation res = new WorldLocation(val);
		final AWTWorldLocationEditor aw = new AWTWorldLocationEditor(res, parent);
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
		
		final MWC.Utilities.TextFormatting.BriefFormatLocation.brokenDown _lat
			= new BriefFormatLocation.brokenDown(_result.getLat(), true);
		final MWC.Utilities.TextFormatting.BriefFormatLocation.brokenDown _long
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
		final Panel btnHolder = new Panel();
		final Panel dataHolder = new Panel();
		final Panel latHolder = new Panel();
		final Panel latBtnHolder = new Panel();
		final Panel longHolder = new Panel();
		final Panel longBtnHolder = new Panel();
		
		// create the ok/cancel buttons
		_okBtn = new Button("OK");
		_okBtn.addActionListener(this);
		_cancelBtn = new Button("Cancel");
		_cancelBtn.addActionListener(this);
		btnHolder.add(_okBtn);
		btnHolder.add(_cancelBtn);
		
		// handle the window closing
		this.addWindowListener(new WindowAdapter(){
			public void windowClosing(final WindowEvent e)
			{
				doWindowClosing(e);
			}			
			});
		
		// latitude group
		final CheckboxGroup latGrp = new CheckboxGroup();		
		_latDegs = new TextField("  ");
		_latMins = new TextField("  ");
		_northBtn = new Checkbox("North", latGrp, true);
		_southBtn = new Checkbox("South", latGrp, false);
		final GridLayout gl1 = new GridLayout(0, 2);
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
		final CheckboxGroup longGrp = new CheckboxGroup();		
		_longDegs = new TextField("  ");
		_longMins = new TextField("  ");
		_eastBtn = new Checkbox("East", longGrp, true);
		_westBtn = new Checkbox("West", longGrp, false);
		final GridLayout gl2 = new GridLayout(0, 2);
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

		final GridLayout gl3 = new GridLayout(0, 2);
		dataHolder.setLayout(gl3);
		dataHolder.add(latHolder);
		dataHolder.add(longHolder);
		
		this.setLayout(new BorderLayout());
		this.add(dataHolder, "Center");
		this.add(btnHolder, "South");
		
	}
	


	public void actionPerformed(final ActionEvent p1)
	{
		// see what value it was
		if(p1.getSource() == _cancelBtn)
		{
			initData();
		}
		else if(p1.getSource() == _okBtn)
		{
			try
			{
				// extract the data values
				double latV = MWCXMLReader.readThisDouble(_latDegs.getText()) +
							   MWCXMLReader.readThisDouble(_latMins.getText()) / 60.0;
				
				if(_southBtn.getState())
					latV = latV * -1.0;
	
				// extract the data values
				double longV = MWCXMLReader.readThisDouble(_longDegs.getText()) +
						MWCXMLReader.readThisDouble(_longMins.getText()) / 60.0;
				 
				if(_westBtn.getState())
					longV = longV * -1.0;
				
				_result = new WorldLocation(latV, longV, _result.getDepth());
			}
			catch(final ParseException pe)
			{
				MWC.Utilities.Errors.Trace.trace(pe);	
			}
		}
		
		setVisible(false);
	}
	
	
	// also handle the window being closed
	public void doWindowClosing(final WindowEvent e)
	{
		// process the cancel event
		initData();
	}
}
