/*
 * Created by IntelliJ IDEA.
 * User: Ian.Mayo
 * Date: Apr 25, 2002
 * Time: 2:28:13 PM
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package MWC.GUI.Java3d;

import java.io.FileNotFoundException;

import javax.media.j3d.Group;
import javax.media.j3d.Node;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;

import com.sun.j3d.loaders.IncorrectFormatException;
import com.sun.j3d.loaders.ParsingErrorException;
import com.sun.j3d.loaders.Scene;
import com.sun.j3d.loaders.vrml97.VrmlLoader;
import com.sun.j3d.utils.geometry.Cone;

/**
 * class which creates and returns 3-d models
 */
public class ModelFactory
{
	// /////////////////////////
	// member variablse
	// /////////////////////////
	static ClassLoader _loader = null;

	/**
	 * the location of the 3-d models
	 */
	static final String _model_path = "images/";

	/**
	 * the sub-directory for low res
	 */
	static final String _low_path = "low_3d/";

	/**
	 * the sub-directory for high res
	 */
	static final String _high_path = "high_3d/";

	/**
	 * and a model factory to use...
	 */
	static ModelFactory _staticInstance = new ModelFactory();

	// /////////////////////////
	// constructor
	// /////////////////////////

	// /////////////////////////
	// member methods
	// /////////////////////////

	public static void init(ModelFactory target)
	{
		// did we receive a model factory?
		if (target != null)
		{
			// yup, overwrite it.
			_staticInstance = target;
		}

		if (_loader == null)
		{
			_loader = _staticInstance.getClass().getClassLoader();
		}
	}

	/**
	 * factory method.
	 * 
	 * @param type
	 *          String representing the type of vessel to create
	 * @param low_res
	 *          whether to use a high or low resolution model
	 */
	public static Node createThis(String type, boolean low_res)
	{
		init(null);

		Group res = null;

		String modelName = null;

		// first handle our special cases (which get renamed to another vessel type)
		if (type.equals("Destroyer"))
		{
			modelName = "frigate.wrl";
		}
		else if (type.equals("Missile"))
		{
			modelName = "torpedo.wrl";
		}

		// shall we have a go at automatically guessing the name?
		if (modelName == null)
		{
			modelName = type.toLowerCase() + ".wrl";
		}

		if (modelName != null)
		{
			// prepend the path to the image
			String pre_path = _model_path;

			if (low_res)
				pre_path += _low_path;
			else
				pre_path += _high_path;

			pre_path += modelName;

			// ok, now create the reader
			VrmlLoader oFile = new VrmlLoader(VrmlLoader.LOAD_ALL);

			Scene scene = null;

			// ok, everything is collated - get it done...
			try
			{
				java.net.URL imLoc = _staticInstance.getURLFor(pre_path, _loader);
				if (imLoc != null)
				{
					scene = oFile.load(imLoc);
				}
			}
			catch (FileNotFoundException e)
			{
				System.out.println("not found!");
			}
			catch (IncorrectFormatException e)
			{
				System.out.println("incorrect format!");
			}
			catch (ParsingErrorException e)
			{
				System.out.println("parsing exception!");
			}

			// check if we were able to create the model
			if (scene != null)
				res = scene.getSceneGroup();
		}

		// ok, final fallback. If we haven't found a symbol at all, just load a cone
		// - which points in the
		// direction of travel
		if (res == null)
		{
			// create the transform group which will hold our symbol and the transform
			TransformGroup tg = new TransformGroup();

			// create the cone, used to represent things we couldn't find a model for
			Cone theCone = new Cone();
			res = theCone;

			// and the transform (which rotates the cone from facing upwards to facing
			// across -
			// the standard orientation for our models)
			Transform3D t3 = new Transform3D();
			t3.rotZ(-Math.PI / 2);

			// and put them together
			tg.setTransform(t3);
			tg.addChild(theCone);

			res = tg;
		}

		return res;
	}

	/**
	 * @param pre_path
	 * @param loader
	 * @return
	 */
	public java.net.URL getURLFor(String pre_path, java.lang.ClassLoader loader)
	{
		java.net.URL imLoc = loader.getResource(pre_path);
		return imLoc;
	}
}
