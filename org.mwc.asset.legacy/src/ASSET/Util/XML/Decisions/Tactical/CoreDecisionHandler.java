/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package ASSET.Util.XML.Decisions.Tactical;

import ASSET.Models.Decision.CoreDecision;

/**
 * Created by IntelliJ IDEA.
 * User: Ian.Mayo
 * Date: 17-Aug-2004
 * Time: 15:23:50
 * To change this template use File | Settings | File Templates.
 */
public class CoreDecisionHandler extends MWC.Utilities.ReaderWriter.XML.MWCXMLReader
{
  protected final static String IS_ACTIVE = "IsActive";
  protected final static String NAME = "Name";

  String _myName;
  boolean _isActive = true;

  //////////////////////////////////////////////////
  // constructor = get going
  //////////////////////////////////////////////////
  public CoreDecisionHandler(final String myType)
  {
    super(myType);

    addAttributeHandler(new HandleAttribute(NAME){
      public void setValue(String name, String value)
      {
        _myName = value;
      }
    });
    addAttributeHandler(new HandleBooleanAttribute(IS_ACTIVE){
      public void setValue(String name, boolean value)
      {
        _isActive = value;
      }
    });
  }

  /** set our attributes within this decision object
   *
   * @param decision the decision object to update
   */
  protected void setAttributes(CoreDecision decision)
  {
    // only set the name if we have one.
    if(_myName != null)
      decision.setName(_myName);
    
    decision.setActive(_isActive);

    // and clear
    _myName = null;
    _isActive = true;
  }


  /** export our attributes within this object
   *
   * @param toExport the decision object we are exporting
   * @param element the element representing this object
   * @param doc the parent document
   */
  static public void exportThis(final CoreDecision toExport, final org.w3c.dom.Element element, final org.w3c.dom.Document doc)
  {
    element.setAttribute(NAME, toExport.getName());
    element.setAttribute(IS_ACTIVE, writeThis(toExport.isActive()));
  }


}
