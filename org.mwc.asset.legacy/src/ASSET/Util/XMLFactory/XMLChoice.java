/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)

 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package ASSET.Util.XMLFactory;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import ASSET.Util.RandomGenerator;

public class XMLChoice implements XMLOperation
{
  /***************************************************************
   *  member variables
   ***************************************************************/

  /** the list of values to choose from
   *
   */
  private HashMap<List<Element>, String> _myList;

  /** the element we will use for this permutation
   *
   */
  private List<Element> _currentVal;



  /***************************************************************
   *  constructor
   ***************************************************************/
  public XMLChoice(final Element element)
  {
    this();

    // you know, get the stuff

    // have a fish around inside it

    final NodeList vars = element.getChildNodes();

    for(int i=0;i<vars.getLength();i++)
    {
      final Element thisE = (Element) vars.item(i);
      final String thisName = thisE.getAttribute("name");
      final NodeList children = thisE.getChildNodes();

      final List<Element> duplicates = new Vector<Element>(0,1);

      if(children.getLength() > 0)
      {
        // keep track of the elements to be detached
        Vector<Element> toBeDetached = new Vector<Element>(0,1);

        // in this first pass, take copies of the children
        for(int j=0;j<children.getLength();j++)
        {
          final Element el = (Element) children.item(j);
          duplicates.add((Element)el.cloneNode(true));
          toBeDetached.add(el);
        }

        // now pass through again and detach all of the children
        Iterator<Element> iter2 = toBeDetached.iterator();
        while (iter2.hasNext())
        {
          Element el = iter2.next();
          thisE.removeChild(el);
        }

        _myList.put(duplicates, thisName);
      }
    }

    // stick in a random variable to start us off
    newPermutation();
  }

  private XMLChoice(XMLChoice other)
  {
    this();
  }

  private XMLChoice()
  {
    _myList = new HashMap<List<Element>, String>();
  }

  /***************************************************************
   *  member methods
   ***************************************************************/
  /** produce a new value for this operation
   *
   */
  public void newPermutation()
  {
    final int index = (int)(RandomGenerator.nextRandom() * _myList.size());

    final Iterator<List<Element>> it = _myList.keySet().iterator();

    for(int i=0;i<=index;i++)
    {
      _currentVal = it.next();
    }

    // done

  }

  /** return the human legible current value of this permutation
   *
   */
  public String getSimpleValue()
  {
    return _myList.get(_currentVal) ;
  }

  /** return the current value of this permutation
   *
   */
  public String getValue()
  {
    String res = "";
    final List<Element> ls = _currentVal;
    final Iterator<Element> it = ls.iterator();
    while (it.hasNext())
    {
      final Object o = it.next();
      res += o.toString();
    }

    return res;
  }


  /** return our object as a list
   *
   */
  public List<Element> getList()
  {
    return _currentVal;
  }


  /** clone this object
   *
   */
  public Object clone()
  {
    final XMLChoice res = new XMLChoice(this);
    return res;
  }


  /** merge ourselves with the supplied operation
   *
   */
  public void merge(XMLOperation other)
  {
  }


}
