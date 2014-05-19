/*
 * Created by IntelliJ IDEA.
 * User: administrator
 * Date: Oct 31, 2001
 * Time: 1:23:24 PM
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package ASSET.Util.MonteCarlo;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public final class XMLNode implements XMLObject
{
  private XMLSnippets _myOperation = null;

  public XMLNode(final Element element)
  {
    // read ourselves in from this node

    // get the choice
    final NodeList nodes = element.getElementsByTagName("XMLChoice");
    if (nodes.getLength() > 0)
    {
      final Element choice = (Element) nodes.item(0);
      _myOperation = new XMLSnippets(choice);
    }
  }

  /**
   * return the operation we perform
   */
  public final XMLSnippets getOperation()
  {
    return _myOperation;
  }

  /**
   * perform our update to the supplied element
   */
  public final String execute(final Element currentInstance, final Document parentDocument)
  {
    // ok, here we go..

    // we're going to replace the object with one of our permutations

    // first get the parent
    final Element parent = (Element) currentInstance.getParentNode();

    // now get a new permutation
    Element newPerm = _myOperation.getInstance();

    // created a cloned instance with the correct parent document
    newPerm = (Element) parentDocument.importNode(newPerm, true);

    // insert this before our current element
    parent.insertBefore(newPerm, currentInstance);

    // and remove the existing instance
    parent.removeChild(currentInstance);

    // convert the new value to a string, to be used in the hashing value
    String asString = ScenarioGenerator.writeToString(newPerm);

    // and return the value we used
    return asString;
  }


  /**
   * randomise ourselves
   */
  public final void randomise()
  {
//    _myOperation.newPermutation();
  }

  /**
   * return the name of this variable
   */
  public final String getName()
  {
    return "un-named";
  }

  public final String getCurValueIn(final Element object)
  {
    return "Empty";
  }

  /**
   * return the last value used for this attribute
   */
  public final String getCurValue()
  {
    return "Empty";
  }

  /**
   * merge ourselves with the supplied object
   */
  public final void merge(final XMLObject other)
  {
  }


}
