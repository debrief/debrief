/**
 */
package info.limpet.stackedcharts.model;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.emf.common.util.Enumerator;

/**
 * <!-- begin-user-doc --> A representation of the literals of the enumeration '<em><b>Axis
 * Direction</b></em>', and utility methods for working with them. <!-- end-user-doc --> <!--
 * begin-model-doc --> Whether the axis is shown in ascending or descending numerical order <!--
 * end-model-doc -->
 * 
 * @see info.limpet.stackedcharts.model.StackedchartsPackage#getAxisDirection()
 * @model
 * @generated
 */
public enum AxisDirection implements Enumerator
{
  /**
   * The '<em><b>Ascending</b></em>' literal object. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see #ASCENDING_VALUE
   * @ordered
   */
  ASCENDING(0, "Ascending", "Ascending"),

  /**
   * The '<em><b>Descending</b></em>' literal object. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see #DESCENDING_VALUE
   * @ordered
   */
  DESCENDING(0, "Descending", "Descending");

  /**
   * The '<em><b>Ascending</b></em>' literal value. <!-- begin-user-doc -->
   * <p>
   * If the meaning of '<em><b>Ascending</b></em>' literal object isn't clear, there really should
   * be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @see #ASCENDING
   * @model name="Ascending"
   * @ordered
   */
  public static final int ASCENDING_VALUE = 0;

  /**
   * The '<em><b>Descending</b></em>' literal value. <!-- begin-user-doc -->
   * <p>
   * If the meaning of '<em><b>Descending</b></em>' literal object isn't clear, there really should
   * be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @see #DESCENDING
   * @ordered
   */
  public static final int DESCENDING_VALUE = 0;

  /**
   * An array of all the '<em><b>Axis Direction</b></em>' enumerators. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   */
  private static final AxisDirection[] VALUES_ARRAY = new AxisDirection[]
  {ASCENDING, DESCENDING,};

  /**
   * A public read-only list of all the '<em><b>Axis Direction</b></em>' enumerators. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   */
  public static final List<AxisDirection> VALUES = Collections.unmodifiableList(
      Arrays.asList(VALUES_ARRAY));

  /**
   * Returns the '<em><b>Axis Direction</b></em>' literal with the specified literal value. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @param literal
   *          the literal.
   * @return the matching enumerator or <code>null</code>.
   */
  public static AxisDirection get(String literal)
  {
    for (int i = 0; i < VALUES_ARRAY.length; ++i)
    {
      AxisDirection result = VALUES_ARRAY[i];
      if (result.toString().equals(literal))
      {
        return result;
      }
    }
    return null;
  }

  /**
   * Returns the '<em><b>Axis Direction</b></em>' literal with the specified name. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @param name
   *          the name.
   * @return the matching enumerator or <code>null</code>.
   */
  public static AxisDirection getByName(String name)
  {
    for (int i = 0; i < VALUES_ARRAY.length; ++i)
    {
      AxisDirection result = VALUES_ARRAY[i];
      if (result.getName().equals(name))
      {
        return result;
      }
    }
    return null;
  }

  /**
   * Returns the '<em><b>Axis Direction</b></em>' literal with the specified integer value. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @param value
   *          the integer value.
   * @return the matching enumerator or <code>null</code>.
   */
  public static AxisDirection get(int value)
  {
    switch (value)
    {
    case ASCENDING_VALUE:
      return ASCENDING;
    }
    return null;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   */
  private final int value;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   */
  private final String name;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   */
  private final String literal;

  /**
   * Only this class can construct instances. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   */
  private AxisDirection(int value, String name, String literal)
  {
    this.value = value;
    this.name = name;
    this.literal = literal;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   */
  public int getValue()
  {
    return value;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   */
  public String getName()
  {
    return name;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
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
   */
  @Override
  public String toString()
  {
    return literal;
  }

} // AxisDirection
