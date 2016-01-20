package edu.nps.moves.disutil;

import java.io.*;
import java.util.logging.*;

import edu.nps.moves.dis.*;
import edu.nps.moves.disenum.PduType;

/**
 * Simple factory for PDUs. When bytes are received on the wire, they're passed off to us
 * and the correct constructor called to return the correct PDU type.<p>
 *
 * This should be reworked to use the separate enumerations package, which is generated
 * from the XML EBV file. That's included with the open-dis distribution, but it's still
 * a little new.
 *
 * @author DMcG
 */
public class PduFactory {

    /** whether we should use return flattened, "fast" espdus with fewer objects */
    private boolean useFastPdu = false;
    
    /** Release version of objects we should return. The DIS version has more features and is suitable for desktops. */
    private Platform release = Platform.DESKTOP;
    
    public enum Platform{DESKTOP, MOBILE};
    
    private Logger logger;
            

    /** Creates a new instance of PduFactory */
    public PduFactory() {
        this(false);
    }

    /** 
     * Create a new PDU factory; if true is passed in, we use "fast PDUs",
     * which minimize the memory garbage generated at the cost of being
     * somewhat less pleasant to work with.
     * @param useFastPdu
     */
    public PduFactory(boolean useFastPdu) {
        this.useFastPdu = useFastPdu;
        
        // By default don't log anything
        logger = Logger.getLogger(PduFactory.class.getName());
        logger.setLevel(Level.OFF);
        
    }

    public void setUseFastPdu(boolean use) {
        this.useFastPdu = use;
    }

    public boolean getUseFastPdu() {
        return this.useFastPdu;
    }
    
    /**
     * Set the logging level that will be printed, typically to Level.INFO
     * @param loggingLevel 
     */
    public void setLoggingLevel(Level loggingLevel)
    {
        logger.setLevel(loggingLevel);
    }

    /** 
     * PDU factory. Pass in an array of bytes, get the correct type of pdu back,
     * based on the PDU type field contained in the byte array.
     * @param data
     * @return A PDU of the appropriate concrete subclass of PDU
     */
    public Pdu createPdu(byte data[]) {
        // Promote a signed byte to an int, then do a bitwise AND to wipe out everthing but the 
        // first eight bits. This effectively lets us read this as an unsigned byte
        int pduType = 0x000000FF & (int) data[2]; // The pdu type is a one-byte, unsigned byte in the third byte position.

        // Do a lookup to get the enumeration instance that corresponds to this value. If we can't find a 
        // relevant enumerated PDU type, return null.
        PduType pduTypeEnum = null;
        
        try
        {
           pduTypeEnum = PduType.lookup[pduType];
        }
        catch(Exception e)
        {
           return null;  // Unknown pdu type detected
        }   

        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(data));
        Pdu aPdu = null;

        switch (pduTypeEnum) {
            case ENTITY_STATE:
                // if the user has created the factory requesting that he get fast espdus back, give him those.
                if (useFastPdu) {
                    aPdu = new FastEntityStatePdu();
                } else {
                    aPdu = new EntityStatePdu();
                }

                aPdu.unmarshal(dis);
                break;

            case ACKNOWLEDGE:
                aPdu = new AcknowledgePdu();
                aPdu.unmarshal(dis);
                break;

            case ACTION_REQUEST:
                aPdu = new ActionRequestPdu();
                aPdu.unmarshal(dis);
                break;

            case COLLISION:
                aPdu = new CollisionPdu();
                aPdu.unmarshal(dis);
                break;

            case COMMENT:
                aPdu = new CommentPdu();
                aPdu.unmarshal(dis);
                break;

            case CREATE_ENTITY:
                aPdu = new CreateEntityPdu();
                aPdu.unmarshal(dis);
                break;

            case DATA:
                aPdu = new DataPdu();
                aPdu.unmarshal(dis);
                break;

            case DATA_QUERY:
                aPdu = new DataQueryPdu();
                aPdu.unmarshal(dis);
                break;

            case DETONATION:
                aPdu = new DetonationPdu();
                aPdu.unmarshal(dis);
                break;

            case FIRE:
                aPdu = new FirePdu();
                aPdu.unmarshal(dis);
                break;

            case SERVICE_REQUEST:
                aPdu = new ServiceRequestPdu();
                aPdu.unmarshal(dis);
                break;

            case REMOVE_ENTITY:
                aPdu = new RemoveEntityPdu();
                aPdu.unmarshal(dis);
                break;

            case REPAIR_COMPLETE:
                aPdu = new RepairCompletePdu();
                aPdu.unmarshal(dis);
                break;

            case RESUPPLY_CANCEL:
                aPdu = new ResupplyCancelPdu();
                aPdu.unmarshal(dis);
                break;

            case RESUPPLY_OFFER:
                aPdu = new ResupplyOfferPdu();
                aPdu.unmarshal(dis);
                break;

            case REPAIR_RESPONSE:
                aPdu = new RepairResponsePdu();
                aPdu.unmarshal(dis);
                break;

            case SET_DATA:
                aPdu = new SetDataPdu();
                aPdu.unmarshal(dis);
                break;
                
            case START_RESUME:
                aPdu = new StartResumePdu();
                aPdu.unmarshal(dis);
                break;

            case STOP_FREEZE:
                aPdu = new StopFreezePdu();
                aPdu.unmarshal(dis);
                break;

            default:
                logger.log(Level.INFO, "PDU not implemented. Type = " + pduType + "\n");
                if (pduTypeEnum != null) {
                    logger.log(Level.INFO, "  PDU  name is: " + pduTypeEnum.getDescription());
                }

        }

        return aPdu;
    }

    /**
     * PDU factory. Pass in an array of bytes, get the correct type of pdu back,
     * based on the PDU type field contained in the byte buffer.
     * @param buff
     * @return null if there was an error creating the Pdu
     */
    public Pdu createPdu(java.nio.ByteBuffer buff) {
        int pos = buff.position();          // Save buffer's position
        if (pos + 2 > buff.limit()) {       // Make sure there's enough space in buffer
            return null;                    // Else return null
        }   // end if: buffer too short
        buff.position(pos + 2);             // Advance to third byte
        int pduType = buff.get() & 0xFF;    // Read Pdu type
        buff.position(pos);                 // Reset buffer

        // Do a lookup to get the enumeration instance that corresponds to this value.
        PduType pduTypeEnum = PduType.lookup[pduType];
        Pdu aPdu = null;

        switch (pduTypeEnum) {
            case ENTITY_STATE:
                // if the user has created the factory requesting that he get fast espdus back, give him those.
                if (useFastPdu) {
                    aPdu = new FastEntityStatePdu();
                } else {
                    aPdu = new EntityStatePdu();
                }
                break;

            case FIRE:
                aPdu = new FirePdu();
                break;

            case DETONATION:
                aPdu = new DetonationPdu();
                break;

            case COLLISION:
                aPdu = new CollisionPdu();
                break;

            case SERVICE_REQUEST:
                aPdu = new ServiceRequestPdu();
                break;

            case RESUPPLY_OFFER:
                aPdu = new ResupplyOfferPdu();
                break;

            case RESUPPLY_RECEIVED:
                aPdu = new ResupplyReceivedPdu();
                break;

            case RESUPPLY_CANCEL:
                aPdu = new ResupplyCancelPdu();
                break;

            case REPAIR_COMPLETE:
                aPdu = new RepairCompletePdu();
                break;

            case REPAIR_RESPONSE:
                aPdu = new RepairResponsePdu();
                break;

            case CREATE_ENTITY:
                aPdu = new CreateEntityPdu();
                break;

            case REMOVE_ENTITY:
                aPdu = new RemoveEntityPdu();
                break;

            case START_RESUME:
                aPdu = new StartResumePdu();
                break;

            case STOP_FREEZE:
                aPdu = new StopFreezePdu();
                break;

            case ACKNOWLEDGE:
                aPdu = new AcknowledgePdu();
                break;

            case ACTION_REQUEST:
                aPdu = new ActionRequestPdu();
                break;

            default:
                System.out.print("PDU not implemented. Type = " + pduType + "\n");
                if (pduTypeEnum != null) {
                    System.out.print("PDU not implemented name is: " + pduTypeEnum.getDescription());
                }
                System.out.println();

        }   // end switch

        try {
            aPdu.unmarshal(buff);
        } catch (Exception exc) {
            //System.err.println("Could not unmarshal Pdu: " + exc.getMessage());
            aPdu = null;
        }

        return aPdu;
    }
}
