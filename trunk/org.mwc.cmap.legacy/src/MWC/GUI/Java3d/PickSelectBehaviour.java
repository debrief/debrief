/*
* Created by IntelliJ IDEA.
* User: Ian.Mayo
* Date: Apr 9, 2002
* Time: 3:45:31 PM
* To change template for new class use
* Code Style | Class Templates options (Tools | IDE Options).
*/
package MWC.GUI.Java3d;

import com.sun.j3d.utils.picking.behaviors.PickMouseBehavior;
import com.sun.j3d.utils.picking.behaviors.PickingCallback;
import com.sun.j3d.utils.picking.PickTool;
import com.sun.j3d.utils.picking.PickResult;
import com.sun.j3d.utils.behaviors.mouse.MouseBehaviorCallback;
import com.sun.j3d.utils.behaviors.mouse.MouseRotate;

import javax.media.j3d.*;


  /**
   * A mouse behavior that allows user to pick and drag scene graph objects.
   * Common usage:
   * <p>
   * 1. Create your scene graph.
   * <p>
   * 2. Create this behavior with root and canvas.
   * <p>
   * <blockquote><pre>
   *	PickRotateBehavior behavior = new PickRotateBehavior(canvas, root, bounds);
   *      root.addChild(behavior);
   * </pre></blockquote>
   * <p>
   * The above behavior will monitor for any picking events on
   * the scene graph (below root node) and handle mouse drags on pick hits.
   * Note the root node can also be a subgraph node of the scene graph (rather
   * than the topmost).
   */

public class PickSelectBehaviour extends PickMouseBehavior
{
    int pickMode = PickTool.BOUNDS;
    private PickingCallback callback=null;
    private TransformGroup currentTG;

    private SelectionListener _myListener;


    /**
     * Creates a pick/rotate behavior that waits for user mouse events for
     * the scene graph. This method has its pickMode set to BOUNDS picking.
     * @param root   Root of your scene graph.
     * @param canvas Java 3D drawing canvas.
     * @param bounds Bounds of your scene.
     **/

    public PickSelectBehaviour(BranchGroup root, Canvas3D canvas, Bounds bounds){
      super(canvas, root, bounds);
      this.setSchedulingBounds(bounds);
    }

    /**
     * Creates a pick/rotate behavior that waits for user mouse events for
     * the scene graph.
     * @param root   Root of your scene graph.
     * @param canvas Java 3D drawing canvas.
     * @param bounds Bounds of your scene.
     * @param pickMode specifys PickObject.USE_BOUNDS or PickObject.USE_GEOMETRY.
     * Note: If pickMode is set to PickObject.USE_GEOMETRY, all geometry object in
     * the scene graph that allows pickable must have its ALLOW_INTERSECT bit set.
     **/

    public PickSelectBehaviour(BranchGroup root, Canvas3D canvas, Bounds bounds,
                               int pickMode){
      super(canvas, root, bounds);
      this.setSchedulingBounds(bounds);
      this.pickMode = pickMode;
    }


    /**
     * Update the scene to manipulate any nodes. This is not meant to be
     * called by users. Behavior automatically calls this. You can call
     * this only if you know what you are doing.
     *
     * @param xpos Current mouse X pos.
     * @param ypos Current mouse Y pos.
     **/
    public void updateScene(int xpos, int ypos)
    {
      TransformGroup tg = null;

      if(mevent.getClickCount() == 2)
      {
        pickCanvas.setShapeLocation(xpos, ypos);
        PickResult pr = pickCanvas.pickClosest();
        if ((pr != null) &&
            ((tg = (TransformGroup)pr.getNode(PickResult.TRANSFORM_GROUP)) != null) &&
            (tg.getCapability(TransformGroup.ALLOW_TRANSFORM_READ)) &&
            (tg.getCapability(TransformGroup.ALLOW_TRANSFORM_WRITE)))
        {
          if(_myListener != null)
          {
            _myListener.newSelection(pr);
          }
        }
      }
    }


    /**
     * Register the class @param callback to be called each
     * time the picked object moves
     */
    public void setListener( SelectionListener callback )
    {
      _myListener = callback;
    }

    static public interface SelectionListener
    {
      public void newSelection(PickResult nearest);
    }

  }


