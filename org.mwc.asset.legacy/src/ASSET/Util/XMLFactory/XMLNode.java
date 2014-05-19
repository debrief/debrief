/*
 * Created by IntelliJ IDEA.
 * User: administrator
 * Date: Oct 31, 2001
 * Time: 1:23:24 PM
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package ASSET.Util.XMLFactory;


import java.util.List;
import java.util.Vector;
import java.util.Iterator;

import org.w3c.dom.Element;

public class XMLNode implements XMLObject
{
  private XMLChoice _myOperation = null;

  public XMLNode(final Element element)
  {
    // read ourselves in from this node

    // get the choice
    final Element choice = (Element) element.getElementsByTagName("Choice").item(0);
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

    final List<Element> list = _myOperation.getList();

    // perform deep copy
    final Vector<Element> duplicate = new Vector<Element>(0,1);
    final Iterator<Element> iter = list.iterator();
    while (iter.hasNext())
    {
      final Object o = iter.next();
      if(o instanceof Element)
      {
        final Element el = (Element)o;
        final Element dup = (Element) el.cloneNode(true);
        el.removeChild(dup);
        duplicate.add(dup);
      }
    }

    // pass though, double-checking that we've removed any duplicates

    // set it's parent
    // remove any existing content
    while(object.hasChildNodes())
    	object.removeChild(object.getFirstChild());

    // set the data
    for (Iterator<Element> iterator = duplicate.iterator(); iterator.hasNext();)
		{
			Element element = iterator.next();
			object.appendChild(element);
		}

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
