/*
 * Created by IntelliJ IDEA.
 * User: administrator
 * Date: Oct 31, 2001
 * Time: 1:23:24 PM
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package ASSET.Util.XMLFactory;

import org.jdom.Element;

import java.util.List;
import java.util.Vector;
import java.util.Iterator;

public class XMLNode implements XMLObject
{
  private XMLChoice _myOperation = null;

  public XMLNode(final org.jdom.Element element)
  {
    // read ourselves in from this node

    // get the choice
    final Element choice = element.getChild("Choice");
    _myOperation = new XMLChoice(choice);
  }

  /** default constructor, used in cloning
   *
   */
  private XMLNode(XMLNode other)
  {

  }

  /** create a clone of this object
   *
   */
  public Object clone()
  {
    final XMLNode res = new XMLNode(this);
    return res;
  }

  /** perform our update to the supplied element
   *
   */
  public void execute(final Element object)
  {

    final List list = _myOperation.getList();

    // perform deep copy
    final Vector duplicate = new Vector(0,1);
    final Iterator iter = list.iterator();
    while (iter.hasNext())
    {
      final Object o = (Object) iter.next();
      if(o instanceof Element)
      {
        final Element el = (Element)o;
        final Element dup = (Element) el.clone();
        dup.detach();
        duplicate.add(dup);
      }
    }

    // pass though, double-checking that we've removed any duplicates

    // set it's parent
    // remove any existing content
    object.removeChildren();

    // set the data
   object.setChildren(duplicate);

  }


  /** randomise ourselves
   *
   */
  public void randomise()
  {
    _myOperation.newPermutation();
  }

  /** return the name of this variable
   *
   */
  public String getName()
  {
    return "un-named";
  }

  public String getCurValueIn(Element object)
  {
    return "Empty";
  }

  /** return the last value used for this attribute
   *
   */
  public String getCurValue()
  {
    return "Empty";
  }

  /** merge ourselves with the supplied object
   *
   */
  public void merge(XMLObject other)
  {
  }


}
