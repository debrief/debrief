/**
 * Created by IntelliJ IDEA.
 * User: Ian.Mayo
 * Date: Nov 25, 2002
 * Time: 10:50:48 AM
 * To change this template use Options | File Templates.
 */
package MWC.GUI;

import java.util.Enumeration;

public interface PlottablesType {

  /** get the elements in this list of plottables
   *
   * @return enumeration of elements
   */
  Enumeration<Editable> elements();

  /** how big is the list?
   *
   * @return the length of the list
   */
  int size();

  /** paint this list of plottables
   *
   * @param dest the graphics destination
   */
  void paint(CanvasType dest);

  /** what area do we cover?
   *
   * @return the area, or null
   */
  MWC.GenericData.WorldArea getBounds();

  /** the name of this set of plottables
   *
   * @return my name
   */
  String getName();
}
