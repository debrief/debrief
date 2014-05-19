package com.borlander.rac525791.dashboard;

/**
 * 
 * This class takes 2 integers 0 &lt; i, j &lt; RANGE = 1000 and autoscales them
 * as decsribed at the
 * http://pml.clientsection.com/projects/57954/msg/cat/397859/3726984/comments.
 * 
 * It is intended to be the mediator between dasboard figure and its speed/depth
 * pointer layers.
 * 
 */
public class AutoScaler {
	public static final int RANGE = 1000;

	private int myActual;

	private int myDemanded;
	
	private int myMultiplier = 1;
	
	/**
	 * @return true if scale should be changed in responce to this change.
	 */
	public boolean setActual(int actual){
		checkRange(actual);
		myActual = actual;
		return updateMultiplier();
	}
	
	public boolean setDemanded(int demanded){
		checkRange(demanded);
		myDemanded = demanded;
		return updateMultiplier();
	}
	
	public int getScaledActual(){
		return myActual * getScaleFactor();
	}
	
	public int getScaledDemanded(){
		return myDemanded * getScaleFactor();
	}
	
	/**
	 * @return the value that should be show at the "unit multipliers" control
	 */
	public int getMultiplier(){
		return myMultiplier;
	}
	
	/**
	 * @return internal scale factor that allows to map the max of
	 *         demanded/actual values into the range of [100-1000] (if
	 *         possible).
	 */
	private int getScaleFactor(){
		//to get explanation of this, please check the table at the 
		//http://pml.clientsection.com/projects/57954/msg/cat/397859/3726984/comments 
		return 100 / myMultiplier; 
	}
	
	private boolean updateMultiplier(){
		int newMultiplier = computeMultiplier(myActual, myDemanded);
		boolean changed = (newMultiplier != myMultiplier);
		myMultiplier = newMultiplier;
		return changed;
	}
	
	private static int computeMultiplier(int actual, int demanded){
		int max = Math.max(actual, demanded);
		if (max <= 10){
			return 1; 
		}
		if (max <= 100){
			return 10;
		}
		return 100;
	}

	private void checkRange(int unscaledValue) {
		if (unscaledValue < 0 || unscaledValue > RANGE) {
			throw new IllegalArgumentException(//
					"Expected value from 0 to " + RANGE + ", actual: " + unscaledValue);
		}
	}
}
