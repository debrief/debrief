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
package ASSET.Util.XML.Control.Observers;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;

import ASSET.Models.Decision.TargetType;
import ASSET.Scenario.Observers.ScenarioObserver;
import ASSET.Scenario.Observers.Recording.AISObserver;
import ASSET.Scenario.Observers.Recording.DebriefFormatHelperHandler;
import ASSET.Util.XML.Decisions.Util.TargetTypeHandler;

/**
 * read in a debrief replay observer from file
 */
abstract class AISObserverHandler extends CoreFileObserverHandler
{

  private final static String type = "AISObserver";

  private boolean _recordDetections = false;
  private boolean _recordPositions = false;
  private TargetType _targetType = null;
  final private List<String> _formatHelpers = new ArrayList<String>();
  private String _subjectSensor = null;

  private static final String RECORD_DETECTIONS = "record_detections";
  private static final String RECORD_POSITIONS = "record_positions";
  private static final String TARGET_TYPE = "SubjectToTrack";
  private static final String SUBJECT_SENSOR = "SubjectSensor";

  public AISObserverHandler(String type)
  {
    super(type);

    addAttributeHandler(new HandleBooleanAttribute(RECORD_DETECTIONS)
    {
      public void setValue(String name, final boolean val)
      {
        _recordDetections = val;
      }
    });
    addAttributeHandler(new HandleAttribute(SUBJECT_SENSOR)
    {
      public void setValue(String name, final String val)
      {
        _subjectSensor = val;
      }
    });
    addAttributeHandler(new HandleBooleanAttribute(RECORD_POSITIONS)
    {
      public void setValue(String name, final boolean val)
      {
        _recordPositions = val;
      }
    });

    addHandler(new TargetTypeHandler(TARGET_TYPE)
    {
      public void setTargetType(TargetType type1)
      {
        _targetType = type1;
      }
    });
    addHandler(new DebriefFormatHelperHandler()
    {
      @Override
      public void storeMe(final String text)
      {
        _formatHelpers.add(text);
      }
    });
  }

  public AISObserverHandler()
  {
    this(type);
  }

  public void elementClosed()
  {
    // create ourselves
    final AISObserver debriefObserver =
        getObserver(_name, _isActive, _recordDetections,
            _recordPositions, _targetType, _formatHelpers);
    
    if(_subjectSensor != null)
    {
      debriefObserver.setSubjectSensor(_subjectSensor);
    }
    
    setObserver(debriefObserver);

    // close the parenet
    super.elementClosed();

    // and clear the data
    _recordDetections = false;
    _recordPositions = true;
    _targetType = null;
    _subjectSensor = null;
    
    // and clear the format helpers
    _formatHelpers.clear();
  }

  protected AISObserver getObserver(String name, boolean isActive,
      boolean recordDetections, boolean recordPositions, TargetType subject,
      List<String> formatHelpers)
  {
    return new AISObserver(_directory, _fileName, recordDetections,
        recordPositions, subject, name, isActive);
  }

  abstract public void setObserver(ScenarioObserver obs);

  static public void exportThis(final Object toExport,
      final Element parent, final org.w3c.dom.Document doc)
  {
    // create ourselves
    final Element thisPart = doc.createElement(type);

    // get data item
    final AISObserver bb = (AISObserver) toExport;

    // output the parent ttributes
    CoreFileObserverHandler.exportThis(bb, thisPart);

    // output it's attributes
    thisPart.setAttribute(RECORD_DETECTIONS,
        writeThis(bb.getRecordDetections()));
    thisPart.setAttribute(RECORD_POSITIONS, writeThis(bb.getRecordPositions()));
    if (bb.getSubjectToTrack() != null)
    {
      TargetTypeHandler.exportThis(TARGET_TYPE, bb.getSubjectToTrack(),
          thisPart, doc);
    }

    // output it's attributes
    parent.appendChild(thisPart);

  }

}