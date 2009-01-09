/*****************************************************************************
 *  J3D.org Copyright (c) 2000
 *   Java Source
 *
 * This source is licensed under the GNU LGPL v2.1
 * Please read http://www.gnu.org/copyleft/lgpl.html for more information
 *
 ****************************************************************************/

package org.j3d.geom;

// Standard imports
import javax.media.j3d.Appearance;
import javax.media.j3d.Shape3D;
import javax.media.j3d.QuadArray;

import javax.vecmath.Vector3f;

// Application specific imports

/**
 * A mobius strip with specified number of divisions per strip, number of
 * strips, position, and appearance.
 * <p>
 * The algorithm was adapted from Tore Nordstrand's Math Image Gallery:
 * <A HREF="http://www.uib.no/people/nfytn/mathgal.htm">
 * http://www.uib.no/people/nfytn/mathgal.htm</A> (This algorithm is not
 * perfect yet: The strips are slightly out of alignment. This is easy to see
 * with a small number of strips
 *
 * @author Unknown
 * @version $Revision: 1.1.1.1 $
 */
public class Mobius
{
    private static final int DIVISIONS = 28;
    private static final int STRIPS = 14;
    private static final float XPOSITION = 0.0f;
    private static final float YPOSITION = 0.0f;
    private static final float ZPOSITION = 0.0f;

    private Shape3D mobius;
    public QuadArray mobiusGeometry;
    private float[] qverts;
    private float  mag, x1, x2, x3, x4, y1, y2, y3, y4,
        z1, z2, z3, z4, xn, yn, zn;
    private int vertCount = 0;
    private int normalcount = 0;
    private Vector3f[] normals;

    /**
    *     Constructs a mobius strip with 28 divisions per strip,
    *     14 strips, appearance 'mobiusAppearance' at 0,0,0.
    **/
    public Mobius(Appearance mobiusAppearance)
    {
        this(DIVISIONS, STRIPS, XPOSITION, YPOSITION,
            ZPOSITION, mobiusAppearance);
    }

    /**
    *     Constructs a mobius strip with number of divisions per
    *     strip 'divs', number of strips 'strips', appearance
    *   'mobiusAppearance' at 0,0,0.
    **/
    public Mobius(int divs, int strips, Appearance mobiusAppearance)
    {
        this(divs, strips, XPOSITION, YPOSITION, ZPOSITION,
            mobiusAppearance);
    }

    /**
    *     Constructs a mobius strip with number of divisions per
    *     strip 'divs', number of strips 'strips', appearance
    *   'mobiusAppearance' at xpos,ypos,zpos.
    **/
    public Mobius(int divs, int strips, float xpos, float ypos, float zpos,
        Appearance mobiusAppearance)
    {
        int uiter = divs;
        if (uiter % 2 == 1)
        {
            uiter ++;
        }
        int viter = strips;

        float vmin = -0.3f;
        float vmax = 0.3f;
        float umin = 0.0f;
        float umax = (float) (2*Math.PI);
        float iu = (umax-umin)/uiter;
        float iv = (vmax-vmin)/viter;

        qverts = new float[(viter+1)*uiter*12];

        // Begin Algorithm
        float uu = umin;
        while (uu <= umax)                //outer loop
        {
            float vv = vmin;
            while (vv <= vmax)            //inner loop
            {
                x1 = (float) (Math.cos(uu) +
                    vv*Math.cos(uu/2)*Math.cos(uu));
                qverts[vertCount] = x1;
                vertCount++;
                y1 = (float) (Math.sin(uu) +
                    vv*Math.cos(uu/2)*Math.sin(uu));
                qverts[vertCount] = y1;
                vertCount++;
                z1 = (float) (vv*Math.sin(uu/2));
                qverts[vertCount] = z1;
                vertCount++;

                uu = uu + iu;

                x2 = (float) (Math.cos(uu) +
                    vv*Math.cos(uu/2)*Math.cos(uu));
                qverts[vertCount] = x2;
                vertCount++;
                y2 = (float) (Math.sin(uu) +
                    vv*Math.cos(uu/2)*Math.sin(uu));
                qverts[vertCount] = y2;
                vertCount++;
                z2 = (float) (vv*Math.sin(uu/2));
                qverts[vertCount] = z2;
                vertCount++;

                vv = vv + iv;

                x3 = (float) (Math.cos(uu) +
                    vv*Math.cos(uu/2)*Math.cos(uu));
                qverts[vertCount] = x3;
                vertCount++;
                y3 = (float) (Math.sin(uu) +
                    vv*Math.cos(uu/2)*Math.sin(uu));
                qverts[vertCount] = y3;
                vertCount++;
                z3 = (float) (vv*Math.sin(uu/2));
                qverts[vertCount] = z3;
                vertCount++;

                uu = uu - iu;

                x4 = (float) (Math.cos(uu) +
                    vv*Math.cos(uu/2)*Math.cos(uu));
                qverts[vertCount] = x4;
                vertCount++;
                y4 = (float) (Math.sin(uu) +
                    vv*Math.cos(uu/2)*Math.sin(uu));
                qverts[vertCount] = y4;
                vertCount++;
                z4 = (float) (vv*Math.sin(uu/2));
                qverts[vertCount] = z4;
                vertCount++;
            }
            uu = uu + iu;
        }
        // End Algorithm

        //position the mobius strip corectly
        for (int j=0; j < vertCount; j = j+3)
        {
            qverts[j] = qverts[j] + xpos;
            qverts[j+1] = qverts[j+1] + ypos;
            qverts[j+2] = qverts[j+2] + zpos;
        }

        mobiusGeometry = new QuadArray( vertCount/3,
            QuadArray.COORDINATES | QuadArray.NORMALS);

        mobiusGeometry.setCapability( QuadArray.ALLOW_COLOR_WRITE );
        mobiusGeometry.setCapability( QuadArray.ALLOW_COORDINATE_WRITE );
        mobiusGeometry.setCoordinates( 0, qverts );

        // Calculate normals of all points.
        normals = new Vector3f[vertCount/3];
        for (int w = 0; w < vertCount; w = w + 3)
        {
            Vector3f norm = new Vector3f(0.0f, 0.0f, 0.0f);
            mag = qverts[w] * qverts[w] + qverts[w+1] * qverts[w+1]
                + qverts[w+2] * qverts[w+2];
            if (mag != 0.0)
            {
                mag = 1.0f / ((float) Math.sqrt(mag));
                xn = qverts[w]*mag;
                yn = qverts[w+1]*mag;
                zn = qverts[w+2]*mag;
                norm = new Vector3f(xn, yn, zn);
            }
            normals[normalcount] = norm;
            mobiusGeometry.setNormal(normalcount, norm);
            normalcount++;
        }

        mobius = new Shape3D(mobiusGeometry, mobiusAppearance);
    }

    public void Scale(float xs, float ys, float zs)
    {
        QuadArray qa = (QuadArray) mobius.getGeometry();
        for (int i=0; i < qa.getVertexCount(); i++)
        {
            float[] q = new float[3];
            qa.getCoordinate(i, q);
            q[0] = xs * q[0];
            q[1] = ys * q[1];
            q[2] = ys * q[2];

            qa.setCoordinate(i, q);
        }
        mobius.setGeometry(qa);
    }

    public Shape3D getChild()
    {
        return mobius;
    }

    public QuadArray getQuadArray()
    {
        return mobiusGeometry;
    }
}