package com.hse.chat.network.packet;

/**
 * Created by kirill on 23.04.17.
 */
public class ChatPacket extends Packet {
    public ChatPacket(String[] rawData) {
        super(rawData);
    }

    public ChatPacket(PacketType packetType) {
        super(packetType);
    }

    @Override
    protected void indexOutgoingData() {

    }
}
