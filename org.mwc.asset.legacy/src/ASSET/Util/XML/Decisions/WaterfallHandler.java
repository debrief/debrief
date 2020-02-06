
package ASSET.Util.XML.Decisions;

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

import ASSET.Models.DecisionType;
import ASSET.Models.Decision.BehaviourList;
import ASSET.Models.Decision.Composite;
import ASSET.Models.Decision.CoreDecision;
import ASSET.Models.Decision.Sequence;
import ASSET.Models.Decision.UserControl;
import ASSET.Models.Decision.Waterfall;
import ASSET.Models.Decision.Movement.Evade;
import ASSET.Models.Decision.Movement.Move;
import ASSET.Models.Decision.Movement.RectangleWander;
import ASSET.Models.Decision.Movement.Trail;
import ASSET.Models.Decision.Movement.Transit;
import ASSET.Models.Decision.Movement.TransitWaypoint;
import ASSET.Models.Decision.Movement.Wander;
import ASSET.Models.Decision.Movement.WorkingTransit;
import ASSET.Models.Decision.Tactical.BearingTrail;
import ASSET.Models.Decision.Tactical.CircularDatumSearch;
import ASSET.Models.Decision.Tactical.DeferredBirth;
import ASSET.Models.Decision.Tactical.ExpandingSquareSearch;
import ASSET.Models.Decision.Tactical.Intercept;
import ASSET.Models.Decision.Tactical.Investigate;
import ASSET.Models.Decision.Tactical.MarkDip;
import ASSET.Models.Decision.Tactical.PatternSearch_InwardSpiral;
import ASSET.Models.Decision.Tactical.PatternSearch_Ladder;
import ASSET.Models.Decision.Tactical.PatternSearch_Ladder2;
import ASSET.Models.Decision.Tactical.PatternSearch_OutwardSpiral;
import ASSET.Models.Decision.Tactical.PatternSearch_Saw;
import ASSET.Models.Decision.Tactical.RaiseBody;
import ASSET.Models.Decision.Tactical.SSKRecharge;
import ASSET.Models.Decision.Tactical.SternArcClearance;
import ASSET.Models.Decision.Tactical.Wait;
import ASSET.Util.XML.Decisions.Tactical.CoreDecisionHandler;
import ASSET.Util.XML.Decisions.Tactical.InterceptHandler;
import ASSET.Util.XML.Decisions.Tactical.InvestigateHandler;
import MWC.Utilities.ReaderWriter.XML.MWCXMLReader;

abstract public class WaterfallHandler extends CoreDecisionHandler {

	private final static String type = "Waterfall";

	// we get into a problem, when we recursively add ChainHandlers to themselves.
	// control this, by only allowing Chains to nest 5 deep (at the fifth level,
	// don't add
	// the chain and sequence handlers)
	public final static int MAX_CHAIN_DEPTH = 6;

	public static int _thisChainDepth = 0;

	static public void exportThis(final Object toExport, final org.w3c.dom.Element parent,
			final org.w3c.dom.Document doc) {
		// create ourselves
		final org.w3c.dom.Element thisPart = doc.createElement(type);

		// get data item
		final ASSET.Models.Decision.BehaviourList bb = (ASSET.Models.Decision.BehaviourList) toExport;

		// output the paernt bits
		CoreDecisionHandler.exportThis((CoreDecision) bb, thisPart, doc);

		// thisPart.setAttribute("MIN_DEPTH", writeThis(bb.getMinDepth()));
		// step through the models
		final java.util.Iterator<DecisionType> it = bb.getModels().iterator();
		while (it.hasNext()) {
			final ASSET.Models.DecisionType dec = it.next();

			exportThisDecisionModel(dec, thisPart, doc);

		}

		parent.appendChild(thisPart);

	}

	protected static void exportThisDecisionModel(final ASSET.Models.DecisionType dec,
			final org.w3c.dom.Element thisPart, final org.w3c.dom.Document doc) {
		if (dec instanceof BearingTrail)
			BearingTrailHandler.exportThis(dec, thisPart, doc);
		else if (dec instanceof Trail)
			TrailHandler.exportThis(dec, thisPart, doc);
		else if (dec instanceof ExpandingSquareSearch)
			ExpandingSquareSearchHandler.exportThis(dec, thisPart, doc);
		else if (dec instanceof PatternSearch_Ladder)
			LadderSearchHandler.exportThis(dec, thisPart, doc);
		else if (dec instanceof PatternSearch_OutwardSpiral)
			PatternOutwardSpiral_SearchHandler.exportThis(dec, thisPart, doc);
		else if (dec instanceof PatternSearch_Saw)
			PatternSaw_SearchHandler.exportThis(dec, thisPart, doc);
		else if (dec instanceof PatternSearch_InwardSpiral)
			PatternInwardSpiral_SearchHandler.exportThis(dec, thisPart, doc);
		else if (dec instanceof PatternSearch_Ladder2)
			PatternLadder2_SearchHandler.exportThis(dec, thisPart, doc);
		else if (dec instanceof WorkingTransit)
			WorkingTransitHandler.exportThis(dec, thisPart, doc);
		else if (dec instanceof TransitWaypoint)
			TransitWaypointHandler.exportThis(dec, thisPart, doc);
		else if (dec instanceof Transit)
			TransitHandler.exportThis(dec, thisPart, doc);
		else if (dec instanceof Wait)
			WaitHandler.exportThis(dec, thisPart, doc);
		else if (dec instanceof DeferredBirth)
			DeferredBirthHandler.exportThis(dec, thisPart, doc);
		else if (dec instanceof MarkDip)
			MarkDipHandler.exportThis(dec, thisPart, doc);
		else if (dec instanceof Investigate)
			InvestigateHandler.exportThis(dec, thisPart, doc);
		else if (dec instanceof Evade)
			EvadeHandler.exportThis(dec, thisPart, doc);
		else if (dec instanceof SternArcClearance)
			SternArcClearanceHandler.exportThis(dec, thisPart, doc);
		else if (dec instanceof SSKRecharge)
			SSKRechargeHandler.exportThis(dec, thisPart, doc);
		else if (dec instanceof RectangleWander)
			RectangleWanderHandler.exportThis(dec, thisPart, doc);
		else if (dec instanceof Wander)
			WanderHandler.exportThis(dec, thisPart, doc);
		else if (dec instanceof Composite)
			CompositeHandler.exportThis(dec, thisPart, doc);
		else if (dec instanceof UserControl)
			UserControlHandler.exportThis(dec, thisPart, doc);
		else if (dec instanceof Move)
			MoveHandler.exportThis(dec, thisPart, doc);
		else if (dec instanceof CircularDatumSearch)
			CircularDatumSearchHandler.exportThis(dec, thisPart, doc);
		else if (dec instanceof Sequence)
			SequenceHandler.exportSequence(dec, thisPart, doc);
		else if (dec instanceof Waterfall)
			WaterfallHandler.exportThisDecisionModel(dec, thisPart, doc);
		else if (dec instanceof RaiseBody)
			RaiseBodyHandler.exportThis(dec, thisPart, doc);
		else if (dec instanceof Intercept)
			InterceptHandler.exportThis(dec, thisPart, doc);
	}

	protected BehaviourList _myList;

	public WaterfallHandler(final int thisDepth) {
		this(type, thisDepth);
	}

	public WaterfallHandler(final String title, final int thisDepth) {
		super(title);
		addHandlers(this, this, thisDepth);
		_myList = createNewList();
	}

	/**
	 * add the set of handlers to this object
	 */
	private void addHandlers(final MWCXMLReader list, final WaterfallHandler handler, int thisDepth) {
		if (thisDepth > 0) {
			list.addHandler(new PatternSaw_SearchHandler() {
				@Override
				public void setModel(final ASSET.Models.DecisionType dec) {
					handler.addModel(dec);
				}
			});
			list.addHandler(new PatternLadder2_SearchHandler() {
				@Override
				public void setModel(final ASSET.Models.DecisionType dec) {
					handler.addModel(dec);
				}
			});
			list.addHandler(new PatternInwardSpiral_SearchHandler() {
				@Override
				public void setModel(final ASSET.Models.DecisionType dec) {
					handler.addModel(dec);
				}
			});
			list.addHandler(new PatternOutwardSpiral_SearchHandler() {
				@Override
				public void setModel(final ASSET.Models.DecisionType dec) {
					handler.addModel(dec);
				}
			});
			list.addHandler(new LadderSearchHandler() {
				@Override
				public void setModel(final ASSET.Models.DecisionType dec) {
					handler.addModel(dec);
				}
			});
			list.addHandler(new ExpandingSquareSearchHandler() {
				@Override
				public void setModel(final ASSET.Models.DecisionType dec) {
					handler.addModel(dec);
				}
			});
			list.addHandler(new EvadeHandler() {
				@Override
				public void setModel(final ASSET.Models.DecisionType dec) {
					handler.addModel(dec);
				}
			});
			list.addHandler(new DetonationHandler() {
				@Override
				public void setModel(final ASSET.Models.DecisionType dec) {
					handler.addModel(dec);
				}
			});
			list.addHandler(new SSKRechargeHandler() {
				@Override
				public void setModel(final ASSET.Models.DecisionType dec) {
					handler.addModel(dec);
				}
			});
			list.addHandler(new RectangleWanderHandler() {
				@Override
				public void setModel(final ASSET.Models.DecisionType dec) {
					handler.addModel(dec);
				}
			});
			list.addHandler(new WanderHandler() {
				@Override
				public void setModel(final ASSET.Models.DecisionType dec) {
					handler.addModel(dec);
				}
			});
			list.addHandler(new LaunchWeaponHandler() {
				@Override
				public void setModel(final ASSET.Models.DecisionType dec) {
					handler.addModel(dec);
				}
			});
			list.addHandler(new WaitHandler() {
				@Override
				public void setModel(final ASSET.Models.DecisionType dec) {
					handler.addModel(dec);
				}
			});
			list.addHandler(new DeferredBirthHandler() {
				@Override
				public void setModel(final ASSET.Models.DecisionType dec) {
					handler.addModel(dec);
				}
			});
			list.addHandler(new TrailHandler() {
				@Override
				public void setModel(final ASSET.Models.DecisionType dec) {
					handler.addModel(dec);
				}
			});
			list.addHandler(new BearingTrailHandler() {
				@Override
				public void setModel(final ASSET.Models.DecisionType dec) {
					handler.addModel(dec);
				}
			});
			list.addHandler(new InterceptHandler() {
				@Override
				public void setModel(final ASSET.Models.DecisionType dec) {
					handler.addModel(dec);
				}
			});
			list.addHandler(new MarkDipHandler() {
				@Override
				public void setModel(final ASSET.Models.DecisionType dec) {
					handler.addModel(dec);
				}
			});
			list.addHandler(new TransitWaypointHandler() {
				@Override
				public void setModel(final ASSET.Models.DecisionType dec) {
					handler.addModel(dec);
				}
			});
			list.addHandler(new TransitHandler() {
				@Override
				public void setModel(final ASSET.Models.DecisionType dec) {
					handler.addModel(dec);
				}
			});
			list.addHandler(new SternArcClearanceHandler() {
				@Override
				public void setModel(final ASSET.Models.DecisionType dec) {
					handler.addModel(dec);
				}
			});
			list.addHandler(new CompositeHandler() {
				@Override
				public void setModel(final ASSET.Models.DecisionType dec) {
					handler.addModel(dec);
				}
			});
			list.addHandler(new CircularDatumSearchHandler() {
				@Override
				public void setModel(final ASSET.Models.DecisionType dec) {
					handler.addModel(dec);
				}
			});
			list.addHandler(new UserControlHandler() {
				@Override
				public void setModel(final ASSET.Models.DecisionType dec) {
					handler.addModel(dec);
				}
			});
			list.addHandler(new TerminateHandler() {
				@Override
				public void setModel(final ASSET.Models.DecisionType dec) {
					handler.addModel(dec);
				}
			});
			list.addHandler(new MoveHandler() {
				@Override
				public void setModel(final ASSET.Models.DecisionType dec) {
					handler.addModel(dec);
				}
			});
			list.addHandler(new RaiseBodyHandler() {
				@Override
				public void setModel(final ASSET.Models.DecisionType dec) {
					handler.addModel(dec);
				}
			});
			list.addHandler(new InvestigateHandler() {
				@Override
				public void setModel(final ASSET.Models.DecisionType dec) {
					handler.addModel(dec);
				}
			});

			_thisChainDepth++;

			list.addHandler(new WorkingTransitHandler(--thisDepth) {
				@Override
				public void setModel(final ASSET.Models.DecisionType dec) {
					handler.addModel(dec);
				}
			});
			list.addHandler(new SwitchHandler(--thisDepth) {
				@Override
				public void setModel(final ASSET.Models.DecisionType dec) {
					handler.addModel(dec);
				}
			});
			list.addHandler(new SequenceHandler(--thisDepth) {
				@Override
				public void setModel(final ASSET.Models.DecisionType dec) {
					handler.addModel(dec);
				}
			});
			list.addHandler(new WaterfallHandler(--thisDepth) {
				@Override
				public void setModel(final ASSET.Models.DecisionType dec) {
					handler.addModel(dec);
				}
			});
		}
	}

	public void addModel(final ASSET.Models.DecisionType dec) {
		_myList.insertAtFoot(dec);
	}

	protected BehaviourList createNewList() {
		return new Waterfall();
	}

	@Override
	public void elementClosed() {

		// setup the parent (or child) bits
		setAttributes((CoreDecision) _myList);

		// finally output it
		setModel(_myList);

		// and reset it, ready for the next chain
		_myList = createNewList();
	}

	abstract public void setModel(ASSET.Models.DecisionType dec);

}