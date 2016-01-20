package edu.nps.moves.dis;

import java.util.*;
import java.io.*;
import edu.nps.moves.disenum.*;
import edu.nps.moves.disutil.*;

// Jaxb and Hibernate annotations generally won't work on mobile devices. XML serialization uses jaxb, and
// javax.persistence uses the JPA JSR, aka hibernate. See the Hibernate site for details.
// To generate Java code without these, and without the annotations scattered through the
// see the XMLPG java code generator, and set the boolean useHibernateAnnotations and useJaxbAnnotions 
// to false, and then regenerate the code

import javax.xml.bind.*;            // Used for JAXB XML serialization
import javax.xml.bind.annotation.*; // Used for XML serialization annotations (the @ stuff)
import javax.persistence.*;         // Used for JPA/Hibernate SQL persistence

/**
 * Section 5.3.8. Abstract superclass for radio communications PDUs.
 *
 * Copyright (c) 2008-2010, MOVES Institute, Naval Postgraduate School. All rights reserved.
 * This work is licensed under the BSD open source license, available at https://www.movesinstitute.org/licenses/bsd.html
 *
 * @author DMcG
 */
@Entity  // Hibernate
@Inheritance(strategy=InheritanceType.JOINED)  // Hibernate
public class RadioCommunicationsFamilyPdu extends Pdu implements Serializable
{
   /** ID of the entitythat is the source of the communication */
   protected EntityID  entityId = new EntityID(); 

   /** particular radio within an entity */
   protected int  radioId;


/** Constructor */
 public RadioCommunicationsFamilyPdu()
 {
    setProtocolFamily( (short)4 );
 }

@Transient  // Marked as transient to prevent hibernate from thinking this is a persistent property
public int getMarshalledSize()
{
   int marshalSize = 0; 

   marshalSize = super.getMarshalledSize();
   marshalSize = marshalSize + entityId.getMarshalledSize();  // entityId
   marshalSize = marshalSize + 2;  // radioId

   return marshalSize;
}


public void setEntityId(EntityID pEntityId)
{ entityId = pEntityId;
}

// HIBERNATE: this ivar is a foreign key, linked to the below class table. 
// It is not a DIS-standard variable and is not marshalled to IEEE-1278.1
public long fk_entityId;

@XmlElement
@OneToOne(cascade = CascadeType.ALL)
@JoinColumn(name="fk_entityId")
public EntityID getEntityId()
{ return entityId; 
}

public void setRadioId(int pRadioId)
{ radioId = pRadioId;
}

@XmlAttribute // Jaxb
@Basic       // Hibernate
public int getRadioId()
{ return radioId; 
}


public void marshal(DataOutputStream dos)
{
    super.marshal(dos);
    try 
    {
       entityId.marshal(dos);
       dos.writeShort( (short)radioId);
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
       entityId.unmarshal(dis);
       radioId = (int)dis.readUnsignedShort();
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
       entityId.marshal(buff);
       buff.putShort( (short)radioId);
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

       entityId.unmarshal(buff);
       radioId = (int)(buff.getShort() & 0xFFFF);
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

    if(!(obj instanceof RadioCommunicationsFamilyPdu))
        return false;

     final RadioCommunicationsFamilyPdu rhs = (RadioCommunicationsFamilyPdu)obj;

     if( ! (entityId.equals( rhs.entityId) )) ivarsEqual = false;
     if( ! (radioId == rhs.radioId)) ivarsEqual = false;

    return ivarsEqual && super.equalsImpl(rhs);
 }
} // end of class
