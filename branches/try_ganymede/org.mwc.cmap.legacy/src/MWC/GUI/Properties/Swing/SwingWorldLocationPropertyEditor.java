package MWC.GUI.Properties.Swing;

// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: SwingWorldLocationPropertyEditor.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.2 $
// $Log: SwingWorldLocationPropertyEditor.java,v $
// Revision 1.2  2004/05/25 15:29:50  Ian.Mayo
// Commit updates from home
//
// Revision 1.1.1.1  2004/03/04 20:31:20  ian
// no message
//
// Revision 1.1.1.1  2003/07/17 10:07:27  Ian.Mayo
// Initial import
//
// Revision 1.5  2002-07-08 11:52:57+01  ian_mayo
// <>
//
// Revision 1.4  2002-06-05 12:56:25+01  ian_mayo
// unnecessarily loaded
//
// Revision 1.3  2002-05-31 16:24:09+01  ian_mayo
// Implement doClose method
//
// Revision 1.2  2002-05-28 09:25:45+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:14:36+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-11 14:01:29+01  ian_mayo
// Initial revision
//
// Revision 1.4  2002-03-12 15:30:44+00  administrator
// Set tooltops for edit/select buttons
//
// Revision 1.3  2002-03-12 11:22:57+00  administrator
// Use the location's toString method instead of doing our own formatting (this lets us edit child-classes of location which do their own presentation formatting
//
// Revision 1.2  2002-02-19 20:23:11+00  administrator
// Set GUI component names to assist JFCUnit testing
//
// Revision 1.1  2001-11-20 18:15:02+00  administrator
// improvements in screen updating
//
// Revision 1.0  2001-07-17 08:43:36+01  administrator
// Initial revision
//
// Revision 1.1  2001-01-03 13:42:41+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 21:46:11  ianmayo
// initial version
//
// Revision 1.6  2000-09-27 14:32:43+01  ian_mayo
// reflect name change
//
// Revision 1.5  2000-08-14 15:49:16+01  ian_mayo
// tidy up UI
//
// Revision 1.4  2000-02-21 16:37:39+00  ian_mayo
// Changed layout to make Panel smaller
//
// Revision 1.3  1999-11-26 15:45:48+00  ian_mayo
// implementing layer management
//
// Revision 1.2  1999-11-23 11:05:03+00  ian_mayo
// further introduction of SWING components
//
// Revision 1.1  1999-11-16 16:42:18+00  ian_mayo
// Initial revision
//


import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import MWC.GUI.PlainChart;
import MWC.GUI.Properties.WorldLocationPropertyEditor;
import MWC.GenericData.WorldLocation;

public class SwingWorldLocationPropertyEditor extends
					WorldLocationPropertyEditor
{
  /////////////////////////////////////////////////////////////
  // member variables
  ////////////////////////////////////////////////////////////
  JLabel _theLabel;
  JPanel _theHolder;
  JButton _selectBtn;
	JButton _editBtn;

  /////////////////////////////////////////////////////////////
  // constructor
  ////////////////////////////////////////////////////////////

  /////////////////////////////////////////////////////////////
  // member functions
  ////////////////////////////////////////////////////////////

  public java.awt.Component getCustomEditor()
  {
		JPanel btnHolder = new JPanel();

    _theHolder = new JPanel();
    _theHolder.setLayout(new java.awt.BorderLayout());
    _theLabel = new JLabel("        ");
    _theHolder.add("West",_theLabel);
		_theHolder.add(_theLabel);
		_editBtn = new JButton("Edit");
    _editBtn.setToolTipText("Open a window to type in a new lat/long/depth");
    _editBtn.setName("Edit");
    _editBtn.setMargin(new java.awt.Insets(0,0,0,0));
    _editBtn.addActionListener(this);
		btnHolder.setLayout(new java.awt.BorderLayout());
		btnHolder.add("West", _editBtn);
    _selectBtn = new JButton("Select Point");
    _selectBtn.setToolTipText("Choose to double-click on the chart to set the position");
    _selectBtn.setName("SelectPoint");
    _selectBtn.setMargin(new java.awt.Insets(0,0,0,0));
    _selectBtn.addActionListener(this);
		btnHolder.add("East", _selectBtn);
    _theHolder.add("East", btnHolder);
    resetData();

		// and make it the smallest size possible
		_theHolder.setPreferredSize(_theHolder.getMinimumSize());
    return _theHolder;
  }

  public void actionPerformed(ActionEvent p1)
  {
		if(p1.getSource() == _selectBtn)
		{
			_selectBtn.setText("Dbl-click chart");
			_theChart.addCursorDblClickedListener(this);
		}
		else if(p1.getSource() == _editBtn)
		{
      if(_myVal != null)
        _myVal = MWC.GUI.Properties.Swing.SwingWorldLocationEditorFrame.doEdit(_myVal);


			// and redisplay the results
			resetData();
		}
  }


  protected void resetData()
  {
    if(_theLabel != null)
    {
      if(_myVal != null)
        _theLabel.setText(_myVal.toString());
      else
        _theLabel.setText("Blank");
    }
  }

  public void cursorDblClicked(PlainChart theChart,
                               WorldLocation theLocation,
                               java.awt.Point thePoint)
  {
    double dp = _myVal.getDepth();
    _myVal = theLocation;
    _myVal.setDepth(dp);
    resetData();
    _theChart.removeCursorDblClickedListener(this);
    _selectBtn.setText("Select Point");
  }

  public void doClose()
  {
    _theChart.removeCursorDblClickedListener(this);
  }

}
