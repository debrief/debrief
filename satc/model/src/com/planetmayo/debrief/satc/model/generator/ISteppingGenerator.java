package com.planetmayo.debrief.satc.model.generator;

/**
 * interface for how to run a stepping data generator
 * 
 * @author ian
 * 
 */
public interface ISteppingGenerator
{
	/**
	 * restart the set of contributions
	 * 
	 */
	public void restart();

	/**
	 * run through the remaining contributions
	 * 
	 */
	public void run();

	/**
	 * move to the next contribution
	 * 
	 */
	public void step();
}
