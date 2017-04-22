package com.hse.chat.network.packet;

import java.net.Socket;

/**
 * Created by kirill on 22.04.17.
 */
public interface PacketListener {
    void packetSent(Packet packet, Socket client);

    void packetReseived(Packet packet, Socket client);
}


// EOF
