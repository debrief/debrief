/**
 */
package info.limpet.stackedcharts.model;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.emf.common.util.Enumerator;

/**
 * <!-- begin-user-doc --> A representation of the literals of the enumeration '<em><b>Axis
 * Scale</b></em>', and utility methods for working with them. <!-- end-user-doc --> <!--
 * begin-model-doc --> List of styles of axis <!-- end-model-doc -->
 * 
 * @see info.limpet.stackedcharts.model.StackedchartsPackage#getAxisScale()
 * @model
 * 
 */
public enum AxisScale implements Enumerator
{
  /**
   * The '<em><b>Linear</b></em>' literal object. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see #LINEAR_VALUE
   * 
   * @ordered
   */
  LINEAR(0, "Linear", "Linear"),

  /**
   * The '<em><b>Log</b></em>' literal object. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see #LOG_VALUE
   * 
   * @ordered
   */
  LOG(0, "Log", "Log");

  /**
   * The '<em><b>Linear</b></em>' literal value. <!-- begin-user-doc -->
   * <p>
   * If the meaning of '<em><b>Linear</b></em>' literal object isn't clear, there really should be
   * more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @see #LINEAR
   * @model name="Linear"
   * 
   * @ordered
   */
  public static final int LINEAR_VALUE = 0;

  /**
   * The '<em><b>Log</b></em>' literal value. <!-- begin-user-doc -->
   * <p>
   * If the meaning of '<em><b>Log</b></em>' literal object isn't clear, there really should be more
   * of a description here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @see #LOG
   * @model name="Log"
   * 
   * @ordered
   */
  public static final int LOG_VALUE = 0;

  /**
   * An array of all the '<em><b>Axis Scale</b></em>' enumerators. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * 
   */
  private static final AxisScale[] VALUES_ARRAY = new AxisScale[]
  {LINEAR, LOG,};

  /**
   * A public read-only list of all the '<em><b>Axis Scale</b></em>' enumerators. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * 
   */
  public static final List<AxisScale> VALUES = Collections.unmodifiableList(
      Arrays.asList(VALUES_ARRAY));

  /**
   * Returns the '<em><b>Axis Scale</b></em>' literal with the specified literal value. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @param literal
   *          the literal.
   * @return the matching enumerator or <code>null</code>.
   * 
   */
  public static AxisScale get(String literal)
  {
    for (int i = 0; i < VALUES_ARRAY.length; ++i)
    {
      AxisScale result = VALUES_ARRAY[i];
      if (result.toString().equals(literal))
      {
        return result;
      }
    }
    return null;
  }

  /**
   * Returns the '<em><b>Axis Scale</b></em>' literal with the specified name. <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * 
   * @param name
   *          the name.
   * @return the matching enumerator or <code>null</code>.
   * 
   */
  public static AxisScale getByName(String name)
  {
    for (int i = 0; i < VALUES_ARRAY.length; ++i)
    {
      AxisScale result = VALUES_ARRAY[i];
      if (result.getName().equals(name))
      {
        return result;
      }
    }
    return null;
  }

  /**
   * Returns the '<em><b>Axis Scale</b></em>' literal with the specified integer value. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @param value
   *          the integer value.
   * @return the matching enumerator or <code>null</code>.
   * 
   */
  public static AxisScale get(int value)
  {
    switch (value)
    {
      case LINEAR_VALUE:
        return LINEAR;
    }
    return null;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * 
   */
  private final int value;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * 
   */
  private final String name;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * 
   */
  private final String literal;

  /**
   * Only this class can construct instances. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * 
   */
  private AxisScale(int value, String name, String literal)
  {
    this.value = value;
    this.name = name;
    this.literal = literal;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * 
   */
  public int getValue()
  {
    return value;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * 
   */
  public String getName()
  {
    return name;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * 
   */
  public String getLiteral()
  {
    return literal;
  }

  /**
   * Returns the literal value of the enumerator, which is its string representation. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * 
   */
  @Override
  public String toString()
  {
    return literal;
  }

} // AxisScale
