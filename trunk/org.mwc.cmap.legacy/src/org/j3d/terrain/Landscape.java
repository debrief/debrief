/*****************************************************************************
 *                      Modified version (c) j3d.org 2002
 *                               Java Source
 *
 * This source is licensed under the GNU LGPL v2.1
 * Please read http://www.gnu.org/copyleft/lgpl.html for more information
 *
 ****************************************************************************/

package org.j3d.terrain;

// Standard imports
import javax.media.j3d.Transform3D;
import javax.vecmath.Matrix3f;
import javax.vecmath.Tuple3f;
import javax.vecmath.Vector3f;

import org.j3d.ui.navigation.FrameUpdateListener;
import org.j3d.ui.navigation.HeightMapGeometry;

/**
 * Representation of a piece of rendered terrain data.
 * <p>
 *
 * The landscape is used to control what it rendered on screen as the user
 * moves about the virtual environment. This instance does not need to maintain
 * all the polygons on the screen at any one time, but may control them as
 * needed.
 * <p>
 *
 * This object is independent of the culling algorithm. It represents something
 * that can be placed in a scenegraph and have view information passed to it
 * without the need to know the specific algorithm in use. To implement a
 * specific algorithm (eg ROAM) you would extend this class and implement the
 * {@link #setView(Tuple3f, Vector3f)} method. Every time that the scene
 * changes, you will be notified by this method. That means you should perform
 * any culling/LOD and update the scene graph at this point. This will be
 * called at most once per frame.
 * <p>
 *
 * For convenience, this class also implements {@link FrameUpdateListener} from
 * the {@link org.j3d.ui.navigation} package so that you can have fast, quick
 * navigation implementation in your code. If you wish to use your own custom
 * user input code, then there is no penalty for doing so. Simply call one of
 * the <code>setView()</code> methods directly with the transformation
 * information.
 * <p>
 *
 * If you are going to use this class with the navigation code, then you
 * should also make the internal geometry not pickable, and make this item
 * pickable. In this way, the navigation code will find this top-level
 * terrain definition and use it directly to make the code much faster. None
 * of these capabilities are set within this implementation, so it is up to
 * the third-party code to make it so via calls to the appropriate methods.
 *
 * @author Justin Couch, based on original ideas from Paul Byrne
 * @version $Revision: 1.1.1.1 $
 */
public abstract class Landscape extends javax.media.j3d.BranchGroup
    implements FrameUpdateListener, HeightMapGeometry
{
    /** The current viewing frustum that is seeing the landscape */
    protected ViewFrustum landscapeView;

    /** Raw terrain information to be rendered */
    protected TerrainData terrainData;

    /**
     * Temporary variable to hold the position information extracted from
     * the full transform class.
     */
    private Vector3f tmpPosition;

    /**
     * Temporary variable to hold the orientation information extracted from
     * the matrix class.
     */
    private Vector3f tmpOrientation;

    /**
     * Temporary variable to hold the orientation matrix extracted from
     * the full transform class.
     */
    private Matrix3f tmpMatrix;

    /**
     * Create a new Landscape with the set view and data. If either are not
     * provided, an exception is thrown.
     *
     * @param view The viewing frustum to see the data with
     * @param data The raw data to view
     * @throws IllegalArgumentException either parameter is null
     */
    public Landscape(ViewFrustum view, TerrainData data)
    {
        if(view == null)
            throw new IllegalArgumentException("ViewFrustum not supplied");

        if(data == null)
            throw new IllegalArgumentException("Terrain data not supplied");

        terrainData = data;
        landscapeView = view;

        tmpPosition = new Vector3f();
        tmpOrientation = new Vector3f();
        tmpMatrix = new Matrix3f();
    }

    //----------------------------------------------------------
    // Methods required by FrameUpdateListener
    //----------------------------------------------------------

    /**
     * The transition from one point to another is completed. Use this to
     * update the transformation.
     *
     * @param t3d The position of the final viewpoint
     */
    public void transitionEnded(Transform3D t3d)
    {
        landscapeView.viewingPlatformMoved();
        setView(t3d);
    }

    /**
     * The frame has just been updated with the latest view information.
     * Update the landscape rendered values now.
     *
     * @param t3d The position of the viewpoint now
     */
    public void viewerPositionUpdated(Transform3D t3d)
    {
        landscapeView.viewingPlatformMoved();
        setView(t3d);
    }

    //----------------------------------------------------------
    // Methods required by FrameUpdateListener
    //----------------------------------------------------------

    /**
     * Get the height at the given X,Z coordinate in the local coordinate
     * system. This implementation delegates to the underlying terrain data
     * to do the real resolution.
     *
     * @param x The x coordinate for the height sampling
     * @param z The z coordinate for the height sampling
     * @return The height at the current point or NaN
     */
    public float getHeight(float x, float z)
    {
        return terrainData.getHeight(x, z);
    }

    //----------------------------------------------------------
    // Local methods
    //----------------------------------------------------------

    /**
     * Set the current viewing direction for the user. The user is located
     * at the given point and looking in the given direction. All information
     * is assumed to be in world coordinates.
     *
     * @param position The position the user is in the virtual world
     * @param direction The orientation of the user's gaze
     */
    public abstract void setView(Tuple3f position, Vector3f direction);

    /**
     * Set the current view location information based on a transform matrix.
     * Only the position and orientation information are extracted from this
     * matrix. Any shear or scale is ignored. Effectively, this transform
     * should be the view transform (particularly if you are using navigation
     * code from this codebase in the {@link org.j3d.ui.navigation} package.
     *
     * @param t3d The transform to use as the view position
     */
    public void setView(Transform3D t3d)
    {
        t3d.get(tmpMatrix, tmpPosition);
        tmpOrientation.set(0, 0, -1);
        tmpMatrix.transform(tmpOrientation);

        setView(tmpPosition, tmpOrientation);
    }
}
