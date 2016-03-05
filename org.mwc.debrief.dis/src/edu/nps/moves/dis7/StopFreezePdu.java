package edu.nps.moves.dis7;

import java.util.*;
import java.io.*;
import edu.nps.moves.disenum.*;
import edu.nps.moves.disutil.*;


/**
 * Section 7.5.5. Stop or freeze an enity (or exercise). COMPLETE
 *
 * Copyright (c) 2008-2016, MOVES Institute, Naval Postgraduate School. All rights reserved.
 * This work is licensed under the BSD open source license, available at https://www.movesinstitute.org/licenses/bsd.html
 *
 * @author DMcG
 */
public class StopFreezePdu extends SimulationManagementFamilyPdu implements Serializable
{
   /** Identifier for originating entity(or simulation) */
   protected EntityID  originatingID = new EntityID(); 

   /** Identifier for the receiving entity(or simulation) */
   protected EntityID  receivingID = new EntityID(); 

   /** real-world(UTC) time at which the entity shall stop or freeze in the exercise */
   protected ClockTime  realWorldTime = new ClockTime(); 

   /** Reason the simulation was stopped or frozen (see section 7 of SISO-REF-010) represented by an 8-bit enumeration */
   protected short  reason;

   /** Internal behavior of the entity(or simulation) and its appearance while frozen to the other participants */
   protected short  frozenBehavior;

   /** padding */
   protected short  padding1 = (short)0;

   /** Request ID that is unique */
   protected long  requestID;


/** Constructor */
 public StopFreezePdu()
 {
    setPduType( (short)14 );
 }

public int getMarshalledSize()
{
   int marshalSize = 0; 

   marshalSize = super.getMarshalledSize();
   marshalSize = marshalSize + originatingID.getMarshalledSize();  // originatingID
   marshalSize = marshalSize + receivingID.getMarshalledSize();  // receivingID
   marshalSize = marshalSize + realWorldTime.getMarshalledSize();  // realWorldTime
   marshalSize = marshalSize + 1;  // reason
   marshalSize = marshalSize + 1;  // frozenBehavior
   marshalSize = marshalSize + 2;  // padding1
   marshalSize = marshalSize + 4;  // requestID

   return marshalSize;
}


public void setOriginatingID(EntityID pOriginatingID)
{ originatingID = pOriginatingID;
}

public EntityID getOriginatingID()
{ return originatingID; 
}

public void setReceivingID(EntityID pReceivingID)
{ receivingID = pReceivingID;
}

public EntityID getReceivingID()
{ return receivingID; 
}

public void setRealWorldTime(ClockTime pRealWorldTime)
{ realWorldTime = pRealWorldTime;
}

public ClockTime getRealWorldTime()
{ return realWorldTime; 
}

public void setReason(short pReason)
{ reason = pReason;
}

public short getReason()
{ return reason; 
}

public void setFrozenBehavior(short pFrozenBehavior)
{ frozenBehavior = pFrozenBehavior;
}

public short getFrozenBehavior()
{ return frozenBehavior; 
}

public void setPadding1(short pPadding1)
{ padding1 = pPadding1;
}

public short getPadding1()
{ return padding1; 
}

public void setRequestID(long pRequestID)
{ requestID = pRequestID;
}

public long getRequestID()
{ return requestID; 
}


public void marshal(DataOutputStream dos)
{
    super.marshal(dos);
    try 
    {
       originatingID.marshal(dos);
       receivingID.marshal(dos);
       realWorldTime.marshal(dos);
       dos.writeByte( (byte)reason);
       dos.writeByte( (byte)frozenBehavior);
       dos.writeShort( (short)padding1);
       dos.writeInt( (int)requestID);
    } // end try 
    catch(Exception e)
    { 
      System.out.println(e);}
    } // end of marshal method

public void unmarshal(DataInputStream dis)
{
     super.unmarshal(dis);

    try 
    {
       originatingID.unmarshal(dis);
       receivingID.unmarshal(dis);
       realWorldTime.unmarshal(dis);
       reason = (short)dis.readUnsignedByte();
       frozenBehavior = (short)dis.readUnsignedByte();
       padding1 = dis.readShort();
       requestID = dis.readInt();
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
       super.marshal(buff);
       originatingID.marshal(buff);
       receivingID.marshal(buff);
       realWorldTime.marshal(buff);
       buff.put( (byte)reason);
       buff.put( (byte)frozenBehavior);
       buff.putShort( (short)padding1);
       buff.putInt( (int)requestID);
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
       super.unmarshal(buff);

       originatingID.unmarshal(buff);
       receivingID.unmarshal(buff);
       realWorldTime.unmarshal(buff);
       reason = (short)(buff.get() & 0xFF);
       frozenBehavior = (short)(buff.get() & 0xFF);
       padding1 = buff.getShort();
       requestID = buff.getInt();
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

@Override
 public boolean equalsImpl(Object obj)
 {
     boolean ivarsEqual = true;

    if(!(obj instanceof StopFreezePdu))
        return false;

     final StopFreezePdu rhs = (StopFreezePdu)obj;

     if( ! (originatingID.equals( rhs.originatingID) )) ivarsEqual = false;
     if( ! (receivingID.equals( rhs.receivingID) )) ivarsEqual = false;
     if( ! (realWorldTime.equals( rhs.realWorldTime) )) ivarsEqual = false;
     if( ! (reason == rhs.reason)) ivarsEqual = false;
     if( ! (frozenBehavior == rhs.frozenBehavior)) ivarsEqual = false;
     if( ! (padding1 == rhs.padding1)) ivarsEqual = false;
     if( ! (requestID == rhs.requestID)) ivarsEqual = false;

    return ivarsEqual && super.equalsImpl(rhs);
 }
} // end of class
