
package ASSET.Util.XMLFactory;

public interface XMLOperation
{
  /** produce a new value for this operation
   *
   */
  public void newPermutation();

  /** return the current value of this permutation
   *
   */
  public String getValue();

  /** return the human legible current value of this permutation
   *
   */
  public String getSimpleValue();

  /** clone operation, to produce an identical copy
   *
   */
  public Object clone();

  /** merge ourselves with the supplied operation
   *
   */
  public void merge(XMLOperation other);


}
