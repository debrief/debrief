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
 * A mobius strip with specified number of divisions per circle-strip, number
 * of divisions per strip (lengthwise), and appearance.
 * <p>
 * The algorithm was adapted from Tore Nordstrand's Math Image Gallery:
 * <A HREF="http://www.uib.no/people/nfytn/mathgal.htm">
 * http://www.uib.no/people/nfytn/mathgal.htm</A>
 *
 * @author Unknown
 * @version $Revision: 1.1.1.1 $
 */
public class Knot extends Object
{
    private static final int DIVISIONS = 80;
    private static final int STRIPS = 12;
    private static final float XPOSITION = 0.0f;
    private static final float YPOSITION = 0.0f;
    private static final float ZPOSITION = 0.0f;

    private Shape3D knot;
    public QuadArray knotGeometry;
    private float[] qverts;
    private float mag, x1, x2, x3,
        x4, y1, y2, y3, y4,
        z1, z2, z3, z4, xn, yn, zn;
    private int vertCount = 0;
    private int normalcount = 0;
    private Vector3f[] normals;

    /**
    *     Constructs a knot with 80 divisions per strip,
    *     12 strips, appearance 'knotAppearance' at 0,0,0.
    **/
    public Knot(Appearance knotAppearance)
    {
        this(DIVISIONS, STRIPS, XPOSITION, YPOSITION, ZPOSITION,
            knotAppearance);
    }

    /**
    *     Constructs a knot with number of divisions per
    *     strip 'divs', number of strips 'strips', appearance
    *   'knotAppearance' at 0,0,0.
    **/
    public Knot(int divs, int strips, Appearance knotAppearance)
    {
        this(divs, strips, XPOSITION, YPOSITION, ZPOSITION,
        knotAppearance);
    }

    /**
    *     Constructs a knot with number of divisions per
    *     strip 'divs', number of strips 'strips', appearance
    *   'knotAppearance' at xpos,ypos,zpos.
    **/
    public Knot(int divs, int strips, float xpos, float ypos, float zpos,
        Appearance knotAppearance)
    {
        int uiter = divs;
        if (uiter % 2 == 1)
        {
            uiter ++;
        }
        int viter = strips;

        float vmin = 0.0f;
        float vmax = (float) (2*Math.PI);
        float umin = 0.0f;
        float umax = (float) (4*Math.PI);
        float iu = (umax-umin)/uiter;
        float iv = (vmax-vmin)/viter;
        int a = 1;
        float b = 0.3f;
        float c = 0.5f;
        float d = 0.3f;

        float r, xx, yy, zz, dx, dy, dz;
        Vector3f vfa, qn, vfb, qvn, ww;

        qverts = new float[(viter+1)*uiter*12];

        // Begin Algorithm
        float uu = umin;
        while (uu <= umax)                //outer loop
        {
            float vv = vmin;
            while (vv <= vmax)            //inner loop
            {
                r =  (float) (a + (b*Math.cos(1.5f*uu)));
                xx = (float) (r*Math.cos(uu));
                yy = (float) (r*Math.sin(uu));
                zz = (float) (c*Math.sin(1.5f*uu));
                dx = (float) (-1.5f*b*Math.sin(1.5f*uu)*
                        Math.cos(uu)
                    - (a + b*Math.cos(1.5f*uu))*Math.sin(uu));
                dy = (float) (-1.5f*b*Math.sin(1.5f*uu)*Math.sin(uu)
                        + (a + b*Math.cos(1.5f*uu))*
                        Math.cos(uu));
                dz = (float) (1.5*c*Math.cos(1.5*uu));
                vfa = new Vector3f(dx,dy,dz);
                qn = new Vector3f();
                qn.normalize(vfa);
                vfb = new Vector3f(qn.y,-qn.x,0);
                qvn = new Vector3f();
                qvn.normalize(vfb);
                ww = new Vector3f();
                ww.cross(qn,qvn);
                x1 = (float) (xx + d*(qvn.x*Math.cos(vv) +
                    ww.x*Math.sin(vv)));
                qverts[vertCount] = x1;
                vertCount++;
                y1 = (float) (yy + d*(qvn.y*Math.cos(vv) +
                    ww.y*Math.sin(vv)));
                qverts[vertCount] = y1;
                vertCount++;
                z1 = (float) (zz + d*ww.z*Math.sin(vv));
                qverts[vertCount] = z1;
                vertCount++;

                vv = vv + iv;

                r =  (float) (a + (b*Math.cos(1.5f*uu)));
                xx = (float) (r*Math.cos(uu));
                yy = (float) (r*Math.sin(uu));
                zz = (float) (c*Math.sin(1.5f*uu));
                dx = (float) (-1.5f*b*Math.sin(1.5f*uu)*Math.cos(uu)
                    - (a + b*Math.cos(1.5f*uu))*Math.sin(uu));
                dy = (float) (-1.5f*b*Math.sin(1.5f*uu)*Math.sin(uu)
                        + (a + b*Math.cos(1.5f*uu))*
                            Math.cos(uu));
                dz = (float) (1.5*c*Math.cos(1.5*uu));
                vfa = new Vector3f(dx,dy,dz);
                qn = new Vector3f();
                qn.normalize(vfa);
                vfb = new Vector3f(qn.y,-qn.x,0);
                qvn = new Vector3f();
                qvn.normalize(vfb);
                ww = new Vector3f();
                ww.cross(qn,qvn);
                x2 = (float) (xx + d*(qvn.x*Math.cos(vv) +
                    ww.x*Math.sin(vv)));
                qverts[vertCount] = x2;
                vertCount++;
                y2 = (float) (yy + d*(qvn.y*Math.cos(vv) +
                    ww.y*Math.sin(vv)));
                qverts[vertCount] = y2;
                vertCount++;
                z2 = (float) (zz + d*ww.z*Math.sin(vv));
                qverts[vertCount] = z2;
                vertCount++;

                uu = uu + iu;

                r =  (float) (a + (b*Math.cos(1.5f*uu)));
                xx = (float) (r*Math.cos(uu));
                yy = (float) (r*Math.sin(uu));
                zz = (float) (c*Math.sin(1.5f*uu));
                dx = (float) (-1.5f*b*Math.sin(1.5f*uu)*Math.cos(uu)
                    - (a + b*Math.cos(1.5f*uu))*Math.sin(uu));
                dy = (float) (-1.5f*b*Math.sin(1.5f*uu)*Math.sin(uu)
                        + (a + b*Math.cos(1.5f*uu))*
                            Math.cos(uu));
                dz = (float) (1.5*c*Math.cos(1.5*uu));
                vfa = new Vector3f(dx,dy,dz);
                qn = new Vector3f();
                qn.normalize(vfa);
                vfb = new Vector3f(qn.y,-qn.x,0);
                qvn = new Vector3f();
                qvn.normalize(vfb);
                ww = new Vector3f();
                ww.cross(qn,qvn);
                x3 = (float) (xx + d*(qvn.x*Math.cos(vv) +
                    ww.x*Math.sin(vv)));
                qverts[vertCount] = x3;
                vertCount++;
                y3 = (float) (yy + d*(qvn.y*Math.cos(vv) +
                    ww.y*Math.sin(vv)));
                qverts[vertCount] = y3;
                vertCount++;
                z3 = (float) (zz + d*ww.z*Math.sin(vv));
                qverts[vertCount] = z3;
                vertCount++;

                vv = vv - iv;

                r =  (float) (a + (b*Math.cos(1.5f*uu)));
                xx = (float) (r*Math.cos(uu));
                yy = (float) (r*Math.sin(uu));
                zz = (float) (c*Math.sin(1.5f*uu));
                dx = (float) (-1.5f*b*Math.sin(1.5f*uu)*Math.cos(uu)
                    - (a + b*Math.cos(1.5f*uu))*Math.sin(uu));
                dy = (float) (-1.5f*b*Math.sin(1.5f*uu)*Math.sin(uu)
                        + (a + b*Math.cos(1.5f*uu))*
                            Math.cos(uu));
                dz = (float) (1.5*c*Math.cos(1.5*uu));
                vfa = new Vector3f(dx,dy,dz);
                qn = new Vector3f();
                qn.normalize(vfa);
                vfb = new Vector3f(qn.y,-qn.x,0);
                qvn = new Vector3f();
                qvn.normalize(vfb);
                ww = new Vector3f();
                ww.cross(qn,qvn);
                x4 = (float) (xx + d*(qvn.x*Math.cos(vv) +
                    ww.x*Math.sin(vv)));
                qverts[vertCount] = x4;
                vertCount++;
                y4 = (float) (yy + d*(qvn.y*Math.cos(vv) +
                    ww.y*Math.sin(vv)));
                qverts[vertCount] = y4;
                vertCount++;
                z4 = (float) (zz + d*ww.z*Math.sin(vv));
                qverts[vertCount] = z4;
                vertCount++;

                uu = uu - iu;
                vv = vv + iv;
            }
            uu = uu + iu;
        }
        // End Algorithm

        //position the knot strip corectly
        for (int j=0; j < vertCount; j = j+3)
        {
            qverts[j] = qverts[j] + xpos;
            qverts[j+1] = qverts[j+1] + ypos;
            qverts[j+2] = qverts[j+2] + zpos;
        }

        knotGeometry = new QuadArray( vertCount/3,
            QuadArray.COORDINATES | QuadArray.NORMALS);

        knotGeometry.setCapability( QuadArray.ALLOW_COLOR_WRITE );
        knotGeometry.setCapability( QuadArray.ALLOW_COORDINATE_WRITE );
        knotGeometry.setCoordinates( 0, qverts );

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
            knotGeometry.setNormal(normalcount, norm);
            normalcount++;
        }

        knot = new Shape3D(knotGeometry, knotAppearance);
    }

    public void Scale(float xs, float ys, float zs)
    {
        QuadArray qa = (QuadArray) knot.getGeometry();
        for (int i=0; i < qa.getVertexCount(); i++)
        {
            float[] q = new float[3];
            qa.getCoordinate(i, q);
            q[0] = xs * q[0];
            q[1] = ys * q[1];
            q[2] = ys * q[2];

            qa.setCoordinate(i, q);
        }
        knot.setGeometry(qa);
    }

    public Shape3D getChild()
    {
        return knot;
    }

    public QuadArray getQuadArray()
    {
        return knotGeometry;
    }
}