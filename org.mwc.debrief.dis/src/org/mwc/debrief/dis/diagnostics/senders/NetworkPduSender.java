package org.mwc.debrief.dis.diagnostics.senders;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import org.mwc.debrief.dis.listeners.IDISStopListener;
import org.mwc.debrief.dis.providers.network.NetworkDISProvider;

import edu.nps.moves.dis.Pdu;
import edu.nps.moves.dis.SimulationManagementFamilyPdu;
import edu.nps.moves.dis.StartResumePdu;
import edu.nps.moves.dis.StopFreezePdu;

public class NetworkPduSender implements IPduSender {

	/**
	 * interface for objects that are interested in listening to DIS control
	 * messages
	 *
	 * @author ian
	 *
	 */
	public static interface IDISControlMessageListener {
		void doPause(int appId, short exId);

		void doPlay(int appId, short exId);

		void doStop(int appId, short exId);
	}

	public enum NetworkMode {
		UNICAST, MULTICAST, BROADCAST
	};

	/** default multicast group we send on */
	public static final String DEFAULT_MULTICAST_GROUP = "239.1.2.3";

	/** Port we send on */
	public static final int PORT = 62040;

	private MulticastSocket socket;

	private InetAddress destinationIp;

	private IDISControlMessageListener _controlListener = null;

	private boolean _running = true;

	private int port;

	/**
	 * Possible system properties, passed in via -Dattr=val networkMode: unicast,
	 * broadcast, multicast destinationIp: where to send the packet. If in multicast
	 * mode, this can be mcast. To determine bcast destination IP, use an online
	 * bcast address caclulator, for example
	 * http://www.remotemonitoringsystems.ca/broadcast.php If in mcast mode, a
	 * join() will be done on the mcast address. port: port used for both source and
	 * destination.
	 *
	 * @param portString
	 * @param destinationIpString
	 * @param networkModeString
	 *
	 * @param args
	 */
	public NetworkPduSender(final String destinationIpString, final String portString, final String networkModeString,
			final IDISControlMessageListener statusListener) {

		_controlListener = statusListener;

		// Default settings. These are used if no system properties are set.
		// If system properties are passed in, these are over ridden.
		port = PORT;
		@SuppressWarnings("unused")
		NetworkMode mode = NetworkMode.MULTICAST;
		destinationIp = null;

		try {
			destinationIp = InetAddress.getByName(DEFAULT_MULTICAST_GROUP);
		} catch (final Exception e) {
			System.out.println(e + " Cannot create multicast address");
			System.exit(0);
		}

		// Set up a socket to send information
		try {
			// Port we send to
			if (portString != null)
				port = Integer.parseInt(portString);

			socket = new MulticastSocket(port);

			// Where we send packets to, the destination IP address
			if (destinationIpString != null) {
				destinationIp = InetAddress.getByName(destinationIpString);
			}

			// Type of transport: unicast, broadcast, or multicast
			if (networkModeString != null) {
				if (networkModeString.equalsIgnoreCase("unicast"))
					mode = NetworkMode.UNICAST;
				else if (networkModeString.equalsIgnoreCase("broadcast"))
					mode = NetworkMode.BROADCAST;
				else if (networkModeString.equalsIgnoreCase("multicast")) {
					mode = NetworkMode.MULTICAST;
					if (!destinationIp.isMulticastAddress()) {
						throw new RuntimeException("Sending to multicast address, but destination address "
								+ destinationIp.toString() + "is not multicast");
					}

					socket.joinGroup(destinationIp);

					doListening();

				}
			}

		} catch (final Exception e) {
			System.out.println("Unable to initialize networking. Exiting.");
			System.out.println(e);
			System.exit(-1);
		}
	}

	@Override
	public void close() {
		_running = false;

		socket.close();
		socket = null;
	}

	private void doListening() {
		final Runnable doListen = new Runnable() {

			@Override
			public void run() {

				// also listen on the port
				_running = true;

				while (_running) {
					final byte buffer[] = new byte[NetworkDISProvider.MAX_PDU_SIZE];
					final DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

					try {
						socket.receive(packet);

						final byte[] data = packet.getData();

						////////////////////////////////
						//
						// note: we aren't using the PDU factory to create the PDU, so we can
						// avoid the dependency on the dis-enums jar = it complicates the scripts
						//
						////////////////////////////////

						// Promote a signed byte to an int, then do a bitwise AND to wipe out everthing
						// but the
						// first eight bits. This effectively lets us read this as an unsigned byte
						final int pduType = 0x000000FF & data[2]; // The pdu type is a one-byte, unsigned byte in the
																	// third byte position.

						final DataInputStream dis = new DataInputStream(new ByteArrayInputStream(data));
						Pdu pdu = null;

						switch (pduType) {
						case 13: {
							pdu = new StartResumePdu();
							break;
						}
						case 14: {
							pdu = new StopFreezePdu();
						}
						}

						////////////////////////////////

						if (pdu != null) {
							pdu.unmarshal(dis);

							if (pdu instanceof SimulationManagementFamilyPdu) {
								final SimulationManagementFamilyPdu simP = (SimulationManagementFamilyPdu) pdu;
								final int appId = simP.getOriginatingEntityID().getApplication();
								final short exId = simP.getExerciseID();

								switch (pdu.getPduType()) {
								case 13: {
									// start resume
									if (_controlListener != null) {
										_controlListener.doPlay(appId, exId);
									}
									break;
								}
								case 14: {
									if (_controlListener != null) {
										// stop pause
										final StopFreezePdu stopper = (StopFreezePdu) simP;
										final short reason = stopper.getReason();
										if (reason == IDISStopListener.PDU_FREEZE) {
											_controlListener.doPause(appId, exId);
										} else {
											_controlListener.doStop(appId, exId);
										}
										break;
									}
								}
								}
							}
						}
					} catch (final IOException ex) {
						_running = false;
					}
				}
			}
		};

		final Thread runner = new Thread(doListen);
		runner.start();
	}

	@Override
	public void sendPdu(final Pdu pdu) {
		// Marshal out the espdu object to a byte array, then send a datagram
		// packet with that data in it.
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		final DataOutputStream dos = new DataOutputStream(baos);
		pdu.marshal(dos);

		// The byte array here is the packet in DIS format. We put that into a
		// datagram and send it.
		final byte[] data = baos.toByteArray();

		final DatagramPacket packet = new DatagramPacket(data, data.length, destinationIp, PORT);

		try {
			socket.send(packet);
		} catch (final IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public String toString() {
		return destinationIp.toString() + " on " + port;
	}
}