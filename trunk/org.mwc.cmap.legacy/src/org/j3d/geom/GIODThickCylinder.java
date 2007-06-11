/*****************************************************************************
 *            J3D.org Copyright (c) 2000
 *                 Java Source
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
 * A cylinder without end caps that has a thickness to it.
 * <p>
 *
 * @author Robin Smith robinsm@sunyit.edu
 * @version $Revision: 1.1.1.1 $
 */
public class GIODThickCylinder extends Object
{
    private Shape3D scylinder;
    private float[] qverts;
    private float[] qtex;
    private float x, y, z, thetao, thetai, to, ti, num,
        numcirc, xn, yn, zn, mag;
    private int vertCount = 0;
    private int normalcount = 0;
    private Vector3f[] normals;

    /**
    * Constructs a Cylinder with 'n' quads (faces), 'wo' width (diameter)
    * of outer surface, 'wi' width (diameter) of inner surface,
    * 'h' height, centered at 'xpos', 'ypos', 'zpos',
    * and appearance 'cylinderAppearance'
    **/
    public  GIODThickCylinder(int n, float wo, float wi, float h, float xpos,
        float ypos, float zpos, Appearance cylinderAppearance)
    {
        qverts = new float[48*n];
        qtex = new float[48*n];

        to = ((float) (2*Math.PI*(wo/2))/n);
        thetao = to/(wo/2);

        ti = ((float) (2*Math.PI*(wi/2))/n);
        thetai = ti/(wi/2);

        numcirc = n;

        // Generate the outer walls...
        for (int i=0; i < numcirc; i++)
        {
            //bottom points
            x = (float) ((wo/2)*Math.cos(thetao*i));
            z = (float) ((wo/2)*Math.sin(thetao*i));
            qverts[vertCount]  = x;
            vertCount++;
            qverts[vertCount]  = 0-(h/2);
            vertCount++;
            qverts[vertCount]  = z;
            vertCount++;

            i++;

            x = (float) ((wo/2)*Math.cos(thetao*i));
            z = (float) ((wo/2)*Math.sin(thetao*i));
            qverts[vertCount]  = x;
            vertCount++;
            qverts[vertCount]  = 0-(h/2);
            vertCount++;
            qverts[vertCount]  = z;
            vertCount++;

            //top points
            x = (float) ((wo/2)*Math.cos(thetao*i));
            z = (float) ((wo/2)*Math.sin(thetao*i));
            qverts[vertCount]  = x;
            vertCount++;
            qverts[vertCount]  = 0+(h/2);
            vertCount++;
            qverts[vertCount]  = z;
            vertCount++;

            i--;

            x = (float) ((wo/2)*Math.cos(thetao*i));
            z = (float) ((wo/2)*Math.sin(thetao*i));
            qverts[vertCount]  = x;
            vertCount++;
            qverts[vertCount]  = 0+(h/2);
            vertCount++;
            qverts[vertCount]  = z;
            vertCount++;
        }

        // Generate the inner walls...
        for (int i=0; i < numcirc; i++)
        {
            //bottom points
            x = (float) ((wi/2)*Math.cos(thetai*i));
            z = (float) ((wi/2)*Math.sin(thetai*i));
            qverts[vertCount]  = x;
            vertCount++;
            qverts[vertCount]  = 0-(h/2);
            vertCount++;
            qverts[vertCount]  = z;
            vertCount++;

            i++;

            x = (float) ((wi/2)*Math.cos(thetai*i));
            z = (float) ((wi/2)*Math.sin(thetai*i));
            qverts[vertCount]  = x;
            vertCount++;
            qverts[vertCount]  = 0-(h/2);
            vertCount++;
            qverts[vertCount]  = z;
            vertCount++;

            //top points
            x = (float) ((wi/2)*Math.cos(thetai*i));
            z = (float) ((wi/2)*Math.sin(thetai*i));
            qverts[vertCount]  = x;
            vertCount++;
            qverts[vertCount]  = 0+(h/2);
            vertCount++;
            qverts[vertCount]  = z;
            vertCount++;

            i--;

            x = (float) ((wi/2)*Math.cos(thetai*i));
            z = (float) ((wi/2)*Math.sin(thetai*i));
            qverts[vertCount]  = x;
            vertCount++;
            qverts[vertCount]  = 0+(h/2);
            vertCount++;
            qverts[vertCount]  = z;
            vertCount++;
        }

        // Generate the top thickness cap
        for (int i=0; i < numcirc; i++)
        {
            //outer points
            x = (float) ((wo/2)*Math.cos(thetai*i));
            z = (float) ((wo/2)*Math.sin(thetai*i));
            qverts[vertCount]  = x;
            vertCount++;
            qverts[vertCount]  = 0+(h/2);
            vertCount++;
            qverts[vertCount]  = z;
            vertCount++;

            i++;

            x = (float) ((wo/2)*Math.cos(thetai*i));
            z = (float) ((wo/2)*Math.sin(thetai*i));
            qverts[vertCount]  = x;
            vertCount++;
            qverts[vertCount]  = 0+(h/2);
            vertCount++;
            qverts[vertCount]  = z;
            vertCount++;

            //inner points
            x = (float) ((wi/2)*Math.cos(thetai*i));
            z = (float) ((wi/2)*Math.sin(thetai*i));
            qverts[vertCount]  = x;
            vertCount++;
            qverts[vertCount]  = 0+(h/2);
            vertCount++;
            qverts[vertCount]  = z;
            vertCount++;

            i--;

            x = (float) ((wi/2)*Math.cos(thetai*i));
            z = (float) ((wi/2)*Math.sin(thetai*i));
            qverts[vertCount]  = x;
            vertCount++;
            qverts[vertCount]  = 0+(h/2);
            vertCount++;
            qverts[vertCount]  = z;
            vertCount++;
        }


        // Generate the bottom thickness cap
        for (int i=0; i < numcirc; i++)
        {
            //outer points
            x = (float) ((wo/2)*Math.cos(thetai*i));
            z = (float) ((wo/2)*Math.sin(thetai*i));
            qverts[vertCount]  = x;
            vertCount++;
            qverts[vertCount]  = 0-(h/2);
            vertCount++;
            qverts[vertCount]  = z;
            vertCount++;

            i++;

            x = (float) ((wo/2)*Math.cos(thetai*i));
            z = (float) ((wo/2)*Math.sin(thetai*i));
            qverts[vertCount]  = x;
            vertCount++;
            qverts[vertCount]  = 0-(h/2);
            vertCount++;
            qverts[vertCount]  = z;
            vertCount++;

            //inner points
            x = (float) ((wi/2)*Math.cos(thetai*i));
            z = (float) ((wi/2)*Math.sin(thetai*i));
            qverts[vertCount]  = x;
            vertCount++;
            qverts[vertCount]  = 0-(h/2);
            vertCount++;
            qverts[vertCount]  = z;
            vertCount++;

            i--;

            x = (float) ((wi/2)*Math.cos(thetai*i));
            z = (float) ((wi/2)*Math.sin(thetai*i));
            qverts[vertCount]  = x;
            vertCount++;
            qverts[vertCount]  = 0-(h/2);
            vertCount++;
            qverts[vertCount]  = z;
            vertCount++;
        }

        QuadArray cylinderGeometry = new QuadArray( vertCount/3,
                        QuadArray.COORDINATES | QuadArray.NORMALS);

        // Calculate normals of all points.
        normals = new Vector3f[vertCount/3];
        for (int s = 0; s < vertCount; s = s + 3)
        {
            Vector3f norm = new Vector3f(0.0f, 0.0f, 0.0f);
            mag = qverts[s] * qverts[s] + qverts[s+1] *
                    qverts[s+1] + qverts[s+2] * qverts[s+2];
            if (mag != 0.0)
            {
                mag = 1.0f / ((float) Math.sqrt(mag));
                xn = qverts[s]*mag;
                yn = qverts[s+1]*mag;
                zn = qverts[s+2]*mag;
                norm = new Vector3f(xn, yn, zn);
            }
            normals[normalcount] = norm;
            cylinderGeometry.setNormal(normalcount, norm);
            normalcount++;
        }

        //position the cylinder
        for (int j=0; j < vertCount; j = j+3)
        {
            qverts[j] = qverts[j] + xpos;
            qverts[j+1] = qverts[j+1] + ypos;
            qverts[j+2] = qverts[j+2] + zpos;
        }

        cylinderGeometry.setCapability( QuadArray.ALLOW_COORDINATE_WRITE );
        cylinderGeometry.setCoordinates( 0, qverts );

        scylinder = new Shape3D(cylinderGeometry, cylinderAppearance);
    }

    public Shape3D getChild()
    {
        return scylinder;
    }
}
