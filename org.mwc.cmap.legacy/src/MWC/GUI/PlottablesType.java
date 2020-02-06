
package MWC.GUI;

public interface PlottablesType extends CanEnumerate
{
 
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
