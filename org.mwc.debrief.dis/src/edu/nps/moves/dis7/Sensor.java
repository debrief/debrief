package edu.nps.moves.dis7;

import java.util.*;
import java.io.*;
import edu.nps.moves.disenum.*;
import edu.nps.moves.disutil.*;


/**
 * An entity's sensor information.  Section 6.2.77.
 *
 * Copyright (c) 2008-2016, MOVES Institute, Naval Postgraduate School. All rights reserved.
 * This work is licensed under the BSD open source license, available at https://www.movesinstitute.org/licenses/bsd.html
 *
 * @author DMcG
 */
public class Sensor extends Object implements Serializable
{
   /**  the source of the Sensor Type field  */
   protected short  sensorTypeSource;

   /** the on/off status of the sensor */
   protected short  sensorOnOffStatus;

   /** the sensor type and shall be represented by a 16-bit enumeration.  */
   protected int  sensorType;

   /**  the station to which the sensor is assigned. A zero value shall indi- cate that this Sensor record is not associated with any particular station and represents the total quan- tity of this sensor for this entity. If this field is non-zero, it shall either reference an attached part or an articulated part */
   protected long  station;

   /** quantity of the sensor  */
   protected int  quantity;

   /** padding */
   protected int  padding = (int)0;


/** Constructor */
 public Sensor()
 {
 }

public int getMarshalledSize()
{
   int marshalSize = 0; 

   marshalSize = marshalSize + 1;  // sensorTypeSource
   marshalSize = marshalSize + 1;  // sensorOnOffStatus
   marshalSize = marshalSize + 2;  // sensorType
   marshalSize = marshalSize + 4;  // station
   marshalSize = marshalSize + 2;  // quantity
   marshalSize = marshalSize + 2;  // padding

   return marshalSize;
}


public void setSensorTypeSource(short pSensorTypeSource)
{ sensorTypeSource = pSensorTypeSource;
}

public short getSensorTypeSource()
{ return sensorTypeSource; 
}

public void setSensorOnOffStatus(short pSensorOnOffStatus)
{ sensorOnOffStatus = pSensorOnOffStatus;
}

public short getSensorOnOffStatus()
{ return sensorOnOffStatus; 
}

public void setSensorType(int pSensorType)
{ sensorType = pSensorType;
}

public int getSensorType()
{ return sensorType; 
}

public void setStation(long pStation)
{ station = pStation;
}

public long getStation()
{ return station; 
}

public void setQuantity(int pQuantity)
{ quantity = pQuantity;
}

public int getQuantity()
{ return quantity; 
}

public void setPadding(int pPadding)
{ padding = pPadding;
}

public int getPadding()
{ return padding; 
}


public void marshal(DataOutputStream dos)
{
    try 
    {
       dos.writeByte( (byte)sensorTypeSource);
       dos.writeByte( (byte)sensorOnOffStatus);
       dos.writeShort( (short)sensorType);
       dos.writeInt( (int)station);
       dos.writeShort( (short)quantity);
       dos.writeShort( (short)padding);
    } // end try 
    catch(Exception e)
    { 
      System.out.println(e);}
    } // end of marshal method

public void unmarshal(DataInputStream dis)
{
    try 
    {
       sensorTypeSource = (short)dis.readUnsignedByte();
       sensorOnOffStatus = (short)dis.readUnsignedByte();
       sensorType = (int)dis.readUnsignedShort();
       station = dis.readInt();
       quantity = (int)dis.readUnsignedShort();
       padding = (int)dis.readUnsignedShort();
    } // end try 
   catch(Exception e)
    { 
      System.out.println(e); 
    }
 } // end of unmarshal method 


/**
 * Packs a Pdu into the ByteBuffer.
 * @throws java.nio.BufferOverflowException if buff is too small
 * @throws java.nio.ReadOnlyBufferException if buff is read only
 * @see java.nio.ByteBuffer
 * @param buff The ByteBuffer at the position to begin writing
 * @since ??
 */
public void marshal(java.nio.ByteBuffer buff)
{
       buff.put( (byte)sensorTypeSource);
       buff.put( (byte)sensorOnOffStatus);
       buff.putShort( (short)sensorType);
       buff.putInt( (int)station);
       buff.putShort( (short)quantity);
       buff.putShort( (short)padding);
    } // end of marshal method

/**
 * Unpacks a Pdu from the underlying data.
 * @throws java.nio.BufferUnderflowException if buff is too small
 * @see java.nio.ByteBuffer
 * @param buff The ByteBuffer at the position to begin reading
 * @since ??
 */
public void unmarshal(java.nio.ByteBuffer buff)
{
       sensorTypeSource = (short)(buff.get() & 0xFF);
       sensorOnOffStatus = (short)(buff.get() & 0xFF);
       sensorType = (int)(buff.getShort() & 0xFFFF);
       station = buff.getInt();
       quantity = (int)(buff.getShort() & 0xFFFF);
       padding = (int)(buff.getShort() & 0xFFFF);
 } // end of unmarshal method 


 /*
  * The equals method doesn't always work--mostly it works only on classes that consist only of primitives. Be careful.
  */
@Override
 public boolean equals(Object obj)
 {

    if(this == obj){
      return true;
    }

    if(obj == null){
       return false;
    }

    if(getClass() != obj.getClass())
        return false;

    return equalsImpl(obj);
 }

 /**
  * Compare all fields that contribute to the state, ignoring
 transient and static fields, for <code>this</code> and the supplied object
  * @param obj the object to compare to
  * @return true if the objects are equal, false otherwise.
  */
 public boolean equalsImpl(Object obj)
 {
     boolean ivarsEqual = true;

    if(!(obj instanceof Sensor))
        return false;

     final Sensor rhs = (Sensor)obj;

     if( ! (sensorTypeSource == rhs.sensorTypeSource)) ivarsEqual = false;
     if( ! (sensorOnOffStatus == rhs.sensorOnOffStatus)) ivarsEqual = false;
     if( ! (sensorType == rhs.sensorType)) ivarsEqual = false;
     if( ! (station == rhs.station)) ivarsEqual = false;
     if( ! (quantity == rhs.quantity)) ivarsEqual = false;
     if( ! (padding == rhs.padding)) ivarsEqual = false;

    return ivarsEqual;
 }
} // end of class
