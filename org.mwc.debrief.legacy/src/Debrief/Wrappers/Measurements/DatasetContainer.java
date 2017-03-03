package Debrief.Wrappers.Measurements;

/** API for classes that are capable of storing supplementatal datasets
 * 
 * @author ian
 *
 */
public interface DatasetContainer
{
  /** get the collection of measurements
   * 
   * @return
   */
  DataFolder getMeasurements();
}
