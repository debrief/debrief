package org.mwc.debrief.dis.providers;

import edu.nps.moves.dis.CollisionPdu;
import edu.nps.moves.dis.DetonationPdu;
import edu.nps.moves.dis.EntityStatePdu;
import edu.nps.moves.dis.FirePdu;
import edu.nps.moves.dis.Pdu;
import edu.nps.moves.dis.SimulationManagementFamilyPdu;

/**
 * collate a set of optional filters
 * 
 * @author ian
 * 
 */
public class DISFilters
{
  final private Integer siteId;
  final private Integer appId;
  final private Integer exId;
  final private boolean hasFilter;

  public DISFilters(final String app, final String site, final String ex)
  {
    if (app != null && app.length() > 0)
      appId = Integer.parseInt(app);
    else
      appId = null;
    if (site != null && site.length() > 0)
      siteId = Integer.parseInt(site);
    else
      siteId = null;
    if (ex != null && ex.length() > 0)
      exId = Integer.parseInt(ex);
    else
      exId = null;

    hasFilter = appId != null || siteId != null || exId != null;
  }

  /**
   * determine if this PDU matches our filters
   * 
   * @param pdu
   * @return
   */
  public boolean accepts(Pdu pdu)
  {
    if (hasFilter)
    {
      if (pdu instanceof SimulationManagementFamilyPdu)
      {
        SimulationManagementFamilyPdu sim = (SimulationManagementFamilyPdu) pdu;
        if (siteId != null && siteId != sim.getOriginatingEntityID().getSite())
        {
          return false;
        }
        if (appId != null
            && appId != sim.getOriginatingEntityID().getApplication())
        {
          return false;
        }
        if (exId != null && exId != sim.getExerciseID())
        {
          return false;
        }
      }
      else if (pdu instanceof EntityStatePdu)
      {
        EntityStatePdu sd = (EntityStatePdu) pdu;
        if (siteId != null && siteId != sd.getEntityID().getSite())
        {
          return false;
        }
        if (appId != null && appId != sd.getEntityID().getApplication())
        {
          return false;
        }
        if (exId != null && exId != sd.getExerciseID())
        {
          return false;
        }
      }
      else if (pdu instanceof DetonationPdu)
      {
        DetonationPdu sd = (DetonationPdu) pdu;
        if (siteId != null && siteId != sd.getFiringEntityID().getSite())
        {
          return false;
        }
        if (appId != null && appId != sd.getFiringEntityID().getApplication())
        {
          return false;
        }
        if (exId != null && exId != sd.getExerciseID())
        {
          return false;
        }
      }
      else if (pdu instanceof FirePdu)
      {
        FirePdu sd = (FirePdu) pdu;
        if (siteId != null && siteId != sd.getFiringEntityID().getSite())
        {
          return false;
        }
        if (appId != null && appId != sd.getFiringEntityID().getApplication())
        {
          return false;
        }
        if (exId != null && exId != sd.getExerciseID())
        {
          return false;
        }
      }
      else if (pdu instanceof CollisionPdu)
      {
        CollisionPdu sd = (CollisionPdu) pdu;
        if (siteId != null && siteId != sd.getIssuingEntityID().getSite())
        {
          return false;
        }
        if (appId != null && appId != sd.getIssuingEntityID().getApplication())
        {
          return false;
        }
        if (exId != null && exId != sd.getExerciseID())
        {
          return false;
        }
      }
      else
      {
        System.err.println("couldn't check:" + pdu);
      }
    }
    return true;
  }
}