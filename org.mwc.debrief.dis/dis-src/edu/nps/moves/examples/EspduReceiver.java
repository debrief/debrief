package edu.nps.moves.examples;

import java.net.*;

import edu.nps.moves.disutil.*;

import edu.nps.moves.dis.*;

/**
 * Receives PDUs from the network in IEEE format.
 *
 * @author DMcG
 * @version $Id:$
 */
public class EspduReceiver {

    /** Max size of a PDU in binary format that we can receive. This is actually
     * somewhat outdated--PDUs can be larger--but this is a reasonable starting point
     */
    public static final int MAX_PDU_SIZE = 8192;

    public static void main(String args[]) {
        MulticastSocket socket;
        DatagramPacket packet;
        InetAddress address;
        PduFactory pduFactory = new PduFactory();

        try {
            // Specify the socket to receive data
            socket = new MulticastSocket(EspduSender.PORT);
            address = InetAddress.getByName(EspduSender.DEFAULT_MULTICAST_GROUP);
            socket.joinGroup(address);

            // Loop infinitely, receiving datagrams
            while (true) {
                byte buffer[] = new byte[MAX_PDU_SIZE];
                packet = new DatagramPacket(buffer, buffer.length);

                socket.receive(packet);

                Pdu pdu = pduFactory.createPdu(packet.getData());

                System.out.print("got PDU of type: " + pdu.getClass().getName());
                if(pdu instanceof EntityStatePdu)
                {
                    EntityID eid = ((EntityStatePdu)pdu).getEntityID();
                    Vector3Double position = ((EntityStatePdu)pdu).getEntityLocation();
                    System.out.print(" EID:[" + eid.getSite() + ", " + eid.getApplication() + ", " + eid.getEntity() + "] ");
                    System.out.print(" Location in DIS coordinates: [" + position.getX() + ", " + position.getY() + ", " + position.getZ() + "]");
                }
                System.out.println();

            } // end while
        } // End try
        catch (Exception e) {

            System.out.println(e);
        }


    } // end main
} // end class
