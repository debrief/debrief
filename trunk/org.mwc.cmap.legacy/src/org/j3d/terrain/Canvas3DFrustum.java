/*****************************************************************************
 *                      Modified version (c) j3d.org 2002
 *                               Java Source
 *
 * This source is licensed under the GNU LGPL v2.1
 * Please read http://www.gnu.org/copyleft/lgpl.html for more information
 *
 ****************************************************************************/

/*
 * Copyright (c) 1996-2002 Sun Microsystems, Inc. All Rights Reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * - Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * - Redistribution in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in
 *   the documentation and/or other materials provided with the
 *   distribution.
 *
 * Neither the name of Sun Microsystems, Inc. or the names of
 * contributors may be used to endorse or promote products derived
 * from this software without specific prior written permission.
 *
 * This software is provided "AS IS," without a warranty of any
 * kind. ALL EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND
 * WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY
 * EXCLUDED. SUN AND ITS LICENSORS SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR
 * DISTRIBUTING THE SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL SUN
 * OR ITS LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR
 * FOR DIRECT, INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR
 * PUNITIVE DAMAGES, HOWEVER CAUSED AND REGARDLESS OF THE THEORY OF
 * LIABILITY, ARISING OUT OF THE USE OF OR INABILITY TO USE SOFTWARE,
 * EVEN IF SUN HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 *
 * You acknowledge that Software is not designed,licensed or intended
 * for use in the design, construction, operation or maintenance of
 * any nuclear facility.
 */

package org.j3d.terrain;

import javax.vecmath.Point3d;
import javax.vecmath.Vector4d;

/**
 * ViewFrustum planes for a single Canvas3D and determining if
 * a triangle or point is visible.
 * <p>
 *
 * The frustum is for the previous Java3D frame that has just been rendered.
 */
class Canvas3DFrustum
{
    /** The geometry is in the view frustum, either partially or completely */
    static final int IN = 0x1;

    /** The geometry is outside the view frustum */
    static final int OUT = 0x2;

    /** The geometry has been clipped to the view frustum */
    static final int CLIPPED = 0x4;

    /** The planes describing this frustum */
    Vector4d frustumPlanes[];

    /**
     * Create a new, default frustum for a canvas.
     */
    Canvas3DFrustum()
    {
        frustumPlanes = new Vector4d[8];

        frustumPlanes[0] = new Vector4d();
        frustumPlanes[1] = new Vector4d();
        frustumPlanes[2] = new Vector4d();
        frustumPlanes[3] = new Vector4d();
        frustumPlanes[4] = new Vector4d();
        frustumPlanes[5] = new Vector4d();
        frustumPlanes[6] = new Vector4d();
        frustumPlanes[7] = new Vector4d();
    }

    /**
     * Check if the triangle is in the Frustum, in some cases this may
     * indicate a triangle is in the frustum when it is not, however the
     * converse is not true.
     *
     * @return IN, OUT or CLIPPED
     */
    int isTriangleInFrustum( Point3d p1, Point3d p2, Point3d p3 ) {
        int out0 = 0;
        int out1 = 0;
        int out2 = 0;


        for(int i=0; i<6; i++)
        {
            if (getDistanceFromPlane( p1, frustumPlanes[i] ) <= 0.0f)
                out0 |= (1<<i);
            if (getDistanceFromPlane( p2, frustumPlanes[i] ) <= 0.0f)
                out1 |= (1<<i);
            if (getDistanceFromPlane( p3, frustumPlanes[i] ) <= 0.0f)
                out2 |= (1<<i);
        }

        int ret_val = CLIPPED;

        if (out0==0 && out1==0 && out2==0)
            ret_val = IN;
        else if ((out0 != 0 && out1 != 0 && out2 != 0) &&
                 ((out0 & out1 & out2) != 0))
            ret_val = OUT;

        return ret_val;
    }

    /**
     * Determine if the point is within the frustum.
     *
     * @param point The location to check
     * @return true if the point is in the frustum volume
     */
    public boolean isPointInFrustum(Point3d point)
    {
        for(int i = 0; i < 6; i++)
        {
            if(getDistanceFromPlane(point, frustumPlanes[i]) <= 0.0f)
                return false;
        }

        return true;
    }


    /**
     * Convenience method to find how far a point is from the plane
     *
     * @param point The point to check
     * @param plane The vector representing the plane
     * @return The distance
     */
    private double getDistanceFromPlane(Point3d point, Vector4d plane)
    {
        double dot = point.x * plane.x +
                     point.y * plane.y +
                     point.z * plane.z;

        //System.out.println( point +"  "+(dot+(float)plane.w) );

        return dot + plane.w;
    }
}
