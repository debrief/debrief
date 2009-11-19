/*
 * Created by IntelliJ IDEA.
 * User: administrator
 * Date: Oct 31, 2001
 * Time: 2:11:33 PM
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package ASSET.Util.MonteCarlo;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.Vector;
import java.util.Iterator;

public final class XMLChoice implements XMLOperation
{
  /***************************************************************
   *  member variables
   ***************************************************************/

  /** the list of values to choose from
   *
   */
  private final Vector<String> _myList;

  /** the element we will use for this permutation
   *
   */
  private String _currentVal;

  /** the name given to this set of choices
   *
   */
  private String _myName;

  /** the title of the name field
   *
   */
  private final static String NAME = "name";

  /** the tag for a single value
   *
   */
  private static final String VALUE_TYPE = "Value";

  /** the attribute within which a value is stored
   *
   */
  private static final String VALUE = "value";


  /***************************************************************
   *  constructor
   ***************************************************************/
  public XMLChoice(final Element element)
  {
    this();

    // you know, get the stuff
    _myName = element.getAttribute(NAME);

    // have a fish around inside it

    final NodeList vars = element.getElementsByTagName(VALUE_TYPE);

    final int len = vars.getLength();
    for(int i=0;i<len;i++)
    {
      final Element thisE = (Element) vars.item(i);
      final String thisName = thisE.getAttribute(VALUE);
      _myList.add(thisName);
    }

    // stick in a random variable to start us off
    newPermutation();
  }

  private XMLChoice(final XMLChoice other)
  {
    this();

    // pass through and copy components
    final Iterator<String> iter = other._myList.iterator();
    while(iter.hasNext())
    {
      final String thisVal = (String)iter.next();
      _myList.add(thisVal);
    }
  }

  private XMLChoice()
  {
    _myList = new Vector<String>();
  }

  /***************************************************************
   *  member methods
   ***************************************************************/
  /** produce a new value for this operation
   *
   */
  public final void newPermutation()
  {
    final int index = (int)(ASSET.Util.RandomGenerator.nextRandom() * _myList.size());

    _currentVal = (String)_myList.get(index);

  }

  /** return the current value of this permutation
   *
   */
  public final String getValue()
  {
    return _currentVal;
  }

  /** clone this object
   *
   */
  public final Object clone()
  {
    final XMLChoice res = new XMLChoice(this);
    return res;
  }

  public final String getName()
  {
    return _myName;
  }

  /** merge ourselves with the supplied operation
   *
   */
  public final void merge(final XMLOperation other)
  {
  }

  public static void main(final String[] args)
  {
    final XMLChoice ch = new XMLChoice();

    ch._myList.add("a1");
    ch._myList.add("a2");
    ch._myList.add("a3");
    ch._myList.add("a4");

    ch.newPermutation();

    final String val = ch.getValue();
    System.out.println(val);


  }

  public final int size()
  {
    return _myList.size();
  }

  public final String get(final int index)
  {
    return (String)_myList.get(index);
  }


}
