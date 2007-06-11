// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: AWTApplication.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.2 $
// $Log: AWTApplication.java,v $
// Revision 1.2  2005/12/13 09:04:19  Ian.Mayo
// Tidying - as recommended by Eclipse
//
// Revision 1.1.1.2  2003/07/21 14:47:06  Ian.Mayo
// Re-import Java files to keep correct line spacing
//
// Revision 1.3  2003-03-19 15:38:07+00  ian_mayo
// improvements according to IntelliJ inspector
//
// Revision 1.2  2002-05-28 12:27:49+01  ian_mayo
// after update
//
// Revision 1.1  2002-05-28 09:12:20+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-23 12:30:09+01  ian_mayo
// Initial revision
//
// Revision 1.1  2001-08-06 12:44:09+01  administrator
// add method to return frame
//
// Revision 1.0  2001-07-17 08:41:42+01  administrator
// Initial revision
//
// Revision 1.1  2001-01-03 13:40:55+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 20:45:02  ianmayo
// initial import of files
//
// Revision 1.4  2000-11-24 10:53:51+00  ian_mayo
// tidying up
//
// Revision 1.3  2000-08-09 16:03:58+01  ian_mayo
// remove stray semi-colons
//
// Revision 1.2  2000-01-20 10:07:07+00  ian_mayo
// added application-wide clipboard
//
// Revision 1.1  1999-10-12 15:34:25+01  ian_mayo
// Initial revision
//
// Revision 1.8  1999-09-14 15:51:57+01  administrator
// splash screen support
//
// Revision 1.7  1999-07-27 12:10:00+01  administrator
// changed update method of canvas to updateMe
//
// Revision 1.6  1999-07-27 09:27:47+01  administrator
// general improvements
//
// Revision 1.5  1999-07-19 12:39:44+01  administrator
// Added painting to a metafile
//
// Revision 1.4  1999-07-16 10:01:50+01  administrator
// Nearing end of phase 2
//
// Revision 1.3  1999-07-12 08:09:24+01  administrator
// Property editing added
//
// Revision 1.2  1999-07-08 13:08:46+01  administrator
// <>
//
// Revision 1.1  1999-07-07 11:10:19+01  administrator
// Initial revision
//
// Revision 1.1  1999-06-16 15:38:09+01  sm11td
// Initial revision
//
// Revision 1.2  1999-02-04 08:02:22+00  sm11td
// Plotting to canvas, scaling canvas,
//
// Revision 1.1  1999-02-01 16:07:45+00  sm11td
// Initial revision
//
// Revision 1.1  1999-01-31 13:32:57+00  sm11td
// Initial revision
//

package Debrief.GUI.Frames.AWT;


import MWC.GUI.*;
import MWC.GUI.Tools.AWT.*;
import Debrief.GUI.Frames.*;
import java.awt.*;
import java.awt.event.*;


/** an AWT implementation of our class */
public final class AWTApplication extends Application {

  /////////////////////////////////////////////////////////////
  // member variables
  ////////////////////////////////////////////////////////////

  /** the menubar for the application
   */
  private java.awt.MenuBar theMenuBar;

  /** copy of the window menu
   */
  private java.awt.Menu theWindowMenu;

  /** the frame in use
   */
  private java.awt.Frame theFrame;

  /** the 'stack' of sessions we are maintaining
   */
  private java.awt.CardLayout theCards;

  /** the Centre portion of the frame, carrying the sessions
   */
  private java.awt.Panel theMainBit;

  /** remember the old cursor, for when we switch to another one
   */
  private java.awt.Cursor _theOldCursor;

  /** keep track of the undo/redo menu items, since we need to update them
   */
//  private java.awt.MenuItem _theUndoItem;
//  private java.awt.MenuItem _theRedoItem;

  /////////////////////////////////////////////////////////////
  // constructor
  ////////////////////////////////////////////////////////////

  /** create the AWT Toolbar, set it in the parent application*/
  public AWTApplication(){
    // create the parent first
    super();

    // create the GUI bits
    initForm();

    // now setup the menus
    buildTheInterface();

    // now fill in the window menu
    refreshWindowMenu();

    // add the window menu, now that we know that the other menus
    // have been added
    final Menu helpMen = new Menu("Help");
    helpMen.add(new MenuItem("About"));
    theMenuBar.setHelpMenu(helpMen);


//IM    d3.setVisible(true);

    try
    {
      // load the coastline data in the background
      new MWC.GUI.Chart.Painters.CoastPainter();

      // IM  Thread.sleep(3000);

    }
    catch(Exception e)
    {
    }

//IM    d3.dispose();

    try
    {
      Thread.sleep(100);
    }
    catch(Exception e)
    {
    }

    theFrame.show();

  }

  /////////////////////////////////////////////////////////////
  // member functions
  ////////////////////////////////////////////////////////////

//
//  public java.awt.Component getFrame()
//  {
//    return theFrame;
//  }


  public final Session getCurrentSession(){
    // get the session currently 'on top'
    Session res = null;

    // we have to go through the panels in the main bit,
    // and return the one which is visible
    for(int i=0; i<theMainBit.getComponentCount();i++){
      final Component thisSess = theMainBit.getComponent(i);
      if(thisSess.isVisible())
      {
        res = getSessionNamed(thisSess.getName());
        break;
      }
    }

    // so, we've either got the top (visible) session, or we're returning null.
    return res;
  }


  /** add the session passed in, to include adding it to our stack
   */
  public final void newSession(Session theSession){

    // see if we are being passed a null parameter,
    // if so, we are to create a fresh session

    if(theSession == null){
      theSession = new AWTSession(this, getClipboard());
      // : implement choice of type of view to put into session
    }

    final AWTSession aws = (AWTSession) theSession;

    // pass the session to the parent
    super.newSession(theSession);

    // add the session to our 'stack'
    theMainBit.add(theSession.getName(), aws.getPanel());
    theCards.next(theMainBit);

    // make sure it's on display
    showSession(theSession);

    // and refresh the window list
    refreshWindowMenu();
  }


  /** fill in the UI details
   */
  private void initForm(){
    // create the frame
    theFrame = new Frame("Debrief 3");
    theFrame.addWindowListener(new WindowAdapter(){
      public void windowClosing(final java.awt.event.WindowEvent e){
      System.exit(0); }});

    // do the layout
    theFrame.setLayout(new BorderLayout());

    // create the components
    final MWC.GUI.Tools.AWT.AWTToolbar theToolbar =
      new MWC.GUI.Tools.AWT.AWTToolbar(AWTToolbar.HORIZONTAL);

    // pass the toolbar back to the parent
    setToolbar(theToolbar);

    // and the panel
    final Panel topSection = new Panel();
    topSection.setLayout(new BorderLayout());
    theMenuBar = new MenuBar();
    theFrame.setMenuBar(theMenuBar);

    // create the 'card' layout manager
    theCards = new CardLayout();
    theMainBit = new Panel(theCards);
    theMainBit.setSize(200,200);

    // add them
    theFrame.add("North", theToolbar);
    theFrame.add("Center", theMainBit);
    final Button testBtn = new Button("test");
    testBtn.addActionListener(new ActionListener(){
      public void actionPerformed(final ActionEvent e){
        doTest();
      }
      });
    theFrame.add("South", testBtn);

    // tidy up

    final Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();

    theFrame.setSize((int)(dim.width * 0.6),
                     (int)(dim.height * 0.6));
    final Dimension sz = theFrame.getSize();
    theFrame.setLocation((dim.width - sz.width)/2,
                         (dim.height - sz.height)/2);



    theFrame.doLayout();

  }


  /** set the title bar text to parameter
   * @param theStr to assign to title bar of frame
   */
  protected final void setTitleName(final String theStr){
    theFrame.setTitle("Debrief: " + theStr);
  }


  private void addMenu(final String theTitle)
  {
    theMenuBar.add(new Menu(theTitle));
  }

  protected final void addMenuSeparator(final String theMenu){
    final Menu mn = getMenu(theMenu);
    mn.addSeparator();
  }

  private Menu getMenu(final String theMenu){
    Menu res = null;

    // find this menu.
    boolean foundIt = false;
    final int menuCount = theMenuBar.getMenuCount();

    // step through menus
    for(int i=0; i<menuCount; i++){

      // does this name match?
      final Menu mu = theMenuBar.getMenu(i);

      if(mu.getLabel().equals(theMenu)){

        foundIt = true;
        res = mu;

        break;
      }
    }

    if(! foundIt){
      // can't find it, we'll have to add it
      addMenu(theMenu);

      // now retrieve it
      res = getMenu(theMenu);
    }

    // return our result
    return res;
  }

  protected final void addMenuItem(final String theMenu,
                             final String theLabel,
                             final Tool theTool,
                             final MenuShortcut theShortCut)
  {
    // create the new item
    final AWTMenuItem res = new AWTMenuItem(theLabel, theTool);

    if(theShortCut != null){
      res.setShortcut(theShortCut);
    }

    // get its memu
    final Menu thisMenu = getMenu(theMenu);

    // add to this item
    thisMenu.add(res);
  }


  protected final void closeSessionGUI(final Session theSession)
  {
    final AWTSession theSess = (AWTSession)theSession;

    // first remove from the card layout
    theCards.removeLayoutComponent(theSess.getPanel());

    // now remove the panel for the indicated session
    theMainBit.remove(theSess.getPanel());

    // close the pane
    theSess.getPanel().setVisible(false);

    // and refresh the window list
    refreshWindowMenu();
  }


  private void doTest(){
    theCards.next(theMainBit);

    MWC.GUI.Dialogs.DialogFactory.showMessage("title", "msg");
  }

  protected final void showSession(final Session theSession){
    theCards.show(theMainBit, theSession.getName());

    setTitleName(theSession.getName());
  }

  private void refreshWindowMenu(){
    // refresh the list of files contained on this menu

    if(theWindowMenu == null){
      theWindowMenu = new Menu("Window");
      theMenuBar.add(theWindowMenu);
    }

    // clear the menu
    theWindowMenu.removeAll();

    // now add the items we know about
    for(int i=0; i<theMainBit.getComponentCount();i++){
      final String thisItem = theMainBit.getComponent(i).getName();
      final MenuItem mn = new MenuItem(thisItem);
      mn.addActionListener(new ActionListener(){
        public void actionPerformed(final ActionEvent e){
          theCards.show(theMainBit, thisItem);
          setTitleName(thisItem);
        }
        });
      theWindowMenu.add(mn);
    }
  }

  public final void setCursor(final int theCursor)
  {
    _theOldCursor = theFrame.getCursor();
    theFrame.setCursor(new Cursor(theCursor));
  }

  public final void restoreCursor(){
    theFrame.setCursor(_theOldCursor);
  }

  public final Session createSession(){
    return new AWTSession(this, getClipboard());
  }
}


















