package MWC.GUI.Properties.AWT;

// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: AWTWorldLocationPropertyEditor.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.2 $
// $Log: AWTWorldLocationPropertyEditor.java,v $
// Revision 1.2  2004/05/25 15:29:28  Ian.Mayo
// Commit updates from home
//
// Revision 1.1.1.1  2004/03/04 20:31:20  ian
// no message
//
// Revision 1.1.1.1  2003/07/17 10:07:26  Ian.Mayo
// Initial import
//
// Revision 1.5  2002-07-08 11:52:51+01  ian_mayo
// <>
//
// Revision 1.4  2002-06-05 12:56:26+01  ian_mayo
// unnecessarily loaded
//
// Revision 1.3  2002-05-31 16:24:05+01  ian_mayo
// Implement doClose method
//
// Revision 1.2  2002-05-28 09:25:44+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:14:38+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-11 14:01:34+01  ian_mayo
// Initial revision
//
// Revision 1.0  2001-07-17 08:43:46+01  administrator
// Initial revision
//
// Revision 1.1  2001-01-03 13:42:45+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 21:45:32  ianmayo
// initial version
//
// Revision 1.2  1999-11-23 11:12:48+00  ian_mayo
// made into instantiations of generic editors
//


import java.beans.*;
import MWC.GenericData.*;
import MWC.GUI.*;
import java.awt.*;
import java.awt.event.*;
import MWC.GUI.Properties.*;

public class AWTWorldLocationPropertyEditor extends
					WorldLocationPropertyEditor
{
  /////////////////////////////////////////////////////////////
  // member variables
  ////////////////////////////////////////////////////////////
  Label _theLabel;
  Panel _theHolder;
  Button _selectBtn;
	Button _editBtn;
  
  /////////////////////////////////////////////////////////////
  // constructor
  ////////////////////////////////////////////////////////////
  
  /////////////////////////////////////////////////////////////
  // member functions
  ////////////////////////////////////////////////////////////

  public java.awt.Component getCustomEditor()
  {
		Panel btnHolder = new Panel();
		
    _theHolder = new Panel();
    _theHolder.setLayout(new BorderLayout());
    _theLabel = new Label("    ");
    _theHolder.add("Center",_theLabel);
		_editBtn = new Button("Edit");
    _editBtn.addActionListener(this);		
		btnHolder.add(_editBtn);
    _selectBtn = new Button("Select Point");
    _selectBtn.addActionListener(this);
		btnHolder.add(_selectBtn);
    _theHolder.add("East", btnHolder);
    resetData();
    return _theHolder;
  }

  public void actionPerformed(ActionEvent p1)
  {
		if(p1.getSource() == _selectBtn)
		{
			_selectBtn.setLabel("Dbl-click chart");
			_theChart.addCursorDblClickedListener(this);
		}
		else if(p1.getSource() == _editBtn)
		{
			_myVal = MWC.GUI.Properties.AWT.AWTWorldLocationEditor.doEdit(_myVal);
			
			// and redisplay the results
			resetData();
		}
  }

	
  protected void resetData()
  {
    if(_theLabel != null)
    { 
      if(_myVal != null)
        _theLabel.setText(MWC.Utilities.TextFormatting.BriefFormatLocation.toString(_myVal));
      else
        _theLabel.setText("Blank");
    }
  }
	
  public void cursorDblClicked(PlainChart theChart, 
                               WorldLocation theLocation, 
                               Point thePoint)
  {    
    double dp = _myVal.getDepth();
    _myVal = theLocation;
    _myVal.setDepth(dp);
    resetData();
    _theChart.removeCursorDblClickedListener(this);
    _selectBtn.setLabel("Select Point");
  }


  public void doClose()
  {
    _theChart.removeCursorDblClickedListener(this);
  }

}
