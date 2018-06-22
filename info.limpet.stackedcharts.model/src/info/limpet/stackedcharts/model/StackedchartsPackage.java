/**
 */
package info.limpet.stackedcharts.model;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

/**
 * <!-- begin-user-doc --> The <b>Package</b> for the model. It contains accessors for the meta
 * objects to represent
 * <ul>
 * <li>each class,</li>
 * <li>each feature of each class,</li>
 * <li>each operation of each class,</li>
 * <li>each enum,</li>
 * <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * 
 * @see info.limpet.stackedcharts.model.StackedchartsFactory
 * @model kind="package"
 * @generated
 */
public interface StackedchartsPackage extends EPackage
{
  /**
   * The package name. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  String eNAME = "model";

  /**
   * The package namespace URI. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  String eNS_URI = "stackedcharts";

  /**
   * The package namespace name. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  String eNS_PREFIX = "stackedcharts";

  /**
   * The singleton instance of the package. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  StackedchartsPackage eINSTANCE =
      info.limpet.stackedcharts.model.impl.StackedchartsPackageImpl.init();

  /**
   * The meta object id for the '{@link info.limpet.stackedcharts.model.impl.ChartSetImpl <em>Chart
   * Set</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see info.limpet.stackedcharts.model.impl.ChartSetImpl
   * @see info.limpet.stackedcharts.model.impl.StackedchartsPackageImpl#getChartSet()
   * @generated
   */
  int CHART_SET = 0;

  /**
   * The feature id for the '<em><b>Charts</b></em>' containment reference list. <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int CHART_SET__CHARTS = 0;

  /**
   * The feature id for the '<em><b>Orientation</b></em>' attribute. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int CHART_SET__ORIENTATION = 1;

  /**
   * The feature id for the '<em><b>Shared Axis</b></em>' containment reference. <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int CHART_SET__SHARED_AXIS = 2;

  /**
   * The number of structural features of the '<em>Chart Set</em>' class. <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int CHART_SET_FEATURE_COUNT = 3;

  /**
   * The number of operations of the '<em>Chart Set</em>' class. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int CHART_SET_OPERATION_COUNT = 0;

  /**
   * The meta object id for the '{@link info.limpet.stackedcharts.model.impl.ChartImpl
   * <em>Chart</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see info.limpet.stackedcharts.model.impl.ChartImpl
   * @see info.limpet.stackedcharts.model.impl.StackedchartsPackageImpl#getChart()
   * @generated
   */
  int CHART = 1;

  /**
   * The feature id for the '<em><b>Parent</b></em>' container reference. <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int CHART__PARENT = 0;

  /**
   * The feature id for the '<em><b>Max Axes</b></em>' containment reference list. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int CHART__MAX_AXES = 1;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int CHART__NAME = 2;

  /**
   * The feature id for the '<em><b>Title</b></em>' attribute. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int CHART__TITLE = 3;

  /**
   * The feature id for the '<em><b>Min Axes</b></em>' containment reference list. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int CHART__MIN_AXES = 4;

  /**
   * The number of structural features of the '<em>Chart</em>' class. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int CHART_FEATURE_COUNT = 5;

  /**
   * The number of operations of the '<em>Chart</em>' class. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int CHART_OPERATION_COUNT = 0;

  /**
   * The meta object id for the '{@link info.limpet.stackedcharts.model.impl.AbstractAxisImpl
   * <em>Abstract Axis</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see info.limpet.stackedcharts.model.impl.AbstractAxisImpl
   * @see info.limpet.stackedcharts.model.impl.StackedchartsPackageImpl#getAbstractAxis()
   * @generated
   */
  int ABSTRACT_AXIS = 11;

  /**
   * The feature id for the '<em><b>Scale</b></em>' attribute. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int ABSTRACT_AXIS__SCALE = 0;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int ABSTRACT_AXIS__NAME = 1;

  /**
   * The feature id for the '<em><b>Auto Scale</b></em>' attribute. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int ABSTRACT_AXIS__AUTO_SCALE = 2;

  /**
   * The feature id for the '<em><b>Direction</b></em>' attribute. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int ABSTRACT_AXIS__DIRECTION = 3;

  /**
   * The feature id for the '<em><b>Font</b></em>' attribute. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int ABSTRACT_AXIS__FONT = 4;

  /**
   * The feature id for the '<em><b>Color</b></em>' attribute. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int ABSTRACT_AXIS__COLOR = 5;

  /**
   * The feature id for the '<em><b>Axis Type</b></em>' containment reference. <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int ABSTRACT_AXIS__AXIS_TYPE = 6;

  /**
   * The number of structural features of the '<em>Abstract Axis</em>' class. <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int ABSTRACT_AXIS_FEATURE_COUNT = 7;

  /**
   * The number of operations of the '<em>Abstract Axis</em>' class. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int ABSTRACT_AXIS_OPERATION_COUNT = 0;

  /**
   * The meta object id for the '{@link info.limpet.stackedcharts.model.impl.DependentAxisImpl
   * <em>Dependent Axis</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see info.limpet.stackedcharts.model.impl.DependentAxisImpl
   * @see info.limpet.stackedcharts.model.impl.StackedchartsPackageImpl#getDependentAxis()
   * @generated
   */
  int DEPENDENT_AXIS = 2;

  /**
   * The feature id for the '<em><b>Scale</b></em>' attribute. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int DEPENDENT_AXIS__SCALE = ABSTRACT_AXIS__SCALE;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int DEPENDENT_AXIS__NAME = ABSTRACT_AXIS__NAME;

  /**
   * The feature id for the '<em><b>Auto Scale</b></em>' attribute. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int DEPENDENT_AXIS__AUTO_SCALE = ABSTRACT_AXIS__AUTO_SCALE;

  /**
   * The feature id for the '<em><b>Direction</b></em>' attribute. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int DEPENDENT_AXIS__DIRECTION = ABSTRACT_AXIS__DIRECTION;

  /**
   * The feature id for the '<em><b>Font</b></em>' attribute. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int DEPENDENT_AXIS__FONT = ABSTRACT_AXIS__FONT;

  /**
   * The feature id for the '<em><b>Color</b></em>' attribute. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int DEPENDENT_AXIS__COLOR = ABSTRACT_AXIS__COLOR;

  /**
   * The feature id for the '<em><b>Axis Type</b></em>' containment reference. <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int DEPENDENT_AXIS__AXIS_TYPE = ABSTRACT_AXIS__AXIS_TYPE;

  /**
   * The feature id for the '<em><b>Datasets</b></em>' containment reference list. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int DEPENDENT_AXIS__DATASETS = ABSTRACT_AXIS_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Annotations</b></em>' containment reference list. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int DEPENDENT_AXIS__ANNOTATIONS = ABSTRACT_AXIS_FEATURE_COUNT + 1;

  /**
   * The number of structural features of the '<em>Dependent Axis</em>' class. <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int DEPENDENT_AXIS_FEATURE_COUNT = ABSTRACT_AXIS_FEATURE_COUNT + 2;

  /**
   * The number of operations of the '<em>Dependent Axis</em>' class. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int DEPENDENT_AXIS_OPERATION_COUNT = ABSTRACT_AXIS_OPERATION_COUNT + 0;

  /**
   * The meta object id for the '{@link info.limpet.stackedcharts.model.impl.DatasetImpl
   * <em>Dataset</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see info.limpet.stackedcharts.model.impl.DatasetImpl
   * @see info.limpet.stackedcharts.model.impl.StackedchartsPackageImpl#getDataset()
   * @generated
   */
  int DATASET = 3;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int DATASET__NAME = 0;

  /**
   * The feature id for the '<em><b>Measurements</b></em>' containment reference list. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int DATASET__MEASUREMENTS = 1;

  /**
   * The feature id for the '<em><b>Styling</b></em>' containment reference. <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int DATASET__STYLING = 2;

  /**
   * The feature id for the '<em><b>Units</b></em>' attribute. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int DATASET__UNITS = 3;

  /**
   * The number of structural features of the '<em>Dataset</em>' class. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int DATASET_FEATURE_COUNT = 4;

  /**
   * The number of operations of the '<em>Dataset</em>' class. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int DATASET_OPERATION_COUNT = 0;

  /**
   * The meta object id for the '{@link info.limpet.stackedcharts.model.impl.DataItemImpl <em>Data
   * Item</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see info.limpet.stackedcharts.model.impl.DataItemImpl
   * @see info.limpet.stackedcharts.model.impl.StackedchartsPackageImpl#getDataItem()
   * @generated
   */
  int DATA_ITEM = 4;

  /**
   * The feature id for the '<em><b>Independent Val</b></em>' attribute. <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int DATA_ITEM__INDEPENDENT_VAL = 0;

  /**
   * The feature id for the '<em><b>Dependent Val</b></em>' attribute. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int DATA_ITEM__DEPENDENT_VAL = 1;

  /**
   * The number of structural features of the '<em>Data Item</em>' class. <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int DATA_ITEM_FEATURE_COUNT = 2;

  /**
   * The number of operations of the '<em>Data Item</em>' class. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int DATA_ITEM_OPERATION_COUNT = 0;

  /**
   * The meta object id for the '{@link info.limpet.stackedcharts.model.impl.AbstractAnnotationImpl
   * <em>Abstract Annotation</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see info.limpet.stackedcharts.model.impl.AbstractAnnotationImpl
   * @see info.limpet.stackedcharts.model.impl.StackedchartsPackageImpl#getAbstractAnnotation()
   * @generated
   */
  int ABSTRACT_ANNOTATION = 5;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int ABSTRACT_ANNOTATION__NAME = 0;

  /**
   * The feature id for the '<em><b>Color</b></em>' attribute. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int ABSTRACT_ANNOTATION__COLOR = 1;

  /**
   * The feature id for the '<em><b>Include In Legend</b></em>' attribute. <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int ABSTRACT_ANNOTATION__INCLUDE_IN_LEGEND = 2;

  /**
   * The number of structural features of the '<em>Abstract Annotation</em>' class. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int ABSTRACT_ANNOTATION_FEATURE_COUNT = 3;

  /**
   * The number of operations of the '<em>Abstract Annotation</em>' class. <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int ABSTRACT_ANNOTATION_OPERATION_COUNT = 0;

  /**
   * The meta object id for the '{@link info.limpet.stackedcharts.model.impl.ZoneImpl
   * <em>Zone</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see info.limpet.stackedcharts.model.impl.ZoneImpl
   * @see info.limpet.stackedcharts.model.impl.StackedchartsPackageImpl#getZone()
   * @generated
   */
  int ZONE = 6;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int ZONE__NAME = ABSTRACT_ANNOTATION__NAME;

  /**
   * The feature id for the '<em><b>Color</b></em>' attribute. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int ZONE__COLOR = ABSTRACT_ANNOTATION__COLOR;

  /**
   * The feature id for the '<em><b>Include In Legend</b></em>' attribute. <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int ZONE__INCLUDE_IN_LEGEND = ABSTRACT_ANNOTATION__INCLUDE_IN_LEGEND;

  /**
   * The feature id for the '<em><b>Start</b></em>' attribute. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int ZONE__START = ABSTRACT_ANNOTATION_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>End</b></em>' attribute. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int ZONE__END = ABSTRACT_ANNOTATION_FEATURE_COUNT + 1;

  /**
   * The number of structural features of the '<em>Zone</em>' class. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int ZONE_FEATURE_COUNT = ABSTRACT_ANNOTATION_FEATURE_COUNT + 2;

  /**
   * The number of operations of the '<em>Zone</em>' class. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int ZONE_OPERATION_COUNT = ABSTRACT_ANNOTATION_OPERATION_COUNT + 0;

  /**
   * The meta object id for the '{@link info.limpet.stackedcharts.model.impl.MarkerImpl
   * <em>Marker</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see info.limpet.stackedcharts.model.impl.MarkerImpl
   * @see info.limpet.stackedcharts.model.impl.StackedchartsPackageImpl#getMarker()
   * @generated
   */
  int MARKER = 7;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int MARKER__NAME = ABSTRACT_ANNOTATION__NAME;

  /**
   * The feature id for the '<em><b>Color</b></em>' attribute. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int MARKER__COLOR = ABSTRACT_ANNOTATION__COLOR;

  /**
   * The feature id for the '<em><b>Include In Legend</b></em>' attribute. <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int MARKER__INCLUDE_IN_LEGEND = ABSTRACT_ANNOTATION__INCLUDE_IN_LEGEND;

  /**
   * The feature id for the '<em><b>Value</b></em>' attribute. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int MARKER__VALUE = ABSTRACT_ANNOTATION_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Marker</em>' class. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int MARKER_FEATURE_COUNT = ABSTRACT_ANNOTATION_FEATURE_COUNT + 1;

  /**
   * The number of operations of the '<em>Marker</em>' class. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int MARKER_OPERATION_COUNT = ABSTRACT_ANNOTATION_OPERATION_COUNT + 0;

  /**
   * The meta object id for the '{@link info.limpet.stackedcharts.model.impl.StylingImpl
   * <em>Styling</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see info.limpet.stackedcharts.model.impl.StylingImpl
   * @see info.limpet.stackedcharts.model.impl.StackedchartsPackageImpl#getStyling()
   * @generated
   */
  int STYLING = 8;

  /**
   * The feature id for the '<em><b>Marker Style</b></em>' attribute. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int STYLING__MARKER_STYLE = 0;

  /**
   * The feature id for the '<em><b>Marker Size</b></em>' attribute. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int STYLING__MARKER_SIZE = 1;

  /**
   * The feature id for the '<em><b>Line Thickness</b></em>' attribute. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int STYLING__LINE_THICKNESS = 2;

  /**
   * The feature id for the '<em><b>Line Style</b></em>' attribute. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int STYLING__LINE_STYLE = 3;

  /**
   * The feature id for the '<em><b>Include In Legend</b></em>' attribute. <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int STYLING__INCLUDE_IN_LEGEND = 4;

  /**
   * The number of structural features of the '<em>Styling</em>' class. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int STYLING_FEATURE_COUNT = 5;

  /**
   * The number of operations of the '<em>Styling</em>' class. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int STYLING_OPERATION_COUNT = 0;

  /**
   * The meta object id for the '{@link info.limpet.stackedcharts.model.impl.PlainStylingImpl
   * <em>Plain Styling</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see info.limpet.stackedcharts.model.impl.PlainStylingImpl
   * @see info.limpet.stackedcharts.model.impl.StackedchartsPackageImpl#getPlainStyling()
   * @generated
   */
  int PLAIN_STYLING = 9;

  /**
   * The feature id for the '<em><b>Marker Style</b></em>' attribute. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int PLAIN_STYLING__MARKER_STYLE = STYLING__MARKER_STYLE;

  /**
   * The feature id for the '<em><b>Marker Size</b></em>' attribute. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int PLAIN_STYLING__MARKER_SIZE = STYLING__MARKER_SIZE;

  /**
   * The feature id for the '<em><b>Line Thickness</b></em>' attribute. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int PLAIN_STYLING__LINE_THICKNESS = STYLING__LINE_THICKNESS;

  /**
   * The feature id for the '<em><b>Line Style</b></em>' attribute. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int PLAIN_STYLING__LINE_STYLE = STYLING__LINE_STYLE;

  /**
   * The feature id for the '<em><b>Include In Legend</b></em>' attribute. <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int PLAIN_STYLING__INCLUDE_IN_LEGEND = STYLING__INCLUDE_IN_LEGEND;

  /**
   * The feature id for the '<em><b>Color</b></em>' attribute. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int PLAIN_STYLING__COLOR = STYLING_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Plain Styling</em>' class. <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int PLAIN_STYLING_FEATURE_COUNT = STYLING_FEATURE_COUNT + 1;

  /**
   * The number of operations of the '<em>Plain Styling</em>' class. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int PLAIN_STYLING_OPERATION_COUNT = STYLING_OPERATION_COUNT + 0;

  /**
   * The meta object id for the '{@link info.limpet.stackedcharts.model.impl.LinearStylingImpl
   * <em>Linear Styling</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see info.limpet.stackedcharts.model.impl.LinearStylingImpl
   * @see info.limpet.stackedcharts.model.impl.StackedchartsPackageImpl#getLinearStyling()
   * @generated
   */
  int LINEAR_STYLING = 10;

  /**
   * The feature id for the '<em><b>Marker Style</b></em>' attribute. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int LINEAR_STYLING__MARKER_STYLE = STYLING__MARKER_STYLE;

  /**
   * The feature id for the '<em><b>Marker Size</b></em>' attribute. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int LINEAR_STYLING__MARKER_SIZE = STYLING__MARKER_SIZE;

  /**
   * The feature id for the '<em><b>Line Thickness</b></em>' attribute. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int LINEAR_STYLING__LINE_THICKNESS = STYLING__LINE_THICKNESS;

  /**
   * The feature id for the '<em><b>Line Style</b></em>' attribute. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int LINEAR_STYLING__LINE_STYLE = STYLING__LINE_STYLE;

  /**
   * The feature id for the '<em><b>Include In Legend</b></em>' attribute. <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int LINEAR_STYLING__INCLUDE_IN_LEGEND = STYLING__INCLUDE_IN_LEGEND;

  /**
   * The feature id for the '<em><b>Start Color</b></em>' attribute. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int LINEAR_STYLING__START_COLOR = STYLING_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>End Color</b></em>' attribute. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int LINEAR_STYLING__END_COLOR = STYLING_FEATURE_COUNT + 1;

  /**
   * The feature id for the '<em><b>Start Val</b></em>' attribute. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int LINEAR_STYLING__START_VAL = STYLING_FEATURE_COUNT + 2;

  /**
   * The feature id for the '<em><b>End Val</b></em>' attribute. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int LINEAR_STYLING__END_VAL = STYLING_FEATURE_COUNT + 3;

  /**
   * The number of structural features of the '<em>Linear Styling</em>' class. <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int LINEAR_STYLING_FEATURE_COUNT = STYLING_FEATURE_COUNT + 4;

  /**
   * The number of operations of the '<em>Linear Styling</em>' class. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int LINEAR_STYLING_OPERATION_COUNT = STYLING_OPERATION_COUNT + 0;

  /**
   * The meta object id for the '{@link info.limpet.stackedcharts.model.impl.IndependentAxisImpl
   * <em>Independent Axis</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see info.limpet.stackedcharts.model.impl.IndependentAxisImpl
   * @see info.limpet.stackedcharts.model.impl.StackedchartsPackageImpl#getIndependentAxis()
   * @generated
   */
  int INDEPENDENT_AXIS = 12;

  /**
   * The feature id for the '<em><b>Scale</b></em>' attribute. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int INDEPENDENT_AXIS__SCALE = ABSTRACT_AXIS__SCALE;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int INDEPENDENT_AXIS__NAME = ABSTRACT_AXIS__NAME;

  /**
   * The feature id for the '<em><b>Auto Scale</b></em>' attribute. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int INDEPENDENT_AXIS__AUTO_SCALE = ABSTRACT_AXIS__AUTO_SCALE;

  /**
   * The feature id for the '<em><b>Direction</b></em>' attribute. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int INDEPENDENT_AXIS__DIRECTION = ABSTRACT_AXIS__DIRECTION;

  /**
   * The feature id for the '<em><b>Font</b></em>' attribute. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int INDEPENDENT_AXIS__FONT = ABSTRACT_AXIS__FONT;

  /**
   * The feature id for the '<em><b>Color</b></em>' attribute. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int INDEPENDENT_AXIS__COLOR = ABSTRACT_AXIS__COLOR;

  /**
   * The feature id for the '<em><b>Axis Type</b></em>' containment reference. <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int INDEPENDENT_AXIS__AXIS_TYPE = ABSTRACT_AXIS__AXIS_TYPE;

  /**
   * The feature id for the '<em><b>Annotations</b></em>' containment reference list. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int INDEPENDENT_AXIS__ANNOTATIONS = ABSTRACT_AXIS_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Independent Axis</em>' class. <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int INDEPENDENT_AXIS_FEATURE_COUNT = ABSTRACT_AXIS_FEATURE_COUNT + 1;

  /**
   * The number of operations of the '<em>Independent Axis</em>' class. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int INDEPENDENT_AXIS_OPERATION_COUNT = ABSTRACT_AXIS_OPERATION_COUNT + 0;

  /**
   * The meta object id for the '{@link info.limpet.stackedcharts.model.impl.ScatterSetImpl
   * <em>Scatter Set</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see info.limpet.stackedcharts.model.impl.ScatterSetImpl
   * @see info.limpet.stackedcharts.model.impl.StackedchartsPackageImpl#getScatterSet()
   * @generated
   */
  int SCATTER_SET = 13;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int SCATTER_SET__NAME = ABSTRACT_ANNOTATION__NAME;

  /**
   * The feature id for the '<em><b>Color</b></em>' attribute. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int SCATTER_SET__COLOR = ABSTRACT_ANNOTATION__COLOR;

  /**
   * The feature id for the '<em><b>Include In Legend</b></em>' attribute. <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int SCATTER_SET__INCLUDE_IN_LEGEND = ABSTRACT_ANNOTATION__INCLUDE_IN_LEGEND;

  /**
   * The feature id for the '<em><b>Datums</b></em>' containment reference list. <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int SCATTER_SET__DATUMS = ABSTRACT_ANNOTATION_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Scatter Set</em>' class. <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int SCATTER_SET_FEATURE_COUNT = ABSTRACT_ANNOTATION_FEATURE_COUNT + 1;

  /**
   * The number of operations of the '<em>Scatter Set</em>' class. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int SCATTER_SET_OPERATION_COUNT = ABSTRACT_ANNOTATION_OPERATION_COUNT + 0;

  /**
   * The meta object id for the '{@link info.limpet.stackedcharts.model.impl.DatumImpl
   * <em>Datum</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see info.limpet.stackedcharts.model.impl.DatumImpl
   * @see info.limpet.stackedcharts.model.impl.StackedchartsPackageImpl#getDatum()
   * @generated
   */
  int DATUM = 14;

  /**
   * The feature id for the '<em><b>Val</b></em>' attribute. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int DATUM__VAL = 0;

  /**
   * The feature id for the '<em><b>Color</b></em>' attribute. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int DATUM__COLOR = 1;

  /**
   * The number of structural features of the '<em>Datum</em>' class. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int DATUM_FEATURE_COUNT = 2;

  /**
   * The number of operations of the '<em>Datum</em>' class. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int DATUM_OPERATION_COUNT = 0;

  /**
   * The meta object id for the '{@link info.limpet.stackedcharts.model.impl.SelectiveAnnotationImpl
   * <em>Selective Annotation</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see info.limpet.stackedcharts.model.impl.SelectiveAnnotationImpl
   * @see info.limpet.stackedcharts.model.impl.StackedchartsPackageImpl#getSelectiveAnnotation()
   * @generated
   */
  int SELECTIVE_ANNOTATION = 15;

  /**
   * The feature id for the '<em><b>Annotation</b></em>' containment reference. <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int SELECTIVE_ANNOTATION__ANNOTATION = 0;

  /**
   * The feature id for the '<em><b>Appears In</b></em>' reference list. <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int SELECTIVE_ANNOTATION__APPEARS_IN = 1;

  /**
   * The number of structural features of the '<em>Selective Annotation</em>' class. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int SELECTIVE_ANNOTATION_FEATURE_COUNT = 2;

  /**
   * The number of operations of the '<em>Selective Annotation</em>' class. <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int SELECTIVE_ANNOTATION_OPERATION_COUNT = 0;

  /**
   * The meta object id for the '{@link info.limpet.stackedcharts.model.impl.AxisTypeImpl <em>Axis
   * Type</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see info.limpet.stackedcharts.model.impl.AxisTypeImpl
   * @see info.limpet.stackedcharts.model.impl.StackedchartsPackageImpl#getAxisType()
   * @generated
   */
  int AXIS_TYPE = 16;

  /**
   * The number of structural features of the '<em>Axis Type</em>' class. <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int AXIS_TYPE_FEATURE_COUNT = 0;

  /**
   * The number of operations of the '<em>Axis Type</em>' class. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int AXIS_TYPE_OPERATION_COUNT = 0;

  /**
   * The meta object id for the '{@link info.limpet.stackedcharts.model.impl.DateAxisImpl <em>Date
   * Axis</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see info.limpet.stackedcharts.model.impl.DateAxisImpl
   * @see info.limpet.stackedcharts.model.impl.StackedchartsPackageImpl#getDateAxis()
   * @generated
   */
  int DATE_AXIS = 17;

  /**
   * The feature id for the '<em><b>Date Format</b></em>' attribute. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int DATE_AXIS__DATE_FORMAT = AXIS_TYPE_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Date Axis</em>' class. <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int DATE_AXIS_FEATURE_COUNT = AXIS_TYPE_FEATURE_COUNT + 1;

  /**
   * The number of operations of the '<em>Date Axis</em>' class. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int DATE_AXIS_OPERATION_COUNT = AXIS_TYPE_OPERATION_COUNT + 0;

  /**
   * The meta object id for the '{@link info.limpet.stackedcharts.model.impl.NumberAxisImpl
   * <em>Number Axis</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see info.limpet.stackedcharts.model.impl.NumberAxisImpl
   * @see info.limpet.stackedcharts.model.impl.StackedchartsPackageImpl#getNumberAxis()
   * @generated
   */
  int NUMBER_AXIS = 18;

  /**
   * The feature id for the '<em><b>Number Format</b></em>' attribute. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int NUMBER_AXIS__NUMBER_FORMAT = AXIS_TYPE_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Auto Includes Zero</b></em>' attribute. <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int NUMBER_AXIS__AUTO_INCLUDES_ZERO = AXIS_TYPE_FEATURE_COUNT + 1;

  /**
   * The feature id for the '<em><b>Units</b></em>' attribute. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int NUMBER_AXIS__UNITS = AXIS_TYPE_FEATURE_COUNT + 2;

  /**
   * The number of structural features of the '<em>Number Axis</em>' class. <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int NUMBER_AXIS_FEATURE_COUNT = AXIS_TYPE_FEATURE_COUNT + 3;

  /**
   * The number of operations of the '<em>Number Axis</em>' class. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int NUMBER_AXIS_OPERATION_COUNT = AXIS_TYPE_OPERATION_COUNT + 0;

  /**
   * The meta object id for the '{@link info.limpet.stackedcharts.model.impl.AngleAxisImpl <em>Angle
   * Axis</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see info.limpet.stackedcharts.model.impl.AngleAxisImpl
   * @see info.limpet.stackedcharts.model.impl.StackedchartsPackageImpl#getAngleAxis()
   * @generated
   */
  int ANGLE_AXIS = 19;

  /**
   * The feature id for the '<em><b>Number Format</b></em>' attribute. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int ANGLE_AXIS__NUMBER_FORMAT = NUMBER_AXIS__NUMBER_FORMAT;

  /**
   * The feature id for the '<em><b>Auto Includes Zero</b></em>' attribute. <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int ANGLE_AXIS__AUTO_INCLUDES_ZERO = NUMBER_AXIS__AUTO_INCLUDES_ZERO;

  /**
   * The feature id for the '<em><b>Units</b></em>' attribute. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int ANGLE_AXIS__UNITS = NUMBER_AXIS__UNITS;

  /**
   * The feature id for the '<em><b>Min Val</b></em>' attribute. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int ANGLE_AXIS__MIN_VAL = NUMBER_AXIS_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Max Val</b></em>' attribute. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int ANGLE_AXIS__MAX_VAL = NUMBER_AXIS_FEATURE_COUNT + 1;

  /**
   * The feature id for the '<em><b>Mid Origin</b></em>' attribute. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int ANGLE_AXIS__MID_ORIGIN = NUMBER_AXIS_FEATURE_COUNT + 2;

  /**
   * The feature id for the '<em><b>Red Green</b></em>' attribute. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int ANGLE_AXIS__RED_GREEN = NUMBER_AXIS_FEATURE_COUNT + 3;

  /**
   * The number of structural features of the '<em>Angle Axis</em>' class. <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int ANGLE_AXIS_FEATURE_COUNT = NUMBER_AXIS_FEATURE_COUNT + 4;

  /**
   * The number of operations of the '<em>Angle Axis</em>' class. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int ANGLE_AXIS_OPERATION_COUNT = NUMBER_AXIS_OPERATION_COUNT + 0;

  /**
   * The meta object id for the '{@link info.limpet.stackedcharts.model.AxisScale <em>Axis
   * Scale</em>}' enum. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see info.limpet.stackedcharts.model.AxisScale
   * @see info.limpet.stackedcharts.model.impl.StackedchartsPackageImpl#getAxisScale()
   * @generated
   */
  int AXIS_SCALE = 20;

  /**
   * The meta object id for the '{@link info.limpet.stackedcharts.model.Orientation
   * <em>Orientation</em>}' enum. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see info.limpet.stackedcharts.model.Orientation
   * @see info.limpet.stackedcharts.model.impl.StackedchartsPackageImpl#getOrientation()
   * @generated
   */
  int ORIENTATION = 21;

  /**
   * The meta object id for the '{@link info.limpet.stackedcharts.model.AxisDirection <em>Axis
   * Direction</em>}' enum. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see info.limpet.stackedcharts.model.AxisDirection
   * @see info.limpet.stackedcharts.model.impl.StackedchartsPackageImpl#getAxisDirection()
   * @generated
   */
  int AXIS_DIRECTION = 22;

  /**
   * The meta object id for the '{@link info.limpet.stackedcharts.model.MarkerStyle <em>Marker
   * Style</em>}' enum. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see info.limpet.stackedcharts.model.MarkerStyle
   * @see info.limpet.stackedcharts.model.impl.StackedchartsPackageImpl#getMarkerStyle()
   * @generated
   */
  int MARKER_STYLE = 23;

  /**
   * The meta object id for the '{@link info.limpet.stackedcharts.model.LineType <em>Line
   * Type</em>}' enum. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see info.limpet.stackedcharts.model.LineType
   * @see info.limpet.stackedcharts.model.impl.StackedchartsPackageImpl#getLineType()
   * @generated
   */
  int LINE_TYPE = 24;

  /**
   * The meta object id for the '<em>Color</em>' data type. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @see java.awt.Color
   * @see info.limpet.stackedcharts.model.impl.StackedchartsPackageImpl#getColor()
   * @generated
   */
  int COLOR = 25;

  /**
   * Returns the meta object for class '{@link info.limpet.stackedcharts.model.ChartSet <em>Chart
   * Set</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for class '<em>Chart Set</em>'.
   * @see info.limpet.stackedcharts.model.ChartSet
   * @generated
   */
  EClass getChartSet();

  /**
   * Returns the meta object for the containment reference list
   * '{@link info.limpet.stackedcharts.model.ChartSet#getCharts <em>Charts</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the containment reference list '<em>Charts</em>'.
   * @see info.limpet.stackedcharts.model.ChartSet#getCharts()
   * @see #getChartSet()
   * @generated
   */
  EReference getChartSet_Charts();

  /**
   * Returns the meta object for the attribute
   * '{@link info.limpet.stackedcharts.model.ChartSet#getOrientation <em>Orientation</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the attribute '<em>Orientation</em>'.
   * @see info.limpet.stackedcharts.model.ChartSet#getOrientation()
   * @see #getChartSet()
   * @generated
   */
  EAttribute getChartSet_Orientation();

  /**
   * Returns the meta object for the containment reference
   * '{@link info.limpet.stackedcharts.model.ChartSet#getSharedAxis <em>Shared Axis</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the containment reference '<em>Shared Axis</em>'.
   * @see info.limpet.stackedcharts.model.ChartSet#getSharedAxis()
   * @see #getChartSet()
   * @generated
   */
  EReference getChartSet_SharedAxis();

  /**
   * Returns the meta object for class '{@link info.limpet.stackedcharts.model.Chart
   * <em>Chart</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for class '<em>Chart</em>'.
   * @see info.limpet.stackedcharts.model.Chart
   * @generated
   */
  EClass getChart();

  /**
   * Returns the meta object for the container reference
   * '{@link info.limpet.stackedcharts.model.Chart#getParent <em>Parent</em>}'. <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * 
   * @return the meta object for the container reference '<em>Parent</em>'.
   * @see info.limpet.stackedcharts.model.Chart#getParent()
   * @see #getChart()
   * @generated
   */
  EReference getChart_Parent();

  /**
   * Returns the meta object for the containment reference list
   * '{@link info.limpet.stackedcharts.model.Chart#getMaxAxes <em>Max Axes</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the containment reference list '<em>Max Axes</em>'.
   * @see info.limpet.stackedcharts.model.Chart#getMaxAxes()
   * @see #getChart()
   * @generated
   */
  EReference getChart_MaxAxes();

  /**
   * Returns the meta object for the attribute '{@link info.limpet.stackedcharts.model.Chart#getName
   * <em>Name</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the attribute '<em>Name</em>'.
   * @see info.limpet.stackedcharts.model.Chart#getName()
   * @see #getChart()
   * @generated
   */
  EAttribute getChart_Name();

  /**
   * Returns the meta object for the attribute
   * '{@link info.limpet.stackedcharts.model.Chart#getTitle <em>Title</em>}'. <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * 
   * @return the meta object for the attribute '<em>Title</em>'.
   * @see info.limpet.stackedcharts.model.Chart#getTitle()
   * @see #getChart()
   * @generated
   */
  EAttribute getChart_Title();

  /**
   * Returns the meta object for the containment reference list
   * '{@link info.limpet.stackedcharts.model.Chart#getMinAxes <em>Min Axes</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the containment reference list '<em>Min Axes</em>'.
   * @see info.limpet.stackedcharts.model.Chart#getMinAxes()
   * @see #getChart()
   * @generated
   */
  EReference getChart_MinAxes();

  /**
   * Returns the meta object for class '{@link info.limpet.stackedcharts.model.DependentAxis
   * <em>Dependent Axis</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for class '<em>Dependent Axis</em>'.
   * @see info.limpet.stackedcharts.model.DependentAxis
   * @generated
   */
  EClass getDependentAxis();

  /**
   * Returns the meta object for the containment reference list
   * '{@link info.limpet.stackedcharts.model.DependentAxis#getDatasets <em>Datasets</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the containment reference list '<em>Datasets</em>'.
   * @see info.limpet.stackedcharts.model.DependentAxis#getDatasets()
   * @see #getDependentAxis()
   * @generated
   */
  EReference getDependentAxis_Datasets();

  /**
   * Returns the meta object for the containment reference list
   * '{@link info.limpet.stackedcharts.model.DependentAxis#getAnnotations <em>Annotations</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the containment reference list '<em>Annotations</em>'.
   * @see info.limpet.stackedcharts.model.DependentAxis#getAnnotations()
   * @see #getDependentAxis()
   * @generated
   */
  EReference getDependentAxis_Annotations();

  /**
   * Returns the meta object for class '{@link info.limpet.stackedcharts.model.Dataset
   * <em>Dataset</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for class '<em>Dataset</em>'.
   * @see info.limpet.stackedcharts.model.Dataset
   * @generated
   */
  EClass getDataset();

  /**
   * Returns the meta object for the attribute
   * '{@link info.limpet.stackedcharts.model.Dataset#getName <em>Name</em>}'. <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * 
   * @return the meta object for the attribute '<em>Name</em>'.
   * @see info.limpet.stackedcharts.model.Dataset#getName()
   * @see #getDataset()
   * @generated
   */
  EAttribute getDataset_Name();

  /**
   * Returns the meta object for the containment reference list
   * '{@link info.limpet.stackedcharts.model.Dataset#getMeasurements <em>Measurements</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the containment reference list '<em>Measurements</em>'.
   * @see info.limpet.stackedcharts.model.Dataset#getMeasurements()
   * @see #getDataset()
   * @generated
   */
  EReference getDataset_Measurements();

  /**
   * Returns the meta object for the containment reference
   * '{@link info.limpet.stackedcharts.model.Dataset#getStyling <em>Styling</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the containment reference '<em>Styling</em>'.
   * @see info.limpet.stackedcharts.model.Dataset#getStyling()
   * @see #getDataset()
   * @generated
   */
  EReference getDataset_Styling();

  /**
   * Returns the meta object for the attribute
   * '{@link info.limpet.stackedcharts.model.Dataset#getUnits <em>Units</em>}'. <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * 
   * @return the meta object for the attribute '<em>Units</em>'.
   * @see info.limpet.stackedcharts.model.Dataset#getUnits()
   * @see #getDataset()
   * @generated
   */
  EAttribute getDataset_Units();

  /**
   * Returns the meta object for class '{@link info.limpet.stackedcharts.model.DataItem <em>Data
   * Item</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for class '<em>Data Item</em>'.
   * @see info.limpet.stackedcharts.model.DataItem
   * @generated
   */
  EClass getDataItem();

  /**
   * Returns the meta object for the attribute
   * '{@link info.limpet.stackedcharts.model.DataItem#getIndependentVal <em>Independent Val</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the attribute '<em>Independent Val</em>'.
   * @see info.limpet.stackedcharts.model.DataItem#getIndependentVal()
   * @see #getDataItem()
   * @generated
   */
  EAttribute getDataItem_IndependentVal();

  /**
   * Returns the meta object for the attribute
   * '{@link info.limpet.stackedcharts.model.DataItem#getDependentVal <em>Dependent Val</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the attribute '<em>Dependent Val</em>'.
   * @see info.limpet.stackedcharts.model.DataItem#getDependentVal()
   * @see #getDataItem()
   * @generated
   */
  EAttribute getDataItem_DependentVal();

  /**
   * Returns the meta object for class '{@link info.limpet.stackedcharts.model.AbstractAnnotation
   * <em>Abstract Annotation</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for class '<em>Abstract Annotation</em>'.
   * @see info.limpet.stackedcharts.model.AbstractAnnotation
   * @generated
   */
  EClass getAbstractAnnotation();

  /**
   * Returns the meta object for the attribute
   * '{@link info.limpet.stackedcharts.model.AbstractAnnotation#getName <em>Name</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the attribute '<em>Name</em>'.
   * @see info.limpet.stackedcharts.model.AbstractAnnotation#getName()
   * @see #getAbstractAnnotation()
   * @generated
   */
  EAttribute getAbstractAnnotation_Name();

  /**
   * Returns the meta object for the attribute
   * '{@link info.limpet.stackedcharts.model.AbstractAnnotation#getColor <em>Color</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the attribute '<em>Color</em>'.
   * @see info.limpet.stackedcharts.model.AbstractAnnotation#getColor()
   * @see #getAbstractAnnotation()
   * @generated
   */
  EAttribute getAbstractAnnotation_Color();

  /**
   * Returns the meta object for the attribute
   * '{@link info.limpet.stackedcharts.model.AbstractAnnotation#isIncludeInLegend <em>Include In
   * Legend</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the attribute '<em>Include In Legend</em>'.
   * @see info.limpet.stackedcharts.model.AbstractAnnotation#isIncludeInLegend()
   * @see #getAbstractAnnotation()
   * @generated
   */
  EAttribute getAbstractAnnotation_IncludeInLegend();

  /**
   * Returns the meta object for class '{@link info.limpet.stackedcharts.model.Zone <em>Zone</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for class '<em>Zone</em>'.
   * @see info.limpet.stackedcharts.model.Zone
   * @generated
   */
  EClass getZone();

  /**
   * Returns the meta object for the attribute '{@link info.limpet.stackedcharts.model.Zone#getStart
   * <em>Start</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the attribute '<em>Start</em>'.
   * @see info.limpet.stackedcharts.model.Zone#getStart()
   * @see #getZone()
   * @generated
   */
  EAttribute getZone_Start();

  /**
   * Returns the meta object for the attribute '{@link info.limpet.stackedcharts.model.Zone#getEnd
   * <em>End</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the attribute '<em>End</em>'.
   * @see info.limpet.stackedcharts.model.Zone#getEnd()
   * @see #getZone()
   * @generated
   */
  EAttribute getZone_End();

  /**
   * Returns the meta object for class '{@link info.limpet.stackedcharts.model.Marker
   * <em>Marker</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for class '<em>Marker</em>'.
   * @see info.limpet.stackedcharts.model.Marker
   * @generated
   */
  EClass getMarker();

  /**
   * Returns the meta object for the attribute
   * '{@link info.limpet.stackedcharts.model.Marker#getValue <em>Value</em>}'. <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * 
   * @return the meta object for the attribute '<em>Value</em>'.
   * @see info.limpet.stackedcharts.model.Marker#getValue()
   * @see #getMarker()
   * @generated
   */
  EAttribute getMarker_Value();

  /**
   * Returns the meta object for class '{@link info.limpet.stackedcharts.model.Styling
   * <em>Styling</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for class '<em>Styling</em>'.
   * @see info.limpet.stackedcharts.model.Styling
   * @generated
   */
  EClass getStyling();

  /**
   * Returns the meta object for the attribute
   * '{@link info.limpet.stackedcharts.model.Styling#getMarkerStyle <em>Marker Style</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the attribute '<em>Marker Style</em>'.
   * @see info.limpet.stackedcharts.model.Styling#getMarkerStyle()
   * @see #getStyling()
   * @generated
   */
  EAttribute getStyling_MarkerStyle();

  /**
   * Returns the meta object for the attribute
   * '{@link info.limpet.stackedcharts.model.Styling#getMarkerSize <em>Marker Size</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the attribute '<em>Marker Size</em>'.
   * @see info.limpet.stackedcharts.model.Styling#getMarkerSize()
   * @see #getStyling()
   * @generated
   */
  EAttribute getStyling_MarkerSize();

  /**
   * Returns the meta object for the attribute
   * '{@link info.limpet.stackedcharts.model.Styling#getLineThickness <em>Line Thickness</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the attribute '<em>Line Thickness</em>'.
   * @see info.limpet.stackedcharts.model.Styling#getLineThickness()
   * @see #getStyling()
   * @generated
   */
  EAttribute getStyling_LineThickness();

  /**
   * Returns the meta object for the attribute
   * '{@link info.limpet.stackedcharts.model.Styling#getLineStyle <em>Line Style</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the attribute '<em>Line Style</em>'.
   * @see info.limpet.stackedcharts.model.Styling#getLineStyle()
   * @see #getStyling()
   * @generated
   */
  EAttribute getStyling_LineStyle();

  /**
   * Returns the meta object for the attribute
   * '{@link info.limpet.stackedcharts.model.Styling#isIncludeInLegend <em>Include In Legend</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the attribute '<em>Include In Legend</em>'.
   * @see info.limpet.stackedcharts.model.Styling#isIncludeInLegend()
   * @see #getStyling()
   * @generated
   */
  EAttribute getStyling_IncludeInLegend();

  /**
   * Returns the meta object for class '{@link info.limpet.stackedcharts.model.PlainStyling
   * <em>Plain Styling</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for class '<em>Plain Styling</em>'.
   * @see info.limpet.stackedcharts.model.PlainStyling
   * @generated
   */
  EClass getPlainStyling();

  /**
   * Returns the meta object for the attribute
   * '{@link info.limpet.stackedcharts.model.PlainStyling#getColor <em>Color</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the attribute '<em>Color</em>'.
   * @see info.limpet.stackedcharts.model.PlainStyling#getColor()
   * @see #getPlainStyling()
   * @generated
   */
  EAttribute getPlainStyling_Color();

  /**
   * Returns the meta object for class '{@link info.limpet.stackedcharts.model.LinearStyling
   * <em>Linear Styling</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for class '<em>Linear Styling</em>'.
   * @see info.limpet.stackedcharts.model.LinearStyling
   * @generated
   */
  EClass getLinearStyling();

  /**
   * Returns the meta object for the attribute
   * '{@link info.limpet.stackedcharts.model.LinearStyling#getStartColor <em>Start Color</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the attribute '<em>Start Color</em>'.
   * @see info.limpet.stackedcharts.model.LinearStyling#getStartColor()
   * @see #getLinearStyling()
   * @generated
   */
  EAttribute getLinearStyling_StartColor();

  /**
   * Returns the meta object for the attribute
   * '{@link info.limpet.stackedcharts.model.LinearStyling#getEndColor <em>End Color</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the attribute '<em>End Color</em>'.
   * @see info.limpet.stackedcharts.model.LinearStyling#getEndColor()
   * @see #getLinearStyling()
   * @generated
   */
  EAttribute getLinearStyling_EndColor();

  /**
   * Returns the meta object for the attribute
   * '{@link info.limpet.stackedcharts.model.LinearStyling#getStartVal <em>Start Val</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the attribute '<em>Start Val</em>'.
   * @see info.limpet.stackedcharts.model.LinearStyling#getStartVal()
   * @see #getLinearStyling()
   * @generated
   */
  EAttribute getLinearStyling_StartVal();

  /**
   * Returns the meta object for the attribute
   * '{@link info.limpet.stackedcharts.model.LinearStyling#getEndVal <em>End Val</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the attribute '<em>End Val</em>'.
   * @see info.limpet.stackedcharts.model.LinearStyling#getEndVal()
   * @see #getLinearStyling()
   * @generated
   */
  EAttribute getLinearStyling_EndVal();

  /**
   * Returns the meta object for class '{@link info.limpet.stackedcharts.model.AbstractAxis
   * <em>Abstract Axis</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for class '<em>Abstract Axis</em>'.
   * @see info.limpet.stackedcharts.model.AbstractAxis
   * @generated
   */
  EClass getAbstractAxis();

  /**
   * Returns the meta object for the attribute
   * '{@link info.limpet.stackedcharts.model.AbstractAxis#getScale <em>Scale</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the attribute '<em>Scale</em>'.
   * @see info.limpet.stackedcharts.model.AbstractAxis#getScale()
   * @see #getAbstractAxis()
   * @generated
   */
  EAttribute getAbstractAxis_Scale();

  /**
   * Returns the meta object for the attribute
   * '{@link info.limpet.stackedcharts.model.AbstractAxis#getName <em>Name</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the attribute '<em>Name</em>'.
   * @see info.limpet.stackedcharts.model.AbstractAxis#getName()
   * @see #getAbstractAxis()
   * @generated
   */
  EAttribute getAbstractAxis_Name();

  /**
   * Returns the meta object for the attribute
   * '{@link info.limpet.stackedcharts.model.AbstractAxis#isAutoScale <em>Auto Scale</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the attribute '<em>Auto Scale</em>'.
   * @see info.limpet.stackedcharts.model.AbstractAxis#isAutoScale()
   * @see #getAbstractAxis()
   * @generated
   */
  EAttribute getAbstractAxis_AutoScale();

  /**
   * Returns the meta object for the attribute
   * '{@link info.limpet.stackedcharts.model.AbstractAxis#getDirection <em>Direction</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the attribute '<em>Direction</em>'.
   * @see info.limpet.stackedcharts.model.AbstractAxis#getDirection()
   * @see #getAbstractAxis()
   * @generated
   */
  EAttribute getAbstractAxis_Direction();

  /**
   * Returns the meta object for the attribute
   * '{@link info.limpet.stackedcharts.model.AbstractAxis#getFont <em>Font</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the attribute '<em>Font</em>'.
   * @see info.limpet.stackedcharts.model.AbstractAxis#getFont()
   * @see #getAbstractAxis()
   * @generated
   */
  EAttribute getAbstractAxis_Font();

  /**
   * Returns the meta object for the attribute
   * '{@link info.limpet.stackedcharts.model.AbstractAxis#getColor <em>Color</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the attribute '<em>Color</em>'.
   * @see info.limpet.stackedcharts.model.AbstractAxis#getColor()
   * @see #getAbstractAxis()
   * @generated
   */
  EAttribute getAbstractAxis_Color();

  /**
   * Returns the meta object for the containment reference
   * '{@link info.limpet.stackedcharts.model.AbstractAxis#getAxisType <em>Axis Type</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the containment reference '<em>Axis Type</em>'.
   * @see info.limpet.stackedcharts.model.AbstractAxis#getAxisType()
   * @see #getAbstractAxis()
   * @generated
   */
  EReference getAbstractAxis_AxisType();

  /**
   * Returns the meta object for class '{@link info.limpet.stackedcharts.model.IndependentAxis
   * <em>Independent Axis</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for class '<em>Independent Axis</em>'.
   * @see info.limpet.stackedcharts.model.IndependentAxis
   * @generated
   */
  EClass getIndependentAxis();

  /**
   * Returns the meta object for the containment reference list
   * '{@link info.limpet.stackedcharts.model.IndependentAxis#getAnnotations <em>Annotations</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the containment reference list '<em>Annotations</em>'.
   * @see info.limpet.stackedcharts.model.IndependentAxis#getAnnotations()
   * @see #getIndependentAxis()
   * @generated
   */
  EReference getIndependentAxis_Annotations();

  /**
   * Returns the meta object for class '{@link info.limpet.stackedcharts.model.ScatterSet
   * <em>Scatter Set</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for class '<em>Scatter Set</em>'.
   * @see info.limpet.stackedcharts.model.ScatterSet
   * @generated
   */
  EClass getScatterSet();

  /**
   * Returns the meta object for the containment reference list
   * '{@link info.limpet.stackedcharts.model.ScatterSet#getDatums <em>Datums</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the containment reference list '<em>Datums</em>'.
   * @see info.limpet.stackedcharts.model.ScatterSet#getDatums()
   * @see #getScatterSet()
   * @generated
   */
  EReference getScatterSet_Datums();

  /**
   * Returns the meta object for class '{@link info.limpet.stackedcharts.model.Datum
   * <em>Datum</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for class '<em>Datum</em>'.
   * @see info.limpet.stackedcharts.model.Datum
   * @generated
   */
  EClass getDatum();

  /**
   * Returns the meta object for the attribute '{@link info.limpet.stackedcharts.model.Datum#getVal
   * <em>Val</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the attribute '<em>Val</em>'.
   * @see info.limpet.stackedcharts.model.Datum#getVal()
   * @see #getDatum()
   * @generated
   */
  EAttribute getDatum_Val();

  /**
   * Returns the meta object for the attribute
   * '{@link info.limpet.stackedcharts.model.Datum#getColor <em>Color</em>}'. <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * 
   * @return the meta object for the attribute '<em>Color</em>'.
   * @see info.limpet.stackedcharts.model.Datum#getColor()
   * @see #getDatum()
   * @generated
   */
  EAttribute getDatum_Color();

  /**
   * Returns the meta object for class '{@link info.limpet.stackedcharts.model.SelectiveAnnotation
   * <em>Selective Annotation</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for class '<em>Selective Annotation</em>'.
   * @see info.limpet.stackedcharts.model.SelectiveAnnotation
   * @generated
   */
  EClass getSelectiveAnnotation();

  /**
   * Returns the meta object for the containment reference
   * '{@link info.limpet.stackedcharts.model.SelectiveAnnotation#getAnnotation
   * <em>Annotation</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the containment reference '<em>Annotation</em>'.
   * @see info.limpet.stackedcharts.model.SelectiveAnnotation#getAnnotation()
   * @see #getSelectiveAnnotation()
   * @generated
   */
  EReference getSelectiveAnnotation_Annotation();

  /**
   * Returns the meta object for the reference list
   * '{@link info.limpet.stackedcharts.model.SelectiveAnnotation#getAppearsIn <em>Appears In</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the reference list '<em>Appears In</em>'.
   * @see info.limpet.stackedcharts.model.SelectiveAnnotation#getAppearsIn()
   * @see #getSelectiveAnnotation()
   * @generated
   */
  EReference getSelectiveAnnotation_AppearsIn();

  /**
   * Returns the meta object for class '{@link info.limpet.stackedcharts.model.AxisType <em>Axis
   * Type</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for class '<em>Axis Type</em>'.
   * @see info.limpet.stackedcharts.model.AxisType
   * @generated
   */
  EClass getAxisType();

  /**
   * Returns the meta object for class '{@link info.limpet.stackedcharts.model.DateAxis <em>Date
   * Axis</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for class '<em>Date Axis</em>'.
   * @see info.limpet.stackedcharts.model.DateAxis
   * @generated
   */
  EClass getDateAxis();

  /**
   * Returns the meta object for the attribute
   * '{@link info.limpet.stackedcharts.model.DateAxis#getDateFormat <em>Date Format</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the attribute '<em>Date Format</em>'.
   * @see info.limpet.stackedcharts.model.DateAxis#getDateFormat()
   * @see #getDateAxis()
   * @generated
   */
  EAttribute getDateAxis_DateFormat();

  /**
   * Returns the meta object for class '{@link info.limpet.stackedcharts.model.NumberAxis <em>Number
   * Axis</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for class '<em>Number Axis</em>'.
   * @see info.limpet.stackedcharts.model.NumberAxis
   * @generated
   */
  EClass getNumberAxis();

  /**
   * Returns the meta object for the attribute
   * '{@link info.limpet.stackedcharts.model.NumberAxis#getNumberFormat <em>Number Format</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the attribute '<em>Number Format</em>'.
   * @see info.limpet.stackedcharts.model.NumberAxis#getNumberFormat()
   * @see #getNumberAxis()
   * @generated
   */
  EAttribute getNumberAxis_NumberFormat();

  /**
   * Returns the meta object for the attribute
   * '{@link info.limpet.stackedcharts.model.NumberAxis#isAutoIncludesZero <em>Auto Includes
   * Zero</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the attribute '<em>Auto Includes Zero</em>'.
   * @see info.limpet.stackedcharts.model.NumberAxis#isAutoIncludesZero()
   * @see #getNumberAxis()
   * @generated
   */
  EAttribute getNumberAxis_AutoIncludesZero();

  /**
   * Returns the meta object for the attribute
   * '{@link info.limpet.stackedcharts.model.NumberAxis#getUnits <em>Units</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the attribute '<em>Units</em>'.
   * @see info.limpet.stackedcharts.model.NumberAxis#getUnits()
   * @see #getNumberAxis()
   * @generated
   */
  EAttribute getNumberAxis_Units();

  /**
   * Returns the meta object for class '{@link info.limpet.stackedcharts.model.AngleAxis <em>Angle
   * Axis</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for class '<em>Angle Axis</em>'.
   * @see info.limpet.stackedcharts.model.AngleAxis
   * @generated
   */
  EClass getAngleAxis();

  /**
   * Returns the meta object for the attribute
   * '{@link info.limpet.stackedcharts.model.AngleAxis#getMinVal <em>Min Val</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the attribute '<em>Min Val</em>'.
   * @see info.limpet.stackedcharts.model.AngleAxis#getMinVal()
   * @see #getAngleAxis()
   * @generated
   */
  EAttribute getAngleAxis_MinVal();

  /**
   * Returns the meta object for the attribute
   * '{@link info.limpet.stackedcharts.model.AngleAxis#getMaxVal <em>Max Val</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the attribute '<em>Max Val</em>'.
   * @see info.limpet.stackedcharts.model.AngleAxis#getMaxVal()
   * @see #getAngleAxis()
   * @generated
   */
  EAttribute getAngleAxis_MaxVal();

  /**
   * Returns the meta object for the attribute
   * '{@link info.limpet.stackedcharts.model.AngleAxis#isMidOrigin <em>Mid Origin</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the attribute '<em>Mid Origin</em>'.
   * @see info.limpet.stackedcharts.model.AngleAxis#isMidOrigin()
   * @see #getAngleAxis()
   * @generated
   */
  EAttribute getAngleAxis_MidOrigin();

  /**
   * Returns the meta object for the attribute
   * '{@link info.limpet.stackedcharts.model.AngleAxis#isRedGreen <em>Red Green</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the attribute '<em>Red Green</em>'.
   * @see info.limpet.stackedcharts.model.AngleAxis#isRedGreen()
   * @see #getAngleAxis()
   * @generated
   */
  EAttribute getAngleAxis_RedGreen();

  /**
   * Returns the meta object for enum '{@link info.limpet.stackedcharts.model.AxisScale <em>Axis
   * Scale</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for enum '<em>Axis Scale</em>'.
   * @see info.limpet.stackedcharts.model.AxisScale
   * @generated
   */
  EEnum getAxisScale();

  /**
   * Returns the meta object for enum '{@link info.limpet.stackedcharts.model.Orientation
   * <em>Orientation</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for enum '<em>Orientation</em>'.
   * @see info.limpet.stackedcharts.model.Orientation
   * @generated
   */
  EEnum getOrientation();

  /**
   * Returns the meta object for enum '{@link info.limpet.stackedcharts.model.AxisDirection <em>Axis
   * Direction</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for enum '<em>Axis Direction</em>'.
   * @see info.limpet.stackedcharts.model.AxisDirection
   * @generated
   */
  EEnum getAxisDirection();

  /**
   * Returns the meta object for enum '{@link info.limpet.stackedcharts.model.MarkerStyle <em>Marker
   * Style</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for enum '<em>Marker Style</em>'.
   * @see info.limpet.stackedcharts.model.MarkerStyle
   * @generated
   */
  EEnum getMarkerStyle();

  /**
   * Returns the meta object for enum '{@link info.limpet.stackedcharts.model.LineType <em>Line
   * Type</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for enum '<em>Line Type</em>'.
   * @see info.limpet.stackedcharts.model.LineType
   * @generated
   */
  EEnum getLineType();

  /**
   * Returns the meta object for data type '{@link java.awt.Color <em>Color</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for data type '<em>Color</em>'.
   * @see java.awt.Color
   * @model instanceClass="java.awt.Color"
   * @generated
   */
  EDataType getColor();

  /**
   * Returns the factory that creates the instances of the model. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @return the factory that creates the instances of the model.
   * @generated
   */
  StackedchartsFactory getStackedchartsFactory();

  /**
   * <!-- begin-user-doc --> Defines literals for the meta objects that represent
   * <ul>
   * <li>each class,</li>
   * <li>each feature of each class,</li>
   * <li>each operation of each class,</li>
   * <li>each enum,</li>
   * <li>and each data type</li>
   * </ul>
   * <!-- end-user-doc -->
   * 
   * @generated
   */
  interface Literals
  {
    /**
     * The meta object literal for the '{@link info.limpet.stackedcharts.model.impl.ChartSetImpl
     * <em>Chart Set</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see info.limpet.stackedcharts.model.impl.ChartSetImpl
     * @see info.limpet.stackedcharts.model.impl.StackedchartsPackageImpl#getChartSet()
     * @generated
     */
    EClass CHART_SET = eINSTANCE.getChartSet();

    /**
     * The meta object literal for the '<em><b>Charts</b></em>' containment reference list feature.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    EReference CHART_SET__CHARTS = eINSTANCE.getChartSet_Charts();

    /**
     * The meta object literal for the '<em><b>Orientation</b></em>' attribute feature. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    EAttribute CHART_SET__ORIENTATION = eINSTANCE.getChartSet_Orientation();

    /**
     * The meta object literal for the '<em><b>Shared Axis</b></em>' containment reference feature.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    EReference CHART_SET__SHARED_AXIS = eINSTANCE.getChartSet_SharedAxis();

    /**
     * The meta object literal for the '{@link info.limpet.stackedcharts.model.impl.ChartImpl
     * <em>Chart</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see info.limpet.stackedcharts.model.impl.ChartImpl
     * @see info.limpet.stackedcharts.model.impl.StackedchartsPackageImpl#getChart()
     * @generated
     */
    EClass CHART = eINSTANCE.getChart();

    /**
     * The meta object literal for the '<em><b>Parent</b></em>' container reference feature. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    EReference CHART__PARENT = eINSTANCE.getChart_Parent();

    /**
     * The meta object literal for the '<em><b>Max Axes</b></em>' containment reference list
     * feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    EReference CHART__MAX_AXES = eINSTANCE.getChart_MaxAxes();

    /**
     * The meta object literal for the '<em><b>Name</b></em>' attribute feature. <!-- begin-user-doc
     * --> <!-- end-user-doc -->
     * 
     * @generated
     */
    EAttribute CHART__NAME = eINSTANCE.getChart_Name();

    /**
     * The meta object literal for the '<em><b>Title</b></em>' attribute feature. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    EAttribute CHART__TITLE = eINSTANCE.getChart_Title();

    /**
     * The meta object literal for the '<em><b>Min Axes</b></em>' containment reference list
     * feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    EReference CHART__MIN_AXES = eINSTANCE.getChart_MinAxes();

    /**
     * The meta object literal for the
     * '{@link info.limpet.stackedcharts.model.impl.DependentAxisImpl <em>Dependent Axis</em>}'
     * class. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see info.limpet.stackedcharts.model.impl.DependentAxisImpl
     * @see info.limpet.stackedcharts.model.impl.StackedchartsPackageImpl#getDependentAxis()
     * @generated
     */
    EClass DEPENDENT_AXIS = eINSTANCE.getDependentAxis();

    /**
     * The meta object literal for the '<em><b>Datasets</b></em>' containment reference list
     * feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    EReference DEPENDENT_AXIS__DATASETS = eINSTANCE.getDependentAxis_Datasets();

    /**
     * The meta object literal for the '<em><b>Annotations</b></em>' containment reference list
     * feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    EReference DEPENDENT_AXIS__ANNOTATIONS = eINSTANCE
        .getDependentAxis_Annotations();

    /**
     * The meta object literal for the '{@link info.limpet.stackedcharts.model.impl.DatasetImpl
     * <em>Dataset</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see info.limpet.stackedcharts.model.impl.DatasetImpl
     * @see info.limpet.stackedcharts.model.impl.StackedchartsPackageImpl#getDataset()
     * @generated
     */
    EClass DATASET = eINSTANCE.getDataset();

    /**
     * The meta object literal for the '<em><b>Name</b></em>' attribute feature. <!-- begin-user-doc
     * --> <!-- end-user-doc -->
     * 
     * @generated
     */
    EAttribute DATASET__NAME = eINSTANCE.getDataset_Name();

    /**
     * The meta object literal for the '<em><b>Measurements</b></em>' containment reference list
     * feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    EReference DATASET__MEASUREMENTS = eINSTANCE.getDataset_Measurements();

    /**
     * The meta object literal for the '<em><b>Styling</b></em>' containment reference feature. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    EReference DATASET__STYLING = eINSTANCE.getDataset_Styling();

    /**
     * The meta object literal for the '<em><b>Units</b></em>' attribute feature. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    EAttribute DATASET__UNITS = eINSTANCE.getDataset_Units();

    /**
     * The meta object literal for the '{@link info.limpet.stackedcharts.model.impl.DataItemImpl
     * <em>Data Item</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see info.limpet.stackedcharts.model.impl.DataItemImpl
     * @see info.limpet.stackedcharts.model.impl.StackedchartsPackageImpl#getDataItem()
     * @generated
     */
    EClass DATA_ITEM = eINSTANCE.getDataItem();

    /**
     * The meta object literal for the '<em><b>Independent Val</b></em>' attribute feature. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    EAttribute DATA_ITEM__INDEPENDENT_VAL = eINSTANCE
        .getDataItem_IndependentVal();

    /**
     * The meta object literal for the '<em><b>Dependent Val</b></em>' attribute feature. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    EAttribute DATA_ITEM__DEPENDENT_VAL = eINSTANCE.getDataItem_DependentVal();

    /**
     * The meta object literal for the
     * '{@link info.limpet.stackedcharts.model.impl.AbstractAnnotationImpl <em>Abstract
     * Annotation</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see info.limpet.stackedcharts.model.impl.AbstractAnnotationImpl
     * @see info.limpet.stackedcharts.model.impl.StackedchartsPackageImpl#getAbstractAnnotation()
     * @generated
     */
    EClass ABSTRACT_ANNOTATION = eINSTANCE.getAbstractAnnotation();

    /**
     * The meta object literal for the '<em><b>Name</b></em>' attribute feature. <!-- begin-user-doc
     * --> <!-- end-user-doc -->
     * 
     * @generated
     */
    EAttribute ABSTRACT_ANNOTATION__NAME = eINSTANCE
        .getAbstractAnnotation_Name();

    /**
     * The meta object literal for the '<em><b>Color</b></em>' attribute feature. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    EAttribute ABSTRACT_ANNOTATION__COLOR = eINSTANCE
        .getAbstractAnnotation_Color();

    /**
     * The meta object literal for the '<em><b>Include In Legend</b></em>' attribute feature. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    EAttribute ABSTRACT_ANNOTATION__INCLUDE_IN_LEGEND = eINSTANCE
        .getAbstractAnnotation_IncludeInLegend();

    /**
     * The meta object literal for the '{@link info.limpet.stackedcharts.model.impl.ZoneImpl
     * <em>Zone</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see info.limpet.stackedcharts.model.impl.ZoneImpl
     * @see info.limpet.stackedcharts.model.impl.StackedchartsPackageImpl#getZone()
     * @generated
     */
    EClass ZONE = eINSTANCE.getZone();

    /**
     * The meta object literal for the '<em><b>Start</b></em>' attribute feature. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    EAttribute ZONE__START = eINSTANCE.getZone_Start();

    /**
     * The meta object literal for the '<em><b>End</b></em>' attribute feature. <!-- begin-user-doc
     * --> <!-- end-user-doc -->
     * 
     * @generated
     */
    EAttribute ZONE__END = eINSTANCE.getZone_End();

    /**
     * The meta object literal for the '{@link info.limpet.stackedcharts.model.impl.MarkerImpl
     * <em>Marker</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see info.limpet.stackedcharts.model.impl.MarkerImpl
     * @see info.limpet.stackedcharts.model.impl.StackedchartsPackageImpl#getMarker()
     * @generated
     */
    EClass MARKER = eINSTANCE.getMarker();

    /**
     * The meta object literal for the '<em><b>Value</b></em>' attribute feature. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    EAttribute MARKER__VALUE = eINSTANCE.getMarker_Value();

    /**
     * The meta object literal for the '{@link info.limpet.stackedcharts.model.impl.StylingImpl
     * <em>Styling</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see info.limpet.stackedcharts.model.impl.StylingImpl
     * @see info.limpet.stackedcharts.model.impl.StackedchartsPackageImpl#getStyling()
     * @generated
     */
    EClass STYLING = eINSTANCE.getStyling();

    /**
     * The meta object literal for the '<em><b>Marker Style</b></em>' attribute feature. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    EAttribute STYLING__MARKER_STYLE = eINSTANCE.getStyling_MarkerStyle();

    /**
     * The meta object literal for the '<em><b>Marker Size</b></em>' attribute feature. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    EAttribute STYLING__MARKER_SIZE = eINSTANCE.getStyling_MarkerSize();

    /**
     * The meta object literal for the '<em><b>Line Thickness</b></em>' attribute feature. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    EAttribute STYLING__LINE_THICKNESS = eINSTANCE.getStyling_LineThickness();

    /**
     * The meta object literal for the '<em><b>Line Style</b></em>' attribute feature. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    EAttribute STYLING__LINE_STYLE = eINSTANCE.getStyling_LineStyle();

    /**
     * The meta object literal for the '<em><b>Include In Legend</b></em>' attribute feature. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    EAttribute STYLING__INCLUDE_IN_LEGEND = eINSTANCE
        .getStyling_IncludeInLegend();

    /**
     * The meta object literal for the '{@link info.limpet.stackedcharts.model.impl.PlainStylingImpl
     * <em>Plain Styling</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see info.limpet.stackedcharts.model.impl.PlainStylingImpl
     * @see info.limpet.stackedcharts.model.impl.StackedchartsPackageImpl#getPlainStyling()
     * @generated
     */
    EClass PLAIN_STYLING = eINSTANCE.getPlainStyling();

    /**
     * The meta object literal for the '<em><b>Color</b></em>' attribute feature. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    EAttribute PLAIN_STYLING__COLOR = eINSTANCE.getPlainStyling_Color();

    /**
     * The meta object literal for the
     * '{@link info.limpet.stackedcharts.model.impl.LinearStylingImpl <em>Linear Styling</em>}'
     * class. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see info.limpet.stackedcharts.model.impl.LinearStylingImpl
     * @see info.limpet.stackedcharts.model.impl.StackedchartsPackageImpl#getLinearStyling()
     * @generated
     */
    EClass LINEAR_STYLING = eINSTANCE.getLinearStyling();

    /**
     * The meta object literal for the '<em><b>Start Color</b></em>' attribute feature. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    EAttribute LINEAR_STYLING__START_COLOR = eINSTANCE
        .getLinearStyling_StartColor();

    /**
     * The meta object literal for the '<em><b>End Color</b></em>' attribute feature. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    EAttribute LINEAR_STYLING__END_COLOR = eINSTANCE
        .getLinearStyling_EndColor();

    /**
     * The meta object literal for the '<em><b>Start Val</b></em>' attribute feature. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    EAttribute LINEAR_STYLING__START_VAL = eINSTANCE
        .getLinearStyling_StartVal();

    /**
     * The meta object literal for the '<em><b>End Val</b></em>' attribute feature. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    EAttribute LINEAR_STYLING__END_VAL = eINSTANCE.getLinearStyling_EndVal();

    /**
     * The meta object literal for the '{@link info.limpet.stackedcharts.model.impl.AbstractAxisImpl
     * <em>Abstract Axis</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see info.limpet.stackedcharts.model.impl.AbstractAxisImpl
     * @see info.limpet.stackedcharts.model.impl.StackedchartsPackageImpl#getAbstractAxis()
     * @generated
     */
    EClass ABSTRACT_AXIS = eINSTANCE.getAbstractAxis();

    /**
     * The meta object literal for the '<em><b>Scale</b></em>' attribute feature. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    EAttribute ABSTRACT_AXIS__SCALE = eINSTANCE.getAbstractAxis_Scale();

    /**
     * The meta object literal for the '<em><b>Name</b></em>' attribute feature. <!-- begin-user-doc
     * --> <!-- end-user-doc -->
     * 
     * @generated
     */
    EAttribute ABSTRACT_AXIS__NAME = eINSTANCE.getAbstractAxis_Name();

    /**
     * The meta object literal for the '<em><b>Auto Scale</b></em>' attribute feature. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    EAttribute ABSTRACT_AXIS__AUTO_SCALE = eINSTANCE
        .getAbstractAxis_AutoScale();

    /**
     * The meta object literal for the '<em><b>Direction</b></em>' attribute feature. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    EAttribute ABSTRACT_AXIS__DIRECTION = eINSTANCE.getAbstractAxis_Direction();

    /**
     * The meta object literal for the '<em><b>Font</b></em>' attribute feature. <!-- begin-user-doc
     * --> <!-- end-user-doc -->
     * 
     * @generated
     */
    EAttribute ABSTRACT_AXIS__FONT = eINSTANCE.getAbstractAxis_Font();

    /**
     * The meta object literal for the '<em><b>Color</b></em>' attribute feature. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    EAttribute ABSTRACT_AXIS__COLOR = eINSTANCE.getAbstractAxis_Color();

    /**
     * The meta object literal for the '<em><b>Axis Type</b></em>' containment reference feature.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    EReference ABSTRACT_AXIS__AXIS_TYPE = eINSTANCE.getAbstractAxis_AxisType();

    /**
     * The meta object literal for the
     * '{@link info.limpet.stackedcharts.model.impl.IndependentAxisImpl <em>Independent Axis</em>}'
     * class. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see info.limpet.stackedcharts.model.impl.IndependentAxisImpl
     * @see info.limpet.stackedcharts.model.impl.StackedchartsPackageImpl#getIndependentAxis()
     * @generated
     */
    EClass INDEPENDENT_AXIS = eINSTANCE.getIndependentAxis();

    /**
     * The meta object literal for the '<em><b>Annotations</b></em>' containment reference list
     * feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    EReference INDEPENDENT_AXIS__ANNOTATIONS = eINSTANCE
        .getIndependentAxis_Annotations();

    /**
     * The meta object literal for the '{@link info.limpet.stackedcharts.model.impl.ScatterSetImpl
     * <em>Scatter Set</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see info.limpet.stackedcharts.model.impl.ScatterSetImpl
     * @see info.limpet.stackedcharts.model.impl.StackedchartsPackageImpl#getScatterSet()
     * @generated
     */
    EClass SCATTER_SET = eINSTANCE.getScatterSet();

    /**
     * The meta object literal for the '<em><b>Datums</b></em>' containment reference list feature.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    EReference SCATTER_SET__DATUMS = eINSTANCE.getScatterSet_Datums();

    /**
     * The meta object literal for the '{@link info.limpet.stackedcharts.model.impl.DatumImpl
     * <em>Datum</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see info.limpet.stackedcharts.model.impl.DatumImpl
     * @see info.limpet.stackedcharts.model.impl.StackedchartsPackageImpl#getDatum()
     * @generated
     */
    EClass DATUM = eINSTANCE.getDatum();

    /**
     * The meta object literal for the '<em><b>Val</b></em>' attribute feature. <!-- begin-user-doc
     * --> <!-- end-user-doc -->
     * 
     * @generated
     */
    EAttribute DATUM__VAL = eINSTANCE.getDatum_Val();

    /**
     * The meta object literal for the '<em><b>Color</b></em>' attribute feature. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    EAttribute DATUM__COLOR = eINSTANCE.getDatum_Color();

    /**
     * The meta object literal for the
     * '{@link info.limpet.stackedcharts.model.impl.SelectiveAnnotationImpl <em>Selective
     * Annotation</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see info.limpet.stackedcharts.model.impl.SelectiveAnnotationImpl
     * @see info.limpet.stackedcharts.model.impl.StackedchartsPackageImpl#getSelectiveAnnotation()
     * @generated
     */
    EClass SELECTIVE_ANNOTATION = eINSTANCE.getSelectiveAnnotation();

    /**
     * The meta object literal for the '<em><b>Annotation</b></em>' containment reference feature.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    EReference SELECTIVE_ANNOTATION__ANNOTATION = eINSTANCE
        .getSelectiveAnnotation_Annotation();

    /**
     * The meta object literal for the '<em><b>Appears In</b></em>' reference list feature. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    EReference SELECTIVE_ANNOTATION__APPEARS_IN = eINSTANCE
        .getSelectiveAnnotation_AppearsIn();

    /**
     * The meta object literal for the '{@link info.limpet.stackedcharts.model.impl.AxisTypeImpl
     * <em>Axis Type</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see info.limpet.stackedcharts.model.impl.AxisTypeImpl
     * @see info.limpet.stackedcharts.model.impl.StackedchartsPackageImpl#getAxisType()
     * @generated
     */
    EClass AXIS_TYPE = eINSTANCE.getAxisType();

    /**
     * The meta object literal for the '{@link info.limpet.stackedcharts.model.impl.DateAxisImpl
     * <em>Date Axis</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see info.limpet.stackedcharts.model.impl.DateAxisImpl
     * @see info.limpet.stackedcharts.model.impl.StackedchartsPackageImpl#getDateAxis()
     * @generated
     */
    EClass DATE_AXIS = eINSTANCE.getDateAxis();

    /**
     * The meta object literal for the '<em><b>Date Format</b></em>' attribute feature. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    EAttribute DATE_AXIS__DATE_FORMAT = eINSTANCE.getDateAxis_DateFormat();

    /**
     * The meta object literal for the '{@link info.limpet.stackedcharts.model.impl.NumberAxisImpl
     * <em>Number Axis</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see info.limpet.stackedcharts.model.impl.NumberAxisImpl
     * @see info.limpet.stackedcharts.model.impl.StackedchartsPackageImpl#getNumberAxis()
     * @generated
     */
    EClass NUMBER_AXIS = eINSTANCE.getNumberAxis();

    /**
     * The meta object literal for the '<em><b>Number Format</b></em>' attribute feature. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    EAttribute NUMBER_AXIS__NUMBER_FORMAT = eINSTANCE
        .getNumberAxis_NumberFormat();

    /**
     * The meta object literal for the '<em><b>Auto Includes Zero</b></em>' attribute feature. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    EAttribute NUMBER_AXIS__AUTO_INCLUDES_ZERO = eINSTANCE
        .getNumberAxis_AutoIncludesZero();

    /**
     * The meta object literal for the '<em><b>Units</b></em>' attribute feature. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    EAttribute NUMBER_AXIS__UNITS = eINSTANCE.getNumberAxis_Units();

    /**
     * The meta object literal for the '{@link info.limpet.stackedcharts.model.impl.AngleAxisImpl
     * <em>Angle Axis</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see info.limpet.stackedcharts.model.impl.AngleAxisImpl
     * @see info.limpet.stackedcharts.model.impl.StackedchartsPackageImpl#getAngleAxis()
     * @generated
     */
    EClass ANGLE_AXIS = eINSTANCE.getAngleAxis();

    /**
     * The meta object literal for the '<em><b>Min Val</b></em>' attribute feature. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    EAttribute ANGLE_AXIS__MIN_VAL = eINSTANCE.getAngleAxis_MinVal();

    /**
     * The meta object literal for the '<em><b>Max Val</b></em>' attribute feature. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    EAttribute ANGLE_AXIS__MAX_VAL = eINSTANCE.getAngleAxis_MaxVal();

    /**
     * The meta object literal for the '<em><b>Mid Origin</b></em>' attribute feature. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    EAttribute ANGLE_AXIS__MID_ORIGIN = eINSTANCE.getAngleAxis_MidOrigin();

    /**
     * The meta object literal for the '<em><b>Red Green</b></em>' attribute feature. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    EAttribute ANGLE_AXIS__RED_GREEN = eINSTANCE.getAngleAxis_RedGreen();

    /**
     * The meta object literal for the '{@link info.limpet.stackedcharts.model.AxisScale <em>Axis
     * Scale</em>}' enum. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see info.limpet.stackedcharts.model.AxisScale
     * @see info.limpet.stackedcharts.model.impl.StackedchartsPackageImpl#getAxisScale()
     * @generated
     */
    EEnum AXIS_SCALE = eINSTANCE.getAxisScale();

    /**
     * The meta object literal for the '{@link info.limpet.stackedcharts.model.Orientation
     * <em>Orientation</em>}' enum. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see info.limpet.stackedcharts.model.Orientation
     * @see info.limpet.stackedcharts.model.impl.StackedchartsPackageImpl#getOrientation()
     * @generated
     */
    EEnum ORIENTATION = eINSTANCE.getOrientation();

    /**
     * The meta object literal for the '{@link info.limpet.stackedcharts.model.AxisDirection
     * <em>Axis Direction</em>}' enum. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see info.limpet.stackedcharts.model.AxisDirection
     * @see info.limpet.stackedcharts.model.impl.StackedchartsPackageImpl#getAxisDirection()
     * @generated
     */
    EEnum AXIS_DIRECTION = eINSTANCE.getAxisDirection();

    /**
     * The meta object literal for the '{@link info.limpet.stackedcharts.model.MarkerStyle
     * <em>Marker Style</em>}' enum. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see info.limpet.stackedcharts.model.MarkerStyle
     * @see info.limpet.stackedcharts.model.impl.StackedchartsPackageImpl#getMarkerStyle()
     * @generated
     */
    EEnum MARKER_STYLE = eINSTANCE.getMarkerStyle();

    /**
     * The meta object literal for the '{@link info.limpet.stackedcharts.model.LineType <em>Line
     * Type</em>}' enum. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see info.limpet.stackedcharts.model.LineType
     * @see info.limpet.stackedcharts.model.impl.StackedchartsPackageImpl#getLineType()
     * @generated
     */
    EEnum LINE_TYPE = eINSTANCE.getLineType();

    /**
     * The meta object literal for the '<em>Color</em>' data type. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @see java.awt.Color
     * @see info.limpet.stackedcharts.model.impl.StackedchartsPackageImpl#getColor()
     * @generated
     */
    EDataType COLOR = eINSTANCE.getColor();

  }

} // StackedchartsPackage
