/*
 * Created by IntelliJ IDEA.
 * User: administrator
 * Date: Oct 31, 2001
 * Time: 1:22:06 PM
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package ASSET.Util.MonteCarlo;


import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.Iterator;

/**
 * list of items in a particular file which may be changed
 */

public class XMLVarianceList
{
  /**
   * the list of variances we manage
   */
  private java.util.List<XMLVariance> _myVariables = null;
  private static final String VARIANCE = "Variance";

  /***************************************************************
   *  constructor
   ***************************************************************/
  /**
   * constructor, received a stream containing the list
   * of variances we are going to manage
   */
  public XMLVarianceList(final Element myElement)
  {
    this();

    loadFrom(myElement);
  }


  public XMLVarianceList()
  {
    _myVariables = new java.util.Vector<XMLVariance>();
  }

  /**
   * ************************************************************
   * member methods
   * *************************************************************
   */

  public Object clone()
  {
    System.err.println("SHOULD USE PROPER CLONE METHOD");
    // todo: IMPLEMENT PROPER CLONE METHYOD
    return this.clone();
  }


  public final void loadFrom(final Element myElement)
  {

    // now try to build up the list of vars
    // build up our list of variances from this document
    final NodeList lis = myElement.getElementsByTagName(VARIANCE);

    final int len = lis.getLength();

    for (int i = 0; i < len; i++)
    {
      final Element o = (Element) lis.item(i);

      // create this variance
      final XMLVariance xv = new XMLVariance(o);

      // and store it
      _myVariables.add(xv);

    }
  }


  /**
   * add this variance to our list
   */
  public final void add(final XMLVariance variance)
  {
    _myVariables.add(variance);
  }

  /**
   * get the list of variances we contain
   */
  public final Iterator<XMLVariance> getIterator()
  {
    return _myVariables.iterator();
  }

  /**
   * get the size of our list
   */
  public final int size()
  {
    return _myVariables.size();
  }

  public final XMLVariance itemAt(final int i)
  {
    return (XMLVariance) _myVariables.get(i);
  }

  /**
   * apply our list of operations to this object
   *
   * @return a hashing string representing the permutations we've used
   */
  public final String apply(final String parentXPath, final Document target) throws XMLVariance.IllegalExpressionException,
      XMLVariance.MatchingException
  {
    String hashStr = "";

    for (int i = 0; i < _myVariables.size(); i++)
    {
      // get this variance
      final XMLVariance variance = (XMLVariance) _myVariables.get(i);

      // do this permutation
      final String thisPerm = variance.permutate(parentXPath, target);

      // insert a field separator if we need to
      if(hashStr.length() > 0)
        hashStr += " ";

      // collate the variance details
      hashStr += variance.getName() +  ":" + thisPerm;
    }

    return hashStr;
  }

}
