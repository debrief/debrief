package ASSET.Models.Vessels;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author
 * @version 1.0
 */

public class SSN extends ASSET.Participants.CoreParticipant {

  /** default BB rad noise
   *
   */
  static final public double DEFAULT_BB_NOISE = 134;

  /** default periscope depth
   *
   */
  static final public double PERISCOPE_DEPTH = 18;

  public SSN(final int id) {
    this(id, null,null,null);
  }

  public SSN(final int id, final ASSET.Participants.Status status, final ASSET.Participants.DemandedStatus demStatus, final String name)
  {
    super(id, status, demStatus, name);
  }
}