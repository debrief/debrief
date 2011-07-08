/*****************************************************************************
 *                      Modified version (c) j3d.org 2002
 *                               Java Source
 *
 * This source is licensed under the GNU LGPL v2.1
 * Please read http://www.gnu.org/copyleft/lgpl.html for more information
 *
 ****************************************************************************/

/*
 * @(#)SplitMergeLandscape.java 1.1 02/01/10 09:27:31
 *
 * Copyright (c) 2000-2002 Sun Microsystems, Inc.  All Rights Reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *    -Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *    -Redistribution in binary form must reproduct the above copyright notice,
 *     this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 * Neither the name of Sun Microsystems, Inc. or the names of contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING ANY
 * IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR
 * NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN AND ITS LICENSORS SHALL NOT BE
 * LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING
 * OR DISTRIBUTING THE SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL SUN OR ITS
 * LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT,
 * INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER
 * CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF
 * OR INABILITY TO USE SOFTWARE, EVEN IF SUN HAS BEEN ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGES.
 *
 * You acknowledge that Software is not designed,licensed or intended for use in
 * the design, construction, operation or maintenance of any nuclear facility.
 */
package org.j3d.terrain.roam;

// Standard imports
import java.util.ArrayList;

import javax.media.j3d.Appearance;
import javax.media.j3d.Material;
import javax.vecmath.Tuple3f;
import javax.vecmath.Vector3f;

import org.j3d.terrain.Landscape;
import org.j3d.terrain.TerrainData;
import org.j3d.terrain.ViewFrustum;

/**
 * ROAM implmentation of a landscape using the split-merge combination
 * algorithm.
 * <p>
 *
 * First patch is at 0,0 in x, z and then patches are laid out along the
 * +ve x axis and the -ve z axis
 *
 * @author Paul Byrne, Justin Couch
 * @version $Revision: 1.1.1.1 $
 */
public class SplitMergeLandscape extends Landscape
{
    static final int PATCH_SIZE = 64;

    /** The collection of all patches in this landscape */
    private ArrayList<Patch> patches = new ArrayList<Patch>();

    /** Queue manager for the pathces needing splits or merges each frame */
    private TreeQueueManager queueManager = new TreeQueueManager();

    /** Number of visible triangles */
    @SuppressWarnings("unused")
		private int triCount = 0;

    /**
     * Creates new Landscape based on the view information and the terrain
     * data.
     *
     * @param view The view frustum looking at this landscape
     * @param terrain The raw data for the terrain
     */
    public SplitMergeLandscape(ViewFrustum view, TerrainData terrain)
    {
        super(view, terrain);

        createPatches();
    }

    /**
     * Change the view of the landscape. The virtual camera is now located in
     * this position and orientation, so adjust the visible terrain to
     * accommodate the changes.
     *
     * @param position The position of the camera
     * @param direction The direction the camera is looking
     */
    public void setView(Tuple3f position, Vector3f direction)
    {
        queueManager.clear();
        landscapeView.viewingPlatformMoved();
        float accuracy = (float)Math.toRadians(0.1);
        TreeNode splitCandidate;
        TreeNode mergeCandidate;
        boolean done;
        int size = patches.size();

        for(int i = 0; i < size; i++)
        {
            Patch p = (Patch)patches.get(i);

            p.setView(position, landscapeView, queueManager);
        }

        done = false;

        while(!done)
        {
            splitCandidate = queueManager.getSplitCandidate();
            mergeCandidate = queueManager.getMergeCandidate();

            if(mergeCandidate == null && splitCandidate != null)
            {
                if (splitCandidate.variance > accuracy)
                {
                    triCount += splitCandidate.split(position, landscapeView, queueManager);
                }
                else
                    done = true;
            }
            else if(mergeCandidate!=null && splitCandidate == null)
            {
                if(mergeCandidate.diamondVariance < accuracy)
                {
                    triCount -= mergeCandidate.merge(queueManager);
                    //System.out.println("No split merge "+mergeCandidate+"  "+mergeCandidate.diamondVariance);
                }
                else
                    done = true;
            }
            else if(mergeCandidate != null && splitCandidate != null &&
                    (splitCandidate.variance > accuracy ||
                     splitCandidate.variance > mergeCandidate.diamondVariance))
            {
                if (splitCandidate.variance > accuracy)
                {
                    triCount += splitCandidate.split(position, landscapeView, queueManager);
                }
                else if (mergeCandidate.diamondVariance < accuracy)
                {
                    triCount -= mergeCandidate.merge(queueManager);
                }
            }
            else
            {
                done = true;
            }
        }


        for(int i = 0; i < size; i++)
        {
            Patch p = (Patch)patches.get(i);

            p.updateGeometry();
        }
    }

    /**
     * Create a new set of patches based on the given terrain data.
     */
    private void createPatches()
    {
        int depth = terrainData.getGridDepth() - PATCH_SIZE;
        int width = terrainData.getGridWidth() - PATCH_SIZE;

        Appearance app = new Appearance();

        app.setTexture(terrainData.getTexture());

        Material mat = new Material();
        mat.setLightingEnable(true);

        app.setMaterial(mat);

//        PolygonAttributes polyAttr = new PolygonAttributes();
//        polyAttr.setPolygonMode(PolygonAttributes.POLYGON_LINE);
//        polyAttr.setCullFace(PolygonAttributes.CULL_NONE);
//        app.setPolygonAttributes(polyAttr);

        Patch[] westPatchNeighbour = new Patch[width];
        Patch southPatchNeighbour = null;
        Patch p = null;

        for(int east = 0; east <= width; east += PATCH_SIZE)
        {
            for(int north = 0; north <= depth; north += PATCH_SIZE)
            {
                p = new Patch(terrainData,
                              PATCH_SIZE,
                              east,
                              north,
                              app,
                              landscapeView,
                              westPatchNeighbour[north/PATCH_SIZE],
                              southPatchNeighbour);

                patches.add(p);
                triCount += 2;
                this.addChild(p.getShape3D());
                southPatchNeighbour = p;
                westPatchNeighbour[north/PATCH_SIZE] = p;
            }

            southPatchNeighbour = null;
        }
    }
}
