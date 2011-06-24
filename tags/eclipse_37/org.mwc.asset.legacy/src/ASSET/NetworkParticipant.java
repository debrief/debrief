package ASSET;

import ASSET.Participants.Category;
import ASSET.Participants.Status;

public interface NetworkParticipant
{

	/**
	 * the status of this participant
	 */
	public abstract Status getStatus();

	/**
	 * the name of this participant
	 */
	public abstract String getName();

	/**
	 * get the id of this participant
	 */
	public abstract int getId();

	/**
	 * return what this participant is currently doing
	 */
	public abstract String getActivity();

	/**
	 * return the category of the target
	 */
	public abstract Category getCategory();

}