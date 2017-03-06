package Debrief.Wrappers.Extensions;

import java.util.List;

import MWC.GUI.Editable;

/** API for Debrief extensions that are able to put content
 * into the Outline view
 * 
 * @author ian
 *
 */
public interface ExtensionContentProvider
{

  /** produce a (possibly empty) list of UI elements for this item
   * 
   * @param item the extension object
   * @return UI elements to represent the object
   */
  List<Editable> itemsFor(Object subject);
}