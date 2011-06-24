// Copyright MWC 1999
// $RCSfile: MenuItemInfo.java,v $
// $Author: Ian.Mayo $
// $Log: MenuItemInfo.java,v $
// Revision 1.2  2004/05/25 15:43:34  Ian.Mayo
// Commit updates from home
//
// Revision 1.1.1.1  2004/03/04 20:31:24  ian
// no message
//
// Revision 1.1.1.1  2003/07/17 10:07:41  Ian.Mayo
// Initial import
//
// Revision 1.2  2002-05-28 09:25:57+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:14:11+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-11 13:03:47+01  ian_mayo
// Initial revision
//
// Revision 1.1  2001-08-10 10:12:51+01  administrator
// tidy up comments
//
// Revision 1.0  2001-07-17 08:43:00+01  administrator
// Initial revision
//
// Revision 1.2  2001-06-14 15:43:40+01  novatech
// include the name of the toolbar group this tool belongs to
//
// Revision 1.1  2001-01-03 13:41:56+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 21:51:07  ianmayo
// initial version
//
// Revision 1.3  2000-04-12 10:45:22+01  ian_mayo
// provide better support for garbage collection (close method)
//
// Revision 1.2  1999-12-03 14:32:27+00  ian_mayo
// added storage slot for mnemonic
//
// Revision 1.1  1999-10-12 15:36:25+01  ian_mayo
// Initial revision
//
// Revision 1.1  1999-07-27 10:50:32+01  administrator
// Initial revision
//
// Revision 1.1  1999-07-07 11:10:03+01  administrator
// Initial revision
//
// Revision 1.1  1999-06-16 15:37:56+01  sm11td
// Initial revision
//
// Revision 1.1  1999-02-01 16:08:16+00  sm11td
// Initial revision
//

package MWC.GUI.Tools;

import java.awt.MenuShortcut;

import MWC.GUI.Tool;

/**
 * description of information necessary to put an item on
 * a menu or a toolbar
 */
public class MenuItemInfo{
  /////////////////////////////////////////////////////////
  // member variables
  /////////////////////////////////////////////////////////
  /**
   * the name of the menu we are placing this item on
   */
  private String _menuName;
  /**
   * the name appearing on the menu
   */
  private String _menuItemName;

  /**
   * the toggle group this tool belongs to
   */
  private String _toggleGroup;

  /**
   * the tool to call when this item is selected
   */
  private Tool _theTool;
  /**
   * the shortcut representing this menu item
   */
  private MenuShortcut _theShortCut;

	/**
   * the shortcut key to apply to this command
   */
	private char _theMnemonic;

  /////////////////////////////////////////////////////////
  // constructor
  /**
   *
   * @param menuName name of the menu we are placing this item on
   * @param toggleGroup toggle group this item belongs to (or null)
   * @param menuItemName the name appearing on the menu
   * @param theTool the tool to call when this item is selected
   * @param theShortCut shortcut for this item
   * @param theMnemonic shortcut key for this command
   */
  public MenuItemInfo(String menuName,
                      String toggleGroup,
                      String menuItemName,
                      Tool theTool,
                      MenuShortcut theShortCut,
											char theMnemonic){
    _menuName     = menuName;
    _menuItemName = menuItemName;
    _toggleGroup  = toggleGroup;
    _theTool      = theTool;
    _theShortCut  = theShortCut;
		_theMnemonic  = theMnemonic;
  }

  /**
   *
   * @param menuName name of the menu we are placing this item on
   * @param toggleGroup toggle group this item belongs to (or null)
   * @param menuItemName the name appearing on the menu
   * @param theTool the tool to call when this item is selected
   * @param theShortCut shortcut for this item
   * @param theMnemonic shortcut key for this command
   */
  public MenuItemInfo(String menuName,
                      String toggleGroup,
                      Tool theTool,
                      MenuShortcut theShortCut,
											char theMnemonic){
    this(menuName, toggleGroup, menuName, theTool, theShortCut, theMnemonic);
  }


  /////////////////////////////////////////////////////////
  // member functions
  /**
 * /////////////////////////////////////////////////////////
 *
 */
  public String getMenuName(){
    return _menuName;
  }
  /**
   * getMenuItemName
   *
   * @return the returned String
   */
  public String getMenuItemName(){
    return _menuItemName;
  }
  /**
   * getToggleGroup
   *
   * @return the returned String
   */
  public String getToggleGroup()
  {
    return _toggleGroup;
  }
  /**
   * getTool
   *
   * @return the returned Tool
   */
  public Tool getTool(){
    return _theTool;
  }
  /**
   * getShortCut
   *
   * @return the returned MenuShortcut
   */
  public MenuShortcut getShortCut(){
    return _theShortCut;
  }
	/**
   * getMnemonic
   *
   * @return the returned char
   */
  public char getMnemonic()
	{
		return _theMnemonic;
	}

	/**
   * provide method to close us, clearing any local references
   *
   */
	public void close()
	{
		_theTool.close();
		_theTool = null;
	}

}
