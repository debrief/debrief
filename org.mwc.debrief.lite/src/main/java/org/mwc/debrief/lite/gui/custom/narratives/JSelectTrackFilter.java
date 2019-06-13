package org.mwc.debrief.lite.gui.custom.narratives;

import java.util.ArrayList;
import java.util.HashSet;

import javax.swing.JList;

import org.mwc.debrief.lite.gui.custom.AbstractSelection;
import org.mwc.debrief.lite.gui.custom.JPopupList;

import MWC.TacticalData.NarrativeEntry;

public class JSelectTrackFilter extends JPopupList<TrackNameColor>
{

  /**
   * 
   */
  private static final long serialVersionUID = 7136974124331608166L;

  final AbstractNarrativeConfiguration _model;

  public JSelectTrackFilter(final AbstractNarrativeConfiguration model)
  {
    super(new TrackPanelItemRenderer(), createTrackFilters(model));
    this._model = model;
  }

  public static JList<TrackNameColor> createTrackFilters(
      final AbstractNarrativeConfiguration model)
  {
    final ArrayList<TrackNameColor> differentTrackNames = new ArrayList<>();
    final HashSet<String> addedTracknames = new HashSet<>();
    for (AbstractSelection<NarrativeEntry> narrative : model.getNarratives())
    {
      if (!addedTracknames.contains(narrative.getItem().getTrackName()))
      {
        addedTracknames.add(narrative.getItem().getTrackName());
        differentTrackNames.add(new TrackNameColor(narrative.getItem()
            .getTrackName(), narrative.getItem().getColor()));
      }
    }

    return new JList<>(differentTrackNames.toArray(new TrackNameColor[]
    {}));
  }
}
