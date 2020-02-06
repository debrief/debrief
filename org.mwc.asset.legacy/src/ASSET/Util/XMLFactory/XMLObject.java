
package ASSET.Util.XMLFactory;

import org.w3c.dom.Element;


public interface XMLObject
{
  /** update the indicated object
   *
   */
  public void execute(Element object);

  /** return the current value of the attribute for this operation
   * @object the node we are taking the attribute from
   * @return the String representation of the attribute in this object
   */
  public String getCurValueIn(Element object);


  /** return the last value used for this attribute
   *
   */
  public String getCurValue();


  /** return the name of this variable
   *
   */
  public String getName();

  /** clone this object
   *
   */
  public Object clone();

  /** randomise ourselves
   *
   */
  public void randomise();

  /** merge ourselves with the supplied object
   *
   */
  public void merge(XMLObject other);


}
