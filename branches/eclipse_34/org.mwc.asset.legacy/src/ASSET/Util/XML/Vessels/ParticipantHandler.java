package ASSET.Util.XML.Vessels;

import ASSET.Models.MovementType;
import ASSET.Util.XML.Decisions.WaterfallHandler;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author
 * @version 1.0
 */


abstract public class ParticipantHandler extends MWC.Utilities.ReaderWriter.XML.MWCXMLReader
{

  public static final String NAME = "Name";
  public static final String MONTE_CARLO_TARGET = "MonteCarloTarget";

  private String _myName;
  private boolean _isMonteCarlo = false;
  private int _myId = ASSET.ScenarioType.INVALID_ID;
  private ASSET.Participants.Category _myCategory;
  private ASSET.Participants.Status _myStatus;
  private ASSET.Participants.DemandedStatus _myDemandedStatus;
  private ASSET.Models.Sensor.SensorList _mySensorList;
  private ASSET.Models.DecisionType _myDecisionModel;
  private ASSET.Models.Vessels.Radiated.RadiatedCharacteristics _myRads;
  private ASSET.Models.Vessels.Radiated.RadiatedCharacteristics _mySelfNoise;

  // MOVEMENT CHARACTERISTICS - retrieved by child classes
  protected ASSET.Models.Movement.MovementCharacteristics _myMoveChars;
	protected MovementType _myMovement;

  ParticipantHandler(final String type)
  {
    // inform our parent what type of class we are
    super(type);

    super.addAttributeHandler(new HandleAttribute("id")
    {
      public void setValue(String name, final String val)
      {
        _myId = Integer.parseInt(val);
      }
    });

    super.addAttributeHandler(new HandleAttribute(NAME)
    {
      public void setValue(String name, final String val)
      {
        _myName = val;
      }
    });

    super.addAttributeHandler(new HandleBooleanAttribute(MONTE_CARLO_TARGET)
    {
      public void setValue(String name, final boolean val)
      {
        _isMonteCarlo = val;
      }
    });

    // add the readers for participant properties
    addHandler(new ASSET.Util.XML.Vessels.Util.CategoryHandler()
    {
      public void setCategory(final ASSET.Participants.Category cat)
      {
        _myCategory = cat;
      }
    });

    addHandler(new ASSET.Util.XML.Vessels.Util.StatusHandler()
    {
      public void setStatus(final ASSET.Participants.Status stat)
      {
        _myStatus = stat;
      }
    });
    addHandler(new ASSET.Util.XML.Vessels.Util.DemandedStatusHandler()
    {
      public void setDemandedStatus(final ASSET.Participants.DemandedStatus stat)
      {
        _myDemandedStatus = stat;
      }
    });
    addHandler(new ASSET.Util.XML.Sensors.SensorFitHandler()
    {
      public void setSensorFit(final ASSET.Models.Sensor.SensorList list)
      {
        _mySensorList = list;
      }
    });
    addHandler(new ASSET.Util.XML.Decisions.WaterfallHandler(WaterfallHandler.MAX_CHAIN_DEPTH)
    {
      public void setModel(final ASSET.Models.DecisionType chain)
      {
        _myDecisionModel = chain;
      }
    });
    addHandler(new ASSET.Util.XML.Decisions.SequenceHandler(WaterfallHandler.MAX_CHAIN_DEPTH)
    {
      public void setModel(final ASSET.Models.DecisionType chain)
      {
        _myDecisionModel = chain;
      }
    });
    addHandler(new ASSET.Util.XML.Decisions.SwitchHandler(WaterfallHandler.MAX_CHAIN_DEPTH)
    {
      public void setModel(final ASSET.Models.DecisionType chain)
      {
        _myDecisionModel = chain;
      }
    });


//    addHandler(new ASSET.Util.XML.Movement.MovementCharsHandler()
//    {
//      public void setMovement(final ASSET.Models.Movement.MovementCharacteristics chars)
//      {
//        _myMoves = chars;
//      }
//    });
    addHandler(new ASSET.Util.XML.Vessels.Util.RadiatedCharsHandler()
    {
      public void setRadiation(final ASSET.Models.Vessels.Radiated.RadiatedCharacteristics chars)
      {
        _myRads = chars;
      }
    });
    addHandler(new ASSET.Util.XML.Vessels.Util.SelfNoiseHandler()
    {
      public void setRadiation(final ASSET.Models.Vessels.Radiated.RadiatedCharacteristics chars)
      {
        _mySelfNoise = chars;
      }
    });
    addHandler(new ASSET.Util.XML.Vessels.Util.MovementHandler()
    {
      public void setMovement(final ASSET.Models.MovementType movement)
      {
        _myMovement = movement;
      }
    });    

  }

  private static int checkId(final int theID)
  {
    int res = theID;

    // if the index is zero, we will create one
    if (res == ASSET.Scenario.CoreScenario.INVALID_ID)
      res = ASSET.Util.IdNumber.generateInt();

    return res;
  }

  public void elementClosed()
  {
    _myId = checkId(_myId);

    // get this instance
    final ASSET.ParticipantType thisPart = getParticipant(_myId);

    // add in the attributes we have observed
    thisPart.setName(_myName);
    thisPart.setCategory(_myCategory);

    // update the id
    _myStatus.setId(_myId);

    thisPart.setStatus(_myStatus);
    thisPart.setDemandedStatus(_myDemandedStatus);
    if(_mySensorList != null)
    	thisPart.setSensorFit(_mySensorList);
    thisPart.setDecisionModel(_myDecisionModel);
    thisPart.setMovementChars(_myMoveChars);
    thisPart.setRadiatedChars(_myRads);
    thisPart.setSelfNoise(_mySelfNoise);
    
    if(_myMovement != null)
    	thisPart.setMovementModel(_myMovement);

    // allow the child classes to finish off the participant
    finishParticipant(thisPart);

    // store in the parent
    addThis(thisPart, _isMonteCarlo);

    // clear local vars
    _myId = ASSET.ScenarioType.INVALID_ID;
    _isMonteCarlo = false;
    _myName = null;
    _myCategory = null;
    _myStatus = null;
    _myDemandedStatus = null;
    _mySensorList = null;
    _myDecisionModel = null;
    _myMoveChars = null;
    _myRads = null;
    _mySelfNoise = null;

  }

  /** extra method provided to allow child classes to interrupt the participant
   * creation process
   */
  void finishParticipant(ASSET.ParticipantType newPart)
  {
  }

  abstract public void addThis(ASSET.ParticipantType part, boolean isMonteCarlo);

  abstract protected ASSET.ParticipantType getParticipant(int index);

  static void exportThis(final Object toExport, final org.w3c.dom.Element thisElement, final org.w3c.dom.Document doc)
  {

    final ASSET.ParticipantType part = (ASSET.ParticipantType) toExport;

    thisElement.setAttribute("Name", part.getName());

    // participant category
    ASSET.Util.XML.Vessels.Util.CategoryHandler.exportThis(part.getCategory(), thisElement, doc);

    // sensor list
    ASSET.Util.XML.Sensors.SensorFitHandler.exportThis(part, thisElement, doc);

    // current status
    ASSET.Util.XML.Vessels.Util.StatusHandler.exportThis(part.getStatus(), thisElement, doc);

    // current demanded status
    ASSET.Util.XML.Vessels.Util.DemandedStatusHandler.exportThis(part.getDemandedStatus(), thisElement, doc);

    // decision model
    final ASSET.Models.DecisionType dec = part.getDecisionModel();

    if (dec instanceof ASSET.Models.Decision.Switch)
      ASSET.Util.XML.Decisions.SwitchHandler.exportThis(dec, thisElement, doc);
    else if (dec instanceof ASSET.Models.Decision.Sequence)
      ASSET.Util.XML.Decisions.SequenceHandler.exportSequence(dec, thisElement, doc);
    else if (dec instanceof ASSET.Models.Decision.Waterfall)
      ASSET.Util.XML.Decisions.WaterfallHandler.exportThis(dec, thisElement, doc);

    // radiated noise characteristics
    ASSET.Util.XML.Vessels.Util.RadiatedCharsHandler.exportThis(part.getRadiatedChars(), thisElement, doc);

    // radiated noise characteristics
    ASSET.Util.XML.Vessels.Util.SelfNoiseHandler.exportThis(part.getSelfNoise(), thisElement, doc);

    // movement model
    final ASSET.Models.MovementType mover = part.getMovementModel();

    // start with the most specific instance first
    if (mover instanceof ASSET.Models.Movement.SSKMovement)
      ASSET.Util.XML.Movement.SSKMovementHandler.exportThis(mover, thisElement, doc);
    else if (mover instanceof ASSET.Models.Movement.CoreMovement)
      ASSET.Util.XML.Movement.MovementHandler.exportThis(mover, thisElement, doc);

  }


}