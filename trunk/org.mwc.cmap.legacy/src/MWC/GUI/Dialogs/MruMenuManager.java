package MWC.GUI.Dialogs;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

/**
Works with a JMenu to manipulate most recently used items.  This works to
achieve the effect found in many popular software products:  keeping track
of the items that a user has chosen most recently for easy retrieval.

When constructed with an ApplicationProperties object, MruMenuManager will
load and store these mru items for persistence between client sessions.

@see MWC.GUI.Dialogs.ApplicationProperties

@author Dave Lamy (daveandjeri@stubbydog.com)
*/
public class MruMenuManager implements ActionListener {
  
  private int startIndex;
  private JMenu mruMenu;
  private int mruSize;
  private List<JMenuItem> mruItems;
  private List<ActionListener> mruListeners;
  private ApplicationProperties properties;
  private String propertyPrefix;

  /**
  Constructs a new MruMenuManager with no persistence capability
  */
  public MruMenuManager(JMenu mruMenu, int startMenuIndex, int mruSize) {
    this(mruMenu, startMenuIndex, mruSize, null, null);
  } // constructor

  /**
  Constructs a new MruMenuManager with the specified parameters.
  This constructor will go ahead and load the menu items present
  in the ApplicationProperties object.  If no ApplicationProperties
  object is passed in, the load operation will be skipped.

  @param mruMenu - the JMenu to manipulate with MRU items
  @param startMenuIndex - the root index of the JMenu where this object
          should start adding and removing MRU items
  @param mruSize - the maximum number of MRU item entries to track.  If
          this size is reached and a new MRU item is added, the last MRU
          item in the list will be removed to make room for the new item.
  @param appProperties - The ApplicationProperties instance that works as
          the persistence store of MRU items.
  @param propertyPrefix - The String prefix of MRU items in the
          ApplicationProperties object.
          <p>
          Example:  if there are 2 MRU items in the ApplicationProperties
                    instance:
                    <p>
                    <code>
                    com.simscomputing.testbed.mru.0<br>
                    com.simscomputing.testbed.mru.1
                    </code>
                    <p>
                    Then the propertyPrefix would be
                    "<code>com.simscomputing.testbed.mru</code>".
  */
  public MruMenuManager(JMenu mruMenu, int startMenuIndex, int mruSize,
                        ApplicationProperties appProperties, String propertyPrefix) {
    this.mruMenu = mruMenu;
    startIndex = startMenuIndex;
    this.mruSize = mruSize;
    mruItems = new ArrayList<JMenuItem>(mruSize + 1);
    this.properties = appProperties;
    this.propertyPrefix = propertyPrefix;
    mruListeners = new ArrayList<ActionListener>();
    loadMruItems();

  }

  /**
  Adds a new mru item to the mru list.  If the item to add
  is already being displayed, it will not be repeated.  If, by
  adding this item, the mru size defined in the constructor will
  be exceeded, this method will remove the last item in the list
  to make room.  The new item will be added to the top of the list.
  The menu refresh will happen immediately.

  @param itemToAdd - the text of the JMenuItem to add.
  */
  public void addMruItem(String itemToAdd) {
    addMruItem(itemToAdd, true);
  }

  
  /**
  Adds a new mru item to the mru list.  If the item to add
  is already being displayed, it will not be repeated.  If, by
  adding this item, the mru size defined in the constructor will
  be exceeded, this method will remove the last item in the list
  to make room.  The new item will be added to the top of the list.
  The JMenu will only be refreshed if <code>refreshImmediately</code>
  has been set to <code>true</code>.

  @param itemToAdd - the text of the JMenuItem to add.
  @param refreshImmediately - whether or not to refesh the JMenu as
                              part of this method.
  */
  public void addMruItem(String itemToAdd, boolean refreshImmediately) {
    Iterator<JMenuItem> it = mruItems.iterator();
    while (it.hasNext()) {
      JMenuItem menuItem = (JMenuItem)it.next();
      if (menuItem.getText().equals(itemToAdd)) {
        return;
      } // if
    } // while
    int oldSize = mruItems.size();
    JMenuItem newItem = new JMenuItem(itemToAdd);
    newItem.addActionListener(this);
    if (mruItems.size() == 0) {
      mruItems.add(newItem);
    } // if
    else {
      for (int i = mruItems.size() - 1; i >= 0; i--) {
        if (i + 1 >= mruSize) continue;
        if (i + 1 == mruItems.size()) {
          mruItems.add(mruItems.get(i));
        } // if
        else {
          mruItems.set(i + 1, mruItems.get(i));
        } // else
      } // for
      mruItems.set(0, newItem);
    } // else
    if (refreshImmediately) {
      resetMenu(oldSize);
    } // if
  }

  /**
  Adds an ActionListener to a private list.  When ActionEvents
  are received from mru JMenuItems, all ActionListeners in
  this list will be notified.
  */
  public void addActionListener(ActionListener listener) {
    mruListeners.add(listener);
  }

  /**
  Removes the specified ActionListener from the list.
  */
  public void removeActionListener(ActionListener listener) {
    mruListeners.remove(listener);
  }

  // Resets the JMenu to whatever is now in the mruItems list.
  private void resetMenu(int oldSize) {
    // first, remove all items from the JMenu
    for (int i=startIndex; i < (startIndex + oldSize); i++) {
      mruMenu.remove(startIndex);
    } // for
    // Now add all current items back in
    for (int i=0; i < mruItems.size(); i++) {
      JMenuItem item = (JMenuItem)mruItems.get(i);
      mruMenu.insert(item, startIndex + i);
    } // for
  } // resetMenu

  /**
  Stores the mru items in the ApplicationProperties object.  Note
  that this will not physically persist the items. The ApplicationProperties
  object will physically persist the data to disk when the
  <code>storeProperties</code> method is called on it.

  If no ApplicationProperties object was set during construction
  this method will not do anything.
  */
  public void storeMruItems() {
    if (properties == null) return;
    // First get rid of all current mru items
    Map<String, String> currentItemMap = properties.getPropertiesLike(propertyPrefix);
    Iterator<String> keyIt = currentItemMap.keySet().iterator();
    while (keyIt.hasNext()) {
      properties.removeProperty((String)keyIt.next());
    } // while

    // Now add all items in
    for (int i=0; i < mruItems.size(); i++) {
      JMenuItem menuItem = (JMenuItem)mruItems.get(i);
      String item = menuItem.getText();
      String propertyName = propertyPrefix + "." + new Integer(i).toString();
      properties.setProperty(propertyName, item);
    } // for
  } // storeMruItems

  /**
  Loads mru items from the ApplicationProperties object.  This
  method is called during construction of the MruMenuManager object.
  If no ApplicationProperties object was passed in during construction,
  this method will not do anything.
  */
  public void loadMruItems() {
    if (properties == null) return;

    /* try to pass through the props loading them, until one fails
     */
    for(int i=mruSize;i>= 0;i--)
    {
      String thisVal = properties.getProperty(propertyPrefix + "." + i);
      if(thisVal != null)
      {
        // and add it
        addMruItem(thisVal, false);
      }
    }
    resetMenu(0);
  } // loadMruItems

  /**
  Implementation of the ActionListener interface.  The MruMenuManager
  will catch events from all mru items it creates and subsequently
  broadcast them to all ActionListeners attached to it.
  */
  public void actionPerformed(ActionEvent e) {
    Iterator<ActionListener> listenerIt = mruListeners.iterator();
    while (listenerIt.hasNext()) {
      ActionListener listener = (ActionListener)listenerIt.next();
      listener.actionPerformed(e);
    } // while
  } // mruMenuItemSelected








}
