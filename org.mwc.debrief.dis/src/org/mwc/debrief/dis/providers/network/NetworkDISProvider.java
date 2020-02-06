package org.mwc.debrief.dis.providers.network;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.mwc.debrief.dis.listeners.IDISGeneralPDUListener;
import org.mwc.debrief.dis.listeners.IDISStopListener;
import org.mwc.debrief.dis.providers.DISFilters;
import org.mwc.debrief.dis.providers.IPDUProvider;

import edu.nps.moves.dis.EntityID;
import edu.nps.moves.dis.Pdu;
import edu.nps.moves.dis.StartResumePdu;
import edu.nps.moves.dis.StopFreezePdu;
import edu.nps.moves.disutil.PduFactory;

/**
 * data provider that listens for data on a network multicast socket
 *
 * @author Ian
 *
 */
public class NetworkDISProvider implements IPDUProvider, IDISController {

	public static interface LogInterface {
		/**
		 * Status type severity (bit mask, value 1) indicating this status is
		 * informational only.
		 *
		 * @see #getSeverity()
		 * @see #matches(int)
		 */
		final int INFO = 0x01;

		/**
		 * Status type severity (bit mask, value 2) indicating this status represents a
		 * warning.
		 *
		 * @see #getSeverity()
		 * @see #matches(int)
		 */
		final int WARNING = 0x02;

		/**
		 * Status type severity (bit mask, value 4) indicating this status represents an
		 * error.
		 *
		 * @see #getSeverity()
		 * @see #matches(int)
		 */
		final int ERROR = 0x04;

		public void log(int status, String msg, Exception e);

	}

	public static final short APPLICATION_ID = 4321;
	public static final short SITE_ID = 5432;

	/**
	 * Max size of a PDU in binary format that we can receive. This is actually
	 * somewhat outdated--PDUs can be larger--but this is a reasonable starting
	 * point
	 */
	public static final int MAX_PDU_SIZE = 8192;
	private final IDISNetworkPrefs _myPrefs;
	private final List<IDISGeneralPDUListener> _gen = new ArrayList<IDISGeneralPDUListener>();
	private boolean _running;
	private MulticastSocket _listenerSocket;
	private Thread newJob;
	private short _curExercise;
	private EntityID _myID;
	private int _thePort;
	private MulticastSocket _senderSocket;
	protected InetAddress _theAddress;

	private final LogInterface _logger;

	public NetworkDISProvider(final IDISNetworkPrefs prefs, final LogInterface logger) {
		_myPrefs = prefs;
		_logger = logger;
	}

	@Override
	public void addListener(final IDISGeneralPDUListener listener) {
		_gen.add(listener);
	}

	/**
	 * start listening
	 *
	 */
	@Override
	public void attach(final DISFilters filters, final EntityID eid) {
		_myID = eid;

		final Runnable runnable = new Runnable() {

			@Override
			public void run() {
				final String msg = "Listening for DIS messages on address:" + _myPrefs.getIPAddress() + " port:"
						+ _myPrefs.getPort();
				System.out.println(msg);

				_logger.log(LogInterface.INFO, msg, null);

				// Specify the socket to receive data
				_thePort = _myPrefs.getPort();
				try {
					_theAddress = InetAddress.getByName(_myPrefs.getIPAddress());

					// Set up a socket to send information
					_senderSocket = new MulticastSocket(_thePort);

					// and start listening
					startListening(filters);
				} catch (final UnknownHostException ue) {
					_logger.log(LogInterface.ERROR, "DIS couldn't connect to host", ue);
				} catch (final SocketException se) {
					_logger.log(LogInterface.ERROR, "DIS socket exception", se);
				} catch (final IOException e) {
					_logger.log(LogInterface.ERROR, "DIS create multicast", e);
				}
			}
		};
		newJob = new Thread(runnable);
		newJob.start();

	}

	private void configureThisPdu(final Pdu pdu) {
		pdu.setProtocolVersion((short) 6);
	}

	/**
	 * stop listening
	 *
	 */
	@Override
	public void detach() {
		// set the flag, so we naturally stop
		_running = false;

		// and force the socket to close

		// hmm, disconnect was hanging, poss because it's synchronised
		// _listenerSocket.disconnect();

		if (_listenerSocket != null)
			_listenerSocket.close();

		if (_senderSocket != null)
			_senderSocket.close();
	}

	/**
	 * get the network preferences
	 *
	 * @return
	 */
	public IDISNetworkPrefs getPrefs() {
		return _myPrefs;
	}

	@Override
	public void sendPause() {
		// create the Pause PDU
		final StopFreezePdu pause = new StopFreezePdu();
		configureThisPdu(pause);
		pause.setExerciseID(_curExercise);
		pause.setOriginatingEntityID(_myID);
		pause.setReason(IDISStopListener.PDU_FREEZE);
		sendPDU(pause);
	}

	private void sendPDU(final Pdu pdu) {
		// Marshal out the espdu object to a byte array, then send a datagram
		// packet with that data in it.
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		final DataOutputStream dos = new DataOutputStream(baos);
		pdu.marshal(dos);

		// The byte array here is the packet in DIS format. We put that into a
		// datagram and send it.
		final byte[] data = baos.toByteArray();

		final DatagramPacket packet = new DatagramPacket(data, data.length, _theAddress, _thePort);

		try {
			_senderSocket.send(packet);
		} catch (final IOException e) {
			_logger.log(LogInterface.ERROR, "DIS couldn't send PDU", e);
		}
	}

	@Override
	public void sendPlay() {
		// create the Pause PDU
		final StartResumePdu play = new StartResumePdu();
		configureThisPdu(play);
		play.setOriginatingEntityID(_myID);
		play.setExerciseID(_curExercise);

		sendPDU(play);
	}

	@Override
	public void sendStop() {
		// create the Pause PDU
		final StopFreezePdu stop = new StopFreezePdu();
		configureThisPdu(stop);
		stop.setExerciseID(_curExercise);
		stop.setOriginatingEntityID(_myID);
		stop.setReason(IDISStopListener.PDU_STOP);
		sendPDU(stop);
	}

	/**
	 * start listening
	 *
	 * @param filters
	 *
	 */
	private void startListening(final DISFilters filters) {
		// set the running flag to true
		_running = true;

		_listenerSocket = null;
		DatagramPacket packet;
		final PduFactory pduFactory = new PduFactory();

		try {
			try {

				_listenerSocket = new MulticastSocket(_thePort);
				_listenerSocket.joinGroup(_theAddress);
			} catch (final SocketException se) {
				_logger.log(LogInterface.ERROR, "Failed to connect socket", se);
			}

			// did it work?
			if (_listenerSocket != null) {
				// Loop infinitely, receiving datagrams
				while (_running) {
					final byte buffer[] = new byte[MAX_PDU_SIZE];
					packet = new DatagramPacket(buffer, buffer.length);

					_listenerSocket.receive(packet);

					final Pdu pdu = pduFactory.createPdu(packet.getData());

					if (pdu != null) {
						// store the current exercise details
						_curExercise = pdu.getExerciseID();

						// check if it matches our filter
						if (filters == null || filters.accepts(pdu)) {
							// share the good news
							final Iterator<IDISGeneralPDUListener> gIter = _gen.iterator();
							while (gIter.hasNext()) {
								final IDISGeneralPDUListener git = gIter.next();
								git.logPDU(pdu);
							}
						}
					} else {
						final int pduType = 0x000000FF & packet.getData()[2]; // The pdu type is a one-byte,
																				// unsigned byte in the third byte
																				// position.

						System.err.println("PDU not recognised, type:" + pduType);
					}

				} // end while
			} // listener created
		} // End try
		catch (final SocketException se) {
			// ok - this happens when the socket is killed.
			// we don't need to do anything
		} catch (final Exception e) {
			_logger.log(LogInterface.ERROR, "DIS Listening Exception", e);
		} finally {
			if (_listenerSocket != null && !_listenerSocket.isClosed()) {
				// ok, we've finished
				try {
					if (_theAddress != null) {
						_listenerSocket.leaveGroup(_theAddress);
					}
				} catch (final IOException e) {
					e.printStackTrace();
				} finally {
					_listenerSocket.close();
				}
			}
		}
	}
}