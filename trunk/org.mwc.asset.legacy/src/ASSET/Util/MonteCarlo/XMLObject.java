/*
 * Created by IntelliJ IDEA.
 * User: administrator
 * Date: Oct 31, 2001
 * Time: 1:47:25 PM
 * To change template for new interface use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package ASSET.Util.MonteCarlo;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public interface XMLObject
{
  /**
   * update the indicated object
   */
  public String execute(Element object, Document parentDocument);

  /**
   * return the current value of the attribute for this operation
   *
   * @param object the node we are taking the attribute from
   * @return the String representation of the attribute in this object
   */
  public String getCurValueIn(Element object);


  /**
   * return the last value used for this attribute
   */
  public String getCurValue();


  /**
   * return the name of this variable
   */
  public String getName();

  /**
   * randomise ourselves
   */
  public void randomise();

  /**
   * merge ourselves with the supplied object
   */
  public void merge(XMLObject other);


}
