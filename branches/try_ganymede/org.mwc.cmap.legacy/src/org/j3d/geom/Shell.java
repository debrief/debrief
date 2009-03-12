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
 * http://www.uib.no/people/nfytn/mathgal.htm</A>. Needs to be cleaned up.
 * There is a brutal workaround.
 *
 * @author Unknown
 * @version $Revision: 1.1.1.1 $
 */
public class  Shell
{
    private static final int DIVISIONS = 30;
    private static final int STRIPS = 60;
    private static final float XPOSITION = 0.0f;
    private static final float YPOSITION = 0.0f;
    private static final float ZPOSITION = 0.0f;

    private Shape3D shell;
    public QuadArray shellGeometry;
    private float[] qverts;
    private float mag, x1, x2, x3,
        x4, y1, y2, y3, y4,
        z1, z2, z3, z4, xn, yn, zn;
    private int vertCount = 0;
    private int normalcount = 0;
    private Vector3f[] normals;

    /**
    *     Constructs a shell with 28 divisions per strip,
    *     14 strips, appearance 'shellAppearance' at 0,0,0.
    **/
    public  Shell(Appearance shellAppearance)
    {
        this(DIVISIONS, STRIPS, XPOSITION, YPOSITION, ZPOSITION,
            shellAppearance);
    }

    /**
    *     Constructs a shell with number of divisions per
    *     strip 'divs', number of strips 'strips', appearance
    *   'shellAppearance' at 0,0,0.
    **/
    public  Shell(int divs, int strips, Appearance shellAppearance)
    {
        this(divs, strips, XPOSITION, YPOSITION, ZPOSITION,
            shellAppearance);
    }

    /**
    *     Constructs a shell with number of divisions per
    *     strip 'divs', number of strips 'strips', appearance
    *   'shellAppearance' at xpos,ypos,zpos.
    **/
    public  Shell(int divs, int strips, float xpos,
        float ypos, float zpos,
        Appearance shellAppearance)
    {
        int uiter = divs;
        if (uiter % 2 == 1)
        {
            uiter ++;
        }
        int viter = strips;

        float vmin = 0.1f;
        float vmax = (float) (2*Math.PI);
        float umin = 0.0f;
        float umax = (float) (2*Math.PI);
        float iu = (umax-umin)/uiter;
        float iv = (vmax-vmin)/viter;
        float a = 0.2f;
        float b = 1.0f;
        float c = 0.1f;
        float n = 2.0f;

        qverts = new float[8*(uiter+1)*viter*12];

        // Begin Algorithm
        float vv = vmin;
        while (vv <= vmax)                //outer loop
        {
            float uu = umin;
            while (uu <= umax)            //inner loop
            {
                x1 = (float) (a*(1 - vv/(2*Math.PI))*Math.cos(n*vv)
                    *(1 + Math.cos(uu)) +
                    c*Math.cos(n*vv));
                qverts[vertCount] = x1;
                vertCount++;
                y1 = (float) (a*(1 - vv/(2*Math.PI))*Math.sin(n*vv)
                    *(1 + Math.cos(uu)) +
                    c*Math.sin(n*vv));
                qverts[vertCount] = y1;
                vertCount++;
            z1 = (float) (b*vv/(2*Math.PI) +
                    a*(1 - vv/(2*Math.PI))*Math.sin(uu));
                qverts[vertCount] = z1;
                vertCount++;

              uu = uu + iu;

              x2 = (float) (a*(1 - vv/(2*Math.PI))*Math.cos(n*vv)
                    *(1 + Math.cos(uu)) +
                    c*Math.cos(n*vv));
                qverts[vertCount] = x2;
                vertCount++;
              y2 = (float) (a*(1 - vv/(2*Math.PI))*Math.sin(n*vv)
                    *(1 + Math.cos(uu)) +
                    c*Math.sin(n*vv));
                qverts[vertCount] = y2;
                vertCount++;
              z2 = (float) (b*vv/(2*Math.PI) + a*(1 - vv/(2*Math.PI))
                    *Math.sin(uu));
                qverts[vertCount] = z2;
                vertCount++;

              vv = vv + iv;

              x3 = (float) (a*(1 - vv/(2*Math.PI))*Math.cos(n*vv)
                    *(1 + Math.cos(uu)) +
                    c*Math.cos(n*vv));
                qverts[vertCount] = x3;
                vertCount++;
              y3 = (float) (a*(1 - vv/(2*Math.PI))*Math.sin(n*vv)
                    *(1 + Math.cos(uu)) +
                    c*Math.sin(n*vv));
                qverts[vertCount] = y3;
                vertCount++;
              z3 = (float) (b*vv/(2*Math.PI) + a*(1 - vv/(2*Math.PI))
                    *Math.sin(uu));
                qverts[vertCount] = z3;
                vertCount++;

                uu = uu - iu;

              x4 = (float) (a*(1 - vv/(2*Math.PI))*Math.cos(n*vv)
                    *(1 + Math.cos(uu)) +
                    c*Math.cos(n*vv));
                qverts[vertCount] = x4;
                vertCount++;
              y4 = (float) (a*(1 - vv/(2*Math.PI))*Math.sin(n*vv)
                    *(1 + Math.cos(uu)) +
                    c*Math.sin(n*vv));
                qverts[vertCount] = y4;
                vertCount++;
                z4 = (float) (b*vv/(2*Math.PI) + a*(1 - vv/(2*Math.PI))
                    *Math.sin(uu));
                qverts[vertCount] = z4;
                vertCount++;

                vv = vv - iv;

                uu = uu + iu;
            }
            vv = vv + iv;
        }
        // End Algorithm

        //position the shell strip corectly
        for (int j=0; j < vertCount; j = j+3)
        {
            qverts[j] = qverts[j] + xpos;
            qverts[j+1] = qverts[j+1] + ypos;
            qverts[j+2] = qverts[j+2] + zpos;
        }

        shellGeometry = new QuadArray( qverts.length/3,
            QuadArray.COORDINATES | QuadArray.NORMALS);

        shellGeometry.setCapability( QuadArray.ALLOW_COLOR_WRITE );
        shellGeometry.setCapability( QuadArray.ALLOW_COORDINATE_WRITE );
        shellGeometry.setCoordinates( 0, qverts );

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
            shellGeometry.setNormal(normalcount, norm);
            normalcount++;
        }

        shell = new Shape3D(shellGeometry, shellAppearance);
    }

    public void Scale(float xs, float ys, float zs)
    {
        QuadArray qa = (QuadArray) shell.getGeometry();
        for (int i=0; i < qa.getVertexCount(); i++)
        {
            float[] q = new float[3];
            qa.getCoordinate(i, q);
            q[0] = xs * q[0];
            q[1] = ys * q[1];
            q[2] = ys * q[2];

            qa.setCoordinate(i, q);
        }
        shell.setGeometry(qa);
    }

    public Shape3D getChild()
    {
        return shell;
    }

    public QuadArray getQuadArray()
    {
        return shellGeometry;
    }
}