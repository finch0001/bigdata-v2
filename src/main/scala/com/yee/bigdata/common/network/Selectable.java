package com.yee.bigdata.common.network;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;

/**
 * An interface for asynchronous, multi-channel network I/O
 */
public interface Selectable {

    /**
     * See {@link #connect(String, InetSocketAddress, int, int) connect()}
     */
    int USE_DEFAULT_BUFFER_SIZE = -1;

    /**
     * Begin establishing a socket connection to the given address identified by the given address
     * @param id The id for this connection
     * @param address The address to connect to
     * @param sendBufferSize The send buffer for the socket
     * @param receiveBufferSize The receive buffer for the socket
     * @throws IOException If we cannot begin connecting
     */
    void connect(String id, InetSocketAddress address, int sendBufferSize, int receiveBufferSize) throws IOException;

    /**
     * Wakeup this selector if it is blocked on I/O
     */
    void wakeup();

    /**
     * Close this selector
     */
    void close();

    /**
     * Close the connection identified by the given id
     */
    void close(String id);

    /**
     * Queue the given request for sending in the subsequent {@link #poll(long) poll()} calls
     * @param send The request to send
     */
    // void send(Send send);

    /**
     * Do I/O. Reads, writes, connection establishment, etc.
     * @param timeout The amount of time to block if there is nothing to do
     * @throws IOException
     */
    void poll(long timeout) throws IOException;

    /**
     * The list of sends that completed on the last {@link #poll(long) poll()} call.
     */
    // List<Send> completedSends();

    /**
     * The list of receives that completed on the last {@link #poll(long) poll()} call.
     */
    // List<NetworkReceive> completedReceives();

    /**
     * The connections that finished disconnecting on the last {@link #poll(long) poll()}
     * call. Channel state indicates the local channel state at the time of disconnection.
     */
    // Map<String, ChannelState> disconnected();

    /**
     * The list of connections that completed their connection on the last {@link #poll(long) poll()}
     * call.
     */
    List<String> connected();

    /**
     * Disable reads from the given connection
     * @param id The id for the connection
     */
    void mute(String id);

    /**
     * Re-enable reads from the given connection
     * @param id The id for the connection
     */
    void unmute(String id);

    /**
     * Disable reads from all connections
     */
    void muteAll();

    /**
     * Re-enable reads from all connections
     */
    void unmuteAll();

    /**
     * returns true  if a channel is ready
     * @param id The id for the connection
     */
    boolean isChannelReady(String id);
}
