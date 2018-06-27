/**
 */
package info.limpet.stackedcharts.model;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.emf.common.util.Enumerator;

/**
 * <!-- begin-user-doc --> A representation of the literals of the enumeration '<em><b>Line
 * Type</b></em>', and utility methods for working with them. <!-- end-user-doc -->
 * 
 * @see info.limpet.stackedcharts.model.StackedchartsPackage#getLineType()
 * @model
 * @generated
 */
public enum LineType implements Enumerator
{
  /**
   * The '<em><b>None</b></em>' literal object. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see #NONE_VALUE
   * @generated
   * @ordered
   */
  NONE(1, "None", "None"),

  /**
   * The '<em><b>Solid</b></em>' literal object. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see #SOLID_VALUE
   * @generated
   * @ordered
   */
  SOLID(0, "Solid", "Solid"),

  /**
   * The '<em><b>Dotted</b></em>' literal object. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see #DOTTED_VALUE
   * @generated
   * @ordered
   */
  DOTTED(2, "Dotted", "Dotted"),

  /**
   * The '<em><b>Dashed</b></em>' literal object. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see #DASHED_VALUE
   * @generated
   * @ordered
   */
  DASHED(3, "Dashed", "Dashed");

  /**
   * The '<em><b>None</b></em>' literal value. <!-- begin-user-doc -->
   * <p>
   * If the meaning of '<em><b>None</b></em>' literal object isn't clear, there really should be
   * more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @see #NONE
   * @model name="None"
   * @generated
   * @ordered
   */
  public static final int NONE_VALUE = 1;

  /**
   * The '<em><b>Solid</b></em>' literal value. <!-- begin-user-doc -->
   * <p>
   * If the meaning of '<em><b>Solid</b></em>' literal object isn't clear, there really should be
   * more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @see #SOLID
   * @model name="Solid"
   * @generated
   * @ordered
   */
  public static final int SOLID_VALUE = 0;

  /**
   * The '<em><b>Dotted</b></em>' literal value. <!-- begin-user-doc -->
   * <p>
   * If the meaning of '<em><b>Dotted</b></em>' literal object isn't clear, there really should be
   * more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @see #DOTTED
   * @model name="Dotted"
   * @generated
   * @ordered
   */
  public static final int DOTTED_VALUE = 2;

  /**
   * The '<em><b>Dashed</b></em>' literal value. <!-- begin-user-doc -->
   * <p>
   * If the meaning of '<em><b>Dashed</b></em>' literal object isn't clear, there really should be
   * more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @see #DASHED
   * @model name="Dashed"
   * @generated
   * @ordered
   */
  public static final int DASHED_VALUE = 3;

  /**
   * An array of all the '<em><b>Line Type</b></em>' enumerators. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   */
  private static final LineType[] VALUES_ARRAY = new LineType[]
  {NONE, SOLID, DOTTED, DASHED,};

  /**
   * A public read-only list of all the '<em><b>Line Type</b></em>' enumerators. <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public static final List<LineType> VALUES = Collections.unmodifiableList(
      Arrays.asList(VALUES_ARRAY));

  /**
   * Returns the '<em><b>Line Type</b></em>' literal with the specified literal value. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @param literal
   *          the literal.
   * @return the matching enumerator or <code>null</code>.
   * @generated
   */
  public static LineType get(String literal)
  {
    for (int i = 0; i < VALUES_ARRAY.length; ++i)
    {
      LineType result = VALUES_ARRAY[i];
      if (result.toString().equals(literal))
      {
        return result;
      }
    }
    return null;
  }

  /**
   * Returns the '<em><b>Line Type</b></em>' literal with the specified name. <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * 
   * @param name
   *          the name.
   * @return the matching enumerator or <code>null</code>.
   * @generated
   */
  public static LineType getByName(String name)
  {
    for (int i = 0; i < VALUES_ARRAY.length; ++i)
    {
      LineType result = VALUES_ARRAY[i];
      if (result.getName().equals(name))
      {
        return result;
      }
    }
    return null;
  }

  /**
   * Returns the '<em><b>Line Type</b></em>' literal with the specified integer value. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @param value
   *          the integer value.
   * @return the matching enumerator or <code>null</code>.
   * @generated
   */
  public static LineType get(int value)
  {
    switch (value)
    {
    case NONE_VALUE:
      return NONE;
    case SOLID_VALUE:
      return SOLID;
    case DOTTED_VALUE:
      return DOTTED;
    case DASHED_VALUE:
      return DASHED;
    }
    return null;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  private final int value;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  private final String name;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  private final String literal;

  /**
   * Only this class can construct instances. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  private LineType(int value, String name, String literal)
  {
    this.value = value;
    this.name = name;
    this.literal = literal;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public int getValue()
  {
    return value;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public String getName()
  {
    return name;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public String getLiteral()
  {
    return literal;
  }

  /**
   * Returns the literal value of the enumerator, which is its string representation. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @Override
  public String toString()
  {
    return literal;
  }

} // LineType
