package interfaces;

import MWC.Algorithms.EarthModel;

/** 
 * Interface for classes which are capable of providing Earth model.
 */
public interface IEarthModelProvider {
	
	public EarthModel getEarthModel();

}
