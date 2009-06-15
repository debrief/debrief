// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: SwingToolbar.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.2 $
// $Log: SwingToolbar.java,v $
// Revision 1.2  2004/05/25 15:44:38  Ian.Mayo
// Commit updates from home
//
// Revision 1.1.1.1  2004/03/04 20:31:26  ian
// no message
//
// Revision 1.1.1.1  2003/07/17 10:07:47  Ian.Mayo
// Initial import
//
// Revision 1.3  2003-03-10 10:21:00+00  ian_mayo
// Tidy javadoc comments
//
// Revision 1.2  2002-05-28 09:26:02+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:14:02+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-11 13:03:34+01  ian_mayo
// Initial revision
//
// Revision 1.2  2002-01-22 15:31:00+00  administrator
// Inform the ToolBarUI who its owner is
//
// Revision 1.1  2001-08-06 14:39:48+01  administrator
// set the UI of the Toolbar to our SPECIAL ui
//
// Revision 1.0  2001-07-17 08:42:51+01  administrator
// Initial revision
//
// Revision 1.2  2001-06-14 15:43:53+01  novatech
// give the toolbar a name
//
// Revision 1.1  2001-01-03 13:41:38+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 21:52:01  ianmayo
// initial version
//
// Revision 1.10  2000-08-16 14:13:37+01  ian_mayo
// make image loading more robust
//
// Revision 1.9  2000-08-07 14:06:01+01  ian_mayo
// switch to class-loader for images
//
// Revision 1.8  2000-06-19 15:07:50+01  ian_mayo
// tidied up, to re-use existing constructors
//
// Revision 1.7  2000-04-12 10:45:21+01  ian_mayo
// provide better support for garbage collection (close method)
//
// Revision 1.6  2000-03-14 14:51:29+00  ian_mayo
// minor formatting
//
// Revision 1.5  2000-03-14 09:54:51+00  ian_mayo
// use icons for these tools
//
// Revision 1.4  2000-01-12 15:38:20+00  ian_mayo
// support for toggled tools
//
// Revision 1.3  1999-12-03 14:32:11+00  ian_mayo
// added accelerator and mnemonic commands
//
// Revision 1.2  1999-11-25 13:26:52+00  ian_mayo
// added toggle button functionality
//
// Revision 1.1  1999-11-18 11:13:38+00  ian_mayo
// new Swing versions
//


package MWC.GUI.Tools.Swing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Dictionary;
import java.util.Hashtable;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;

import MWC.GUI.Tool;
import MWC.GUI.Toolbar;

/** implementation of toolbar using Swing controls
 * Swing*/
public class SwingToolbar extends JToolBar implements Toolbar {

  /////////////////////////////////////////////////////////
  // member objects
  /////////////////////////////////////////////////////////

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** keep track of the button groups we are creating
	 */
	Dictionary<String, ButtonGroup> _theGroups;

  /** the name of this toolbar - used for when it's dragged out
   */
  String _myName;

  /////////////////////////////////////////////////////////
  // constructor
  /////////////////////////////////////////////////////////
	public SwingToolbar(int theDirection, String name, MyMetalToolBarUI.ToolbarOwner  owner)
	{

    // override the UI, with our special one which keeps the toolbar on top, yet it
    // allows the toolbar to resize
    super.setUI(new MyMetalToolBarUI(owner));

    // align the buttons
    if(theDirection == Toolbar.HORIZONTAL)
    {
	    this.setOrientation(JToolBar.HORIZONTAL);
    }
    else
	    this.setOrientation(JToolBar.VERTICAL);

		/** get ready to store any button groups we have to create
		 */
		_theGroups = new Hashtable<String, ButtonGroup>();

    // store the name
    _myName = name;
  }

  public String toString()
  {
    return _myName;
  }

  public String getName()
  {
    return toString();
  }

  /////////////////////////////////////////////////
  // member functions
  //////////////////////////////////////////////////

	/** method to remove tools from toolbar
	 */
	public void close()
	{
		this.removeAll();
	}


  public void addTool(Tool theTool)
	{
		addTool(theTool,null,(char)0);
	}

  public void addTool(Tool theTool,
											java.awt.MenuShortcut theShortcut,
											char theMnemonic){
    // cast the tool back to the correct type

		// see if the tool has an image assigned
		String val = theTool.getImage();
		SwingToolbarButton theBtn = null;
		if(val != null)
		{
			// first try to get the URL of the image
			java.lang.ClassLoader loader = getClass().getClassLoader();
      if(loader != null)
      {
        java.net.URL imLoc = loader.getResource(val);
        if(imLoc != null)
        {
          ImageIcon im = new ImageIcon(imLoc);
          theBtn = new SwingToolbarButton(theTool, im);
        }
      }
			else
			{
				System.err.println("Failed to load image:" + val);
				theBtn = new SwingToolbarButton(theTool);
			}
		}

    // check if we have managed to create our button
    if(theBtn == null)
			theBtn = new SwingToolbarButton(theTool);

    this.add(theBtn);

		if(theShortcut != null)
		{
			this.registerKeyboardAction(new Listener(theTool),
																	KeyStroke.getKeyStroke(theShortcut.getKey(), java.awt.Event.CTRL_MASK) ,
																	JComponent.WHEN_IN_FOCUSED_WINDOW);
		}

		if(theMnemonic != ' ')
		{
			theBtn.setMnemonic(theMnemonic);
		}

  }

	public void addToggleTool(String group,
														Tool theTool)
	{
		// see if there is a group for this string
		javax.swing.ButtonGroup theGroup = (ButtonGroup) _theGroups.get(group);

		if(theGroup == null)
		{
			theGroup = new ButtonGroup();
			_theGroups.put(group, theGroup);
		}

		// now create the button, so we can add it to the group
		SwingToggleButton theBtn = null;

		// see if there is an image for this tool
		String theImage = theTool.getImage();
		if(theImage != null)
		{
			java.lang.ClassLoader loader = getClass().getClassLoader();
			if(loader != null)
			{
				java.net.URL imURL = loader.getResource(theImage);
				if(imURL != null)
					theBtn = new SwingToggleButton(theTool, new ImageIcon(imURL));
			}
		}

		// see if we have managed to create the button yet
		if(theBtn == null)
			theBtn = new SwingToggleButton(theTool);

		// apply some formatting
		theBtn.setHorizontalAlignment(AbstractButton.CENTER);

		//
		theGroup.add(theBtn);

		// put it into our panel
		this.add(theBtn);

		// if this is the first button, fire it
		if(_theGroups.size() == 1)
		{
			theBtn.doClick();
		}
	}


	public void addToggleTool(String group,
														Tool theTool,
														java.awt.MenuShortcut theShortcut,
														char theMnemonic)
	{
		// see if there is a group for this string
		javax.swing.ButtonGroup theGroup = (ButtonGroup) _theGroups.get(group);

		if(theGroup == null)
		{
			theGroup = new ButtonGroup();
			_theGroups.put(group, theGroup);
		}

		// now create the button, so we can add it to the group
		SwingToggleButton theBtn = null;

		// see if there is an image for this tool
		String theImage = theTool.getImage();

		if(theImage != null)
		{
			java.lang.ClassLoader loader = getClass().getClassLoader();
			if(loader != null)
			{
				java.net.URL imURL = loader.getResource(theImage);
				if(imURL != null)
					theBtn = new SwingToggleButton(theTool, new ImageIcon(imURL));
			}
		}

		// see if we have successfully created it
		if(theBtn == null)
			theBtn = new SwingToggleButton(theTool);

		//
		theGroup.add(theBtn);

		// put it into our panel
		this.add(theBtn);

		// if this is the first button, fire it
		if(_theGroups.size() == 1)
		{
			theBtn.doClick();
		}

		if(theMnemonic != ' ')
		{
			theBtn.setMnemonic(theMnemonic);
		}

		if(theShortcut != null)
		{
			this.registerKeyboardAction(new Listener(theTool),
																	KeyStroke.getKeyStroke(theShortcut.getKey(), java.awt.Event.CTRL_MASK) ,
																	JComponent.WHEN_IN_FOCUSED_WINDOW);
		}


	}

	protected class Listener implements ActionListener
	{
		protected Tool myTool;
		public Listener(Tool theTool)
		{
			myTool = theTool;
		}

		public void actionPerformed(ActionEvent p1)
		{
			myTool.execute();
		}
	}

}










