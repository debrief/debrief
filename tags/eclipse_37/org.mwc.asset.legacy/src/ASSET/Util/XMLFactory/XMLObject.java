/*
 * Created by IntelliJ IDEA.
 * User: administrator
 * Date: Oct 31, 2001
 * Time: 1:47:25 PM
 * To change template for new interface use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
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
