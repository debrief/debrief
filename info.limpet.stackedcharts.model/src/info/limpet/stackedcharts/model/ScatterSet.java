/*******************************************************************************
 * Debrief - the Open Source Maritime Analysis Application
 * http://debrief.info
 *  
 * (C) 2000-2020, Deep Blue C Technology Ltd
 *  
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html)
 *  
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 *******************************************************************************/
/**
 */
package info.limpet.stackedcharts.model;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Scatter Set</b></em>'. <!--
 * end-user-doc -->
 *
 * <!-- begin-model-doc --> Collection of values, to be presented as markers along the axis.
 * Clustering should present densely packed values as areas/zones
 * 
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 * <li>{@link info.limpet.stackedcharts.model.ScatterSet#getDatums <em>Datums</em>}</li>
 * </ul>
 *
 * @see info.limpet.stackedcharts.model.StackedchartsPackage#getScatterSet()
 * @model
 * @generated
 */
public interface ScatterSet extends AbstractAnnotation
{
  /**
   * Returns the value of the '<em><b>Datums</b></em>' containment reference list. The list contents
   * are of type {@link info.limpet.stackedcharts.model.Datum}. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Datums</em>' containment reference list isn't clear, there really
   * should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @return the value of the '<em>Datums</em>' containment reference list.
   * @see info.limpet.stackedcharts.model.StackedchartsPackage#getScatterSet_Datums()
   * @model containment="true"
   * @generated
   */
  EList<Datum> getDatums();

} // ScatterSet
