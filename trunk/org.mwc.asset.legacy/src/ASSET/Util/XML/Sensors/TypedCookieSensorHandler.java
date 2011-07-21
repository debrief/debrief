package ASSET.Util.XML.Sensors;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author
 * @version 1.0
 */

import java.util.Vector;

import ASSET.Models.SensorType;
import ASSET.Models.Detection.DetectionEvent;
import ASSET.Models.Detection.DetectionEvent.DetectionStatePropertyEditor;
import ASSET.Models.Environment.EnvironmentType;
import ASSET.Models.Sensor.Cookie.TypedCookieSensor;
import ASSET.Models.Sensor.Cookie.TypedCookieSensor.TypedRangeDoublet;
import ASSET.Util.XML.Decisions.Util.TargetTypeHandler;
import MWC.GenericData.WorldDistance;
import MWC.Utilities.ReaderWriter.XML.Util.WorldDistanceHandler;

abstract public  class TypedCookieSensorHandler extends CoreSensorHandler
{

  private final static String type = "TypedCookieSensor";
  private final static String HAS_RANGE = "ProducesRange";
	private Vector<TypedRangeDoublet> _rangeDoublets;
	
  String _detectionLevel;
  protected final static String DETECTION_LEVEL = "DetectionLevel";
  
  int _medium = -1;
  private final static String MEDIUM = "Medium";
  boolean _produceRange = true;

  public static EnvironmentType.MediumPropertyEditor _myEditor =
    new EnvironmentType.MediumPropertyEditor();



  public TypedCookieSensorHandler()
  {
    super(type);

    addHandler(new TypedRangeDoubletHandler(){

			@Override
			public void setRangeDoublet(TypedRangeDoublet doublet)
			{
				if(_rangeDoublets == null)
					_rangeDoublets = new Vector<TypedRangeDoublet>();
				
				_rangeDoublets.add(doublet);
			}});
    
    addAttributeHandler(new HandleBooleanAttribute(HAS_RANGE)
    {
      public void setValue(String name, final boolean val)
      {
      	_produceRange = val;
      }
    });
    addAttributeHandler(new HandleAttribute(MEDIUM)
    {
      public void setValue(String name, final String val)
      {
        _myEditor.setValue(val);
        _medium = _myEditor.getIndex();
      }
    });
    
    addAttributeHandler(new HandleAttribute(DETECTION_LEVEL)
    {
      public void setValue(String name, final String val)
      {
        _detectionLevel = val;
      }
    });
    
  }
  protected SensorType getSensor(int myId, String myName)
  {
    Integer thisDetLevel = DetectionEvent.DETECTED;

    if (_detectionLevel != null)
    {
    	DetectionStatePropertyEditor detHandler = 
    		new DetectionEvent.DetectionStatePropertyEditor();
    	detHandler.setAsText(_detectionLevel);
      thisDetLevel = ((Integer) detHandler.getValue());
    }
    
    final ASSET.Models.Sensor.Cookie.TypedCookieSensor typedSensor = new TypedCookieSensor(myId, _rangeDoublets, thisDetLevel);
    typedSensor.setName(myName);
    
    typedSensor.setProducesRange(_produceRange);

    
    // do we have a medium
    if(_medium != -1)
    	typedSensor.setMedium(_medium);

    return typedSensor;
  }

  public void elementClosed()
  {
    super.elementClosed();

    _rangeDoublets = null;
    _detectionLevel = null;
    _medium = -1;
    _produceRange = true;
  }

  static public void exportThis(final Object toExport, final org.w3c.dom.Element parent,
                                final org.w3c.dom.Document doc)
  {
    // create ourselves
    final org.w3c.dom.Element thisPart = doc.createElement(type);
    parent.appendChild(thisPart);

    throw new RuntimeException("failed to implement export for TypedCookieSensorHandler");


  }
  
  static abstract public class TypedRangeDoubletHandler extends MWC.Utilities.ReaderWriter.XML.MWCXMLReader
  {
		private static final String DETECTION_RANGE = "DetectionRange";
		private static final String MY_TYPE = "TypedRangeDoublet";
		private Vector<String> _targetTypes;
		protected WorldDistance _detRange;


		public TypedRangeDoubletHandler()
		{
			super(MY_TYPE);
	    
	    this.addHandler(new TargetTypeHandler.TypeHandler()
			{
				public void addType(String attr)
				{
					if(_targetTypes == null)
						_targetTypes = new Vector<String>();
					_targetTypes.add(attr);
				}
			});	    
	    this.addHandler(new WorldDistanceHandler(DETECTION_RANGE){

				@Override
				public void setWorldDistance(WorldDistance res)
				{
					_detRange = res;
				}});
		}
		
	  public void elementClosed()
	  {
	  	
	  	TypedRangeDoublet res = new TypedRangeDoublet(_targetTypes, _detRange);
	    // pass to parent
	    setRangeDoublet(res);

	    // restart
	    _targetTypes = null;
	    _detRange = null;
	  }

	  abstract public void setRangeDoublet(TypedRangeDoublet doublet);

  	
  }
}