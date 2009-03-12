package ASSET.Models.Vessels;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 *
 * @author
 * @version 1.0
 */

public class FixedWing extends ASSET.Participants.CoreParticipant
{


  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public FixedWing(final int id)
  {
    this(id, null, null, null);
  }

  public FixedWing(final int id,
                   final ASSET.Participants.Status status,
                   final ASSET.Participants.DemandedStatus demStatus, final String name)
  {
    super(id, status, demStatus, name);
    this.setName("FixedWing");
  }
}