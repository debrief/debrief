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

public interface XMLOperation
{
  /** produce a new value for this operation
   *
   */
  public void newPermutation();

  /** return the current value of this permutation
   *
   */
  public String getValue();

  /** return the human legible current value of this permutation
   *
   */
  public String getSimpleValue();

  /** clone operation, to produce an identical copy
   *
   */
  public Object clone();

  /** merge ourselves with the supplied operation
   *
   */
  public void merge(XMLOperation other);


}
