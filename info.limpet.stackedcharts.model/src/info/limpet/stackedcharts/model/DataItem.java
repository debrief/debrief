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

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Data Item</b></em>'. <!--
 * end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 * <li>{@link info.limpet.stackedcharts.model.DataItem#getIndependentVal <em>Independent
 * Val</em>}</li>
 * <li>{@link info.limpet.stackedcharts.model.DataItem#getDependentVal <em>Dependent Val</em>}</li>
 * </ul>
 *
 * @see info.limpet.stackedcharts.model.StackedchartsPackage#getDataItem()
 * @model
 * @generated
 */
public interface DataItem extends EObject
{
  /**
   * Returns the value of the '<em><b>Independent Val</b></em>' attribute. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Independent Val</em>' attribute isn't clear, there really should be
   * more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @return the value of the '<em>Independent Val</em>' attribute.
   * @see #setIndependentVal(double)
   * @see info.limpet.stackedcharts.model.StackedchartsPackage#getDataItem_IndependentVal()
   * @model
   * @generated
   */
  double getIndependentVal();

  /**
   * Sets the value of the '{@link info.limpet.stackedcharts.model.DataItem#getIndependentVal
   * <em>Independent Val</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @param value
   *          the new value of the '<em>Independent Val</em>' attribute.
   * @see #getIndependentVal()
   * @generated
   */
  void setIndependentVal(double value);

  /**
   * Returns the value of the '<em><b>Dependent Val</b></em>' attribute. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Dependent Val</em>' attribute isn't clear, there really should be
   * more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @return the value of the '<em>Dependent Val</em>' attribute.
   * @see #setDependentVal(double)
   * @see info.limpet.stackedcharts.model.StackedchartsPackage#getDataItem_DependentVal()
   * @model
   * @generated
   */
  double getDependentVal();

  /**
   * Sets the value of the '{@link info.limpet.stackedcharts.model.DataItem#getDependentVal
   * <em>Dependent Val</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @param value
   *          the new value of the '<em>Dependent Val</em>' attribute.
   * @see #getDependentVal()
   * @generated
   */
  void setDependentVal(double value);

} // DataItem
