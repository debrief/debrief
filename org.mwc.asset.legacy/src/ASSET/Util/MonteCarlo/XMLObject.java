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
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
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
