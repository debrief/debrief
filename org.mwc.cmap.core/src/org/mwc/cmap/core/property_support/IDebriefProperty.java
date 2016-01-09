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
package org.mwc.cmap.core.property_support;

import java.lang.annotation.Annotation;

import MWC.GUI.Editable;

public interface IDebriefProperty
{

  public void setValue(Object text);

  Object getValue();

  Object getRawValue();

  public String getName();

  public String getDisplayName();

  public EditorHelper getHelper();

  public Editable getEditable();

  public Annotation[] getAnnotationsForSetter();

}
