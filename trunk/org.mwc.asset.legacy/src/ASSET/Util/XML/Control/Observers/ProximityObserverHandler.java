package ASSET.Util.XML.Control.Observers;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author
 * @version 1.0
 */

import ASSET.Models.Decision.TargetType;
import ASSET.Scenario.Observers.ProximityObserver;
import ASSET.Scenario.Observers.Summary.BatchCollator;
import MWC.GenericData.WorldDistance;
import MWC.Utilities.ReaderWriter.XML.Util.WorldDistanceHandler;

abstract class ProximityObserverHandler extends MWC.Utilities.ReaderWriter.XML.MWCXMLReader
{

  private final static String type = "ProximityObserver";

  private final static String ACTIVE = "Active";

  private final static String TARGET_TYPE = "Target";
  private final static String WATCH_TYPE = "Watch";

  protected TargetType _watchType = null;
  protected TargetType _targetType = null;
  protected boolean _isActive;
  protected String _name = "Proximity Observer";


  protected BatchCollatorHandler _myBatcher = new BatchCollatorHandler();

  public ProximityObserverHandler()
  {
    this(type);
  }

  public ProximityObserverHandler(String type)
  {
    super(type);

    addAttributeHandler(new HandleBooleanAttribute(ACTIVE)
    {
      public void setValue(String name, final boolean val)
      {
        _isActive = val;
      }
    });

    addAttributeHandler(new HandleAttribute("Name")
    {
      public void setValue(String name, final String val)
      {
        _name = val;
      }
    });

    addHandler(new TargetHandler(TARGET_TYPE)
    {
      public void setTargetType(final TargetType type1)
      {
        _targetType = type1;
      }
    });

    addHandler(new TargetHandler(WATCH_TYPE)
    {
      public void setTargetType(final TargetType type1)
      {
        _watchType = type1;
      }
    });


    addHandler(_myBatcher);
  }

  public void elementClosed()
  {
    // create ourselves
    final BatchCollator timeO = new ProximityObserver(_watchType, _targetType, _name, _isActive);



    // (possibly) set batch collation results
    _myBatcher.setData(timeO);


    setObserver(timeO);

    // and reset
    _name = "Proximity Observer";
    _targetType = null;
    _watchType = null;
  }


  abstract public void setObserver(BatchCollator obs);

  static public void exportThis(final Object toExport, final org.w3c.dom.Element parent,
                                final org.w3c.dom.Document doc)
  {
    // create ourselves
    final org.w3c.dom.Element thisPart = doc.createElement(type);

    // get data item
    final ProximityObserver bb = (ProximityObserver) toExport;

    // output it's attributes
    thisPart.setAttribute("Name", bb.getName());
    thisPart.setAttribute(ACTIVE, writeThis(bb.isActive()));

    TargetHandler.exportThis(bb.getTargetType(), thisPart, doc, TARGET_TYPE);
    TargetHandler.exportThis(bb.getWatchType(), thisPart, doc, WATCH_TYPE);

    // output it's attributes
    parent.appendChild(thisPart);

  }


  ////////////////////////////////////////////////////////////
  // AND THE STOP ON PROXIMITY HANDLER
  ////////////////////////////////////////////////////////////
  public static abstract class StopOnProximityHandler extends ProximityObserverHandler
  {
    public final static String THIS_TYPE = "StopOnProximityObserver";
    public final static String STOP_RANGE = "Range";


    protected WorldDistance _thisDist = null;

    public StopOnProximityHandler()
    {
      super(THIS_TYPE);

      // and add our extra handler
      addHandler(new WorldDistanceHandler(STOP_RANGE)
      {
        public void setWorldDistance(WorldDistance res)
        {
          //To change body of implemented methods use File | Settings | File Templates.
          _thisDist = res;
        }
      });

    }

    public void elementClosed()
    {
      // create ourselves
      final BatchCollator timeO = new ProximityObserver.StopOnProximityObserver(_watchType, _targetType, _thisDist, _name, _isActive);

      // (possibly) set batch collation results
      _myBatcher.setData(timeO);

      setObserver(timeO);

      // and reset
      _name = null;
      _targetType = null;
      _watchType = null;
      _thisDist = null;
    }


    static public void exportThis(final Object toExport, final org.w3c.dom.Element parent,
                                  final org.w3c.dom.Document doc)
    {
      // create ourselves
      final org.w3c.dom.Element thisPart = doc.createElement(type);

      // get data item
      final ProximityObserver.StopOnProximityObserver bb = (ProximityObserver.StopOnProximityObserver) toExport;

      // output it's attributes
      thisPart.setAttribute("Name", bb.getName());
      thisPart.setAttribute(ACTIVE, writeThis(bb.isActive()));

      TargetHandler.exportThis(bb.getTargetType(), thisPart, doc, TARGET_TYPE);
      TargetHandler.exportThis(bb.getWatchType(), thisPart, doc, WATCH_TYPE);
      WorldDistanceHandler.exportDistance(STOP_RANGE, bb.getRange(), thisPart, doc);

      // output it's attributes
      parent.appendChild(thisPart);

    }

  }
}