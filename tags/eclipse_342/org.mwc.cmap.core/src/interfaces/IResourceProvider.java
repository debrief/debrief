/**
 * 
 */
package interfaces;

import org.eclipse.core.resources.IResource;

/**
 * @author ian.mayo
 *
 */


public interface IResourceProvider
{
	/** get the file location representing where this items is stored
	 * 
	 * @return
	 */
	public IResource getResource();
}
